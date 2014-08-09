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

    public static List<User> getMockFriends(GraphAPI.User mockUserFb)
    throws Exception
    {
        FacebookUser facebookUserOfFriend = FacebookUserDao.createUpdateFacebookUser(mockUserFb.getFriends().get(0));
        User userOfFriend = UserDao.createUpdateUserFromFacebookInfo(facebookUserOfFriend);
        return Arrays.asList(new User[] {userOfFriend});
    }

    @Test
    public void testCreateGetThreadAndThreadUsers()
    throws Exception
    {
        GraphAPI.User mockUserFb = FacebookUserDaoTest.getFreshMockUserFb();
        User user = getMockUser(mockUserFb);
        List<User> friends = getMockFriends(mockUserFb);
        String title = "Should entities and rest objects use getters and setters, or just public fields?";
        ThreadModel thread = ThreadDao.createThread(user, title);
        ThreadUserDao.createThreadUsers(thread, friends);

        JPA.em().flush();
        JPA.em().refresh(thread);
        assertNotNull(thread);
        assertEquals(title, thread.title);
        assertEquals(user, thread.getUser());
        assertEquals(friends.size() + 1, thread.threadUsers.size());

        List<User> usersInThread = new ArrayList<User>();
        for (ThreadUser threadUser: thread.threadUsers)
        {
            usersInThread.add(threadUser.user);
        }
        assertTrue(usersInThread.containsAll(friends));
    }
}
