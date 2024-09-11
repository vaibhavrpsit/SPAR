/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * 
 * Rev 2.0   May 15,2023            Kumar Vaibhav    Changes for store credit lock
 * Rev 1.0   Dec 20,2016    		Ashish Yadav     Changes for Store credit FES
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.storecredit;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import max.retail.stores.domain.tender.MAXTenderStoreCredit;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXStoreCredit;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.ado.tender.MAXTenderConstantsIfc;
import max.retail.stores.pos.ado.tender.MAXTenderStoreCreditADO;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * @author Himanshu
 * 
 */
public class MAXStoreCreditActionSite extends PosSiteActionAdapter
{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String CO_OFFLINE_MESG = "Error getting home interface for service";
	
    /**      
     * MAX Customizations
     * Add store credit tender to the transaction 
     * Rev 1.0 starts
    **/
    public void arrive(BusIfc bus)
    {
        MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
        
        boolean transactionReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		HashMap tenderAttributes = cargo.getTenderAttributes();
		String storeCreditNumber = (String) tenderAttributes.get(TenderConstants.NUMBER);

		
		((MAXTenderCargo)cargo).setStoreCreditExpirtDate((EYSDate)tenderAttributes.get(MAXTenderConstantsIfc.STORE_CREDIT_EXPIRED));
        String storeID = ContextFactory.getInstance()
                                       .getContext()
                                       .getRegisterADO()
                                       .getStoreADO()
                                       .getStoreID();
        
        tenderAttributes.put(TenderConstants.STORE_NUMBER, storeID);
        tenderAttributes.put(TenderConstants.STATE, TenderCertificateIfc.REDEEMED);
        // Changes start for Rev 1.0 (Ashish: Storecredit)
        MAXTenderStoreCreditADO storeCreditTender = null;
		if (cargo.getTenderADO() == null)
		{
			try
			{
				TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
				storeCreditTender = (MAXTenderStoreCreditADO) factory.createTender(tenderAttributes);
			}
			catch (ADOException adoe)
			{
				logger.warn(adoe);
			}
			catch (TenderException e)
			{
				TenderErrorCodeEnum error = e.getErrorCode();
				if (error == TenderErrorCodeEnum.INVALID_AMOUNT)
				{
					assert(false) : "This should never happen, because UI enforces proper format";
				}
			}
		}
		else{
         storeCreditTender = (MAXTenderStoreCreditADO) cargo.getTenderADO();
         
		}
		// Changes ends for Rev 1.0 (Ashish : Storecredit)
        storeCreditTender.setTransactionReentryMode(transactionReentryMode);

        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
        try
        {
        	
            storeCreditTender.setTenderAttributes(tenderAttributes);   
        	// check the store number
            storeCreditTender.checkStoreNumber();
            // Changes start for Rev 1.1 (Ashish : Storecredit)
            //txnADO.addTender(storeCreditTender);
         // Changes start for Rev 1.1 (Ashish : Storecredit)
            cargo.setLineDisplayTender(storeCreditTender);
            // journal the added tender
            
            //ADDED BY VAIBHAV LS CREDIT NOTE CODE MERGING START---Rev 2.0 
          if(((MAXTenderStoreCreditIfc) txnADO.getTenderStoreCreditIfcLineItem()).isStoreCreditLock()!=null){
            if(((MAXTenderStoreCreditIfc) txnADO.getTenderStoreCreditIfcLineItem()).isStoreCreditLock()) { 
				MAXSaleReturnTransaction trns = (MAXSaleReturnTransaction) txnADO.toLegacy();
				Vector tenderLineitems = trns.getTenderLineItemsVector();
				Iterator itr = tenderLineitems.iterator();
				Enumeration e = tenderLineitems.elements();
				while (e.hasMoreElements()) {
					Object obj = e.nextElement();
					if (obj instanceof MAXTenderStoreCredit) {
						StoreCreditIfc storeCreditIfc = ((MAXTenderStoreCredit) obj).getStoreCredit();
						if (storeCreditIfc instanceof MAXStoreCredit) {
							MAXStoreCredit storeCredit = (MAXStoreCredit) storeCreditIfc;

							// Rev 1.4 start --end
							if ((storeCredit.getStoreCreditID()
									.equalsIgnoreCase(tenderAttributes.get(TenderConstants.NUMBER).toString()))) {
								cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
							}
						}
					}
				}
  
				String[] args = { storeCreditNumber };
				  displayDialog(bus,DialogScreensIfc.ACKNOWLEDGEMENT, "StoreCreditLockError", args, "Invalid");
				  return; 
			}
        }
          
		/*
		 * else { String[] args = { storeCreditNumber };
		 * cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
		 * displayDialog(bus,DialogScreensIfc.ACKNOWLEDGEMENT, "StoreCreditNotFound",
		 * args, "Invalid"); return; }
		 */
//END Rev 2.0 
            JournalFactoryIfc jrnlFact = null;
            try
            {
                jrnlFact = JournalFactory.getInstance();
            }
            catch (ADOException e)
            {
                logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
                throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            }
            RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
            registerJournal.journal(storeCreditTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);

          //changes Startss For CreditNote OTP
            String amount = (String) tenderAttributes.get(TenderConstants.AMOUNT);
    		String mobile = (String) tenderAttributes.get(MAXTenderConstants.mobileNumber);
    		
            UtilityIfc util;
    		try {
    			util = Utility.createInstance();
    		} catch (ADOException e) {
    			String message = "Configuration problem: could not instantiate UtilityIfc instance";
    			logger.error(message, e);
    			throw new RuntimeException(message, e);
    		}
    		
    		if (amount != null) {
            String validateStoreCredit = util.getParameterValue("ValidateStoreCredit", "N");
			if (validateStoreCredit.equals("Y")) {
				String[] args = new String[2];
				args[0]=amount;
				if (mobile != null)
				{
				args[1]=mobile;
				if(args[1].equalsIgnoreCase(""))
					UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "StoreCreditAmountNotice1", args, CommonLetterIfc.FAILURE);
				else
					UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "StoreCreditAmountNotice", args, CommonLetterIfc.SUCCESS);		
				}
				else
					UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "StoreCreditAmountNotice1", args, CommonLetterIfc.FAILURE);

			} 
			else {
			
            // mail a letter
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
			}
    		}
    		else {
    			bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    		}
    		//changes Ends For CreditNote OTP
	}
        catch (TenderException e) 
        {
        	  logger.error(e);
		}
    }
    protected void displayDialog(BusIfc bus, int screenType, String message, String[] args, String letter) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		if (letter != null) {
			UIUtilities.setDialogModel(ui, screenType, message, args, letter);
		} else {
			UIUtilities.setDialogModel(ui, screenType, message, args);
		}
	}
}
