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

import serum.util.Facebook;

public class UserDaoTest extends DaoTest
{
    @Test
    public void testGetUserFromFacebookInfo()
    throws Exception
    {
        Facebook.User mockUserFb = FacebookUserDaoTest.getFreshMockUserFb();
        // Create in DB
        User user = UserDao.getUserFromFacebookInfo(mockUserFb);
        assertNotNull(user);
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
        Facebook.User mockUserFb = FacebookUserDaoTest.getFreshMockUserFb();
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
    }
}
