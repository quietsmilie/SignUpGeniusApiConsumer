/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stjohnwheaton.signupgeniusapiconsumer;


import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import com.stjohnwheaton.sociallydistantgroupedseating.SeatingGroup;
import com.stjohnwheaton.sociallydistantgroupedseating.SociallyDistantGroupedSeatingArrangement;

/**
 *
 * @author Emilie Yonkers
 *         emilie.yonkers@gmail.com
 */
public class SignUpGeniusAPIConsumer {
    
    private static final String baseApiUri = "https://api.signupgenius.com/v2/k/";
    private static final String getSignupsApiPath = "signups/report/filled/";
    private SociallyDistantGroupedSeatingArrangement seating;
    
    //private static String userKey = "dTNkQkdKLzhsSVlMMXU2dWg2NTFHZz09"; 
    
    public SignUpGeniusAPIConsumer()
    {
        seating = new SociallyDistantGroupedSeatingArrangement();
    }
    
    
    
    public void getDataFromAPI(String signupId, String userKey)
    {
        String name;
//        StringBuilder uriToCall = new StringBuilder(baseApiUri);
//        uriToCall.append(getSignupsApiPath);
//        uriToCall.append(signupId);
//        uriToCall.append("/?user_key=");
//        uriToCall.append(userKey);
//        
//        Client client;
//        client = ClientBuilder.newClient();
//        name = client.target(uriToCall.toString())
//        .request(MediaType.APPLICATION_JSON)
//       .get(String.class);
        
        /* READ DATA FROM FILE */
 
       String filePath = "C:\\Source\\API_Response.txt";
       StringBuilder contentBuilder = new StringBuilder();
 
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
 
        name = contentBuilder.toString();
        
        /* WRITE DATA TO FILE */        
//        try
//        {
//        FileWriter fw = new FileWriter("C:\\Source\\API_Response.txt");
//        fw.write(name);
//        fw.flush();
//        fw.close();
//        }
//        catch (IOException e)
//        {
//            System.out.println(e.toString());
//        }
        
        JsonReader stringData = Json.createReader( new StringReader(name));
        JsonObject fullData = stringData.readObject();
        parseJson(name);
        
    }

    private void parseJson(String jsonString) 
    {
            JsonParser parser = Json.createParser(new StringReader(jsonString));
            String keyName="";
            String value = "";
            int arrayCount = 0;
            Hashtable<String, String> currentSeatingGroupInfo = new Hashtable<String, String>();
            int currentSeatingGroupSize = 0;
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch (event) {
                case START_OBJECT:
                    currentSeatingGroupInfo.clear();
                    currentSeatingGroupSize=0;
                    break;
                case END_OBJECT:
                    if (currentSeatingGroupInfo.size()>0 && currentSeatingGroupSize > 0)
                    {
                        SeatingGroup sg = new SeatingGroup(currentSeatingGroupSize,currentSeatingGroupInfo);
                        seating.AddSeatingGroup(sg);
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
                    currentSeatingGroupInfo.put(keyName,"false");
                    break;
                case VALUE_NULL:
                    currentSeatingGroupInfo.put(keyName,null);
                    break;
                case VALUE_TRUE:
                    currentSeatingGroupInfo.put(keyName,"true");
                    break;
                case VALUE_STRING:
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
  

//        private static void printKey(String key) {
//            System.out.print(key + ": ");
//        }
//
//        private static void printValue(String x) {
//            System.out.println(x);
//        }
}