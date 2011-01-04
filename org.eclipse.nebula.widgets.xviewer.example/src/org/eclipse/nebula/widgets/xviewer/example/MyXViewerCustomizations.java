/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.xviewer.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.util.FileUtil;
import org.eclipse.nebula.widgets.xviewer.util.MatchFilter;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.XViewerLog;

/**
 * Implementation for example XViewer implementation. Saves customizations as files at C:/UserData
 * 
 * @author Donald G. Dunne
 */
public class MyXViewerCustomizations extends XViewerCustomizations {

   @Override
   public void deleteCustomization(CustomizeData custData) throws Exception {
      File file = new File(getFilename(custData));
      if (file.exists()) {
         boolean success = file.delete();
         if (!success) {
            throw new XViewerException("Delete Customization Failed");
         }
      }
   }

   @Override
   public List<CustomizeData> getSavedCustDatas() throws XViewerException {
      List<CustomizeData> custDatas = new ArrayList<CustomizeData>();
      custDatas.add(MyDefaultCustomizations.getCompletionCustomization());
      custDatas.add(MyDefaultCustomizations.getDescriptionCustomization());
      for (String filename : XViewerLib.readListFromDir(new File("C:/UserData/"), new MatchFilter("CustData_.*\\.xml"),
         true)) {
         custDatas.add(new CustomizeData(FileUtil.readFile("C:/UserData/" + filename)));
      }
      return custDatas;
   }

   @Override
   public CustomizeData getUserDefaultCustData() throws XViewerException {
      File file = new File(getDefaultFilename());
      if (!file.exists()) {
         return null;
      }
      String defaultGuid = FileUtil.readFile(file).replaceAll("\\s", "");
      if (defaultGuid != null) {
         for (CustomizeData custData : getSavedCustDatas()) {
            if (custData.getGuid().equals(defaultGuid)) {
               return custData;
            }
         }
      }
      return null;
   }

   @Override
   public boolean isCustomizationPersistAvailable() {
      return true;
   }

   @Override
   public boolean isCustomizationUserDefault(CustomizeData custData) throws XViewerException {
      File file = new File(getDefaultFilename());
      if (!file.exists()) {
         return false;
      }
      String defaultGuid = FileUtil.readFile(getDefaultFilename()).replaceAll("\\s", "");
      return custData.getGuid().equals(defaultGuid);
   }

   @Override
   public void saveCustomization(CustomizeData custData) throws Exception {
      XViewerLib.writeStringToFile(custData.getXml(true), new File(getFilename(custData)));
      Thread.sleep(2000);
   }

   private String getFilename(CustomizeData custData) {
      return "C:/UserData/CustData_" + custData.getGuid() + ".xml";
   }

   private String getDefaultFilename() {
      return "C:/UserData/CustDataUserDefault.txt";
   }

   @Override
   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) throws XViewerException {
      if (set) {
         try {
            XViewerLib.writeStringToFile(newCustData.getGuid(), new File(getDefaultFilename()));
         } catch (IOException ex) {
            XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
         }
      } else {
         File file = new File(getDefaultFilename());
         if (file.exists()) {
            boolean success = file.delete();
            if (!success) {
               throw new XViewerException("Delete Customization Failed");
            }
         }
      }
   }

}
