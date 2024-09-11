/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/ManualApprovalRoad.java /rgbustores_13.4x_generic_branch/4 2011/11/03 17:08:30 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/06/11 - save encrypted and masked account numbers, use entry
 *                         method from request.
 *    ohorne    08/09/11 - APF:foreign currency support
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.activation;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc.AuthorizationMethod;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

import org.apache.commons.codec.binary.Base64;

/**
 * This road sets manual approval values on the response.
 * 
 * @author jswan
 * @since 13.4
 */
@SuppressWarnings("serial")
public class ManualApprovalRoad extends PosLaneActionAdapter
{
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ActivationCargo cargo = (ActivationCargo)bus.getCargo();
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();
        AuthorizeTransferRequestIfc request = cargo.getCurrentRequest();
        response.setResponseCode(ResponseCode.Approved);
        response.setCurrentBalance(request.getBaseAmount());
        response.setBaseAmount(request.getBaseAmount());
        response.setStatus(StatusCode.Unknown);
        response.setAuthorizationMethod(AuthorizationMethod.Manual);
        response.setTenderType(TenderType.GIFT_CARD);
        response.setEntryMethod(request.getEntryMethod());
        response.setFinancialNetworkStatus(AuthorizationConstantsIfc.BANK_OFFLINE);
        // save the encrypted account number
        String encryptedAccountNumber = request.getAccountNumber();
        response.setAccountNumber(encryptedAccountNumber);
        KeyStoreEncryptionManagerIfc encryptionManager = (KeyStoreEncryptionManagerIfc)Gateway.getDispatcher().getManager(KeyStoreEncryptionManagerIfc.TYPE);
        byte[] clearAccountNumber = null;
        try
        {
            // get the masked account number and save it.
            clearAccountNumber = encryptionManager.decrypt(Base64.decodeBase64(encryptedAccountNumber.getBytes()));
            EncipheredCardDataIfc cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(clearAccountNumber);
            response.setMaskedAccountNumber(cardData.getMaskedAcctNumber());
        }
        catch(EncryptionServiceException ese)
        {
            logger.error("Could not decrypt account number", ese);
        }
        finally
        {
            Util.flushByteArray(clearAccountNumber);
        }
    }
}
