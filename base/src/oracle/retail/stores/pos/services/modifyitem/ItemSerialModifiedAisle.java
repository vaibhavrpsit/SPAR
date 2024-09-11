/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ItemSerialModifiedAisle.java /main/13 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         6/4/2007 6:01:32 PM    Alan N. Sinton  CR
 *         26486 - Changes per review comments.
 *    4    360Commerce 1.3         5/14/2007 2:32:57 PM   Alan N. Sinton  CR
 *         26486 - EJournal enhancements for VAT.
 *    3    360Commerce 1.2         3/31/2005 4:28:33 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:41 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:03  mcs
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
 *    Rev 1.0   Aug 29 2003 16:01:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:17:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:37:16   msg
 * Initial revision.
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
package oracle.retail.stores.pos.services.modifyitem;

// foundation imports
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when a serial number has been entered.
    <p>
    @version $Revision: /main/13 $
    @deprecated - functionality replaced by services\modifyitem\serialnumber\serializedItem.xml service
    **/
//--------------------------------------------------------------------------
public class ItemSerialModifiedAisle extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //----------------------------------------------------------------------
    /**
       Sets serial number entered in the UI.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        POSUIManagerIfc ui
            = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

        // Add the serial number to the SaleReturnLineItem
        String serialNumber = ui.getInput();
        SaleReturnLineItemIfc lineItem = cargo.getItem();

        // Journal item removal
        StringBuffer sb = new StringBuffer(formatter.toJournalRemoveString(lineItem));

        // Set the new serial number
        lineItem.setItemSerial(serialNumber);

        JournalManagerIfc jmgr =
            (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);

        if (jmgr != null)
        {
            //save new item info in stringbuffer for journal
            sb.append(Util.EOL);
            sb.append(formatter.toJournalString(lineItem, null, null));
            //write the journal
            jmgr.journal(sb.toString());
        }
        else
        {
            logger.warn( "No journal manager found!");
        }

        // Done modifying serial number, mail a final letter.
        bus.mail(new Letter(CommonLetterIfc.OK), BusIfc.CURRENT);

    }
}
