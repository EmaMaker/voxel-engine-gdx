package com.emamaker.voxelengine;

import static com.emamaker.voxelengine.utils.VoxelSettings.chunkSize;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btAxisSweep3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.emamaker.voxelengine.block.TextureManagerAtlas;
import com.emamaker.voxelengine.player.Player;
import com.emamaker.voxelengine.shaders.TestShader;
import com.emamaker.voxelengine.utils.VoxelSettings;
import com.emamaker.voxelengine.world.WorldManager;

public class VoxelWorld {

	public WorldManager worldManager;
	public VoxelSettings globals;
	public TextureManagerAtlas textManager;
	public Game mainApp;

	public Environment environment;
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public ModelBatch modelBatch;

	// Physics
	btGhostPairCallback ghostPairCallback;
	public btDiscreteDynamicsWorld dynamicsWorld;
	public btCollisionConfiguration collisionConfig;
	public btDispatcher dispatcher;
	public btAxisSweep3 broadphase;
	public btSequentialImpulseConstraintSolver constraintSolver;
	public RenderContext renderContext;
	public TestShader shader;
	Player p1;

	public void init(Game adapter) {
		System.out.println("Starting setup of voxel engine");
		mainApp = adapter;

		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);

		initEnvironment();
		initPhysics();

		System.out.println("Creating voxel engine utils");
		globals = new VoxelSettings(this);
		customSettings();
		textManager = new TextureManagerAtlas(this);
		worldManager = new WorldManager(this);

		System.out.println("Spawing a test player");
		p1 = new Player(Keys.UP, Keys.DOWN, Keys.LEFT, Keys.RIGHT, 8, VoxelSettings.chunkSize*1.5f*(1+VoxelSettings.worldHeight), 8);

		System.out.println("Finished setup of voxel engine");

		renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
		shader = new TestShader();
		shader.init();

	}

	/*----		Init Bullet for Physics		----*/
	void initPhysics() {
		System.out.println("Initializing Bullet physics");
		Bullet.init();

		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btAxisSweep3(new Vector3(-1000, -1000, -1000), new Vector3(1000, 1000, 1000));
		constraintSolver = new btSequentialImpulseConstraintSolver();
		ghostPairCallback = new btGhostPairCallback();
		broadphase.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
		dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
	}

	void initEnvironment() {
		System.out.println("Initializing environment");
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		modelBatch = new ModelBatch();

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 0f);
		cam.lookAt(chunkSize, chunkSize, chunkSize);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);
	}

	void customSettings() {
		System.out.println("Applying settings");
		VoxelSettings.setTesting(false);
		VoxelSettings.setWorldGenerator("generatorTerrain");
		VoxelSettings.setWorldHeight(0);
		VoxelSettings.setBlockSize(1);
		VoxelSettings.setDebugEnabled(false);
		VoxelSettings.setRenderDistance(8);
		VoxelSettings.MAXX = 20;
		VoxelSettings.MAXY = 20;
		VoxelSettings.MAXZ = 20;
	}

	public void render() {

		final float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
		dynamicsWorld.stepSimulation(delta, 5, 1f / 60f);

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camController.update();
		cam.update();

		modelBatch.begin(cam);
		renderUpdate();

		if (VoxelSettings.isUsingPlayer()) {
			p1.render(modelBatch, environment);
			// Make camera follow player
			cam.lookAt(p1.instance.transform.getTranslation(new Vector3()));
		}

		worldManager.renderChunks(modelBatch, environment);
		modelBatch.end();

	}

	public void renderUpdate() {
		worldManager.updateChunks();
	}

	public void dispose() {
		p1.dispose();
		worldManager.dispose();

		dynamicsWorld.dispose();
		constraintSolver.dispose();
		broadphase.dispose();
		dispatcher.dispose();
		collisionConfig.dispose();

		modelBatch.dispose();
		ghostPairCallback.dispose();
	}

	public void resize(int width, int height) {

	}

	public void pause() {

	}

	public void resume() {

	}

	class MyContactListener extends ContactListener {
		@Override
		public boolean onContactAdded(btManifoldPoint cp, btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
				btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
			return true;
		}
	}
	
	public void resetCamera() {
		cam.position.set(0f, 0f, 0.5f); // Set cam position at origin
		cam.lookAt(0, 0, 0); // Direction to look at, for setting direction (perhaps better to set manually)
//		 cam.near = 1f;            // Minimum distance visible
//		 cam.far = 300f;            // Maximum distance visible
		cam.up.set(0f, 1f, 0f); // Up is in y positive direction
		cam.view.idt(); // Reset rotation matrix
		cam.update();
	}public void resetCameraPosition() {
		cam.position.set(0f, 0f, 0.5f); // Set cam position at origin
		cam.lookAt(0, 0, 0); // Direction to look at, for setting direction (perhaps better to set manually)
//		 cam.near = 1f;            // Minimum distance visible
//		 cam.far = 300f;            // Maximum distance visible
		cam.up.set(0f, 1f, 0f); // Up is in y positive direction
		cam.update();
	}

	public void setCamera(Vector3 position, Vector3 rotation) {
		resetCamera();
		cam.translate(position); // set cam absolute position
		cam.rotate(rotation.x, 0f, 1f, 0f); // set cam absolute rotation on axis X
		cam.rotate(rotation.y, 1f, 0f, 0f); // set cam absolute rotation on axis Y
		cam.rotate(rotation.z, 0f, 0f, 1f); // set cam absolute rotation on axis Z
		cam.update();
	}
	public void setCameraPosition(Vector3 position) {
		resetCamera();
		cam.translate(position); // set cam absolute position
		cam.update();
	}
	public void setCameraPosition(float x, float y, float z) {
		resetCamera();
		cam.translate(x,y,z); // set cam absolute position
		cam.update();
	}

}
