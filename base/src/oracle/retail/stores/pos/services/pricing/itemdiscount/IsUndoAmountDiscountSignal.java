/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/itemdiscount/IsUndoAmountDiscountSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:35 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/03/22 03:49:27  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.3  2004/02/12 16:51:36  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:05  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Oct 17 2003 10:44:42   bwf
 * Initial revision.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.itemdiscount;

// foundation imports
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.pricing.PricingCargo;

//--------------------------------------------------------------------------
/**
    This signal determines if we are in Item Amount Discount
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IsUndoAmountDiscountSignal implements TrafficLightIfc
{    
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8900544589097655576L;

    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Signal name for toString
    **/
    public static final String SIGNALNAME = "IsUndoAmountDiscountSignal";
    
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
        PricingCargo cargo = (PricingCargo)bus.getCargo();
        boolean flag = false;
        
        if(cargo.getDiscountType() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
        {
            flag = true;
        }
        
        return flag;
    }   
     
    //  ----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
        Returns a string representation of the object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        String strResult = new String("Class:  " + SIGNALNAME + " (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
         return(strResult);
    }                                   // end toString()
}
