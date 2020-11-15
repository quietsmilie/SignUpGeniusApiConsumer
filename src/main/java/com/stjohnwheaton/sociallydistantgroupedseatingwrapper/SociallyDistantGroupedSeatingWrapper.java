/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stjohnwheaton.sociallydistantgroupedseatingwrapper;


import com.stjohnwheaton.sociallydistantgroupedseating.SeatingGroup;
import com.stjohnwheaton.sociallydistantgroupedseating.SeatingGroups;
import com.stjohnwheaton.sociallydistantgroupedseating.SeatingRow;
import com.stjohnwheaton.sociallydistantgroupedseating.SeatingRows;
import com.stjohnwheaton.sociallydistantgroupedseating.SociallyDistantGroupedSeatingArrangement;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Emilie Yonkers
 *         emilie.yonkers@gmail.com
 */
public class SociallyDistantGroupedSeatingWrapper {
    
    private static final String baseApiUri = "https://api.signupgenius.com/v2/k/";
    private static final String getSignupsApiPath = "signups/report/filled/";
    private SociallyDistantGroupedSeatingArrangement seating;
    private SeatingGroups seatingGroups;
    private SeatingRows seatingRows;
    private LocalDate dateToProcess = null;
    private String outputFilePath;
    TimeZone currentTimeZone;
    
    //private static String userKey = "dTNkQkdKLzhsSVlMMXU2dWg2NTFHZz09"; 
    
    public SociallyDistantGroupedSeatingWrapper()
    {
        seating = new SociallyDistantGroupedSeatingArrangement();
        seatingGroups = new SeatingGroups();
        seatingRows = new SeatingRows();
        currentTimeZone = TimeZone.getDefault();
    }
    
    public SociallyDistantGroupedSeatingWrapper(int sociallyDistantSeats)
    {
        seating = new SociallyDistantGroupedSeatingArrangement();
        seatingGroups = new SeatingGroups();
        seatingRows = new SeatingRows(sociallyDistantSeats);
        currentTimeZone = TimeZone.getDefault();
    }
    
    public void setEventDate(LocalDate eventDate)
    {
        dateToProcess = eventDate;
    }
    
    public LocalDate getEventDate()
    {
        return dateToProcess;
    }
    
    public void setOutputFilePath(String filePath)
    {
        outputFilePath = filePath;
    }

public SeatingGroups getSeatingGroups()
{
    return seatingGroups;
}

public SeatingRows getSeatingRows()
{
    return seatingRows;
}

public void setTimeZone(TimeZone timeZone)
{
    currentTimeZone = timeZone;
}
    public void getGroupDataFromAPI(String signupId, String userKey)
    {
        
        String name;
        StringBuilder uriToCall = new StringBuilder(baseApiUri);
        uriToCall.append(getSignupsApiPath);
        uriToCall.append(signupId);
        uriToCall.append("/?user_key=");
        uriToCall.append(userKey);
  
        getGroupDataFromFile("C:\\Source\\API_TestData.json");
        Client client;
        client = ClientBuilder.newClient();
        name = client.target(uriToCall.toString())
        .request(MediaType.APPLICATION_JSON)
       .get(String.class);
        
        parseGroupJson(name);
        
    }

