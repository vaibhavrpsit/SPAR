/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/StoreSafe.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         4/25/2007 10:00:53 AM  Anda D. Cadar   I18N
 *      merge
 * 3    360Commerce 1.2         3/31/2005 4:30:13 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:25:36 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:14:30 PM  Robert Pearse   
 *
 *Revision 1.6  2004/09/23 00:30:53  kmcbride
 *@scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *Revision 1.5  2004/07/09 18:39:18  aachinfiev
 *@scr 6082 - Replacing "new" with DomainObjectFactory.
 *
 *Revision 1.4  2004/05/26 21:15:34  dcobb
 *@scr 4302 Correct compiler warnings
 *
 *Revision 1.3  2004/02/12 17:13:34  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 23:25:27  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:52:46   msg
 * Initial revision.
 * 
 *    Rev 1.2   25 Mar 2002 12:30:06   epd
 * Jose asked me to check these in.  Updates to use TenderDescriptor
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.6   17 Jan 2002 08:32:32   jfv
 * Added currentOperatingFunds
 * 
 *    Rev 1.5   13 Dec 2001 15:58:02   epd
 * minor modifications
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.4   12 Dec 2001 16:32:36   adc
 * Removed fields 
 * Resolution for Backoffice SCR-20: Till Pickup/Loan
 *
 *    Rev 1.3   05 Dec 2001 10:47:04   adc
 * Added openOperatingFunds and closeOperatingFunds
 * Resolution for Backoffice SCR-20: Till Pickup/Loan
 *
 *    Rev 1.2   03 Dec 2001 11:35:38   adc
 * Changes for updating the open and close funds and for validatiog the store safe tenders
 * Resolution for Backoffice SCR-20: Till Pickup/Loan
 *
 *    Rev 1.1   28 Nov 2001 15:51:46   adc
 * Added safeOperation instance variable
 * Resolution for Backoffice SCR-20: Till Pickup/Loan
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;
// java imports
import java.util.ArrayList;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This class implements the store Safe object. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class StoreSafe implements StoreSafeIfc
{                                       // begin class StoreSafe
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -839798981108540237L;


    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";


    /**
        store identifier
    **/
    protected String storeID = "";

    /**
    * Store Safe identifier
    */
    protected String storeSafeID= "1";

    /**
    * Operation type
    */
    protected int operationType = StoreSafeIfc.OPEN;

    /**
        The calendar date equivalent of the associated fiscal day, fiscal
        week and fiscal year.
    **/
    protected EYSDate businessDay = null;

    /**
        The safe's current operating funds value
    **/
    protected FinancialCountIfc currentOperatingFunds = DomainGateway.getFactory().getFinancialCountInstance();

    /**
        The safe's opening operating funds value
    **/
    protected FinancialCountIfc openOperatingFunds = DomainGateway.getFactory().getFinancialCountInstance();

    /**
        The safe's closing operating funds value
    **/
    protected FinancialCountIfc closeOperatingFunds = DomainGateway.getFactory().getFinancialCountInstance();


    /**
        The safe's pickup counts
    **/
    protected FinancialCountIfc pickupCounts =  DomainGateway.getFactory().getFinancialCountInstance();

    /**
        The safe's loan counts
    **/
    protected FinancialCountIfc loanCounts =  DomainGateway.getFactory().getFinancialCountInstance();

    /**
        The safe's deposit counts
    **/
    protected FinancialCountIfc depositCounts =  DomainGateway.getFactory().getFinancialCountInstance();

    /**
        status code @see AbstractStatusEntityIfc
    **/
    protected int status = AbstractStatusEntityIfc.STATUS_OPEN;

    /**
        The safe's OpenTillFunds
    **/
    protected FinancialCountIfc openTillCounts =  DomainGateway.getFactory().getFinancialCountInstance();

    /**
        The safe's CloseTillFunds
    **/
    protected FinancialCountIfc closeTillCounts =  DomainGateway.getFactory().getFinancialCountInstance();


    /**
        store tender strings.  Used to validate tenders.
    **/
    protected ArrayList tendersForStoreSafe = new ArrayList();

    protected ArrayList tendersDescForStoreSafe = new ArrayList();

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
        Sets store identifier. <P>
        @param value store identifier
    **/
    //---------------------------------------------------------------------
    public void setStoreID(String value)
    {                                   // begin setStoreID()
        storeID = value;
    }                                   // end setStoreID()

    //---------------------------------------------------------------------
    /**
        Retrieves store identifier. <P>
        @return store identifier
    **/
    //---------------------------------------------------------------------
    public String getStoreID()
    {                                   // begin getStoreID()
        return(storeID);
    }                                   // end getStoreID()

    //----------------------------------------------------------------------------
    /**
        Sets the operation type <P>
        @param value
    **/
    //----------------------------------------------------------------------------
    public void setOperationType(int value)
    {
      operationType = value;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves operation type. <P>
        @return operationType
    **/
    //----------------------------------------------------------------------------
    public int getOperationType()
    {
      return operationType;
    }
    //---------------------------------------------------------------------
    /**
        Sets store identifier. <P>
        @param value store identifier
    **/
    //---------------------------------------------------------------------
    public void setStoreSafeID(String value)
    {                                   // begin setStoreSafeID()
        storeSafeID = value;
    }                                   // end setStoreSafeID()

    //---------------------------------------------------------------------
    /**
        Retrieves store identifier. <P>
        @return store identifier
    **/
    //---------------------------------------------------------------------
    public String getStoreSafeID()
    {                                   // begin getStoreSafeID()
        return(storeSafeID);
    }                                   // end getStoreSafeID()


    //----------------------------------------------------------------------------
    /**
        Retrieves status. <P>
        @return status
        @see AbstractStatusEntityIfc
    **/
    //----------------------------------------------------------------------------
    public int getStatus()
    {                                   // begin getStatus()
        return(status);
    }                                   // end getStatus()

    //----------------------------------------------------------------------------
    /**
        Sets status.<p>
        @param value  status
        @see AbstractStatusEntityIfc
    **/
    //----------------------------------------------------------------------------
    public void setStatus(int value)
    {                                   // begin setStatus()
      status = value;
    }                                   // end setStatus()


     //----------------------------------------------------------------------------
    /**
       Indicates if status is open. <P>
       @return true if status is open, false otherwise
       @see AbstractStatusEntityIfc
    **/
    //----------------------------------------------------------------------------
    public boolean isOpen()
    {
        boolean b = false;
        if (status == AbstractStatusEntityIfc.STATUS_OPEN)
        {
            b = true;
        }

        return (b);
    }



    //---------------------------------------------------------------------
    /**
        Sets business day to current date. <P>
    **/
    //---------------------------------------------------------------------
    public void setBusinessDay()
    {                                   // begin setBusinessDay()
        businessDay = DomainGateway.getFactory().getEYSDateInstance();
    }                                   // end setBusinessDay()

    //---------------------------------------------------------------------
    /**
        Sets business day to specified value. <P>
        @param value timestamp setting
    **/
    //---------------------------------------------------------------------
    public void setBusinessDay(EYSDate value)
    {                                   // begin setBusinessDay()
        businessDay = value;
    }                                   // end setBusinessDay()

    //---------------------------------------------------------------------
    /**
        Returns business day. <P>
        @return business day.
    **/
    //---------------------------------------------------------------------
    public EYSDate getBusinessDay()
    {                                   // begin setBusinessDay()
        return(businessDay);
    }                                   // end setBusinessDay()



    //---------------------------------------------------------------------
    /**
        Adds a FinancialCountIfc object to the pickup count list. <P>
        @param addCount financial count to add
    **/
    //---------------------------------------------------------------------
    public void addPickupCount(FinancialCountIfc addCount)
    {                                   // begin addPickupCount()
      pickupCounts.add(getValidTenderItems(addCount));
      setOperationType(StoreSafeIfc.PICKUP);
    }                                   // end addPickupCount()

    //----------------------------------------------------------------------------
    /**
        Retrieves the pickupCounts for this safe. <P>
        @return pickupCounts for this safe
    **/
    //----------------------------------------------------------------------------
    public FinancialCountIfc getPickupCounts()
    {                                   // begin getPickupCounts()

        return(pickupCounts);
    }                                   // end getPickupCounts()

    //---------------------------------------------------------------------
    /**
        Returns a CurrencyIfc object containing the total of all pickup counts. <P>
        @return The total of all pickup counts in the safe.
    **/
    //---------------------------------------------------------------------
    public CurrencyIfc getPickupCountAmount()
    {

        // Get the total of all pickup counts.
        return pickupCounts.getAmount();
    }

    //---------------------------------------------------------------------
    /**
        Adds a FinancialCountIfc object to the loan count list. <P>
        @param addCount financial count to add
    **/
    //---------------------------------------------------------------------
    public void addLoanCount(FinancialCountIfc addCount)
    {                                   // begin addLoanCount()
      loanCounts.add(getValidTenderItems(addCount));
      setOperationType(StoreSafeIfc.LOAN);
    }                                   // end addLoanCount()

    //----------------------------------------------------------------------------
    /**
        Retrieves the loanCounts for this safe. <P>
        @return loanCounts for this safe
    **/
    //----------------------------------------------------------------------------
    public FinancialCountIfc getLoanCounts()
    {                                   // begin getLoanCounts()

        return(loanCounts);
    }                                   // end getLoanCounts()

    //---------------------------------------------------------------------
    /**
        Returns a CurrencyIfc object containing the total of all loan counts. <P>
        @return The total of all loan counts in the safe.
    **/
    //---------------------------------------------------------------------
    public CurrencyIfc getLoanCountAmount()
    {                                   // begin getLoanCountAmount()

        // Get the total of all loan counts.
        return loanCounts.getAmount();
    }                                   // end getLoanCountAmount()

    //---------------------------------------------------------------------
    /**
        Adds a FinancialCountIfc object to the deposit count list. <P>
        @param addCount financial count to add
    **/
    //---------------------------------------------------------------------
    public void addDepositCount(FinancialCountIfc addCount)
    {                                   // begin addDepositCount()
        depositCounts.add(getValidTenderItems(addCount));
        setOperationType(StoreSafeIfc.DEPOSIT);
    }                                   // end addDepositCount()

    //----------------------------------------------------------------------------
    /**
        Retrieves the depositCounts for this safe. <P>
        @return depositCounts for this safe
    **/
    //----------------------------------------------------------------------------
    public FinancialCountIfc getDepositCounts()
    {                                   // begin getDepositCounts()

        return(depositCounts);
    }                                   // end getDepositCounts()

    //---------------------------------------------------------------------
    /**
        Returns a CurrencyIfc object containing the total of all deposit counts. <P>
        @return The total of all deposit counts in the safe.
    **/
    //---------------------------------------------------------------------
    public CurrencyIfc getDepositCountAmount()
    {                                   // begin getDepositCountAmount()

        // Get the total of all deposit counts.
        return depositCounts.getAmount();
    }                                   // end getDepositCountAmount()

    //----------------------------------------------------------------------------
    /**
        Retrieves the openTillCounts for this safe. <P>
        @return openTillCounts for this safe
    **/
    //----------------------------------------------------------------------------
    public FinancialCountIfc getOpenTillCounts()
    {                                   // begin getOpenTillCounts()

        return(openTillCounts);
    }                                   // end getOpenTillCounts()

    //---------------------------------------------------------------------
    /**
        Adds a FinancialCountIfc object to the open till fund counts. <P>
        @param addCount financial count to add
    **/
    //---------------------------------------------------------------------
    public void addOpenTillCount(FinancialCountIfc addCount)
    {                                   // begin addOpenTillCount()

        // Check the financialCount items to see if they are accepted tenders
        // Only add the items that are accepted tenders.

        openTillCounts.add(getValidTenderItems(addCount));
        setOperationType(StoreSafeIfc.OPEN);
    }                                   // end addOpenTillCount()

    //---------------------------------------------------------------------
    /**
        Returns a CurrencyIfc object containing the total of all close till fund counts. <P>
        @return The total of all open till fund counts in the safe.
    **/
    //---------------------------------------------------------------------
    public CurrencyIfc getCloseTillAmount()
    {                                   // begin getCloseTillAmount()

        // Get the total of all closeTill counts.
        return closeTillCounts.getAmount();
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves the closeTillCounts for this safe. <P>
        @return closeTillCounts for this safe
    **/
    //----------------------------------------------------------------------------
    public FinancialCountIfc getCloseTillCounts()
    {                                   // begin getCloseTillCounts()

        return(closeTillCounts);
    }                                   // end getCloseTillCounts()

    //---------------------------------------------------------------------
    /**
        Adds a FinancialCountIfc object to the close till fund counts. <P>
        @param addCount financial count to add
    **/
    //---------------------------------------------------------------------
    public void addCloseTillCount(FinancialCountIfc addCount)
    {                                   // begin addCloseTillCount()

        // Check the financialCount items to see if they are accepted tenders
        // Only add the items that are accepted tenders.

        closeTillCounts.add(getValidTenderItems(addCount));
        setOperationType(StoreSafeIfc.CLOSE);
    }                                   // end addCloseTillCount()

    //---------------------------------------------------------------------
    /**
        Returns a CurrencyIfc object containing the total of all open till fund counts. <P>
        @return The total of all open till fund counts in the safe.
    **/
    //---------------------------------------------------------------------
    public CurrencyIfc getOpenTillCountAmount()
    {                                   // begin getOpenTillCountAmount()

        // Get the total of all openTillFund counts.
        return openTillCounts.getAmount();
    }

    //---------------------------------------------------------------------
    /**
        Sets the closeOperating Funds to specified value.<P>
        @param value the closing operating funds setting
    **/
    //---------------------------------------------------------------------
    public void setCloseOperatingFunds(FinancialCountIfc value)
    {
        // Check the financialCount items to see if they are accepted tenders
        // If the items are accepted tenders, add them, otherwise just ignore them

        closeOperatingFunds = getValidTenderItems(value);
        setOperationType(StoreSafeIfc.CLOSE);
    }

    //---------------------------------------------------------------------
    /**
        Returns the closeOperating Funds value.<P>
        @return the closing operating funds
    **/
    //---------------------------------------------------------------------
    public FinancialCountIfc getCloseOperatingFunds()
    {                                   // begin getCloseOperatingFunds()
        return closeOperatingFunds;
    }

    //---------------------------------------------------------------------
    /**
        Sets the openOperating Funds to specified value.<P>
        @param value the opening operating funds setting
    **/
    //---------------------------------------------------------------------
    public void setCurrentOperatingFunds(FinancialCountIfc value)
    {                             

        // Check the financialCount items to see if they are accepted tenders
        // If the items are accepted tenders, add them, otherwise just ignore them

        currentOperatingFunds = getValidTenderItems(value);
    }                            

    //---------------------------------------------------------------------
    /**
        Returns the openOperating Funds value.<P>
        @return the opening operating funds
    **/
    //---------------------------------------------------------------------
    public FinancialCountIfc getCurrentOperatingFunds()
    {                                   
        return currentOperatingFunds;
    }                                  
                                   

    //---------------------------------------------------------------------
    /**
        Sets the openOperating Funds to specified value.<P>
        @param value the opening operating funds setting
    **/
    //---------------------------------------------------------------------
    public void setOpenOperatingFunds(FinancialCountIfc value)
    {                                   // begin setOpenOperatingFunds()

        // Check the financialCount items to see if they are accepted tenders
        // If the items are accepted tenders, add them, otherwise just ignore them

        openOperatingFunds = getValidTenderItems(value);
        setOperationType(StoreSafeIfc.OPEN);
    }                                   // end setOpenOperatingFunds()

    //---------------------------------------------------------------------
    /**
        Returns the openOperating Funds value.<P>
        @return the opening operating funds
    **/
    //---------------------------------------------------------------------
    public FinancialCountIfc getOpenOperatingFunds()
    {                                   // begin getOpenOperatingFunds()
        return openOperatingFunds;
    }                                   // end getOpenOperatingFunds()
                                   // end getCloseOperatingFunds()


    //---------------------------------------------------------------------
    /**
        Creates a FinancialCountIfc object from the FinancialCountIfc object received,
        containing only tender items deemed valid via the tenderForStoreSafe list.<P>
        @param value the FinancialCountIfc object to "validate"
        @return a FinancialCountIfc object containing only the "valid" tender items
    **/
    //---------------------------------------------------------------------
    public FinancialCountIfc getValidTenderItems(FinancialCountIfc value)
    {                                   // begin getValidTenderItems()

        // Check the financialCount items to see if they are accepted tenders
        // If the items are accepted tenders, add them, otherwise just ignore them.

        FinancialCountIfc fcValid = DomainGateway.getFactory().getFinancialCountInstance();

        FinancialCountTenderItemIfc[] tenderItems = value.getTenderItems();

        for (int i = 0; i < tenderItems.length; i++)    // Loop through all of the tenderItems received
        {
            if (tendersDescForStoreSafe.contains((Object)tenderItems[i].getTenderDescriptor()))
            {
                fcValid.addTenderItem(tenderItems[i]);  //  Keep the tender item if it is in the
                                                        //  list of valid tender items

            }  // if (tenderForStoreSafe.contains((Object)tenderItems[i].getDescription()))
        }  // for (int i = 0; i < tenderItems.length; i++)


        return fcValid;
    }                                   // end getValidTenderItems()

     //---------------------------------------------------------------------
    /**
        Returns a string array containing a list of valid tenders for the safe
        @return a String array containing the valid store safe tenders
    **/
    //---------------------------------------------------------------------
    public String[] getValidTenderList()
    {                                   // begin getValidTenderList()

        String[] returnArray = (String[]) tendersForStoreSafe.toArray( new String[tendersForStoreSafe.size()] );
        return returnArray;


    }                                   // end getValidTenderList()

    public TenderDescriptorIfc[] getValidTenderDescList()
    {   

        TenderDescriptorIfc[] returnArray = (TenderDescriptorIfc[]) tendersDescForStoreSafe.toArray( new TenderDescriptorIfc[tendersDescForStoreSafe.size()] );
        return returnArray;


    }  

    //---------------------------------------------------------------------
    /**
        Populates the tendersForStoreSafe arraylist
        @param value the ArrayList to the received ArrayList
    **/
    //---------------------------------------------------------------------
    public void setValidTenderList(ArrayList value)
    {                                   // begin setValidTenderList(ArrayList)

        if (value != null)
        {
            tendersForStoreSafe = value;
        }

    }                                   // end setValidTenderList(ArrayList)

    //---------------------------------------------------------------------
    /**
        Populates the tendersDescForStoreSafe arraylist
        @param value the ArrayList to the received ArrayList
    **/
    //---------------------------------------------------------------------
    public void setValidTenderDescList(ArrayList value)
    {                                   // begin setValidTenderDescList()

        if (value != null)
        {
            tendersDescForStoreSafe = value;
        }

    }                                   // end setValidTenderDescList()

    //---------------------------------------------------------------------
    /**
        Populates the tenderForStoreSafe arraylist
        @param value the Array of Strings to the received ArrayList
    **/
    //---------------------------------------------------------------------
    public void setValidTenderList(String [] value)
    {                                   // begin setValidTenderList(String[])

        if (value.length != 0)
        {

            for (int i = 0; i < value.length; i++)
                tendersForStoreSafe.add(value[i]);


        }
    }                                   // end setValidTenderList(String[])
    //---------------------------------------------------------------------
    /**
        Clones the StoreSafe object. <P>
        @return instance of StoreSafe object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
       // instantiate new object
       StoreSafe s = new StoreSafe();

        // set attributes
        setCloneAttributes(s);

        // pass back object
        return((Object) s);
    }                                   // end clone()

    //---------------------------------------------------------------------
    /**
            Sets attributes in clone. <P>
    @param newClass new instance of class
    **/
    //---------------------------------------------------------------------
    protected void setCloneAttributes(StoreSafeIfc newClass)
    {                                   // begin setCloneAttributes()
      // set values
      newClass.setStoreID(storeID);
      newClass.setBusinessDay(businessDay);
      newClass.setStoreSafeID(storeSafeID);
      newClass.setStatus(status);
      newClass.setOperationType(operationType);
      newClass.addDepositCount((FinancialCountIfc)depositCounts.clone());
      newClass.addLoanCount((FinancialCountIfc)loanCounts.clone());
      newClass.addPickupCount((FinancialCountIfc)pickupCounts.clone());
      newClass.addOpenTillCount((FinancialCountIfc)openTillCounts.clone());
      newClass.addCloseTillCount((FinancialCountIfc)closeTillCounts.clone());
      newClass.setCloseOperatingFunds((FinancialCountIfc)closeOperatingFunds.clone());
      newClass.setOpenOperatingFunds((FinancialCountIfc)openOperatingFunds.clone());


    }                                   // end setCloneAttributes()

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean equals(Object obj)
    {                                   // begin equals()
        boolean isEqual = false;

        if (obj instanceof StoreSafe)
        {
            StoreSafe c = (StoreSafe) obj;          // downcast the input object
            // compare all the attributes of StoreSafe
            if (super.equals(obj) &&
                Util.isObjectEqual(getStoreID(), c.getStoreID()) &&
                Util.isObjectEqual(getStoreSafeID(), c.getStoreSafeID()) &&
                Util.isObjectEqual(getBusinessDay(), c.getBusinessDay()) &&
                getStatus() == c.getStatus()&&
                getOperationType() == c.getOperationType()&&
                Util.isObjectEqual(getDepositCounts(), c.getDepositCounts())&&
                Util.isObjectEqual(getLoanCounts(), c.getLoanCounts())&&
                Util.isObjectEqual(getPickupCounts(), c.getPickupCounts())&&
                Util.isObjectEqual(getOpenTillCounts(), c.getOpenTillCounts())&&
                Util.isObjectEqual(getCloseTillCounts(), c.getCloseTillCounts()) &&
                Util.isObjectEqual(getCloseOperatingFunds(), c.getCloseOperatingFunds()) &&
                Util.isObjectEqual(getOpenOperatingFunds(), c.getOpenOperatingFunds())
                )
            {
                isEqual = true;             // set the return code to true
            }
            else
            {
                isEqual = false;            // set the return code to false
            }
            return(isEqual);
        }

        return isEqual;
    }                                   // end equals()


    //---------------------------------------------------------------------
    /**
        Returns the string version of the StoreSafe object. <P>
        @return String instance of StoreSafe object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        StringBuffer strResult = new StringBuffer();
        strResult.append("Class:  StoreSafe (Revision ");
        strResult.append(getRevisionNumber()).append(") @").append(hashCode()).append("\n");
        strResult.append("storeID:                         [" + storeID + "]\n");
        strResult.append("storeSafeID:                     [" + storeSafeID + "]\n");
        strResult.append("operationType:                   [" + operationType + "]\n");
        strResult.append("status:                          [" + AbstractStatusEntityIfc.STATUS_DESCRIPTORS[status] + "]\n");

        if (businessDay == null)
        {
            strResult.append("BusinessDay:                     [null]\n\n");
        }
        else
        {
            strResult.append("BusinessDay: ").append(businessDay.toString()).append("\n\n");
        }

        strResult.append("Pickup Counts: \n"+pickupCounts+"\n");
        strResult.append("Loan Counts: \n"+loanCounts+"\n");
        strResult.append("Deposit Counts: \n"+depositCounts+"\n");
        strResult.append("Current Operational Fund Counts: \n"+ currentOperatingFunds +"\n");
        strResult.append("Open Operational Fund Counts: \n"+ openOperatingFunds +"\n");
        strResult.append("Close Operational Fund Counts: \n"+ closeOperatingFunds +"\n");

        // pass back result
        return(strResult.toString());

    }                                   // end toString()



}                                       // end class StoreSafe
