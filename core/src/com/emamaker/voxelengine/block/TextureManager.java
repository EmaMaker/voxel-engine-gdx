package com.emamaker.voxelengine.block;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.emamaker.voxelengine.VoxelWorld;

public class TextureManager {

	// offset in the array, x in the atlas, y in the atlas
	public static final int[] OFF_DIRT = { 0, 0, 0 };
	public static final int[] OFF_GRASS_SIDE = { 1, 1, 0 };
	public static final int[] OFF_GRASS_TOP = { 2, 2, 0 };
	public static final int[] OFF_LEAVES = {3, 3, 0};
	public static final int[] OFF_WOOD_SIDE = { 4, 4, 0 };
	public static final int[] OFF_WOOD_TOP_BOTTOM = { 5, 5, 0 };
	public static final int[] OFF_STONE = { 6, 6, 0 };

	public static List<TextureRegion> images = new ArrayList<TextureRegion>();
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
    	
    	addTextureRegion(OFF_DIRT[0], new TextureRegion(textureAtlas, OFF_DIRT[1]*TEXTURE_WIDTH, OFF_DIRT[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
    	addTextureRegion(OFF_GRASS_SIDE[0], new TextureRegion(textureAtlas, OFF_GRASS_SIDE[1]*TEXTURE_WIDTH, OFF_GRASS_SIDE[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
    	addTextureRegion(OFF_GRASS_TOP[0], new TextureRegion(textureAtlas, OFF_GRASS_TOP[1]*TEXTURE_WIDTH, OFF_GRASS_TOP[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
    	addTextureRegion(OFF_LEAVES[0], new TextureRegion(textureAtlas, OFF_LEAVES[1]*TEXTURE_WIDTH, OFF_LEAVES[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
    	addTextureRegion(OFF_WOOD_SIDE[0], new TextureRegion(textureAtlas, OFF_WOOD_SIDE[1]*TEXTURE_WIDTH, OFF_WOOD_SIDE[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
    	addTextureRegion(OFF_WOOD_TOP_BOTTOM[0], new TextureRegion(textureAtlas, OFF_WOOD_TOP_BOTTOM[1]*TEXTURE_WIDTH, OFF_WOOD_TOP_BOTTOM[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));
    	addTextureRegion(OFF_STONE[0], new TextureRegion(textureAtlas, OFF_STONE[1]*TEXTURE_WIDTH, OFF_STONE[2]*TEXTURE_WIDTH, TEXTURE_WIDTH, TEXTURE_WIDTH));

//        addImage(OFF_DIRT[0],main.getAssetManager().loadTexture("Textures/dirt.png").getImage());
//        addImage(OFF_GRASS_SIDE[0], main.getAssetManager().loadTexture("Textures/grass_side.png").getImage());
//        addImage(OFF_GRASS_TOP, main.getAssetManager().loadTexture("Textures/grass_top.png").getImage());
//        addImage(OFF_WOOD_SIDE[0], main.getAssetManager().loadTexture("Textures/wood_side.png").getImage());
//        addImage(OFF_WOOD_TOP_BOTTOM[0], main.getAssetManager().loadTexture("Textures/wood_bottom_top.png").getImage());
//        addImage(OFF_STONE[0], main.getAssetManager().loadTexture("Textures/stone.jpg").getImage());
//        addImage(OFF_LEAVES[0] main.getAssetManager().loadTexture("Textures/leaves.jpg").getImage());

        setIdTexture(CellId.ID_GRASS, OFF_GRASS_SIDE[0], OFF_GRASS_SIDE[0], OFF_GRASS_SIDE[0], OFF_GRASS_SIDE[0], OFF_DIRT[0],OFF_GRASS_TOP[0]);
        setIdTexture(CellId.ID_DIRT, OFF_DIRT[0],OFF_DIRT[0],OFF_DIRT[0],OFF_DIRT[0],OFF_DIRT[0],OFF_DIRT[0]);
        setIdTexture(CellId.ID_WOOD, OFF_WOOD_SIDE[0], OFF_WOOD_SIDE[0], OFF_WOOD_SIDE[0], OFF_WOOD_SIDE[0], OFF_WOOD_TOP_BOTTOM[0], OFF_WOOD_TOP_BOTTOM[0]);
        setIdTexture(CellId.ID_LEAVES, OFF_LEAVES[0], OFF_LEAVES[0], OFF_LEAVES[0], OFF_LEAVES[0], OFF_LEAVES[0], OFF_LEAVES[0]);
        setIdTexture(CellId.ID_STONE, OFF_STONE[0], OFF_STONE[0], OFF_STONE[0], OFF_STONE[0], OFF_STONE[0], OFF_STONE[0]);
    }

	public void setIdTexture(int id, int offWest, int offEast, int offNorth, int offSouth, int offBottom, int offTop) {
		int[] offsets = { offWest, offEast, offNorth, offSouth, offBottom, offTop };
		textures.add(id, offsets);
	}

	public void addTextureRegion(int offset, TextureRegion img) {
		images.add(offset, img);
	}

	public static TextureRegion getImageForId(int id, int index) {
		return images.get(textures.get(id)[index]);
	}
	
}
