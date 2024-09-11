/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/useraccess/AbstractUserAccess.java /main/10 2011/02/16 09:13:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:08 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:29 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:21 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/09 16:56:03  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/02/27 01:46:35  baa
 *   @scr 0 remove deprecated 5.0 classes
 *
 *   Revision 1.3  2004/02/12 16:52:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:08:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Aug 07 2002 19:34:00   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jul 18 2002 17:51:34   baa
 * remove references to maximum_ACS which limits the ability to extend role functions
 * Resolution for POS SCR-1593: Optimize role function access check
 * 
 *    Rev 1.0   Apr 29 2002 14:57:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   04 Apr 2002 15:22:30   baa
 * Remove references to Rolefunction descriptor array and maximun number of role functions
 * Resolution for POS SCR-1565: Remove references to RoleFunctionIfc.Descriptor Security Service
 *
 *    Rev 1.0   Mar 18 2002 11:50:06   msg
 * Initial revision.
 *
 *    Rev 1.3   17 Jan 2002 17:37:54   baa
 * update roles/security model
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.2   09 Nov 2001 15:27:42   pdd
 * Cleanup.
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.1   22 Oct 2001 15:34:08   pdd
 * Added hasAccess() method that does not use cargo.
 * Deprecated hasAccess() method that uses cargo.
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.0   Sep 21 2001 11:27:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:15:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.useraccess;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.EmployeeIfc;

/**
 * This class houses the static method used to check user access.
 * 
 * @version $Revision: /main/10 $
 */
public abstract class AbstractUserAccess
{
    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(AbstractUserAccess.class);

    /**  revision number of this class */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * Determines if a user has access to a function.
     * 
     * @param employee EmployeeIfc
     * @param functionID int function identifier
     * @return boolean flag indicating if employee has access
     * @see oracle.retail.stores.domain.employee.RoleFunctionIfc
     */
    public static boolean hasAccess(EmployeeIfc employee, int functionID)
    {
        if (logger.isInfoEnabled()) logger.info("Entering AbstractUserAccess.hasAccess");
        boolean access = false;

        if (employee != null)
        {
                access = employee.hasAccessToFunction(functionID);
        }
        else
        {
            logger.warn( "AbstractUserAccess:  Employee is null");
        }

        if (logger.isInfoEnabled()) logger.info("Exiting AbstractUserAccess.hasAccess:  access = " + access);

        return access;
    }
}