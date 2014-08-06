package serum.dao;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.Mockito.*;
import play.test.*;

import play.db.jpa.*;

import serum.model.*;

import serum.facebook.GraphAPITest;
import serum.facebook.GraphAPI;

public class FacebookUserDaoTest extends DaoTest
{
    protected GraphAPI.User mockUserFb = null;

    public static GraphAPI.User getFreshMockUserFb()
    {
        GraphAPI.User mockUserFb = GraphAPITest.getMockUserFb();
        // Make sure the Facebook user and their friends are deleted from our database
        FacebookUserDao.deleteFacebookUserFriendByIdFacebook(mockUserFb.getId());
        FacebookUserDao.deleteFacebookUserByIdFacebook(mockUserFb.getId());
        FacebookUserDao.deleteFacebookUserByIdFacebook(mockUserFb.getFriends().get(0).getId());
        return mockUserFb;
    }

    @Before
    public void setUp()
    {
        mockUserFb = getFreshMockUserFb();
    }

    @Test
    public void testCreateUpdateFacebookUser()
    {
        // Create their info record
        FacebookUser facebookUser = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        assertEquals(mockUserFb.getId(), facebookUser.idFacebook);
        assertEquals(mockUserFb.getAccessToken(), facebookUser.accessToken);
        assertEquals(mockUserFb.getName(), facebookUser.name);
        assertEquals(mockUserFb.getPicture().getData().getUrl(), facebookUser.pictureUrl);

        // Another call to this method should yield the exact same record
        FacebookUser facebookUser2 = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        assertEquals(facebookUser.id, facebookUser2.id);
        assertEquals(mockUserFb.getId(), facebookUser2.idFacebook);
        assertEquals(mockUserFb.getAccessToken(), facebookUser2.accessToken);
        assertEquals(mockUserFb.getName(), facebookUser2.name);
        assertEquals(mockUserFb.getPicture().getData().getUrl(), facebookUser2.pictureUrl);
    }

    @Test
    public void testCreateUpdateFacebookUserRemoved()
    {
        // Create their info record
        FacebookUser facebookUser = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        assertEquals(mockUserFb.getId(), facebookUser.idFacebook);
        assertEquals(mockUserFb.getAccessToken(), facebookUser.accessToken);
        assertEquals(mockUserFb.getName(), facebookUser.name);
        assertEquals(mockUserFb.getPicture().getData().getUrl(), facebookUser.pictureUrl);

        // Remove the record, which actually just sets a field to true
        FacebookUserDao.removeFacebookUserById(mockUserFb.getId());

        // Another call to this method should yield the exact same record, but different Id
        FacebookUser facebookUser2 = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        assertNotSame(facebookUser.id, facebookUser2.id);
        assertEquals(mockUserFb.getId(), facebookUser2.idFacebook);
        assertEquals(mockUserFb.getAccessToken(), facebookUser2.accessToken);
        assertEquals(mockUserFb.getName(), facebookUser2.name);
        assertEquals(mockUserFb.getPicture().getData().getUrl(), facebookUser2.pictureUrl);
    }

    @Test
    public void testCreateUpdateFacebookUserNullUpdate()
    {
        // Create their info record
        FacebookUser facebookUser = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        assertEquals(mockUserFb.getId(), facebookUser.idFacebook);
        assertEquals(mockUserFb.getAccessToken(), facebookUser.accessToken);
        assertEquals(mockUserFb.getName(), facebookUser.name);
        assertEquals(mockUserFb.getPicture().getData().getUrl(), facebookUser.pictureUrl);

        // Set properties to null.
        GraphAPI.User mockUserFb2 = mock(GraphAPI.User.class);
        String originalId = mockUserFb.getId();
        when(mockUserFb2.getId()).thenReturn(originalId);
        when(mockUserFb2.getAccessToken()).thenReturn(null);
        when(mockUserFb2.getName()).thenReturn(null);
        when(mockUserFb2.getPicture()).thenReturn(null);

        // Another call to this method should yield the original data
        FacebookUser facebookUser2 = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        assertEquals(facebookUser.id, facebookUser2.id);
        assertEquals(mockUserFb.getId(), facebookUser2.idFacebook);
        assertEquals(mockUserFb.getAccessToken(), facebookUser2.accessToken);
        assertEquals(mockUserFb.getName(), facebookUser2.name);
        assertEquals(mockUserFb.getPicture().getData().getUrl(), facebookUser2.pictureUrl);
    }

