package serum.dao;

import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import play.db.ebean.*;

import serum.model.*;

public class UserDao
{
    @Transactional
    public static User getUserFromFacebookInfo(com.restfb.types.User userFb)
    throws Exception
    {
        User user = Ebean.find(User.class)
            .fetch("facebookUser")
            .fetch("userAuthToken")
            .where().and(
                Expr.eq("facebookUser.idFacebook", userFb.getId()),
                Expr.or(
                    Expr.isNull("userAuthToken"),
                    Expr.eq("userAuthToken.isDeleted", false)))
            .findUnique();

        if (user == null)
        {
            user = createUser();
        }

        if (user.facebookUser == null)
        {
            user.facebookUser = createFacebookUser(user, userFb);
        }
        else
        {
            updateFacebookUser(user.facebookUser, userFb);
        }

        if (user.userAuthToken == null)
        {
            user.userAuthToken = createUserAuthToken(user);
        }

        return user;
    }

    public static User createUser()
    {
        User user = new User();
        Ebean.save(user);
        return user;
    }

    public static FacebookUser createFacebookUser(User user, com.restfb.types.User userFb)
    {
        FacebookUser facebookUser = new FacebookUser(user);
        updateFacebookUser(facebookUser, userFb);
        Ebean.save(facebookUser);
        return facebookUser;
    }

    public static void updateFacebookUser(FacebookUser facebookUser, com.restfb.types.User userFb)
    {
        facebookUser.idFacebook = userFb.getId();
        facebookUser.firstName = userFb.getFirstName();
        facebookUser.middleName = userFb.getMiddleName();
        facebookUser.lastName = userFb.getLastName();
    }

    public static UserAuthToken createUserAuthToken(User user)
    throws Exception
    {
        UserAuthToken userAuthToken = new UserAuthToken(user);
        Ebean.save(userAuthToken);
        return userAuthToken;
    }

    public static UserAuthToken getUserAuthTokenByToken(String token)
    {
        return Ebean.find(UserAuthToken.class)
            .where().eq("token", token)
            .findUnique();
    }
}
