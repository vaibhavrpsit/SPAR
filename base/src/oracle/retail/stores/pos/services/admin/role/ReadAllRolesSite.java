/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/ReadAllRolesSite.java /main/13 2011/12/05 12:16:17 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:33 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:30 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:32 PM  Robert Pearse   
 *
 *   Revision 1.7.2.1  2004/10/15 18:50:28  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.8  2004/10/11 22:00:48  jdeleau
 *   @scr 7306 Fix roles not appearing after they are created
 *
 *   Revision 1.7  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:10:59  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/14 15:17:09  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:54:19  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Feb 24 2003 15:18:46   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.0   Apr 29 2002 15:37:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role;

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
    This site reads the Roles from the database.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class ReadAllRolesSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
       site name constant
    **/
    public static final String SITENAME = "ReadAllRolesSite";

    /**
       letter
    **/
    protected Letter letter = null;

    //--------------------------------------------------------------------------
    /**
       This method retrieves roles from the database.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // change Cargo type to RoleOptionsCargo
        RoleMainCargo cargo = (RoleMainCargo)bus.getCargo();

        RoleIfc[] roles = null;
        int errorCode = -1;

        try
        {
            // create a new database role transaction
            RoleTransaction rt = null;

            // Load all needed locales into a LocaleRequestor
            Locale[] locales = { LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE),
                                 LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE),
                                 LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)};           
            LocaleRequestor requestor = new LocaleRequestor(locales);            
            
            rt = (RoleTransaction) DataTransactionFactory.create(DataTransactionKeys.ROLE_TRANSACTION);
            
            SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
            inquiry.setLocaleRequestor(requestor);
            inquiry.setApplicationId(RoleIfc.POINT_OF_SALE);

            // read into memory all the roles from the database
            roles = rt.readRoles(inquiry);

            // set the roles into the RoleOptionsCargo
            cargo.setRoleList(roles);

            letter = new Letter(CommonLetterIfc.SUCCESS);
        }
        catch (DataException e)
        {
            errorCode = e.getErrorCode();

            switch(errorCode)
            {
                // we mail a success letter for error code 6,
                // which denotes an empty roles table
                case DataException.NO_DATA:
                // the query did not find a record or records
                // that matches your search criteria.
                cargo.setDataExceptionErrorCode(errorCode);
                // no existing records, but proceed onwards
                letter = new Letter(CommonLetterIfc.SUCCESS);
                break;
                // for error code numbers other than 6, we
                // mail a DBError letter
                case DataException.UNKNOWN:     // an Unknown error occurred
                case DataException.SQL_ERROR:     // a SQL error occurred
                case DataException.CONNECTION_ERROR:     // the database is offline
                default:
                cargo.setDataExceptionErrorCode(errorCode);
                // send a DBError letter to diplay an error dialog
                letter = new Letter(CommonLetterIfc.DB_ERROR);
                break;
            }
        }
        bus.mail(letter, BusIfc.CURRENT);

    }

    //---------------------------------------------------------------------
    /**
       Method to default display string function. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: ReadAllRolesSite"
                                      + " (Revision " + getRevisionNumber() + ")"
                                      + hashCode());

        // pass back result
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}
