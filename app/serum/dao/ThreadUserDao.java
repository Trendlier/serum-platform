package serum.dao;

import java.util.*;

import javax.persistence.*;
import play.db.jpa.*;

import serum.model.*;

public class ThreadUserDao
{
    public static void createThreadUsers(ThreadModel thread, User user, Collection<User> invitedUsers)
    {
        // Create thread user for user who started thread
        ThreadUser threadUserOwner = new ThreadUser(thread, user, true);
        // TODO: put proper, randomly, and uniquely selected values here.
        threadUserOwner.iconUrl = "http://trendlier.com/abc/xyz.jpeg";
        threadUserOwner.colourRed = 127;
        threadUserOwner.colourGreen = 127;
        threadUserOwner.colourBlue = 127;
        JPA.em().persist(threadUserOwner);

        // Create thread users for users who got invited to this thread
        for (User invitedUser: invitedUsers)
        {
            ThreadUser threadUser = new ThreadUser(thread, invitedUser, false);
            // TODO: put proper, randomly, and uniquely selected values here.
            threadUser.iconUrl = "http://trendlier.com/abc/xyz.jpeg";
            threadUser.colourRed = 127;
            threadUser.colourGreen = 127;
            threadUser.colourBlue = 255;
            JPA.em().persist(threadUser);
        }
    }

    public static void removeThreadUser(ThreadUser threadUser)
    {
        threadUser.isDeleted = true;
    }
}
