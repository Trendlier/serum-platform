package serum.rest;

public class LoginResponse
{
    public boolean success;
    public String message;
    public String userAuthToken;

    public LoginResponse(boolean success, String message)
    {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(boolean success, String message, String userAuthToken)
    {
        this.success = success;
        this.message = message;
        this.userAuthToken = userAuthToken;
    }
}
