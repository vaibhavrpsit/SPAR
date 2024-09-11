/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/FinancialCountTenderItem.java /main/12 2012/12/14 09:46:19 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     09/30/14 - added methods to return positive values for reports per 
 *                         a forward port as the count of currency was printing
 *                         negative values on the till report. Removed references
 *                         to deprecated code.
 *    abhinavs  12/11/12 - Fixing HP Fortify missing null check issues
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   03/24/09 - added method hasDenominations
 *    cgreene   03/24/09 - fixed toString to not call currencyservice to format
 *                         currencies
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
 *         changes to export and import POSLog.
 *    6    360Commerce 1.5         5/23/2007 7:10:48 PM   Jack G. Swan    Fixed
 *          issues with tills and CurrencyID.
 *    5    360Commerce 1.4         5/22/2007 1:07:51 PM   Peter J. Fierro
 *         Receipt changes, save currency id in till tender history.
 *    4    360Commerce 1.3         4/25/2007 10:00:53 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:28:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:04 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.5  2004/07/13 22:33:39  cdb
 *   @scr 5970 in Services Impact Tracker database - removed hardcoding of class names
 *   in all getHardTotalsData methods.
 *
 *   Revision 1.4  2004/07/09 18:39:18  aachinfiev
 *   @scr 6082 - Replacing "new" with DomainObjectFactory.
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Oct 29 2003 13:56:54   baa
 * refactor retrieve decimal values as string 
 * Resolution for 3392: 610/700 Cleanup
 * 
 *    Rev 1.0.1.0   Oct 29 2003 08:10:42   baa
 * refactoring, decimal formatting 
 * Resolution for 3392: 610/700 Cleanup
 * 
 *    Rev 1.0   Aug 29 2003 15:35:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jun 10 2003 11:50:36   jgs
 * Backout hardtotals deprecations and compression change due to performance consideration.
 * 
 *    Rev 1.1   May 20 2003 07:25:58   jgs
 * Deprecated getHardtotalsData() and setHardtotalsData() methods.
 * Resolution for 2573: Modify Hardtotals compress to remove dependency on code modifications.
 * 
 *    Rev 1.0   Jun 03 2002 16:51:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:00:42   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:20:50   msg
 * Initial revision.
 * 
 *    Rev 1.4   17 Jan 2002 15:45:16   epd
 * Fixed bug in negate() method
 * removed all ridiculous new String() calls
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.3   16 Jan 2002 09:15:40   epd
 * Made setSummaryDescription() defensive
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.2   04 Jan 2002 10:26:04   pdd
 * Removed tenderType and currencyCode.
 * Added tenderDescriptor (TenderDescriptorIfc).
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 * 
 *    Rev 1.1   20 Dec 2001 21:49:58   pdd
 * Added tenderType and currencyCode.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 * 
 *    Rev 1.0   Sep 20 2001 16:14:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:37:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

// java imports
import java.io.Serializable;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.common.utility.Util;

//----------------------------------------------------------------------------
/**
     This class represents a tender detail item for a FinancialCount class.  An 
     instance of this class is used to accumulate totals of each 
     tender type (identified by the <code>description</code> attribute). <P>
     The <code>summary</code> attribute indicates whether an entry is
     to be used for summary purposes.  For instance, the number of
     five-dollar bills might be recorded in a till count, but that value
     won't be used to compare the count against expected values; rather
     the sum of all the cash tender types would be used for the comparison.
     In that case, an entry would exist for five-dollar bills with the
     <code>summary</code> attribute set to false and the 
     <code>summaryDescription</code> attribute indicating "cash".  Another entry 
     (with the <code>summary</code> attribute set to true) would exist
     for cash types, including five-dollar bills. <P>
     @see FinancialTotals
     @version $Revision: /main/12 $
**/
//----------------------------------------------------------------------------
public class FinancialCountTenderItem implements FinancialCountTenderItemIfc
{ // begin class FinancialCountTenderItem
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3488476158012248333L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/12 $";
    /**
        number of items taken in
    **/
    protected int numberItemsIn = 0;
    /**
        number of items paid out
    **/
    protected int numberItemsOut = 0;
    /**
        amount of this tender taken in
    **/
    protected CurrencyIfc amountIn = null;
    /**
        amount of this tender paid out                          
    **/
    protected CurrencyIfc amountOut = null;
    /**
        net amount (amount taken in less amount paid out)
    **/
    protected CurrencyIfc amountTotal = null;
    /**
        tender descriptor
    **/
    protected TenderDescriptorIfc tenderDescriptor = null;
    /**
        description of tender type 
    **/
    protected String description = "";
    /**
        description of summary tender type (empty if <code>summary</code> is true)
    **/
    protected String summaryDescription = "";
    /**
        indicates if this type is to be used for summary purposes
    **/
    protected boolean summary = true;
    /**
        indicates this tender has denominations
    **/
    protected boolean hasDenominations = false;

