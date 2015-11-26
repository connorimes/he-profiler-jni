package edu.uchicago.cs.heprofiler;

/**
 * A profiling event.
 * 
 * @author Connor Imes
 */
public interface HeartbeatEnergyMonProfilerEvent {

	void eventBegin();

	void eventEnd(int profiler, long id, long work);

	void eventEndBegin(int profiler, long id, long work);

	void finish();
}
