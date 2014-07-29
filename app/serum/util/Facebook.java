package serum.util;

import java.util.*;

import com.restfb.*;
import com.restfb.exception.*;

/**
 * Wrapper class for Facebook API
 */
public class Facebook
{
    protected FacebookClient facebookClient;

    // Object returned by Facebook API /user
    public static class User extends com.restfb.types.User
    {
        protected List<User> friends;
        protected String accessToken;
        protected Picture picture;

        // Object returned by Facebook API /user/picture
        public static class Picture
        {
            @com.restfb.Facebook
            protected Data data;

            public static class Data
            {
                @com.restfb.Facebook
                protected String url;

                public Data()
                {
                }

                public Data(String url)
                {
                    this.url = url;
                }

                public String getUrl()
                {
                    return url;
                }

                public void setUrl(String url)
                {
                    this.url = url;
                }
            }

            public Picture()
            {
            }

            public Picture(String url)
            {
                this.data = new Data(url);
            }

            public Data getData()
            {
                return data;
            }

            public void setData(Data data)
            {
                this.data = data;
            }
        }

        public Picture getPicture()
        {
            return picture;
        }

        public void setPicture(Picture picture)
        {
            this.picture = picture;
        }

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

    protected List<User> getFriends()
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
            throw new AuthenticationException(e.getMessage());
        }
        return friends;
    }

    public void pullPicture(User userFb)
    throws AuthenticationException
    {
        userFb.setPicture(getPicture());
    }

    protected User.Picture getPicture()
    throws AuthenticationException
    {
        User.Picture picture = null;
        try
        {
            Parameter redirectFalse = Parameter.with("redirect", false);
            picture = facebookClient.fetchObject("me/picture", User.Picture.class, redirectFalse);
        }
        catch (FacebookOAuthException e)
        {
            throw new AuthenticationException(e.getMessage());
        }
        return picture;
    }
}
