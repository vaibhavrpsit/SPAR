/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryTenderMenuBeanModel.java /main/15 2012/05/07 11:09:44 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  05/07/12 - Fortify, fix redundant null checks, take 4
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     5    360Commerce 1.4         7/9/2007 3:07:55 PM    Anda D. Cadar   I18N
 *           changes for CR 27494: POS 1st initialization when Server is
 *          offline
 *     4    360Commerce 1.3         4/25/2007 8:51:26 AM   Anda D. Cadar   I18N
 *           merge
 *     3    360Commerce 1.2         3/31/2005 4:30:16 PM   Robert Pearse   
 *     2    360Commerce 1.1         3/10/2005 10:25:40 AM  Robert Pearse   
 *     1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse   
 *    $
 *    Revision 1.6  2004/06/29 17:05:38  cdb
 *    @scr 4205 Removed merging of money orders into checks.
 *    Added ability to count money orders at till reconcile.
 *
 *    Revision 1.5  2004/06/07 18:29:37  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Add foreign currency counts.
 *
 *    Revision 1.4  2004/05/20 20:40:53  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Removed alternate tender from Select Tender screen and 
 *    corrected Select Charge screen.
 *
 *    Revision 1.3  2004/03/16 17:15:18  build
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 20:56:27  rhafernik
 *    @scr 0 Log4J conversion and code cleanup
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *    updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 16 2004 16:10:46   bwf
 * Added echeck.
 * Resolution for 3605: e-Checks are not listed on the Select Tender screen when closing Till
 * 
 *    Rev 1.0   Aug 29 2003 16:12:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.6   Jun 18 2003 12:56:14   bwf
 * Add tenderCurrencyCountryCode.
 * Resolution for 2613: Internationalization: try to print till summary report, POS client hangs up.
 * 
 *    Rev 1.5   Dec 09 2002 15:10:46   DCobb
 * Fixed expected amounts for alternate currencies.
 * Resolution for POS SCR-1852: Multiple defects on Till Close Select Tenders screen funtionality.
 * 
 *    Rev 1.4   Nov 27 2002 15:55:58   DCobb
 * Add Canadian Check tender.
 * Resolution for POS SCR-1842: POS 6.0 Canadian Check Tender
 * 
 *    Rev 1.3   Nov 26 2002 14:08:14   DCobb
 * Add Mall Certificate to form.
 * Resolution for POS SCR-1821: POS 6.0 Mall Gift Certificates
 *
 *    Rev 1.2   Nov 18 2002 13:42:40   kmorneau
 * added ui functionality for display of expected amounts for non-blind close
 * Resolution for 1824: Blind Close
 *
 *    Rev 1.1   Sep 19 2002 11:47:14   DCobb
 * Add Purchase Order tender type.
 * Resolution for POS SCR-1799: POS 5.5 Purchase Order Tender Package
 *
 *    Rev 1.0   Apr 29 2002 14:52:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:57:58   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 07 2002 14:53:02   mpm
 * Text externalization for till UI screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   04 Mar 2002 16:21:12   epd
 * Updates to accommodate use of TenderTypeMap class
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   Jan 19 2002 10:32:20   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   02 Jan 2002 15:37:32   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     Class description. <P>
     @version $Revision: /main/15 $
**/
//----------------------------------------------------------------------------
public class SummaryTenderMenuBeanModel extends POSBaseBeanModel
{                                       // begin class SummaryTenderMenuBeanModel
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/15 $";
    /**
        holds description and expected amount for each tender or charge
    **/
    protected SummaryCountBeanModel[] summaryCountBeanModel = null;   
    /** 
        Number of tenders to count 
    **/
    public static final int SUMMARY_COUNT_MAX = 13;
    /**
        blind close flag - expected to be true if expected amount fields are
        to be hidden
    **/
    protected boolean blindClose = false;
    
