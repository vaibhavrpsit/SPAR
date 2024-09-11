/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ModifyTransactionLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/22/14 - Refactor to restore Fulfillment main option flow.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    aariyer   02/02/09 - Added files for Item Basket feature
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/28 16:06:53  rsachdeva
 *   @scr 4865 Transaction Sales Associate
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 07 2003 12:37:30   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.0   Nov 05 2003 14:14:28   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionCargo;

//--------------------------------------------------------------------------
/**
    This class transfers data from the POS service to the Modify Transaction
    Service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ModifyTransactionLaunchShuttle extends FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.ModifyTransactionLaunchShuttle.class);

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       POS cargo
    **/
    protected SaleCargoIfc saleCargo = null;
    /**
       sales associate set using modify transaction sales associate
    **/
    protected boolean salesAssociateAlreadySet = false;
    //----------------------------------------------------------------------
    /**
       This method will clone the retail transaction from the parent cargo.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // load financial cargo
        super.load(bus);

        // retrieve cargo from the parent
        saleCargo = (SaleCargoIfc)bus.getCargo();
        salesAssociateAlreadySet = saleCargo.isAlreadySetTransactionSalesAssociate();



    }

    //----------------------------------------------------------------------
    /**
       The child cargo is passsed in here. Provide a reference for the cloned
       object from the child cargo.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // unload financial cargo
        super.unload(bus);

        RetailTransactionIfc transaction = null;
        // clone the transaction
        if (saleCargo.getTransaction() != null)
        {
            transaction = (RetailTransactionIfc)saleCargo.getTransaction().clone();
        }

        // retrieve cargo from the child(ModifyTransaction Cargo)
        ModifyTransactionCargo cargo = (ModifyTransactionCargo) bus.getCargo();

        // set the child reference to the cloned object
        cargo.setTransaction(transaction);
        cargo.setSalesAssociate(saleCargo.getEmployee());
        cargo.setOperator(saleCargo.getOperator());
        cargo.setRegister(saleCargo.getRegister());
        cargo.setStoreStatus(saleCargo.getStoreStatus());
        cargo.setTenderLimits(saleCargo.getTenderLimits());

        if (salesAssociateAlreadySet)
        {
            cargo.setAlreadySetTransactionSalesAssociate(true);
        }

        // transfer the fromFulfillment value
        cargo.setFromFulfillment(saleCargo.isFromFulfillment());
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  ModifyTransactionLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
