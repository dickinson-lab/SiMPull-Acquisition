/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpull_acquisition;


///////////////////////////////////////////////////////////////////////////////
//PROJECT:       Micro-Manager
//SUBSYSTEM:     mmstudio
//-----------------------------------------------------------------------------
//
// AUTHOR:       Nico Stuurman, 2019
//
// COPYRIGHT:    University of California, San Francisco, 2019
//
// LICENSE:      This file is distributed under the BSD license.
//               License text is included with the source distribution.
//
//               This file is distributed in the hope that it will be useful,
//               but WITHOUT ANY WARRANTY; without even the implied warranty
//               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import ij.process.LUT;
import java.io.IOException;
import java.lang.Math;
import org.micromanager.Studio;
import org.micromanager.data.Datastore;
import org.micromanager.data.Coordinates;
import org.micromanager.data.Coords;
import org.micromanager.data.DataProvider;
import org.micromanager.data.Image;
import org.micromanager.data.internal.DefaultImageJConverter;
import org.micromanager.display.DisplayWindow;


/**
 *
 * @author Nico
 * @modified by Dan Dickinson
 */

public final class ToImageJ {
   private Studio studio_;
   
   public ImagePlus toImageJ(Datastore dp, DisplayWindow display, boolean showImage) {
      final boolean copy = false;
      final boolean setProps = true;
      // TODO: UI to set copy, give option to only do partial data, and multiple positions
      
      //DataProvider dp = display.getDataProvider();
      Coords displayPosition = display.getDisplayPosition();
      int p = displayPosition.getP();
      
      ImagePlus iPlus = null;
      Image image = null;
      if (dp.getNumImages() == 1) {
         try {
            image = dp.getAnyImage();
            ImageProcessor iProc = DefaultImageJConverter.createProcessor(image, copy);
            iPlus = new ImagePlus(dp.getName() + "-ij", iProc);
            
         } catch (IOException ex) {
            // TODO: report error
         }
         if (setProps && iPlus != null && image != null) {
            setCalibration(iPlus, dp, image);
         }
         if (iPlus != null) {
            iPlus.show();   
            /* I can not figure out how to set zoom programmatically...
            iPlus.getCanvas().setMagnification(display.getZoom());
            iPlus.getWindow().pack();
            */
         }
      } else if (dp.getNumImages() > 1) {
         try {
            ImageStack imgStack = new ImageStack(dp.getAnyImage().getWidth(), 
                    dp.getAnyImage().getHeight());
            Coords.Builder cb = Coordinates.builder().c(0).t(0).p(0).z(0);
            int tmax = dp.getMaxIndices().getT();
            int zmax = dp.getMaxIndices().getZ();
            int cmax = dp.getMaxIndices().getC();
            for (int t = 0; t <= tmax; t++) {
               for (int z = 0; z <= zmax; z++) {
                  for (int c = 0; c <= cmax; c++) {
                     image = dp.getImage(cb.c(c).t(t).p(0).z(z).build());
                     ImageProcessor iProc = DefaultImageJConverter.createProcessor(image, copy);
                     imgStack.addSlice(iProc);
                  }
               }
            }
            iPlus = new ImagePlus(dp.getName() + "-ij");
            iPlus.setOpenAsHyperStack(true);
            iPlus.setStack(imgStack, dp.getMaxIndices().getC() + 1, 
                    dp.getMaxIndices().getZ() + 1, dp.getMaxIndices().getT() + 1);
            
            if (showImage) {
                int displayMode;
                switch (display.getDisplaySettings().getColorMode()) {
                   case COLOR: { displayMode = IJ.COLOR; break; }
                   case COMPOSITE: { displayMode = IJ.COMPOSITE; break; }
                   case GRAYSCALE: { displayMode = IJ.GRAYSCALE; break; }
                   default: { displayMode = IJ.GRAYSCALE; break; }
                }
                iPlus.setDisplayMode(displayMode);  
                CompositeImage ci = new CompositeImage(iPlus, displayMode);
                ci.setTitle(dp.getName() + "-ij");
                for (int c = 0; c <= dp.getMaxIndices().getC(); c++) {
                   ci.setChannelLut(
                           LUT.createLutFromColor(display.getDisplaySettings().getChannelColor(c)),
                           c + 1);
                }
                if (setProps && image != null) {
                   setCalibration(ci, dp, image);
                }
                ci.show();
                // would like to also copy the zoom....
            } 
            
            
         } catch (IOException ex) {
            // TODO: report
         }
         
      }
      return iPlus;
   }
   
   private void setCalibration(ImagePlus iPlus, DataProvider dp, Image image) {
      Calibration cal = new Calibration(iPlus);
      Double pSize = image.getMetadata().getPixelSizeUm();
      if (pSize != null) {
         cal.pixelWidth = pSize;
         cal.pixelHeight = pSize;
      }
      Double zStep = dp.getSummaryMetadata().getZStepUm();
      if (zStep != null) {
         cal.pixelDepth = zStep;
      }
      Double waitInterval = dp.getSummaryMetadata().getWaitInterval();
      if (waitInterval != null) {
         cal.frameInterval = waitInterval / 1000.0;  // MM in ms, IJ in s
      }
      cal.setUnit("micron");
      iPlus.setCalibration(cal);
   }

   public void setContext(Studio studio) {
      studio_ = studio;
   }

   public String getName() {
      return "To ImageJ...";
   }

   public String getHelpText() {
      return "Makes selected data available in ImageJ";
   }

   public String getVersion() {
      return "0.1";
   }

   public String getCopyright() {
     return "Copyright (c) Regents of the University of California";
   }
   
   
}