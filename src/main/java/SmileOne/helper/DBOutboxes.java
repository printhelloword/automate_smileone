package SmileOne.helper;

import SmileOne.model.Inboxes;
import SmileOne.model.Outboxes;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DBOutboxes {

    private static SessionFactory factory;

    static {
        factory = new Configuration().configure().buildSessionFactory();
    }

    public Session getSession() {
        return factory.openSession();
    }

    public void saveOutbox(Outboxes outbox) {
        Integer output=null;

        Session session = getSession();

        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            session.persist(outbox);

            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

//        return output;
    }

}