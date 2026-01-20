# PlayerMorphToMob - Code Review

**Mod-Version:** 1.0.0
**Autor:** Gorduan
**Review-Datum:** 20. Januar 2026
**Hytale-Version:** Early Access (0.x)

---

## Zusammenfassung

PlayerMorphToMob ist ein Hytale Server-Plugin, das Spieler-Avatare durch Mob-Modelle ersetzen kann. Das Plugin ist konzeptionell gut strukturiert, leidet jedoch unter mehreren API-Inkompatibilitäten, da Hytale sich noch im Early Access befindet und die API häufigen Änderungen unterliegt.

### Gesamtbewertung: 6/10

| Kategorie | Bewertung | Anmerkungen |
|-----------|-----------|-------------|
| Architektur | 8/10 | Saubere Trennung, gute Patterns |
| Code-Qualität | 7/10 | Gut lesbar, konsistent |
| API-Kompatibilität | 3/10 | Mehrere NoSuchMethodErrors |
| Fehlerbehandlung | 6/10 | Grundlegend vorhanden |
| Dokumentation | 7/10 | Gute JavaDocs und Kommentare |
| Testbarkeit | 4/10 | Keine Tests vorhanden |

---

## 1. Projektstruktur

```
Gorduan-PlayerMorphToMob-1.0.0/
├── src/com/gorduan/hytale/playermorphtomob/
│   ├── PlayerMorphToMobPlugin.java    # Hauptklasse (73 Zeilen)
│   ├── MorphManager.java              # Kern-Logik (205 Zeilen)
│   ├── Permissions.java               # Berechtigungen (88 Zeilen)
│   ├── commands/
│   │   └── MorphCommand.java          # Command Handler (212 Zeilen)
│   ├── ui/
│   │   ├── MorphUIPage.java           # GUI (159 Zeilen)
│   │   └── MorphUIEventData.java      # UI Events (94 Zeilen)
│   └── data/
│       └── MorphData.java             # Datenklasse (60 Zeilen)
├── stubs/                             # 52 API-Stub-Dateien
├── manifest.json
└── resources/ (deaktiviert)
```

**Positiv:**
- Klare Package-Struktur nach Verantwortlichkeiten
- Trennung von Daten, Logik und Präsentation
- Stub-Dateien für Kompilierung ohne Server-Quellcode

**Verbesserungspotenzial:**
- Fehlende `resources/` für UI-Assets (aktuell deaktiviert)
- Keine Konfigurationsdatei für Mob-Liste

---

## 2. Kritische API-Probleme

### 2.1 ModelAsset.getAssetMap() - KRITISCH

**Fehler im Server-Log:**
```
java.lang.NoSuchMethodError: 'ModelAsset$AssetMap ModelAsset.getAssetMap()'
    at MorphManager.isValidModel(MorphManager.java:188)
```

**Problem:** Der Stub definiert `ModelAsset.getAssetMap()`, aber die Server-API verwendet möglicherweise eine andere Methode.

**Betroffene Stellen:**
- `MorphManager.java:89` - `ModelAsset.getAssetMap().getAsset(modelId)`
- `MorphManager.java:181` - `ModelAsset.getAssetMap().getAssetIds()`
- `MorphManager.java:188` - `ModelAsset.getAssetMap().getAsset(modelId)`

