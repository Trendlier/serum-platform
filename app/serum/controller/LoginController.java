package serum.controller;

import play.*;
import play.mvc.*;
import play.libs.F.*;
import static play.libs.Json.*;

import com.fasterxml.jackson.databind.JsonNode;

import com.restfb.*;
import com.restfb.exception.*;

import serum.rest.LoginUsingFacebookRequest;
import serum.rest.LoginUsingFacebookResponse;

public class LoginController extends Controller {
    public static class AuthenticationException extends Exception
    {
        public AuthenticationException(String message)
        {
            super(message);
        }
    }

    /**
     * Content-Type: application/json
     * INPUT: facebook login credentials
     * OUTPUT: user auth token
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result loginUsingFacebook()
    {
        LoginUsingFacebookResponse response = null;

        JsonNode json = request().body().asJson();
        LoginUsingFacebookRequest request = fromJson(json, LoginUsingFacebookRequest.class);

        if (request.userAuthToken == null)
        {
            if (request.facebookId != null && request.facebookAccessToken != null)
            {
                try
                {
                    com.restfb.types.User user = checkUserInfoFromFacebook(request);
                }
                catch (AuthenticationException e)
                {
                    response = new LoginUsingFacebookResponse(false, e.getMessage());
                    return badRequest(toJson(response));
                }
            }
            else
            {
                response = new LoginUsingFacebookResponse(false, "No auth token provided: expected Facebook login");
                return badRequest(toJson(response));
            }
        }

        response = new LoginUsingFacebookResponse(true, null, "TODO");
        return ok(toJson(response));
    }

    protected static com.restfb.types.User checkUserInfoFromFacebook(LoginUsingFacebookRequest request)
    throws AuthenticationException
    {
        com.restfb.types.User user = null;

        // Get info about the user from Facebook
        FacebookClient fb = new DefaultFacebookClient(request.facebookAccessToken);
        try {
            user = fb.fetchObject("me", com.restfb.types.User.class);
            if (!request.facebookId.equals(user.getId()))
            {
                throw new AuthenticationException("Liar");
            }
        }
        catch (FacebookOAuthException e)
        {
            Logger.error("Error authenticating " + request.facebookId + " with Facebook", e);
            throw new AuthenticationException(e.getMessage());
        }

        return user;
    }
}
