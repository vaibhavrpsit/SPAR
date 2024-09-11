/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/AssociateProductivity.java /rgbustores_13.4x_generic_branch/2 2011/07/12 10:32:37 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  07/12/11 - Fortify fix: Removed main method
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   5    360Commerce 1.4         4/12/2008 5:44:57 PM   Christian Greene
 *        Upgrade StringBuffer to StringBuilder
 *   4    360Commerce 1.3         4/25/2007 10:00:56 AM  Anda D. Cadar   I18N
 *        merge
 *   3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:19:41 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/09/23 00:30:53  kmcbride
 *  @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *  Revision 1.3  2004/02/12 17:13:34  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 23:25:28  bwf
 *  @scr 0 Organize imports.
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:51:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   09 Apr 2002 16:57:00   jbp
 * Initial revision.
 * Resolution for POS SCR-15: Sales associate activity report performs inadequately, crashes
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;
// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     Class description. <P>
     @$Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//----------------------------------------------------------------------------
public class AssociateProductivity
implements AssociateProductivityIfc
{                                       // begin class AssociateProductivity
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1842533466617994291L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";
    /**
        Date of productivity informaition
    **/
    protected EYSDate date = null;
    /**
        Sales associate
    **/
    protected EmployeeIfc associate = null;
    /**
        Net amount
    **/
    protected CurrencyIfc netAmount = null;

    //----------------------------------------------------------------------------
    /**
        Constructs AssociateProductivity object. <P>
    **/
    //----------------------------------------------------------------------------
    public AssociateProductivity()
    {                                   // begin AssociateProductivity()
        netAmount = DomainGateway.getBaseCurrencyInstance();
    }                                   // end AssociateProductivity()

    //----------------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //----------------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        AssociateProductivity c = new AssociateProductivity();

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
    public void setCloneAttributes(AssociateProductivity newClass)
    {                                   // begin setCloneAttributes()
        if (date != null)
        {
            newClass.setDate((EYSDate) getDate().clone());
        }
        if (associate != null)
        {
            newClass.setAssociate((EmployeeIfc) getAssociate().clone());
        }
        if (netAmount != null)
        {
            newClass.setNetAmount((CurrencyIfc) getNetAmount().clone());
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
        if (obj instanceof AssociateProductivity)
        {                                   // begin compare objects

            AssociateProductivity c = (AssociateProductivity) obj;      // downcast the input object

            // compare all the attributes of AssociateProductivity
            if (Util.isObjectEqual(getDate(), c.getDate()) &&
                Util.isObjectEqual(getAssociate(), c.getAssociate()) &&
                Util.isObjectEqual(getNetAmount(), c.getNetAmount()))
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

    //----------------------------------------------------------------------------
    /**
        Retrieves Date of productivity informaition. <P>
        @return Date of productivity informaition
    **/
    //----------------------------------------------------------------------------
    public EYSDate getDate()
    {                                   // begin getDate()
        return(date);
    }                                   // end getDate()

    //----------------------------------------------------------------------------
    /**
        Sets Date of productivity informaition. <P>
        @param value  Date of productivity informaition
    **/
    //----------------------------------------------------------------------------
    public void setDate(EYSDate value)
    {                                   // begin setDate()
        date = value;
    }                                   // end setDate()

    //----------------------------------------------------------------------------
    /**
        Retrieves Sales associate. <P>
        @return Sales associate
    **/
    //----------------------------------------------------------------------------
    public EmployeeIfc getAssociate()
    {                                   // begin getAssociate()
        return(associate);
    }                                   // end getAssociate()

    //----------------------------------------------------------------------------
    /**
        Sets Sales associate. <P>
        @param value  Sales associate
    **/
    //----------------------------------------------------------------------------
    public void setAssociate(EmployeeIfc value)
    {                                   // begin setAssociate()
        associate = value;
    }                                   // end setAssociate()

    //----------------------------------------------------------------------------
    /**
        Retrieves Net amount. <P>
        @return Net amount
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getNetAmount()
    {                                   // begin getNetAmount()
        return(netAmount);
    }                                   // end getNetAmount()

    //----------------------------------------------------------------------------
    /**
        Sets Net amount. <P>
        @param value  Net amount
    **/
    //----------------------------------------------------------------------------
    public void setNetAmount(CurrencyIfc value)
    {                                   // begin setNetAmount()
        netAmount = value;
    }                                   // end setNetAmount()

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
          Util.classToStringHeader("AssociateProductivity",
                                    getRevisionNumber(),
                                    hashCode());
        strResult.append("Class:  AssociateProductivity (Revision ")
                 .append(getRevisionNumber())
                 .append(") @")
                 .append(hashCode())
                 .append(Util.EOL);
        // add attributes to string
        if (getDate() == null)
        {
            strResult.append("date:                               [null]")
                     .append(Util.EOL);
        }
        else
        {
            strResult.append("date:                               ")
                     .append("[").append(getDate()).append("]")
                     .append(Util.EOL);
        }
        if (getAssociate() == null)
        {
            strResult.append("associate:                          [null]")
                     .append(Util.EOL);
        }
        else
        {
            strResult.append(getAssociate().toString());
        }
        if (getNetAmount() == null)
        {
            strResult.append("netAmount:                          [null]")
                     .append(Util.EOL);
        }
        else
        {
            strResult.append("netAmount:                          ")
                     .append("[").append(getNetAmount()).append("]")
                     .append(Util.EOL);
        }
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
}                                       // end class AssociateProductivity
