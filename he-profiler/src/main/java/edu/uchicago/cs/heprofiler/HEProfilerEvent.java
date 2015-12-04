package edu.uchicago.cs.heprofiler;

/**
 * A profiling event.
 * 
 * @author Connor Imes
 */
public interface HEProfilerEvent {

	/**
	 * Begin an event by taking timing and energy readings.
	 */
	void eventBegin();

	/**
	 * End an event. Defaults to 1 unit of work.
	 * 
	 * @param profiler
	 * @param id
	 * @see #eventEnd(int, long, long, boolean)
	 */
	void eventEnd(int profiler, long id);

	/**
	 * End an event. Defaults to 1 unit of work.
	 * 
	 * @param profiler
	 * @param id
	 * @param dispose
	 * @see #eventEnd(int, long, long, boolean)
	 */
	void eventEnd(int profiler, long id, boolean dispose);

	/**
	 * End an event.
	 * 
	 * @param profiler
	 * @param id
	 * @param work
	 * @see #eventEnd(int, long, long, boolean)
	 */
	void eventEnd(int profiler, long id, long work);

	/**
	 * End an event by taking timing and energy readings and issuing a
	 * heartbeat. Optionally dispose (free native resources) - saves a JNI
	 * boundary crossing if the event is not going to be reused since
	 * {@link #dispose()} would need to be called eventually.
	 * 
	 * @param profiler
	 * @param id
	 * @param work
	 * @param dispose
	 */
	void eventEnd(int profiler, long id, long work, boolean dispose);

	/**
	 * End/Begin event. Defaults to 1 unit of work.
	 * 
	 * @param profiler
	 * @param id
	 * @see HEProfilerEvent#eventEndBegin(int, long, long)
	 */
	void eventEndBegin(int profiler, long id);

	/**
	 * End an event by taking timing and energy readings and issuing a
	 * heartbeat, then set the end values as the begin values. This begins a new
	 * event immediately, without any time or energy loss between events. As a
	 * bonus, it prevents the need for making extra timing/energy readings and
	 * JNI boundary crossings by naively invoking
	 * {@link #eventEnd(int, long, long)} then {@link #eventBegin()}.
	 * 
	 * @param profiler
	 * @param id
	 * @param work
	 */
	void eventEndBegin(int profiler, long id, long work);

	/**
	 * Free native resources. The instance will likely not be usable after this
	 * method is invoked.
	 */
	void dispose();
}
