/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/FrankTendersSite.java /main/18 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/06/12 - prevent npe when runnign without devices
 *    ohorne    06/03/10 - added support for franking external orders
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    blarsen   07/09/09 - XbranchMerge blarsen_bug-8654666 from
 *                         rgbustores_13.1x_branch
 *    blarsen   07/02/09 - echeck parameter in application.xml was recently
 *                         *fixed* to be consistent with bo and co
 *                         parameterset.xml files. Unfortunately, long ago, a
 *                         hack was placed in this class to work around the
 *                         *bad* parameter value. This fix changes the hack to
 *                         use the standard echeck value.
 *    blarsen   04/16/09 - Several post-void frankings were being added to the
 *                         franking list, even though they were not in the
 *                         parameter list. Added comment for mail-bank-checks
 *                         to clarify that functionality. Fixed several compile
 *                         warnings.
 *    acadar    03/27/09 - franking of void redeem gift certificate
 *    mahising  02/22/09 - Fixed issue to avoid printing of purchase order
 *                         franking during postvoid transaction
 *    mahising  02/19/09 - Fixed Decline Check Bug ID:2365
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         11/15/2007 11:17:28 AM Christian Greene
 *         Belize merge - check for new layaways
 *    5    360Commerce 1.4         4/25/2007 8:52:16 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         4/13/2007 3:11:22 PM   Peter J. Fierro
 *         CR3921 Merge from 7.2.2 to trunk -       4    .v7x      1.2.1.0
 *          8/1/2006 12:19:25 AM   K R. Kanagasabbai CR
 *         3921 : Send �print� letter for storecredit post void transaction.
 *
 *    3    360Commerce 1.2         3/31/2005 4:28:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:46 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:08 PM  Robert Pearse
 *
 *   Revision 1.15  2004/09/09 18:49:19  bwf
 *   @scr 7164 Fixed check franking to read as deposited check.
 *
 *   Revision 1.14  2004/07/19 14:14:41  khassen
 *   @scr 6236 - Removed unnecessary conditionals that were preventing certain tenders from being franked.
 *
 *   Revision 1.13  2004/07/13 16:01:50  jdeleau
 *   @scr 5841 Make sure already franked gift certificates dont
 *   get refranked on a printer error.
 *
 *   Revision 1.12  2004/05/25 15:12:39  blj
 *   @scr 5117 - fixed printing issues for redeem store credit
 *
 *   Revision 1.11  2004/05/12 17:49:16  kll
 *   @scr 4525: Franking of Mail Bank Checks
 *
 *   Revision 1.10  2004/04/30 22:46:13  tfritz
 *   @scr 4169 Added the FrankGiftCertificateIssued parameter
 *
 *   Revision 1.9  2004/04/30 13:48:11  aschenk
 *   @scr 4615 - When ever there was pre-printed store credit all tenders were Franked, even when they were not in the franking tender list.
 *
 *   Revision 1.8  2004/04/27 18:45:40  lzhao
 *   @scr 4553: Gift Certificate Franking
 *
 *   Revision 1.7  2004/04/15 20:56:18  blj
 *   @scr 3871 - updated to fix problems with void and offline.
 *
 *   Revision 1.6  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.5  2004/02/25 05:15:50  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.4  2004/02/21 02:07:51  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.3  2004/02/12 16:51:40  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.8   Feb 10 2004 14:41:26   bwf
 * Refactor Echeck.
 *
 *    Rev 1.7   Jan 20 2004 10:25:00   epd
 * fixed name of parameter
 *
 *    Rev 1.6   Dec 17 2003 15:30:08   nrao
 * Added franking capabilities for Purchase Order Tender.
 *
 *    Rev 1.5   Dec 16 2003 15:06:54   blj
 * added franking for store credit tenders
 *
 *    Rev 1.4   Dec 01 2003 14:08:24   bwf
 * Make sure franks for declines.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.3   Nov 24 2003 18:51:24   crain
 * Added post void for gift certificate
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.2   Nov 20 2003 18:22:06   bwf
 * Check franking
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.1   Oct 27 2003 15:42:36   blj
 * updated for money order tender franking
 *
 *    Rev 1.0   Aug 29 2003 16:05:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   Jul 08 2003 17:49:50   vxs
 * traversing all tenders and calling cargo.addTenderForFranking(tli) to add tenders in order as per the transaction.
 *
 *    Rev 1.5   Apr 09 2003 08:31:54   KLL
 * instanceof check to prevent classcast exception
 * Resolution for POS SCR-2084: Franking Functional Enhancements
 *
 *    Rev 1.4   Apr 04 2003 09:44:52   KLL
 * frank store coupons
 * Resolution for POS SCR-2084: Franking Functional Enhancements
 *
 *    Rev 1.3   Mar 12 2003 11:22:34   KLL
 * mall certificates, gift certificates and store credit franking
 * Resolution for POS SCR-2084: Franking Functional Enhancements
 *
 *    Rev 1.2   Nov 04 2002 11:41:58   DCobb
 * Add Mall Gift Certificate.
 * Resolution for POS SCR-1821: POS 6.0 Mall Gift Certificates
 *
 *    Rev 1.1   21 May 2002 16:17:32   baa
 * fix padding for franking
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.0   09 May 2002 20:00:10   baa
 * Initial revision.
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.1   22 Mar 2002 14:51:24   pdd
 * Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-1564: Remove uses of TenderLineItemIfc.TENDER_TYPE_DESCRIPTOR from pos
 *
 *    Rev 1.0   Mar 18 2002 11:44:26   msg
 * Initial revision.
 *
 *    Rev 1.4   01 Mar 2002 19:05:54   baa
 * fix franking multiple checks, halt /proceed options
 * Resolution for POS SCR-1362: Selecting No on Slip Printer Timeout retries franking, should not
 *
 *    Rev 1.3   25 Feb 2002 20:19:30   pdd
 * Fixed null pointer bug.
 * Resolution for POS SCR-1387: Changed the FrankingTenderList parameter to Traveler's Checks only, was prompted to frank on Check tender
 *
 *    Rev 1.1   Feb 21 2002 08:37:12   mpm
 * Added ability to designate printer as franking-capable
 * Resolution for POS SCR-1134: Bypass franking when printer is not franking-capable
 *
 *    Rev 1.0   Sep 21 2001 11:22:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderMailBankCheckIfc;
