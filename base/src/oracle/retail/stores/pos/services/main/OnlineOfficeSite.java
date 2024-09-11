/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/OnlineOfficeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse   
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
 *    Rev 1.1   Jan 13 2004 13:22:00   bjosserand
 * code review
 * 
 *    Rev 1.0   Dec 15 2003 09:30:32   bjosserand
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:01:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:19:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:36:04   msg
 * Initial revision.
 * 
 *    Rev 1.5   31 Jan 2002 16:11:48   jbp
 * moved BackOfficeURL parameter into browser group and renamed to back office from OnlineOffice
 * Resolution for POS SCR-894: Ice Browser not working.  Parm url chg in POS does  not save to POS Appl xml
 *
 *    Rev 1.4   05 Nov 2001 12:28:46   jbp
 * Changed to use OnlineOfficeBean
 * Resolution for POS SCR-217: Combine CrossReach, POS, and OnlineOffice
 *
 *    Rev 1.3   17 Oct 2001 16:16:46   jbp
 * Remove System outs
 * Resolution for POS SCR-217: Combine CrossReach, POS, and OnlineOffice
 *
 *    Rev 1.0   17 Oct 2001 13:36:32   jbp
 * Initial revision.
 * Resolution for POS SCR-217: Combine CrossReach, POS, and OnlineOffice
 *
 *    Rev 1.1   Sep 17 2001 13:08:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

//java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.BrowserBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the application main menu.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class OnlineOfficeSite extends SiteActionAdapter
{
    /**
        The logger to which log messages will be sent.
    **/
    protected Logger logger = Logger.getLogger(getClass());

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Displays the main menu.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        BrowserBeanModel bbModel = new BrowserBeanModel();

        String onlineOfficeURL = new String("");

        // get PromptForCustomerInfo Parameter
        try
        {
            onlineOfficeURL = pm.getStringValue("BackOfficeURL");
        }
        catch (ParameterException pe)
        {
            logger.error(pe);
        }

        bbModel.setHomeUrl(onlineOfficeURL);

        ui.showScreen(POSUIManagerIfc.ONLINE_OFFICE, bbModel);
    }
}
