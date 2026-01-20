# Verifizierte Hytale API Patterns

Dieses Dokument enthält alle verifizierten API-Patterns für die Hytale Server-Entwicklung.
Stand: Januar 2026

## BuilderCodec Pattern

Die echte API verwendet `.append().add()` Pattern:

```java
// KORREKT - Echte API
public static final BuilderCodec<MorphUIEventData> CODEC =
    BuilderCodec.builder(MorphUIEventData.class, MorphUIEventData::new)
        .append(new KeyedCodec<>("MorphId", Codec.STRING),
            (data, value) -> data.morphId = value,
            data -> data.morphId)
        .add()  // .add() auf FieldBuilder!
        .build();
```

**KRITISCH:** `append()` gibt `FieldBuilder` zurück, nicht `Builder`!

## UICommandBuilder.set() Überladungen

Nur spezifische Überladungen existieren:

```java
commands.set("#Element.Text", "String value");     // set(String, String)
commands.set("#Element.Visible", true);            // set(String, boolean)
commands.set("#Element.Count", 42);                // set(String, int)
commands.set("#Element.Scale", 1.5f);              // set(String, float)

// FALSCH - Generische Methode existiert NICHT!
// commands.set("#Element.Property", someObject);  // NoSuchMethodError!
```

## EventData Pattern

```java
events.addEventBinding(
    CustomUIEventBindingType.Activating,
    "#SelectButton",
    new EventData().append("MorphId", morphId),
    false
);
```

## InteractiveCustomUIPage

```java
public class MorphUIPage extends InteractiveCustomUIPage<MorphUIEventData> {
    public MorphUIPage(PlayerRef playerRef, Player player, List<String> morphList) {
        // Codec im Konstruktor, NICHT getDataCodec() überschreiben!
        super(playerRef, CustomPageLifetime.CanDismiss, MorphUIEventData.CODEC);
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store,
                                MorphUIEventData data) {
        // Event verarbeiten
        close();  // WICHTIG: Immer close() oder sendUpdate() aufrufen!
    }
}
```

## UI-Pfade

UI-Dateien relativ zu `Common/UI/Custom/`:

```java
commands.append("Pages/MorphSelector.ui");  // Lädt Common/UI/Custom/Pages/MorphSelector.ui
commands.append("#MorphList", "Pages/MorphEntry.ui");
```

## Model Reset (via CosmeticsModule)

**WICHTIG:** NICHT `removeComponent()` verwenden!

```java
// KORREKT - So macht es Hytale's /model reset
PlayerSkinComponent skinComponent = store.getComponent(ref, PlayerSkinComponent.getComponentType());
CosmeticsModule cosmeticsModule = CosmeticsModule.get();
Model newModel = cosmeticsModule.createModel(skinComponent.getPlayerSkin());
store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(newModel));
skinComponent.setNetworkOutdated();
```

**Kritische Import-Pfade:**
- `PlayerSkin` ist in `com.hypixel.hytale.protocol.PlayerSkin`
- NICHT in `com.hypixel.hytale.server.core.modules.entity.player`

## Thread-Safety (world.execute)

Alle ECS-Operationen müssen im World-Thread laufen:

```java
World world = ref.getStore().getExternalData().getWorld();
world.execute(() -> {
    store.putComponent(ref, componentType, component);
});
```

**Fehler ohne world.execute():**
```
Assert not in thread! Thread[#79,WorldThread - default,5,...] but was in Thread[#89,pool-1-thread-1,5,main]
```

## Spielersuche (Universe/NameMatching)

```java
@Nullable
private PlayerRef findPlayerByName(@Nonnull String name) {
    for (World world : Universe.get().getWorlds().values()) {
        Collection<PlayerRef> playerRefs = world.getPlayerRefs();
        PlayerRef found = NameMatching.DEFAULT.find(playerRefs, name, PlayerRef::getUsername);
        if (found != null) {
            return found;
        }
    }
    return null;
}
```

## Page öffnen

```java
Ref<EntityStore> ref = player.getReference();
Store<EntityStore> store = ref.getStore();
PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

player.getPageManager().openCustomPage(ref, store,
    new MorphUIPage(plugin, playerRef, player, morphList));
```
