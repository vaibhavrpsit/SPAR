/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/EnterRedeemAmountUISite.java /main/17 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    rabhawsa  08/20/12 - removed place holder from key RedeemAmountPrompt
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/12/10 - use default locale for display of currency
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       01/13/09 - specify decimal format (non locale sensitive) in
 *                         tender attraibutes
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         7/26/2006 12:03:23 PM  Brendan W. Farrell
 *         Merged from v7.x.
 *    3    360Commerce 1.2         3/31/2005 4:28:04 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse
 *
 *   Revision 1.11  2004/07/15 23:22:45  crain
 *   @scr 5280 Gift Certificates issued in Training Mode can be Tendered outside of Training Mode
 *
 *   Revision 1.10  2004/05/20 20:55:34  tmorris
 *   @scr 5098 -Removed hard coding of max length in the response field and set the default value in redeemuicfg.xml
 *
 *   Revision 1.9  2004/05/20 19:48:52  crain
 *   @scr 5108 Tender Redeem_Redeem Foreign Gift Certificate Receipt Incorrect
 *
 *   Revision 1.8  2004/05/20 15:54:58  tmorris
 *   @scr 5098 -Set the max length for the response region.
 *
 *   Revision 1.7  2004/05/07 22:01:14  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.6  2004/04/29 23:48:50  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.5  2004/04/12 18:37:47  blj
 *   @scr 3872 - fixed a problem with validation occuring after foreign currency has been converted.
 *
 *   Revision 1.4  2004/04/07 22:49:40  blj
 *   @scr 3872 - fixed problems with foreign currency, fixed ui labels, redesigned to do validation and adding tender to transaction in separate sites.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * @author blj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class EnterRedeemAmountUISite extends SiteActionAdapter
{
//--------------------------------------------------------------------------
    /**

     This site displays the Redeem Number Site and
     collects this number from the ui in the depart method.
     @param bus the bus arriving at this site
     **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        JournalManagerIfc journalmgr = null;
        journalmgr = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        journalmgr.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);

        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // Append transaction id to prompt response
        String redeemTypeText = utility.retrieveText(
                "common",
                "commonText",
                cargo.getRedeemTypeSelected(),
                cargo.getRedeemTypeSelected());

        String pattern = utility.retrieveText("PromptAndResponsePanelSpec", "redeemText", "RedeemAmountPrompt",
                "Enter the amount of Gift Certificate or Store Credit, then press Next.");
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String message = LocaleUtilities.formatComplexMessage(pattern, redeemTypeText.toLowerCase(locale));
        PromptAndResponseModel promptModel = new PromptAndResponseModel();
        promptModel.setPromptText(message);

        beanModel.setPromptAndResponseModel(promptModel);

        ui.showScreen(POSUIManagerIfc.REDEEM_AMOUNT, beanModel);
    }

   //--------------------------------------------------------------------------
    /**
     *  Collect data from the UI upon depart.
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    //--------------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        if (bus.getCurrentLetter().getName().equals("Next"))
        {
            // Get information from UI
            RedeemCargo cargo = (RedeemCargo)bus.getCargo();
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            // amount from ui.
            String amount = LocaleUtilities.parseCurrency(ui.getInput().trim(), LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE)).toString();
            cargo.getTenderAttributes().put(TenderConstants.AMOUNT, amount);

            // set the training mode in tender attributes
            if (cargo.getRegister() != null)
            {
                boolean isTrainingMode = cargo.getRegister().getWorkstation().isTrainingMode();
                cargo.getTenderAttributes().put(TenderConstants.TRAINING_MODE, new Boolean(isTrainingMode));
            }
        }
    }
}
