package io.github.rowak.nanotunes;

public class NativeAudioUtil {
	
	static {
		System.loadLibrary("nativeaudioutil");
	}
	
	public static native long open();
	public static native void close(long pulse);
	public static native int getMaxAmplitude(long pulse);
}