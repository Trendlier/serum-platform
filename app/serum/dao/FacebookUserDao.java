package serum.dao;

import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import play.db.ebean.*;

import serum.model.*;

import serum.util.Facebook;

public class FacebookUserDao
{
    public static FacebookUser createUpdateFacebookUser(Facebook.User userFb)
    {
        FacebookUser facebookUser =
            Ebean.find(FacebookUser.class)
            .fetch("user")
            .fetch("user.userAuthToken")
            .where().and(
                Expr.eq("idFacebook", userFb.getId()),
                Expr.eq("isDeleted", false))
            .findUnique();
        if (facebookUser == null)
        {
            facebookUser = new FacebookUser();
        }
        facebookUser.idFacebook = userFb.getId();
        if (userFb.getAccessToken() != null)
        {
            facebookUser.accessToken = userFb.getAccessToken();
        }
        facebookUser.name = userFb.getName();
        Ebean.save(facebookUser);
        return facebookUser;
    }

    protected static void createUpdateFacebookUserFriend(FacebookUser facebookUser, FacebookUser facebookUserOfFriend)
    {
        FacebookUserFriend facebookUserFriend =
            Ebean.find(FacebookUserFriend.class)
            .where().and(
                Expr.and(
                    Expr.eq("facebookUser", facebookUser),
                    Expr.eq("facebookUserOfFriend", facebookUserOfFriend)),
                Expr.eq("isDeleted", false))
            .findUnique();
        if (facebookUserFriend == null)
        {
            facebookUserFriend = new FacebookUserFriend(facebookUser, facebookUserOfFriend);
            Ebean.save(facebookUserFriend);
        }
    }

    protected static void removeUpdateFacebookUserFriends(FacebookUser facebookUser, Facebook.User userFb)
    {
        Set<String> facebookFriendIds = new HashSet<String>();
        for (Facebook.User userFbOfFriend: userFb.getFriends())
        {
            facebookFriendIds.add(userFbOfFriend.getId());
        }

        List<FacebookUserFriend> facebookUserFriendList =
            Ebean.find(FacebookUserFriend.class)
            .fetch("facebookUserOfFriend")
            .where().and(
                Expr.eq("facebookUser", facebookUser),
                Expr.eq("isDeleted", false))
            .findList();
        for (FacebookUserFriend facebookUserFriend: facebookUserFriendList)
        {
            // Remove facebook-user-friend record if it shouldn't exist
            if (!facebookFriendIds.contains(facebookUserFriend.facebookUserOfFriend.idFacebook))
            {
                facebookUserFriend.isDeleted = true;
                facebookUserFriend.deletedUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
                Ebean.save(facebookUserFriend);
            }
        }
    }

    public static void createUpdateFacebookUserFriends(FacebookUser facebookUser, Facebook.User userFb)
    {
        if (userFb.getFriends() == null)
        {
            return;
        }
        for (Facebook.User userFbOfFriend: userFb.getFriends())
        {
            FacebookUser facebookUserOfFriend = createUpdateFacebookUser(userFbOfFriend);
            createUpdateFacebookUserFriend(facebookUser, facebookUserOfFriend);
        }
        removeUpdateFacebookUserFriends(facebookUser, userFb);
    }
}
