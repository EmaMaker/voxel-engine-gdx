package com.emamaker.voxelengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;

public class VoxelEngineGDX extends ApplicationAdapter {

	VoxelWorld world = new VoxelWorld();
	FPSLogger fps = new FPSLogger();

	@Override
	public void create() {
		world.init(this);
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		world.render();
		fps.log();
	}

	@Override
	public void dispose() {
		world.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
