//package com.emamaker.voxelengine.block;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.Texture.TextureWrap;
//import com.badlogic.gdx.graphics.g3d.Material;
//import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
//import com.emamaker.voxelengine.VoxelWorld;
//
//public class TextureManager {
//
//	// offset in the array, x in the atlas, y in the atlas
//	public static final int OFF_DIRT = 0;
//	public static final int OFF_GRASS_SIDE = 1;
//	public static final int OFF_GRASS_TOP = 2;
//	public static final int OFF_LEAVES = 3;
//	public static final int OFF_WOOD_SIDE = 4;
//	public static final int OFF_WOOD_TOP_BOTTOM = 5;
//	public static final int OFF_STONE = 6;
//
//	public static List<Texture> images = new ArrayList<Texture>();
//	public static List<int[]> textures = new ArrayList<int[]>();
//	public static List<Material> materials = new ArrayList<Material>();
//
//	public static final int TEXTURE_WIDTH = 64;
//	public static final int TEXTURE_HEIGHT = 64;
//
//	public static final int ATLAS_WIDTH = 1024;
//	public static final int ATLAS_HEIGHT = 1024;
//
//	public VoxelWorld voxelWorld;
////    SimpleApplication main;
//
//	Texture textureAtlas = new Texture("texture_atlas.png");
//
//	public TextureManager(VoxelWorld world) {
//		voxelWorld = world;
//
//		addTexture(OFF_DIRT, new Texture("dirt.png"));
//		addTexture(OFF_GRASS_SIDE, new Texture("grass_side.png"));
//		addTexture(OFF_GRASS_TOP, new Texture("grass_top.png"));
//		addTexture(OFF_LEAVES, new Texture("leaves.jpg"));
//		addTexture(OFF_WOOD_SIDE, new Texture("wood_side.png"));
//		addTexture(OFF_WOOD_TOP_BOTTOM, new Texture("wood_bottom_top.png"));
//		addTexture(OFF_STONE, new Texture("stone.jpg"));
//		
//		setIdTexture(CellId.ID_GRASS, OFF_GRASS_SIDE, OFF_GRASS_SIDE, OFF_GRASS_SIDE, OFF_GRASS_SIDE, OFF_DIRT,
//				OFF_GRASS_TOP);
//		setIdTexture(CellId.ID_DIRT, OFF_DIRT, OFF_DIRT, OFF_DIRT, OFF_DIRT, OFF_DIRT, OFF_DIRT);
//		setIdTexture(CellId.ID_WOOD, OFF_WOOD_SIDE, OFF_WOOD_SIDE, OFF_WOOD_SIDE, OFF_WOOD_SIDE, OFF_WOOD_TOP_BOTTOM,
//				OFF_WOOD_TOP_BOTTOM);
//		setIdTexture(CellId.ID_LEAVES, OFF_LEAVES, OFF_LEAVES, OFF_LEAVES, OFF_LEAVES, OFF_LEAVES, OFF_LEAVES);
//		setIdTexture(CellId.ID_STONE, OFF_STONE, OFF_STONE, OFF_STONE, OFF_STONE, OFF_STONE, OFF_STONE);
//	}
//
//	public void setIdTexture(int id, int offWest, int offEast, int offNorth, int offSouth, int offBottom, int offTop) {
//		int[] offsets = { offWest, offEast, offNorth, offSouth, offBottom, offTop };
//		textures.add(id, offsets);
//	}
//
//	public void addTexture(int offset, Texture img) {
//		img.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
//		images.add(offset, img);
//		addMaterial(offset, new Material(TextureAttribute.createDiffuse(img)));
//	}
//	
//	public void addMaterial(int id, Material mat) {
//		materials.add(id, mat);
//	}
//	
//	public static Texture getTexture(int id, int index) {
//		return images.get(textures.get(id)[index]);
//	}
//	
//	public static Material getMaterial(int id, int index) {
//		return materials.get(textures.get(id)[index]);
//	}
//
//}
