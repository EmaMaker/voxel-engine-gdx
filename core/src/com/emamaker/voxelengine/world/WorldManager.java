package com.emamaker.voxelengine.world;

import static com.emamaker.voxelengine.utils.VoxelSettings.MAXX;
import static com.emamaker.voxelengine.utils.VoxelSettings.MAXY;
import static com.emamaker.voxelengine.utils.VoxelSettings.MAXZ;
import static com.emamaker.voxelengine.utils.VoxelSettings.chunkSize;
import static com.emamaker.voxelengine.utils.VoxelSettings.debug;
import static com.emamaker.voxelengine.utils.VoxelSettings.pX;
import static com.emamaker.voxelengine.utils.VoxelSettings.pY;
import static com.emamaker.voxelengine.utils.VoxelSettings.pZ;
import static com.emamaker.voxelengine.utils.VoxelSettings.renderDistance;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.emamaker.voxelengine.VoxelWorld;
import com.emamaker.voxelengine.block.CellId;
import com.emamaker.voxelengine.utils.VoxelSettings;
import com.emamaker.voxelengine.utils.math.MathHelper;
//import com.emamaker.voxelengine.control.ControlsHandler;

public class WorldManager {

	Chunk[][][] chunks;

	Random rand = new Random();

//    SimpleApplication app;
//    AppStateManager stateManager;
//    ControlsHandler controlHandler;

	public boolean updateChunks = true;
	public boolean generateChunks = true;

	public VoxelWorld voxelWorld;

	public WorldManager(VoxelWorld world) {
		this.voxelWorld = world;

		chunks = new Chunk[MAXX][MAXY][MAXZ];
		preload();
	}

//    @Override
//    public void initialize(AppStateManager stateManager, Application app) {
//        super.initialize(stateManager, app);
//        this.app = (SimpleApplication) app;
//        this.stateManager = stateManager;
//        controlHandler = stateManager.getState(ControlsHandler.class);
//
//        chunks = new Chunk[MAXX][MAXY][MAXZ];
//        preload();
//    }

	public void preload() {
		updateChunks = true;
		generateChunks = true;

		if (VoxelSettings.isTesting()) {
			updateChunks = true;
			generateChunks = false;

			newChunk(0, 0, 0);
			VoxelSettings.setWorldGenerator("generatorBase");
			getChunk(0, 0, 0).generate();
//			getChunk(0,0,0).setCell(0, 0, 0, CellId.ID_GRASS);
			getChunk(0,0,0).processCells();
		}
//        Globals.executor.submit(chunkManager);
	}

//    public void update() {
//
//        for (int i = pX - renderDistance; i < pX + renderDistance * 1.5; i++) {
//            for (int j = pY - renderDistance; j < pY + renderDistance * 1.5; j++) {
//                for (int k = pZ - renderDistance; k < pZ + renderDistance * 1.5; k++) {
//
//                    if (i >= 0 && i < MAXX && j >= 0 && j < MAXY && k >= 0 && k < MAXZ) {
//                        if (getChunk(i, j, k) != null) {
//                            getChunk(i, j, k).updateMesh();
//                        }
//                    }
//                }
//            }
//        }
//    }

	// replaces the Cell.setId(id), and replaces making all the cell air when chunk
	// is created. Commento storico del 2016 (Si, lo so che è il 2019 ora) -
	// historical comment from 2016 (Yes, I know it's 2019 now)
	public void setCell(int i, int j, int k, byte id) {
		int plusX = i % chunkSize, plusY = j % chunkSize, plusZ = k % chunkSize;
		int chunkX = (i - plusX) / chunkSize, chunkY = (j - plusY) / chunkSize, chunkZ = (k - plusZ) / chunkSize;

		if (chunkX >= 0 && chunkY >= 0 && chunkZ >= 0 && chunkX < MAXX && chunkY < MAXY && chunkZ < MAXZ) {
			if (getChunk(chunkX, chunkY, chunkZ) == null) {
				setChunk(chunkX, chunkY, chunkZ, new Chunk(chunkX, chunkY, chunkZ));
			}
			getChunk(chunkX, chunkY, chunkZ).setCell(plusX, plusY, plusZ, id);
		}
	}

	public byte getCell(int i, int j, int k) {
		int plusX = i % chunkSize, plusY = j % chunkSize, plusZ = k % chunkSize;
		int chunkX = (i - plusX) / chunkSize, chunkY = (j - plusY) / chunkSize, chunkZ = (k - plusZ) / chunkSize;

		if (chunkX >= 0 && chunkY >= 0 && chunkZ >= 0 && chunkX < MAXX && chunkY < MAXY && chunkZ < MAXZ) {
			if (getChunk(chunkX, chunkY, chunkZ) != null) {
				return getChunk(chunkX, chunkY, chunkZ).getCell(plusX, plusY, plusZ);
			}
		}
		return Byte.MIN_VALUE;
	}

	// returns the chunk is the specified coords
	public Chunk getChunkInWorld(int i, int j, int k) {
		int plusX = i % chunkSize, plusY = j % chunkSize, plusZ = k % chunkSize;
		int chunkX = (i - plusX) / chunkSize, chunkY = (j - plusY) / chunkSize, chunkZ = (k - plusZ) / chunkSize;

		return chunks[chunkX][chunkY][chunkZ];
	}

	public Chunk getChunk(int i, int j, int k) {
		return chunks[i][j][k];
	}

	public void setChunk(int i, int j, int k, Chunk c) {
		chunks[i][j][k] = c;
	}

