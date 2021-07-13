package org.mmarini.genesis.j3d;

import java.io.FileNotFoundException;

import javax.media.j3d.Link;
import javax.media.j3d.Node;
import javax.media.j3d.SharedGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;

/**
 * 
 * @author us00852
 * 
 */
public class BlockFactory implements Constants {
	private static final String GROUND_BLOCK_MODEL = "/mdl/groundBlock.obj";
	private static final String WALL_BLOCK_MODEL = "/mdl/wallBlock.obj";
	private static final String CURSOR_BLOCK_MODEL = "/mdl/cursorBlock.obj";
	private static final Logger logger = LoggerFactory
			.getLogger(BlockFactory.class);
	private static final BlockFactory instance = new BlockFactory();

	/**
	 * @return the instance
	 */
	public static BlockFactory getInstance() {
		return instance;
	}

	private SharedGroup groundBox;
	private SharedGroup wallBox;

	private SharedGroup cursorBox;

	/**
	 * 
	 * @param i
	 * @param j
	 * @param k
	 * @return
	 */
	public Block createCursorBlock(final int i, final int j, final int k) {
		return new Block(new BlockId(i, j, k), createCursorBox());
	}

	/**
	 * 
	 * @return
	 */
	private Node createCursorBox() {
		if (cursorBox == null) {
			try {
				final Scene s = new ObjectFile().load(getClass().getResource(
						CURSOR_BLOCK_MODEL));
				cursorBox = new SharedGroup();
				cursorBox.addChild(s.getSceneGroup());
			} catch (FileNotFoundException | IncorrectFormatException
					| ParsingErrorException e) {
				logger.error(e.getMessage(), e);
				throw new Error(e);
			}
		}
		return new Link(cursorBox);
	}

	/**
	 * 
	 * @param i
	 * @param j
	 * @param k
	 * @return
	 */
	public Block createGroundBlock(final int i, final int j, final int k) {
		return new Block(new BlockId(i, j, k), createGroundBox());
	}

	/**
	 * 
	 * @return
	 */
	private Node createGroundBox() {
		if (groundBox == null) {
			try {
				final Scene s = new ObjectFile().load(getClass().getResource(
						GROUND_BLOCK_MODEL));
				groundBox = new SharedGroup();
				groundBox.addChild(s.getSceneGroup());
			} catch (FileNotFoundException | IncorrectFormatException
					| ParsingErrorException e) {
				logger.error(e.getMessage(), e);
				throw new Error(e);
			}
		}
		return new Link(groundBox);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Block createWallBlock(final BlockId id) {
		return new Block(id, createWallBox());
	}

	/**
	 * 
	 * @param i
	 * @param j
	 * @param k
	 * @return
	 */
	public Block createWallBlock(final int i, final int j, final int k) {
		return createWallBlock(new BlockId(i, j, k));
	}

	/**
	 * 
	 * @return
	 */
	private Node createWallBox() {
		if (wallBox == null) {
			try {
				final Scene s = new ObjectFile().load(getClass().getResource(
						WALL_BLOCK_MODEL));
				wallBox = new SharedGroup();
				wallBox.addChild(s.getSceneGroup());
			} catch (FileNotFoundException | IncorrectFormatException
					| ParsingErrorException e) {
				logger.error(e.getMessage(), e);
				throw new Error(e);
			}
		}
		return new Link(wallBox);
	}
}
