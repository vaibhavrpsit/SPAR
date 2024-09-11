/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/SendCustomerReturnShuttle.java /main/15 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *      3    360Commerce 1.2         4/1/2005 2:59:55 AM    Robert Pearse
 *      2    360Commerce 1.1         3/10/2005 9:55:10 PM   Robert Pearse
 *      1    360Commerce 1.0         2/11/2005 11:44:09 PM  Robert Pearse
 *     $
 *     Revision 1.2  2004/09/23 00:07:10  kmcbride
 *     @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects
 *     , minus the JComponents
 *
 *     Revision 1.1  2004/02/12 21:36:28  epd
 *     @scr 0
 *     These files comprise all new/modified files that make up the refactored
 *     send service
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;

import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;

/**
 * @author epd
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SendCustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3419541856729091918L;

    /**
     customer main cargo
     **/
    protected CustomerMainCargo customerMainCargo = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        // retrieve cargo from the customer service
        customerMainCargo = (CustomerMainCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        CustomerIfc customer = customerMainCargo.getCustomer();

        // retrieve cargo from the parent
        ItemCargo iCargo = (ItemCargo)bus.getCargo();
        iCargo.setCustomer(customer);

        // get the transaction from cargo
        SaleReturnTransactionIfc txn = (SaleReturnTransactionIfc)iCargo.getTransaction();

        // get send manager
        SendManagerIfc sendMgr = null;
        try
        {
            sendMgr = (SendManagerIfc)ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
        }
        catch (ManagerException e)
        {
            // default to product version
            sendMgr = new SendManager();
        }

        // link the customer
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        sendMgr.linkCustomer(journal, iCargo);

        // journal customer tour exit
        if(txn != null)
        {
           CustomerUtilities.journalCustomerExit(bus, txn.getCashier().getEmployeeID(),
                                                 txn.getTransactionID());
        }

    }
}
