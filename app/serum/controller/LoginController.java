package serum.controller;

import play.*;
import play.mvc.*;
import play.libs.F.*;
import static play.libs.Json.*;

import com.fasterxml.jackson.databind.JsonNode;

import com.restfb.*;

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
        //FacebookClient fb = new FacebookClient(request.accessToken);

        return ok(request.facebookId + " " + request.accessToken);
    }
}
