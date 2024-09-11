/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/ValidateReturnSerialQuantityAisle.java /main/12 2011/12/05 12:16:30 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         1/25/2006 4:11:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:43 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     9/6/2005 11:01:16      Rohit Sachdeva
 *         QUANTITY_CANNOT_BE_ZERO dialog added for quantity 0
 *    3    360Commerce1.2         3/31/2005 15:30:43     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:26:42     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:15:29     Robert Pearse
 *
 *Log:
 *    5    360Commerce 1.4         1/25/2006 4:11:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:43 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *: ValidateReturnSerialQuantityAisle.java,v $
 *Log:
 *    5    360Commerce 1.4         1/25/2006 4:11:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:43 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *:
 *    4    .v710     1.2.2.0     10/20/2005 14:35:41    Murali Vasaudevan as
 *         part of 7.0.2 merge, it is fixed and tested
 *    3    360Commerce1.2         3/31/2005 15:30:43     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:26:42     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:15:29     Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:06:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:05:44   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Mar 2002 11:50:00   cir
 * Set the validation failed flag to true
 * Resolution for POS SCR-110: Return Item Info data cleared after Qty Notice for entering Qty > 1 and serial number
 *
 *    Rev 1.0   Mar 18 2002 11:46:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 23 2002 10:35:04   mpm
 * Modified Util.BIG_DECIMAL to Util.I_BIG_DECIMAL, Util.ROUND_HALF to Util.I_ROUND_HALF
 * Resolution for POS SCR-1398: Accept Foundation BigDecimal backward-compatibility changes
 *
 *    Rev 1.0   Sep 21 2001 11:25:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import java.math.BigDecimal;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle validates the returned item information.
 * 
 * @version $Revision: /main/12 $
 */
public class ValidateReturnSerialQuantityAisle extends LaneActionAdapter
{
    private static final long serialVersionUID = 4893408968279209378L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Constant for invalid serial quantity error screen
     */
    public static final String QUANTITY_NOTICE = "QuantityNotice";

    /**
     * Constant for invalid quantity cannot be zero
     */
    public static final String QUANTITY_CANNOT_BE_ZERO = "QuantityCannotBeZero";

    /**
     * This aisle validates the returned item information. Specifically, it
     * makes sure that user does return more items than were purchased.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Check to see if the quantity to return entered by the operator
        // is greater than the quantity avaiable to be return.
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();
        ReturnItemIfc itemReturn = cargo.getReturnItem();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        if (Util.isObjectEqual(cargo.getItemQuantity(), BigDecimal.ZERO))
        {
            cargo.setValidationFailed(true);
            UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, QUANTITY_CANNOT_BE_ZERO);
            return;
        }
        if (!Util.isEmpty(itemReturn.getSerialNumber())
                && !Util.isObjectEqual(cargo.getItemQuantity(), BigDecimalConstants.ONE_AMOUNT))
        {
            // set the validation failed flag in the cargo
            cargo.setValidationFailed(true);

            // Using "generic dialog bean".
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(QUANTITY_NOTICE);
            model.setType(DialogScreensIfc.ERROR);

            // set and display the model
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }

    }
}
