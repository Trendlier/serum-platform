package serum.rest;

import java.util.*;

public class ThreadResponse extends Response
{
    public String id;
    public Integer numberOfInvitedUsers;
    public User userOwner;
    public String title;
    public Long createdTimestamp;
    public String imageUrl;
    public Integer imageWidth;
    public Integer imageHeight;
    public List<Response> responses;

    public static class User
    {
        public String id;
        public String name;
        public String pictureUrl;
    }

    public static class Response
    {
        public String id;
        public ThreadUser threadUser;
        public String text;
        public Long createdTimestamp;

        public static class ThreadUser
        {
            public String id;
            public Boolean isOwner;
            public String iconUrl;
            public Integer[] colourRGB;
        }
    }

    public ThreadResponse(Boolean success, String message)
    {
        super(success, message);
    }
}
