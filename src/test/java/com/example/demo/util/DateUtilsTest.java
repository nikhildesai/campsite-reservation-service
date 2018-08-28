package com.example.demo.util;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {

	@Test
	public void testIsValidDateString() {
		// valid dates
		Assert.assertTrue(DateUtils.isValidDateString("2018-01-12"));
		Assert.assertTrue(DateUtils.isValidDateString("2020-04-03"));

		// only 7 digits
		Assert.assertFalse(DateUtils.isValidDateString("2018-11-2"));

		// Invalid month
		Assert.assertFalse(DateUtils.isValidDateString("2018-13-12"));

		// Invalid day
		Assert.assertFalse(DateUtils.isValidDateString("2018-09-32"));

		// null
		Assert.assertFalse(DateUtils.isValidDateString(null));

		// empty string
		Assert.assertFalse(DateUtils.isValidDateString(""));
	}

}
