/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/AddItemAisle.java /rgbustores_13.4x_generic_branch/2 2011/07/07 12:20:06 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This site adds an item to the transaction.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
@SuppressWarnings("serial")
public class AddItemAisle extends PosLaneActionAdapter
{
    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * Adds the item to the transaction. Mails Continue letter is special order
     * to not ask for serial numbers, else mails GetSerialNumbers letter to
     * possibly ask for serial numbers.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Grab the item from the cargo
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();

        SaleReturnLineItemIfc item = cargo.getLineItem();

        // Indicate whether the item ID was scanned or typed
        if (cargo.isItemScanned())
        {
            item.setEntryMethod(EntryMethod.Scan);
            cargo.setItemScanned(false);
        }
        cargo.setLineItem(item);        
     
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}

