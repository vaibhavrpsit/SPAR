/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/CancelVoidTransactionSite.java /main/12 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:48 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/23 22:17:26  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:28:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:15:58   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:28:28   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:03:18   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * This site cancels the current void transaction and adds an entry to the
 * journal to indicate that.
 * 
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class CancelVoidTransactionSite extends PosSiteActionAdapter
{
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    protected static final String CANCEL_BANNER = "\t** Transaction Canceled **\n";

    /**
     * Cancels the current void transaction. Adds an entry to the journal.
     * 
     * @param bus Service Bus
     */
    public void arrive(BusIfc bus)
    {
        // Add a journal entry
        RegisterADO registerADO = ContextFactory.getInstance().getContext().getRegisterADO();
        RegisterJournalIfc journal = registerADO.getRegisterJournal();
        journal.journal(null, JournalFamilyEnum.TRANSACTION, JournalActionEnum.CANCEL);

        bus.mail(new Letter(CommonLetterIfc.CANCEL), BusIfc.CURRENT);

    }
}
