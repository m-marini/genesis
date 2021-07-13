/**
 * 
 */
package org.mmarini.genesis.ogl;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * @author us00852
 * 
 */
public class CopyOfMain {

	/**
	 * 
	 */
	public static void main(final String[] args) throws Throwable {
		new CopyOfMain().run();
	}

	/**
	 * 
	 */
	public CopyOfMain() {
	}

	private void run() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.create();
		} catch (final LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 800, 0, 600, 1, -1);
		glMatrixMode(GL_MODELVIEW);

		// init OpenGL here

		while (!Display.isCloseRequested()) {
			// Clear the screen and depth buffer
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			// set the color of the quad (R,G,B,A)
			glColor3f(0.5f, 0.5f, 1.0f);

			// draw quad
			glBegin(GL_QUADS);
			glVertex2f(100, 100);
			glVertex2f(100 + 200, 100);
			glVertex2f(100 + 200, 100 + 200);
			glVertex2f(100, 100 + 200);
			glEnd();

			// render OpenGL here

			Display.update();
		}

		Display.destroy();
	}

}
