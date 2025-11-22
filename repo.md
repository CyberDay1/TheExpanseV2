# TheExpanseV2 Repository

## Overview

**TheExpanseV2** (VerticalExpansion) is a **NeoForge mod** for **Minecraft 1.21.1–1.21.10** that transforms world generation by **globally extending the Overworld's vertical height** and replacing vanilla terrain.

**Core Features:**
- Extends Overworld world height from **Y -256 → Y 1024** (configurable) for **all overworlds** while the mod is installed
- Ensures there is **never a vanilla-height Overworld** when this mod is present – the vanilla `minecraft:overworld` dimension type and the Normal world preset are both overridden to use the extended range
- Replaces vanilla Overworld terrain with a multi-noise terrain pipeline using the `verticalexpansion:vertical_noise` settings
- Adds large-scale features: mega mountains, deep caves, massive trees, coral reefs
- Implements a **Vertical Section Manager** to keep performance optimal at tall heights
- Maintains multiversion support across patch versions using platform abstraction layer

---

## Project Metadata

- **Group ID**: `com.cyber3d.verticalexpansion`
- **Module ID**: `verticalexpansion`
- **Version**: `0.1.0`
- **Target Minecraft**: `1.21.1–1.21.10`
- **Mod Loader**: NeoForge (`21.1.209`)
- **Java Version**: 21 (LTS)

---

## Directory Structure

```
C:\Users\conno\IdeaProjects\TheExpanseV2/
├── docs/                          # Design and architecture documentation
│   ├── architecture.md            # High-level system design
│   ├── terrain_design.md          # Noise fields, height bands, terrain shaping
│   ├── vertical_sections.md       # Vertical activity policy and manager
│   ├── multiversion_support.md    # Version detection and platform hooks
│   ├── rules_for_ai.md            # Guidelines for AI code assistants
│   └── dev_debugging.md           # Debugging tips and development notes
│
├── src/main/
│   ├── java/com/cyber3d/verticalexpansion/
│   │   ├── core/                  # World height config, registries, utilities
│   │   ├── terrain/               # Noise fields, terrain profiles, height functions
│   │   ├── worldgen/              # Chunk generator, noise router integration
│   │   ├── features/              # Trees, coral, mega caves, structures
│   │   ├── ore/                   # Ore profiles, ore placement, modded ore support
│   │   ├── vertical/              # VerticalSectionManager, VerticalSectionPolicy
│   │   ├── platform/              # Version detection, PlatformHooks implementations
│   │   └── api/                   # Public extension points
│   │
│   └── resources/
│       ├── data/
│       │   ├── minecraft/         # Vanilla-namespace worldgen data
│       │   └── verticalexpansion/ # Custom mod data (biome modifiers, features, etc.)
│       └── META-INF/
│           └── neoforge.mods.toml # NeoForge mod metadata
│
├── src/test/
│   └── java/                      # JUnit 5 test suite
│
├── build.gradle.kts               # Gradle build configuration
├── gradle.properties              # Minecraft version, NeoForge version, mod metadata
├── settings.gradle.kts            # Gradle project settings
│
├── IMPLEMENTATION_STATUS.md       # Feature checklist and progress tracking
├── PHASE3_ARCHITECTURE.md         # Phase 3 design notes
├── PHASE4_ARCHITECTURE.md         # Phase 4 design notes
└── CHECKLIST_COMPLETION.md        # Task completion tracking

```

---

## Key Systems

### 1. **Terrain Generation Pipeline**

The terrain is built using **Tectonic-inspired** (but original) noise composition:

**Noise Fields:**
- **Continents**: Large-scale landmass distribution
- **Erosion**: Valley carving and weathering
- **Ridges**: Mountain range formation
- **Valleys/Rivers**: Water flow patterns
- **Detail**: Local surface variation

**Height Function (`TerrainHeightFunction`):**
- Combines noise fields into a single height calculation
- Takes X, Z coordinates and `WorldTerrainProfile`
- Returns final surface height, clamped to `[minY, maxY]`

**Height Bands (Vertical Structure):**
- **Deep Underground** (Y -256…0): Dense stone, mega caves, high ore density
- **Core Surface** (Y 0…256): Primary terrain (plains, forests, oceans)
- **Highlands** (Y 256…512): Common mountains and cliffs
- **Extreme Peaks** (Y 512…900): Rare vertical terrain, jagged ridges
- **Sky Band** (Y 900+): Optional floating islands (configurable)

See **`docs/terrain_design.md`** for full specification.

### 2. **Vertical Section Manager**

Reduces performance cost at extreme heights by managing chunk section activity:

**Concepts:**
- Vanilla chunks are divided into 16-block-tall **sections**
- Only sections near a player's current Y are kept **active**
- Active sections: tick, render, stay in memory
- Inactive sections: unloaded or stored compactly

**Section Index Formula:**
```
sectionIndex = floor((y - minY) / 16)
```

**Configuration:**
- `verticalSectionWindow`: range of active sections around players
- Configurable per-player or global

See **`docs/vertical_sections.md`** for full behavior.

### 3. **World Height Configuration**

All worldgen code derives bounds from a **`WorldHeightConfig`** interface:

```java
public interface WorldHeightConfig {
    int minY();      // Default: -256
    int maxY();      // Default: 1024
    int seaLevel();  // Default: 64
}
```

**Key Rule:** Never hard-code vanilla height constants. Always use the config.

### 4. **Ore System**

