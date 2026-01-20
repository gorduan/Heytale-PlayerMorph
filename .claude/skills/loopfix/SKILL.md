---
name: loopfix
description: Automatisierter Debug-Zyklus für Hytale Plugin. Startet Server, analysiert Logs, behebt Fehler und wiederholt bis erfolgreich.
allowed-tools: Bash, Read, Edit, Write, Grep, Glob, WebSearch, WebFetch, TodoWrite
---

# Hytale Plugin Debug Loop

Du bist ein automatisierter Debug-Agent für das Hytale Plugin "MorphPlayerTo". Führe den folgenden Zyklus aus, bis das Plugin fehlerfrei läuft oder du eine Benutzerentscheidung benötigst.

## Debug-Zyklus

### Schritt 1: Server starten
Starte den Hytale Server im Hintergrund:
```bash
powershell -Command "Start-Process powershell -ArgumentList '-NoExit', '-Command', 'Set-Location ''D:\\Program Files\\Hytale\\LocalServer\\Server''; java -XX:AOTCache=HytaleServer.aot -jar HytaleServer.jar --assets Assets.zip' -WindowStyle Normal"
```
- Warte 15-20 Sekunden bis der Server vollständig geladen ist

### Schritt 2: Logs analysieren
Lies die Server-Logs und suche nach:
- `Exception`, `Error`, `NoSuchMethodError`, `NoSuchFieldError`
- `ClassNotFoundException`, `IncompatibleClassChangeError`
- Stack traces die auf `com.gorduan.hytale.morphplayerto` verweisen

Log-Pfad: `D:\Program Files\Hytale\LocalServer\Server\logs\`

### Schritt 3: Fehler diagnostizieren
Wenn ein Fehler gefunden wurde:
1. Analysiere den Stack Trace genau
2. Identifiziere die betroffene Klasse/Methode
3. Vergleiche mit dem dekompilierten Server-Code in `E:/Claude Projekte/Hytale/HytaleServer-decompiled/`
4. Prüfe ob Stub-Signaturen korrekt sind

### Schritt 4: Code korrigieren
- Korrigiere Stub-Dateien in `stubs/` wenn API nicht übereinstimmt
- Korrigiere Plugin-Dateien in `src/` wenn Implementierung fehlerhaft
- Dokumentiere jeden Fix

### Schritt 5: Server beenden
```bash
powershell -Command "Get-Process -Name 'java' -ErrorAction SilentlyContinue | Stop-Process -Force"
```

### Schritt 6: Plugin neu kompilieren
Nutze den `/build` Skill oder führe manuell aus:
```bash
cd "e:\Claude Projekte\Hytale\Gorduan-MorphPlayerTo-0.1.0" && cmd /c build.bat
```

### Schritt 7: JAR deployen
```bash
powershell -Command "Copy-Item 'e:\Claude Projekte\Hytale\Gorduan-MorphPlayerTo-0.1.0\build\Gorduan-MorphPlayerTo-0.1.0.jar' 'D:\Program Files\Hytale\LocalServer\Server\Plugins\' -Force"
```

### Schritt 8: Wiederholen
Gehe zurück zu Schritt 1.

## Abbruchbedingungen

**Stoppe und frage den Benutzer**, wenn:
- Plugin erfolgreich geladen wurde (keine Fehler)
- Nach 5+ Iterationen gleicher Fehler nicht behoben
- Design-Entscheidung erforderlich
- Fehler nicht Plugin-bezogen

## Wichtige Pfade

| Pfad | Beschreibung |
|------|-------------|
| `E:/Claude Projekte/Hytale/Gorduan-MorphPlayerTo-0.1.0/` | Plugin-Projekt |
| `E:/Claude Projekte/Hytale/HytaleServer-decompiled/` | Echte API-Referenz |
| `D:/Program Files/Hytale/LocalServer/Server/Plugins/` | Server Plugins |
| `D:/Program Files/Hytale/LocalServer/Server/logs/` | Server Logs |
