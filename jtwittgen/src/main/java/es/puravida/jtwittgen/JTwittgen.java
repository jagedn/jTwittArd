package es.puravida.jtwittgen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class JTwittgen {
	public static void main(String args[]) throws Exception {
		
		Twitter twitter = TwitterFactory.getSingleton();

		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out
					.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out.print("Enter the PIN or just hit enter.[PIN]:");
			String pin = br.readLine();
			try {
				if (pin.length() > 0) {
					accessToken = twitter
							.getOAuthAccessToken(requestToken, pin);
				} else {
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if (401 == te.getStatusCode()) {
					System.out.println("Unable to get the access token.");
				} else {
					te.printStackTrace();
				}
			}
		}
		// persist to the accessToken for future reference.
		System.out.println(" Id " + twitter.verifyCredentials().getId());
		System.out.println(" AccessToken " + accessToken.getToken());
		System.out
				.println(" AccessTokenSecret " + accessToken.getTokenSecret());

		File f = new File(".");
		System.out.println(f.getAbsolutePath());

		FileWriter fw = new FileWriter("../OAuthKeys.java");
		PrintWriter pw = new PrintWriter(fw);
		pw.println("package es.puravida.jtwittard;");
		pw.println("public class OAuthKeys {");
		pw.println("public static final String oauthConsumerKey = \""+ "aqui tu consumer key" +"\";");
		pw.println("public static final String oauthConsumerSecret = \""+ "aqui tu consumer secret" +"\";");
		pw.println("public static final String oauthAccessToken = \""+ accessToken.getToken() +"\";");
		pw.println("public static final String oauthAccessTokenSecret = \""+ accessToken.getTokenSecret() +"\";");
		pw.println("}");

				
		Properties prp = new Properties();
		//prp.put("oauth.consumerKey", consumerKey);
		//prp.put("oauth.consumerSecret", consumerSecret);
		prp.put("oauth.accessToken", accessToken.getToken());
		prp.put("oauth.accessTokenSecret", accessToken.getTokenSecret());
		//prp.store(new FileWriter("../resources/kk.properties"), "");
		for(Map.Entry<Object, Object> entry : prp.entrySet()){
			System.out.println(entry.getKey()+"="+entry.getValue());
		}
		System.exit(0);
	}

}
