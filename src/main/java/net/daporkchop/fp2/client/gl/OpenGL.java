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

package net.daporkchop.fp2.client.gl;

import lombok.experimental.UtilityClass;
import net.daporkchop.fp2.FP2;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class OpenGL {
    public final int FLOAT_SIZE = Float.BYTES;
    public final int INT_SIZE = Integer.BYTES;
    public final int DOUBLE_SIZE = Double.BYTES;

    public final int VEC2_ELEMENTS = 2;
    public final int VEC2_SIZE = VEC2_ELEMENTS * FLOAT_SIZE;
    public final int IVEC2_SIZE = VEC2_ELEMENTS * INT_SIZE;
    public final int DVEC2_SIZE = VEC2_ELEMENTS * DOUBLE_SIZE;

    public final int VEC3_ELEMENTS = 3;
    public final int VEC3_SIZE = (VEC3_ELEMENTS + 1) * FLOAT_SIZE;
    public final int IVEC3_SIZE = (VEC3_ELEMENTS + 1) * INT_SIZE;
    public final int DVEC3_SIZE = (VEC3_ELEMENTS + 1) * DOUBLE_SIZE;

    public final int VEC4_ELEMENTS = 4;
    public final int VEC4_SIZE = VEC4_ELEMENTS * FLOAT_SIZE;
    public final int IVEC4_SIZE = VEC4_ELEMENTS * INT_SIZE;
    public final int DVEC4_SIZE = VEC4_ELEMENTS * DOUBLE_SIZE;

    public final int MAT4_ELEMENTS = 4 * 4;
    public final int MAT4_SIZE = MAT4_ELEMENTS * FLOAT_SIZE;

    public void checkGLError(String message) {
        for (int error; (error = glGetError()) != GL_NO_ERROR; ) {
            FP2.LOGGER.error("########## GL ERROR ##########");
            FP2.LOGGER.error("@ {}", message);
            FP2.LOGGER.error("{}: {}", error, gluErrorString(error));
        }
    }
}