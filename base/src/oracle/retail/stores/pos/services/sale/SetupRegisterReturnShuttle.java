/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/SetupRegisterReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/22/14 - deprecating this class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.2  2004/07/23 22:17:26  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.1  2004/04/13 15:23:47  tfritz
 *   @scr 3884 - More training mode changes
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

// foundation imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.services.common.StoreStatusCargoIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents between
    the Main service to the SetupRegister service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated As of 14.1, this shuttle is no longer used.
**/
//------------------------------------------------------------------------------
@Deprecated
public class SetupRegisterReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7306781261028281186L;


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

    //--------------------------------------------------------------------------
    /**
       Copies information to the cargo. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {        
        AbstractFinancialCargoIfc cargo = 
                (AbstractFinancialCargoIfc) bus.getCargo();
        
        RegisterIfc register = fromCargo.getRegister();
        
        if (register != null)
        {    
//            String tillID = register.getCurrentTillID();
            
            // Make sure the training mode register has a till
//            if (tillID == null || tillID.length() == 0)
//            {
//                ContextFactory contextFactory = ContextFactory.getInstance();
//                RegisterIfc normalRegister = (RegisterIfc) contextFactory.getContext().getRegisterADO().toLegacy();
//                register.setTills(normalRegister.getTills());
//                register.setCurrentTillID(normalRegister.getCurrentTillID());
//            }
                
            cargo.setRegister(fromCargo.getRegister());
        }
    }
}
