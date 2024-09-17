package dat;

import dat.config.HibernateConfig;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("person");

    }


}