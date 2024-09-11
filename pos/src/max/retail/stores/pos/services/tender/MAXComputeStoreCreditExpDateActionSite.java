/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 2004 360Commerce, Inc.    All Rights Reserved.

     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:20:21 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:10:10 PM  Robert Pearse   
     $
     Revision 1.6.2.1  2004/11/12 14:28:53  kll
     @scr 7337: JournalFactory extensibility initiative

     Revision 1.6  2004/07/22 22:38:41  bwf
     @scr 3676 Add tender display to ingenico.

     Revision 1.5  2004/03/04 23:26:42  nrao
     Code review changes for Issue Store Credit.

     Revision 1.4  2004/02/28 00:03:24  nrao
     Added Capture Customer info to store credit tender

     Revision 1.3  2004/02/27 01:07:15  nrao
     Added information from the Capture Customer use case to the store credit tender.

     Revision 1.2  2004/02/19 19:05:56  nrao
     Added entry method to the transaction.

     Revision 1.1  2004/02/17 17:56:49  nrao
     New site for Issue Store Credit

     
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderStoreCreditADO;
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
        
        if(cargo instanceof MAXTenderCargo)
        {
          storeCreditExpiryDate=((MAXTenderCargo)cargo).getStoreCreditExpirtDate();
        
        }
        
        
        HashMap tenderAttributes = cargo.getTenderAttributes();
            
        MAXTenderStoreCreditADO storeCreditTender = (MAXTenderStoreCreditADO) cargo.getTenderADO();
        
        // compute expiration date
        
        if(storeCreditExpiryDate==null)
        {
        	storeCreditExpiryDate = storeCreditTender.computeExpiryDate();
        }
        
        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
        
        TenderStoreCreditIfc tscRedeem = txnADO.getTenderStoreCreditIfcLineItem();    
        
        // remove existing store credit tender from transaction 
        txnADO.removeTender(storeCreditTender);

        storeCreditTender.fromLegacy(tscRedeem);
        
        // add store credit info to store credit tender
        // Changes start for ocd emerging(commenting below line as per MAX)
        /*storeCreditTender.setStoreCreditInfo(storeCreditExpiryDate, cargo.getCustomer(),  
                                             (String) tenderAttributes.get(TenderConstants.ENTRY_METHOD),
                                             cargo.getIdType());*/
        
        storeCreditTender.setStoreCreditInfo(storeCreditExpiryDate, cargo.getCustomer(),  
        		(EntryMethod)tenderAttributes
                .get(TenderConstants.ENTRY_METHOD), cargo.getLocalizedPersonalIDCode().getCode());
        // Changes ends for code merging
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