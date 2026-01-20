# Git Workflow & GitHub Issues

## Repository

**GitHub:** https://github.com/gorduan/Heytale-PlayerMorph

## Issue-Driven Development

**Alle Aufgaben werden über GitHub Issues verwaltet.**

### Vor jeder Aufgabe (PFLICHT!)

```bash
# Aktuellen Stand prüfen
git log --oneline -10
gh issue list --state all --limit 10 -R gorduan/Heytale-PlayerMorph
```

Dies verhindert doppelte Arbeit und stellt Konsistenz sicher.

### Issue-Struktur

Jedes Issue benötigt:

1. **Title:** Aktions-orientiert (z.B. "Add OAuth login", "Fix morph persistence bug")
2. **Description:** Erklärt *warum*, nicht *wie*
3. **Acceptance Criteria:** Testbare Checkboxen
4. **Labels:** `enhancement`, `bug`, `documentation`, etc.

### Issue-Lebenszyklus

```
1. Creation    → Issue mit Titel, Beschreibung, AC erstellen
2. Development → Branch via `gh issue develop 123 -c`
3. Implementation → Code schreiben, committen
4. Review      → PR erstellen mit Issue-Referenz
5. Completion  → PR mergen, Issue auto-closed
```

## gh CLI Befehle

**KRITISCH:** Alle `gh` Befehle MÜSSEN einzeilig sein! Keine Backslash-Continuations!

### Issues

```bash
# Issue erstellen
gh issue create -t "feat: Titel" -b "Beschreibung" -l "enhancement" -R gorduan/Heytale-PlayerMorph

# Issue mit Body aus Datei
gh issue create -t "feat: Titel" -F /tmp/issue-body.md -l "enhancement" -R gorduan/Heytale-PlayerMorph

# Issues auflisten
gh issue list --state open -R gorduan/Heytale-PlayerMorph
gh issue list --state all --limit 20 -R gorduan/Heytale-PlayerMorph

# Issue anzeigen
gh issue view 123 -R gorduan/Heytale-PlayerMorph

# Issue kommentieren
gh issue comment 123 -b "Implementation plan: ..." -R gorduan/Heytale-PlayerMorph

# Issue schließen
gh issue close 123 -R gorduan/Heytale-PlayerMorph

# Branch für Issue erstellen
gh issue develop 123 -c --base main -R gorduan/Heytale-PlayerMorph

# Labels hinzufügen
gh issue edit 123 --add-label "status:in-progress" -R gorduan/Heytale-PlayerMorph
```

### Pull Requests

```bash
# PR erstellen (IMMER einzeilig!)
gh pr create -t "feat(morph): add model preview (#123)" -b "Fixes #123" -R gorduan/Heytale-PlayerMorph

# PR mit Body aus Datei
gh pr create -t "feat(morph): add model preview (#123)" -F /tmp/pr-body.md -R gorduan/Heytale-PlayerMorph

# PR auflisten
gh pr list -R gorduan/Heytale-PlayerMorph

# PR anzeigen
gh pr view 123 -R gorduan/Heytale-PlayerMorph

# PR mergen
gh pr merge 123 --squash --delete-branch -R gorduan/Heytale-PlayerMorph

# PR Kommentare lesen
gh api repos/gorduan/Heytale-PlayerMorph/pulls/123/comments
```

## Branching Strategy

```
main                    # Stabile Releases
├── feature/123-titel   # Feature-Branches (Issue-Nummer im Namen!)
├── bugfix/124-titel    # Bug Fixes
└── hotfix/125-titel    # Hotfixes
```

### Branch erstellen

```bash
# Via gh (empfohlen - verknüpft automatisch mit Issue)
gh issue develop 123 -c --base main -R gorduan/Heytale-PlayerMorph

# Manuell
git checkout -b feature/123-add-model-preview
```

## Conventional Commits

### Format

```
type(scope): description (#issue)

[optional body]

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Types

| Type | Beschreibung | Beispiel |
|------|-------------|----------|
| `feat` | Neues Feature | `feat(morph): add model preview (#123)` |
| `fix` | Bug Fix | `fix(ui): correct selector binding (#124)` |
| `refactor` | Code-Refactoring | `refactor(manager): simplify morph logic (#125)` |
| `docs` | Dokumentation | `docs: update API patterns` |
| `chore` | Build, Dependencies | `chore: update stubs` |
| `test` | Tests | `test: add morph unit tests (#126)` |

### Scopes

| Scope | Bereich |
|-------|---------|
| `morph` | Morph-System |
| `ui` | UI/Pages |
| `cmd` | Commands |
| `stub` | Stubs |
| `storage` | Persistenz |

## Workflow Beispiel

```bash
# 1. Issue prüfen
gh issue view 123 -R gorduan/Heytale-PlayerMorph

# 2. Branch erstellen
gh issue develop 123 -c --base main -R gorduan/Heytale-PlayerMorph

# 3. Implementieren und committen
git add .
git commit -m "feat(morph): add model preview (#123)

Co-Authored-By: Claude <noreply@anthropic.com>"

# 4. Push
git push -u origin feature/123-add-model-preview

# 5. PR erstellen
gh pr create -t "feat(morph): add model preview (#123)" -b "Fixes #123" -R gorduan/Heytale-PlayerMorph

# 6. Nach Review: Mergen
gh pr merge 123 --squash --delete-branch -R gorduan/Heytale-PlayerMorph
```

## Definition of Done

Ein Issue ist abgeschlossen wenn:

- [ ] Alle Acceptance Criteria erfüllt
- [ ] Code kompiliert (.\build.bat)
- [ ] Server-Test ohne Errors
- [ ] PR erstellt mit Issue-Referenz
- [ ] PR gemerged (squash)
- [ ] Issue automatisch geschlossen

## Kritische Regeln

**NIEMALS:**
- Force Push auf `main`
- Secrets committen
- Hooks überspringen (`--no-verify`)
- Mehrzeilige `gh` Befehle mit Backslash

**IMMER:**
- Issue-Nummer in Commits referenzieren (#123)
- Conventional Commits verwenden
- Co-Authored-By Header hinzufügen
- Build testen vor Commit
- `-R gorduan/Heytale-PlayerMorph` bei allen gh Befehlen
