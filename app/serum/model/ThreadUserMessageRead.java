package serum.model;

import java.util.*;
import javax.persistence.*;

import serum.util.IdHashUtil;

@Entity
@Table(name="thread_user_message_read")
public class ThreadUserMessageRead
{
    @Id
    @SequenceGenerator(name="threadUserMessageReadSeq", sequenceName="thread_user_message_read_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="threadUserMessageReadSeq")
    @Column(name="id")
    public Long id;

    @ManyToOne
    @JoinColumn(name="thread_user_id")
    public ThreadUser threadUser;

    @ManyToOne
    @JoinColumn(name="thread_message_id")
    public ThreadMessage threadMessage;

    @Column(name="created_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar createdUTC;

    public ThreadUserMessageRead()
    {
    }

    public ThreadUserMessageRead(ThreadUser threadUser, ThreadMessage threadMessage)
    {
        this.threadUser = threadUser;
        this.threadMessage = threadMessage;
        this.createdUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
    }
}
