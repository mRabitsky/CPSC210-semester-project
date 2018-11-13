package notifier;

import java.util.List;
import models.Course;

public class CLINotifier extends Notifier {
	public CLINotifier(List<Course> courses) {
		super(courses);
	}
	
	@Override
	protected void doNotify(List<Course> courses) {
		System.out.println(System.currentTimeMillis());
		courses.forEach(c -> System.out.println("There is a space open in " + c));
	}
	@Override
	public void onError(Throwable throwable) {
		// TODO Auto-generated method stub
	}
}
