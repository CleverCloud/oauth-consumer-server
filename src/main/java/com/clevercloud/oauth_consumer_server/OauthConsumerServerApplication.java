package com.clevercloud.oauth_consumer_server;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clevercloud.api.OauthApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class OauthConsumerServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OauthConsumerServerApplication.class, args);
	}

}

@Controller
class OAuthController {
	@Value("${app.url:}")
	String appURL;


  private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);
  
	private ConcurrentHashMap<String, OAuth10aService> services = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, OAuth1RequestToken> requestTokens = new ConcurrentHashMap<>();

	@RequestMapping(method = RequestMethod.GET, path = "/")
	public String startDance(@RequestParam("consumerKey") String consumerKey,
			@RequestParam("consumerSecret") String consumerSecret, HttpServletResponse httpServletResponse) {

		// Create an Oauth service provider using consumerKey and consumerSecret, and 
		// the Clever Cloud API definition for endpoints
		OAuth10aService service = new ServiceBuilder(consumerKey)
				.apiSecret(consumerSecret)
				.callback(appURL + "callback")
				.build(OauthApi.instance());

		// fetches a request token from the service provider and builds
		// a url based on AUTHORIZE_WEBSITE_URL and CALLBACK_URL to
		// which your app must now send the user
		try {
			logger.debug("Getting request token ...");
			OAuth1RequestToken requestToken = service.getRequestToken();

			services.put(requestToken.getToken(),service);
			requestTokens.put(requestToken.getToken(),requestToken);

			String url = service.getAuthorizationUrl(requestToken);
			logger.debug(String.format("URL : %s", url));
			return String.format("redirect:%s", url);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}
	}


	@RequestMapping(method = RequestMethod.GET,  path = "/callback")
	public @ResponseBody
	String danceCallback(@RequestParam("oauth_token") String oauth_token, @RequestParam("oauth_verifier") String oauth_verifier, @RequestParam("user") String user) {

		try {
			logger.debug(String.format("OAuth token : [%s]", oauth_token));
			logger.debug(String.format("OAuth verifier : [%s]", oauth_verifier));
			logger.debug(String.format("User : [%s]", user));
			
			OAuth10aService service = services.get(oauth_token);
			OAuth1RequestToken requestToken = requestTokens.get(oauth_token);
			OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauth_verifier);
			logger.debug("Access Token: " + accessToken);
			return "Your token : " + accessToken.getToken() + "\nYour token secret : " + accessToken.getTokenSecret();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}
	} 
}
