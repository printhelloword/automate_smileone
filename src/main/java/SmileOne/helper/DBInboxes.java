package SmileOne.helper;

import SmileOne.SmileOneBot;
import SmileOne.model.Inboxes;
import org.apache.logging.log4j.Level;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.Query;
import java.util.List;

public class DBInboxes {

    private static SessionFactory factory;

    static {
        factory = new Configuration().configure().buildSessionFactory();
    }

    public Session getSession() {
        return factory.openSession();
    }

    public Integer saveInbox(Inboxes inbox) {
        Integer generatedID=null;

        Session session = getSession();

        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            session.persist(inbox);

            tx.commit();

            generatedID = inbox.getId();

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return generatedID;

    }

    public boolean isTrxIdExists(String trxId) {

        Session session = getSession();
        boolean result=false;

        try {
            Query query = session.
                    createSQLQuery("select * from inboxes where trx_id=:trxId");
            query.setParameter("trxId", trxId);
            query.setMaxResults(1);

            List<Object[]> results = query.getResultList();
            if (!results.isEmpty()) {
                result=true;
            }

        }
        catch(Exception e) {
            e.printStackTrace();
//            SmileOneBot.logger.log(Level.FATAL, e.getMessage());
        }
        finally {
            session.close();
        }

        return result;
    }



}
