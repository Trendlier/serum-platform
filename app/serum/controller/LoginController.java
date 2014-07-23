package serum.controller;

import play.*;
import play.mvc.*;
import play.libs.F.*;
import static play.libs.Json.*;

import com.fasterxml.jackson.databind.JsonNode;

import com.restfb.*;
import com.restfb.types.*;

import serum.rest.LoginUsingFacebookRequest;

public class LoginController extends Controller {
    /**
     * Content-Type: application/json
     * INPUT: facebook login credentials
     * OUTPUT: user auth token
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result loginUsingFacebook()
    {
        // Get the credentials from request
        JsonNode json = request().body().asJson();
        LoginUsingFacebookRequest request = fromJson(json, LoginUsingFacebookRequest.class);

        // Get info from Facebook
        FacebookClient fb = new DefaultFacebookClient(request.accessToken);
        User user = fb.fetchObject(request.facebookId, User.class);
        System.out.println(user.getFirstName());
        System.out.println(user.getLastName());

        return ok(request.facebookId + " " + request.accessToken);
    }
}
