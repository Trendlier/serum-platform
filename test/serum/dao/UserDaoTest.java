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
    public void testGetUserFromFacebookInfo()
    throws Exception
    {
        GraphAPI.User mockUserFb = FacebookUserDaoTest.getFreshMockUserFb();
        // Create in DB
        User user = UserDao.getUserFromFacebookInfo(mockUserFb);
        assertNotNull(user);
        // FacebookUser should be populated
        assertNotNull(user.facebookUser);
        assertEquals(mockUserFb.getId(), user.facebookUser.idFacebook);
        // Now fetch it again. It should have the same auth token.
        User user2 = UserDao.getUserFromFacebookInfo(mockUserFb);
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
        User user = UserDao.getUserFromFacebookInfo(mockUserFb);
        assertNotNull(user);
        assertNotNull(user.userAuthToken);
        // Look up user info by the same auth token
        User user2 = UserDao.getUserByAuthToken(user.userAuthToken.token);
        assertNotNull(user2);
        assertEquals(user.id, user2.id);
        assertEquals(user.userAuthToken.token, user2.userAuthToken.token);
        assertNotNull(user2.facebookUser);
        assertNotNull(user2.facebookUser.friends);
        assertTrue(user2.facebookUser.friends.size() > 0);
    }
}
