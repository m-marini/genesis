/**
 * 
 */
package org.mmarini.genesis.j3d;

import java.awt.Color;
import java.awt.Font;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.j3d.Appearance;
import javax.media.j3d.Group;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.mmarini.fp.FPArrayList;
import org.mmarini.fp.FPList;
import org.mmarini.fp.Functor1;
import org.mmarini.fp.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.j3d.utils.geometry.Text2D;

/**
 * @author us00852
 * 
 */
public class Avatar extends Group implements Constants {

	public enum Action {
		X, Y, Z
	}

	class CollisionAction {
		private final Action action;
		private final float value;

		/**
		 * @param action
		 * @param value
		 */
		public CollisionAction(final Action action, final float value) {
			super();
			this.action = action;
			this.value = value;
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
			final CollisionAction other = (CollisionAction) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (action != other.action)
				return false;
			if (Float.floatToIntBits(value) != Float
					.floatToIntBits(other.value))
				return false;
			return true;
		}

		private Avatar getOuterType() {
			return Avatar.this;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((action == null) ? 0 : action.hashCode());
			result = prime * result + Float.floatToIntBits(value);
			return result;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("CollisionAction [action=").append(action)
					.append(", value=").append(value).append("]");
			return builder.toString();
		}
	}

	private static final float DEFAULT_HEIGHT = 1.7f;
	private static final float DEFAULT_WIDTH = 0.6f;
	private static Vector3f SIZE = new Vector3f(DEFAULT_WIDTH, DEFAULT_HEIGHT,
			DEFAULT_WIDTH);
	private static final float ARM_HEIGHT = 1.5f;
	private static final float ARM_LENGTH = (float) ((BLOCK_WIDTH + DEFAULT_WIDTH) * Math
			.sqrt(2));
	private static final int MAX_TICKS_INTERVAL = 100;
	private static final float EPSILON = 1e-3f;
	private static final float MAX_COMPASS_ACCELERATION = 2;
	private static final float MAX_COMPASS_SPEED = (float) (Math.PI / 2);
	private static float JUMP_SPEED = (float) Math.sqrt(2 * Constants.GRAVITY
			* (BLOCK_HEIGHT + 100e-3));
	private static final String HUD_PATTERN = "x=%+07.3f, y=%+07.3f, z=%+07.3f fps=%3.0f";
	private final static float MAX_PITCH_ACCELERATION = 10;
	private static final float MAX_PITCH_SPEED = (float) (Math.PI / 2);
	private static final Logger logger = LoggerFactory.getLogger(Avatar.class);

	/**
	 * 
	 * @param a
	 * @param b
	 * @param e
	 * @return
	 */
	private static boolean le(final float a, final float b, final float e) {
		return a - b < e;
	}

	/**
	 * 
	 * @param world
	 * @param transformGroup
	 * @return
	 */
	public static Avatar newInstance(final WorldModel world,
			final TransformGroup transformGroup) {
		final Avatar a = new Avatar(world, transformGroup, new Point3f(), 0.0f,
				0.0f);
		// final Avatar a = new Avatar(world, transformGroup, new Point3d(0,
		// -0.6,
		// 6.6), 0.0, 0.0);
		return a;
	}

	/**
	 * 
	 * @param x
	 * @param min
	 * @param max
	 * @return
	 */
	private static float range(final float x, final float min, final float max) {
		return Math.max(min, Math.min(x, max));
	}

	private final TransformGroup transformGroup;
	private final Point3f location;
	private final Vector3f speed;
	private final Vector3f ds;
	private final float leg;
	private float pitch;
	private float compass;
	private float eyes;
	private long time;
	private int stepCount;
	private float dt;
	private long strideStart;
	private float strideLength;
	private float strideTime;
	private float compassSpeed;
	private float pitchSpeed;
	private final Text2D hud;
	private int jumpCount;
	private float tickCrono;
	private int tickCount;
	private float fps;
	private float strideEnd;
	private float compassForce;
	private float pitchForce;
	private final Vector3f size;
	private boolean standing;
	private final WorldModel world;
	private Block cursorBlock;

