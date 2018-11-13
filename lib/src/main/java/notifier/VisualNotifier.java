package notifier;

import java.util.List;
import models.Course;

public class VisualNotifier extends Notifier {
	public VisualNotifier(List<Course> courses) {
		super(courses);
	}
	
	@Override
	protected void doNotify(List<Course> courses) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onError(Throwable throwable) {
		// TODO Auto-generated method stub
	}
}
