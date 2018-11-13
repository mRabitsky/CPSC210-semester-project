package models;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CourseTester {
	@BeforeAll
	public static void killLogger() {
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	}
	
	@Test
	void changeCourseIncorrectly() {
		final var course = Course.of("CPSC", "221", "101").get();
		assertThrows(InvalidSeatCountException.class, () -> {
			course.setGeneralSeatsRemaining(-20);
		});
	}
	@Test
	void createCourse() {
		final var course = Course.of("CPSC", "221", "101");
		assertTrue(course.isPresent());
		System.out.println(course.get().verbose());
	}
	@Test
	void fakeCourse() {
		final var course = Course.of("ABC", "123", "L1A");
		assertFalse(course.isPresent());
	}
	@Test
	void parseCourse() {
		final var course = Course.parse("CPSC 221 101");
		assertTrue(course.isPresent());
		System.out.println(course.get().verbose());
	}
	@Test
	void updateCourse() {
		final var course = Course.of("CPSC", "221", "101");
		assertTrue(course.isPresent());
		final var before = course.get().verbose();
		assertTrue(course.get().update());
		assertEquals(before, course.get().verbose());
	}
}