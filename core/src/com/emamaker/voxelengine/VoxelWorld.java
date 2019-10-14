package com.emamaker.voxelengine;

import com.badlogic.gdx.ApplicationAdapter; 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.emamaker.voxelengine.block.TextureManager;
import com.emamaker.voxelengine.utils.VoxelSettings;
import com.emamaker.voxelengine.world.WorldManager;

public class VoxelWorld {

	public WorldManager worldManager;
	public VoxelSettings globals;
	public TextureManager textManager;
	public ApplicationAdapter mainApp;

	public Environment environment;
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public ModelBatch modelBatch;
	public Model model;
	public ModelInstance instance;

	public void init(ApplicationAdapter adapter) {
		mainApp = adapter;

		globals = new VoxelSettings(this);
		textManager = new TextureManager(this);

		customSettings();

		worldManager = new WorldManager(this);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
//		environment.add(new DirectionalLight.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		modelBatch = new ModelBatch();

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

//		instance = new ModelInstance(makeModel());

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		launchUpdateThread();
	}

	void customSettings() {
		VoxelSettings.setTesting(false);
		VoxelSettings.setWorldGenerator("generatorTerrain");
		VoxelSettings.setWorldHeight(0);
		VoxelSettings.setBlockSize(1);
	}

	void launchUpdateThread() {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (true)
//					update();
//			}
//		}).start();
	}

	public void update() {
		worldManager.updateChunks();
	}

	void render() {
		camController.update();

		update();
		modelBatch.begin(cam);
		worldManager.renderChunks(modelBatch, environment);
//		modelBatch.render(instance, environment);
		modelBatch.end();
	}

	public void dispose() {
		modelBatch.dispose();
	}

}