    //---------------------------------------------------------------------
    /**
            Constructs FinancialCountTenderItem object. <P>
            <B>Pre-Condition(s)</B>eder
            <UL>
            <LI>none
            </UL>
            <B>Post-Condition(s)</B>
            <UL>
            <LI>none
            </UL>
    **/
    //---------------------------------------------------------------------
    public FinancialCountTenderItem()
    { // begin FinancialCountTenderItem()
        tenderDescriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
        tenderDescriptor.setCountryCode(DomainGateway.getBaseCurrencyType().getCountryCode());
        tenderDescriptor.setCurrencyID(DomainGateway.getBaseCurrencyType().getCurrencyId());
        amountIn = DomainGateway.getBaseCurrencyInstance();
        amountOut = DomainGateway.getBaseCurrencyInstance();
        amountTotal = DomainGateway.getBaseCurrencyInstance();
    } // end FinancialCountTenderItem()

    //---------------------------------------------------------------------
    /**
        Initializes the attributes.
        @param currencyCode String
    **/
    //---------------------------------------------------------------------
    public void initialize(String currencyCode)
    {
        tenderDescriptor.setCountryCode(currencyCode);
        tenderDescriptor.setCurrencyID(DomainGateway.getAlternateCurrencyInstance(currencyCode).getType().getCurrencyId());
        amountIn = DomainGateway.getCurrencyInstance(currencyCode);
        amountOut = DomainGateway.getCurrencyInstance(currencyCode);
        amountTotal = DomainGateway.getCurrencyInstance(currencyCode);
    }

    //---------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //--------------------------------------------------------------------- 
    public Object clone()
    { // begin clone()
        // instantiate new object
        FinancialCountTenderItem c = new FinancialCountTenderItem();
        setCloneAttributes(c);
        return ((Object) c);
    } // end clone()

    //---------------------------------------------------------------------
    /**
        Sets attributes in clone. <P>
        @param newClass new instance of class
    **/
    //--------------------------------------------------------------------- 
    protected void setCloneAttributes(FinancialCountTenderItem newClass)
    { // begin setCloneAttributes()
        // set values
        newClass.setNumberItemsIn(numberItemsIn);
        newClass.setNumberItemsOut(numberItemsOut);

        if (tenderDescriptor != null)
        {
            newClass.setTenderDescriptor((TenderDescriptorIfc) tenderDescriptor.clone());
        }
        if (amountIn != null)
        {
            newClass.setAmountIn((CurrencyIfc) amountIn.clone());
        }
        if (amountOut != null)
        {
            newClass.setAmountOut((CurrencyIfc) amountOut.clone());
        }
        newClass.deriveAmountTotal();
        if (description != null)
        {
            newClass.setDescription(description);
        }
        if (summaryDescription != null)
        {
            newClass.setSummaryDescription(summaryDescription);
        }
        newClass.setSummary(summary);
        newClass.setHasDenominations(getHasDenominations());
        newClass.setCurrencyID(getCurrencyID());
    } // end setCloneAttributes()

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //--------------------------------------------------------------------- 
    public boolean equals(Object obj)
    { // begin equals()
    	if(obj == this)
    	{
    		return true;
    	}
    	boolean isEqual = false;

    	if(obj instanceof FinancialCountTenderItem)
    	{
    		FinancialCountTenderItem c = (FinancialCountTenderItem) obj;
    		// downcast the input object

    		// compare all the attributes of FinancialCountTenderItem
    		if (numberItemsIn == c.getNumberItemsIn()
    				&& numberItemsOut == c.getNumberItemsOut()
    				&& Util.isObjectEqual(amountIn, c.getAmountIn())
    				&& Util.isObjectEqual(amountOut, c.getAmountOut())
    				&& Util.isObjectEqual(amountTotal, c.getAmountTotal())
    				&& Util.isObjectEqual(tenderDescriptor, c.getTenderDescriptor())
    				&& Util.isObjectEqual(description, c.getDescription())
    				&& Util.isObjectEqual(summaryDescription, c.getSummaryDescription())
    				&& summary == c.isSummary()
    				&& getHasDenominations() == c.getHasDenominations())
    		{
    			isEqual = true; // set the return code to true
    		}

    	}   
    	return (isEqual);
    } // end equals()

