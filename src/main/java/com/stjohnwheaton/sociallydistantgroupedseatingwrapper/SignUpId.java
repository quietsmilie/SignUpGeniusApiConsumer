/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stjohnwheaton.sociallydistantgroupedseatingwrapper;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.swing.JFrame;
/**
 *
 * @author Emilie Yonkers
 *         emilie.yonkers@gmail.com
 */
public class SignUpId extends javax.swing.JFrame {

    /**
     * Creates new form SignUpId
     */
    public SignUpId() {
        initComponents();
    }

    public static void main(String[] args)
    {
               javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
        private static void createAndShowGUI() {
        //Create and set up the window.
        
        SignUpId startForm = new SignUpId();
        
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");  
        LocalDate now = LocalDate.now();
        if (now.getDayOfWeek().equals(DayOfWeek.FRIDAY))
        {
            now = now.plusDays(2);
        }
        else
        {
            now = now.plusDays(1);
        }
        startForm.txtEventDate.setText(dtf.format(now));
        startForm.setTitle("St. John Wheaton Socially Distant Seating");
        
        try {
            getConfigDataFromFile(startForm);
        }
        catch (Exception e){
            e.printStackTrace(System.out);
        }
        
        
        startForm.validate();
        startForm.setVisible(true);
        
        startForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startForm.validate();
        
        //Display the window.
        startForm.pack();
        startForm.setVisible(true);

    }
        
                // try to read config info to pre-fill the form
    private static void getConfigDataFromFile(SignUpId formToFill)
    {
        String data;
        /* READ DATA FROM FILE */
 
       StringBuilder contentBuilder = new StringBuilder();
       Path filePath = Paths.get("./data/SociallyDistantGroupSeatingWrapperConfig.json");
 
        try (Stream<String> stream = Files.lines( filePath , StandardCharsets.UTF_8)) 
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
        parseConfigJson(formToFill, data);
        
    }

    private static void parseConfigJson(SignUpId formToFill, String jsonString) 
    {
            JsonParser parser = Json.createParser(new StringReader(jsonString));
            String keyName="";
            String value = "";
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch (event) {
                case KEY_NAME:
                    keyName = parser.getString();
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                    value = parser.getString();
                    switch (keyName)
                    {
                        case "signupid":
                            formToFill.txtSignUpId.setText(value);
                            break;
                        case "apikey":
                            formToFill.txtApiKey.setText(value);
                            break;
                        case "rowfile":
                            formToFill.txtFilePath.setText(value);
                            break;
                        case "outputfilelocation":
                            formToFill.txtOutputFilePath.setText(value);
                            break;
                        case "socialdistanceseats":
                            formToFill.txtSocialDistanceSeats.setText(value);
                            break;
                    }
                            
                    break;
                case START_OBJECT:
                case END_OBJECT:
                    break;
                case START_ARRAY:
                case END_ARRAY:
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_TRUE:
                    break;
            }
        }
    }//GEN-LAST:event_txtEventDateComponentShown

    private void txtOutputFilePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOutputFilePathActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOutputFilePathActionPerformed

