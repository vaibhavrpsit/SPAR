/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryForeignTenderMenuBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:50 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     5    360Commerce 1.4         7/9/2007 3:07:54 PM    Anda D. Cadar   I18N
 *           changes for CR 27494: POS 1st initialization when Server is
 *          offline
 *     4    360Commerce 1.3         4/25/2007 8:51:26 AM   Anda D. Cadar   I18N
 *           merge
 *     3    360Commerce 1.2         3/31/2005 4:30:16 PM   Robert Pearse   
 *     2    360Commerce 1.1         3/10/2005 10:25:40 AM  Robert Pearse   
 *     1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse   
 *    $
 *    Revision 1.1  2004/06/07 18:29:37  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Add foreign currency counts.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     The bean model for the Summary Foreign Tender menu bean. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class SummaryForeignTenderMenuBeanModel extends POSBaseBeanModel
{                                       // begin class SummaryForeignTenderMenuBeanModel
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        holds description and expected amount for each tender
    **/
    protected SummaryCountBeanModel[] summaryCountBeanModel = null;
    /**
        Tender Currency Country Code     
     **/
    protected String tenderCurrencyCountryCode = "CA";    
    /** 
        Number of tenders to count
    **/
    public static final int SUMMARY_COUNT_MAX = 5;
    /** 
        blind close flag - true if expected amount fields are to be hidden 
    **/
    protected boolean blindClose = false;
    /**
        tenders to count
    **/
    protected String[] tendersToCount = {"Cash", "Check", "TravelCheck", "GiftCert", "StoreCredit"};
    /**
        tenders accepted
    **/
    protected String[] tendersAccepted = {"Cash", "Check", "TravCheck", "GiftCert", "StoreCredit"};
    
    /** Store instance of logger here **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.SummaryForeignTenderMenuBeanModel.class);
    
    
    //---------------------------------------------------------------------
    /**
        Constructs SummaryForeignTenderMenuBeanModel object. <P>
    **/
    //---------------------------------------------------------------------
    public SummaryForeignTenderMenuBeanModel()
    {                                   // begin SummaryForeignMenuBeanModel()

        TenderTypeMapIfc tenderMap = DomainGateway.getFactory().getTenderTypeMapInstance();

        // Setup the SummaryCountBeanModel with our tender list
        summaryCountBeanModel = new SummaryCountBeanModel[SUMMARY_COUNT_MAX];
        CurrencyIfc zero = null;
        try
        {
            zero = DomainGateway.getAlternateCurrencyInstance(getTenderCurrencyCountryCode());
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database or server may be offline, using default number of fraction digits", e);
        }


        summaryCountBeanModel[0] = new SummaryCountBeanModel();
        summaryCountBeanModel[0].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH));
        summaryCountBeanModel[0].setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        summaryCountBeanModel[0].setActionName("Cash");
        summaryCountBeanModel[0].setLabelTag("CashFieldLabel");
        summaryCountBeanModel[0].setLabel("Cash:");
        if (zero != null)
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
        summaryCountBeanModel[2].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK));
        summaryCountBeanModel[2].setTenderType(TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK);
        summaryCountBeanModel[2].setActionName("TravelCheck");
        summaryCountBeanModel[2].setLabelTag("TravelersCheckFieldLabel");
        summaryCountBeanModel[2].setLabel("Travel Check:");
        if(zero != null)
        {
            summaryCountBeanModel[2].setAmount(zero);
        }
        summaryCountBeanModel[2].setFieldDisabled(true);        

        summaryCountBeanModel[3] = new SummaryCountBeanModel();
        summaryCountBeanModel[3].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE));
        summaryCountBeanModel[3].setTenderType(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE);
        summaryCountBeanModel[3].setActionName("GiftCert");
        summaryCountBeanModel[3].setLabelTag("GiftCertificateFieldLabel");
        summaryCountBeanModel[3].setLabel("Gift Certificate:");
        
        if(zero != null)
        {
            summaryCountBeanModel[3].setAmount(zero);
        }
        summaryCountBeanModel[3].setFieldDisabled(true);

        summaryCountBeanModel[4] = new SummaryCountBeanModel();
        summaryCountBeanModel[4].setDescription(tenderMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT));
        summaryCountBeanModel[4].setTenderType(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT);
        summaryCountBeanModel[4].setActionName("StoreCredit");
        summaryCountBeanModel[4].setLabelTag("StoreCreditFieldLabel");
        summaryCountBeanModel[4].setLabel("Store Credit:");
        
        if(zero != null)
        {
            summaryCountBeanModel[4].setAmount(zero);
        }
        summaryCountBeanModel[4].setFieldDisabled(true);           
    }                                   // end SummaryForeignMenuBeanModel()

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

    //----------------------------------------------------------------------------
    /**
        Retrieves tender currency country code. <P>
        @return currency country code
    **/
    //----------------------------------------------------------------------------
    public String getTenderCurrencyCountryCode()
    {                                   // begin getTenderCurrencyCountryCode()
        return(tenderCurrencyCountryCode);
    }                                   // end getTenderCurrencyCountryCode()

    //----------------------------------------------------------------------------
    /**
        Sets tender currency country code. <P>
        @param value  currency country code
    **/
    //----------------------------------------------------------------------------
    public void setTenderCurrencyCountryCode(String value)
    {                                   // begin setTenderCurrencyCountryCode()
        tenderCurrencyCountryCode = value;
    }                                   // end setTenderCurrencyCountryCode()

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
    
    //----------------------------------------------------------------------
    /**
        Returns teh list of tenders to count.
        @return String[]  The list of tenders to count
    **/
    //----------------------------------------------------------------------
    public String[] getTendersToCount()
    {
        return tendersToCount;
    }

    //----------------------------------------------------------------------
    /**
        Sets the list of tenders to count.
        @param value  The list of tenders to count.
    **/
    //----------------------------------------------------------------------
    public void setTendersToCount(String[] value)
    {
        tendersToCount = value;
    }
    
    //----------------------------------------------------------------------
    /**
        Returns teh list of tenders to count.
        @return String[]  The list of tenders to count
    **/
    //----------------------------------------------------------------------
    public String[] getTendersAccepted()
    {
        return tendersAccepted;
    }

    //----------------------------------------------------------------------
    /**
        Sets the list of tenders to count.
        @param value  The list of tenders to count.
    **/
    //----------------------------------------------------------------------
    public void setTendersAccepted(String[] value)
    {
        tendersAccepted = value;
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
        String strResult = new String("Class:  SummaryForeignTenderMenuBeanModel (Revision " +
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
        SummaryForeignTenderMenuBeanModel main method. <P>
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
        SummaryForeignTenderMenuBeanModel c = new SummaryForeignTenderMenuBeanModel();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class SummaryForeignTenderMenuBeanModel
