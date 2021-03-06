/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.go.moh.oec.mpi;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.util.List;
import ke.go.moh.oec.PersonResponse;
import ke.go.moh.oec.RequestTypeId;
import ke.go.moh.oec.Person;
import ke.go.moh.oec.PersonRequest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jim Grace
 */
public class MpiTest {

    static Mpi mpi = new Mpi(); // Make it static so it won't reinitialize between tests.
    private static final SimpleDateFormat SIMPLE_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public MpiTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private String n(String s) { // Protect against nulls for printing.
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    private String n(Date d) { // Protect against nulls for printing.
        if (d == null) {
            return "";
        } else {
            return d.toString();
        }
    }

    private String n(Enum e) { // Protect against nulls for printing.
        if (e == null) {
            return "";
        } else {
            return e.name();
        }
    }

    /**
     * Test of getData method, of class Mpi.
     */
    @Test
    public void testFindPerson() {
        System.out.println("testFindPerson");

        int requestTypeId = RequestTypeId.FIND_PERSON_MPI;
        PersonRequest requestData = new PersonRequest();
        Person p = new Person();
        requestData.setPerson(p);
        Object result;
        PersonResponse pr;

        // Clan name that will not be found
        System.out.println("testFindPerson - Clan name that will not be found");
        p.setClanName("ThisClanNameWillNotBeFound");
        result = mpi.getData(requestTypeId, requestData);
        assertNotNull(result);
        assertSame(PersonResponse.class, result.getClass());
        pr = (PersonResponse) result;
        assertTrue(pr.isSuccessful());
        assertNull(pr.getPersonList());

        // Clan name having 8 matches in the first 100 people
        System.out.println("testFindPerson - Clan name returning 8 matches");
        p.setClanName("KONYANGO");
        result = mpi.getData(requestTypeId, requestData);
        assertNotNull(result);
        assertSame(PersonResponse.class, result.getClass());
        pr = (PersonResponse) result;
        assertTrue(pr.isSuccessful());
        List<Person> pList = pr.getPersonList();

        for (Person person : pList) {
            System.out.println("guid: " + person.getPersonGuid()
                    + " score: " + person.getMatchScore()
                    + " name: " + n(person.getFirstName()) + " " + n(person.getMiddleName()) + " " + n(person.getLastName()) + " [" + n(person.getOtherName()) + "]"
                    + " sex: " + n(person.getSex())
                    + " birth/death: " + n(person.getBirthdate()) + "/" + n(person.getDeathdate())
                    + " clan: " + n(person.getClanName())
                    + " mother: " + n(person.getMothersFirstName()) + " " + n(person.getMothersMiddleName()) + " " + n(person.getMothersLastName())
                    + " father: " + n(person.getFathersFirstName()) + " " + n(person.getFathersMiddleName()) + " " + n(person.getFathersLastName())
                    + " compHead: " + n(person.getCompoundHeadFirstName()) + " " + n(person.getCompoundHeadMiddleName()) + " " + n(person.getCompoundHeadLastName())
                    + " village: " + n(person.getVillageName())
                    + " marital: " + n(person.getMaritalStatus()));
        }

        assertNotNull(pList);
        int pCount = pList.size();
        assertEquals(8, pCount);
        Person p0 = pList.get(0);
        int score = p0.getMatchScore();
        assertEquals(100, score);
        
        System.out.println("testFindPerson - Search by birthdate");
        p = new Person(); // Start fresh
        p.setBirthdate(parseDate("1933-06-15"));
        String birthdate = p.getBirthdate().toString();
        requestData.setPerson(p);
        result = mpi.getData(requestTypeId, requestData);
        assertNotNull(result);
        assertSame(PersonResponse.class, result.getClass());
        pr = (PersonResponse) result;
        assertTrue(pr.isSuccessful());
        assertNotNull(pr.getPersonList());
        int listSize = pr.getPersonList().size();
        for (Person q : pr.getPersonList()) {
            score = q.getMatchScore();
            Date dq = q.getBirthdate();
            String ds = dq.toString();
            ds = dq.toString();
        }


    }

    /**
     * Test of getData method, request type CREATE_PERSON_MPI
     */
    @Test
    public void testCreatePerson() {
        System.out.println("testCreatePerson");

        Connection conn = Sql.connect();
        String s1 = "DELETE FROM person WHERE first_name = 'Cain' AND middle_name = 'Human' AND last_name = 'One';";
        String s2 = "DELETE FROM village WHERE village_name IN ('Eden', 'OutOfEden');";
        Sql.startTransaction(conn);
        Sql.execute(conn, s1);
        Sql.execute(conn, s2);
        Sql.commit(conn);

        int requestTypeId = RequestTypeId.CREATE_PERSON_MPI;
        PersonRequest requestData = new PersonRequest();
        Person p = new Person();
        requestData.setPerson(p);
        Object result;
        PersonResponse pr;

        p.setFirstName("Cain");
        p.setMiddleName("Human");
        p.setLastName("One");
        p.setMothersFirstName("Eve");
        p.setMothersMiddleName("Human");
        p.setMothersLastName("One");
        p.setFathersFirstName("Adam");
        p.setFathersMiddleName("Human");
        p.setFathersLastName("One");
        p.setCompoundHeadFirstName("God");
        p.setCompoundHeadMiddleName("The");
        p.setCompoundHeadLastName("Creator");
        p.setVillageName("Eden");
        p.setClanName("Human");
        result = mpi.getData(requestTypeId, requestData);
        assertNull(result);
    }

    /**
     * Test of getData method, request type CREATE_PERSON_MPI
     */
    @Test
    public void testModifyPerson() {
        System.out.println("testModifyPerson");

        int requestTypeId;
        PersonRequest requestData = new PersonRequest();
        Person p;
        List<Person> pList;
        int pCount;
        Object result;
        PersonResponse pr;

        // Find the person to modify.
        requestTypeId = RequestTypeId.FIND_PERSON_MPI;
        p = new Person();
        requestData.setPerson(p);
        p.setVillageName("Eden");
        result = mpi.getData(requestTypeId, requestData);
        assertNotNull(result);
        assertSame(PersonResponse.class, result.getClass());
        pr = (PersonResponse) result;
        assertTrue(pr.isSuccessful());

        pList = pr.getPersonList();
        assertNotNull(pList);
        pCount = pList.size();
        assertEquals(1, pCount);
        Person p0 = pList.get(0);
        assertEquals("Cain", p0.getFirstName());
        assertEquals("Human", p0.getMiddleName());
        assertEquals("One", p0.getLastName());

        // Modify the village name.
        requestTypeId = RequestTypeId.MODIFY_PERSON_MPI;
        p0.setVillageName("OutOfEden");
        requestData.setPerson(p0);
        result = mpi.getData(requestTypeId, requestData);
        assertNull(result); // MODIFY PERSON returns no result object.

        // Search for residents of village Eden -- should find none.
        requestTypeId = RequestTypeId.FIND_PERSON_MPI;
        p = new Person();
        p.setVillageName("Eden");
        requestData.setPerson(p);
        result = mpi.getData(requestTypeId, requestData);
        assertNotNull(result);
        assertSame(PersonResponse.class, result.getClass());
        pr = (PersonResponse) result;
        assertTrue(pr.isSuccessful());
        pList = pr.getPersonList();
        assertNull(pList);

        // Search for residents of village OutOfEden -- should find one.
        requestTypeId = RequestTypeId.FIND_PERSON_MPI;
        p = new Person();
        p.setVillageName("OutOfEden");
        requestData.setPerson(p);
        result = mpi.getData(requestTypeId, requestData);
        assertNotNull(result);
        assertSame(PersonResponse.class, result.getClass());
        pr = (PersonResponse) result;
        assertTrue(pr.isSuccessful());
        pList = pr.getPersonList();
        assertNotNull(pList);
        pCount = pList.size();
        assertEquals(1, pCount);

    }
    
        /**
     * Parses a date and time in MySQL string format into a <code>Date</code>
     *
     * @param sDateTime contains date and time
     * @return the date and time in <code>Date</code> format
     * Returns null if the date and time string was null.
     */
    private Date parseDate(String sDateTime) {
        Date returnDateTime = null;
        if (sDateTime != null) {
            try {
                returnDateTime = SIMPLE_DATE_TIME_FORMAT.parse(sDateTime);
            } catch (ParseException ex) {
                Logger.getLogger(MpiTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return returnDateTime;
    }

}