    private void processSeating() {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate eventDate = LocalDate.parse(txtEventDate.getText(), dtf);
            String seatingMessage;
        
            if (txtFilePath.getText().isBlank() || txtOutputFilePath.getText().isBlank()
                    || txtOutputFilePath.getText().isBlank()) {
                MessageDisplay 
                missingInputWindow = new MessageDisplay(this,false);          
                missingInputWindow.setText("Error", "Please complete the missing fields", "OK", null);
                missingInputWindow.pack();
                missingInputWindow.setVisible(true);
            } else
            {
                SociallyDistantGroupedSeatingWrapper sc = new SociallyDistantGroupedSeatingWrapper(Integer.parseInt(txtSocialDistanceSeats.getText()));
                sc.setEventDate(eventDate);
                if (txtApiKey.getText().isBlank() || txtSignUpId.getText().isBlank())
                {
                    sc.getGroupDataFromFile(".//data//GroupData.json");
                }
                else
                {
                    sc.getGroupDataFromAPI(txtSignUpId.getText(), txtApiKey.getText());
                }
                sc.getRowDataFromFile(txtFilePath.getText());
                sc.setOutputFilePath(txtOutputFilePath.getText());
                seatingMessage = sc.SeatGroups();
                if (seatingMessage.isEmpty())
                {
                    MessageDisplay successWindow = new MessageDisplay(this, false);
                    successWindow.setText("Success", "Seating Files written successfully", "OK", null);
                    successWindow.pack();
                    successWindow.setVisible(true);   
                }
                else
                {
                    MessageDisplay partialSuccessWindow = new MessageDisplay(this, false);
                    partialSuccessWindow.setText("Partial Success", "One or more families were not seated\r\n OR there was an issue writing the seating files", "OK", seatingMessage);
                    partialSuccessWindow.pack();
                    partialSuccessWindow.setVisible(true);   
                }                    
                
            }
        } catch (Exception e) {
            MessageDisplay errorWindow = new MessageDisplay(this,false);
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] ste = e.getStackTrace();
            for (int i=0;i<ste.length; i++)
            {
                sb.append(ste[i].toString());
                sb.append("\r\n");
            }
            
            errorWindow.setText("Error", e.getMessage(), "OK", sb.toString());
            errorWindow.pack();
            errorWindow.setVisible(true);
            
            
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jFileChooser1 = new javax.swing.JFileChooser();
        jFileChooserOutput = new javax.swing.JFileChooser();
        pnlMain = new javax.swing.JPanel();
        btnFileBrowseOutput = new javax.swing.JButton();
        lblApiKey = new javax.swing.JLabel();
        lblEventDate = new javax.swing.JLabel();
        txtApiKey = new javax.swing.JTextField();
        txtEventDate = new javax.swing.JTextField();
        lblFamilyInfo = new javax.swing.JLabel();
        lblPewInfo = new javax.swing.JLabel();
        lblFilePath = new javax.swing.JLabel();
        btnFileBrowse = new javax.swing.JButton();
        txtFilePath = new javax.swing.JTextField();
        txtSignUpId = new javax.swing.JTextField();
        lblSignUpId = new javax.swing.JLabel();
        lblOutputPath = new javax.swing.JLabel();
        txtOutputFilePath = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        lblSocialDistanceSeats = new javax.swing.JLabel();
        txtSocialDistanceSeats = new javax.swing.JTextField();
        lblEventInfo = new javax.swing.JLabel();

