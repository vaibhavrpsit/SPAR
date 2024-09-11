/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/IsTillReconciledSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:35 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.2  2004/04/15 20:38:54  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 *   Revision 1.4  2004/03/30 17:21:42  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Seperate close and reconcile services.
 *
 *   Revision 1.3  2004/02/12 16:49:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:28:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:28:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   20 Nov 2001 09:47:48   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;


//--------------------------------------------------------------------------
/**
    This determines if the store object has a status of OPEN
    <P>
    @deprecated as of Release 7.0. No longer used.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IsTillReconciledSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2721376696830579168L;

    /** The logger to which log messages will be sent. */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile.IsTillReconciledSignal.class);
    /** revision number of this class */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Determines whether it is safe for the bus to proceed.
       <p>
       @param bus the bus trying to proceed
       @return true if register is not open; false otherwise
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {

        TillReconcileCargo cargo = (TillReconcileCargo)bus.getCargo();
        boolean reconciled = false;

        TillIfc till = cargo.getRegister().getTillByID(cargo.getTillID());

        // Check the register to make sure it is open
        if (till.getStatus() == AbstractStatusEntityIfc.STATUS_RECONCILED)
        {
            reconciled = true;
        }


        return(reconciled);
    }

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

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        String strResult = new String("Class:  " + getClass().getName() + " (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        return(strResult);
    }                                   // end toString()
}
