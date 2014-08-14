package serum.validation;

import serum.model.ThreadModel;
import serum.model.ThreadMessage;
import serum.model.User;

public class ThreadActionValidator
{
    public static boolean hasPermissionToAddMessage(ThreadModel thread, User user)
    {
        return thread.isThreadUser(user);
    }

    public static boolean hasPermissionToSee(ThreadModel thread, User user)
    {
        return thread.isThreadUser(user);
    }

    public static boolean hasPermissionToAddImage(ThreadModel thread, User user)
    {
        return thread.isOwner(user);
    }

    public static boolean hasPermissionToRemove(ThreadModel thread, User user)
    {
        return thread.isOwner(user);
    }
}
