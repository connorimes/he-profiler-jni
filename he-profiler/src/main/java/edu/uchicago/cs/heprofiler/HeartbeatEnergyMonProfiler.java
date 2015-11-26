package edu.uchicago.cs.heprofiler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Initialize and finish the native Heartbeat/EnergyMon profilers. Methods are
 * static because profilers are global.
 * 
 * @author Connor Imes
 */
public class HeartbeatEnergyMonProfiler {
	protected static final AtomicBoolean initialized = new AtomicBoolean(false);
	protected static final AtomicBoolean finished = new AtomicBoolean(false);

	/**
	 * Initialize the profilers. This method should only be called once!
	 * 
	 * @param numProfilers
	 * @param applicationProfiler
	 * @param profilerNames
	 * @param defaultWindowSize
	 * @param envVarPrefix
	 * @param logPath
	 * @throws IllegalStateException
	 *             if already initialized
	 */
	public static void init(final int numProfilers, final int applicationProfiler, final String[] profilerNames,
			final long defaultWindowSize, final String envVarPrefix, final String logPath) {
		if (initialized.compareAndSet(false, true)) {
			if (HEProfilerJNI.get().init(numProfilers, applicationProfiler, profilerNames, defaultWindowSize,
					envVarPrefix, logPath) != 0) {
				throw new IllegalStateException("Init failed");
			}
		} else {
			throw new IllegalStateException("Already initialized!");
		}

	}

	/**
	 * Destroy the profilers. This method should only be called once, and
	 * {@link #init(int, int, String[], long, String, String)} must have been
	 * called first.
	 * 
	 * @throws IllegalStateException
	 *             if not initialized or already finished
	 */
	public static void finish() {
		if (!initialized.get()) {
			throw new IllegalStateException("Not initialized!");
		}
		try {
			if (finished.compareAndSet(false, true)) {
				if (HEProfilerJNI.get().finish() != 0) {
					throw new IllegalStateException("Finish failed");
				}
			} else {
				throw new IllegalStateException("Already finished!");
			}
		} finally {
			initialized.set(false);
			finished.set(false);
		}
	}
}
