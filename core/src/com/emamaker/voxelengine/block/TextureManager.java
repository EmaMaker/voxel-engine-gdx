package com.emamaker.voxelengine.block;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
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

	public static List<Texture> images = new ArrayList<Texture>();
	public static List<int[]> textures = new ArrayList<int[]>();

	public static final int TEXTURE_WIDTH = 64;
	public static final int TEXTURE_HEIGHT = 64;

	public static final int ATLAS_WIDTH = 1024;
	public static final int ATLAS_HEIGHT = 1024;

	public VoxelWorld voxelWorld;
//    SimpleApplication main;

	Texture textureAtlas = new Texture("texture_atlas.png");

	public TextureManager(VoxelWorld world) {
		voxelWorld = world;

//    	addTextureRegion(OFF_DIRT, new TextureRegion(textureAtlas, OFF_DIRT[1]*TEXTURE_WIDTH, OFF_DIRT[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
//    	addTextureRegion(OFF_GRASS_SIDE, new TextureRegion(textureAtlas, OFF_GRASS_SIDE[1]*TEXTURE_WIDTH, OFF_GRASS_SIDE[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
//    	addTextureRegion(OFF_GRASS_TOP, new TextureRegion(textureAtlas, OFF_GRASS_TOP[1]*TEXTURE_WIDTH, OFF_GRASS_TOP[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
//    	addTextureRegion(OFF_LEAVES, new TextureRegion(textureAtlas, OFF_LEAVES[1]*TEXTURE_WIDTH, OFF_LEAVES[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
//    	addTextureRegion(OFF_WOOD_SIDE, new TextureRegion(textureAtlas, OFF_WOOD_SIDE[1]*TEXTURE_WIDTH, OFF_WOOD_SIDE[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
//    	addTextureRegion(OFF_WOOD_TOP_BOTTOM, new TextureRegion(textureAtlas, OFF_WOOD_TOP_BOTTOM[1]*TEXTURE_WIDTH, OFF_WOOD_TOP_BOTTOM[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
//    	addTextureRegion(OFF_STONE, new TextureRegion(textureAtlas, OFF_STONE[1]*TEXTURE_WIDTH, OFF_STONE[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));

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

	public void setIdTexture(int id, int offWest, int offEast, int offNorth, int offSouth, int offBottom, int offTop) {
		int[] offsets = { offWest, offEast, offNorth, offSouth, offBottom, offTop };
		textures.add(id, offsets);
	}

	public void addTexture(int offset, Texture img) {
		img.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		images.add(offset, img);
	}
	
	public static Texture getTexture(int id, int index) {
		return images.get(textures.get(id)[index]);
	}

}
