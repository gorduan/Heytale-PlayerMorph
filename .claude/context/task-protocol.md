# Task Completion Protocol

Nach **jeder signifikanten Aufgabe** (Features, Fixes, Refactorings):

## 1. Build & Test (PFLICHT)

```bash
# Build
.\build.bat

# Deploy
copy "build\Gorduan-MorphPlayerTo-0.1.0.jar" "D:\Program Files\Hytale\LocalServer\Server\Plugins\"

# Server starten und Logs prüfen
cd "D:\Program Files\Hytale\LocalServer\Server"
java -XX:AOTCache=HytaleServer.aot -jar HytaleServer.jar --assets Assets.zip
```

**Prüfe auf:**
- Keine `NoSuchMethodError`
- Keine `ClassNotFoundException`
- Plugin erfolgreich geladen

## 2. Git Commit (PFLICHT)

```bash
# Conventional Commits Format
git add .
git commit -m "type(scope): description

Co-Authored-By: Claude <noreply@anthropic.com>"
```

**Commit Types:**
- `feat`: Neues Feature
- `fix`: Bug Fix
- `refactor`: Code-Refactoring
- `docs`: Dokumentation
- `chore`: Build, Dependencies

## 3. Dokumentation (falls nötig)

Aktualisiere relevante Docs wenn:
- Neue API-Patterns entdeckt wurden → `api-patterns.md`
- Neue Fehler und Lösungen → `troubleshooting.md`
- Stub-Änderungen → Kommentar im Stub

## 4. Re-Entry Point (bei komplexen Tasks)

Bei mehrstufigen Aufgaben: Dokumentiere den aktuellen Stand für die nächste Session.

```markdown
# Aktueller Stand: [Task Name]

## Abgeschlossen
- [x] Task 1
- [x] Task 2

## Nächste Schritte
- [ ] Task 3
- [ ] Task 4

## Kontext
[Wichtige Informationen für die Fortsetzung]
```

---

## Checkliste

```
[ ] Build erfolgreich (.\build.bat)
[ ] Server-Test ohne Errors
[ ] Git commit mit Conventional Commits
[ ] Docs aktualisiert (falls neue Erkenntnisse)
[ ] Re-Entry Point (bei komplexen Tasks)
```

## Wann anwenden

- Nach jedem Feature/Fix/Refactoring
- Am Ende einer Work-Session (auch wenn unvollständig)
- Vor Wechsel zu anderem Feature
- **NICHT** bei kleinen Fixes (<30min, eine Datei)
