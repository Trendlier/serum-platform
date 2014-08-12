package serum.controller;

import java.util.*;

import play.*;
import play.mvc.*;
import play.libs.F.*;
import static play.libs.Json.*;

import com.fasterxml.jackson.databind.JsonNode;

import play.db.jpa.*;

import serum.dao.UserDao;
import serum.dao.ThreadDao;
import serum.dao.ThreadUserDao;
import serum.dao.ThreadMessageDao;

import serum.model.User;
import serum.model.ThreadModel;
import serum.model.ThreadUser;
import serum.model.ThreadMessage;

import serum.rest.AddThreadMessageRequest;
import serum.rest.CreateThreadRequest;

import serum.rest.Response;
import serum.rest.CreateThreadResponse;
import serum.rest.AddThreadImageResponse;
import serum.rest.ThreadResponse;
import serum.rest.ThreadsResponse;
import serum.rest.AddThreadMessageResponse;

import serum.validation.ThreadActionValidator;

public class ThreadController extends Controller
{
    /**
     * Content-Type: application/json
     * INPUT: user auth token
     * OUTPUT: 
     */
    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result createThread()
    {
        try
        {
            JsonNode json = request().body().asJson();
            CreateThreadRequest request = fromJson(json, CreateThreadRequest.class);
            User user = UserDao.getUserByAuthToken(request.userAuthToken);
            if (user != null)
            {
                ThreadModel thread = ThreadDao.createThread(request.title);
                List<User> invitedUsers = UserDao.getUsersByIdHash(request.invitedUserIds);
                ThreadUserDao.createThreadUsers(thread, user, invitedUsers);
                CreateThreadResponse response = new CreateThreadResponse(true, null, thread.getIdHash());
                return ok(toJson(response));
            }
            else
            {
                CreateThreadResponse response =
                    new CreateThreadResponse(false, "Could not find auth token and/or user. Try logging in again.");
                return badRequest(toJson(response));
            }
        }
        catch (Exception e)
        {
            Logger.error("Error creating thread", e);
            CreateThreadResponse response = new CreateThreadResponse(false, "Unexpected error");
            return internalServerError(toJson(response));
        }
    }

    /**
     * Content-Type: application/json
     * INPUT: thread ID hash, user auth token
     * OUTPUT: thread info
     */
    @Transactional
    public static Result addThreadImage(String threadIdHash, String userAuthToken)
    {
        try
        {
            User user = UserDao.getUserByAuthToken(userAuthToken);
            if (user != null)
            {
                ThreadModel thread = ThreadDao.getThreadById(ThreadModel.getIdFromHash(threadIdHash));
                if (ThreadActionValidator.hasPermissionToAddImage(thread, user))
                {
                    byte[] imageBytes = request().body().asRaw().asBytes();
                }
                else
                {
                    AddThreadImageResponse response =
                        new AddThreadImageResponse(
                            false,
                            "You do not have permission to add an image to this thread.");
                    return badRequest(toJson(response));
                }
                return ok("TODO");
            }
            else
            {
                AddThreadImageResponse response =
                    new AddThreadImageResponse(
                        false,
                        "Could not find auth token and/or user. Try logging in again.");
                return badRequest(toJson(response));
            }
        }
        catch (Exception e)
        {
            Logger.error("Error adding image to thread question", e);
            AddThreadImageResponse response = new AddThreadImageResponse(false, "Unexpected error");
            return internalServerError(toJson(response));
        }
    }

    /**
     * Content-Type: application/json
     * INPUT: thread ID hash, user auth token
     * OUTPUT: thread info
     */
    @Transactional
    public static Result getThread(String threadIdHash, String userAuthToken)
    {
        try
        {
            User user = UserDao.getUserByAuthToken(userAuthToken);
            if (user != null)
            {
                ThreadModel thread = ThreadDao.getThreadById(ThreadModel.getIdFromHash(threadIdHash));
                if (ThreadActionValidator.hasPermissionToSee(thread, user))
                {
                    User userOwner = thread.getUserOwner();
                    List<User> invitedUsers = thread.getInvitedUsers();
                    ThreadResponse response = new ThreadResponse(true, null);
                    response.threadId = thread.getIdHash();
                    response.numberOfInvitedUsers = invitedUsers.size();
                    response.title = thread.title;
                    response.imageUrl = thread.imageUrl;
                    response.createdTimestamp = thread.createdUTC.getTimeInMillis()/1000;
                    response.userOwner = new ThreadResponse.User();
                    response.userOwner.userId = userOwner.getIdHash();
                    if (userOwner.facebookUser != null)
                    {
                        response.userOwner.name = userOwner.facebookUser.name;
                        response.userOwner.pictureUrl = userOwner.facebookUser.pictureUrl;
                    }
                    // TODO: add responses/messages
                    return ok(toJson(response));
                }
                else
                {
                    ThreadResponse response =
                        new ThreadResponse(false, "You don't have permission to see this thread.");
                    return badRequest(toJson(response));
                }
            }
            else
            {
                ThreadResponse response =
                    new ThreadResponse(false, "Could not find auth token and/or user. Try logging in again.");
                return badRequest(toJson(response));
            }
        }
        catch (Exception e)
        {
            Logger.error("Error getting thread", e);
            ThreadResponse response = new ThreadResponse(false, "Unexpected error");
            return internalServerError(toJson(response));
        }
    }

