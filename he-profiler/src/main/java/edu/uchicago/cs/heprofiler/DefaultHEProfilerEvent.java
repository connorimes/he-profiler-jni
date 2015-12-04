package edu.uchicago.cs.heprofiler;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A profiling event. This implementation is a simple wrapper around the JNI
 * functions.
 * 
 * Attempting to perform operations after {@link #finish()} is called will
 * result in an {@link IllegalStateException}.
 * 
 * @author Connor Imes
 */
public class DefaultHEProfilerEvent implements HEProfilerEvent {
	protected volatile ByteBuffer nativePtr;
	// r/w lock for pointer to prevent race conditions that could cause crash
	protected final ReadWriteLock lock;

	protected DefaultHEProfilerEvent(final ByteBuffer nativePtr) {
		this.nativePtr = nativePtr;
		this.lock = new ReentrantReadWriteLock(true);
	}

	public static DefaultHEProfilerEvent create() {
		return create(false);
	}

	public static DefaultHEProfilerEvent create(final boolean begin) {
		final ByteBuffer ptr = HEProfilerJNI.get().eventAlloc(begin);
		if (ptr == null) {
			throw new IllegalStateException("Failed to allocate event over JNI");
		}
		return new DefaultHEProfilerEvent(ptr);
	}

	public void eventBegin() {
		try {
			lock.readLock().lock();
			enforceNotFinished();
			if (!HEProfilerJNI.get().eventBegin(nativePtr)) {
				throw new IllegalStateException("Unexpected failure in JNI - bad pointer?");
			}
		} finally {
			lock.readLock().unlock();
		}
	}

	public void eventEnd(final int profiler, final long id) {
		eventEnd(profiler, id, 1, false);
	}

	public void eventEnd(final int profiler, final long id, final boolean finish) {
		eventEnd(profiler, id, 1, finish);
	}

	public void eventEnd(final int profiler, final long id, final long work) {
		eventEnd(profiler, id, work, false);
	}

	public void eventEnd(final int profiler, final long id, final long work, final boolean finish) {
		try {
			lock.readLock().lock();
			enforceNotFinished();
			if (!HEProfilerJNI.get().eventEnd(nativePtr, profiler, id, work, finish)) {
				throw new IllegalStateException("Unexpected failure in JNI - bad pointer?");
			}
			if (finish) {
				nativePtr = null;
			}
		} finally {
			lock.readLock().unlock();
		}
	}

	public void eventEndBegin(final int profiler, final long id) {
		eventEndBegin(profiler, id, 1);
	}

	public void eventEndBegin(final int profiler, final long id, final long work) {
		try {
			lock.readLock().lock();
			enforceNotFinished();
			if (!HEProfilerJNI.get().eventEndBegin(nativePtr, profiler, id, work)) {
				throw new IllegalStateException("Unexpected failure in JNI - bad pointer?");
			}
		} finally {
			lock.readLock().unlock();
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
		try {
			lock.writeLock().lock();
			enforceNotFinished();
			finishAndFree();
		} finally {
			lock.writeLock().unlock();
		}

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