    //---------------------------------------------------------------------
    /**
        Adds values from specified object to this object and returns
        results. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param item data to be added to this object
        @return new item
    **/
    //--------------------------------------------------------------------- 
    public FinancialCountTenderItemIfc add(FinancialCountTenderItemIfc item)
    { // begin add()
        // clone this object
        FinancialCountTenderItemIfc newItem = (FinancialCountTenderItemIfc) clone();
        // add attributes
        newItem.addNumberItemsIn(item.getNumberItemsIn());
        newItem.addNumberItemsOut(item.getNumberItemsOut());
        newItem.addAmountIn(item.getAmountIn());
        newItem.addAmountOut(item.getAmountOut());
        newItem.setHasDenominations(item.hasDenominations());
        // pass back new item
        return (newItem);
    } // end add()

    //---------------------------------------------------------------------
    /**
        Creates a negative copy of this object. Note that "ins" and "outs"
        are not reversed; their values are changed to negative. <P>.
        results. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return new item
    **/
    //--------------------------------------------------------------------- 
    public FinancialCountTenderItemIfc negate()
    { // begin negate()
        // clone this object
        FinancialCountTenderItemIfc newItem = DomainGateway.getFactory().getFinancialCountTenderItemInstance();
        newItem.initialize(tenderDescriptor.getCountryCode());
        newItem.setTenderDescriptor(tenderDescriptor);

        // negate attributes
        newItem.setDescription(description);
        newItem.setSummary(summary);
        newItem.setSummaryDescription(summaryDescription);
        newItem.setTenderType(tenderDescriptor.getTenderType());
        newItem.addNumberItemsIn(-1 * getNumberItemsIn());
        newItem.addNumberItemsOut(-1 * getNumberItemsOut());
        newItem.addAmountIn(getAmountIn().negate());
        newItem.addAmountOut(getAmountOut().negate());
        // pass back new item
        return (newItem);
    } // end negate()

    //----------------------------------------------------------------------------
    /**
        Retrieves number of items taken in. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return number of items taken in
    **/
    //----------------------------------------------------------------------------
    public int getNumberItemsIn()
    { // begin getNumberItemsIn()
        return (numberItemsIn);
    } // end getNumberItemsIn()

    //----------------------------------------------------------------------------
    /**
        Sets number of items taken in. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  number of items taken in
    **/
    //----------------------------------------------------------------------------
    public void setNumberItemsIn(int value)
    { // begin setNumberItemsIn()
        numberItemsIn = value;
    } // end setNumberItemsIn()

    //----------------------------------------------------------------------------
    /**
        Retrieves number of items paid out. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return number of items paid out
    **/
    //----------------------------------------------------------------------------
    public int getNumberItemsOut()
    { // begout getNumberItemsOut()
        return (numberItemsOut);
    } // end getNumberItemsOut()

