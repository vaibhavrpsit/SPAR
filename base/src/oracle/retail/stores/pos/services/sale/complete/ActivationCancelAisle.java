/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/ActivationCancelAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:08 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:30 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:23 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/06/23 23:19:29  lzhao
 *   @scr 5332: add open and close draw if activation fail.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

// Foundation imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    This aisle convert Continue and Cancel letter from Open Drawer failure dialog.

     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ActivationCancelAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    //----------------------------------------------------------------------
    /**
       Set attempts to open the cash drawer.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        bus.mail(new Letter("ActivationCancelled"), BusIfc.CURRENT);
    }
}