	/**
	 * 
	 * @param world
	 * @param transformGroup
	 * @param point3f
	 * @param compass
	 * @param pitch
	 */
	private Avatar(final WorldModel world, final TransformGroup transformGroup,
			final Point3f point3f, final float compass, final float pitch) {
		this.world = world;
		this.transformGroup = transformGroup;
		this.location = new Point3f(point3f);
		this.speed = new Vector3f();
		this.ds = new Vector3f();
		this.compass = compass;
		this.pitch = pitch;
		this.eyes = 0.0f;
		this.leg = 0.92f;
		size = SIZE;

		hud = new Text2D(getHudText(), new Color3f(Color.BLACK),
				Font.MONOSPACED, 8, Font.PLAIN);
		hud.getAppearance().setCapability(Appearance.ALLOW_TEXTURE_WRITE);

		final TransformGroup t = new TransformGroup();
		final float s = 0.5f;
		t.setTransform(new TransformUtils().scale(new Vector3f(s, s, s))
				.translate(new Vector3f(-0.4f, 0.15f, -1)).create());
		t.addChild(hud);
		addChild(t);
		apply();
	}

	/**
	 * 
	 * @return
	 */
	private void apply() {
		transformGroup.setTransform(new TransformUtils().rotateX(pitch)
				.rotateY(compass)
				.translate(new Vector3f(0f, size.y + eyes, 0f))
				.translate(location).create());
		hud.setString(getHudText());
	}

	/**
	 * 
	 */
	private void doStep() {
		if (strideStart == 0) {
			strideStart = time;
			final float k = Math.min((float) (stepCount - 1) / 6, 1);
			strideLength = k * (1.2f - 0.6f) + 0.6f;
			strideTime = (1 - k) * (0.5f - 0.2f) + 0.2f;
			strideEnd = Math.round(strideStart + strideTime / SECS_PER_TICK);
		}

		speed.x = (float) (-strideLength / strideTime * Math.sin(compass) * (stepCount > 0 ? 1
				: -1));
		speed.z = (float) (-strideLength / strideTime * Math.cos(compass) * (stepCount > 0 ? 1
				: -1));
		if (time > strideEnd) {
			speed.x = 0;
			speed.z = 0;
			strideStart = 0;
			eyes = 0;
			if (stepCount > 0)
				--stepCount;
			else if (stepCount < 0)
				++stepCount;
			logger.debug("Remaining {} steps at {}.", stepCount, this);
		} else if (dt > 0) {
			final float alpha = (time - strideStart) * SECS_PER_TICK
					/ strideTime;
			final float s = (1 - Math.abs(1 - alpha * 2)) * strideLength / 2;
			eyes = (float) (-leg + Math.sqrt(leg * leg - s * s));
		}
	}

	/**
	 * 
	 */
	public void doTimeTick() {
		final long t = System.currentTimeMillis();
		final long dtick = (time != 0) ? (t - time) : 0;
		time = t;

		if (dtick > 0 && dtick < MAX_TICKS_INTERVAL) {
			dt = dtick * SECS_PER_TICK;
			final Vector3f a = new Vector3f(0, -GRAVITY * dt, 0);
			if (stepCount != 0 && standing)
				doStep();
			if (jumpCount > 0 && standing) {
				--jumpCount;
				speed.y = JUMP_SPEED;
			}
			++tickCount;
			tickCrono += dt;
			if (tickCrono > 1) {
				fps = tickCount / tickCrono;
				tickCount = 0;
				tickCrono = 0;
			}
			turnCompass();
			turnPitch();
			speed.add(a);
			ds.scale(dt, speed);
			location.add(ds);
			apply();
			standing = false;
		}
	}

	/**
	 * 
	 */
	public void drop() {
		if (cursorBlock != null) {
			world.removeBlock(cursorBlock);
			world.addBlock(BlockFactory.getInstance().createWallBlock(
					cursorBlock.getId()));
			cursorBlock = null;
		}

	}

	/**
	 * 
	 * @return
	 */
	public BoxBound getBoxBounds() {
		return new BoxBound(new Point3f(location.x - size.x / 2, location.y,
				location.z - size.z / 2), size);
	}

	/**
	 * 
	 * @return
	 */
	private String getHudText() {
		return String.format(HUD_PATTERN, location.x, location.y, location.z,
				fps);
	}

	/**
	 * 
	 * @return
	 */
	public Point3f getLocation() {
		return location;
	}

	/**
	 * 
	 */
	public void jump() {
		++jumpCount;
	}

