package com.emamaker.voxelengine.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.emamaker.voxelengine.VoxelWorld;

public class TextureManager {

	// offset in the array, x in the atlas, y in the atlas
	public static final int OFF_DIRT = 0;
	public static final int OFF_GRASS_SIDE = 1;
	public static final int OFF_GRASS_TOP = 2;
	public static final int OFF_LEAVES = 3;
	public static final int OFF_WOOD_SIDE = 4;
	public static final int OFF_WOOD_TOP_BOTTOM = 5;
	public static final int OFF_STONE = 6;

	public static HashMap<Integer,Texture> images = new HashMap<Integer,Texture>();
	public static HashMap<CellId, int[]> textures = new HashMap<CellId, int[]>();
	public static HashMap<Integer, Material> materials = new HashMap<Integer,Material>();

	public VoxelWorld voxelWorld;

	public TextureManager(VoxelWorld world) {
		voxelWorld = world;

		addTexture(OFF_DIRT, new Texture("/home/emamaker/github/voxel-engine-gdx/android/assets/dirt.png"));
		addTexture(OFF_GRASS_SIDE, new Texture("/home/emamaker/github/voxel-engine-gdx/android/assets/grass_side.png"));
		addTexture(OFF_GRASS_TOP, new Texture("/home/emamaker/github/voxel-engine-gdx/android/assets/grass_top.png"));
		addTexture(OFF_LEAVES, new Texture("/home/emamaker/github/voxel-engine-gdx/android/assets/leaves.jpg"));
		addTexture(OFF_WOOD_SIDE, new Texture("/home/emamaker/github/voxel-engine-gdx/android/assets/wood_side.png"));
		addTexture(OFF_WOOD_TOP_BOTTOM, new Texture("/home/emamaker/github/voxel-engine-gdx/android/assets/wood_bottom_top.png"));
		addTexture(OFF_STONE, new Texture("/home/emamaker/github/voxel-engine-gdx/android/assets/stone.jpg"));
		
		setIdTexture(CellId.ID_GRASS, OFF_GRASS_SIDE, OFF_GRASS_SIDE, OFF_GRASS_SIDE, OFF_GRASS_SIDE, OFF_DIRT,
				OFF_GRASS_TOP);
		setIdTexture(CellId.ID_DIRT, OFF_DIRT, OFF_DIRT, OFF_DIRT, OFF_DIRT, OFF_DIRT, OFF_DIRT);
		setIdTexture(CellId.ID_WOOD, OFF_WOOD_SIDE, OFF_WOOD_SIDE, OFF_WOOD_SIDE, OFF_WOOD_SIDE, OFF_WOOD_TOP_BOTTOM,
				OFF_WOOD_TOP_BOTTOM);
		setIdTexture(CellId.ID_LEAVES, OFF_LEAVES, OFF_LEAVES, OFF_LEAVES, OFF_LEAVES, OFF_LEAVES, OFF_LEAVES);
		setIdTexture(CellId.ID_STONE, OFF_STONE, OFF_STONE, OFF_STONE, OFF_STONE, OFF_STONE, OFF_STONE);
		
	}

	public void setIdTexture(CellId idDirt, int offWest, int offEast, int offNorth, int offSouth, int offBottom, int offTop) {
		int[] offsets = { offWest, offEast, offNorth, offSouth, offBottom, offTop };
		textures.put(idDirt, offsets);
	}

	public void addTexture(int offset, Texture img) {
		img.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		images.put(offset, img);
		addMaterial(offset, new Material(TextureAttribute.createDiffuse(img)));
	}
	
	public void addMaterial(int id, Material mat) {
		materials.put(id, mat);
	}
	
	public static Texture getTexture(CellId id, int index) {
		return images.get(textures.get(id)[index]);
	}
	
	public static Material getMaterial(CellId id, int index) {
		return materials.get(textures.get(id)[index]);
	}

}
