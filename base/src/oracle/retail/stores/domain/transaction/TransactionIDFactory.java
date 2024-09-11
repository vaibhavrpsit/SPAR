/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/TransactionIDFactory.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:46 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:34 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:14 PM  Robert Pearse   
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
 *    Rev 1.0   Jun 03 2002 17:06:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:16   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:31:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:05:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:39:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;
// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.DomainGateway;

//----------------------------------------------------------------------------
/**
     Factory class for transaction ID objects. <P>
     @deprecated As of release 4.5.0, replaced by oracle.retail.stores.domain.DomainGateway#getFactory().getTransactionIDInstance()
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class TransactionIDFactory implements Serializable
{                                       // begin class TransactionIDFactory
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6655847010849579903L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
        Retrieves instance of TransactionID object cast as TransactionIDIfc. <P>
        @return TransactionIDIfc object
    **/
    //---------------------------------------------------------------------
    public TransactionIDIfc getInstance()
    {                                   // begin getInstance()
        return(DomainGateway.getFactory().getTransactionIDInstance());
    }                                   // end getInstance()

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        String strResult = new String("Class:  TransactionIDFactory (Revision " +
                                      getRevisionNumber() +
                                      ") @" +
                                      hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

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
    }                                   // end getRevisionNumber()

}                                       // end class TransactionIDFactory
