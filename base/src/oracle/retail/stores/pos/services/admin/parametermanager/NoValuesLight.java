/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/NoValuesLight.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:06 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:47 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:48:50  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:33  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:39:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:00   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   08 Feb 2002 17:29:20   KAC
 * Initial revision.
 * Resolution for POS SCR-1176: Update "list from list" parameter editing
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

import java.io.Serializable;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.ui.beans.ListFromListParameterBeanModel;

//------------------------------------------------------------------------------
/**
    This signal is green if the parameter in the cargo has at least one value.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class NoValuesLight implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4439412329879894877L;

    public static final String SIGNALNAME = "NoValuesLight";

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
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        ListFromListParameterBeanModel beanModel =
            (ListFromListParameterBeanModel)cargo.getParameter();
        Serializable[] values = beanModel.getNewValues();

        boolean isGreen = (values == null) || (values.length <= 0);
        return isGreen;
    }
}
