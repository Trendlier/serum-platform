package serum.model;

import java.util.*;
import javax.persistence.*;

import serum.util.IdHashUtil;

@Entity
@Table(name="thread_message")
public class ThreadMessage
{
    @Id
    @SequenceGenerator(name="threadMessageSeq", sequenceName="thread_message_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="threadMessageSeq")
    @Column(name="id")
    public Long id;

    @ManyToOne
    @JoinColumn(name="thread_user_id")
    public ThreadUser threadUser;

    @Column(name="`text`")
    public String text;

    @Column(name="image_url")
    public String imageUrl;

    @Column(name="created_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar createdUTC;

    @Column(name="is_deleted")
    public Boolean isDeleted;

    @Column(name="deleted_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar deletedUTC;

    @OneToMany(mappedBy="threadMessage")
    public List<ThreadUserMessageRead> threadUserMessagesRead;

    public ThreadMessage()
    {
    }

    public ThreadMessage(ThreadUser threadUser, String text)
    {
        this.threadUser = threadUser;
        this.text = text;
        this.createdUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
        this.isDeleted = false;
    }

    public boolean isOwner(User user)
    {
        return threadUser.user.id.equals(user.id);
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
