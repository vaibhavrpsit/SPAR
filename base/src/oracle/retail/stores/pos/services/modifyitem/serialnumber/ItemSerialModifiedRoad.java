/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/serialnumber/ItemSerialModifiedRoad.java /main/13 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   05/29/12 - added code to journal serial number for the case of
 *                         MPOS
 *    cgreene   05/08/12 - implement force use of entered serial number instead
 *                         of checking with SIM
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  11/17/09 - Serialisation code changes
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/25/2008 4:38:10 AM   Sujay Beesnalli
 *         Forward ported from v12x. Add serial number to journal entry, if
 *         the item is non-serialized.
 *    3    360Commerce 1.2         3/31/2005 4:28:33 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:31 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:41 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:51:06  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:18:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:54   msg
 * Initial revision.
 *
 *    Rev 1.1   16 Jan 2002 13:01:36   baa
 * allow for adding serial item to non serialized items
 * Resolution for POS SCR-579: Unable to manually enter a serial number to an item
 *
 *    Rev 1.0   07 Dec 2001 12:53:06   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.1   02 Dec 2001 11:13:44   pjf
 * Deprecated in favor of services\modifyitem\serialnumber\serializedItem.xml
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   Sep 21 2001 11:29:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.serialnumber;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * This aisle is traversed when a serial number has been entered and the user
 * has chosen to accept the input.
 * 
 * @version $Revision: /main/13 $
 */
@SuppressWarnings("serial")
public class ItemSerialModifiedRoad extends PosLaneActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * Sets serial number entered in the UI.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        SerializedItemCargo cargo   = (SerializedItemCargo)bus.getCargo();
        //get the current SaleReturnLineItem
        SaleReturnLineItemIfc item = cargo.getItem();

        //check for a previously entered serial number
        String oldSerial = cargo.getItem().getItemSerial();
        String newSerial = ui.getInput();

        //if a serial number was previously entered
        if (!Util.isEmpty(oldSerial))
        {
            //check to see if it changed
            if (!oldSerial.equals(newSerial))
            {
                //if so, journal the change and set the new value
                journalSerialNumberChange(bus, item, newSerial);
            }
        }
        else
        {
            //otherwise just set the serial number and proceed
            item.setItemSerial(newSerial);
            //also add a journal entry for this if the item is non-serialized one
            if (!item.isSerializedItem())
            {
            	journalSerialNumberAddedToItem(bus, item, newSerial);
            }
            else
            {
                // MobilePOS will have serialized items that do not
                // yet have serial number so journal it here.
                journalSerialNumber(bus, item, newSerial);
            }
        }
    }

    /**
     * Journal the serial number as if it was original to the serialized item
     * @param bus
     * @param item
     * @param newSerial
     */
    protected void journalSerialNumber(BusIfc bus, SaleReturnLineItemIfc item, String newSerial)
    {
        Object serialDataArgs[] = {newSerial};
        String serialNumberData=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SERIAL_NUMBER, serialDataArgs);
        StringBuilder builder = new StringBuilder(serialNumberData);
        builder.append("\n");
        JournalManagerIfc journalManager = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if(journalManager != null)
        {
            journalManager.journal(builder.toString());
        }
    }

    /**
     * Journals the serial Number added to an item.
     * @param item The item against which the serial number is added
     * @param newSerial Serial Number
     */
    protected void journalSerialNumberAddedToItem(BusIfc bus, SaleReturnLineItemIfc item, String newSerial)
    {
        Object dataArgs[]={item.getItemID()};
        String itemData=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_DATA, dataArgs);

        Object serialDataArgs[] = {newSerial};
        String serialNumberData=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SERIAL_NUMBER, serialDataArgs);

        StringBuilder s = new StringBuilder ("\n")
        			.append(itemData)
        			.append("\n").append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SERIAL_NUMBER_ADDED_LABEL, null))
        			.append("\n").append(serialNumberData);

    	item.setItemSerial(newSerial);
    	JournalManagerIfc journalManager = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
    	if(journalManager != null){
    		journalManager.journal(s.toString());
    	}
    }

    /**
     * Journals serial number changes and sets serial number entered in the UI.
     * 
     * @param bus Service Bus
     */
    protected void journalSerialNumberChange(BusIfc bus, SaleReturnLineItemIfc lineItem, String newSerial)
    {
        //journal item removal
        Object dataArgs[]={lineItem.getItemID()};
        String itemData=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_DATA, dataArgs);

        Object oldSerialDataArgs[]={lineItem.getItemSerial()};
        String oldSerialNumber=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SERIAL_NUMBER_OLD, oldSerialDataArgs);

        Object newSerialDataArgs[]={newSerial};
        String newSerialNumber=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SERIAL_NUMBER_NEW, newSerialDataArgs);
        StringBuilder s = new StringBuilder("\n")
        					.append(itemData)
        					.append("\n").append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SERIAL_NUMBER_MODIFIED_LABEL,null))
        					.append("\n").append(oldSerialNumber)
        					.append("\n").append(newSerialNumber)
        					.append("\n");



        //set the new serial number
        lineItem.setItemSerial(newSerial);

        JournalManagerIfc jmgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        if (jmgr != null)
        {
            //write the journal
            jmgr.journal(s.toString());
        }
    }

}
