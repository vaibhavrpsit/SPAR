/********************************************************************************
 * Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
 *
 * Rev 1.0 Hitesh.dua 		23jan,2017	Initial revision.
 * changes for printing loyalty charge slip 
 * ===========================================================================
 */

package max.retail.stores.pos.services.printing;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;

import max.retail.stores.domain.tender.MAXTenderLineItemIfc;
import max.retail.stores.domain.tender.MAXTenderLoyaltyPoints;
import max.retail.stores.domain.tender.MAXTenderLoyaltyPointsIfc;
import max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import max.retail.stores.pos.receipt.blueprint.MAXParameterConstantsIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.printing.PrintingCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Print credit signature slips if configured and needed.
 *
 */
public class MAXPrintLoyaltySlipSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -3619252291492212914L;

    public static final String SITENAME = "MAXPrintLoyaltySlipSite";
    private TenderableTransactionIfc trans;

    /**
     * If loyalty slips are configured and there are approved loyalty tenders, print
     * the slips for them.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        boolean sendMail = true;

        trans = cargo.getTransaction();
        if(cargo.isPrintPaperReceipt() && trans.getTransactionType() != TransactionIfc.TYPE_VOID)
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

            // Get Loyalty tenders
            try
            {
                TenderLineItemIfc[] tenders = trans.getTenderLineItems();
                for (int i = 0; i < tenders.length; i++)
                {
                    if (tenders[i].getTypeCode() == MAXTenderLineItemIfc.TENDER_TYPE_LOYALTY_POINTS)
                    {

                        // pass them to the POSDeviceActions object
                    	PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
                    	MAXReceiptParameterBeanIfc bean = (MAXReceiptParameterBeanIfc)pdm.getReceiptParameterBeanInstance((SessionBusIfc)bus, trans);
                			// Read Group Like Items on Receipt parameter value.
                    	bean.setLoyaltyRedeemedPoints(getRedeemedPoints(pm
        						.getDoubleValue(MAXParameterConstantsIfc.TENDER_LoyaltyPointConversionRate)));
                		bean.setLoyaltyRedeemedAmount(getRedeemedAmount());
                    	bean.setLocale(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT));
                        bean.setTransaction(trans);
                        bean.setDocumentType(MAXReceiptTypeConstantsIfc.LOYALTY_POINTS);
                        pdm.printReceipt((SessionBusIfc)bus, bean);
                        cargo.setReceiptPrinted(true);
                    }
                }

                // Update printer status
                ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
                                 POSUIManagerIfc.ONLINE, POSUIManagerIfc.DO_NOT_UNLOCK_CONTAINER);
            }
            catch (PrintableDocumentException e)
            {
                logger.warn(
                            "Unable to print loyalty slip. " + e.getMessage());

                // Update printer status
                ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS,
                                 POSUIManagerIfc.OFFLINE, POSUIManagerIfc.DO_NOT_UNLOCK_CONTAINER);

                if (e.getNestedException() != null)
                {
                    logger.warn("NestedException:\n" + Util.throwableToString(e.getNestedException()));
                }

                String msg[] = new String[1];
                UtilityManagerIfc utility =
                  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                msg[0] = utility.retrieveDialogText("RetryContinue.PrinterOffline",
                                                    "Printer is offline.");

                    DialogBeanModel model = new DialogBeanModel();
                    model.setResourceID("RetryContinue");
                    model.setType(DialogScreensIfc.RETRY_CONTINUE);
                    model.setArgs(msg);

                    // display dialog
                    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

                sendMail = false;
            } catch (ParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("unable to ", e);
			}
        }                                           // end Trans Void
        if (sendMail)
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }

    private String getRedeemedAmount()
	{
		Vector lineTenderItem = trans.getTenderLineItemsVector();
    	Iterator it = lineTenderItem.iterator();
    	CurrencyIfc amountTender = DomainGateway.getBaseCurrencyInstance();
    	for(int i=0;i<lineTenderItem.size();i++)
    	{
    		if(lineTenderItem.get(i) instanceof MAXTenderLoyaltyPoints)
    		{
    			MAXTenderLoyaltyPointsIfc pt = (MAXTenderLoyaltyPointsIfc)lineTenderItem.get(i);
    			amountTender = amountTender.add(pt.getAmountTender());
    		}
    	}
    	return amountTender.getStringValue();
	}
	private String getRedeemedPoints(Double loyaltyPointConversionRate)
	{
		BigDecimal d = new BigDecimal(getRedeemedAmount());
		BigDecimal point = d.multiply(new BigDecimal(loyaltyPointConversionRate));
		point = point.divide(new BigDecimal("100.00"), 0);
		return point+"";
	}
}
