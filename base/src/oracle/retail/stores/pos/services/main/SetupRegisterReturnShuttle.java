/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/SetupRegisterReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/07/23 22:17:25  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.4  2004/03/14 21:12:40  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.3  2004/02/12 16:48:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:24:06  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.5   Jan 06 2004 13:42:38   bjosserand
 * main refactoring.
 * 
 *    Rev 1.4   Dec 26 2003 10:28:56   bjosserand
 * Transfer store status in unload().
 * 
 *    Rev 1.3   Dec 18 2003 13:38:54   bjosserand
 * Removed debugging output.
 * 
 *    Rev 1.2   Dec 17 2003 17:24:04   bjosserand
 * Remove debug output. 
 * 
 *    Rev 1.1   Dec 16 2003 13:30:48   bjosserand
 * Main Refactor. Rewrite initialization using ADO and TDO objects.
 * 
 *    Rev 1.0   Dec 15 2003 09:30:34   bjosserand
 * Initial revision.
 * 
 *    Rev 1.0   Nov 03 2003 15:11:44   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

// foundation imports
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.services.common.StoreStatusCargoIfc;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents between
    the Main service to the SetupRegister service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SetupRegisterReturnShuttle implements ShuttleIfc
{                                       // begin class SetupRegisterShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6945337969976760215L;


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
        MainCargo mainCargo = (MainCargo) bus.getCargo();
        RegisterADO regADO = mainCargo.getRegisterADO();
        RegisterIfc register = fromCargo.getRegister();
        if (fromCargo.getRegister().getWorkstation().isTrainingMode())
        {
            register.getWorkstation().setTrainingMode(true);                    
        }
        else
        {
            register.getWorkstation().setTrainingMode(false);
        }                
        regADO.fromLegacy(register);
        regADO.getStoreADO().fromLegacy(fromCargo.getStoreStatus());
        
        AbstractFinancialCargoIfc cargo = 
                (AbstractFinancialCargoIfc) bus.getCargo();

// EPD        
//        cargo.setRegister(fromCargo.getRegister());
        cargo.setTenderLimits(fromCargo.getTenderLimits());

// EPD
//        cargo.setStoreStatus(fromCargo.getStoreStatus());
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()
}                                       // end class SetupRegisterShuttle
