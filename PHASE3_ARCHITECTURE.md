# Phase 3: Chunk Generator Registration & Worldgen Integration

## Overview

Phase 3 establishes the worldgen pipeline for VerticalExpansion by:
1. **Centralizing generator state** via `ChunkGeneratorRegistry`
2. **Mapping altitudes to biomes** via `BiomePalette` (5 altitude bands)
3. **Integrating density functions** via `DensityFunctionIntegration`
4. **Validating structure placement** via `StructurePlacementHooks` (11 vanilla structures)
5. **Orchestrating initialization** via `WorldGenInitializer`

---

## Architecture Components

### 1. ChunkGeneratorRegistry
**File**: `worldgen/ChunkGeneratorRegistry.java`  
**Purpose**: Singleton tracking the active chunk generator

```
registerChunkGenerator()        → Log registration event
getActiveGenerator()            → Return current generator
setActiveGenerator(gen)         → Track active instance
isVerticalExpansionWorld()      → Check if VE is active
```

**Key Pattern**: Thread-safe singleton with logging for debug tracking.

---

### 2. BiomePalette
**File**: `worldgen/BiomePalette.java`  
**Purpose**: Map altitude bands to biomes

**5 Altitude Bands**:
- **Band 0** (Deep): `minecraft:deep_dark` (height < seaLevel - 64)
- **Band 1** (Ocean): `minecraft:ocean` (seaLevel - 64 to seaLevel)
- **Band 2** (Plains): `minecraft:plains` (seaLevel to seaLevel + 128)
- **Band 3** (Mountains): `minecraft:mountains` (seaLevel + 128 to seaLevel + 256)
- **Band 4** (Sky): `minecraft:snowy_peaks` (height > seaLevel + 256)

**Methods**:
```
getBiomeForBand(band)           → Get biome name by band index
getBiomeForHeight(height)       → Determine band from Y coordinate
setBiomeForBand(band, name)     → Override biome for band (modding)
getSeaLevel()                   → Retrieve configured sea level
```

**Integration Point**: Used by `VerticalExpansionBiomeSource` to select biomes during chunk generation.

---

### 3. DensityFunctionIntegration
**File**: `worldgen/DensityFunctionIntegration.java`  
**Purpose**: Adapter linking terrain height function to NeoForge density function pipeline

**Composition**:
- Wraps `TerrainHeightFunction` (computes elevation)
- Wraps `WorldTerrainProfile` (noise parameters)
- Creates `TerrainHeightDensityFunction` (NeoForge adapter)

**Methods**:
```
getDensityFunction()            → Return wrapped density function
getHeightFunction()             → Access elevation calculator
getTerrainProfile()             → Access noise configuration
initialize()                    → Log initialization metrics
```

**Initialization**:
```java
new DensityFunctionIntegration(terrainHeightFunction, terrainProfile)
  .initialize()
```

---

### 4. StructurePlacementHooks
**File**: `worldgen/StructurePlacementHooks.java`  
**Purpose**: Validate and adjust structure placement heights for expanded world

**Supported Structures** (11 types):
- Stronghold: `seaLevel - 128` to `seaLevel + 64`
- Village: `seaLevel - 64` to `seaLevel + 128`
- Shipwreck: `seaLevel - 128` to `seaLevel`
- Ocean Monument: `seaLevel - 256` to `seaLevel`
- Fortress: `seaLevel - 256` to `seaLevel - 64`
- Jungle/Desert Temple: `seaLevel` to `seaLevel + 200`
- Swamp Hut: `seaLevel - 16` to `seaLevel + 128`
- Pillager Outpost: `seaLevel` to `seaLevel + 256`
- Ancient City: `seaLevel - 256` to `seaLevel - 128`
- Deep Dark City: `seaLevel - 256` to `seaLevel - 64`

**Methods**:
```
isValidPlacementHeight(type, y, seaLevel)    → Check if height valid for structure
adjustHeightForStructure(type, y, seaLevel)  → Clamp to valid range
logStructurePlacement(type, x, y, z)         → Debug logging
```

**Pattern**: Switch expression with tight height bounds for each structure.

---

