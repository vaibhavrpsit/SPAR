/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/browser/BrowserCargo.java /main/12 2012/10/29 16:37:48 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    10/29/12 - deprecating class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:10:50 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:37 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/15/2005 14:57:21    Jason L. DeLeau 4204:
 *         Remove duplicate instances of UserAccessCargoIfc
 *    3    360Commerce1.2         3/31/2005 15:27:17     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:19:50     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:09:37     Robert Pearse
 *
 *   Revision 1.4  2004/02/12 16:49:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.3  2004/02/12 00:47:15  bjosserand
 *   @scr 0
 *
 *   Revision 1.2  2004/02/11 21:38:35  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *    Rev 1.0   Aug 29 2003 15:54:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:36:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:08:00   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:21:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Jan 10 2002 13:03:40   dfh
 * added security access to xml and cargo (role function id),
 * service alert passes in the operator to web store to check
 * sercurity access
 * Resolution for POS SCR-186: CR/Webstore, app hangs when unauth user enters Password screen
 *
 *    Rev 1.0   Sep 21 2001 11:13:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.browser;

// java imports
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
//--------------------------------------------------------------------------
/**
    Cargo that carries the data for the CustomerLookup
    service.
    <p>
    @version $Revision: /main/12 $
    @deprecated as of 14.0 Use {@link oracle.retail.stores.pos.services.browserfoundation.BrowserFoundationCargo} instead.
**/
//--------------------------------------------------------------------------
public class BrowserCargo extends UserAccessCargo implements UserAccessCargoIfc
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";


    //---------------------------------------------------------------------
    /**
        Take a snapshot of the current state of the cargo. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>Cargo state is placed on the stack.
        </UL>
        @param none
        @return SnapshotIfc  Snapshot object containing relevent data from the cargo
        @exception none
    **/
    //---------------------------------------------------------------------
    public SnapshotIfc makeSnapshot()
    {                                   // Begin makeSnapshot()

        return new TourCamSnapshot(this);

    }                                   // End makeSnapshot()
    //----------------------------------------------------------------------
    /**
        Returns the function ID whose access is to be checked.
        @return int Role Function ID
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.WEB_STORE;
    }

    //---------------------------------------------------------------------
    /**
        Restore cargo to original state. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>Original cargo values are restored
        </UL>
        @param snapshot  object that contains cargo state information
        @return void
        @exception ObjectRestoreException   Bedrock is unable to restore cargo to original state
    **/
    //---------------------------------------------------------------------
    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {                                   // Begin restoreSnapshot()
   }                                   // End restoreSnapshot()


    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        return "Class:  CustomerLookupCargo (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode();
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
