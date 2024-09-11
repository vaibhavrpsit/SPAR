/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/EnterEmployeeIDSite.java /rgbustores_13.4x_generic_branch/3 2011/05/24 19:03:16 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/18/11 - remove major card swipe from enrollment flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse
 *
 *Revision 1.3  2004/02/12 16:50:42  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 24 2003 19:42:46   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the Sales Associate Identification screen.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/3 $
**/
//--------------------------------------------------------------------------
public class EnterEmployeeIDSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/3 $";

    // static string constant
    /**
     * Parameter name
     */
    public static final String PRODUCTIVITY = "Productivity";


    //----------------------------------------------------------------------
    /**
        Displays the Sales Associate Identification screen.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        // check productivity param
        boolean productivity = false;
        try
        {
            productivity = pm.getBooleanValue(PRODUCTIVITY);
        }
        catch(ParameterException pe)
        {
            logger.warn( pe.getStackTraceAsString());
        }

        if (productivity)
        {
            ui.showScreen(POSUIManagerIfc.INSTANT_CREDIT_ITEM_SALES_ASSC, new POSBaseBeanModel());
        }
        else
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }
}
