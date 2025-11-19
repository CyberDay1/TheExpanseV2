# VerticalExpansion — Architecture Overview

VerticalExpansion is a NeoForge mod targeting **Minecraft 1.21.1–1.21.10**.

## Core Goals

- Extend world height to roughly **Y -256 → Y 1024** (configurable).
- Replace vanilla Overworld terrain with a **Tectonic-inspired**, but original, terrain pipeline.
- Add large-scale features (mega mountains, deep caves, massive trees, coral reefs).
- Rebuild ore generation around the expanded vertical range (vanilla + modded ores).
- Introduce a **Vertical Section Manager** that only keeps vertical slices near players active.
- Keep the codebase maintainable across patch versions using a **PlatformHooks** abstraction.

This document is the master high-level overview. Other docs go deeper into specific systems:

- `terrain_design.md` — noise fields, height function, bands, configs.
- `vertical_sections.md` — vertical chunk section policy and implementation.
- `multiversion_support.md` — how we handle 1.21.1–1.21.10 differences.
- `rules_for_ai.md` — rules for AI coding assistants working in this repo.

---

## 1. Project Structure

Suggested logical layout:

```text
src/main/java/com/cyber3d/verticalexpansion/
  core/         ← world height config, registries, utilities
  terrain/      ← noise fields, terrain profiles, height functions
  worldgen/     ← chunk generator, noise router integration
  features/     ← trees, coral, mega caves, extra structures
  ore/          ← OreProfile, OreProfileRegistry, integration hooks
  vertical/     ← VerticalSectionManager, VerticalSectionPolicy
  platform/     ← PlatformVersion, PlatformHooks_* implementations
  api/          ← public extension points (ore, features, terrain)
```

Package names are guidelines; consistency matters more than exact paths.

---

## 2. Core Concepts

### 2.1 World Height

- Default target:
  - `minY = -256`
  - `maxY = 1024` (must remain a multiple of 16 above `minY`).
- These values:
  - Gate all block placement / build-height checks.
  - Inform noise sampling and height calculation.
  - Are exposed via server config and can be changed before world creation.

A simple configuration object (in `core`) provides these:

```java
public interface WorldHeightConfig {
    int minY();
    int maxY();
    int seaLevel();
}
```

All worldgen code must derive bounds from this config, never hard-code vanilla height constants.

### 2.2 Tectonic-Inspired Terrain

- Terrain is driven by multiple 2D noise fields:
  - Continents, erosion, ridges, valleys/rivers, local detail.
- We combine these fields into a single **height function**.
- The world is conceptually divided into vertical bands:
  - Deep underground, core surface, highlands, extreme peaks, sky terrain.
- Each band can change how noise is interpreted (smooth vs jagged, plateaus vs spikes, etc.).

The exact rules live in `terrain_design.md`.

### 2.3 Vertical Section Manager

Vanilla already splits chunks into 16-block-tall sections.  
We add a **policy layer**:

- Only vertical sections close to a player’s Y level are considered “active”.
- “Active” means:
  - Ticks run (blocks, fluids, entities).
  - Data is sent to clients.
  - Sections are kept in memory.
- Sections outside the window can be:
  - Unloaded, or
  - Parked in a low-cost dormant state (implementation detail).

Spec details: `vertical_sections.md`.

### 2.4 Multiversion Support (1.21.1–1.21.10)

- We ship **one jar** for all supported patch versions.
- Version-sensitive logic is isolated behind `PlatformHooks`.
- We have patch-range implementations:
  - `PlatformHooks_1_21_1` for early patches.
  - `PlatformHooks_1_21_5` for later patches (example split).
- At runtime we detect the exact patch and choose the right implementation.

Details and guidelines: `multiversion_support.md`.

---

## 3. Worldgen Overview

High-level Overworld worldgen flow:

1. **Chunk generator selection**
   - Our custom chunk generator (`VerticalExpansionChunkGenerator`) is registered for the Overworld (or a custom world type).
   - Registration goes through `PlatformHooks` to allow for patch differences.

2. **Noise & density functions**
   - The generator builds a `TerrainHeightFunction` using a configured `WorldTerrainProfile`.
   - Density functions use the height function as their core input for surface height.
   - Noise settings (minY, height, sampling scales) are built from `WorldHeightConfig`.

3. **Biome selection**
   - Biome selection is multi-noise:
     - Temperature, humidity, continentalness, erosion, weirdness.
   - Biomes may restrict their allowed vertical bands.

4. **Features & structures**
   - Features (trees, coral, ores) read the **final or approximate height** and band data.
   - Some features are restricted to specific bands (e.g., mega mountains, deep caves).

5. **Vertical section policy**
   - After chunk data exists, `VerticalSectionManager` determines which sections are active.
   - Server tick and networking use this to skip inactive sectors.

---

## 4. Feature Sets

### 4.1 Massive Terrain Features

- Mega mountain ranges:
  - Arise from interactions of continent/ridge/erosion noise.
  - Often reach 300–500 Y, with rare peaks into 600–900.
- Sky terrain:
  - Optional band around Y 900+:
    - Floating islands, arches, spires, etc.
  - Can be disabled via config.

### 4.2 Massive Trees

- 100+ block tall tree variants:
  - Oak, dark oak, jungle.
- Generated as structured features:
  1. Placement: select a trunk root position.
  2. Skeleton: build trunk path and branch endpoints.
  3. Foliage: generate canopy blobs and decorations (vines, etc.).
- Configurable:
  - Height range, density, biome allowlist/denylist.

### 4.3 Coral Reefs

- Multi-chunk “reef blob” structures in warm oceans.
- Generated via 3D noise field over a local volume:
  - Defines coral vs water vs rock pockets.
- Configurable radius, thickness, density.

---

## 5. Ore System

- `OreProfile` represents one ore distribution:
  - Target blocks (tag), min/max Y, distribution curve, vein size, frequency, biome filters.
- `OreProfileRegistry` stores vanilla and modded ore profiles.
- Vanilla ore generation is overridden or adjusted to:
  - Preserve roughly similar relative bands,
  - But expanded to the new height range.
- Modded ores:
  - May register profiles via a small API.
  - If not registered, we adapt their placement as best we can (simple rescaling or passthrough).

---

## 6. Coding Conventions

1. **No hard-coded vanilla height constants** in worldgen.
   - Always use `WorldHeightConfig`.
2. **No version checks outside `platform` package.**
   - Core code must be version-agnostic.
3. **Document intent near TODO blocks.**
   - When leaving AI-fillable TODOs, explain requirements in comments.
4. **Do not copy external project code.**
   - Tectonic is used as a conceptual reference only.
5. **Keep docs in sync.**
   - If you change major terrain/vertical logic, update the corresponding `.md`.

---

## 7. How to Extend

- To add a new feature:
  - Put feature code in `features/`.
  - Add config entries (server config, optionally datapack support).
  - Integrate via biome modifiers or worldgen bootstrap in `worldgen/`.
- To add new terrain behavior:
  - Adjust `WorldTerrainProfile` and `TerrainHeightFunction` in `terrain/`.
  - Add configuration values.
  - Update `terrain_design.md`.

This document is the top-level reference: if behavior differs from what is written here, either the behavior is a bug or this file is out of date.
