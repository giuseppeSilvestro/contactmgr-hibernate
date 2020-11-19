package com.gsilvestro.contactmgr;

import com.gsilvestro.contactmgr.model.Contact;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;


public class Application {
    // Hold a reusable reference to a SessionFactory (since we need only one)
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        // Create a StandardServiceRegistry
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {

        Contact contact = new Contact.ContactBuilder("Giuseppe", "Silvestro")
                .withEmail("cnslanc@live.com")
                .withPhone(123456789L)
                .build();

        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        // To support Hibernate transaction, we need to add a JTA dependency to our build.gradle
        session.beginTransaction();

        // Use the session to save the contact
        session.save(contact);

        // Commit the transaction
        // In the hibernate.cfg.xml we have added a property 'show_sql' and set it to 'true'
        // so that every time a transaction is executed we have a message.
        session.getTransaction().commit();

        // Close the session
        session.close();
    }
}