Extends vanilla ore generation to the new height range:

**OreProfile:**
- Target blocks (Minecraft tag)
- Y range (min/max)
- Distribution curve
- Vein size and frequency
- Biome filters

**OreProfileRegistry:**
- Stores vanilla and modded ore profiles
- Vanilla ores are rescaled to new height bands
- Modded ores can register custom profiles via API

### 5. **Feature Sets**

#### Massive Mountains
- Arise from continent/ridge/erosion noise interactions
- Typical peaks: 300–500 Y, rare peaks: 600–900 Y
- Configurable height and density

#### Massive Trees
- 100+ block tall variants (Oak, Dark Oak, Jungle)
- Generated as structured features with:
  - Trunk path and branch endpoints
  - Canopy blobs and decorations
- Configurable height, density, biome allowlist/denylist

#### Coral Reefs
- Multi-chunk blob structures in warm oceans
- 3D noise field controls distribution
- Configurable radius, thickness, density

#### Deep Caves & Mega Caves
- Expanded to utilize full depth (-256 to surface)
- More complex cave systems in extended depth

---

## Platform Abstraction (Multiversion Support)

The mod ships **one JAR** supporting Minecraft `1.21.1–1.21.10`.

**PlatformHooks Pattern:**
- All version-specific code lives in `platform/`
- Core code (terrain, features, vertical) is version-agnostic
- At runtime, detect Minecraft patch version and instantiate correct hook implementation

**Example:**
- `PlatformHooks_1_21_1` for early patches
- `PlatformHooks_1_21_5` for later patches (if needed)

**Rule:** Core code must never check version numbers or call patch-specific APIs directly.

See **`docs/multiversion_support.md`** for details.

---

## Build & Testing

### Gradle Configuration
- **Plugin**: `net.neoforged.moddev` 2.0+
- **Java**: Version 21
- **Testing**: JUnit 5 (Jupiter)
- **Repositories**: Maven Central, NeoForge

### Build Commands
```bash
./gradlew build        # Compile and package
./gradlew test         # Run JUnit 5 tests
./gradlew runClient    # Run Minecraft client
./gradlew runServer    # Run Minecraft server
```

### Key Gradle Configuration
In `build.gradle.kts`:
- Excludes vanilla density functions (to preserve vanilla definitions)
- Excludes vanilla dimension type override (to prevent height crashes)
- Configured for mod loader compatibility

---

## Code Conventions & Rules

1. **No Hard-Coded Vanilla Heights**
   - Use `WorldHeightConfig` for all height constants
   - Never reference vanilla min/max Y in worldgen code

2. **Version Logic Isolated to `platform/` Package**
   - Core code remains version-agnostic
   - Use `Platform.hooks()` to access version-specific behavior

3. **Document Intent Near TODOs**
   - When leaving AI-fillable TODOs, explain requirements in comments
   - Follow the pattern: `// TODO: AI IMPLEMENT` with detailed description

4. **No External Code Copying**
   - Tectonic is used as a **conceptual reference only**
   - All implementations are original (inspired by, not copied from)
   - Document inspiration in code comments if applicable

5. **Keep Docs in Sync**
   - If changing major terrain/vertical logic, update corresponding `.md` file
   - The design docs are the source of truth

6. **Tests & Invariants**
   - Add tests when modifying public logic (especially terrain/vertical)
   - Preserve key invariants:
     - Heights remain within `[minY, maxY]`
     - Vertical section windows behave as documented
     - Platform hooks remain the only source of version-specific behavior

---

## Documentation Files

| File | Purpose |
|------|---------|
| **`docs/architecture.md`** | High-level system design, core goals, structure overview |
| **`docs/terrain_design.md`** | Noise fields, height bands, terrain shaping algorithms |
| **`docs/vertical_sections.md`** | Vertical section activity policy and manager implementation |
| **`docs/multiversion_support.md`** | Version detection, platform hooks, patch-specific handling |
| **`docs/rules_for_ai.md`** | Rules for AI assistants working in this codebase |
| **`docs/dev_debugging.md`** | Development tips, debugging hints, common issues |
| **`IMPLEMENTATION_STATUS.md`** | Feature checklist and implementation progress |

---

## Development Workflow

### Getting Started
1. Read **`docs/architecture.md`** for system overview
2. Read **`docs/terrain_design.md`** for terrain pipeline
3. Read **`docs/vertical_sections.md`** for vertical system
4. Read **`docs/rules_for_ai.md`** if working with AI tools

### Adding Features
- **New terrain behavior**: Modify `WorldTerrainProfile` and `TerrainHeightFunction`, update `terrain_design.md`
- **New feature**: Add to `features/`, configure in datapack or server config
- **New ore type**: Register in `OreProfileRegistry` with `OreProfile`
- **Version-specific code**: Add to `platform/` package, never in core code

### Running Tests
```bash
./gradlew test
```

### Debugging
See **`docs/dev_debugging.md`** for debugging tips, log locations, and common issues.

---

## Project Status

- **Version**: 0.1.0 (Alpha)
- **Target Release**: Minecraft 1.21.1–1.21.10
- **Progress**: See `IMPLEMENTATION_STATUS.md` and `CHECKLIST_COMPLETION.md`

---

## External Links

- **NeoForge**: https://neoforged.net/
- **Minecraft Modding Wiki**: https://docs.neoforged.net/
- **Tectonic (Inspiration)**: https://modrinth.com/mod/tectonic (Conceptual reference only)
