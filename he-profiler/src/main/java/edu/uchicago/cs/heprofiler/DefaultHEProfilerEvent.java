package edu.uchicago.cs.heprofiler;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A profiling event. This implementation is a simple wrapper around the JNI
 * functions.
 * 
 * Attempting to perform operations after {@link #dispose()} is called will
 * result in an {@link IllegalStateException}.
 * 
 * @author Connor Imes
 */
public class DefaultHEProfilerEvent implements HEProfilerEvent, HEProfilerEvent14 {
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
			enforceNotDisposed();
			if (!HEProfilerJNI.get().eventBegin(nativePtr)) {
				throw new IllegalStateException("Failed to begin event in JNI");
			}
		} finally {
			lock.readLock().unlock();
		}
	}

	public void eventEnd(final Enum<?> profiler, final long id) {
		if (profiler == null) {
			throw new IllegalArgumentException();
		}
		eventEnd(profiler.ordinal(), id);
	}

	public void eventEnd(final int profiler, final long id) {
		eventEnd(profiler, id, 1, false);
	}

	public void eventEnd(final Enum<?> profiler, final long id, final boolean dispose) {
		if (profiler == null) {
			throw new IllegalArgumentException();
		}
		eventEnd(profiler.ordinal(), id, dispose);
	}

	public void eventEnd(final int profiler, final long id, final boolean dispose) {
		eventEnd(profiler, id, 1, dispose);
	}

	public void eventEnd(final Enum<?> profiler, final long id, final long work) {
		if (profiler == null) {
			throw new IllegalArgumentException();
		}
		eventEnd(profiler.ordinal(), id, work);
	}

	public void eventEnd(final int profiler, final long id, final long work) {
		eventEnd(profiler, id, work, false);
	}

	public void eventEnd(final Enum<?> profiler, final long id, final long work, final boolean dispose) {
		if (profiler == null) {
			throw new IllegalArgumentException();
		}
		eventEnd(profiler.ordinal(), id, work, dispose);
	}

	public void eventEnd(final int profiler, final long id, final long work, final boolean dispose) {
		try {
			lock.readLock().lock();
			enforceNotDisposed();
			if (!HEProfilerJNI.get().eventEnd(nativePtr, profiler, id, work, dispose)) {
				throw new IllegalStateException("Failed to end event in JNI");
			}
			if (dispose) {
				nativePtr = null;
			}
		} finally {
			lock.readLock().unlock();
		}
	}

	public void eventEndBegin(final Enum<?> profiler, final long id) {
		if (profiler == null) {
			throw new IllegalArgumentException();
		}
		eventEndBegin(profiler.ordinal(), id);
	}

	public void eventEndBegin(final int profiler, final long id) {
		eventEndBegin(profiler, id, 1);
	}

	public void eventEndBegin(final Enum<?> profiler, final long id, final long work) {
		if (profiler == null) {
			throw new IllegalArgumentException();
		}
		eventEndBegin(profiler.ordinal(), id, work);
	}

	public void eventEndBegin(final int profiler, final long id, final long work) {
		try {
			lock.readLock().lock();
			enforceNotDisposed();
			if (!HEProfilerJNI.get().eventEndBegin(nativePtr, profiler, id, work)) {
				throw new IllegalStateException("Failed to end/begin event in JNI");
			}
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * This method does NOT enforce if this instance is already freed!
	 */
	protected void free() {
		HEProfilerJNI.get().eventFree(nativePtr);
		nativePtr = null;
	}

	public void dispose() {
		try {
			lock.writeLock().lock();
			enforceNotDisposed();
			free();
		} finally {
			lock.writeLock().unlock();
		}

	}

	@Override
	protected void finalize() throws Throwable {
		// last-ditch effort to cleanup if user didn't follow protocol
		try {
			if (nativePtr != null) {
				free();
			}
		} finally {
			super.finalize();
		}
	}

	/**
	 * Throws an {@link IllegalStateException} if {@link #nativePtr} is null.
	 */
	protected void enforceNotDisposed() {
		if (nativePtr == null) {
			throw new IllegalStateException("Already disposed");
		}
	}
}
