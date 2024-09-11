/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/kit/ItemDiscountLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:25 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:25 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:37 PM  Robert Pearse   
 *
 *Revision 1.4  2004/04/09 16:56:00  cdb
 *@scr 4302 Removed double semicolon warnings.
 *
 *Revision 1.3  2004/02/12 16:51:05  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:48  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 29 2003 14:37:42   DCobb
 * Set the access function ID.
 * Resolution for POS SCR-3281: When manager override is performed for Discount Amt on the component options screen the Security Error appears again with the <ARG> missing.
 * 
 *    Rev 1.1   Apr 15 2003 14:16:26   bwf
 * Fix comments
 * 
 *    Rev 1.0   Apr 15 2003 14:12:14   bwf
 * Initial revision.
 * Resolution for 2103: Remove uses of deprecated items in POS.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.kit;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
//--------------------------------------------------------------
/**
    This shuttle carries the required contents from
    the components service to the item discount service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------
public class ItemDiscountLaunchShuttle extends FinancialCargoShuttle
{
    /** The logger to which log messages will be sent. **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifyitem.kit.ItemDiscountLaunchShuttle.class);

    /** revision number supplied by source-code-control system **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /** class name constant **/
    public static final String SHUTTLENAME = "ItemDiscountLaunchShuttle";

    /** Pricing Cargo **/
    protected ItemCargo itemCargo = null;

    //--------------------------------------------------------------------------
    /**
        Copies information from the cargo used in the components service. <P>
        @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        itemCargo = (ItemCargo)bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
       Copies information to the cargo used in the components service. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        PricingCargo pc = (PricingCargo) bus.getCargo();
        pc.setResourceID(itemCargo.getResourceID());
        pc.setAccessFunctionID(itemCargo.getAccessFunctionID());
        pc.setItem(itemCargo.getItem());
        pc.setItems(itemCargo.getItems());
        pc.setIndices(itemCargo.getIndices());
        pc.setTransaction(itemCargo.getTransaction());
        pc.setDiscountType(itemCargo.getDiscountType());
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
