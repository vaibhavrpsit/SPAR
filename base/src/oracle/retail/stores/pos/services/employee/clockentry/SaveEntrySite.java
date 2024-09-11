/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/clockentry/SaveEntrySite.java /main/20 2012/09/12 11:57:18 blarsen Exp $
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
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    vchengeg  12/10/08 - EJ defect fixes
 *    vchengeg  12/09/08 - EJ I18n changes
 *    deghosh   11/25/08 - EJ i18n changes
 *    mdecama   11/05/08 - I18N Reason Code - Refactored the EmployeeClockEntry
 *                         reason field.
 *    mdecama   11/04/08 - I18N - Fixed the way the locale was being retrieved
 *    akandru   10/30/08 - EJ changes
 *    mdecama   10/20/08 - Refactored Dropdowns to use the new
 *                         CodeListManagerIfc

     $Log:
      4    360Commerce 1.3         4/25/2007 8:52:27 AM   Anda D. Cadar   I18N
           merge

      3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:25:02 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse
     $
     Revision 1.6  2004/06/03 14:47:43  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.5  2004/04/15 20:17:50  tmorris
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.4  2004/03/03 23:15:07  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:50:15  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:49:15  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:59:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   07 May 2002 15:24:22   dfh
 * updates for adding clock entry type -
 * Resolution for POS SCR-1622: Employee Clock In/Out needs type code
 *
 *    Rev 1.0   Apr 29 2002 15:24:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:32:02   msg
 * Initial revision.
 *
 *    Rev 1.0   28 Oct 2001 17:55:54   mpm
 * Initial revision.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.employee.clockentry;
// java imports
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeWriteTimeClockTransaction;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
//------------------------------------------------------------------------------
/**
    Saves the clock entry to the queue.
**/
//------------------------------------------------------------------------------
public class SaveEntrySite
extends PosSiteActionAdapter
{
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = -5332434584115722367L;

    /**
       journal string constant for entry type code In value
    **/
    public static String inString = EmployeeClockEntryIfc.TYPE_DESCRIPTORS[EmployeeClockEntryIfc.TYPE_IN];
    /**
       journal string constant for entry type code Out value
    **/
    public static String outString = EmployeeClockEntryIfc.TYPE_DESCRIPTORS[EmployeeClockEntryIfc.TYPE_OUT];
    /**
       journal string constant for the entry type code label
    **/
    public static String typeString = "Type: ";

    //--------------------------------------------------------------------------
    /**
        Saves the clock entry to the queue and journals the entry.
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        String letterName = CommonLetterIfc.SUCCESS;

        // get the Journal manager
        JournalManagerIfc jmi =
          (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);

        ClockEntryCargo cargo = (ClockEntryCargo) bus.getCargo();
        EmployeeClockEntryIfc clockEntry = cargo.getClockEntry();
        try
        {
            // write clock entry for employee
            EmployeeWriteTimeClockTransaction writeDataTransaction = null;

            writeDataTransaction = (EmployeeWriteTimeClockTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_WRITE_TIME_CLOCK_TRANSACTION);

            writeDataTransaction.insertEmployeeClockEntry(clockEntry);

            // journal results, if possible
            if (jmi == null)
            {
                logger.error(
                             "Journal not available.");
            }
            else
            {
                // build string for data entry
                StringBuffer journalString = new StringBuffer();
				journalString
						.append(I18NHelper
								.getString(
										I18NConstantsIfc.EJOURNAL_TYPE,
										JournalConstantsIfc.ENTERING_EMPLOYEE_CLOCKIN_CLOCKOUT_LABEL,
										null));

				// append date time, register ID
				Date date = clockEntry.getClockEntry().dateValue();
				Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
				DateTimeServiceIfc dateTimeService = DateTimeServiceLocator
						.getDateTimeService();
				String dateString = dateTimeService.formatDate(date,
						journalLocale, DateFormat.SHORT);
				String timeString = dateTimeService.formatTime(date,
						journalLocale, DateFormat.SHORT);
				journalString.append(Util.EOL).append(dateString).append(" ")
						.append(timeString).append(Util.EOL);
				Object[] dataArgs = new Object[1];
				dataArgs[0] = cargo.getRegister().getWorkstation()
						.getWorkstationID();
				journalString.append(I18NHelper.getString(
						I18NConstantsIfc.EJOURNAL_TYPE,
						JournalConstantsIfc.REG_SAVE_ENTRY_LABEL, dataArgs));
				journalString.append(Util.EOL)
						.append(Util.EOL);
				dataArgs[0] = clockEntry.getEmployee().getEmployeeID();
				journalString.append(
						I18NHelper
								.getString(I18NConstantsIfc.EJOURNAL_TYPE,
										JournalConstantsIfc.EMPLOYEE_LABEL,
										dataArgs)).append(Util.EOL);

                // get reason text
                String reasonCode = clockEntry.getReason().getCode();

                Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

                String reasonString =
                cargo.getReasonCodes().findListEntryByCode(reasonCode).getText(lcl);
                // get the entry type code
                String typeCodeString = "";
                if (clockEntry.getTypeCode() == EmployeeClockEntryIfc.TYPE_IN) // in type
                {
                    typeCodeString = inString;
                }
                else  // out type
                {
                    typeCodeString = outString;
                }

                dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,"JournalEntry."+typeCodeString+ "Label", null);

                journalString.append(reasonString)
                             .append(Util.EOL)
                             .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TYPE_LABEL, dataArgs))
                             .append(Util.EOL).append(Util.EOL)
                             .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EXITING_EMPLOYEE_CLOCK_INCLOCK_OUT_LABEL_LABEL, null));
                jmi.journal(clockEntry.getEmployee().getEmployeeID(),"",
                            journalString.toString());
            }
        }
        // eat the data exception
        catch (DataException de)
        {
            cargo.setDataExceptionErrorCode(de.getErrorCode());
            letterName = CommonLetterIfc.FAILURE;
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
}
