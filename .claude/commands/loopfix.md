# Hytale Plugin Debug Loop

Du bist ein automatisierter Debug-Agent für das Hytale Plugin "PlayerMorphToMob". Führe den folgenden Zyklus aus, bis das Plugin fehlerfrei läuft oder du eine Benutzerentscheidung benötigst.

## Debug-Zyklus

### Schritt 1: Server starten
Starte den Hytale Server im Hintergrund:
```bash
cd "D:/Program Files/Hytale/LocalServer/Server" && java -XX:AOTCache=HytaleServer.aot -jar HytaleServer.jar --assets Assets.zip
```
- Starte den Server als Background-Task
- Warte 15-20 Sekunden bis der Server vollständig geladen ist

### Schritt 2: Logs analysieren
- Lies die Server-Ausgabe und suche nach:
  - `Exception`, `Error`, `NoSuchMethodError`, `NoSuchFieldError`
  - `ClassNotFoundException`, `IncompatibleClassChangeError`
  - Stack traces die auf `com.gorduan.hytale.playermorphtomob` verweisen
  - Plugin-bezogene Fehlermeldungen

### Schritt 3: Fehler diagnostizieren
Wenn ein Fehler gefunden wurde:
1. Analysiere den Stack Trace genau
2. Identifiziere die betroffene Klasse/Methode
3. Vergleiche mit dem dekompilierten Server-Code in `E:/Claude Projekte/Hytale/HytaleServer-decompiled/`
4. Nutze **WebSearch** um nach Hytale API Dokumentation zu suchen
5. Nutze **WebFetch** um relevante Dokumentationsseiten zu lesen

### Schritt 4: Code korrigieren
- Korrigiere die Stub-Dateien in `stubs/` wenn die API nicht übereinstimmt
- Korrigiere die Plugin-Dateien in `src/` wenn die Implementierung fehlerhaft ist
- Stelle sicher, dass Typen, Methodensignaturen und Klassenhierarchien korrekt sind

### Schritt 5: Server beenden
Beende den laufenden Server-Prozess (kill the background task).

### Schritt 6: Plugin neu kompilieren
```bash
cd "E:/Claude Projekte/Hytale/Gorduan-PlayerMorphToMob-1.0.0"

# Stubs kompilieren
javac -d build/stub-classes -cp "libs/*" $(find stubs -name "*.java")

# Plugin kompilieren
javac -d build/plugin-classes -cp "build/stub-classes;libs/*" $(find src -name "*.java")

# JAR erstellen
jar cf build/Gorduan-PlayerMorphToMob-1.0.0.jar -C build/plugin-classes . -C . manifest.json -C resources .
```

### Schritt 7: JAR kopieren
```bash
cp "E:/Claude Projekte/Hytale/Gorduan-PlayerMorphToMob-1.0.0/build/Gorduan-PlayerMorphToMob-1.0.0.jar" "D:/Program Files/Hytale/LocalServer/Server/Mods/"
```

### Schritt 8: Wiederholen
Gehe zurück zu **Schritt 1** und starte den Server erneut.

## Abbruchbedingungen

**Stoppe den Zyklus und frage den Benutzer**, wenn:
- Das Plugin erfolgreich geladen wurde (keine Fehler in den Logs)
- Du nach 5+ Iterationen den gleichen Fehler nicht beheben kannst
- Der Fehler eine Design-Entscheidung erfordert (z.B. alternative API-Ansätze)
- Du unsicher bist, welche Lösung korrekt ist
- Der Fehler nicht mit dem Plugin zusammenhängt (Server-Fehler)

## Wichtige Pfade

- **Plugin-Projekt:** `E:/Claude Projekte/Hytale/Gorduan-PlayerMorphToMob-1.0.0/`
- **Stubs:** `E:/Claude Projekte/Hytale/Gorduan-PlayerMorphToMob-1.0.0/stubs/`
- **Source:** `E:/Claude Projekte/Hytale/Gorduan-PlayerMorphToMob-1.0.0/src/`
- **Dekompilierter Server:** `E:/Claude Projekte/Hytale/HytaleServer-decompiled/`
- **Server Mods:** `D:/Program Files/Hytale/LocalServer/Server/Mods/`
- **Build Output:** `E:/Claude Projekte/Hytale/Gorduan-PlayerMorphToMob-1.0.0/build/`

## Hinweise

- Nutze den dekompilierten Server-Code als Referenz für die korrekte API
- Achte besonders auf: Interface vs. Class, Methodensignaturen, Feldtypen
- Dokumentiere jeden Fix in der Todo-Liste
- Bei WebSearch: Suche nach "Hytale plugin development", "Hytale modding API", spezifische Klassennamen
