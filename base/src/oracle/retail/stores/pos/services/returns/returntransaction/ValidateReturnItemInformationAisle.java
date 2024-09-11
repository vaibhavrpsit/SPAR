/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/ValidateReturnItemInformationAisle.java /main/14 2012/10/29 12:55:21 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/25/12 - Modified use method on SaleReturnLineItemIfc rather
 *                         than calcualtating the value in the site.
 *                         OrderLineItem has its own implementation of this
 *                         method.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/22/2006 11:45:20 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/20 16:33:14  mweis
 *   @scr 6159 Serial number message takes precedence over typical item quantity.
 *
 *   Revision 1.4  2004/03/03 23:15:14  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:04:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:52   msg
 * Initial revision.
 * 
 *    Rev 1.2   Feb 23 2002 10:35:06   mpm
 * Modified Util.BIG_DECIMAL to Util.I_BIG_DECIMAL, Util.ROUND_HALF to Util.I_ROUND_HALF
 * Resolution for POS SCR-1398: Accept Foundation BigDecimal backward-compatibility changes
 * 
 *    Rev 1.1   Feb 05 2002 16:43:28   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.0   Sep 21 2001 11:25:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;
import java.math.BigDecimal;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle validates the returned item information.
 */
public class ValidateReturnItemInformationAisle extends PosLaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -6197051283897524435L;

    /**
     * Constant for invalid quantity error screen
     */
    public static final String INVALID_QUANTITY = "InvalidQuantity";

    /**
     * Constant for invalid serial quantity error screen
     */
    public static final String QUANTITY_NOTICE = "QuantityNotice";

    /**
     * This aisle validates the returned item information. Specifically, it
     * makes sure that user does not return more items than were purchased.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Check to see if the quantity to return entered by the operator
        // is greater than the quantity available to be return.
        ReturnTransactionCargo cargo = (ReturnTransactionCargo)bus.getCargo();
        BigDecimal   qtyToReturn = cargo.getItemQuantity();
        SaleReturnLineItemIfc      item = cargo.getSaleLineItem();
        BigDecimal qtyReturnable = item.getQuantityReturnable().subtract(qtyToReturn);
        
        // Serial number takes precedence
        if (!Util.isEmpty(item.getItemSerial()) &&
                 !Util.isObjectEqual(cargo.getItemQuantity(),
                                        BigDecimalConstants.ONE_AMOUNT))
        {
            // Using "generic dialog bean".
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(QUANTITY_NOTICE);
            model.setType(DialogScreensIfc.ERROR);

            // set and display the model
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else if (qtyReturnable.signum() < 0)
        {
            // Using "generic dialog bean".
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(INVALID_QUANTITY);
            model.setType(DialogScreensIfc.ERROR);

            // set and display the model
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }
}
