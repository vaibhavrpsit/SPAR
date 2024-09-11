/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ModifyItemSendReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:25 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:01:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:17:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:37:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   26 Nov 2001 10:52:40   sfl
 * Clean up on comments
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.0   19 Nov 2001 14:49:52   sfl
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

// foundation imports
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//------------------------------------------------------------------------------
/**
    Shuttles the required data from the Item cargo.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ModifyItemSendReturnShuttle extends FinancialCargoShuttle
{
    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Item Cargo
    **/

    protected ItemCargo iCargo = null;

    protected SaleReturnLineItemIfc[] items = null;

    protected RetailTransactionIfc transaction = null;


    //---------------------------------------------------------------------
    /**
       Get a local copy of the item cargo. Retrieve the send information.
       <P>
       @param bus the bus being loaded
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        // retrieve item cargo
        iCargo = (ItemCargo) bus.getCargo();
        items = iCargo.getItems();
        transaction = iCargo.getTransaction();
    }

    //---------------------------------------------------------------------
    /**
       Copy required data from the send service item cargo to the modify item service
       item Cargo.

       @param bus the bus being unloaded
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        // retrieve Item cargo
        ItemCargo cargo = (ItemCargo)bus.getCargo();

        cargo.setTransaction((RetailTransactionIfc)transaction);
        cargo.setItems(items);

    }

}

