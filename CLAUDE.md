# PlayerMorphToMob - Hytale Server Plugin

> **Version:** 1.1.0 | **Java:** 25 | **Main:** `com.gorduan.hytale.playermorphtomob.PlayerMorphToMobPlugin`

## GitHub Repository

**URL:** https://github.com/gorduan/Heytale-PlayerMorph

### Issue-Driven Development

Alle Aufgaben werden über GitHub Issues verwaltet. Vor jeder Arbeit:

```bash
# Status prüfen (PFLICHT vor jeder Aufgabe!)
git log --oneline -5
gh issue list --state open -R gorduan/Heytale-PlayerMorph
```

### Wichtige gh Befehle

```bash
# Issue erstellen
gh issue create -t "feat: Titel" -b "Beschreibung" -l "enhancement" -R gorduan/Heytale-PlayerMorph

# Issue bearbeiten (Branch erstellen)
gh issue develop 123 -c --base main -R gorduan/Heytale-PlayerMorph

# Issue kommentieren
gh issue comment 123 -b "Status Update" -R gorduan/Heytale-PlayerMorph

# PR erstellen (IMMER einzeilig!)
gh pr create -t "feat(scope): description (#123)" -b "Fixes #123" -R gorduan/Heytale-PlayerMorph

# PR mergen
gh pr merge 123 --squash --delete-branch -R gorduan/Heytale-PlayerMorph
```

> **WICHTIG:** Alle `gh` Befehle MÜSSEN einzeilig sein (keine Backslash-Continuations)!

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
| `/fix-issue` | GitHub Issue bearbeiten und PR erstellen |

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
| [git-workflow.md](.claude/context/git-workflow.md) | Git, Conventional Commits & GitHub Issues |

| Docs | Inhalt |
|------|--------|
| [api-patterns.md](.claude/docs/api-patterns.md) | Verifizierte API-Patterns |
| [troubleshooting.md](.claude/docs/troubleshooting.md) | Fehler & Lösungen |

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
[ ] gh issue list prüfen (offene Issues)
[ ] git log prüfen (letzte Commits)
[ ] Relevante Docs lesen
[ ] Bei API-Fragen: HytaleServer-decompiled prüfen
```

## Post-Task Checklist

```
[ ] Build erfolgreich (.\build.bat)
[ ] Server-Test ohne Errors
[ ] Git commit mit Conventional Commits (feat/fix/refactor)
[ ] Issue referenzieren in Commit (#123)
[ ] PR erstellen falls Feature-Branch
```

---
**Last Updated:** 2026-01-21
