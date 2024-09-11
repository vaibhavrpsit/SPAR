/* ===========================================================================
* Copyright (c) 2010, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ReverseTenderAuthLaunchShuttle.java /main/4 2014/01/07 18:00:13 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/07/14 - fix dereferencing of null objects.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   07/28/11 - added support for manager override for card decline
 *    blarsen   07/08/11 - TenderAuthCargo moved from tender.tenderauth (which
 *                         was deleted) into new service tender.reversal.
 *    kelesika  12/06/10 - Multiple reversal of gift cards
 *    kelesika  12/03/10 - Multiple gift card reversals
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.services.sale.validate.TenderLaunchShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;

public class ReverseTenderAuthLaunchShuttle implements ShuttleIfc
{
    /**
     * serial version UID
     */
    private static final long serialVersionUID = 4798389829030710726L;

    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(ReverseTenderAuthLaunchShuttle.class);

    /**
     * Sale Cargo
     */
    protected SaleCargoIfc saleCargo;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        saleCargo = (SaleCargoIfc)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // create a register
        StoreFactory storeFactory = StoreFactory.getInstance();
        RegisterADO registerADO = storeFactory.getRegisterADOInstance();
        registerADO.fromLegacy(saleCargo.getRegister());
        cargo.setOperator(saleCargo.getOperator());

        // create the store
        StoreADO storeADO = storeFactory.getStoreADOInstance();
        storeADO.fromLegacy(saleCargo.getStoreStatus());

        // put store in register
        registerADO.setStoreADO(storeADO);

        // Create/convert/set in cargo ADO transaction
        TransactionIfc t = saleCargo.getTransaction();
        if(t != null)
        {
            TransactionPrototypeEnum txnType = TransactionPrototypeEnum
                        .makeEnumFromTransactionType(t.getTransactionType());
            RetailTransactionADOIfc txnADO = null;
            try
            {
                txnADO = txnType.getTransactionADOInstance();
            }
            catch (ADOException e1)
            {
                // Exception getting ADO instance, log exception and return
                logger.error("Exception while getting ADO Instance " + e1.getMessage());
                return;
            }
            ((ADO)txnADO).fromLegacy(t);
            cargo.setCurrentTransactionADO(txnADO);
        }
        //set the register and store status
        cargo.setRegister(saleCargo.getRegister());
        cargo.setStoreStatus(saleCargo.getStoreStatus());
    }

}
