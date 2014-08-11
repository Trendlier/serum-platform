package serum.validation;

import serum.model.ThreadModel;
import serum.model.User;

public class ThreadActionValidator
{
    public static boolean hasPermissionToSee(ThreadModel thread, User user)
    {
        if (user.id == thread.getUserOwner().id)
        {
            return true;
        }
        for (User invitedUser: thread.getInvitedUsers())
        {
            if (user.id == invitedUser.id)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean hasPermissionToRemove(ThreadModel thread, User user)
    {
        if (user.id == thread.getUserOwner().id)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
