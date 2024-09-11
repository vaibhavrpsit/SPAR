/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/CheckAlterationsPriceEntrySite.java /main/15 2011/12/05 12:16:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - refreshed to tip
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    sgu       06/08/10 - add item # & desc to the screen prompt. fix unknow
 *                         item screen to disable price and quantity for
 *                         external item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:34 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:04 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:53 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Nov 01 2002 12:00:56   DCobb
 * Fixed internationalization of price entry in currency text field.
 * Resolution for POS SCR-1753: POS 6.0 Alterations Package
 *
 *    Rev 1.3   Oct 14 2002 16:07:38   DCobb
 * Internationalization of price entry in currency text field.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.2   Sep 25 2002 17:37:22   DCobb
 * Added price entered indicator.
 * Resolution for POS SCR-1802: Response region defaults 0.00 after alterations item is added
 *
 *    Rev 1.1   Aug 21 2002 11:21:20   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

// Java imports
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    This site checks to see if the item requires a manual price entry.
    <p>
    @version $Revision: /main/15 $
**/
//--------------------------------------------------------------------------
public class CheckAlterationsPriceEntrySite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/15 $";
    /**
        class name constant
    **/
    public static final String SITENAME = "CheckAlterationsPriceEntrySite";

    //----------------------------------------------------------------------
    /**
        Checks the item to see if manual price entry is required.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        /*
         * Grab the item from the cargo
         */
        AlterationsCargo cargo = (AlterationsCargo)bus.getCargo();
        PLUItemIfc pluItem = null;
        boolean skipPriceEntry = false;
        Letter letter = null;

        pluItem = cargo.getPLUItem();
        skipPriceEntry = cargo.getSkipPriceEntryFlag();
        if (pluItem.getItemClassification().isPriceEntryRequired() && !skipPriceEntry)
        {
            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

            /*
             * Setup the bean model and display the screen
             */
            PromptAndResponseModel responseModel = new PromptAndResponseModel();
            String[] args = new String[2];
            args[0] = pluItem.getPosItemID();
            args[1] = pluItem.getDescription(locale);
            responseModel.setArguments(args);

            // display price
            CurrencyIfc price = pluItem.getPrice();
            if ((price != null) && (cargo.isPriceEntered()))
            {
                responseModel.setResponseText(price.toFormattedString());
            }

            POSBaseBeanModel baseModel = new POSBaseBeanModel();
            baseModel.setPromptAndResponseModel(responseModel);
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.ENTER_ZERO_ALLOWED_PRICE, baseModel);
        }
        else
        {
            letter = new Letter(CommonLetterIfc.CONTINUE);
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
