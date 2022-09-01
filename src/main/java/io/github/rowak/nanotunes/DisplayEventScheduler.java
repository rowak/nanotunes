package io.github.rowak.nanotunes;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import io.github.rowak.nanoleafapi.CustomEffect;
import io.github.rowak.nanoleafapi.Effect;
import io.github.rowak.nanoleafapi.NanoleafDevice;
import io.github.rowak.nanoleafapi.StaticEffect;

public class DisplayEventScheduler {
	private int timeOffset;
	private NanoleafDevice device;
	private Queue<DisplayEvent> eventQueue;
	private Timer timer;

	public DisplayEventScheduler(NanoleafDevice device, int timeOffset) {
		this.device = device;
		this.timeOffset = timeOffset;
		eventQueue = new LinkedList<DisplayEvent>();
	}
	
	public void enqueueEvent(DisplayEvent event) {
		eventQueue.add(event);
	}
	
	public void start() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
		else {
			timer = new Timer();
		}
		
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				processEvents();
			}
		}, 0, 10);
	}
	
	public void processEvents() {
		if (!eventQueue.isEmpty()) {
			DisplayEvent currEvent = eventQueue.peek();
			long currTime = Calendar.getInstance().getTimeInMillis();
			if (currTime >= currEvent.getTimestamp() + timeOffset) {
				eventQueue.remove();
				sendEffect(currEvent.getEffect());
			}
		}
	}
	
	public void sendEffect(Effect effect) {
		try {
			if (effect instanceof StaticEffect) {
				device.sendStaticEffectExternalStreaming((StaticEffect)effect);
			}
			else if (effect instanceof CustomEffect) {
				device.sendAnimData(((CustomEffect)effect).getAnimationData());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