    public void writeGroupDataToFile(String fileNameWithPath, String groupData)
    {
        /* WRITE DATA TO FILE */
        // faster testing by skipping API when working on non-API items
         try {
        String filePath = "C:\\Source\\API_TestData.json";
        
        Files.writeString(Paths.get(filePath), groupData, StandardCharsets.UTF_8);
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void getGroupDataFromFile(String fileNameWithPath)
    {
        /* READ DATA FROM FILE */
        // faster testing by skipping API when working on non-API items

        String filePath = fileNameWithPath;
        StringBuilder contentBuilder = new StringBuilder();
        String name;

        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        name = contentBuilder.toString();

        parseGroupJson(name);

    }
    
    private void parseGroupJson(String jsonString) 
    {
            JsonParser parser = Json.createParser(new StringReader(jsonString));
            String keyName="";
            String value = "";
            int arrayCount = 0;
            Hashtable<String, String> currentSeatingGroupInfo = new Hashtable<String, String>();
            int currentSeatingGroupSize = 0;
            boolean skipEntry = false;
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch (event) {
                case START_OBJECT:
                    currentSeatingGroupInfo = new Hashtable<String, String>();
                    currentSeatingGroupSize = 0;
                    skipEntry = false;
                    break;
                case END_OBJECT:
                    if (currentSeatingGroupInfo.size()>0 && currentSeatingGroupSize > 0 && !skipEntry)
                    {
                        SeatingGroup sg = new SeatingGroup(currentSeatingGroupSize,currentSeatingGroupInfo);
                        seatingGroups.add(sg);

                        //if (!currentSeatingGroupInfo.containsKey("EventDate") || !currentSeatingGroupInfo.containsKey("EventTime"))
                        //{
                        //    currentSeatingGroupInfo.containsKey("startdatetimestring");
                        //}
                    }
                    currentSeatingGroupInfo = new Hashtable<String, String>();
                    currentSeatingGroupSize = 0;
                    skipEntry = false;
                    break;
                case START_ARRAY:
                    arrayCount++;
                    break;
                case END_ARRAY:
                    arrayCount--;
                    break;
                case KEY_NAME:
                    keyName = parser.getString();
                    break;
                case VALUE_FALSE:
                    currentSeatingGroupInfo.put(keyName,"false");
                    break;
                case VALUE_NULL:
                    currentSeatingGroupInfo.put(keyName,null);
                    break;
                case VALUE_TRUE:
                    currentSeatingGroupInfo.put(keyName,"true");
                    break;
                case VALUE_STRING:
                   value = parser.getString();
                   currentSeatingGroupInfo.put(keyName,value);
                   
                   if (keyName.equals("startdatestring"))
                   {
                       DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
                       ZonedDateTime entryDateTime, convertedDateTime;
                       
                       LocalDateTime localDateTime;
                       LocalDate entryDate;
                       LocalTime entryTime;
                       //Calendar calendar = new GregorianCalendar();
                       entryDateTime = ZonedDateTime.parse(value,dtf);
                       convertedDateTime = entryDateTime.withZoneSameInstant(currentTimeZone.toZoneId());
                       
                       localDateTime = convertedDateTime.toLocalDateTime(); //LocalDateTime.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                       entryDate = localDateTime.toLocalDate();
                       entryTime = localDateTime.toLocalTime();
                       if (!entryDate.equals(dateToProcess) && dateToProcess!=null)
                       {
                           skipEntry = true;
                       }
                       else
                       {
                           DateTimeFormatter dtfDateString = DateTimeFormatter.ofPattern("yyyyMMdd");
                           DateTimeFormatter dtfTimeString = DateTimeFormatter.ofPattern("HHmm");
                           currentSeatingGroupInfo.put("EventDate",entryDate.format(dtfDateString));
                           currentSeatingGroupInfo.put("EventTime",entryTime.format(dtfTimeString));
                       }
                   }    
                   break; 
                case VALUE_NUMBER:
                    value = parser.getString();
                    currentSeatingGroupInfo.put(keyName,value);
                    if (keyName.equals("myqty"))
                    {
                        try
                        {
                            currentSeatingGroupSize = Integer.parseInt(value);
                        } catch (Exception e) {}
                    }
                    break;
            }
        }
    }
  

     public void getRowDataFromFile(String filePath)
    {
        String data;
        /* READ DATA FROM FILE */
 
       StringBuilder contentBuilder = new StringBuilder();
 
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
 
        data = contentBuilder.toString();
        
        //JsonReader stringData = Json.createReader( new StringReader(data));
        //JsonObject fullData = stringData.readObject();
        parseRowJson(data);
        
    }

    private void parseRowJson(String jsonString) 
    {
            JsonParser parser = Json.createParser(new StringReader(jsonString));
            String keyName="";
            String value = "";
            int arrayCount = 0;
            Hashtable<String, String> currentSeatingRowInfo = new Hashtable<String, String>();
            int currentSeatingRowSize = 0;
            int currentRowsFromFront = 1;
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch (event) {
                case START_OBJECT:
                    currentSeatingRowInfo = new Hashtable<String, String>();
                    currentSeatingRowSize=0;
                    break;
                case END_OBJECT:
                    if (currentSeatingRowInfo.size()>0 && currentSeatingRowSize > 0)
                    {
                        SeatingRow sr = new SeatingRow().setSize(currentSeatingRowSize).setInfo(currentSeatingRowInfo).setDistanceFromFront(currentRowsFromFront);
                        seatingRows.add(sr);

                        currentSeatingRowInfo = new Hashtable<String, String>();
                        currentSeatingRowSize = 0;
                    }

                    break;
                case START_ARRAY:
                    arrayCount++;
                    break;
                case END_ARRAY:
                    arrayCount--;
                    break;
                case KEY_NAME:
                    keyName = parser.getString();
                    break;
                case VALUE_FALSE:
                    currentSeatingRowInfo.put(keyName,"false");
                    break;
                case VALUE_NULL:
                    currentSeatingRowInfo.put(keyName,null);
                    break;
                case VALUE_TRUE:
                    currentSeatingRowInfo.put(keyName,"true");
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                    value = parser.getString();
                    currentSeatingRowInfo.put(keyName,value);
                    if (keyName.equals("RowNumber"))
                    {
                        try
                        {
                            currentRowsFromFront = Integer.parseInt(value);
                        } catch (Exception e) {}
                    }
                    if (keyName.equals("NumberSeats"))
                    {
                        try
                        {
                            currentSeatingRowSize = Integer.parseInt(value);
                        } catch (Exception e) {}
                    }
                    
                    break;
            }
        }
    }
    
