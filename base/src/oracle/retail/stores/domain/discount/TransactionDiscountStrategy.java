/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/TransactionDiscountStrategy.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    amishash  09/29/14 -Added a boolean to indicate whether the system
 *                         discount is applied or not
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    akandru   10/31/08 - EJ Changes_I18n
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:34 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:21 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:13 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/03/17 22:23:57  cdb
 *   @scr 3588 Updated setCloneAttributes to preserve employee.
 *
 *   Revision 1.5  2004/03/02 23:38:07  cdb
 *   @scr 3588 Added unit testing of TransactionDiscountStrategies
 *   and corrected bug that the resulting unit tests caught.
 *
 *   Revision 1.4  2004/03/02 22:44:28  cdb
 *   @scr 3588 Added ability to save Employee ID associated
 *   with a discount at the transaction level.
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
 *    Rev 1.0   Aug 29 2003 15:35:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:50:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:58:36   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:18:50   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:13:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

// foundation imports
import java.util.Locale;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;

//--------------------------------------------------------------------------
/**
    Transaction discount strategy abstract class. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see oracle.retail.stores.domain.discount.DiscountRule
    @see oracle.retail.stores.domain.transaction.TransactionDiscountStrategyIfc
**/
//--------------------------------------------------------------------------
public abstract class TransactionDiscountStrategy
extends DiscountRule
implements TransactionDiscountStrategyIfc
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     discount employee
     **/
    protected EmployeeIfc discountEmployee = null;
    
    /**
     * Indicates whether this system discount is applied or not 
     */
    protected boolean systemDiscountApplied = false;

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean equals(Object obj)
    {
        boolean objectIsEqual = (obj instanceof TransactionDiscountStrategy)
                               ?
                    super.equals(obj) : false;
        if (objectIsEqual)
        {
            TransactionDiscountStrategy otherStrategy = (TransactionDiscountStrategy)obj;
            objectIsEqual = (getDiscountEmployeeID().equals(otherStrategy.getDiscountEmployeeID()) && isSystemDiscountApplied() == otherStrategy
                    .isSystemDiscountApplied());
        }
        return objectIsEqual;
    }

    //---------------------------------------------------------------------
    /**
        Clone this object.   <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>Object cloned and returned
        </UL>
        @return generic object copy of this object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {
        TransactionDiscountStrategyIfc newDiscount = null;

        try
        {
            newDiscount = (TransactionDiscountStrategyIfc)getClass().newInstance();
            setCloneAttributes(newDiscount);
        }
        catch (Exception e)
        {
            // doing nothing will return null
        }

        return(newDiscount);
    }

    //---------------------------------------------------------------------
    /**
            Sets attributes in clone. <P>
    @param newClass new instance of class
    **/
    //---------------------------------------------------------------------
    protected void setCloneAttributes(TransactionDiscountStrategyIfc newClass)
    {
        super.setCloneAttributes((DiscountRule) newClass);
        newClass.setDiscountEmployee(discountEmployee);
    }

    //---------------------------------------------------------------------
    /**
         Restores the object from the contents of the xml tree based on the
         current node property of the converter.
         @param converter is the conversion utility
                 @exception XMLConversionException if error occurs translating XML
    **/
    //---------------------------------------------------------------------
    public abstract void translateFromElement(XMLConverterIfc converter) throws XMLConversionException;

    //---------------------------------------------------------------------
    /**
        Returns journal string for this object. <P>
                @return journal string
                @deprecated new method added to take the journal locale from client.
    **/
    //---------------------------------------------------------------------
    public abstract String toJournalString();

    // ---------------------------------------------------------------------
    /**
        Returns journal string for this object. <P>
        @param journalLocale client's journal locale
        @return journal string
    **/
    //---------------------------------------------------------------------
    public abstract String toJournalString(Locale journalLocale);

    //---------------------------------------------------------------------
    /**
     Sets employee discount employee. <P>
     @param value employee discount employee
     **/
    //---------------------------------------------------------------------
    public void setDiscountEmployee(EmployeeIfc value)
    {
        discountEmployee = value;
    }

    //---------------------------------------------------------------------
    /**
     Sets employee discount employee. <P>
     @param value employee discount employee ID
     **/
    //---------------------------------------------------------------------
    public void setDiscountEmployee(String value)
    {
        setDiscountEmployee(DomainGateway.getFactory().getEmployeeInstance());
        getDiscountEmployee().setEmployeeID(value);
    }

    //---------------------------------------------------------------------
    /**
     Retrieves employee discount employee. <P>
     @return employee discount employee
     **/
    //---------------------------------------------------------------------
    public EmployeeIfc getDiscountEmployee()
    {
        return discountEmployee;
    }

    //---------------------------------------------------------------------
    /**
     Returns identifier for employee discount employee
     @return identifier for employee discount employee
     **/
    //---------------------------------------------------------------------
    public String getDiscountEmployeeID()
    {                                   // begin getAuthorizingEmployeeID()
        String employeeID = "";
        if (getDiscountEmployee() != null)
        {
            employeeID = getDiscountEmployee().getEmployeeID();
        }
        return(employeeID);
    }                                   // end getAuthorizingEmployeeID()
    
    
    /**
     * Returns whether system discount is applied or not
     * @return
     */
    public boolean isSystemDiscountApplied()
    {
        return systemDiscountApplied;
    }

    /**
     * Set systemDiscountApplied
     * @param systemDiscountApplied
     */
    public void setSystemDiscountApplied(boolean systemDiscountApplied)
    {
        this.systemDiscountApplied = systemDiscountApplied;
    }

}
