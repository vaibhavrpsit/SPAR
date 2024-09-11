/* =============================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/SendFinalResultAisle.java /main/7 2014/02/27 10:24:27 kavdurai Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    kavdurai  02/26/14 - Modified SendFinalResult message
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    rkar      12/08/08 - (1) Changes for i18N (2) Removed TODOs, reformat.
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.manager.rm.RPIFinalResultIfc;
import oracle.retail.stores.domain.manager.rm.RPIManagerIfc;
import oracle.retail.stores.domain.manager.rm.RPIRequestIfc;
import oracle.retail.stores.domain.manager.rm.RPIResponseIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.utility.RMUtility;

/**
 *  Creates RPIFinalResult from Cago data, and sends it to RPIManager.
 */
public class SendFinalResultAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -1938647238737064013L;

    /**
     * @see SendFinalResultAisle
     * @param bus the bus arriving at this site
     */
    public void traverse(BusIfc bus)
    { 
        String letter = "ExitTender";
        boolean isVoid = false;
        boolean isOffline = false;

        TenderCargo cargo = (TenderCargo)bus.getCargo();
        RPIResponseIfc  rpiResponse = cargo.getReturnResponse();
        RPIRequestIfc   rpiRequest  = cargo.getReturnRequest();

        if ( bus.getCurrentLetter().getName().equals(CommonLetterIfc.UNDO) ||
                bus.getCurrentLetter().getName().equals(CommonLetterIfc.CANCEL) )
        {
               isVoid = true;
        }

        RPIFinalResultIfc rpiFinalResult = cargo.getReturnResult();
        if ( rpiFinalResult == null )
        {
            if ( rpiResponse != null )
            {
                rpiFinalResult = RMUtility.getInstance().getFinalResult(
                                                        cargo.getTransaction(), 
                                                        rpiRequest, 
                                                        rpiResponse,
                                                        isVoid);
            }
            else if ( rpiRequest != null )
            {
                // have request but no response, it means the manager is not
                // able to get response, execute offline flow.
                rpiFinalResult = RMUtility.getInstance().getOfflineFinalResult(
                                                        cargo.getTransaction(), 
                                                        rpiRequest,
                                                        isVoid);
                isOffline = true;
            }

            if ( rpiFinalResult != null )
            {
                try {
                    RPIManagerIfc rpiReturnsManager =
                        (RPIManagerIfc)Gateway.getDispatcher().getManager(RPIManagerIfc.TYPE);

                    rpiReturnsManager.sendReturnsFinalResult(rpiFinalResult);

                    String ticketID = rpiFinalResult.getTicketID();
                    cargo.getTransaction().setReturnTicket(ticketID);

                    JournalManagerIfc journal;
                    journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
                    Object[] messageArgs = {"Online"};
                    if(isOffline){
                        messageArgs[0] = "Offline";
                    }
                        
                    journal.journal( I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, 
                            JournalConstantsIfc.SEND_RETURN_RESULT, messageArgs) );
                    if ( !Util.isEmpty(ticketID) )
                    {
                        messageArgs[0] = ticketID;
                        journal.journal( I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, 
                                JournalConstantsIfc.RETURN_TICKET_ID, messageArgs) );
                    }
                }
                catch (Exception e) {
                    logger.error("Not able to send Return Result: ", e);
                }
            }
        }

        // Clean the return data from the cargo
        cargo.setReturnRequest(null);
        cargo.setReturnResponse(null);
        cargo.setReturnResult(null);

        bus.mail(new Letter(letter), BusIfc.CURRENT); 
    }
}