**Recherche-Ergebnis:** Laut [Hytale Modding Dokumentation](https://hytalemodding.dev/en/docs/guides/plugin/spawning-entities) ist das korrekte Pattern:
```java
ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Minecart");
```

Die Methode existiert, aber möglicherweise mit einer anderen Signatur oder einem anderen Rückgabetyp. Verifizierung mit dem dekompilierten Server-Code erforderlich.

### 2.2 Model.createScaledModel() - Potenziell problematisch

**Aktueller Code:**
```java
Model newModel = Model.createScaledModel(modelAsset, 1.0f);
```

**Recherche-Ergebnis:** Die Dokumentation zeigt zwei Varianten:
- `Model.createScaledModel(modelAsset, 1.0f)` - Standard
- `Model.createRandomScaleModel(newModel)` - Für Effects

Der aktuelle Ansatz scheint korrekt zu sein, aber die genaue Signatur muss verifiziert werden.

### 2.3 Behobene API-Probleme (zur Referenz)

| Problem | Lösung | Datei |
|---------|--------|-------|
| `PlayerRef.getName()` | → `getUsername()` | Alle |
| `CommandContext.getPlayerRef()` | → `senderAs(Player.class).getPlayerRef()` | MorphCommand |
| `OptionalArg.get()` returning Optional | → `arg.provided(context) ? arg.get(context) : null` | MorphCommand |
| `FlagArg.get(context)` | → Argument-Vererbungshierarchie korrigiert | Stubs |

---

## 3. Code-Analyse nach Datei

### 3.1 PlayerMorphToMobPlugin.java

**Stärken:**
- Saubere Lifecycle-Methoden (`setup()`, `start()`, `shutdown()`)
- Event-Listener für Spieler-Disconnect
- Singleton-Pattern für globalen Zugriff

**Schwächen:**
- Keine Fehlerbehandlung beim Command-Registrieren
- Keine Null-Prüfung für `morphManager` in `onPlayerDisconnect()`

**Code-Ausschnitt mit Problem:**
```java
private void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
    String playerName = event.getPlayerRef().getUsername();
    if (morphManager.isMorphed(playerName)) {  // morphManager könnte null sein
        morphManager.forceRemove(playerName);
    }
}
```

**Empfehlung:**
```java
private void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
    if (morphManager == null) return;
    // ...
}
```

### 3.2 MorphManager.java

**Stärken:**
- Singleton-Pattern (thread-safe reicht für Hytale)
- Gute Logging-Praxis mit `HytaleLogger`
- Speicherung des Original-Modells für Reset

**Kritische Probleme:**

1. **API-Aufruf fehlerhaft (Zeile 89):**
```java
ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(modelId);
```
→ Verursacht `NoSuchMethodError`

2. **Hardcodierte Mob-Liste:**
```java
private static final List<String> KNOWN_MOBS = Arrays.asList(
    "trork", "kweebec", "feran", "scaraks",
    "frost_wolf", "void_spawn", "zombie", "skeleton"
);
```
→ Sollte aus Konfiguration geladen werden

3. **MorphData speichert Referenz auf PlayerSkinComponent:**
```java
MorphData morphData = new MorphData(
    currentModel.getModel().getModelAssetId(),
    skinComponent  // Direkter Referenz-Speicher
);
```
→ Potenzielle Memory-Leak-Gefahr bei langen Sessions

**Empfehlung:** Asset-ID statt Komponenten-Referenz speichern.

### 3.3 MorphCommand.java

**Stärken:**
- Unterstützung für Aliase (`/morph`, `/pmtm`)
- Gute Hilfe-Ausgabe mit Farbcodes
- Berechtigungsprüfungen

**Probleme:**

1. **Keine Command-Permission registriert:**
Das Command prüft keine generelle Nutzungsberechtigung:
```java
@Override
protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
    if (!context.isPlayer()) { ... }
    // Fehlt: if (!Permissions.canUseCommand(playerRef)) { ... }
```

2. **Synchrone Ausführung in CompletableFuture:**
Alle Operationen laufen synchron, obwohl die Methode `CompletableFuture<Void>` zurückgibt:
```java
return CompletableFuture.completedFuture(null);
```
→ Für Model-Operationen wäre asynchrone Ausführung sinnvoller.

### 3.4 Permissions.java

**Stärken:**
- Saubere Konstanten-Definition
- Utility-Klasse korrekt als `final` mit privatem Konstruktor
- Admin-Fallback bei allen Prüfungen

**Verbesserungspotenzial:**
```java
public static final String ALL = BASE + ".*";
```
→ Wildcard wird nicht verwendet/implementiert

### 3.5 MorphUIEventData.java

**Stärken:**
- Saubere BuilderCodec-Implementierung
- Hilfs-Methoden für Action-Prüfungen
- Default-Werte im Konstruktor

**Problem:**
UI ist derzeit deaktiviert (`IncludesAssetPack: false`), da:
- `MorphSelector.ui` fehlt oder hat Syntaxfehler
- UI-Referenzpfad unbekannt

### 3.6 MorphUIPage.java

**Stärken:**
- Korrekte Vererbung von `InteractiveCustomUIPage<T>`
- Event-Binding für Buttons
- Trennung von Apply/Reset/Cancel-Logik

**Probleme:**

1. **UI-Pfad nicht überprüfbar:**
```java
private static final String UI_PATH = "Custom/Pages/MorphSelector.ui";
```
→ Wird nirgends verwendet, `@Override` von Basis-Klasse fehlt.

2. **Keine Online-Spieler-Liste in UI:**
```java
// handleApply prüft nur lokale Eingabe
playerRef.sendMessage(Message.of("§eMorphing other players via UI is not yet implemented."));
```

### 3.7 MorphData.java

**Stärken:**
- Immutable Original-Daten
- Timestamp für Morph-Dauer
- Gute `toString()` Implementation

**Problem:**
```java
private final PlayerSkinComponent originalSkinComponent;
```
→ Komponenten-Referenz kann ungültig werden. Besser: Skin-Daten (String/ID) speichern.

---

## 4. Stub-Analyse

### 4.1 Korrekte Stubs

| Stub | Status | Anmerkungen |
|------|--------|-------------|
| `JavaPlugin.java` | ✓ OK | Lifecycle korrekt |
| `CommandContext.java` | ✓ OK | Nach Fixes |
| `Argument.java` | ✓ OK | Vererbungshierarchie |
| `HytaleLogger.java` | ✓ OK | Fluent API |
| `PlayerRef.java` | ✓ OK | `getUsername()` |

### 4.2 Problematische Stubs

| Stub | Problem | Auswirkung |
|------|---------|------------|
| `ModelAsset.java` | `getAssetMap()` Signatur | Runtime-Error |
| `Model.java` | Unvollständig | Unbekannt |
| `CosmeticsModule.java` | Nicht verifiziert | Potenzielle Fehler |

---

## 5. Best Practices Vergleich

### 5.1 Hytale Community Standards

Basierend auf [Britakee Studios Documentation](https://britakee-studios.gitbook.io/hytale-modding-documentation) und [Hytale Plugin Examples](https://github.com/sammwyy/Hytale-Plugin-Examples):

| Kriterium | Empfehlung | PlayerMorphToMob |
|-----------|------------|------------------|
| Java-Version | Java 25 | Unbekannt (Gradle) |
| Build-Tool | Gradle mit shadowJar | Gradle (vermutet) |
| Logging | HytaleLogger | ✓ Verwendet |
| Thread-Safety | `world.execute()` für Entitäten | ✗ Nicht verwendet |
| Codecs | BuilderCodec für Config | ✓ Für UI-Events |
| Event-Pattern | registerGlobal | ✓ Korrekt |

### 5.2 Model-Manipulation Best Practices

Laut [Entity Effects Dokumentation](https://hytale-docs.pages.dev/modding/systems/entity-effects/):

**Empfohlenes Pattern:**
```java
// Aus EffectControllerComponent
ModelComponent currentModel = componentAccessor.getComponent(ownerRef, ModelComponent.getComponentType());
this.originalModel = currentModel.getModel();  // Speichere Original

ModelAsset newModelAsset = ModelAsset.getAssetMap().getAsset(entityEffect.getModelChange());
Model newModel = Model.createRandomScaleModel(newModelAsset);
componentAccessor.putComponent(ownerRef, ModelComponent.getComponentType(), new ModelComponent(newModel));
```

**Aktueller Code (MorphManager.java):**
```java
// Ähnlich, aber mit potenziellem API-Mismatch
ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(modelId);
Model newModel = Model.createScaledModel(modelAsset, 1.0f);
store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(newModel));
```

---

## 6. Sicherheitsanalyse

### 6.1 Keine Malware-Indikatoren

Das Plugin zeigt **keine Anzeichen von Malware**:
- Keine versteckten Netzwerk-Calls
- Keine Dateisystem-Manipulation außerhalb des Plugins
- Keine Code-Injection oder Obfuskation
- Keine verdächtigen Abhängigkeiten

### 6.2 Potenzielle Sicherheitsprobleme

1. **Fehlende Input-Validierung:**
```java
String firstArg = playerArg.provided(context) ? playerArg.get(context) : null;
// Keine Sanitization von Spielernamen
```

2. **Permission-Bypass möglich:**
Wenn `PermissionsModule.checkPermission()` fehlschlägt, könnten Standardwerte greifen.

---

## 7. Performance-Betrachtungen

### 7.1 Memory

- `activeMorphs` HashMap wächst mit aktiven Spielern
- `cleanup()` wird nur bei Plugin-Shutdown aufgerufen
- `forceRemove()` bei Disconnect ist korrekt

### 7.2 CPU

- Keine Hot-Paths in kritischen Loops
- Model-Lookup ist O(1) über HashMap

### 7.3 Empfehlungen

```java
// Periodisches Cleanup für lange Server-Sessions
@Scheduled(every = "1h")
public void cleanupStaleMorphs() {
    activeMorphs.entrySet().removeIf(e ->
        e.getValue().getMorphDuration() > TimeUnit.HOURS.toMillis(24)
    );
}
```

---

## 8. Priorisierte Empfehlungen

### KRITISCH (Sofort beheben)

1. **ModelAsset API korrigieren:**
   - Dekompilieren: `com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset`
   - Exakte Methoden-Signaturen verifizieren
   - Stub aktualisieren

2. **Thread-Safety für Entity-Operationen:**
```java
// Ändern von:
store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(newModel));

// Zu:
world.execute(() -> {
    store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(newModel));
});
```

### HOCH (Kurzfristig)

3. **Konfigurationsdatei für Mob-Liste:**
```java
// config.json
{
  "availableMobs": ["trork", "kweebec", ...],
  "defaultScale": 1.0,
  "allowCustomModels": false
}
```

4. **Command-Permission prüfen:**
```java
if (!Permissions.canUseCommand(playerRef)) {
    playerRef.sendMessage(Message.of("§cYou don't have permission to use this command."));
    return CompletableFuture.completedFuture(null);
}
```

### MITTEL (Mittelfristig)

5. **MorphData refactoren:** Skin-ID statt Komponenten-Referenz

6. **Unit-Tests hinzufügen** für MorphManager-Logik

7. **UI aktivieren** nach Syntax-Verifizierung

### NIEDRIG (Langfristig)

8. **Async Model-Operations** für bessere Server-Performance

9. **Metrics/Telemetrie** für Nutzungsstatistiken

---

## 9. Fazit

PlayerMorphToMob ist ein gut strukturiertes Plugin mit klarer Architektur und sauberem Code. Die Hauptprobleme sind API-Inkompatibilitäten aufgrund der Early-Access-Phase von Hytale.

**Nächste Schritte:**
1. Server-Code dekompilieren und `ModelAsset` API verifizieren
2. Stub für `ModelAsset.getAssetMap()` korrigieren
3. Thread-Safety für Entity-Manipulationen implementieren
4. UI-Komponente testen und aktivieren

---

## Quellen

- [Hytale Modding Documentation (Britakee Studios)](https://britakee-studios.gitbook.io/hytale-modding-documentation)
- [Hytale Plugin Examples (GitHub)](https://github.com/sammwyy/Hytale-Plugin-Examples)
- [Entity Effects Documentation](https://hytale-docs.pages.dev/modding/systems/entity-effects/)
- [Spawning Entities Guide](https://hytalemodding.dev/en/docs/guides/plugin/spawning-entities)
- [Hytale Modding Strategy (Official)](https://hytale.com/news/2025/11/hytale-modding-strategy-and-status)
- [HytaleDocs Community Wiki](https://hytale-docs.com/docs/modding/overview)
- [Hytale Server Unpacked (GitHub)](https://github.com/Ranork/Hytale-Server-Unpacked)

---

*Review erstellt von Claude Code am 20.01.2026*
