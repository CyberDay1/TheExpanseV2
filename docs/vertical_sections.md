# Vertical Sections — Activity Policy and Manager

VerticalExpansion uses a **Vertical Section Manager** to reduce CPU and memory cost at very tall world heights.

This document defines the intended behavior.

---

## 1. Concepts

Vanilla splits chunks into 16-block-tall vertical sections (sub-chunks).  
We add an explicit notion of **section activity**:

- A section is **active** if:
  - It should tick blocks and fluids.
  - Entities inside it should be ticked.
  - It should be kept in memory.
  - The server may send its data to nearby players.
- A section is **inactive** if:
  - It is not ticked.
  - It may be unloaded or stored in a more compact form.

The goal:

- Allow worlds to be thousands of blocks tall.
- Avoid fully loading and ticking all vertical sections in a column when players are only using one band.

---

## 2. Section Indices

For a given dimension:

- `minY` and `maxY` come from `WorldHeightConfig`.
- Section height is fixed at 16 blocks.

Section index for a Y coordinate:

```text
sectionIndex = floor((y - minY) / 16)
```

Valid indices:

```text
0 .. numSections - 1
where numSections = (maxY - minY) / 16
```

This mapping is used consistently across the mod.

---

## 3. VerticalSectionPolicy

Activity is controlled by a `VerticalSectionPolicy`:

```java
package com.cyber3d.verticalexpansion.vertical;

public interface VerticalSectionPolicy {

    /**
     * Decide whether the given section should be active.
     *
     * Implementations must be pure and side-effect free.
     */
    boolean isActiveSection(SectionContext ctx, ServerVerticalConfig config);
}
```

`SectionContext` describes the section and nearby players:

```java
package com.cyber3d.verticalexpansion.vertical;

public interface SectionContext {
    int chunkX();
    int chunkZ();
    int sectionIndex();
    Iterable<PlayerView> nearbyPlayers();
}

public interface PlayerView {
    int x();
    int y();
    int z();
    int viewDistanceChunks();
}
```

`ServerVerticalConfig` (NeoForge config-backed) contains:

- `verticalSectionWindow` — sections above and below the player that should be active.
- `maxActiveSectionsPerColumn` — safety cap per (chunkX, chunkZ).
- Additional tuning knobs as needed.

### 3.1 Default Policy

Default policy (`SimpleVerticalSectionPolicy`) rules:

For each section:

1. For each nearby player:
   - Compute the horizontal distance in chunks between the section’s column and the player’s column.
   - If horizontal distance > player’s view distance, ignore this player.
   - Compute the player’s section index:

     ```text
     playerSection = floor((playerY - minY) / 16)
     ```

   - The section is active for this player if:

     ```text
     |sectionIndex - playerSection| <= verticalSectionWindow
     ```

2. A section is **globally active** if it is active for at least one player.

3. If the number of active sections in this column would exceed `maxActiveSectionsPerColumn`, the implementation may:
   - Prefer sections nearest to any player.
   - Mark the furthest ones inactive.

---

## 4. VerticalSectionManager

The `VerticalSectionManager` is responsible for coordinating policy, chunk lifecycle, and server tick integration.

Responsibilities:

- Compute active sections per tick (or on player movement events).
- Notify the world/chunk system about:
  - Sections entering the active set (load/tick).
  - Sections leaving the active set (unload or pause).
- Optionally expose metrics for debugging (active section counts, per-player bands).

Example skeleton:

```java
package com.cyber3d.verticalexpansion.vertical;

public final class VerticalSectionManager {

    private final VerticalSectionPolicy policy;
    private final ServerVerticalConfig config;

    public VerticalSectionManager(VerticalSectionPolicy policy, ServerVerticalConfig config) {
        this.policy = policy;
        this.config = config;
    }

    public void updateForTick(Iterable<SectionContext> sections) {
        for (SectionContext ctx : sections) {
            boolean active = policy.isActiveSection(ctx, config);
            // TODO: integrate with NeoForge / MC hooks to mark the section as active/inactive.
        }
    }
}
```

---

## 5. Integration Points

Implementation details differ per MC/NeoForge patch, so they are delegated to `PlatformHooks`:

- Hook into:
  - Chunk load/unload events.
  - Player movement events.
  - Server/world tick.
- Provide `SectionContext` instances for all known sections.
- Apply activity changes using whatever APIs the version exposes.

The core logic (policy, manager, config) lives under `vertical/`.  
The version-specific wiring lives under `platform/`.

---

## 6. Config Defaults

Suggested defaults:

- `verticalSectionWindow = 3`
   - Roughly ±48 blocks from player (with 16-block sections).
- `maxActiveSectionsPerColumn = 12`
   - Allows about 192 blocks of active vertical space per column by default.

These values should be safe for typical servers but are fully configurable.

