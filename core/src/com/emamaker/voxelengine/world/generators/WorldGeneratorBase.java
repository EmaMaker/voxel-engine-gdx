package  com.emamaker.voxelengine.world.generators;

import  com.emamaker.voxelengine.block.CellId;
import static  com.emamaker.voxelengine.utils.Globals.chunkSize;
import  com.emamaker.voxelengine.world.Chunk;

public class WorldGeneratorBase extends WorldGenerator {

    int nBases = 1;

    public WorldGeneratorBase() {
        this(1);
    }

    public WorldGeneratorBase(int nBases_) {
        this.nBases = nBases_;
    }
    
    @Override
    public void generate(Chunk c) {
        if (c.y == 0) {
            for (int a = 0; a < nBases; a++) {
                for (int i = 0; i < chunkSize; i++) {
                    for (int j = 0; j < chunkSize; j++) {
                        c.setCell(i, a, j, CellId.ID_GRASS);
                    }
                }
            }
            c.markForUpdate(true);
        }
    }

}
