/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/TransactionKey.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:47 mszekely Exp $
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
 * 4    360Commerce 1.3         4/12/2008 5:44:57 PM   Christian Greene Upgrade
 *       StringBuffer to StringBuilder
 * 3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:26:23 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:15:14 PM  Robert Pearse   
 *
 *Revision 1.4  2004/09/23 00:30:51  kmcbride
 *@scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *Revision 1.3  2004/02/12 17:14:42  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 23:28:51  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:41:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 20 2003 18:47:56   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Apr 19 2003 09:53:20   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;
// foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This class defines the unique key for a transaction; i.e., a
     business date and a transaction identifier consisting of store identifier,
     workstation identifier and sequence number.
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class TransactionKey
implements TransactionKeyIfc
{                                       // begin class TransactionKey
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 8351388899781037032L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        transaction identifier object
    **/
    protected TransactionIDIfc transactionID = null;
    /**
        business date
    **/
    protected EYSDate businessDate = null;

    //----------------------------------------------------------------------------
    /**
        Constructs TransactionKey object. <P>
    **/
    //----------------------------------------------------------------------------
    public TransactionKey()
    {                                   // begin TransactionKey()
    }                                   // end TransactionKey()

    //----------------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //----------------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        TransactionKey c = new TransactionKey();

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
    public void setCloneAttributes(TransactionKey newClass)
    {                                   // begin setCloneAttributes()
        if (transactionID != null)
        {
            newClass.setTransactionID((TransactionIDIfc) getTransactionID().clone());
        }
        if (businessDate != null)
        {
            newClass.setBusinessDate((EYSDate) getBusinessDate().clone());
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
        if (obj instanceof TransactionKey)
        {                                   // begin compare objects

            TransactionKey c = (TransactionKey) obj;      // downcast the input object

            // compare all the attributes of TransactionKey
            if (Util.isObjectEqual(getTransactionID(), c.getTransactionID()) &&
                Util.isObjectEqual(getBusinessDate(), c.getBusinessDate()))
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
        Retrieves transaction identifier object. <P>
        @return transaction identifier object
    **/
    //----------------------------------------------------------------------------
    public TransactionIDIfc getTransactionID()
    {                                   // begin getTransactionID()
        return(transactionID);
    }                                   // end getTransactionID()

    //----------------------------------------------------------------------------
    /**
        Sets transaction identifier object. <P>
        @param value  transaction identifier object
    **/
    //----------------------------------------------------------------------------
    public void setTransactionID(TransactionIDIfc value)
    {                                   // begin setTransactionID()
        transactionID = value;
    }                                   // end setTransactionID()

    //---------------------------------------------------------------------
    /**
       Sets transaction identifier object with store identifier, workstation
       identifier and sequence number.
       @param storeID store identifier
       @param workstation workstation identifier
       @param sequenceNumber sequence number
    **/
    //---------------------------------------------------------------------
    public void setTransactionID(String storeID,
                                 String workstationID,
                                 long sequenceNumber)
    {                                   // begin setTransactionID()
        setTransactionID
          (DomainGateway.getFactory().getTransactionIDInstance());
        getTransactionID().setTransactionID(storeID, workstationID, sequenceNumber);
    }                                   // end setTransactionID()

    //----------------------------------------------------------------------------
    /**
        Retrieves store identifier.  If transaction ID is null, empty string
        is returned. <P>
        @return store identifier
    **/
    //----------------------------------------------------------------------------
    public String getStoreID()
    {                                   // begin getStoreID()
        String storeID = "";
        if (getTransactionID() != null)
        {
            storeID = getTransactionID().getStoreID();
        }
        return(storeID);
    }                                   // end getStoreID()

    //----------------------------------------------------------------------------
    /**
        Retrieves workstation identifier.  If transaction ID is null, empty string
        is returned. <P>
        @return workstation identifier
    **/
    //----------------------------------------------------------------------------
    public String getWorkstationID()
    {                                   // begin getWorkstationID()
        String workstationID = "";
        if (getTransactionID() != null)
        {
            workstationID = getTransactionID().getWorkstationID();
        }
        return(workstationID);
    }                                   // end getWorkstationID()

    //----------------------------------------------------------------------------
    /**
        Retrieves transaction seqeuence number.  If transaction ID is null,
        0 is returned. <P>
        @return transaction sequence number
    **/
    //----------------------------------------------------------------------------
    public long getSequenceNumber()
    {                                   // begin getSequenceNumber()
        long sequenceNumber = 0;
        if (getTransactionID() != null)
        {
            sequenceNumber = getTransactionID().getSequenceNumber();
        }
        return(sequenceNumber);
    }                                   // end getSequenceNumber()

    //----------------------------------------------------------------------------
    /**
        Retrieves business date. <P>
        @return business date
    **/
    //----------------------------------------------------------------------------
    public EYSDate getBusinessDate()
    {                                   // begin getBusinessDate()
        return(businessDate);
    }                                   // end getBusinessDate()

    //----------------------------------------------------------------------------
    /**
        Sets business date. <P>
        @param value  business date
    **/
    //----------------------------------------------------------------------------
    public void setBusinessDate(EYSDate value)
    {                                   // begin setBusinessDate()
        businessDate = value;
    }                                   // end setBusinessDate()

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
          Util.classToStringHeader("TransactionKey",
                                    getRevisionNumber(),
                                    hashCode());
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
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

}                                       // end class TransactionKey
