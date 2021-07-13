/**
 * 
 */
package org.mmarini.genesis.j3d;

import org.mmarini.fp.Tuple2;

/**
 * @author us00852
 * 
 */
public class BlockCollision extends Tuple2<Block, CollisionVolume> {

	/**
	 * @param block
	 * @param volume
	 */
	public BlockCollision(final Block block, final CollisionVolume volume) {
		super(block, volume);
	}

}
