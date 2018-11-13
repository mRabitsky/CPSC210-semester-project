package notifier;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import models.Course;

public abstract class Notifier implements Subscriber<List<Boolean>> {
	protected final List<Course> courses;
	
	private Subscription subscription;
	
	protected Notifier(List<Course> courses) {
		this.courses = courses;
	}
	
	protected abstract void doNotify(List<Course> courses);
	
	@Override
	public void onComplete() {
		subscription.cancel();
		// TODO: verify if this is the end of the program
	}
	@Override
	public void onNext(List<Boolean> item) {
		doNotify(IntStream
				.range(0, Math.min(item.size(), courses.size()))
				.mapToObj(i -> item.get(i) ? courses.get(i) : null)
				.filter(c -> c != null)
				.collect(Collectors.toList()));
		subscription.request(1);
	}
	@Override
	public void onSubscribe(Subscription subscription) {
		(this.subscription = subscription).request(1);
	}
}
