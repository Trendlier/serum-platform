package serum.model;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name="facebook_user_friend")
public class FacebookUserFriend
{
    @Id
    @SequenceGenerator(name="facebookUserFriendSeq", sequenceName="facebook_user_friend_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="facebookUserFriendSeq")
    @Column(name="id")
    public Long id;

    @ManyToOne
    @JoinColumn(name="facebook_user_id")
    public FacebookUser facebookUser;

    @ManyToOne
    @JoinColumn(name="facebook_user_id_of_friend")
    public FacebookUser facebookUserOfFriend;

    @Column(name="created_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar createdUTC;

    @Column(name="is_deleted")
    public boolean isDeleted;

    @Column(name="deleted_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar deletedUTC;

    public FacebookUserFriend(FacebookUser facebookUser, FacebookUser facebookUserOfFriend)
    {
        this.facebookUser = facebookUser;
        this.facebookUserOfFriend = facebookUserOfFriend;
        this.createdUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
    }
}
