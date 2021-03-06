package com.emamaker.voxelengine.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.emamaker.voxelengine.VoxelWorld;
import com.emamaker.voxelengine.world.decorators.WorldDecorator;
import com.emamaker.voxelengine.world.decorators.WorldDecoratorTrees;
import com.emamaker.voxelengine.world.generators.WorldGenerator;
import com.emamaker.voxelengine.world.generators.WorldGeneratorBase;
import com.emamaker.voxelengine.world.generators.WorldGeneratorCube;
import com.emamaker.voxelengine.world.generators.WorldGeneratorTerrain;

public class VoxelSettings {
	
	public final static short GROUND_FLAG = 1 << 8;
	public final static short OBJECT_FLAG = 1 << 9;
	public final static short ALL_FLAG = -1;

	// the lenght of a chunk side
	public static int chunkSize = 16;
	public static int blockSize = 1;

    //max world height to be generated
    //basically it's the number of cubic chunks to generator under the simplex-noise generated ones
	public static int worldHeight = 1;

	// a static instantiate of Main class
	public static VoxelWorld voxelWorld;
	public static ModelBuilder modelBuilder = new ModelBuilder();
	
	// settings
	static boolean TESTING = false;
	static boolean enableDebug = false;
	static boolean enablePhysics = true;
	static boolean enablePlayer = true;
	static boolean enableWireframe = false;
	static boolean usePlayer = false;

	static WorldGenerator generator = new WorldGeneratorBase();
	static String generatorS = "", decoratorS = "";
	static WorldDecorator decorator = new WorldDecoratorTrees();
	static boolean enableDecorators = true;

	public static boolean LOAD_FROM_FILE = false;
	public static boolean SAVE_ON_EXIT = false;

	public static String workingDir = System.getProperty("user.dir") + "/chunk-saves/";
	public static String permtableName = "perm.table";

	public static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);

	public static int MAXX = 100, MAXY = 40, MAXZ = 100;
	public static int pX = 8, pY = 16, pZ = 8;
	public static int pGX = 8, pGY = 8, pGZ = 8;
	public static int renderDistance = 8;
	static int pickingDistance = 6;

	@SuppressWarnings("serial")
	static HashMap<String, WorldDecorator> decorators = new HashMap<String,WorldDecorator>(){{put("decoratorTrees",new WorldDecoratorTrees());}};
	@SuppressWarnings("serial")
	static HashMap<String, WorldGenerator> generators = new HashMap<String,WorldGenerator>(){{put("generatorBase",new WorldGeneratorBase());put("generatorCube",new WorldGeneratorCube());put("generatorTerrain",new WorldGeneratorTerrain());}};

	public VoxelSettings(VoxelWorld world) {
		VoxelSettings.voxelWorld = world;

		if (VoxelSettings.LOAD_FROM_FILE) {
			VoxelSettings.loadFromFile();
		}
	}


	public static void saveToFile() {
		String save = "";
		save += "chunkSize=" + chunkSize + "\n";
		save += "worldHeight=" + worldHeight + "\n";
		save += "TESTING=" + TESTING + "\n";
		save += "enableDebug=" + enableDebug + "\n";
		save += "enablePhysics=" + enablePhysics + "\n";
		save += "enablePlayer=" + enablePlayer + "\n";
		save += "enableWireframe=" + enableWireframe + "\n";
		save += "worldGenerator=" + generatorS + "\n";
		save += "worldDecorator=" + decoratorS + "\n";
		save += "enableDecorators=" + enableDecorators + "\n";
		save += "LOAD_FROM_FILE=" + LOAD_FROM_FILE + "\n";
		save += "SAVE_ON_EXIT=" + SAVE_ON_EXIT + "\n";
		save += "workingDir=" + workingDir + "\n";
		save += "permtableName=" + permtableName + "\n";
		save += "MAXX=" + MAXX + "\n";
		save += "MAXY=" + MAXY + "\n";
		save += "MAXZ=" + MAXZ + "\n";
		save += "pX=" + pX + "\n";
		save += "pY=" + pY + "\n";
		save += "pZ=" + pZ + "\n";
//        save += "pGX=" + (int) engine.getCamera().getLocation().getX() + "\n";
//        save += "pGY=" + (int) engine.getCamera().getLocation().getY() + "\n";
//        save += "pGZ=" + (int) engine.getCamera().getLocation().getZ() + "\n";
		save += "renderDistance=" + renderDistance + "\n";
		save += "pickingDistance=" + pickingDistance + "\n";

		File f = Paths.get(VoxelSettings.workingDir + "settings").toFile();
		f.delete();

		if (!f.exists()) {
			try {
				PrintWriter writer = new PrintWriter(f);
				writer.print(save);
				writer.close();
			} catch (FileNotFoundException e) {
			}
		}
	}

	public static void loadFromFile() {
		File f = Paths.get(VoxelSettings.workingDir + "settings").toFile();
		String[] s1;

		try {
			for (String s : Files.readAllLines(f.toPath())) {
				s1 = s.split("=");
				// System.out.println(s1[1]);
				switch (s1[0]) {
				case "chunkSize":
					VoxelSettings.chunkSize = Integer.valueOf(s1[1]);
					break;
				case "worldHeight":
					VoxelSettings.worldHeight = Integer.valueOf(s1[1]);
					break;
				case "TESTING":
					VoxelSettings.TESTING = Boolean.valueOf(s1[1]);
					break;
				case "enableDebug":
					VoxelSettings.enableDebug = Boolean.valueOf(s1[1]);
					break;
				case "enablePhysics":
					VoxelSettings.enablePhysics = Boolean.valueOf(s1[1]);
					break;
				case "enablePlayer":
					VoxelSettings.enablePlayer = Boolean.valueOf(s1[1]);
					break;
				case "enableWireframe":
					VoxelSettings.enableWireframe = Boolean.valueOf(s1[1]);
					break;
//                    case "worldGenerator":
//                        Globals.setWorldGenerator(s1[1]);
//                        break;
//                    case "worldDecorator":
//                        Globals.setWorldDecorator(s1[1]);
//                        break;
				case "enableDecorators":
					VoxelSettings.enableDecorators = Boolean.valueOf(s1[1]);
					break;
				case "LOAD_FROM_FILE":
					VoxelSettings.LOAD_FROM_FILE = Boolean.valueOf(s1[1]);
					break;
				case "SAVE_ON_EXIT":
					VoxelSettings.SAVE_ON_EXIT = Boolean.valueOf(s1[1]);
					break;
				case "workingDir":
					VoxelSettings.workingDir = s1[1];
					break;
				case "permtableName":
					VoxelSettings.permtableName = s1[1];
					break;
				case "MAXX":
					VoxelSettings.MAXX = Integer.valueOf(s1[1]);
					break;
				case "MAXY":

					VoxelSettings.MAXY = Integer.valueOf(s1[1]);
					break;
				case "MAXZ":
					VoxelSettings.MAXZ = Integer.valueOf(s1[1]);
					break;
				case "pX":
					VoxelSettings.pX = Integer.valueOf(s1[1]);
					break;
				case "pY":
					VoxelSettings.pY = Integer.valueOf(s1[1]);
					break;
				case "pZ":
					VoxelSettings.pZ = Integer.valueOf(s1[1]);
					break;
				case "pGX":
					VoxelSettings.pGX = Integer.valueOf(s1[1]);
					break;
				case "pGY":
					VoxelSettings.pGY = Integer.valueOf(s1[1]);
					break;
				case "pGZ":
					VoxelSettings.pGZ = Integer.valueOf(s1[1]);
					break;
				case "renderDistance":
					renderDistance = Integer.valueOf(s1[1]);
					break;
				case "pickingDistance":
					pickingDistance = Integer.valueOf(s1[1]);
					break;
				default:
					System.out.println("Ouch " + s);
					break;
				}
			}
			f.delete();
		} catch (IOException | NumberFormatException e) {
			// e.printStackTrace();
		}
	}

