# PlayerMorphToMob - Architektur & Design

## System-Übersicht

```mermaid
graph TB
    subgraph "Client Layer"
        Client[Hytale Client]
    end

    subgraph "Server Layer"
        Plugin[PlayerMorphToMob Plugin]
        CommandSystem[Command System]
        UISystem[UI System]
        PermSystem[Permission System]
        MorphManager[MorphManager]
        EntityStore[Entity Store / ECS]
    end

    Client <-->|"Assets, Commands"| Plugin
    Plugin --> CommandSystem
    Plugin --> UISystem
    Plugin --> PermSystem
    Plugin --> MorphManager
    MorphManager --> EntityStore

    style Plugin fill:#4a9eff,stroke:#333,stroke-width:2px
    style MorphManager fill:#ff9f4a,stroke:#333,stroke-width:2px
```

## Komponenten-Architektur

```mermaid
classDiagram
    class PlayerMorphToMobPlugin {
        -MorphManager morphManager
        -static instance
        +setup()
        +start()
        +shutdown()
        +getInstance()
    }

    class MorphManager {
        -Map~String, MorphData~ activeMorphs
        -static instance
        +applyMorph(playerRef, modelId)
        +resetMorph(playerRef)
        +isMorphed(playerName)
        +getMorphData(playerName)
        +cleanup()
    }

    class MorphData {
        -String originalModelId
        -PlayerSkinComponent skinComponent
        -String currentModelId
        +getters()
    }

    class MorphCommand {
        -OptionalArg~String~ playerArg
        -OptionalArg~String~ mobArg
        -FlagArg hiddenNameFlag
        +execute(context)
    }

    class MorphUIPage {
        +MorphUIPage(playerRef)
        +build(ref, commands, events, store)
        +handleDataEvent(ref, store, data)
    }

    class MorphUIEventData {
        +String playerInput
        +String mobSelection
        +boolean hideNametag
        +static CODEC
    }

    class Permissions {
        +static BASE
        +static MORPH_SELF
        +static MORPH_OTHERS
        +hasPermission(playerRef, perm)
        +canMorphSelf(playerRef)
        +canMorphOthers(playerRef)
    }

    PlayerMorphToMobPlugin --> MorphManager
    PlayerMorphToMobPlugin --> MorphCommand
    PlayerMorphToMobPlugin --> Permissions
    MorphCommand --> MorphManager
    MorphCommand --> MorphUIPage
    MorphUIPage --> MorphUIEventData
    MorphUIPage --> MorphManager
    MorphManager --> MorphData
```

## Datenfluss

### Command-Ausführung (ohne GUI)

```mermaid
sequenceDiagram
    participant P as Player
    participant C as MorphCommand
    participant PM as PermissionsModule
    participant MM as MorphManager
    participant ES as EntityStore
    participant MA as ModelAsset

    P->>C: /playermorphtomob Alex zombie --hiddenname
    C->>PM: hasPermission(playerRef, "morph.others")
    PM-->>C: true

    C->>MM: applyMorph(targetPlayerRef, "zombie")
    MM->>ES: getComponent(ref, ModelComponent)
    ES-->>MM: ModelComponent

    MM->>MM: Store original model ID + PlayerSkinComponent
    MM->>MA: getAssetMap().getAsset("zombie")
    MA-->>MM: ModelAsset

    MM->>MM: Model.createScaledModel(asset, 1.0f)
    MM->>ES: putComponent(ref, type, new ModelComponent(newModel))

    MM-->>C: success
    C-->>P: "Player Alex morphed to zombie"
```

**Hinweis:** Model-Änderung erfolgt über `putComponent()` mit neuem `ModelComponent`, NICHT über `setModelAssetPath()`!

### GUI-basierte Ausführung

```mermaid
sequenceDiagram
    participant P as Player
    participant C as MorphCommand
    participant UI as MorphUIPage
    participant Data as MorphUIEventData
    participant MM as MorphManager

    P->>C: /playermorphtomob
    C->>UI: openCustomPage()
    UI->>P: Display MorphSelector.ui

    P->>UI: Select player, mob, toggle nametag
    UI->>Data: Serialize form data
    Data-->>UI: MorphUIEventData

    UI->>UI: handleDataEvent()
    UI->>MM: applyMorph(store, ref, mobId, hideNametag)
    MM-->>UI: success

    UI->>P: sendUpdate() / Close page
```

## State Management

