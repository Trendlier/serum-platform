package serum.rest;

public class Response
{
    public Boolean success;
    public String message;

    public Response(Boolean success, String message)
    {
        this.success = success;
        this.message = message;
    }
}
