/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryChargeMenuBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:39 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     5    360Commerce 1.4         7/9/2007 3:07:53 PM    Anda D. Cadar   I18N
 *           changes for CR 27494: POS 1st initialization when Server is
 *          offline
 *     4    360Commerce 1.3         4/25/2007 8:51:27 AM   Anda D. Cadar   I18N
 *           merge
 *     3    360Commerce 1.2         3/31/2005 4:30:15 PM   Robert Pearse   
 *     2    360Commerce 1.1         3/10/2005 10:25:39 AM  Robert Pearse   
 *     1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse   
 *    $
 *    Revision 1.5  2004/05/26 21:21:52  dcobb
 *    @scr 4302 Correct compiler warnings
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
 *    Rev 1.0   Aug 29 2003 16:12:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.5   Apr 18 2003 17:14:02   baa
 * fix buttons on screen
 * Resolution for POS SCR-2170: Missing property names in bundles
 * 
 *    Rev 1.4   Apr 18 2003 09:42:38   baa
 * fixes to bundles
 * Resolution for POS SCR-2170: Missing property names in bundles
 * 
 *    Rev 1.3   Apr 10 2003 17:20:28   bwf
 * To remove instance of UtilityManagerIfc reverted back to old version of constructor and deprecated.  Then formed new constructor that takes in a list of cardTypes.
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.2   Nov 18 2002 13:42:42   kmorneau
 * added ui functionality for display of expected amounts for non-blind close
 * Resolution for 1824: Blind Close
 * 
 *    Rev 1.1   Sep 23 2002 12:04:34   kmorneau
 * added dynamic configuration using a configured CardType
 * Resolution for 1815: Credit Card Types Accepted
 * 
 *    Rev 1.0   Apr 29 2002 14:51:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:57:50   msg
 * Initial revision.
 * 
 *    Rev 1.3   04 Mar 2002 16:21:10   epd
 * Updates to accommodate use of TenderTypeMap class
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.2   Feb 17 2002 16:27:38   mpm
 * Modified to use object factory to implement CardType.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.1   Jan 19 2002 10:32:16   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   02 Jan 2002 15:37:30   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.Card;
import oracle.retail.stores.domain.utility.CardType;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     Class description. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class SummaryChargeMenuBeanModel extends POSBaseBeanModel
{                                       // begin class SummaryChargeMenuBeanModel
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        holds description and expected amount for each tender or charge
    **/
    protected SummaryCountBeanModel[] summaryCountBeanModel = null;
    /**
        blind close flag
    **/
    protected boolean blindClose = false;
    
    /** Store instance of logger here **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.SummaryChargeMenuBeanModel.class);

    //---------------------------------------------------------------------
    /**
            Constructs SummaryChargeMenuBeanModel object. <P>
            <B>Pre-Condition(s)</B>
            <UL>
            <LI>none
            </UL>
            <B>Post-Condition(s)</B>
            <UL>
            <LI>none
            </UL>
            @deprecated As of release 6.0.0, replaced by {@link #SummaryChargeMenuBeanModel(List)}.
    **/
    //---------------------------------------------------------------------
    public SummaryChargeMenuBeanModel()
    {                                   // begin SummaryMenuBeanModel()
        Dispatcher d = Dispatcher.getDispatcher();
        UtilityManager util = (UtilityManager) d.getManager(UtilityManagerIfc.TYPE);
        CardType cardType = util.getConfiguredCardTypeInstance();
        List tempCardList = cardType.getCardList();
        initSummaryCountBeanModel(tempCardList);
    }                                   // end SummaryMenuBeanModel()

    //---------------------------------------------------------------------
    /**
            Constructs SummaryMenuBeanModel object. <P>
            <B>Pre-Condition(s)</B>
            <UL>
            <LI>none
            </UL>
            <B>Post-Condition(s)</B>
            <UL>
            <LI>none
            </UL>
            @param cardList  The list of cards for this card type (Credit or Debit)
    **/
    //---------------------------------------------------------------------
    public SummaryChargeMenuBeanModel(List cardList)
    {                                   // begin SummaryMenuBeanModel()
        // Setup the SummaryCountBeanModel with our tender list
        initSummaryCountBeanModel(cardList);
    }                                   // end SummaryMenuBeanModel()

    //---------------------------------------------------------------------
    /**
        Creates the summary count bean model array from the card list. 
        @param cardList  List  The card list
    **/
    //---------------------------------------------------------------------
    protected void initSummaryCountBeanModel(List cardList)
    {
        // Determine the length of the summary count bean model array
        // Do not include gift cards
        Iterator it = cardList.iterator();                
        int modelLength = 0;
        while (it.hasNext())
        {
            Card currentCard = (Card) it.next();
            if (!"GiftCard".equals(currentCard.getCardName()))
            {
                modelLength++;
            }
        }

        summaryCountBeanModel = new SummaryCountBeanModel[modelLength];
        CurrencyIfc zero = null;
        try
        {
            zero = DomainGateway.getBaseCurrencyInstance("0.00");
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database may be online"); 
        }

        int count = 0;
        it = cardList.iterator();
        while (it.hasNext())
        {
            Card currentCard = (Card) it.next();
            if (!"GiftCard".equals(currentCard.getCardName()))
            {    
                summaryCountBeanModel[count] = new SummaryCountBeanModel();
                summaryCountBeanModel[count].setDescription(currentCard.getCardName());
                summaryCountBeanModel[count].setTenderType(currentCard.getCardTypeCode());
                summaryCountBeanModel[count].setLabel(currentCard.getCardName());
                summaryCountBeanModel[count].setLabelTag(currentCard.getCardName());
                if (zero != null)
                {
                    summaryCountBeanModel[count].setAmount(zero);
                }
                count++;
            }
        }
    }                                   // end initSummaryCountBeanModel[]()
        

    //----------------------------------------------------------------------------
    /**
        Retrieves holds description and expected amount for each tender or charge. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return holds description and expected amount for each tender or charge
    **/
    //----------------------------------------------------------------------------
    public SummaryCountBeanModel[] getSummaryCountBeanModel()
    {                                   // begin getSummaryCountBeanModel[]()
        return(summaryCountBeanModel);
    }                                   // end getSummaryCountBeanModel[]()

    //----------------------------------------------------------------------------
    /**
        Sets holds description and expected amount for each tender or charge. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  holds description and expected amount for each tender or charge
    **/
    //----------------------------------------------------------------------------
    public void setSummaryCountBeanModel(SummaryCountBeanModel[] value)
    {                                   // begin setSummaryCountBeanModel[]()
        summaryCountBeanModel = value;
    }                                   // end setSummaryCountBeanModel[]()

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        String strResult = new String("Class:  SummaryChargeMenuBeanModel (Revision " +
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

    //----------------------------------------------------------------------
    /**
        Return whether the expected amounts should be hidden.
        @return true if the expected amounts should be hidden
    **/
    //----------------------------------------------------------------------
    public boolean isBlindClose()
    {
        return blindClose;
    }

    //----------------------------------------------------------------------
    /**
        Set whether to show the expected amounts.
        @param blind  true if the amounts should be hidden
    **/
    //----------------------------------------------------------------------
    public void setBlindClose(boolean blind)
    {
        blindClose = blind;
    }

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
        SummaryChargeMenuBeanModel c = new SummaryChargeMenuBeanModel();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class SummaryChargeMenuBeanModel
