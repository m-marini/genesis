/**
 * 
 */
package org.mmarini.genesis.j3d;

import javax.vecmath.Point3f;

/**
 * @author us00852
 * 
 */
public class BlockId implements Constants {
	private final int i, j, k;
	private final Point3f location;
	private final BoxBound bounds;

	/**
	 * 
	 * @param i
	 * @param j
	 * @param k
	 */
	public BlockId(final int i, final int j, final int k) {
		super();
		this.i = i;
		this.j = j;
		this.k = k;
		location = new Point3f(i * BLOCK_WIDTH, j * BLOCK_HEIGHT, k
				* BLOCK_WIDTH);
		bounds = new BoxBound(new Point3f(i * BLOCK_WIDTH - BLOCK_WIDTH / 2,
				(j - 1) * BLOCK_HEIGHT, k * BLOCK_WIDTH - BLOCK_WIDTH / 2),
				BLOCK_SIZE);
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
		final BlockId other = (BlockId) obj;
		if (i != other.i)
			return false;
		if (j != other.j)
			return false;
		if (k != other.k)
			return false;
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public BoxBound getBounds() {
		return bounds;
	}

	/**
	 * @return the i
	 */
	public int getI() {
		return i;
	}

	/**
	 * @return the j
	 */
	public int getJ() {
		return j;
	}

	/**
	 * @return the k
	 */
	public int getK() {
		return k;
	}

	/**
	 * 
	 * @return
	 */
	public Point3f getLocation() {
		return location;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + i;
		result = prime * result + j;
		result = prime * result + k;
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("(").append(i).append(", ").append(j).append(", ")
				.append(k).append(")");
		return builder.toString();
	}
}
