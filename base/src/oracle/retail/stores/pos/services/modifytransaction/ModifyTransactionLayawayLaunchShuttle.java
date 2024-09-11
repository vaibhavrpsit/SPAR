/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionLayawayLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:31 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:14:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   06 Mar 2002 16:29:36   baa
 * Replace get/setAccessEmployee with get/setOperator
 * Resolution for POS SCR-802: Security Access override for Reprint Receipt does not journal to requirements
 *
 *    Rev 1.0   Sep 21 2001 11:30:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

// foundation imports
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//------------------------------------------------------------------------------
/**
    Shuttles the required data from the Modify Transaction cargo to the Layaway Cargo.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ModifyTransactionLayawayLaunchShuttle
extends FinancialCargoShuttle
{
    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        Outgoing ModifyTransactionCargo
    **/
    protected ModifyTransactionCargo cargo = null;

    //---------------------------------------------------------------------
    /**
       Get a local copy of the Modify Transaction cargo. Retrieves the sale transaction,
       access employee, sales associated, and the customer, if linked to the current
       sale transaction.
       <P>
       @param bus the bus being loaded
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load FinancialCargoShuttle
        super.load(bus);

        // retrieve Modify Transaction cargo
        cargo = (ModifyTransactionCargo) bus.getCargo();
    }

    //---------------------------------------------------------------------
    /**
       Copy required data from the Modify Transaction cargo to the Layaway Cargo.
       sets the sale Transaction, access employee, customer, and sales associate.

       @param bus the bus being unloaded
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);

        // retrieve layaway cargo
        LayawayCargo layawayCargo = (LayawayCargo) bus.getCargo();

        layawayCargo.setSaleTransaction((SaleReturnTransactionIfc)cargo.getTransaction());
        //layawayCargo.setAccessEmployee(cargo.getAccessEmployee());
        layawayCargo.setSalesAssociate(cargo.getSalesAssociate());
        if (cargo.getTransaction() != null)
        {
            layawayCargo.setCustomer(cargo.getTransaction().getCustomer());
        }
        layawayCargo.setRegister(cargo.getRegister());
    }

}
