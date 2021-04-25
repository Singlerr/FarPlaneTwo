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

import net.daporkchop.fp2.compat.vanilla.biome.layer.c.NativeFastLayerIsland;
import net.daporkchop.fp2.compat.vanilla.biome.layer.java.JavaLayerProvider;
import net.minecraft.world.gen.layer.GenLayerIsland;

/**
 * Extension of {@link JavaLayerProvider} which replaces a number of the layer types with native C++ implementations.
 *
 * @author DaPorkchop_
 */
public class NativeLayerProvider extends JavaLayerProvider {
    private static native void init0();

    protected NativeLayerProvider() {
        System.load("/media/daporkchop/PortableIDE/Minecraft/FarPlaneTwo/src/main/resources/net/daporkchop/fp2/compat/vanilla/biome/layer/c/x86_64-linux-gnu.so");
        init0(); //ensure JNI libs are initialized

        this.fastMapperOverrides.put(GenLayerIsland.class, layer -> new NativeFastLayerIsland(layer.worldGenSeed));
    }

    @Override
    public boolean isNative() {
        return true;
    }
}