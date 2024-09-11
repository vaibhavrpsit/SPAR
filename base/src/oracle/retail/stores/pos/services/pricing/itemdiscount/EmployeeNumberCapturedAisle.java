/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/itemdiscount/EmployeeNumberCapturedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:00 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:20 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:50 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/17/2005 16:39:29    Jason L. DeLeau 4345:
 *         Replace any uses of Gateway.log() with the log4j.
 *    3    360Commerce1.2         3/31/2005 15:27:58     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:20     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:50     Robert Pearse
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
 *    Rev 1.0   Oct 17 2003 10:45:16   bwf
 * Initial revision.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.itemdiscount;

// foundation imports
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;


//--------------------------------------------------------------------------
/**
    This aisle reads in the employee number.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EmployeeNumberCapturedAisle extends LaneActionAdapter
{
    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     *   Reads in the employee number from the UI. <P>
     *   @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
    	if (logger.isDebugEnabled()) logger.debug("Entering EmployeeNumberEnteredAisle.traverse()");

        PricingCargo cargo = (PricingCargo) bus.getCargo();
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // get input from ui
        cargo.setEmployeeDiscountID(ui.getInput());

        String letter = "Amount";
        // check to see if this is an item discount by amount or percent
        if(cargo.getDiscountType() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
        {
            letter = "Amount";
        }
        else
        {
            letter = "Percent";
        }
        bus.mail(new Letter(letter), BusIfc.CURRENT);

        if (logger.isDebugEnabled()) logger.debug("Exiting EmployeeNumberEnteredAisle.traverse()");
    }
    //  ----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  EmployeeNumberCapturedAisle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
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
    }
}
