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
import serum.model.FacebookUser;

import serum.rest.UserResponse;
import serum.rest.UserFriendsResponse;
import serum.rest.UserFriendsToInviteResponse;

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
                userResponse.gender = user.facebookUser.gender;
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
            Logger.error("Error pulling user friend info from DB", e);
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
        try
        {
            User user = UserDao.getUserByAuthToken(userAuthToken);
            if (user != null)
            {
                UserFriendsResponse userFriendsResponse = new UserFriendsResponse(true, null);
                userFriendsResponse.friends = new ArrayList<UserFriendsResponse.Friend>();
                for (User userOfFriend: user.getFriends())
                {
                    UserFriendsResponse.Friend responseFriend = new UserFriendsResponse.Friend();
                    responseFriend.idHash = userOfFriend.getIdHash();
                    responseFriend.name = userOfFriend.facebookUser.name;
                    responseFriend.gender = userOfFriend.facebookUser.gender;
                    responseFriend.pictureUrl = userOfFriend.facebookUser.pictureUrl;
                    userFriendsResponse.friends.add(responseFriend);
                }
                return ok(toJson(userFriendsResponse));
            }
            else
            {
                UserFriendsResponse userFriendsResponse =
                    new UserFriendsResponse(false, "Could not find auth token and/or user. Try logging in again.");
                return badRequest(toJson(userFriendsResponse));
            }
        }
        catch (Exception e)
        {
            Logger.error("Error pulling user friend info from DB", e);
            UserFriendsResponse userFriendsResponse = new UserFriendsResponse(false, "Unexpected error");
            return internalServerError(toJson(userFriendsResponse));
        }
    }

    /**
     * Content-Type: application/json
     * INPUT: user auth token
     * OUTPUT: user friend info
     */
    @Transactional
    public static Result myFriendsToInvite(String userAuthToken)
    {
        try
        {
            User user = UserDao.getUserByAuthToken(userAuthToken);
            if (user != null)
            {
                UserFriendsToInviteResponse userFriendsToInviteResponse = new UserFriendsToInviteResponse(true, null);
                userFriendsToInviteResponse.friends = new ArrayList<UserFriendsToInviteResponse.Friend>();
                for (FacebookUser facebookUserOfFriend: user.facebookUser.getFriendFacebookUsers())
                {
                    if (facebookUserOfFriend.user == null)
                    {
                        UserFriendsToInviteResponse.Friend responseFriend = new UserFriendsToInviteResponse.Friend();
                        responseFriend.idFacebook = facebookUserOfFriend.idFacebook;
                        responseFriend.name = facebookUserOfFriend.name;
                        responseFriend.gender = facebookUserOfFriend.gender;
                        responseFriend.pictureUrl = facebookUserOfFriend.pictureUrl;
                        userFriendsToInviteResponse.friends.add(responseFriend);
                    }
                }
                return ok(toJson(userFriendsToInviteResponse));
            }
            else
            {
                UserFriendsToInviteResponse userFriendsToInviteResponse =
                    new UserFriendsToInviteResponse(false,
                        "Could not find auth token and/or user. Try logging in again.");
                return badRequest(toJson(userFriendsToInviteResponse));
            }
        }
        catch (Exception e)
        {
            Logger.error("Error pulling user FB invite friend info from DB", e);
            UserFriendsToInviteResponse userFriendsToInviteResponse =
                new UserFriendsToInviteResponse(false, "Unexpected error");
            return internalServerError(toJson(userFriendsToInviteResponse));
        }
    }
}
