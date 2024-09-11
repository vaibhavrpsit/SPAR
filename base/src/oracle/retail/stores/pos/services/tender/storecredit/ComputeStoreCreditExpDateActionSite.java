/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/storecredit/ComputeStoreCreditExpDateActionSite.java /rgbustores_13.4x_generic_branch/2 2011/07/07 12:20:07 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    deghosh   01/22/09 - EJ i18n defect fixes
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to reason codes
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:21 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:10 PM  Robert Pearse
 *
 *   Revision 1.6.2.1  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.6  2004/07/22 22:38:41  bwf
 *   @scr 3676 Add tender display to ingenico.
 *
 *   Revision 1.5  2004/03/04 23:26:42  nrao
 *   Code review changes for Issue Store Credit.
 *
 *   Revision 1.4  2004/02/28 00:03:24  nrao
 *   Added Capture Customer info to store credit tender
 *
 *   Revision 1.3  2004/02/27 01:07:15  nrao
 *   Added information from the Capture Customer use case to the store credit tender.
 *
 *   Revision 1.2  2004/02/19 19:05:56  nrao
 *   Added entry method to the transaction.
 *
 *   Revision 1.1  2004/02/17 17:56:49  nrao
 *   New site for Issue Store Credit
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.storecredit;

import java.util.HashMap;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.customer.CaptureCustomer;
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

/**
 * Compute Store Credit Expiration Date and add to the transaction
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
@SuppressWarnings("serial")
public class ComputeStoreCreditExpDateActionSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    // Static Strings
    public static final String SUCCESS_LETTER = "Success";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap tenderAttributes = cargo.getTenderAttributes();

        TenderStoreCreditADO storeCreditTender = (TenderStoreCreditADO)cargo.getTenderADO();

        // compute expiration date
        EYSDate expirationDate = storeCreditTender.computeExpiryDate();

        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();

        TenderStoreCreditIfc tscRedeem = txnADO.getTenderStoreCreditIfcLineItem();

        // remove existing store credit tender from transaction
        txnADO.removeTender(storeCreditTender);

        storeCreditTender.fromLegacy(tscRedeem);
        CaptureCustomer customer = (CaptureCustomer)cargo.getCustomer();
        LocalizedCodeIfc locCodeIdType = customer.getPersonalIDType();

        // add store credit info to store credit tender
        storeCreditTender.setStoreCreditInfo(expirationDate, cargo.getCustomer(), (EntryMethod)tenderAttributes
                .get(TenderConstants.ENTRY_METHOD), cargo.getLocalizedPersonalIDCode().getCode());

        // add store credit personal id to store credit tender
        storeCreditTender.setStoreCreditPersonalIdInfo(locCodeIdType);
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
