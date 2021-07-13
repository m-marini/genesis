package org.mmarini.genesis.j3d;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.junit.Test;

public class CollisionActionTest {
	class A1 extends CollisionAction {

		public A1(final float value) {
			super(value);
		}

		@Override
		public void apply(final Point3f point, final Vector3f speed) {
		}

	};

	class A2 extends CollisionAction {

		public A2(final float value) {
			super(value);
		}

		@Override
		public void apply(final Point3f point, final Vector3f speed) {
		}

	};

	@Test
	public void testEquals() {
		final A1 a = new A1(1);
		final A1 b = new A1(1);
		final A1 c = new A1(2);
		final A2 d = new A2(1);
		final A2 e = new A2(1);
		assertThat(a.equals(b), equalTo(true));
		assertThat(b.equals(a), equalTo(true));
		assertThat(a.equals(c), equalTo(false));
		assertThat(c.equals(a), equalTo(false));
		assertThat(a.equals(d), equalTo(false));
		assertThat(d.equals(a), equalTo(false));
		assertThat(d.equals(e), equalTo(true));
		assertThat(e.equals(d), equalTo(true));
	}

}
