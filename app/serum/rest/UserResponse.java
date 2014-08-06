package serum.rest;

public class UserResponse
{
    public Boolean success;
    public String message;

    public String idHash;
    public String name;
    public String pictureUrl;
    public Integer threadCapacity;

    public UserResponse(Boolean success, String message)
    {
        this.success = success;
        this.message = message;
    }
}
