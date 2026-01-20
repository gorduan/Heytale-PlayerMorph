# Agent Registry

> **Maintained by:** Claude Code
> **Last Updated:** 2026-01-21
> **Total Agents:** 3
> **Skills:** 1

---

## GitHub Repository

**URL:** https://github.com/gorduan/Heytale-PlayerMorph

Alle Agents unterstützen GitHub Issues Integration. Siehe [git-workflow.md](../context/git-workflow.md) für Details.

---

## Active Agents Overview

| Name | Domain | Model | Tools | Description |
|------|--------|-------|-------|-------------|
| `hytale-api-researcher` | API Research | haiku | Read, Grep, Glob, WebSearch, WebFetch | Findet API-Signaturen im dekompilierten Server |
| `stub-validator` | Stub Validation | sonnet | Read, Grep, Glob, Edit | Validiert Stubs gegen echte API |
| `hytale-web-researcher` | Web Research | opus | WebSearch, WebFetch, Read, Write | Intensive Online-Recherche für Hytale Modding |

## Skills Overview

| Name | Purpose | Trigger |
|------|---------|---------|
| `fix-issue` | GitHub Issue bearbeiten, implementieren, PR erstellen | `/fix-issue <nummer>` |

---

## Agent Cards

### hytale-api-researcher

- **File:** `hytale-api-researcher.md`
- **Model:** haiku (schnelle, kosteneffiziente Suche)
- **Description:** Recherchiert Hytale Server API im dekompilierten Server-Code
- **Primary Use Cases:**
  - API-Signaturen finden
  - Methoden-Parameter verifizieren
  - Package-Pfade ermitteln
  - Verwendungsbeispiele finden
- **Tools:** Read, Grep, Glob, WebSearch, WebFetch
- **Primary Source:** `E:\Claude Projekte\Hytale\HytaleServer-decompiled\`
- **When to Use:**
  - "Welche Parameter hat Methode X?"
  - "In welchem Package ist Klasse Y?"
  - "Wie wird Z verwendet?"

---

### stub-validator

- **File:** `stub-validator.md`
- **Model:** sonnet (gute Balance für Analyse + Edits)
- **Description:** Validiert Stub-Dateien gegen den echten Hytale Server
- **Primary Use Cases:**
  - NoSuchMethodError diagnostizieren
  - Stub-Signaturen korrigieren
  - API-Diskrepanzen identifizieren
  - Automatische Stub-Fixes
- **Tools:** Read, Grep, Glob, Edit
- **Comparison Sources:**
  - Stubs: `e:\Claude Projekte\Hytale\Gorduan-PlayerMorphToMob-1.0.0\stubs\`
  - Echte API: `E:\Claude Projekte\Hytale\HytaleServer-decompiled\`
- **When to Use:**
  - Bei `NoSuchMethodError` zur Laufzeit
  - Bei `NoSuchFieldError`
  - Bei `IncompatibleClassChangeError`
  - Vor größeren Stub-Änderungen

---

### hytale-web-researcher

- **File:** `hytale-web-researcher.md`
- **Model:** opus (maximale Qualität für tiefe Recherche)
- **Description:** Intensive Web-Recherche für Hytale Modding
- **Primary Use Cases:**
  - Tiefgehende Online-Recherche
  - Community Best Practices finden
  - Referenz-Plugins analysieren
  - Dokumentation durchsuchen
- **Tools:** WebSearch, WebFetch, Read, Write
- **Key Sources:**
  - https://hytale-docs.pages.dev/
  - https://hytalemodding.dev/
  - https://britakee-studios.gitbook.io/hytale-modding-documentation
  - https://www.curseforge.com/hytale/mods
- **Characteristics:**
  - Sehr gründlich (nicht token-optimiert)
  - Multiple Suchanfragen parallel
  - Kreuzreferenzierung von Quellen
  - Strukturierter Bericht als Output
- **When to Use:**
  - "Wie implementieren andere Plugins Feature X?"
  - "Gibt es Dokumentation zu Y?"
  - "Was sind Best Practices für Z?"

---

## Agent Selection Guide

```
NoSuchMethodError?
├── Ja → stub-validator
│         └── Vergleicht Stub mit echter API, schlägt Fix vor
└── Nein
    ├── API-Frage zu lokalem Code?
    │   └── Ja → hytale-api-researcher
    │             └── Durchsucht HytaleServer-decompiled
    └── Allgemeine Recherche?
        └── Ja → hytale-web-researcher
                  └── Intensive Web-Suche
```

---

## Agent Creation Guidelines

Neue Agents für dieses Projekt sollten:

1. **Spezifisch sein** - Ein klarer Anwendungsfall
2. **Das richtige Model wählen:**
   - `haiku` - Schnelle, einfache Suchen
   - `sonnet` - Analyse + Code-Änderungen
   - `opus` - Komplexe Recherche, tiefe Analyse
3. **Relevante Pfade kennen:**
   - Dekompilierter Server
   - Stub-Verzeichnis
   - Plugin-Quellcode
4. **Klare Output-Formate** definieren

---

## Skills

### fix-issue

- **File:** `skills/fix-issue/SKILL.md`
- **Trigger:** `/fix-issue <issue-nummer>`
- **Description:** Automatisierter Workflow für Issue-basierte Entwicklung
- **Workflow:**
  1. Issue Details abrufen (`gh issue view`)
  2. Branch erstellen (`gh issue develop`)
  3. Implementierung durchführen
  4. Build testen (`.\build.bat`)
  5. Commit mit Issue-Referenz
  6. PR erstellen (`gh pr create`)
- **Wichtig:** Alle gh Befehle MÜSSEN einzeilig sein!

---

## Maintenance

| Task | Frequency | Last Run | Next Due |
|------|-----------|----------|----------|
| Agent Review | Monthly | 2026-01-21 | 2026-02-21 |
| Source URLs prüfen | Quarterly | 2026-01-21 | 2026-04-21 |

---

**Registry Version:** 1.1.0
