package org.mmarini.genesis.j3d;

import javax.media.j3d.Group;
import javax.vecmath.Point3f;

import org.mmarini.fp.FPArrayList;
import org.mmarini.fp.FPHashMap;
import org.mmarini.fp.FPList;
import org.mmarini.fp.FPMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author us00852
 * 
 */
public class WorldModel implements Constants {
	private final static Logger logger = LoggerFactory
			.getLogger(WorldModel.class);
	private static final int BLOCK_COUNT = 50;

	/**
	 * 
	 * @return
	 */
	public static WorldModel create() {
		final WorldModel m = new WorldModel();
		final BlockFactory f = BlockFactory.getInstance();
		for (int i = -BLOCK_COUNT; i <= BLOCK_COUNT; ++i) {
			for (int k = -BLOCK_COUNT; k <= BLOCK_COUNT; ++k) {
				final int d2 = i * i + k * k;
				m.addBlock(f.createGroundBlock(i, d2 <= 100 ? 0 : d2 < 900 ? -1
						: 0, k));
			}
			for (int j = 1; j <= 5; ++j) {
				m.addBlock(f.createWallBlock(i, j, -BLOCK_COUNT));
				m.addBlock(f.createWallBlock(i, j, BLOCK_COUNT));
				m.addBlock(f.createWallBlock(-BLOCK_COUNT, j, i));
				m.addBlock(f.createWallBlock(BLOCK_COUNT, j, i));
			}
		}

		for (int p = 0; p < 16; ++p) {
			final double a = 2 * Math.PI * p / 16;
			final int i = (int) Math.round(20 * Math.sin(a));
			final int k = (int) Math.round(20 * Math.cos(a));
			m.addBlock(f.createGroundBlock(i, 0, k));
			m.addBlock(f.createGroundBlock(i, 1, k));
		}
		logger.info("Ground created.");
		return m;
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	private static BlockId getBlockId(final Point3f p) {
		return new BlockId((int) Math.floor((p.x + BLOCK_WIDTH / 2)
				/ BLOCK_WIDTH), -(int) Math.floor(-p.y / BLOCK_HEIGHT),
				(int) Math.floor((p.z + BLOCK_WIDTH / 2) / BLOCK_WIDTH));
	}

	private final FPMap<BlockId, Block> blocks;
	private final Group node;

	/**
	 * 
	 */
	private WorldModel() {
		blocks = new FPHashMap<>();
		node = new Group();
		node.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		node.setCapability(Group.ALLOW_CHILDREN_WRITE);
	}

	/**
	 * 
	 * @param b
	 */
	public void addBlock(final Block b) {
		blocks.put(b.getId(), b);
		node.addChild(b.getNode());
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	public FPList<BlockCollision> findCollision(final Avatar a) {
		final BoxBound ab = a.getBoxBounds();

		final BlockId id0 = getBlockId(ab.getMin());
		final BlockId id1 = getBlockId(ab.getMax());

		logger.debug("find collision");
		logger.debug("  location {}", a.getBoxBounds());
		logger.debug("  from {} to {}", id0, id1);
		final FPList<BlockCollision> l = new FPArrayList<>();
		for (int i = id0.getI(); i <= id1.getI(); ++i) {
			for (int j = id0.getJ(); j <= id1.getJ(); ++j) {
				for (int k = id0.getK(); k <= id1.getK(); ++k) {
					final Block b = blocks.get(new BlockId(i, j, k));
					if (b != null) {
						final CollisionVolume v = b.computeCollisionVolume(a);
						if (v != null)
							l.add(new BlockCollision(b, v));
						logger.debug("  block {} volume {}", b, v);
					}
				}
			}
		}
		return l;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Block getBlock(final BlockId id) {
		return blocks.get(id);
	}

	/**
	 * @return the node
	 */
	public Group getNode() {
		return node;
	}

	/**
	 * 
	 * @param block
	 */
	public void removeBlock(final Block block) {
		node.removeChild(block.getNode());
		blocks.remove(block);
	}
}
