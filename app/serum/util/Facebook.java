package serum.util;

import play.*;

import com.restfb.*;
import com.restfb.exception.*;

/**
 * Wrapper class for Facebook API
 */
public class Facebook
{
    public static class User extends com.restfb.types.User
    {
    }

    public static class AuthenticationException extends Exception
    {
        public AuthenticationException(String message)
        {
            super(message);
        }
    }

    public static User checkUserInfoFromFacebook(
        String facebookId,
        String facebookAccessToken
    )
    throws AuthenticationException
    {
        User userFb = null;

        // Get info about the user from Facebook
        FacebookClient fb = new DefaultFacebookClient(facebookAccessToken);
        try {
            userFb = fb.fetchObject("me", User.class);
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

        return userFb;
    }
}
