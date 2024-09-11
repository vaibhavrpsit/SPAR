/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/TenderReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:50 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:56 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:28:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:16:08   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:28:38   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:03:26   epd
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:08:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:44:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:22:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;


// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.VoidTransactionADO;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
    Copies the information needed by the Void service
    from the cargo of the Tender service.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TenderReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1740663914193813780L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.postvoid.TenderReturnShuttle.class);
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       void transaction
    **/
    protected TenderableTransactionIfc transaction;

    //----------------------------------------------------------------------
    /**
       Load tender data into shuttle.

       @param  bus     Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        transaction = cargo.getTransaction();
    }

    //----------------------------------------------------------------------
    /**
       Unload tender data into calling service cargo.

       @param  bus     Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        VoidCargo cargo = (VoidCargo) bus.getCargo();

        VoidTransactionADO voidTxn = (VoidTransactionADO)cargo.getCurrentTransactionADO();
        ((ADO)voidTxn).fromLegacy(transaction);        
        //cargo.setCurrentTransactionADO(voidTxn);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  TenderReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}
