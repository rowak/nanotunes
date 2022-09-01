package io.github.rowak.nanotunes;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import io.github.rowak.nanoleafapi.Aurora;
import io.github.rowak.nanoleafapi.Color;
import io.github.rowak.nanoleafapi.CustomEffect;
import io.github.rowak.nanoleafapi.Effect;
import io.github.rowak.nanoleafapi.Frame;
import io.github.rowak.nanoleafapi.Panel;
import io.github.rowak.nanoleafapi.StaticEffect;

public class Main_Beats {
	
	final String ip = "192.168.2.70";
	final int port = 16021;
	final String token = "sdW17SPrHBwNyvhcTjJCKTyLUMPz5eeP";
	
	private Aurora aurora;
	private List<Panel> panels;
	private DisplayEventScheduler eventScheduler;
	private long pulsePointer; // Native pointer to pulseaudio stream
	
	public Main_Beats() {
		if (init()) {
			processAudio();
		}
		else {
			System.err.println("Init failed.");
		}
	}
	
	private void processAudio() {
		LinkedList<Integer> q = new LinkedList<Integer>();
		int lastAmp = 0;
		int lastAvg = 0;
		boolean lastBeat = false;
		long lastBeatTime = 0;
		while (true) {
			int maxAmp = NativeAudioUtil.getMaxAmplitude(pulsePointer);
			if (maxAmp > 0) {
				float normalizedAmp = AudioAnalysisHelper.normalizeAmplitude(maxAmp);
				long time = Calendar.getInstance().getTimeInMillis();
				boolean beat = (float)getAvg(q)/lastAvg > 0.15 && maxAmp > lastAmp && !lastBeat && (maxAmp/(float)lastAmp-1) > 0.7 && time-lastBeatTime > 150;
				lastBeat = beat;
				showDebugSoundbar(normalizedAmp, beat);
				if (beat)
				{
					lastBeatTime = time;
					updatePanels();
				}
			}
			lastAvg = getAvg(q);
			q.addLast(maxAmp);
			if (q.size() > 3) {
				q.removeFirst();
			}
			lastAmp = maxAmp;
		}
	}
	
	private int getAvg(List<Integer> list) {
		if (list.size() == 0) {
			return 0;
		}
		int avg = 0;
		for (Integer x : list) {
			avg += x;
		}
		return (int)(avg/list.size());
	}
	
	private void showDebugSoundbar(float normalizedAmp, boolean beat) {
		for (int i = 0; i < normalizedAmp*100; i++) {
			System.out.print("-");
		}
		if (beat) {
			System.out.print("(beat)");
		}
		System.out.println();
	}
	
	private void updatePanels() {
		StaticEffect.Builder builder = new StaticEffect.Builder(panels);
		StaticEffect.Builder builder2 = new StaticEffect.Builder(panels);
		java.util.Random r = new java.util.Random();
		int x = r.nextInt(panels.size());
		int x1 = r.nextInt(panels.size());
		int y = r.nextInt(5);
		Color colors[] = new Color[] {Color.RED, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.GREEN};
		builder.setPanel(panels.get(x), new Frame(colors[y], 0));
		builder.setPanel(panels.get(x1), new Frame(colors[y], 0));
		builder2.setPanel(panels.get(x), new Frame(Color.BLACK, 10));
		builder2.setPanel(panels.get(x1), new Frame(Color.BLACK, 10));
		
		CustomEffect frameEffect;
		try {
			frameEffect = builder.build(null);
			enqueueEvent(frameEffect);
			frameEffect = builder2.build(null);
			enqueueEvent(frameEffect);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Enqueues an effect to be displayed at a later date.
	// This allows an audio offset to be applied.
	private void enqueueEvent(Effect effect) {
		long timestamp = Calendar.getInstance().getTimeInMillis();
		DisplayEvent event = new DisplayEvent(timestamp, effect);
		eventScheduler.enqueueEvent(event);
	}
	
	private boolean init() {
		try {
			aurora = new Aurora(ip, port, token);
			aurora.enableExternalStreaming();
			panels = aurora.getPanelsRotated();
			
			// Open a pulseaudio stream
			pulsePointer = NativeAudioUtil.open();
			
			eventScheduler = new DisplayEventScheduler(aurora, 100);
			eventScheduler.start();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
		new Main_Beats();
	}
}
