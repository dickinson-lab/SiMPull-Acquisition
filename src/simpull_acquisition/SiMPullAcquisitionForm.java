/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpull_acquisition;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import static java.lang.Math.max;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import mmcorej.TaggedImage;

import org.micromanager.data.Datastore;
import org.micromanager.data.Image;
import org.micromanager.data.Coordinates;
import org.micromanager.Studio;
import org.micromanager.data.Coords; 
import org.micromanager.display.DisplayWindow;

import ij.ImagePlus;
import ij.io.FileSaver;

/**
 *
 * @author Dickinson Lab
 */
public class SiMPullAcquisitionForm extends JFrame { 
    private final Studio gui_;
    private final mmcorej.CMMCore mmc_;    
    
    //Create Dialog Box Components
    /* Old layout
    private final JLabel combine375label = new JLabel("<html>Combine w/375</html>");
    private final JLabel combine488label = new JLabel("<html>Combine w/488</html>");
    private final JLabel combine561label = new JLabel("<html>Combine w/561</html>");
    private final JLabel label_n375= new JLabel("<html>375 Frames:</html>");
    private final JFormattedTextField textField_n375= new JFormattedTextField();
    private final JLabel label_exp375= new JLabel("<html>375 Exposure:</html>");
    private final JFormattedTextField textField_exp375= new JFormattedTextField();
    private final JLabel label_n488= new JLabel("<html>488 Frames:</html>");
    private final JFormattedTextField textField_n488= new JFormattedTextField();
    private final JLabel label_exp488= new JLabel("<html>488 Exposure:</html>");
    private final JFormattedTextField textField_exp488= new JFormattedTextField();
    private final JLabel label_n561= new JLabel("<html>561 Frames:</html>");
    private final JFormattedTextField textField_n561= new JFormattedTextField();
    private final JLabel label_exp561= new JLabel("<html>561 Exposure:</html>");
    private final JFormattedTextField textField_exp561= new JFormattedTextField();
    private final JLabel label_561comb= new JLabel("<html>561</html>");
    private final JCheckBox check375_561= new JCheckBox();
    private final JCheckBox check488_561= new JCheckBox();
    private final JLabel label_n638= new JLabel("<html>638 Frames:</html>");
    private final JFormattedTextField textField_n638= new JFormattedTextField();
    private final JLabel label_exp638= new JLabel("<html>638 Exposure:</html>");
    private final JFormattedTextField textField_exp638= new JFormattedTextField();
    private final JLabel label_638comb= new JLabel("<html>638</html>");
    private final JCheckBox check375_638 = new JCheckBox();
    private final JCheckBox check488_638 = new JCheckBox();
    private final JCheckBox check561_638 = new JCheckBox(); */
    
    // New layout
    String[] none = {"<none>"};
    
    private final JLabel label_ch1 = new JLabel("<html>First Channel to Acquire:</html");
    private final JComboBox channel1 = new JComboBox(none);
    private final JLabel label_n_ch1= new JLabel("<html>Frames:</html>");
    private final JFormattedTextField textField_n_ch1= new JFormattedTextField();
    private final JLabel label_exp_ch1= new JLabel("<html>Exposure:</html>");
    private final JFormattedTextField textField_exp_ch1= new JFormattedTextField();

    private final JLabel label_ch2= new JLabel("<html>Second Channel to Acquire:</html");
    private final JComboBox channel2 = new JComboBox(none);
    private final JLabel label_n_ch2= new JLabel("<html>Frames:</html>");
    private final JFormattedTextField textField_n_ch2= new JFormattedTextField();
    private final JLabel label_exp_ch2= new JLabel("<html>Exposure:</html>");
    private final JFormattedTextField textField_exp_ch2= new JFormattedTextField();
    
    private final JLabel label_ch3 = new JLabel("<html>Third Channel to Acquire:</html");
    private final JComboBox channel3 = new JComboBox(none);
    private final JLabel label_n_ch3 = new JLabel("<html>Frames:</html>");
    private final JFormattedTextField textField_n_ch3 = new JFormattedTextField();
    private final JLabel label_exp_ch3 = new JLabel("<html>Exposure:</html>");
    private final JFormattedTextField textField_exp_ch3 = new JFormattedTextField();
    