import oracle.retail.stores.domain.tender.TenderMoneyOrderIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * Mail letter to frank tender documents
 * 
 * @version $Revision: /main/18 $
 */
public class FrankTendersSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 2891475361799443195L;

    /** The name of this site. */
    public static final String SITENAME = "FrankTendersSite";

    /** Franking tender list parameter */
    protected static final String FRANKING_TENDER_LIST = "FrankingTenderList";
    protected static final String FRANKING_VOIDED_TENDER_LIST = "TendersToFrankOnPostVoid";

    /** Franking tender list values */
    protected static final String CHECK = "DepositedCheck";
    protected static final String TRAVCHECK = DomainGateway.getFactory().getTenderTypeMapInstance()
            .getDescriptor(TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK);
    protected static final String MALLCERT = DomainGateway.getFactory().getTenderTypeMapInstance()
            .getDescriptor(TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE);
    protected static final String STORECREDIT = DomainGateway.getFactory().getTenderTypeMapInstance()
            .getDescriptor(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT);
    protected static final String GIFTCERT = DomainGateway.getFactory().getTenderTypeMapInstance()
            .getDescriptor(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE);
    protected static final String STORECOUPON = DomainGateway.getFactory().getTenderTypeMapInstance()
            .getDescriptor(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE);
    protected static final String PURCHASEORDER = DomainGateway.getFactory().getTenderTypeMapInstance()
            .getDescriptor(TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER);
    protected static final String MAIL_BANK_CHECK = DomainGateway.getFactory().getTenderTypeMapInstance()
            .getDescriptor(TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK);
    protected static final String ECHECK = DomainGateway.getFactory().getTenderTypeMapInstance()
            .getDescriptor(TenderLineItemIfc.TENDER_TYPE_E_CHECK);

    /**
     * Print endorsements if needed.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        String letter = new String(CommonLetterIfc.DONE);
        // see if printer can frank
        boolean frankingCapable = isPrinterFrankingCapable(bus);

        try
        {
            String[] frankConfig = null;
            // get the appropriate franking list
            if (cargo.getTransType() == TransactionIfc.TYPE_VOID)
            {
                frankConfig = pm.getStringValues(FRANKING_VOIDED_TENDER_LIST);
            }
            else
            {
                frankConfig = pm.getStringValues(FRANKING_TENDER_LIST);
            }

            // If franking is configured, it's not a void and there are
            if (frankingCapable
                && frankConfig != null
                && frankNone(frankConfig) == false
                && frankConfig.length != 0)
            {
                ArrayList<String> frankingSettings = new ArrayList<String>(Arrays.asList(frankConfig));
                // check if there are any checks or travel checks that need to be franked.
                boolean inclChecks = cargo.includesCheck();
                boolean inclEChecks = cargo.includesECheck();
                boolean inclTravChecks = cargo.includesTravelerChecks();
                boolean inclMallCerts = cargo.includesMallCertificate();
                boolean inclStoreCreditRedeem = cargo.includesStoreCreditRedeem();
                boolean inclGiftCertsRedeem = cargo.includesGiftCertificateRedeem();
                boolean inclGiftCerts = cargo.includesGiftCertificate();
                boolean inclStoreCredit = cargo.includesStoreCredit();
                boolean inclStoreCoupons = false;
                boolean inclMoneyOrders = false;
                boolean inclPreprintedStoreCredit = false;
                boolean inclPurchaseOrders = false;
                boolean frankGiftCertIssued = true;
                boolean inclMailBankChecks = false;
                boolean frankExternalOrderConfig = true;
                boolean isExternalOrderTransaction = false;

                frankGiftCertIssued = pm.getStringValue("FrankGiftCertificateIssued").equalsIgnoreCase("Y");

                frankExternalOrderConfig = pm.getStringValue("FrankExternalOrder").equalsIgnoreCase("Y");

                TenderableTransactionIfc trans = cargo.getTenderableTransaction();

                // Coupons are valid during Sales, Exchanges and Layaway Initiate (new)
                if (trans instanceof LayawayTransactionIfc)
                {
                    if (((LayawayTransactionIfc)trans).getLayaway().getStatus() == LayawayConstantsIfc.STATUS_NEW)
                        inclStoreCoupons = cargo.includesStoreCoupon(trans);
                }
                else if (trans instanceof SaleReturnTransactionIfc)
                {
                    inclStoreCoupons = cargo.includesStoreCoupon(trans);
                    isExternalOrderTransaction = ((SaleReturnTransactionIfc)trans).hasExternalOrder();
                }

                // Check to see if preprinted store credit parameter is set to yes
                // if so we need to frank the preprinted store credit form.
                String prePrintedSC = pm.getStringValue("PrePrintedStoreCredit");
                if (prePrintedSC.equals("Y") && inclStoreCredit)
                {
                    inclPreprintedStoreCredit = true;
                }

                // Add tenders to frank
                Vector<TenderLineItemIfc> tenderLineItems = cargo.getTransaction().getTenderLineItemsVector();
                // Make sure we have tender items
                if (tenderLineItems != null)
                {
                    Enumeration<TenderLineItemIfc> e = tenderLineItems.elements();
                    TenderLineItemIfc tli = null;
                    int typeCode;
                    while (e.hasMoreElements())
                    {
                        tli = e.nextElement();
                        typeCode = tli.getTypeCode();
                        if ((tli instanceof TenderTravelersCheckIfc ||
                            tli instanceof TenderCheckIfc ||
                            typeCode == TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE ||
                            (typeCode == TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT
                                && ((TenderStoreCreditIfc)tli).getState().equals(TenderStoreCreditIfc.REDEEM)) ||
                            (typeCode == TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT
                                && inclPreprintedStoreCredit) ||
                            (typeCode == TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE &&
                                    (tli.getAmountTender().signum() != CurrencyIfc.NEGATIVE ||
                                    (tli.getAmountTender().signum() == CurrencyIfc.NEGATIVE &&
                                            frankGiftCertIssued)))))
                        {
                            cargo.addTenderForFranking(tli);
                        }
                        // if this is a check and
                        // this is a check and checks are allowed
                        // or this is an echeck and echecks are allowed
                        else if((tli instanceof TenderCheckIfc) &&
                                 ((((TenderCheckIfc)tli).getTypeCode() == TenderLineItemConstantsIfc.TENDER_TYPE_CHECK &&
                                    frankingSettings.contains(CHECK)) ||
                                  ((TenderCheckIfc)tli).getTypeCode() == TenderLineItemConstantsIfc.TENDER_TYPE_E_CHECK &&
                                    frankingSettings.contains(ECHECK))
                                    || (tli instanceof TenderGiftCertificateIfc))
                        {
                            cargo.addTenderForFranking(tli);
                        }

                        /**
                         * Money order tenders are franked
                         */
                        if (tli instanceof TenderMoneyOrderIfc)
                        {
                            String moneyOrder = TenderLineItemIfc.TENDER_LINEDISPLAY_DESC[TenderLineItemIfc.TENDER_TYPE_MONEY_ORDER];
                            if (frankingSettings.contains(moneyOrder))
                            {
                                cargo.addTenderForFranking(tli);
                                inclMoneyOrders = true;
                            }
                        }

                        // Purchase Order Tenders are franked
                        if (tli instanceof TenderPurchaseOrderIfc)
                        {
                            String purchaseOrder = TenderLineItemIfc.TENDER_LINEDISPLAY_DESC[TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER];
                            if (frankingSettings.contains(purchaseOrder))
                            {
                                cargo.addTenderForFranking(tli);
                                inclPurchaseOrders = true;
                            }
                        }

                        /* Mail Bank Checks (MBC)
                         * 
                         * MBCs are not franked.
                         * 
                         * The MBC franking was added for GAP to frank a form the user
                         * fills out when an MBC is issued for refunds.
                         * 
                         * This capability is customer specific.  However, since it is controlled
                         * by a parameter, it was decided to keep it in base product.
                         * (Printing->Franking Tender List->Mail Bank Check)
                         * 
                         * Note that the "Printing->Tenders To Frank on Post Void" parameter list
                         * does not include MBCs.  
                         * 
                         */
                        if (tli instanceof TenderMailBankCheckIfc)
                        {
                            String mailBankCheck = TenderLineItemIfc.TENDER_LINEDISPLAY_DESC[TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK];
                            if (frankingSettings.contains(mailBankCheck))
                            {
                                cargo.addTenderForFranking(tli);
                                inclMailBankChecks = true;
                            }
                        }
                    }
                }// done adding tenders to frank

                if (trans.getTransactionType() == TransactionIfc.TYPE_REDEEM
                        && (inclStoreCreditRedeem || inclGiftCertsRedeem))
                {
                    cargo.addTenderForFranking(((RedeemTransactionIfc)trans).getRedeemTender());
                }

                if (trans.getTransactionType() == TransactionIfc.TYPE_VOID && inclStoreCreditRedeem)
                {
                    if (((VoidTransactionIfc)trans).getOriginalTransaction() instanceof RedeemTransactionIfc)
                    {
                        TenderableTransactionIfc origTrans = ((VoidTransactionIfc)trans).getOriginalTransaction();
                        cargo.addTenderForFranking(((RedeemTransactionIfc)origTrans).getRedeemTender());
                    }
                }

                if (trans.getTransactionType() == TransactionIfc.TYPE_VOID && inclGiftCertsRedeem)
                {
                    if (((VoidTransactionIfc)trans).getOriginalTransaction() instanceof RedeemTransactionIfc)
                    {
                        TenderableTransactionIfc origTrans = ((VoidTransactionIfc)trans).getOriginalTransaction();
                        cargo.addTenderForFranking(((RedeemTransactionIfc)origTrans).getRedeemTender());
                    }
                }

                // if not a void transaction get declined echecks
                boolean inclDeclEChecks = false;

                // Add gift certificates issued to frank if Parameter set to YES
                if (frankGiftCertIssued)
                {
                    if (cargo.getTransaction() instanceof RetailTransactionIfc)
                    {
                        Vector<AbstractTransactionLineItemIfc> lineItems = ((RetailTransactionIfc)cargo
                                .getTransaction()).getLineItemsVector();
                        // Make sure we have line items
                        if (lineItems != null)
                        {
                            Enumeration<AbstractTransactionLineItemIfc> e = lineItems.elements();
                            SaleReturnLineItemIfc sli = null;
                            while (e.hasMoreElements())
                            {
                                sli = (SaleReturnLineItemIfc)e.nextElement();
                                if (sli.getPLUItem() instanceof GiftCertificateItemIfc)
                                {
                                    inclGiftCerts = true;
                                    cargo.addGiftCertificateForFranking(sli);
                                }
                            }
                        }
                    }
                }

                if ((inclChecks && frankingSettings.contains(CHECK))
                     || (inclEChecks && frankingSettings.contains(ECHECK))
                     || (inclDeclEChecks && frankingSettings.contains(ECHECK))
                     || (inclTravChecks && frankingSettings.contains(TRAVCHECK))
                     || (inclMallCerts  && frankingSettings.contains(MALLCERT))
                     || (inclStoreCreditRedeem  && frankingSettings.contains(STORECREDIT))
                     || (inclPreprintedStoreCredit  && frankingSettings.contains(STORECREDIT))
                     || (inclGiftCerts  && frankingSettings.contains(GIFTCERT))
                     || (inclGiftCertsRedeem  && frankingSettings.contains(GIFTCERT))
                     || (inclMoneyOrders)
                     || (inclStoreCoupons) && frankingSettings.contains("StoreCoupon")
                     || (inclPurchaseOrders)
                     || (inclMailBankChecks)
                     || (inclStoreCredit) &&(frankingSettings.contains(STORECREDIT))
                     || (isExternalOrderTransaction && frankExternalOrderConfig))
                {
                    letter = "Print";
                }
                else
                {
                    Vector<TenderLineItemIfc> tenders = cargo.getTendersToFrank();
                    if (tenders != null)
                    {
                        tenders.removeAllElements();
                    }
                }
            }
        }
        catch (ParameterException pe)
        {
            logger.error("" + Util.throwableToString(pe) + "");
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    /**
     * Determines if printer is franking-capable.
     * 
     * @param bus instance of bus
     * @return true if printer is franking-capable, false otherwise.
     */
    protected boolean isPrinterFrankingCapable(BusIfc bus)
    {
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);

        try
        {
            Boolean frankingCapable = (Boolean)pda.isFrankingCapable();
            if (frankingCapable != null)
            {
                return frankingCapable;
            }
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to determine if printer is capable of franking.", e);
        }
        return false;
    }

    /**
     * Determines if list of tenders to frank includes "none".
     * 
     * @param frankParams array of tender types (as strings)
     * @return true if list includes "none", false otherwise
     */
    protected boolean frankNone(String[] frankParams)
    {
        boolean retValue = false;

        for (int i = 0; i < frankParams.length; i++)
        {
            if (frankParams[i].equalsIgnoreCase("none"))
            {
                retValue = true;
                break;
            }
        }

        return retValue;
    }

    /**
     * Adds declined echecks.
     * 
     * @param bus
     * @return
     */
    protected boolean includeDeclinedEChecks(BusIfc bus)
    {
        boolean returnBool = false;
        Vector<TenderLineItemIfc> declinedEChecks = null;
        PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTenderableTransaction();

        if (trans instanceof SaleReturnTransactionIfc)
        {
            declinedEChecks = ((SaleReturnTransactionIfc)trans).getECheckDeclinedItems();
        }
        if (declinedEChecks != null)
        {
            Enumeration<TenderLineItemIfc> e = declinedEChecks.elements();
            TenderLineItemIfc tli = null;

            // See if there are any checks
            while (e.hasMoreElements())
            {
                tli = e.nextElement();
                if (tli instanceof TenderCheckIfc)
                {
                    cargo.addTenderForFranking(tli);
                    returnBool = true;
                }
            }
        }
        return returnBool;
    }
}
