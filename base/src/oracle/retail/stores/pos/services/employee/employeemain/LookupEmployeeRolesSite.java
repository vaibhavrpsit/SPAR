/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeemain/LookupEmployeeRolesSite.java /main/13 2011/12/05 12:16:19 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:20 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse   
 *
 *   Revision 1.7.2.1  2004/10/21 16:16:01  jdeleau
 *   @scr 7436 Fix crash on looking up employee by ID
 *
 *   Revision 1.7  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:16  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:49:04  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Feb 24 2003 11:05:30   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.0   Apr 29 2002 15:23:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:23:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeemain;

// java imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.RoleTransaction;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//------------------------------------------------------------------------------

/**
    The LookupEmployeeRolesSite get all the role rows from the
    the database and update the arrays of role names and role IDs
    in the cargo.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class LookupEmployeeRolesSite extends PosSiteActionAdapter
{

    /**
       class name constant
    **/
    public static final String SITENAME = "LookupEmployeeRolesSite";

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    //--------------------------------------------------------------------------
    /**
       The LookupEmployeeRolesSite get all the role rows from the
       the database and update the arrays of role names and role IDs
       in the cargo.

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // default the letter to continue
        Letter result       = new Letter(CommonLetterIfc.SUCCESS);
        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();

        try
        {
            // Read all the roles
            RoleTransaction rt = null;
            
            rt = (RoleTransaction) DataTransactionFactory.create(DataTransactionKeys.ROLE_TRANSACTION);
            
            // Load all needed locales            
            Locale[] locales = { LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE),
                                 LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE),
                                 LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL) };
            
            LocaleRequestor requestor = new LocaleRequestor(locales);
            SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
            inquiry.setLocaleRequestor(requestor);
            inquiry.setApplicationId(RoleIfc.POINT_OF_SALE);
            
            RoleIfc[] roles    = rt.readRoles(inquiry);

            // Move IDs and Titles to arrays
            String[]  titles   = new String[roles.length];
            // Use the UI locale
            Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            for (int i = 0; i < roles.length; i++)
            {
                titles[i] = roles[i].getTitle(userLocale);
            }

            // Update the cargo
            cargo.setRoleTitles(titles);
            cargo.setRoles(roles);
        }
        catch (DataException de)
        {
            // if no matches were found, on add that's not an error.  Go on.
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                cargo.setDataExceptionErrorCode(de.getErrorCode());
                cargo.setFatalError(true);
                result = new Letter(CommonLetterIfc.DB_ERROR);
                logger.error( "Employee Role error: " + de.getMessage() + "");
            }
        }                               // end database error catch

        // Mail the appropriate result to continue
        bus.mail(result, BusIfc.CURRENT);

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @param none
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class: " + SITENAME + " (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

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

}  // end class LookupEmployeeRolesSite
