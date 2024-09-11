/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/signaturecapture/CaptureSignatureSite.java /main/17 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   04/25/12 - implement locking mechanism for concurrent users
 *                         using a single CPOI device.
 *    icole     03/06/12 - Refactor to remove CPOIPaymentUtility and attempt to
 *                         have more generic code, rather than heavily Pincomm.
 *    jswan     08/24/11 - Added the store ID to the workstation in the request
 *                         object to remedy a journal key issue.
 *    blarsen   06/30/11 - replaced SIG_CAP with FINANCIAL_NETWORK_STATUS. Sig
 *                         cap status was removed as part of advance payment
 *                         foundation feature.
 *    jswan     06/22/11 - Modified for APF.
 *    blarsen   06/28/11 - Renamed CustomerInteractionRequest.RequestType to
 *                         RequestSubType.
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    icole     06/09/11 - Updates required due to changing
 *                         CustomerInteractionRequest
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    acadar    06/08/10 - changes for signature capture, disable txn send, and
 *                         discounts
 *    acadar    06/02/10 - refactoring
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   03/24/09 - set the POSDeviceActions in the cargo before calling
 *                         beginSignatureCapture. This affects the logic in the
 *                         catch(DeviceException).
 *    npoola    02/11/09 - fix offline issue for device
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:49 PM  Robert Pearse
 *
 *   Revision 1.15  2004/09/29 16:33:03  rsachdeva
 *   @scr 4951 DeviceException when thrown checks Simulated at start
 *
 *   Revision 1.14  2004/08/19 17:33:40  kll
 *   @scr 6816: do not change status if SigCap device is simulated
 *
 *   Revision 1.13  2004/08/05 15:09:49  bwf
 *   @scr 3574 Moved ui display beneath sig cap start.  This way if exception
 *                     we will not show screen.
 *
 *   Revision 1.12  2004/08/02 20:03:37  rzurga
 *   @scr 3676 Ingenico does not update balance due if split tender is performed
 *   Back away from the change where the sigcap start is executed from within IngenicoSigCap
 *   as it precluded simulator sigcap and also disabled detecting whether the sigcap device is
 *   configured and online.
 *
 *   Revision 1.11  2004/07/21 20:01:20  rzurga
 *   @scr 6395 Ingenico signature capture not working
 *   Small cleanup
 *
 *   Revision 1.10  2004/07/21 19:48:20  rzurga
 *   @scr 6395 Ingenico signature capture not working
 *
 *   Moved start of signature capture to IngenicoSigCap module
 *
 *   Revision 1.9  2004/07/20 23:28:14  rzurga
 *   @scr 3676 Ingenico does not update balance due if split tender is performed
 *
 *   Change of the actions order regarding the signature capture. To make sure
 *   everything is executed properly.
 *
 *   Revision 1.8  2004/05/07 20:52:44  rzurga
 *   @scr 4720 Add amount tendered to CPOI sigcap screen [code review]
 *
 *   Revision 1.7  2004/05/04 01:58:34  rzurga
 *   @scr 4672 Add amount tendered to CPOI sigcap screen
 *
 *   Revision 1.6  2004/04/05 15:47:54  jdeleau
 *   @scr 4090 Code review comments incorporated into the codebase
 *
 *   Revision 1.5  2004/03/25 20:25:15  jdeleau
 *   @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 *   See the scr for more info.
 *
 *   Revision 1.4  2004/03/03 23:15:12  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Jan 06 2004 16:07:56   rsachdeva
 * Status changed for DeviceException
 * Resolution for POS SCR-3542: Status of Signature Capture remains Online on Device Status even when Signature Capture is disabled
 *
 *    Rev 1.2   Oct 22 2003 10:44:20   rsachdeva
 * SIGNATURE_CAPTURE_STATUS
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 *
 *    Rev 1.1   Sep 03 2003 15:46:12   RSachdeva
 * Add CIDScreen support
 * Resolution for POS SCR-3355: Add CIDScreen support
 *
 *    Rev 1.0   Aug 29 2003 16:07:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:02:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:47:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 17 2002 16:00:10   mpm
 * Added code to check for simulated device if no-device exception not thrown from simulated device.
 *
 *    Rev 1.0   Sep 21 2001 11:25:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.signaturecapture;

// Foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.payment.SignatureCaptureRequestIfc;
import oracle.retail.stores.domain.manager.payment.SignatureCaptureResponseIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 *
 *  Attempt to capture customer's signature if a device is configured.
 *
**/
public class CaptureSignatureSite extends PosSiteActionAdapter
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -6374020570165720199L;

    /**
     * If a signature capture device is configured and there are approved credit
     * tenders, capture signatures.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter letter = null;
        String sigCapEnabled = Gateway.getProperty("application", "SignatureCaptureEnabled", "true");
        if (Boolean.valueOf(sigCapEnabled).booleanValue())
        {
            letter = performSignatureCapture(bus);
        }
        else
        {
            // If the getSignature method is not supported by the payment
            // service, just mail "Success" and the signature line will
            // be printed on the receipt.
            letter = new Letter(CommonLetterIfc.SUCCESS);
        }
        bus.mail(letter,BusIfc.CURRENT);
    }

    /*
     * Request an electronic signature from the customer
     * 
     * @param bus the bus arriving at this site
     * @return letter
     */
    protected Letter performSignatureCapture(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.FAILURE);
        SignatureCaptureCargo cargo = (SignatureCaptureCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        //default signature capture status
        ui.statusChanged(POSUIManagerIfc.FINANCIAL_NETWORK_STATUS,
                         POSUIManagerIfc.ONLINE);
        // get access to payment manager
        PaymentManagerIfc paymentManager = (PaymentManagerIfc)Gateway.getDispatcher().getManager(PaymentManagerIfc.TYPE);

        //if transaction has an external order and signature capture is required
        String labelText = "";
        if(cargo.getLegalDocument() != null)
        {
            try
            {
                ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
                labelText = pm.getStringValue("LegalExtOrderSigCap") +
                                    " " + cargo.getLegalDocument().getExternalOrderNumber();
            }
            catch (ParameterException pe)
            {
                logger.warn("Parameter LegalExtOrderSigCap could not be read. Default message will be displayed", pe);
            }
        }

        ui.showScreen(POSUIManagerIfc.SIGNATURE_CAPTURE, new POSBaseBeanModel());

        // Stop the scrolling receipt.
        WorkstationIfc workstation = cargo.getRegister().getWorkstation();
        paymentManager.endScrollingReceipt(workstation);

        try
        {
            // send the request and get the response
            SignatureCaptureRequestIfc request = DomainGateway.getFactory().getSignatureCaptureRequestInstance();
            request.setWorkstation(workstation);
    
            request.setLabelText(labelText);
            SignatureCaptureResponseIfc response = paymentManager.getSignature(request);
            
            cargo.setSignature(response.getSignature());

            if(cargo.getLegalDocument() != null)
            {
                // store the captured signature in the legal document
                cargo.getLegalDocument().setSignature(response.getSignature());
            }

            //test retrieved values and determine what letter to send
            if (response.getSignature() == null)
            {
                //capture failed so exit service with a failure letter
                letter = new Letter(CommonLetterIfc.FAILURE);
            }
            else if (cargo.verifySignature())
            {
                //verification requested so go to VerifySignatureSite
                letter = new Letter("Verify");
            }
            else
            {
                //verification not requested so exit the service with Success letter
                letter = new Letter(CommonLetterIfc.SUCCESS);
            }
        }
        catch (DeviceException de)
        {
            logger.warn(
                    "Error while using signature capture device: " + de.getMessage() + "");
            ui.statusChanged(POSUIManagerIfc.FINANCIAL_NETWORK_STATUS,
                    POSUIManagerIfc.OFFLINE);
            letter = new Letter(CommonLetterIfc.FAILURE);
        }
        
        return letter;
    }
}
