package serum.dao;

import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import play.db.ebean.*;

import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.Mockito.*;
import play.test.*;

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
        User user = UserDao.createUpdateUserFromFacebookInfo(mockUserFb);
        assertNotNull(user);
        // FacebookUser should be populated
        assertNotNull(user.facebookUser);
        assertEquals(mockUserFb.getId(), user.facebookUser.idFacebook);
        // Now try to create/update it again. This should just retrieve the existing one.
        // Thus, it should have the same auth token.
        User user2 = UserDao.createUpdateUserFromFacebookInfo(mockUserFb);
        assertNotNull(user2);
        assertEquals(user.userAuthToken.token, user2.userAuthToken.token);
        // Test the get token method returns the same token
        assertNotNull(UserDao.getUserAuthTokenByToken(user.userAuthToken.token));
    }

    @Test
    public void testGetUserByToken()
    throws Exception
    {
        GraphAPI.User mockUserFb = FacebookUserDaoTest.getFreshMockUserFb();
        // Create in DB
        User user = UserDao.createUpdateUserFromFacebookInfo(mockUserFb);
        assertNotNull(user);
        assertNotNull(user.userAuthToken);
        // Create a user for this user's Facebook friend too
        User userOfFriend = UserDao.createUpdateUserFromFacebookInfo(mockUserFb.getFriends().get(0));
        // Update friends in DB
        FacebookUserDao.createUpdateFacebookUserFriends(user.facebookUser, mockUserFb);
        // Look up user info by the same auth token
        User user2 = UserDao.getUserByAuthToken(user.userAuthToken.token);
        assertNotNull(user2);
        assertEquals(user.id, user2.id);
        assertEquals(user.userAuthToken.token, user2.userAuthToken.token);
        assertNotNull(user2.facebookUser);
        assertNotNull(user2.getFriends());
        assertTrue(user2.getFriends().size() > 0);
        assertTrue(user2.getFriends().contains(userOfFriend));
    }
}
