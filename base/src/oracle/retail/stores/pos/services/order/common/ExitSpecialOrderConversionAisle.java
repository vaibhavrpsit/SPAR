/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/ExitSpecialOrderConversionAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:33 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    blarsen   09/09/09 - ExitSpecialOrder letter will provide a path out of
 *                         special orders and into the normal initiate sale
 *                         tours. This supports the fix to enable auto-logout
 *                         when register accountability is set.
 *    blarsen   09/09/09 - Helper Aisle to change letter to ExitSpecialOrder.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.order.common;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    Changes letter to ExitSpecialOrder 
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ExitSpecialOrderConversionAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -6298741871351621195L;

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    // Class name
    public static final String LANENAME = "ExitSpecialOrderConversionAisle";
    
    /*
     * This letter is used to unwind the special order flows which finalize transactions.
     * 
     * In these cases, accountability must be checked and, potentially, the operator should be logged out.
     * 
     */
    public static final String EXIT_SPECIAL_ORDER_LETTER = "ExitSpecialOrder";

    //----------------------------------------------------------------------
    /**
       Changes letter from Continue to ExitSpecialOrder.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
         bus.mail(new Letter(EXIT_SPECIAL_ORDER_LETTER), BusIfc.CURRENT);        
    }
}
