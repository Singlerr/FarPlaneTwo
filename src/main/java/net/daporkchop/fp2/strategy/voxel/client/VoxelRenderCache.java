/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2020 DaPorkchop_
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

package net.daporkchop.fp2.strategy.voxel.client;

import lombok.NonNull;
import net.daporkchop.fp2.strategy.base.client.AbstractFarRenderCache;
import net.daporkchop.fp2.strategy.voxel.VoxelPiece;
import net.daporkchop.fp2.strategy.voxel.VoxelPos;
import net.daporkchop.fp2.util.alloc.Allocator;
import net.daporkchop.fp2.util.alloc.VariableSizedAllocator;
import net.daporkchop.lib.primitive.lambda.LongLongConsumer;

import static net.daporkchop.fp2.strategy.voxel.client.VoxelRenderHelper.*;

/**
 * @author DaPorkchop_
 */
public class VoxelRenderCache extends AbstractFarRenderCache<VoxelPos, VoxelPiece, VoxelRenderTile> {
    public VoxelRenderCache(@NonNull VoxelRenderer renderer) {
        super(renderer, -1);
    }

    @Override
    public VoxelRenderTile createTile(VoxelRenderTile parent, @NonNull VoxelPos pos) {
        return new VoxelRenderTile(this, parent, pos);
    }

    @Override
    public void tileAdded(@NonNull VoxelRenderTile tile) {
    }

    @Override
    public void tileRemoved(@NonNull VoxelRenderTile tile) {
    }
}
