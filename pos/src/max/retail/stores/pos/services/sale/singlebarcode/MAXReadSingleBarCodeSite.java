package max.retail.stores.pos.services.sale.singlebarcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.StringTokenizer;
import java.util.Vector;

import max.retail.stores.domain.singlebarcode.SingleBarCodeData;
import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXReadSingleBarCodeSite extends PosSiteActionAdapter {

	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel();
		PromptAndResponseModel rspModel = model.getPromptAndResponseModel();
		String barcode = rspModel.getResponseText();
		readSingleBarCode(bus, barcode);
	}
	private void readSingleBarCode(BusIfc bus, String barcode)
	{
		String path = Gateway.getProperty("application","path","");
		String extension = Gateway.getProperty("application", "extension", "");
		String file = path+"\\"+barcode+extension;
		Vector pluData = fetchPLUData(file);
		if(pluData == null)
			showConfirmationDialog(bus);
		else
		{
			MAXSaleCargoIfc cargo = (MAXSaleCargoIfc)bus.getCargo();
			cargo.setSingleBarCodeVector(pluData);
			cargo.setSingleBarCodeLineItem(0);
        	//UNCOMMENTED BY VAIBHAV below line
		
			moveFileToBackup(barcode);
			bus.mail("Next");
		}
	}
	private Vector fetchPLUData(String file)
	{
		Vector data = new Vector();
		SingleBarCodeData sgData = null;
			
		BufferedReader br = null;

		try 
		{
			String sCurrentLine;
			br = new BufferedReader(new FileReader(file));
			while ((sCurrentLine = br.readLine()) != null) 
			{

				StringTokenizer st2 = new StringTokenizer(sCurrentLine, ",");
				sgData = new SingleBarCodeData();
				while (st2.hasMoreElements()) 
				{
					sgData.setItemId((String)st2.nextElement());
					sgData.setQuantity(new BigDecimal((String)st2.nextElement()));
				}
				data.add(sgData);
			} 
		} 
		catch (IOException e) 
		{
			return null;
		}
		finally 
		{
			try 
			{
				if (br != null)
					br.close();
			} 
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}
   /*     break MISSING_BLOCK_LABEL_165;
        Exception exception;
        try
        {
            if(br != null)
                br.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        throw exception;
        try
        {
            if(br != null)
                br.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }*/
		return data;
	}
	private void showConfirmationDialog(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("FileLookupFailed");        
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.ACKNOWLEDGEMENT, "Ok");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}
	private void moveFileToBackup(String barcode)
	{
		String path = Gateway.getProperty("application","path","");
		String backupPath = Gateway.getProperty("application","backuppath","");
		String extension = Gateway.getProperty("application", "extension", "");
		String file = path+"\\"+barcode+extension;;
		
		InputStream inStream = null;
		OutputStream outStream = null;
	 
		try{
			 
    	    File afile =new File(path+"\\"+barcode+extension);
    	    File bfile =new File(backupPath+"\\"+barcode+extension);
 
    	    inStream = new FileInputStream(afile);
    	    outStream = new FileOutputStream(bfile);
 
    	    byte[] buffer = new byte[1024];
 
    	    int length;
    	    //copy the file content in bytes 
    	    while ((length = inStream.read(buffer)) > 0){
 
    	    	outStream.write(buffer, 0, length);
 
    	    }
 
    	    inStream.close();
    	    outStream.close();
 
    	    //delete the original file
    	    afile.delete();
		}catch(IOException e){
	    e.printStackTrace();
		}
	}
}