```mermaid
stateDiagram-v2
    [*] --> Normal: Player joins

    Normal --> SelectingMorph: /playermorphtomob (GUI)
    Normal --> Morphed: /playermorphtomob player mob

    SelectingMorph --> Normal: Cancel
    SelectingMorph --> Morphed: Apply morph

    Morphed --> Normal: /playermorphtomob reset
    Morphed --> Morphed: Change morph

    Normal --> [*]: Player leaves
    Morphed --> [*]: Player leaves (cleanup)
```

## Datei-Struktur

```
Gorduan-PlayerMorphToMob-1.0.0/
├── .claude/
│   └── docs/
│       ├── plugin-api.md
│       ├── commands.md
│       ├── ui-system.md
│       ├── permissions.md
│       ├── entity-morphing.md
│       └── architecture.md
│
├── src/
│   └── com/
│       └── gorduan/
│           └── hytale/
│               └── playermorphtomob/
│                   ├── PlayerMorphToMobPlugin.java
│                   ├── MorphManager.java
│                   ├── Permissions.java
│                   │
│                   ├── commands/
│                   │   └── MorphCommand.java
│                   │
│                   ├── ui/
│                   │   ├── MorphUIPage.java
│                   │   └── MorphUIEventData.java
│                   │
│                   └── data/
│                       ├── MorphData.java
│                       └── MobRegistry.java
│
├── resources/
│   └── Common/
│       └── UI/
│           └── Custom/
│               └── Pages/
│                   └── MorphSelector.ui
│
├── META-INF/
│   └── MANIFEST.MF
│
├── manifest.json
└── .Claude.md
```

## Entity Component Beziehungen

```mermaid
erDiagram
    PLAYER ||--o{ MODEL_COMPONENT : has
    PLAYER ||--o{ NAMETAG_COMPONENT : has
    PLAYER ||--o{ EFFECT_CONTROLLER : has
    PLAYER ||--|| UUID_COMPONENT : has
    PLAYER ||--o{ TRANSFORM_COMPONENT : has

    MORPH_DATA ||--|| PLAYER : tracks
    MORPH_DATA {
        string originalModel
        boolean originalNametagVisible
        string currentMobModel
        boolean nametagHidden
        long timestamp
    }

    ACTIVE_MORPHS ||--o{ MORPH_DATA : contains
    ACTIVE_MORPHS {
        Map uuid_to_data
    }
```

## UI-Komponenten Hierarchie

```mermaid
graph TB
    subgraph "MorphSelector.ui"
        Container[Container]
        Container --> Title[Title Group]
        Container --> Content[Content Group]

        Content --> PlayerSection[Player Selection]
        PlayerSection --> PlayerLabel[Label: Select Player]
        PlayerSection --> PlayerDropdown[DropdownBox #PlayerDropdown]

        Content --> MobSection[Mob Selection]
        MobSection --> MobLabel[Label: Select Mob]
        MobSection --> MobDropdown[DropdownBox #MobDropdown]

        Content --> PreviewSection[Preview Group]
        PreviewSection --> PreviewImage[Mob Preview]

        Content --> OptionsSection[Options Group]
        OptionsSection --> HideCheckbox[CheckBox #HideNametag]
        OptionsSection --> HideLabel[Label: Hide Nametag]

        Content --> ButtonSection[Button Group]
        ButtonSection --> ApplyBtn[TextButton #ApplyButton]
        ButtonSection --> ResetBtn[TextButton #ResetButton]
        ButtonSection --> CancelBtn[CancelButton #CancelButton]
    end

    style Container fill:#2d3436,color:#fff
    style Title fill:#636e72,color:#fff
    style Content fill:#636e72,color:#fff
```

## Permission Flow

```mermaid
flowchart TD
    A[Command ausgeführt] --> B{Hat Basis-Permission?}
    B -->|Nein| C[Zugriff verweigert]
    B -->|Ja| D{Target = Self?}

    D -->|Ja| E{Hat morph.self?}
    D -->|Nein| F{Hat morph.others?}

    E -->|Ja| G[Morph erlaubt]
    E -->|Nein| C

    F -->|Ja| H{Nametag toggle?}
    F -->|Nein| C

    H -->|Ja| I{Hat nametag.toggle?}
    H -->|Nein| G

    I -->|Ja| G
    I -->|Nein| J[Morph ohne Nametag-Toggle]

    style C fill:#e74c3c,color:#fff
    style G fill:#27ae60,color:#fff
    style J fill:#f39c12,color:#fff
```

