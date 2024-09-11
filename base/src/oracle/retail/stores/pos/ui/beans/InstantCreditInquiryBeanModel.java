/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/InstantCreditInquiryBeanModel.java /rgbustores_13.4x_generic_branch/2 2011/05/24 19:03:16 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/23/11 - move inquiry for payment into instantcredit service
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:51:32 AM   Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:08 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:25 PM  Robert Pearse
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 20 2003 17:43:28   nrao
 * Added first name, last name and account number variables with setters and getters.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// domain imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDate;

//-----------------------------------------------------------------------------------
/**
    Data transport between the bean and the application for instant credit card data
**/
//-----------------------------------------------------------------------------------
public class InstantCreditInquiryBeanModel extends POSBaseBeanModel
{
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    // first name
    protected String firstName;
    // last name
    protected String lastName;
    // account Number
    protected String accountNumber;
    // current balance on instant credit card
    protected CurrencyIfc currentBalance;
    // credit limit on instant credit card
    protected CurrencyIfc creditLimit;
    // credit available on instant credit card
    protected CurrencyIfc creditAvailable;

   //-------------------------------------------------------------------------
   /*
    * Gets the current balance on instant credit card
    */
   //-------------------------------------------------------------------------
    public CurrencyIfc getCurrentBalance()
    {
        return currentBalance;
    }

    //------------------------------------------------------------------------
    /*
     * Sets the current balance on instant credit card
     */
    //------------------------------------------------------------------------
    public void setCurrentBalance(CurrencyIfc currentBalance)
    {
        this.currentBalance = currentBalance;
    }

    //------------------------------------------------------------------------
    /*
     * Gets the credit limit on instant credit card
     */
    //------------------------------------------------------------------------
    public CurrencyIfc getCreditLimit()
    {
        return creditLimit;
    }

    //------------------------------------------------------------------------
    /*
     * Sets the credit limit on instant credit card
     */
    //------------------------------------------------------------------------
    public void setCreditLimit(CurrencyIfc creditLimit)
    {
        this.creditLimit = creditLimit;
    }

    //------------------------------------------------------------------------
    /*
     * Gets the credit available on instant credit card
     */
    //------------------------------------------------------------------------
    public CurrencyIfc getCreditAvailable()
    {
        return creditAvailable;
    }

    //------------------------------------------------------------------------
    /*
     * Sets the credit available on instant credit card
     */
    //------------------------------------------------------------------------
    public void setCreditAvailable(CurrencyIfc creditAvailable)
    {
        this.creditAvailable = creditAvailable;
    }

    //------------------------------------------------------------------------
    /**
     * Gets the Account number
     * @return accountNumber as String
     */
    //------------------------------------------------------------------------
    public String getAccountNumber()
    {
        return accountNumber;
    }

    //------------------------------------------------------------------------
    /**
     * Gets the first name
     * @return firstName as String
     */
    //------------------------------------------------------------------------
    public String getFirstName()
    {
        return firstName;
    }

    //------------------------------------------------------------------------
    /**
     * Gets the last name
     * @return lastName as String
     */
    //------------------------------------------------------------------------
    public String getLastName()
    {
        return lastName;
    }

    //------------------------------------------------------------------------
    /**
     * Sets the account number
     * @param string Account Number
     */
    //------------------------------------------------------------------------
    public void setAccountNumber(String string)
    {
        accountNumber = string;
    }

    //------------------------------------------------------------------------
    /**
     * Sets the first name
     * @param string First Name
     */
    //------------------------------------------------------------------------
    public void setFirstName(String string)
    {
        firstName = string;
    }

    //------------------------------------------------------------------------
    /**
     * Gets the last name
     * @param string Last Name
     */
    //------------------------------------------------------------------------
    public void setLastName(String string)
    {
        lastName = string;
    }

}
