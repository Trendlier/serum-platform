package serum.dao;

import java.util.*;

import javax.persistence.*;
import play.db.jpa.*;

import serum.model.*;

import serum.facebook.GraphAPI;

public class UserDao
{
    public static User createUpdateUserFromFacebookInfo(FacebookUser facebookUser)
    throws Exception
    {
        if (facebookUser.user == null)
        {
            facebookUser.user = new User();
            facebookUser.user.facebookUser = facebookUser;
            JPA.em().persist(facebookUser.user);
        }
        if (facebookUser.user.userAuthToken == null)
        {
            facebookUser.user.userAuthToken = new UserAuthToken(facebookUser.user);
            JPA.em().persist(facebookUser.user.userAuthToken);
        }
        return facebookUser.user;
    }

    public static User getUserByAuthToken(String token)
    {
        try
        {
            return JPA.em().createQuery(
                    "select t.user from UserAuthToken t " +
                    "where t.token = :token ",
                    User.class)
                .setParameter("token", token)
                .getSingleResult();
        }
        catch (NoResultException e)
        {
            return null;
        }
    }

    public static UserAuthToken getUserAuthTokenByToken(String token)
    {
        try
        {
            return JPA.em().createQuery(
                    "select t from UserAuthToken t " +
                    "where t.token = :token ",
                    UserAuthToken.class)
                .setParameter("token", token)
                .getSingleResult();
        }
        catch (NoResultException e)
        {
            return null;
        }
    }

    public static List<User> getUsersByIdHash(List<String> idHashList)
    throws Exception
    {
        List<Long> ids = new ArrayList<Long>();
        for (String idHash: idHashList)
        {
            ids.add(User.getIdFromHash(idHash));
        }
        return getUsersByIds(ids);
    }

    public static List<User> getUsersByIds(List<Long> ids)
    {
        return
            JPA.em().createQuery(
                "select u from User u " +
                "where u.id in :ids ",
                User.class)
            .setParameter("ids", ids)
            .getResultList();
    }
}