	/**
	 * 
	 */
	private void locateCursor() {
		final float x = (float) (location.x - Math.sin(compass) * ARM_LENGTH);
		final float y = location.y + ARM_HEIGHT;
		final float z = (float) (location.z - Math.cos(compass) * ARM_LENGTH);
		final int i = Math.round(x / BLOCK_SIZE.x);
		final int j = Math.round(y / BLOCK_SIZE.y);
		final int k = Math.round(z / BLOCK_SIZE.z);
		final Block c = BlockFactory.getInstance().createCursorBlock(i, j, k);

		if (cursorBlock == null || !cursorBlock.equals(c)) {
			if (cursorBlock != null)
				world.removeBlock(cursorBlock);
			if (world.getBlock(c.getId()) == null) {
				cursorBlock = c;
				world.addBlock(cursorBlock);
			} else {
				cursorBlock = null;
			}
		}
	}

	/**
	 * @return
	 * 
	 */
	public Avatar moveBackward() {
		logger.debug("move forward");
		--stepCount;
		return this;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public Avatar moveForward() {
		logger.debug("move forward");
		++stepCount;
		return this;
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	private float normalizeAngle(float a) {
		while (a >= Math.PI)
			a -= 2 * Math.PI;
		while (a < -Math.PI)
			a += 2 * Math.PI;
		return a;
	}

	/**
	 * 
	 * @param object
	 */
	public void onCollision(final FPList<BlockCollision> fpList) {
		if (!fpList.isEmpty()) {
			// Compute collision actions
			final FPList<Tuple2<CollisionAction, BlockCollision>> a = new FPArrayList<>();
			for (final BlockCollision t : fpList) {
				final CollisionVolume v = t.getValue();
				final Vector3f dv = v.getSize();
				final BoxBound b = v.getBounds();
				final Point3f min = b.getMin();
				final Point3f max = b.getMax();
				for (final CollisionSide s : v.getSides()) {
					switch (s) {
					case LEFT:
						if (ds.x > 0 && le(dv.x, ds.x, EPSILON))
							a.add(new Tuple2<Avatar.CollisionAction, BlockCollision>(
									new CollisionAction(Action.X, min.x
											- size.x / 2), t));
					case RIGHT:
						if (ds.x < 0 && le(dv.x, -ds.x, EPSILON))
							a.add(new Tuple2<Avatar.CollisionAction, BlockCollision>(
									new CollisionAction(Action.Y, max.x
											+ size.x / 2), t));
						break;
					case TOP:
						if (ds.y < 0 && le(dv.y, -ds.y, EPSILON))
							a.add(new Tuple2<Avatar.CollisionAction, BlockCollision>(
									new CollisionAction(Action.Y, max.y), t));
						break;
					case BOTTOM:
						if (ds.y > 0 && le(dv.y, ds.y, EPSILON))
							a.add(new Tuple2<Avatar.CollisionAction, BlockCollision>(
									new CollisionAction(Action.Y, min.y
											- size.y), t));
						break;
					case FRONT:
						if (ds.z < 0 && le(dv.z, -ds.z, EPSILON))
							a.add(new Tuple2<Avatar.CollisionAction, BlockCollision>(
									new CollisionAction(Action.Z, max.z
											+ size.z / 2), t));
						break;
					case BACK:
						if (ds.z > 0 && le(dv.z, ds.z, EPSILON))
							a.add(new Tuple2<Avatar.CollisionAction, BlockCollision>(
									new CollisionAction(Action.Z, min.z
											- size.z / 2), t));
						break;
					default:
						break;
					}
				}
			}
			// Group by action
			final FPList<Tuple2<CollisionAction, FPList<BlockCollision>>> m = a
					.groupBy(
							new Functor1<CollisionAction, Tuple2<CollisionAction, BlockCollision>>() {
								@Override
								public CollisionAction apply(
										final Tuple2<CollisionAction, BlockCollision> p) {
									return p.getKey();
								}
							})
					.map(new Functor1<Tuple2<CollisionAction, FPList<BlockCollision>>, Map.Entry<CollisionAction, FPList<Tuple2<CollisionAction, BlockCollision>>>>() {
						@Override
						public Tuple2<CollisionAction, FPList<BlockCollision>> apply(
								final Entry<CollisionAction, FPList<Tuple2<CollisionAction, BlockCollision>>> e) {
							return new Tuple2<CollisionAction, FPList<BlockCollision>>(
									e.getKey(),
									e.getValue()
											.map(new Functor1<BlockCollision, Tuple2<CollisionAction, BlockCollision>>() {
												@Override
												public BlockCollision apply(
														final Tuple2<CollisionAction, BlockCollision> a) {
													return a.getValue();
												}
											}));
						}
					});

			if (m.isEmpty() || location.y < -0.8) {
				logger.debug("!!! No actions !!!");
				logger.debug("  location: {} ", location);
				logger.debug("  speed: {} ", speed);
				logger.debug("  ds {} ", ds);
				logger.debug("  dt {} ", dt);
				final Point3f pl = new Point3f(location);
				pl.sub(ds);
				logger.debug("  prev location {}", pl);
				logger.debug("  collisions {} ", fpList);
				System.exit(-1);
			}

			// Sort by resolution rank
			Collections
					.sort(m,
							new Comparator<Tuple2<CollisionAction, FPList<BlockCollision>>>() {

								@Override
								public int compare(
										final Tuple2<CollisionAction, FPList<BlockCollision>> o1,
										final Tuple2<CollisionAction, FPList<BlockCollision>> o2) {
									return o2.getValue().size()
											- o1.getValue().size();
								};
							});

			// Choose the actions
			final FPList<BlockCollision> t = new FPArrayList<>(fpList);
			final FPList<Tuple2<CollisionAction, FPList<BlockCollision>>> ac = m
					.filter(new Functor1<Boolean, Tuple2<CollisionAction, FPList<BlockCollision>>>() {
						@Override
						public Boolean apply(
								final Tuple2<CollisionAction, FPList<BlockCollision>> arg0) {
							if (t.isEmpty())
								return false;
							t.removeAll(arg0.getValue());
							return true;
						}
					});

			logger.debug("onCollision actions {}", ac);
			logger.debug(" before at {}, speed {}", location, speed);
			for (final Tuple2<CollisionAction, FPList<BlockCollision>> tt : ac) {
				final CollisionAction act = tt.getKey();
				switch (act.action) {
				case X:
					standing = false;
					location.x = act.value;
					speed.x = 0;
					break;
				case Y:
					standing = true;
					location.y = act.value;
					speed.set(0, 0, 0);
					break;
				case Z:
					standing = false;
					location.z = act.value;
					speed.z = 0;
					break;
				}
			}
			logger.debug(" after at {}, speed {}", location, speed);

			apply();
		} else {
			logger.debug("Avatar dt {}, at {}, ds {}, speed {}", dt, location,
					ds, speed);

		}
		locateCursor();
	}

	/**
	 * @param compassForce
	 *            the compassForce to set
	 */
	public void setCompassForce(final float compassForce) {
		this.compassForce = compassForce;
	}

	/**
	 * @param pitchForce
	 *            the pitchForce to set
	 */
	public void setPitchForce(final float pitchForce) {
		this.pitchForce = pitchForce;
	}

	/**
	 * 
	 */
	public void stop() {
		stepCount = Math.min(stepCount, 1);
		jumpCount = Math.min(jumpCount, 1);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Avatar [location=").append(location)
				.append(", compass=").append(compass).append("]");
		return builder.toString();
	}

	/**
	 * 
	 */
	private void turnCompass() {
		final float v = -compassForce * MAX_COMPASS_SPEED;
		final float dv = v - compassSpeed;
		if (Math.abs(dv / dt) < MAX_COMPASS_ACCELERATION) {
			compassSpeed = v;
		} else {
			final float a = dv < 0 ? -MAX_COMPASS_ACCELERATION
					: MAX_COMPASS_ACCELERATION;
			compassSpeed = range(compassSpeed + a * dt, -MAX_COMPASS_SPEED,
					MAX_COMPASS_SPEED);
		}
		compass = normalizeAngle(compass + compassSpeed * dt);
	}

	/**
	 * 
	 */
	private void turnPitch() {
		final float th = (float) (pitchForce * Math.PI / 2);
		final float s = th - pitch;
		if (s == 0 && pitchSpeed == 0)
			return;
		final float af = 3 * pitchSpeed * pitchSpeed / 2
				/ MAX_PITCH_ACCELERATION;
		final float a;
		if (s < 0) {
			if (pitchSpeed <= 0 && (-s < af))
				a = MAX_PITCH_ACCELERATION;
			else
				a = -MAX_PITCH_ACCELERATION;
		} else if (pitchSpeed >= 0 && s < af)
			a = -MAX_PITCH_ACCELERATION;
		else
			a = MAX_PITCH_ACCELERATION;
		pitchSpeed = range(pitchSpeed + a * dt, -MAX_PITCH_SPEED,
				MAX_PITCH_SPEED);
		final float ds = pitchSpeed * dt;
		if (s < 0 && ds < s || s > 0 && ds > s) {
			pitch = th;
			pitchSpeed = 0;
		} else
			pitch = range(pitch + ds, (float) -Math.PI / 2, (float) Math.PI / 2);
	}
}
