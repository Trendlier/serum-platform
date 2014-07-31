package serum.dao;

import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.Expr;
import play.db.ebean.*;

import serum.model.*;

import serum.facebook.GraphAPI;

public class FacebookUserDao
{
    public static FacebookUser createUpdateFacebookUser(GraphAPI.User userFb)
    {
        FacebookUser facebookUser =
            Ebean.find(FacebookUser.class)
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

    protected static FacebookUser createFacebookUserOfFriend(GraphAPI.User userFb)
    {
        FacebookUser facebookUser = new FacebookUser();
        facebookUser.idFacebook = userFb.getId();
        facebookUser.name = userFb.getName();
        if (userFb.getPicture() != null &&
            userFb.getPicture().getData() != null)
        {
            facebookUser.pictureUrl = userFb.getPicture().getData().getUrl();
        }
        Ebean.save(facebookUser);
        return facebookUser;
    }

    protected static Map<String, FacebookUser> getFacebookUserMapByIds(Set<String> facebookIds)
    {
        if (facebookIds.isEmpty())
        {
            return new HashMap<String, FacebookUser>();
        }
        // Get the list of facebookUsers that are currently in the friends list
        List<FacebookUser> facebookUserList =
            Ebean.createNamedQuery(FacebookUser.class, "findUsersByIdFacebook")
            .setParameter("idFacebookList", facebookIds)
            .findList();
        // Convert this list to a map of Facebook ID to Facebook user object for quick lookup.
        return FacebookUser.getFacebookUserMap(facebookUserList);
    }

    public static void createUpdateFacebookUserFriends(FacebookUser facebookUser, GraphAPI.User userFb)
    {
        Transaction transaction = Ebean.beginTransaction();
        try
        {
            if (userFb.getFriends() == null)
            {
                return;
            }
            Set<String> facebookFriendIds = userFb.getFriendIds();
            Ebean.refreshMany(facebookUser, "friends");
            Map<String, FacebookUserFriend> existingFacebookFriendMap = facebookUser.getFacebookUserFriendMap();
            Map<String, FacebookUser> existingFacebookUserOfFriendMap = getFacebookUserMapByIds(facebookFriendIds);
            // Remove each friend that is currently in the old list but not in the new list.
            for (String idFacebook: existingFacebookFriendMap.keySet())
            {
                FacebookUserFriend facebookUserFriend = existingFacebookFriendMap.get(idFacebook);
                if (!facebookFriendIds.contains(idFacebook) && !facebookUserFriend.isDeleted)
                {
                    facebookUserFriend.isDeleted = true;
                    Ebean.save(facebookUserFriend);
                }
            }
            // Add each friend that currently is in the new list but not in the old list.
            for (GraphAPI.User userFbOfFriend: userFb.getFriends())
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
                    facebookUser.friends.add(facebookUserFriend);
                }
            }
            transaction.commit();
        }
        finally
        {
            transaction.end();
        }
    }
}
