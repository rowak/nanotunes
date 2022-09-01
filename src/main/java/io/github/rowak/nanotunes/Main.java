package io.github.rowak.nanotunes;

import java.util.Calendar;
import java.util.List;

import io.github.rowak.nanoleafapi.Aurora;
import io.github.rowak.nanoleafapi.Color;
import io.github.rowak.nanoleafapi.Frame;
import io.github.rowak.nanoleafapi.Panel;
import io.github.rowak.nanoleafapi.StaticEffect;

public class Main {
	
	final String ip = "192.168.2.70";
	final int port = 16021;
	final String token = "sdW17SPrHBwNyvhcTjJCKTyLUMPz5eeP";
	
	private Aurora aurora;
	private List<Panel> panels;
	private Panel[][] panelGrid;
	private DisplayEventScheduler eventScheduler;
	private long pulsePointer; // Native pointer to pulseaudio stream
	
	public Main() {
		if (init()) {
			processAudio();
		}
		else {
			System.err.println("Init failed.");
		}
	}
	
	private void processAudio() {
		while (true) {
			int maxAmp = NativeAudioUtil.getMaxAmplitude(pulsePointer);
			if (maxAmp > 0) {
				float normalizedAmp = AudioAnalysisHelper.normalizeAmplitude(maxAmp);
				showDebugSoundbar(normalizedAmp);
				updatePanels((int)(normalizedAmp*panelGrid.length));
			}
		}
	}
	
	private void showDebugSoundbar(float normalizedAmp) {
		for (int i = 0; i < normalizedAmp*100; i++) {
			System.out.print("-");
		}
		System.out.println();
	}
	
	private void updatePanels(int width) {
		StaticEffect.Builder builder = new StaticEffect.Builder(panels);
		
		for (int i = 0; i < panelGrid.length; i++) {
			for (int j = 0; j < panelGrid[i].length; j++) {
				if (i < width) {
					builder.setPanel(panelGrid[i][j], new Frame(Color.BLUE, 0));
				}
				else {
					builder.setPanel(panelGrid[i][j], new Frame(Color.BLACK, 2));
				}
			}
		}
		
		StaticEffect frameEffect;
		try {
			frameEffect = builder.build(null);
			enqueueEvent(frameEffect);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Enqueues an effect to be displayed at a later date.
	// This allows an audio offset to be applied.
	private void enqueueEvent(StaticEffect effect) {
		long timestamp = Calendar.getInstance().getTimeInMillis();
		DisplayEvent event = new DisplayEvent(timestamp, effect);
		eventScheduler.enqueueEvent(event);
	}
	
	private boolean init() {
		try {
			aurora = new Aurora(ip, port, token);
			aurora.enableExternalStreaming();
			panels = aurora.getPanelsRotated();
			panelGrid = PanelGridSort.getColumns(panels.toArray(new Panel[0]));
			
			// Open a pulseaudio stream
			pulsePointer = NativeAudioUtil.open();
			
			eventScheduler = new DisplayEventScheduler(aurora, 0);
			eventScheduler.start();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
		new Main();
	}
}
