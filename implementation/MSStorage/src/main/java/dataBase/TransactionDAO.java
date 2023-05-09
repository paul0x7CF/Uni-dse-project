package dataBase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import sendable.Transaction;

import java.util.UUID;

public class TransactionDAO {

    private final SessionFactory sessionFactory;

    public TransactionDAO() {
        Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
        configuration.addAnnotatedClass(Transaction.class);
        this.sessionFactory = configuration.buildSessionFactory();
    }

    public void create(Transaction transaction) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(transaction);
        session.getTransaction().commit();
    }

    public Transaction read(UUID id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Transaction transaction = session.get(Transaction.class, id);
        session.getTransaction().commit();
        return transaction;
    }

    public void update(Transaction transaction) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(transaction);
        session.getTransaction().commit();
    }

    public void delete(UUID id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Transaction transaction = session.get(Transaction.class, id);
        session.delete(transaction);
        session.getTransaction().commit();
    }

}

