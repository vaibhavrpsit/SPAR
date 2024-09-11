/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/DiscountAmountSelectedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:35 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:34  mcs
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
 *    Rev 1.0   Aug 29 2003 16:05:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   02 May 2002 17:39:06   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

// foundation imports
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This road is traveled when discount by amt is selected.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class DiscountAmountSelectedRoad extends LaneActionAdapter
{
    /**
       revision number supplied by version control
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sets proper discount method
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        cargo.setDiscountType(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  " + this.getClass() + " Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
