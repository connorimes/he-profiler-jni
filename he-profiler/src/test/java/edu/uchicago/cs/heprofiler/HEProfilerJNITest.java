package edu.uchicago.cs.heprofiler;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * Test the JNI methods. Those calls without asserts means we can't say anything
 * about the value, but need to make sure it doesn't crash.
 * 
 * @author Connor Imes
 */
public final class HEProfilerJNITest {

	@Test
	public void test_normal() {
		HEProfilerJNI profiler = HEProfilerJNI.get();
		assertNotNull("HEProfiler", profiler);
		assertEquals(0, profiler.init(1, 0, null, 20, null, null));
		ByteBuffer event = profiler.eventAlloc(false);
		assertNotNull("event", event);
		assertEquals(0, profiler.eventBegin(event));
		assertEquals(0, profiler.eventEndBegin(event, 0, 0, 1));
		assertEquals(0, profiler.eventEnd(event, 0, 1, 1));
		assertEquals(0, profiler.finish());
	}

	@Test
	public void test_normal_init_strings() {
		HEProfilerJNI profiler = HEProfilerJNI.get();
		assertNotNull("HEProfiler", profiler);
		assertEquals(0, profiler.init(1, 0, new String[] { "Test" }, 20, "PREFIX", "target"));
		assertEquals(0, profiler.finish());
	}

	@Test
	public void test_null_values() {
		HEProfilerJNI profiler = HEProfilerJNI.get();
		assertNotNull("HEProfiler", profiler);
		assertNotEquals(0, profiler.eventBegin(null));
		assertNotEquals(0, profiler.eventEndBegin(null, 0, 0, 1));
		assertNotEquals(0, profiler.eventEnd(null, 0, 1, 1));
	}

}
