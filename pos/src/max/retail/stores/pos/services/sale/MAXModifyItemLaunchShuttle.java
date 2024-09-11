/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.1 		Tanmaya		05/04/2013		Change for Scan and void
  Rev 1.0		Prateek		23/03/2013		Initial Draft: Changes for Quantity Button
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

import java.util.ArrayList;

import max.retail.stores.pos.services.modifyitem.MAXItemCargo;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.sale.ModifyItemLaunchShuttle;

public class MAXModifyItemLaunchShuttle extends ModifyItemLaunchShuttle {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5315470497854429130L;
	/**
	 * 
	 */
	protected MAXSaleCargoIfc saleCargo;
    protected String letter = null;

    public void load(BusIfc bus)
	{
    	// load financial cargo
	    super.load(bus);

	    // retrieve cargo from the parent
	    saleCargo = (MAXSaleCargoIfc) bus.getCargo();
	    LetterIfc ltr = bus.getCurrentLetter();
	    if (ltr != null)
	    {
	    	String letterName = ltr.getName();
	        if (letterName != null)
	        {
	        	letter = letterName;
	        }
	        
	    }
	}
    
  //----------------------------------------------------------------------
    /**
     * Copies information to the cargo used in the Modify Item service.
     * <P>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // unload financial cargo
        super.unload(bus);

        SaleReturnTransactionIfc transaction = saleCargo.getTransaction();
        int index = saleCargo.getIndex();
    
        // retrieve cargo from the child
        MAXItemCargo cargo = (MAXItemCargo) bus.getCargo();
        cargo.setParentLetter(letter);
        
        if (transaction != null)
        {
            // Get the selected lines items from the sale cargo
            ArrayList itemList = new ArrayList();
            SaleReturnLineItemIfc[] cargoItems = saleCargo.getLineItems();
            if (cargoItems != null)
            {
                for (int i = 0; i < cargoItems.length; i++)
                {
                    if ( !(cargoItems[i].isPriceAdjustmentLineItem() || cargoItems[i].isPartOfPriceAdjustment()) )
                    {
                        itemList.add(cargoItems[i]);
                    }
                }
            }
                        
            SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[])itemList.toArray(new SaleReturnLineItemIfc[itemList.size()]);     
        
            // set the child reference to the cloned object
            if (items != null)
            {
                int[] indices = new int[items.length];
    
                // The reason for still saving single item thourgh cargo.setItem is,
                // many existing services are dependent on using item cargo's getItem().
                if (items.length == 1)
                {
                    cargo.setItem(items[0]);
                    cargo.setIndex(items[0].getLineNumber());
                }
    
                if (items.length > 0)
                {
                    cargo.setItems(items);
                    for (int j = 0; j < items.length; j++)
                    {
                        indices[j] = items[j].getLineNumber();
                    }
                    cargo.setIndices(indices);
                }
            }
            else if (saleCargo.getLineItem() != null)
            {
                cargo.setItem(saleCargo.getLineItem());
                cargo.setIndex(saleCargo.getLineItem().getLineNumber());
            }
            else
            {
                int[] indices = new int[1];
    
                indices[0] = index;
                cargo.setItems(null);
                cargo.setIndices(indices);
    
                // The reason for still saving single item thourgh cargo.setItem is,
                // many existing services are dependent on using item cargo's getItem().
                cargo.setItem(null);
                cargo.setIndex(index);
    
            }
        }
        cargo.setScanNVoidFlow(((MAXSaleCargoIfc)saleCargo).isScanNVoidFlow());
        cargo.setTransaction(transaction);
    }
}
