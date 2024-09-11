/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/DisplayDailyOpsScreenSite.java /main/16 2012/10/16 17:37:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/10/12 - Popup menu implementation
 *    nkgautam  08/25/10 - fixed online/offline status colour issue
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    mahising  03/10/09 - Fixed register number display issue if a new user
 *                         created having admin right
 *
 * ===========================================================================
 * $Log:
 *  8    360Commerce 1.7         6/7/2008 6:09:16 AM    Manikandan Chellapan
 *       CR#31924 Enabled audit logging for training and reentry login logout 
 *  7    360Commerce 1.6         5/22/2008 7:05:00 AM   subramanyaprasad gv For
 *        CR 31731: Code reviewed by Manikandan Chellapan.
 *  6    360Commerce 1.5         3/6/2008 5:42:01 AM    Chengegowda Venkatesh
 *       For CR 30275
 *  5    360Commerce 1.4         1/10/2008 7:44:50 AM   Manas Sahu      Event
 *       Originator changes
 *  4    360Commerce 1.3         1/7/2008 7:59:16 AM    Chengegowda Venkatesh
 *       Audit log changes
 *  3    360Commerce 1.2         3/31/2005 4:27:47 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse   
 * $
 * Revision 1.9.2.1  2005/01/17 15:41:51  rsachdeva
 * @scr 7723 Cashier Name set in Status Model instance already there
 *
 * Revision 1.9  2004/09/23 00:07:17  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.8  2004/03/26 21:18:20  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.7  2004/03/26 15:56:29  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.6  2004/03/24 23:23:55  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.5  2004/03/21 17:40:37  bjosserand
 * @scr 4093 Transaction Reentry
 *
 *
 * Revision 1.4  2004/03/21 16:34:29  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.3 2004/02/12 16:49:34 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:48:14 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:15 cschellenger updating to pvcs 360store-current
 *
 * Rev 1.0 Aug 29 2003 15:56:10 CSchellenger Initial revision.
 *
 * Rev 1.0 Apr 29 2002 15:25:04 msg Initial revision.
 *
 * Rev 1.1 Mar 18 2002 23:13:38 msg - updated copyright
 *
 * Rev 1.0 Mar 18 2002 11:26:02 msg Initial revision.
 *
 * Rev 1.1 05 Feb 2002 10:02:36 epd cashier name now displays Resolution for POS SCR-975: User name does not display in
 * status region of DO after fresh start of appl.
 *
 * Rev 1.0 Sep 21 2001 11:16:10 msg Initial revision.
 *
 * Rev 1.1 Sep 17 2001 13:07:16 msg header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations;

import oracle.retail.stores.commerceservices.audit.AuditLoggerConstants;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.UserEvent;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.StoreStatusCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * This site displays the options available from the Daily Ops screen.
 * 
 * @version $Revision: /main/16 $
 */
public class DisplayDailyOpsScreenSite extends PosSiteActionAdapter implements SiteActionIfc
{
    static final long serialVersionUID = 1214949520330615003L;

    public static final String SITENAME = "DisplayDailyOpsScreenSite";

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/16 $";

    /**
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        StoreStatusCargo cargo = (StoreStatusCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();

        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCashierName(cargo.getOperator().getPersonName().getFirstLastName());
        statusModel.setRegister(cargo.getRegister());
        
        boolean trainingModeOn = false;
        if (cargo.getRegister() != null)
        {
          trainingModeOn = cargo.getRegister().getWorkstation().isTrainingMode();
        }
        statusModel.setStatus(POSUIManagerIfc.TRAINING_MODE_STATUS, trainingModeOn);
        baseModel.setStatusBeanModel(statusModel);

        ui.showScreen(POSUIManagerIfc.DAILY_OPS_OPTIONS, baseModel);
    }

    /**
     * @param bus the bus departing from this site
     */
    @Override
    public void depart(BusIfc bus)
    {

        LetterIfc letter = bus.getCurrentLetter();
        if (letter instanceof ButtonPressedLetter)
        {
            String letterName = letter.getName();
            StoreStatusCargo cargo = (StoreStatusCargo) bus.getCargo();
            if (letterName != null && letterName.equals(CommonLetterIfc.UNDO))
            {
                // Audit Logging UserEvent for user logout
                AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();

                UserEvent ev = (UserEvent) AuditLoggingUtils.createLogEvent(UserEvent.class, AuditLogEventEnum.LOG_OUT);
                RegisterIfc ri = cargo.getRegister();
                if (ri != null)
                {
                    WorkstationIfc wi = ri.getWorkstation();
                    if (wi != null)
                    {
                        ev.setRegisterNumber(wi.getWorkstationID());
                    }
                }
                ev.setStoreId(cargo.getOperator().getStoreID());
                ev.setUserId(cargo.getOperator().getLoginID());
                ev.setStatus(AuditLoggerConstants.SUCCESS);
                ev.setEventOriginator("DisplayDailyOpsScreenSite.depart");
                auditService.logStatusSuccess(ev);
            }
        }

    }

    // --------------------------------------------------------------------------
    /**
     * @param bus the bus undoing its actions
     */
    // --------------------------------------------------------------------------
    public void undo(BusIfc bus)
    {
    }

    // --------------------------------------------------------------------------
    /**
     * @param bus the bus being reset
     */
    // --------------------------------------------------------------------------
    public void reset(BusIfc bus)
    {
    }
}
