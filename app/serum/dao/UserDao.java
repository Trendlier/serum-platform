package serum.dao;

import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import play.db.ebean.*;

import serum.model.*;

import serum.facebook.GraphAPI;

public class UserDao
{
    public static User getUserFromFacebookInfo(GraphAPI.User userFb)
    throws Exception
    {
        FacebookUser facebookUser = null;
        Ebean.beginTransaction();
        try
        {
            facebookUser = FacebookUserDao.createUpdateFacebookUser(userFb);
            FacebookUserDao.createUpdateFacebookUserFriends(facebookUser, userFb);
            if (facebookUser.user == null)
            {
                facebookUser.user = new User();
                facebookUser.user.facebookUser = facebookUser;
                Ebean.save(facebookUser.user);
                Ebean.save(facebookUser);
            }
            createUserAuthToken(facebookUser.user);

            Ebean.commitTransaction();
            Ebean.endTransaction();
        }
        catch (Exception e)
        {
            Ebean.endTransaction();
            throw(e);
        }
        return facebookUser.user;
    }

    protected static void createUserAuthToken(User user)
    throws Exception
    {
        if (user.userAuthToken == null || user.userAuthToken.isDeleted)
        {
            UserAuthToken userAuthToken = new UserAuthToken(user);
            Ebean.save(userAuthToken);
            user.userAuthToken = userAuthToken;
        }
    }

    public static User getUserByAuthToken(String token)
    {
        User user =
            Ebean.find(User.class)
            .fetch("userAuthToken")
            .fetch("facebookUser")
            .fetch("facebookUser.friends")
            .where().eq("userAuthToken.token", token)
            .findUnique();
        return user;
    }

    public static UserAuthToken getUserAuthTokenByToken(String token)
    {
        return Ebean.find(UserAuthToken.class)
            .where().eq("token", token)
            .findUnique();
    }
}
