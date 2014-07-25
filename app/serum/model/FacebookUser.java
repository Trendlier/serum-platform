package serum.model;

import java.util.*;
import javax.persistence.*;

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

    @Column(name="created_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar createdUTC;

    @Column(name="is_deleted")
    public boolean isDeleted;

    @Column(name="deleted_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar deletedUTC;

    public FacebookUser()
    {
        this.createdUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
    }
}
