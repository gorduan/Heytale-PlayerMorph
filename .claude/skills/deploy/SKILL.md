---
name: deploy
description: Kopiert das kompilierte Plugin zum Hytale Server und startet den Server neu.
allowed-tools: Bash
---

# Deploy Plugin

Deploye das MorphPlayerTo Plugin zum Hytale Server.

## Voraussetzungen

- Plugin muss kompiliert sein (`/build` ausführen)
- Server sollte gestoppt sein für sauberes Deployment

## Deployment-Schritte

### 1. Server stoppen (falls läuft)
```bash
powershell -Command "Get-Process -Name 'java' -ErrorAction SilentlyContinue | Stop-Process -Force; Write-Host 'Server stopped'"
```

### 2. JAR kopieren
```bash
powershell -Command "Copy-Item 'e:\Claude Projekte\Hytale\Gorduan-MorphPlayerTo-0.1.0\build\Gorduan-MorphPlayerTo-0.1.0.jar' 'D:\Program Files\Hytale\LocalServer\Server\Plugins\' -Force; Write-Host 'Plugin deployed'"
```

### 3. Server starten
```bash
powershell -Command "Start-Process powershell -ArgumentList '-NoExit', '-Command', 'Set-Location ''D:\\Program Files\\Hytale\\LocalServer\\Server''; java -XX:AOTCache=HytaleServer.aot -jar HytaleServer.jar --assets Assets.zip' -WindowStyle Normal; Write-Host 'Server starting...'"
```

## Pfade

| Was | Pfad |
|-----|------|
| Build JAR | `e:\Claude Projekte\Hytale\Gorduan-MorphPlayerTo-0.1.0\build\Gorduan-MorphPlayerTo-0.1.0.jar` |
| Server Plugins | `D:\Program Files\Hytale\LocalServer\Server\Plugins\` |
| Server Logs | `D:\Program Files\Hytale\LocalServer\Server\logs\` |

## Nach dem Deployment

- Warte 15-20 Sekunden bis Server vollständig geladen
- Prüfe Logs auf Fehler
- Teste im Spiel mit `/morph list`
