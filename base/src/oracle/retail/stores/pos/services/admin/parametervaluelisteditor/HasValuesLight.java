/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametervaluelisteditor/HasValuesLight.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:18 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:48:51  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   10 Feb 2002 14:29:02   KAC
 * Initial revision.
 * Resolution for POS SCR-1226: Update list parameter value editor per new requirements
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametervaluelisteditor;

import java.util.Vector;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;


//------------------------------------------------------------------------------
/**
    This signal is green if the parameter in the cargo has at least one value.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class HasValuesLight implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6142541989526781392L;

    public static final String SIGNALNAME = "HasValuesLight";

    //--------------------------------------------------------------------------
    /**
       roadClear determines whether it is safe for the bus to proceed
       This signal is green if the parameter in the cargo has at least
       one value.
       @param bus the bus trying to proceed
       @return true if the parameter in the cargo has at least
       one value.
    **/
    //--------------------------------------------------------------------------

    public boolean roadClear(BusIfc bus)
    {
        ListEditorCargo cargo = (ListEditorCargo)bus.getCargo();
        ReasonCodeGroupBeanModel reasonCodeGroup = cargo.getReasonCodeGroup();
        Vector reasonCodes = reasonCodeGroup.getReasonCodes();

        boolean isGreen = (reasonCodes != null) && (reasonCodes.size() > 0);
        return isGreen;
    }
}
