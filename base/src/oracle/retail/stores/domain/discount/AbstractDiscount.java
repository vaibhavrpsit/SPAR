/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/AbstractDiscount.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:43 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:26 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:19 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:28  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:34:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:49:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:57:18   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:17:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:12:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:37:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This is the abstract class for discount classes. This class handles reason
 * code and the enabled flag.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @deprecated This functionality has been replaced by that in
 *             oracle.retail.stores.domain.discount.DiscountRule. Subclasses of
 *             AbstractDiscount will now extend DiscountRule instead.
 */
public abstract class AbstractDiscount implements DiscountRuleConstantsIfc
{
    /**
     * revision number supplied by source-code control system
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * reason code for the discount
     **/
    protected int reasonCode = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;

    /**
     * enabled flag
     **/
    protected boolean enabled = true;

    // ---------------------------------------------------------------------
    /**
     * Sets attributes in new instance of class.
     * <P>
     * 
     * @param newClass new instance of class
     **/
    // ---------------------------------------------------------------------
    protected void setCloneAttributes(AbstractDiscount newClass)
    { // begin setCloneAttributes()
        newClass.reasonCode = reasonCode;
        newClass.enabled = enabled;
    } // end setCloneAttributes()

    // ----------------------------------------------------------------------------
    /**
     * Retrieves reason code
     * <P>
     * 
     * @return reason code
     **/
    // ----------------------------------------------------------------------------
    public int getReasonCode()
    { // begin getReasonCode()
        return (reasonCode);
    } // end getReasonCode()

    // ----------------------------------------------------------------------------
    /**
     * Sets reason code
     * <P>
     * 
     * @param value reason code
     **/
    // ----------------------------------------------------------------------------
    public void setReasonCode(int value)
    { // begin setReasonCode()
        reasonCode = value;
    } // end setReasonCode()

    // ----------------------------------------------------------------------------
    /**
     * Retrieves enabled flag
     * <P>
     * 
     * @return enabled flag
     **/
    // ----------------------------------------------------------------------------
    public boolean getEnabled()
    { // begin getEnabled()
        return (enabled);
    } // end getEnabled()

    // ----------------------------------------------------------------------------
    /**
     * Retrieves enabled flag
     * <P>
     * 
     * @return enabled flag
     **/
    // ----------------------------------------------------------------------------
    public boolean isEnabled()
    { // begin isEnabled()
        return (getEnabled());
    } // end isEnabled()

    // ----------------------------------------------------------------------------
    /**
     * Sets enabled flag
     * <P>
     * 
     * @param value enabled flag
     **/
    // ----------------------------------------------------------------------------
    public void setEnabled(boolean value)
    { // begin setEnabled()
        enabled = value;
    } // end setEnabled()

    // ---------------------------------------------------------------------
    /**
     * Retrieves discount assignment basis.
     * <P>
     * 
     * @return discount assignment basis
     **/
    // ---------------------------------------------------------------------
    public int getAssignmentBasis()
    {
        return (ASSIGNMENT_MANUAL);
    }

    // --------------------------------------------------------------------------
    /**
     * Determine whether the provided object is the same type and has the same
     * field values as this one.
     * <P>
     * 
     * @param obj the object to compare
     * @return true if the fields are equal; false otherwise
     **/
    // --------------------------------------------------------------------------
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        // Only test for equality if the objects are instances of the
        // same class.
        if (obj instanceof AbstractDiscount)
        {
            AbstractDiscount strategy = (AbstractDiscount) obj;
            if (getReasonCode() == strategy.getReasonCode() && getEnabled() == strategy.getEnabled()
                    && getAssignmentBasis() == strategy.getAssignmentBasis())
            {
                isEqual = true;
            }
        }
        return isEqual;
    }

    // ---------------------------------------------------------------------
    /**
     * Method to default display string function.
     * <P>
     * 
     * @return String representation of object
     **/
    // ---------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        StringBuffer strResult = new StringBuffer("Class:  AbstractDiscount");
        strResult.append(" (Revision ").append(getRevisionNumber()).append(") @").append(hashCode()).append(Util.EOL)
                .append("reason code:                    [").append(getReasonCode()).append("]").append(Util.EOL)
                .append("enabled:                        [").append(getEnabled()).append("]").append(Util.EOL);
        // pass back result
        return (strResult.toString());
    } // end toString()

    // ---------------------------------------------------------------------
    /**
     * Retrieves the Team Connection revision number.
     * <P>
     * 
     * @return String representation of revision number
     **/
    // ---------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()

} // end class AbstractDiscount