    private final JLabel label_ch4 = new JLabel("<html>Fourth Channel to Acquire:</html");
    private final JComboBox channel4 = new JComboBox(none);
    private final JLabel label_n_ch4= new JLabel("<html>Frames:</html>");
    private final JFormattedTextField textField_n_ch4= new JFormattedTextField();
    private final JLabel label_exp_ch4= new JLabel("<html>Exposure:</html>");
    private final JFormattedTextField textField_exp_ch4= new JFormattedTextField();
    
    private final JLabel labelY= new JLabel("<html>Distance to move in Y<br/>(along channel)</html>");
    private final JFormattedTextField textField_Y= new JFormattedTextField();
    private final JLabel labelX= new JLabel("<html>Distance to move in X<br/>(corrects for non-vertical channel)</html>");
    private final JFormattedTextField textField_X= new JFormattedTextField();
    private final JLabel labeln= new JLabel("<html>Number of positions to acquire</html>");
    private final JFormattedTextField textField_n= new JFormattedTextField();
    private final JLabel label_dir = new JLabel("<html>Save Location:</html>");
    private final JFormattedTextField textField_dir = new JFormattedTextField();
    private final JButton dirButton = new JButton("...");
    private final JFileChooser dirChooser = new JFileChooser();
    private final JLabel label_name = new JLabel("<html>Name Prefix:</html>");
    private final JFormattedTextField textField_name = new JFormattedTextField();
    private final JButton button = new JButton("Start SiMPull Acquisition");
    private final JButton finishButton = new JButton("Stop after this Stage Position");
    //private final JButton abortButton = new JButton("Stop Immediately");
    
    private acquisitionWorker acquisition;
    
    public SiMPullAcquisitionForm(Studio gui) {
        gui_ = gui;
        mmc_ = gui_.core();
        makeDialog();
    }
    
    // SwingWorker to acquire SiMPull data
    class acquisitionWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            //Get info from dialog
            String ch1 = (String)channel1.getSelectedItem();
            String ch1_fileSuffix = ch1.replaceAll("\\+","-");
            int n_ch1 = Integer.parseInt(textField_n_ch1.getText());
            int exp_ch1 = Integer.parseInt(textField_exp_ch1.getText());
            
            String ch2 = (String)channel2.getSelectedItem();
            String ch2_fileSuffix = ch2.replaceAll("\\+","-");
            int n_ch2 = Integer.parseInt(textField_n_ch2.getText());
            int exp_ch2 = Integer.parseInt(textField_exp_ch2.getText());
            
            String ch3 = (String)channel3.getSelectedItem();
            String ch3_fileSuffix = ch3.replaceAll("\\+","-");
            int n_ch3 = Integer.parseInt(textField_n_ch3.getText());
            int exp_ch3 = Integer.parseInt(textField_exp_ch3.getText());
            
            String ch4 = (String)channel4.getSelectedItem();
            String ch4_fileSuffix = ch4.replaceAll("\\+","-");
            int n_ch4 = Integer.parseInt(textField_n_ch4.getText());
            int exp_ch4 = Integer.parseInt(textField_exp_ch4.getText());
            int yShift = Integer.parseInt(textField_Y.getText());
            int xShift = Integer.parseInt(textField_X.getText());
            int nPos = Integer.parseInt(textField_n.getText());
            String dir = textField_dir.getText();
            dir = dir.replaceAll("\\\\","/");
            String baseName = textField_name.getText();
            // Check for saving directory, create if necessary
            File folder = new File(dir);
            if (!folder.exists()) {
                    folder.mkdir();
            }
            
            /* Old layout
            // Name channels
            String ch375 = "375";
            String ch488 = "488";
            String ch561 = "561";
            String ch638 = "638";
            String ch375_561 = "375+561";
            String ch375_638 = "375+638";
            String ch488_561 = "488+561";
            String ch488_638 = "488+638";
            String ch561_638 = "561+638"; */
                        
