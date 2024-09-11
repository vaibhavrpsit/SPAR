/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2001 360Commerce, Inc.    All Rights Reserved.

     $Log:
      6    360Commerce 1.5         4/5/2006 5:59:48 AM    Akhilashwar K. Gupta
           CR-3861: As per BA decision, reverted back the changes done earlier
            to fix the CR i.e. addition of following 4 fields in Store Credit
           and related code:
           - RetailStoreID
           - WorkstationID
           - TransactionSequenceNumber
           - BusinessDayDate
      5    360Commerce 1.4         3/20/2006 5:21:12 AM   Akhilashwar K. Gupta
           CR-3861: Updated "depart()" to check for null before putting
           business day date into Tender Attributes.
      4    360Commerce 1.3         3/15/2006 11:44:12 PM  Akhilashwar K. Gupta
           CR-3861: Modified “depart()” method to put business day date into
           tender attributes.
      3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:24:37 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:13:37 PM  Robert Pearse   
     $
	 
	  Revision 1.6 -- By seema kumari --for  cash and credit refund

	 
     Revision 1.5  2004/08/12 20:46:35  bwf
     @scr 6567, 6069 No longer have to swipe debit or credit for return if original
                                 transaction tendered with one debit or credit.

     Revision 1.4  2004/05/03 20:50:41  aarvesen
     @scr 4626 fix for getting the unparseable --9.35 numbers

     Revision 1.3  2004/03/24 20:11:14  bwf
     @scr 3956 Code Review

     Revision 1.2  2004/02/12 16:48:22  mcs
     Forcing head revision

     Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.3   Jan 28 2004 13:52:34   epd
 * Adds chosen tender type as tender attributes attribute
 * 
 *    Rev 1.2   Jan 05 2004 16:25:02   nrao
 * Changed Tender_Options to Refund_Options to fix defect 3485.
 * 
 *    Rev 1.1   Nov 19 2003 14:10:52   epd
 * TDO refactoring to use factory
 * 
 *    Rev 1.0   Nov 04 2003 11:17:52   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:29:52   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:06:48   epd
 * Initial revision.
     
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

//import com._360commerce.tdo.services.tender.TenderOptionsTDO;
import java.math.BigDecimal;
import java.util.HashMap;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.tdo.RefundOptionsTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 *  Put up a configured UI screen 
 */
public class MAXRefundOptionsUISite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        // Create map for TDO
        HashMap attributeMap = new HashMap();
        attributeMap.put(RefundOptionsTDO.BUS, bus);
        attributeMap.put(RefundOptionsTDO.TRANSACTION, cargo.getCurrentTransactionADO());
        attributeMap.put(RefundOptionsTDO.ORIG_RETURN_TXNS, cargo.getOriginalReturnTxnADOs());
        
        // build bean model helper
        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.RefundOptions");
        }
        catch (TDOException tdoe)
        {
            logger.error("Problem creating Refund Options screen: " + tdoe.getMessage());
        }
        
        // display the configured tender options screen
        // get the UI manager

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.REFUND_OPTIONS, tdo.buildBeanModel(attributeMap));
    }
    
    /* (non-Javadoc)
     * At this point, we know the amount entered.  Save it in the tender
     * attributes.
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#depart(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void depart(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        
        // reset the tender attributes Map.  At this point
        // it is either no longer needed, or we have a new tender.
        cargo.resetTenderAttributes();
        cargo.setTenderADO(null);

        String letterName = bus.getCurrentLetter().getName();
        // save the entered amount in the tender attributes
        if (!letterName.equals("Undo") &&
            !letterName.equals("Clear") &&
            !letterName.equals("Cancel"))
        {
            HashMap attributes = cargo.getTenderAttributes();
            attributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.makeEnumFromString(letterName));
            // save the entered amount in the tender attributes
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            //Verify if the input is valid
            String amountStr;
            if (!Util.isEmpty(ui.getInput()))
            {
                amountStr = LocaleUtilities.parseNumber(ui.getInput(), LocaleConstantsIfc.USER_INTERFACE).toString();
                // make the amount negative if necessary
                // because this is a refund (or change) tender
                if (!amountStr.startsWith("-"))
                {
                    amountStr = "-" + amountStr;
                }
            }
            else
            {
                amountStr = new BigDecimal(0.00).toString();
            }

            cargo.getTenderAttributes().put(TenderConstants.AMOUNT, amountStr);

            if ((letterName.equals("Credit") ||
                 letterName.equals("Debit")) &&
                cargo.getNextTender() != null)
            {
                TenderADOIfc refundTender = cargo.getNextTender();
                HashMap tenderAttributes = refundTender.getTenderAttributes();
                tenderAttributes.put(TenderConstants.AMOUNT, amountStr);
                try
                {
                    refundTender.setTenderAttributes(tenderAttributes);
                    cargo.setNextTender(refundTender);
                }
                catch (TenderException te)
                {
                    logger.error(te);
                }
            }
			/*Rev 1.6 starts*/
            else if (letterName.equals("Cash"))
            {
            	cargo.setAccessFunctionID(MAXRoleFunctionIfc.CASH_REFUND);
            }
			 /*Rev 1.6 ends*/
        }
    }
}
