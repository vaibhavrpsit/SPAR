/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayout/UpdateStatusSite.java /main/17 2013/01/31 14:56:10 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    rabhaw 01/31/13 - Cash text is always displayed in English for any
 *                      locale.
 *    cgreen 03/16/12 - split transaction-methods out of utilitymanager
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    abhayg 08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/26/10 - convert to oracle packaging
 *    acadar 04/06/10 - use default locale for currency, date and time display
 *    acadar 04/01/10 - use default locale for currency display
 *    abonda 01/03/10 - update header date
 *    deghos 12/02/08 - EJ i18n changes
 *    ohorne 10/31/08 - Localization of Till-related Reason Codes
 *
 * ===========================================================================


     $Log:
      6    360Commerce 1.5         8/14/2007 9:38:11 AM   Anda D. Cadar
           externaliza EJ for Pickup
      5    360Commerce 1.4         5/18/2007 9:18:13 AM   Anda D. Cadar   EJ
           and currency UI changes
      4    360Commerce 1.3         4/18/2007 1:30:03 PM   Ashok.Mondal    CR
           4259 :V7.2.2 merge to trunk.
      3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:26:37 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse
     $
     Revision 1.8.2.1  2004/10/15 18:50:29  kmcbride
     Merging in trunk changes that occurred during branching activity

     Revision 1.9  2004/10/08 17:45:29  bwf
     @scr 7332 Fixed payout journal.

     Revision 1.8  2004/07/23 21:54:13  rsachdeva
     @scr 4375 Address Lines in Journal match to what appears in the Receipt for Pay Out

     Revision 1.7  2004/07/22 04:56:57  khassen
     @scr 6296/6297/6298 - Updating pay in, pay out, payroll pay out:
     Adding database fields, print and reprint receipt functionality to reflect
     persistence of additional data in transaction.

     Revision 1.6  2004/07/20 22:42:48  bjbrown
     @scr 4375 added support for addres line 2 in reciept and e journal

     Revision 1.5  2004/03/12 18:16:27  khassen
     @scr 0 Till Pay In/Out use case

     Revision 1.4  2004/03/03 23:15:16  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:50:04  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:47:51  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Jan 21 2004 17:28:10   DCobb
 * Set journalEndOfTransaction=false in call to utility.saveTransaction() method.
 * Resolution for 3701: Timing problem can occur in CancelTransactionSite (multiple).
 *
 *    Rev 1.0   Aug 29 2003 15:58:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:26:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:28   msg
 * Initial revision.
 *
 *    Rev 1.3   08 Feb 2002 15:13:48   epd
 * fixed many ejournal defects
 * Resolution for POS SCR-696: Till pay out - ejournal entry incorrect, 'PST' should be 'CST'
 * Resolution for POS SCR-699: Till pay out - ejournal entry is incorrect, the cashier is repeated in the header
 * Resolution for POS SCR-700: Till Pay-out - ejournal entry is incorrect, broken line below Till Pay Out should be deleted
 * Resolution for POS SCR-701: Till pay out, ejournal entry is incorrect, 'amount' should be deleted
 * Resolution for POS SCR-702: Till pay out - ejournal entry is incorrect - the amount should not have a '-' sign
 *
 *    Rev 1.2   29 Jan 2002 09:54:22   epd
 * Deprecated all methods using accumulate parameter and added new methods without this parameter.  Also removed all reference to the parameter wherever used.
 * (The behavior is to accumulate totals)
 * Resolution for POS SCR-770: Remove the accumulate parameter and all references to it.
 *
 *    Rev 1.1   21 Nov 2001 14:27:46   epd
 * 1)  Creating txn at start of flow
 * 2)  Added new security access
 * 3)  Added cancel transaction site
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:19:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:54   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayout;

import java.util.Locale;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Updates the transaction to the database, does journaling.
 *
 * @author khassen
 */
