# Terrain Design — VerticalExpansion

This document describes how VerticalExpansion shapes terrain.

It is inspired by the ideas used in mods like **Tectonic** (continents, erosion, large-scale mountains, configurable noise), but all formulas and implementations here are original.

---

## 1. Goals

- Use the expanded world height (`minY = -256`, `maxY ≈ 1024`) in a meaningful way.
- Produce **large, coherent continents** with:
  - Long mountain ranges.
  - Deep valleys and river basins.
  - Interesting high-altitude terrain.
- Allow **extensive tuning** of:
  - Vertical scale.
  - Noise scales / amplitudes.
  - Band thresholds.
- Maintain reasonable performance: more complex than vanilla, but not absurdly heavy.

---

## 2. Height Bands

Conceptual vertical bands:

- **Deep Underground**: `-256 .. 0`
  - Dense stone, mega caves, underground rivers, high ore density.
- **Core Surface**: `0 .. 256`
  - Primary “playable” terrain (plains, forests, oceans, forests, deserts).
- **Highlands**: `256 .. 512`
  - Common mountain ranges, cliffs, alpine lakes.
- **Extreme Peaks**: `512 .. 900`
  - Rare vertical monsters: towering ridges, cliffs, jagged peaks.
- **Sky Band**: `900 .. maxY`
  - Optional sky islands / special terrain. Can be disabled via config.

Default band thresholds (example):

```text
highlandsStartY = 220
extremeStartY   = 420
skyBandStartY   = 900
```

Bands are semantic: terrain can cross them freely, but band indices influence biome choice and shaping.

---

## 3. Noise Fields

We use several primary 2D noise fields over world coordinates `(x, z)`:

- `continents(x, z)` in [-1, 1]
  - Very low frequency.
  - Controls land vs ocean and base elevation.
- `erosion(x, z)` in [-1, 1]
  - Medium-low frequency.
  - Controls smooth vs rugged terrain.
- `ridge(x, z)` in [-1, 1]
  - Medium frequency.
  - Boosts elevations into long chains (mountain ranges).
- `valley(x, z)` in [0, 1]
  - River mask and valley strength.
  - Higher values ⇒ deeper valley cuts.
- `detail(x, z)` in [-1, 1]
  - Medium-high frequency.
  - Adds local hills and bumps.

Interface:

```java
package com.cyber3d.verticalexpansion.terrain;

public interface NoiseSampler {
    /**
     * Sample the noise field at the given world coordinates.
     * The return value should usually be in [-1, 1], but implementations may
     * choose different normalisation as long as they are consistent with the terrain formulas.
     */
    double sample(int x, int z);
}
```

Implementations can wrap Minecraft's internal noise primitives.

---

## 4. WorldTerrainProfile

Global knobs are stored in a profile:

```java
package com.cyber3d.verticalexpansion.terrain;

public interface WorldTerrainProfile {

    int minY();
    int maxY();
    int seaLevel();

    // Horizontal noise scales
    double continentsScale();
    double erosionScale();
    double ridgeScale();
    double valleyScale();
    double detailScale();

    // Amplitudes
    double baseHeightAmplitude();
    double mountainBoostAmplitude();
    double valleyDepth();
    double extremeMountainBoost();

    // Vertical bands
    int highlandsStartY();
    int extremeStartY();
    int skyBandStartY();

    // Flags
    boolean enableSkyTerrain();
    boolean enableUndergroundRivers();
    boolean enableMegaMountains();
}
```

A default implementation reads from NeoForge server config (`verticalexpansion-server.toml`).

---

## 5. TerrainHeightFunction

The height function `H(x, z)` produces a **target surface height** in `[minY, maxY]`.

We expose it as:

```java
package com.cyber3d.verticalexpansion.terrain;

public interface TerrainHeightFunction {
    int computeHeight(int x, int z, WorldTerrainProfile profile);
}
```

### 5.1 Base Elevation

```text
c   = continents(x, z)        // [-1, 1]
c01 = (c + 1.0) / 2.0         // [0, 1]

base = profile.seaLevel()
     + profile.baseHeightAmplitude() * (c01 - 0.5);
```

