/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/CustomerDiscountByPercentage.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/21/14 - removed low hanging deprecations.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    acadar    11/02/08 - updates to unit tests
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         1/22/2006 11:41:28 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:27:35 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:20:35 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:20 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 17:13:28  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:27  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:34:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:49:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:57:36   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:17:54   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 05 2002 16:34:14   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.1   13 Nov 2001 07:03:16   mpm
 * Installed support for ItemContainerProxy.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 20 2001 16:12:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;
// java imports
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;

import java.math.BigDecimal;

//------------------------------------------------------------------------------
/**
    This class handles a customer-based discount by percentage strategy. It
    is derived from the standard transaction-based discount by percentage.<P>

    @see oracle.retail.stores.domain.transaction.TransactionDiscountByPercentageStrategy
    @see oracle.retail.stores.domain.discount.CustomerDiscountByPercentageIfc
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class CustomerDiscountByPercentage
extends TransactionDiscountByPercentageStrategy
implements CustomerDiscountByPercentageIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2882557506777312378L;
    
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
        Constructs CustomerDiscountByPercentage object. <P>
    **/
    //---------------------------------------------------------------------
    public CustomerDiscountByPercentage()
    {
        super();
        includedInBestDeal = false;
    }

    //---------------------------------------------------------------------
    /**
        Constructs CustomerDiscountByPercentage object,
        setting rate and reason code attributes. <P>
        @param rate discount rate
        @param reason code
        @deprecated as of 13.1. Use {@link CustomerDiscountByPercentage(BigDecimal rate, LocalizedCode reason)}
    **/
    //---------------------------------------------------------------------
    public CustomerDiscountByPercentage(BigDecimal rate, int reason)
    {
        super(rate, reason);
        includedInBestDeal = false;
    }

    /**
        Constructs CustomerDiscountByPercentage object,
        setting rate and reason code attributes. <P>
        @param rate discount rate
        @param reason code
    */
    public CustomerDiscountByPercentage(BigDecimal rate, LocalizedCodeIfc reason)
    {
        super(rate, reason);
        includedInBestDeal = false;
    }


    //---------------------------------------------------------------------
    /**
        Constructs CustomerDiscountByPercentage object,
        setting rate and reason code attributes. <P>
        @param rate discount rate
        @param reason code
        @param exclusiveFlag exclusive indicator
        @deprecated as of 13.1. Use {@link CustomerDiscountByPercentage(BigDecimal rate,
                                        LocalizedCodeIfc reason,
                                        boolean includedInBestDealFlag)}

    **/
    //---------------------------------------------------------------------
    public CustomerDiscountByPercentage(BigDecimal rate,
                                        int reason,
                                        boolean includedInBestDealFlag)
    {
        super(rate, reason);
        includedInBestDeal = includedInBestDealFlag;
    }

    /**
    Constructs CustomerDiscountByPercentage object,
    setting rate and reason code attributes. <P>
    @param rate discount rate
    @param reason code
    @param exclusiveFlag exclusive indicator
    */
    public CustomerDiscountByPercentage(BigDecimal rate,
                                        LocalizedCodeIfc reason,
                                        boolean includedInBestDealFlag)
    {
        super(rate, reason);
        includedInBestDeal = includedInBestDealFlag;
    }
    //---------------------------------------------------------------------
    /**
        Constructs CustomerDiscountByPercentage object,
        setting rate and reason code attributes. <P>
        @param rate discount rate
        @param reason code
        @param ruleID rule identifier
        @param exclusiveFlag exclusive indicator
        @deprecated as of 13.1. Use {@link CustomerDiscountByPercentage(BigDecimal rate,
                                        LocalizedCodeIfc reason,
                                        String ruleID,
                                        boolean includedInBestDealFlag)
    **/
    //---------------------------------------------------------------------
    public CustomerDiscountByPercentage(BigDecimal rate,
                                        int reason,
                                        String ruleID,
                                        boolean includedInBestDealFlag)
    {
        super(rate, reason);
        this.ruleID = ruleID;
        includedInBestDeal = includedInBestDealFlag;
    }

    /**
        Constructs CustomerDiscountByPercentage object,
        setting rate and reason code attributes. <P>
        @param rate discount rate
        @param reason code
        @param ruleID rule identifier
        @param exclusiveFlag exclusive indicator

    */
     public CustomerDiscountByPercentage(BigDecimal rate,
                                        LocalizedCodeIfc reason,
                                        String ruleID,
                                        boolean includedInBestDealFlag)
    {
        super(rate, reason);
        this.ruleID = ruleID;
        includedInBestDeal = includedInBestDealFlag;
    }


    //---------------------------------------------------------------------
    /**
        Initializes this object with specified values. <P>
        @param rate discount rate
        @param reason code
        @param rule rule identifier
        @param exclusiveFlag exclusive indicator
        @deprecated as of 13.1. Use {@link initialize(BigDecimal rate, LocalizedCodeIfc reason, String rule, boolean includedInBestDealFlag)}
    **/
    //---------------------------------------------------------------------
   public void initialize(BigDecimal rate,
                           int reason,
                           String rule,
                           boolean includedInBestDealFlag)
    {
        setDiscountRate(rate);
        setReasonCode(reason);
        setRuleID(rule);
        setIncludedInBestDeal(includedInBestDealFlag);
    }

    /**
     * Initializes this object with specified values. <P>
     * @param rate discount rate
     * @param reason code
     * @param rule rule identifier
     * @param exclusiveFlag exclusive indicator
    */
    public void initialize(BigDecimal rate, LocalizedCodeIfc reason, String rule, boolean includedInBestDealFlag)
    {
        setDiscountRate(rate);
        setReason(reason);
        setRuleID(rule);
        setIncludedInBestDeal(includedInBestDealFlag);
    }

    //---------------------------------------------------------------------
    /**
        Clones this object. <P>
        @return this object as Object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        CustomerDiscountByPercentageIfc newClass =
                                          new CustomerDiscountByPercentage();
        setCloneAttributes(newClass);

        return newClass;
    }

    //---------------------------------------------------------------------
    /**
        Retrieves discount assignment basis. <P>
        @return discount assignment basis
    **/
    //---------------------------------------------------------------------
    public int getAssignmentBasis()
    {
        return(ASSIGNMENT_CUSTOMER);
    }

    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        StringBuilder strResult =
          new StringBuilder("Class:  CustomerDiscountByPercentage (Revision ");
        strResult.append(getRevisionNumber())
                 .append(") @")
                 .append(hashCode())
                 .append(Util.EOL)
                 .append("includedInBestDeal:                  [")
                 .append(isIncludedInBestDeal())
                 .append("]")
                 .append(Util.EOL)
                 .append(super.toString());

        // pass back result
        return(strResult.toString());
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number of this class. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
        CustomerDiscountByPercentage main method. <P>
        @param args  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        CustomerDiscountByPercentage obj =
          new CustomerDiscountByPercentage();
        // output toString()
        System.out.println(obj.toString());
    }                                  // end main()
}