    //----------------------------------------------------------------------------
    /**
        Sets number of items paid out. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  number of items paid out
    **/
    //----------------------------------------------------------------------------
    public void setNumberItemsOut(int value)
    { // begout setNumberItemsOut()
        numberItemsOut = value;
    } // end setNumberItemsOut()

    //----------------------------------------------------------------------------
    /**
        Retrieves net number of items (number of items taken in less number of 
        items paid out). <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return net number of items (number of items taken in less number of items
        paid out)
    **/
    //----------------------------------------------------------------------------
    public int getNumberItemsTotal()
    {
        return (numberItemsIn - numberItemsOut); // number may be negative
    }
    
    /**
    Added to Retrieve positive count of number of items (number of items taken in less number of
    items paid out) for reporting. <P>
    <B>Pre-Condition(s)</B>
    <UL>
    <LI>none
    </UL>
    <B>Post-Condition(s)</B>
    <UL>
    <LI>none
    </UL>
    @return net number of items (number of items taken in less number of items
    paid out)
    @since 14.1
     **/
    //----------------------------------------------------------------------------
    public int getCountItemsTotal()
    {
        return Math.abs(getNumberItemsTotal());
    }

    //----------------------------------------------------------------------------
    /**
        Increments number of items taken in by specified amount. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  increment
    **/
    //----------------------------------------------------------------------------
    public void addNumberItemsIn(int value)
    { // begin addNumberItemsIn()
        numberItemsIn = numberItemsIn + value;
    } // end addNumberItemsIn()

    //----------------------------------------------------------------------------
    /**
        Increments number of items paid out by specified amount. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  increment
    **/
    //----------------------------------------------------------------------------
    public void addNumberItemsOut(int value)
    { // begin addNumberItemsOut()
        numberItemsOut = numberItemsOut + value;
    } // end addNumberItemsOut()

    //----------------------------------------------------------------------------
    /**
        Retrieves amount of this tender taken in. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return amount of this tender taken in
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getAmountIn()
    { // begin getAmountIn()
        return (amountIn);
    } // end getAmountIn()

    //----------------------------------------------------------------------------
    /**
        Sets amount of this tender taken in. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  amount of this tender taken in
    **/
    //----------------------------------------------------------------------------
    public void setAmountIn(CurrencyIfc value)
    { // begin setAmountIn()
        amountIn = value;
        deriveAmountTotal();
    } // end setAmountIn()

    //----------------------------------------------------------------------------
    /**
        Increments amount of this tender taken in by specified value. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  amount to increment
    **/
    //----------------------------------------------------------------------------
    public void addAmountIn(CurrencyIfc value)
    { // begin addAmountIn()
        amountIn = amountIn.add(value);
        deriveAmountTotal();
    } // end addAmountIn()

    //----------------------------------------------------------------------------
    /**
        Retrieves amount of this tender paid out. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return amount of this tender paid out                          
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getAmountOut()
    { // begin getAmountOut()
        return (amountOut);
    } // end getAmountOut()

    //----------------------------------------------------------------------------
    /**
        Sets amount of this tender paid out. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  amount of this tender paid out                            
    **/
    //----------------------------------------------------------------------------
    public void setAmountOut(CurrencyIfc value)
    { // begin setAmountOut()
        amountOut = value;
        deriveAmountTotal();
    } // end setAmountOut()

    //----------------------------------------------------------------------------
    /**
        Outcrements amount of this tender paid out by specified value. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  amount to increment
    **/
    //----------------------------------------------------------------------------
    public void addAmountOut(CurrencyIfc value)
    { // begin addAmountOut()
        amountOut = amountOut.add(value);
        deriveAmountTotal();
    } // end addAmountOut()

