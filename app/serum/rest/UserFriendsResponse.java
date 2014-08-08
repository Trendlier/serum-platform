package serum.rest;

import java.util.*;

public class UserFriendsResponse extends Response
{
    public List<Friend> friends;

    public static class Friend
    {
        public String id;
        public String name;
        public String gender;
        public String pictureUrl;
    }

    public UserFriendsResponse(Boolean success, String message)
    {
        super(success, message);
    }
}
