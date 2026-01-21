# MorphPlayerTo

A Hytale server plugin that allows players to transform into any mob model in the game.

## Features

- **Morph into any mob** - Transform yourself into any of 400+ mob models
- **Morph other players** - Admins can morph other players
- **Persistent morphs** - Your morph survives server restarts and reconnects
- **In-game GUI** - Browse and select mobs from a visual interface
- **Fuzzy name matching** - Type "Kweebec" and it finds "Kweebec_Worker"
- **Permission system** - Fine-grained control over who can morph

## Installation

1. Download the latest `Gorduan-MorphPlayerTo-x.x.x.jar` from [Releases](https://github.com/gorduan/Hytale-PlayerMorph/releases)
2. Place the JAR file in your server's `Mods` folder
3. Restart the server

## Commands

| Command | Description |
|---------|-------------|
| `/morphplayerto` | Opens the morph selection GUI |
| `/morphplayerto <mob>` | Morph into a specific mob |
| `/morphplayerto <mob> <player>` | Morph another player (requires permission) |
| `/morphplayerto reset` | Return to your normal player form |
| `/morphplayerto reset <player>` | Reset another player's morph |
| `/morphplayerto list` | List all available mobs |
| `/morphplayerto help` | Show help message |

### Examples

```
/morphplayerto Trork          # Morph into a Trork
/morphplayerto Kweebec        # Finds Kweebec_Worker (fuzzy match)
/morphplayerto goblin         # Case-insensitive
/morphplayerto reset          # Back to normal
```

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `morphplayerto.morph.self` | Morph yourself | OP |
| `morphplayerto.morph.others` | Morph other players | OP |
| `morphplayerto.gui` | Use the morph GUI | OP |
| `morphplayerto.command` | Use the morph command | OP |
| `morphplayerto.*` | All permissions | OP |

## Building from Source

### Requirements

- Java 25+
- Hytale Server (for reference)

### Build

```bash
# Windows
.\build.bat

# Or using PowerShell
.\build.ps1
```

The compiled JAR will be in the `build/` directory.

## Project Structure

```
MorphPlayerTo/
├── src/                          # Plugin source code
│   └── com/gorduan/hytale/morphplayerto/
│       ├── MorphPlayerToPlugin.java    # Main plugin class
│       ├── MorphManager.java           # Morph logic
│       ├── Permissions.java            # Permission constants
│       ├── commands/
│       │   └── MorphCommand.java       # Command handler
│       ├── data/
│       │   └── MorphData.java          # Morph data model
│       ├── storage/
│       │   └── MorphStorage.java       # Persistence
│       └── ui/
│           ├── MorphUIPage.java        # GUI handler
│           └── MorphUIEventData.java   # UI events
├── stubs/                        # Compile-time stubs for Hytale API
├── resources/Common/UI/          # UI definition files
├── manifest.json                 # Plugin manifest
├── build.bat                     # Windows build script
└── build.ps1                     # PowerShell build script
```

## Technical Details

- Uses Hytale's ECS (Entity Component System) for model changes
- All ECS operations are thread-safe via `world.execute()`
- Morphs are persisted to JSON files in `Mods/MorphPlayerTo/`
- Model changes sync automatically to all connected clients

## License

MIT License - see [LICENSE](LICENSE) for details.

## Author

**Gorduan** - [GitHub](https://github.com/Gorduan)

---

Made for Hytale Server
