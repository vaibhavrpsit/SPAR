/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/employeediscount/CaptureEmployeeNumberSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   12/21/10 - Changed to use an updated (and consistent) method in
 *                         DiscountUtility.
 *    abondala  06/21/10 - Disable item level editing for an external order
 *                         line item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:09:48 PM  Robert Pearse   
 *
 *Revision 1.8  2004/03/22 18:35:05  cdb
 *@scr 3588 Corrected some javadoc
 *
 *Revision 1.7  2004/03/22 03:49:28  cdb
 *@scr 3588 Code Review Updates
 *
 *Revision 1.6  2004/03/16 18:30:43  cdb
 *@scr 0 Removed tabs from all java source code.
 *
 *Revision 1.5  2004/02/24 16:21:31  cdb
 *@scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *Revision 1.4  2004/02/20 17:34:58  cdb
 *@scr 3588 Removed "developmental" log entries from file header.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.employeediscount;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the EMPLOYEE_NUMBER screen if the employee ID
    hasn't been captured earlier.
    <p>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CaptureEmployeeNumberSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 2038336700927718529L;

    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
       Displays the EMPLOYEE_NUMBER screen if the employee ID
       hasn't been captured earlier.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        
        boolean employeeIDFound = false;
        if (!Util.isEmpty(cargo.getEmployeeDiscountID()))
        {    
            employeeIDFound = true;
        }
        else
        {    
            String employeeID = checkForEmployeeID(cargo.getItems());
            if (!Util.isEmpty(employeeID))
            {
                employeeIDFound = true;
                cargo.setEmployeeDiscountID(employeeID);
            }
        }
        
        if (employeeIDFound)
        {    
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
        else
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            
            // display employee_number screen
            // setModel makes sure that there is model in the
            // ui subsystem for the bean to work with.
            ui.showScreen(POSUIManagerIfc.EMPLOYEE_NUMBER, new POSBaseBeanModel());
        }
    }

    //----------------------------------------------------------------------
    /**
         Determines if any of the line items have an employee discount with
         an employee ID. <P>
         @param  lineItems The Sale return Line Items to search
         @return The employee ID
     **/
    //----------------------------------------------------------------------
    public String checkForEmployeeID(SaleReturnLineItemIfc[] lineItems)
    {
        String employeeID = "";
        if (lineItems != null)
        {
            mainLoop: for(int i=0; i < lineItems.length; i++)
            {
                SaleReturnLineItemIfc srli = lineItems[i];
                
                // If the item is not discount eligible, go on to 
                // next item.
                if (!(DiscountUtility.isEmployeeDiscountEligible(srli)))
                {
                    continue;
                }
                else
                {
                    // Scan through the discounts that exist in search of an employee ID
                    // Check discounts by amount first
                    ItemDiscountStrategyIfc[] currentDiscounts = srli.getItemPrice().getItemDiscountsByAmount();
                    for (int x = 0; x < currentDiscounts.length; x++)
                    {
                        // We're only interested in employee discounts
                        if (currentDiscounts[x].getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
                        {
                            // Once we find an employee discount with an employee ID, we're done
                            employeeID = currentDiscounts[x].getDiscountEmployeeID();
                            if (!Util.isEmpty(employeeID))
                            {    
                                break mainLoop;
                            }
                        }
                    }
                    // Then check discounts by percent
                    currentDiscounts = srli.getItemPrice().getItemDiscountsByPercentage();
                    for (int x = 0; x < currentDiscounts.length; x++)
                    {
                        // We're only interested in employee discounts
                        if (currentDiscounts[x].getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
                        {
                            // Once we find an employee discount with an employee ID, we're done
                            employeeID = currentDiscounts[x].getDiscountEmployeeID();
                            if (!Util.isEmpty(employeeID))
                            {    
                                break mainLoop;
                            }
                        }
                    }
                }
            }
        }
        return employeeID;
    }
}
