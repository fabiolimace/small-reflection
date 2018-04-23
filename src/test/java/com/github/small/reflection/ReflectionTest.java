package com.github.small.reflection;

import java.lang.reflect.Field;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ReflectionTest extends TestCase {

	Reflection r = new Reflection(Dummy.class);

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public ReflectionTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(ReflectionTest.class);
	}

	public void testGetClazz() {
		assertEquals(Dummy.class, r.getClazz());
	}

	public void testGetClazzName() {
		assertEquals(Dummy.class.getSimpleName(), r.getClazzName());
	}

	public void testGetClazzFullName() {
		assertEquals(Dummy.class.getName(), r.getClazzFullName());
	}

	public void testGetFields() {

		int count = 0;
		String fieldNames[] = { "id", "firstName", "lastName", "birthDate", "city" };

		for (Field field : r.getFields()) {
			for (String name : fieldNames) {
				if (field.getName().equals(name)) {
					count++;
				}
			}
		}

		assertEquals(fieldNames.length, count);
	}

}
