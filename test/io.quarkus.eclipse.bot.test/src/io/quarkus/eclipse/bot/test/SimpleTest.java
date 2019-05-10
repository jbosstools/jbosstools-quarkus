package io.quarkus.eclipse.bot.test;


import static org.junit.Assert.assertTrue;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RedDeerSuite.class)
public class SimpleTest {

	@Ignore
	@Test
	public void test() {
		assertTrue(true);
	}

}
