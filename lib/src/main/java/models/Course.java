package models;
import java.util.Optional;
import java.util.stream.Collectors;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

public class Course {
	private static final String COURSE_REGEX = "[A-Z]{2,4} [0-9]{3}[A-Z]? [A-Za-z0-9]{3}",
			JAVASCRIPT_TURNED_OFF_SEARCH_TEXT = "requires that you have Javascript enabled",
			MISSING_COURSE_SEARCH_TEXT = "no longer offered",
			URL = "https://courses.students.ubc.ca/cs/main?pname=subjarea&tname=subjareas&req=5";
	
	private final String dept, course, section;

	private int totalSeatsRemaining, generalSeatsRemaining, restrictedSeatsRemaining, totalRegistered;

	private Course(final String dept, final String course, final String section) {
		this.dept = dept;
		this.course = course;
		this.section = section;
	}

	/**
	 * Retrieves the course with the given identifiers from
	 * <link>courses.students.ubc.ca</link>.
	 * 
	 * @param dept Department code
	 * @param course Course code
	 * @param section Full section number
	 * @return An optional containing either the requested course, or nothing if
	 *         such a course could not be found.
	 */
	public static Optional<Course> of(final String dept, final String course, final String section) {
		final Course c = new Course(dept, course, section);
		if(c.update()) return Optional.of(c);
		return Optional.empty();
	}
	/**
	 * Reads course identifiers in the format <em>DEPT COURSE SECTION</em>, matching
	 * the following regex: <code>[A-Z]{2,4} [0-9]{3}[A-Z]? [A-Za-z0-9]{3}</code>
	 * 
	 * @param str String containing the department code, course code, and section
	 *            number
	 * @return An Optional containing either the course associated with the input
	 *         string, or nothing if such a course could not be found.
	 */
	public static Optional<Course> parse(String str) {
		str = str.trim().toUpperCase();
		final var arr = str.split(" ");
		
		if(str.matches(Course.COURSE_REGEX)) return Course.of(arr[0], arr[1], arr[2]);
		return Optional.empty();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof Course)) return false;
		final Course other = (Course) obj;
		if(course == null) {
			if(other.course != null) return false;
		} else if(!course.equals(other.course)) return false;
		if(dept == null) {
			if(other.dept != null) return false;
		} else if(!dept.equals(other.dept)) return false;
		if(generalSeatsRemaining != other.generalSeatsRemaining) return false;
		if(restrictedSeatsRemaining != other.restrictedSeatsRemaining) return false;
		if(section == null) {
			if(other.section != null) return false;
		} else if(!section.equals(other.section)) return false;
		if(totalRegistered != other.totalRegistered) return false;
		if(totalSeatsRemaining != other.totalSeatsRemaining) return false;
		return true;
	}
	public String getCourse() {
		return course;
	}
	public String getDept() {
		return dept;
	}
	public int getGeneralSeatsRemaining() {
		return generalSeatsRemaining;
	}
	public int getRestrictedSeatsRemaining() {
		return restrictedSeatsRemaining;
	}
	public String getSection() {
		return section;
	}
	public int getTotalRegistered() {
		return totalRegistered;
	}
	public int getTotalSeatsRemaining() {
		return totalSeatsRemaining;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((course == null) ? 0 : course.hashCode());
		result = prime * result + ((dept == null) ? 0 : dept.hashCode());
		result = prime * result + generalSeatsRemaining;
		result = prime * result + restrictedSeatsRemaining;
		result = prime * result + ((section == null) ? 0 : section.hashCode());
		result = prime * result + totalRegistered;
		result = prime * result + totalSeatsRemaining;
		return result;
	}
	public Course setGeneralSeatsRemaining(final int generalSeatsRemaining) throws InvalidSeatCountException {
		if(generalSeatsRemaining < 0) throw new InvalidSeatCountException("Cannot have less than 0 general seats remaining...");
		this.generalSeatsRemaining = generalSeatsRemaining;
		return this;
	}
	public Course setRestrictedSeatsRemaining(final int restrictedSeatsRemaining) throws InvalidSeatCountException {
		if(restrictedSeatsRemaining < 0) throw new InvalidSeatCountException("Cannot have less than 0 restricted seats remaining...");
		this.restrictedSeatsRemaining = restrictedSeatsRemaining;
		return this;
	}
	public Course setTotalRegistered(final int totalRegistered) throws InvalidSeatCountException {
		if(totalRegistered < 0) throw new InvalidSeatCountException("Cannot have fewer than 0 people ...");
		this.totalRegistered = totalRegistered;
		return this;
	}
	public Course setTotalSeatsRemaining(final int totalSeatsRemaining) throws InvalidSeatCountException {
		if(totalSeatsRemaining < 0) throw new InvalidSeatCountException("Cannot have less than 0 total seats remaining...");
		this.totalSeatsRemaining = totalSeatsRemaining;
		return this;
	}
	@Override
	public String toString() {
		return String.format("%s %s %s", dept, course, section);
	}
	/**
	 * Attempts to update the course with the current information available on the
	 * UBC website.
	 * 
	 * @return <code>true</code> if the course was updated, <code>false</code>
	 *         otherwise.
	 */
	public boolean update() {
		try(final WebClient wc = new WebClient(BrowserVersion.FIREFOX_60)) {
			final HtmlPage coursePage = wc.getPage(String.format("%s&dept=%s&course=%s&section=%s", Course.URL, dept, course, section));
			final var exists = !(coursePage.getBody().getTextContent().contains(Course.MISSING_COURSE_SEARCH_TEXT)
					|| coursePage.getBody().getTextContent().contains(Course.JAVASCRIPT_TURNED_OFF_SEARCH_TEXT));
			
			if(exists) {
				final HtmlTable table = (HtmlTable) coursePage.querySelector("table.\\'table");
				final var tableValues = table.getRows().stream()
						.skip(1)
						.map(row -> Integer.parseInt(row.getCells().get(row.getCells().size() - 1).getTextContent()))
						.collect(Collectors.toList());
				
				final var iter = tableValues.iterator();
				this.setTotalSeatsRemaining(iter.next())
					.setTotalRegistered(iter.next())
					.setGeneralSeatsRemaining(iter.next())
					.setRestrictedSeatsRemaining(iter.next());
				
				return true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * @return The verbose printout of this object.
	 */
	public String verbose() {
		return String.format(
			"%s\n\tTotal Seats Remaining: %d\n\tRegistered: %d\n\tGeneral Seats Remaining: %d\n\tRestricted Seats Remaining: %d",
			this.toString(), totalSeatsRemaining, totalRegistered, generalSeatsRemaining, restrictedSeatsRemaining
		);
	}
}