
/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0     Oct 14, 2017        Bhanu Priya     Changes done for Paytm Integration FES

 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender.wallet.paytm;

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.ado.tender.MAXTenderPaytmADO;
import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
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
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.purchaseorder.PurchaseOrderLimitActionSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

public class MAXPaytmTenderActionSite extends PosSiteActionAdapter
{
	static String  REENTRY_WALLETID="1111111111111111111111";
	static String  REENTRY_PAYTM_TRXID="11111111";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void arrive(BusIfc bus)
	{
	
		 MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		 boolean isReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
	        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
	        POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
	        DataInputBeanModel model=(DataInputBeanModel)uiManager.getModel(MAXPOSUIManagerIfc.ENTER_WALLET_MOBILE_NUMBER_AND_TOTP);
	        model.getValueAsString("MobileNumberField");
	        HashMap tenderAttributes = cargo.getTenderAttributes();       
	        
	        //Start obtaining customer info and set into tender attributes
	        CustomerIfc customer = null;
	        if(cargo.getCustomer() != null)
	        {
	        	customer = cargo.getCustomer();
	            
	        } 
	     

	        tenderAttributes.put(TenderConstants.NUMBER, model.getValueAsString("MobileNumberField"));
	        tenderAttributes.put(TenderConstants.TENDER_TYPE,MAXTenderTypeEnum.PAYTM);
	       if(isReentryMode){
	        tenderAttributes.put(MAXTenderConstants.WALLET_ORDERID, REENTRY_WALLETID);
	        tenderAttributes.put(MAXTenderConstants.WALLET_TRANSACTIONID, model.getValueAsString("TotpCodeField"));
	        tenderAttributes.put(TenderConstants.AUTH_CODE, model.getValueAsString("TotpCodeField"));
	        }
	       else
	       {
	    	    tenderAttributes.put(MAXTenderConstants.WALLET_ORDERID, cargo.getPaytmResp().getOrderId());
		        tenderAttributes.put(MAXTenderConstants.WALLET_TRANSACTIONID, cargo.getPaytmResp().getWalletTxnId());
				tenderAttributes.put(TenderConstants.AUTH_CODE, cargo.getPaytmResp().getWalletTxnId());
	       }
	       
	        MAXTenderPaytmADO paytmADO = null;
	        if(cargo.getTenderADO()==null)
	        {
	        	try
	            {
	                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
	                paytmADO = (MAXTenderPaytmADO)factory.createTender(tenderAttributes);
	            }
	            catch (ADOException adoe)
	            {
	                adoe.printStackTrace();
	            }
	            catch (TenderException e)
	            {
	                TenderErrorCodeEnum error = e.getErrorCode();
	                if (error == TenderErrorCodeEnum.INVALID_AMOUNT)
	                {
	                    assert(false):"This should never happen, because UI enforces proper format";
	                }
	            }
	        }
	        else
	        {
	        	paytmADO = (MAXTenderPaytmADO)cargo.getTenderADO();
	        }
	        try
	        {
	            cargo.getCurrentTransactionADO().addTender(paytmADO);
	            cargo.setLineDisplayTender(paytmADO);

	            journalTaxStatus(bus, tenderAttributes);
	            // journal tender
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
	            try   // added by atul
	            {
	            RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
	            registerJournal.journal(paytmADO, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
	            }catch(NullPointerException ne)
	            {
	            	ne.printStackTrace();
	            }catch(Exception e)
	            {
	            	e.printStackTrace();
	            }
	      //   boolean reEntry=   cargo.getRegister().getWorkstation().isTransReentryMode();
	            // mail a letter  code added by atul bug Id 18574
	            if(cargo.getRegister().getWorkstation().isTransReentryMode() || cargo.getRegister().getWorkstation().isTrainingMode())
	            {
	            	  bus.mail(new Letter("PaytmReentry"), BusIfc.CURRENT);
	            }
	            else{
	            bus.mail(new Letter("Success"), BusIfc.CURRENT);
	        }
	        }
	        catch(TenderException e)
	        {
	            assert(false) : "This should never happen, because UI enforces proper format";
	            cargo.setTenderADO(paytmADO);
	        }
		}
		 protected void journalTaxStatus(BusIfc bus, HashMap tenderAttributes)
		 {
			 String status = (String)tenderAttributes.get(TenderConstants.TAXABLE_STATUS);
		     if (status != null && status.equals(PurchaseOrderLimitActionSite.TAX_EXEMPT)) {
		    	 JournalManagerIfc journal =
		    		 (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
		    	 journal.journal("Transaction Tax Removed");
		     }
		 }
	
}

