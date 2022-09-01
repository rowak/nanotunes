package io.github.rowak.nanotunes;

import io.github.rowak.nanoleafapi.Effect;

public class DisplayEvent {
	private long timestamp;
	private Effect effect;
	
	public DisplayEvent(long timestamp, Effect effect) {
		this.timestamp = timestamp;
		this.effect = effect;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public Effect getEffect() {
		return effect;
	}
}
