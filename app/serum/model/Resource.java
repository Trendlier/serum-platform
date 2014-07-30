package serum.model;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name="`resource`")
public class Resource
{
    @Id
    @SequenceGenerator(name="resourceSeq", sequenceName="resource_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="resourceSeq")
    @Column(name="id")
    public Long id;

    @Column(name="type")
    public String type;

    @Column(name="content_type")
    public String contentType;

    @Column(name="url")
    public String url;

    @Column(name="width")
    public Integer width;

    @Column(name="height")
    public Integer height;

    @Column(name="created_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar createdUTC;

    @Column(name="is_deleted")
    public boolean isDeleted;
}
