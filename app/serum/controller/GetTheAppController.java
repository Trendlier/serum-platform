package serum.controller;

import play.*;
import play.mvc.*;

import serum.view.html.*;

public class GetTheAppController extends Controller {
    public static Result index()
    {
        String appUrl = play.Play.application().configuration().getString("serum.app.url");
        String iosStoreUrl = play.Play.application().configuration().getString("serum.app.ios.store.url");
        String androidStoreUrl = play.Play.application().configuration().getString("serum.app.android.store.url");
        return ok(getTheApp.render(appUrl, iosStoreUrl, androidStoreUrl));
    }
}
