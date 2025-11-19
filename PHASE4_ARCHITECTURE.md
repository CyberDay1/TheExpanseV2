# Phase 4: Feature Implementation (Mega Trees, Coral Reefs, Deep Caves)

## Overview

Phase 4 implements custom world generation features leveraging the Phase 3 worldgen pipeline. Three major feature generators are created:

1. **MegaTreeGenerator** - Generates massive trees in mountain biomes (seaLevel + 64 to seaLevel + 512)
2. **CoralReefGenerator** - Generates coral reefs in ocean depths (seaLevel - 128 to seaLevel)
3. **DeepCaveGenerator** - Generates deep cave systems in extreme depths (seaLevel - 256 to seaLevel - 128)

All features integrate with `BiomePalette` for altitude band validation and `StructurePlacementHooks` for height constraint enforcement.

---

## Architecture Components

### 1. MegaTreeGenerator

**File**: `features/MegaTreeGenerator.java`

**Purpose**: Generate massive trees with configurable height ranges and biome filtering

**Constructor**:
```java
MegaTreeGenerator(TreeFeatureConfig config, BiomePalette biomePalette)
```

**Configuration** (from FeatureRegistry):
- Min Height: 64 blocks
- Max Height: 512 blocks
- Frequency: 0.08 (8% of eligible locations)
- Biome Filter: "mountain" (case-insensitive substring match)

**Methods**:
```
canPlaceTreeAt(x, y, z, biomeName)    → Validate placement (height, biome, structure bounds)
placeTree(x, y, z)                    → Place tree and log
getFrequency()                         → Spawn chance (0-1)
getMinHeight() / getMaxHeight()        → Height range
getTreesGenerated()                    → Statistics counter
```

**Height Validation**:
- Uses `StructurePlacementHooks.StructureType.PILLAGER_OUTPOST` bounds
- Valid range: seaLevel to seaLevel + 256 (from structure hook)
- Additional filter: seaLevel + 64 to seaLevel + 512 (feature-specific)

**Biome Filtering**:
- Checks if biome name contains configured filter string
- Empty filter = all biomes accepted

---

### 2. CoralReefGenerator

**File**: `features/CoralReefGenerator.java`

**Purpose**: Generate coral reef structures in ocean biomes at intermediate depths

**Constructor**:
```java
CoralReefGenerator(BiomePalette biomePalette)
```

**Auto-Configuration** (from BiomePalette sea level):
- Min Depth: seaLevel - 128
- Max Depth: seaLevel
- Frequency: 0.15 (15% of eligible locations)

**Methods**:
```
canPlaceReefAt(x, y, z, biomeName)    → Validate placement (depth, biome, structure bounds)
placeReef(x, y, z)                    → Place reef and log
getFrequency()                         → Spawn chance (0-1)
getMinDepth() / getMaxDepth()          → Depth range
getReefsGenerated()                    → Statistics counter
```

**Depth Validation**:
- Uses `StructurePlacementHooks.StructureType.OCEAN_MONUMENT` bounds
- Ocean Monument range: seaLevel - 256 to seaLevel (from structure hook)
- Feature range: seaLevel - 128 to seaLevel (subset of valid range)

**Biome Filtering**:
- Accepts biomes containing "ocean" or "deep_dark"
- Deep dark included for experimental deep sea variants

---

### 3. DeepCaveGenerator

**File**: `features/DeepCaveGenerator.java`

**Purpose**: Generate deep cave systems in extreme depths with sparse placement

**Constructor**:
```java
DeepCaveGenerator(BiomePalette biomePalette)
```

**Auto-Configuration** (from BiomePalette sea level):
- Min Depth: seaLevel - 256
- Max Depth: seaLevel - 128
- Frequency: 0.25 (25% of eligible locations)

**Methods**:
```
canPlaceCaveAt(x, y, z)                → Validate placement (depth, structure bounds)
placeCave(x, y, z)                    → Place cave system and log
getFrequency()                         → Spawn chance (0-1)
getMinDepth() / getMaxDepth()          → Depth range
getCavesGenerated()                    → Statistics counter
```

**Depth Validation**:
- Uses `StructurePlacementHooks.StructureType.ANCIENT_CITY` bounds
- Ancient City range: seaLevel - 256 to seaLevel - 128 (from structure hook)
- Perfect alignment with deep cave placement bounds

**No Biome Filtering**:
- Deep caves exist in all biome types at extreme depths
- Only Y coordinate validation required

---

