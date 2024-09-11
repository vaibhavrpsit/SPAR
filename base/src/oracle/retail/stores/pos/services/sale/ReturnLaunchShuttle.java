/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ReturnLaunchShuttle.java /main/14 2012/12/10 19:16:38 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/19/11 - encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:52 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:54 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/08 20:33:03  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;

/**
 * This shuttle transfers data from the POS service to the Return service.
 * 
 * @version $Revision: /main/14 $
 */
public class ReturnLaunchShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 2393401230567868135L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ReturnLaunchShuttle.class);

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    // Parent Cargo
    protected SaleCargoIfc pCargo = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        // Call load on FinancialCargoShuttle
        super.load(bus);

        // retrieve cargo from the parent(Sales Cargo)
        pCargo = (SaleCargoIfc)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        // Call unload on FinancialCargoShuttle
        super.unload(bus);

        // retrieve cargo from the child(ReturnOptions Cargo)
        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();

        // Set data in child cargo.
        cargo.setOriginalReturnTransactions(pCargo.getOriginalReturnTransactions());
        cargo.setSalesAssociate(pCargo.getEmployee());
        cargo.setAccessFunctionID(RoleFunctionIfc.RETURN);
        if (pCargo.getTransaction() != null)
        {
            cargo.setTransaction((SaleReturnTransactionIfc)pCargo.getTransaction().clone());
        }
        
        // Check to see if there is already valid customer info for a return.
        CustomerInfoIfc customerInfo = pCargo.getCustomerInfo();
        if (customerInfo != null)
        {
            if (!StringUtils.isEmpty(customerInfo.getPersonalID().getMaskedNumber()))
            {
                cargo.setCustomerInfoCollected(true);
            }
            else
            {
                cargo.setCustomerInfo((CustomerInfoIfc)customerInfo.clone());
                cargo.setCustomerInfoCollected(false);
            }
        }
    }
}