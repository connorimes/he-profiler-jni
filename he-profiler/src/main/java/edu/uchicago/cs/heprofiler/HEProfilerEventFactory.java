package edu.uchicago.cs.heprofiler;

/**
 * Create {@link HEProfilerEvent} and {@link HEProfilerEvent14} instances.
 * 
 * @author Connor Imes
 */
public class HEProfilerEventFactory {

	/**
	 * Create a new {@link HEProfilerEvent}.
	 * 
	 * @return event
	 */
	public static HEProfilerEvent createHEProfilerEvent() {
		return DefaultHEProfilerEvent.create();
	}

	/**
	 * Create a new {@link HEProfilerEvent}.
	 * 
	 * @param begin
	 * @return event
	 */
	public static HEProfilerEvent createHEProfilerEvent(final boolean begin) {
		return DefaultHEProfilerEvent.create(begin);
	}

	/**
	 * Create a new {@link HEProfilerEvent14} (Java 1.4-compatible).
	 * 
	 * @return event
	 */
	public static HEProfilerEvent14 createHEProfilerEvent14() {
		return DefaultHEProfilerEvent.create();
	}

	/**
	 * Create a new {@link HEProfilerEvent14} (Java 1.4-compatible).
	 * 
	 * @param begin
	 * @return event
	 */
	public static HEProfilerEvent14 createHEProfilerEvent14(final boolean begin) {
		return DefaultHEProfilerEvent.create(begin);
	}
}
