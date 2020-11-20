package com.gsilvestro.contactmgr;

import com.gsilvestro.contactmgr.model.Contact;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;


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

        int id = save(contact);

        // Display a list of contacts before the update
        System.out.printf("%n%nBefore update%n%n");
        fetchAllContacts().stream()
                .forEach(System.out::println);

        // Get the persisted contact
        Contact contact1 =findContactById(id);

        // Update the contact
        contact1.setFirstName("Gennaro");

        // Persist the changes
        System.out.printf("%n%nUpdating...%n%n");
        update(contact1);
        System.out.printf("%n%nUpdate complete!%n%n");

        //Display a list of contacts after the update
        System.out.printf("%n%nAfter the update%n%n");
        fetchAllContacts().stream()
                .forEach(System.out::println);

        // Before running check which ids are present in the database
//        System.out.printf("%n%nDeleting the contact with id of 2");
//        Contact toBeDeleted = fetchAllContacts().stream()
//                .filter(e -> e.getId() == 2)
//                .findFirst()
//                .orElse(null);
//        delete(toBeDeleted);
//
//        System.out.printf("%n%nAfter the delete");
//        fetchAllContacts().stream()
//                .forEach(System.out::println);
    }

    private static List<Contact> fetchAllContacts() {
        // Open a session
        Session session = sessionFactory.openSession();

        // DEPRECATED: Create Criteria
        // Criteria criteria = session.createCriteria(Contact.class);

        // DEPRECATED: Get a list of Contact objects according to the Criteria object
        // List<Contact> contacts = criteria.list();

        // UPDATED: Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // UPDATED: Create CriteriaQuery
        CriteriaQuery<Contact> criteria = builder.createQuery(Contact.class);

        // UPDATED: Specify criteria root
        criteria.from(Contact.class);

        // UPDATED: Execute query
        List<Contact> contacts = session.createQuery(criteria).getResultList();

        // Close the session
        session.close();

        return contacts;
    }

    private static int save(Contact contact) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        // To support Hibernate transaction, we need to add a JTA dependency to our build.gradle
        session.beginTransaction();

        // Use the session to save the contact
        int id = (int)session.save(contact);

        // Commit the transaction
        // In the hibernate.cfg.xml we have added a property 'show_sql' and set it to 'true'
        // so that every time a transaction is executed we have a message.
        session.getTransaction().commit();

        // Close the session
        session.close();

        return id;
    }

    private static Contact findContactById(int id) {
        Session session = sessionFactory.openSession();

        Contact contact = session.get(Contact.class, id);

        session.close();

        return contact;
    }

    private static void update(Contact contact) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();

        session.update(contact);

        session.getTransaction().commit();

        session.close();

    }

    private static void delete(Contact contact) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(contact);
        session.getTransaction().commit();
        session.close();
    }

}
