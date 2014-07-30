package serum.model;

import java.util.*;
import javax.persistence.*;
import com.avaje.ebean.annotation.*;

@NamedQueries(value={
    @NamedQuery(
        name="findFriendsByIdFacebook",
        query="where id in ( " +
                  "SELECT ffu.id " +
                  "FROM facebook_user_friend fuf " +
                  "INNER JOIN facebook_user fu ON fu.id = fuf.facebook_user_id " +
                  "INNER JOIN facebook_user ffu ON ffu.id = fuf.facebook_user_id_of_friend " +
                  "WHERE fu.id_facebook = :idFacebook " +
                  "AND NOT fuf.is_deleted " +
                  "AND NOT ffu.is_deleted " +
                ") ")
})
@NamedUpdates(value={
    @NamedUpdate(
        name="removeByIdFacebook",
        update=
            "update FacebookUser " +
            "set isDeleted = true " +
            "where idFacebook = :idFacebook "),
    // This delete should only be used for testing purposes.
    // Use removeByIdFacebook instead, so we can track history.
    @NamedUpdate(
        name="deleteByIdFacebook",
        update=
            "delete from FacebookUser " +
            "where idFacebook = :idFacebook ")
})
@Entity
@Table(name="facebook_user")
public class FacebookUser
{
    @Id
    @SequenceGenerator(name="facebookUserSeq", sequenceName="facebook_user_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="facebookUserSeq")
    @Column(name="id")
    public Long id;

    @OneToOne
    @JoinColumn(name="user_id")
    public User user;

    @Column(name="id_facebook")
    public String idFacebook;

    @Column(name="access_token")
    public String accessToken;

    @Column(name="name")
    public String name;

    @Column(name="picture_url")
    public String pictureUrl;

    @Column(name="created_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar createdUTC;

    @Column(name="is_deleted")
    public boolean isDeleted;

    @Column(name="deleted_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar deletedUTC;

    @OneToMany(mappedBy="facebookUser")
    public Set<FacebookUserFriend> friends;

    public FacebookUser()
    {
        this.createdUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
    }
}
