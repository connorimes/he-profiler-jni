package edu.uchicago.cs.heprofiler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link HeartbeatEnergyMonProfiler}.
 * 
 * @author Connor Imes
 */
public final class HeartbeatEnergyMonProfilerTest {

	@Before
	public void init() {
		HeartbeatEnergyMonProfiler.init(1, -1, null, 20, null, null);
	}

	@After
	public void destroy() {
		try {
			HeartbeatEnergyMonProfiler.finish();
		} catch (IllegalStateException e) {
			// do nothing
		}
	}

	@Test
	public void test_normal() {
		// just need the before and after methods to run
	}

	@Test(expected = IllegalStateException.class)
	public void test_double_init() {
		HeartbeatEnergyMonProfiler.init(1, -1, null, 20, null, null);
	}

	@Test(expected = IllegalStateException.class)
	public void test_double_finish() {
		HeartbeatEnergyMonProfiler.finish();
		HeartbeatEnergyMonProfiler.finish();
	}

}
