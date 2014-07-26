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

public class UserDaoTest
{
    public static FakeApplication app;

    @BeforeClass
    public static void setUp()
    {
        final HashMap<String,String> settings = new HashMap<String, String>();
        // TODO: Set "db.default.*" to a different test database, preferably in-memory.
        settings.put("evolutionplugin", "disabled");
        app = Helpers.fakeApplication(settings);
        Helpers.start(app);
    }

    @AfterClass
    public static void tearDown()
    {
        Helpers.stop(app);
    }

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
        FacebookUser facebookUser =
            Ebean.find(FacebookUser.class)
            .where().and(
                Expr.eq("idFacebook", mockUserFb.getId()),
                Expr.eq("isDeleted", false))
            .findUnique();
        if (facebookUser != null)
        {
            facebookUser.isDeleted = true;
            Ebean.save(facebookUser);
        }
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
