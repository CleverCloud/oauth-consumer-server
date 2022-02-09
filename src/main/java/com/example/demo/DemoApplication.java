package com.example.demo;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		 SpringApplication.run(DemoApplication.class, args);
	}
}

@Controller
class IndexController {
	@Value("${app.url:}")
	String appURL;

	OAuthProvider provider;

	OAuthConsumer consumer;

	@RequestMapping(method = RequestMethod.GET, path = "/")
	public String startDance(@RequestParam("consumerKey") String consumerKey,@RequestParam("consumerSecret") String consumerSecret, HttpServletResponse httpServletResponse) {
		System.out.println(appURL);

		// create a consumer object and configure it with the access
		// token and token secret obtained from the service provider
		consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);


		// create a new service provider object and configure it with
		// the URLs which provide request tokens, access tokens, and
		// the URL to which users are sent in order to grant permission
		// to your application to access protected resources
		provider = new DefaultOAuthProvider(
			"https://api.clever-cloud.com/v2/oauth/request_token_query",
			"https://api.clever-cloud.com/v2/oauth/access_token_query",
			"https://api.clever-cloud.com/v2/oauth/authorize"
		);

		// fetches a request token from the service provider and builds
		// a url based on AUTHORIZE_WEBSITE_URL and CALLBACK_URL to
		// which your app must now send the user
		try {
			System.out.println("Getting request token ...");
			String url = provider.retrieveRequestToken(consumer, appURL + "/callback");
			System.out.println(String.format("URL : %s", url));
			return String.format("redirect:%s", url);
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/callback")
	public @ResponseBody
	String danceCallback(@RequestParam("oauth_token") String oauth_token, @RequestParam("oauth_verifier") String oauth_verifier, @RequestParam("user") String user) {
		try {
			System.out.println(String.format("OAuth token : [%s]", oauth_token));
			System.out.println(String.format("OAuth verifier : [%s]", oauth_verifier));
			System.out.println(String.format("User : [%s]", user));
			provider.retrieveAccessToken(consumer, oauth_verifier);
			return "Your token : " + consumer.getToken() + "\nYour token secret : " + consumer.getTokenSecret();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return e.getMessage();
		}
	}


}