public class UpdateStatusSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -1129172304469147720L;

    public static final String SITENAME = "UpdateStatusSite";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TillPayOutCargo cargo = (TillPayOutCargo) bus.getCargo();

        boolean saveTransSuccess = true;
    	POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        Letter letter = new Letter (CommonLetterIfc.SUCCESS);

        // Set updated information in transaction
        TillAdjustmentTransactionIfc transaction = cargo.getTransaction();
        transaction.setTimestampEnd();

        transaction.setAdjustmentAmount(cargo.getAmount());
        //each trip through the service only adjusts the financial totals data once
        transaction.setAdjustmentCount(1);
        transaction.setReason(cargo.getSelectedLocalizedReasonCode());
        transaction.setComments(cargo.getComments());
        transaction.setPayeeName(cargo.getPaidTo());
        transaction.setApproval(cargo.getSelectedLocalizedApprovalCode());
        transaction.setAddressLine(cargo.getAddressLine(0), 0);
        transaction.setAddressLine(cargo.getAddressLine(1), 1);
        transaction.setAddressLine(cargo.getAddressLine(2), 2);

        transaction.setTransactionType(TillAdjustmentTransactionIfc.TYPE_PAYOUT_TILL);

        FinancialTotalsIfc  totals      = transaction.getFinancialTotals();
        RegisterIfc         register    = cargo.getRegister();
        TillIfc             till        = register.getCurrentTill();

        till.addTotals(totals);
        register.addTotals(totals);

        //attempt to save the transaction
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        try
        {
            // Pass the transaction, a null totals object, the till,
            // register, and false indicating that the transaction has
            // not completed its journaling yet
            utility.saveTransaction(transaction, null, till, register, false);
            // set flag to reprint ID
            cargo.setLastReprintableTransactionID(transaction.getTransactionID());
        }
        catch (DataException e)
        {
            logger.error(
                    "Till payout error: " + Util.EOL + "" + e + "");
        	if (e.getErrorCode() == DataException.QUEUE_FULL_ERROR ||
        			e.getErrorCode() == DataException.STORAGE_SPACE_ERROR ||
        			e.getErrorCode() == DataException.QUEUE_OP_FAILED)
        	{
        		saveTransSuccess = false;
                UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        		DialogBeanModel dialogModel = util.createErrorDialogBeanModel(e, false);
                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
        	}
        	else
        	{
                // set error code
                cargo.setDataExceptionErrorCode(e.getErrorCode());
                letter = new Letter (CommonLetterIfc.UPDATE_ERROR);
        	}
        }
        if (saveTransSuccess)
        {
	        cargo.setTransaction(transaction);
	
	        if(letter.getName().equals(CommonLetterIfc.SUCCESS))
	        {
	            // get the Journal manager
	            JournalManagerIfc jmi =
	                (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
	
	            // journal the till status
	            if (jmi != null)
	            {
	                StringBuffer sb = new StringBuffer();
	                Object[] dataArgs = new Object[2];
	                Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
	
	                // journal pay-out detail here.
	                sb.append(Util.EOL);
	                sb.append(Util.EOL);
	                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PAY_OUT_LABEL, null));
	                sb.append(Util.EOL);
	                sb.append(Util.EOL);
	
	                dataArgs[0] = cargo.getRegister().getCurrentTillID();
	                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
							JournalConstantsIfc.TILL_ID_LABEL, dataArgs));
	                sb.append(Util.EOL);
	
	                //use default locale for currency display
	                dataArgs[0] = cargo.getAmount().toFormattedString();
	                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PAY_OUT_TILLPAYOUT_LABEL, dataArgs));
	                sb.append(Util.EOL);
	
	                dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CASH);
	                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TENDER_TYPE_LABEL, dataArgs));
	                sb.append(Util.EOL);
	
	                LocalizedCodeIfc selectedReasonCode = cargo.getSelectedLocalizedReasonCode();
	
	                dataArgs[0] = selectedReasonCode.getText(locale);
	                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REASON_LABEL, dataArgs));
	                sb.append(Util.EOL);
	
	                dataArgs[0] = selectedReasonCode.getCode();
	                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REASON_NUMBER_LABEL, dataArgs));
	                sb.append(Util.EOL);
	
	                dataArgs[0] = cargo.getPaidTo();
	                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PAID_TO_LABEL, dataArgs));
	                sb.append(Util.EOL);
	
	                String [] addressLines = transaction.getAddressLines();
	                for (int j = 0; j< addressLines.length; j++)
	                {
	                    if ((addressLines[j] != null) && (addressLines[j].length() > 0))
	                    {
	                    	dataArgs[0] = j+1;
	                    	dataArgs[1] = addressLines[j];
	                    	sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ADDRESS_LINE_TAG_LABEL, dataArgs))
	                          .append(Util.EOL);
	                    }
	                }
	
	                LocalizedCodeIfc selectedApprovalCode = cargo.getSelectedLocalizedApprovalCode();
	
	                dataArgs[0] = selectedApprovalCode.getText(locale);
	                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.APPROVAL_LABEL, dataArgs));
	                sb.append(Util.EOL);
	
	                dataArgs[0] = cargo.getComments();
	                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.COMMENTS_LABEL, dataArgs));
	                sb.append(Util.EOL);
	
	                jmi.journal(cargo.getOperator().getEmployeeID(),
	                            transaction.getTransactionID(), sb.toString());
	                utility.completeTransactionJournaling(transaction);
	            }
	            else
	            {
	                logger.warn( "No journal manager found.");
	            }
	        }
	
	        bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
