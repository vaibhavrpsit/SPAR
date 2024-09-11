/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/common/CheckAccessSite.java /main/11 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         8/21/2007 5:18:37 PM   Owen D. Horne   CR10731:
 *       Now consults ManualEntryID parameter to determine whether login id or
 *       employee id should be used for journaling
 *      
 * 3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:20:04 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:09:52 PM  Robert Pearse   
 *
 *Revision 1.5  2004/05/19 15:25:00  lzhao
 *@scr 3693: make journal read, write none transaction message.
 *
 *Revision 1.4  2004/03/03 23:15:08  bwf
 *@scr 0 Fixed CommonLetterIfc deprecations.
 *
 *Revision 1.3  2004/02/12 16:47:59  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:27:27  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 00:53:02   baa
 * cleanup - sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 18:58:38   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * Resolution for POS SCR-219: Add Tender Limit Override
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security.common;

import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * This site checks to see if the current operator has access to the specified
 * function.
 * 
 * @version $Revision: /main/11 $
 */
public class CheckAccessSite extends SiteActionAdapter
{
    private static final long serialVersionUID = -425259168040011215L;
    /** revision number */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Check access and mail appropriate letter.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // return variable to indicate whether the user has access to the
        // code function or not.
        boolean access = false;

        // Default letter assumes that the operator will need to override the
        // access credentials of the current user.
        String letter = CommonLetterIfc.OVERRIDE;

        // get the cargo for the information needed to check the access.
        UserAccessCargoIfc cargo = (UserAccessCargoIfc) bus.getCargo();

        // get the security manager to be able to check the access to the code
        // function of the current user.
        SecurityManagerIfc securityManager =
                (SecurityManagerIfc) Gateway.getDispatcher(). getManager(
                SecurityManagerIfc.TYPE);

        // Check the access of the user to the code function
        access = securityManager.checkAccess(cargo.getAppID(),
                cargo.getAccessFunctionID());

        // if access has been granted then need to send a CONTINUE letter.
        if (access)
        {
            letter = CommonLetterIfc.CONTINUE;
            if ( cargo instanceof SaleCargo )
               {
                SaleCargo saleCargo = (SaleCargo)cargo;
                JournalManagerIfc journal = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
                EmployeeIfc employee = saleCargo.getOperator();
                if ( employee != null )
                {
                    String userParm = null;
                    try
                    {
                      UtilityIfc util = Utility.createInstance();
                      userParm = util.getParameterValue(ParameterConstantsIfc.OPERATORID_ManualEntryID, ParameterConstantsIfc.OPERATORID_ManualEntryID_USER);        
                    }
                    catch(Throwable e)  //catching throwable to handle Error thrown by unit tests using Mock Object framework
                    {
                      //In case of exception set userParm to USER_LOGIN by default
                      userParm = ParameterConstantsIfc.OPERATORID_ManualEntryID_USER;
                    }                  

                    if (userParm.equalsIgnoreCase(ParameterConstantsIfc.OPERATORID_ManualEntryID_USER))
                    {
                      journal.setCashierID(employee.getLoginID());
                      journal.setSalesAssociateID(employee.getLoginID());
                    }
                    else
                    {
                      journal.setCashierID(employee.getEmployeeID());
                      journal.setSalesAssociateID(employee.getEmployeeID());
                    }                    
                }
                RegisterIfc register = saleCargo.getRegister();
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
}
