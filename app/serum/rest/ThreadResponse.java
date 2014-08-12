package serum.rest;

import java.util.*;

public class ThreadResponse extends Response
{
    public String threadId;
    public Integer numberOfInvitedUsers;
    public User userOwner;
    public String title;
    public Long createdTimestamp;
    public String imageUrl;
    public Integer imageWidth;
    public Integer imageHeight;
    public List<ThreadMessage> messages;

    public static class User
    {
        public String userId;
        public String name;
        public String pictureUrl;
    }

    public static class ThreadMessage
    {
        public String threadMessageId;
        public ThreadUser threadUser;
        public String text;
        public Long createdTimestamp;

        public static class ThreadUser
        {
            public String threadUserId;
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
