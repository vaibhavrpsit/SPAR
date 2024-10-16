/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.pos.services.modifyitem;

// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the ModifyItem service cargo to the
    Send service cargo.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class MAXModifyItemSendLaunchShuttle extends FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifyitem.ModifyItemSendLaunchShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       The line item to modify
    **/
    //protected SaleReturnLineItemIfc lineItem = null;

    /**
       The highlighted line items
    **/
    protected SaleReturnLineItemIfc[] items = null;

    /**
       transaction
    **/
    protected RetailTransactionIfc transaction;

    /**
       line item index
    **/
    protected int index;

    //----------------------------------------------------------------------
    /**
       Copies information from the cargo used in the ModifyItem service.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // load financial cargo
        super.load(bus);

        // retrieve cargo from the parent
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        transaction = cargo.getTransaction();
        
        // copy the index
        index = cargo.getIndex();

        items = cargo.getItems();

    }

    //----------------------------------------------------------------------
    /**
       Copies information to the cargo used in the Send service.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);

        // retrieve cargo from the child
        ItemCargo cargo = (ItemCargo)bus.getCargo();

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
        // Transaction information
        cargo.setTransaction(transaction);
        //Change fro rev 1.0 : Start
        ((MAXItemCargo)cargo).setSend(true);
    }
}
