package com.mcjty.mytutorial.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

public class TutorialChunkGenerator extends ChunkGenerator {

//    public static final Codec<TutorialChunkGenerator> CODEC = RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY)
//            .xmap(TutorialChunkGenerator::new, TutorialChunkGenerator::getBiomeRegistry).codec();

    public static final Codec<TutorialChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(TutorialChunkGenerator::getBiomeRegistry),
                    Codec.FLOAT.fieldOf("height").forGetter(TutorialChunkGenerator::getSineHeight)
            ).apply(instance, TutorialChunkGenerator::new));

    private float sineHeight;

    public TutorialChunkGenerator(Registry<Biome> registry, float sineHeight) {
        super(new TutorialBiomeProvider(registry), new DimensionStructuresSettings(false));
        this.sineHeight = sineHeight;
    }

    public Registry<Biome> getBiomeRegistry() {
        return ((TutorialBiomeProvider)biomeProvider).getBiomeRegistry();
    }

    public float getSineHeight() {
        return sineHeight;
    }

    @Override
    public void generateSurface(WorldGenRegion region, IChunk chunk) {
        BlockState bedrock = Blocks.BEDROCK.getDefaultState();
        BlockState stone = Blocks.STONE.getDefaultState();
        ChunkPos chunkpos = chunk.getPos();

        BlockPos.Mutable pos = new BlockPos.Mutable();

        int x;
        int z;

        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                chunk.setBlockState(pos.setPos(x, 0, z), bedrock, false);
            }
        }

        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                int realx = chunkpos.x * 16 + x;
                int realz = chunkpos.z * 16 + z;
                int height = (int) (65 + Math.sin(realx / 20.0f)*sineHeight + Math.cos(realz / 20.0f)*sineHeight);
                for (int y = 1 ; y < height ; y++) {
                    chunk.setBlockState(pos.setPos(x, y, z), stone, false);
                }
            }
        }
    }

    @Override
    protected Codec<? extends ChunkGenerator> func_230347_a_() {
        return CODEC;
    }

    @Override
    public ChunkGenerator func_230349_a_(long seed) {
        return new TutorialChunkGenerator(getBiomeRegistry(), sineHeight);
    }

    @Override
    public void func_230352_b_(IWorld world, StructureManager structureManager, IChunk chunk) {

    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType) {
        return 0;
    }

    @Override
    public IBlockReader func_230348_a_(int p_230348_1_, int p_230348_2_) {
        return new Blockreader(new BlockState[0]);
    }
}
