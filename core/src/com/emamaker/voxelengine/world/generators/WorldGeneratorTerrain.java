package  com.emamaker.voxelengine.world.generators;

import static  com.emamaker.voxelengine.utils.VoxelSettings.chunkSize;

import java.util.Random;

import  com.emamaker.voxelengine.block.CellId;
import  com.emamaker.voxelengine.utils.VoxelSettings;
import  com.emamaker.voxelengine.utils.math.SimplexNoise;
import  com.emamaker.voxelengine.world.Chunk;

public class WorldGeneratorTerrain extends WorldGenerator {

    Random rand = new Random();

    @Override
    public void generate(Chunk c) {
        double p;
        //System.out.println(Math.abs(SimplexNoise.noise((x*chunkSize+i)*0.025, (z*chunkSize+k)*0.025)));
        if (c.y < VoxelSettings.getWorldHeight()) {
            for (int i = 0; i < chunkSize; i++) {
                for (int j = 0; j < chunkSize; j++) {
                    for (int k = 0; k < chunkSize; k++) {
                        c.setCell(i, j, k, CellId.ID_DIRT);
                    }
                }
            }
        } else if (c.y == VoxelSettings.getWorldHeight()) {
            for (int i = 0; i < chunkSize; i++) {
                for (int k = 0; k < chunkSize; k++) {
                    p = 1 + Math.abs(SimplexNoise.noise((c.x * chunkSize + i) * 0.01, (c.z * chunkSize + k) * 0.01)) * 10;
                    for (int a = 0; a < p; a++) {
                        c.setCell(i, a, k, CellId.ID_GRASS);
                        c.generated = true;
                        c.markForUpdate(true);
                    }
                }
            }
        }
    }
}
