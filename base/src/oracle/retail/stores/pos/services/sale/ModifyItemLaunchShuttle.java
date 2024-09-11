/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ModifyItemLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse   
 * $
 * Revision 1.7  2004/07/14 21:32:08  jriggins
 * @scr 6268 Filter out price adjustment related items from the modify item and pricing services so that they will not be displayed
 *
 * Revision 1.6  2004/03/16 18:30:42  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.5  2004/03/05 20:08:06  bjosserand
 * @scr 3954 Tax Override
 * Revision 1.4 2004/03/05 00:41:53 bjosserand @scr 3954 Tax Override
 * 
 * Revision 1.3 2004/02/12 16:48:17 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:22:50 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:11 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.2 Nov 13 2003 11:10:04 baa sale refactoring Resolution for 3430: Sale Service Refactoring
 * 
 * Rev 1.1 Nov 07 2003 12:37:20 baa use SaleCargoIfc Resolution for 3430: Sale Service Refactoring
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;

//--------------------------------------------------------------------------
/**
 * This shuttle copies information from the POS service cargo to the Modify Item service cargo.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class ModifyItemLaunchShuttle extends FinancialCargoShuttle
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.ModifyItemLaunchShuttle.class);
    ;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected SaleCargoIfc saleCargo;

    //----------------------------------------------------------------------
    /**
     * Copies information from the cargo used in the POS service.
     * <P>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);

        // retrieve cargo from the parent
        saleCargo = (SaleCargoIfc) bus.getCargo();

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
        ItemCargo cargo = (ItemCargo) bus.getCargo();
        
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
        
        cargo.setTransaction(transaction);
    }
}
