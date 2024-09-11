/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/ValidateOrderShippingSite.java /main/12 2012/12/10 19:16:05 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    ohorne    08/09/10 - Siebel shipping address now printed on receipts
 *    sgu       06/22/10 - added the logic to process multiple send package
 *                         instead of just on per order
 *    sgu       06/21/10 - added back the check for shipping charge
 *    sgu       06/21/10 - added site declaration
 *    acadar    06/11/10 - changes for postvoid and signature capture
 *    acadar    06/07/10 - set the shipping charge
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - additional changes for process order flow
 *    acadar    05/17/10 - additional logic added for processing orders
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;



import java.util.Iterator;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderSendPackageIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ReadShippingMethodTransaction;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodSearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import org.apache.commons.lang3.StringUtils;



/**
 * This site checks if the order has any warehouse shipping flag
 *
 * @author acadar
 *
 */
public class ValidateOrderShippingSite extends PosSiteActionAdapter
{

    /**
     *
     */
    private static final long serialVersionUID = -808775588516119896L;


    /**
     * Calls the ExternalOrder Manager API to lock the order
     */
    public void arrive(BusIfc bus)
    {
    	boolean successful = true;

        ProcessOrderCargo cargo = (ProcessOrderCargo)bus.getCargo();
        Iterator<ExternalOrderSendPackageIfc> sendPackageIt = cargo.getExternalOrder().getSendPackageIterator();

        if(sendPackageIt.hasNext())
        {
            // if transaction has a transaction level send exit the flow
            SaleReturnTransactionIfc transaction = cargo.getTransaction();
            if (transaction.isTransactionLevelSendAssigned())
            {
                //if the transaction already has transaction level send, reject the order
                successful = false;
            }
        }

        while (successful && sendPackageIt.hasNext())
        {
        	ExternalOrderSendPackageIfc sendPackage = sendPackageIt.next();
        	if(incompleteItemLevelShipping(sendPackage))
        	{
        		//if external order line items have incomplete shipping information coming from
        		// the external order system
        		successful = false;
        	}
        	else
        	{

        		// try finding the ORPOS shipping method
        		ReadShippingMethodTransaction shippingTransaction = (ReadShippingMethodTransaction) DataTransactionFactory.create(DataTransactionKeys.READ_SHIPPING_METHOD_TRANSACTION);

        		UtilityManagerIfc utility =
        			(UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        		LocaleRequestor localeReq = utility.getRequestLocales();
        		localeReq.setSortByLocale(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        		ShippingMethodSearchCriteriaIfc searchCriteria = DomainGateway.getFactory().getShippingMethodSearchCriteria();
        		searchCriteria.setLocaleRequestor(localeReq);
        		searchCriteria.setShippingCarrier(sendPackage.getShippingCarrier());
        		searchCriteria.setShippingType(sendPackage.getShippingType());
        		try
        		{
        			ShippingMethodIfc[] methods = shippingTransaction.readShippingMethod(searchCriteria);
        			if (methods == null || methods.length == 0)
        			{
        				// no shipping method found in pos, exit the flow
            			successful = false;
        			}
        			else
        			{
        				methods[0].setCalculatedShippingCharge(sendPackage.getShippingCharge());
        				cargo.addExternalOrderSendPackageItem(sendPackage.getId(), methods[0], sendPackage);
        			}
        		}
        		catch (DataException e)
        		{
        			logger.error("Error occured retrieving the shipping methods in ORPOS", e);
        			successful = false;

        		}
        	}
        }

        Letter letter = new Letter(CommonLetterIfc.CONTINUE);
        if (!successful)
        {
        	letter = new Letter(CommonLetterIfc.FAILURE);
        }
        bus.mail(letter, BusIfc.CURRENT);

    }


    /**
     * If an external order line item(s) has been designated for shipping but does not contain all the requiered
     * information  like shipping charge, shippig carrier, shipping type or destination zip code) an error dialog is
     * displayed for the operator and the use case return to the calling use case.
     * @param ExternalOrderIfc external order
     * @param cargo
     * @return boolean
     */
    private boolean incompleteItemLevelShipping(ExternalOrderSendPackageIfc sendPackage)
    {
        boolean hasIncompleteShipping = false;
        if (sendPackage != null)
        {
            if (StringUtils.isEmpty(sendPackage.getShippingCarrier()) ||
            	StringUtils.isEmpty(sendPackage.getShippingType()) ||
            	StringUtils.isEmpty(sendPackage.getDestinationPostalCode()) ||
            	sendPackage.getShippingCharge()== null)
            {
                hasIncompleteShipping = true;
            }
        }

        return hasIncompleteShipping;

    }




}
