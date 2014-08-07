package serum.rest;

import java.util.*;

public class UserFriendsToInviteResponse extends Response
{
    public List<Friend> friends;

    public static class Friend
    {
        public String idFacebook;
        public String name;
        public String gender;
        public String pictureUrl;
    }

    public UserFriendsToInviteResponse(Boolean success, String message)
    {
        super(success, message);
    }
}
