/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/common/ShowNoWarningSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:01 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:14 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:49:36  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:02  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Feb 17 2003 12:24:02   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.common;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.common.TillCargo;

//--------------------------------------------------------------------------
/**
    This signal checks to see if the service should not display a
    confirmation screen before proceeding.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ShowNoWarningSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4265274326884476449L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Checks to see if the confirmation screen should not be displayed.
        <p>
        @param  bus the bus trying to proceed
        @return true if screen should not be displayed; false otherwise
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        TillCargo cargo = (TillCargo)bus.getCargo();
        return(!cargo.getShowWarning());
    }
}
