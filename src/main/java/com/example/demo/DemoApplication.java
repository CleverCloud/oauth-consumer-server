package com.example.demo;

import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

	private OAuth1AuthorizationFlow authFlow;

	@RequestMapping(method = RequestMethod.GET, path = "/")
	public String startDance(@RequestParam("consumerKey") String consumerKey,@RequestParam("consumerSecret") String consumerSecret, HttpServletResponse httpServletResponse) {
		System.out.println(appURL);
		ConsumerCredentials consumerCredentials = new ConsumerCredentials(consumerKey, consumerSecret);
        Client client = ClientBuilder.newBuilder().build();
		authFlow = OAuth1ClientSupport.builder(consumerCredentials)
				.authorizationFlow(
						"https://api.clever-cloud.com/v2/oauth/request_token_query",
						"https://api.clever-cloud.com/v2/oauth/access_token_query",
						"https://api.clever-cloud.com/v2/oauth/authorize")
				.client(client)
				.callbackUri(appURL+"callback").build();

		final String authorizationUri = authFlow.start();
		return "redirect:"+authorizationUri;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/callback")
	public @ResponseBody
	String danceCallback(@RequestParam("oauth_token") String oauth_token, @RequestParam("oauth_verifier") String oauth_verifier, @RequestParam("user") String user) {
		AccessToken accessToken = authFlow.finish(oauth_verifier);
		return "Your token : " + accessToken.getToken() + "\nYour token secret : " + accessToken.getAccessTokenSecret();
	}


}

