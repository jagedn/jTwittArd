package es.puravida.jtwittard;

import java.util.Calendar;

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
import twitter4j.conf.ConfigurationBuilder;

public class FindTwitter {

	long[] last;

	String[] search = new String[0];

	FindTwitterListener listener;

	Twitter twitter;

	public FindTwitter(String[] search, FindTwitterListener listener) {
		this.search = search;
		this.listener = listener;
		this.last = new long[search.length];
		for (int i = 0; i < this.last.length; i++) {
			this.last[i] = Calendar.getInstance().getTimeInMillis();
		}

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(OAuthKeys.oauthConsumerKey)
				.setOAuthConsumerSecret(OAuthKeys.oauthConsumerSecret)
				.setOAuthAccessToken(OAuthKeys.oauthAccessToken)
				.setOAuthAccessTokenSecret(OAuthKeys.oauthAccessTokenSecret);

		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}

	void search() throws Exception {
		for (int i = 0; i < search.length; i++) {
			Query query = new Query(search[i]);
			QueryResult result = twitter.search(query);
			for (Status status : result.getTweets()) {
				if (last[i] < status.getCreatedAt().getTime()) {
					System.out.println("@" + status.getUser().getScreenName()
							+ ":" + status.getText());
					last[i] = status.getCreatedAt().getTime();
					listener.hashtagFounded(i, search[i], status.getUser()
							.getScreenName());
				}
			}
		}
	}

}