    @Test
    public void testCreateUpdateFacebookUserFriends()
    {
        // Create their info record
        FacebookUser facebookUser = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        // Now create their friends.
        FacebookUserDao.createUpdateFacebookUserFriends(facebookUser, mockUserFb);
        // Friend should have been created.
        JPA.em().flush();
        JPA.em().refresh(facebookUser);
        List<FacebookUser> friendFacebookUsers = facebookUser.getFriendFacebookUsers();
        assertEquals(1, friendFacebookUsers.size());
        // Assert that the data we expect about facebook friends are stored
        assertEquals(mockUserFb.getFriends().get(0).getId(), friendFacebookUsers.get(0).idFacebook);
        assertEquals(mockUserFb.getFriends().get(0).getName(), friendFacebookUsers.get(0).name);
        assertEquals(
            mockUserFb.getFriends().get(0).getPicture().getData().getUrl(),
            friendFacebookUsers.get(0).pictureUrl);

        // Let's make some friends. :)
        GraphAPI.User mockFriendUserFb = mock(GraphAPI.User.class);
        when(mockFriendUserFb.getId()).thenReturn("123462");
        when(mockFriendUserFb.getName()).thenReturn("Mno Pqr");
        mockUserFb.getFriends().add(mockFriendUserFb);
        // Now run the method again.
        FacebookUserDao.createUpdateFacebookUserFriends(facebookUser, mockUserFb);
        // Friend should have been created. Existing one should remain.
        Set<String> expectedFriendIds = new HashSet<String>();
        expectedFriendIds.add(mockUserFb.getFriends().get(0).getId());
        expectedFriendIds.add(mockUserFb.getFriends().get(1).getId());
        JPA.em().flush();
        JPA.em().refresh(facebookUser);
        friendFacebookUsers = facebookUser.getFriendFacebookUsers();
        assertEquals(2, friendFacebookUsers.size());
        assertTrue(expectedFriendIds.contains(friendFacebookUsers.get(0).idFacebook));
        assertTrue(expectedFriendIds.contains(friendFacebookUsers.get(1).idFacebook));

        // Let's lose some friends. :(
        mockUserFb.getFriends().remove(mockFriendUserFb);
        // Now run the method again.
        FacebookUserDao.createUpdateFacebookUserFriends(facebookUser, mockUserFb);
        // Friend should have been removed. Existing one should remain.
        JPA.em().flush();
        JPA.em().refresh(facebookUser);
        friendFacebookUsers = facebookUser.getFriendFacebookUsers();
        assertEquals(1, friendFacebookUsers.size());
        assertEquals(mockUserFb.getFriends().get(0).getId(), friendFacebookUsers.get(0).idFacebook);
    }

    @Test
    public void testCreateUpdateFacebookUserFriendsNull()
    {
        GraphAPI.User mockUserFb2 = mock(GraphAPI.User.class);
        String originalId = mockUserFb.getId();
        when(mockUserFb2.getId()).thenReturn(originalId);
        when(mockUserFb2.getFriends()).thenReturn(null);
        // Now try to create their friends.
        FacebookUserDao.createUpdateFacebookUserFriends(
            new FacebookUser(mockUserFb.getId(), mockUserFb.getName()),
            mockUserFb2);
        // TODO: assert existing friends were not touched.
    }
}
