package  com.emamaker.voxelengine.world.generators;

import static  com.emamaker.voxelengine.utils.VoxelSettings.chunkSize;

import java.util.Random;

import  com.emamaker.voxelengine.block.CellId;
import  com.emamaker.voxelengine.world.Chunk;

public class WorldGeneratorRandom extends WorldGenerator {

    Random rand = new Random();

    @Override
    public void generate(Chunk c) {
        for (int i = 0; i < chunkSize; i++) {
            for (int j = 0; j < chunkSize; j++) {
                for (int k = 0; k < chunkSize; k++) {
                    if (rand.nextFloat() < 0.25) {
                        c.setCell(i, j, k, CellId.ID_GRASS);
                    }
                }
            }
        }
        c.markForUpdate(true);
    }

}
