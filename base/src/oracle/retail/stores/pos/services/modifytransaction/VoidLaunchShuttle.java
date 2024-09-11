/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/VoidLaunchShuttle.java /main/13 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:33 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:30  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.4   Nov 06 2003 15:22:12   epd
 * fixed package name
 * 
 *    Rev 1.3   Nov 06 2003 15:18:36   epd
 * fixed packaging
 * 
 *    Rev 1.2   Oct 23 2003 15:46:52   epd
 * points to renamed ADO services
 * 
 *    Rev 1.1   Oct 17 2003 12:58:48   epd
 * Updated for new ADO postvoid service
 * 
 *    Rev 1.0   Apr 29 2002 15:14:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   17 Jan 2002 17:35:26   baa
 * update roles/security model
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.0   Sep 21 2001 11:30:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.postvoid.VoidCargo;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * This shuttle copies the contents of the calling service's cargo.
 * 
 * @version $Revision: /main/13 $
 */
public class VoidLaunchShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 7709011143880230485L;
    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(VoidLaunchShuttle.class);
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * Copies information from the cargo used in the service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
    }

    /**
     * Copies information to the cargo used in the service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        VoidCargo cargo = (VoidCargo)bus.getCargo();
        cargo.setAccessFunctionID(RoleFunctionIfc.VOID);

        // ///////////////////////////
        // Construct ADO's
        // ///////////////////////////

        // create a register
        StoreFactory storeFactory = StoreFactory.getInstance();
        RegisterADO registerADO = storeFactory.getRegisterADOInstance();
        registerADO.fromLegacy(cargo.getRegister());

        // create the store
        StoreADO storeADO = storeFactory.getStoreADOInstance();
        storeADO.fromLegacy(cargo.getStoreStatus());

        // put store in register
        registerADO.setStoreADO(storeADO);
    }

}
