/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/register/VirtualRegisterADOFactory.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:42 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.register;

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryAdapter;
import oracle.retail.stores.pos.ado.journal.RegisterJournal;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreADOIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;

/**
 * @author rwh
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class VirtualRegisterADOFactory
    extends ADOFactoryAdapter
    implements VirtualRegisterADOFactoryIfc
{

    /**
     *  
     */
    public VirtualRegisterADOFactory()
    {
        super();
    }

    /**
     * Creates a new VirtualRegister, RegisterJournal, and Store. Calls the
     * read financials method on the register to read the persisted financial
     * information
     * 
     * @return reference to VirtualRegisterADOIfc
     */
    public VirtualRegisterADOIfc create() throws ADOException
    {
        StoreADOIfc store = (StoreADOIfc) new StoreADO();
        RegisterJournalIfc journal = new RegisterJournal();

        VirtualRegisterADOIfc register = new VirtualRegisterADO(store, journal);
        register.readFinancials();

        return register;
    }

    /**
     * Returns
     */
    public VirtualRegisterADOIfc create(
        RegisterIfc rdoRegister,
        StoreStatusIfc rdoStoreStatus)
        throws ADOException
    {
        StoreADOIfc store = (StoreADOIfc) new StoreADO();
        store.fromLegacy(rdoStoreStatus);
        RegisterJournalIfc journal = new RegisterJournal();

        VirtualRegisterADO register = new VirtualRegisterADO(store, journal);
        register.fromLegacy(rdoRegister);
        return (VirtualRegisterADOIfc) register;
    }

}
