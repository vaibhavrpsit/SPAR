/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/CheckCountTillSite.java /main/18 2011/12/05 12:16:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    nganesh   04/10/09 - Internationalized currency denominations in auditlog
 *    nganesh   03/20/09 - Modified EJournal for denomination
 *                         internationalization refactoring
 *
 * ===========================================================================
 * $Log:
 *12   360Commerce 1.11        6/6/2008 5:46:22 AM    Manikandan Chellapan
 *     CR#31956 Fixed audit log user id logging
 *11   360Commerce 1.10        4/18/2008 5:10:23 AM   Deepankar Ghosh Modified
 *     a line for setting correct operator ID
 *10   360Commerce 1.9         3/3/2008 4:51:51 AM    Manikandan Chellapan
 *     CR#30393 CountFloatAtReconcile event changes
 *9    360Commerce 1.8         1/24/2008 2:45:51 AM   Chengegowda Venkatesh
 *     PABP 30 - Originating Point checkin for BO and CO audit Log
 *8    360Commerce 1.7         1/24/2008 12:03:32 AM  Chengegowda Venkatesh
 *     PABP 30 - Originating Point checkin for BO and CO audit Log
 *7    360Commerce 1.6         1/18/2008 4:29:59 AM   Chengegowda Venkatesh
 *     PABP 30 - Originating Point checkin for BO and CO audit Log
 *6    360Commerce 1.5         1/7/2008 10:18:23 PM   Chengegowda Venkatesh
 *     Changes for AuditLog incorporation
 *5    360Commerce 1.4         12/19/2007 9:16:04 AM  Manikandan Chellapan
 *     Reverting back to previous version
 *4    360Commerce 1.3         12/19/2007 8:13:52 AM  Manikandan Chellapan PAPB
 *      FR27 Bulk Checkin -2
 *3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse
 *2    360Commerce 1.1         3/10/2005 10:20:05 AM  Robert Pearse
 *1    360Commerce 1.0         2/11/2005 12:09:53 PM  Robert Pearse
 *
 Revision 1.2  2004/09/23 00:07:12  kmcbride
 @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 Revision 1.1  2004/04/15 18:57:00  dcobb
 @scr 4205 Feature Enhancement: Till Options
 Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

// java imports
import java.util.ArrayList;
import java.util.Hashtable;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.CountFloatAtTillReconcileEvent;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;

//------------------------------------------------------------------------------
/**
 @version $Revision: /main/18 $
 **/
