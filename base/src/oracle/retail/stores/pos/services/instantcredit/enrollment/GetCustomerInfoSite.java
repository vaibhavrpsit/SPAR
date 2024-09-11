/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/GetCustomerInfoSite.java /main/18 2013/07/08 18:18:48 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/08/13 - Mark it updated to void clearing the content when
 *                         Customer Name link clicked.
 *    mchellap  04/24/13 - Splitting DOB to birth date and birth year
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       05/18/11 - remove major card swipe from enrollment flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   03/16/10 - fix missing EOL in journal
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         5/29/2008 4:07:26 PM   Deepti Sharma
 *       CR-31672 changes for instant credit enrollment. Code reviewed by Alan
 *        Sinton
 *  4    360Commerce 1.3         7/19/2007 1:51:14 PM   Mathews Kochummen
 *       format date,time
 *  3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:21:48 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse
 * $
 * Revision 1.7  2004/04/02 23:19:01  jdeleau
 * @scr 4218 Remove code that was in place for debugging
 *
 * Revision 1.6  2004/04/02 23:07:51  jdeleau
 * @scr 4218 Register Reports - House Account and initial changes to
 * the way SummaryReports are built.
 *
 * Revision 1.5  2004/03/26 21:18:20  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.4  2004/03/25 23:42:10  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.3 2004/02/12 16:50:42 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:51:22 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:16 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.4 Jan 09 2004 15:24:50 nrao Fix for SCR 3699. Fixed journaling for House Account Enroll. Resolution for 3699:
 * House Account Enroll- E. Jouranl is not correct.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.InstantCreditCustomerBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * @version $Revision: /main/18 $
 */
public class GetCustomerInfoSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 1538034651349784097L;
    /** revision number supplied by version control * */
    public static final String revisionNumber = "$Revision: /main/18 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        JournalManagerIfc jm = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        UtilityManager utility = (UtilityManager) bus.getManager(UtilityManagerIfc.TYPE);

        CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();

        // model to use for the UI -- similar to the way Add Customer screen
        // gets it's model.
        InstantCreditCustomerBeanModel custModel = InstantCreditUtilities
                .getInstantCreditCustomerInfo(customer, utility, pm);

        custModel.setFirstRun(cargo.isFirstRun());
        custModel.setBirthdateValid(true);
        custModel.setBirthYearValid(true);

        if (cargo.getTransaction() == null)
        {
            InstantCreditTransactionIfc trans = DomainGateway.getFactory()
                                        .getInstantCreditTransactionInstance();
            TransactionUtilityManagerIfc tutility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
            tutility.initializeTransaction(trans);
            trans.setTransactionType(TransactionIfc.TYPE_INSTANT_CREDIT_ENROLLMENT);
            cargo.setTransaction(trans);
        }

        if (jm != null)
        {
            StringBuilder ejText = new StringBuilder(200);
            Object[] dataArgs = new Object[2];
            ejText.append(Util.EOL).append(
            		I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
            				JournalConstantsIfc.HOUSE_ACCOUNT_ENROLL_LABEL, null));
            
            String transactionID = cargo.getTransaction().getTransactionID();
            dataArgs[0] = cargo.getStoreStatus().getStore().getStoreID();
            ejText.append(Util.EOL).append(
            		I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
            				JournalConstantsIfc.STORE_NUMBER_LABEL, dataArgs));
            
            dataArgs[0] = cargo.getRegister().getWorkstation().getWorkstationID();
            ejText.append(Util.EOL).append(
            		I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
            				JournalConstantsIfc.REGISTER_NUMBER_LABEL, dataArgs));

            dataArgs[0] = cargo.getOperator().getLoginID();
            ejText.append(Util.EOL).append(
            		I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
            				JournalConstantsIfc.CASHIER_ID_LABEL, dataArgs));

            dataArgs[0] = cargo.getEmployeeID();
            ejText.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.SALES_ASSOCIATE_ID_LABEL, dataArgs));

            dataArgs[0] = transactionID;
            ejText.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.TRANSACTION_NUMBER_LABEL, dataArgs));
            // journal the House Account Enroll info
            jm.journal(ejText.toString());

            ejText.setLength(0);
            Date date = new Date();
            DateTimeServiceIfc dateTimeService = DateTimeServiceLocator
            		.getDateTimeService();
            Locale defaultLocale = LocaleMap
                    .getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            String dateString = dateTimeService.formatDate(date, defaultLocale,
                    DateFormat.SHORT);
            String timeString = dateTimeService.formatTime(date, defaultLocale,
                    DateFormat.SHORT);
            dataArgs[0] = dateString;
            ejText.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.DATESTAMP_LABEL, dataArgs));
            dataArgs[0] = timeString;
            ejText.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.TIMESTAMP_LABEL, dataArgs));
            // journal the date and time
            jm.journal(ejText.toString());

            if (logger.isInfoEnabled())
                logger.info("Transaction " + transactionID + " Number");
        }
        else
        {
            logger.error(Util.getSimpleClassName(getClass()) + ": No JournalManager available.");
        }

        ui.showScreen(POSUIManagerIfc.INSTANT_CREDIT_CUSTOMER_INFO, custModel);
        
        custModel.setFirstRun(false);
    }
}