    public String SeatGroups()
    {
        String currentEventDate = "";
        String currentEventTime = "";

        StringBuilder unSeatedGroups = new StringBuilder();
        StringBuilder fileWritingIssue = new StringBuilder();
        seating.setGroupSortKey("EventTime");
        Collections.sort(seatingGroups.getGroups(), seating.groupStringComparator);
        Hashtable currentSeatingGroupInfo;
        SeatingGroups sgToSeat = seatingGroups.getCopyForSubsets();
        SeatingGroups sgToFilter;
        SeatingRows srToFilter;
        int dailyEventCount = 0;
        String[] eventDateTime;
        ArrayList<String[]> eventDateTimesProcessed = new ArrayList();

        while (sgToSeat.getGroups().size() > 0)
        {

            sgToFilter = sgToSeat.getCopyForSubsets();
            srToFilter = seatingRows.getCopyForSubsets();
            if (eventDateTimesProcessed.size() > 0)
            {
                filterSeatingGroupsAlreadyProcessedEvents(sgToFilter, eventDateTimesProcessed);
            }
            currentSeatingGroupInfo = sgToFilter.getGroups().get(0).getGroupInfo();
            if (currentSeatingGroupInfo.containsKey("EventDate"))
            {
                currentEventDate = currentSeatingGroupInfo.get("EventDate").toString();
            }
            if (currentSeatingGroupInfo.containsKey("EventTime"))
            {
                currentEventTime = currentSeatingGroupInfo.get("EventTime").toString();
            }
            eventDateTime = new String[2];
            eventDateTime[0] = currentEventDate;
            eventDateTime[1] = currentEventTime;
            eventDateTimesProcessed.add(eventDateTime);
            if (currentEventDate.length() > 0)
            {
                if (currentEventTime.length() > 0)
                {
                    filterSeatingGroups(sgToFilter, "EventDate", currentEventDate);
                    filterSeatingGroups(sgToFilter, "EventTime", currentEventTime);
                    dailyEventCount += 1;

                    filterSeatingRows(srToFilter, "IncludeInArrangements", String.valueOf(dailyEventCount));
                } else
                {// shouldn't happen with this data set
                }
            }
            SociallyDistantGroupedSeatingArrangement seatingArrangement = new SociallyDistantGroupedSeatingArrangement(sgToFilter.getCopyForSubsets(), srToFilter);
            seatingArrangement.seatAllGroups();
        int unSeatedGroupsCount = seatingArrangement.getUnSeatedGroups().size();
            if (unSeatedGroupsCount > 0)
            {
                
                unSeatedGroups.append(unSeatedGroupsCount);
                if (unSeatedGroupsCount>1)
                {
                    unSeatedGroups.append(" unseated groups for event at ");
                }
                else
                {
                    unSeatedGroups.append(" unseated group for event at ");
                }
                unSeatedGroups.append(currentEventTime);
                unSeatedGroups.append("\r\n");
            }
            if (!writeSeatingGroupsFile(seatingArrangement))
            {                
                fileWritingIssue.append("Problem writing file for event at ");
                fileWritingIssue.append(currentEventTime);
                fileWritingIssue.append("\r\n");
            }
            sgToSeat.removeAll(sgToFilter);
        }
        return fileWritingIssue.toString() + unSeatedGroups.toString();
    }

    private void filterSeatingGroups(SeatingGroups sg, String infoKey, String infoValue) {
        Predicate<SeatingGroup> infoValueFilter;
        infoValueFilter = i -> (i.getGroupInfo().get(infoKey).toString().equals(infoValue));
        sg.removeIf(infoValueFilter.negate());
    }

