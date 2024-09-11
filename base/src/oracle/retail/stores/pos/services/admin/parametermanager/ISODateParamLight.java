/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/ISODateParamLight.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:05 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:34 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.2  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.1  2004/03/19 21:02:56  mweis
 *   @scr 4113 Enable ISO_DATE datetype
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.ui.beans.ISODateParameterBeanModel;
import oracle.retail.stores.pos.ui.beans.RetailParameter;


//------------------------------------------------------------------------------
/**
    This signal is green if the parameter in the cargo is modifiable and has a
    date value.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class ISODateParamLight implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -658590453178762379L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.parametermanager.ISODateParamLight.class);

    /** 
        The signal name.
     **/
    public static final String SIGNALNAME = "ISODateParamLight";

    //--------------------------------------------------------------------------
    /**
       roadClear determines whether it is safe for the bus to proceed
       @param bus the bus trying to proceed
       @return true if the parameter in the cargo is modifiable and has a
       date value; false otherwise
    **/
    //--------------------------------------------------------------------------

    public boolean roadClear(BusIfc bus)
    {
        ParameterCargo cargo = (ParameterCargo)bus.getCargo();
        RetailParameter parameter = cargo.getParameter();
        boolean isGreen =
            (parameter instanceof ISODateParameterBeanModel)
            && cargo.parameterModificationPermitted();
        return isGreen;
    }
}
