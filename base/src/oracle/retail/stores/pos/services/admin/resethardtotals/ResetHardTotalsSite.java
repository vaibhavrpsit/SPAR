/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/resethardtotals/ResetHardTotalsSite.java /main/16 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    vchengeg  11/26/08 - Inserted appropriate New Lines in the Ejournal for
 *                         Reset Hardtotals
 *
 * ===========================================================================
 * $Log:
 5    360Commerce 1.4         7/11/2007 10:14:01 AM  Mathews Kochummen use
 short time format
 4    360Commerce 1.3         8/9/2006 11:27:24 AM   Charles D. Baker
 Updated to include date in hard total reset journalling.
 3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:24:44 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:13:44 PM  Robert Pearse
 *
 Revision 1.4  2004/03/03 23:15:08  bwf
 @scr 0 Fixed CommonLetterIfc deprecations.
 *
 Revision 1.3  2004/02/12 16:48:55  mcs
 Forcing head revision
 *
 Revision 1.2  2004/02/11 21:36:47  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 26 2003 11:39:38   RSachdeva
 * Removed use of  toFormattedString()
 * Resolution for POS SCR-2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.1   24 May 2002 18:54:34   vxs
 * Removed unncessary concatenations from log statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:38:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:06:42   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:20:46   msg
 * Initial revision.
 *
 *    Rev 1.2   12 Mar 2002 16:52:36   pdd
 * Modified to use the factory.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.1   06 Dec 2001 13:43:12   epd
 * added date and operator ID to journalling of HT reset
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   28 Nov 2001 12:58:40   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.resethardtotals;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.HardTotalsBuilderIfc;
import oracle.retail.stores.domain.financial.HardTotalsFormatException;
import oracle.retail.stores.domain.financial.HardTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This site confirms that the user wants to open the store.
 *
 */

@SuppressWarnings("serial")
public class ResetHardTotalsSite extends PosSiteActionAdapter
{

    /**
     * Confirms that the user wants to open the store.
     *
     * @param bus the bus
     */
    @Override

    public void arrive(BusIfc bus)
    {
        JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE); // TODO MPOS: is getting this from the bus ok for both POSes?
//		JournalManagerIfc jmi = (JournalManagerIfc) Gateway.getDispatcher()
//				.getManager(JournalManagerIfc.TYPE);
		ResetHardTotalsCargo cargo = (ResetHardTotalsCargo) bus.getCargo();
		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
		Letter letter = new Letter(CommonLetterIfc.CONTINUE);

		// The hard totals object
		// Read, save, journal, print, reset, and write hard totals
		try {
			// Read hard totals
			Serializable data = pda.readHardTotals();
			HardTotalsBuilderIfc builder = DomainGateway.getFactory()
					.getHardTotalsBuilderInstance();
			builder.setHardTotalsInput(data);
			HardTotalsIfc ht = (HardTotalsIfc) builder.getFieldAsClass();
			ht.setHardTotalsData(builder);

			RegisterIfc register = ht.getRegister();

			// Journal the hard totals
			if (jmi != null) {
				String storeID = ht.getStoreStatus().getStore().getStoreID();
				String registerID = ht.getRegister().getWorkstation()
						.getWorkstationID();
				EmployeeIfc cashier = cargo.getOperator();
				EYSDate currentDate = DomainGateway.getFactory()
						.getEYSDateInstance();
				StringBuffer entry = new StringBuffer();
				Object[] dataArgs = new Object[2];
				entry.append(Util.EOL);
				entry.append(Util.EOL);
				//For journal subsystem
				Locale locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
				entry.append(currentDate.toFormattedString(locale));
				entry.append(" ")
						.append(
								DomainGateway.getFactory().getEYSDateInstance()
										.toFormattedTimeString(
												DateFormat.SHORT, locale));
				if (cashier != null) {
					entry.append(Util.EOL);
					dataArgs[0] = cashier.getEmployeeID();
					entry.append(I18NHelper.getString(
							I18NConstantsIfc.EJOURNAL_TYPE,
							JournalConstantsIfc.OPERATOR_ID_LABEL, dataArgs));
					entry.append(Util.EOL);
					dataArgs[0] = storeID;
					entry.append(I18NHelper.getString(
							I18NConstantsIfc.EJOURNAL_TYPE,
							JournalConstantsIfc.STORE_ID_LABEL, dataArgs));
					entry.append(Util.EOL);
					dataArgs[0] = registerID;
					entry.append(I18NHelper.getString(
							I18NConstantsIfc.EJOURNAL_TYPE,
							JournalConstantsIfc.REGISTER_ID_LABEL, dataArgs));

				}
				entry.append(Util.EOL);
				entry.append(I18NHelper.getString(
						I18NConstantsIfc.EJOURNAL_TYPE,
						JournalConstantsIfc.HARD_TOTALS_HAVE_BEEN_RESET_LABEL,
						null));
				entry.append(Util.EOL);
				entry.append(Util.EOL);
				jmi.journal(" ", " ", entry.toString());
			}

			// Reset totals
			register.resetTotals();

			// Save hard totals
			ht.setLastUpdate();
			builder = DomainGateway.getFactory().getHardTotalsBuilderInstance();
			ht.getHardTotalsData(builder);
			pda.writeHardTotals(builder.getHardTotalsOutput());
		} catch (DeviceException e) {
			logger.warn("Unable to reset hard totals. " + e.getMessage() + "");

			if (e.getCause() != null) {
				logger.warn("DeviceException.NestedException:\n"
						+ Util.throwableToString(e.getCause()));
			}
		} catch (HardTotalsFormatException htfe) {
			logger
					.warn("Unable to reset hard totals due to a HardTotalsFormatException. "
							+ htfe.getMessage() + "");
		}

		bus.mail(letter, BusIfc.CURRENT);
	}

    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
