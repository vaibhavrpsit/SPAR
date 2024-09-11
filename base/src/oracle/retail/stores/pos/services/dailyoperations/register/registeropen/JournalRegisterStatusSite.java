/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registeropen/JournalRegisterStatusSite.java /main/16 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    nganesh   02/18/09 - Modified Register Open EJ format
 *    deghosh   12/23/08 - EJ i18n changes
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         6/13/2007 9:15:59 AM   Rohit Sachdeva
 *         26364:Fixing the Issue regarding EJ always having this error
 *         �Failure: Foreign Exchange Rate update�. Requirements are
 *         accordingly updated
 *         as per recent changes done in I18N regarding currency xml files not
 *          being used.
 *    4    360Commerce 1.3         4/26/2007 6:10:07 PM   Ashok.Mondal    CR
 *         19537 :V7.2.2 merge to trunk.
 *    3    360Commerce 1.2         3/31/2005 4:28:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:58 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:11 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:13  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:57:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   May 23 2003 06:52:36   jgs
 * Modified to delay the end of transaction journal entry.
 * Resolution for 2543: Modify EJournal to put entries into a JMS Queue on the store server.
 *
 *    Rev 1.0   Apr 29 2002 15:29:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:15:08   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:27:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:17:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registeropen;

import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This site writes the register status to the journal.
 * 
 * @version $Revision: /main/16 $
 */
public class JournalRegisterStatusSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 43717231336445649L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * Journals the register status.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get the Journal manager
        JournalManagerIfc jmi;
        jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        RegisterOpenCargo cargo = (RegisterOpenCargo)bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        String letterName = null;
        StringBuffer entry = new StringBuffer();
		Object[] dataArgs = new Object[2];

        // journal the store status
        if (register.getStatus() == AbstractStatusEntityIfc.STATUS_OPEN) {
			letterName = CommonLetterIfc.CONTINUE;
			dataArgs[0] = register.getWorkstation().getWorkstationID();
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.REG_LABEL, dataArgs));
			entry.append(" ");
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.OPENED_LABEL, dataArgs));
			entry.append(Util.EOL);
			dataArgs[0] = register.getBusinessDate().toFormattedString();
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.BUS_DATE_LABEL, dataArgs));
			entry.append(Util.EOL);
			dataArgs[0] = register.getSignOnOperator().getEmployeeID();
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.EMPLOYEE_ID_LABEL, dataArgs));
			jmi.journal(cargo.getOperator().getLoginID(), (String) null, entry
					.toString());
		} else {
			letterName = CommonLetterIfc.FAILURE;
			dataArgs[0] = register.getWorkstation().getWorkstationID();
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.REG_LABEL, dataArgs));
			entry.append(" ");
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.OPEN_FAILED_LABEL, dataArgs));
			entry.append(Util.EOL);
			dataArgs[0] = register.getBusinessDate();
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.BUS_DATE_LABEL, dataArgs));
			entry.append(Util.EOL);
			dataArgs[0] = register.getSignOnOperator().getEmployeeID();
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.EMPLOYEE_ID_LABEL, dataArgs));
			jmi.journal(cargo.getOperator().getLoginID(), (String) null, entry
					.toString());
		}

        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        utility.completeTransactionJournaling(cargo.getTransaction());

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
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
