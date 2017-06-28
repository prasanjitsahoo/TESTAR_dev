package org.fruit.a11y.wcag;

import static org.junit.Assert.*;

import org.fruit.alayer.Verdict;
import org.junit.BeforeClass;
import org.junit.Test;

public class SuccessCriterionTest {
	
	private final int NLEVELS = 3; // A, AA, AAA
	private final double PRIO_A = 1.0, // high/error
			PRIO_AA = 0.666, // medium/warning
			PRIO_AAA = 0.333, // low/notice
			DELTA = 0.1;
	
	@Test
	public void testGetNr() {
		Principle p = new Principle(1, "Foo");
		AbstractGuideline g = new KeyboardAccessibleGuideline(2, p);
		SuccessCriterion s = new SuccessCriterion(3, "Bar", g, Level.A);
		assertEquals("1.2.3", s.getNr());
	}
	
	@Test
	public void testToString() {
		Principle p = new Principle(1, "Foo");
		AbstractGuideline g = new KeyboardAccessibleGuideline(2, p);
		SuccessCriterion s = new SuccessCriterion(3, "Bar", g, Level.A);
		assertEquals("1.2.3 Bar (Level A)", s.toString());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testGetVerdictPriority() {
		if (PRIO_AAA < Verdict.SEVERITY_MIN || PRIO_A > Verdict.SEVERITY_MAX) {
			fail("The Verdict priority range has changed, update this test");
		}
		Principle p = new Principle(1, "Foo");
		AbstractGuideline g = new KeyboardAccessibleGuideline(2, p);
		SuccessCriterion s1 = new SuccessCriterion(3, "Bar", g, Level.A),
				s2 = new SuccessCriterion(3, "Baz", g, Level.AA),
				s3 = new SuccessCriterion(3, "Bat", g, Level.AAA);
		assertEquals("Level A shall have a high priority",
				PRIO_A, s1.getVerdictPriority(), DELTA);
		assertEquals("Level AA shall have a medium priority",
				PRIO_AA, s2.getVerdictPriority(), DELTA);
		assertEquals("Level AAA shall have a low priority",
				PRIO_AAA, s3.getVerdictPriority(), DELTA);
	}

}
