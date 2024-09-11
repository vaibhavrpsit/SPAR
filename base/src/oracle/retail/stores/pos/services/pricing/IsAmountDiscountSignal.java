/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/IsAmountDiscountSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:31 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/03/22 03:49:27  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.2  2004/03/03 21:03:45  cdb
 *   @scr 3588 Added employee transaction discount service.
 *
 *   Revision 1.1  2004/02/24 22:36:29  cdb
 *   @scr 3588 Added ability to check for previously existing
 *   discounts of the same type and capture the prorate user
 *   selection. Also migrated item discounts to validate in
 *   the percent and amount entered aisle to be consistent
 *   with employee discounts.
 *
 *   Revision 1.4  2004/02/20 17:34:58  cdb
 *   @scr 3588 Removed "developmental" log entries from file header.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

// foundation imports
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This signal determines if we are in Item Amount Discount
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IsAmountDiscountSignal implements TrafficLightIfc
{    
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6588615552429029099L;

    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Signal name for toString
    **/
    public static final String SIGNALNAME = "IsAmountDiscountSignal";
    
    //----------------------------------------------------------------------
    /**
        Determines whether it is safe for the bus to proceed.
        <p>
        @param bus the bus trying to proceed
        @return true if in item percent discount, false otherwise
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        DiscountCargoIfc cargo = (DiscountCargoIfc)bus.getCargo();
        boolean flag = false;
        
        if(cargo.getDiscountType() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
        {
            flag = true;
        }
        
        return flag;
    }   
}
