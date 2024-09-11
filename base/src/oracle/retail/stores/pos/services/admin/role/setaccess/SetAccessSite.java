/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/setaccess/SetAccessSite.java /main/15 2013/03/22 16:31:08 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  03/21/13 - Restrict Role Display and access
 *    mkutiana  03/19/13 - Restricting access point and role access based on
 *                         operators role
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    08/23/10 - changes for roles
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:14 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:11 PM  Robert Pearse
 *
 *   Revision 1.6  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/20 13:11:00  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.4  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:48:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:54  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 23 2003 17:00:04   baa
 * allow for modifying roles
 * Resolution for POS SCR-2194: Secruity/Role not saving changes when Setting Access
 *
 *    Rev 1.0   Apr 29 2002 15:37:50   msg
 * Initial revision.
 *
 *    Rev 1.5   26 Apr 2002 10:32:38   baa
 * read role functions titles if not part of role yet
 * Resolution for POS SCR-1609: Names of the Functions on Set Access screen in Role missing
 *
 *    Rev 1.4   25 Apr 2002 15:40:40   baa
 * call local get functions if role function names are not available
 * Resolution for POS SCR-1609: Names of the Functions on Set Access screen in Role missing
 *
 *    Rev 1.3   04 Apr 2002 15:22:16   baa
 * Remove references to Rolefunction descriptor array and maximun number of role functions
 * Resolution for POS SCR-1565: Remove references to RoleFunctionIfc.Descriptor Security Service
 *
 *    Rev 1.1   Mar 18 2002 23:07:16   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:21:16   msg
 * Initial revision.
 *
 *    Rev 1.3   05 Mar 2002 16:36:02   baa
 * deprecate function description
 * Resolution for POS SCR-626: Make the list of Role functions extendible.
 *
 *    Rev 1.2   03 Mar 2002 20:22:50   baa
 * role functions
 * Resolution for POS SCR-1483: Adding a new role to current employee then viewing role shows role out of order & change in functions
 *
 *    Rev 1.1   23 Jan 2002 16:55:10   baa
 * sort roles in alphabetical order
 * Resolution for POS SCR-197: Find Role not listing role names in alphabetical order
 * Resolution for POS SCR-198: Set Access should be in alpha order - new feature 4.5 missed
 *
 *    Rev 1.0   Sep 21 2001 11:12:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role.setaccess;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.RoleTransaction;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.SetAccessSelectBeanModel;

//------------------------------------------------------------------------------
/**
    This site is used to present the user with a list of role functions to
    set the access for.
    @version $Revision: /main/15 $
**/
//------------------------------------------------------------------------------
public class SetAccessSite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = 8103513743410923176L;

    /**
       class name constant
    **/
    public static final String SITENAME = "SetAccessSite";
    protected SetAccessCargo cargo = null;

    //--------------------------------------------------------------------------
    /**
       Display the role functions titles and access options which
       a user can set
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Need to change Cargo type to SetAccessCargo for this service
        cargo = (SetAccessCargo)bus.getCargo();

        // get the one particular role to be modified
        RoleIfc roleSelected = cargo.getRoleSelected();

        // retrieve role functions
        RoleFunctionIfc[] roleFunction = roleSelected.getFunctions();

        if (roleSelected.getFunctionAccessMap() == null)
        {
            // create role functions for this new role
            cargo.setNewRole(true);

            // save this new role as being the one selected
            cargo.setRoleSelected(roleSelected);
        }

        if (roleFunction == null || Util.isEmpty(roleFunction[0].getLocalizedTitle(LocaleMap.getLocale(LocaleMap.DEFAULT)))  )
        {
            roleFunction = getFunctions();
            roleSelected.setFunctions(roleFunction);
        }
        
        RoleIfc operatorRole =cargo.getOperator().getRole();
        //Filtering the role's functions(access points) based on the operators roles functions(access points)
        roleFunction = operatorRole.getFilteredRoleFunctions(roleFunction);

        cargo.setFilteredRoleFunctionsForRole(roleFunction);    
        
        /*
         * Setup bean model information for the UI to display
         */
        SetAccessSelectBeanModel beanModel = new SetAccessSelectBeanModel();

        // save the functions array size
        int functionSize = roleFunction.length;
        beanModel.setFunctionsArraySize(functionSize);

        // add the role function titles to the beanModel
        beanModel.setRoleFunctionTitle(retrieveFunctionTitle(roleFunction, functionSize));

        // add the role function Access values to the beanModel
        String[] accessValues = retrieveFunctionAccess(roleFunction,functionSize, bus);
        beanModel.setRoleFunctionAccess(accessValues);

        // save these function access values as
        // the roles original access values before
        // any modifications at this site
        cargo.setOldFunctionAccess(accessValues);

        // set the bean model to the FindRoleBeanModel
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.SET_ACCESS, beanModel);

    }

    /**
       This is a method to retrieve the role function titles
       from the cargo.
       @param roleFunctionTitle the array of function titles
       @param functionSize an int value for the number of function titles
       @return a String array of function titles
    **/
    private String[] retrieveFunctionTitle(RoleFunctionIfc[] roleFunction,
                                           int functionSize)
    {
        // Get the UI locale
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        String[] titleValues = new String[functionSize];

        // retrieve the role function titles from the cargo
        for (int i = 0; i < functionSize; i++)
        {
            titleValues[i] =  roleFunction[i].getLocalizedTitle(userLocale);
        }
        return titleValues;
    }

    /**
       This is a method to retrieve the role function access values
       from the cargo
       @param roleFunctionAccess the array of function access values
       @param functionSize an int value for the number of access values
       @return a String array of function access values
    **/
    private String[] retrieveFunctionAccess(RoleFunctionIfc[] roleFunction,
                                            int functionSize, BusIfc bus)
    {
        String[] accessValues = new String[functionSize];
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // retrieve the role function access boolean values


        for (int i = 0; i < functionSize; i++)
        {
            // check the boolean Access value and
            // convert to a "Yes/No" string

            if (roleFunction[i].getAccess())
            {
                accessValues[i] = utility.retrieveText(SetAccessCargo.SET_ACCESS_SPEC,
                                                       BundleConstantsIfc.ROLE_BUNDLE_NAME,
                                                       SetAccessCargo.YES_TAG,
                                                       SetAccessCargo.YES_LABEL);
            }
            else
            {
                accessValues[i] = utility.retrieveText(SetAccessCargo.SET_ACCESS_SPEC,
                                                       BundleConstantsIfc.ROLE_BUNDLE_NAME,
                                                       SetAccessCargo.NO_TAG,
                                                       SetAccessCargo.NO_LABEL);
            }
        }
        return accessValues;
    }

    /**
       Get an array of role functions to initialize the role object. <P>
       @return RoleFunctionIfc[] array of RoleFunctionIfc objects
       @deprecated as of release 5.0.0
    **/
    public RoleFunctionIfc[] getFunctions()
    {
        RoleTransaction trans = null;

        trans = (RoleTransaction) DataTransactionFactory.create(DataTransactionKeys.ROLE_TRANSACTION);

        RoleFunctionIfc[] funcs;
        try
        {
            funcs = trans.getRoleFunctions();
        }
        catch (DataException e)
        {
            funcs = null;
            cargo.setDataExceptionErrorCode(e.getErrorCode());
        }
        return(funcs);
    }


}
