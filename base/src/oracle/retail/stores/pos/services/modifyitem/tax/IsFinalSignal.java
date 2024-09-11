/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/IsFinalSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:32 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:51:07  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:18:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:29:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.tax;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;


//------------------------------------------------------------------------------
/**
    Clears if already received "Next" letter from UI
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class IsFinalSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3086849243536560113L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifyitem.tax.IsFinalSignal.class);

    public static final String SIGNALNAME = "IsFinalSignal";

    //--------------------------------------------------------------------------
    /**
       roadClear determines whether it is safe for the bus to proceed
       @param bus the bus trying to proceed
       @return true if safe; false otherwise
    **/
    //--------------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {

        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();


        return cargo.getFinalFlag();
    }
}
