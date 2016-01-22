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
		assertEquals(0, profiler.init(1, null, null, 20, 0, 0, null));
		ByteBuffer event = profiler.eventAlloc(false);
		assertNotNull("event", event);
		assertTrue(profiler.eventBegin(event));
		assertTrue(profiler.eventEndBegin(event, 0, 0, 1));
		assertTrue(profiler.eventEnd(event, 0, 1, 1, false));
		assertEquals(0, profiler.finish());
	}

	@Test
	public void test_normal_init_strings() {
		HEProfilerJNI profiler = HEProfilerJNI.get();
		assertNotNull("HEProfiler", profiler);
		assertEquals(0, profiler.init(1, new String[] { "Test" }, null, 20, 0, 0, "target"));
		assertEquals(0, profiler.finish());
	}

	@Test
	public void test_null_values() {
		HEProfilerJNI profiler = HEProfilerJNI.get();
		assertNotNull("HEProfiler", profiler);
		assertFalse(profiler.eventBegin(null));
		assertFalse(profiler.eventEndBegin(null, 0, 0, 1));
		assertFalse(profiler.eventEnd(null, 0, 1, 1, false));
	}

}
