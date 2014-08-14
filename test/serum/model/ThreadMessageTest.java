package serum.model;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class ThreadMessageTest
{
    @Test
    public void testIsOwnerFalse()
    {
        User user = new User();
        user.id = 1L;
        User user2 = new User();
        user2.id = 2L;
        ThreadModel thread = new ThreadModel("Test thread");
        ThreadUser threadUser = new ThreadUser(thread, user, false);
        ThreadMessage threadMessage = new ThreadMessage(threadUser, "EHLO WORLD");
        assertTrue(threadMessage.isOwner(user));
        assertFalse(threadMessage.isOwner(user2));
    }
}
