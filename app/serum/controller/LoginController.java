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

        try
        {
            JsonNode json = request().body().asJson();
            LoginRequest request = fromJson(json, LoginRequest.class);

            if (request.userAuthToken == null)
            {
                if (request.facebookId != null && request.facebookAccessToken != null)
                {
                    try
                    {
                        Facebook facebook = Facebook.getInstance(request.facebookAccessToken);
                        userFb = facebook.checkUserInfoFromFacebook(request.facebookId, request.facebookAccessToken);
                        facebook.pullMyPicture(userFb);
                        facebook.pullMyFriends(userFb);
                    }
                    catch (Facebook.AuthenticationException e)
                    {
                        Logger.error(
                            "Error authenticating " + request.facebookId + " with " +
                            request.facebookAccessToken + " with Facebook", e);
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
            }
        }
        catch (Exception e)
        {
            Logger.error("Error logging user in", e);
            response = new LoginResponse(false, "Unexpected error");
            return internalServerError(toJson(response));
        }

        response = new LoginResponse(true, null, userAuthToken.token);
        return ok(toJson(response));
    }
}
