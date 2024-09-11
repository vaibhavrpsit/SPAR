/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/ChangeDueOptionsTDO.java /main/16 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mahising  02/18/09 - fixed amount issue at refund screen
 *    cgreene   12/17/08 - Use TenderTypeEnums for constants
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:51 PM  Robert Pearse
 *
 *   Revision 1.9  2004/09/20 20:03:46  bwf
 *   @scr 3511 Remove unnecessary cast.
 *
 *   Revision 1.8  2004/06/04 21:27:18  bwf
 *   @scr 5205 Fixed change due options and store credit flow for undo
 *   and cancel during change and refund.
 *
 *   Revision 1.7  2004/04/27 15:50:29  epd
 *   @scr 4513 Fixing tender change options functionality
 *
 *   Revision 1.6  2004/04/21 18:56:40  epd
 *   @scr 4322 Fixing UI for tender invariant
 *
 *   Revision 1.5  2004/04/15 22:03:37  epd
 *   @scr 4322 Updates for Tender Invariant work: handling Change invariant
 *
 *   Revision 1.4  2004/04/14 22:37:53  epd
 *   @scr 4322 Tender Invariant work.  Specifically for change invariant
 *
 *   Revision 1.3  2004/03/23 17:41:28  bwf
 *   @scr 3956 Code Review
 *
 *   Revision 1.2  2004/03/18 18:56:32  bwf
 *   @scr 3956 Update Refund Options Buttons.
 *
 *   Revision 1.1  2004/03/09 20:10:23  bwf
 *   @scr 3956 General Tenders work.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;

/**
 * This class defines the Change Due Options screen.
 * 
 * @version $Revision: /main/16 $
 */
public class ChangeDueOptionsTDO extends TDOAdapter implements TDOUIIfc
{
    /** @deprecated as of 13.1 use {@link TenderTypeEnum#CASH} instead */
    public static final String CASH_TYPE = "Cash";
    /** @deprecated as of 13.1 use {@link TenderTypeEnum#MAIL_CHECK} instead */
    public static final String MAIL_CHECK_TYPE = "MailCheck";
    /** @deprecated as of 13.1 use {@link TenderTypeEnum#GIFT_CARD} instead */
    public static final String GIFT_CARD_TYPE = "GiftCard";
    /** @deprecated as of 13.1 use {@link TenderTypeEnum#GIFT_CERT} instead */
    public static final String GIFT_CERTIFICATE_TYPE = "GiftCert";
    /** @deprecated as of 13.1 use {@link TenderTypeEnum#STORE_CREDIT} instead */
    public static final String STORE_CREDIT_TYPE = "StoreCredit";


    /**
     * This method build the bean model.
     *
     * @param attributeMap
     * @return base bean model
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc)attributeMap.get(TenderTDOConstants.TRANSACTION);
        // Get RDO version of transaction for use in some processing
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();

        // get new tender bean model
        TenderBeanModel model = new TenderBeanModel();
        // populate tender bean model w/ tender and totals info
        model.setTransactionTotals(txnRDO.getTenderTransactionTotals());

        // We only want the forced cash change item here.
        TenderADOIfc[] cashChange = txnADO.getTenderLineItems(TenderLineItemCategoryEnum.FORCED_CASH_CHANGE);
        if (cashChange.length > 0)
        {
            Vector<TenderLineItemIfc> lineItemVector = new Vector<TenderLineItemIfc>();
            lineItemVector.add((TenderLineItemIfc)cashChange[0].toLegacy());
            model.setTenderLineItems(lineItemVector);
        }
        model.setLocalButtonBeanModel(getNavigationBeanModel(txnADO.getEnabledChangeOptions()));

        PromptAndResponseModel parModel = new PromptAndResponseModel();
        CurrencyIfc due = txnADO.getBalanceDue().abs();
        String changes = LocaleUtilities.formatCurrency(due.getDecimalValue(), LocaleMap
                .getLocale(LocaleMap.DEFAULT), false);
        String change = changes.toString();
        parModel.setGrabFocus(true);
        parModel.setResponseEditable(false);
        parModel.setResponseText(change);
        parModel.setResponseEditable(false);
        //parModel.setGrabFocus(false);
        model.setPromptAndResponseModel(parModel);

        return model;
    }

    /**
     * Enables and disables tender buttons for change due.
     *
     * @param enabledTypes
     * @return navigation button model
     */
    protected NavigationButtonBeanModel getNavigationBeanModel(TenderTypeEnum[] enabledTypes)
    {
        // convert to list
        ArrayList<TenderTypeEnum> typeList = new ArrayList<TenderTypeEnum>(enabledTypes.length);
        for (int i = 0; i < enabledTypes.length; i++)
        {
            typeList.add(enabledTypes[i]);
        }

        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        navModel.setButtonEnabled(TenderTypeEnum.CASH.toString(), typeList.contains(TenderTypeEnum.CASH));
        navModel.setButtonEnabled(TenderTypeEnum.MAIL_CHECK.toString(), typeList.contains(TenderTypeEnum.MAIL_CHECK));
        navModel.setButtonEnabled(TenderTypeEnum.GIFT_CARD.toString(), typeList.contains(TenderTypeEnum.GIFT_CARD));
        navModel.setButtonEnabled(TenderTypeEnum.GIFT_CERT.toString(), typeList.contains(TenderTypeEnum.GIFT_CERT));
        navModel.setButtonEnabled(TenderTypeEnum.STORE_CREDIT.toString(), typeList.contains(TenderTypeEnum.STORE_CREDIT));

        return navModel;
    }

    /**
     * This method defines the first line of the pole display.
     *
     * @param txnADO
     * @return string
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine1(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        return null;
    }

    /**
     * This method formats the second line of the pole display.
     *
     * @param txnADO
     * @return string
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine2(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        return null;
    }
}
