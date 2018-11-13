package main;
import java.time.Duration;
import java.util.Optional;
import cli.CLIClient;
import gui.GUIClient;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
	name = "course-checking-buddy",
	description = "Course-Checking Buddy is a program that periodically checks the UBC courselist for open sections of a given course (or courses), and then notifies the user, in their preferred manner, when an opening is detected.",
	mixinStandardHelpOptions = true,
	version = "Course-Checking Buddy: v0.9.0"
) public class Runner {
	@Option(names = {"-d", "--duration"}, description = "Delay between each time the program checks.")
	private Duration duration;
	@Option(names = {"-f", "--file"}, description = "Use a file as input instead of entering courses on the command line.")
	private String file;
	@Option(names = {"-g", "--graphical"}, description = "Run this program with a GUI; otherwise, it normally runs headless.") 
	private boolean graphical = false;
	
	private Client client;
	
	public void initGraphicalClient() {
		this.client = new GUIClient(Optional.ofNullable(file));
	}
	public void initCommandLineClient() {
		this.client = new CLIClient(Optional.ofNullable(duration), Optional.ofNullable(file));
	}
	public void startClient() {
		this.client.start();
	}
	
	public static void main(String[] args) {
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		
		final Runner app = new Runner();
		final CommandLine cmd = new CommandLine(app);
		cmd.parse(args);
		
		if(cmd.isUsageHelpRequested()) {
			cmd.usage(System.out);
			return;
		} else if(cmd.isVersionHelpRequested()) {
			cmd.printVersionHelp(System.out);
			return;
		} else if(cmd.getParseResult().hasMatchedOption('g')) {
			app.initGraphicalClient();
		} else {
			app.initCommandLineClient();
		}
		
		app.startClient();
	}
}