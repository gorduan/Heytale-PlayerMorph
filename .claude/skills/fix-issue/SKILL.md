---
name: fix-issue
description: Bearbeitet ein GitHub Issue, implementiert die Lösung und erstellt einen PR. Nutze /fix-issue <issue-nummer>
allowed-tools: Bash, Read, Write, Edit, Grep, Glob, TodoWrite
---

# Fix GitHub Issue Skill

Du bearbeitest ein GitHub Issue für das Hytale PlayerMorph Plugin.

## Argument

`$ARGUMENTS` enthält die Issue-Nummer (z.B. "123")

## Repository

**GitHub:** gorduan/Hytale-PlayerMorph

## Workflow

### Phase 1: Issue verstehen

```bash
# Issue Details abrufen
gh issue view $ARGUMENTS -R gorduan/Hytale-PlayerMorph

# Aktuellen Stand prüfen
git log --oneline -5
gh issue list --state open -R gorduan/Hytale-PlayerMorph
```

Analysiere:
- Was ist das Problem/Feature?
- Was sind die Acceptance Criteria?
- Welche Dateien sind betroffen?

### Phase 2: Branch erstellen

```bash
# Branch für Issue erstellen (verknüpft automatisch)
gh issue develop $ARGUMENTS -c --base main -R gorduan/Hytale-PlayerMorph

# Falls das nicht funktioniert, manuell:
git checkout -b feature/$ARGUMENTS-beschreibung
```

### Phase 3: Implementieren

1. **Relevante Dateien finden** - Codebase durchsuchen
2. **Änderungen vornehmen** - Code schreiben/ändern
3. **Testen** - `.\build.bat` ausführen

### Phase 4: Committen

```bash
# Änderungen stagen
git add .

# Commit mit Issue-Referenz (WICHTIG: einzeilig!)
git commit -m "type(scope): description (#$ARGUMENTS)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

Commit Types:
- `feat` - Neues Feature
- `fix` - Bug Fix
- `refactor` - Refactoring
- `docs` - Dokumentation

### Phase 5: PR erstellen

```bash
# Push
git push -u origin HEAD

# PR erstellen (MUSS einzeilig sein!)
gh pr create -t "type(scope): description (#$ARGUMENTS)" -b "Fixes #$ARGUMENTS" -R gorduan/Hytale-PlayerMorph
```

## Checkliste

```
[ ] Issue verstanden
[ ] Branch erstellt
[ ] Implementierung abgeschlossen
[ ] Build erfolgreich (.\build.bat)
[ ] Commit mit Issue-Referenz (#$ARGUMENTS)
[ ] PR erstellt
```

## Kritische Regeln

1. **Alle gh Befehle einzeilig** - Keine Backslash-Continuations!
2. **Immer -R gorduan/Hytale-PlayerMorph** - Repository Flag nicht vergessen
3. **Issue-Nummer im Commit** - Format: `(#123)`
4. **Co-Authored-By** - Immer am Ende des Commits

## Bei Problemen

Falls API-Fragen aufkommen:
- Nutze `hytale-api-researcher` Subagent
- Prüfe `E:\Claude Projekte\Hytale\HytaleServer-decompiled`

Falls Stub-Fehler:
- Nutze `stub-validator` Subagent
