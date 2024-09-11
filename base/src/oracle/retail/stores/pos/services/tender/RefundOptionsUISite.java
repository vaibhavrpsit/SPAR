/* =============================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/RefundOptionsUISite.java /main/23 2013/09/05 10:36:16 abondala Exp $
 * =============================================================================
 * NOTES
 *
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    sgu       10/17/11 - prompt for card swipe or manual entry once card
 *                         tender buttons are clicked
 *    cgreene   09/22/11 - formatting and correct use of booloean parameter
 *    sgu       09/08/11 - add house account as a refund tender
 *    cgreene   07/12/11 - update generics
 *    sgu       02/03/11 - check in all
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    nkgautam  03/26/09 - fixed the logic for comparison of useRMTender
 *                         parameter
 *    mdecama   12/22/08 - Using constant RM_REFUND_OPTIONS for the Returns
 *                         Management Refund Options screen and clean ups.
 *    abondala  12/02/08 - RM-POS integration
 *    rkar      11/12/08 - Adds/changes for POS-RM integration
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
     $Log:
      7    360Commerce 1.6         6/7/2008 1:32:20 PM    Maisa De Camargo CR
           31953 - Validating the tender amount. Code Reviewed by Alan Sinton.
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
           CR-3861: Modified depart() method to put business day date into
           tender attributes.
      3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:37 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:37 PM  Robert Pearse
     $
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
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.math.BigDecimal;
import java.util.HashMap;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.tdo.RefundOptionsTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.manager.rm.RPIFinalResultIfc;
import oracle.retail.stores.domain.manager.rm.RPIManagerIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.utility.RMUtility;

/**
 * Put up a configured UI screen
 */
public class RefundOptionsUISite extends PosSiteActionAdapter
{
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = 1338684144602926798L;
    
    public final static String USE_RM_TENDERS = "UseRMTenders";
    public final static String REFUND_TDO    = "tdo.tender.RefundOptions";
    public final static String RM_REFUND_TDO = "tdo.tender.RMRefundOptions";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.setRefundToOriginalCard(false);

        // Create map for the TDO
        HashMap<String, Object> attributeMap = new HashMap<String, Object>(4);
        attributeMap.put(RefundOptionsTDO.BUS, bus);
        attributeMap.put(RefundOptionsTDO.TRANSACTION, cargo.getCurrentTransactionADO());
        attributeMap.put(RefundOptionsTDO.ORIG_RETURN_TXNS, cargo.getOriginalReturnTxnADOs());

        String tdoName    = REFUND_TDO;
        String screenName = POSUIManagerIfc.REFUND_OPTIONS;
        if(useRMConfiguredTenders(bus, cargo, attributeMap))
        {
            tdoName    = RM_REFUND_TDO;
            screenName = POSUIManagerIfc.RM_REFUND_OPTIONS;
            attributeMap.put(RefundOptionsTDO.RETURN_RESPONSE, cargo.getReturnResponse());
        }
        
        // build bean model helper
        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc)TDOFactory.create(tdoName);
        }
        catch (TDOException tdoe)
        {
            logger.error("Problem creating Refund Options screen: " + tdoe.getMessage());
        }

        // display the configured tender options screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(screenName, tdo.buildBeanModel(attributeMap));
    }

    /*
     * This method determines if the 
     */
    private boolean useRMConfiguredTenders(BusIfc bus, TenderCargo cargo, HashMap<String, Object> attributeMap)
    {
        // Initialize the return value
        boolean useRM = false;
        
        // Get the use RM paramater
        ParameterManagerIfc paramManager = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            useRM = paramManager.getBooleanValue(USE_RM_TENDERS);
        }
        catch (ParameterException e)
        {
            logger.error(e);
        }
        
        // If this ugly code to determine if the manager override button has
        // been pressed.
        if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.SUCCESS))
        {
            cargo.setRefundTenderOverrideApproval(true);
        }

        // If the application is configured to use RM, the application will
        // still use the stand alone method to determine the return tenders
        // if there is no RM response or if the manager override has been
        // selected.
        if (useRM)
        {
            if (cargo.getReturnResponse() == null || cargo.isRefundTenderOverrideApproval())
            {
                // display refund tender options
                // without using RM or tender override from manager approval
                if (attributeMap.get(RefundOptionsTDO.RETURN_RESPONSE) != null)
                    attributeMap.remove(RefundOptionsTDO.RETURN_RESPONSE);
                useRM = false;
            }
        }
        return useRM;
    }

    /**
     * At this point, we know the amount entered. Save it in the tender attributes.
     *
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
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
            HashMap<String,Object> attributes = cargo.getTenderAttributes();
            attributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.makeEnumFromString(letterName));
            // save the entered amount in the tender attributes
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            // Verify if the input is valid
            String amountStr;
            if (!Util.isEmpty(ui.getInput()))
            {
                amountStr = LocaleUtilities.parseCurrency(ui.getInput(), LocaleMap.getLocale(LocaleMap.DEFAULT)).toString();
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
                 letterName.equals("HouseAccount") ||
                 letterName.equals("Debit") ||
                 letterName.equals("GiftCard")) &&
                 cargo.getNextTender() != null)
            {
                TenderADOIfc refundTender = cargo.getNextTender();
                HashMap<String,Object> tenderAttributes = refundTender.getTenderAttributes();
                tenderAttributes.put(TenderConstants.AMOUNT, amountStr);
                try
                {
                    refundTender.setTenderAttributes(tenderAttributes);
                    cargo.setNextTender(refundTender);
                    cargo.setTenderAttributes(tenderAttributes);
                }
                catch (TenderException te)
                {
                    logger.error(te);
                }
            }
        }

        // send void return result to RM
        if ( ( cargo.getReturnRequest() != null ) &&  //make sure RPI is enabled
             ( letterName.equals(CommonLetterIfc.UNDO) ||
               letterName.equals("Clear") ||
               letterName.equals(CommonLetterIfc.CANCEL) ) )
        {
            RPIFinalResultIfc rpiFinalResult = null;
            if (cargo.getReturnResponse() == null)
            {
                // void final result
                rpiFinalResult = RMUtility.getInstance().getOfflineFinalResult(cargo.getTransaction(),
                        cargo.getReturnRequest(), true);
            }
            else
            {
                // void final result
                rpiFinalResult = RMUtility.getInstance().getFinalResult(cargo.getTransaction(),
                        cargo.getReturnRequest(), cargo.getReturnResponse(), true);
            }

            cargo.setReturnResult(rpiFinalResult);
            cargo.setRefundTenderOverrideApproval(false);

            try
            {
                RPIManagerIfc rpiReturnsManager =
                    (RPIManagerIfc)Gateway.getDispatcher().getManager(RPIManagerIfc.TYPE);

                rpiReturnsManager.sendReturnsFinalResult(rpiFinalResult);
                cargo.getTransaction().setReturnTicket(rpiFinalResult.getTicketID());

                JournalManagerIfc journal;
                journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
                journal.journal("Send void final result, return ticket = " + rpiFinalResult.getTicketID());
            }
            catch (Exception e)
            {
                logger.error("Unable to send void final result. " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
