/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	20/05/2013		Prateek		Initial Draft: Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.loyaltypoints;

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderLoyaltyPointsADO;
import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
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
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.purchaseorder.PurchaseOrderLimitActionSite;

public class MAXAddLoyaltyPointsTenderSite extends PosSiteActionAdapter {

	public void arrive(BusIfc bus)
	{
		 System.out.println("Inside MaxAddLoyaltyPointsTenderSite");
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        HashMap tenderAttributes = cargo.getTenderAttributes();       
        //Start obtaining customer info and set into tender attributes
        CustomerIfc customer = null;
        if(cargo.getCustomer() != null)
        {
        	customer = cargo.getCustomer();
            tenderAttributes.put(TenderConstants.NUMBER, customer.getCustomerID());
        }       
        tenderAttributes.put(TenderConstants.TENDER_TYPE, MAXTenderTypeEnum.LOYALTY_POINTS);
        MAXTenderLoyaltyPointsADO lyptADO = null;
        if(cargo.getTenderADO()==null)
        {
        	try
            {
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                lyptADO = (MAXTenderLoyaltyPointsADO)factory.createTender(tenderAttributes);
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
        	lyptADO = (MAXTenderLoyaltyPointsADO)cargo.getTenderADO();
        }
        try
        {
            cargo.getCurrentTransactionADO().addTender(lyptADO);
            cargo.setLineDisplayTender(lyptADO);

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
            RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
            registerJournal.journal(lyptADO, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);

            // mail a letter
            bus.mail(new Letter("PaidUp"), BusIfc.CURRENT);
        }
        catch(TenderException e)
        {
            assert(false) : "This should never happen, because UI enforces proper format";
            cargo.setTenderADO(lyptADO);
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