    /**
     * Content-Type: application/json
     * INPUT: user auth token
     * OUTPUT: list of threads visible to me
     */
    @Transactional
    public static Result getThreads(String userAuthToken)
    {
        try
        {
            User user = UserDao.getUserByAuthToken(userAuthToken);
            if (user != null)
            {
                return ok("TODO");
            }
            else
            {
                ThreadsResponse response =
                    new ThreadsResponse(false, "Could not find auth token and/or user. Try logging in again.");
                return badRequest(toJson(response));
            }
        }
        catch (Exception e)
        {
            Logger.error("Error getting threads", e);
            ThreadsResponse response = new ThreadsResponse(false, "Unexpected error");
            return internalServerError(toJson(response));
        }
    }

    /**
     * Content-Type: application/json
     * INPUT: thread ID hash, user auth token
     * OUTPUT: 
     */
    @Transactional
    public static Result removeThread(String threadIdHash, String userAuthToken)
    {
        try
        {
            User user = UserDao.getUserByAuthToken(userAuthToken);
            if (user != null)
            {
                ThreadModel thread = ThreadDao.getThreadById(ThreadModel.getIdFromHash(threadIdHash));
                if (ThreadActionValidator.hasPermissionToRemove(thread, user))
                {
                    ThreadDao.removeThread(thread);
                    return ok(toJson(new Response(true, null)));
                }
                else
                {
                    Response response = new Response(false, "You don't have permission to remove this thread.");
                    return badRequest(toJson(response));
                }
            }
            else
            {
                Response response =
                    new Response(false, "Could not find auth token and/or user. Try logging in again.");
                return badRequest(toJson(response));
            }
        }
        catch (Exception e)
        {
            Logger.error("Error removing thread", e);
            Response response = new Response(false, "Unexpected error");
            return internalServerError(toJson(response));
        }
    }

    /**
     * Content-Type: application/json
     * INPUT: thread ID hash, thread user ID hash, user auth token
     * OUTPUT: 
     */
    @Transactional
    public static Result removeThreadUser(String threadIdHash, String threadUserIdHash, String userAuthToken)
    {
        try
        {
            User user = UserDao.getUserByAuthToken(userAuthToken);
            if (user != null)
            {
                return ok("TODO");
            }
            else
            {
                Response response =
                    new Response(false, "Could not find auth token and/or user. Try logging in again.");
                return badRequest(toJson(response));
            }
        }
        catch (Exception e)
        {
            Logger.error("Error removing thread user", e);
            Response response = new Response(false, "Unexpected error");
            return internalServerError(toJson(response));
        }
    }

    /**
     * Content-Type: application/json
     * INPUT:
     * OUTPUT:
     */
    @Transactional
    public static Result addThreadMessage()
    {
        try
        {
            JsonNode json = request().body().asJson();
            AddThreadMessageRequest request = fromJson(json, AddThreadMessageRequest.class);
            User user = UserDao.getUserByAuthToken(request.userAuthToken);
            if (user != null)
            {
                ThreadModel thread = ThreadDao.getThreadById(ThreadModel.getIdFromHash(request.threadId));
                if (ThreadActionValidator.hasPermissionToAddMessage(thread, user))
                {
                    ThreadUser threadUser = thread.getThreadUserFromUser(user);
                    ThreadMessage m = ThreadMessageDao.createThreadMessage(threadUser, request.message);
                    return ok(toJson(new AddThreadMessageResponse(true, null, m.getIdHash())));
                }
                else
                {
                    String errorMessage = "You don't have permission to add messages to this thread.";
                    AddThreadMessageResponse response = new AddThreadMessageResponse(false, errorMessage);
                    return badRequest(toJson(response));
                }
            }
            else
            {
                String errorMessage = "Could not find auth token and/or user. Try logging in again.";
                AddThreadMessageResponse response = new AddThreadMessageResponse(false, errorMessage);
                return badRequest(toJson(response));
            }
        }
        catch (Exception e)
        {
            Logger.error("Error adding thread message", e);
            AddThreadMessageResponse response = new AddThreadMessageResponse(false, "Unexpected error");
            return internalServerError(toJson(response));
        }
    }
}
