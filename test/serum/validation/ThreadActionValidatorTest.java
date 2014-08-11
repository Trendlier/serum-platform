package serum.validation;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.Mockito.*;
import play.test.*;

import serum.model.ThreadModel;
import serum.model.ThreadUser;
import serum.model.User;

public class ThreadActionValidatorTest
{
    @Test
    public void testHasPermissionToSee()
    {
        ThreadModel thread = new ThreadModel("Test thread");
        thread.threadUsers = new ArrayList<ThreadUser>();
        User userOwner = new User();
        userOwner.id = 1L;
        ThreadUser threadUserOwner = new ThreadUser(thread, userOwner, true);
        thread.threadUsers.add(threadUserOwner);
        assertTrue(ThreadActionValidator.hasPermissionToSee(thread, userOwner));

        User invitedUser = new User();
        invitedUser.id = 2L;
        ThreadUser threadUserInvited = new ThreadUser(thread, invitedUser, false);
        thread.threadUsers.add(threadUserInvited);
        assertTrue(ThreadActionValidator.hasPermissionToSee(thread, invitedUser));

        User notInvitedUser = new User();
        notInvitedUser.id = 3L;
        assertFalse(ThreadActionValidator.hasPermissionToSee(thread, notInvitedUser));
    }

    @Test
    public void testHasPermissionToRemove()
    {
        ThreadModel thread = new ThreadModel("Test thread");
        thread.threadUsers = new ArrayList<ThreadUser>();
        User userOwner = new User();
        userOwner.id = 1L;
        ThreadUser threadUserOwner = new ThreadUser(thread, userOwner, true);
        thread.threadUsers.add(threadUserOwner);
        assertTrue(ThreadActionValidator.hasPermissionToRemove(thread, userOwner));

        User invitedUser = new User();
        invitedUser.id = 2L;
        ThreadUser threadUserInvited = new ThreadUser(thread, invitedUser, false);
        thread.threadUsers.add(threadUserInvited);
        assertFalse(ThreadActionValidator.hasPermissionToRemove(thread, invitedUser));

        User notInvitedUser = new User();
        notInvitedUser.id = 3L;
        assertFalse(ThreadActionValidator.hasPermissionToRemove(thread, notInvitedUser));
    }
}
