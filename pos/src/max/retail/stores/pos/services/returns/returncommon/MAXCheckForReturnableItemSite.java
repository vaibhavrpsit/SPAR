/********************************************************************************
 *   
 *	Copyright (c) 2016-2018  MAX Hypermarket, Inc    All Rights Reserved.
 *	Rev 1.3	 15/May/2024	Kamlesh Pant		Changes For CN Issuance 30 Days validity
 *  Rev 1.2  05/Oct/2018	Purushotham Reddy 	Returns are accepting for Cancelled Transaction(Fix for the Bug-20382)
 *  Rev 1.1  17/Jun/2013	Jyoti Rawal			Correct Message not coming 
 *	Rev 1.0  23/Apr/2013	Jyoti Rawal 		Initial Draft: Changes for Gift Card Functionality 
 *
 ********************************************************************************/

package max.retail.stores.pos.services.returns.returncommon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javafx.util.Duration;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
import max.retail.stores.domain.lineitem.MAXItemTaxIfc;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.manager.tenderauth.MAXItemActivationRequest;
import max.retail.stores.domain.manager.tenderauth.MAXTenderAuthConstantsIfc;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.gstinCentralJob.MAXEGSTINDataTransferTransaction;
import max.retail.stores.pos.services.modifytransaction.MAXGSTINUtility;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCard;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnableItemCargoIfc;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