### 4. FeatureRegistry

**File**: `features/FeatureRegistry.java`

**Purpose**: Singleton managing all three feature generators with lazy initialization

**Singleton Pattern**:
```java
FeatureRegistry.getInstance()          → Get registry instance
```

**Initialization**:
```java
void initialize(BiomePalette biomePalette)
```

Called once from platform hooks with `BiomePalette` reference. Creates all three generators with appropriate configurations.

**Access Methods**:
```
getMegaTreeGenerator()                 → Get MegaTreeGenerator instance
getCoralReefGenerator()                → Get CoralReefGenerator instance
getDeepCaveGenerator()                 → Get DeepCaveGenerator instance
isInitialized()                        → Check init status
reset()                                → Clear state (testing)
logStatistics()                        → Log generation counters
```

**Initialization Sequence**:
1. Check if already initialized (idempotent)
2. Create `TreeFeatureConfig` for mega trees
3. Instantiate `MegaTreeGenerator` with config
4. Instantiate `CoralReefGenerator` with biome palette
5. Instantiate `DeepCaveGenerator` with biome palette
6. Set `initialized` flag
7. Log initialization complete

---

## Integration with Phase 3

### BiomePalette Integration
- **MegaTreeGenerator**: Uses `getBiomeForHeight()` to validate placement in mountain bands
- **CoralReefGenerator**: Uses sea level for depth calculations
- **DeepCaveGenerator**: Uses sea level to determine extreme depth bounds

### StructurePlacementHooks Integration
All generators validate placement heights against vanilla structure constraints:

| Feature | Structure Type | Valid Height Range |
|---------|---------------|--------------------|
| Mega Trees | PILLAGER_OUTPOST | seaLevel to seaLevel + 256 |
| Coral Reefs | OCEAN_MONUMENT | seaLevel - 256 to seaLevel |
| Deep Caves | ANCIENT_CITY | seaLevel - 256 to seaLevel - 128 |

---

## Platform Integration

### Updated Platform Hooks
Both `PlatformHooks_1_21_1.java` and `PlatformHooks_1_21_5.java` now:

```java
private void initializeFeatures() {
    LOGGER.debug("Initializing features for 1.21.x");
    FeatureRegistry.getInstance().initialize(
        WorldGenInitializer.getBiomePalette()
    );
}
```

Called during `init()` after world generation initialization:
```
init()
├→ registerOreProfiles()
├→ initializeWorldGen()
│  └→ WorldGenInitializer.initialize()
├→ initializeFeatures()          ← NEW
│  └→ FeatureRegistry.initialize()
│     ├→ MegaTreeGenerator(config, biomePalette)
│     ├→ CoralReefGenerator(biomePalette)
│     └→ DeepCaveGenerator(biomePalette)
├→ registerChunkGenerator()
├→ registerBiomeModifiers()
├→ hookBiomeLoadingEvents()
└→ registerVerticalSectionIntegration()
```

---

## Data Flow: Initialization to Feature Placement

```
Mod Startup
    ↓
VerticalExpansion.init()
    ↓
Platform.hooks().init()
    ├→ PlatformHooks_1_21_x.init()
    │  ├→ registerOreProfiles()
    │  ├→ initializeWorldGen()
    │  │  └→ WorldGenInitializer.initialize()
    │  │     ├→ WorldHeightConfig
    │  │     ├→ WorldTerrainProfile (standard)
    │  │     ├→ 5× PerlinNoiseSampler
    │  │     ├→ NoiseBasedTerrainHeightFunction
    │  │     ├→ DensityFunctionIntegration
    │  │     ├→ BiomePalette (5 altitude bands)
    │  │     └→ ChunkGeneratorRegistry.registerChunkGenerator()
    │  │
    │  ├→ initializeFeatures()
    │  │  └→ FeatureRegistry.getInstance().initialize(biomePalette)
    │  │     ├→ TreeFeatureConfig (64-512 blocks, 8% freq, "mountain" biome)
    │  │     ├→ MegaTreeGenerator(config, biomePalette)
    │  │     ├→ CoralReefGenerator(biomePalette)
    │  │     │  └→ Depth range: seaLevel-128 to seaLevel, 15% freq
    │  │     ├→ DeepCaveGenerator(biomePalette)
    │  │     │  └→ Depth range: seaLevel-256 to seaLevel-128, 25% freq
    │  │     └→ Set initialized flag
    │  │
    │  └→ registerChunkGenerator()
    │     registerBiomeModifiers()
    │     hookBiomeLoadingEvents()
    │     registerVerticalSectionIntegration()
```

