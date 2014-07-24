package serum.controller;

import java.util.*;

import play.*;
import play.mvc.*;
import play.libs.F.*;
import static play.libs.Json.*;

import com.fasterxml.jackson.databind.JsonNode;

import serum.util.Facebook;

import serum.rest.LoginRequest;
import serum.rest.LoginResponse;

import serum.model.User;
import serum.model.UserAuthToken;

import serum.dao.UserDao;

public class LoginController extends Controller {
    /**
     * Content-Type: application/json
     * INPUT: facebook login credentials
     * OUTPUT: user auth token
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result login()
    throws Exception
    {
        LoginResponse response = null;
        Facebook.User userFb = null;
        UserAuthToken userAuthToken = null;
        User user = null;

        JsonNode json = request().body().asJson();
        LoginRequest request = fromJson(json, LoginRequest.class);

        if (request.userAuthToken == null)
        {
            if (request.facebookId != null && request.facebookAccessToken != null)
            {
                try
                {
                    userFb = Facebook.checkUserInfoFromFacebook(request.facebookId, request.facebookAccessToken);
                }
                catch (Facebook.AuthenticationException e)
                {
                    response = new LoginResponse(false, e.getMessage());
                    return badRequest(toJson(response));
                }
                user = UserDao.getUserFromFacebookInfo(userFb);
                userAuthToken = user.userAuthToken;
            }
            else
            {
                response = new LoginResponse(false, "No auth token provided: expected Facebook login.");
                return badRequest(toJson(response));
            }
        }
        else
        {
            userAuthToken = UserDao.getUserAuthTokenByToken(request.userAuthToken);
            if (userAuthToken == null)
            {
                response = new LoginResponse(false, "Auth token does not exist. Log in again.");
                return badRequest(toJson(response));
            }
            user = userAuthToken.user;
        }

        response = new LoginResponse(true, null, userAuthToken.token);
        return ok(toJson(response));
    }
}
