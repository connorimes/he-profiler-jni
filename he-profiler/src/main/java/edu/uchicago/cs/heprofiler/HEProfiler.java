package edu.uchicago.cs.heprofiler;

import java.util.Arrays;

/**
 * Initialize and dispose the native Heartbeat/EnergyMon profilers. Methods are
 * static because profilers are global.
 * 
 * @author Connor Imes
 */
public class HEProfiler {
	protected static boolean initialized = false;

	/**
	 * Check if the profiler is initialized.
	 * 
	 * @return true if initialized, false otherwise
	 */
	public static synchronized boolean isInitialized() {
		return initialized;
	}

	/**
	 * Initialize the profilers based on the given enum class. Profiler names
	 * (and therefore logs) are automatically enabled for all enum values. If
	 * this is not desirable, use
	 * {@link #init(int, String[], long[], long, int, long, String)} instead.
	 * 
	 * @param profilerEnum
	 * @param windowSizes
	 * @param defaultWindowSize
	 * @param applicationProfiler
	 * @param appProfilerMinSleepUs
	 * @param logPath
	 */
	public static synchronized <E extends Enum<E>> void init(final Class<E> profilerEnum, final long[] windowSizes,
			final long defaultWindowSize, final E applicationProfiler, final long appProfilerMinSleepUs,
			final String logPath) {
		HEProfiler.init(profilerEnum.getEnumConstants().length, HEProfiler.toProfilerNames(profilerEnum), windowSizes,
				defaultWindowSize,
				applicationProfiler == null ? profilerEnum.getEnumConstants().length : applicationProfiler.ordinal(),
				appProfilerMinSleepUs, logPath);
	}

	/**
	 * Initialize the profilers (Java 1.4-compatible method).
	 * 
	 * @param numProfilers
	 * @param profilerNames
	 * @param windowSizes
	 * @param defaultWindowSize
	 * @param applicationProfiler
	 * @param appProfilerMinSleepUs
	 * @param logPath
	 * @throws IllegalStateException
	 *             if already initialized or initialization fails
	 */
	public static synchronized void init(final int numProfilers, final String[] profilerNames, final long[] windowSizes,
			final long defaultWindowSize, final int applicationProfiler, final long appProfilerMinSleepUs,
			final String logPath) {
		if (initialized) {
			throw new IllegalStateException("Already initialized!");
		} else {
			if (HEProfilerJNI.get().init(numProfilers, profilerNames, windowSizes, defaultWindowSize,
					applicationProfiler, appProfilerMinSleepUs, logPath) != 0) {
				throw new IllegalStateException("Init failed in JNI");
			}
			initialized = true;
		}
	}

	/**
	 * Destroy the profilers.
	 * {@link #init(int, int, String[], long, String, String)} must have been
	 * called first.
	 * 
	 * @throws IllegalStateException
	 *             if not initialized or finish fails
	 */
	public static synchronized void dispose() {
		if (initialized) {
			initialized = false;
			if (HEProfilerJNI.get().finish() != 0) {
				throw new IllegalStateException("Finish failed in JNI");
			}
		} else {
			throw new IllegalStateException("Not initialized!");
		}
	}

	/**
	 * Utility function to convert enum names to Strings. Useful for specifying
	 * profilerNames in {@link #init(int, int, String[], long, String, String)}.
	 * 
	 * @param e
	 * @return String array
	 */
	public static String[] toProfilerNames(final Class<? extends Enum<?>> e) {
		if (e == null) {
			return null;
		}
		return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
	}

}
