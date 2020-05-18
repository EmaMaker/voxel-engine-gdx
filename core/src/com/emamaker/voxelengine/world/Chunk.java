package com.emamaker.voxelengine.world;

import static com.emamaker.voxelengine.utils.VoxelSettings.MAXX;
import static com.emamaker.voxelengine.utils.VoxelSettings.MAXY;
import static com.emamaker.voxelengine.utils.VoxelSettings.MAXZ;
import static com.emamaker.voxelengine.utils.VoxelSettings.blockSize;
import static com.emamaker.voxelengine.utils.VoxelSettings.chunkSize;
import static com.emamaker.voxelengine.utils.VoxelSettings.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.emamaker.voxelengine.block.CellId;
import com.emamaker.voxelengine.block.TextureManagerAtlas;
import com.emamaker.voxelengine.physics.GameObject;
import com.emamaker.voxelengine.utils.VoxelSettings;
import com.emamaker.voxelengine.utils.math.MathHelper;

public class Chunk {

	boolean toBeSet = true;
	boolean toUpdateInstance = false;
	boolean toUpdateMesh = false;
	boolean keepUpdating = true;
	boolean loaded = false;
	boolean toUnload = false;
	boolean phyLoaded = false;
	boolean meshing = false;
	public boolean generated = false;
	public boolean decorated = false;

	GameObject.Constructor constructor;
	GameObject object;

	// chunk model and instance for rendering
	Mesh mesh;

	// the chunk coords in the world
	public int x, y, z;

	// public Cell[] cells = new Cell[chunkSize * chunkSize * chunkSize];
	public CellId[] cells = new CellId[chunkSize * chunkSize * chunkSize];

	ModelInstance instance;
	Vector3 pos = new Vector3();
	Random rand = new Random();

	public Chunk() {
		this(0, 0, 0);
	}

	public Chunk(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;

		pos = new Vector3(x * chunkSize * blockSize, y * chunkSize * blockSize, z * chunkSize * blockSize);
		debug("Creating chunk starting at world coords" + pos.toString());

		prepareCells();
		markForUpdate(true);
	}

	void prepareCells() {
		for (int i = 0; i < cells.length; i++) {
			cells[i] = CellId.ID_AIR;
		}
	}

	public void processCells() {
		if (toBeSet) {
			for (int i = 0; i < chunkSize; i++) {
				for (int j = 0; j < chunkSize; j++) {
					for (int k = 0; k < chunkSize; k++) {
						dirtToGrass(i, j, k);
						grassToDirt(i, j, k);
					}
				}
			}

			kindaBetterGreedy();
			markForUpdate(false);
			debug("Updated " + this.toString() + " at " + x + ", " + y + ", " + z);
		}
	}

	// Render the chunk
	public void render(ModelBatch batch, Environment e) {
		if (object != null && !meshing)
			batch.render(object, e);
	}

	/*-------		Some CellId get methods			-------*/
	public CellId getHighestCellAt(int i, int j) {
		return getCell(i, getHighestYAt(i, j), j);
	}

	public int getHighestYAt(int i, int j) {
		for (int a = chunkSize; a >= 0; a--) {
			if (getCell(i, a, j) != null && getCell(i, a, j) != CellId.ID_AIR) {
				return a;
			}
		}
		return Integer.MAX_VALUE;
	}

	/*-------		Cells getters and setters			-------*/
	public CellId getCell(int i, int j, int k) {
		if (i >= 0 && j >= 0 && k >= 0 && i < chunkSize && j < chunkSize && k < chunkSize) {
			return cells[MathHelper.flatCell3Dto1D(i, j, k)];
		}
		return null;
	}

	public CellId getCell(int index) {
		if (index >= 0 && index < cells.length || index != Integer.MAX_VALUE) {
			return cells[index];
		}
		return null;

	}

