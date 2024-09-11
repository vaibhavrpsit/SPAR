/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CheckPriceEntrySite.java /main/15 2011/02/16 09:13:25 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    sgu       06/08/10 - rename mandatoryPrice to externalPrice to be
 *                         consistent
 *    sgu       06/08/10 - add item # & desc to the screen prompt. fix unknow
 *                         item screen to disable price and quantity for
 *                         external item
 *    sgu       06/03/10 - add item # and description to enter price screen
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:11 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:58 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 12 2003 14:08:28   lzhao
 * change letter from OpenAmountGC to GiftCard
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.0   Aug 29 2003 15:54:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:35:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:08:40   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:22:20   msg
 * Initial revision.
 *
 *    Rev 1.2   Oct 31 2001 15:11:36   cir
 * Took out System.err.println
 * Resolution for POS SCR-224: Open Amount Gift Card
 *
 *    Rev 1.1   Oct 23 2001 11:28:58   cir
 * Check for gift card/open amount gift card and send the appropriate letters
 * Resolution for POS SCR-224: Open Amount Gift Card
 *
 *    Rev 1.0   Sep 21 2001 11:13:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.foundation.utility.ReflectionUtility;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This site checks to see if the item requires a manual price entry.
 * 
 * @version $Revision: /main/15 $
 */
public class CheckPriceEntrySite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 1854243605573347220L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Checks the item to see if manual price entry is required.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        /*
         * Grab the item from the cargo
         */
        CargoIfc cargo = bus.getCargo();
        PLUItemIfc pluItem = null;
        CurrencyIfc externalPrice = null;
        Letter letter = null;

        try
        {
            pluItem = (PLUItemIfc)ReflectionUtility.getAttribute(cargo, "PLUItem");
            externalPrice = (CurrencyIfc)ReflectionUtility.getAttribute(cargo, "externalPrice");
        }
        catch (Exception e)
        {
            logger.error( e.getMessage());
        }

        boolean skipPriceEntry = externalPrice != null;
        if (pluItem.getItemClassification().isPriceEntryRequired() && !skipPriceEntry)
        {
            if (pluItem instanceof GiftCardPLUItemIfc)
            {
                letter = new Letter(CommonLetterIfc.GIFTCARD);
            }
            else
            {
                /*
                 * Setup the bean model and display the screen
                 */
                POSBaseBeanModel baseModel = new POSBaseBeanModel();
                PromptAndResponseModel responseModel = new PromptAndResponseModel();
                String[] args = new String[2];
                args[0] = pluItem.getPosItemID();
                args[1] = pluItem.getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
                responseModel.setArguments(args);
                baseModel.setPromptAndResponseModel(responseModel);
                POSUIManagerIfc ui;
                ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.ENTER_PRICE, baseModel);
            }
        }
        else
        {
            if (pluItem instanceof GiftCardPLUItemIfc)
            {
                letter = new Letter(CommonLetterIfc.GIFTCARD);
            }
            else
            {
                letter = new Letter(CommonLetterIfc.CONTINUE);
            }
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
