/**
 * 
 */
package org.mmarini.genesis.j3d;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.mmarini.fp.FPArrayList;
import org.mmarini.fp.FPList;
import org.mmarini.genesis.j3d.Constants.CollisionSide;

/**
 * @author us00852
 * 
 */
public class BoxBound {

	private final Point3f min;
	private final Point3f max;
	private final Vector3f size;

	/**
	 * @param min
	 * @param max
	 */
	public BoxBound(final Point3f min, final Point3f max) {
		this.min = min;
		this.max = max;
		size = new Vector3f(max);
		size.sub(min);
	}

	/**
	 * @param min
	 * @param max
	 */
	public BoxBound(final Point3f min, final Vector3f size) {
		this.min = min;
		this.size = size;
		this.max = new Point3f(min);
		max.add(size);
	}

	/**
	 * 
	 * @param o
	 * @return
	 */
	public CollisionVolume computeCollision(final BoxBound o) {
		final Point3f p0 = getMin();
		final Point3f p1 = getMax();
		final Point3f o0 = o.getMin();
		final Point3f o1 = o.getMax();

		final float x0 = Math.max(p0.x, o0.x);
		final float x1 = Math.min(p1.x, o1.x);
		final float y0 = Math.max(p0.y, o0.y);
		final float y1 = Math.min(p1.y, o1.y);
		final float z0 = Math.max(p0.z, o0.z);
		final float z1 = Math.min(p1.z, o1.z);
		if (y0 >= y1 || x0 >= x1 || z0 >= z1)
			return null;
		final FPList<CollisionSide> s = new FPArrayList<>();
		if (x0 == p0.x)
			s.add(CollisionSide.LEFT);
		if (x1 == p1.x)
			s.add(CollisionSide.RIGHT);
		if (y0 == p0.y)
			s.add(CollisionSide.BOTTOM);
		if (y1 == p1.y)
			s.add(CollisionSide.TOP);
		if (z0 == p0.z)
			s.add(CollisionSide.BACK);
		if (z1 == p1.z)
			s.add(CollisionSide.FRONT);

		return new CollisionVolume(s, new BoxBound(new Point3f(x0, y0, z0),
				new Point3f(x1, y1, z1)));
	}

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
		final BoxBound other = (BoxBound) obj;
		if (max == null) {
			if (other.max != null)
				return false;
		} else if (!max.equals(other.max))
			return false;
		if (min == null) {
			if (other.min != null)
				return false;
		} else if (!min.equals(other.min))
			return false;
		return true;
	}

	/**
	 * @return the max
	 */
	public Point3f getMax() {
		return max;
	}

	/**
	 * @return the min
	 */
	public Point3f getMin() {
		return min;
	}

	/**
	 * 
	 * @return
	 */
	public Vector3f getSize() {
		return size;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((max == null) ? 0 : max.hashCode());
		result = prime * result + ((min == null) ? 0 : min.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BoxBound [min=").append(min).append(", max=")
				.append(max).append(", ds=").append(size).append("]");
		return builder.toString();
	}
}
