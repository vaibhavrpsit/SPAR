/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/SaleReturnShuttle.java /main/18 2014/02/26 14:19:58 vbongu Exp $
* ===========================================================================
* Rev 1.0	Aug 23,2016		Ashish Yadav	Changes for code merging
* ===================================================
*/
package max.retail.stores.pos.services.sale;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.main.SaleReturnShuttle;

/**
 * This shuttle copies information from the cargo used in the modifyItem service
 * to the cargo used in the Alterations service.
 *
 * @version $Revision: /main/18 $
 */
public class MAXSaleReturnShuttle extends SaleReturnShuttle
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -1545911469313547532L;

    /**
     * The logger to which log messages will be sent.
     */
	 // changes starts for rev 1.0
    //protected static final Logger logger = Logger.getLogger(SaleReturnShuttle.class);
		protected static final Logger logger = Logger.getLogger(MAXSaleReturnShuttle.class);
	// changes ends for rev 1.0	

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/18 $";

    /**
     * sale cargo
     */
    protected SaleCargoIfc saleCargo;

    /**
     * Loads cargo from alterations service.
     *
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        saleCargo = (SaleCargoIfc) bus.getCargo();
    }

    /**
     * Loads cargo for modifyItem service.
     *
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
		// changes starts for rev 1.0
        //SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
		// changes ends for rev 1.0
        cargo.setItemQuantity(BigDecimal.valueOf(1));
        cargo.setAccessFunctionID(saleCargo.getAccessFunctionID());
        cargo.setPasswordRequired(saleCargo.isPasswordRequired());
        cargo.setCustomerInfo(saleCargo.getCustomerInfo());
        cargo.setEmployee(saleCargo.getEmployee());
        cargo.setLineItem(saleCargo.getLineItem());
        cargo.setOperator(saleCargo.getOperator());
        cargo.setPLUItem(saleCargo.getPLUItem());
        cargo.setIndices(saleCargo.getIndices());
        cargo.setTransaction(saleCargo.getTransaction());
        cargo.setRegister(saleCargo.getRegister());
        cargo.setStoreStatus(saleCargo.getStoreStatus());
        cargo.setLastReprintableTransactionID(saleCargo.getLastReprintableTransactionID());
        cargo.setOriginalReturnTransactions(saleCargo.getOriginalReturnTransactions());
        cargo.setOriginalPriceAdjustmentTransactions(saleCargo.getOriginalPriceAdjustmentTransactions());
        cargo.setAlreadySetTransactionSalesAssociate(false);
		// changes starts for rev 1.0
		cargo.setGiftCardApproved(((MAXSaleCargoIfc) saleCargo).isGiftCardApproved());
		// changes ends for rev 1.0
		//cargo.setPLUItems(((MAXSaleCargo) saleCargo).getPLUItems());
        /*
         * Set refresh needed to true so that the items get updated.  This can
         * happen when a gift card activation fails and we return to the sale service
         * and a transaction is still in progress.
         */
        cargo.setRefreshNeeded(true);

        if( !cargo.isCashDrawerUnderWarning())
        {
          cargo.setCashDrawerUnderWarning(saleCargo.isCashDrawerUnderWarning());
        }
		
		//Changes starts for Rev 1.10


        
        if(saleCargo.getTransaction()!=null && saleCargo.getTransaction() instanceof MAXSaleReturnTransaction){
        	MAXSaleReturnTransaction returnTransaction=(MAXSaleReturnTransaction)saleCargo.getTransaction();
        if(returnTransaction.getMAXTICCustomer()!=null && returnTransaction.getMAXTICCustomer() instanceof MAXTICCustomer){
        	cargo.setTicCustomer((MAXTICCustomer)returnTransaction.getMAXTICCustomer());
        }
        }

        
    	//changes ends for Rev 1.10

        if(cargo instanceof TimedCargoIfc && saleCargo instanceof TimedCargoIfc)
        {
            ((TimedCargoIfc)cargo).setTimeout(((TimedCargoIfc)saleCargo).isTimeout());
        }
        // set flag to indicate that gift card activation should be suppressed
        cargo.setSuppressGiftCardActivation(saleCargo.isSuppressGiftCardActivation());
  }

    /**
     * Returns a string representation of this object.
     *
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  SaleReturnShuttle (Revision " + getRevisionNumber() + ") @" + hashCode());
        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
