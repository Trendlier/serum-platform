package serum.controller;

import java.util.*;

import play.*;
import play.mvc.*;
import play.libs.F.*;
import static play.libs.Json.*;

import com.fasterxml.jackson.databind.JsonNode;

import play.db.jpa.*;

import serum.dao.UserDao;

import serum.model.User;

import serum.rest.RequestWithUserAuthToken;
import serum.rest.UserResponse;

public class UsersController extends Controller
{
    /**
     * Content-Type: application/json
     * INPUT: user auth token
     * OUTPUT: user info
     */
    @Transactional
    public static Result me(String userAuthToken)
    {
        try
        {
            User user = UserDao.getUserByAuthToken(userAuthToken);
            if (user != null)
            {
                UserResponse userResponse = new UserResponse(true, null);
                userResponse.idHash = user.getIdHash();
                userResponse.threadCapacity = user.threadCapacity;
                userResponse.name = user.facebookUser.name;
                userResponse.pictureUrl = user.facebookUser.pictureUrl;
                return ok(toJson(userResponse));
            }
            else
            {
                UserResponse userResponse =
                    new UserResponse(false, "Could not find auth token and/or user. Try logging in again.");
                return badRequest(toJson(userResponse));
            }
        }
        catch (Exception e)
        {
            Logger.error("Error pulling user info from DB: " + request().body().asJson(), e);
            UserResponse userResponse = new UserResponse(false, "Unexpected error");
            return internalServerError(toJson(userResponse));
        }
    }

    /**
     * Content-Type: application/json
     * INPUT: user auth token
     * OUTPUT: user friend info
     */
    @Transactional
    public static Result myFriends(String userAuthToken)
    {
        return null;
    }

    /**
     * Content-Type: application/json
     * INPUT: user auth token
     * OUTPUT: user friend info
     */
    @Transactional
    public static Result myFriendsToInvite(String userAuthToken)
    {
        return null;
    }
}
