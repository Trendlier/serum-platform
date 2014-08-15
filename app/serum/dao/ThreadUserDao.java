package serum.dao;

import java.util.*;

import javax.persistence.*;
import play.db.jpa.*;

import serum.model.*;

public class ThreadUserDao
{
    // XXX: We are keeping icons as identifiers, not URLs. This will change.
    // If a large number of icons need to be stored, we may need to put these in a database.
    protected final static String[] icons = new String[] {
        "purple-dalek",
        "aqua-rocketship",
        "blue-face",
        "orange-monkey"
    };

    protected static void generateRandomUniqueIcon(ThreadUser threadUser, List<String> icons)
    {
        if (icons.isEmpty())
        {
            threadUser.iconUrl = "";
        }
        else if (threadUser.isOwner)
        {
            threadUser.iconUrl = "";
        }
        else
        {
            threadUser.iconUrl = icons.get(0);
            icons.remove(0);
        }
        threadUser.colourRed = 0;
        threadUser.colourGreen = 0;
        threadUser.colourBlue = 0;
    }

    /**
     * @return a randomly shuffled list of icons
     */
    protected static List<String> getIcons()
    {
        // Randomly shuffle icons
        List<String> icons = new ArrayList<String>(
            Arrays.asList(Arrays.copyOf(ThreadUserDao.icons, ThreadUserDao.icons.length))
        );
        Collections.shuffle(icons);
        return icons;
    }

    public static void createThreadUsers(ThreadModel thread, User user, Collection<User> invitedUsers)
    {
        List<String> icons = getIcons();

        // Create thread user for user who started thread
        ThreadUser threadUserOwner = new ThreadUser(thread, user, true);
        generateRandomUniqueIcon(threadUserOwner, icons);
        JPA.em().persist(threadUserOwner);

        // Create thread users for users who got invited to this thread
        for (User invitedUser: invitedUsers)
        {
            ThreadUser threadUser = new ThreadUser(thread, invitedUser, false);
            generateRandomUniqueIcon(threadUser, icons);
            JPA.em().persist(threadUser);
        }
    }

    public static void removeThreadUser(ThreadUser threadUser)
    {
        threadUser.isDeleted = true;
    }
}