- `c01 = 0` ⇒ deep oceans.
- `c01 = 1` ⇒ higher ground / continental interiors.

### 5.2 Mountain Boost, Valley Cut, Detail

```text
e = erosion(x, z)             // [-1, 1]
r = ridge(x, z)               // [-1, 1]
v = valley(x, z)              // [0, 1]
d = detail(x, z)              // [-1, 1]

erosionFactor  = 1.0 - ((e + 1.0) / 2.0);  // [0, 1], high where erosion is low
mountainBoost  = r * erosionFactor * profile.mountainBoostAmplitude();
valleyCut      = v * profile.valleyDepth();
localDetail    = d * (profile.baseHeightAmplitude() * 0.3);
```

- High ridge + low erosion ⇒ high mountains.
- High valley ⇒ deeper cut into terrain.

### 5.3 Extreme Peaks

If mega mountains are enabled:

```text
if (profile.enableMegaMountains()) {
    extremeMask  = clamp(erosionFactor * ((c01 - 0.5) * 2.0), 0.0, 1.0);
    extremeBoost = extremeMask * profile.extremeMountainBoost();
} else {
    extremeBoost = 0;
}
```

This tends to produce tall peaks in continental interiors where erosion is low.

### 5.4 Final Height

```text
rawHeight = base
          + mountainBoost
          + localDetail
          + extremeBoost
          - valleyCut

height = clamp(rawHeight, profile.minY(), profile.maxY())
```

Implementation in `NoiseBasedTerrainHeightFunction` should follow this outline and:

- Clamp result to bounds.
- Avoid unnecessary allocations in hot loops.
- Document any deviations here.

---

## 6. Deep Caves & Underground Rivers

Underground content uses 3D noise but is informed by the same 2D fields:

- Use `valley(x, z)` and/or a separate river noise to position underground rivers and canyons.
- Use `erosion(x, z)` to vary cave smoothness.
- Use vertical curves to:
  - Increase cave density at mid-depths (e.g. `-128 .. 0`).
  - Decrease near bedrock and surface.

This integrates with Minecraft's existing 3D noise cave system, but with parameters tuned to the expanded height and our noise fields.

---

## 7. Biome Interaction

Biomes should be chosen via a multi-noise source:

- Temperature, humidity, continentalness, erosion, weirdness.
- Optionally an altitude or band index derived from `H(x, z)`.

Biomes may declare allowed bands, for example:

- Plains: `< highlandsStartY`.
- Mountains: `>= highlandsStartY && < extremeStartY`.
- Extreme peaks: `>= extremeStartY`.
- Sky biomes: `>= skyBandStartY`.

The terrain function does not hardwire biomes; it only provides smooth height and band information.

---

## 8. Configurability

All major knobs should be configurable:

- Vertical scale, band thresholds, noise scales, and amplitudes.
- Enable/disable special bands (extreme, sky).
- Tuning extremes for different packs (e.g. “mild highlands” vs “ludicrous peaks”).

These are backed by NeoForge server config and potentially datapacks for advanced users.

---

## 9. Tectonic-style Example (Conceptual)

This is a conceptual example (not copied code) that shows the kind of composition we want:

```java
// NOTE: This is a conceptual example inspired by Tectonic-style mods.
// It is not copied from any external project.

public int computeHeight(int x, int z, WorldTerrainProfile profile) {
    double cx = x * profile.continentsScale();
    double cz = z * profile.continentsScale();

    double ex = x * profile.erosionScale();
    double ez = z * profile.erosionScale();

    double rx = x * profile.ridgeScale();
    double rz = z * profile.ridgeScale();

    double vx = x * profile.valleyScale();
    double vz = z * profile.valleyScale();

    double dx = x * profile.detailScale();
    double dz = z * profile.detailScale();

    // sample fields
    double c = continents.sample(cx, cz);
    double e = erosion.sample(ex, ez);
    double r = ridges.sample(rx, rz);
    double v = valley.sample(vx, vz);
    double d = detail.sample(dx, dz);

    // then use the formulas from this document to combine them
    // ...
}
```

This is roughly analogous to how Tectonic composes multiple scaled noise fields, but the specific math and parameters are defined by this document.

