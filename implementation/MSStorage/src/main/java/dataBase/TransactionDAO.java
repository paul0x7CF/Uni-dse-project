package dataBase;

import main.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.UUID;

public class TransactionDAO {

    private static final Logger log = LogManager.getLogger(TransactionDAO.class);

    private SessionFactory sessionFactory = null;

    public TransactionDAO() {
        Configuration config = new Configuration().configure("hibernate.cfg.xml");
        config.addAnnotatedClass(DbTransaction.class);
        this.sessionFactory = config.buildSessionFactory();


    }

    public void create(DbTransaction transaction) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(transaction);
        session.getTransaction().commit();
    }

    public DbTransaction read(UUID id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        log.info("Reading transaction with id: " + id);
        DbTransaction transaction = session.get(DbTransaction.class, id);
        session.getTransaction().commit();
        return transaction;
    }

    public void update(DbTransaction transaction) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(transaction);
        session.getTransaction().commit();
    }

    public void delete(UUID id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        DbTransaction transaction = session.get(DbTransaction.class, id);
        session.delete(transaction);
        session.getTransaction().commit();
    }

}

