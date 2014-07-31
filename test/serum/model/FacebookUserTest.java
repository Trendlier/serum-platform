package serum.model;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class FacebookUserTest
{
    @Test
    public void testGetFacebookUserFriendMap()
    {
        FacebookUser facebookUser = new FacebookUser();
        FacebookUser facebookUserOfFriend = new FacebookUser();
        facebookUserOfFriend.idFacebook = "123";
        FacebookUserFriend facebookUserFriend = new FacebookUserFriend(facebookUser, facebookUserOfFriend);
        facebookUser.friends = new HashSet<FacebookUserFriend>();
        facebookUser.friends.add(facebookUserFriend);
        Map<String, FacebookUserFriend> facebookUserFriendMap = facebookUser.getFacebookUserFriendMap();
        assertEquals(1, facebookUserFriendMap.size());
        assertEquals(facebookUserFriend, facebookUserFriendMap.get(facebookUserOfFriend.idFacebook));
    }

    @Test
    public void testGetFacebookUserMapByIds()
    {
        FacebookUser facebookUser = new FacebookUser();
        facebookUser.idFacebook = "123";
        List<FacebookUser> facebookUserList = new ArrayList<FacebookUser>();
        facebookUserList.add(facebookUser);
        Map<String, FacebookUser> facebookUserMap = FacebookUser.getFacebookUserMap(facebookUserList);
        assertEquals(1, facebookUserMap.size());
        assertEquals(facebookUser, facebookUserMap.get(facebookUser.idFacebook));
    }
}
