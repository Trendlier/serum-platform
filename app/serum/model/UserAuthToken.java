package serum.model;

import java.math.BigInteger;
import java.security.*;
import java.util.*;
import javax.persistence.*;

import play.*;

@Entity
@Table(name="user_auth_token")
public class UserAuthToken
{
    public static final int DEFAULT_EXPIRES_DAYS = 30;

    @Id
    @SequenceGenerator(name="userAuthTokenSeq", sequenceName="user_auth_token_id_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="userAuthTokenSeq")
    @Column(name="id")
    public Long id;

    @OneToOne
    @JoinColumn(name="user_id")
    public User user;

    @Column(name="token")
    public String token;

    @Column(name="expires_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar expiresUTC;

    @Column(name="created_utc")
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar createdUTC;

    @Column(name="is_deleted")
    public boolean isDeleted;

    public UserAuthToken(User user)
    throws Exception
    {
        this.user = user;
        this.token = generateAuthToken(user);
        this.expiresUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
        this.expiresUTC.add(Calendar.DAY_OF_MONTH, DEFAULT_EXPIRES_DAYS);
        this.createdUTC = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
    }

    protected static String generateAuthToken(User user)
    throws Exception
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String text = user.id + "_" + System.currentTimeMillis();
            md.update(text.getBytes("UTF-8"));
            return new BigInteger(1, md.digest()).toString(16);
        }
        catch(Exception e)
        {
            Logger.error("Error generating auth token for user " + user.id, e);
            throw e;
        }
    }
} 
