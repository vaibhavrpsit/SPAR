/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/AbstractFinancialEntity.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
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
 *   Revision 1.4  2004/09/23 00:30:54  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Jun 10 2003 11:50:36   jgs
 * Backout hardtotals deprecations and compression change due to performance consideration.
 * 
 *    Rev 1.2   May 20 2003 07:21:10   jgs
 * Deprecated getHardtotals() and setHardtotals() methods.
 * Resolution for 2573: Modify Hardtotals compress to remove dependency on code modifications.
 * 
 *    Rev 1.1   Jan 29 2003 11:03:24   mpm
 * Merge 5.1 to 6.0.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Jun 03 2002 16:51:34   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;
// java imports
// foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
    This abstract class represents a store financial entity.  Each of these
    entities identifies a sign-on and sign-off operator, a business day, an open
    and close timestamp, accountability flag, a count object and a status. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public abstract class AbstractFinancialEntity extends    AbstractStatusEntity
                                              implements AbstractFinancialEntityIfc
{                                       // begin class AbstractFinancialEntity
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3292661803044252727L;


    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        financial totals
    **/
    protected FinancialTotalsIfc totals = null;

        //---------------------------------------------------------------------
        /**
                Constructs AbstractFinancialEntity object. <P>
                <B>Pre-Condition(s)</B>
                <UL>
                <LI>none
                </UL>
                <B>Post-Condition(s)</B>
                <UL>
                <LI>none
                </UL>
        **/
        //---------------------------------------------------------------------
        public AbstractFinancialEntity()
        {                                  // begin AbstractFinancialEntity()
        super();
        setTotals(instantiateFinancialTotals());
        }                                  // end AbstractFinancialEntity()

    //---------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return Object clone of this object
    **/
    //---------------------------------------------------------------------
    public abstract Object clone();

    //---------------------------------------------------------------------
    /**
        Sets attributes in clone of this object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param newEntity new AbstractFinancialEntity object
    **/
    //---------------------------------------------------------------------
    protected void setCloneAttributes(AbstractFinancialEntity newEntity)
    {                                   // begin setCloneAttributes()
        // set values
        super.setCloneAttributes(newEntity);
        if (totals != null)
        {
            newEntity.setTotals((FinancialTotalsIfc) totals.clone());
        }
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

        if (obj instanceof AbstractFinancialEntity)
        {
            AbstractFinancialEntity c = (AbstractFinancialEntity) obj;          // downcast the input object
            // compare all the attributes of FinancialTotals
            if (super.equals(obj) &&
                Util.isObjectEqual(getTotals(), c.getTotals()))
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
        Instantiates financial totals class.  This is isolated so that
        the actual implementation of FinancialTotalsIfc can be overridden
        easily. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @return FinancialTotalsIfc object
    **/
    //---------------------------------------------------------------------
    protected FinancialTotalsIfc instantiateFinancialTotals()
    {                                   // begin instantiateFinancialTotals()
        // instantiate base financial totals class
        return(DomainGateway.getFactory().getFinancialTotalsInstance());
    }                                   // end instantiateFinancialTotals()

    //---------------------------------------------------------------------
    /**
        Resets expected financial totals object. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
    **/
    //---------------------------------------------------------------------
    public void resetTotals()
    {                                   // begin resetTotals()
        getTotals().resetTotals();
    }                                   // end resetTotals()

    //---------------------------------------------------------------------
    /**
        Add a totals object to the financial totals for this till. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>Totals object updated
        </UL>
        @param t totals object to add to this till's totals
    **/
    //---------------------------------------------------------------------
    public void addTotals(FinancialTotalsIfc t)
    {                                   // begin addTotals()
        getTotals().add(t);
    }                                   // end addTotals()

    //----------------------------------------------------------------------------
    /**
        Retrieves financial totals for this entity. <P>
        @return financial totals for this entity
    **/
    //----------------------------------------------------------------------------
    public FinancialTotalsIfc getTotals()
    {                                   // begin getTotals()
        return(totals);
    }                                   // end getTotals()

    //----------------------------------------------------------------------------
    /**
        Sets financial totals for this entity. <P>
        @param value  financial totals for this entity
    **/
    //----------------------------------------------------------------------------
    public void setTotals(FinancialTotalsIfc value)
    {                                   // begin setTotals()
        totals = value;
    }                                   // end setTotals()

    //---------------------------------------------------------------------
    /**
        Converts attributes to string value.<P>
        @return string representation of attributes
    **/
    //---------------------------------------------------------------------
    public String attributesToString()
    {                                   // begin attributesToString()
        String strResult = super.attributesToString();
        strResult += "\nFINANCIAL ATTRIBUTES:\n";
        if (totals == null)
        {
            strResult += "Totals:                                 [null]\n";
        }
        else
        {
            strResult += totals.toString();
        }

        return(strResult);

    }                                   // end attributesToString()

    //---------------------------------------------------------------------
    /**
        This method converts hard totals information to a comma delimited
        String. <P>
        @return String
    **/
    //---------------------------------------------------------------------
    public void getHardTotalsData(HardTotalsBuilderIfc builder)
    {
        super.getHardTotalsData(builder);

        if (totals == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            totals.getHardTotalsData(builder);
        }
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
        super.setHardTotalsData(builder);
        // Get the totals
        totals    = (FinancialTotalsIfc)builder.getFieldAsClass();
        if (totals != null)
        {
            totals.setHardTotalsData(builder);
        }
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()

}                                      // end class AbstractFinancialEntity
