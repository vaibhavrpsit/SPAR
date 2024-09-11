/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ModifyItemSendLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:51:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:17:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:37:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   19 Nov 2001 14:37:28   sfl
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
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
public class ModifyItemSendLaunchShuttle extends FinancialCargoShuttle
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
    }
}
