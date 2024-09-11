/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/AddAlterationItemSite.java /main/11 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:23 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.4  2004/02/20 19:51:59  epd
 *   @scr 3561 Updates to prompt for item size if the item requires a size
 *
 *   Revision 1.3  2004/02/12 16:49:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.8   Jun 25 2003 17:33:46   DCobb
 * Extracted Alteration journal string composition to AlterationUtilities.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 * 
 *    Rev 1.7   Mar 05 2003 18:18:02   DCobb
 * Generalized names of alterations attributes.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.6   Sep 23 2002 11:14:38   DCobb
 * Corrected journaling of alteration type.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.5   Sep 20 2002 18:23:04   DCobb
 * Moved ALT_LINE_LENGTH & ALT_INDENT from the bundle files to java definitions.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.4   Sep 20 2002 17:45:38   DCobb
 * New journaling specification.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.3   Aug 23 2002 12:04:26   DCobb
 * Dont' use right margin for ItemDescriptionField.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.2   Aug 22 2002 16:41:36   DCobb
 * Added alteration type to journaling.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.1   Aug 21 2002 11:21:18   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * This site adds an alteration item to the transaction.
 * 
 * @version $Revision: /main/11 $
 */
@SuppressWarnings("serial")
public class AddAlterationItemSite extends PosSiteActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Adds the item to the transaction.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Grab the item from the cargo
        AlterationsCargo cargo = (AlterationsCargo) bus.getCargo();
        SaleReturnTransactionIfc transaction;
        PLUItemIfc item;

        if (cargo.getTransaction() != null)
        {
            // Get the transaction and add the item
            transaction = (SaleReturnTransactionIfc) cargo.getTransaction();

            if (cargo.getPLUItem()!=null)
            {
                item = cargo.getPLUItem();

                if (cargo.isModifyItemService())
                {
                    if (cargo.isNewPLUItem())
                    {
                        // set the add PLU item flag for the modify item service
                        cargo.setAddPLUItem(true);
                    }
                }
                else
                {
                    SaleReturnLineItemIfc lineItem = transaction.addPLUItem(item);
                    //Set the Alteration Item Flag
                    lineItem.setAlterationItemFlag(true);
                }

                // Set the transaction back in the cargo
                cargo.setTransaction(transaction);

                // Write the journal entry
                JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
                if (journal != null)
                {
                    String journalString = AlterationsUtilities.journalAlterationItem((AlterationPLUItemIfc)item);
                    journal.journal(transaction.getSalesAssociateID(),
                            transaction.getTransactionID(),
                            journalString);
                }
                else
                {
                    logger.error("No JournalManager found");
                }
            }
        }
        // Proceed to next site
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}
