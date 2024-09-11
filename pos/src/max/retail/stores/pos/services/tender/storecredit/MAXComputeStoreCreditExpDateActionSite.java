/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
MGTenderStoreCreditIfc
  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  29/April/2013               Himanshu              MAX-StoreCreditTender-FES_v1 2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.storecredit;

import java.util.HashMap;

import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;

//----------------------------------------------------------------------------
/**
 *  Compute Store Credit Expiration Date and add to the transaction
 *  
 *  $Revision: 3$
 */
//----------------------------------------------------------------------------
public class MAXComputeStoreCreditExpDateActionSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 612511861706835458L;

	/** revision number **/
    public static final String revisionNumber = "$Revision: 3$";
    
    // Static Strings
    public static final String SUCCESS_LETTER = "Success";
    
    //------------------------------------------------------------------------
    /* 
     * @param bus  The bus arriving at this site.
     */
    //------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
    	EYSDate storeCreditExpiryDate=null;
        TenderCargo cargo = (TenderCargo) bus.getCargo();
         // Rev 1.0   strats here
        if(cargo instanceof MAXTenderCargo)
        {
          storeCreditExpiryDate=((MAXTenderCargo)cargo).getStoreCreditExpirtDate();
        
        }
        // Rev 1.0 ends here 
        
        HashMap tenderAttributes = cargo.getTenderAttributes();
            
        TenderStoreCreditADO storeCreditTender = (TenderStoreCreditADO) cargo.getTenderADO();
        
        // compute expiration date
        
		// Rev 1.0  strats here
        if(storeCreditExpiryDate==null)
        {
        	storeCreditExpiryDate = storeCreditTender.computeExpiryDate();
        }
		// Rev 1.0  ends here
        
        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
        
        TenderStoreCreditIfc tscRedeem = txnADO.getTenderStoreCreditIfcLineItem();    
        
        // remove existing store credit tender from transaction 
        txnADO.removeTender(storeCreditTender);

        storeCreditTender.fromLegacy(tscRedeem);
        
        // add store credit info to store credit tender
        storeCreditTender.setStoreCreditInfo(storeCreditExpiryDate, cargo.getCustomer(),  
        		(EntryMethod)tenderAttributes
                .get(TenderConstants.ENTRY_METHOD), cargo.getLocalizedPersonalIDCode().getCode());

        // re-add the store credit tender to the transaction
        try
        {
            txnADO.addTender(storeCreditTender);
            cargo.setLineDisplayTender(storeCreditTender);
            // journal the added tender
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
        }
        catch (TenderException e)
        {
            cargo.setTenderADO(storeCreditTender);
            logger.error("Error adding Issue Store Credit Tender", e);
        }
        
        bus.mail(new Letter(SUCCESS_LETTER), BusIfc.CURRENT);
    }
}