/**
 * 
 */
package org.mmarini.genesis.j3d;

import javax.vecmath.Vector3f;

import org.mmarini.fp.FPList;
import org.mmarini.genesis.j3d.Constants.CollisionSide;

/**
 * @author us00852
 * 
 */
public class CollisionVolume {
	private final FPList<CollisionSide> sides;
	private final BoxBound bounds;

	/**
	 * 
	 * @param sides
	 * @param bounds
	 */
	public CollisionVolume(final FPList<CollisionSide> sides,
			final BoxBound bounds) {
		super();
		this.sides = sides;
		this.bounds = bounds;
	}

	/**
	 * @return the bounds
	 */
	public BoxBound getBounds() {
		return bounds;
	}

	/**
	 * @return the side
	 */
	public FPList<CollisionSide> getSides() {
		return sides;
	}

	/**
	 * 
	 * @return
	 */
	public Vector3f getSize() {
		return bounds.getSize();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CollisionVolume [sides=").append(sides)
				.append(", bounds=").append(bounds).append("]");
		return builder.toString();
	}
}
