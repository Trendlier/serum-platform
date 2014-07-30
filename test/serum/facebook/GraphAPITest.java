package serum.facebook;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

import com.restfb.*;
import com.restfb.exception.*;

public class GraphAPITest
{
    @Test
    public void testCheckUserInfoFromFacebook()
    throws Exception
    {
        // Create mock Facebook user
        GraphAPI.User mockUserFb = mock(GraphAPI.User.class);
        when(mockUserFb.getId()).thenReturn("123456");
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchObject("me", GraphAPI.User.class)).thenReturn(mockUserFb);

        GraphAPI graphApi = new GraphAPI(mockFacebookClient);
        GraphAPI.User userFb = graphApi.checkUserInfoFromFacebook("123456", "abcdef");
        verify(mockUserFb).setAccessToken("abcdef");
        assertEquals(userFb, mockUserFb);
    }

    @Test(expected=GraphAPI.AuthenticationException.class)
    public void testCheckUserInfoFromFacebookIdIncorrect()
    throws Exception
    {
        // Create mock Facebook user
        GraphAPI.User mockUserFb = mock(GraphAPI.User.class);
        when(mockUserFb.getId()).thenReturn("123456");
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchObject("me", GraphAPI.User.class)).thenReturn(mockUserFb);

        GraphAPI graphApi = new GraphAPI(mockFacebookClient);
        GraphAPI.User userFb = graphApi.checkUserInfoFromFacebook("654321", "abcdef");
    }

    @Test(expected=GraphAPI.AuthenticationException.class)
    public void testCheckUserInfoFromFacebookOAuthFailure()
    throws Exception
    {
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchObject("me", GraphAPI.User.class))
            .thenThrow(FacebookOAuthException.class);

        GraphAPI graphApi = new GraphAPI(mockFacebookClient);
        GraphAPI.User userFb = graphApi.checkUserInfoFromFacebook("654321", "abcdef");
    }

    @Test
    public void testPullFriends()
    throws Exception
    {
        // Create mock GraphAPI user
        GraphAPI.User mockUserFb = mock(GraphAPI.User.class);
        // Create mock friends
        List<GraphAPI.User> mockFriends = new ArrayList<GraphAPI.User>();
        GraphAPI.User mockFriendUserFb = mock(GraphAPI.User.class);
        when(mockFriendUserFb.getId()).thenReturn("123459");
        mockFriends.add(mockFriendUserFb);
        Connection<GraphAPI.User> mockConnectionFriends = mock(Connection.class);
        when(mockConnectionFriends.getData()).thenReturn(mockFriends);
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchConnection("me/friends", GraphAPI.User.class)).thenReturn(mockConnectionFriends);

        GraphAPI graphApi = new GraphAPI(mockFacebookClient);
        graphApi.pullMyFriends(mockUserFb);
        verify(mockUserFb).setFriends(mockFriends);
    }

    @Test(expected=GraphAPI.AuthenticationException.class)
    public void testPullFriendsOAuthFailure()
    throws Exception
    {
        // Create mock Facebook user
        GraphAPI.User mockUserFb = mock(GraphAPI.User.class);
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchConnection("me/friends", GraphAPI.User.class))
            .thenThrow(FacebookOAuthException.class);

        GraphAPI graphApi = new GraphAPI(mockFacebookClient);
        graphApi.pullMyFriends(mockUserFb);
    }

    @Test
    public void testPullPicture()
    throws Exception
    {
        // Create mock Facebook user
        GraphAPI.User mockUserFb = mock(GraphAPI.User.class);
        // Create mock picture
        GraphAPI.User.Picture mockPicture = new GraphAPI.User.Picture("http://trendlier.com/xyz.jpeg");
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        Parameter redirectFalse = Parameter.with("redirect", false);
        when(mockFacebookClient.fetchObject(eq("me/picture"), eq(GraphAPI.User.Picture.class),
                anyObject(),
                anyObject(),
                anyObject()))
            .thenReturn(mockPicture);

        GraphAPI graphApi = new GraphAPI(mockFacebookClient);
        graphApi.pullMyPicture(mockUserFb);
        verify(mockUserFb).setPicture(mockPicture);
    }

    @Test(expected=GraphAPI.AuthenticationException.class)
    public void testPullPictureOAuthFailure()
    throws Exception
    {
        // Create mock Facebook user
        GraphAPI.User mockUserFb = mock(GraphAPI.User.class);
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchObject(eq("me/picture"), eq(GraphAPI.User.Picture.class),
                anyObject(),
                anyObject(),
                anyObject()))
            .thenThrow(FacebookOAuthException.class);

        GraphAPI graphApi = new GraphAPI(mockFacebookClient);
        graphApi.pullMyPicture(mockUserFb);
    }
}