    //----------------------------------------------------------------------------
    /**
        Retrieves net amount (amount taken in less amount paid out). <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return net amount (amount taken in less amount paid out)
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getAmountTotal()
    { // begin getAmountTotal()
        return (amountTotal);
    } // end getAmountTotal()

    /**
    Retrieves net amount (amount taken in less amount paid out). <P>
    <B>Pre-Condition(s)</B>
    <UL>
    <LI>none
    </UL>
    <B>Post-Condition(s)</B>
    <UL>
    <LI>none
    </UL>
    @return net positive amount (amount taken in less amount paid out)
    @since 14.1
     **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getCountAmountTotal()
    { // begin getAmountTotal()
        return (amountTotal.abs());
    } // end getAmountTotal()

    //----------------------------------------------------------------------------
    /**
        Derives net amount (amount taken in less amount paid out).  This is called
        from the set methods for amountIn and amountOut. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //----------------------------------------------------------------------------
    protected void deriveAmountTotal()
    { // begin deriveAmountTotal()
        amountTotal = amountIn.subtract(amountOut);
    } // end deriveAmountTotal()

    //----------------------------------------------------------------------------
    /**
        Retrieves description of tender type (used as key). <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return description of tender type (used as key)
    **/
    //----------------------------------------------------------------------------
    public String getDescription()
    { // begin getDescription()
        return (description);
    } // end getDescription()

    //----------------------------------------------------------------------------
    /**
        Sets description of tender type (used as key). <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  description of tender type (used as key)
    **/
    //----------------------------------------------------------------------------
    public void setDescription(String value)
    { // begin setDescription()
        description = value;
    } // end setDescription()

    //----------------------------------------------------------------------------
    /**
        Returns the tender type.
        @return int tender type. See {@link oracle.retail.stores.domain.tender.TenderLineItemIfc} for valid values.
    **/
    //----------------------------------------------------------------------------
    public int getTenderType()
    {
        return tenderDescriptor.getTenderType();
    }

    //----------------------------------------------------------------------------
    /**
        Sets the tender type.
        @param value int tender type. See {@link oracle.retail.stores.domain.tender.TenderLineItemIfc} for valid values.
    **/
    //----------------------------------------------------------------------------
    public void setTenderType(int value)
    {
        tenderDescriptor.setTenderType(value);
    }

    //----------------------------------------------------------------------------
    /**
        Returns the tender subtype.
        @return String tender subtype.
    **/
    //----------------------------------------------------------------------------
    public String getTenderSubType()
    {
        return tenderDescriptor.getTenderSubType();
    }

    //----------------------------------------------------------------------------
    /**
        Sets the tender subtype.
        @param value String tender subtype.
    **/
    //----------------------------------------------------------------------------
    public void setTenderSubType(String value)
    {
        tenderDescriptor.setTenderSubType(value);
    }

    //----------------------------------------------------------------------------
    /**
        Returns the currency code.
        @return String code.
    **/
    //----------------------------------------------------------------------------
    public String getCurrencyCode()
    {
        return tenderDescriptor.getCountryCode();
    }

    //----------------------------------------------------------------------------
    /**
        Sets the Currency Code.
        @param value String.
    **/
    //----------------------------------------------------------------------------
    public void setCurrencyCode(String value)
    {
        tenderDescriptor.setCountryCode(value);
    }
    
    //----------------------------------------------------------------------------
    /**
        Returns the currency code.
        @return String code.
    **/
    //----------------------------------------------------------------------------
    public int getCurrencyID()
    {
        return tenderDescriptor.getCurrencyID();
    }

    //----------------------------------------------------------------------------
    /**
        Sets the Currency Code.
        @param value String.
    **/
    //----------------------------------------------------------------------------
    public void setCurrencyID(int value)
    {
        tenderDescriptor.setCurrencyID(value);
    }    

    //----------------------------------------------------------------------------
    /**
        Retrieves summaryDescription of tender type (used as key). <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return summaryDescription of tender type (used as key)
    **/
    //----------------------------------------------------------------------------
    public String getSummaryDescription()
    { // begin getSummaryDescription()
        return (summaryDescription);
    } // end getSummaryDescription()

