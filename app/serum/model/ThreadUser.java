package serum.model;

import java.util.*;
import javax.persistence.*;

import serum.util.IdHashUtil;

@Entity
@Table(name="thread_user")
public class ThreadUser
{
    @Id
    @SequenceGenerator(name="threadUserSeq", sequenceName="thread_user_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="threadUserSeq")
    @Column(name="id")
    public Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    public User user;

    @ManyToOne
    @JoinColumn(name="thread_id")
    public ThreadModel thread;

    @Column(name="icon_url")
    public String iconUrl;

    @Column(name="colour_red")
    public Integer colourRed;

    @Column(name="colour_green")
    public Integer colourGreen;

    @Column(name="colour_blue")
    public Integer colourBlue;

    @Column(name="is_asker")
    public Boolean isOwner;

    @Column(name="created_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar createdUTC;

    @Column(name="is_deleted")
    public Boolean isDeleted;

    @Column(name="deleted_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar deletedUTC;

    @OneToMany(mappedBy="threadUser")
    public List<ThreadMessage> threadMessages;

    public ThreadUser()
    {
    }

    public ThreadUser(ThreadModel thread, User user, boolean isOwner)
    {
        this.thread = thread;
        this.user = user;
        this.isOwner = isOwner;
        this.createdUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
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
}
