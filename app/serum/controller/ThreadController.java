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
                    ThreadUser threadUser = thread.getThreadUserFromUser(user);
                    List<ThreadMessage> unreadMessages = ThreadMessageDao.getUnreadMessages(threadUser);
                    User userOwner = thread.getUserOwner();
                    List<User> invitedUsers = thread.getInvitedUsers();
                    ThreadResponse response = new ThreadResponse(true, null);
                    response.threadId = thread.getIdHash();
                    response.numberOfInvitedUsers = invitedUsers.size();
                    response.title = thread.title;
                    response.imageUrl = thread.imageUrl;
                    response.unreadMessages = unreadMessages.size();
                    response.createdTimestamp = thread.createdUTC.getTimeInMillis()/1000;
                    response.userOwner = new ThreadResponse.User();
                    response.userOwner.userId = userOwner.getIdHash();
                    if (userOwner.facebookUser != null)
                    {
                        response.userOwner.name = userOwner.facebookUser.name;
                        response.userOwner.pictureUrl = userOwner.facebookUser.pictureUrl;
                    }
                    Long latestMessageTimestamp = null;
                    response.messages = new ArrayList<ThreadResponse.ThreadMessage>();
                    for (ThreadMessage threadMessage: thread.getThreadMessages())
                    {
                        ThreadResponse.ThreadMessage threadMessageResponse = new ThreadResponse.ThreadMessage();
                        threadMessageResponse.threadMessageId = threadMessage.getIdHash();
                        threadMessageResponse.text = threadMessage.text;
                        threadMessageResponse.isRead = !unreadMessages.contains(threadMessage);
                        threadMessageResponse.createdTimestamp = threadMessage.createdUTC.getTimeInMillis()/1000;
                        if (latestMessageTimestamp == null ||
                            latestMessageTimestamp < threadMessageResponse.createdTimestamp)
                        {
                            latestMessageTimestamp = threadMessageResponse.createdTimestamp;
                        }
                        ThreadResponse.ThreadMessage.ThreadUser threadUserResponse =
                            new ThreadResponse.ThreadMessage.ThreadUser();
                        threadUserResponse.threadUserId = threadMessage.threadUser.getIdHash();
                        threadUserResponse.isOwner = threadMessage.threadUser.isOwner;
                        threadUserResponse.iconUrl = threadMessage.threadUser.iconUrl;
                        threadUserResponse.colourRGB = new Integer[] {
                            threadMessage.threadUser.colourRed,
                            threadMessage.threadUser.colourGreen,
                            threadMessage.threadUser.colourBlue
                        };
                        threadMessageResponse.threadUser = threadUserResponse;
                        response.messages.add(threadMessageResponse);
                    }
                    response.lastEditTimestamp = latestMessageTimestamp;
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
                ThreadsResponse response = new ThreadsResponse(true, null);
                response.threads = new ArrayList<ThreadsResponse.ThreadResponse>();
                for (ThreadModel thread: user.getOpenThreads())
                {
                    ThreadUser threadUser = thread.getThreadUserFromUser(user);
                    List<ThreadMessage> unreadMessages = ThreadMessageDao.getUnreadMessages(threadUser);
                    User userOwner = thread.getUserOwner();
                    List<User> invitedUsers = thread.getInvitedUsers();
                    ThreadsResponse.ThreadResponse threadResponse = new ThreadsResponse.ThreadResponse();
                    threadResponse.threadId = thread.getIdHash();
                    threadResponse.numberOfInvitedUsers = invitedUsers.size();
                    threadResponse.title = thread.title;
                    threadResponse.imageUrl = thread.imageUrl;
                    threadResponse.unreadMessages = unreadMessages.size();
                    threadResponse.createdTimestamp = thread.createdUTC.getTimeInMillis()/1000;
                    threadResponse.userOwner = new ThreadsResponse.ThreadResponse.User();
                    threadResponse.userOwner.userId = userOwner.getIdHash();
                    if (userOwner.facebookUser != null)
                    {
                        threadResponse.userOwner.name = userOwner.facebookUser.name;
                        threadResponse.userOwner.pictureUrl = userOwner.facebookUser.pictureUrl;
                    }
                    Long latestMessageTimestamp = null;
                    threadResponse.messages = new ArrayList<ThreadsResponse.ThreadResponse.ThreadMessage>();
                    for (ThreadMessage threadMessage: thread.getThreadMessages())
                    {
                        ThreadsResponse.ThreadResponse.ThreadMessage threadMessageResponse =
                            new ThreadsResponse.ThreadResponse.ThreadMessage();
                        threadMessageResponse.threadMessageId = threadMessage.getIdHash();
                        threadMessageResponse.text = threadMessage.text;
                        threadMessageResponse.isRead = !unreadMessages.contains(threadMessage);
                        threadMessageResponse.createdTimestamp = threadMessage.createdUTC.getTimeInMillis()/1000;
                        if (latestMessageTimestamp == null ||
                            latestMessageTimestamp < threadMessageResponse.createdTimestamp)
                        {
                            latestMessageTimestamp = threadMessageResponse.createdTimestamp;
                        }
                        ThreadsResponse.ThreadResponse.ThreadMessage.ThreadUser threadUserResponse =
                            new ThreadsResponse.ThreadResponse.ThreadMessage.ThreadUser();
                        threadUserResponse.threadUserId = threadMessage.threadUser.getIdHash();
                        threadUserResponse.isOwner = threadMessage.threadUser.isOwner;
                        threadUserResponse.iconUrl = threadMessage.threadUser.iconUrl;
                        threadUserResponse.colourRGB = new Integer[] {
                            threadMessage.threadUser.colourRed,
                            threadMessage.threadUser.colourGreen,
                            threadMessage.threadUser.colourBlue
                        };
                        threadMessageResponse.threadUser = threadUserResponse;
                        threadResponse.messages.add(threadMessageResponse);
                    }
                    threadResponse.lastEditTimestamp = latestMessageTimestamp;
                    response.threads.add(threadResponse);
                }
                return ok(toJson(response));
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
    public static Result removeThreadUser(String threadUserIdHash, String userAuthToken)
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
    @BodyParser.Of(BodyParser.Json.class)
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

    /**
     * Content-Type: application/json
     * INPUT:
     * OUTPUT:
     */
    @Transactional
    public static Result markThreadMessageAsRead(String threadMessageIdHash, String userAuthToken)
    {
        try
        {
            User user = UserDao.getUserByAuthToken(userAuthToken);
            if (user != null)
            {
                ThreadMessage threadMessage =
                    ThreadMessageDao.getThreadMessageById(ThreadMessage.getIdFromHash(threadMessageIdHash));
                ThreadUser threadUser = threadMessage.threadUser.thread.getThreadUserFromUser(user);
                ThreadMessageDao.markThreadMessageAsRead(threadUser, threadMessage);
                return ok(toJson(new Response(true, null)));
            }
            else
            {
                String errorMessage = "Could not find auth token and/or user. Try logging in again.";
                Response response = new Response(false, errorMessage);
                return badRequest(toJson(response));
            }
        }
        catch (Exception e)
        {
            Logger.error("Error adding thread message", e);
            Response response = new Response(false, "Unexpected error");
            return internalServerError(toJson(response));
        }
    }
}
