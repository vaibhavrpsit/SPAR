/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/crossborderreturn/CrossBorderReturnStartSite.java /main/7 2013/04/19 16:22:01 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     04/01/13 - CBR cleanup
 *    rgour     02/15/13 - Setting the current store currency code for CBR
 *    rgour     01/22/13 - Adding Parameter Selling Price for the cross border
 *                         return to parameter list
 *    rgour     10/16/12 - CBR fix if item is not available in current store
 *    rgour     09/21/12 - added a parameter selling price for CBR
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    rsnayak   03/26/12 - Cross Border Return
 *
 * ===========================================================================
 *
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans.crossborderreturn;

import java.math.BigDecimal;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.arts.PLUTransaction;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItem;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteria;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;

public class CrossBorderReturnStartSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 2064712084485035576L;

    private ReturnFindTransCargo cargo = null;

    /*
     * Final Set of Sale Return Line Items
     */
    private AbstractTransactionLineItemIfc[] result = null;
    
    /**
     * The logger to which log messages will be sent
     */
    protected static final Logger logger = Logger.getLogger(CrossBorderReturnStartSite.class);

    @Override
    public void arrive(BusIfc bus)
    {

        cargo = (ReturnFindTransCargo) bus.getCargo();
        SaleReturnTransactionIfc trans = cargo.getOriginalTransaction();        
        Vector<AbstractTransactionLineItemIfc> items = new Vector<AbstractTransactionLineItemIfc>();
        SearchCriteriaIfc inquiry = new SearchCriteria();
        items = trans.getLineItemsVector();
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
        SaleReturnLineItemIfc slri = null;
        SaleReturnTransactionIfc tempTxn = DomainGateway.getFactory().getSaleReturnTransactionInstance();

        TransactionTaxIfc transactionTax = utility.getInitialTransactionTax();
        double defaultTaxRate = transactionTax.getDefaultRate();

        tempTxn.setTransactionTax(transactionTax);
        ReturnItemIfc itemReturn = DomainGateway.getFactory().getReturnItemInstance();

        itemReturn.setTaxRate(defaultTaxRate);

        result = new AbstractTransactionLineItemIfc[items.size()];
        PLURequestor pluRequestor = new PLURequestor();
        pluRequestor.removeRequestType(PLURequestor.RequestType.AdvancedPricingRules);

        inquiry.setPLURequestor(pluRequestor);

        for (int i = 0; i < items.size(); i++)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) items.elementAt(i);
            String itemId = srli.getItemID();
            inquiry.setItemID(itemId);
            PLUItemIfc pluItem = getItems(inquiry, bus);
            int pricingGroupID = -1;
            if (pluItem != null)
            {
               
              // PLUItem item = (PLUItem)pluItem;
               String returnPriceForCBR="Lowest_in_X_days";
               try
               {
                   ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
                   returnPriceForCBR = pm.getStringValue(ParameterConstantsIfc.RETURN_ReturnPriceForCBR);
                   
               }
               catch (Exception e)
               {
                   returnPriceForCBR = "Lowest_in_X_days";
                   logger.error("Failed to retrieve or convert to string the 'returnPriceForCBR'.", e);
               }               
               
               if (returnPriceForCBR.equalsIgnoreCase("Current_Selling_Price"))
                {
                    itemReturn.setPrice(pluItem.getPrice());                      
                }
                else
                {
                    itemReturn.setPrice(pluItem.getReturnPrice(pricingGroupID));
                }  
                itemReturn.setItemQuantity(BigDecimalConstants.ONE_AMOUNT);
                BigDecimal quantity = srli.getItemQuantityDecimal();
                slri = tempTxn.addReturnItem(pluItem, itemReturn, quantity);
                srli.setItemPrice((ItemPriceIfc)slri.getItemPrice().clone());
                itemReturn.setItemTax((ItemTaxIfc)slri.getItemTax().clone());  
                result[i] = srli;
            }
            else
            {
               pluItem = srli.getPLUItem();
               pluItem.setAvailableInCurrentStore(false);
               result[i] = srli;              
            }
        }
        trans.getItemContainerProxy().setLineItems(result);        
        bus.mail("Success");
    }

    private PLUItemIfc getItems(SearchCriteriaIfc inquiry, BusIfc bus)
    {
        try
        {

            PLUTransaction pluTransaction = null;
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            pluTransaction = (PLUTransaction) DataTransactionFactory.create(DataTransactionKeys.PLU_TRANSACTION);
            inquiry.setLocaleRequestor(utility.getRequestLocales());
            inquiry.setStoreNumber(cargo.getRegister().getWorkstation().getStoreID());
            inquiry.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());

            PLUItemIfc[] pluItems = pluTransaction.getPLUItems(inquiry);

            if (pluItems != null)
            {
                return pluItems[0];
            }

        }
        catch (DataException de)
        {
        	 logger.error("Item :" + inquiry.getItemID() + " is not availabe in current store", de);
        }
        return null;

    }

}
