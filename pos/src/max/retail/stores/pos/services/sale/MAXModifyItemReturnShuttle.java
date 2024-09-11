/***********************************************************************************************************************
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 
 * Copyright (c) 1998-2002 360Commerce, Inc. All Rights Reserved.
 * 
 *	Rev 1.0	13/09/16	Ashish Yadav	Changes done for code merging
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */

package max.retail.stores.pos.services.sale;
// java imports
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.common.MAXRoundingConstantsIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.journal.JournalFormatterManager;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

//--------------------------------------------------------------------------
/**
 * This shuttle copies information from the Modify Item service cargo to the POS service cargo.
 * <p>
 * 
 * @version $Revision: /rgbustores_12.0.9in_branch/1 $
 */
//--------------------------------------------------------------------------
public class MAXModifyItemReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2026579451830842000L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.MAXModifyItemReturnShuttle.class);
    ;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";

    /**
     * The modified line item.
     */
    protected SaleReturnLineItemIfc lineItem = null;

    /**
     * The modified line items.
     */
    protected SaleReturnLineItemIfc[] lineItemList = null;

    /**
     * The flag that indicates whether an item is being added
     */
    protected boolean addPLUItem = false;

    /**
     * The item to add.
     */
    protected PLUItemIfc pluItem = null;

    /**
     * Item Quantity
     */
    protected BigDecimal itemQuantity = null;

    /**
     * Flag indicating whether item added is service and added thru inquiry/services
     */
    protected boolean serviceItemFlag = false;

    /**
     * transaction type - sale or return
     */
    protected SaleReturnTransactionIfc transaction;

    //----------------------------------------------------------------------
    /**
     * Copies information from the cargo used in the Modify Item service.
     * <P>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo from the child
        ItemCargo cargo = (ItemCargo) bus.getCargo();

        // set the add item flag
        addPLUItem = cargo.getAddPLUItem();

        // set whether service item added thru inquiry/services
        serviceItemFlag = cargo.getServiceItemFlag();

        //return the transaction only if adding an alteration item
        transaction = null;

        if (addPLUItem)
        {
            // set the item to add
            pluItem = cargo.getPLUItem();
            itemQuantity = cargo.getItemQuantity();

            String productGroup = pluItem.getProductGroupID();
            if (productGroup != null
                && (productGroup.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_ALTERATION)
                    || productGroup.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD)))
            {
                // return the transaction in case this is the first line item
                // in order to carry customer linked info
                transaction = (SaleReturnTransactionIfc) cargo.getTransaction();
            }
        }
        else
        {
            // set the child reference to the temp
            lineItem = cargo.getItem();
            lineItemList = cargo.getItems();
        }
        if (transaction == null &&
            ((SaleReturnTransactionIfc) cargo.getTransaction()).getAgeRestrictedDOB() != null)
        {
            transaction = (SaleReturnTransactionIfc) cargo.getTransaction();
        }
    }

    //----------------------------------------------------------------------
    /**
     * Copies information to the cargo used in the POS service.
     * <P>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // retrieve cargo from the parent
        MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
        cargo.setRefreshNeeded(true);

       //India Localization changes start 
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);    
        try
        {
        	cargo.setRounding(pm.getStringValue(MAXRoundingConstantsIfc.ROUNDING));
        	String[] roundingDenominationsArray = pm.getStringValues(MAXRoundingConstantsIfc.ROUNDING_DENOMINATIONS);
        	if(roundingDenominationsArray == null || roundingDenominationsArray.length == 0)
        		{
        			throw new ParameterException("List of parameters undefined");
        		}
        		List roundingDenominations = new ArrayList();
        		roundingDenominations.add(0,new BigDecimal(0.0));
        		for(int i=0;i<roundingDenominationsArray.length;i++)
        		{
        			roundingDenominations.add(new BigDecimal(roundingDenominationsArray[i]));
        		}
        		roundingDenominations.add(roundingDenominationsArray.length,new BigDecimal(1.00));

				//List must be sorted before setting on the cargo.
        		Collections.sort(roundingDenominations,new Comparator()	{
        			public int compare(Object o1, Object o2) {
        				BigDecimal denomination1 = (BigDecimal)o1;
        				BigDecimal denomination2 = (BigDecimal)o2;
        				return denomination1.compareTo(denomination2);
        			}
        		});

        		cargo.setRoundingDenominations(roundingDenominations);
        }
        catch(ParameterException pe)
        {
        	//if there is an error with the parameters, the price rounding logic should be disabled
        	 //cargo.setRoundingEnabledLogic(false);
        	 logger.error( "" + Util.throwableToString(pe) + "");
        }
        //India Localization changes end
        if (addPLUItem)
        {
            if (cargo.getTransaction() == null)
            {
                if (transaction == null)
                {
                    cargo.initializeTransaction(bus);
                    //India Localization changes start
                	if(cargo.getTransaction() != null)
                	{
                		// Changes start for Bug 20307
                    	/*((MAXSaleCargoIfc) cargo.getTransaction()).setRounding(cargo.getRounding());
                    	((MAXSaleCargoIfc) cargo.getTransaction()).setRoundingDenominations(cargo.getRoundingDenominations());*/
                		
                        ((MAXSaleReturnTransaction) cargo.getTransaction()).setRounding(cargo.getRounding());
                    	((MAXSaleReturnTransaction) cargo.getTransaction()).setRoundingDenominations(cargo.getRoundingDenominations());
                	}
                	if(cargo.getTransaction().getTransactionTotals() != null)
                	{
                		/*((MAXSaleCargoIfc) cargo.getTransaction().getTransactionTotals()).setRounding(cargo.getRounding());
                		((MAXSaleCargoIfc) cargo.getTransaction().getTransactionTotals()).setRoundingDenominations(cargo.getRoundingDenominations());*/
                		((MAXSaleReturnTransaction) cargo.getTransaction()).setRounding(cargo.getRounding());
                    	((MAXSaleReturnTransaction) cargo.getTransaction()).setRoundingDenominations(cargo.getRoundingDenominations());
                    	// Changes End for Bug 20307
                	} 
                	//India Localization changes end
                }
                else
                {
                    cargo.setTransaction(transaction);
                }
            }
			// Changes starts for rev 1.0
			cargo.setPLUItem(pluItem);
			// Changes ends for rev 1.0
            SaleReturnLineItemIfc item = cargo.getTransaction().addPLUItem(pluItem, itemQuantity);

            String productGroup = pluItem.getProductGroupID();
            if (productGroup != null && productGroup.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_ALTERATION))
            {
                //Set the Alteration Item Flag
                item.setAlterationItemFlag(true);
            }

            //set the line item for the serialized item service
            cargo.setLineItem(item);

            if (serviceItemFlag) // journal the service item added to the transaction
            {
                JournalManagerIfc journal =
                    (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
                JournalFormatterManagerIfc formatter =
                    (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManager.TYPE);
                if (journal != null)
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append(formatter.toJournalString(item, null, null));

                    if (cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
                        // add status
                    {
                        sb.append(Util.EOL).append("  Status: New");
                    }

                    journal.journal(
                        cargo.getOperator().getLoginID(),
                        cargo.getTransaction().getTransactionID(),
                        sb.toString());
                }
                else
                {
                    logger.error("No JournalManager found");
                }
            }

            //Show item on Line Display device
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            try
            {
                pda.lineDisplayItem(item);
            }
            catch (DeviceException e)
            {
                logger.warn("Unable to use Line Display: " + e.getMessage() + "");
            }
            
        } // end if (addPLUItem)
        else if (cargo.getIndex() >= 0 || lineItemList != null)
        {
        	//Change for Rev 1.2 : Starts
			if( cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc){	
				transaction= cargo.getTransaction();
			if(((MAXSaleReturnTransactionIfc) cargo.getTransaction()).isIgstApplicable() && ((MAXSaleReturnTransactionIfc) cargo.getTransaction()).getLineItemsSize()==1){
				lineItem=((MAXSaleReturnLineItemIfc)((MAXSaleReturnTransactionIfc) transaction).getItemContainerProxy().getLineItems()[0]);
				
				
				ArrayList saleReturnTxn = new ArrayList();
				saleReturnTxn.add(lineItem);
				MAXSaleReturnLineItemIfc[] saleReturnLineItemList = new MAXSaleReturnLineItemIfc[saleReturnTxn.size()];
				for (int k = 0; k <= saleReturnLineItemList.length - 1; k++) {
					saleReturnLineItemList[k] = (MAXSaleReturnLineItemIfc) saleReturnTxn
							.get(k);
				} 
				lineItemList = saleReturnLineItemList;
			}
			}
			//Change for Rev 1.2 : Ends
        	if (lineItem != null)
            {
                    cargo.getTransaction().replaceLineItem(lineItem, lineItem.getLineNumber());
                    cargo.setItemModifiedIndex(lineItem.getLineNumber());
            }
            if (lineItemList != null)
            {
                for (int i = 0; i < lineItemList.length; i++)
                {
                    cargo.getTransaction().replaceLineItem(lineItemList[i], lineItemList[i].getLineNumber());
                    cargo.setItemModifiedIndex(lineItemList[i].getLineNumber());
                }
            }
        }


        
        if (transaction != null &&
            cargo.getTransaction() == null)
        {
            cargo.setTransaction(transaction);
        }
    }
}