        jFileChooserOutput.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);

        setName("topPanel"); // NOI18N

        btnFileBrowseOutput.setText("Browse...");
        btnFileBrowseOutput.setNextFocusableComponent(btnSubmit);

        lblApiKey.setText("API Key / User Key");

        lblEventDate.setText("Event Date (mm/dd/yyyy):");

        txtApiKey.setNextFocusableComponent(txtFilePath);
        txtApiKey.setPreferredSize(new java.awt.Dimension(120, 20));
        txtApiKey.setRequestFocusEnabled(false);

        txtEventDate.setNextFocusableComponent(lblSocialDistanceSeats);

        lblFamilyInfo.setText("Family Info (SignUpGenius)");
        lblFamilyInfo.setAlignmentX(1.0F);
        lblFamilyInfo.setAlignmentY(2.0F);
        lblFamilyInfo.setEnabled(false);
        lblFamilyInfo.setFocusable(false);

        lblPewInfo.setText("Pew Info (File)");
        lblPewInfo.setAlignmentX(1.0F);
        lblPewInfo.setAlignmentY(2.0F);
        lblPewInfo.setEnabled(false);
        lblPewInfo.setFocusable(false);

        lblFilePath.setText("File Name (with path):");

        btnFileBrowse.setText("Browse...");
        btnFileBrowse.setNextFocusableComponent(txtEventDate);
        btnFileBrowse.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnFileBrowseActionPerformed(evt);
            }
        });

        txtFilePath.setNextFocusableComponent(btnFileBrowse);

        txtSignUpId.setFocusCycleRoot(true);
        txtSignUpId.setNextFocusableComponent(txtApiKey);

        lblSignUpId.setText("Enter SignUp ID");

        lblOutputPath.setText("Output File Location (path only):");

        txtOutputFilePath.setNextFocusableComponent(btnFileBrowseOutput);

        btnSubmit.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSubmit.setText("Run");
        btnSubmit.setActionCommand("");
        btnSubmit.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnSubmit.setNextFocusableComponent(this);
        btnSubmit.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                btnSubmitMouseClicked(evt);
            }
        });
        btnSubmit.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                btnSubmitKeyReleased(evt);
            }
        });

        lblSocialDistanceSeats.setText("Social Distance Seats");

        txtSocialDistanceSeats.setNextFocusableComponent(txtOutputFilePath);

        lblEventInfo.setText("Event Info");
        lblEventInfo.setAlignmentX(1.0F);
        lblEventInfo.setAlignmentY(2.0F);
        lblEventInfo.setEnabled(false);
        lblEventInfo.setFocusable(false);

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtApiKey, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFamilyInfo)
                            .addComponent(lblSignUpId, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblApiKey, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSignUpId, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(63, 63, 63)
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlMainLayout.createSequentialGroup()
                                .addComponent(lblEventDate)
                                .addGap(18, 18, 18)
                                .addComponent(txtEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblEventInfo)
                            .addGroup(pnlMainLayout.createSequentialGroup()
                                .addGap(147, 147, 147)
                                .addComponent(txtSocialDistanceSeats, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblSocialDistanceSeats, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPewInfo)
                            .addGroup(pnlMainLayout.createSequentialGroup()
                                .addComponent(txtFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFileBrowse)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblOutputPath)
                                .addGroup(pnlMainLayout.createSequentialGroup()
                                    .addComponent(txtOutputFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btnFileBrowseOutput)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createSequentialGroup()
                                .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)))))
                .addContainerGap())
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFamilyInfo)
                    .addComponent(lblEventInfo))
                .addGap(1, 1, 1)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblEventDate)
                            .addComponent(txtEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSocialDistanceSeats, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtSocialDistanceSeats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addComponent(lblOutputPath)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtOutputFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFileBrowseOutput))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addComponent(lblSignUpId)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSignUpId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblApiKey)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtApiKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblPewInfo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblFilePath)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFileBrowse))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleParent(this);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSubmitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSubmitMouseClicked
            processSeating();
    }//GEN-LAST:event_btnSubmitMouseClicked

    private void btnSubmitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnSubmitKeyReleased
            processSeating();
    }//GEN-LAST:event_btnSubmitKeyReleased

    private void btnFileBrowseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnFileBrowseActionPerformed
    {//GEN-HEADEREND:event_btnFileBrowseActionPerformed
        if (jFileChooser1.showDialog(this,"Choose the file for Pew Data")>0)
        {
            txtFilePath.setText(jFileChooser1.getSelectedFile().getName());
        }
    }//GEN-LAST:event_btnFileBrowseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton btnFileBrowse;
    private static javax.swing.JButton btnFileBrowseOutput;
    private static javax.swing.JButton btnSubmit;
    private static javax.swing.JFileChooser jFileChooser1;
    private static javax.swing.JFileChooser jFileChooserOutput;
    private static javax.swing.JLabel lblApiKey;
    private static javax.swing.JLabel lblEventDate;
    private static javax.swing.JLabel lblEventInfo;
    private static javax.swing.JLabel lblFamilyInfo;
    private static javax.swing.JLabel lblFilePath;
    private static javax.swing.JLabel lblOutputPath;
    private static javax.swing.JLabel lblPewInfo;
    private static javax.swing.JLabel lblSignUpId;
    private static javax.swing.JLabel lblSocialDistanceSeats;
    private static javax.swing.JPanel pnlMain;
    private static javax.swing.JTextField txtApiKey;
    private static javax.swing.JTextField txtEventDate;
    private static javax.swing.JTextField txtFilePath;
    private static javax.swing.JTextField txtOutputFilePath;
    private static javax.swing.JTextField txtSignUpId;
    private static javax.swing.JTextField txtSocialDistanceSeats;
    // End of variables declaration//GEN-END:variables
}