//    public static HashMap getGenerators() {
//        return generators;
//    }
//
//    public static HashMap getDecorators() {
//        return decorators;
//    }

	// Actually only does prints in console things, but it's useful to not comment
	// the debug lines each time, but only pressing a key
	public static void debug(Object... s) {
		if (enableDebug) {
			System.out.println("Debugging: " + Arrays.toString(s));
		}
	}

	public static void debug(Exception e) {
		debug(Arrays.toString(e.getStackTrace()));
	}

	public static void setPhysicsEnabled(boolean enable) {
		enablePhysics = enable;
	}

	public static boolean playerEnabled() {
		return enablePlayer;
	}

	public static void setPlayerEnabled(boolean enable) {
		enablePlayer = enable;
	}

	public static boolean phyEnabled() {
		return enablePhysics;
	}

	public static void setDebugEnabled(boolean enable) {
		enableDebug = enable;
	}

	public static boolean debugEnabled() {
		return enableDebug;
	}

	public static void setWireFrameEnabled(boolean enable) {
		enableWireframe = enable;
	}

	public static boolean wireFrameEnabled() {
		return enableWireframe;
	}

	public static void setWorkingDir(String s) {
		workingDir = s;
	}

	public static String getWorkingDir() {
		return workingDir;
	}

	public static void setPermTable(String s) {
		permtableName = s;
	}

	public static String getPermTable() {
		return permtableName;
	}

	public static void setBlockSize(int s) {
		blockSize = s;
	}

	public static int getBlockSize() {
		return blockSize;
	}

	public static void setWorldHeight(int s) {
		worldHeight = s;
	}

	public static int getWorldHeight() {
		return worldHeight;
	}

	public static void setStartPoint(int x, int y, int z) {
		pX = x;
		pY = y;
		pZ = z;
	}

	public static void setRenderDistance(int render) {
		renderDistance = render;
	}

	public static int getRenderDistance() {
		return renderDistance;
	}

	public static void setPickingDistance(int picking) {
		pickingDistance = picking;
	}

	public static int getPickingDistance() {
		return pickingDistance;
	}

	public static void setTesting(boolean b) {
		TESTING = b;
	}

	public static boolean isTesting() {
		return TESTING;
	}

	public static void setWorldGenerator(WorldGenerator g) {
		generator = g;
	}

	public static void setWorldDecorator(WorldDecorator d) {
		decorator = d;
	}

	public static void setWorldGenerator(String s) {
		if (VoxelSettings.generators.containsKey(s)) {
			generator = VoxelSettings.generators.get(s);
			generatorS = s;
		}
	}

	public static WorldGenerator getWorldGenerator() {
		return generator;
	}

	public static void setWorldDecorator(String s) {
		if (VoxelSettings.decorators.containsKey(s)) {
			decorator = VoxelSettings.decorators.get(s);
			decoratorS = s;
		}
	}

	public static WorldDecorator getWorldDecorator() {
		return decorator;
	}

	public static void enableDecorators(boolean b) {
		enableDecorators = b;
	}

	public static boolean decoratorsEnabled() {
		return enableDecorators;
	}

	public static void setWorldSize(int x, int y, int z) {
		MAXX = x;
		MAXY = y;
		MAXZ = z;
	}
	
	public static void usePlayer(boolean b) {
		usePlayer = b;
	}
	
	public static boolean isUsingPlayer() {
		return usePlayer;
	}

}
