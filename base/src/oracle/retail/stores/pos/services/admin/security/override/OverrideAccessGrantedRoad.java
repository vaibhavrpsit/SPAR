/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/override/OverrideAccessGrantedRoad.java /main/18 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    kelesika  10/25/10 - Improper journalling
 *    jkoppolu  07/12/10 - Modified as part of the fix for bug#9704082
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    deghosh   12/23/08 - EJ i18n changes
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:56 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:56 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:49:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:37:44  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   May 13 2003 10:56:52   adc
 * Removed deprecation
 * Resolution for 2340:  security override for Void  is not in E.Journals
 *
 *    Rev 1.0   May 13 2003 10:54:24   adc
 * Initial revision.
 * Resolution for 2340:  security override for Void  is not in E.Journals
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security.override;

// Java imports
import java.util.Locale;

import oracle.retail.stores.domain.employee.Role;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//------------------------------------------------------------------------------
/**
 * This class adds a journal entry when access is granted
 *
 **/
// ------------------------------------------------------------------------------
public class OverrideAccessGrantedRoad extends LaneActionAdapter
{
  /**
   * lane name constant
   **/
  public static final String LANENAME = "OverrideAccessGrantedRoad";


  // --------------------------------------------------------------------------
  /**
   * Make a journal entry.
   *
   * @param bus the bus traversing this lane
   **/
  // --------------------------------------------------------------------------
  public void traverse(BusIfc bus)
  { // begin traverse()
    // get the cargo for the information needed to check the access.
    SecurityOverrideCargo cargo = (SecurityOverrideCargo) bus.getCargo();
    int functionID = cargo.getAccessFunctionID();

    JournalManagerIfc journal = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);

    if ((journal != null) && (cargo.getOperator() != null) && (cargo.getLastOperator() != null))
    {

      // get the needed locale
      Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

      StringBuffer entry = new StringBuffer();
      Object[] dataArgs = new Object[2];
      entry.append(Util.EOL);
      entry.append(Role.getFunctionTitle(journalLocale, functionID));
      entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
          JournalConstantsIfc.SECURITY_OVERRIDE_GRANTED_LABEL, null));
      entry.append(Util.EOL);
      dataArgs[0] = cargo.getLastOperator().getEmployeeID();
      entry
          .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TO_CASHIER_LABEL, dataArgs));
      dataArgs[0] = cargo.getOperator().getEmployeeID();
      entry.append(Util.EOL);
      entry.append(I18NHelper
          .getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BY_EMPLOYEE_LABEL, dataArgs));
      journal.journal(entry.toString());
    }

  } // end traverse()
}
