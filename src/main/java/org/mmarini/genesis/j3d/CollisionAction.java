/**
 * 
 */
package org.mmarini.genesis.j3d;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @author us00852
 * 
 */
public abstract class CollisionAction {
	private final float value;

	/**
	 * 
	 * @param value
	 */
	public CollisionAction(final float value) {
		this.value = value;
	}

	/**
	 * 
	 * @param point
	 * @param speed
	 */
	public abstract void apply(Point3f point, Vector3f speed);

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CollisionAction other = (CollisionAction) obj;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}

	/**
	 * @return the value
	 */
	protected float getValue() {
		return value;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(value);
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CollisionAction [value=").append(value).append("]");
		return builder.toString();
	}
}
