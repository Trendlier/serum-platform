package serum.dao;

import java.util.*;

import javax.persistence.*;
import play.db.jpa.*;

import serum.model.*;

import serum.facebook.GraphAPI;

public class FacebookUserDao
{
    public static FacebookUser getById(Long id)
    {
        return JPA.em().find(FacebookUser.class, id);
    }

    public static FacebookUser createUpdateFacebookUser(GraphAPI.User userFb)
    {
        FacebookUser facebookUser;
        try
        {
            facebookUser =
                JPA.em().createQuery(
                    "select fu " +
                    "from FacebookUser fu " +
                    "where fu.idFacebook = :idFacebook " +
                    "and fu.isDeleted = false ",
                    FacebookUser.class)
                .setParameter("idFacebook", userFb.getId())
                .getSingleResult();
        }
        catch (NoResultException e)
        {
            facebookUser = new FacebookUser(userFb.getId(), userFb.getName());
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
        if (userFb.getGender() != null)
        {
            facebookUser.gender = userFb.getGender();
        }
        if (userFb.getPicture() != null &&
            userFb.getPicture().getData() != null &&
            userFb.getPicture().getData().getUrl() != null)
        {
            facebookUser.pictureUrl = userFb.getPicture().getData().getUrl();
        }
        JPA.em().persist(facebookUser);
        return facebookUser;
    }

    protected static FacebookUser createFacebookUserOfFriend(GraphAPI.User userFb)
    {
        FacebookUser facebookUser = new FacebookUser(userFb.getId(), userFb.getName());
        facebookUser.idFacebook = userFb.getId();
        facebookUser.name = userFb.getName();
        facebookUser.gender = userFb.getGender();
        if (userFb.getPicture() != null &&
            userFb.getPicture().getData() != null)
        {
            facebookUser.pictureUrl = userFb.getPicture().getData().getUrl();
        }
        JPA.em().persist(facebookUser);
        return facebookUser;
    }

    protected static Map<String, FacebookUser> getFacebookUserMapByIds(Set<String> facebookIds)
    {
        if (facebookIds.isEmpty())
        {
            return new HashMap<String, FacebookUser>();
        }
        List<FacebookUser> facebookUserList =
            JPA.em().createQuery(
                "select fu " +
                "from FacebookUser fu " +
                "where fu.idFacebook in :facebookIds " +
                "and fu.isDeleted = false",
                FacebookUser.class)
            .setParameter("facebookIds", facebookIds)
            .getResultList();
        return FacebookUser.getFacebookUserMap(facebookUserList);
    }

    public static void createUpdateFacebookUserFriends(FacebookUser facebookUser, GraphAPI.User userFb)
    {
        if (userFb.getFriends() == null)
        {
            return;
        }
        if (facebookUser.friends == null)
        {
            play.Logger.error(
                "Facebook user friends has not been lazy-loaded. Probably the transaction that " +
                "loaded it did not close. This should never happen.");
            JPA.em().flush();
            JPA.em().refresh(facebookUser);
        }
        Set<String> facebookFriendIds = userFb.getFriendIds();
        Map<String, FacebookUserFriend> existingFacebookFriendMap = facebookUser.getFacebookUserFriendMap();
        Map<String, FacebookUser> existingFacebookUserOfFriendMap = getFacebookUserMapByIds(facebookFriendIds);
        // Remove each friend that is currently in the old list but not in the new list.
        for (String idFacebook: existingFacebookFriendMap.keySet())
        {
            FacebookUserFriend facebookUserFriend = existingFacebookFriendMap.get(idFacebook);
            if (!facebookFriendIds.contains(idFacebook) && !facebookUserFriend.isDeleted)
            {
                facebookUserFriend.isDeleted = true;
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
                JPA.em().persist(facebookUserFriend);
                facebookUser.friends.add(facebookUserFriend);
            }
        }
    }

    public static void removeFacebookUserById(String idFacebook)
    {
        JPA.em().createQuery(
                "update FacebookUser set isDeleted = true " +
                "where idFacebook = :idFacebook ")
            .setParameter("idFacebook", idFacebook)
            .executeUpdate();
    }

    /**
     * This delete should only be used for testing purposes.
     * Use remove-by-id instead, so we can track history.
     */
    protected static void deleteFacebookUserByIdFacebook(String idFacebook)
    {
        JPA.em().createQuery(
                "delete FacebookUser " +
                "where idFacebook = :idFacebook ")
            .setParameter("idFacebook", idFacebook)
            .executeUpdate();
    }

    /**
     * This delete should only be used for testing purposes.
     * Use remove-by-id instead, so we can track history.
     */
    protected static void deleteFacebookUserFriendByIdFacebook(String idFacebook)
    {
        JPA.em().createQuery(
                "delete FacebookUserFriend " +
                "where facebookUser = ( " +
                  "select fu from FacebookUser fu " +
                  "where fu.idFacebook = :idFacebook " +
                  "and fu.isDeleted = false " +
                ") ")
            .setParameter("idFacebook", idFacebook)
            .executeUpdate();
    }
}
