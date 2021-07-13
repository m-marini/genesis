package org.mmarini.genesis.j3d;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * 
 * @author us00852
 * 
 */
public class TransformUtils {

	private final Transform3D transform3d;

	/**
	 * 
	 * @param transform3d
	 */
	public TransformUtils() {
		this(new Transform3D());
	}

	/**
	 * 
	 * @param transform3d
	 */
	public TransformUtils(final Transform3D transform3d) {
		this.transform3d = transform3d;
	}

	/**
	 * 
	 * @return
	 */
	public Transform3D create() {
		return transform3d;
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	public TransformUtils rotateX(final float a) {
		final Transform3D t = new Transform3D();
		t.rotX(a);
		t.mul(transform3d);
		return new TransformUtils(t);
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	public TransformUtils rotateY(final float a) {
		final Transform3D t = new Transform3D();
		t.rotY(a);
		t.mul(transform3d);
		return new TransformUtils(t);
	}

	/**
	 * 
	 * @param vector3f
	 * @return
	 */
	public TransformUtils scale(final Vector3f vector3f) {
		final Transform3D t = new Transform3D();
		t.setScale(new Vector3d(vector3f));
		t.mul(transform3d);
		return new TransformUtils(t);
	}

	/**
	 * 
	 * @param location
	 * @return
	 */
	public TransformUtils translate(final Point3f location) {
		return translate(new Vector3f(location));
	}

	/**
	 * 
	 * @param v
	 * @return
	 */
	public TransformUtils translate(final Vector3d v) {
		final Transform3D t = new Transform3D();
		t.setTranslation(v);
		t.mul(transform3d);
		return new TransformUtils(t);
	}

	/**
	 * 
	 * @param v
	 * @return
	 */
	public TransformUtils translate(final Vector3f v) {
		final Transform3D t = new Transform3D();
		t.setTranslation(v);
		t.mul(transform3d);
		return new TransformUtils(t);
	}

}
