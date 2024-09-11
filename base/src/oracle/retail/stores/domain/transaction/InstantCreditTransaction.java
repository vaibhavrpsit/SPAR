/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/InstantCreditTransaction.java /main/14 2013/11/16 07:07:51 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   11/16/13 - fixed a null pointer
 *    asinton   11/05/13 - prevent null pointer exception when getInstantCredit
 *                         returns null
 *    sgu       08/23/11 - check nullpointer for approval status
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         4/12/2008 5:44:57 PM   Christian Greene
 *       Upgrade StringBuffer to StringBuilder
 *  4    360Commerce 1.3         6/8/2006 6:11:44 PM    Brett J. Larsen CR
 *       18490 - UDM - InstantCredit AuthorizationResponseCode changed to a
 *       String
 *  3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:22:08 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:25 PM  Robert Pearse
 *
 * Revision 1.7  2004/09/23 00:30:51  kmcbride
 * @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 * Revision 1.6  2004/04/27 20:01:17  jdeleau
 * @scr 4218 Add in the concrete calls for register reports data, refactor
 * the houseCardEnrollment methods to be in line with other FinancialTotals
 * methods.
 *
 * Revision 1.5  2004/04/02 23:07:34  jdeleau
 * @scr 4218 Register Reports - House Account and initial changes to
 * the way SummaryReports are built.
 *
 * Revision 1.4  2004/02/17 16:18:52  rhafernik
 * @scr 0 log4j conversion
 *
 * Revision 1.3  2004/02/12 17:14:42  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 23:28:51  bwf
 * @scr 0 Organize imports.
 *
 * Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 21 2003 15:22:48   nrao
 * Javadoc.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

// Foundation Imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 *   Instant Credit Transaction, used for house credit
 *   enrollments.
 *   @version $Revision: /main/14 $
 */
public class InstantCreditTransaction extends Transaction
implements InstantCreditTransactionIfc
{                                       // begin class InstantCreditTransaction
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3948917850733964426L;

    /**
     *  revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/14 $";
    /**
     *  customer information
     */
    protected CustomerIfc customer = null;

    /**
     *  Instant Credit Card
     */
    protected InstantCreditIfc instantCredit = null;

    /**
     *  Constructs Instant Credit Transaction object. <P>
     */
    public InstantCreditTransaction()
    {                                   // begin InstantCreditTransaction()
    }                                   // end InstantCreditTransaction()

    /**
     *  Creates clone of this object. <P>
     *  @return Object clone of this object
     */
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        InstantCreditTransaction c = new InstantCreditTransaction();

        // set values
        setCloneAttributes(c);

        // pass back Object
        return c;
    }                                   // end clone()

    /**
     *  Sets attributes in clone of this object. <P>
     *  @param newClass new instance of object
     */
    public void setCloneAttributes(InstantCreditTransaction newClass)
    {                                   // begin setCloneAttributes()
        if (customer != null)
        {
            newClass.setCustomer((CustomerIfc) getCustomer().clone());
        }
        if(instantCredit != null)
        {
            newClass.setInstantCredit(this.getInstantCredit());
        }
    }                                   // end setCloneAttributes()

    /**
     *  Determine if two objects are identical. <P>
     *  @param obj object to compare with
     *  @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {                                   // begin equals()
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof InstantCreditTransaction)
        {                                   // begin compare objects

            InstantCreditTransaction c = (InstantCreditTransaction) obj;      // downcast the input object

            // compare all the attributes of InstantCreditTransaction
            if (Util.isObjectEqual(getCustomer(), c.getCustomer()) &&
                Util.isObjectEqual(this.getInstantCredit(), c.getInstantCredit()))
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

    /**
     *  Retrieves customer information. <P>
     *  @return customer information
     */
    public CustomerIfc getCustomer()
    {                                   // begin getCustomer()
        return(customer);
    }                                   // end getCustomer()

    /**
     *  Sets customer information. <P>
     *  @param value  customer information
     */
    public void setCustomer(CustomerIfc value)
    {                                   // begin setCustomer()
        customer = value;
    }                                   // end setCustomer()

    /**
        Returns default display string. <P>
     * @return String respresenting this object
     */
    public String toString()
    {                                   // begin toString()
        // build result string
        String parentString = super.toString();
        StringBuilder strResult =
          Util.classToStringHeader("InstantCreditTransaction",
                                    getRevisionNumber(),
                                    hashCode());
        strResult.append("Class:  InstantCreditTransaction (Revision ")
                 .append(getRevisionNumber())
                 .append(") @")
                 .append(hashCode())
                 .append(Util.EOL);
        // add attributes to string
        strResult.append(parentString);
        if (getCustomer() == null)
        {
            strResult.append("customer:                           [null]")
                     .append(Util.EOL);
        }
        else
        {
            strResult.append(getCustomer().toString());
        }

        if (this.getInstantCredit() == null)
        {
            strResult.append("instantCredit:                     [null]")
                     .append(Util.EOL);
        }
        else
        {
            strResult.append(this.getInstantCredit().toString());
        }

        // pass back result

        return(strResult.toString());
    }                                   // end toString()

    /**
     * Retrieves the source-code-control system revision number. <P>
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()


    /**
     * Get the instant credit card
     *
     * @return instantCredit
     */
    public InstantCreditIfc getInstantCredit()
    {
        return this.instantCredit;
    }

    /**
     * Set the instant credit object
     * @param instantCredit InstantCredit object to use
     */
    public void setInstantCredit(InstantCreditIfc instantCredit)
    {
        this.instantCredit = instantCredit;
    }

    /**
     * Implement the abstract getFinancialTotals method, so that the
     * financial totals can be calculated for use in Register Reports.
     *
     *  @return Financial totals
     */
    public FinancialTotalsIfc getFinancialTotals()
    {
        FinancialTotalsIfc totals = DomainGateway.getFactory().getFinancialTotalsInstance();
        if(getInstantCredit() != null)
        {
            if (InstantCreditApprovalStatus.APPROVED.equals(getInstantCredit().getApprovalStatus()))
            {
                totals.addHouseCardEnrollmentsApproved(1);
            }
            else
            {
                totals.addHouseCardEnrollmentsDeclined(1);
            }
        }
        return totals;

    }
}                                       // end class InstantCreditTransaction
