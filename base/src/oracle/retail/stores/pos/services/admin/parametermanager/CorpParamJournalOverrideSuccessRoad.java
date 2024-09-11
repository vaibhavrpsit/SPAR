/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametermanager/CorpParamJournalOverrideSuccessRoad.java /main/12 2012/09/12 11:57:09 blarsen Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:31 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:24 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:13 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:48:50  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:33  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:52:34   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:39:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:04:06   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:19:06   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:11:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametermanager;

// foundation imports
import java.util.Locale;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.common.utility.LanguageResourceBundleUtil;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    This site writes the register status to the journal.
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class CorpParamJournalOverrideSuccessRoad extends LaneActionAdapter

{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    //----------------------------------------------------------------------
    /**
        Journals the register Override status. <P>
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
   public void traverse(BusIfc bus)
    {

        //Journal String
       // String     text = null;
        // get the Journal manager
        JournalManagerIfc jmi;
        jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        ParameterCargo cargo = (ParameterCargo)bus.getCargo();


        // journal the store status
		StringBuffer entry = new StringBuffer();
		Object[] dataArgs = new Object[2];
		dataArgs[0] = cargo.getSecurityOverrideRequestEmployee()
				.getEmployeeID();
		entry
				.append(I18NHelper
						.getString(
								I18NConstantsIfc.EJOURNAL_TYPE,
								JournalConstantsIfc.CORP_LEVEL_PARAMETER_SECURITY_OVERRIDE_LABEL,
								dataArgs));
		entry.append(Util.EOL);
		dataArgs[0] = cargo.getAccessEmployee().getEmployeeID();
		entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.ACCESS_GIVEN_BY_EMPLOYEE_LABEL, dataArgs));

		jmi.journal(entry.toString());
 }

    // ----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------

    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

}
