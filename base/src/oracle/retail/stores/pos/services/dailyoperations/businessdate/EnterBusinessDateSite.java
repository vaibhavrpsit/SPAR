/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/businessdate/EnterBusinessDateSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:35  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:31  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 07 2002 19:33:58   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:31:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:22   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:12   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 10 2002 09:37:22   mpm
 * Externalized text.
 * Resolution for POS SCR-351: Internationalization
 * 
 *    Rev 1.1   Mar 09 2002 14:49:04   mpm
 * Text externalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:16:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.businessdate;

// foundation imports
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EnterBusinessDateBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
    This site displays the store-open screen, using the business date
    from cargo as the default. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EnterBusinessDateSite extends PosSiteActionAdapter
{                                       // begin class EnterBusinessDateSite

    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       site name constant
    **/
    public static final String SITENAME = "EnterBusinessDateSite";

    public static final String PREVIOUS_PROMPT_TAG = "EnterBusinessDateSite.EnterPreviousBusinessDatePrompt";
    public static final String PREVIOUS_PROMPT = "Enter previous business date or accept the default business date and press Next.";

    //--------------------------------------------------------------------------
    /**
       Displays store-open screen, using store-status business date in
       cargo as the default business date. <P.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()

        // pull business date from cargo
        BusinessDateCargo cargo = (BusinessDateCargo) bus.getCargo();
        EYSDate defaultDate = cargo.getDefaultBusinessDate();

        // get the user interface manager, bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        EnterBusinessDateBeanModel model = new EnterBusinessDateBeanModel();
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // set reference to business date
        model.setBusinessDate(defaultDate);
        // set reference to next business date - to refresh date if cleared in ui
        model.setNextBusinessDate(defaultDate);

        // if database offline, override prompt
        if (cargo.isDatabaseOffline())
        {
            // set prompt text depending whether online
            PromptAndResponseModel prm = new PromptAndResponseModel();
            prm.setPromptText(utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                                   BundleConstantsIfc.BUSINESS_DATE_BUNDLE_NAME,
                                                   PREVIOUS_PROMPT_TAG,
                                                   PREVIOUS_PROMPT));
            // update the model prompt text
            model.setPromptAndResponseModel(prm);
        }


        // display user interface
        ui.showScreen(POSUIManagerIfc.ENTER_BUSINESS_DATE,
                      (UIModelIfc)model);
    }                                   // end arrive()

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

}                                       // end class EnterBusinessDateSite
