package serum.controller;

import play.*;
import play.mvc.*;
import play.libs.F.*;
import play.libs.Json.*;

import com.fasterxml.jackson.databind.JsonNode;

public class LoginController extends Controller {
    public static Result loginUsingFacebook()
    {
        return ok("Hello, world");
    }
}
