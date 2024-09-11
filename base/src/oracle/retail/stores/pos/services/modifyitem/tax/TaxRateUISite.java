/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/TaxRateUISite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    acadar 10/29/08 - cleaned up commented out code
 *    acadar 10/28/08 - localization for item tax reason codes
 * ===========================================================================

 * $Log:
 *  5    .v8x      1.3.1.0     3/11/2007 2:21:30 PM   Brett J. Larsen CR 4530 -
 *        default reason code not being displayed (except when default is 1st
 *       in list)
 *
 *       adding support for default reason value
 *  4    360Commerce1.3         1/22/2006 11:45:12 AM  Ron W. Haight   removed
 *       references to com.ibm.math.BigDecimal
 *  3    360Commerce1.2         3/31/2005 4:30:20 PM   Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:25:49 AM  Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:14:44 PM  Robert Pearse
 * $
 * Revision 1.8  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.7  2004/03/11 23:10:27  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.6  2004/03/11 22:28:39  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.5  2004/03/11 20:21:31  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.3  2004/03/09 15:52:16  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.2  2004/03/07 18:44:11  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.1  2004/03/05 22:57:24  bjosserand
 * @scr 3954 Tax Override
 * Revision 1.3 2004/02/12 16:51:07 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:51:47 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:18 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.0 Aug 29 2003 16:01:58 CSchellenger Initial revision.
 *
 * Rev 1.5 Feb 27 2003 14:09:02 HDyer Don't use getSortIndex as it may not be the index into the reason code vector.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 * Rev 1.4 Feb 26 2003 16:12:48 HDyer Remove call to retrieveCommonText for the reason codes because this is now done
 * by the bean - standardizing behavior. Also use the selected index in setting the reason code in the bean model.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 * Rev 1.3 Sep 18 2002 17:15:24 baa country/state changes Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.2 Aug 09 2002 11:30:24 RSachdeva Code conversion Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.1 06 Jun 2002 17:33:46 sfl Fix for defect 1715 Resolution for POS SCR-1715: Send - crash when tax is turned
 * off for an item and send is selected
 *
 * Rev 1.0 Apr 29 2002 15:17:58 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:37:58 msg Initial revision.
 *
 * Rev 1.4 Mar 10 2002 08:52:34 mpm Text externalization. Resolution for POS SCR-351: Internationalization
 *
 * Rev 1.3 08 Feb 2002 12:29:42 vxs Updated if condition for OverrideUnallowed display Resolution for POS SCR-1117:
 * Modify Item - override tax percentage on an item with the tax turned off, system allows, but should not
 *
 * Rev 1.2 Feb 05 2002 16:42:48 mpm Modified to use IBM BigDecimal. Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 * Rev 1.1 08 Jan 2002 17:22:38 baa add tax override to flow when items are sent out of state Resolution for POS
 * SCR-520: Prepare Send code for review
 *
 * Rev 1.0 Sep 21 2001 11:29:28 msg Initial revision.
 *
 * Rev 1.1 Sep 17 2001 13:09:24 msg header update * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */

package oracle.retail.stores.pos.services.modifyitem.tax;

import java.math.BigDecimal;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
 * Site for entering tax rate data.
 * <P>
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class TaxRateUISite extends PosSiteActionAdapter
{

    //--------------------------------------------------------------------------
    /**
     * Revision Number furnished by TeamConnection.
     * <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * constant for out of area tax rate prompt tag
     */
    protected static String SEND_OUT_OF_AREA_TAX_PROMPT_TAG = "SendOutOfAreaTaxPrompt";
    /**
     * constant for out of area tax rate prompt
     */
    protected static String SEND_OUT_OF_AREA_TAX_PROMPT =
        "Enter tax rate for send items, select a reason code and press Next.";

    //----------------------------------------------------------------------
    /**
     * This method shows the UI so the user can enter an override tax rate.
     * <P>
     *
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // get the cargo
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();

        // set up the ui model
        DecimalWithReasonBeanModel beanModel = new DecimalWithReasonBeanModel();

        // response model
        PromptAndResponseModel responseModel = new PromptAndResponseModel();
        // get reason codes and set list in bean model
        CodeListIfc rcl = utility.getReasonCodes(cargo.getOperator().getStoreID(),CodeConstantsIfc.CODE_LIST_ITEM_TAX_RATE_OVERRIDE_REASON_CODES);
        cargo.setLocalizedOverrideRateReasons(rcl);
        cargo.setFinalFlag(false);


        // get default amount, only if item tax by amount override is in place
        ItemTaxIfc tax = cargo.getItemTax();
        String code = tax.getReason().getCode();
        String selectedCode = null;


        if (!code.equals(CodeConstantsIfc.CODE_UNDEFINED))
        {
            selectedCode = code;
        }
        beanModel.inject(rcl, selectedCode, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

        beanModel.setValue(new BigDecimal(tax.getOverrideRate()));

        // indicate a selection on the ui
        if (cargo.isSendOutOfArea())
        {
            responseModel.setPromptText(utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                                                BundleConstantsIfc.MODIFYITEM_BUNDLE_NAME,
                                                                    SEND_OUT_OF_AREA_TAX_PROMPT_TAG,
                                                                        SEND_OUT_OF_AREA_TAX_PROMPT));
            beanModel.setPromptAndResponseModel(responseModel);
        }
        // Show the input screen
        uiManager.showScreen(POSUIManagerIfc.ITEM_TAX_OVERRIDE_RATE, beanModel);
    }
}