	public void setCell(int i, int j, int k, CellId id) {
		if (i >= 0 && j >= 0 && k >= 0 && i < chunkSize && j < chunkSize && k < chunkSize) {
			cells[MathHelper.flatCell3Dto1D(i, j, k)] = id;
		}
		markForUpdate(true);
	}

	/*-------		Generators and decorators to use when chunk is being generated			-------*/
	public void generate() {
		if (!generated) {
			VoxelSettings.getWorldGenerator().generate(this);
			generated = true;
		}
	}

	public void decorate() {
		if (!decorated && VoxelSettings.decoratorsEnabled()) {
			VoxelSettings.getWorldDecorator().decorate(this);
			decorated = true;
		}
	}

	/*-------			An orrible implementation of save files			-------*/
	// Saves the chunk to text file, with format X Y Z ID, separated by spaces
	public void saveToFile() {
		File f = Paths.get(VoxelSettings.workingDir + x + "-" + y + "-" + z + ".chunk").toFile();
		int[] coords;

		if (!f.exists() && !isEmpty()) {
			try {
				PrintWriter writer = new PrintWriter(f);
				for (int i = 0; i < cells.length; i++) {
					coords = MathHelper.cell1Dto3D(i);
					writer.println(coords[0] + "," + coords[1] + "," + coords[2] + "," + cells[i]);
				}

				writer.close();
			} catch (FileNotFoundException e) {
			}
		}
	}

	// Retrieves back from the text file (X Y Z ID separated by commas)
	public void loadFromFile(File f) {
		List<String> lines;

		if (f.exists()) {
			if (!(f.length() == 0)) {
				try {
					lines = Files.readAllLines(f.toPath());

					for (String s : lines) {
						setCell(Integer.valueOf(s.split(",")[0]), Integer.valueOf(s.split(",")[1]),
								Integer.valueOf(s.split(",")[2]), CellId.valueOf(s.split(",")[3]));
					}

					generated = true;
					decorated = true;
					markForUpdate(true);
					f.delete();
				} catch (IOException | NumberFormatException e) {
				}
			} else {
				generated = false;
				decorated = false;
				generate();
			}
		} else {
			generated = false;
			decorated = false;
			generate();
		}
	}

	public void markForUpdate(boolean b) {
		toBeSet = b;
	}

	public void markMeshForUpdate(boolean b) {
		toUpdateMesh = b;
	}

	public void markInstanceForUpdate(boolean b) {
		toUpdateInstance = b;
	}

	/*-----			Greedy meshing algorithm. Doesn't quite work in GDX, was really faster and useful in jMonkeyEngine			-----*/
	static boolean[][] meshed = new boolean[chunkSize * chunkSize * chunkSize][6];

	// Here go vertices and normals
	ArrayList<Float> verts = new ArrayList<>();
	// Here go indices
	ArrayList<Short> indices = new ArrayList<>();
	short i0, i1, i2, i3;
	float[] verts2;
	short[] indices2;

	ModelBuilder builder;
	Node n;
	Model model;
	
