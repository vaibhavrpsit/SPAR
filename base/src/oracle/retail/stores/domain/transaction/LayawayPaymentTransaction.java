/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/LayawayPaymentTransaction.java /main/13 2013/07/09 14:32:43 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/09/13 - Fixed issues saving the cash adjustment total to the
 *                         history tables for order, layaway, redeem and voided
 *                         transactions.
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/16/2007 7:56:04 PM   Brett J. Larsen
 *         CR 26903 - 8.0.1 merge to trunk
 *
 *         BackOffice <ARG> Summary Report overhaul (many CRs fixed)
 *
 *    4    360Commerce 1.3         6/8/2006 6:11:44 PM    Brett J. Larsen CR
 *         18490 - UDM - InstantCredit AuthorizationResponseCode changed to a
 *         String
 *    3    360Commerce 1.2         3/31/2005 4:28:50 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:30:51  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.4  2004/02/17 16:18:52  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:14:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:28:50  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Nov 21 2003 16:58:14   nrao
 * Added Javadoc.
 *
 *    Rev 1.1   Nov 03 2003 18:24:30   nrao
 * Added implements InstantCreditTransactionIfc and related methods.
 *
 *    Rev 1.0   Aug 29 2003 15:40:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 17:05:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:26   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:30:12   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:06:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:40:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;
// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This class describes a layaway payment transaction.  A layaway payment
     transaction reflects a payment on a layaway.  This is not used for the
     initial and final payments on a layaway (which reflect movement of stock).
     This is only used for the intermediate payments. <P>
     @see oracle.retail.stores.domain.financial.LayawayIfc
     @see oracle.retail.stores.domain.transaction.LayawayTransactionIfc
     @see oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc
     @see oracle.retail.stores.domain.transaction.PaymentTransactionIfc
     @version $Revision: /main/13 $
**/
//----------------------------------------------------------------------------
public class LayawayPaymentTransaction
extends PaymentTransaction
implements LayawayPaymentTransactionIfc, InstantCreditTransactionIfc
{                                       // begin class LayawayPaymentTransaction
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7635593625345792158L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/13 $";
    /**
        layaway associated with this transaction
    **/
    protected LayawayIfc layaway = null;

    /**
     * Instant Credit Enrollment
     */
    protected InstantCreditIfc instantCredit = null;

    //----------------------------------------------------------------------------
    /**
        Constructs LayawayPaymentTransaction object  <P>
    **/
    //----------------------------------------------------------------------------
    public LayawayPaymentTransaction()
    {                                   // begin LayawayPaymentTransaction()
    }                                   // end LayawayPaymentTransaction()

    //----------------------------------------------------------------------------
    /**
        Initilaizes LayawayPaymentTransaction object
        using the seed transaction  <P>
    **/
    //----------------------------------------------------------------------------
    public void initialize(TransactionIfc transaction)
    {                                   // begin LayawayPaymentTransaction()
        transaction.setTransactionAttributes(this);
    }                                   // end LayawayPaymentTransaction()


    //----------------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //----------------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        LayawayPaymentTransaction c = new LayawayPaymentTransaction();

        // set values
        setCloneAttributes(c);

        // pass back Object
        return((Object) c);
    }                                   // end clone()

    //----------------------------------------------------------------------------
    /**
        Sets attributes in clone of this object. <P>
        @param newClass new instance of object
    **/
    //----------------------------------------------------------------------------
    protected void setCloneAttributes(LayawayPaymentTransaction newClass)
    {                                   // begin setCloneAttributes()
        super.setCloneAttributes(newClass);
        if (layaway != null)
        {
            newClass.setLayaway((LayawayIfc) getLayaway().clone());
        }
    }                                   // end setCloneAttributes()

    //----------------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //----------------------------------------------------------------------------
    public boolean equals(Object obj)
    {                                   // begin equals()
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof LayawayPaymentTransaction)
        {                                   // begin compare objects
            // downcast the input object
            LayawayPaymentTransaction c = (LayawayPaymentTransaction) obj;
            // compare all the attributes of Layaway
            if  (Util.isObjectEqual(getLayaway(), c.getLayaway()))
            {
                isEqual = true;             // set the return code to true
            }
            else
            {
                isEqual = false;            // set the return code to false
            }
        }                                   // end compare objects
        else
        {
             isEqual = false;
        }
        return(isEqual);
    }                                   // end equals()

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

        // since this is assumed to a middle payment (neither initiate
        // nor eliminate), only the payment need by handled
        financialTotals.addAmountLayawayPayments(getPaymentAmount());
        financialTotals.addCountLayawayPayments(1);

        // get totals from tender line items
        financialTotals.add
          (getTenderFinancialTotals(getTenderLineItems(),
                                    getTenderTransactionTotals()));

        // Add the rounded (cash) change amount to the financial totals object.
        if (getTenderTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.NEGATIVE)
        {
            financialTotals.addAmountChangeRoundedOut(getTenderTransactionTotals().getCashChangeRoundingAdjustment().abs());
        }
        if (getTenderTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.POSITIVE)
        {
            financialTotals.addAmountChangeRoundedIn(getTenderTransactionTotals().getCashChangeRoundingAdjustment().abs());
        }

        return(financialTotals);
    }                                   // end getFinancialTotals()

    //----------------------------------------------------------------------------
    /**
        Retrieves layaway associated with this transaction. <P>
        @return layaway associated with this transaction
    **/
    //----------------------------------------------------------------------------
    public LayawayIfc getLayaway()
    {                                   // begin getLayaway()
        return(layaway);
    }                                   // end getLayaway()

    //----------------------------------------------------------------------------
    /**
        Sets the layaway associated with this transaction. <P>
        @param value  layaway associated with this transaction
    **/
    //----------------------------------------------------------------------------
    public void setLayaway(LayawayIfc value)
    {                                   // begin setLayaway()
        layaway = value;
    }                                   // end setLayaway()

    /*
     *  get instant credit
     *  @return instant credit
     */
    public InstantCreditIfc getInstantCredit()
    {
        return this.instantCredit;
    }

    /*
     * set instant credit
     * @param instantCredit instant credit
     */
    public void setInstantCredit(InstantCreditIfc instantCredit)
    {
        this.instantCredit = instantCredit;
    }


    //----------------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        StringBuilder strResult =
          Util.classToStringHeader("LayawayPaymentTransaction",
                                    getRevisionNumber(),
                                    hashCode());

        strResult.append(Util.formatToStringEntry("layaway",
                                                  getLayaway()))
                 .append(super.toString());
        // pass back result
        return(strResult.toString());
    }                                   // end toString()

    //----------------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()


    //----------------------------------------------------------------------------
    /**
        Layawaymain method. <P>
        @param String args[]  command-line parameters
    **/
    //----------------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        LayawayPaymentTransaction c = new LayawayPaymentTransaction();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class LayawayPaymentTransaction
