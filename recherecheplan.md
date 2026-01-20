# Recherche-Plan: PlayerMorphToMob Plugin

**Erstellungsdatum:** 20. Januar 2026
**Zweck:** Systematische Recherche-Grundlage für die Entwicklung eines Hytale Server-Plugins, das Spieler-Avatare durch Mob-Modelle ersetzen kann.

---

## Inhaltsverzeichnis

1. [Plugin-Konzept](#1-plugin-konzept)
2. [Funktionale Anforderungen](#2-funktionale-anforderungen)
3. [Technische Recherche-Bereiche](#3-technische-recherche-bereiche)
4. [Existierende Referenzen](#4-existierende-referenzen)
5. [API-Dokumentation](#5-api-dokumentation)
6. [Asset-Recherche](#6-asset-recherche)
7. [Best Practices](#7-best-practices)
8. [Offene Fragen](#8-offene-fragen)

---

## 1. Plugin-Konzept

### 1.1 Vision
Ein Hytale Server-Plugin, das es ermöglicht, die visuelle Darstellung von Spielern temporär durch Mob-Modelle zu ersetzen ("Morphing").

### 1.2 Zielgruppe
- Server-Administratoren (Rollenspiel-Server, Events, Minigames)
- Content-Creator (Videos, Streams)
- Spieler (mit entsprechenden Berechtigungen)

### 1.3 Kernfunktionen
| ID | Funktion | Beschreibung |
|----|----------|--------------|
| F01 | Spieler morphen | Avatar eines Spielers durch Mob-Modell ersetzen |
| F02 | Morph zurücksetzen | Original-Avatar wiederherstellen |
| F03 | Nametag steuern | Nametag während Morph ein-/ausblenden |
| F04 | Mob-Auswahl | Liste verfügbarer Mobs anzeigen |
| F05 | Selbst-Morph | Spieler kann sich selbst morphen |
| F06 | Fremd-Morph | Spieler kann andere morphen (Admin) |
| F07 | GUI-Auswahl | Grafische Oberfläche für Mob-Auswahl |
| F08 | Persistenz | Optional: Morph über Reconnect erhalten |

---

## 2. Funktionale Anforderungen

### 2.1 Commands

#### 2.1.1 Haupt-Command: `/playermorphtomob`

**Aliase zu recherchieren:**
- [ ] Welche Aliase sind sinnvoll? (`/morph`, `/pmtm`, `/disguise`?)
- [ ] Gibt es Konflikte mit existierenden Hytale-Commands?

**Syntax-Varianten zu recherchieren:**

| Syntax | Funktion | Recherche-Fragen |
|--------|----------|------------------|
| `/morph` | GUI öffnen oder Hilfe | Wie öffnet man Custom UI in Hytale? |
| `/morph <spieler> <mob>` | Spieler zu Mob morphen | Positional Arguments in Hytale Commands? |
| `/morph <spieler> <mob> --hiddenname` | Mit verstecktem Nametag | Flag-Arguments Syntax? |
| `/morph reset [spieler]` | Morph zurücksetzen | Subcommands vs. Arguments? |
| `/morph list` | Verfügbare Mobs auflisten | Dynamische Listen von Assets? |
| `/morph reload` | Konfiguration neu laden | Hot-Reload Pattern in Hytale? |

#### 2.1.2 Command-System Recherche

- [ ] Wie registriert man Commands in Hytale?
- [ ] Welche Argument-Typen existieren? (`RequiredArg`, `OptionalArg`, `FlagArg`)
- [ ] Wie funktioniert Tab-Completion?
- [ ] Wie zeigt man Command-Hilfe an?
- [ ] Können Commands asynchron ausgeführt werden?
- [ ] Wie behandelt man Command-Fehler?
- [ ] Gibt es Rate-Limiting für Commands?

### 2.2 Berechtigungen (Permissions)

**Permission-Nodes zu definieren:**

| Permission | Beschreibung | Recherche |
|------------|--------------|-----------|
| `playermorphtomob.command` | Basis-Zugriff auf Command | Wie prüft man Permissions? |
| `playermorphtomob.morph.self` | Sich selbst morphen | Permission-Modul API? |
| `playermorphtomob.morph.others` | Andere morphen | Hierarchische Permissions? |
| `playermorphtomob.nametag.toggle` | Nametag steuern | Wildcard-Support? |
| `playermorphtomob.gui` | GUI-Zugriff | GUI-spezifische Permissions? |
| `playermorphtomob.admin` | Alle Rechte | Admin-Bypass Pattern? |
| `playermorphtomob.reload` | Config neu laden | Operator-Level? |

**Recherche-Fragen:**
- [ ] Wie funktioniert `PermissionsModule` in Hytale?
- [ ] Gibt es Default-Permissions für Spieler?
- [ ] Wie definiert man Permission-Hierarchien?
- [ ] Gibt es Gruppen-Support (wie LuckPerms)?

### 2.3 Use Cases

#### UC01: Spieler morpht sich selbst
```
Akteur: Spieler mit Permission
Vorbedingung: Spieler ist eingeloggt, nicht gemorpht
Ablauf:
  1. Spieler führt `/morph <eigenername> trork` aus
  2. System prüft Permission
  3. System prüft, ob Mob-ID gültig ist
  4. System speichert Original-Modell
  5. System ersetzt Spieler-Modell durch Mob-Modell
  6. System bestätigt Erfolg
Nachbedingung: Spieler erscheint als Trork
```

#### UC02: Admin morpht anderen Spieler
```
Akteur: Admin
Vorbedingung: Ziel-Spieler ist online
Ablauf:
  1. Admin führt `/morph Spieler123 zombie --hiddenname` aus
  2. System prüft Admin-Permission
  3. System findet Ziel-Spieler
  4. System ersetzt Modell
  5. System blendet Nametag aus
  6. Beide erhalten Bestätigung
Nachbedingung: Spieler123 erscheint als Zombie ohne Nametag
```

#### UC03: Morph zurücksetzen
```
Akteur: Spieler oder Admin
Vorbedingung: Ziel ist gemorpht
Ablauf:
  1. Ausführen: `/morph reset [spieler]`
  2. System stellt Original-Modell wieder her
  3. System stellt Nametag wieder her
  4. System entfernt Morph-Daten
Nachbedingung: Spieler hat Original-Avatar
```

#### UC04: Spieler verlässt Server während Morph
```
Akteur: System
Vorbedingung: Spieler ist gemorpht
Trigger: PlayerDisconnectEvent
Ablauf:
  1. System erkennt Disconnect
  2. System räumt Morph-Daten auf
  3. Optional: System speichert für Reconnect
Nachbedingung: Keine Memory-Leaks
```

#### UC05: GUI-basierte Mob-Auswahl
```
Akteur: Spieler mit GUI-Permission
Ablauf:
  1. Spieler führt `/morph` ohne Argumente aus
  2. System öffnet Custom UI Page
  3. UI zeigt Dropdown mit Mobs
  4. UI zeigt Checkbox für Nametag
  5. Spieler wählt aus und klickt "Apply"
  6. System wendet Morph an
  7. UI schließt sich
```

---

## 3. Technische Recherche-Bereiche

### 3.1 Entity-System

#### 3.1.1 Spieler-Entity

- [ ] Wie ist die Player-Entity strukturiert?
- [ ] Welche Komponenten hat ein Spieler? (`ModelComponent`, `NametagComponent`, etc.)
- [ ] Wie greift man auf den aktuellen Spieler zu?
- [ ] Was ist der Unterschied zwischen `Player` und `PlayerRef`?
- [ ] Was ist `Ref<EntityStore>` und wie verwendet man es?

#### 3.1.2 Komponenten-System (ECS)

- [ ] Wie funktioniert das Entity-Component-System in Hytale?
- [ ] Wie liest man Komponenten? (`store.getComponent()`)
- [ ] Wie schreibt man Komponenten? (`store.putComponent()`)
- [ ] Welche Thread-Regeln gelten? (`world.execute()`)
- [ ] Wie klont man Komponenten?

#### 3.1.3 Relevante Komponenten

| Komponente | Recherche-Fragen |
|------------|------------------|
| `ModelComponent` | Struktur? Wie Model setzen? |
| `NametagComponent` | Existiert? Oder Teil von Player? |
| `PlayerSkinComponent` | Wie funktioniert Skin-System? |
| `TransformComponent` | Position/Rotation Handling? |
| `BoundingBox` | Ändert sich bei Morph? |

### 3.2 Model-System

#### 3.2.1 ModelAsset

- [ ] Wie lädt man ein ModelAsset?
- [ ] Wie ist `ModelAsset.getAssetMap()` strukturiert?
- [ ] Welche Asset-IDs existieren für Mobs?
- [ ] Wie validiert man Asset-IDs?
- [ ] Gibt es Asset-Loading-Events?

#### 3.2.2 Model

- [ ] Wie erstellt man ein Model aus ModelAsset?
- [ ] `Model.createScaledModel()` vs `Model.createRandomScaleModel()`?
- [ ] Wie setzt man Model-Skalierung?
- [ ] Wie erhält man die BoundingBox?
- [ ] Gibt es Model-Animationen?

#### 3.2.3 Asset-Pfade

- [ ] Format der Asset-IDs? (`hytale:model/trork`? oder nur `trork`?)
- [ ] Wo liegen Model-Definitionen?
- [ ] Kann man Custom Models hinzufügen?
- [ ] Gibt es einen Asset-Browser/Liste?

### 3.3 Cosmetics-System

- [ ] Was macht `CosmeticsModule`?
- [ ] Wie erstellt man Model aus PlayerSkin?
- [ ] Wie stellt man Original-Skin wieder her?
- [ ] Gibt es Skin-Caching?

### 3.4 UI-System

#### 3.4.1 Custom UI Pages

- [ ] Wie erstellt man eine Custom UI Page?
- [ ] Was ist `InteractiveCustomUIPage<T>`?
- [ ] Wie definiert man `.ui` Dateien?
- [ ] Welche UI-Elemente gibt es? (Dropdown, Button, Checkbox)
- [ ] Wie bindet man Events?

#### 3.4.2 UI-Events

- [ ] Wie funktioniert `BuilderCodec` für UI-Events?
- [ ] Wie mappt man UI-Elemente auf Daten?
- [ ] Welche Event-Binding-Types gibt es?
- [ ] Wie schließt man eine UI-Page?

#### 3.4.3 UI-Dateien

- [ ] Syntax von `.ui` Dateien?
- [ ] Wo müssen UI-Dateien liegen?
- [ ] Wie referenziert man Common-UI-Komponenten?
- [ ] Gibt es UI-Vorlagen/Templates?

### 3.5 Event-System

#### 3.5.1 Relevante Events

| Event | Recherche |
|-------|-----------|
| `PlayerDisconnectEvent` | Wie registrieren? Wann feuert? |
| `PlayerJoinEvent` (existiert?) | Für Reconnect-Persistenz |
| `PlayerDeathEvent` | Morph bei Tod? |
| `PlayerRespawnEvent` | Morph wiederherstellen? |

#### 3.5.2 Event-Handling

- [ ] `registerGlobal()` vs `register()` Unterschied?
- [ ] Können Events gecancelled werden?
- [ ] Event-Prioritäten?
- [ ] Asynchrone Event-Handler?

### 3.6 Plugin-Lifecycle

- [ ] `setup()` vs `start()` vs `shutdown()` Unterschiede?
- [ ] Wann werden Commands registriert?
- [ ] Wann sind andere Plugins verfügbar?
- [ ] Wie handled man Plugin-Dependencies?

### 3.7 Konfiguration

- [ ] Wie erstellt man Plugin-Konfigurationen?
- [ ] Wie verwendet man `BuilderCodec` für Config?
- [ ] Wo werden Configs gespeichert?
- [ ] Wie implementiert man Config-Reload?

### 3.8 Logging

- [ ] Wie funktioniert `HytaleLogger`?
- [ ] Welche Log-Levels gibt es?
- [ ] Wo erscheinen Plugin-Logs?
- [ ] Wie formatiert man Log-Messages?

---

## 4. Existierende Referenzen

### 4.1 Ähnliche Plugins recherchieren

**Suchen nach:**
- [ ] "Hytale morph plugin"
- [ ] "Hytale disguise plugin"
- [ ] "Hytale player model change"
- [ ] "Hytale transform plugin"
- [ ] "Hytale shapeshifter mod"

**Plattformen zu durchsuchen:**
- [ ] CurseForge Hytale Mods
- [ ] GitHub (hytale plugin)
- [ ] Hytale Hub Forums
- [ ] Hytale Discord Community
- [ ] ModDB/NexusMods (falls vorhanden)

### 4.2 Code-Beispiele recherchieren

**Repositories:**
- [ ] `github.com/sammwyy/Hytale-Plugin-Examples`
- [ ] `github.com/realBritakee/hytale-template-plugin`
- [ ] `github.com/HytaleModding`
- [ ] `github.com/Ranork/Hytale-Server-Unpacked`

**Konkret suchen:**
- [ ] ModelComponent Verwendung
- [ ] Entity-Model-Änderung
- [ ] Player-Skin-Manipulation
- [ ] Custom UI Implementation
- [ ] Command mit optionalen Arguments

### 4.3 Vergleichbare Konzepte aus anderen Spielen

| Spiel | Plugin/Mod | Recherche-Wert |
|-------|------------|----------------|
| Minecraft | LibsDisguises | Command-Struktur, UX |
| Minecraft | iDisguise | Permission-System |
| Minecraft | DisguiseCraft | Feature-Scope |
| Bukkit/Spigot | EntityAPI | Entity-Manipulation |

---

## 5. API-Dokumentation

### 5.1 Offizielle Dokumentation

- [ ] Hytale Server Manual (support.hytale.com)
- [ ] Offizielle Modding-Blogposts (hytale.com/news)
- [ ] In-Game Asset Editor Dokumentation

### 5.2 Community-Dokumentation

- [ ] Britakee Studios GitBook
- [ ] HytaleDocs Wiki
- [ ] Hytale Hub Tutorials
- [ ] HytaleModding.dev Guides

### 5.3 API-Klassen im Detail

**Priorität HOCH:**

| Klasse | Zu dokumentieren |
|--------|------------------|
| `ModelAsset` | Alle Methoden, Signaturen |
| `ModelAsset.AssetMap` | getAsset(), getAssetIds() |
| `Model` | createScaledModel(), getBoundingBox() |
| `ModelComponent` | Konstruktor, getModel() |
| `Store<EntityStore>` | getComponent(), putComponent() |
| `Ref<EntityStore>` | isValid(), getStore() |
| `PlayerRef` | getUsername(), getReference(), sendMessage() |

**Priorität MITTEL:**

| Klasse | Zu dokumentieren |
|--------|------------------|
| `CosmeticsModule` | createModel() |
| `PlayerSkinComponent` | getPlayerSkin(), setNetworkOutdated() |
| `NametagComponent` (falls existiert) | setVisible()? |
| `InteractiveCustomUIPage<T>` | build(), handleDataEvent() |
| `BuilderCodec<T>` | Syntax, Usage |

**Priorität NIEDRIG:**

| Klasse | Zu dokumentieren |
|--------|------------------|
| `PermissionsModule` | checkPermission() |
| `PlayerLookup` | getPlayer() |
| `HytaleLogger` | at(), log() |

### 5.4 Dekompilierung

**Zu dekompilierende Klassen:**
- [ ] `com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset`
- [ ] `com.hypixel.hytale.server.core.asset.type.model.config.Model`
- [ ] `com.hypixel.hytale.server.core.modules.entity.component.ModelComponent`
- [ ] `com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent`
- [ ] `com.hypixel.hytale.server.core.cosmetics.CosmeticsModule`

---

## 6. Asset-Recherche

### 6.1 Verfügbare Mob-Modelle

**Zu recherchieren:**
- [ ] Vollständige Liste aller Mob-IDs in Hytale
- [ ] Welche Mobs sind im Early Access verfügbar?
- [ ] Model-IDs vs Display-Namen Mapping
- [ ] Gibt es Varianten? (z.B. `zombie_burning`?)

**Bekannte/vermutete Mobs:**
```
- trork (Troll-artig)
- kweebec (Wald-Wesen)
- feran (Wolf-artig)
- scaraks (Insekten)
- frost_wolf
- void_spawn
- zombie
- skeleton
- [weitere zu recherchieren]
```

### 6.2 Asset-Pfad-Konventionen

- [ ] Namespace-Format? (`hytale:` Prefix?)
- [ ] Ordnerstruktur in Assets?
- [ ] Model vs Entity Unterscheidung?

### 6.3 Custom Assets

- [ ] Kann man eigene Mob-Modelle hinzufügen?
- [ ] Wo platziert man Custom Models?
- [ ] Blockbench-Integration für Hytale?

---

## 7. Best Practices

### 7.1 Code-Architektur

- [ ] Empfohlene Package-Struktur für Hytale Plugins?
- [ ] Singleton vs Dependency Injection?
- [ ] Service-Storage Pattern?
- [ ] Thread-Pool-Nutzung?

### 7.2 Fehlerbehandlung

- [ ] Try-Catch Best Practices?
- [ ] Wie logged man Fehler?
- [ ] Graceful Degradation?

### 7.3 Performance

- [ ] Caching-Strategien für Assets?
- [ ] Batch-Operations für Entity-Updates?
- [ ] Memory-Management?

### 7.4 Testing

- [ ] Gibt es Test-Frameworks für Hytale Plugins?
- [ ] Mock-Objekte für API?
- [ ] Integration-Testing?

### 7.5 Deployment

- [ ] Build mit Gradle shadowJar?
- [ ] JAR-Platzierung im Server?
- [ ] Hot-Reload Möglichkeiten?

---

## 8. Offene Fragen (BEANTWORTET)

### 8.1 Konzeptionelle Fragen

1. **Skalierung:** Soll das Mob-Modell die Spieler-Größe behalten oder Mob-Größe annehmen?
   - ✅ **Antwort:** Mob-Größe wird übernommen.

2. **Kollision:** Ändert sich die Hitbox bei Morph?
   - ✅ **Antwort:** Ja, die Kollision/Hitbox ändert sich mit.

3. **Animationen:** Übernimmt der Spieler Mob-Animationen?
   - ✅ **Antwort:** Ja, Animationen werden mit übernommen.

4. **Sounds:** Sollen Mob-Sounds abgespielt werden?
   - ✅ **Antwort:** Einstellbar (Standard: aus).

5. **Fähigkeiten:** Erhält der Spieler Mob-Fähigkeiten? (Fliegen als Vogel?)
   - ✅ **Antwort:** Einstellbar (Standard: aus).

6. **Interaktionen:** Können NPCs/Mobs den gemorphten Spieler erkennen?
   - ✅ **Antwort:** Einstellbar (Standard: aus).

7. **PvP:** Wie interagiert Morph mit PvP-Systemen?
   - ✅ **Antwort:** Einstellbar. Entweder:
     - Kosmetisch: Spieler behält seine Fähigkeiten (Standard)
     - Vollständig: Spieler übernimmt Mob-Eigenschaften und Angriffe

8. **Persistenz:** Soll Morph Server-Neustarts überleben?
   - ✅ **Antwort:** Einstellbar (Standard: aus).

### 8.2 Technische Fragen

1. **Thread-Safety:** Welche Operationen müssen in `world.execute()` laufen?
   - ✅ **Antwort (verifiziert durch morphmod):** ECS-Operationen (getComponent, putComponent, removeComponent) sollten in `world.execute()` laufen!

2. **Referenz-Validität:** Wann wird eine `Ref<EntityStore>` ungültig?
   - ✅ **Antwort:** Wenn Entity entfernt wird. Immer `ref.isValid()` prüfen!

3. **Nametag:** Gibt es eine dedizierte Nametag-Komponente oder ist es Teil des Player-Modells?
   - ⏳ **Teilweise:** Es gibt `DisplayNameComponent`, genaue API noch zu verifizieren.

4. **Skin-Recovery:** Wie stellt man einen Spieler-Skin exakt wieder her?
   - ✅ **Antwort (verifiziert durch morphmod):**
     1. `store.removeComponent(ref, ModelComponent.getComponentType())` - ModelComponent entfernen
     2. `skinComponent.setNetworkOutdated()` - Sync triggern
     3. `store.putComponent(ref, PlayerSkinComponent.getComponentType(), skinComponent)` - Skin aktualisieren

5. **Asset-Verfügbarkeit:** Wann sind ModelAssets geladen und verfügbar?
   - ✅ **Antwort (verifiziert durch morphmod):** `ModelAsset.getAssetMap().getAsset(id)` - gibt null zurück wenn nicht verfügbar

6. **UI-Lifecycle:** Wann wird eine UI-Page garbage-collected?
   - ✅ **Antwort (verifiziert durch morphmod):** Nach `close()` Aufruf in handleDataEvent()

### 8.3 UX-Fragen

1. **Feedback:** Welche visuellen/akustischen Effekte bei Morph?
   - ✅ **Antwort:** Standard: keine. Aber Möglichkeit sollte offengehalten werden (erweiterbar).

2. **Cooldown:** Soll es einen Morph-Cooldown geben?
   - ✅ **Antwort:** Einstellbar. Standard: kein Cooldown.

3. **Duration:** Zeitbegrenzter Morph vs permanenter Morph?
   - ✅ **Antwort:** Einstellbar. Standard: permanent (bis Reset).

4. **Vorschau:** Mob-Vorschau vor Morph-Anwendung?
   - ✅ **Antwort:**
     - **UI:** Mob-Vorschau wird in der UI angezeigt.
     - **Command:** Model wird erst nur für den Command-Ausführenden gewechselt (Vorschau), dann Bestätigung nötig.
     - **--force Flag:** Überspringt Vorschau und wendet direkt an.

---

## Anhang A: Recherche-Checkliste

### Phase 1: Grundlagen
- [ ] Hytale Plugin Grundstruktur verstehen
- [ ] Build-System einrichten (Gradle)
- [ ] Minimales Hello-World Plugin erstellen
- [ ] Command-Registrierung testen

### Phase 2: Entity-System
- [ ] Entity-Component-System verstehen
- [ ] Player-Entity analysieren
- [ ] ModelComponent Nutzung verstehen
- [ ] Model-Wechsel implementieren (Prototyp)

### Phase 3: Assets
- [ ] ModelAsset-API verstehen
- [ ] Verfügbare Mobs auflisten
- [ ] Asset-Loading verifizieren
- [ ] Validierung implementieren

### Phase 4: Features
- [ ] Morph-Logik implementieren
- [ ] Reset-Logik implementieren
- [ ] Nametag-Steuerung implementieren
- [ ] Event-Handling implementieren

### Phase 5: UI
- [ ] UI-System verstehen
- [ ] .ui Datei erstellen
- [ ] Event-Binding implementieren
- [ ] UI-Integration testen

### Phase 6: Polish
- [ ] Konfiguration hinzufügen
- [ ] Permissions verfeinern
- [ ] Error-Handling verbessern
- [ ] Dokumentation schreiben

---

## Anhang B: Suchbegriffe für Recherche

```
Englisch:
- "hytale plugin model change"
- "hytale morph mod"
- "hytale entity disguise"
- "hytale player transform"
- "hytale ModelComponent tutorial"
- "hytale server api documentation"
- "hytale custom ui page"
- "hytale plugin development guide"

Deutsch:
- "hytale plugin entwicklung"
- "hytale mod tutorial deutsch"
- "hytale server modding"
```

---

## Anhang C: Kontakt-Ressourcen

- **Hytale Discord:** Offizieller Modding-Channel
- **Hytale Hub Forum:** Community-Support
- **Reddit r/hytale:** Entwickler-Diskussionen
- **GitHub Issues:** Bei Open-Source Referenz-Plugins

---

*Dokument erstellt am 20.01.2026 - Vor Durchführung der Recherche*
