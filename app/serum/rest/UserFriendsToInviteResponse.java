package serum.rest;

import java.util.*;

public class UserFriendsToInviteResponse
{
    public Boolean success;
    public String message;

    public List<Friend> friends;

    public static class Friend
    {
        public String idFacebook;
        public String name;
        public String pictureUrl;
    }

    public UserFriendsToInviteResponse(Boolean success, String message)
    {
        this.success = success;
        this.message = message;
    }
}
