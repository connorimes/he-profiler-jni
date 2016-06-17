package edu.uchicago.cs.heprofiler;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the JNI methods. Those calls without asserts means we can't say anything
 * about the value, but need to make sure it doesn't crash.
 * 
 * @author Connor Imes
 */
public final class DefaultHEProfilerEventTest {

	@BeforeClass
	public static void init() {
		HEProfiler.init(1, null, null, 20, 1, 0, "target");
	}

	@AfterClass
	public static void teardown() {
		try {
			HEProfiler.dispose();
		} catch (IllegalStateException e) {
			// do nothing
		}
	}

	@Test
	public void test_normal() {
		DefaultHEProfilerEvent profiler = DefaultHEProfilerEvent.create();
		profiler.eventBegin();
		profiler.eventEndBegin(0, 0, 1);
		profiler.eventEnd(0, 1, 1);
	}

}
