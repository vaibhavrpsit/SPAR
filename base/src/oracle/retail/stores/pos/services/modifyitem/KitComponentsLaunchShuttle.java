/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/KitComponentsLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:25 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    nkgautam  03/08/10 - Added transaction to the cargo
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:00 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:13 PM  Robert Pearse
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:51:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:17:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:42:40   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   26 Oct 2001 10:00:18   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;
// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;


//--------------------------------------------------------------------------
/**
    This shuttle copies information from the Modify Item service cargo to the
    Kit Components service cargo.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class KitComponentsLaunchShuttle extends FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifyitem.KitComponentsLaunchShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       The modified line item.
    **/
    protected SaleReturnLineItemIfc lineItem = null;

    /**
     * Parent Item Cargo class
     */
    protected ItemCargo itemCargo;

    //----------------------------------------------------------------------
    /**
       Copies information from the cargo used in the Modify Item service.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        itemCargo = (ItemCargo)bus.getCargo();
        lineItem = itemCargo.getItem();
    }

    //----------------------------------------------------------------------
    /**
       Copies information to the cargo used in the components service.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        // retrieve cargo from the parent
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        cargo.setKitHeader((KitHeaderLineItemIfc)lineItem);
        cargo.setItem(null);
        cargo.setTransaction(itemCargo.getTransaction());
    }
}
