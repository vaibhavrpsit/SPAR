/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/searchorder/ProcessExternalOrderLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    07/09/10 - extend FinancialShuttle
 *    sgu       06/22/10 - added the logic to process multiple send package
 *                         instead of just on per order
 *    acadar    06/11/10 - removed unused imports
 *    acadar    06/11/10 - changes for postvoid and signature capture
 *    acadar    06/08/10 - changes for signature capture, disable txn send, and
 *                         discounts
 *    acadar    06/02/10 - refactoring
 *    acadar    06/02/10 - signature capture changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/25/10 - additional fixes for the process order flow
 *    acadar    05/21/10 - renamed from _externalorder to externalorder
 *    acadar    05/21/10 - save external order id in the transaction object
 *    acadar    05/21/10 - additional changes for process order flow
 *    acadar    05/18/10 - changes
 *    acadar    05/17/10 - additional logic added for processing orders
 *    acadar    05/14/10 - initial version for process external order tour
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.searchorder;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.externalorder.processorder.ProcessOrderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.externalorder.LegalDocumentIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;


/**
 * This shuttle carries the required contents from
 * the search external order service to process external order service <P>
 * @author acadar
 */

public class ProcessExternalOrderLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{

    /**
     *
     */
    private static final long serialVersionUID = -3361075475644661830L;

    /**
        class name constant
    **/
    public static final String SHUTTLENAME = "ProcessExternalOrderLaunchShuttle";


    /**
     * Search External Order cargo
     */
    protected SearchOrderCargo cargo = null;



    /**
     * Gets the external order from the cargo
     * @param bus the bus being unloaded
     */
    public void load(BusIfc bus)
    {
    	super.load(bus);
        cargo = (SearchOrderCargo)bus.getCargo();
    }


    /**
     * Copies information to the cargo used by the process order service. <P>
     * @param bus the bus being unloaded
    */
    public void unload(BusIfc bus)
    {
    	super.unload(bus);
        ProcessOrderCargo processOrderCargo = (ProcessOrderCargo)bus.getCargo();
        processOrderCargo.setEmployee(cargo.getSalesAssociate());
        processOrderCargo.setStoreID(cargo.getOperator().getStoreID());


        //if a transaction is in progress set it in the cargo
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        if(transaction == null)
        {
            processOrderCargo.initializeTransaction(bus);
        }
        else
        {
            processOrderCargo.setTransaction(transaction);
        }


        ExternalOrderIfc externalOrder = (ExternalOrderIfc)cargo.getExternalOrder();

        processOrderCargo.setExternalOrder(externalOrder);
        processOrderCargo.setExternalOrderItems(externalOrder.getItemList());

        //save the order id and order number in the transaction
        processOrderCargo.getTransaction().setExternalOrderID(externalOrder.getId());
        processOrderCargo.getTransaction().setExternalOrderNumber(externalOrder.getNumber());

        //save the requireServiceContract flag
        processOrderCargo.getTransaction().setRequireServiceContractFlag(externalOrder.hasContract());
        // create legal documents
        if(externalOrder.hasContract())
        {

                LegalDocumentIfc document = DomainGateway.getFactory().getLegalDocumentInstance();

                document.setId(externalOrder.getContractId());

                document.setExternalOrderNumber(externalOrder.getNumber());
                processOrderCargo.getTransaction().addLegalDocument(document);


        }


    }


}