            //Get Stage Info
            String XYStage = mmc_.getXYStageDevice();
            double x0 = mmc_.getXPosition(XYStage);
            double y0 = mmc_.getYPosition(XYStage);
            //Determine Name to Save
            String posName = baseName;
            int b=1;
            for (int a=0; a<nPos; a++) {
                mmc_.setXYPosition(XYStage, x0 + a*xShift, y0 + a*yShift);
                mmc_.waitForSystem();
                mmc_.sleep(10000); //Wait 10 s for system to settle after stage movement
                //Figure out the first position name; check for existing positions in the dataset
                posName = baseName + "_" + String.format("%02d",new Object[]{a+b});
                File im_ch1 = new File(dir+"/"+posName+"_"+ch1_fileSuffix+".tif");
                File im_ch2 = new File(dir+"/"+posName+"_"+ch2_fileSuffix+".tif");
                File im_ch3 = new File(dir+"/"+posName+"_"+ch3_fileSuffix+".tif");
                File im_ch4 = new File(dir+"/"+posName+"_"+ch4_fileSuffix+".tif");
                while ( im_ch1.exists() || im_ch2.exists() || im_ch3.exists() || im_ch4.exists() ) {
                    b++;
                    posName = baseName + "_" + String.format("%02d",new Object[]{a+b});
                    im_ch1 = new File(dir+"/"+posName+"_"+ch1_fileSuffix+".tif");
                    im_ch2 = new File(dir+"/"+posName+"_"+ch2_fileSuffix+".tif");
                    im_ch3 = new File(dir+"/"+posName+"_"+ch3_fileSuffix+".tif");
                    im_ch4 = new File(dir+"/"+posName+"_"+ch4_fileSuffix+".tif");
                }
                        
                /* Old layout
                File im375 = new File(dir+"/"+posName+"_375.tif");
                File im488 = new File(dir+"/"+posName+"_488.tif");
                File im561 = new File(dir+"/"+posName+"_561.tif");
                File im638 = new File(dir+"/"+posName+"_638.tif");
                File im375_561 = new File(dir+"/"+posName+"_375-561.tif");
                File im375_638 = new File(dir+"/"+posName+"_375-638.tif");
                File im488_561 = new File(dir+"/"+posName+"_488-561.tif");
                File im488_638 = new File(dir+"/"+posName+"_488-638.tif");
                File im561_638 = new File(dir+"/"+posName+"_561-638.tif");
                while ( im375.exists() || im488.exists() || im561.exists() || im638.exists() || im375_561.exists() || im375_638.exists() || im488_561.exists() || im488_638.exists() || im561_638.exists() ) {
                        b++;
                        posName = baseName + "_" + String.format("%02d",new Object[]{a+b});
                        im375 = new File(dir+"/"+posName+"_375.tif");
                        im488 = new File(dir+"/"+posName+"_488.tif");
                        im561 = new File(dir+"/"+posName+"_561.tif");
                        im638 = new File(dir+"/"+posName+"_638.tif");
                        im375_561 = new File(dir+"/"+posName+"_375-561.tif");
                        im375_638 = new File(dir+"/"+posName+"_375-638.tif");
                        im488_561 = new File(dir+"/"+posName+"_488-561.tif");
                        im488_638 = new File(dir+"/"+posName+"_488-638.tif");
                        im561_638 = new File(dir+"/"+posName+"_561-638.tif");
                } */
                
                //Acquire First Channel
                if ( n_ch1>0 && !ch1.equals(none)) {
                    String acqName = posName+"_"+ch1_fileSuffix;
                    acquireChannel(ch1, n_ch1, exp_ch1, acqName, dir);
                }
                
                //Acquire Second Channel
                if ( n_ch2>0 && !ch2.equals(none)) {
                    String acqName = posName+"_"+ch2_fileSuffix;
                    acquireChannel(ch2, n_ch2, exp_ch2, acqName, dir);
                }
                
                //Acquire Third Channel
                if ( n_ch3>0 && !ch3.equals(none)) {
                    String acqName = posName+"_"+ch3_fileSuffix;
                    acquireChannel(ch3, n_ch3, exp_ch3, acqName, dir);
                }
                
                //Acquire Fourth Channel
                if ( n_ch4>0 && !ch4.equals(none)) {
                    String acqName = posName+"_"+ch4_fileSuffix;
                    acquireChannel(ch4, n_ch4, exp_ch4, acqName, dir);
                }
                
                /* Old layout
                //Acquire 638 Channel
                if ( n638>0 && !check375_638.isSelected() && !check488_638.isSelected() && !check561_638.isSelected() ) {
                    String acqName = posName+"_638";
                    acquireChannel(ch638, n638, exp638, acqName, dir);
                }
                //Acquire 561 Channel
                if ( n561>0 && !check375_561.isSelected() && !check488_561.isSelected() && !check561_638.isSelected() ) {
                    String acqName = posName+"_561";
                    acquireChannel(ch561, n561, exp561, acqName, dir);
                }
                //Acquire 488 Channel
                if ( n488>0 && !check488_638.isSelected() && !check488_561.isSelected() ) {
                    String acqName = posName+"_488";
                    acquireChannel(ch488, n488, exp488, acqName, dir);
                }
                //Acquire 375 Channel
                if ( n375>0 && !check375_638.isSelected() && !check375_561.isSelected() ) {
                    String acqName = posName+"_375";
                    acquireChannel(ch375, n375, exp375, acqName, dir);
                }
                //Acquire 561+638
                if ( check561_638.isSelected() ) {
                    String acqName = posName+"_561-638";
                    acquireChannel(ch561_638, max(n561,n638), exp561, acqName, dir);
                }
                //Acquire 488+638
                if ( check488_638.isSelected() ) {
                    String acqName = posName+"_488-638";
                    acquireChannel(ch488_638, max(n488,n638), exp488, acqName, dir);
                }
                //Acquire 488+561
                if ( check488_561.isSelected() ) {
                    String acqName = posName+"_488-561";
                    acquireChannel(ch488_561, max(n488,n561), exp488, acqName, dir);
                }
                //Acquire 375+638
                if ( check375_638.isSelected() ) {
                    String acqName = posName+"_375-638";
                    acquireChannel(ch375_638, max(n375,n638), exp375, acqName, dir);
                }
                //Acquire 375+561
                if ( check375_561.isSelected() ) {
                    String acqName = posName+"_375-561";
                    acquireChannel(ch375_561, max(n375,n561), exp375, acqName, dir);
                } */

                if (isCancelled() ) {
                        break;
                }
            }
            return null;
        }
    }        
    
    // Method to acquire a single channel of imaging data
    private void acquireChannel(String ch, int n, int exp, String acqName, String dir) {
        try {
            // Select Configuration
            mmc_.setConfig(" ImagingChannel", ch);
            
            // Initialize Acquisition
            Datastore store = gui_.data().createRAMDatastore();
            DisplayWindow display = gui_.displays().createDisplay(store);
            mmc_.setExposure(exp);
            
            // Perform Acquisition
            // Arguments are the number of images to collect, the amount of time to wait
            // between images, and whether or not to halt the acquisition if the
            // sequence buffer overflows.
            mmc_.startSequenceAcquisition(n, 0, false);
            // Set up a Coords.CoordsBuilder for applying coordinates to each image.
            //Coords.CoordsBuilder builder = gui_.data().getCoordsBuilder();
            Coords.Builder builder = Coordinates.builder();
            int frame = 0;
            while ( mmc_.getRemainingImageCount() > 0 || mmc_.isSequenceRunning(mmc_.getCameraDevice()) ) {
                if (mmc_.getRemainingImageCount() > 0) {
                    TaggedImage tagged = mmc_.popNextTaggedImage();
                    Image image = gui_.data().convertTaggedImage(tagged,builder.c(0).t(frame).p(0).z(0).build(), null);
                    store.putImage(image);
                    frame++;
                } else {
                    mmc_.sleep(Math.min(0.5 * exp, 20));
                }
            }
            mmc_.stopSequenceAcquisition();
            //ij.IJ.setSlice(1);

            // Save and close
            ToImageJ dump = new ToImageJ();
            ImagePlus iPlus = dump.toImageJ(store, display, false);
            
            String savePath = dir+"/"+acqName+".tif";
            FileSaver saver = new FileSaver(iPlus);
            saver.saveAsTiffStack(savePath);
            iPlus.close();
            //ij.IJ.saveAs("Tiff",dir+"/"+acqName+".tif");
            //ij.IJ.run("Close");
            //store.save(Datastore.SaveMode.MULTIPAGE_TIFF, savePath);
            gui_.displays().closeDisplaysFor(store);
            store.close();
            System.gc(); //Clean up
        } catch (Exception ex) {
            Logger.getLogger(SiMPullAcquisitionForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void makeDialog() {
        // Set Default Values
        textField_n_ch1.setValue(0);
        textField_n_ch1.setColumns(4);
        textField_n_ch1.setEditable(true);
        textField_exp_ch1.setValue(50);
        textField_exp_ch1.setColumns(4);
        textField_exp_ch1.setEditable(true);
        textField_n_ch2.setValue(0);
        textField_n_ch2.setColumns(4);
        textField_n_ch2.setEditable(true);
        textField_exp_ch2.setValue(50);
        textField_exp_ch2.setColumns(4);
        textField_exp_ch2.setEditable(true);
        textField_n_ch3.setValue(0);
        textField_n_ch3.setColumns(4);
        textField_n_ch3.setEditable(true);
        textField_exp_ch3.setValue(50);
        textField_exp_ch3.setColumns(4);
        textField_exp_ch3.setEditable(true);
        textField_n_ch4.setValue(0);
        textField_n_ch4.setColumns(4);
        textField_n_ch4.setEditable(true);
        textField_exp_ch4.setValue(50);
        textField_exp_ch4.setColumns(4);
        textField_exp_ch4.setEditable(true);
        textField_Y.setValue(150);
        textField_Y.setColumns(4);
        textField_Y.setEditable(true);
        textField_X.setValue(0);
        textField_X.setColumns(4);
        textField_X.setEditable(true);
        textField_n.setValue(20);
        textField_n.setColumns(4);
        textField_n.setEditable(true);
        textField_dir.setColumns(20);
        textField_dir.setEditable(true);
        dirChooser.setApproveButtonText("Select Directory");
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        textField_name.setColumns(20);
        textField_name.setEditable(true);      

        // Populate drop-down menus
        mmcorej.StrVector channelGroupVector = mmc_.getAvailableConfigGroups();
        String[] groupList =channelGroupVector.toArray();
        mmcorej.StrVector channelVector = mmc_.getAvailableConfigs(groupList[0]);
        String[] channelList = channelVector.toArray();
        for (String channel:channelList) {
            channel1.addItem(channel);
            channel2.addItem(channel);
            channel3.addItem(channel);
            channel4.addItem(channel);
        }
        
        
        // Arrange GUI window
        setTitle("SiMPull Acquisition");
        setSize(550, 750);
        setLocation(0, 500);
        Container cp = getContentPane();
        cp.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridwidth = 1;
        /* old layout 
        c.gridx = 5;
        c.gridy = 0;
        cp.add(combine375label,c);
        c.gridx = 6;
        cp.add(combine488label,c);
        c.gridx = 7;
        cp.add(combine561label,c); */
        c.gridx = 0;
        c.gridy = 0;
        cp.add(label_ch1,c);
        c.gridy = 1;
        cp.add(channel1,c);
        c.gridx = 1;
        cp.add(label_n_ch1,c);
        c.gridx = 2;
        cp.add(textField_n_ch1,c);
        c.gridx = 3;
        cp.add(label_exp_ch1,c);
        c.gridx = 4;
        cp.add(textField_exp_ch1,c);
        c.gridx = 0;
        c.gridy = 2;
        cp.add(label_ch2,c);
        c.gridy = 3;
        cp.add(channel2,c);
        c.gridx = 1;
        cp.add(label_n_ch2,c);
        c.gridx = 2;
        cp.add(textField_n_ch2,c);
        c.gridx = 3;
        cp.add(label_exp_ch2,c);
        c.gridx = 4;
        cp.add(textField_exp_ch2,c);
        c.gridx = 0;
        c.gridy = 4;
        cp.add(label_ch3,c);
        c.gridy = 5;
        cp.add(channel3,c);
        c.gridx = 1;
        cp.add(label_n_ch3,c);
        c.gridx = 2;
        cp.add(textField_n_ch3,c);
        c.gridx = 3;
        cp.add(label_exp_ch3,c);
        c.gridx = 4;
        cp.add(textField_exp_ch3,c);
        /* Old layout
        c.gridx = 4; 
        cp.add(label_561comb,c);
        c.gridx = 5;
        cp.add(check375_561,c);
        c.gridx = 6;
        cp.add(check488_561,c); */
        c.gridx = 0;
        c.gridy = 6;
        cp.add(label_ch4,c);
        c.gridy = 7;
        cp.add(channel4,c);
        c.gridx = 1;
        cp.add(label_n_ch4,c);
        c.gridx = 2;
        cp.add(textField_n_ch4,c);
        c.gridx = 3;
        cp.add(label_exp_ch4,c);
        c.gridx = 4;
        cp.add(textField_exp_ch4,c);
       /* Old layout
        c.gridx = 4;
        cp.add(label_638comb,c);
        c.gridx = 5;
        cp.add(check375_638,c);
        c.gridx = 6;
        cp.add(check488_638,c);
        c.gridx = 7;
        cp.add(check561_638,c); */
        c.gridy = 8;
        c.gridx = 0;
        c.gridwidth = 2;
        cp.add(labelY,c);
        c.gridx = 2;
        cp.add(textField_Y,c);
        c.gridy = 9;
        c.gridx = 0;
        cp.add(labelX,c);
        c.gridx = 2;
        cp.add(textField_X,c);
        c.gridy = 10;
        c.gridx = 0;
        cp.add(labeln,c);
        c.gridx = 2;
        cp.add(textField_n,c);
        c.gridy = 11;
        c.gridx = 0;
        c.gridwidth = 1;
        cp.add(label_dir,c);
        c.gridx = 1;
        c.gridwidth = 2;
        cp.add(textField_dir,c);
        c.gridx = 3;
        c.gridwidth = 1;
        cp.add(dirButton,c);
        c.gridy = 12;
        c.gridx = 0;
        c.gridwidth = 1;
        cp.add(label_name,c);
        c.gridx = 1;
        c.gridwidth = 2;
        cp.add(textField_name,c);
        c.gridy = 13;
        c.gridx = 0;
        c.gridwidth = 4;
        c.anchor = GridBagConstraints.CENTER;
        cp.add(button,c);
        c.gridy = 14;
        c.gridwidth = 4;
        cp.add(finishButton,c);
        //c.gridx = 2;
        //cp.add(abortButton,c);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        
        /* Old layout
        // Controls Checkbox behavior
        check375_561.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent eText) {
               if (check375_561.isSelected()) {
                   check375_638.setSelected(false);
                   check488_561.setSelected(false);
               }
           }
        });
        
        check488_561.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent eText) {
               if (check488_561.isSelected()) {
                   check375_561.setSelected(false);
                   check488_638.setSelected(false);
               }
           }
        });
        
        check375_638.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent eText) {
               if (check375_638.isSelected()) {
                   check375_561.setSelected(false);
                   check488_638.setSelected(false);
                   check561_638.setSelected(false);
               }
           }
        });
        
        check488_638.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent eText) {
               if (check488_638.isSelected()) {
                   check488_561.setSelected(false);
                   check375_638.setSelected(false);
                   check561_638.setSelected(false);
               }
           }
        });
        
        check561_638.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent eText) {
               if (check561_638.isSelected()) {
                   check375_561.setSelected(false);
                   check375_638.setSelected(false);
                   check488_638.setSelected(false);
               }
           }
        }); */
        
        // Directory Chooser button
        dirButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent eText) {
                if (dirChooser.showOpenDialog(SiMPullAcquisitionForm.this) == JFileChooser.APPROVE_OPTION) {
                    File dir = dirChooser.getSelectedFile();
                    textField_dir.setValue(dir.getPath());
                }
            }
        });
                
        // Acquire SiMPull data on button press
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent eText) {
                
                // These if statements make sure user hasn't done something silly
                boolean ok = true;
                int n_ch1 = Integer.parseInt(textField_n_ch1.getText());
                int exp_ch1 = Integer.parseInt(textField_exp_ch1.getText());
                int n_ch2 = Integer.parseInt(textField_n_ch2.getText());
                int exp_ch2 = Integer.parseInt(textField_exp_ch2.getText());
                int n_ch3 = Integer.parseInt(textField_n_ch3.getText());
                int exp_ch3 = Integer.parseInt(textField_exp_ch3.getText());
                int n_ch4 = Integer.parseInt(textField_n_ch4.getText());
                int exp_ch4 = Integer.parseInt(textField_exp_ch4.getText());
                String[] options = {"Cancel", "Ok"};
                /* Old layout
                if ( check375_561.isSelected() ) {
                    if ( n375 != n561 ) {
                        int ans = JOptionPane.showOptionDialog(null,
                                "Different numbers of frames are set for te two wavelengths you want to combine. The greater number of frames will be acquired.",
                                "Heads Up",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                        if (ans == 0) {
                            ok = false;
                        }
                    }
                    if ( exp375 != exp561 ) {
                        JOptionPane.showMessageDialog(null,"Exposure times must be the same for both wavelengths you choose to combine.","Error",JOptionPane.ERROR_MESSAGE);
                        ok = false;
                    }
                }
                if ( check488_561.isSelected() ) {
                    if ( n488 != n561 ) {
                        int ans = JOptionPane.showOptionDialog(null,
                                "Different numbers of frames are set for te two wavelengths you want to combine. The greater number of frames will be acquired.",
                                "Heads Up",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                        if (ans == 0) {
                            ok = false;
                        }
                    }
                    if ( exp488 != exp561 ) {
                        JOptionPane.showMessageDialog(null,"Exposure times must be the same for both wavelengths you choose to combine.","Error",JOptionPane.ERROR_MESSAGE);
                        ok = false;
                    }
                }
                if ( check375_638.isSelected() ) {
                    if ( n375 != n638 ) {
                        int ans = JOptionPane.showOptionDialog(null,
                                "Different numbers of frames are set for te two wavelengths you want to combine. The greater number of frames will be acquired.",
                                "Heads Up",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                        if (ans == 0) {
                            ok = false;
                        }
                    }
                    if ( exp375 != exp638 ) {
                        JOptionPane.showMessageDialog(null,"Exposure times must be the same for both wavelengths you choose to combine.","Error",JOptionPane.ERROR_MESSAGE);
                        ok = false;
                    }
                }
                if ( check488_638.isSelected() ) {
                    if ( n488 != n638 ) {
                        int ans = JOptionPane.showOptionDialog(null,
                                "Different numbers of frames are set for te two wavelengths you want to combine. The greater number of frames will be acquired.",
                                "Heads Up",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                        if (ans == 0) {
                            ok = false;
                        }
                    }
                    if ( exp488 != exp638 ) {
                        JOptionPane.showMessageDialog(null,"Exposure times must be the same for both wavelengths you choose to combine.","Error",JOptionPane.ERROR_MESSAGE);
                        ok = false;
                    }                
                }
                if ( check561_638.isSelected() ) {
                    if ( n561 != n638 ) {
                        int ans = JOptionPane.showOptionDialog(null,
                                "Different numbers of frames are set for te two wavelengths you want to combine. The greater number of frames will be acquired.",
                                "Heads Up",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
                        if (ans == 0) {
                            ok = false;
                        }
                    }
                    if ( exp561 != exp638 ) {
                        JOptionPane.showMessageDialog(null,"Exposure times must be the same for both wavelengths you choose to combine.","Error",JOptionPane.ERROR_MESSAGE);
                        ok = false;
                    }                
                } */
                
                // If we're ok, go!
                if (ok) {
                    acquisition = new acquisitionWorker();
                    acquisition.execute();	
                } 
            }
        });

        //Finish Button
        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent eText) {
        	acquisition.cancel(false);
            }
        });
       
    }

}
