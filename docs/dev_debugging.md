# VerticalExpansion – Dev Debugging Guide

## Commands

- `/ve_height`  
  - Shows the computed terrain height for either:
    - Your current position (no args), or
    - A specific `(x, z)` pair.
  - Uses the same terrain profile and noise function as worldgen.

- `/ve_section_info`  
  - Shows whether the vertical section containing your current Y is considered **active**
    by the `VerticalSectionManager`.

- `/ve_debug <true|false>`  
  - Toggles verbose debug logging (worldgen, noise, sections).
  - When enabled, additional `LOGGER.debug(...)` messages are emitted from:
    - `WorldGenInitializer`
    - `ChunkGeneratorRegistry`
    - `DensityFunctionIntegration`
    - `VerticalIntegration`
    - Other worldgen entrypoints as needed.

## Suggested Workflow

1. Enable debug logs:
   - `/ve_debug true`

2. Inspect height function:
   - Stand somewhere interesting and run `/ve_height`
   - Teleport to high coordinates and run again

3. Inspect vertical sections:
   - Move up and down with elytra or spectator
   - Run `/ve_section_info` at various heights

4. Once satisfied, disable debug logs:
   - `/ve_debug false`
