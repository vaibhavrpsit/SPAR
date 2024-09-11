/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/PaidUpTDO.java /main/19 2013/05/21 15:18:12 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     05/20/13 - Refactored the location of cash change adjustment
 *                         calculation in the tender tour to handle cancel
 *                         order refunds.
 *    jswan     02/20/13 - Modified for Currency Rounding.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    rsnayak   08/16/11 - Fix to update Change due
 *    rrkohli   05/06/11 - added for pos ui quickwin
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency display
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    sgu       02/13/09 - fix line splitting in promot text
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:43 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:29:18 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:58 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:58 PM  Robert Pearse
 *
 *   Revision 1.7.2.2  2004/11/05 21:54:00  bwf
 *   @scr 3511 Set correct totals.
 *
 *   Revision 1.7.2.1  2004/11/05 19:36:09  lzhao
 *   @scr 7416: use calculateChangeDue() in transaction to replace balanceDue in Totals. BalanceDue is zero.
 *
 *   Revision 1.7  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.6  2004/05/20 22:54:58  cdb
 *   @scr 4204 Removed tabs from code base again.
 *
 *   Revision 1.5  2004/05/20 19:22:51  jeffp
 *   @scr 3772 - added check if the transaction was a layaway or special order.
 *
 *   Revision 1.4  2004/05/17 18:00:00  khassen
 *   @scr 4196 - Fix for special order when change due should report as balance due.
 *
 *   Revision 1.3  2004/04/27 15:50:29  epd
 *   @scr 4513 Fixing tender change options functionality
 *
 *   Revision 1.2  2004/02/12 16:48:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 19 2003 14:11:00   epd
 * TDO refactoring to use factory
 *
 *    Rev 1.0   Nov 04 2003 11:19:10   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 12:45:26   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActionGroupIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

import org.apache.log4j.Logger;

/**
 *
 */
public class PaidUpTDO extends TDOAdapter
                       implements TDOUIIfc
{
    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(PaidUpTDO.class);

    // attributeMap keys
    public static final String TRANSACTION = "Transaction";
    public static final String BUS         = "Bus";

    /* (non-Javadoc)
     * @see oracle.retail.stores.tdo.TDOIfc#buildBeanModel(java.util.HashMap)
     */
    @SuppressWarnings("rawtypes")
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        // create the bean model and update it with transaction information
        TenderBeanModel model = new TenderBeanModel();


        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc)attributeMap.get(TRANSACTION);
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();

        model.setTenderLineItems(txnRDO.getTenderLineItemsVector());
        
        model.setTransactionTotals(txnRDO.getTenderTransactionTotals());

        // set prompt and response model
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        String change = LocaleUtilities.formatCurrency(txnADO.getTotalCashChangeAmount().abs().getStringValue(),
                                                       LocaleMap.getLocale(LocaleMap.DEFAULT));
        
        if (txnRDO.getTenderTotalAmountPlusChangeDue()!= null)
        {
            model.getTotalsModel().setTendered(txnRDO.getTenderTotalAmountPlusChangeDue().toFormattedString());
            if ( txnRDO.getTenderTransactionTotals().getGrandTotal() != null)
            {
                CurrencyIfc balanceDue = txnRDO.getTenderTransactionTotals().getGrandTotal().subtract(txnRDO.getTenderTotalAmountPlusChangeDue());
                model.getTotalsModel().setBalanceDue(balanceDue.toFormattedString());
            }
        }
        
        String arg = change;
        boolean openDrawer = txnADO.openDrawer();
        if (openDrawer)
        {
            BusIfc bus = (BusIfc)attributeMap.get(BUS);
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String closeDrawerTxt = utility.retrieveText("PromptAndResponsePanelSpec",
                                                         BundleConstantsIfc.TENDER_BUNDLE_NAME,
                                                         "IssueChangeCloseDrawerPrompt",
                                                         "Issue change of {0} and close the cash drawer.");
            parModel.setPromptText(closeDrawerTxt);
        }
        parModel.setArguments(arg);
        model.setPromptAndResponseModel(parModel);

        return model;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine1(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        return null;
    }

    /**
     * Format the pole display message
     * @param txnADO
     * @return
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        int digits = UIFactory.getInstance().getCurrencyDigits();
        int descLen = POSDeviceActionGroupIfc.LINE_DISPLAY_SIZE - digits;

        // get context
        BusIfc bus = TourContext.getInstance().getTourBus();
        // use context to get manager
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        String changeDue;

        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();

        CurrencyIfc displayAmount = null;
        // change message if the transaction is a deposit
        if (txnRDO.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_INITIATE ||
            txnRDO.getTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_INITIATE)
        {
            displayAmount = txnRDO.getTenderTransactionTotals().getBalanceDue();
            txnRDO.getTenderTransactionTotals().setChangeDue(displayAmount);
            changeDue = utility.retrieveLineDisplayText("BalanceDueText", "BALANCE DUE");
        }
        else
        {
            displayAmount = txnRDO.calculateChangeGiven();
            txnRDO.getTenderTransactionTotals().setChangeDue(displayAmount);
            changeDue = utility.retrieveLineDisplayText("ChangeDueText", "CHANGE DUE");
        }
        StringBuffer sb = new StringBuffer();
        sb.append(Util.formatTextData(changeDue,descLen,false));
        sb.append(Util.formatTextData(displayAmount.toFormattedString(),digits,true));

        return sb.toString();
    }

}
