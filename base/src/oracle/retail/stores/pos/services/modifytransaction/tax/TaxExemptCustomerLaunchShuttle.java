/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/TaxExemptCustomerLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    asinton   06/01/09 - Set the offline indicator to OFFLINE_ADD instead of
 *                         OFFLINE_LINK as orders require the attached customer
 *                         object.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:41 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:31  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   15 Jan 2002 17:18:00   baa
 * fix defects
 * Resolution for POS SCR-676: Application hangs when adding a customer thru the Tax Exmept use case
 *
 *    Rev 1.0   Sep 21 2001 11:31:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;
// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;

//------------------------------------------------------------------------------
/**
    Launch shuttle class for TaxExemptCustomerLaunchShuttle service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TaxExemptCustomerLaunchShuttle extends  FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifytransaction.tax.TaxExemptCustomerLaunchShuttle.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

   /**
       modify transaction tax cargo
    **/
    protected ModifyTransactionTaxCargo modifyTransactionTaxCargo = null;

    //---------------------------------------------------------------------
    /**
       Loads parent (ModifyTransactionTax) cargo class. <P>
       @param b  bus interface
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo
        modifyTransactionTaxCargo = (ModifyTransactionTaxCargo) bus.getCargo();
    }

    //---------------------------------------------------------------------
    /**
       Unloads to child (CustomerCargo) cargo class. <P>
       @param b  bus interface
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // pull out transaction object, employee acces etc.
        RetailTransactionIfc saleTransaction =
          modifyTransactionTaxCargo.getTransaction();
        String transactionID = null;
        if (saleTransaction !=null)
        {
           transactionID = saleTransaction.getTransactionID();
        }


        // retrieve cargo
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();
        // if customer link is required set offline flag to OFFLINE_EXIT
        if (modifyTransactionTaxCargo.getCustomerLinked())
        {
           cargo.setOfflineIndicator(CustomerMainCargo.OFFLINE_ADD);
        }
        cargo.setTransactionID(transactionID);
        cargo.setRegister(modifyTransactionTaxCargo.getRegister());
        cargo.setOperator(modifyTransactionTaxCargo.getOperator());
        cargo.setSalesAssociate(modifyTransactionTaxCargo.getSalesAssociate());
    }

}   // end class TaxExemptLaunchShuttle

