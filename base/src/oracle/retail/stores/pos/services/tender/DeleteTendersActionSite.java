/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/DeleteTendersActionSite.java /rgbustores_13.4x_generic_branch/4 2011/09/16 15:13:31 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  09/16/11 - Don't mail Cancel letter for order transaction undo
 *                         action
 *    asinton   08/12/11 - add case for isTenderCanceled to mail Cancel letter
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  04/09/09 - fixed issue for pdo service alert if refund
 *                         confirmation cancelled
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:54 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:33 PM  Robert Pearse
 *
 *   Revision 1.2.4.1  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.2  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Nov 11 2003 16:19:40   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.transaction.OrderTransactionADO;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Deletes all tenders from a transaction.
 */
public class DeleteTendersActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 3466529913391121106L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
    
        // Get and delete all tenders from the transaction
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        TenderADOIfc[] tenders = cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.ALL);

        for (int i = 0; i < tenders.length; i++)
        {
            // delete this tender from the transaction
            cargo.getCurrentTransactionADO().removeTender(tenders[i]);

            // journal the deletion
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
            registerJournal.journal(tenders[i], JournalFamilyEnum.TENDER, JournalActionEnum.DELETE);
        }
        // if tender canceled by operator
        if (cargo.isTenderCanceled())
        {
            bus.mail(new Letter(CommonLetterIfc.CANCEL), BusIfc.CURRENT);
        }
        else
        {
            // go back to calling service (Mailing same letter that invoked this
            // functionality)
            bus.mail(new Letter(CommonLetterIfc.UNDO), BusIfc.CURRENT);
        }
    }
}