---

## Feature Placement Algorithm (High-Level)

### Per-Chunk Generation

```
For each chunk in world generation:
    For each target feature generator:
        For each potential placement location in chunk:
            
            Mega Trees:
                If random() < 0.08:                    // Frequency check
                    If canPlaceTreeAt(x, y, z, biome):  // Validation
                        placeTree(x, y, z)
                        
            Coral Reefs:
                If random() < 0.15:                    // Frequency check
                    If canPlaceReefAt(x, y, z, biome):  // Validation
                        placeReef(x, y, z)
                        
            Deep Caves:
                If random() < 0.25:                    // Frequency check
                    If canPlaceCaveAt(x, y, z):        // Validation
                        placeCave(x, y, z)
```

### Validation for Each Feature

**MegaTreeGenerator.canPlaceTreeAt**:
1. Check biome contains "mountain" string
2. Check Y >= 64 and Y <= 512
3. Check `StructurePlacementHooks.isValidPlacementHeight(PILLAGER_OUTPOST, y, seaLevel)`
4. Return true if all checks pass

**CoralReefGenerator.canPlaceReefAt**:
1. Check biome contains "ocean" or "deep_dark"
2. Check Y >= (seaLevel - 128) and Y <= seaLevel
3. Check `StructurePlacementHooks.isValidPlacementHeight(OCEAN_MONUMENT, y, seaLevel)`
4. Return true if all checks pass

**DeepCaveGenerator.canPlaceCaveAt**:
1. Check Y >= (seaLevel - 256) and Y <= (seaLevel - 128)
2. Check `StructurePlacementHooks.isValidPlacementHeight(ANCIENT_CITY, y, seaLevel)`
3. Return true if all checks pass

---

## Testing Checklist

- [ ] `FeatureRegistry` initializes exactly once (idempotent)
- [ ] `MegaTreeGenerator` validates mountain biome placement correctly
- [ ] `MegaTreeGenerator` respects min/max height constraints
- [ ] `CoralReefGenerator` validates ocean biome placement correctly
- [ ] `CoralReefGenerator` respects depth constraints (seaLevel-128 to seaLevel)
- [ ] `DeepCaveGenerator` validates extreme depth constraints (seaLevel-256 to seaLevel-128)
- [ ] All three generators initialize with correct frequency values
- [ ] `StructurePlacementHooks` integration prevents out-of-bounds placement
- [ ] Feature counters increment correctly during generation
- [ ] All logging occurs at DEBUG level with proper metrics
- [ ] Platform hooks call feature initialization after worldgen setup
- [ ] FeatureRegistry.reset() clears all state (for testing)

---

## File Summary

| File | Lines | Purpose |
|------|-------|---------|
| MegaTreeGenerator.java | ~65 | Mountain tree generation with height/biome validation |
| CoralReefGenerator.java | ~70 | Ocean reef generation with depth constraints |
| DeepCaveGenerator.java | ~60 | Deep cave system generation in extreme depths |
| FeatureRegistry.java | ~85 | Singleton coordinator for all three generators |
| PlatformHooks_1_21_1.java | +8 | Added initializeFeatures() method and call |
| PlatformHooks_1_21_5.java | +8 | Added initializeFeatures() method and call |

**Total New Code**: ~280 Java lines across 4 new files + 16 updated lines in platform hooks

---

## Next Phases

### Phase 5: Event Hooks & Integration
- Wire feature generators to NeoForge chunk generation events
- Integrate with `LevelChunkGenContext` for actual world modification
- Handle multi-chunk feature placement (mega trees, cave systems)

### Phase 6: Configuration & Balancing
- Expose feature frequencies and height ranges to configuration
- Add enable/disable toggles for each feature
- Server/client config synchronization

### Phase 7: Performance Optimization
- Cache structure placement checks
- Optimize noise sampling for feature distribution
- Profile chunk generation performance

---

## Architecture Decisions

1. **Separation of Concerns**: Each generator handles only its feature type
2. **BiomePalette Dependency**: All generators receive reference, not create new instance
3. **Height Validation Reuse**: All use existing `StructurePlacementHooks` enum and methods
4. **Lazy Initialization**: `FeatureRegistry` initializes once on first platform hook call
5. **Statistics Tracking**: Each generator tracks placement count for debugging/metrics
6. **No World Modification**: Current implementation validates placement only (no actual block placement)
