/**
 * 
 */
package org.mmarini.genesis.j3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.ExponentialFog;
import javax.media.j3d.Fog;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import org.mmarini.swing.SwingTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * @author us00852
 * 
 */
public class Main {
	private static final float FOG_DENSITY = 4 / 150f;
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private static final Color3f WHITE = new Color3f(Color.WHITE);
	private static final Color3f BACKGROUND_COLOR = new Color3f(new Color(
			0xb5c5e7));
	// private static final Color3f AMBIENT_COLOR_LIGHT = new Color3f(0.1f,
	// 0.1f,
	// 0.1f);
	private static final Color3f AMBIENT_COLOR_LIGHT = BACKGROUND_COLOR;
	private static final Color3f LIGHT1_COLOR = WHITE;

	/**
	 * 
	 */
	public static void main(final String[] args) throws Throwable {
		logger.info("Starting ...");
		new Main().run();
	}

	private final JFrame frame;
	private final BranchGroup scene;
	private Avatar avatar;
	private final WorldModel worldModel;

	/**
	 * 
	 */
	public Main() {
		frame = new JFrame();
		scene = new BranchGroup();
		worldModel = WorldModel.create();
		frame.setTitle("Genesis");
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingTools.centerOnScreen(frame);
		final Container c = frame.getContentPane();
		c.setLayout(new BorderLayout());

		c.add(createCanvas(), BorderLayout.CENTER);
	}

	/**
	 * 
	 * @param b
	 * @return
	 */
	private Background createBackground(final BoundingSphere b) {
		final Background bg = new Background(BACKGROUND_COLOR);
		bg.setApplicationBounds(b);
		return bg;
	}

	/**
	 * @return
	 */
	private Canvas3D createCanvas() {
		logger.info("Creating canvas ...");

		// Create and add branches to the universe
		final Canvas3D c = new Canvas3D(
				SimpleUniverse.getPreferredConfiguration());
		final SimpleUniverse u = new SimpleUniverse(c);

		final TransformGroup t = u.getViewingPlatform()
				.getViewPlatformTransform();

		avatar = Avatar.newInstance(worldModel, t);

		createScene();
		final BranchGroup bg = new BranchGroup();
		bg.addChild(createFog());
		bg.addChild(new AvatarBehaviour(avatar, worldModel));
		bg.addChild(avatar);
		t.addChild(bg);

		scene.compile();

		u.addBranchGraph(scene);

		logger.info("Canvas created.");
		return c;
	}

	/**
	 * 
	 * @return
	 */
	private Fog createFog() {
		final Fog fog = new ExponentialFog(BACKGROUND_COLOR, FOG_DENSITY);
		fog.setInfluencingBounds(new BoundingSphere(new Point3d(), 100f));
		return fog;
	}

	/**
	 * 
	 */
	private void createScene() {
		logger.info("Creating scene ...");

		// Create a bounds for the background and lights
		final BoundingSphere b = new BoundingSphere(new Point3d(), 100.0);

		// Set up the global lights
		final AmbientLight al = new AmbientLight(AMBIENT_COLOR_LIGHT);
		al.setInfluencingBounds(b);

		final DirectionalLight l1 = new DirectionalLight(LIGHT1_COLOR,
				new Vector3f(-1.0f, -1.0f, -1.0f));
		l1.setInfluencingBounds(b);

		// Create the root of the branch graph
		scene.addChild(createBackground(b));
		scene.addChild(al);
		scene.addChild(l1);
		scene.addChild(worldModel.getNode());
		logger.info("Scene created.");
	}

	private void run() {
		frame.setVisible(true);
	}
}
