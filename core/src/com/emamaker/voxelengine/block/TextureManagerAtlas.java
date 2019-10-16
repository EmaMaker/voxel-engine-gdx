package com.emamaker.voxelengine.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.emamaker.voxelengine.VoxelWorld;

public class TextureManagerAtlas {

	// offset in the array, x in the atlas, y in the atlas
	public static final int[] OFF_DIRT = {0, 0, 0};
	public static final int[] OFF_GRASS_SIDE = {1, 1, 0};
	public static final int[] OFF_GRASS_TOP = {2, 2, 0};
	public static final int[] OFF_LEAVES = {3, 3, 0};
	public static final int[] OFF_WOOD_SIDE = {4, 4, 0};
	public static final int[] OFF_WOOD_TOP_BOTTOM = {5, 5, 0};
	public static final int[] OFF_STONE = {6, 6, 0};

//	public static List<TextureRegion> images = new ArrayList<TextureRegion>();
	public static List<float[]> images = new ArrayList<float[]>();
	public static List<int[]> textures = new ArrayList<int[]>();
	public static List<Material> materials = new ArrayList<Material>();

	public static final float TEXTURE_WIDTH = 64;
	public static final float TEXTURE_HEIGHT = 64;

	public static final float ATLAS_WIDTH = 1024;
	public static final float ATLAS_HEIGHT = 1024;

	public VoxelWorld voxelWorld;
//    SimpleApplication main;

	Texture textureAtlas = new Texture("texture_atlas.png");
	public static Material material;

	public TextureManagerAtlas(VoxelWorld world) {
		voxelWorld = world;
		
		material = new Material(TextureAttribute.createDiffuse(textureAtlas));

//		addTextureRegion(OFF_DIRT, new Texture("dirt.png"));
//		addTextureRegion(OFF_GRASS_SIDE, new Texture("grass_side.png"));
//		addTextureRegion(OFF_GRASS_TOP, new Texture("grass_top.png"));
//		addTextureRegion(OFF_LEAVES, new Texture("leaves.jpg"));
//		addTextureRegion(OFF_WOOD_SIDE, new Texture("wood_side.png"));
//		addTextureRegion(OFF_WOOD_TOP_BOTTOM, new Texture("wood_bottom_top.png"));
//		addTextureRegion(OFF_STONE, new Texture("stone.jpg"));

		addTextureRegion(OFF_DIRT);
		addTextureRegion(OFF_GRASS_SIDE);
		addTextureRegion(OFF_GRASS_TOP);
		addTextureRegion(OFF_LEAVES);
		addTextureRegion(OFF_WOOD_SIDE);
		addTextureRegion(OFF_WOOD_TOP_BOTTOM);
		addTextureRegion(OFF_STONE);

		setIdTexture(CellId.ID_GRASS, OFF_GRASS_SIDE[0], OFF_GRASS_SIDE[0], OFF_GRASS_SIDE[0], OFF_GRASS_SIDE[0], OFF_DIRT[0],
				OFF_GRASS_TOP[0]);
		setIdTexture(CellId.ID_DIRT, OFF_DIRT[0], OFF_DIRT[0], OFF_DIRT[0], OFF_DIRT[0], OFF_DIRT[0], OFF_DIRT[0]);
		setIdTexture(CellId.ID_WOOD, OFF_WOOD_SIDE[0], OFF_WOOD_SIDE[0], OFF_WOOD_SIDE[0], OFF_WOOD_SIDE[0], OFF_WOOD_TOP_BOTTOM[0],
				OFF_WOOD_TOP_BOTTOM[0]);
		setIdTexture(CellId.ID_LEAVES, OFF_LEAVES[0], OFF_LEAVES[0], OFF_LEAVES[0], OFF_LEAVES[0], OFF_LEAVES[0], OFF_LEAVES[0]);
		setIdTexture(CellId.ID_STONE, OFF_STONE[0], OFF_STONE[0], OFF_STONE[0], OFF_STONE[0], OFF_STONE[0], OFF_STONE[0]);
	}

	public void setIdTexture(int id, int offWest, int offEast, int offNorth, int offSouth, int offBottom, int offTop) {
		int[] offsets = { offWest, offEast, offNorth, offSouth, offBottom, offTop };
		textures.add(id, offsets);
	}

	public void addTextureRegion(int array[]) {
//		images.add(array[0], new TextureRegion(img, array[1]*TEXTURE_WIDTH, array[2]*TEXTURE_HEIGHT, (array[1]+1)*TEXTURE_WIDTH, (array[2]+1)*TEXTURE_HEIGHT));
		images.add(array[0], new float[] { (((float)array[1])*TEXTURE_WIDTH) / ATLAS_WIDTH, (((float)array[2])*TEXTURE_HEIGHT) / ATLAS_HEIGHT, (((float)array[1]+1)*TEXTURE_WIDTH) / ATLAS_WIDTH, (((float)array[2]+1)*TEXTURE_HEIGHT) / ATLAS_HEIGHT } );
		System.out.println(Arrays.toString(images.get(array[0])));
	}
	
	public static float[] getTexture(int id, int index) {
		return images.get(textures.get(id)[index]);
	}
	
}
