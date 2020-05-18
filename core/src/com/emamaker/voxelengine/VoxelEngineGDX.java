package com.emamaker.voxelengine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector3;
import com.emamaker.voxelengine.utils.VoxelSettings;

public class VoxelEngineGDX extends Game {

	VoxelWorld world = new VoxelWorld();
	FPSLogger fps = new FPSLogger();

    public float speedH = .9f;
    public float speedV = .9f;

    private float yaw = 0.0f;
    private float pitch = 0.0f;


	@Override
	public void create() {
		//Set windowed resolution
		Gdx.graphics.setWindowedMode(1280, 720);
		
		VoxelSettings.usePlayer(true);
		world.init(this);
		//Disable camera controller
//		Gdx.input.setInputProcessor(null);
		
	}

	@Override
	public void render() {
		fps.log();
		world.render();

        yaw -= speedH * Gdx.input.getDeltaY();
        pitch -= speedV * Gdx.input.getDeltaX();

//		if(VoxelSettings.isUsingPlayer()) world.setCamera(new Vector3(world.p1.getPos().x, world.p1.getPos().y + 1, world.p1.getPos().z), new Vector3(pitch, 0f, yaw));
        if(VoxelSettings.isUsingPlayer()) world.camController.target.set(world.p1.getPos());
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
