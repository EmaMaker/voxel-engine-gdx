package com.emamaker.voxelengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.emamaker.voxelengine.block.TextureManagerAtlas;
import com.emamaker.voxelengine.utils.VoxelSettings;
import com.emamaker.voxelengine.world.WorldManager;

public class VoxelWorld {

	public WorldManager worldManager;
	public VoxelSettings globals;
	public TextureManagerAtlas textManager;
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
		textManager = new TextureManagerAtlas(this);

		customSettings();

		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
		worldManager = new WorldManager(this);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		modelBatch = new ModelBatch();

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f, 0f, 0f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

//		instance = new ModelInstance(makeModel());

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

//		launchUpdateThread();
	}

	void customSettings() {
		VoxelSettings.setTesting(false);
		VoxelSettings.setWorldGenerator("generatorTerrain");
		VoxelSettings.setWorldHeight(0);
		VoxelSettings.setBlockSize(1);
		VoxelSettings.setDebugEnabled(false);
		VoxelSettings.setRenderDistance(6);
	}

	void launchUpdateThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true)
					update();
			}
		}).start();
	}

	public void update() {
		worldManager.updateChunks();
	}
	
	public void renderUpdate() {
		update();
	}
	
	void render() {
		camController.update();

		renderUpdate();
		modelBatch.begin(cam);
		worldManager.renderChunks(modelBatch, environment);
//		modelBatch.render(instance, environment);
		modelBatch.end();
	}

	public void dispose() {
		modelBatch.dispose();
	}

}
