package serum.rest;

public class LoginResponse extends Response
{
    public String userAuthToken;

    public LoginResponse(boolean success, String message)
    {
        super(success, message);
    }

    public LoginResponse(boolean success, String message, String userAuthToken)
    {
        super(success, message);
        this.userAuthToken = userAuthToken;
    }
}
