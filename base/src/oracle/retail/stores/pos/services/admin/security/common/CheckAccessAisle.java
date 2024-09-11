/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/common/CheckAccessAisle.java /main/11 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         1/25/2006 4:10:51 PM   Brett J. Larsen merge
 *      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * 3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:20:03 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:09:52 PM  Robert Pearse   
 *:
 * 4    .v700     1.2.1.0     11/15/2005 14:57:22    Jason L. DeLeau 4204:
 *      Remove duplicate instances of UserAccessCargoIfc
 * 3    360Commerce1.2         3/31/2005 15:27:23     Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:20:03     Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:09:52     Robert Pearse
 *
 *Revision 1.4  2004/05/19 15:25:01  lzhao
 *@scr 3693: make journal read, write none transaction message.
 *
 *Revision 1.3  2004/02/12 16:49:02  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:37:44  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   May 13 2003 10:50:10   adc
 * Security override
 * Resolution for 2340:  security override for Void  is not in E.Journals
 *
 *    Rev 1.0   Apr 29 2002 15:37:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:07:20   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:21:36   msg
 * Initial revision.
 *
 *    Rev 1.0   09 Nov 2001 16:13:12   pdd
 * Initial revision.
 * Resolution for POS SCR-219: Add Tender Limit Override
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security.common;

// Foundation imports
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.admin.AdminCargo;

//------------------------------------------------------------------------------
/**
    This aisle checks to see if the current operator has access to the
    specified function.

    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------
public class CheckAccessAisle extends LaneActionAdapter
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
        class name constant
    **/
    public static final String LANENAME = "CheckAccessAisle";

    //--------------------------------------------------------------------------
    /**
        Check access and mail appropriate letter.
         @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        String letter = "Override";
        UserAccessCargoIfc cargo = (UserAccessCargoIfc) bus.getCargo();

       /* if (AbstractUserAccess.hasAccess(cargo.getOperator(),
                                         cargo.getAccessFunctionID(),
                                         bus.getServiceName()))
        {
            letter = "Continue";
        }*/

         boolean access = false;

        // get the security manager to be able to check the access to the code
        // function of the current user.
        SecurityManagerIfc securityManager =
                (SecurityManagerIfc) Gateway.getDispatcher(). getManager(
                SecurityManagerIfc.TYPE);

        // Check the access of the user to the code function
        access = securityManager.checkAccess(cargo.getAppID(),
                cargo.getAccessFunctionID());

        if (access)
        {
            letter = "Continue";
            if ( cargo instanceof AdminCargo )
            {
                AdminCargo adminCargo = (AdminCargo)cargo;
                JournalManagerIfc journal = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
                EmployeeIfc employee = adminCargo.getOperator();
                if ( employee != null )
                {
                    journal.setCashierID(employee.getLoginID());
                    journal.setSalesAssociateID(employee.getLoginID());
                }
                RegisterIfc register = adminCargo.getRegister();
                if ( register != null )
                {
                    WorkstationIfc workstation = register.getWorkstation();
                    if ( workstation != null )
                    {
                        journal.setRegisterID(workstation.getWorkstationID());
                        StoreIfc store = workstation.getStore();
                        if ( store != null )
                        {
                            journal.setStoreID(store.getStoreID());
                        }
                    }
                }
             }
          }



        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @param none
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}