//--------------------------------------------------------------------------
/**

    This site checks the retrived trans action to determine if it contains
    any items that can be returned.

    <p>
    @version $Revision: 4$
**/
//--------------------------------------------------------------------------
public class MAXCheckForReturnableItemSite extends PosSiteActionAdapter
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 5373084445895130551L;

	/**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 4$";

    /**
       Error msg key
    **/
    public static final String INVALID_TRANSACTION_NO_SELL_ITEMS = "InvalidTransactionNoSellItems";
    
    
    public static final String INVALID_RETURN_ITEMS = "InvalidReturnItems";

    /**
       Error msg key
    **/
    public static final String INVALID_TRANSACTION_NO_QUANTITIES = "InvalidTransactionNoQuantities";

    //----------------------------------------------------------------------
    /**
       Check the retrived trans action to determine if it contains
       any items that can be returned.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        ReturnableItemCargoIfc cargo = (ReturnableItemCargoIfc)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // Loop through the line items to see if there are returnable items.
        boolean returnableItems    = false;
        boolean saleItemsAvailable = false;
        GiftCardIfc giftCard = null;
        SaleReturnTransactionIfc tran = cargo.getOriginalTransaction();

       //Rev 1.3 Changes Start
        Calendar cal = Calendar.getInstance();
        if(cargo.getOriginalTransaction().getBusinessDay()!=null) {
        	cal.setTime(cargo.getOriginalTransaction().getBusinessDay().dateValue());
        }
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		EYSDate current_Date = new EYSDate();
		Date fromDate = null,ToDate = null;
		try {
			fromDate = sdf.parse(current_Date.toString());
			ToDate = sdf.parse(cal.getTime().toString());
		} catch (ParseException e) {
			logger.error("CreditNote Exception " + e.getMessage());
		}
		 
		 long diffMilliseconds = fromDate.getTime() - ToDate.getTime();
		 long diffDays = diffMilliseconds / (24 * 60 * 60 * 1000);
		//Date toDate = cal.getTime();
		 if (Math.abs(diffDays) >= 30){ // if the selected date are within one month
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("CNMoreThan30daysNotice");
			dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Failure");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			return;
			//bus.mail("Failure");
		}
		 //Rev 1.3 Changes Ends
		else {
        Vector items = tran.getLineItemsVector();
        
       // System.out.println("hi GSTno ___"+tran.getExternalOrderID());
        for(int i = 0; i < items.size(); i++)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)items.elementAt(i);
            
            // If the return item is a giftcard, get the current balance on the 
            // card and return this amount the customer.  The gift card may have been
            // used and the balance left on the card may be less than the issued
            // amount.
            if (srli.getPLUItem() instanceof GiftCardPLUItemIfc)
 {
				GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc) srli
						.getPLUItem();
				giftCard = giftCardPLUItem.getGiftCard();

				if (giftCard != null) {
					// Rev 1.1 changes
					DialogBeanModel dialogModel = new DialogBeanModel();
					dialogModel.setResourceID("GC_NOT_ALLOWED_RETURN");
					dialogModel.setType(DialogScreensIfc.ERROR);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
							"Failure2");
					ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;

					/**
					 * Rev 1.0 changes end here
					 */
				}
			}

            if (!srli.isKitHeader())//only kit components are viewable during return processing
            {                       //header items are maintained by the system
                
                // If This transaction was retrieved by tender type
                // make sure that the item used for search is returnable
                if (cargo.getSearchCriteria() != null && cargo.isSearchByTender() &&
                    srli.getPLUItemID().equals(cargo.getSearchCriteria().getItemID()))
                {
                   String itemSize = cargo.getSearchCriteria().getItemSizeCode();
                   boolean isSizeRequired = srli.getPLUItem().isItemSizeRequired();
                   
                   // check If the exact  item/size used for searching the transaction is returnable
                   if ((!isSizeRequired ||  (isSizeRequired && srli.getItemSizeCode().equals(itemSize))) &&
                      srli.isReturnable())
                   {
                       returnableItems = true;
                       break;
                   }

                }
                else
                {
                   if (srli.isReturnable() && tran.getTransactionStatus() == TransactionIfc.STATUS_COMPLETED)
                   {
                      returnableItems = true;
                      break;
                   }
                   else{// Changes for Rev 1.2
                       returnableItems = false;
                      // break;
                   }
                }
                if (srli.getItemQuantityDecimal().signum() > 0)
                {
                    saleItemsAvailable = true;
                }
            }
        }

        
        
 ////// get the GST number and set to the trasnction object the response form cygnet 
        //changes by Anuj Singh
       
         
        checkforGSTINNumberAndUpdate((MAXSaleReturnTransactionIfc)tran,bus);
        //System.out.println("updated response from cygent in trsnaction object "+ ((MAXSaleReturnTransactionIfc)tran).getGstinresp());
        
        
        if (returnableItems)
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        else
        {
            // Get the ui manager
             ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            // Use the "generic dialog bean".
            DialogBeanModel model = new DialogBeanModel();

            // Display no returnable items error
            if (saleItemsAvailable)
            {
                model.setResourceID(INVALID_TRANSACTION_NO_QUANTITIES);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Ok");
            }
            else if (cargo instanceof ReturnCustomerCargo &&
                    ((ReturnCustomerCargo)cargo).getTransactionSummary().length > 1 )
            {
                model.setResourceID(INVALID_TRANSACTION_NO_SELL_ITEMS);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure2");                
            }
         // Changes for Rev 1.2
            else if(tran.getTransactionStatus() != TransactionIfc.STATUS_COMPLETED){
            	  model.setResourceID(INVALID_RETURN_ITEMS);
                  model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Ok");
            }
            else
            {
                model.setResourceID(INVALID_TRANSACTION_NO_SELL_ITEMS);
                model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Ok");                
            }
              
            model.setType(DialogScreensIfc.ERROR);
            // set and display the model
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
 
 }
 
    }
        
    /**
     * Setup request to authorizer
     * @param giftCard
     * @return
     */
    protected MAXItemActivationRequest createInquiryRequest(GiftCardIfc giftCard)
    {
        // create an activation request
        MAXItemActivationRequest request = new MAXItemActivationRequest();
        request.setActionCode(MAXTenderAuthConstantsIfc.INQUIRY);
        request.setItemType(MAXTenderAuthConstantsIfc.GIFT_CARD);
        request.setGiftCard((GiftCard)giftCard);
        return request;
    }
    
 private void checkforGSTINNumberAndUpdate(MAXSaleReturnTransactionIfc tran,BusIfc bus) {
	 POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
	 String gstin = ((MAXSaleReturnTransactionIfc)tran).getGSTINNumber();
    	MAXGSTINValidationResponseIfc response = null;
		String args[] = new String[1];
		boolean flag = false;
		String storeGSTNu = "";
		MAXGSTINValidationResponseIfc storeGstin = getStoreGstin(tran.getFormattedStoreID(), tran.getTransactionID());
		logger.error("storeGstin"+storeGstin.getGstin());
		if((storeGstin != null && storeGstin.getGstin() != null) && (storeGstin.getLgnm() == null || storeGstin.getLgnm().equals(""))) {
			try {
				storeGSTNu = storeGstin.getGstin();
				MAXGSTINValidationResponseIfc resp = (MAXGSTINValidationResponseIfc) MAXGSTINUtility.validate(storeGstin.getGstin(), tran.getTransactionID());
				logger.error("api respose"+resp);
		          String respon=resp.getStatusCode();
				if(resp != null && respon.equals("1")) {
					///nothing to do 
				}else {
					args[0] = resp.getErrormsg() + " Store GSTIN No: "+storeGstin.getGstin();
					showDialogMessage(uiManager, "invalidGSTIN", args, CommonLetterIfc.FAILURE);
				}

			} 
			catch (Exception e) {
				logger.error(e);
				logger.error("Store GSTIN Validation error ::::::::::::::::" +e);
				args[0] = "Unable to validate Store GSTIN ";
				//System.out.println("ERRROR:::::1"+e);
				showDialogMessage(uiManager, "invalidGSTIN", args, CommonLetterIfc.FAILURE);
			}
		}else {
			flag = true;
			storeGSTNu = storeGstin.getGstin();
		}
		if(flag) {
			try {
				logger.error("gstin"+gstin);
				response = (MAXGSTINValidationResponseIfc) MAXGSTINUtility.validate(gstin, tran.getTransactionID());
				
				if(response != null && response.getStatusCode().equals("1") && response.getSts().equalsIgnoreCase("Active")) {	
			
					MAXSaleReturnTransactionIfc maxSaleReturn = null;
					if (tran != null && tran instanceof MAXSaleReturnTransactionIfc) {
						if (tran instanceof MAXLayawayTransaction)	
							maxSaleReturn = (MAXLayawayTransaction) tran;
						else
							maxSaleReturn = (MAXSaleReturnTransaction) tran;
						
					}		
					maxSaleReturn.setGstEnable(true);
					maxSaleReturn.setGSTINNumber(gstin);
					//System.out.println("maxSaleReturn.setGSTINNumber(gstin);" +maxSaleReturn.getGSTINNumber());
					maxSaleReturn.setStoreGSTINNumber(storeGSTNu);
					maxSaleReturn.setGstinresp(response);
					
					boolean deliveryFlag = true;
						if(((RetailTransactionIfc) tran).getItemSendPackagesCount() >0) {
							if( tran instanceof SaleReturnTransaction) {
								Vector lineItems = ((SaleReturnTransaction) tran).getItemContainerProxy().getLineItemsVector(); 
								for (Iterator itemsIter = lineItems.iterator(); itemsIter.hasNext();) {
									SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) itemsIter.next(); 
									MAXLineItemTaxBreakUpDetailIfc[] lineItemBreakUpDetails = ((MAXItemTaxIfc) (srli.getItemPrice().getItemTax())).getLineItemTaxBreakUpDetail();
									logger.error("breakupDetails"+lineItemBreakUpDetails.length  );
									for(int i = 0; i<lineItemBreakUpDetails.length; i++) {
										MAXLineItemTaxBreakUpDetailIfc breakupDetails = lineItemBreakUpDetails[i];
										MAXTaxAssignmentIfc taxAssignment = breakupDetails.getTaxAssignment();
										if(taxAssignment.getTaxCodeDescription() != null && taxAssignment.getTaxCodeDescription().toUpperCase().contains("IGST")){
											logger.error("deliveryFlag"+ taxAssignment.getTaxCodeDescription());
											deliveryFlag = true;
										}
									}										
								}
							}
						}
						
						logger.error(!storeGstin.getStcd().equalsIgnoreCase(response.getStcd()) && deliveryFlag);
						
						
					
				} 
				else if(response != null && response.getStatusCode().equals("1") && !response.getSts().equalsIgnoreCase("Active"))
				{
				String arg[] = new String[2];
				arg[0] = gstin;
				arg[1] = response.getSts();
				showDialogMessage(uiManager, "GSTINInactiveNotice", arg, CommonLetterIfc.FAILURE);
				
				}else {
					args[0] = response.getErrormsg();
					showDialogMessage(uiManager, "invalidGSTIN", args, CommonLetterIfc.FAILURE);
				}
			} catch (Exception e) {
				logger.error("GSTIN Validation error ::::::::::::::::" +e);
				args[0] = "Unable to validate GSTIN ";
				showDialogMessage(uiManager, "invalidGSTIN", args, CommonLetterIfc.FAILURE);
			}
		}else {
			args[0] = "Unable to validate Store GSTIN ";
			showDialogMessage(uiManager, "invalidGSTIN", args, CommonLetterIfc.FAILURE);
		}

    }
 
 private MAXGSTINValidationResponseIfc getStoreGstin(String storeID, String txnId) {

		MAXGSTINValidationResponseIfc storeGstinDetails = null;
		HashMap inputData = new HashMap();
		inputData.put(MAXCodeConstantsIfc.STORE_ID, storeID);
		inputData.put(MAXCodeConstantsIfc.GSTIN_DATA_TRANFER_ID, 3);
		inputData.put(MAXCodeConstantsIfc.TXNID, txnId);		
		MAXEGSTINDataTransferTransaction gstinTransaction =(MAXEGSTINDataTransferTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.GSTIN_DATA_TRANSFER_TRANSACTION);

		try {
			storeGstinDetails = gstinTransaction.getStoreGSTIN(inputData);
		} catch (DataException e) {
			e.printStackTrace();
		}
		return storeGstinDetails;
	}
 private boolean saveGstin(MAXGSTINValidationResponseIfc resp, String storeId) throws DataException {

		HashMap inputData = new HashMap();
		boolean flag = false;
		inputData.put(MAXCodeConstantsIfc.STORE_ID, storeId);
		inputData.put(MAXCodeConstantsIfc.GSTIN_DATA_TRANFER_ID, 4);
		inputData.put(MAXCodeConstantsIfc.GSTIN_STORE, resp);		
		MAXEGSTINDataTransferTransaction gstinTransaction = (MAXEGSTINDataTransferTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.GSTIN_DATA_TRANSFER_TRANSACTION);

		flag = gstinTransaction.updateGSTINTransferStatus(inputData);
		return flag;
	}
	
  

	public void showDialogMessage( POSUIManagerIfc ui, String resourceID, String args[], String letter) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		if (args != null) {
			dialogModel.setArgs(args);
		}
		dialogModel.setResourceID(resourceID);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
}