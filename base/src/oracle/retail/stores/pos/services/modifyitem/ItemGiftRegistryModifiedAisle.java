/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ItemGiftRegistryModifiedAisle.java /main/12 2012/09/12 11:57:10 blarsen Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:26 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:37 PM  Robert Pearse
 *
 *   Revision 1.6  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/20 13:17:05  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.4  2004/04/14 15:17:09  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:51:02  mcs
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
 *    Rev 1.0   Aug 29 2003 16:01:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Apr 11 2003 12:47:08   bwf
 * Deprecation Fix for Gift Registry.
 * Resolution for 2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.2   Mar 05 2003 10:46:48   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Oct 10 2002 08:22:42   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:16:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:08   msg
 * Initial revision.
 *
 *    Rev 1.3   12 Mar 2002 16:52:54   pdd
 * Modified to use the factory.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.2   Mar 09 2002 18:36:40   mpm
 * Externalized text.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.1   01 Feb 2002 15:21:38   sfl
 * Took away the unnecessary gift registry id logging
 * from the E-Journal.
 * Resolution for POS SCR-962: Entering invalid Gift Registry numbers journals like valid numbers
 *
 *    Rev 1.0   Sep 21 2001 11:28:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.RegistryDataTransaction;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.FinalLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
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
public class ItemGiftRegistryModifiedAisle extends PosLaneActionAdapter
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
        POSUIManagerIfc ui =(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        ItemCargo cargo = (ItemCargo)bus.getCargo();
        String itemID = cargo.getItem().getItemID();
        String currentGiftId = "";

        String newGiftId = ui.getInput();
        StringBuilder buf =
            new StringBuilder("ItemGiftRegistryModifiedAisle Received input:");
        buf.append(newGiftId);
        if (logger.isInfoEnabled()) logger.info(buf.toString());

        // if there is a current gift registry, update the ID.  If not,
        // create a new gift registry object with the new ID.
        RegistryIDIfc giftRegistry = null;

        StringBuilder sb = new StringBuilder();

        // if the string is > 0, set the gift registry to new id.
        if( newGiftId.length() > 0 )
        {
            if (cargo.getItem().getRegistry() != null)  // one exists already
            {
                giftRegistry  = cargo.getItem().getRegistry();
                // Save existing gift reg ID to journal it as having been removed,
                // then set the new ID.
                currentGiftId = giftRegistry.getID();
                giftRegistry.setID(newGiftId);

                // If gift reg ID changed, then reflect this in the ejournal
                // along with the item # on which the gift ID was changed
                if (!(currentGiftId.equals(newGiftId)) && !(currentGiftId.equals("")))
                {
                    Object dataArgs[]={itemID.trim()};
                    String itemData=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM, dataArgs);

                    //
                    Object giftRegdataArgs[]={currentGiftId};
                    String giftReg=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_GIFTREG_REMOVED, giftRegdataArgs);

                    sb.append(Util.EOL)
                      .append(itemData)
                      .append(Util.EOL)
                      .append(giftReg)
                      .append(Util.EOL);


                }
            }
            else
            {
                giftRegistry = (RegistryIDIfc) DomainGateway.getFactory().getGiftRegistryInstance();
                giftRegistry.setID(newGiftId);
            }

            // attempt to do the database lookup
            RegistryDataTransaction registryTr = null;

            registryTr = (RegistryDataTransaction) DataTransactionFactory.create(DataTransactionKeys.REGISTRY_DATA_TRANSACTION);

            try
            {

                giftRegistry = registryTr.readRegistryID(newGiftId);

                if ( giftRegistry != null)
                {
                    //And, move along.
                    bus.mail(new FinalLetter("Next"), BusIfc.CURRENT);

                    Object dataArgs[]={itemID.trim()};
                    String itemData=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM, dataArgs);

                    //
                    Object giftRegdataArgs[]={newGiftId};
                    String giftReg=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_REGISTER_LABEL, giftRegdataArgs);

                    sb.append(Util.EOL)
                      .append(itemData)
                      .append(Util.EOL)
                      .append(giftReg);

                }
            }
            catch (DataException e)
            {
                logger.warn( "" + e + "");
                cargo.setDataExceptionErrorCode(e.getErrorCode());
                //letterName = CommonLetterIfc.FAILURE;
                String[] args = new String[1];
                UtilityManagerIfc utility =
                  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                args[0] = utility.getErrorCodeString(DataException.CONNECTION_ERROR);

                if (e.getErrorCode() == 6)
                {
                    DialogBeanModel dialogModel = new DialogBeanModel();
                    dialogModel.setResourceID("INFO_NOT_FOUND_ERROR");
                    dialogModel.setType(DialogScreensIfc.ERROR);

                    giftRegistry.setID("");

                    //display dialog
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                }
                else
                {

                    DialogBeanModel dialogModel = new DialogBeanModel();
                    dialogModel.setResourceID("GiftRegConfirm");
                    dialogModel.setType(DialogScreensIfc.CONFIRMATION);
                    dialogModel.setArgs(args);

                    // display dialog
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                }
            }
        }
        // otherwise, if no id is entered, reset the gift registry to null.
        else
        {
            cargo.getItem().modifyItemRegistry((RegistryIDIfc) null, false);
            newGiftId = " ";
        }

        //then set the item to the new value in the modified giftregistry.
        cargo.getItem().modifyItemRegistry(giftRegistry, true);

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