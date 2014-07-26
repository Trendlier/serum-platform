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
        // Create mock Facebook user
        Facebook.User mockUserFb = mock(Facebook.User.class);
        when(mockUserFb.getId()).thenReturn("123456");
        when(mockUserFb.getAccessToken()).thenReturn("abcdef");
        when(mockUserFb.getName()).thenReturn("Abc Def");
        // First, make sure the Facebook user is deleted from our database
        Ebean.createUpdate(
            FacebookUser.class,
            "update FacebookUser " +
            "set isDeleted = true " +
            "where idFacebook = :idFacebook ")
            .set("idFacebook", mockUserFb.getId())
            .execute();
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
}
