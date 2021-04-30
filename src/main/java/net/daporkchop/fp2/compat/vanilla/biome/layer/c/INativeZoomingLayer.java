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
import net.daporkchop.fp2.compat.vanilla.biome.layer.IFastLayer;
import net.daporkchop.fp2.compat.vanilla.biome.layer.IZoomingLayer;
import net.daporkchop.fp2.util.alloc.IntArrayAllocator;

/**
 * Extension of {@link IZoomingLayer} for native implementations.
 *
 * @author DaPorkchop_
 */
public interface INativeZoomingLayer extends IZoomingLayer {
    /**
     * @return the seed used by this layer for random number generation
     */
    long seed();

    /**
     * @return the next layer in the generation chain
     */
    IFastLayer child();

    @Override
    default void getGrid(@NonNull IntArrayAllocator alloc, int x, int z, int sizeX, int sizeZ, @NonNull int[] out) {
        int shift = this.shift();
        int padding = IZoomingLayer.isAligned(shift, x, z, sizeX, sizeZ) ? 1 : 2;
        int lowSizeX = (sizeX >> shift) + padding;
        int lowSizeZ = (sizeZ >> shift) + padding;

        int[] in = alloc.get(lowSizeX * lowSizeZ);
        try {
            this.child().getGrid(alloc, x >> shift, z >> shift, lowSizeX, lowSizeZ, in);

            this.getGrid0(this.seed(), x, z, sizeX, sizeZ, out, in);
        } finally {
            alloc.release(in);
        }
    }

    void getGrid0(long seed, int x, int z, int sizeX, int sizeZ, @NonNull int[] out, @NonNull int[] in);

    @Override
    default void multiGetGridsCombined(@NonNull IntArrayAllocator alloc, int x, int z, int size, int dist, int depth, int count, @NonNull int[] out) {
        int shift = this.shift();
        int lowSize = ((((dist >> depth) + 1) * count) >> shift) + 2;
        int[] in = alloc.get(lowSize * lowSize);
        try {
            this.child().getGrid(alloc, x >> (depth + shift), z >> (depth + shift), lowSize, lowSize, in);

            this.multiGetGridsCombined0(this.seed(), x, z, size, dist, depth, count, out, in);
        } finally {
            alloc.release(in);
        }
    }

    void multiGetGridsCombined0(long seed, int x, int z, int size, int dist, int depth, int count, @NonNull int[] out, @NonNull int[] in);

    @Override
    default void multiGetGridsIndividual(@NonNull IntArrayAllocator alloc, int x, int z, int size, int dist, int depth, int count, @NonNull int[] out) {
        int shift = this.shift();
        int lowSize = (size >> shift) + 2;

        int[] in = alloc.get(count * count * lowSize * lowSize);
        try {
            this.child().multiGetGrids(alloc, x, z, lowSize, dist, depth + shift, count, in);

            this.multiGetGridsIndividual0(this.seed(), x, z, size, dist, depth, count, out, in);
        } finally {
            alloc.release(in);
        }
    }

    void multiGetGridsIndividual0(long seed, int x, int z, int size, int dist, int depth, int count, @NonNull int[] out, @NonNull int[] in);
}