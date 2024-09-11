/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/TenderLineDisplayTDO.java /main/15 2012/09/12 11:57:20 blarsen Exp $
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
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:01 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:48:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:23:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Dec 18 2003 17:12:16   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 *    Rev 1.1   Nov 19 2003 16:21:18   epd
 * Refactoring updates
 *
 *    Rev 1.0   Nov 04 2003 11:19:12   epd
 * Initial revision.
 *
 *    Rev 1.1   Oct 22 2003 19:32:38   epd
 * removed BAD rdo logic
 *
 *    Rev 1.0   Oct 17 2003 12:45:26   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.HashMap;
import java.util.Locale;


import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TenderStateEnum;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActionGroupIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;


/**
 *
 */
public class TenderLineDisplayTDO extends TDOAdapter
                                  implements TDOUIIfc
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        // convert txn to RDO
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();

        // our display line
        String result = "";

        int tenderLISize = txnRDO.getTenderLineItemsSize();
        if(tenderLISize > 0)
        {
            // process most current line item
            TenderLineItemIfc tenderLI = txnRDO.getTenderLineItems()[tenderLISize - 1];
            String tenderTypeDesc = TenderLineItemIfc.TENDER_LINEDISPLAY_DESC[tenderLI.getTypeCode()];
            // get context
            BusIfc bus = TourContext.getInstance().getTourBus();
            // get utility manager
            UtilityManagerIfc utility =  (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            // get localized text for tender type
            tenderTypeDesc = utility.retrieveLineDisplayText(tenderTypeDesc,tenderTypeDesc);
            // get locale for currency display

            // get formatted tender amount
            String displayTenderAmount = null;
            if (tenderLI instanceof TenderAlternateCurrencyIfc &&
                ((TenderAlternateCurrencyIfc)tenderLI).getAlternateCurrencyTendered() != null)
            {
                //When alternate currency is tendered,
                //the tender amount displayed is the alternate currency value
                displayTenderAmount =
                  ((TenderAlternateCurrencyIfc)tenderLI).getAlternateCurrencyTendered().toFormattedString();
            }
            else
            {
                displayTenderAmount = tenderLI.getAmountTender().toFormattedString();
            }


            // format string
            StringBuffer sb = new StringBuffer();
            int length = POSDeviceActionGroupIfc.LINE_DISPLAY_SIZE - displayTenderAmount.length();
            sb.append(Util.formatTextData(tenderTypeDesc, length, false))
              .append(Util.formatTextData(displayTenderAmount, displayTenderAmount.length(), false));

            result = sb.toString();
        }
        return result;
    }

    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        // convert txn to RDO
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();
        // get locale for currency display
        Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);

        // Our formatted string
        String result = "";

        TransactionTotalsIfc totals = txnRDO.getTenderTransactionTotals();
        String balanceText = "";
        if (txnADO.evaluateTenderState() == TenderStateEnum.TENDER_OPTIONS)
        {
            // get context
            BusIfc bus = TourContext.getInstance().getTourBus();
            // get utility manager
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            // get balance text String
            balanceText = utility.retrieveLineDisplayText("BalanceDueText", "BAL. DUE");

            String balanceAmt = totals.getBalanceDue().toFormattedString();
            int descLen = POSDeviceActionGroupIfc.LINE_DISPLAY_SIZE - balanceAmt.length();

            StringBuffer sb = new StringBuffer();
            sb.append(Util.formatTextData(balanceText,descLen,false))
              .append(Util.formatTextData(balanceAmt,balanceAmt.length(),true));
            result = sb.toString();
        }
        else if (txnADO.evaluateTenderState() == TenderStateEnum.REFUND_OPTIONS)
        {
            // get context
            BusIfc bus = TourContext.getInstance().getTourBus();
            // get utility manager
            UtilityManagerIfc utility =  (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            // get balance text String
            balanceText = utility.retrieveLineDisplayText("RefundText", "REFUND");

            String balanceAmt = totals.getBalanceDue().toFormattedString();
            int descLen = POSDeviceActionGroupIfc.LINE_DISPLAY_SIZE - balanceAmt.length();

            StringBuffer sb = new StringBuffer();
            sb.append(Util.formatTextData(balanceText,descLen,false))
              .append(Util.formatTextData(balanceAmt,balanceAmt.length(),true));
            result = sb.toString();
        }

        return result;
    }
}
