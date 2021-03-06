package dev.wtamaso.addressbook.util;

import dev.wtamaso.addressbook.entities.Address;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.util.ArrayList;
import java.util.Properties;

public class SessionFactoryUtil {

    public static SessionFactory getSessionFactory() {
        if (AddressBookContextListener.sessionFactory == null) {
            try {
                Configuration cfg = new Configuration();

                Properties p = new Properties();
                p.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
                p.put(Environment.URL, "jdbc:mysql://localhost:3306/address_book?useSSL=false");
                p.put(Environment.USER, "root");
                p.put(Environment.PASS, "123456");
                p.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
                p.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                p.put(Environment.HBM2DDL_AUTO, "update");

                ArrayList<Class> classes = new ArrayList<>();
                classes.add(Address.class);

                cfg.setProperties(p);
                classes.forEach(cfg::addAnnotatedClass);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();

                return cfg.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return AddressBookContextListener.sessionFactory;
    }

    public static Session session() {
        return getSessionFactory().openSession();
    }
}
