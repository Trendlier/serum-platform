package serum.dao;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.Mockito.*;
import play.test.*;

import play.db.jpa.*;

import serum.model.*;

import serum.facebook.GraphAPI;

public class UserDaoTest extends DaoTest
{
    @Test
    public void testCreateUpdateUserFromFacebookInfo()
    throws Exception
    {
        GraphAPI.User mockUserFb = FacebookUserDaoTest.getFreshMockUserFb();
        // Create in DB
        FacebookUser facebookUser = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        User user = UserDao.createUpdateUserFromFacebookInfo(facebookUser);
        txnRule.getTransaction().commit();
        txnRule.getTransaction().begin();
        user = UserDao.getUserByAuthToken(user.userAuthToken.token);
        assertNotNull(user);
        // FacebookUser should be populated
        assertNotNull(user.facebookUser);
        assertEquals(mockUserFb.getId(), user.facebookUser.idFacebook);

        // Now try to create/update it again. This should just retrieve the existing one.
        // Thus, it should have the same auth token.
        facebookUser = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        User user2 = UserDao.createUpdateUserFromFacebookInfo(facebookUser);
        assertNotNull(user2);
        assertEquals(user.userAuthToken.token, user2.userAuthToken.token);

        // Test the get token method returns the same token
        assertNotNull(UserDao.getUserAuthTokenByToken(user.userAuthToken.token));
    }

    @Test
    public void testGetFriends()
    throws Exception
    {
        GraphAPI.User mockUserFb = FacebookUserDaoTest.getFreshMockUserFb();
        // Create in DB
        FacebookUser facebookUser = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        User user = UserDao.createUpdateUserFromFacebookInfo(facebookUser);
        assertNotNull(user);

        // Add Facebook friends
        FacebookUser facebookUserOfFriend = FacebookUserDao.createUpdateFacebookUser(mockUserFb.getFriends().get(0));
        User userOfFriend = UserDao.createUpdateUserFromFacebookInfo(facebookUserOfFriend);
        FacebookUserDao.createUpdateFacebookUserFriends(user.facebookUser, mockUserFb);

        // Get the friends
        txnRule.getTransaction().commit();
        txnRule.getTransaction().begin();
        user = UserDao.getUserByAuthToken(user.userAuthToken.token);
        List<User> friends = user.getFriends();
        assertNotNull(friends);
        assertTrue(friends.size() > 0);
        assertTrue(friends.contains(userOfFriend));
    }

    @Test
    public void testGetUsersByIds()
    throws Exception
    {
        // First create user and friend
        GraphAPI.User mockUserFb = FacebookUserDaoTest.getFreshMockUserFb();
        FacebookUser facebookUser = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        User user = UserDao.createUpdateUserFromFacebookInfo(facebookUser);
        FacebookUser facebookUserOfFriend = FacebookUserDao.createUpdateFacebookUser(mockUserFb.getFriends().get(0));
        User userOfFriend = UserDao.createUpdateUserFromFacebookInfo(facebookUserOfFriend);

        // Now pull them using the method
        List<Long> ids = Arrays.asList(new Long[] {user.id, userOfFriend.id});
        List<User> users = UserDao.getUsersByIds(ids);
        assertTrue(users.contains(user));
        assertTrue(users.contains(userOfFriend));
    }
}
