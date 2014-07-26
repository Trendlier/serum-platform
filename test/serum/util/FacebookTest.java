package serum.util;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

import com.restfb.*;
import com.restfb.exception.*;

public class FacebookTest
{
    @Test
    public void testCheckUserInfoFromFacebook()
    throws Exception
    {
        // Create mock Facebook user
        Facebook.User mockUserFb = mock(Facebook.User.class);
        when(mockUserFb.getId()).thenReturn("123456");
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchObject("me", Facebook.User.class)).thenReturn(mockUserFb);

        Facebook facebook = new Facebook(mockFacebookClient);
        Facebook.User userFb = facebook.checkUserInfoFromFacebook("123456", "abcdef");
        verify(mockUserFb).setAccessToken("abcdef");
        assertEquals(userFb, mockUserFb);
    }

    @Test(expected=Facebook.AuthenticationException.class)
    public void testCheckUserInfoFromFacebookIdIncorrect()
    throws Exception
    {
        // Create mock Facebook user
        Facebook.User mockUserFb = mock(Facebook.User.class);
        when(mockUserFb.getId()).thenReturn("123456");
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchObject("me", Facebook.User.class)).thenReturn(mockUserFb);

        Facebook facebook = new Facebook(mockFacebookClient);
        Facebook.User userFb = facebook.checkUserInfoFromFacebook("654321", "abcdef");
    }

    @Test(expected=Facebook.AuthenticationException.class)
    public void testCheckUserInfoFromFacebookOAuthFailure()
    throws Exception
    {
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchObject("me", Facebook.User.class))
            .thenThrow(FacebookOAuthException.class);

        Facebook facebook = new Facebook(mockFacebookClient);
        Facebook.User userFb = facebook.checkUserInfoFromFacebook("654321", "abcdef");
    }

    @Test
    public void testPullFriends()
    throws Exception
    {
        // Create mock Facebook user
        Facebook.User mockUserFb = mock(Facebook.User.class);
        // Create mock friends
        List<Facebook.User> mockFriends = new ArrayList<Facebook.User>();
        Facebook.User mockFriendUserFb = mock(Facebook.User.class);
        when(mockFriendUserFb.getId()).thenReturn("123459");
        mockFriends.add(mockFriendUserFb);
        Connection<Facebook.User> mockConnectionFriends = mock(Connection.class);
        when(mockConnectionFriends.getData()).thenReturn(mockFriends);
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchConnection("me/friends", Facebook.User.class)).thenReturn(mockConnectionFriends);

        Facebook facebook = new Facebook(mockFacebookClient);
        facebook.pullFriends(mockUserFb);
        verify(mockUserFb).setFriends(mockFriends);
    }

    @Test(expected=Facebook.AuthenticationException.class)
    public void testPullFriendsOAuthFailure()
    throws Exception
    {
        // Create mock Facebook user
        Facebook.User mockUserFb = mock(Facebook.User.class);
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchConnection("me/friends", Facebook.User.class))
            .thenThrow(FacebookOAuthException.class);

        Facebook facebook = new Facebook(mockFacebookClient);
        facebook.pullFriends(mockUserFb);
    }
}
