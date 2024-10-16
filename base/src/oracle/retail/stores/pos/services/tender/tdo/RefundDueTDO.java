/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/RefundDueTDO.java /main/18 2013/05/21 15:18:12 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     05/20/13 - Refactored the location of cash change adjustment
 *                         calculation in the tender tour to handle cancel
 *                         order refunds.
 *    jswan     02/25/13 - Modified for Currency Rounding.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    nkgautam  02/12/10 - fixed incorrect data issue on the status panel area
 *    abondala  01/03/10 - update header date
 *    sgu       02/13/09 - fix concatenated prompt text
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:43 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:36 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:37 PM  Robert Pearse
 *
 *   Revision 1.3  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.2  2004/02/12 16:48:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 19 2003 14:11:02   epd
 * TDO refactoring to use factory
 *
 *    Rev 1.0   Nov 04 2003 11:19:12   epd
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
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
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

/**
 *
 */
public class RefundDueTDO extends TDOAdapter
                          implements TDOUIIfc
{
    // attributeMap keys
    public static final String TRANSACTION = "Transaction";
    public static final String BUS         = "Bus";

    /* (non-Javadoc)
     * @see oracle.retail.stores.tdo.TDOIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(@SuppressWarnings("rawtypes") HashMap attributeMap)
    {
        // create the bean model and update it with transaction information
        TenderBeanModel model = new TenderBeanModel();

        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc)attributeMap.get(TRANSACTION);
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();

        model.setTenderLineItems(txnRDO.getTenderLineItemsVector());
        model.setTransactionTotals(txnRDO.getTenderTransactionTotals());

        PromptAndResponseModel parModel = new PromptAndResponseModel();
        boolean openDrawer = txnADO.openDrawer();
        CurrencyIfc refundAmt = txnADO.getTenderTotal(TenderTypeEnum.CASH);
        String refund = refundAmt.toFormattedString();
        String arg = refund;
        if (openDrawer &&
            txnRDO.getTransactionTotals().getGrandTotal().signum() == CurrencyIfc.NEGATIVE &&
            txnRDO.getTransactionTotals().getAmountTender().signum() == CurrencyIfc.NEGATIVE)
        {
            BusIfc bus = (BusIfc)attributeMap.get(BUS);
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String closeDrawerTxt = utility.retrieveText("PromptAndResponsePanelSpec",
                                                         BundleConstantsIfc.TENDER_BUNDLE_NAME,
                                                         "IssueRefundCloseDrawerPrompt",
                                                         "Issue refund of {0} and close the cash drawer.");
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
        
        // get the bus from the tour context; use the bus to get the utility manager
        BusIfc bus = TourContext.getInstance().getTourBus();
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        String refundDue =
          utility.retrieveLineDisplayText("RefundText", "REFUND");

        // get refund amount
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();
        CurrencyIfc refund = txnRDO.getTransactionTotals().getAmountTender();

        StringBuilder sb = new StringBuilder();
        sb.append(Util.formatTextData(refundDue,descLen,false));
        
        //use default locale for currency and date/time display
        sb.append(Util.formatTextData(refund.toFormattedString(),digits,true));

        return sb.toString();
    }

}