	public void newChunk(int i, int j, int k) {
		Chunk c = new Chunk(i, j, k);
		chunks[i][j][k] = c;
	}

	public void setCellFromVertices(ArrayList<Vector3> al, byte id) {
		setCell(getCellPosFromVertices(al), id);
	}

	public byte getCellFromVertices(ArrayList<Vector3> al) {
		return getCell(getCellPosFromVertices(al));
	}

	public Vector3 getCellPosFromVertices(ArrayList<Vector3> al) {
		Vector3 v = MathHelper.lowestVectorInList(al.get(0), al.get(1), al.get(2), al.get(3));

		if (al.get(0).x == al.get(1).x && al.get(0).x == al.get(2).x && al.get(0).x == al.get(3).x) {
			if (getCell(v) != CellId.ID_AIR) {
				debug(v.toString());
				return v;
			} else {
				v.set((int) (v.x - 1), (int) v.y, (int) v.z);
				debug(v.toString());
				return v;
			}
		} else if (al.get(0).y == al.get(1).y && al.get(0).y == al.get(2).y && al.get(0).y == al.get(3).y) {
			if (getCell(v) != CellId.ID_AIR) {
				debug(v.toString());
				return v;
			} else {
				v.set((int) v.x, (int) (v.y - 1), (int) v.z);
				debug(v.toString());
				return v;
			}
		} else if (al.get(0).z == al.get(1).z && al.get(0).z == al.get(2).z && al.get(0).z == al.get(3).z) {
			if (getCell(v) != CellId.ID_AIR) {
				debug(v.toString());
				return v;
			} else {
				v.set((int) v.x, (int) v.y, (int) (v.z - 1));
				debug(v.toString());
				return v;
			}
		}
		return null;
	}

	public byte getHighestCellAt(int i, int j) {
		for (int a = MAXY * chunkSize; a >= 0; a--) {
			if (getCell(i, a, j) != CellId.ID_AIR) {
				return getCell(i, a, j);
			}
		}
		return Byte.MIN_VALUE;
	}

//    @Override
//    public void cleanup() {
//        updateChunks = false;
//        Globals.executor.shutdownNow();
//    }

	public void loadFromFile(int i, int j, int k) {
		File f = Paths.get(VoxelSettings.workingDir + i + "-" + j + "-" + k + ".chunk").toFile();
		getChunk(i, j, k).loadFromFile(f);
	}

	public boolean canLoadFromFile(int i, int j, int k) {
		File f = Paths.get(VoxelSettings.workingDir + i + "-" + j + "-" + k + ".chunk").toFile();
		return f.exists();
	}

//    final Callable<Object> chunkManager = new Callable<Object>() {
//        @Override
//        public Object call() {
//            while (updateChunks) {
//                updateChunks();
//            }
//            return null;
//        }
//    };

	public void updateChunks() {
//        try {
//            if (controlHandler.placeBlock) {
//                controlHandler.placeBlock();
//                controlHandler.placeBlock = false;
//
//            }
//            if (controlHandler.breakBlock) {
//                controlHandler.breakBlock();
//                controlHandler.breakBlock = false;
//            }

		pX = 0;
		pY = 0;
		pZ = 0;

		for (int i = pX - renderDistance; i < pX + renderDistance * 1.5; i++) {
			for (int j = pY - renderDistance; j < pY + renderDistance * 1.5; j++) {
				for (int k = pZ - renderDistance; k < pZ + renderDistance * 1.5; k++) {

					if (i >= 0 && i < MAXX && j >= 0 && j < MAXY && k >= 0 && k < MAXZ) {
						if (getChunk(i, j, k) != null) {
							if (generateChunks) {
								getChunk(i, j, k).generate();
								getChunk(i, j, k).decorate();
							}
							getChunk(i, j, k).processCells();
						} else {
							if (generateChunks) {
								if (canLoadFromFile(i, j, k)) {
									newChunk(i, j, k);
									loadFromFile(i, j, k);
								} else {
									if (j <= VoxelSettings.getWorldHeight()) {
										newChunk(i, j, k);
									}
								}
							}
						}
					}
				}
			}
		}

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
	}

	public void renderChunks(ModelBatch batch, Environment e) {

		for (int i = pX - renderDistance; i < pX + renderDistance * 1.5; i++) {
			for (int j = pY - renderDistance; j < pY + renderDistance * 1.5; j++) {
				for (int k = pZ - renderDistance; k < pZ + renderDistance * 1.5; k++) {

					if (i >= 0 && i < MAXX && j >= 0 && j < MAXY && k >= 0 && k < MAXZ) {
						if (getChunk(i, j, k) != null) {
							getChunk(i, j, k).render(batch, e);
						}
					}
				}
			}
		}
	}

	/* SOME USEFUL METHOD OVERRIDING */
	public void setCell(Vector3 v, byte id) {
		if (v != null) {
			this.setCell((int) v.x, (int) v.y, (int) v.z, id);
		}
	}

	public void setCell(float i, float j, float k, byte id) {
		this.setCell((int) i, (int) j, (int) k, id);
	}

	public byte getCell(Vector3 v) {
		return v != null ? getCell((int) v.x, (int) v.y, (int) v.z) : null;
	}

	public byte getCell(float i, float j, float k) {
		return getCell((int) i, (int) j, (int) k);
	}

	public Chunk getChunk(Vector3 v) {
		return this.getChunk((int) v.x, (int) v.y, (int) v.z);
	}
}
