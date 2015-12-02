package edu.uchicago.cs.heprofiler;

import java.nio.ByteBuffer;

/**
 * JNI bindings for he-profiler.
 * 
 * @author Connor Imes
 */
public final class HEProfilerJNI {
	private static HEProfilerJNI instance = null;

	/**
	 * Get an instance of {@link HEProfilerJNI}. On the first access, this
	 * method loads the native library. Failure to load will result in runtime
	 * exceptions or errors.
	 * 
	 * @return {@link HEProfilerJNI}
	 */
	public static HEProfilerJNI get() {
		if (instance == null) {
			System.loadLibrary("he-profiler-wrapper");
			instance = new HEProfilerJNI();
		}
		return instance;
	}

	public native int init(int numProfilers, int applicationProfiler, String[] profilerNames, long defaultWindowSize,
			String envVarPrefix, String logPath);

	public native int finish();

	public native ByteBuffer eventAlloc(boolean begin);

	public native void eventFree(ByteBuffer ptr);

	public native int eventBegin(ByteBuffer ptr);

	public native int eventEnd(ByteBuffer ptr, int profiler, long id, long work);

	public native int eventEndBegin(ByteBuffer ptr, int profiler, long id, long work);
}
