# TheExpanseV2 Repository

## Overview
TheExpanseV2 is a Gradle-based Java project containing multiple components, with the primary focus being the **Tectonic** Minecraft mod for terrain generation.

## Project Structure

### Root Directory
- **Main Project**: Simple Gradle Java project with a basic `Main.java` class
- **Build System**: Gradle with wrapper scripts (gradlew, gradlew.bat)
- **Group ID**: `org.example`
- **Version**: `1.0-SNAPSHOT`

### `/tectonic-rewrite-squared` - Tectonic Mod
A sophisticated Minecraft mod that transforms terrain generation. The mod is a modular project supporting multiple Minecraft versions and mod loaders.

#### Directory Structure
```
tectonic-rewrite-squared/
├── src/
│   ├── common/main/java/dev/worldgen/tectonic/
│   │   ├── client/              # Client-side features (ConfigListBuilder)
│   │   ├── command/             # Commands (TectonicCommand)
│   │   ├── config/              # Configuration system (ConfigHandler, ConfigState)
│   │   │   └── state/
│   │   │       └── object/      # Config objects (HeightLimits, NoiseState)
│   │   ├── mixin/               # Minecraft behavior modifications via Mixin
│   │   │   └── client/
│   │   ├── worldgen/
│   │   │   └── densityfunction/ # Custom density functions (ConfigClamp, ConfigConstant, ConfigNoise, Invert)
│   │   ├── Tectonic.java        # Main mod class
│   │   └── TectonicTags.java    # Minecraft tag definitions
│   │
│   ├── fabric/                  # Fabric mod loader implementations
│   │   ├── 1.20.1/main/java/dev/worldgen/tectonic/
│   │   ├── 1.21.1/main/java/dev/worldgen/tectonic/
│   │   └── 1.21.10/main/java/dev/worldgen/tectonic/
│   │
│   ├── forge/                   # Forge mod loader implementations
│   │   └── 1.20.1/main/java/dev/worldgen/tectonic/
│   │
│   └── neoforge/                # NeoForge mod loader implementations
│       └── 1.21.1/main/java/dev/worldgen/tectonic/
│
├── build.gradle.kts
├── gradle.properties
├── README.md
└── uploader.py
```

## Key Features

### Configuration System
- **ConfigHandler**: Manages configuration loading and persistence
- **ConfigState**: Stores current configuration state
- **ConfigPresets**: Pre-defined configuration templates (V1ConfigState, V2ConfigState)
- **HeightLimits**: Configuration for height constraints
- **NoiseState**: Configuration for noise generation parameters

### Terrain Generation
- **Density Functions**: Custom implementations for terrain generation
  - `ConfigClamp`: Clamps values within configured limits
  - `ConfigConstant`: Provides constant values
  - `ConfigNoise`: Generates configurable noise
  - `Invert`: Inverts density function values

### Mixins
Tectonic modifies Minecraft internals through Mixins targeting:
- Biome generation
- Chunk access and generation
- Blending data
- Dimension types
- Heightmaps
- Noise-based chunk generation
- Noise settings
- Structure pieces
- Temperature modifiers
- World carvers

## Build System

### Gradle Configuration
- **Plugin**: Java
- **Java Testing**: JUnit 5 (Jupiter)
- **Repositories**: Maven Central

### Gradle Properties
- Supports multiple Minecraft versions and mod loaders through version-specific source sets

### Build Artifacts
Built from `build.gradle.kts` files in both root and tectonic-rewrite-squared directories

## Supported Platforms

### Fabric Mod Loader
- Minecraft 1.20.1
- Minecraft 1.21.1
- Minecraft 1.21.10

### Forge Mod Loader
- Minecraft 1.20.1

### NeoForge Mod Loader
- Minecraft 1.21.1

## External Resources
- **Modrinth**: https://modrinth.com/mod/tectonic

## Testing
- **Framework**: JUnit 5
- **Test Sources**: `src/test/java/`
- **Test Configuration**: Custom test task with JUnit Platform launcher

## Notable Components

### TectonicCommand
Provides in-game commands for mod configuration and management

### TectonicModMenuCompat
Compatibility layer for ModMenu on Fabric loaders

### ConfigResourceCondition
Resource condition handler for Fabric 1.21.1+ and NeoForge

### Uploaders
`uploader.py` - Python script for uploading mod distributions

## Development Notes
- Multi-platform support requires careful version management across different mod loaders
- Configuration system separates concerns between preset templates and runtime state
- Mixins allow deep integration with Minecraft's world generation pipeline
- Custom density functions enable flexible terrain generation customization
