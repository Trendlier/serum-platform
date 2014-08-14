package serum.dao;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.Mockito.*;
import play.test.*;

import static play.libs.F.*;
import play.db.jpa.*;

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
    throws Throwable
    {
        final String title = "Should entities and rest objects use getters and setters, or just public fields?";
        final Long threadId = JPA.withTransaction(new Function0<Long>() {
            @Override
            public Long apply() throws Throwable
            {
                GraphAPI.User mockUserFb = FacebookUserDaoTest.getFreshMockUserFb();
                User userOwner = getMockUser(mockUserFb);
                List<User> invitedUsers = getMockInvitedUsers(mockUserFb);
                ThreadModel thread = ThreadDao.createThread(title);
                ThreadUserDao.createThreadUsers(thread, userOwner, invitedUsers);
                return thread.id;
            }
        });

        JPA.withTransaction(new Callback0() {
            @Override
            public void invoke() throws Throwable
            {
                ThreadModel thread = ThreadDao.getThreadById(threadId);

                assertNotNull(thread);
                assertEquals(title, thread.title);
                assertNotNull(thread.getUserOwner());
                assertTrue(thread.threadUsers.size() > 1);

                List<User> actualInvitedUsers = thread.getInvitedUsers();
                assertTrue(thread.getInvitedUsers().size() > 0);

                ThreadUserDao.removeThreadUser(thread.threadUsers.get(1));
                assertEquals(actualInvitedUsers.size() - 1, thread.getInvitedUsers().size());

                ThreadDao.removeThread(thread);
                assertTrue(thread.isDeleted);
            }
        });
    }

    @Test
    public void testCreateThreadMessageAndGetThreadMessages()
    throws Throwable
    {
        final Long threadId = JPA.withTransaction(new Function0<Long>() {
            @Override
            public Long apply() throws Throwable
            {
                GraphAPI.User mockUserFb = FacebookUserDaoTest.getFreshMockUserFb();
                User userOwner = getMockUser(mockUserFb);
                List<User> invitedUsers = getMockInvitedUsers(mockUserFb);
                String title = "My test thread. Hello! World?";

                ThreadModel thread = ThreadDao.createThread(title);
                ThreadUserDao.createThreadUsers(thread, userOwner, invitedUsers);
                return thread.id;
            }
        });

        final Long threadMessageId = JPA.withTransaction(new Function0<Long>() {
            @Override
            public Long apply() throws Throwable
            {
                ThreadModel thread = ThreadDao.getThreadById(threadId);
                assertEquals(0, thread.getThreadMessages().size());

                ThreadUser threadUserOwner = thread.threadUsers.get(0);
                assertNotNull(threadUserOwner);
                ThreadMessage threadMessage = ThreadMessageDao.createThreadMessage(threadUserOwner, "A B C");
                return threadMessage.id;
            }
        });

        JPA.withTransaction(new Callback0() {
            @Override
            public void invoke() throws Throwable
            {
                ThreadModel thread = ThreadDao.getThreadById(threadId);
                assertEquals(1, thread.getThreadMessages().size());
                assertEquals(threadMessageId, thread.getThreadMessages().get(0).id);

                ThreadUser threadUserInvited = thread.threadUsers.get(1);
                assertNotNull(threadUserInvited);
                ThreadMessageDao.createThreadMessage(threadUserInvited, "D E F");
            }
        });

        JPA.withTransaction(new Callback0() {
            @Override
            public void invoke() throws Throwable
            {
                ThreadModel thread = ThreadDao.getThreadById(threadId);
                assertEquals(2, thread.getThreadMessages().size());

                ThreadMessage threadMessage = thread.getThreadMessages().get(0);
                ThreadMessageDao.removeThreadMessage(threadMessage);
                thread = ThreadDao.getThreadById(thread.id);
                assertEquals(1, thread.getThreadMessages().size());
            }
        });
    }
}
