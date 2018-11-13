package watcher;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import models.Course;

public class Watcher extends SubmissionPublisher<List<Boolean>> {
	private final List<Course> courses;
	private final ScheduledExecutorService scheduler;
	private final ScheduledFuture<?> task;
	
	public Watcher(List<Course> courses, Duration d) {
		this.courses = courses;
		scheduler = new ScheduledThreadPoolExecutor(1);
		task = scheduler.scheduleAtFixedRate(() -> submit(next()), 0, d.getSeconds(), TimeUnit.SECONDS);
	}
	
	public void close() {
		task.cancel(false);
		scheduler.shutdown();
		super.close();
	}
	
	private List<Boolean> next() {
		return courses.stream().map(Course::update).collect(Collectors.toList());
	}
}
