package serum.facebook;

import java.util.*;

import com.restfb.*;
import com.restfb.exception.*;

/**
 * Wrapper class for Facebook API
 */
public class GraphAPI
{
    protected FacebookClient facebookClient;

    // Object returned by Facebook API /user
    public static class User extends com.restfb.types.User
    {
        protected List<User> friends;
        protected String accessToken;

        @com.restfb.Facebook
        protected Picture picture;

        public static final String STANDARD_FIELDS = "id,name,picture,picture.width(100),picture.height(100)";

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

        public Set<String> getFriendIds()
        {
            Set<String> facebookFriendIds = new HashSet<String>();
            for (User userFbOfFriend: getFriends())
            {
                facebookFriendIds.add(userFbOfFriend.getId());
            }
            return facebookFriendIds;
        }
    }

    public static class AuthenticationException extends Exception
    {
        public AuthenticationException(String message)
        {
            super(message);
        }
    }

    public GraphAPI(FacebookClient facebookClient)
    {
        this.facebookClient = facebookClient;
    }

    public static GraphAPI getInstance(String facebookAccessToken)
    {
        return new GraphAPI(new DefaultFacebookClient(facebookAccessToken));
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
            userFb = facebookClient.fetchObject("me", User.class, Parameter.with("fields", User.STANDARD_FIELDS));
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

    public void pullMyFriends(User userFb)
    throws AuthenticationException
    {
        userFb.setFriends(getMyFriends());
    }

    protected List<User> getMyFriends()
    throws AuthenticationException
    {
        List<User> friends = null;
        try
        {
            Connection<User> connectionFriends =
                facebookClient.fetchConnection(
                    "me/friends", User.class, Parameter.with("fields", User.STANDARD_FIELDS));
            friends = connectionFriends.getData();
        }
        catch (FacebookOAuthException e)
        {
            throw new AuthenticationException(e.getMessage());
        }
        return friends;
    }
}