	private void kindaBetterGreedy() {
		meshing = true;
		verts.clear();
		indices.clear();
		index = 0;

		if(model != null) model.dispose();
		if(object != null) {
			VoxelSettings.voxelWorld.dynamicsWorld.removeRigidBody(object.body);
			object.dispose();
		}
		
		for (int i = 0; i < meshed.length; i++) {
			for (int j = 0; j < 6; j++) {
				meshed[i][j] = false;
			}
		}

		int startX, startY, startZ, offX, offY, offZ, index, cPos, c1Pos;
		CellId c, c1;
		boolean done = false;

		for (int a = 0; a < cells.length; a++) {
			for (int s = 0; s < 3; s++) {
				for (int i = 0; i < 2; i++) {

					c = getCell(a);
					int backfaces[] = { 0, 0, 0 };
					backfaces[s] = i;
					index = s * 2 + i;

					if (c != CellId.ID_AIR && c != null && cellHasFreeSideChunk(a, index) && !meshed[a][index]) {
						cPos = a;
						startX = MathHelper.cell1Dto3D(cPos)[0];
						startY = MathHelper.cell1Dto3D(cPos)[1];
						startZ = MathHelper.cell1Dto3D(cPos)[2];

						offX = 0;
						offY = 0;
						offZ = 0;

						if (startX + offX < chunkSize && startY + offY < chunkSize && startZ + offZ < chunkSize) {
							if (s == 0 || s == 2) {
								offZ++;
							} else {
								offX++;
							}
						}

						c1Pos = MathHelper.flatCell3Dto1D(startX + offX, startY + offY, startZ + offZ);
						c1 = getCell(c1Pos);
						while (c1 != CellId.ID_AIR && c1 != null && c1 == c && cellHasFreeSideChunk(c1Pos, index)
								&& !meshed[c1Pos][index]) {

							meshed[c1Pos][index] = true;
							if (startX + offX < chunkSize && startY + offY < chunkSize && startZ + offZ < chunkSize) {

								if ((s == 0 || s == 2)) {
									offZ++;
								} else {
									offX++;
								}
							}
							c1Pos = MathHelper.flatCell3Dto1D(startX + offX, startY + offY, startZ + offZ);
							c1 = getCell(c1Pos);
						}

						done = false;

						switch (s) {
						case 0:
							while (!done) {
								offY++;
								for (int k = startZ; k < startZ + offZ; k++) {
									c1Pos = MathHelper.flatCell3Dto1D(startX + offX, startY + offY, k);
									c1 = getCell(c1Pos);

									if (c1 != c || meshed[c1Pos][index] || !cellHasFreeSideChunk(c1Pos, index)) {
										done = true;
										break;
									}
								}

								if (!done) {
									for (int k = startZ; k < startZ + offZ; k++) {
										c1Pos = MathHelper.flatCell3Dto1D(startX + offX, startY + offY, k);
										meshed[c1Pos][index] = true;
									}
								}
							}
							break;
						case 1:
							while (!done) {
								offY++;
								for (int k = startX; k < startX + offX; k++) {
									c1Pos = MathHelper.flatCell3Dto1D(k, startY + offY, startZ + offZ);
									c1 = getCell(c1Pos);

									if (c1 != c || !cellHasFreeSideChunk(c1Pos, index) || meshed[c1Pos][index]) {
										done = true;
										break;
									}
								}
								if (!done) {
									for (int k = startX; k < startX + offX; k++) {
										c1Pos = MathHelper.flatCell3Dto1D(k, startY + offY, startZ + offZ);
										c1 = getCell(c1Pos);
										meshed[c1Pos][index] = true;
									}
								}
							}
							break;
						case 2:
							while (!done) {
								offX++;
								for (int k = startZ; k < startZ + offZ; k++) {
									c1Pos = MathHelper.flatCell3Dto1D(startX + offX, startY + offY, k);
									c1 = getCell(c1Pos);

									if (c1 != c || !cellHasFreeSideChunk(c1Pos, index) || meshed[c1Pos][index]) {
										done = true;
										break;
									}
								}
								if (!done) {
									for (int k = startZ; k < startZ + offZ; k++) {
										c1Pos = MathHelper.flatCell3Dto1D(startX + offX, startY + offY, k);
										c1 = getCell(c1Pos);
										meshed[c1Pos][index] = true;
									}
								}
							}
							break;
						}
						meshed[cPos][index] = true;

						// sets the vertices
						switch (s) {
						case 0:
							for (int p = startZ; p < startZ + offZ; p++) {
								for (int q = startY; q < startY + offY; q++) {
									i0 = addVertex(startX + backfaces[0], q, p,
											TextureManagerAtlas.getTexture(c, index)[0],
											TextureManagerAtlas.getTexture(c, index)[3]);
									i1 = addVertex(startX + backfaces[0], q + 1, p,
											TextureManagerAtlas.getTexture(c, index)[0],
											TextureManagerAtlas.getTexture(c, index)[1]);
									i2 = addVertex(startX + backfaces[0], q + 1, p + 1,
											TextureManagerAtlas.getTexture(c, index)[2],
											TextureManagerAtlas.getTexture(c, index)[1]);
									i3 = addVertex(startX + backfaces[0], q, p + 1,
											TextureManagerAtlas.getTexture(c, index)[2],
											TextureManagerAtlas.getTexture(c, index)[3]);
									if (backfaces[0] == 1) {
										indices.add(i0);
										indices.add(i1);
										indices.add(i2);
										indices.add(i2);
										indices.add(i3);
										indices.add(i0);
									} else {
										indices.add(i0);
										indices.add(i3);
										indices.add(i2);
										indices.add(i2);
										indices.add(i1);
										indices.add(i0);
									}
								}
							}
							break;
						case 1:
							for (int p = startX; p < startX + offX; p++) {
								for (int q = startY; q < startY + offY; q++) {
									i0 = addVertex(p, q, startZ + backfaces[1],
											TextureManagerAtlas.getTexture(c, index)[0],
											TextureManagerAtlas.getTexture(c, index)[3]);
									i1 = addVertex(p, q + 1, startZ + backfaces[1],
											TextureManagerAtlas.getTexture(c, index)[0],
											TextureManagerAtlas.getTexture(c, index)[1]);
									i2 = addVertex(p + 1, q + 1, startZ + backfaces[1],
											TextureManagerAtlas.getTexture(c, index)[2],
											TextureManagerAtlas.getTexture(c, index)[1]);
									i3 = addVertex(p + 1, q, startZ + backfaces[1],
											TextureManagerAtlas.getTexture(c, index)[2],
											TextureManagerAtlas.getTexture(c, index)[3]);
									if (backfaces[1] == 1) {
										indices.add(i0);
										indices.add(i3);
										indices.add(i2);
										indices.add(i2);
										indices.add(i1);
										indices.add(i0);
									} else {
										indices.add(i0);
										indices.add(i1);
										indices.add(i2);
										indices.add(i2);
										indices.add(i3);
										indices.add(i0);
									}
								}
							}
							break;

						case 2:
							for (int p = startZ; p < startZ + offZ; p++) {
								for (int q = startX; q < startX + offX; q++) {
									i0 = addVertex(q, startY + backfaces[2], p,
											TextureManagerAtlas.getTexture(c, index)[0],
											TextureManagerAtlas.getTexture(c, index)[3]);
									i1 = addVertex(q + 1, startY + backfaces[2], p,
											TextureManagerAtlas.getTexture(c, index)[0],
											TextureManagerAtlas.getTexture(c, index)[1]);
									i2 = addVertex(q + 1, startY + backfaces[2], p + 1,
											TextureManagerAtlas.getTexture(c, index)[2],
											TextureManagerAtlas.getTexture(c, index)[1]);
									i3 = addVertex(q, startY + backfaces[2], p + 1,
											TextureManagerAtlas.getTexture(c, index)[2],
											TextureManagerAtlas.getTexture(c, index)[3]);
									if (backfaces[2] == 1) {
										indices.add(i0);
										indices.add(i3);
										indices.add(i2);
										indices.add(i2);
										indices.add(i1);
										indices.add(i0);
									} else {
										indices.add(i0);
										indices.add(i1);
										indices.add(i2);
										indices.add(i2);
										indices.add(i3);
										indices.add(i0);
									}
								}
							}
							break;
						default:
							System.out.println("puzzette");
							break;
						}

					}

				}
			}
		}

		verts2 = new float[verts.size()];
		indices2 = new short[indices.size()];
		for (int p = 0; p < verts.size(); p++) {
			verts2[p] = verts.get(p);
//			if (p % 5 == 0)
//				verts2[p] += pos.x;
//			if (p % 5 == 1)
//				verts2[p] += pos.y;
//			if (p % 5 == 2)
//				verts2[p] += pos.z;
		}
		for (int p = 0; p < indices.size(); p++)
			indices2[p] = indices.get(p);

//		System.out.println(verts2.length + " " + Arrays.toString(verts2));
//		System.out.println(indices2.length + " " + Arrays.toString(indices2));

		mesh = new Mesh(false, verts2.length / 4, indices2.length,
				new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
		mesh.setVertices(verts2);
		mesh.setIndices(indices2);

		builder = new ModelBuilder();
		builder.begin();
		n = builder.node();
		n.id = "a";
		MeshPartBuilder builder2 = builder.part("a", GL20.GL_TRIANGLES,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates,
				TextureManagerAtlas.material);
		builder2.addMesh(mesh);
		Model model = builder.end();
		instance = new ModelInstance(model);

		constructor = new GameObject.Constructor(model, "a",
				Bullet.obtainStaticNodeShape(instance.nodes), 0);
		object = constructor.construct();

		object.transform.trn(pos);
		object.body.proceedToTransform(object.transform);

		object.body.setCollisionFlags(
				object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		VoxelSettings.voxelWorld.dynamicsWorld.addRigidBody(object.body);
		object.body.setContactCallbackFlag(VoxelSettings.GROUND_FLAG);
		object.body.setContactCallbackFilter(0);
		object.body.setActivationState(Collision.DISABLE_DEACTIVATION);

		meshing = false;
	}

	btGhostPairCallback ghostPairCallback;
	btPairCachingGhostObject ghostObject;
	btConvexShape ghostShape;
	btKinematicCharacterController characterController;
	Matrix4 characterTransform;
	Vector3 characterDirection = new Vector3();
	Vector3 walkDirection = new Vector3();

	/**
	 * @javadoc Adds the vertices to the array and returns the index of the vertex
	 * 
	 * @param x X position value of the vertex
	 * @param y Y position value of the vertex
	 * @param z Z position value of the vertex
	 */
	short index = 0;

	private short addVertex(int x, int y, int z, float u, float v) {
		verts.add((float) x);
		verts.add((float) y);
		verts.add((float) z);
		verts.add(u);
		verts.add(v);
		return index++;
	}

	/*-----			To know some infos about the chunk and the cells		----- */
	public boolean isVisible() {
		boolean v = false;

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (x + i >= 0 && x + i < MAXX && y + j >= 0 && y + j < MAXY && z + k >= 0 && z + k < MAXZ) {
						if (VoxelSettings.voxelWorld.worldManager.getChunk(x + i, y + j, z + k) != null) {
							v = true;
						}
					} else {
						v = true;
					}
				}
			}
		}
		return v;
	}

	public boolean isEmpty() {
		for (int i = 0; i < cells.length; i++) {
			if (cells[i] != CellId.ID_AIR) {
				return false;
			}
		}
		return true;
	}

	public boolean cellHasFreeSideWorld(int cellX, int cellY, int cellZ, int side) {
//        System.out.println("Checking at world coords " + cellX + ", " + cellY + ", " + cellZ + " with side " + side);
		switch (side) {
		case 0:
			return (VoxelSettings.voxelWorld.worldManager.getCell(cellX - 1, cellY, cellZ) == CellId.ID_AIR
					|| VoxelSettings.voxelWorld.worldManager.getCell(cellX - 1, cellY, cellZ) == null);
		case 1:
			return (VoxelSettings.voxelWorld.worldManager.getCell(cellX + 1, cellY, cellZ) == CellId.ID_AIR
					|| VoxelSettings.voxelWorld.worldManager.getCell(cellX + 1, cellY, cellZ) == null);
		case 2:
			return (VoxelSettings.voxelWorld.worldManager.getCell(cellX, cellY, cellZ - 1) == CellId.ID_AIR
					|| VoxelSettings.voxelWorld.worldManager.getCell(cellX, cellY, cellZ - 1) == null);
		case 3:
			return (VoxelSettings.voxelWorld.worldManager.getCell(cellX, cellY, cellZ + 1) == CellId.ID_AIR
					|| VoxelSettings.voxelWorld.worldManager.getCell(cellX, cellY, cellZ + 1) == null);
		case 4:
			return (VoxelSettings.voxelWorld.worldManager.getCell(cellX, cellY - 1, cellZ) == CellId.ID_AIR
					|| VoxelSettings.voxelWorld.worldManager.getCell(cellX, cellY - 1, cellZ) == null);
		case 5:
			return (VoxelSettings.voxelWorld.worldManager.getCell(cellX, cellY + 1, cellZ) == CellId.ID_AIR
					|| VoxelSettings.voxelWorld.worldManager.getCell(cellX, cellY + 1, cellZ) == null);
		default:
			System.out.println("Ouch!");
			return false;
		}
	}

	public boolean cellHasFreeSideChunkToWorld(int cPos, int side) {
		return cellHasFreeSideWorld(x * chunkSize + MathHelper.cell1Dto3D(cPos)[0],
				y * chunkSize + MathHelper.cell1Dto3D(cPos)[1], z * chunkSize + MathHelper.cell1Dto3D(cPos)[2], side);
	}

	public boolean cellHasFreeSideChunkToWorld(int cellChunkX, int cellChunkY, int cellChunkZ, int side) {
		return cellHasFreeSideWorld(x * chunkSize + cellChunkX, y * chunkSize + cellChunkY, z * chunkSize + cellChunkZ,
				side);
	}

	public boolean cellHasFreeSideChunk(int cPos, int side) {
		return cellHasFreeSideChunk(MathHelper.cell1Dto3D(cPos)[0], MathHelper.cell1Dto3D(cPos)[1],
				MathHelper.cell1Dto3D(cPos)[2], side);
	}

	public boolean cellHasFreeSideChunk(int cellX, int cellY, int cellZ, int side) {
		switch (side) {
		case 0:
			return (getCell(cellX - 1, cellY, cellZ) == CellId.ID_AIR || getCell(cellX - 1, cellY, cellZ) == null);
		case 1:
			return (getCell(cellX + 1, cellY, cellZ) == CellId.ID_AIR || getCell(cellX + 1, cellY, cellZ) == null);
		case 2:
			return (getCell(cellX, cellY, cellZ - 1) == CellId.ID_AIR || getCell(cellX, cellY, cellZ - 1) == null);
		case 3:
			return (getCell(cellX, cellY, cellZ + 1) == CellId.ID_AIR || getCell(cellX, cellY, cellZ + 1) == null);
		case 4:
			return (getCell(cellX, cellY - 1, cellZ) == CellId.ID_AIR || getCell(cellX, cellY - 1, cellZ) == null);
		case 5:
			return (getCell(cellX, cellY + 1, cellZ) == CellId.ID_AIR || getCell(cellX, cellY + 1, cellZ) == null);
		default:
			System.out.println("Ouch!");
			return false;
		}
	}

	public void dirtToGrass(int cellX, int cellY, int cellZ) {
		if (getCell(cellX, cellY, cellZ) == CellId.ID_DIRT && getCell(cellX, cellY + 1, cellZ) == CellId.ID_AIR) {
			setCell(cellX, cellY, cellZ, CellId.ID_GRASS);
		}
	}

	public void grassToDirt(int cellX, int cellY, int cellZ) {
		if (getCell(cellX, cellY, cellZ) == CellId.ID_GRASS && getCell(cellX, cellY + 1, cellZ) != CellId.ID_AIR) {
			setCell(cellX, cellY, cellZ, CellId.ID_DIRT);
		}
	}

	public String info() {
		return (this.toString() + " at " + x + ", " + y + ", " + z);
	}

//	@Override
	public void dispose() {
		if(mesh != null) mesh.dispose();
	}

}