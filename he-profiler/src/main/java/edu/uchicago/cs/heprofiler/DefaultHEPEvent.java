package edu.uchicago.cs.heprofiler;

import java.nio.ByteBuffer;

/**
 * A profiling event. This implementation is a simple wrapper around the JNI
 * functions.
 * 
 * This implementation is <b>NOT</b> thread safe and should be synchronized
 * externally. Attempting to perform operations after {@link #finish()} is
 * called will result in an {@link IllegalStateException}.
 * 
 * @author Connor Imes
 */
public class DefaultHEPEvent implements HeartbeatEnergyMonProfilerEvent {
	protected volatile ByteBuffer nativePtr;

	protected DefaultHEPEvent(final ByteBuffer nativePtr) {
		this.nativePtr = nativePtr;
	}

	public static DefaultHEPEvent create() {
		return create(false);
	}

	public static DefaultHEPEvent create(final boolean begin) {
		final ByteBuffer ptr = HEProfilerJNI.get().eventAlloc(begin);
		if (ptr == null) {
			throw new IllegalStateException("Failed to allocate event over JNI");
		}
		return new DefaultHEPEvent(ptr);
	}

	public void eventBegin() {
		enforceNotFinished();
		if (!HEProfilerJNI.get().eventBegin(nativePtr)) {
			throw new IllegalStateException("Unexpected failure in JNI - bad pointer?");
		}
	}

	public void eventEnd(final int profiler, final long id, final long work) {
		eventEnd(profiler, id, work, false);
	}

	public void eventEnd(final int profiler, final long id, final long work, final boolean finish) {
		enforceNotFinished();
		if (!HEProfilerJNI.get().eventEnd(nativePtr, profiler, id, work, finish)) {
			throw new IllegalStateException("Unexpected failure in JNI - bad pointer?");
		}
		if (finish) {
			nativePtr = null;
		}
	}

	public void eventEndBegin(final int profiler, final long id, final long work) {
		enforceNotFinished();
		if (!HEProfilerJNI.get().eventEndBegin(nativePtr, profiler, id, work)) {
			throw new IllegalStateException("Unexpected failure in JNI - bad pointer?");
		}
	}

	/**
	 * This method does NOT enforce if this instance is already finished!
	 */
	protected void finishAndFree() {
		HEProfilerJNI.get().eventFree(nativePtr);
		nativePtr = null;
	}

	public void finish() {
		enforceNotFinished();
		finishAndFree();
	}

	@Override
	protected void finalize() throws Throwable {
		// last-ditch effort to cleanup if user didn't follow protocol
		try {
			if (nativePtr != null) {
				finishAndFree();
			}
		} finally {
			super.finalize();
		}
	}

	/**
	 * Throws an {@link IllegalStateException} if {@link #nativePtr} is null.
	 */
	protected void enforceNotFinished() {
		if (nativePtr == null) {
			throw new IllegalStateException("Already finished");
		}
	}
}