//------------------------------------------------------------------------------
public class CheckCountTillSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2794458007749896929L;

    /** The revision number */
    public static final String revisionNumber = "$Revision: /main/18 $";

    //--------------------------------------------------------------------------
    /**
     @param bus the bus arriving at this site
     **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        TillReconcileCargo cargo = (TillReconcileCargo) bus.getCargo();

        //log float count event
        if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
        {
            logAuditEvent(cargo);
        }

        // set default letter
        Letter letter = new Letter(CommonLetterIfc.NO);

        boolean setEmptyTotals = false;

        RegisterIfc register = cargo.getRegister();

        String TillCountTillAtReconcile = FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register
                .getTillCountTillAtReconcile()];

        if (TillCountTillAtReconcile.compareTo("No") == 0)
        {
            cargo.setTillCountType(FinancialCountIfc.COUNT_TYPE_NONE);
            letter = new Letter(CommonLetterIfc.NO);
            setEmptyTotals = true;
        }
        else if (TillCountTillAtReconcile.compareTo("Summary") == 0)
        {
            cargo.setTillCountType(FinancialCountIfc.COUNT_TYPE_SUMMARY);
            letter = new Letter(CommonLetterIfc.YES);
        }
        else if (TillCountTillAtReconcile.compareTo("Detail") == 0)
        {
            cargo.setTillCountType(FinancialCountIfc.COUNT_TYPE_DETAIL);
            letter = new Letter(CommonLetterIfc.YES);
        }
        else
        {
            // set to default as defined in requirements
            cargo.setTillCountType(FinancialCountIfc.COUNT_TYPE_SUMMARY);
            letter = new Letter(CommonLetterIfc.YES);
        }

        if (setEmptyTotals == true)
        {
            cargo.setTillTotals(DomainGateway.getFactory().getFinancialTotalsInstance());
        }

        bus.mail(letter, BusIfc.CURRENT);

    }

    //--------------------------------------------------------------------------
    /**
     * Logs current event to audit log
     @param cargo The till reconcile cargo
     **/
    //--------------------------------------------------------------------------
    private void logAuditEvent(TillReconcileCargo cargo)
    {
        // Get audit log service
        AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();

        // Create credit transaction event
        CountFloatAtTillReconcileEvent countFloatEvent = (CountFloatAtTillReconcileEvent) AuditLoggingUtils
                .createLogEvent(CountFloatAtTillReconcileEvent.class, AuditLogEventEnum.COUNT_FLOAT_AT_RECONCILE);

        // Set store id
        countFloatEvent.setStoreId(cargo.getRegister().getWorkstation().getStoreID());

        // Set register id
        countFloatEvent.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());

        // Set till id
        countFloatEvent.setTillID(cargo.getTillID());

        // Set user id
        if (cargo.getOperator() != null)
        {
            countFloatEvent.setUserId(cargo.getOperator().getLoginID());
        }

        // Set operator id
        if (cargo.getTill() != null)
        {
            countFloatEvent.setOperatorID(cargo.getTill().getSignOnOperator().getEmployeeID());
        }

        // Set business date
        countFloatEvent.setBusinessDate(cargo.getRegister().getBusinessDate().dateValue());

        // Set float amount
        String floatCountType = FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[cargo.getRegister()
                .getTillCountFloatAtReconcile()];


        if (floatCountType.compareTo("No") == 0)
        {
            // if count type is "No" then set the float to expected float
            countFloatEvent.setFloatAmount(cargo.getTill().getTotals().getStartingFloatCount().getEntered().getAmount()
                    .toFormattedString());
        }
        else if (floatCountType.compareTo("Summary") == 0)
        {
            // if count type is of summary type then set the float to entered float
            countFloatEvent.setFloatAmount(cargo.getFloatTotals().getEndingFloatCount().getEntered().getAmount()
                    .toFormattedString());
        }
        else if (floatCountType.compareTo("Detail") == 0)
        {
            countFloatEvent.setFloatAmount(cargo.getFloatTotals().getEndingFloatCount().getEntered().getAmount()
                    .toFormattedString());

            Hashtable<String, String> denomTable = new Hashtable<String, String>();

            ArrayList<String> denominationOrder = new ArrayList<String>();

            FinancialTotalsIfc floatFti = cargo.getFloatTotals();
            ReconcilableCountIfc floatRci = floatFti.getEndingFloatCount();
            FinancialCountIfc floatFci = floatRci.getEntered();
            FinancialCountTenderItemIfc[] floatFcti = floatFci.getTenderItems();

            for (int i = 0; i < floatFcti.length; i++)
            {
                if (floatFcti[i].isSummary() == false)
                {

                	String desc = floatFcti[i].getTenderDescriptor().getDenomination().getDenominationDisplayName(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
                    int num = floatFcti[i].getNumberItemsOut();

                    denominationOrder.add(desc);
                    denomTable.put(desc, String.valueOf(num));

                }

            }

            // Create a denomination log element
            String[] order = new String[denominationOrder.size()];
            denominationOrder.toArray(order);

            // Add denomination details
            countFloatEvent.addDenomination(denomTable, order);

        }

        // Log the event details
        countFloatEvent.setEventOriginator("LookupStoreStatusSite.arrive");

        auditService.logStatusSuccess(countFloatEvent);

    }
}
