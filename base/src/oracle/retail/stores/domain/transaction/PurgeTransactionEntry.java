/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/PurgeTransactionEntry.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:47 mszekely Exp $
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
 *    4    360Commerce 1.3         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    3    360Commerce 1.2         3/31/2005 4:29:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:29 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:31 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:40:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jan 22 2003 10:41:50   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;
// foundation imports
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntry;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This class class adds the transaction type to POSLogTransactionEntry.
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class PurgeTransactionEntry extends POSLogTransactionEntry
implements PurgeTransactionEntryIfc
{                                       // begin class PurgeTransactionEntry
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8502238231693528661L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        transaction type
    **/
    protected int transactionType = -1;

    //----------------------------------------------------------------------------
    /**
        Constructs PurgeTransactionEntry object. <P>
    **/
    //----------------------------------------------------------------------------
    public PurgeTransactionEntry()
    {                                   // begin PurgeTransactionEntry()
        super();
    }                                   // end PurgeTransactionEntry()

    //----------------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //----------------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        PurgeTransactionEntry c = new PurgeTransactionEntry();

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
    public void setCloneAttributes(PurgeTransactionEntry newClass)
    {                                   // begin setCloneAttributes()
        super.setCloneAttributes(newClass);
        newClass.setTransactionType(getTransactionType());
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
        if (obj instanceof PurgeTransactionEntry)
        {                                   // begin compare objects

            PurgeTransactionEntry c = (PurgeTransactionEntry) obj;      // downcast the input object

            // compare all the attributes of PurgeTransactionEntry
            if (super.equals(obj) && getTransactionType() == c.getTransactionType())
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
        Retrieves transaction type. <P>
        @return transaction type
    **/
    //----------------------------------------------------------------------------
    public int getTransactionType()
    {
        return(transactionType);
    }

    //----------------------------------------------------------------------------
    /**
        Sets transaction type. <P>
        @param value  transaction type
    **/
    //----------------------------------------------------------------------------
    public void setTransactionType(int value)
    {
        transactionType = value;
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
          Util.classToStringHeader("PurgeTransactionEntry",
                                    getRevisionNumber(),
                                    hashCode());

        strResult.append("transactionType:                      ")
                     .append("[")
                     .append(transactionType)
                     .append("]")
                     .append(Util.EOL);

        strResult.append(super.toString());

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
        PurgeTransactionEntrymain method. <P>
        @param String args[]  command-line parameters
    **/
    //----------------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        PurgeTransactionEntry c = new PurgeTransactionEntry();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class PurgeTransactionEntry
