# Troubleshooting Guide

Häufige Fehler und deren Lösungen bei der Hytale Plugin-Entwicklung.

## NoSuchMethodError zur Laufzeit

**Ursache:** Stub-Signatur stimmt nicht mit echter API überein.

**Lösung:**
1. Echte API in `E:\Claude Projekte\Hytale\HytaleServer-decompiled` prüfen
2. Stub-Signatur exakt anpassen (inkl. Package-Pfade!)
3. Neu bauen und deployen

**Beispiele:**
- `BuilderCodec$Builder.append` - `append()` muss `FieldBuilder` zurückgeben
- `UICommandBuilder.set(String, Object)` - Nur spezifische Überladungen existieren
- `PlayerSkinComponent.getPlayerSkin()` - Gibt `com.hypixel.hytale.protocol.PlayerSkin` zurück

## "Failed to load CustomUI documents"

**Ursache:** UI-Datei nicht gefunden.

**Lösung:**
1. UI-Dateien müssen in `resources/Common/UI/Custom/` liegen
2. Pfade in Java relativ zu `Common/UI/Custom/` angeben
3. `manifest.json` muss `"IncludesAssetPack": true` haben

## Model Reset funktioniert nicht

**Ursache:** `removeComponent()` für ModelComponent funktioniert nicht.

**Lösung:** Neues Player-Model über `CosmeticsModule.get().createModel(playerSkin)` erstellen und via `putComponent()` setzen.

## "Assert not in thread!" Error

**Ursache:** ECS-Operationen außerhalb des World-Threads.

**Typische Fehlermeldung:**
```
Assert not in thread! Thread[#79,WorldThread - default,5,...] but was in Thread[#89,pool-1-thread-1,5,main]
```

**Lösung:** Alle ECS-Operationen in `world.execute()` wrappen:

```java
World world = ref.getStore().getExternalData().getWorld();
world.execute(() -> {
    store.putComponent(ref, componentType, component);
});
```

**Tritt häufig auf bei:**
- ScheduledExecutorService Callbacks
- Event-Handler die nicht im World-Thread laufen
- Async-Operationen

## Connection-Probleme (LAN Discovery)

**Ursache:** Netzwerk-Cache oder Windows-Dienste.

**Lösung:** PC neu starten.

## Kompilierfehler mit Stubs

**Symptom:** javac findet Klassen/Methoden nicht.

**Lösungen:**
1. Prüfe ob alle benötigten Stubs vorhanden sind
2. Prüfe Package-Struktur in `stubs/`
3. Vergleiche mit `HytaleServer-decompiled`

## Plugin wird nicht geladen

**Prüfliste:**
1. JAR liegt in `D:\Program Files\Hytale\LocalServer\Server\Plugins\`
2. `manifest.json` ist korrekt formatiert
3. Main-Class Pfad stimmt
4. Keine Exceptions beim Laden (Server-Logs prüfen)

## Server-Logs finden

Logs unter: `D:\Program Files\Hytale\LocalServer\Server\logs\`

Suche nach:
- `Exception`
- `Error`
- `com.gorduan.hytale.playermorphtomob`
