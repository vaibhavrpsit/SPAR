/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/ChangeDueTDO.java /main/16 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       02/13/09 - fix concatenated prompt text
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:43 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:52 PM  Robert Pearse
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
 *    Rev 1.1   Nov 19 2003 14:10:56   epd
 * TDO refactoring to use factory
 *
 *    Rev 1.0   Nov 04 2003 11:19:10   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 12:45:24   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.foundation.utility.Util;
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

/**
 *
 */
public class ChangeDueTDO extends TDOAdapter
                                  implements TDOUIIfc
{
    // attributeMap keys
    public static final String TRANSACTION = "Transaction";
    public static final String BUS         = "Bus";
    public static final String OPEN_DRAWER = "OpenDrawer";

    /* (non-Javadoc)
     * @see oracle.retail.stores.tdo.TDOIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        // create the bean model and update it with transaction information
        TenderBeanModel model = new TenderBeanModel();

        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc)attributeMap.get(TRANSACTION);
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();

        model.setTenderLineItems(txnRDO.getTenderLineItemsVector());
        model.setTransactionTotals(txnRDO.getTransactionTotals());

        // set prompt and response model
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        String change = LocaleUtilities.formatCurrency(txnRDO.getTransactionTotals().getBalanceDue().getStringValue().substring(1),
                                                       LocaleMap.getLocale(LocaleMap.DEFAULT));
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
        // TODO Auto-generated method stub
        return null;
    }

    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        int digits = UIFactory.getInstance().getCurrencyDigits();
        int descLen = POSDeviceActionGroupIfc.LINE_DISPLAY_SIZE - digits;
        //use store locale for formatting of currency


        // get context
        BusIfc bus = TourContext.getInstance().getTourBus();
        // use context to get manager
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        String changeDue =
          utility.retrieveLineDisplayText("ChangeDueText", "CHANGE DUE");

        // get balance due
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();
        CurrencyIfc balance = txnRDO.getTransactionTotals().getBalanceDue();

        StringBuffer sb = new StringBuffer();
        sb.append(Util.formatTextData(changeDue,descLen,false));
        sb.append(Util.formatTextData(balance.toFormattedString(),digits,true));

        return sb.toString();
    }
}
