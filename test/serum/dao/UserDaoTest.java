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
    protected Facebook.User getFreshMockUserFb()
    {
        // Create mock Facebook user
        Facebook.User mockUserFb = mock(Facebook.User.class);
        when(mockUserFb.getId()).thenReturn("123456");
        when(mockUserFb.getAccessToken()).thenReturn("abcdef");
        when(mockUserFb.getName()).thenReturn("Abc Def");
        // Make sure the Facebook user and their friends are deleted from our database
        Ebean.createNamedUpdate(FacebookUserFriend.class, "deleteByIdFacebook")
            .set("idFacebook", mockUserFb.getId())
            .execute();
        Ebean.createNamedUpdate(FacebookUser.class, "deleteByIdFacebook")
            .set("idFacebook", mockUserFb.getId())
            .execute();
        return mockUserFb;
    }

    @Test
    public void testGetUserFromFacebookInfo()
    throws Exception
    {
        Facebook.User mockUserFb = getFreshMockUserFb();
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
