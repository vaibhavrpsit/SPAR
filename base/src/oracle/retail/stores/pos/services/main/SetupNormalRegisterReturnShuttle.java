/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/SetupNormalRegisterReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    08/10/10 - removed the training register object
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.2  2004/07/23 22:17:25  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.1  2004/04/07 17:50:56  tfritz
 *   @scr 3884 - Training Mode rework
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.services.common.StoreStatusCargoIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
    This shuttle carries the required contents between
    the Main service to the SetupRegister service. <P>

    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SetupNormalRegisterReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7814361864459883268L;

    /**
     revision number supplied by source-code-control system
     **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    public StoreStatusCargoIfc  fromCargo;
    
    //--------------------------------------------------------------------------
    /**
     Copies information from the cargo. <P>
     @param bus the bus being loaded
     **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        fromCargo = (StoreStatusCargoIfc) bus.getCargo();
    }
    //----------------------------------------------------------------------
    /**        
       Copies information to the cargo. <P>
       @param bus the bus being unloaded
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        MainCargo mainCargo = (MainCargo) bus.getCargo();

        RegisterADO registerADO = mainCargo.getRegisterADO();
        RegisterIfc register = fromCargo.getRegister();
        if (mainCargo.isTrainingMode())
        {
            register.getWorkstation().setTrainingMode(true);
        }        
        else
        {
            register.getWorkstation().setTrainingMode(false);
        }        
        registerADO.fromLegacy(register);
        registerADO.getStoreADO().fromLegacy(fromCargo.getStoreStatus());

        mainCargo.setTenderLimits(fromCargo.getTenderLimits());
    }
}
