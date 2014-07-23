package serum.rest;

public class LoginUsingFacebookResponse
{
    public boolean success;
    public String message;
    public String userAuthToken;

    public LoginUsingFacebookResponse(boolean success, String message)
    {
        this.success = success;
        this.message = message;
    }

    public LoginUsingFacebookResponse(boolean success, String message, String userAuthToken)
    {
        this.success = success;
        this.message = message;
        this.userAuthToken = userAuthToken;
    }
}
