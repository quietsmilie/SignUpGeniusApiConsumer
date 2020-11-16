/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stjohnwheaton.sociallydistantgroupedseatingwrapper;

import com.stjohnwheaton.sociallydistantgroupedseating.SeatingGroup;
import com.stjohnwheaton.sociallydistantgroupedseating.SeatingGroups;
import com.stjohnwheaton.sociallydistantgroupedseating.SeatingRows;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author emili
 */
public class SociallyDistantGroupedSeatingWrapperTest {
    
    public SociallyDistantGroupedSeatingWrapperTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getGroupDataFromAPI method, of class SociallyDistantGroupedSeatingWrapper.
     */
    @Test
    public void testSeatGroups() {
        System.out.println("test Seat Groups (from test file)");
        String expResult, result;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SociallyDistantGroupedSeatingWrapper instance = new SociallyDistantGroupedSeatingWrapper();

        
        instance.setEventDate(LocalDate.parse("2020-09-02",dtf));
        instance.getGroupDataFromFile("C:\\Source\\API_testdata.txt");
        SeatingGroups seatingGroups = instance.getSeatingGroups();
        System.out.println("test size");
        assertEquals(3,seatingGroups.size());
        
        System.out.println("test getGroupSize");
        ArrayList<Integer> expGroupSizes = new ArrayList();
        expGroupSizes.add(1);
        expGroupSizes.add(2);
        expGroupSizes.add(4);
        
        assertEquals(expGroupSizes ,seatingGroups.getGroupSizes());
        
        System.out.println("test event date");
        expResult = "20200902";
        Object eventDate;
        ArrayList<SeatingGroup> listOfGroups= seatingGroups.getGroups();
        for (int i=0;i<listOfGroups.size();i++)
        {
            eventDate = listOfGroups.get(i).getGroupInfo().get("EventDate");
            if (eventDate != null)
            {
                result = eventDate.toString();
                assert(expResult.equalsIgnoreCase(result));
            }
            else
            {
                System.out.println("missing date");
            }
        }       

        System.out.println("test event time");
        expResult = "1900";
        result = "";
           for (int i=0;i<listOfGroups.size();i++)
        {
            eventDate = listOfGroups.get(i).getGroupInfo().get("EventTime");
            if (eventDate != null)
            {
                result = eventDate.toString();
                assert(expResult.equalsIgnoreCase(result));
            }
            else
            {
                System.out.println("missing time");
            }
        }
        assertEquals(expResult, result);


//        System.out.println("test seat groups");        
//        instance.getRowDataFromFile("C:\\Source\\PewTestData.json");
//        result = instance.SeatGroups();
//        assertEquals("",result);
        
         
    }

    /**
     * Test of getRowDataFromFile method, of class SociallyDistantGroupedSeatingWrapper.
     */
    @Test
    public void testGetRowDataFromFile() {
        System.out.println("getRowDataFromFile");
        String filePath = "C:\\Source\\PewTestData.json";
        SociallyDistantGroupedSeatingWrapper instance = new SociallyDistantGroupedSeatingWrapper();
        instance.getRowDataFromFile(filePath);
        
        SeatingRows seatingRows = instance.getSeatingRows();
        
        int [] expResult = new int[3];
        expResult[0] = 4;
        expResult[1] = 2;
        expResult[2] = 1;
        
        int[] result = seatingRows.getSeatingRowSizes();
        for (int i=0; i<result.length;i++)
        {
            assertEquals(expResult[i], result[i]);
        }
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
