package serum.dao;

import java.util.*;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import play.db.ebean.*;

import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.Mockito.*;
import play.test.*;

import serum.model.*;

import serum.util.Facebook;

public class DaoTest
{
    protected static FakeApplication app;

    @BeforeClass
    public static void setUpClass()
    {
        final HashMap<String,String> settings = new HashMap<String, String>();
        // TODO: Set "db.default.*" to a different test database, preferably in-memory.
        settings.put("evolutionplugin", "disabled");
        app = Helpers.fakeApplication(settings);
        Helpers.start(app);
    }

    @AfterClass
    public static void tearDownClass()
    {
        Helpers.stop(app);
    }
}
