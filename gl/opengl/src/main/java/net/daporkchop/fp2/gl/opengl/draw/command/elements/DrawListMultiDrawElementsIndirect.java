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

package net.daporkchop.fp2.gl.opengl.draw.command.elements;

import lombok.NonNull;
import net.daporkchop.fp2.gl.draw.binding.DrawMode;
import net.daporkchop.fp2.gl.bitset.GLBitSet;
import net.daporkchop.fp2.gl.buffer.BufferUsage;
import net.daporkchop.fp2.gl.draw.command.DrawCommandIndexed;
import net.daporkchop.fp2.gl.opengl.GLAPI;
import net.daporkchop.fp2.gl.opengl.GLEnumUtil;
import net.daporkchop.fp2.gl.opengl.draw.binding.DrawBindingIndexedImpl;
import net.daporkchop.fp2.gl.opengl.bitset.AbstractGLBitSet;
import net.daporkchop.fp2.gl.opengl.buffer.BufferTarget;
import net.daporkchop.fp2.gl.opengl.buffer.GLBufferImpl;
import net.daporkchop.fp2.gl.opengl.draw.command.DrawListBuilderImpl;
import net.daporkchop.fp2.gl.opengl.draw.command.DrawListImpl;
import net.daporkchop.fp2.gl.opengl.draw.index.IndexFormatImpl;
import net.daporkchop.fp2.gl.opengl.draw.shader.DrawShaderProgramImpl;
import net.daporkchop.fp2.gl.draw.shader.DrawShaderProgram;
import net.daporkchop.lib.unsafe.PUnsafe;

