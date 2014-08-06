package serum.facebook;

import java.util.*;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

import com.restfb.*;
import com.restfb.exception.*;

public class GraphAPITest
{
    public static GraphAPI.User getMockUserFb()
    {
        // Create mock Facebook user
        // XXX: User contains methods that call other methods, so we have to use a spy
        // instead of a mock.
        GraphAPI.User mockUserFb = spy(new GraphAPI.User());
        when(mockUserFb.getId()).thenReturn("123456");
        when(mockUserFb.getAccessToken()).thenReturn("abcdef");
        when(mockUserFb.getName()).thenReturn("Abc Def");
        when(mockUserFb.getGender()).thenReturn("female");
        when(mockUserFb.getPicture()).thenReturn(new GraphAPI.User.Picture("http://trendlier.com/xyz.jpeg"));
        // Create mock friends
        List<GraphAPI.User> mockFriends = new ArrayList<GraphAPI.User>();
        GraphAPI.User mockFriendUserFb = mock(GraphAPI.User.class);
        when(mockFriendUserFb.getId()).thenReturn("123459");
        when(mockFriendUserFb.getName()).thenReturn("Ghi Jkl");
        when(mockFriendUserFb.getGender()).thenReturn("male");
        when(mockFriendUserFb.getPicture()).thenReturn(new GraphAPI.User.Picture("http://trendlier.com/zyx.jpeg"));
        mockFriends.add(mockFriendUserFb);
        when(mockUserFb.getFriends()).thenReturn(mockFriends);
        return mockUserFb;
    }

    @Test
    public void testGetFriendIds()
    {
        GraphAPI.User mockUserFb = getMockUserFb();
        Set<String> friendIds = mockUserFb.getFriendIds();
        assertEquals(mockUserFb.getFriends().size(), friendIds.size());
        assertTrue(friendIds.contains(mockUserFb.getFriends().get(0).getId()));
    }

    @Test
    public void testCheckUserInfoFromFacebook()
    throws Exception
    {
        // Create mock Facebook user
        GraphAPI.User mockUserFb = mock(GraphAPI.User.class);
        when(mockUserFb.getId()).thenReturn("123456");
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchObject(eq("me"), eq(GraphAPI.User.class), anyObject()))
            .thenReturn(mockUserFb);

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
        when(mockFacebookClient.fetchObject(eq("me"), eq(GraphAPI.User.class), anyObject()))
            .thenReturn(mockUserFb);

        GraphAPI graphApi = new GraphAPI(mockFacebookClient);
        GraphAPI.User userFb = graphApi.checkUserInfoFromFacebook("654321", "abcdef");
    }

    @Test(expected=GraphAPI.AuthenticationException.class)
    public void testCheckUserInfoFromFacebookOAuthFailure()
    throws Exception
    {
        // Create mock of underlying client
        FacebookClient mockFacebookClient = mock(DefaultFacebookClient.class);
        when(mockFacebookClient.fetchObject(eq("me"), eq(GraphAPI.User.class), anyObject()))
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
        when(mockFacebookClient.fetchConnection(eq("me/friends"), eq(GraphAPI.User.class), anyObject()))
            .thenReturn(mockConnectionFriends);

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
        when(mockFacebookClient.fetchConnection(eq("me/friends"), eq(GraphAPI.User.class), anyObject()))
            .thenThrow(FacebookOAuthException.class);

        GraphAPI graphApi = new GraphAPI(mockFacebookClient);
        graphApi.pullMyFriends(mockUserFb);
    }
}
