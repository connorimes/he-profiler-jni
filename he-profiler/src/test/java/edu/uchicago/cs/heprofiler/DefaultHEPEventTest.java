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
public final class DefaultHEPEventTest {

	@BeforeClass
	public static void init() {
		HeartbeatEnergyMonProfiler.init(1, 0, null, 20, null, null);
	}

	@AfterClass
	public static void teardown() {
		try {
			HeartbeatEnergyMonProfiler.finish();
		} catch (IllegalStateException e) {
			// do nothing
		}
	}

	@Test
	public void test_normal() {
		DefaultHEPEvent profiler = DefaultHEPEvent.create();
		profiler.eventBegin();
		profiler.eventEndBegin(0, 0, 1);
		profiler.eventEnd(0, 1, 1);
	}

}
