package serum.model;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

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
    }

    public FacebookUser(String idFacebook, String name)
    {
        this.idFacebook = idFacebook;
        this.name = name;
        this.createdUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
    }

    public static Map<String, FacebookUser> getFacebookUserMap(List<FacebookUser> facebookUserList)
    {
        Map<String, FacebookUser> facebookUserMap = new HashMap<String, FacebookUser>();
        for (FacebookUser facebookUser: facebookUserList)
        {
            facebookUserMap.put(facebookUser.idFacebook, facebookUser);
        }
        return facebookUserMap;
    }
}
