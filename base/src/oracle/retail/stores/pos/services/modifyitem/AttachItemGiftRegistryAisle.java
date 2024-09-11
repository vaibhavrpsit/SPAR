/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/AttachItemGiftRegistryAisle.java /main/12 2012/09/12 11:57:10 blarsen Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:41 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:32 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:51:01  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 11 2003 13:14:38   bwf
 * Remove deprecated Gift Registry calls.
 * Resolution for 2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.0   Apr 29 2002 15:16:38   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:36:50   msg
 * Initial revision.
 *
 *    Rev 1.1   01 Feb 2002 15:20:46   sfl
 * Took away the unnecessary gift registry id logging
 * from the E-Journal.
 * Resolution for POS SCR-962: Entering invalid Gift Registry numbers journals like valid numbers
 *
 *    Rev 1.0   Sep 21 2001 11:28:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.FinalLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Update the modified item's Gift Registry Put it into cargo Mail a final
 * letter
 * 
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class AttachItemGiftRegistryAisle extends PosLaneActionAdapter
{
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Get UI input Put it into cargo update the modified item. Mail a final
     * letter
     * 
     * @param BusIfc bus
     * @return void
     * @exception
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        String itemID = cargo.getItem().getItemID();

        String newGiftId = "";
        String currentGiftId = "";

        StringBuilder buf =
            new StringBuilder("AttachItemGiftRegistryAisle Received input:");
        buf.append(newGiftId);
        if (logger.isInfoEnabled()) logger.info(buf.toString());

        // if there is a current gift registry, update the ID.  If not,
        // create a new gift registry object with the new ID.
        RegistryIDIfc giftRegistry = null;

        StringBuilder sb = new StringBuilder();

        if (cargo.getItem().getRegistry() != null)  // one exists already
        {
            giftRegistry  = cargo.getItem().getRegistry();

            // Save existing gift reg ID to journal it as having been removed,
            // then set the new ID.
            currentGiftId = giftRegistry.getID();
            giftRegistry.setID(currentGiftId);
        }


        //then set the item to the new value in the modified giftregistry.
        cargo.getItem().modifyItemRegistry(giftRegistry, true);

        Object dataArgs[]={itemID.trim()};
        String itemData=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM, dataArgs);

        //
        Object giftRegdataArgs[]={currentGiftId};
        String giftReg=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_REGISTER_LABEL, giftRegdataArgs);

        sb.append(Util.EOL)
          .append(itemData)
          .append(Util.EOL)
          .append(giftReg)
          .append(Util.EOL);


        // journal it here
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (journal != null)
        {
            journal.journal(cargo.getCashier().getEmployeeID(),
                            cargo.getTransactionID(),
                            sb.toString());
        }
        else
        {
            logger.warn( "No journal manager found!");
        }

        //And, move along.
        bus.mail(new FinalLetter("Next"), BusIfc.CURRENT);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
