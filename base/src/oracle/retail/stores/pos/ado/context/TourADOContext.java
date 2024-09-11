/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/context/TourADOContext.java /main/12 2012/11/26 09:21:03 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.context;

import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.manager.ManagerIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;

/**
 * A tour context
 * @deprecated as of 13.4.1. Retrieve the {@link BusIfc} with {@link TourContext} instead.
 */
public class TourADOContext implements ADOContextIfc
{
    /** The current bus instance */
    protected BusIfc bus;

    protected String applicationID = "POS";

    /**
     * Construct the context with the bus.
     * 
     * @param bus
     *            The bus from the current tour.
     */
    public TourADOContext(BusIfc bus)
    {
        this.bus = bus;
    }

    /**
     * Returns the current bus
     * 
     * @return
     */
    public BusIfc getBus()
    {
        return bus;
    }

    /**
     * Return the desired manager
     * 
     * @see oracle.retail.stores.pos.ado.context.ADOContextIfc#getManager(java.lang.String)
     */
    public ManagerIfc getManager(String managerType)
    {
        return bus.getManager(managerType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see oracle.retail.stores.ado.context.ADOContextIfc#getApplicationID()
     */
    public String getApplicationID()
    {
        return applicationID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see oracle.retail.stores.ado.context.ADOContextIfc#setApplicationID(java.lang.String)
     */
    public void setApplicationID(String ID)
    {
        applicationID = ID;
    }
    
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.context.ADOContextIfc#getRegisterADO()
     */
    public RegisterADO getRegisterADO()
    {
        RegisterADO register = StoreFactory.getInstance().getRegisterADOInstance();
        StoreADO store = StoreFactory.getInstance().getStoreADOInstance();
        register.setStoreADO(store);
        
        // configure register and store
        AbstractFinancialCargo cargo = (AbstractFinancialCargo)bus.getCargo();
        register.fromLegacy(cargo.getRegister());
        store.fromLegacy(cargo.getStoreStatus());
        
        return register;
    }
}
