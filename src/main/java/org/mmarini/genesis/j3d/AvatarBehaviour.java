package org.mmarini.genesis.j3d;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.media.j3d.WakeupOr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author us00852
 * 
 */
public class AvatarBehaviour extends Behavior {
	private final static Logger logger = LoggerFactory
			.getLogger(AvatarBehaviour.class);
	private final WakeupCondition wakeupCondition;
	private final Avatar avatar;
	private final WorldModel model;
	private float hPos;
	private float vPos;

	/**
	 * 
	 * @param avatar
	 * @param avatorBounds
	 */
	public AvatarBehaviour(final Avatar avatar, final WorldModel model) {
		this.avatar = avatar;
		this.model = model;
		wakeupCondition = new WakeupOr(new WakeupCriterion[] {
				new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED),
				new WakeupOnElapsedFrames(0) });
		setSchedulingBounds(new BoundingBox());
		logger.debug("Avatar behaviour created.");
	}

	/**
	 * @see javax.media.j3d.Behavior#initialize()
	 */
	@Override
	public void initialize() {
		wakeupOn(wakeupCondition);
	}

	/**
	 * 
	 * @param c
	 */
	private void onElapsedFrames(final WakeupOnElapsedFrames c) {
		avatar.doTimeTick();
	}

	/**
	 * 
	 * @param eventKey
	 */
	private void onKey(final KeyEvent eventKey) {
		switch (eventKey.getID()) {
		case KeyEvent.KEY_PRESSED:
			onKeyPressed(eventKey);
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @param eventKey
	 */
	private void onKeyPressed(final KeyEvent eventKey) {
		switch (eventKey.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			hPos -= 0.2f;
			if (hPos < -1)
				hPos = -1;
			avatar.setCompassForce(hPos);
			break;

		case KeyEvent.VK_RIGHT:
			hPos += 0.2f;
			if (hPos > 1)
				hPos = 1;
			avatar.setCompassForce(hPos);
			break;

		case KeyEvent.VK_UP:
			vPos += 0.2f;
			if (vPos > 1)
				vPos = 1;
			avatar.setPitchForce(vPos);
			break;

		case KeyEvent.VK_DOWN:
			vPos -= 0.2f;
			if (vPos < -1)
				vPos = -1;
			avatar.setPitchForce(vPos);
			break;

		case KeyEvent.VK_ENTER:
			avatar.drop();
			break;

		case KeyEvent.VK_SPACE:
			avatar.jump();
			break;

		case KeyEvent.VK_H:
			avatar.stop();
			break;

		case KeyEvent.VK_W:
			avatar.moveForward();
			break;

		case KeyEvent.VK_X:
			avatar.moveBackward();
			break;

		case KeyEvent.VK_S:
			vPos = 0;
			hPos = 0;
			avatar.setPitchForce(vPos);
			avatar.setCompassForce(hPos);
			break;

		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		}
	}

	/**
	 * @see javax.media.j3d.Behavior#processStimulus(java.util.Enumeration)
	 */
	@Override
	public void processStimulus(
			@SuppressWarnings("rawtypes") final Enumeration criteria) {
		WakeupCriterion c;
		while (criteria.hasMoreElements()) {
			c = (WakeupCriterion) criteria.nextElement();
			if (c instanceof WakeupOnAWTEvent) {
				for (final AWTEvent e : ((WakeupOnAWTEvent) c).getAWTEvent())
					if (e instanceof KeyEvent)
						onKey((KeyEvent) e);
			} else if (c instanceof WakeupOnElapsedFrames) {
				onElapsedFrames((WakeupOnElapsedFrames) c);
				avatar.onCollision(model.findCollision(avatar));
			}
		}

		// Set wakeup criteria for next time
		wakeupOn(wakeupCondition);
	}
}
