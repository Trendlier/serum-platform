package serum.dao;

import org.junit.rules.MethodRule;
import org.junit.runners.model.Statement;
import org.junit.runners.model.FrameworkMethod;

import play.db.jpa.*;
import javax.persistence.*;

public class TransactionRule implements MethodRule
{
    private EntityManager em;
    protected EntityTransaction txn;

    public EntityTransaction getTransaction()
    {
        return txn;
    }

    @Override
    public Statement apply(final Statement statement, FrameworkMethod method, Object test)
    {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable
            {
                em = JPA.em("default");
                JPA.bindForCurrentThread(em);
                txn = em.getTransaction();
                txn.begin();
                try
                {
                    statement.evaluate();
                    txn.commit();
                }
                catch (Throwable t)
                {
                    try { txn.rollback(); } catch(Throwable t2) {}
                    throw t;
                }
                finally
                {
                    JPA.bindForCurrentThread(null);
                    if (em != null)
                    {
                        em.close();
                    }
                }
            }
        };
    }
}