## Mathematische Konzepte

### Morph-Persistenz Wahrscheinlichkeit

Die Wahrscheinlichkeit, dass ein Morph über Zeit $t$ bestehen bleibt:

$$P(persist) = e^{-\lambda t}$$

Wobei $\lambda$ die Rate von Disconnect-Events ist.

### Speicherbedarf pro Morph

$$M_{morph} = S_{originalModel} + S_{currentModel} + S_{metadata}$$

Geschätzter Speicher pro aktivem Morph:

$$M_{total} \approx 256 + 256 + 64 = 576 \text{ bytes}$$

Bei $n$ aktiven Morphs:

$$M_{gesamt} = n \times 576 \text{ bytes}$$

### UUID-Kollisionswahrscheinlichkeit

Bei UUIDv4 (122 random bits):

$$P(collision) = 1 - e^{-\frac{n^2}{2 \times 2^{122}}}$$

Praktisch vernachlässigbar selbst bei Milliarden Spielern.

## API-Schnittstellenübersicht

### Öffentliche API

```java
// MorphManager API
public interface IMorphManager {
    // Model-Änderung: Verwendet putComponent() mit neuem ModelComponent
    boolean applyMorph(PlayerRef playerRef, String modelId);
    boolean resetMorph(PlayerRef playerRef);
    boolean isMorphed(String playerName);
    MorphData getMorphData(String playerName);
    List<String> getAvailableMobs();
    Set<String> getGameModelAssets();
    boolean isValidModel(String modelId);
    void cleanup();
}

// Custom Events (optional implementierbar)
public class PlayerMorphEvent extends IBaseEvent<Void> {
    PlayerRef getPlayerRef();
    String getOldModelId();
    String getNewModelId();
}

public class PlayerUnmorphEvent extends IBaseEvent<Void> {
    PlayerRef getPlayerRef();
    String getMorphedModelId();
}
```

## Fehlerbehandlung

```mermaid
flowchart TD
    A[Morph-Anfrage] --> B{Player existiert?}
    B -->|Nein| C[PlayerNotFoundException]
    B -->|Ja| D{Model existiert?}

    D -->|Nein| E[ModelNotFoundException]
    D -->|Ja| F{Permission OK?}

    F -->|Nein| G[PermissionDeniedException]
    F -->|Ja| H{ECS-Zugriff OK?}

    H -->|Nein| I[ComponentAccessException]
    H -->|Ja| J[Morph erfolgreich]

    C --> K[Error Response]
    E --> K
    G --> K
    I --> K
    J --> L[Success Response]

    style J fill:#27ae60,color:#fff
    style K fill:#e74c3c,color:#fff
    style L fill:#27ae60,color:#fff
```

## Deployment-Diagramm

```mermaid
graph TB
    subgraph "Server"
        HytaleServer[Hytale Server JAR]
        PluginDir[/mods/ Directory]
        ConfigDir[/config/ Directory]

        subgraph "Plugin"
            JAR[PlayerMorphToMob.jar]
            Assets[UI Assets]
        end

        PluginDir --> JAR
        JAR --> Assets
    end

    subgraph "Clients"
        Client1[Player 1]
        Client2[Player 2]
        ClientN[Player N]
    end

    HytaleServer --> PluginDir
    HytaleServer --> ConfigDir
    Client1 <-->|Auto-Sync Assets| HytaleServer
    Client2 <-->|Auto-Sync Assets| HytaleServer
    ClientN <-->|Auto-Sync Assets| HytaleServer
```

## Technologie-Stack

| Komponente | Technologie |
|------------|-------------|
| Runtime | OpenJDK 25 |
| Server | Hytale Server API |
| Plugin-System | JavaPlugin (com.hypixel.hytale.server.core.plugin) |
| UI-Format | .ui (Hytale-spezifisch, NoesisGUI in Planung) |
| Daten-Serialisierung | BuilderCodec / JSON |
| Logging | HytaleLogger (java.util.logging) |
| Build | Gradle 9.2.0 |
| IDE | IntelliJ IDEA (empfohlen) |

## Quellen

- [Hytale Modding Strategy](https://hytale.com/news/2025/11/hytale-modding-strategy-and-status)
- [Hytale Server Docs](https://hytale-docs.pages.dev/)
- [Hytale Modding Dev](https://hytalemodding.dev/en/docs)
- [Britakee Documentation](https://britakee-studios.gitbook.io/hytale-modding-documentation)
