/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/TransactionDiscountStrategy.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:47 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:34 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:21 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:13 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:51  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:14:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:28:51  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:41:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 17:06:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:12   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:31:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:06:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:39:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

// foundation imports
import oracle.retail.stores.domain.discount.AbstractDiscount;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;

//--------------------------------------------------------------------------
/**
    Transaction discount strategy abstract class. <P>
         @deprecated As of release 4.5.0,
        replaced by {@link oracle.retail.stores.domain.discount.TransactionDiscountStrategy}
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $

    @see oracle.retail.stores.domain.discount.AbstractDiscount
    @see oracle.retail.stores.domain.transaction.TransactionDiscountStrategyIfc
**/
//--------------------------------------------------------------------------
public abstract class TransactionDiscountStrategy
extends AbstractDiscount
implements TransactionDiscountStrategyIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 9042428008405577362L;

    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

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
        if (obj instanceof TransactionDiscountStrategy)
        {                               // begin compare objects
            TransactionDiscountStrategy c =
               (TransactionDiscountStrategy) obj;

            // compare all the attributes of TransactionDiscountStrategy
            if (super.equals(obj))
            {
                isEqual = true;         // set the return code to true
            }
            else
            {
                isEqual = false;        // set the return code to false
            }
        }                               // end compare objects
        return(isEqual);
    }                                   // end equals()

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
                        // set clone attributes
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
        {                                   // begin setCloneAttributes()
        super.setCloneAttributes((AbstractDiscount) newClass);
        }                                   // end setCloneAttributes()

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
    **/
    //---------------------------------------------------------------------
    public abstract String toJournalString();

}
