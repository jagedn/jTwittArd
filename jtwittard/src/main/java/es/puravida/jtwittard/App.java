package es.puravida.jtwittard;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/**
 * Hello world!
 * 
 */
public class App {

	public static void main(String[] args) {
		App theApp = new App();
		theApp.start();
	}

	private ScheduledExecutorService scheduledExecutorService;

	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutorService;
	}

	protected void start() {

		scheduledExecutorService = Executors.newScheduledThreadPool(1);

		Properties prp = new Properties();
		try {
			prp.load(ClassLoader
					.getSystemResourceAsStream("/jtwittard.properties"));
		} catch (Exception e1) {
			try {
				prp.load(new FileInputStream("./jtwittard.properties"));
			} catch (Exception e2) {
			}
		}
		for (Map.Entry<Object, Object> entry : prp.entrySet()) {
			if (System.getProperties().get(entry.getKey()) == null)
				System.getProperties().put(entry.getKey(), entry.getValue());
		}

		Arduino arduino = new Arduino();
		initializeArduino(arduino);

	}

	protected void initializeArduino(final Arduino arduino) {
		if (arduino.initialize()) {
			initializeFinder(arduino);
			return;
		}
		scheduledExecutorService.schedule(new Runnable() {
			public void run() {
				initializeArduino(arduino);
			}
		}, 30, TimeUnit.SECONDS);
	}

	protected void initializeFinder(final Arduino arduino) {
		List<String> list = new ArrayList<String>();
		for (int i = 1; i < 4; i++) {
			if (System.getProperty("hashtag" + i) == null) {
				break;
			}
			if (System.getProperty("hashtag" + i).trim().length() == 0) {
				continue;
			}
			list.add("#" + System.getProperty("hashtag" + i).trim());
		}
		String[] hashtags = list.toArray(new String[list.size()]);
		for (String s : hashtags) {
			System.out.println("Searching for " + s);
		}

		final FindTwitter find = new FindTwitter(hashtags, arduino);

		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					System.out.println("Searching!");
					if (!find.search()){
						System.out.println("Restarting the App!");
						main(new String[]{});
						scheduledExecutorService.shutdown();					
					}
				} catch (Exception e) {
					System.out.println(e);
					scheduledExecutorService.shutdownNow();
				}
			}
		}, 1, 30, TimeUnit.SECONDS);
	}
}
