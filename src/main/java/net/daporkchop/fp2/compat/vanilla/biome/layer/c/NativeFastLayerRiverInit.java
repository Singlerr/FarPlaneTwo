/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.fp2.compat.vanilla.biome.layer.c;

import lombok.NonNull;
import net.daporkchop.fp2.compat.vanilla.biome.layer.java.FastLayerRiverInit;
import net.daporkchop.fp2.util.alloc.IntArrayAllocator;

/**
 * @author DaPorkchop_
 */
public class NativeFastLayerRiverInit extends FastLayerRiverInit {
    public NativeFastLayerRiverInit(long seed) {
        super(seed);
    }

    @Override
    public void getGrid(@NonNull IntArrayAllocator alloc, int x, int z, int sizeX, int sizeZ, @NonNull int[] out) {
        //needed parent area is same size as requested area, so we can have the parent write into the output array and then update it ourselves
        this.child.getGrid(alloc, x, z, sizeX, sizeZ, out);

        this.getGrid0(this.seed, x, z, sizeX, sizeZ, out);
    }

    protected native void getGrid0(long seed, int x, int z, int sizeX, int sizeZ, @NonNull int[] out);
}
