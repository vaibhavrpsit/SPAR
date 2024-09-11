/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/RequestTicketAisle.java /rgbustores_13.4x_generic_branch/4 2011/09/16 15:17:06 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/16/11 - repackage commext
 *    mchellap  06/15/11 - Added NPE check for locale
 *    rrkohli   05/17/11 - user batch description in LATUI fixed
 *    rsnayak   04/20/11 - XbranchMerge rsnayak_bug-12377840 from main
 *    rsnayak   04/20/11 - added storeid and locale to message
 *    rsnayak   03/04/11 - pos lat integration for label batch
 * ===========================================================================
 * $Log: RequestTicketAisle.java
 * Revision 1.0 rsnayak
 * ===========================================================================
 * */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import java.util.Locale;

import oracle.retail.stores.commext.manager.ConnectorManager;
import oracle.retail.stores.commext.message.Message;
import oracle.retail.stores.commext.message.MessageIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.comm.CommException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.ValetException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.formatter.LATRequestTicketBean;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

@SuppressWarnings("serial")
public class RequestTicketAisle extends LaneActionAdapter
{
    private String LAT_REQUEST_TICKET = "LAT_REQUEST_TICKET";

    private String result = null;

    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        String itemId = cargo.getPLUItem().getItemID();
        String storeID = cargo.getRegister().getWorkstation().getStoreID();
        Locale locale = cargo.getOperator().getPreferredLocale();
        if (locale == null)
        {
            locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        }
        String userID = cargo.getOperator().getLoginID();
        LATRequestTicketBean bean = new LATRequestTicketBean(itemId, storeID, locale.getLanguage(), userID);
        MessageIfc message = new Message(LAT_REQUEST_TICKET, bean);
        ConnectorManager connMgr = (ConnectorManager)bus.getManager("ConnectorManager");
        Object resp = null;

        try
        {
            resp = connMgr.execute(message);
            if (resp instanceof String)
            {
                result = (String)resp;
                if (!("-1".equals(result)))
                    displayResult(ui);

            }
        }
        catch (CommException e)
        {
            logger.error("Failed to create LAT ticket :", e);
            displayError(ui);

        }
        catch (ValetException e)
        {
            logger.error("Failed to create LAT ticket :", e);
            displayError(ui);
        }

    }

    private void displayResult(POSUIManagerIfc ui)
    {
        DialogBeanModel dialogBean = new DialogBeanModel();
        dialogBean.setResourceID("RequestTicketSuccess");
        dialogBean.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogBean.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.NEXT);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBean);

    }

    private void displayError(POSUIManagerIfc ui)
    {
        DialogBeanModel dialogBean = new DialogBeanModel();
        dialogBean.setResourceID("RequestTicketFailure");
        dialogBean.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogBean.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.NEXT);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBean);

    }
}
