package serum.rest;

public class CreateThreadResponse extends Response
{
    public String id;

    public CreateThreadResponse(Boolean success, String message)
    {
        super(success, message);
    }

    public CreateThreadResponse(Boolean success, String message, String id)
    {
        super(success, message);
        this.id = id;
    }
}
