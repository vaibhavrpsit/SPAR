/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/ValidateTransactionIDAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/11/10 - Pre code reveiw clean up.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;


import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
//--------------------------------------------------------------------------
/**
 * This aisle is traveled when the transaction ID has been entered.
 */
//--------------------------------------------------------------------------
public class ValidateTransactionIDAisle extends PosLaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -1780029381051839424L;
    
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * Maximum number of characters in a receiptid field
     */
    public static final int MAX_RECIEPT_LENGTH = 14;
    /**
     * Constant for error screen
     */
    public static final String INVALID_RETURN_NUMBER = "InvalidNumberError";

    /**
     * purchase date field
     */
    public static final String PURCHASE_DATE_FIELD = "purchaseDateField";

    /**
     *  store number
     */
    public static final String STORE_NUMBER_FIELD = "storeNumberField";

    /**
     * register number
     */
    public static final String REGISTER_NUMBER_FIELD = "registerNumberField";

    /**
     * transaction number
     */
    public static final String TRANS_NUMBER_FIELD = "transactionNumberField";

    /**
     * Number type receipt bundle tag.
     */
    public static final String NUMBER_TYPE_RECEIPT_TAG = "NumberTypeReceipt";

    /**
     * Receipt bundle tag.
     */
    public static final String NUMBER_TYPE_RECEIPT_TEXT = "receipt";

    //----------------------------------------------------------------------
    /**
     * Validates and stores the transaction ID in the cargo.
     * <P>
     *
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        boolean validTransactionID = true;
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        String receiptId = ui.getInput();

        // Validate the transactionID length and contents
        if (receiptId.length() == TransactionID.getTransactionIDLength())
        {
            TransactionIDIfc receiptNo = DomainGateway.getFactory().getTransactionIDInstance();
            receiptNo.setTransactionID(receiptId);
            
            if(receiptNo.getSequenceNumber()==0 && 
               receiptNo.getStoreID().equals("")&&
               receiptNo.getWorkstationID().equals(""))
            {
                validTransactionID = false;
            }
            else
            {
                if (bus.getCargo() instanceof ReturnOptionsCargo)
                {
                    ((ReturnOptionsCargo)bus.getCargo()).setOriginalTransactionId(receiptNo);
                }
                if (bus.getCargo() instanceof ReturnFindTransCargo)
                {
                    ((ReturnFindTransCargo)bus.getCargo()).setOriginalTransactionId(receiptNo);
                }
                bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }
        }
        else
        {
            validTransactionID = false;
        }
        
        // If the transaction ID is not valid, display the error dialog.
        if (!validTransactionID)
        {
            UtilityManagerIfc utility =
                (UtilityManagerIfc) Gateway.getDispatcher().getManager(
                    UtilityManagerIfc.TYPE);
            String numberTypeText =
                utility.retrieveText(
                    POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                    BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                    NUMBER_TYPE_RECEIPT_TAG,
                    NUMBER_TYPE_RECEIPT_TEXT);
        	 String args[] = new String[2];
             args[0] = numberTypeText;
             args[1] = numberTypeText;

             // Using "generic dialog bean".
             DialogBeanModel dialogModel = new DialogBeanModel();
             dialogModel.setResourceID(INVALID_RETURN_NUMBER);
             dialogModel.setType(DialogScreensIfc.ERROR);
             dialogModel.setArgs(args);

             // set and display the model
             ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }
}
