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

public class FacebookUserDaoTest extends DaoTest
{
    protected Facebook.User mockUserFb = null;

    @Before
    public void setUp()
    {
        // Create mock Facebook user
        mockUserFb = mock(Facebook.User.class);
        when(mockUserFb.getId()).thenReturn("123456");
        when(mockUserFb.getAccessToken()).thenReturn("abcdef");
        when(mockUserFb.getName()).thenReturn("Abc Def");
        // Create mock friends
        List<Facebook.User> mockFriends = new ArrayList<Facebook.User>();
        Facebook.User mockFriendUserFb = mock(Facebook.User.class);
        when(mockFriendUserFb.getId()).thenReturn("123459");
        mockFriends.add(mockFriendUserFb);
        when(mockUserFb.getFriends()).thenReturn(mockFriends);
        // Make sure the Facebook user and their friends are deleted from our database
        Ebean.createNamedUpdate(FacebookUserFriend.class, "deleteByIdFacebook")
            .set("idFacebook", mockUserFb.getId())
            .execute();
        Ebean.createNamedUpdate(FacebookUser.class, "deleteByIdFacebook")
            .set("idFacebook", mockUserFb.getId())
            .execute();
    }

    @Test
    public void testCreateUpdateFacebookUser()
    {
        // Create their info record
        FacebookUser facebookUser = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        assertEquals(mockUserFb.getId(), facebookUser.idFacebook);
        assertEquals(mockUserFb.getAccessToken(), facebookUser.accessToken);
        assertEquals(mockUserFb.getName(), facebookUser.name);
        // Another call to this method should yield the exact same record
        FacebookUser facebookUser2 = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        assertEquals(facebookUser.id, facebookUser2.id);
        assertEquals(mockUserFb.getId(), facebookUser.idFacebook);
        assertEquals(mockUserFb.getAccessToken(), facebookUser.accessToken);
        assertEquals(mockUserFb.getName(), facebookUser.name);
    }

    @Test
    public void testCreateUpdateFacebookUserRemoved()
    {
        // Create their info record
        FacebookUser facebookUser = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        assertEquals(mockUserFb.getId(), facebookUser.idFacebook);
        assertEquals(mockUserFb.getAccessToken(), facebookUser.accessToken);
        assertEquals(mockUserFb.getName(), facebookUser.name);
        // Remove the record, which actually just sets a field to true
        Ebean.createNamedUpdate(FacebookUser.class, "removeByIdFacebook")
            .set("idFacebook", mockUserFb.getId())
            .execute();
        // Another call to this method should yield the exact same record, but different Id
        FacebookUser facebookUser2 = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        assertNotSame(facebookUser.id, facebookUser2.id);
        assertEquals(mockUserFb.getId(), facebookUser.idFacebook);
        assertEquals(mockUserFb.getAccessToken(), facebookUser.accessToken);
        assertEquals(mockUserFb.getName(), facebookUser.name);
    }

    @Test
    public void testCreateUpdateFacebookUserFriends()
    {
    }
}
