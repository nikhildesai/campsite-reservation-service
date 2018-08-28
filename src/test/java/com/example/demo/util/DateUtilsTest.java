package com.example.demo.util;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {

	@Test
	public void testIsValidDateString() {
		// valid dates
		Assert.assertTrue(DateUtils.isValidDateString("20180112"));
		Assert.assertTrue(DateUtils.isValidDateString("20200403"));

		// only 7 digits
		Assert.assertFalse(DateUtils.isValidDateString("2018112"));

		// Invalid month
		Assert.assertFalse(DateUtils.isValidDateString("20181312"));

		// Invalid day
		Assert.assertFalse(DateUtils.isValidDateString("20180932"));

		// null
		Assert.assertFalse(DateUtils.isValidDateString(null));

		// empty string
		Assert.assertFalse(DateUtils.isValidDateString(""));
	}

}
