package serum.controller;

import play.*;
import play.mvc.*;
import play.libs.F.*;
import play.libs.Json.*;

import com.fasterxml.jackson.databind.JsonNode;

import serum.view.html.*;

public class Application extends Controller {
    public static Result index()
    {
        return ok(main.render());
    }
}
