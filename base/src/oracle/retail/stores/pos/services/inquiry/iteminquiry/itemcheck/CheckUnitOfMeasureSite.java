/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/itemcheck/CheckUnitOfMeasureSite.java /main/15 2011/12/05 12:16:19 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    tksharma  10/10/11 - Added skipUOMCheckFlag
 *    rrkohli   09/30/11 - UOM scrren fix
 *    vtemker   07/25/11 - Fixed code review comments for the fix for 12530442
 *    vtemker   07/25/11 - Bug 12530442: If multiple items are added, skip UOM
 *                         screen
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mchellap  05/19/09 - Fixed the UOM check
 *    aariyer   04/01/09 - Checked in files for not performing UOM Check and
 *                         indented code/added comments
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:14 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse
 *
 *   Revision 1.7  2004/08/05 22:17:54  dcobb
 *   @scr 6655 Remove letter checks from shuttles.
 *   Modified itemcheck service to initialize the modifyFlag to false and set to true when the item is ready to add to the sale.
 *
 *   Revision 1.6  2004/07/30 22:02:55  aschenk
 *   @scr 4960 - Selling a kit with a UOM item now asks for the qty.
 *
 *   Revision 1.5  2004/07/22 00:06:35  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.4  2004/03/03 23:15:12  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:36  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   13 Nov 2003 10:35:08   jriggins
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry.itemcheck;

// foundation imports
import java.util.Locale;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;


//--------------------------------------------------------------------------
/**
    This site checks to see if additional Unit of Measure information
    is needed.
    @version $Revision: /main/15 $
**/
//--------------------------------------------------------------------------
public class CheckUnitOfMeasureSite extends PosSiteActionAdapter
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/15 $";
    /**
        Constant for unit of measure UNITS.
    **/
    public static final String UNITS = "UN";

    /**
     * Prompt respose spec
     */
    protected static final String PROMPT_SPEC = "PromptAndResponsePanelSpec";

    /**
     * Prompt message tag
     */
    protected static final String PROMPT_MESSAGE_TAG = "UnitOfMeasureKitPrompt";

    /**
     *  Prompt message default text
     */
    protected static final String PROMPT_MESSAGE = "Enter total {0} for item number {1}.";


    //----------------------------------------------------------------------
    /**
        Checks the Unit of Measure information to see if additional
        information is needed.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        String letter = null;

        // retrieve item object
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        PLUItemIfc pluItem = cargo.getPLUItem();
        // This check has been added for ItemBasket to skip the UOM check.
        // If items with multiple quantity are added from the Sell item screen,
        // like (100*1234), skip UOM screen
        if (cargo.isSkipUOMCheck())
        {
            letter = CommonLetterIfc.CONTINUE;
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
        else
        {
            //if it is a kit we must go through each item and check for UOM items.
            if (pluItem.isKitHeader()){
                KitComponentIfc kc[] = ((ItemKitIfc)cargo.getPLUItem()).getComponentItems();
                int index = ((ItemKitIfc)cargo.getPLUItem()).getindex();
                index++;
                while (index < kc.length)
                {
                    if (kc[index].getUnitOfMeasure() == null ||
                            kc[index].getUnitOfMeasure().getUnitID().equals(UNITS))
                    {
                        index = index+1;
                        continue;
                    }
                    else
                    {
                        // save the index of the kit component unit of measure item
                        ((ItemKitIfc)cargo.getPLUItem()).setindex(index);
                        // initialize the bean model
                        POSBaseBeanModel baseModel = new POSBaseBeanModel();
                        PromptAndResponseModel beanModel = new PromptAndResponseModel();
                        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                        String pattern =
                            utility.retrieveText(
                                    PROMPT_SPEC,
                                    BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
                                    PROMPT_MESSAGE_TAG,
                                    PROMPT_MESSAGE);

                        String argSt[] = new String[] {kc[index].getUnitOfMeasure().getName(locale), kc[index].getItemID()};
                        String message = LocaleUtilities.formatComplexMessage(pattern, argSt);

                        beanModel.setPromptText(message);
                        baseModel.setPromptAndResponseModel(beanModel);

                        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                        ui.showScreen(POSUIManagerIfc.UNIT_OF_MEASURE, baseModel);
                        break;
                    }
                }

                if (index >= kc.length)
                {
                    letter = CommonLetterIfc.CONTINUE;
                    bus.mail(new Letter(letter), BusIfc.CURRENT);
                }
            }
            else
            {
                if (pluItem.getUnitOfMeasure() == null ||
                        pluItem.getUnitOfMeasure().getUnitID().equals(UNITS))
                {   // Default UOM; if it is a gift card the next site will get
                    // the gift card information
                    if (pluItem instanceof GiftCardPLUItemIfc)
                    {
                        letter = CommonLetterIfc.GIFTCARD;
                    }
                    else
                    {
                        letter = CommonLetterIfc.CONTINUE;
                    }
                    bus.mail(new Letter(letter), BusIfc.CURRENT);
                }
                else
                {
                    // initialize the bean model
                    POSBaseBeanModel baseModel = new POSBaseBeanModel();
                    PromptAndResponseModel beanModel = new PromptAndResponseModel();

                    //beanModel.setResponseText(cargo.getItemQuantity().toString());
                    beanModel.setArguments(pluItem.getUnitOfMeasure().getName(locale));
                    baseModel.setPromptAndResponseModel(beanModel);

                    POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                    ui.showScreen(POSUIManagerIfc.UNIT_OF_MEASURE, baseModel);
                }
            }
        }
    }
}