    //----------------------------------------------------------------------------
    /**
        Sets summaryDescription of tender type (used as key). <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  summaryDescription of tender type (used as key)
    **/
    //----------------------------------------------------------------------------
    public void setSummaryDescription(String value)
    { // begin setSummaryDescription()
        // It is essential due to code elsewhere that this field be non-null
        // therefore, I have made this defensive.
        if (value == null)
        {
            summaryDescription = "";
        }
        else
        {
            summaryDescription = value;
        }
    } // end setSummaryDescription()

    //----------------------------------------------------------------------------
    /**
        Retrieves indicates if this type is to be used for summary purposes. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return indicates if this type is to be used for summary purposes
    **/
    //----------------------------------------------------------------------------
    public boolean isSummary()
    { // begin isSummary()
        return (summary);
    } // end isSummary()

    //----------------------------------------------------------------------------
    /**
        Sets indicates if this type is to be used for summary purposes. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param value  indicates if this type is to be used for summary purposes
    **/
    //----------------------------------------------------------------------------
    public void setSummary(boolean value)
    { // begin setSummary()
        summary = value;
    } // end setSummary()

    //---------------------------------------------------------------------
    /**
        Sets has-denominations indicator. <P>
        @param value has-denominations indicator
    **/
    //---------------------------------------------------------------------
    public void setHasDenominations(boolean value)
    { // begin setHasDenominations()
        hasDenominations = value;
    } // end setHasDenominations()

    //---------------------------------------------------------------------
    /**
        @deprecated as of 13.1 use {@link #hasDenominations()}
    **/
    //---------------------------------------------------------------------
    public boolean getHasDenominations()
    { // begin getHasDenominations()
        return (hasDenominations);
    } // end getHasDenominations()
    //---------------------------------------------------------------------
    /**
        Retrieves has-denominations indicator. <P>
        @return has-denominations indicator
    **/
    //---------------------------------------------------------------------
    public boolean hasDenominations()
    { // begin getHasDenominations()
        return (hasDenominations);
    } // end getHasDenominations()

    //---------------------------------------------------------------------
    /**
        This method converts hard totals information to a comma delimited
        String. <P>
        @return String
    **/
    //--------------------------------------------------------------------- 
    public void getHardTotalsData(HardTotalsBuilderIfc builder)
    {
        builder.appendStringObject(getClass().getName());
        builder.appendInt(numberItemsIn);
        builder.appendInt(numberItemsOut);
        builder.appendInt(tenderDescriptor.getTenderType());
        builder.appendString(tenderDescriptor.getTenderSubType());
        builder.appendString(tenderDescriptor.getCountryCode());
        builder.appendInt(tenderDescriptor.getCurrencyID()); //I18N
        builder.appendStringObject(amountIn.getStringValue());
        builder.appendStringObject(amountOut.getStringValue());
        builder.appendStringObject(amountTotal.getStringValue());
        builder.appendString(description);
        builder.appendString(summaryDescription);
        builder.appendStringObject(new Boolean(summary).toString());
        builder.appendStringObject(new Boolean(getHasDenominations()).toString());
    }

    //---------------------------------------------------------------------
    /**
        This method populates this object from a comma delimited string.
        <P>
        @param int      offset of the current record
        @param String   String containing hard totals data.
    **/
    //--------------------------------------------------------------------- 
    public void setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException
    {
        numberItemsIn = builder.getIntField();
        numberItemsOut = builder.getIntField();
        tenderDescriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
        tenderDescriptor.setTenderType(builder.getIntField());
        tenderDescriptor.setTenderSubType(builder.getStringField());
        String currencyCode = builder.getStringField();
        tenderDescriptor.setCountryCode(currencyCode);
        tenderDescriptor.setCurrencyID(builder.getIntField()); //I18N
        amountIn = DomainGateway.getCurrencyInstance(currencyCode, builder.getStringObject());
        amountOut = DomainGateway.getCurrencyInstance(currencyCode, builder.getStringObject());
        amountTotal = DomainGateway.getCurrencyInstance(currencyCode, builder.getStringObject());
        description = builder.getStringField();
        summaryDescription = builder.getStringField();
        summary = new Boolean(builder.getStringObject()).booleanValue();
        hasDenominations = new Boolean(builder.getStringObject()).booleanValue();
    }

