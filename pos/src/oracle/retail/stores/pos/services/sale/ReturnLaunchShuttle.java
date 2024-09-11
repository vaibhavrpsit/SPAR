/* ===========================================================================
Rev 1.0  Aug 25, 2021              Atul Shukla                   EWallet FES Implementation
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

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
        
      //Changes starts for Rev 1.0
        MAXSaleReturnTransaction transaction=null;
        MAXCustomer cust=null;
        boolean isEwalletFlag=false;
        if(pCargo.getTransaction() instanceof MAXSaleReturnTransaction)
        {
        	transaction=(MAXSaleReturnTransaction)pCargo.getTransaction();
        }
        if(transaction.getCustomer() instanceof MAXCustomer)
        {
        	isEwalletFlag=((MAXCustomer)transaction.getCustomer()).isLMREWalletCustomerFlag();
        	   logger.info(" Inside ReturnLaunchShuttle Ewallet flag value   "+ isEwalletFlag );
        }
        if (cargo.getTransaction() != null && cargo.getTransaction().getCustomer() != null && cargo.getTransaction() instanceof MAXSaleReturnTransaction)
        {
        	if(cargo.getTransaction().getCustomer() instanceof MAXCustomer)
        	{
        		cust=(MAXCustomer)cargo.getTransaction().getCustomer();
        		cust.setLMREWalletCustomerFlag(isEwalletFlag);
        	
        		cargo.getTransaction().setCustomer(cust);
        	}
        }
        //Changes End for Rev 1.0
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