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
		HEProfiler.init(EnumA.class, null, 20, null, 0, "target");
	}

	@After
	public void destroy() {
		try {
			HEProfiler.dispose();
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
		HEProfiler.init(EnumA.class, null, 20, null, 0, "target");
	}

	@Test(expected = IllegalStateException.class)
	public void test_double_dipose() {
		HEProfiler.dispose();
		HEProfiler.dispose();
	}

	private enum EnumA {
		A
	}

}