    /** Store instance of logger here **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.SummaryTenderMenuBeanModel.class);

    //---------------------------------------------------------------------
    /**
        Constructs SummaryTenderMenuBeanModel object. <P>
    **/
    //---------------------------------------------------------------------
    public SummaryTenderMenuBeanModel()
    {                                   // begin SummaryMenuBeanModel()

        TenderTypeMapIfc tenderMap = DomainGateway.getFactory().getTenderTypeMapInstance();

        // Setup the SummaryCountBeanModel with our tender list
        summaryCountBeanModel = new SummaryCountBeanModel[SUMMARY_COUNT_MAX];
        CurrencyIfc zero = null;
        try
        {
            zero = DomainGateway.getBaseCurrencyInstance();
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database/server may be offline");

        }

        summaryCountBeanModel[0] = new SummaryCountBeanModel();
        summaryCountBeanModel[0].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH));
        summaryCountBeanModel[0].setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        summaryCountBeanModel[0].setActionName("Cash");
        summaryCountBeanModel[0].setLabelTag("CashFieldLabel");
        summaryCountBeanModel[0].setLabel("Cash:");
        if(zero != null)
        {
            summaryCountBeanModel[0].setAmount(zero);
        }
        summaryCountBeanModel[0].setFieldDisabled(true);

        summaryCountBeanModel[1] = new SummaryCountBeanModel();
        summaryCountBeanModel[1].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK));
        summaryCountBeanModel[1].setTenderType(TenderLineItemIfc.TENDER_TYPE_CHECK);
        summaryCountBeanModel[1].setActionName("Check");
        summaryCountBeanModel[1].setLabelTag("CheckFieldLabel");
        summaryCountBeanModel[1].setLabel("Check:");
        
        if(zero != null)
        {
            summaryCountBeanModel[1].setAmount(zero);
        }
        summaryCountBeanModel[1].setFieldDisabled(true);            

        summaryCountBeanModel[2] = new SummaryCountBeanModel();
        summaryCountBeanModel[2].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_E_CHECK));
        summaryCountBeanModel[2].setTenderType(TenderLineItemIfc.TENDER_TYPE_E_CHECK);
        summaryCountBeanModel[2].setActionName("ECheck");
        summaryCountBeanModel[2].setLabelTag("ECheckFieldLabel");
        summaryCountBeanModel[2].setLabel("e-Check :");
        
        if(zero != null)
        {
            summaryCountBeanModel[2].setAmount(zero);
        }
        summaryCountBeanModel[2].setFieldDisabled(true);
        
        summaryCountBeanModel[3] = new SummaryCountBeanModel();
        summaryCountBeanModel[3].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHARGE));
        summaryCountBeanModel[3].setTenderType(TenderLineItemIfc.TENDER_TYPE_CHARGE);
        summaryCountBeanModel[3].setActionName("Credit");
        summaryCountBeanModel[3].setLabelTag("CreditFieldLabel");
        summaryCountBeanModel[3].setLabel("Credit:");
        
        if(zero != null)
        {
            summaryCountBeanModel[3].setAmount(zero);
        }
        summaryCountBeanModel[3].setFieldDisabled(true);

        summaryCountBeanModel[4] = new SummaryCountBeanModel();
        summaryCountBeanModel[4].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_DEBIT));
        summaryCountBeanModel[4].setTenderType(TenderLineItemIfc.TENDER_TYPE_DEBIT);
        summaryCountBeanModel[4].setActionName("Debit");
        summaryCountBeanModel[4].setLabelTag("DebitFieldLabel");
        summaryCountBeanModel[4].setLabel("Debit Card:");
        
        if(zero != null)
        {
            summaryCountBeanModel[4].setAmount(zero);
        }
        summaryCountBeanModel[4].setFieldDisabled(true);

        summaryCountBeanModel[5] = new SummaryCountBeanModel();
        summaryCountBeanModel[5].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_GIFT_CARD));
        summaryCountBeanModel[5].setTenderType(TenderLineItemIfc.TENDER_TYPE_GIFT_CARD);
        summaryCountBeanModel[5].setActionName("GiftCard");
        summaryCountBeanModel[5].setLabelTag("GiftCardFieldLabel");
        summaryCountBeanModel[5].setLabel("Gift Card:");
        if(zero != null)
        {
            summaryCountBeanModel[5].setAmount(zero);
        }
        summaryCountBeanModel[5].setFieldDisabled(true);

        summaryCountBeanModel[6] = new SummaryCountBeanModel();
        summaryCountBeanModel[6].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE));
        summaryCountBeanModel[6].setTenderType(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE);
        summaryCountBeanModel[6].setActionName("GiftCert");
        summaryCountBeanModel[6].setLabelTag("GiftCertificateFieldLabel");
        summaryCountBeanModel[6].setLabel("Gift Certificate:");
        
        if(zero != null)
        {
            summaryCountBeanModel[6].setAmount(zero);
        }
        summaryCountBeanModel[6].setFieldDisabled(true);

        summaryCountBeanModel[7] = new SummaryCountBeanModel();
        summaryCountBeanModel[7].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK));
        summaryCountBeanModel[7].setTenderType(TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK);
        summaryCountBeanModel[7].setActionName("TravelCheck");
        summaryCountBeanModel[7].setLabelTag("TravelersCheckFieldLabel");
        summaryCountBeanModel[7].setLabel("Travel Check:");
        
        if(zero != null)
        {
            summaryCountBeanModel[7].setAmount(zero);
        }
        summaryCountBeanModel[7].setFieldDisabled(true);

        summaryCountBeanModel[8] = new SummaryCountBeanModel();
        summaryCountBeanModel[8].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_COUPON));
        summaryCountBeanModel[8].setTenderType(TenderLineItemIfc.TENDER_TYPE_COUPON);
        summaryCountBeanModel[8].setActionName("Coupon");
        summaryCountBeanModel[8].setLabelTag("CouponFieldLabel");
        summaryCountBeanModel[8].setLabel("Coupon:");
        if(zero != null)
        {
            summaryCountBeanModel[8].setAmount(zero);
        }
        summaryCountBeanModel[8].setFieldDisabled(true);

        summaryCountBeanModel[9] = new SummaryCountBeanModel();
        summaryCountBeanModel[9].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT));
        summaryCountBeanModel[9].setTenderType(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT);
        summaryCountBeanModel[9].setActionName("StoreCredit");
        summaryCountBeanModel[9].setLabelTag("StoreCreditFieldLabel");
        summaryCountBeanModel[9].setLabel("Store Credit:");
        
        if(zero != null)
        {
            summaryCountBeanModel[9].setAmount(zero);
        }
        summaryCountBeanModel[9].setFieldDisabled(true);

        summaryCountBeanModel[10] = new SummaryCountBeanModel();
        summaryCountBeanModel[10].setDescription(tenderMap.getDescriptor(
                                             TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE));
        summaryCountBeanModel[10].setTenderType(
                                             TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE);
        summaryCountBeanModel[10].setActionName("MallCert");
        summaryCountBeanModel[10].setLabelTag("MallCertificateFieldLabel");
        summaryCountBeanModel[10].setLabel("Mall Certificate:");
        
        if(zero != null)
        {
            summaryCountBeanModel[10].setAmount(zero);
        }
        summaryCountBeanModel[10].setFieldDisabled(true);

        summaryCountBeanModel[11] = new SummaryCountBeanModel();
        summaryCountBeanModel[11].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER));
        summaryCountBeanModel[11].setTenderType(TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER);
        summaryCountBeanModel[11].setActionName("PurchaseOrder");
        summaryCountBeanModel[11].setLabelTag("PurchaseOrderFieldLabel");
        summaryCountBeanModel[11].setLabel("Purchase Order:");
        if(zero != null)
        {
            summaryCountBeanModel[11].setAmount(zero);
        }
        summaryCountBeanModel[11].setFieldDisabled(true);           

        summaryCountBeanModel[12] = new SummaryCountBeanModel();
        summaryCountBeanModel[12].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_MONEY_ORDER));
        summaryCountBeanModel[12].setTenderType(TenderLineItemIfc.TENDER_TYPE_MONEY_ORDER);
        summaryCountBeanModel[12].setActionName("MoneyOrder");
        summaryCountBeanModel[12].setLabelTag("MoneyOrderFieldLabel");
        summaryCountBeanModel[12].setLabel("NOT Money Order:");
        
        if(zero != null)
        {
            summaryCountBeanModel[12].setAmount(zero);
        }
        summaryCountBeanModel[12].setFieldDisabled(true);           
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves description and expected amount for each tender or charge. <P>
        @return holds description and expected amount for each tender or charge
    **/
    //----------------------------------------------------------------------------
    public SummaryCountBeanModel[] getSummaryCountBeanModel()
    {                                   // begin getSummaryCountBeanModel[]()
        return(summaryCountBeanModel);
    }                                   // end getSummaryCountBeanModel[]()

    //----------------------------------------------------------------------------
    /**
        Sets description and expected amount for each tender or charge. <P>
        @param value  holds description and expected amount for each tender or charge
    **/
    //----------------------------------------------------------------------------
    public void setSummaryCountBeanModel(SummaryCountBeanModel[] value)
    {                                   // begin setSummaryCountBeanModel[]()
        summaryCountBeanModel = value;
    }                                   // end setSummaryCountBeanModel[]()

    //----------------------------------------------------------------------
    /**
        Returns whether to hide expected amounts for a blind close.
        @return true if the expected amounts should be hidden
    **/
    //----------------------------------------------------------------------
    public boolean isBlindClose()
    {
        return blindClose;
    }

    //----------------------------------------------------------------------
    /**
        Sets the blind close flag.
        @param blind set to true to hide expected amount fields
    **/
    //----------------------------------------------------------------------
    public void setBlindClose(boolean blind)
    {
        blindClose = blind;
    }

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        String strResult = new String("Class:  SummaryTenderMenuBeanModel (Revision " +
                                      getRevisionNumber() +
                                      ") @" +
                                      hashCode());
        strResult += "\n";
        // add attributes to string
        if (summaryCountBeanModel == null)
        {
            strResult += "summaryCountBeanModel[]:            [null]";
        }
        else
        {
            for(int i = 0; i < summaryCountBeanModel.length; i++)
            {
                strResult += summaryCountBeanModel[i].toString();
            }
        }
        // pass back result
        return(strResult);
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        SummaryMenuBeanModel main method. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>toString() output
        </UL>
        @param args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        SummaryTenderMenuBeanModel c = new SummaryTenderMenuBeanModel();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class SummaryTenderMenuBeanModel
