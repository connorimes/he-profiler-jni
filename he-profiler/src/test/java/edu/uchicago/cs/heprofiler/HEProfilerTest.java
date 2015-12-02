package edu.uchicago.cs.heprofiler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link HEProfiler}.
 * 
 * @author Connor Imes
 */
public final class HEProfilerTest {

	@Before
	public void init() {
		HEProfiler.init(1, -1, null, 20, null, null);
	}

	@After
	public void destroy() {
		try {
			HEProfiler.finish();
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
		HEProfiler.init(1, -1, null, 20, null, null);
	}

	@Test(expected = IllegalStateException.class)
	public void test_double_finish() {
		HEProfiler.finish();
		HEProfiler.finish();
	}

}
