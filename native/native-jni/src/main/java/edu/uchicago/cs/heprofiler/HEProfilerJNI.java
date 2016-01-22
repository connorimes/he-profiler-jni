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

	public native int init(int numProfilers, String[] profilerNames, long[] windowSizes, long defaultWindowSize,
			int applicationProfiler, long appProfilerMinSleepUs, String logPath);

	public native int finish();

	public native ByteBuffer eventAlloc(boolean begin);

	public native void eventFree(ByteBuffer ptr);

	public native boolean eventBegin(ByteBuffer ptr);

	public native boolean eventEnd(ByteBuffer ptr, int profiler, long id, long work, boolean free);

	public native boolean eventEndBegin(ByteBuffer ptr, int profiler, long id, long work);
}
