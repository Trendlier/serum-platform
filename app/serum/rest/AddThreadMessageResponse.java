package serum.rest;

public class AddThreadMessageResponse extends Response
{
    public String id;

    public AddThreadMessageResponse(Boolean success, String errorMessage)
    {
        super(success, errorMessage);
    }

    public AddThreadMessageResponse(Boolean success, String errorMessage, String id)
    {
        super(success, errorMessage);
        this.id = id;
    }
}
