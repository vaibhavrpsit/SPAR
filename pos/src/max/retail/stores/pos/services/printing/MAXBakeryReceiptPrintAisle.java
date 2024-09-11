package max.retail.stores.pos.services.printing;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import max.retail.stores.domain.bakery.MAXBakeryItem;
import max.retail.stores.domain.bakery.MAXBakeryItemIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.transaction.MAXVoidTransaction;
import max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransaction;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import org.apache.log4j.Logger;


public class MAXBakeryReceiptPrintAisle extends PosLaneActionAdapter{
	
	private static final long serialVersionUID = -609800267867089201L;
	private static Logger logger = Logger.getLogger(MAXBakeryReceiptPrintAisle.class);

	public void traverse(BusIfc bus)
	{
		boolean mailLetter = true;
	//	MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		MAXPrintingCargo cargo = (MAXPrintingCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		MAXSaleReturnTransaction transaction=null;
		//ArrayList list=new ArrayList();
		final String ENABLE_BAKERY_POS_RECEIPT = "IsBakeryPOS";
		ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		
			
				
	
		try {
			Boolean param=pm.getBooleanValue(ENABLE_BAKERY_POS_RECEIPT);
			if(param.booleanValue()==true)
			{	
				if((cargo.getTransaction() instanceof MAXSaleReturnTransaction) && cargo.getTransaction().getTransactionType() == 1)
				{
				 transaction=(MAXSaleReturnTransaction) cargo.getTransaction();
			SaleReturnLineItemIfc[] lineitem=	(SaleReturnLineItemIfc[]) transaction.getItemContainerProxy().getLineItems();
				HashMap<String, ArrayList<MAXBakeryItem>> map=new HashMap<String, ArrayList<MAXBakeryItem>>();
				ArrayList<MAXBakeryItem> categoryArray = null ;
				MAXBakeryItem bakeryItem;
				for(int i=0; i<lineitem.length; i++)
		{
					if(lineitem[i] instanceof MAXSaleReturnLineItem)
					{
						bakeryItem = new MAXBakeryItem();
						String itemDesc =	((MAXSaleReturnLineItem)lineitem[i]).getItemDescription();	
						String itemID =	((MAXSaleReturnLineItem)lineitem[i]).getItemID();
						String categoryDesc =	((MAXSaleReturnLineItem)lineitem[i]).getScansheetCategoryDesc();
						BigDecimal quantity=((MAXSaleReturnLineItem)lineitem[i]).getItemQuantityDecimal();
																	
					String categoryId =	((MAXSaleReturnLineItem)lineitem[i]).getScansheetCategoryID();	
					if(categoryId == null){
						categoryId = "0";
					}
					if(categoryDesc == null){
						categoryDesc = "Bakery Item";
					}
						bakeryItem.setItemDesc(itemDesc);
						bakeryItem.setItemId(itemID);
						bakeryItem.setCategoryDesc(categoryDesc);
						bakeryItem.setQuantity(quantity);
					if(i==0)
					{	
						categoryArray = new ArrayList<MAXBakeryItem>();
						categoryArray.add(bakeryItem);
						map.put(categoryId, categoryArray);
						
					}
					
					else 
					{
						if(map.containsKey(categoryId))
						{	
							ArrayList<MAXBakeryItem> newcategoryArray = map.get(categoryId);
							newcategoryArray.add(bakeryItem);

							map.put(categoryId, newcategoryArray);
						}
						
						else
						{
							ArrayList<MAXBakeryItem> newcategoryList = new ArrayList<MAXBakeryItem>();
							newcategoryList.add(bakeryItem);
							map.put(categoryId, newcategoryList);
							
						}
							
							
						}
					}
					
							
					}
				for(Map.Entry newMap : map.entrySet())	
				{

					Object printLineItem = newMap.getValue();
					ArrayList<MAXBakeryItemIfc> newcategoryList = new ArrayList<MAXBakeryItemIfc>();
					ArrayList<MAXBakeryItemIfc> newcategoryArray = new ArrayList<MAXBakeryItemIfc>();
					if(printLineItem instanceof ArrayList)
					{
					newcategoryArray = (ArrayList)printLineItem;
					
					
					for(int m=0 ; m< newcategoryArray.size() ; m++)
					{ 
						for(int j=m+1 ; j<newcategoryArray.size() ; j++)
						{
							if((newcategoryArray.get(m)).equals(newcategoryArray.get(j)))
							{
								newcategoryArray.get(m).setQuantity(((newcategoryArray.get(m)).getQuantity()).add((newcategoryArray.get(j)).getQuantity()));
								
								newcategoryArray.remove(j);
	
						}
					}
					
					}
					
					}
					
					
					
							      newcategoryList.addAll(newcategoryArray);
					
					
					MAXSaleReturnTransaction txn = (MAXSaleReturnTransaction)(cargo.getTransaction());
					txn.getScansheetLineItemsVector().removeAllElements();
					txn.getScansheetLineItemsVector().addAll(newcategoryList);
			
					printBakeryReceiptSlip(bus, "WITHDRAW", txn);
				}
		
		
			
		
	} 
			
		}}
		catch (PrintableDocumentException e) {
			mailLetter = false;
			
			
            logger.error("Unable to print Bakery WIthdrawal Receipt ", e);
            StatusBeanModel statusModel = new StatusBeanModel();
            // Update printer status
            statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);

            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

            String msg[] = new String[1];
            msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                    BundleConstantsIfc.PRINTER_OFFLINE);

            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryContinue");
            model.setType(DialogScreensIfc.RETRY_CONTINUE);
            model.setArgs(msg);
           // model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Success");
            
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
       
     
            
		} catch (ParameterException e) {
				logger.error("Unable to print Bakery WIthdrawal Receipt ", e);
			}
		
		
		if (mailLetter)
        {
          //  bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
			//  bus.mail("ReceiptPrint");
			//System.out.println("206 :"+cargo.includesMallCertificate());
			if(cargo.includesMallCertificate())
			{
				//System.out.println("209");
			 bus.mail("PrintMallCert");
			}
			else
			{
				bus.mail("ExitPrinting");
			}
        }
		
	}
	
	
	public void printBakeryReceiptSlip(BusIfc bus,String requestType,MAXSaleReturnTransactionIfc trans ) 
			throws ParameterException,PrintableDocumentException {
				PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
				MAXReceiptParameterBeanIfc receipt=(MAXReceiptParameterBeanIfc) BeanLocator.getApplicationBean("application_ReceiptParameterBean");

				receipt.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
				//receipt.setPaytmResponse(paytmResponse);
				receipt.setDocumentType(MAXReceiptTypeConstantsIfc.BAKERYPOSRECEIPT);
				receipt.setTransaction(trans); 
		
				List<MAXBakeryItemIfc> arraylist1 = new ArrayList<MAXBakeryItemIfc>(trans.getScansheetLineItemsVector());
				String  categoryDesc=arraylist1.get(0).getCategoryDesc();
				MAXBakeryItemIfc newcategoryArray[] = new MAXBakeryItemIfc[arraylist1.size()];
				receipt.setBakeryItems((MAXBakeryItemIfc[]) arraylist1.toArray(newcategoryArray));
				receipt.setCategoryDesc(categoryDesc);
				pdm.printReceipt((SessionBusIfc)bus, receipt);
	}
}
