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

import serum.model.User;
import serum.model.Thread;
import serum.model.ThreadUser;

import serum.rest.CreateThreadRequest;

import serum.rest.Response;
import serum.rest.CreateThreadResponse;
import serum.rest.AddThreadImageResponse;
import serum.rest.ThreadResponse;
import serum.rest.ThreadsResponse;

public class ThreadController extends Controller
{
    /**
     * Content-Type: application/json
     * INPUT: user auth token
     * OUTPUT: 
     */
    @Transactional
    @BodyParser.Of(BodyParser.Json.class)
    public static Result createThread(String userAuthToken)
    {
        try
        {
            JsonNode json = request().body().asJson();
            CreateThreadRequest request = fromJson(json, CreateThreadRequest.class);
            User user = UserDao.getUserByAuthToken(userAuthToken);
            if (user != null)
            {
                return ok("TODO");
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
                return ok("TODO");
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
            Logger.error("Error removing thread", e);
            Response response = new Response(false, "Unexpected error");
            return internalServerError(toJson(response));
        }
    }

    /**
     * Content-Type: application/json
     * INPUT: thread ID hash, user ID hash, user auth token
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
            Logger.error("Error removing thread", e);
            Response response = new Response(false, "Unexpected error");
            return internalServerError(toJson(response));
        }
    }
}
