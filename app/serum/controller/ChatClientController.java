package serum.controller;

import play.*;
import play.mvc.*;

import serum.view.html.*;

public class ChatClientController extends Controller {
    public static Result index()
    {
        return ok(chatClient.render());
    }
}
