package serum.dao;

import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.Expr;
import play.db.ebean.*;

import serum.model.*;

import serum.facebook.GraphAPI;

public class UserDao
{
    public static User createUpdateUserFromFacebookInfo(GraphAPI.User userFb)
    throws Exception
    {
        FacebookUser facebookUser = null;
        Transaction transaction = Ebean.beginTransaction();
        try
        {
            facebookUser = FacebookUserDao.createUpdateFacebookUser(userFb);
            Ebean.refresh(facebookUser);
            if (facebookUser.user != null)
            {
                Ebean.refresh(facebookUser.user);
            }
            createUser(facebookUser);
            createUserAuthToken(facebookUser.user);

            transaction.commit();
        }
        finally
        {
            transaction.end();
        }
        return facebookUser.user;
    }

    protected static void createUser(FacebookUser facebookUser)
    {
        if (facebookUser.user == null || facebookUser.user.isDeleted)
        {
            facebookUser.user = new User();
            facebookUser.user.facebookUser = facebookUser;
            Ebean.save(facebookUser.user);
            Ebean.save(facebookUser);
        }
    }

    protected static void createUserAuthToken(User user)
    throws Exception
    {
        if (user.userAuthToken == null || user.userAuthToken.isDeleted)
        {
            user.userAuthToken = new UserAuthToken(user);
            user.userAuthToken.user = user;
            Ebean.save(user.userAuthToken);
            Ebean.save(user);
        }
    }

    public static User getUserByAuthToken(String token)
    {
        User user =
            Ebean.find(User.class)
            .fetch("userAuthToken")
            .fetch("facebookUser")
            .fetch("facebookUser.friends")
            .fetch("facebookUser.friends.facebookUserOfFriend.user")
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
