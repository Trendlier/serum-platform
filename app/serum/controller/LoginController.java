package serum.controller;

import java.util.*;

import play.*;
import play.mvc.*;
import play.libs.F.*;
import play.libs.Akka;
import static play.libs.Json.*;

import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.Duration;

import com.fasterxml.jackson.databind.JsonNode;

import play.db.jpa.*;

import serum.facebook.GraphAPI;

import serum.rest.LoginRequest;
import serum.rest.LoginResponse;

import serum.model.User;
import serum.model.UserAuthToken;
import serum.model.FacebookUser;

import serum.dao.UserDao;
import serum.dao.FacebookUserDao;

public class LoginController extends Controller {
    /**
     * Content-Type: application/json
     * INPUT: facebook login credentials
     * OUTPUT: user auth token
     */
    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result login()
    {
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
                        final GraphAPI graphApi = GraphAPI.getInstance(request.facebookAccessToken);
                        final GraphAPI.User userFb =
                            graphApi.checkUserInfoFromFacebook(request.facebookId, request.facebookAccessToken);
                        FacebookUser facebookUser = FacebookUserDao.createUpdateFacebookUser(userFb);
                        final User user = UserDao.createUpdateUserFromFacebookInfo(facebookUser);

                        // Pull Facebook friends data asynchronously
                        Akka.system().scheduler().scheduleOnce(
                            Duration.create(10, TimeUnit.MILLISECONDS),
                            () -> {
                                try
                                {
                                    graphApi.pullMyFriends(userFb);
                                    JPA.withTransaction(new Callback0() {
                                        public void invoke()
                                        {
                                            FacebookUser facebookUser = FacebookUserDao.getById(user.facebookUser.id);
                                            FacebookUserDao.createUpdateFacebookUserFriends(facebookUser, userFb);
                                        }
                                    });
                                }
                                catch (GraphAPI.AuthenticationException e)
                                {
                                    Logger.error(
                                        "Error authenticating " + request.facebookId + " with " +
                                        request.facebookAccessToken + " with Facebook", e);
                                }
                            },
                            Akka.system().dispatcher()
                        );

                        LoginResponse response = new LoginResponse(true, null, user.userAuthToken.token);
                        return ok(toJson(response));
                    }
                    catch (GraphAPI.AuthenticationException e)
                    {
                        Logger.error(
                            "Error authenticating " + request.facebookId + " with " +
                            request.facebookAccessToken + " with Facebook", e);
                        LoginResponse response = new LoginResponse(false, e.getMessage());
                        return badRequest(toJson(response));
                    }
                }
                else
                {
                    LoginResponse response =
                        new LoginResponse(false, "No auth token provided: expected Facebook login.");
                    return badRequest(toJson(response));
                }
            }
            else
            {
                UserAuthToken userAuthToken = UserDao.getUserAuthTokenByToken(request.userAuthToken);
                if (userAuthToken == null)
                {
                    LoginResponse response = new LoginResponse(false, "Auth token does not exist. Log in again.");
                    return badRequest(toJson(response));
                }
                else
                {
                    LoginResponse response = new LoginResponse(true, null, userAuthToken.token);
                    return ok(toJson(response));
                }
            }
        }
        catch (Exception e)
        {
            Logger.error("Error logging user in", e);
            LoginResponse response = new LoginResponse(false, "Unexpected error");
            return internalServerError(toJson(response));
        }
    }
}
