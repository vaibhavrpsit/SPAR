/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/SetupRegisterShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
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
 *   Revision 1.2  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.1  2004/04/07 17:50:55  tfritz
 *   @scr 3884 - Training Mode rework
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents between
    the Sale service to the SetupRegister service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @since 14.1, this shuttle is not used.
**/
//------------------------------------------------------------------------------
@Deprecated
public class SetupRegisterShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 512244663105999452L;


    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
       Sale Cargo to be unloaded
     */
    protected SaleCargoIfc saleCargo = null; 
    
    //--------------------------------------------------------------------------
    /**
     Get a local copy of the Pos cargo.
     @param bus the bus being loaded
     **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve Pos cargo 
        saleCargo = (SaleCargoIfc) bus.getCargo(); 
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
                
        cargo.setStoreStatus(saleCargo.getStoreStatus());
        RegisterIfc register = saleCargo.getRegister();
        cargo.setRegister(register);
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
