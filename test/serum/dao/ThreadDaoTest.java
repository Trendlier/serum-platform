package serum.dao;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.Mockito.*;
import play.test.*;

import play.db.jpa.*;

import serum.rest.CreateThreadRequest;
import serum.model.*;

import serum.facebook.GraphAPI;

public class ThreadDaoTest extends DaoTest
{
    public static User getMockUser(GraphAPI.User mockUserFb)
    throws Exception
    {
        FacebookUser facebookUser = FacebookUserDao.createUpdateFacebookUser(mockUserFb);
        return UserDao.createUpdateUserFromFacebookInfo(facebookUser);
    }

    public static List<User> getMockInvitedUsers(GraphAPI.User mockUserFb)
    throws Exception
    {
        FacebookUser facebookUserOfFriend = FacebookUserDao.createUpdateFacebookUser(mockUserFb.getFriends().get(0));
        User invitedUser = UserDao.createUpdateUserFromFacebookInfo(facebookUserOfFriend);
        return Arrays.asList(new User[] {invitedUser});
    }

    @Test
    public void testCreateGetRemoveThreadAndThreadUsers()
    throws Exception
    {
        GraphAPI.User mockUserFb = FacebookUserDaoTest.getFreshMockUserFb();
        User userOwner = getMockUser(mockUserFb);
        List<User> invitedUsers = getMockInvitedUsers(mockUserFb);
        String title = "Should entities and rest objects use getters and setters, or just public fields?";
        ThreadModel thread = ThreadDao.createThread(title);
        ThreadUserDao.createThreadUsers(thread, userOwner, invitedUsers);
        JPA.em().flush();
        JPA.em().refresh(thread);

        thread = ThreadDao.getThreadById(thread.id);

        assertNotNull(thread);
        assertEquals(title, thread.title);
        assertEquals(userOwner, thread.getUserOwner());
        assertEquals(invitedUsers.size() + 1, thread.threadUsers.size());

        List<User> actualInvitedUsers = thread.getInvitedUsers();
        assertEquals(invitedUsers, actualInvitedUsers);

        ThreadDao.removeThread(thread);
        assertTrue(thread.isDeleted);
    }
}
