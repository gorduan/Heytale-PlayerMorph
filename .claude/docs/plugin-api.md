# Hytale Plugin API Grundlagen

## Übersicht

Hytale verwendet ein **100% serverseitiges Modding-System**. Alle Spiellogik, Regeln und Inhalte werden vom Server ausgeführt und kontrolliert. Assets werden automatisch an Clients übertragen.

## Plugin-Typen

### 1. Plugins (Java-basiert)

- Kompiliert als `.jar` Dateien
- Interagieren direkt mit der Hytale Server API
- Ermöglichen neue Mechaniken, Systeme und Commands
- **Benötigt:** Java 25, IntelliJ IDEA (empfohlen), Gradle 9.2.0

### 2. Content Packs (Asset Packs)

- Ohne Code - nur Daten
- Blöcke, Items, NPCs, Worldgen
- Über Asset Editor erstellbar

### 3. Bootstrap Plugins

- Laufen vor Server-Initialisierung
- Können Java Bytecode modifizieren
- Installiert in `earlyplugins/` Verzeichnis

## Plugin-Struktur

### Hauptklasse

```java
package com.gorduan.hytale.morphplayerto;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import javax.annotation.Nonnull;
import java.util.logging.Level;

public class MorphPlayerToPlugin extends JavaPlugin {

    private static MorphPlayerToPlugin instance;

    public MorphPlayerToPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static MorphPlayerToPlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        // Registrierungsphase: Commands, Events, Assets, Components
        getLogger().at(Level.INFO).log("Setting up MorphPlayerTo...");

        // Command registrieren
        getCommandRegistry().registerCommand(new MorphCommand());

        // Events registrieren
        getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
    }

    @Override
    protected void start() {
        // Wird aufgerufen nachdem ALLE Plugins setup() abgeschlossen haben
        // Hier können Plugin-übergreifende Abhängigkeiten initialisiert werden
        getLogger().at(Level.INFO).log("MorphPlayerTo started!");
    }

    @Override
    protected void shutdown() {
        // Plugin wird deaktiviert - Aufräumarbeiten durchführen
        getLogger().at(Level.INFO).log("MorphPlayerTo shutting down...");
    }
}
```

### manifest.json

```json
{
    "Name": "MorphPlayerTo",
    "Version": "1.0.0",
    "Main": "com.gorduan.hytale.morphplayerto.MorphPlayerToPlugin",
    "Author": "Gorduan",
    "Website": "https://github.com/Gorduan",
    "IncludesAssetPack": true
}
```

**Wichtige Felder:**
- `Name`: Plugin-Name (eindeutig)
- `Version`: Semantische Versionierung
- `Main`: Vollqualifizierter Klassenname der Hauptklasse
- `Author`: Autor-Name
- `IncludesAssetPack`: `true` wenn UI-Dateien oder andere Assets enthalten

## Wichtige Imports

```java
// Plugin-System
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

// Entity Component System (ECS)
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

// Player
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;

// Components
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;

// Events
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;

// Commands
import com.hypixel.hytale.server.core.command.system.CommandRegistry;

// Logging
import com.hypixel.hytale.logger.HytaleLogger;
import java.util.logging.Level;
```

## Entity Component System (ECS)

Hytale verwendet ein **Entity Component System** für alle Entities.

### Ref (Referenz)

```java
Ref<EntityStore> entityRef; // Referenz auf eine Entity

// WICHTIG: Immer Gültigkeit prüfen!
if (entityRef != null && entityRef.isValid()) {
    // Entity existiert noch
}
```

### Store (Speicher)

```java
// Store aus Ref abrufen
Store<EntityStore> store = entityRef.getStore();
```

### Component-Zugriff

```java
// Player-Komponente abrufen
Player player = store.getComponent(ref, Player.getComponentType());

// Model-Komponente abrufen
ModelComponent model = store.getComponent(ref, ModelComponent.getComponentType());

// PlayerSkin-Komponente abrufen
PlayerSkinComponent skin = store.getComponent(ref, PlayerSkinComponent.getComponentType());
```

### Component-Änderung

Components sind quasi-immutable. Um sie zu ändern, erstellt man eine neue Instanz:

```java
// Neues Model setzen (NICHT modelComponent.setModelAssetPath()!)
Model newModel = Model.createScaledModel(modelAsset, 1.0f);
store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(newModel));
```

## Logging

```java
// HytaleLogger verwenden
HytaleLogger logger = getLogger();

logger.at(Level.INFO).log("Information");
logger.at(Level.WARNING).log("Warnung: %s", details);
logger.at(Level.SEVERE).log("Fehler");
logger.at(Level.FINE).log("Debug-Info");
```

## Server-Konfiguration

### Entwicklungsmodus

```bash
# Sentry Crash-Tracking deaktivieren während Entwicklung
java -jar HytaleServer.jar --disable-sentry
```

### Mod-Installation

- Mods in `mods/` Ordner ablegen
- Server startet und lädt automatisch

## Quellen

- [Hytale Modding Documentation](https://britakee-studios.gitbook.io/hytale-modding-documentation)
- [Hytale Modding Dev](https://hytalemodding.dev/en/docs)
- [Hytale Server Docs](https://hytale-docs.pages.dev/)
- [Official Hytale Modding Strategy](https://hytale.com/news/2025/11/hytale-modding-strategy-and-status)
