package cli;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import main.Client;
import models.Course;
import notifier.CLINotifier;
import watcher.Watcher;

public class CLIClient extends Client {
	private Duration duration;
	
	/**
	 * Creates a new command-line interface (CLI) client for the course availability
	 * checker.
	 * 
	 * @param dur Optional: duration between queries
	 * @param file Optional: file from which to read a course-list (line separated)
	 */
	public CLIClient(Optional<Duration> dur, Optional<String> file) {
		duration = dur.orElse(null);
		if(file.isPresent()) super.readFile(file.get());
	}
	
	@Override
	public void start() {
		System.out.println("Starting CLI client...");
		final var in = new Scanner(System.in);
		if(duration == null) duration = _queryForDuration(in);
		if(courses.isEmpty()) _queryForCourses(in);
		
		final Watcher watcher = new Watcher(courses, duration);
		watcher.subscribe(new CLINotifier(courses));
		
		// WARNING: `watcher` is never closed. Not sure when to do that, I guess we need
		// to break for user input at some point to allow the user to kill the system in
		// some way other than `CTRL-C`. Real talk tho, I'm totally cool with just
		// letting it be `CTRL-C` to kill, this shouldn't be a leaked resource after the
		// JVM shutdowns (JVM should close all closeables on exit I'm pretty sure).
		
		// Okay, here's my idea: register a shutdownHook that closes the watcher.
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				in.close();
				watcher.close();
				System.out.println("Watcher closed");
			}
		});
	}
	
	private void _queryForCourses(final Scanner in) { // WARNING: this could overflow the stack if they keep inputting garbage.
		System.out.println("Enter the courses (comma separated) you would like to watch for:");
		final String input = in.nextLine().trim();
		if(input.equalsIgnoreCase("done")) return;
		
		final var cs = Arrays.stream(input.split(", "))
				.map(s -> Course.parse(s.trim()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
		if(cs.isEmpty()) {
			System.out.println("Sorry, I didn't quite catch that...\n");
			_queryForCourses(in);
		} else {
			courses.addAll(cs);
			System.out.println("You added:");
			System.out.println(cs.stream().map(c -> c.toString()).collect(Collectors.joining("\n", "\t", "")));
			
			System.out.println("\nWould you like to add more courses (Y/n)?");
			final String confirmation = in.nextLine().trim();
			if(confirmation.equalsIgnoreCase("Y") || confirmation.equalsIgnoreCase("Yes")) _queryForCourses(in);
		}
	}
	private Duration _queryForDuration(final Scanner in) {
		System.out.println("Enter the duration you would like the system to query for:");
		final String input = in.nextLine().trim();
		try {
			return Duration.parse(input);
		} catch(Exception e) {
			System.out.println("Sorry, I didn't quite catch that...\n");
			return _queryForDuration(in);
		}
	}
}