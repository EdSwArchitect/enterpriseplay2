package com.bsc.cache;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * Created by EdwinBrown on 4/26/2017.
 */
public class TheCacheTest {
    private static Logger log = LoggerFactory.getLogger(TheCacheTest.class);

    private static TheCache<String, String>cache;

    public class Person implements Serializable {
        /**
         *
         */

        private String firstName;
        private String lastName;
        private String address;

        /**
         *
         * @param firstName
         * @param lastName
         * @param address
         */
        public Person(String firstName, String lastName, String address) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Person person = (Person) o;

            if (!firstName.equals(person.firstName)) return false;
            if (!lastName.equals(person.lastName)) return false;
            return address.equals(person.address);
        }

        @Override
        public int hashCode() {
            int result = firstName.hashCode();
            result = 31 * result + lastName.hashCode();
            result = 31 * result + address.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    @BeforeClass
    public static void setup() {
        cache = new TheCache<String, String>(String.class, String.class, "myTest");

        log.info("Cache created");
    }

    @Test
    public void put() throws Exception {
        cache.put("lastName", "Brown");
        cache.put("firstName", "Edwin");
        cache.put("address", "1234 Main St");
        log.info("lastName: " + cache.get("lastName"));
        log.info("noKey: " + cache.get("noKey"));

        log.info("All: " + cache.getAll());
    }

    @Test
    public void testPerson() {
        TheCache<String, Person> pcache = new TheCache<String, Person>(String.class, Person.class, "personTest");

        pcache.put("edwin", new Person("Edwin", "Brown", "1234 Main St"));
        pcache.put("sabrina", new Person("Sabrina", "Brown", "1234 Main St"));
        pcache.put("janet", new Person("Janet", "Jackson", "1234 Coo Coo Lane"));

        log.info("Edwin: " + pcache.get("edwin"));
        log.info("noKey: " + pcache.get("noKey"));

        log.info("All: " + pcache.getAll());

        pcache.close();
    }

//    @Test
//    public void get() throws Exception {
//        log.info("lastName: " + cache.get("lastName"));
//        log.info("noKey: " + cache.get("noKey"));
//    }
//
//    @Test
//    public void get1() throws Exception {
//    }
//
//    @Test
//    public void get2() throws Exception {
//    }
//
//    @Test
//    public void get3() throws Exception {
//    }
//
//    @Test
//    public void clear() throws Exception {
//    }
//
//    @Test
//    public void close() throws Exception {
//    }

    @AfterClass
    public static void tearDown() {
        log.info("Shutting it down");
        cache.close();
    }
}