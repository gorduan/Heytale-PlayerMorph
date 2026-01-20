# Hytale UI System

## Übersicht

Hytale's serverseitiges GUI-System besteht aus drei Subsystemen:

- **Windows** - Inventare, Container
- **Pages** - Dialoge, Menüs (für MorphPlayerTo relevant!)
- **HUD** - Overlays, Status

## KRITISCH: Stubs vs. Echte API

**Stubs sind NUR für die Kompilierung!** Die echte API wird zur Laufzeit verwendet.

Bei `NoSuchMethodError` IMMER die echte API prüfen:
```
E:\Claude Projekte\Hytale\HytaleServer-decompiled\
```

## Verifizierte API-Patterns (Januar 2026)

### BuilderCodec - KORREKT

```java
// Die echte API verwendet .append().add() Pattern
// .append() gibt FieldBuilder zurück, .add() gibt Builder zurück
public static final BuilderCodec<MorphUIEventData> CODEC =
    BuilderCodec.builder(MorphUIEventData.class, MorphUIEventData::new)
        .append(new KeyedCodec<>("MorphId", Codec.STRING),
            (data, value) -> data.morphId = value,
            data -> data.morphId)
        .add()  // Auf FieldBuilder, gibt Builder zurück
        .append(new KeyedCodec<>("Action", Codec.STRING),
            (data, value) -> data.action = value,
            data -> data.action)
        .add()
        .build();
```

**Stub-Signatur (muss exakt so sein):**
```java
// In BuilderCodec.BuilderBase:
public <FieldType> BuilderField.FieldBuilder<T, FieldType, S> append(
    KeyedCodec<FieldType> codec,
    BiConsumer<T, FieldType> setter,
    Function<T, FieldType> getter)

// In BuilderField.FieldBuilder:
public Builder add()  // Gibt parent Builder zurück
```

### UICommandBuilder.set() - KORREKT

Die echte API hat **spezifische Überladungen**, KEINE generische `set(String, Object)`:

```java
// KORREKT - Spezifische Typen
commands.set("#MorphName.Text", "Dragon_Frost");  // String
commands.set("#Visible", true);                    // boolean
commands.set("#Count", 42);                        // int
commands.set("#Scale", 1.5f);                      // float

// FALSCH - Gibt NoSuchMethodError!
// commands.set("#Property", someObject);
```

**Stub muss alle Überladungen haben:**
```java
public UICommandBuilder set(String selector, String str);
public UICommandBuilder set(String selector, boolean b);
public UICommandBuilder set(String selector, int n);
public UICommandBuilder set(String selector, float n);
public UICommandBuilder set(String selector, double n);
public <T> UICommandBuilder set(String selector, Value<T> ref);
public UICommandBuilder setObject(String selector, Object data);
public <T> UICommandBuilder set(String selector, T[] data);
public <T> UICommandBuilder set(String selector, List<T> data);
```

### EventData - KORREKT

```java
// new EventData().append() Pattern
events.addEventBinding(
    CustomUIEventBindingType.Activating,
    "#SelectButton",
    new EventData().append("MorphId", morphId),
    false
);
```

### InteractiveCustomUIPage - KORREKT

```java
public class MorphUIPage extends InteractiveCustomUIPage<MorphUIEventData> {

    public MorphUIPage(PlayerRef playerRef, Player player, List<String> morphList) {
        // Codec im Konstruktor, NICHT getDataCodec() überschreiben!
        super(playerRef, CustomPageLifetime.CanDismiss, MorphUIEventData.CODEC);
    }

    @Override
    public void build(Ref<EntityStore> ref, UICommandBuilder commands,
                      UIEventBuilder events, Store<EntityStore> store) {
        // UI-Datei laden (Pfad relativ zu Common/UI/Custom/)
        commands.append("Pages/MorphSelector.ui");

        // Event-Bindings
        events.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#CloseButton",
            new EventData().append("Action", "close"),
            false
        );

        // Dynamische Einträge
        commands.append("#MorphList", "Pages/MorphEntry.ui");
        commands.set("#MorphList[0] #MorphName.Text", "Trork");
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store,
                                MorphUIEventData data) {
        // Event verarbeiten
        if ("close".equals(data.action)) {
            close();
            return;
        }
        // ... Logik
        close();  // WICHTIG: Immer close() oder sendUpdate()!
    }
}
```

## UI-Pfade

UI-Dateien werden relativ zu `Common/UI/Custom/` geladen:

```java
commands.append("Pages/MorphSelector.ui");
// Lädt: resources/Common/UI/Custom/Pages/MorphSelector.ui

commands.append("#MorphList", "Pages/MorphEntry.ui");
// Fügt Entry aus Pages/MorphEntry.ui in #MorphList Container ein
```

## .ui Datei-Format

### Basis-Struktur (Waypoints-Style)

```
$C = "../Common.ui";

$C.@Container {
  Anchor: (Left: 50, Top: 50, Width: 350, Height: 500);

  #Title {
    Group {
      LayoutMode: Left;
      $C.@Title #TitleText {
        Text: "Morph Selector";
      }
    }
  }

  #Content {
    Group #MorphList {
      FlexWeight: 1;
      LayoutMode: TopScrolling;
      ScrollbarStyle: $C.@DefaultScrollbarStyle;
    }
  }

  $C.@TextButton #CloseButton {
    Anchor: (Height: 32);
    Text: "Close";
  }
}

$C.@BackButton {}
```

### Entry-Template

```
$C = "../Common.ui";

Group {
  Background: $C.@InputBoxBackground;
  Anchor: (Height: 40);
  Padding: (All: 4);

  Label #MorphName {
    Style: (FontSize: 14, TextColor: #d3d6db);
    Text: "";
  }

  $C.@TextButton #SelectButton {
    Anchor: (Width: 80, Height: 28, Right: 0);
    Text: "Select";
  }
}
```

## Page öffnen

```java
// Im World-Thread ausführen!
player.getWorld().execute(() -> {
    Ref<EntityStore> ref = player.getReference();
    Store<EntityStore> store = ref.getStore();
    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

    player.getPageManager().openCustomPage(ref, store,
        new MorphUIPage(plugin, playerRef, player, morphList));
});
```

## Datei-Speicherort

```
resources/Common/UI/Custom/Pages/
├── MorphSelector.ui
└── MorphEntry.ui
```

**Wichtig:** `manifest.json` muss `"IncludesAssetPack": true` enthalten!

## Häufige Fehler

### NoSuchMethodError: BuilderCodec$Builder.append

**Ursache:** Stub hat falsche Signatur - `append()` muss `FieldBuilder` zurückgeben.

**Lösung:** Stub prüfen gegen `HytaleServer-decompiled/com/hypixel/hytale/codec/builder/BuilderCodec.java`

### NoSuchMethodError: UICommandBuilder.set(String, Object)

**Ursache:** Generische `set(String, Object)` existiert nicht.

**Lösung:** Spezifische Überladung verwenden (`set(String, String)`, etc.)

### Failed to load CustomUI documents

**Ursache:** UI-Datei nicht gefunden.

**Lösung:**
1. Dateien in `resources/Common/UI/Custom/` ablegen
2. Pfade relativ zu `Common/UI/Custom/` angeben
3. `"IncludesAssetPack": true` in manifest.json

## Quellen

- **HytaleServer-decompiled** - Echte API-Signaturen (PRIMÄR)
- **Waypoints-1.2.0** - Funktionierende UI-Referenz
- https://hytale-docs.pages.dev/gui/pages/
- https://hytalemodding.dev/en/docs/guides/plugin/ui
