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

    @Transient
    public FacebookUser facebookUser;

    @OneToOne(mappedBy="user")
    public UserAuthToken userAuthToken;

    public User()
    {
        this.threadCapacity = DEFAULT_THREAD_CAPACITY;
        this.createdUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
    }
}