import static java.lang.Math.*;
import static net.daporkchop.fp2.common.util.TypeSize.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class DrawListMultiDrawElementsIndirect extends DrawListImpl<DrawCommandIndexed, DrawBindingIndexedImpl> {
    public static final long _COUNT_OFFSET = 0L;
    public static final long _INSTANCECOUNT_OFFSET = _COUNT_OFFSET + INT_SIZE;
    public static final long _FIRSTINDEX_OFFSET = _INSTANCECOUNT_OFFSET + INT_SIZE;
    public static final long _BASEVERTEX_OFFSET = _FIRSTINDEX_OFFSET + INT_SIZE;
    public static final long _BASEINSTANCE_OFFSET = _BASEVERTEX_OFFSET + INT_SIZE;

    public static final long _SIZE = _BASEINSTANCE_OFFSET + INT_SIZE;

    public static int _count(long cmd) {
        return PUnsafe.getInt(cmd + _COUNT_OFFSET);
    }

    public static void _count(long cmd, int count) {
        PUnsafe.putInt(cmd + _COUNT_OFFSET, count);
    }

    public static int _instanceCount(long cmd) {
        return PUnsafe.getInt(cmd + _INSTANCECOUNT_OFFSET);
    }

    public static void _instanceCount(long cmd, int instanceCount) {
        PUnsafe.putInt(cmd + _INSTANCECOUNT_OFFSET, instanceCount);
    }

    public static int _firstIndex(long cmd) {
        return PUnsafe.getInt(cmd + _FIRSTINDEX_OFFSET);
    }

    public static void _firstIndex(long cmd, int firstIndex) {
        PUnsafe.putInt(cmd + _FIRSTINDEX_OFFSET, firstIndex);
    }

    public static int _baseVertex(long cmd) {
        return PUnsafe.getInt(cmd + _BASEVERTEX_OFFSET);
    }

    public static void _baseVertex(long cmd, int baseVertex) {
        PUnsafe.putInt(cmd + _BASEVERTEX_OFFSET, baseVertex);
    }

    public static int _baseInstance(long cmd) {
        return PUnsafe.getInt(cmd + _BASEINSTANCE_OFFSET);
    }

    public static void _baseInstance(long cmd, int baseInstance) {
        PUnsafe.putInt(cmd + _BASEINSTANCE_OFFSET, baseInstance);
    }

    protected final GLBufferImpl buffer;

    protected long commandsAddr;

    protected final int indexType;
    protected final int indexSize;

    public DrawListMultiDrawElementsIndirect(@NonNull DrawListBuilderImpl builder) {
        super(builder);

        this.buffer = this.gl().createBuffer(BufferUsage.STREAM_DRAW);

        IndexFormatImpl format = this.binding.indices().format();
        this.indexType = GLEnumUtil.from(format.type());
        this.indexSize = format.size();
    }

    @Override
    protected void resize0(int oldCapacity, int newCapacity) {
        this.commandsAddr = this.alloc.realloc(this.commandsAddr, newCapacity * _SIZE);

        if (newCapacity > oldCapacity) { //zero out the commands
            PUnsafe.setMemory(this.commandsAddr + oldCapacity * _SIZE, (newCapacity - oldCapacity) * _SIZE, (byte) 0);
        }
    }

    @Override
    public void close() {
        this.alloc.free(this.commandsAddr);
        this.buffer.close();
    }

    @Override
    public void set(int index, @NonNull DrawCommandIndexed command) {
        long commandAddr = this.commandsAddr + checkIndex(this.capacity, index) * _SIZE;
        _firstIndex(commandAddr, command.firstIndex());
        _count(commandAddr, command.count());
        _baseVertex(commandAddr, command.baseVertex());
        _baseInstance(commandAddr, index);
        _instanceCount(commandAddr, 1);
    }

    @Override
    public DrawCommandIndexed get(int index) {
        long commandAddr = this.commandsAddr + checkIndex(this.capacity, index) * _SIZE;
        return new DrawCommandIndexed(_firstIndex(commandAddr), _count(commandAddr), _baseVertex(commandAddr));
    }

    @Override
    public void clear(int index) {
        _instanceCount(this.commandsAddr + checkIndex(this.capacity, index) * _SIZE, 0);
    }

    @Override
    public void execute(@NonNull DrawMode mode, @NonNull DrawShaderProgram shader) {
        this.buffer.upload(this.commandsAddr, this.capacity * _SIZE);
        this.buffer.bind(BufferTarget.DRAW_INDIRECT_BUFFER, target -> ((DrawShaderProgramImpl) shader).bind(() -> this.binding.bind(() -> {
            this.api.glMultiDrawElementsIndirect(GLEnumUtil.from(mode), this.indexType, 0L, this.capacity, 0);
        })));
    }

    @Override
    public void execute(@NonNull DrawMode mode, @NonNull DrawShaderProgram shader, @NonNull GLBitSet _selector) {
        AbstractGLBitSet selector = (AbstractGLBitSet) _selector;

        this.executeMappedClient(mode, shader, selector);
    }

    protected void executeMappedClient(@NonNull DrawMode mode, @NonNull DrawShaderProgram shader, @NonNull AbstractGLBitSet selector) {
        long dstCommands = PUnsafe.allocateMemory(this.capacity * _SIZE);
        try {
            selector.mapClient(this.capacity, (bitsBase, bitsOffset) -> {
                long srcCommandAddr = this.commandsAddr;
                long dstCommandAddr = dstCommands;
                for (int bitIndex = 0; bitIndex < this.capacity; bitsOffset += INT_SIZE) {
                    int word = PUnsafe.getInt(bitsBase, bitsOffset);

                    for (int endBit = min(bitIndex + Integer.SIZE, this.capacity); bitIndex < endBit; bitIndex++, srcCommandAddr += _SIZE, dstCommandAddr += _SIZE) {
                        _firstIndex(dstCommandAddr, _firstIndex(srcCommandAddr));
                        _count(dstCommandAddr, _count(srcCommandAddr));
                        _baseVertex(dstCommandAddr, _baseVertex(srcCommandAddr));
                        _baseInstance(dstCommandAddr, _baseInstance(srcCommandAddr));
                        _instanceCount(dstCommandAddr, _instanceCount(srcCommandAddr) & (word >>> bitIndex));
                    }
                }
            });

            this.buffer.upload(dstCommands, this.capacity * _SIZE);
            this.buffer.bind(BufferTarget.DRAW_INDIRECT_BUFFER, target -> ((DrawShaderProgramImpl) shader).bind(() -> this.binding.bind(() -> {
                this.api.glMultiDrawElementsIndirect(GLEnumUtil.from(mode), this.indexType, 0L, this.capacity, 0);
            })));
        } finally {
            PUnsafe.freeMemory(dstCommands);
        }
    }

    @Override
    public void draw0(GLAPI api, int mode) {
        this.buffer.upload(this.commandsAddr, this.capacity * _SIZE);
        this.buffer.bind(BufferTarget.DRAW_INDIRECT_BUFFER, target -> api.glMultiDrawElementsIndirect(mode, this.indexType, 0L, this.capacity, 0));
    }
}
