/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/EmployeeFindRoleInformationSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:49 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.4  2004/04/19 14:43:01  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:50:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Jan 26 2004 15:57:48   jriggins
 * Initial revision.
 * Resolution for 3597: Employee 7.0 Updates
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

// domain imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.RoleTransaction;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EmployeeLookupRoleBeanModel;

//------------------------------------------------------------------------------
/**

      This site is used to enter employee Role information
      which will be used to find the employee.

      @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class EmployeeFindRoleInformationSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "EmployeeFindRoleInformationSite";

    //--------------------------------------------------------------------------
    /**

       This site is used to enter employee Role information
       which will be used to find the employee.

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        // Need to change Cargo type to <ServiceName>Cargo
        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();

        // Need to change screen ID and bean type
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
        // Retrieve the list of available roles to search by.
        RoleIfc roles[] = cargo.getRoles();
        if (roles == null)
        {
            RoleTransaction rTrans = null;

            // Load all needed locales into a LocaleRequestor
            Locale[] locales = { LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE),
                                 LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE),
                                 LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)};           
            LocaleRequestor requestor = new LocaleRequestor(locales);
            
            rTrans = (RoleTransaction) DataTransactionFactory.create(DataTransactionKeys.ROLE_TRANSACTION);
            
            try
            {
                roles = rTrans.readRoles(requestor);
            }
            catch (Exception e)
            {
                bus.mail("DbError");
                logger.error( e);
            }
            
            cargo.setRoles(roles);
        }        
        
        EmployeeLookupRoleBeanModel model = new EmployeeLookupRoleBeanModel();
        model.setRoles(cargo.getRoles());

        ui.showScreen(POSUIManagerIfc.EMPLOYEE_FIND_ROLE, model);

    }
}  // end class EmployeeFindRoleInformationSite
