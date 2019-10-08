package com.emamaker.voxelengine;

import com.emamaker.voxelengine.utils.Globals;
import com.emamaker.voxelengine.world.WorldManager;

public class VoxelWorld {

	public WorldManager worldManager;
	public Globals globals;

	public void init() {
		globals = new Globals(this);
		worldManager = new WorldManager(this);

	}

	public void update() {
		worldManager.updateChunks();

	}

	void render() {
	}

	public void dispose() {
	}

}
