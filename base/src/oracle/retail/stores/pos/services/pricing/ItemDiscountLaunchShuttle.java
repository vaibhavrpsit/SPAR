/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/ItemDiscountLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:37 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/13 22:24:29  cdb
 *   @scr 3588 Added dialog to indicate when discount will reduce
 *   some prices below zero but not others.
 *
 *   Revision 1.3  2004/02/12 16:51:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:06  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Oct 17 2003 10:24:42   bwf
 * Add employeeDiscountID and remove unused imports.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 * 
 *    Rev 1.0   Aug 29 2003 16:05:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 11 2003 11:28:42   baa
 * add security access check for price override, markdowns and item discounts
 * Resolution for 3140: The Manager Override screen is not presented when a user with no access selects the Item Discount Amount button.
 * 
 *    Rev 1.1   05 Jun 2002 17:13:14   jbp
 * changes for pricing feature
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   02 May 2002 17:39:06   jbp
 * Initial revision.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents from
    the pricing service to the price override service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ItemDiscountLaunchShuttle extends FinancialCargoShuttle
{
    /** The logger to which log messages will be sent. **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.pricing.ItemDiscountLaunchShuttle.class);

    /** revision number supplied by source-code-control system **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** class name constant **/
    public static final String SHUTTLENAME = "ItemDiscountLaunchShuttle";

    /** Pricing Cargo **/
    protected PricingCargo pricingCargo = null;

    //--------------------------------------------------------------------------
    /**
       Copies information from the cargo used in the pricing service. <P>
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        pricingCargo = (PricingCargo)bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
       Copies information to the cargo used in the pricing service. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        PricingCargo pc = (PricingCargo) bus.getCargo();
        pc.setItems(pricingCargo.getItems());
        pc.setIndices(pricingCargo.getIndices());
        pc.setTransaction(pricingCargo.getTransaction());
        pc.setDiscountType(pricingCargo.getDiscountType());
        pc.setAccessFunctionID(RoleFunctionIfc.DISCOUNT);
        pc.setEmployeeDiscountID(pricingCargo.getEmployeeDiscountID());

    }

    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
