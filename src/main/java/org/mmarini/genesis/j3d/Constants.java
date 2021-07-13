package org.mmarini.genesis.j3d;

import javax.vecmath.Vector3f;

/**
 * 
 * @author us00852
 * 
 */
public interface Constants {
	public enum CollisionSide {
		NONE, BOTTOM, TOP, LEFT, RIGHT, BACK, FRONT
	}

	public static final float GRAVITY = 9.81f;
	public static final float SECS_PER_TICK = 1e-3f;
	public final static float BLOCK_WIDTH = 0.6f;
	public final static float BLOCK_HEIGHT = 0.6f;
	public static final Vector3f BLOCK_SIZE = new Vector3f(BLOCK_WIDTH,
			BLOCK_HEIGHT, BLOCK_WIDTH);
	public static final String DIRT_TEXTURE = "layers.png";
	public static final String GRASS_TEXTURE = "grass.png";;
	public static final String WALL_TEXTURE = "bricks.png";;
}
