package gui;
import java.util.Optional;
import main.Client;

public class GUIClient extends Client {
	public GUIClient(Optional<String> path) {
		if(path.isPresent()) super.readFile(path.get());
	}
	
	@Override
	public void start() {
		System.out.println("Starting GUI client...");
	}
}