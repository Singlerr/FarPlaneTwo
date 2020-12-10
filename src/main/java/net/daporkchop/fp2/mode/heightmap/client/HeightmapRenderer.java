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

package net.daporkchop.fp2.mode.heightmap.client;

import lombok.NonNull;
import net.daporkchop.fp2.FP2Config;
import net.daporkchop.fp2.client.TexUVs;
import net.daporkchop.fp2.client.gl.camera.IFrustum;
import net.daporkchop.fp2.client.gl.shader.ShaderManager;
import net.daporkchop.fp2.client.gl.shader.ShaderProgram;
import net.daporkchop.fp2.client.render.DrawMode;
import net.daporkchop.fp2.client.render.IShaderHolder;
import net.daporkchop.fp2.client.render.RenderPass;
import net.daporkchop.fp2.mode.RenderMode;
import net.daporkchop.fp2.mode.common.client.AbstractFarRenderer;
import net.daporkchop.fp2.mode.common.client.FarRenderIndex;
import net.daporkchop.fp2.mode.common.client.IFarRenderBaker;
import net.daporkchop.fp2.mode.heightmap.HeightmapPos;
import net.daporkchop.fp2.mode.heightmap.piece.HeightmapPiece;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author DaPorkchop_
 */
@SideOnly(Side.CLIENT)
public class HeightmapRenderer extends AbstractFarRenderer<HeightmapPos, HeightmapPiece> implements IShaderHolder {
    public static final ShaderProgram TERRAIN_SHADER = ShaderManager.get("heightmap/terrain");
    public static final ShaderProgram WATER_STENCIL_SHADER = ShaderManager.get("heightmap/water_stencil");
    public static final ShaderProgram WATER_SHADER = ShaderManager.get("heightmap/water");

    public static final ShaderProgram XFB_TERRAIN_SHADER = ShaderManager.get("heightmap/xfb/terrain");

    public HeightmapRenderer(@NonNull WorldClient world) {
        super(world);
    }

    @Override
    protected HeightmapRenderCache createCache() {
        return new HeightmapRenderCache(this);
    }

    @Override
    public IFarRenderBaker<HeightmapPos, HeightmapPiece> baker() {
        return new HeightmapRenderBaker();
    }

    @Override
    protected void render0(float partialTicks, @NonNull WorldClient world, @NonNull Minecraft mc, @NonNull IFrustum frustum, @NonNull FarRenderIndex index) {
        try (ShaderProgram program = WATER_STENCIL_SHADER.use()) { //TODO: make this cleaner (possibly by making these fields part of FP2's state UBO)
            glUniform1i(program.uniformLocation("seaLevel"), 63);
        }
        try (ShaderProgram program = WATER_SHADER.use()) {
            glUniform1i(program.uniformLocation("seaLevel"), 63);
            glUniform1i(program.uniformLocation("in_state"), TexUVs.STATEID_TO_INDEXID.get(Block.getStateId(Blocks.WATER.getDefaultState())));
        }

        try (DrawMode drawMode = FP2Config.compatibility.drawMode.start(index, this.drawCommandBuffer, this)) {
            drawMode.draw(RenderPass.SOLID, 0);
            drawMode.draw(RenderPass.TRANSPARENT, 0);
        }
    }

    @Override
    public ShaderProgram getAndUseShader(@NonNull DrawMode mode, @NonNull RenderPass pass, boolean stencil) {
        switch (mode) {
            case MULTIDRAW:
                switch (pass) {
                    case SOLID:
                        return TERRAIN_SHADER.use();
                    case TRANSPARENT:
                        return (stencil ? WATER_STENCIL_SHADER : WATER_SHADER).use();
                }
                break;
            case TRANSFORM_FEEDBACK:
                switch (pass) {
                    case SOLID:
                        return XFB_TERRAIN_SHADER.use();
                }
        }
        throw new IllegalArgumentException("mode=" + mode + ", pass=" + pass + ", stencil=" + stencil);
    }

    @Override
    public RenderMode mode() {
        return RenderMode.HEIGHTMAP;
    }
}
