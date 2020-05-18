package com.emamaker.voxelengine.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

/* 
 * Credits:
 * https://xoppa.github.io/blog/using-the-libgdx-3d-physics-bullet-wrapper-part2/
 */

public class MyMotionState extends btMotionState {
	public Matrix4 transform;

	@Override
	public void getWorldTransform(Matrix4 worldTrans) {
		worldTrans.set(transform);
	}

	@Override
	public void setWorldTransform(Matrix4 worldTrans) {
		transform.set(worldTrans);
	}
}