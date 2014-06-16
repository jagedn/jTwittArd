package es.puravida.jtwittard;

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

	private final static String HASHTAG1 = "jagedn";
	private final static String HASHTAG2 = "uochrome";

	protected void start() {
		try {
			Properties prp = new Properties();
			prp.load(ClassLoader
					.getSystemResourceAsStream("jtwittard.properties"));
			for (Map.Entry<Object, Object> entry : prp.entrySet()) {
				if (System.getProperties().get(entry.getKey()) == null)
					System.getProperties()
							.put(entry.getKey(), entry.getValue());
			}

			Arduino arduino = new Arduino();
			if (!arduino.initialize()) {
				return;
			}

			List<String> list = new ArrayList<String>();
			list.add(HASHTAG1);
			list.add(HASHTAG2);
			for (int i = 1; i < 4; i++) {
				if (System.getProperty("hashtag" + i) == null) {
					break;
				}
				list.add("#"+System.getProperty("hashtag" + i));
			}
			String[] hashtags = list.toArray(new String[list.size()]);
			for(String s : hashtags){
				System.out.println("Searching for "+s);
			}

			final FindTwitter find = new FindTwitter(hashtags, arduino);

			final ScheduledExecutorService scheduledExecutorService = Executors
					.newScheduledThreadPool(5);

			final ScheduledFuture scheduledFuture = scheduledExecutorService
					.scheduleAtFixedRate(new Runnable() {
						public void run() {
							try {
								System.out.println("Searching!");
								find.search();
							} catch (Throwable e) {
								e.printStackTrace();
								scheduledExecutorService.shutdownNow();
							}
						}
					}, 1, 30, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
