/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/RoleTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    blarsen   11/06/08 - adding application id to the search criteria passed
 *                         to read roles
 *    tzgarba   11/03/08 - More updates for collecting multiple locales for
 *                         roles.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:57 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:59 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:34:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Feb 24 2003 11:03:34   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.0   Jun 03 2002 16:42:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:51:12   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:10:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   05 Mar 2002 16:38:34   baa
 * make role fn extendible
 * Resolution for POS SCR-626: Make the list of Role functions extendible.
 *
 *    Rev 1.0   Sep 20 2001 15:55:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
//------------------------------------------------------------------------------
/**
    The RoleTransaction implements the role lookup and save operations
**/
//------------------------------------------------------------------------------
public class RoleTransaction extends DataTransaction
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        Insert operation const
    **/
    public static final String INSERT_ROLE = "InsertRole";
    /**
        Update operation const
    **/
    public static final String UPDATE_ROLE = "UpdateRole";
    /**
        Read operation const
    **/
    public static final String READ_ROLES  = "ReadRoles";

    /**
        The transactionName name links this transaction to a command within the
        DataScript.
    **/
    protected static String transactionName="RoleTransaction";

    //---------------------------------------------------------------------
    /**
        DataCommand constructor.  Initializes dataOperations and
        dataConnectionPool.
    **/
    //---------------------------------------------------------------------
    public RoleTransaction()
    {
        super(transactionName);
    }

    //---------------------------------------------------------------------
    /**
        Obtains all roles.
        <p>
        @param inquiry SearchCriteriaIfc
        @return an array of RoleIfc objects
        @exception DataException is thrown if the role cannot be found.
    **/
    //---------------------------------------------------------------------
    public RoleIfc[] readRoles(SearchCriteriaIfc inquiry) throws DataException
    {
       // creates an anynonmous DataActionIfc object.
        DataAction[] dataActions = new DataAction[1];
        dataActions[0] = new DataAction();
        dataActions[0].setDataOperationName(READ_ROLES);
        dataActions[0].setDataObject(inquiry);
        setDataActions(dataActions);

        RoleIfc[] roles = (RoleIfc[])getDataManager().execute(this);

        return(roles);
    }

    //---------------------------------------------------------------------
    /**
        Obtains all roles.
        <p>
        @param requestor LocaleRequestor containing the locales to load
        @return an array of RoleIfc objects
        @exception DataException is thrown if the role cannot be found.
    **/
    //---------------------------------------------------------------------
    public RoleIfc[] readRoles(LocaleRequestor requestor) throws DataException
    {
        // Wrap the locale requestor in a criteria object
        SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
        inquiry.setLocaleRequestor(requestor);
        inquiry.setApplicationId(RoleIfc.POINT_OF_SALE);
        // Delegate to the search criteria based lookup
        return readRoles(inquiry);
    }
    
    //---------------------------------------------------------------------
    /**
        Obtains all roles.
        <p>
        @return an array of RoleIfc objects
        @exception DataException is thrown if the role cannot be found.
        @deprecated As of release 13.1, replaced by {@link #readRoles(LocaleRequestor)}
    **/
    //---------------------------------------------------------------------
    public RoleIfc[] readRoles() throws DataException
    {
       // creates an anynonmous DataActionIfc object.
        DataAction[] dataActions = new DataAction[1];
        dataActions[0] = new DataAction();
        dataActions[0].setDataOperationName(READ_ROLES);
        setDataActions(dataActions);

        RoleIfc[] roles = (RoleIfc[])getDataManager().execute(this);

        return(roles);
    }

    //---------------------------------------------------------------------
    /**
        Updates a role.
        <p>
        @param role   the object to update.
        @exception DataException is thrown if the role cannot be update.
    **/
    //---------------------------------------------------------------------
    public void updateRole(RoleIfc role) throws DataException
    {
        saveRole(role, UPDATE_ROLE);
    }

    //---------------------------------------------------------------------
    /**
        Inserts a role.
        <p>
        @param role   the object to insert.
        @return int   Role Id of the role inserted;
        @exception DataException is thrown if the role cannot be update.
    **/
    //---------------------------------------------------------------------
    public int insertRole(RoleIfc role) throws DataException
    {
        return (saveRole(role, INSERT_ROLE));
    }

    //---------------------------------------------------------------------
    /**
        Updates or inserts a role.
        <p>
        @param role   the object to update.
        @param opName the name of the DataOperation.
        @exception DataException is thrown if the role cannot be update.
        @return If updated, 0; inserted > 0;
    **/
    //---------------------------------------------------------------------
    protected int saveRole(RoleIfc role, String opName) throws DataException
    {
       // creates an anynonmous DataActionIfc object.
        DataAction[] dataActions = new DataAction[1];
        dataActions[0] = new DataAction();
        dataActions[0].setDataObject(role);
        dataActions[0].setDataOperationName(opName);
        setDataActions(dataActions);

        Integer roleID = (Integer)getDataManager().execute(this);
        return (roleID.intValue());
    }

    //---------------------------------------------------------------------
    /**
        Retrieve role functions.
        <p>
        @exception DataException is thrown if the role cannot be update.
        @return array of rolefunctions;
    **/
    //---------------------------------------------------------------------
    public RoleFunctionIfc[] getRoleFunctions() throws DataException
    {
        DataAction[] dataActions = new DataAction[1];
        dataActions[0] = new DataAction();
        dataActions[0].setDataOperationName("ReadRoleFunctions");
        setDataActions(dataActions);

        RoleFunctionIfc[] roleDesc = (RoleFunctionIfc[])getDataManager().execute(this);
        return (roleDesc);
    }

     //---------------------------------------------------------------------
    /**
        Returns the revision number of this class.
        <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
       Returns the string representation of this object.
       <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: RoleTransaction (Revision "
                                        + getRevisionNumber() + ") @"
                                        + hashCode());
        return(strResult);
    }
} // end class RoleTransaction

