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
            .fetch("user.facebookUser")
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
        if (userFb.getName() != null)
        {
            facebookUser.name = userFb.getName();
        }
        if (userFb.getPicture() != null &&
            userFb.getPicture().getData() != null &&
            userFb.getPicture().getData().getUrl() != null)
        {
            facebookUser.pictureUrl = userFb.getPicture().getData().getUrl();
        }
        Ebean.save(facebookUser);
        return facebookUser;
    }

    protected static FacebookUser createFacebookUserOfFriend(Facebook.User userFb)
    {
        FacebookUser facebookUser = new FacebookUser();
        facebookUser.idFacebook = userFb.getId();
        facebookUser.name = userFb.getName();
        Ebean.save(facebookUser);
        return facebookUser;
    }

    protected static Set<String> getFacebookFriendIds(Facebook.User userFb)
    {
        // Generate the list of Facebook IDs that should be in the friends list
        Set<String> facebookFriendIds = new HashSet<String>();
        for (Facebook.User userFbOfFriend: userFb.getFriends())
        {
            facebookFriendIds.add(userFbOfFriend.getId());
        }
        return facebookFriendIds;
    }

    protected static Map<String, FacebookUserFriend> getFacebookUserFriendMap(FacebookUser facebookUser)
    {
        // Get the list of facebookUsers that are currently in the friends list
        List<FacebookUserFriend> facebookUserFriendList =
            Ebean.find(FacebookUserFriend.class)
            .fetch("facebookUserOfFriend")
            .where().and(
                Expr.eq("facebookUser", facebookUser),
                Expr.eq("isDeleted", false))
            .findList();
        // Convert this list to a map of Facebook ID to Facebook-user-friend object for quick lookup.
        Map<String, FacebookUserFriend> facebookUserFriendMap = new HashMap<String, FacebookUserFriend>();
        for (FacebookUserFriend facebookUserFriend: facebookUserFriendList)
        {
            facebookUserFriendMap.put(facebookUserFriend.facebookUserOfFriend.idFacebook, facebookUserFriend);
        }
        return facebookUserFriendMap;
    }

    protected static Map<String, FacebookUser> getFacebookUserMapByIds(Set<String> facebookIds)
    {
        // Get the list of facebookUsers that are currently in the friends list
        List<FacebookUser> facebookUserList =
            Ebean.find(FacebookUser.class)
            .where().and(
                Expr.in("idFacebook", facebookIds),
                Expr.eq("isDeleted", false))
            .findList();
        // Convert this list to a map of Facebook ID to Facebook user object for quick lookup.
        Map<String, FacebookUser> facebookUserMap = new HashMap<String, FacebookUser>();
        for (FacebookUser facebookUser: facebookUserList)
        {
            facebookUserMap.put(facebookUser.idFacebook, facebookUser);
        }
        return facebookUserMap;
    }

    public static void createUpdateFacebookUserFriends(FacebookUser facebookUser, Facebook.User userFb)
    {
        if (userFb.getFriends() == null)
        {
            return;
        }
        Set<String> facebookFriendIds = getFacebookFriendIds(userFb);
        Map<String, FacebookUserFriend> existingFacebookFriendMap = getFacebookUserFriendMap(facebookUser);
        Map<String, FacebookUser> existingFacebookUserOfFriendMap = getFacebookUserMapByIds(facebookFriendIds);
        // Remove each friend that is currently in the old list but not in the new list.
        for (String idFacebook: existingFacebookFriendMap.keySet())
        {
            if (!facebookFriendIds.contains(idFacebook))
            {
                FacebookUserFriend facebookUserFriend = existingFacebookFriendMap.get(idFacebook);
                facebookUserFriend.isDeleted = true;
                Ebean.save(facebookUserFriend);
            }
        }
        // Add each friend that currently is in the new list but not in the old list.
        for (Facebook.User userFbOfFriend: userFb.getFriends())
        {
            if (!existingFacebookFriendMap.containsKey(userFbOfFriend.getId()))
            {
                FacebookUser facebookUserOfFriend = existingFacebookUserOfFriendMap.get(userFbOfFriend.getId());
                if (facebookUserOfFriend == null)
                {
                    facebookUserOfFriend = createFacebookUserOfFriend(userFbOfFriend);
                }
                FacebookUserFriend facebookUserFriend = new FacebookUserFriend(facebookUser, facebookUserOfFriend);
                Ebean.save(facebookUserFriend);
            }
        }
    }
}
