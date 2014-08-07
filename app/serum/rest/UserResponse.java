package serum.rest;

public class UserResponse extends Response
{
    public String idHash;
    public String name;
    public String gender;
    public String pictureUrl;
    public Integer threadCapacity;

    public UserResponse(Boolean success, String message)
    {
        super(success, message);
    }
}
