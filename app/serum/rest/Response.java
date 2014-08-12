package serum.rest;

public class Response
{
    public Boolean success;
    public String errorMessage;

    public Response(Boolean success, String errorMessage)
    {
        this.success = success;
        this.errorMessage = errorMessage;
    }
}
