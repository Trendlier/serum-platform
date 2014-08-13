package serum.dao;

import java.util.*;

import org.junit.*;
import play.test.*;

public class DaoTest
{
    private static FakeApplication app;

    @Rule
    public final TransactionRule txnRule = new TransactionRule();

    @BeforeClass
    public static void setUpApp()
    {
        app = Helpers.fakeApplication();
        Helpers.start(app);
    }

    @AfterClass
    public static void tearDownApp()
    {
        Helpers.stop(app);
    }
}
