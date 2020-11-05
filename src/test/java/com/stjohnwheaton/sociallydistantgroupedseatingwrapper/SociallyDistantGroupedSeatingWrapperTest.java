/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stjohnwheaton.sociallydistantgroupedseatingwrapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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
//    @Test
//    public void testSeatGroups() {
//        System.out.println("getGroupDataFromAPI");
//        String signupId = "";
//        String userKey = "";
//        String expResult, result;
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        SociallyDistantGroupedSeatingWrapper instance = new SociallyDistantGroupedSeatingWrapper();
//        instance.setEventDate(LocalDate.parse("2020-09-02",dtf));
//        instance.getGroupDataFromAPI(signupId, userKey);
//        SeatingGroups seatingGroups = instance.getSeatingGroups();
//        System.out.println("test size");
//        //assertEquals(3,seatingGroups.size());
//        
//        System.out.println("test getGroupSize");
//        ArrayList<Integer> expGroupSizes = new ArrayList();
//        expGroupSizes.add(1);
//        expGroupSizes.add(2);
//        expGroupSizes.add(4);
//        
//        //assertEquals(expGroupSizes ,seatingGroups.getGroupSizes());
//        
//        System.out.println("test event date");
//        expResult = "20200902";
//        Object eventDate;
//        ArrayList<SeatingGroup> listOfGroups= seatingGroups.getGroups();
//        for (int i=0;i<listOfGroups.size();i++)
//        {
//            eventDate = listOfGroups.get(i).getGroupInfo().get("EventDate");
//            if (eventDate != null)
//            {
//                result = eventDate.toString();
//                assert(expResult.equalsIgnoreCase(result));
//            }
//            else
//            {
//                System.out.println("missing date");
//            }
//        }       
//       /* System.out.println("test event time");
//        expResult = "1900";
//           for (int i=0;i<listOfGroups.size();i++)
//        {
//            eventDate = listOfGroups.get(i).getGroupInfo().get("EventTime");
//            if (eventDate != null)
//            {
//                result = eventDate.toString();
//                assert(expResult.equalsIgnoreCase(result));
//            }
//            else
//            {
//                System.out.println("missing time");
//            }
//        } */
//        //assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        //fail("The test case is a prototype.");
//    }

    /**
     * Test of getRowDataFromFile method, of class SociallyDistantGroupedSeatingWrapper.
     */
    @Test
    public void testGetRowDataFromFile() {
        System.out.println("getRowDataFromFile");
        String filePath = "C:\\Source\\StJohnPewData.txt";
        SociallyDistantGroupedSeatingWrapper instance = new SociallyDistantGroupedSeatingWrapper();
        instance.getRowDataFromFile(filePath);
        
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
