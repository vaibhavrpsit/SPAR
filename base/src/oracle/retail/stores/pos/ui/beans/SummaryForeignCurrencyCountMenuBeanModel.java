/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryForeignCurrencyCountMenuBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     6    360Commerce 1.5         6/11/2007 11:51:27 AM  Anda D. Cadar   SCR
 *          27206: replace getNationality with getCountryCode; Nationality
 *          column in co_cny was poulated previosly with the value for the
 *          country code. I18N change was to populate nationality with
 *          nationality value
 *     5    360Commerce 1.4         5/8/2007 11:32:29 AM   Anda D. Cadar
 *          currency changes for I18N
 *     4    360Commerce 1.3         4/24/2007 1:16:09 PM   Charles D. Baker CR
 *          26556 - I18N Code Merge.
 *     3    360Commerce 1.2         3/31/2005 4:30:15 PM   Robert Pearse   
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

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.utility.Util;

import org.apache.log4j.Logger;

//----------------------------------------------------------------------------
/**
     Models the Foreign Currency Counts. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class SummaryForeignCurrencyCountMenuBeanModel extends POSBaseBeanModel
{                                       // begin class SummaryForeignCurrencyCountMenuBeanModel
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        prepend to country code for label tag
    **/
    public static final String LABEL_TAG_PREFIX = "ForeignCurrency";
    /**
        holds description and expected amount for each tender or charge
    **/
    protected SummaryCountBeanModel[] summaryCountBeanModel = null;
    /**
        blind close flag - true if expected amount fields are to be hidden
    **/
    protected boolean blindClose = false;

    //---------------------------------------------------------------------
    /**
        Constructs SummaryForeignTenderMenuBeanModel object. <P>
    **/
    //---------------------------------------------------------------------
    public SummaryForeignCurrencyCountMenuBeanModel()
    {                                   // begin SummaryForeignCurrencyCountMenuBeanModel()
        CurrencyTypeIfc[]   altCurrencies = DomainGateway.getAlternateCurrencyTypes();
        // Setup the SummaryCountBeanModel with alternate currencies list
        initSummaryCountBeanModel(altCurrencies);
    }                                   // end SummaryForeignCurrencyCountMenuBeanModel()

    //---------------------------------------------------------------------
    /**
        Constructs SummaryForeignTenderMenuBeanModel object. <P>
        @param altCurrencies  The list of foreign currencies
    **/
    //---------------------------------------------------------------------
    public SummaryForeignCurrencyCountMenuBeanModel(CurrencyTypeIfc[] altCurrencies)
    {                                   // begin SummaryForeignCurrencyCountMenuBeanModel()
        // Setup the SummaryCountBeanModel with our tender list
        initSummaryCountBeanModel(altCurrencies);
    }                                   // end SummaryForeignCurrencyCountMenuBeanModel()

    //---------------------------------------------------------------------
    /**
        Creates the summary count bean model array from the alternate
        currency type array
        @param altCurrencies CurrencyTypeIfc[] The alternate currency type array.
    **/
    //---------------------------------------------------------------------
    protected void initSummaryCountBeanModel(CurrencyTypeIfc[] altCurrencies)
    {                                   // end initSummaryCountBeanModel()
        // Determine the length of the summary count bean model array
        summaryCountBeanModel = new SummaryCountBeanModel[altCurrencies.length];

        for (int i = 0; i < altCurrencies.length; i++)
        {
            CurrencyTypeIfc currentCurrencyType = altCurrencies[i];
            try
            {
              //  CurrencyIfc zero = (CurrencyIfc)currentCurrencyType.getCurrencyInstance();

                CurrencyIfc zero = CurrencyServiceLocator.getCurrencyService().createCurrency(currentCurrencyType);

                summaryCountBeanModel[i] = new SummaryCountBeanModel();
                String countryCode = currentCurrencyType.getCountryCode();
                String currencyCode = currentCurrencyType.getCurrencyCode();
                summaryCountBeanModel[i].setActionName(currencyCode);
                summaryCountBeanModel[i].setDescription(currencyCode);
                summaryCountBeanModel[i].setTenderType(TenderLineItemIfc.TENDER_TYPE_UNKNOWN);
                summaryCountBeanModel[i].setLabel(countryCode);
                summaryCountBeanModel[i].setLabelTag(LABEL_TAG_PREFIX + countryCode);
                summaryCountBeanModel[i].setAmount(zero);
            }
            catch (Exception e)
            {
                Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.SummaryForeignCurrencyCountMenuBeanModel.class);
                logger.warn( "SummaryForeignCurrencyCountMenuBeanModel.initSummaryCountBeanModel() could not instantiate the currency amount for "
                             + currentCurrencyType.getCurrencyCode());
            }
        }

    }                                   // end initSummaryCountBeanModel()

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
        String strResult = new String("Class:  SummaryForeignCurrencyCountMenuBeanModel (Revision " +
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
        SummaryForeignCurrencyCountMenuBeanModel c = new SummaryForeignCurrencyCountMenuBeanModel();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class SummaryForeignCurrencyCountMenuBeanModel
