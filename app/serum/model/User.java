package serum.model;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name="`user`")
public class User
{
    public static final int DEFAULT_THREAD_CAPACITY = 10;

    @Id
    @SequenceGenerator(name="userSeq", sequenceName="user_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="userSeq")
    @Column(name="id")
    public Long id;

    @Column(name="thread_capacity")
    public int threadCapacity;

    @Column(name="created_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar createdUTC;

    @Column(name="is_deleted")
    public boolean isDeleted;

    @Column(name="deleted_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar deletedUTC;

    @OneToOne(mappedBy="user")
    public FacebookUser facebookUser;

    @OneToOne(mappedBy="user")
    public UserAuthToken userAuthToken;

    public User()
    {
        this.threadCapacity = DEFAULT_THREAD_CAPACITY;
        this.createdUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
    }

    /**
     * Basically a view for friends linked to this user through other tables
     */
    public List<User> getFriends()
    {
        List<User> friends = new ArrayList<User>();
        if (facebookUser != null && facebookUser.friends != null)
        {
            for (FacebookUserFriend facebookUserFriend: facebookUser.friends)
            {
                if (!facebookUserFriend.isDeleted &&
                    !facebookUserFriend.facebookUserOfFriend.isDeleted &&
                    facebookUserFriend.facebookUserOfFriend.user != null &&
                    !facebookUserFriend.facebookUserOfFriend.user.isDeleted
                )
                {
                    friends.add(facebookUserFriend.facebookUserOfFriend.user);
                }
            }
        }
        return friends;
    }
}
