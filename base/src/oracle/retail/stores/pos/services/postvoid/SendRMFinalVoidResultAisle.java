/* =============================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/SendRMFinalVoidResultAisle.java /main/6 2012/09/12 11:57:10 blarsen Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    rkar      12/08/08 - (1) Changes for i18N (2) Removed TODOs, reformat.
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.manager.rm.RPIFinalResultIfc;
import oracle.retail.stores.domain.manager.rm.RPIManagerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.utility.RMUtility;

/**
 * To send ReturnResult to RM, if return transaction is post-voided. 
 */
public class SendRMFinalVoidResultAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 6527888585456682415L;

    /**
     * If "Use ORRM" parameter is set, and not in training mode, creates a sale-return
     * transaction, and sends a void Return Result to RPIManager.
     *
     * @param bus the bus arriving at this site
     */
    public void traverse(BusIfc bus)
    { 
        VoidCargo cargo = (VoidCargo)bus.getCargo();
        RetailTransactionADOIfc txnADO = cargo.getOriginalTransactionADO();
        EYSDomainIfc domain = txnADO.toLegacy();
        if ( domain instanceof SaleReturnTransactionIfc )
        {
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);           
            try
            {
                Boolean manageReturns  = pm.getBooleanValue("UseOracleRetailReturnManagement");
                if (manageReturns.booleanValue() == true)
                {
                    boolean trainingModeOn = cargo.getRegister().getWorkstation().isTrainingMode();
                    if ( !trainingModeOn )
                    {
                        SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc)domain;
                        String returnTicketID = transaction.getReturnTicket();
                        if ( !Util.isEmpty(returnTicketID) )
                        {
                            RPIFinalResultIfc rpiFinalResult = 
                                 RMUtility.getInstance().getFinalResult(
                                           transaction,
                                           null, //request
                                           null, //response
                                           true); //post void         
                            try 
                            {
                                RPIManagerIfc rpiReturnsManager =
                                    (RPIManagerIfc)Gateway.getDispatcher().getManager(RPIManagerIfc.TYPE);                

                                rpiReturnsManager.sendReturnsFinalResult(rpiFinalResult);

                                JournalManagerIfc journal;
                                journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
                                Object[] messageArgs = {returnTicketID};
                                journal.journal( I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, 
                                        JournalConstantsIfc.SEND_VOID_RETURN_RESULT, messageArgs) );
                            }
                            catch (Exception e)
                            {
                                logger.error("Exception sending void Return Result", e);
                            }
                        }
                    }
                }
            }
            catch (ParameterException e)
            {
                logger.error(e.getMessage());
            }   
        }
        bus.mail(CommonLetterIfc.NEXT, BusIfc.CURRENT); 
    }
}