    private void filterSeatingRows(SeatingRows sr, String infoKey, String infoValue) {
        Predicate<SeatingRow> infoValueFilter;
        infoValueFilter = i -> (((String) i.getInfo().get(infoKey)).contains(infoValue));
        sr.removeIf(infoValueFilter.negate());
    }

    private void filterSeatingGroupsAlreadyProcessedEvents(SeatingGroups sgToFilter, ArrayList<String[]> eventDateTimesProcessed)
    {
        eventDateTimesProcessed.forEach(i -> filterSeatingGroupsAlreadyProcessedEvent(sgToFilter, i));
    }
    
    private void filterSeatingGroupsAlreadyProcessedEvent(SeatingGroups sgToFilter, String[] eventDateTimeProcessed)
    {
        Predicate<SeatingGroup> eventDateFilter, eventTimeFilter;
        eventDateFilter = i -> (((String) i.getGroupInfo().get("EventDate")).contains(eventDateTimeProcessed[0]));
        eventTimeFilter = i -> (((String) i.getGroupInfo().get("EventTime")).contains(eventDateTimeProcessed[1]));
        sgToFilter.removeIf(eventDateFilter.and(eventTimeFilter));
    }
    
    private boolean writeSeatingGroupsFile(SociallyDistantGroupedSeatingArrangement seatingArrangement) {
        String filePath, fileName;
        StringBuilder sb = new StringBuilder();
        ArrayList<SeatingGroup> seatedGroups, unSeatedGroups;
        boolean returnValue = false;
        seatedGroups = seatingArrangement.getSeatedGroups();
        unSeatedGroups = seatingArrangement.getUnSeatedGroups();
        sb.append("seatingarrangement_");
        sb.append((String) seatedGroups.get(0).getGroupInfo().get("EventDate"));
        sb.append("_");
        sb.append((String) seatedGroups.get(0).getGroupInfo().get("EventTime"));
        sb.append(".csv");
        fileName = sb.toString();
        seatingArrangement.setGroupSortKey("lastname");
        Collections.sort(seatedGroups, seatingArrangement.groupStringComparator);
        Collections.sort(unSeatedGroups, seatingArrangement.groupStringComparator);

        try {
            File fileOutput = new File(Paths.get(outputFilePath, fileName).toUri());

            FileWriter fileWriter = new FileWriter(fileOutput);

            fileWriter.write("Family Name, First Name, Family Size, Pew #, Seats, Sign Up Comment\r\n");

            for (int i = 0; i < seatedGroups.size(); i++) {
                sb = new StringBuilder();
                sb.append(seatedGroups.get(i).getGroupInfo().get("lastname").toString());
                sb.append(",");
                sb.append(seatedGroups.get(i).getGroupInfo().get("firstname").toString());
                sb.append(",");
                sb.append(seatedGroups.get(i).getGroupInfo().get("myqty").toString());
                sb.append(",");
                sb.append(((Hashtable) seatedGroups.get(i).getGroupInfo().get("Row")).get("PewNumber").toString());
                sb.append(",[");
                sb.append(seatedGroups.get(i).getGroupInfo().get("Seats").toString());
                sb.append("],");
                sb.append(seatedGroups.get(i).getGroupInfo().get("comment").toString());

                fileWriter.write(sb.toString());
                fileWriter.write("\r\n");
            }
            if (unSeatedGroups.size() > 0) {
                fileWriter.write("\r\n\r\n\r\n\r\n\r\n");

                fileWriter.write("Unseated Families\r\nFamily Name, First Name, Family Size, Sign Up Comment\r\n");
                for (int i = 0; i < unSeatedGroups.size(); i++) {
                    sb = new StringBuilder();
                    sb.append(unSeatedGroups.get(i).getGroupInfo().get("lastname").toString());
                    sb.append(",");
                    sb.append(unSeatedGroups.get(i).getGroupInfo().get("firstname").toString());
                    sb.append(",");
                    sb.append(unSeatedGroups.get(i).getGroupInfo().get("myqty").toString());
                    sb.append(",");
                    sb.append(unSeatedGroups.get(i).getGroupInfo().get("comment").toString());

                    fileWriter.write(sb.toString());
                    fileWriter.write("\r\n");
                }
            }
            //fileWriter.flush();
            fileWriter.close();
            returnValue = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnValue;
    }


        
}
//        private static void printKey(String key) {
//            System.out.print(key + ": ");
//        }
//
//        private static void printValue(String x) {
//            System.out.println(x);
//        }
