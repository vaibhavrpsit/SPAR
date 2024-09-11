/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/SetAccessRoleRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:25 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:11 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Jul 29 2003 14:40:18   DCobb
 * Use ItemDiscountStation.
 * Resolution for POS SCR-3280: When a user with no access attempts to apply a discount amount to a kit component, selecting yes on the security error screen results in a loop.
 * 
 *    Rev 1.3   Jun 26 2003 11:30:10   bwf
 * Set role id for discount % and amount.
 * Resolution for 2384: Suspended and Retrieved receipt printing <> on Item Discount % and Amt.
 * 
 *    Rev 1.2   May 19 2003 16:25:52   adc
 * Put back the code that checks for PriceOverride letter
 * Resolution for 2536: Override price on a kit component not working for user "POS" who has full access
 * 
 *    Rev 1.1   02 May 2002 17:35:48   jbp
 * initial changes for pricing service
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Apr 29 2002 15:17:20   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:42   msg
 * Initial revision.
 *
 *    Rev 1.0   Jan 19 2002 14:18:40   mpm
 * Initial revision.
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:28:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

// foundation imports
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//------------------------------------------------------------------------------
/**
     The UserAccessErrorAisle is traversed when the operator attempts
     access a function the she does have the authority to use.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/

//------------------------------------------------------------------------------

public class SetAccessRoleRoad extends PosLaneActionAdapter
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        class name constant
    **/
    public static final String LANENAME = "SetAccessRoleRoad";

    //--------------------------------------------------------------------------
    /**
        The SetAccessRoleRoad is traversed when the operator attempts
        access a function that requires a security access
         @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        String   letter  = bus.getCurrentLetter().getName();
        // if override flag is set
        ItemCargo cargo = (ItemCargo) bus.getCargo();
        cargo.setSecurityOverrideReturnLetter(letter);

        // check letter for security access
        if ( letter.equals("SalesAssociate"))
        {
           cargo.setAccessFunctionID(RoleFunctionIfc.SET_SALES_ASSOCIATE);
        }
        else if ( letter.equals("GiftRegistry"))
        {
           cargo.setAccessFunctionID(RoleFunctionIfc.SET_GIFT_REGISTRY);
        }
        else if(letter.equals("PriceOverride"))
        {
            cargo.setAccessFunctionID(RoleFunctionIfc.PRICE_OVERRIDE);
        }
        else if(letter.equals("DiscountAmount"))
        {
            cargo.setAccessFunctionID(RoleFunctionIfc.DISCOUNT);
            cargo.setDiscountType(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
        }
        else if (letter.equals("DiscountPercent"))
        {
            cargo.setAccessFunctionID(RoleFunctionIfc.DISCOUNT);
            cargo.setDiscountType(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE);
        }
    }


} // end class SetAccessRoleRoad
