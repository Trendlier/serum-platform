package serum.util;

import java.util.*;

import play.*;

import com.restfb.*;
import com.restfb.exception.*;

/**
 * Wrapper class for Facebook API
 */
public class Facebook
{
    protected FacebookClient facebookClient;

    public static class User extends com.restfb.types.User
    {
        protected List<User> friends;
        protected String accessToken;

        public List<User> getFriends()
        {
            return friends;
        }

        public void setFriends(List<User> friends)
        {
            this.friends = friends;
        }

        public String getAccessToken()
        {
            return accessToken;
        }

        public void setAccessToken(String accessToken)
        {
            this.accessToken = accessToken;
        }
    }

    public static class AuthenticationException extends Exception
    {
        public AuthenticationException(String message)
        {
            super(message);
        }
    }

    public Facebook(FacebookClient facebookClient)
    {
        this.facebookClient = facebookClient;
    }

    public static Facebook getInstance(String facebookAccessToken)
    {
        return new Facebook(new DefaultFacebookClient(facebookAccessToken));
    }

    public User checkUserInfoFromFacebook(
        String facebookId,
        String facebookAccessToken
    )
    throws AuthenticationException
    {
        User userFb = null;

        // Get info about the user from Facebook
        try
        {
            userFb = facebookClient.fetchObject("me", User.class);
            if (!facebookId.equals(userFb.getId()))
            {
                throw new AuthenticationException("Facebook user ID and access token do not match");
            }
        }
        catch (FacebookOAuthException e)
        {
            Logger.error("Error authenticating " + facebookId + " with Facebook", e);
            throw new AuthenticationException(e.getMessage());
        }

        // Set access token
        userFb.setAccessToken(facebookAccessToken);

        return userFb;
    }

    public void pullFriends(User userFb)
    throws AuthenticationException
    {
        userFb.setFriends(getFriends());
    }

    public List<User> getFriends()
    throws AuthenticationException
    {
        List<User> friends = null;
        try
        {
            Connection<User> connectionFriends = facebookClient.fetchConnection("me/friends", User.class);
            friends = connectionFriends.getData();
        }
        catch (FacebookOAuthException e)
        {
            Logger.error("Error authenticating with Facebook", e);
            throw new AuthenticationException(e.getMessage());
        }
        return friends;
    }
}