### 5. WorldGenInitializer
**File**: `worldgen/WorldGenInitializer.java`  
**Purpose**: Orchestrates all worldgen initialization (called once on startup)

**Initialization Sequence**:
1. Validate if already initialized (idempotent)
2. Retrieve `WorldHeightConfig` from `VerticalExpansionConfig`
3. Create standard `WorldTerrainProfile` with noise parameters
4. Initialize 5 Perlin noise samplers
5. Create `NoiseBasedTerrainHeightFunction` with samplers
6. Wrap in `DensityFunctionIntegration`
7. Create `BiomePalette` with sea level
8. Register chunk generator via `ChunkGeneratorRegistry`
9. Set `initialized` flag to prevent re-initialization

**Lazy Access**:
```java
WorldGenInitializer.getDensityFunctionIntegration()  // Auto-init if needed
WorldGenInitializer.getBiomePalette()                // Auto-init if needed
WorldGenInitializer.reset()                          // Clear state (testing)
```

**Called From**: `PlatformHooks_1_21_1` and `PlatformHooks_1_21_5` during `init()`

---

## Integration with Existing Code

### Platform Layer
Both `PlatformHooks_1_21_1.java` and `PlatformHooks_1_21_5.java` now:
```java
private void initializeWorldGen() {
    LOGGER.debug("Initializing world generation system for 1.21.x");
    WorldGenInitializer.initialize();
}
```

Called during `init()` before chunk generator registration hooks.

### Existing Worldgen Files
- **VerticalExpansionChunkGenerator**: Updated to use `ChunkGeneratorRegistry`
- **VerticalExpansionBiomeSource**: Uses `BiomePalette` for band-to-biome mapping
- **TerrainHeightDensityFunction**: Wrapped by `DensityFunctionIntegration`
- **VerticalExpansionWorldgenEvents**: Enhanced with mod event bus listener class

---

## Data Flow Diagram

```
Mod Startup
    ↓
VerticalExpansion.init()
    ↓
Platform.hooks().init()
    ↓
PlatformHooks_1_21_x.init()
    ├→ registerOreProfiles()
    ├→ initializeWorldGen()
    │   ↓
    │   WorldGenInitializer.initialize()
    │   ├→ WorldHeightConfig (from VerticalExpansionConfig)
    │   ├→ WorldTerrainProfile (standard)
    │   ├→ 5× PerlinNoiseSampler
    │   ├→ NoiseBasedTerrainHeightFunction
    │   ├→ DensityFunctionIntegration (wraps height function)
    │   ├→ BiomePalette (5 altitude bands)
    │   └→ ChunkGeneratorRegistry.registerChunkGenerator()
    └→ registerChunkGenerator()
       registerBiomeModifiers()
       hookBiomeLoadingEvents()
       registerVerticalSectionIntegration()
```

---

## Altitude Band Heights (Example: seaLevel = 64)

| Band | Biome | Y Range | Elevation |
|------|-------|---------|-----------|
| 0 | deep_dark | < 0 | Deep caverns, ancient cities |
| 1 | ocean | 0–64 | Ocean floor, underwater |
| 2 | plains | 64–192 | Surface, villages, temples |
| 3 | mountains | 192–320 | Peaks, outposts, ravines |
| 4 | snowy_peaks | > 320 | Sky realm, extreme peaks |

---

## Next Phase: Feature Implementation

Phase 4 will use these components to generate:
- **Mega trees** (height-adjusted, placed in mountain bands)
- **Coral reefs** (deep ocean band with structure hooks)
- **Deep cave systems** (extreme depth using Ancient City bounds)
- **Custom structures** (leveraging `StructurePlacementHooks` for height validation)

---

## Testing Checklist

- [ ] `WorldGenInitializer` initializes exactly once (idempotent)
- [ ] `BiomePalette` correctly maps 5 bands for given sea level
- [ ] `StructurePlacementHooks` clamps all 11 structure types to valid ranges
- [ ] `DensityFunctionIntegration` wraps height function without errors
- [ ] `ChunkGeneratorRegistry` tracks active generator state
- [ ] All logging at DEBUG level includes metrics (scale factors, sea level, band counts)
- [ ] NeoForge event handlers fire correctly with `@EventBusSubscriber`
