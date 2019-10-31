package com.emamaker.voxelengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.FPSLogger;

public class VoxelEngineGDX extends ApplicationAdapter {

	VoxelWorld world = new VoxelWorld();
	FPSLogger fps = new FPSLogger();

	@Override
	public void create() {
		world.init(this);
	}

	@Override
	public void render() {
		
		world.render();
		fps.log();
	}

	@Override
	public void dispose() {
		world.dispose();
	}

	@Override
	public void resize(int width, int height) {
		world.resize(width, height);
	}

	@Override
	public void pause() {
		world.pause();
	}

	@Override
	public void resume() {
		world.resume();
	}
}
