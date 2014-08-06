package serum.rest;

import java.util.*;

public class UserFriendsResponse
{
    public Boolean success;
    public String message;

    public List<Friend> friends;

    public static class Friend
    {
        public String idHash;
        public String name;
        public String gender;
        public String pictureUrl;
    }

    public UserFriendsResponse(Boolean success, String message)
    {
        this.success = success;
        this.message = message;
    }
}
