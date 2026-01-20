# PlayerMorphToMob - Hytale Server Plugin

> **Version:** 1.1.0 | **Java:** 25 | **Main:** `com.gorduan.hytale.playermorphtomob.PlayerMorphToMobPlugin`

## Quick Reference

```bash
# Build & Deploy
.\build.bat                    # Plugin kompilieren
# Deploy: JAR nach D:\Program Files\Hytale\LocalServer\Server\Plugins\ kopieren

# Server starten
cd "D:\Program Files\Hytale\LocalServer\Server"
java -XX:AOTCache=HytaleServer.aot -jar HytaleServer.jar --assets Assets.zip
```

## Pfade

| Was | Pfad |
|-----|------|
| Server | `D:\Program Files\Hytale\LocalServer\Server` |
| Plugins | `D:\Program Files\Hytale\LocalServer\Server\Plugins` |
| Echte API | `E:\Claude Projekte\Hytale\HytaleServer-decompiled` |
| Referenz-Plugin | `E:\Claude Projekte\Hytale\x3Dev-Waypoints-1.2.0` |
| GitHub | https://github.com/gorduan/Heytale-PlayerMorph |

## Kritische Regeln

1. **Stubs = echte API** - Bei `NoSuchMethodError` → `HytaleServer-decompiled` prüfen
2. **ECS in `world.execute()`** - Sonst "Assert not in thread!" Error
3. **`PlayerSkin`** - Package: `com.hypixel.hytale.protocol`
4. **Model Reset** - `CosmeticsModule.createModel()`, NICHT `removeComponent()`

## Skills & Subagents

| Skill | Beschreibung |
|-------|-------------|
| `/loopfix` | Debug-Zyklus: Build → Deploy → Server → Logs → Fix → Repeat |
| `/build` | Plugin kompilieren |
| `/deploy` | Plugin deployen und Server neustarten |

| Agent | Wann nutzen |
|-------|-------------|
| `hytale-api-researcher` | API-Signaturen im dekompilierten Server finden |
| `stub-validator` | Stubs validieren, NoSuchMethodError debuggen |
| `hytale-web-researcher` | Intensive Web-Recherche (Opus, sehr gründlich) |

> **Vollständige Agent-Dokumentation:** [AGENT_REGISTRY.md](.claude/agents/AGENT_REGISTRY.md)

## Context & Dokumentation

| Context | Inhalt |
|---------|--------|
| [code-patterns.md](.claude/context/code-patterns.md) | Kritische Code-Patterns & Don'ts |
| [task-protocol.md](.claude/context/task-protocol.md) | Task-Completion Protocol |
| [git-workflow.md](.claude/context/git-workflow.md) | Git & Conventional Commits |

| Docs | Inhalt |
|------|--------|
| [api-patterns.md](.claude/docs/api-patterns.md) | Verifizierte API-Patterns |
| [troubleshooting.md](.claude/docs/troubleshooting.md) | Fehler & Lösungen |
| [entity-morphing.md](.claude/docs/entity-morphing.md) | Model-Änderung via ECS |
| [ui-system.md](.claude/docs/ui-system.md) | UI-System |
| [plugin-api.md](.claude/docs/plugin-api.md) | Plugin-API |

## Projektstruktur

```
├── stubs/                    # Kompilier-Stubs (müssen echte API matchen!)
├── src/                      # Plugin Source Code
├── resources/Common/UI/      # UI-Dateien
├── build/                    # Build Output
├── .claude/
│   ├── context/              # Code-Patterns, Protokolle
│   ├── docs/                 # Detaillierte Dokumentation
│   ├── skills/               # Custom Slash Commands
│   └── agents/               # Custom Subagents
└── manifest.json
```

## Pre-Task Checklist

```
[ ] Relevante Docs lesen
[ ] Passenden Subagent identifizieren
[ ] Bei API-Fragen: HytaleServer-decompiled prüfen
```

## Post-Task Checklist

```
[ ] Build erfolgreich (.\build.bat)
[ ] Server-Test ohne Errors
[ ] Git commit mit Conventional Commits
[ ] Docs aktualisiert (falls nötig)
```

---
**Last Updated:** 2025-01-20
