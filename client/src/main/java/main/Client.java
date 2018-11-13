package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import models.Course;

public abstract class Client {
	protected final List<Course> courses = new LinkedList<>();
	
	/**
	 * Start this client.
	 */
	public abstract void start();
	
	/**
	 * Reads the file at the given path, treating each line as a course identifier
	 * that needs to be parsed. Assumes that the given path should be based off of
	 * the user's home directory.
	 * 
	 * @param path Path to a file containing line-separated course identifiers
	 */
	protected void readFile(String path) {
		final var file = new File(System.getProperty("user.home") + path);
		if(file.exists() && file.canRead()) {
			try(final BufferedReader in = new BufferedReader(new FileReader(file))) {
				courses.addAll(in.lines().map(Course::parse).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
				System.out.println(courses);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}