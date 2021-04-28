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

package net.daporkchop.fp2.compat.vanilla.biome.layer.java;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import net.daporkchop.fp2.compat.vanilla.biome.layer.AbstractFastLayer;
import net.daporkchop.fp2.util.alloc.IntArrayAllocator;
import net.minecraft.init.Biomes;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.gen.layer.GenLayerBiome;
import net.minecraftforge.common.BiomeManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 * @see GenLayerBiome
 */
public class FastLayerBiome extends AbstractFastLayer {
    protected static final List<BiomeManager.BiomeEntry>[] ENTRIES;
    protected static final int[] WEIGHTS;

    static {
        BiomeManager.BiomeType[] types = BiomeManager.BiomeType.values();
        ENTRIES = uncheckedCast(Arrays.stream(types).map(type -> {
            ImmutableList<BiomeManager.BiomeEntry> forgeEntries = BiomeManager.getBiomes(type);
            return forgeEntries != null ? new ArrayList<>(forgeEntries) : new ArrayList<>();
        }).toArray(List[]::new));

        List<BiomeManager.BiomeEntry> desert = ENTRIES[BiomeManager.BiomeType.DESERT.ordinal()];
        desert.add(new BiomeManager.BiomeEntry(Biomes.DESERT, 30));
        desert.add(new BiomeManager.BiomeEntry(Biomes.SAVANNA, 20));
        desert.add(new BiomeManager.BiomeEntry(Biomes.PLAINS, 10));

        WEIGHTS = Arrays.stream(types).mapToInt(type -> {
            int totalWeight = WeightedRandom.getTotalWeight(ENTRIES[type.ordinal()]);
            boolean modded = BiomeManager.isTypeListModded(type);
            return modded ? (1 << 31) | (totalWeight / 10) : totalWeight;
        }).toArray();
    }

    public FastLayerBiome(long seed) {
        super(seed);
    }

    @Override
    public int getSingle(@NonNull IntArrayAllocator alloc, int x, int z) {
        int k = this.child.getSingle(alloc, x, z);
        int l = (k & 0xF00) >> 8;
        k = k & ~0xF00;
        //TODO

        /*if (this.settings != null && this.settings.fixedBiome >= 0)
                {
                    aint1[j + i * areaWidth] = this.settings.fixedBiome;
                }
                else */
        return 0;
    }
}
