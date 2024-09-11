/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016	MAX HyperMarkets.    All Rights Reserved.
 
	Rev 1.0 	12/07/2016		Abhishek Goyal		Initial Draft: Changes for CR
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

import java.util.HashMap;

import max.retail.stores.pos.ado.journal.MAXJournalFactory;
import max.retail.stores.pos.ado.tender.MAXTenderEComPrepaidADO;
import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXEComPrepaidTenderActionSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void arrive(BusIfc bus)
	{
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		
		MAXTenderEComPrepaidADO ecomPrepaidTenderADO = null;
		if (cargo.getTenderADO() == null)
	    {
		      HashMap tenderAttributes = cargo.getTenderAttributes();
		      
		      tenderAttributes.put(TenderConstants.TENDER_TYPE, MAXTenderTypeEnum.ECOM_PREPAID);
		      try
		      {
		    	  TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
		    	  ecomPrepaidTenderADO = (MAXTenderEComPrepaidADO)factory.createTender(tenderAttributes);
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
			ecomPrepaidTenderADO = (MAXTenderEComPrepaidADO)cargo.getTenderADO();
		}
		
		try
		{
		      RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
		      txnADO.validateTenderLimits(ecomPrepaidTenderADO.getTenderAttributes());
		      txnADO.addTender(ecomPrepaidTenderADO);
		      cargo.setLineDisplayTender(ecomPrepaidTenderADO);
		      
		      JournalFactoryIfc jrnlFact = null;
		      try
		      {
		          jrnlFact = MAXJournalFactory.getInstance();
		      }
		      catch (ADOException e)
		      {
		          logger.error("Configuration problem: could not instantiate JournalFactoryIfc instance", e);
		          throw new RuntimeException("Configuration problem: could not instantiate JournalFactoryIfc instance", e);
		      }
		      RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
		      registerJournal.journal(ecomPrepaidTenderADO, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
		      

		      bus.mail(new Letter("Success"), BusIfc.CURRENT);
		 }
	     catch (TenderException e)
	     {
		      TenderErrorCodeEnum errorCode = e.getErrorCode();
		      POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
		      if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL) {
		        displayErrorDialog(ui, "EComPrepaidOvertenderNotAllowed");
		      }
		 }
	}
	
	 protected void displayErrorDialog(POSUIManagerIfc ui, String name)
	 {
	    DialogBeanModel dialogModel = new DialogBeanModel();
	    dialogModel.setResourceID(name);
	    dialogModel.setArgs(null);
	    dialogModel.setType(DialogScreensIfc.ERROR);
	    dialogModel.setButtonLetter(DialogScreensIfc.CONFIRMATION, "Failure");
	    ui.showScreen("DIALOG_TEMPLATE", dialogModel);
	 }
}