    //---------------------------------------------------------------------
    /**
        Sets the tender descriptor for this item.
        @param value TenderDescriptorIfc
    **/
    //---------------------------------------------------------------------
    public void setTenderDescriptor(TenderDescriptorIfc value)
    {
        tenderDescriptor = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the tender descriptor for this item.
        @return TenderDescriptorIfc
    **/
    //---------------------------------------------------------------------
    public TenderDescriptorIfc getTenderDescriptor()
    {
        return tenderDescriptor;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = new StringBuilder("Class:  FinancialCountTenderItem (Revision ");
        strResult.append(getRevisionNumber()).append(") @").append(hashCode());
        // add attributes to string
        strResult.append("\n")
            .append("tenderDescriptor: ")
            .append(tenderDescriptor)
            .append("\n")
            .append("description:                            [")
            .append(description)
            .append("]\n")
            .append("numberItemsIn:                          [")
            .append(numberItemsIn)
            .append("]\n")
            .append("numberItemsOut:                         [")
            .append(numberItemsOut)
            .append("]\n")
            .append("amountIn:                               [")
            .append(amountIn)
            .append("]\n")
            .append("amountOut:                              [")
            .append(amountOut)
            .append("]\n")
            .append("amountTotal:                            [")
            .append(amountTotal)
            .append("]\n")
            .append("summaryDescription:                     [")
            .append(summaryDescription)
            .append("]\n")
            .append("summary:                                [")
            .append(summary)
            .append("]\n")
            .append("hasDenominations:                       [")
            .append(hasDenominations)
            .append("]\n");
        // pass back result
        return strResult.toString();
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        FinancialCountTenderItem main method. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>toString() output
        </UL>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    { // begin main()
        // instantiate class
        FinancialCountTenderItem c = new FinancialCountTenderItem();
        // output toString()
        System.out.println(c.toString());

        try
        {
            // instantiate class
            HardTotalsStringBuilder builder = null;
            FinancialCountTenderItem a1 = new FinancialCountTenderItem();
            FinancialCountTenderItemIfc a2 = null;

            builder = new HardTotalsStringBuilder();
            a1.getHardTotalsData(builder);
            Serializable obj = builder.getHardTotalsOutput();
            builder.setHardTotalsInput(obj);
            a2 = (FinancialCountTenderItemIfc) builder.getFieldAsClass();
            a2.setHardTotalsData(builder);

            if (a1.equals(a2))
            {
                System.out.println("Empty FinancialCountTenderItemes are equal");
            }
            else
            {
                System.out.println("Empty FinancialCountTenderItemes are NOT equal");
                System.out.println("FCTI 1 = " + a1.toString());
                System.out.println("FCTI 2 = " + a2.toString());
            }

            // instantiate class
            a1.setNumberItemsIn(8);
            a1.setNumberItemsOut(2);
            a1.setAmountIn(DomainGateway.getBaseCurrencyInstance("133.47"));
            a1.setAmountOut(DomainGateway.getBaseCurrencyInstance("22.27"));
            a1.setDescription("Credit");
            a1.setSummaryDescription("Visa");
            a1.setSummary(true);

            builder = new HardTotalsStringBuilder();
            a1.getHardTotalsData(builder);
            obj = builder.getHardTotalsOutput();
            builder.setHardTotalsInput(obj);
            a2 = (FinancialCountTenderItemIfc) builder.getFieldAsClass();
            a2.setHardTotalsData(builder);

            if (a1.equals(a2))
            {
                System.out.println("Full FinancialCountTenderItemes are equal");
            }
            else
            {
                System.out.println("Full FinancialCountTenderItemes are NOT equal");
                System.out.println("FCTI 1 = " + a1.toString());
                System.out.println("FCTI 2 = " + a2.toString());
            }
        }
        catch (HardTotalsFormatException iae)
        {
            System.out.println("FinancialCountTenderItem convertion failed:");
            iae.printStackTrace();
        }
    } // end main()
} // end class FinancialCountTenderItem
