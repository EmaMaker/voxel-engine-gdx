package  com.emamaker.voxelengine.world.generators;

import static  com.emamaker.voxelengine.utils.Globals.chunkSize;

import  com.emamaker.voxelengine.block.CellId;
import  com.emamaker.voxelengine.world.Chunk;

public class WorldGeneratorCube extends WorldGenerator {

    @Override
    public void generate(Chunk c) {
        for (int i = 0; i < chunkSize; i++) {
            for (int j = 0; j < chunkSize; j++) {
                for (int k = 0; k < chunkSize; k++) {
                    c.setCell(i, j, k, CellId.ID_GRASS);
                }
            }
        }
        c.markForUpdate(true);
    }

}
