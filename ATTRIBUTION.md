# Attribution

This file documents code borrowed from external MIT-licensed projects.

---

## Tectonic

**Project**: [Tectonic](https://github.com/apolloterraforming/Tectonic) by Apollo Terraforming  
**License**: MIT  
**Copyright**: © 2024 Apollo Terraforming contributors

### Code Used

The following mixins are adapted from Tectonic's implementation for extending vertical world height:

- `src/main/java/com/cyber3d/verticalexpansion/mixin/ChunkAccessMixin.java`
  - Prevents crash when section index is out of bounds during chunk generation
  - Modified method name prefix from `tectonic$` to `verticalexpansion$`

- `src/main/java/com/cyber3d/verticalexpansion/mixin/HeightmapMixin.java`
  - Silences heightmap-related console warnings by replacing logger with NOP logger
  - No modifications from original

- `src/main/java/com/cyber3d/verticalexpansion/mixin/ChunkSerializerMixin.java`
  - Handles backward compatibility with pre-extended world chunks
  - Modified to use VerticalExpansion constants instead of Tectonic class
  - Changed method prefixes from `tectonic$` to `verticalexpansion$`

- `src/main/java/com/cyber3d/verticalexpansion/mixin/IOWorkerMixin.java`
  - Marks old chunks without version tags as needing blending
  - Modified to use VerticalExpansion constants and simplified version check
  - Changed method prefix from `tectonic$` to `verticalexpansion$`

- `src/main/java/com/cyber3d/verticalexpansion/mixin/NoiseBasedChunkGeneratorMixin.java`
  - Extends NoiseSettings vertical range and adjusts lava generation level
  - Combined Tectonic's approach with VerticalExpansion configuration system
  - Uses NoiseSettingsAccessor for cleaner direct field mutation
  - Changed method prefix from `tectonic$` to `verticalexpansion$`

- `src/main/java/com/cyber3d/verticalexpansion/mixin/NoiseSettingsAccessor.java`
  - Provides mutable accessors to NoiseSettings private fields
  - No modifications from original

- `src/main/java/com/cyber3d/verticalexpansion/mixin/DimensionTypeAccessor.java`
  - Provides mutable accessors to DimensionType private fields
  - No modifications from original

---

## MIT License Text

```
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
