package serum.model;

import java.util.*;
import javax.persistence.*;

import serum.util.IdHashUtil;

/**
 * Cannot call this class "Thread" because it is ambiguous with java.lang.Thread
 */
@Entity
@Table(name="thread")
public class ThreadModel
{
    @Id
    @SequenceGenerator(name="threadSeq", sequenceName="thread_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="threadSeq")
    @Column(name="id")
    public Long id;

    @Column(name="title")
    public String title;

    @Column(name="image_url")
    public String imageUrl;

    @Column(name="last_updated_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar lastUpdatedUTC;

    @Column(name="created_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar createdUTC;

    @Column(name="is_deleted")
    public Boolean isDeleted;

    @Column(name="deleted_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar deletedUTC;

    @OneToMany(mappedBy="thread")
    public List<ThreadUser> threadUsers;

    public ThreadModel()
    {
    }

    public ThreadModel(String title)
    {
        this.title = title;
        this.createdUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
        this.lastUpdatedUTC = this.createdUTC;
        this.isDeleted = false;
    }

    /**
     * @param id hash
     * @return id
     */
    public static Long getIdFromHash(String idHash)
    throws Exception
    {
        return IdHashUtil.decrypt(idHash);
    }

    /**
     * @return id hash
     */
    public String getIdHash()
    throws Exception
    {
        return IdHashUtil.encrypt(id);
    }

    public User getUserOwner()
    {
        for (ThreadUser threadUser: threadUsers)
        {
            if (threadUser.isOwner && !threadUser.isDeleted)
            {
                return threadUser.user;
            }
        }
        return null;
    }

    public boolean isOwner(User user)
    {
        if (user.id.equals(getUserOwner().id))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public List<User> getInvitedUsers()
    {
        List<User> invitedUsers = new ArrayList<User>();
        for (ThreadUser threadUser: threadUsers)
        {
            if (!threadUser.isOwner && !threadUser.isDeleted)
            {
                invitedUsers.add(threadUser.user);
            }
        }
        return invitedUsers;
    }

    public ThreadUser getThreadUserFromUser(User user)
    {
        for (ThreadUser threadUser: threadUsers)
        {
            if (user.id.equals(threadUser.user.id) && !threadUser.isDeleted)
            {
                return threadUser;
            }
        }
        return null;
    }

    public boolean isThreadUser(User user)
    {
        if (getThreadUserFromUser(user) != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
