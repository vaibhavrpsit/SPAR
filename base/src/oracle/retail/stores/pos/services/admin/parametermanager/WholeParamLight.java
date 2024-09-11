/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/WholeParamLight.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:06 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:51 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:38 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
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
 *    Rev 1.0   Aug 29 2003 15:53:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:40:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:42   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:11:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.ui.beans.RetailParameter;
import oracle.retail.stores.pos.ui.beans.WholeParameterBeanModel;


//------------------------------------------------------------------------------
/**
    This signal is green if the parameter in the cargo is modifiable and has a
    whole number value.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class WholeParamLight implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1389774024851968947L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.parametermanager.WholeParamLight.class);


    public static final String SIGNALNAME = "WholeParamLight";

    //--------------------------------------------------------------------------
    /**
       roadClear determines whether it is safe for the bus to proceed
       @param bus the bus trying to proceed
       @return true if the parameter in the cargo is modifiable and has a
       whole value; false otherwise
    **/
    //--------------------------------------------------------------------------

    public boolean roadClear(BusIfc bus)
    {
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        RetailParameter parameter = cargo.getParameter();
        boolean isGreen =
            (parameter instanceof WholeParameterBeanModel)
            && cargo.parameterModificationPermitted();
        return isGreen;
    }
}
