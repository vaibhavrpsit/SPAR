/* ===========================================================================
* Copyright (c) 2003, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/reference/SaveTransactionSite.java /main/13 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    sgu       05/11/11 - define approval status for instant credit as enum
 *                         type
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         6/9/2006 3:22:03 PM    Brett J. Larsen CR 18490
 *       - UDM - instant credit auth code changed to varchar from int
 * 3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:25:03 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse
 *
 *Revision 1.3  2004/02/12 16:50:45  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Dec 24 2003 09:34:56   nrao
 * Added authorization response to the transaction before writing to the database.
 *
 *    Rev 1.3   Nov 25 2003 17:35:02   nrao
 * Added check for null transaction.
 *
 *    Rev 1.2   Nov 24 2003 20:00:50   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.reference;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @version $Revision: /main/13 $
 */
public class SaveTransactionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -6978412027977692969L;

    /**
     * If transaction is not saved, save it
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        if (!cargo.isTransactionSaved())
        {
            save(bus);
        }
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

    /**
     * Save the transaction to the database.
     *
     * @param bus the bus arriving at this site
     */
    public static void save(BusIfc bus)
    {
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();

        try
        {
            InstantCreditTransactionIfc instantCreditTransaction = (InstantCreditTransactionIfc) cargo.getTransaction();
            TillIfc till = cargo.getRegister().getCurrentTill();

            if (instantCreditTransaction != null)
            {
                // if instant Credit object in the transaction is null, create a new one
                if(instantCreditTransaction.getInstantCredit() == null)
                {
                    instantCreditTransaction.setInstantCredit(DomainGateway.getFactory()
                                                                .getInstantCreditInstance());
                }
                // set the approval code with the authorization response
                if (cargo.getInstantCreditResponse() != null)
                {
                    instantCreditTransaction.getInstantCredit()
                                            .setApprovalStatus(cargo.getInstantCreditResponse().getApprovalStatus());
                }

                // save the transaction
                utility.saveTransaction(instantCreditTransaction, till, cargo.getRegister());
            }

            utility.writeHardTotals();
            cargo.setTransactionSaved(true);
         }
        catch (DataException de)
        {
        	logger.error(de);
        	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            DialogBeanModel dialogModel = util.createErrorDialogBeanModel(de);
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }
}