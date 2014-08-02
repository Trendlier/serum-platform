package serum.dao;

import java.util.*;

import org.junit.*;
import play.test.*;

import play.db.jpa.*;
import javax.persistence.*;

public class DaoTest
{
    private static FakeApplication app;
    private static EntityManager em;

    protected EntityTransaction txn;

    @BeforeClass
    public static void setUpApp()
    {
        app = Helpers.fakeApplication();
        Helpers.start(app);
        em = JPA.em("default");
        JPA.bindForCurrentThread(em);
    }

    @Before
    public void setUpTransaction()
    {
        txn = em.getTransaction();
        txn.begin();
    }

    @After
    public void tearDownTransaction()
    {
        txn.commit();
    }

    @AfterClass
    public static void tearDownApp()
    {
        JPA.bindForCurrentThread(null);
        em.close();
        Helpers.stop(app);
    }
}
