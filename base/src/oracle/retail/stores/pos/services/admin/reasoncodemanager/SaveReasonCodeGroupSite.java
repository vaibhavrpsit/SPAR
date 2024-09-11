/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/reasoncodemanager/SaveReasonCodeGroupSite.java /main/14 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mdecama   11/03/08 - Using a new transaction to save the code list
 * ===========================================================================
 $Log:
  4    360Commerce 1.3         8/14/2007 7:27:24 AM   Manikandan Chellapan
       CR28329 Fixed EJ Spelling mistake
  3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse
  2    360Commerce 1.1         3/10/2005 10:25:02 AM  Robert Pearse
  1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse
 $
 Revision 1.9  2004/07/19 15:15:09  bjbrown
 @scr 4962
 *
 added journaling after change saved
 *
 Revision 1.8  2004/06/03 14:47:44  epd
 @scr 5368 Update to use of DataTransactionFactory
 *
 Revision 1.7  2004/04/20 13:11:00  tmorris
 @scr 4332 -Sorted imports
 *
 Revision 1.6  2004/04/15 16:50:11  tmorris
 @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 Revision 1.5  2004/04/09 13:01:40  pkillick
 @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 Revision 1.4  2004/03/03 23:15:10  bwf
 @scr 0 Fixed CommonLetterIfc deprecations.
 *
 Revision 1.3  2004/02/12 16:48:53  mcs
 Forcing head revision
 *
 Revision 1.2  2004/02/11 21:36:39  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:53:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Feb 17 2003 13:59:32   crain
 * Updated the code list map in utility manager
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.0   Apr 29 2002 15:38:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:06:28   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:20:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:12:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.reasoncodemanager;

import java.util.Vector;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.CodeListSaveDataTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSaveCriteria;
import oracle.retail.stores.domain.utility.CodeListSaveCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;
import oracle.retail.stores.pos.ui.beans.ReasonCodesCommon;

/**
 * Save the modified reason code group.
 * 
 * @version $Revision: /main/14 $
 */
@SuppressWarnings("serial")
public class SaveReasonCodeGroupSite extends PosSiteActionAdapter
{
    public static final String SITENAME = "SaveReasonCodeGroupSite";

    /** The system dependent line separator. * */
    protected static final String EOL = System.getProperty("line.separator");

    /**
     * Try to save the reason code values. Send a letter indicating whether the
     * save was successful.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ReasonCodeCargo cargo = (ReasonCodeCargo) bus.getCargo();
        ReasonCodeGroupBeanModel rcGroup = cargo.getReasonCodeGroup();
        Letter letter = new Letter(ReasonCodesCommon.SAVE_SUCCEEDED);


        // Make the reason code journal entry
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        try {
            CodeListIfc list = cargo.convertBeanToCodeList(rcGroup);

            // Save the code list to the database.
            CodeListSaveDataTransaction dt = null;
            dt = (CodeListSaveDataTransaction) DataTransactionFactory
                    .create(DataTransactionKeys.CODE_LIST_SAVE_DATA_TRANSACTION);

            CodeListSaveCriteriaIfc criteria = new CodeListSaveCriteria();
            criteria.setCodeList(list);
            criteria.setUserLocale(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            dt.saveCodeList(criteria);


            // construct journal entry from cargo
            StringBuffer entry = new StringBuffer();
			Object[] dataArgs = new Object[2];
			dataArgs[0] = cargo.reasonCodeGroup.getGroupName();
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.REASON_CODE_GROUP_EDITED_LABEL,
					dataArgs));
			entry.append(Util.EOL);
			entry.append(Util.EOL);

			Vector<ReasonCode> reasonCodes = cargo.reasonCodeGroup.getReasonCodes();

			dataArgs[0] = cargo.reasonCodeGroup.getDefaultReasonCode();
			entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.DEFAULT_LABEL, dataArgs));
			entry.append(Util.EOL);
			entry.append(Util.EOL);

            // iterate through reason code entries
            for (int i = 0; i < reasonCodes.size(); i++) {
                ReasonCode reason = reasonCodes.get(i);
                // no real modified flag so check if edit screen was used
                if (reason.getStatusBeanModel() != null) {
                    entry.append(reason.getStatusBeanModel().getScreenName())
                            .append(EOL).append(reason.getNewReasonCodeName())
                            .append(" = ").append(reason.getNewDatabaseId())
                            .append(EOL).append(EOL);
                }
            }

            journal.journal(entry.toString());

        } // end read code list map
          // catch problems on the lookup
        catch (DataException e)
        { // begin handle data exception
            // set error code
            cargo.setDataExceptionErrorCode(e.getErrorCode());
            logger.error("Code list write error.");
            letter = new Letter(CommonLetterIfc.DB_ERROR);
        } // end handle data exception

        bus.mail(letter, BusIfc.CURRENT);
    }
}
