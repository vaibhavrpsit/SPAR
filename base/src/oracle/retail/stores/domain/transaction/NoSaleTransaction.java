/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/NoSaleTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:46 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   11/07/08 - I18N - updated toString()
 *    mdecama   11/07/08 - I18N - Fixed Clone Method
 *    mdecama   10/21/08 - I18N - Localizing No Sale ReasonCode

     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:09 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:42 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:46 PM  Robert Pearse
     $
     Revision 1.5  2004/09/23 00:30:51  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.4  2004/07/14 15:53:11  kll
     @scr 6239: use super's toString

     Revision 1.3  2004/02/12 17:14:42  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:28:50  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:40:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 17:05:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:30   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:30:16   msg
 * Initial revision.
 *
 *    Rev 1.1   12 Mar 2002 09:38:00   pjf
 * Call factory for new financial totals instance.
 * Resolution for POS SCR-1550: Use Factory to get new object instances in POS & Domain
 *
 *    Rev 1.0   Sep 20 2001 16:05:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:40:06   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.transaction;
// java imports
import java.io.Serializable;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.utility.Util;
//--------------------------------------------------------------------------
/**
    A non-sales transaction that conducts no business, but causes the till
    drawer to open.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class NoSaleTransaction
extends Transaction
implements NoSaleTransactionIfc, Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -732359412494583203L;

    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        reason code
        @deprecated as of 13.1 Use {@link #localizedReasonCode}
    **/
    protected String reasonCode;

    LocalizedCodeIfc localizedReasonCode = null;

    //---------------------------------------------------------------------
    /**
        Constructs NoSaleTransaction object.
    **/
    //---------------------------------------------------------------------
    public NoSaleTransaction()
    {
        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Constructs a new, usable NoSaleTransaction object.
    **/
    //---------------------------------------------------------------------
    public NoSaleTransaction(WorkstationIfc station)
    {
        super(station);
        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Initializes the protected data members of the object.
    **/
    //---------------------------------------------------------------------
    protected void initialize()
    {
        localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
        setTransactionType(TransactionIfc.TYPE_NO_SALE);
    }

    //---------------------------------------------------------------------
    /**
        Calculates FinancialTotals based on current transaction. <P>
        @return FinancialTotalsIfc object
    **/
    //---------------------------------------------------------------------
    public FinancialTotalsIfc getFinancialTotals()
    {                                   // begin getFinancialTotals()
        FinancialTotalsIfc financialTotals =
          DomainGateway.getFactory().getFinancialTotalsInstance();
        financialTotals.setNumberNoSales(1);
        return(financialTotals);
    }                                   // end getFinancialTotals()

    //---------------------------------------------------------------------
    /**
        Returns the reason code.
        <p>
        @return The reason code.
        @deprecated as of 13.1 Use {@link #getLocalizedReasonCode().getCode()}
    **/
    //---------------------------------------------------------------------
    public String getReasonCode()
    {
        if (localizedReasonCode != null)
            return localizedReasonCode.getCode();
        else
            return null;

    }

    //---------------------------------------------------------------------
    /**
        Sets the reason code.
        <p>
        @param  code     The reason code.
        @deprecated as of 13.1 Use {@link #setLocalizedReasonCode(LocalizedCodeIfc).setCode()}
    **/
    //---------------------------------------------------------------------
    public void setReasonCode(String code)
    {
        if (localizedReasonCode == null)
        {
            localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
        }
        localizedReasonCode.setCode(code);
    }

    //---------------------------------------------------------------------
    /**
        Clones NoSaleTransaction object.
        <p>
        @return instance of NoSaleTransaction object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        // instantiate new object
        NoSaleTransaction trans = new NoSaleTransaction();

                setCloneAttributes(trans);

        // pass back object
        return((Object)trans);
    }

    // ---------------------------------------------------------------------
    /**
     * Sets attributes in new instance of class.
     * <P>
     *
     * @param newClass new instance of class
     */
    // ---------------------------------------------------------------------
    public void setCloneAttributes(NoSaleTransaction newClass)
    { // begin setCloneAttributes()
        // set attributes in super class
        super.setCloneAttributes(newClass);
        // set attributes
        if (localizedReasonCode != null)
        {
            newClass.setLocalizedReasonCode((LocalizedCodeIfc) localizedReasonCode.clone());
        }
    } // end setCloneAttributes()

    // ---------------------------------------------------------------------
    /**
        Returns string representation of object.
        <p>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        StringBuilder strResult =
          new StringBuilder("Class:  NoSaleTransaction (Revision ");
                strResult.append(getRevisionNumber())
                         .append(") @").append(hashCode())
                                 .append(Util.EOL)
                 .append("Transaction type:   [")
                 .append(transactionType).append("]").append(Util.EOL)
                 .append("Transaction status: [")
                 .append(transactionStatus).append("]").append(Util.EOL)
                 .append("Business day        [")
                 .append(businessDay).append("]").append(Util.EOL)
                 .append("Start timestamp:    [")
                 .append(timestampBegin).append("]").append(Util.EOL)
                 .append("End timestamp:      [")
                 .append(timestampEnd).append("]").append(Util.EOL)
                 .append("Reason Code:        [")
                 .append(localizedReasonCode).append("]")
                 .append(super.toString());;
        // pass back result
        return(strResult.toString());
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number.
        <p>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
        NoSaleTransaction main method.
        <p>
        @param args     command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {
        // instantiate class
        NoSaleTransaction t = new NoSaleTransaction();
        System.out.println(t.getRevisionNumber());
    }

    //---------------------------------------------------------------------
    /**
        Build test transaction.
        <P>
        @return NoSaleTransaction object
    **/
    //---------------------------------------------------------------------
    public static NoSaleTransaction buildTestTransaction()
    {
        // instantiate class
        NoSaleTransaction t = new NoSaleTransaction();

        // pass back transaction
        return(t);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.NoSaleTransactionIfc#getLocalizedReasonCode()
     */
    public LocalizedCodeIfc getLocalizedReasonCode()
    {
        return localizedReasonCode;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.NoSaleTransactionIfc#getLocalizedReasonCode(java.util.Locale)
     */
    public String getLocalizedReasonCode(Locale lcl)
    {
        Locale bestMatch = LocaleMap.getBestMatch(lcl);
        return localizedReasonCode.getText(bestMatch);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.NoSaleTransactionIfc#setLocalizedReasonCode(oracle.retail.stores.common.utility.LocalizedCodeIfc)
     */
    public void setLocalizedReasonCode(LocalizedCodeIfc localizedReasonCode)
    {
        this.localizedReasonCode = localizedReasonCode;
    }


}
