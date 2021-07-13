package org.mmarini.genesis.j3d;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

/**
 * 
 * @author us00852
 * 
 */
public class Block implements Constants {
	private final BlockId id;
	private final BranchGroup node;

	/**
	 * 
	 * @param i
	 * @param j
	 * @param k
	 * @param node
	 */
	protected Block(final BlockId id, final Node node) {
		this.id = id;
		this.node = new BranchGroup();
		final TransformGroup tg = new TransformGroup();
		tg.setTransform(new TransformUtils().translate(
				new Vector3d(id.getLocation())).create());
		tg.addChild(node);
		this.node.addChild(tg);
		this.node.setCapability(BranchGroup.ALLOW_DETACH);
		this.node.compile();
	}

	/**
	 * 
	 * @param avatar
	 * @return
	 */
	public CollisionVolume computeCollisionVolume(final Avatar avatar) {
		return id.getBounds().computeCollision(avatar.getBoxBounds());
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
		final Block other = (Block) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * @return the id
	 */
	public BlockId getId() {
		return id;
	}

	/**
	 * @return the node
	 */
	public BranchGroup getNode() {
		return node;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Block [id=").append(id).append("]");
		return builder.toString();
	}
}
