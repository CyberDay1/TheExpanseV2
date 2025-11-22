# Rules for AI Coding Assistants (Codex / Zencoder / etc.)

This repo is designed to be AI-friendly, but you **must** follow these rules when generating or modifying code.

These rules apply to all AI tools operating on this project.

---

## 1. Respect the Architecture

- Read and follow:
  - `docs/architecture.md`
  - `docs/terrain_design.md`
  - `docs/vertical_sections.md`
  - `docs/multiversion_support.md`
- Do **not** invent new architectures or large structural changes without explicit instruction.
- When in doubt, prefer:
  - Adding a small interface or method over refactoring entire subsystems.

---

## 2. Worldgen & Terrain

- Implement height and terrain behavior according to `terrain_design.md`.
- Use:
  - `WorldTerrainProfile`
  - `NoiseSampler`
  - `TerrainHeightFunction`
- Do **not** hard-code world heights; always use `profile.minY()`, `profile.maxY()`, and `profile.seaLevel()`.

---

## 3. Multiversion Support

- All version-specific logic goes under `platform/`.
- Only `platform/` code may:
  - Detect Minecraft patch versions.
  - Use version-specific NeoForge APIs.
- Core code (terrain, features, vertical sections, ore) must **never** check version numbers or call patch-specific APIs directly.
- Use `Platform.hooks()` as the entry to platform-specific code.

---

## 4. Vertical Sections

- Follow the behavior defined in `vertical_sections.md`.
- Implement or modify:
  - `VerticalSectionPolicy`
  - `VerticalSectionManager`
- Do not change the semantics of:
  - Section index computation: `sectionIndex = floor((y - minY) / 16)`
  - Activity decision based on player Y and `verticalSectionWindow`.

---

## 5. External Code & Licensing

- You **may** use code from MIT-licensed projects (including Tectonic) with proper attribution:
  - Include the original project's copyright notice in the source file header.
  - Add a comment indicating the source: `// Adapted from <project> (<url>) under MIT license`.
  - Ensure `LICENSE` or `ATTRIBUTION.md` documents all borrowed code.
- You **may**:
  - Use high-level ideas and patterns from any project.
  - Modify borrowed code to fit this project's architecture and conventions.
- When borrowing substantial code blocks:
  - Preserve original copyright headers.
  - Document what was changed and why.

---

## 6. TODO Blocks & Instructions

- TODO markers like `// TODO: AI IMPLEMENT` are **invitations** for the AI to implement logic.
- Within a TODO:
  - Read surrounding comments carefully.
  - Do not modify method signatures or public interfaces unless explicitly instructed.
  - Keep methods small and focused.

Example pattern:

```java
@Override
public int computeHeight(int x, int z, WorldTerrainProfile profile) {
    // TODO: AI IMPLEMENT
    // Requirements:
    // - Follow docs/terrain_design.md.
    // - Use continents/erosion/ridge/valley/detail noise fields.
    // - Clamp result to [minY, maxY].
    // - Avoid heap allocations inside this loop.
    return 0;
}
```

- When generating code for these blocks, do **only** what is described in the comments.

---

## 7. Tests & Invariants

- When adding or modifying public logic (especially terrain/vertical), prefer **adding tests** over removing old ones.
- Preserve key invariants:
  - Heights remain within `[minY, maxY]`.
  - Vertical section windows behave as documented.
  - Platform hooks remain the only source of version-specific behavior.

---

## 8. Comments & Documentation

- Keep comments short but clear.
- If your implementation deviates from the design docs:
  - Add a comment explaining why.
  - Update the relevant `.md` file if the deviation is intentional and permanent.

---

## 9. Summary for Custom Instructions

When configuring Codex/Zencoder for this repo, use the following short summary:

> "This project is a NeoForge Minecraft 1.21.1–1.21.10 mod called VerticalExpansion.  
> Follow docs in the `docs/` folder (architecture, terrain_design, vertical_sections, multiversion_support).  
> Use the PlatformHooks abstraction for all version-specific logic.  
> Implement terrain using WorldTerrainProfile + TerrainHeightFunction as defined in terrain_design.md.  
