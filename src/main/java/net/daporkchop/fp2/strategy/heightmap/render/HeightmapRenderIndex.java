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

package net.daporkchop.fp2.strategy.heightmap.render;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.fp2.util.Constants;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.IntBuffer;

import static net.daporkchop.fp2.strategy.heightmap.render.HeightmapRenderHelper.*;
import static net.daporkchop.lib.common.util.PValidation.*;
import static org.lwjgl.opengl.GL15.*;

/**
 * @author DaPorkchop_
 */
class HeightmapRenderIndex {
    protected IntBuffer buffer = Constants.createIntBuffer(256);
    @Getter
    protected int size = 0;

    public int mark() {
        return this.size;
    }

    public void restore(int mark) {
        this.buffer.position(mark * 4 * 8);
        this.size = mark;
    }

    public boolean add(@NonNull Tile tile) {
        if (tile.hasAddress()) {
            this.ensureWritable(4 * 8);

            for (Tile t : tile.neighbors) {
                this.writeTile(t);
            }
            if (tile.parent != null) {
                this.writeTile(tile.parent);
                this.writeTile((tile.z & 1) == 0 ? tile.parent : tile.parent.neighbors[1]);
                this.writeTile((tile.x & 1) == 0 ? tile.parent : tile.parent.neighbors[2]);
                this.writeTile((tile.x & 1) != 0 && (tile.z & 1) != 0 ? tile.parent.neighbors[3] : (tile.x & 1) != 0 ? tile.parent.neighbors[2] : (tile.z & 1) != 0 ? tile.parent.neighbors[1] : tile.parent);
            } else {
                for (int i = 0; i < 4; i++) {
                    this.writeTile(null);
                }
            }

            this.size++;
            return true;
        } else {
            return false;
        }
    }

    private void writeTile(Tile tile) {
        if (tile != null && tile.hasAddress()) {
            this.buffer.put(tile.x).put(tile.z).put(tile.level).put(toInt(tile.address / HEIGHTMAP_RENDER_SIZE));
        } else {
            this.buffer.put(0).put(0).put(0).put(0);
        }
    }

    private void ensureWritable(int count) {
        while (this.buffer.remaining() < count) {  //buffer doesn't have enough space, grow it
            IntBuffer bigger = Constants.createIntBuffer(this.buffer.capacity() << 1);
            this.buffer.flip();
            bigger.put(this.buffer);
            PUnsafe.pork_releaseBuffer(this.buffer);
            this.buffer = bigger;
        }
    }

    public void reset() {
        this.buffer.clear();
        this.size = 0;
    }

    public void upload(int slot) {
        if (this.size > 0) {
            this.buffer.flip();
            glBufferData(slot, this.buffer, GL_STREAM_DRAW);
        } else {
            glBufferData(slot, 0L, GL_STREAM_DRAW);
        }
    }
}