/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
*	Rev 1.2  Sep 26, 2022   	Kamlesh Pant					CapLimit Enforcement for Liquor
 *  Rev 1.1  15/APR/2016        Mohd Arif                                  Change for remove gift card discount when delete gift card and scan normal items.
 *  Rev 1.0  29/May/2013        Izhar                                      Discount rule
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.sale;

// foundation imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import max.retail.stores.domain.MaxLiquorDetails;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXHotKeysTransaction;
import max.retail.stores.domain.lineitem.MAXItemContainerProxyIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

//--------------------------------------------------------------------------
/**
    This road is traversed when the user presses the
    Delete key from the SELL_ITEM screen.
    <p>
    @version $Revision: 8$
**/
//--------------------------------------------------------------------------
public class MAXDeleteItemRoad extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: 8$";

    /**
       static index value indicating no selected row
    **/
    protected static final int NO_SELECTION = -1;

    //----------------------------------------------------------------------
    /**
       Deletes the selected item from the transaction.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        /*
         * Get the indices of all selected items
         */
        LineItemsModel beanModel                 = (LineItemsModel)ui.getModel();
        int[] allSelected                        = beanModel.getRowsToDelete();
        int selected                             = NO_SELECTION;
        MAXSaleCargoIfc cargo                       = (MAXSaleCargoIfc)bus.getCargo();
        MAXSaleReturnTransactionIfc transaction     = (MAXSaleReturnTransactionIfc)cargo.getTransaction();
        SaleReturnTransactionIfc[] originalTrans = cargo.getOriginalReturnTransactions();
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
        
        Vector lineItemsVector 					 = transaction.getLineItemsVector(); 
        HashMap lineNumbers						 = new HashMap();
        for(int i=0;i<lineItemsVector.size();i++)
        {
        	lineNumbers.put(((SaleReturnLineItemIfc) lineItemsVector.elementAt(i)).getItemID(),""+((SaleReturnLineItemIfc) lineItemsVector.elementAt(i)).getLineNumber());
        	
        }
        // if the item being deleted is a primary item
        // with deletable items
        ArrayList itemsToDelete = new ArrayList();
        for (int i = 0; i < allSelected.length; i++)
        {
            
            SaleReturnLineItemIfc item = (SaleReturnLineItemIfc) transaction.retrieveItemByIndex(allSelected[i]);
            SaleReturnLineItemIfc[] relatedLineItems = item.getRelatedItemLineItems();
            if (relatedLineItems != null)
            {
                for (int j = 0;j < relatedLineItems.length;j++)
                {
                	if(lineNumbers.get(relatedLineItems[j].getItemID())!=null)
                	{
	                	String lineNumber = lineNumbers.get(relatedLineItems[j].getItemID()).toString();
	                	if(!hasMoreRelatedItem(relatedLineItems[j],lineItemsVector,allSelected))
	                	{
	                		if (!itemsToDelete.contains(new Integer(lineNumber)))
	                			itemsToDelete.add(new Integer(lineNumber));
	                	}
                	}
                }
            }
            
            // make sure that the item was net set to be deleted already
            // because it is a related item for a primary item that is
            // to be deleted.
            Integer itemNumber = new Integer(allSelected[i]);
            if (!itemsToDelete.contains(itemNumber))
            {
                itemsToDelete.add(itemNumber);
            }

        }
        //Deletion is from back of the array, hence sort the line number
        Collections.sort(itemsToDelete);

        // for each item in the array.  Process from the back of the array,
        // because if you process from the front, items will not be in the
        // expected locations in the transaction when processing the end of the array.
        for (int i = itemsToDelete.size() - 1; i > -1; i--)
        {
            selected = ((Integer)itemsToDelete.get(i)).intValue();
            /*
             * Update the transaction in the cargo
             */
            SaleReturnLineItemIfc item =
                (SaleReturnLineItemIfc) transaction.retrieveItemByIndex(selected);
            
            //Rev 1.2 Changes for liquor : Starts
            MaxLiquorDetails liquorDetail = null;
    		MAXHotKeysTransaction hotKeysTransaction = (MAXHotKeysTransaction) DataTransactionFactory
    				.create(MAXDataTransactionKeys.MAX_HOT_KEYS_LOOKUP_TRANSACTION);
    		try {
    			liquorDetail = ((MAXHotKeysTransaction) hotKeysTransaction)
    					.getLiquorUMAndCategory(item.getItemID());
    		} catch (DataException e) {
    			logger.warn(e.getMessage());
    		}
    		
    		if(liquorDetail.getDepartment()!=null && liquorDetail.getDepartment().equals("41"))
    		{
    			if(liquorDetail.getLiquorCategory().equals("BEER"))
    			{
    				float beertot = transaction.getBeertot()-(Float.parseFloat(liquorDetail.getLiqUMinLtr())*Float.parseFloat(item.getItemQuantity().toString()));
    				transaction.setliquortot(transaction.getliquortot()-(Float.parseFloat(liquorDetail.getLiqUMinLtr())*Float.parseFloat(item.getItemQuantity().toString())));
    				transaction.setBeertot(beertot);
    			}
    			else if(liquorDetail.getLiquorCategory().equals("INDN LIQR"))
    			{
    				float InLiqtot = transaction.getInLiqtot()-(Float.parseFloat(liquorDetail.getLiqUMinLtr())*Float.parseFloat(item.getItemQuantity().toString()));
    				transaction.setliquortot(transaction.getliquortot()-(Float.parseFloat(liquorDetail.getLiqUMinLtr())*Float.parseFloat(item.getItemQuantity().toString())));
    				transaction.setInLiqtot(InLiqtot);
    			}
    			else if(liquorDetail.getLiquorCategory().equals("FORN LIQR"))
    			{
    				float frnLiqtot = transaction.getfrnLiqtot()-(Float.parseFloat(liquorDetail.getLiqUMinLtr())*Float.parseFloat(item.getItemQuantity().toString()));
    				transaction.setliquortot(transaction.getliquortot()-(Float.parseFloat(liquorDetail.getLiqUMinLtr())*Float.parseFloat(item.getItemQuantity().toString())));
    				transaction.setfrnLiqtot(frnLiqtot);
    			}
    		}
            
            
            
            //Rev 1.2 Changes for liquor : Ends
            // If the line item is associated with a price adjustment, remove that data from the 
            // list of original transactions for price adjustments so that it may be reentered later.
            if (item != null && (item.isPriceAdjustmentLineItem() || item.isPartOfPriceAdjustment()) )
            {
                PriceAdjustmentLineItemIfc priceAdjLineItem = null;
                TransactionIDIfc origTransID = null;
                
                if (item.isPartOfPriceAdjustment())
                {
                    priceAdjLineItem = 
                        transaction.retrievePriceAdjustmentByReference(item.getPriceAdjustmentReference());
                }
                else
                {
                    priceAdjLineItem = (PriceAdjustmentLineItemIfc)item;
                }
                
                ReturnItemIfc returnItem = priceAdjLineItem.getReturnItem(); 
                if (returnItem != null)
                {
                    origTransID = returnItem.getOriginalTransactionID();
                }
                
                if (origTransID != null)
                {
                    cargo.removeOriginalPriceAdjustmentTransaction(origTransID.getTransactionIDString());
                }
            }
           

			// This transaction level discount is set to null by Gaurav because
			// of the invoice rules to be deleted if item is deleted.
			TransactionDiscountStrategyIfc[] discounts = transaction.getItemContainerProxy().getTransactionDiscounts();
			/*Rev 1.1 Start*/
			if(discounts!=null && itemsToDelete.size()==lineItemsVector.size()){   // Change for discount issue in GC - Karni
				transaction.getItemContainerProxy().setTransactionDiscounts(null);
				transaction.setEmployeeDiscountID(null);
			}
			/*Rev 1.1 End*/
			if (transaction.getEmployeeDiscountID() == null && transaction.getEmployeeDiscountID() == "")
				transaction.getItemContainerProxy().setTransactionDiscounts(null);
			try {
				// if( cargo.getTransaction() instanceof
				// SaleReturnTransactionIfc)
				((MAXSaleReturnTransaction) cargo.getTransaction()).setTaxTypeLegal(false);
				((MAXItemContainerProxyIfc)((SaleReturnTransaction) cargo.getTransaction()).getItemContainerProxy()).setTaxApplied(false);
			} catch (ClassCastException e) {

			}

			// ...ends here

			// gaurav
            // Remove the line item from the transaction
            transaction.removeLineItem(selected);  
            
            
            // Refresh items stored in the data structure hashtable keyed by the tax group keyed
            //transaction.removeItemByTaxGroup(item);
            transaction.addItemByTaxGroup();

            // Save each to be cleared line item into the deletedLineItems vector
            transaction.addDeletedLineItems(item);
         

            // Attempt to restore the original transaction.
            if ((item.getReturnItem() != null) &&
                // if return item original line number is -1, there was no original transaction
                (item.getReturnItem().getOriginalLineNumber() != -1)  &&  (originalTrans != null))
            {
                SaleReturnTransactionIfc restored = restoreOriginalTransaction(item, originalTrans);
                if (restored != null)
                {
                    // This methods replaces existing transactions.
             //       cargo.addOriginalReturnTransaction(restored);
                }
            }
         //   cargo.setInvoiceRuleAlreadyApplied(false);
            writeJournalEntry(transaction, item, bus.getServiceName());
            try
            {
                CIDAction action = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.REMOVE_ITEM);
                //action.setLineItem(item);
                action.setLineNumber(new Integer(selected));
                pda.cidScreenPerformAction(action);
            }
            catch (DeviceException e)
            {
                logger.warn(
                            "Unable to use Line Display: " + e.getMessage() + "");
            }
        }

        //clear the line display device
        try
        {
            pda = new POSDeviceActions((SessionBusIfc) bus);
            pda.clearText();
        }
        catch (DeviceException e)
        {
            logger.warn(
                        "Unable to use Line Display: " + e.getMessage() + "");
        }
    }

    /**
     * Reverses the returned count on the original transaction line item.
     * <p>
     * @param item Item to return
     * @param originalTrans array of transactions to search for the return item in
     * 
     * @return SaleReturnTransaction for the given item
     */
    protected SaleReturnTransactionIfc restoreOriginalTransaction(SaleReturnLineItemIfc item,
                                                                  SaleReturnTransactionIfc[] originalTrans)
    {
        ReturnItemIfc            ri = item.getReturnItem();
        TransactionIDIfc originalID = ri.getOriginalTransactionID();
        int               lineIndex = ri.getOriginalLineNumber();

        // find the original transaction.
        SaleReturnTransactionIfc trans = null;
        for(int i = 0; i < originalTrans.length; i++)
        {
            if (originalID.equals(originalTrans[i].getTransactionIdentifier()))
            {
                trans = originalTrans[i];
                i     = originalTrans.length;
            }
        }

        // Find the original line item.
        if (trans != null && lineIndex < trans.getLineItemsSize())
        {
            if(lineIndex >= 0)
            {
                SaleReturnLineItemIfc originalItem =
                    (SaleReturnLineItemIfc) trans.retrieveItemByIndex(lineIndex);
                originalItem.setQuantityReturned(originalItem.getQuantityReturnedDecimal().add(item.getItemQuantityDecimal()));
                trans.replaceLineItem(originalItem, lineIndex);
            }
        }

        return trans;
    }

    //----------------------------------------------------------------------
    /**
       Writes an entry in the journal.
       <p>
       @param transaction  The void transaction to journal
       @param item line item
       @param serviceName service name for log
    **/
    //----------------------------------------------------------------------
    protected void writeJournalEntry(SaleReturnTransactionIfc transaction,
                                     SaleReturnLineItemIfc item,
                                     String serviceName)
    {                                   // begin toString()
        /*
         * Write the journal entry
         */
        JournalManagerIfc journal =
            (JournalManagerIfc)Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

        if (journal != null)
        {
            StringBuffer sb = new StringBuffer();
            sb.append(formatter.toJournalRemoveString(item));

            ItemDiscountStrategyIfc[] itemDiscounts =
                item.getItemPrice().getItemDiscounts();

            if((itemDiscounts != null) && (itemDiscounts.length > 0))
            {
                for(int i = 0; i < itemDiscounts.length; i++)
                {
                    if (!(itemDiscounts[i] instanceof ItemTransactionDiscountAudit))
                    {
                        sb.append(Util.EOL);
                        sb.append(formatter.toJournalManualDiscount(item, itemDiscounts[i], true));
                    }
                }
            }

            String saTrans = transaction.getSalesAssociate().getEmployeeID();
            if(item.getSalesAssociate() != null)
            {
                String saItem = item.getSalesAssociate().getEmployeeID();
                if(!(saTrans.equals(saItem)))
                {
                    sb.append(Util.EOL)
                        .append(Util.EOL)
                        .append("ITEM: ")
                        .append(item.getItemID())
                        .append(Util.EOL)
                        .append("  Sales Assoc.: ")
                        .append(saItem)
                        .append(" Deleted");
                }
            }

            journal.journal(transaction.getSalesAssociateID(),
                            transaction.getTransactionID(),
                            sb.toString());
        }
        else
        {
            logger.error( "No JournalManager found");
        }
    }
    private boolean hasMoreRelatedItem(SaleReturnLineItemIfc relatedLineItem,Vector lineItemsVector,int[] allSelected)
    {
    	boolean hasMoreRelatedItem = false;
    	int countOfRelatedItem = 0;
    	int countUnselectedRelatedItem = 0;
    	String relatedLineItemItemId= relatedLineItem.getItemID();
        for(int i=0;i<lineItemsVector.size();i++)
        {       	
        	SaleReturnLineItemIfc[] relatedLineItems = ((SaleReturnLineItemIfc) lineItemsVector.elementAt(i)).getRelatedItemLineItems();
            if (relatedLineItems != null)
            {
                for (int j = 0;j < relatedLineItems.length;j++)
                {
                	if(relatedLineItemItemId.trim().equals(relatedLineItems[j].getItemID().trim()))
                		countOfRelatedItem++;
                }
            }
            
        }
        for(int i=0;i<lineItemsVector.size();i++)
        {       	
        	boolean isNotSelected = true;
        	for(int j=0;j<allSelected.length;j++){
        		 Integer itemNumber = new Integer(allSelected[j]);
        		 if(itemNumber.intValue()==i)
        			 isNotSelected=false;
        	}
        	if(isNotSelected)
        	{
	        	SaleReturnLineItemIfc[] relatedLineItems = ((SaleReturnLineItemIfc) lineItemsVector.elementAt(i)).getRelatedItemLineItems();
	            if (relatedLineItems != null)
	            {
	                for (int j = 0;j < relatedLineItems.length;j++)
	                {
	                	if(relatedLineItemItemId.trim().equals(relatedLineItems[j].getItemID().trim()))
	                		countUnselectedRelatedItem++;
	                }
	            }
        	}
        }

        if(countOfRelatedItem>1 && countUnselectedRelatedItem>0)
        	hasMoreRelatedItem=true;

    	return hasMoreRelatedItem;
    }
    // end main()
}
