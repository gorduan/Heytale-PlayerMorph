# Git Workflow

## Repository

**GitHub:** https://github.com/gorduan/Heytale-PlayerMorph

## Branching Strategy

```
main                    # Stabile Releases
├── develop             # Entwicklungs-Branch
│   ├── feature/XXX     # Neue Features
│   ├── bugfix/XXX      # Bug Fixes
│   └── refactor/XXX    # Refactorings
```

## Conventional Commits

### Format

```
type(scope): description

[optional body]

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Types

| Type | Beschreibung | Beispiel |
|------|-------------|----------|
| `feat` | Neues Feature | `feat(morph): add model preview` |
| `fix` | Bug Fix | `fix(ui): correct selector binding` |
| `refactor` | Code-Refactoring | `refactor(manager): simplify morph logic` |
| `docs` | Dokumentation | `docs: update API patterns` |
| `chore` | Build, Dependencies | `chore: update stubs` |
| `style` | Formatierung | `style: fix indentation` |
| `test` | Tests | `test: add morph unit tests` |

### Scopes

| Scope | Bereich |
|-------|---------|
| `morph` | Morph-System |
| `ui` | UI/Pages |
| `cmd` | Commands |
| `stub` | Stubs |
| `storage` | Persistenz |

## Workflow

### 1. Feature Branch erstellen

```bash
git checkout develop
git pull origin develop
git checkout -b feature/add-morph-preview
```

### 2. Entwickeln & Committen

```bash
# Änderungen committen
git add .
git commit -m "feat(morph): add model preview

Co-Authored-By: Claude <noreply@anthropic.com>"
```

### 3. Push & PR

```bash
git push -u origin feature/add-morph-preview
# PR auf GitHub erstellen
```

### 4. Merge

```bash
git checkout develop
git merge --no-ff feature/add-morph-preview
git push origin develop
git branch -d feature/add-morph-preview
```

---

## Kritische Regeln

**NIEMALS:**
- Force Push auf `main` oder `develop`
- Secrets committen (API Keys, Passwörter)
- Hooks überspringen (`--no-verify`)
- Direkt auf `main` pushen

**IMMER:**
- Conventional Commits verwenden
- Co-Authored-By Header hinzufügen
- Build testen vor Commit
- PR für Merges zu `main`

## .gitignore

```gitignore
# Build Output
build/
*.jar
!stubs/**/*.jar

# IDE
.idea/
*.iml
.vscode/

# OS
.DS_Store
Thumbs.db

# Local Config
.claude/settings.local.json
```
