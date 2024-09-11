/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/kit/KitComponentPriceModifiedAisle.java /rgbustores_13.4x_generic_branch/1 2011/11/07 11:48:54 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   11/04/11 - clear the discounts for price modified kit component
 *                         item
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:13 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/08/03 19:12:55  dcobb
 *   @scr 5440 Price Override: Indicator not printing on Kit Components
 *
 *   Revision 1.3  2004/02/12 16:51:05  mcs
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
 *    Rev 1.0   Apr 29 2002 15:18:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:37:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   08 Nov 2001 09:21:42   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.kit;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.modifyitem.ItemPriceModifiedAisle;

/**
 * Journals the item price modification.
 */
public class KitComponentPriceModifiedAisle extends ItemPriceModifiedAisle
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1672649923569299690L;

    /**
     * Calls the superclass method to journal the item price override and mails
     * a letter to return to the component options screen.
     * 
     * @param BusIfc bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        SaleReturnLineItemIfc item = cargo.getItem();
        // Clear item discounts
        item.getItemPrice().clearItemDiscounts();

        savePriceModification(bus);
        bus.mail(new Letter(CommonLetterIfc.OPTIONS), BusIfc.CURRENT);
    }
}
