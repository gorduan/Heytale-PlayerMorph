# Kritische Code-Patterns

## 1. ECS Thread-Safety (KRITISCH!)

```java
// KORREKT: ECS-Operationen im World-Thread
player.getWorld().execute(() -> {
    Ref<EntityStore> ref = player.getReference();
    Store<EntityStore> store = ref.getStore();
    store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(model));
});

// FALSCH: Direkter Aufruf außerhalb des World-Threads
store.putComponent(ref, type, component); // "Assert not in thread!" Error
```

**Wann nötig:**
- Alle `putComponent()`, `removeComponent()` Aufrufe
- ScheduledExecutorService Callbacks
- Event-Handler die nicht im World-Thread laufen

## 2. Model-Änderung (KRITISCH!)

```java
// KORREKT: Neues Model erstellen und via putComponent setzen
ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("trork");
Model newModel = Model.createUnitScaleModel(modelAsset);
store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(newModel));

// FALSCH: Es gibt KEIN setModelAssetPath()!
// modelComponent.setModelAssetPath("trork"); // NoSuchMethodError!
```

## 3. Model Reset (KRITISCH!)

```java
// KORREKT: CosmeticsModule für Player-Model Reset
CosmeticsModule cosmeticsModule = CosmeticsModule.get();
Model newModel = cosmeticsModule.createModel(skinComponent.getPlayerSkin());
store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(newModel));
skinComponent.setNetworkOutdated();

// FALSCH: removeComponent() funktioniert nicht für Model Reset!
// store.removeComponent(ref, ModelComponent.getComponentType());
```

## 4. BuilderCodec Pattern

```java
// KORREKT: append() gibt FieldBuilder zurück, add() gibt Builder zurück
public static final BuilderCodec<MorphUIEventData> CODEC =
    BuilderCodec.builder(MorphUIEventData.class, MorphUIEventData::new)
        .append(new KeyedCodec<>("MorphId", Codec.STRING),
            (data, value) -> data.morphId = value,
            data -> data.morphId)
        .add()  // Auf FieldBuilder aufrufen!
        .build();
```

## 5. UICommandBuilder.set() Überladungen

```java
// KORREKT: Spezifische Überladungen verwenden
commands.set("#Element.Text", "String");     // String
commands.set("#Element.Visible", true);      // boolean
commands.set("#Element.Count", 42);          // int
commands.set("#Element.Scale", 1.5f);        // float

// FALSCH: Generische set(String, Object) existiert NICHT!
// commands.set("#Property", someObject);    // NoSuchMethodError!
```

## 6. PlayerSkin Import (KRITISCH!)

```java
// KORREKT
import com.hypixel.hytale.protocol.PlayerSkin;

// FALSCH - Dieses Package existiert nicht!
// import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkin;
```

## 7. Ref-Validierung

```java
// IMMER Ref validieren vor Verwendung
Ref<EntityStore> ref = playerRef.getReference();
if (ref == null || !ref.isValid()) {
    LOGGER.at(Level.WARNING).log("Invalid player reference");
    return false;
}
```

---

## Don'ts

**NIEMALS:**
- `store.putComponent()` ohne `world.execute()` aufrufen
- `.Result` oder `.Wait()` für Blocking verwenden
- Generische `UICommandBuilder.set(String, Object)` verwenden
- `removeComponent()` für Model Reset nutzen
- PlayerSkin aus falschem Package importieren
- Ref verwenden ohne `isValid()` Prüfung

**IMMER:**
- Bei `NoSuchMethodError` → HytaleServer-decompiled prüfen
- Stub-Signaturen mit echter API abgleichen
- ECS-Operationen in `world.execute()` wrappen
