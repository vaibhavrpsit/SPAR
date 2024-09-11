/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/PriceAdjustmentLaunchShuttle.java /main/13 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:28 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:24:19 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:13:22 PM  Robert Pearse   
 * $
 * Revision 1.2  2004/06/07 14:58:49  jriggins
 * @scr 5016 Added logic to persist previously entered transactions with price adjustments outside of the priceadjustment service so that a user cannot enter the same receipt multiple times in a transaction.
 *
 * Revision 1.1  2004/05/05 18:44:53  jriggins
 * @scr 4680 Moved Price Adjustment button from Sale to Pricing
 *
 * Revision 1.4  2004/04/27 21:26:14  jriggins
 * @scr 3979 Code review cleanup
 *
 * Revision 1.3  2004/03/30 23:49:17  jriggins
 * @scr 3979 Price Adjustment feature dev
 *
 * Revision 1.2  2004/03/30 00:04:59  jriggins
 * @scr 3979 Price Adjustment feature dev
 * Revision 1.1 2004/03/05 16:34:26 jriggins @scr 3979 Price Adjustment
 * additions
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.priceadjustment.PriceAdjustmentCargo;

/**
 * This shuttle transfers data from the POS service to the priceadjustment
 * service.
 * 
 * @version $Revision: /main/13 $
 */
public class PriceAdjustmentLaunchShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = -2716569916987530699L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(PriceAdjustmentLaunchShuttle.class);

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    // Parent Cargo
    PricingCargo pricingCargo = null;

    /**
     * Loads sale cargo data
     * 
     * @param bus Parent Service Bus to copy cargo from.
     */
    @Override
    public void load(BusIfc bus)
    {
        // Call load on FinancialCargoShuttle
        super.load(bus);

        // retrieve cargo from the parent(Sales Cargo)
        pricingCargo = (PricingCargo) bus.getCargo();
    }

    /**
     * Unloads data from the sale cargo to the priceadjustment cargo
     * 
     * @param bus Bus to copy cargo from
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        // Call unload on super class
        super.unload(bus);

        // retrieve cargo from the child(PriceAdjustmentCargo Cargo)
        PriceAdjustmentCargo cargo = (PriceAdjustmentCargo) bus.getCargo();

        // Set data in child cargo.
        cargo.setOriginalPriceAdjustmentTransactions(pricingCargo.getOriginalPriceAdjustmentTransactions());

        // Return Find Transaction info
        cargo.setHaveReceipt(true);
        cargo.setGiftReceiptSelected(false);
        cargo.setSearchByTender(false);
        SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc) pricingCargo.getTransaction();
        if (transaction == null)
        {
            // Copied from SaleCargo.initializeTransaction()
            transaction = DomainGateway.getFactory().getSaleReturnTransactionInstance();
            transaction.setCashier(pricingCargo.getOperator());
            transaction.setSalesAssociate(pricingCargo.getOperator());

            boolean transReentry = pricingCargo.getRegister().getWorkstation().isTransReentryMode();
            transaction.setReentryMode(transReentry);

            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
            utility.initializeTransaction(transaction);
            pricingCargo.setTransaction(transaction);
        }

        cargo.setTransaction(transaction);
        cargo.setCustomer(transaction.getCustomer());

        // Security Override info
        cargo.setOperator(pricingCargo.getOperator());
        cargo.setAccessFunctionID(RoleFunctionIfc.PRICE_ADJUST);
    }
}