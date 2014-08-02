package serum.model;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

import serum.dao.DaoTest;

public class FacebookUserTest extends DaoTest
{
    @Test
    public void testGetFacebookUserMapByIds()
    {
        FacebookUser facebookUser = new FacebookUser("123", "Bob");
        List<FacebookUser> facebookUserList = new ArrayList<FacebookUser>();
        facebookUserList.add(facebookUser);
        Map<String, FacebookUser> facebookUserMap = FacebookUser.getFacebookUserMap(facebookUserList);
        assertEquals(1, facebookUserMap.size());
        assertEquals(facebookUser, facebookUserMap.get(facebookUser.idFacebook));
    }
}
