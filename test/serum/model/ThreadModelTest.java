package serum.model;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ThreadModelTest
{
    @Test
    public void testGetThreadUserFromUser()
    {
        ThreadModel thread = new ThreadModel("Test thread");
        thread.threadUsers = new ArrayList<ThreadUser>();
        User user = new User();
        user.id = 1L;
        ThreadUser threadUser = new ThreadUser(thread, user, true);
        thread.threadUsers.add(threadUser);
        assertNotNull(thread.getThreadUserFromUser(user));

        threadUser.isDeleted = true;
        assertNull(thread.getThreadUserFromUser(user));

        User notInvitedUser = new User();
        notInvitedUser.id = 2L;
        assertNull(thread.getThreadUserFromUser(notInvitedUser));
    }

    @Test
    public void testIsThreadUser()
    {
        ThreadModel thread = new ThreadModel("Test thread");
        thread.threadUsers = new ArrayList<ThreadUser>();
        User user = new User();
        user.id = 1L;
        ThreadUser threadUser = new ThreadUser(thread, user, true);
        thread.threadUsers.add(threadUser);
        assertTrue(thread.isThreadUser(user));

        threadUser.isDeleted = true;
        assertFalse(thread.isThreadUser(user));

        User notInvitedUser = new User();
        notInvitedUser.id = 2L;
        assertFalse(thread.isThreadUser(notInvitedUser));
    }

    @Test
    public void testGetUserOwner()
    {
        ThreadModel thread = new ThreadModel("Test thread");
        thread.threadUsers = new ArrayList<ThreadUser>();
        User userOwner = new User();
        userOwner.id = 1L;
        ThreadUser threadUserOwner = new ThreadUser(thread, userOwner, true);
        thread.threadUsers.add(threadUserOwner);
        assertEquals(userOwner, thread.getUserOwner());

        threadUserOwner.isDeleted = true;
        assertNull(thread.getUserOwner());
    }

    @Test
    public void testIsOwner()
    {
        ThreadModel thread = new ThreadModel("Test thread");
        thread.threadUsers = new ArrayList<ThreadUser>();
        User userOwner = new User();
        userOwner.id = 1L;
        ThreadUser threadUserOwner = new ThreadUser(thread, userOwner, true);
        thread.threadUsers.add(threadUserOwner);
        assertTrue(thread.isOwner(userOwner));

        User invitedUser = new User();
        invitedUser.id = 2L;
        ThreadUser threadUserInvited = new ThreadUser(thread, invitedUser, false);
        thread.threadUsers.add(threadUserInvited);
        assertFalse(thread.isOwner(invitedUser));

        User notInvitedUser = new User();
        notInvitedUser.id = 3L;
        assertFalse(thread.isOwner(notInvitedUser));
    }
}
