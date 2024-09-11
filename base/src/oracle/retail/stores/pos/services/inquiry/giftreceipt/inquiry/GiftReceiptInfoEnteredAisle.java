/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/giftreceipt/inquiry/GiftReceiptInfoEnteredAisle.java /main/18 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  09/25/14 - Implement the giftcode lookup by running a tour
 *                         to reuse the eJournaling flow
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale when displaying currency
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    vchengeg  12/08/08 - EJ I18n formatting
 *    deghosh   10/29/08 - EJI18n_changes_ExtendyourStore
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *
 * ===========================================================================
     $Log:
      6    360Commerce 1.5         7/11/2007 3:07:55 PM   Ashok.Mondal    CR
           27628 :Correcting the Gift Price format in eJournal.
      5    360Commerce 1.4         1/25/2006 4:11:02 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce 1.3         1/22/2006 11:45:10 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:21:58 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/17/2005 16:39:26    Jason L. DeLeau 4345:
           Replace any uses of Gateway.log() with the log4j.
      3    360Commerce1.2         3/31/2005 15:28:18     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:21:58     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:11:16     Robert Pearse
     $
     Revision 1.10  2004/09/23 00:07:14  kmcbride
     @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents

     Revision 1.9  2004/05/18 21:46:01  lzhao
     @scr 4914: gift code inquiry.

     Revision 1.8  2004/03/16 18:30:48  cdb
     @scr 0 Removed tabs from all java source code.

     Revision 1.7  2004/02/26 22:09:33  lzhao
     @scr 3841 Inquiry Options Enhancement.
     Add new screens for item inventory inquiry and update the service.

     Revision 1.6  2004/02/19 18:06:25  lzhao
     @scr 3841 Inquiry Options Enhancement.
     Modify comments.

     Revision 1.5  2004/02/19 16:36:07  lzhao
     @scr 3841 Inquiry Options Enhancement
     add journal

     Revision 1.4  2004/02/16 22:41:05  lzhao
     @scr 3841:Inquiry Option Enhancement
     add gift code and add multiple inquiry.

     Revision 1.3  2004/02/12 16:50:28  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:10  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:59:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:22:56   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:33:26   msg
 * Initial revision.
 *
 *    Rev 1.4   Feb 05 2002 16:42:28   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.3   Dec 10 2001 17:23:34   blj
 * updated per codereview findings.
 * Resolution for POS SCR-237: Gift Receipt Feature
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.giftreceipt.inquiry;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.inquiry.giftreceipt.GiftReceiptCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;
import oracle.retail.stores.common.utility.StringUtils;

public class GiftReceiptInfoEnteredAisle extends LaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -3485052505007971205L;

    // Aisle class name
    public static final String LANENAME = "GiftReceiptInfoEnteredAisle";

    // gift code inquiry information for logging
    protected static final String GIFT_CODE_INQUIRY = "Gift Code Inquiry";

    // gift code information for logging
    protected static final String GIFT_CODE = "Gift Code: ";

    // gift price info for logging
    protected static final String GIFT_PRICE = "Gift Price: ";

    // ++ CR 27628
    // Currency Service
    protected static CurrencyServiceIfc currencyService = CurrencyServiceLocator.getCurrencyService();

    // Locale
    protected static Locale journalLocale = null;

    /**
     * Gets the locale used for Journaling
     *
     * @return
     */
    public static Locale getJournalLocale()
    {
        // attempt to get instance
        if (journalLocale == null)
        {
            journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
        }
        return journalLocale;
    }

    // -- CR 27628

    /**
     * @param bus the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {
        if (logger.isDebugEnabled())
            logger.debug(LANENAME + ".traverse starting...");

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        GiftReceiptCargo cargo = (GiftReceiptCargo)bus.getCargo();
        BigDecimal price = null;
        if (StringUtils.isEmpty(ui.getInput()))
        {
            price = cargo.convertPriceCodeToPrice(cargo.getPriceCode());
        }
        else
        {
            price = cargo.convertPriceCodeToPrice(ui.getInput());
        }

        cargo.setPrice(price);

        if (logger.isDebugEnabled())
            logger.debug(LANENAME + ".traverse ending...");

        // display journal information
        showGiftCodeJournale(bus, ui.getInput(), price);

        // for displaying gift code information
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

    /**
     * Journal the gift code inquiry results.
     *
     * @param String giftCode
     * @param BigDecimal giftPrice
     */
    protected void showGiftCodeJournale(BusIfc bus, String giftCode, BigDecimal giftPrice)
    {
        JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (jmi != null)
        {
            String formattedGiftPrice = currencyService.formatCurrency(giftPrice.toString(), LocaleMap.getLocale(LocaleMap.DEFAULT)); // CR
                                                                                                                  // 27628
            StringBuffer entry = new StringBuffer();
			Object[] dataArgs = new Object[1];
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.GIFT_CODE_INQUIRY_LABEL, null));
			entry.append(Util.EOL);
			dataArgs[0] = giftCode;
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.GIFT_CODE_LABEL, dataArgs));
			entry.append(Util.EOL);
			dataArgs[0] = formattedGiftPrice;
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.GIFT_PRICE_LABEL, dataArgs));
			entry.append(Util.EOL);
			//buffer.append(GIFT_PRICE).append(formattedGiftPrice).append("\n"); // CR 27628
			jmi.journal(entry.toString());
        }
    }
}
