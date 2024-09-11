/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processordersend/ProcessOrderSendCargo.java /main/10 2012/10/22 15:36:18 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED (MM/DD/YY)
*    yiqzha 10/19/12 - Add tax object for calling station to update lineitem
*                      tax.
*    yiqzha 04/03/12 - refactor store send for cross channel
*    sgu    06/22/10 - fix tabs
*    sgu    06/22/10 - added the logic to process multiple send package instead
*                      of just on per order
*    acadar 06/02/10 - refactoring
*    acadar 06/02/10 - signature capture changes
*    cgreen 05/26/10 - convert to oracle packaging
*    acadar 05/25/10 - additional fixes for the process order flow
*    acadar 05/21/10 - renamed from _externalorder to externalorder
*    sgu    05/20/10 - refactor site code due to api changes
*    acadar 05/17/10 - temporarily rename the package
*    acadar 05/17/10 - incorporated feedback from code review
*    acadar 05/17/10 - additional logic added for processing orders
*    acadar 05/14/10 - initial version for external order processing
*    acadar 05/14/10 - initial version
* ===========================================================================
*/
package oracle.retail.stores.pos.services.externalorder.processordersend;


import java.util.List;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tax.GeoCodeVO;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;


/**
    This is the cargo used by the process order send service
**/
public class ProcessOrderSendCargo extends AbstractFinancialCargo implements TimedCargoIfc,CargoIfc
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Show whether or not this transaction has timed out
     */
    protected boolean timeout = false;

    /**
     * Sale Return Transaction
     */
    protected SaleReturnTransactionIfc transaction;

    /**
     * Sale return line items marked for send
     */
    protected List<SaleReturnLineItemIfc> saleReturnSendLineItems;

    /**
     * ORPOS shipping method
     */
    protected ShippingMethodIfc shippingMethod;

    /**
     * Ship to Customer info
     */
    protected CustomerIfc shipToCustomer;

    /**
     * Shipping postal code
     */
    protected String shippingPostalCode;

    /**
     * List of geo codes
     */
    protected GeoCodeVO[] geoCodes;
    
    /**
     * destination tax rule is obtained by shipping destination postal code.
     */
    protected TaxRulesVO destinationTaxRule;
    
    /**
     * Tell whether or not a timeout has
     * occurred
     *
     * @return
     * @see oracle.retail.stores.pos.services.common.TimedCargoIfc#isTimeout()
     */
    public boolean isTimeout()
    {
        return this.timeout;
    }

    /**
     * Set whether or not a timeout
     * has occurred
     *
     * @param aValue
     * @see oracle.retail.stores.pos.services.common.TimedCargoIfc#setTimeout(boolean)
     */
    public void setTimeout(boolean aValue)
    {
        this.timeout = aValue;
    }

    /**
     * @return the transaction
     */
    public SaleReturnTransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * @param transaction the transaction to set
     */
    public void setTransaction(SaleReturnTransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    /**
     * @return the shippingMethod
     */
    public ShippingMethodIfc getShippingMethod()
    {
        return shippingMethod;
    }

    /**
     * @param shippingMethod the shippingMethod to set
     */
    public void setShippingMethod(ShippingMethodIfc shippingMethod)
    {
        this.shippingMethod = shippingMethod;
    }

    /**
     * @return the shippingPostalCode
     */
    public String getShippingPostalCode()
    {
        return shippingPostalCode;
    }

    /**
     * @param shippingPostalCode the shippingPostalCode to set
     */
    public void setShippingPostalCode(String shippingPostalCode)
    {
        this.shippingPostalCode = shippingPostalCode;
    }

    /**
     * @return Returns the geoCodes.
     */
    public GeoCodeVO[] getGeoCodes()
    {
        return geoCodes;
    }

    /**
     * @param geoCodes The geoCodes to set.
     */
    public void setGeoCodes(GeoCodeVO[] geoCodes)
    {
        this.geoCodes = geoCodes;
    }

    /**
     * @return the shipToCustomer
     */
    public CustomerIfc getShipToCustomer()
    {
        return shipToCustomer;
    }

    /**
     * @param shipToCustomer the shipToCustomer to set
     */
    public void setShipToCustomer(CustomerIfc shipToCustomer)
    {
        this.shipToCustomer = shipToCustomer;
    }

    /**
     * @return sale return line items marked for send
     */
    public List<SaleReturnLineItemIfc> getSaleReturnSendLineItems()
    {
    	return saleReturnSendLineItems;
    }

    /**
     * Set sale return line items marked for send
     * @param saleReturnSendLineItems
     */
    public void setSaleReturnSendLineItems(
    		List<SaleReturnLineItemIfc> saleReturnSendLineItems)
    {
    	this.saleReturnSendLineItems = saleReturnSendLineItems;
    }
    
	
	/**
	 * get shipping destination tax rule value object
	 * @return
	 */
    public TaxRulesVO getDestinationTaxRule() 
    {
		return destinationTaxRule;
	}

    /**
     * set destination tax rule value object
     * @param destinationTaxRule
     */
	public void setDestinationTaxRule(TaxRulesVO destinationTaxRule) 
	{
		this.destinationTaxRule = destinationTaxRule;
	}
}


