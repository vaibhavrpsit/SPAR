/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogRedeemTransaction.java /rgbustores_13.4x_generic_branch/2 2011/07/18 16:21:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/18/11 - remove hashed number column from gift card tables
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         4/22/2008 6:16:48 PM   Sharma Yanamandra
 *         Added new Aisle, modified redeem tour and modified
 *         LogRedeemTransaction
 *    8    360Commerce 1.7         4/19/2008 2:48:12 PM   Michael P. Barnett In
 *          createBaseElements, set the encrypted and hashed gift card account
 *          number values.
 *    7    360Commerce 1.6         6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
 *         changes to export and import POSLog.
 *    6    360Commerce 1.5         4/25/2007 10:00:44 AM  Anda D. Cadar   I18N
 *         merge
 *    5    360Commerce 1.4         7/25/2006 7:53:04 PM   Charles D. Baker
 *         Updated to handle DB update breaking single address line column
 *         into two. Corrected other special handling as mail bank check
 *         shares Customer object with Capture Cutomer.
 *    4    360Commerce 1.3         2/8/2006 10:32:19 AM   Jason L. DeLeau 5373:
 *          Make sure a pos log is generated for the post void of a redeemed
 *         gift card.
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/08/10 07:17:09  mwright
 *   Merge (3) with top of tree
 *
 *   Revision 1.1.2.3  2004/08/09 12:37:48  mwright
 *   Export the (apparently) constant redemtion state
 *
 *   Revision 1.1.2.2  2004/08/01 23:46:12  mwright
 *   Removed TO-DO tags on completed tasks
 *
 *   Revision 1.1.2.1  2004/07/29 01:21:10  mwright
 *   Initial revision
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.Redemption360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RedemptionTenderLineItem360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionCustomer360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionLineItemIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.customer.LogCustomerIfc;
import oracle.retail.stores.domain.ixretail.tender.LogTenderLineItemIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogRedeemTransactionIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

/**
 * This class creates the TLog in IXRetail format for a redeem transaction.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class LogRedeemTransaction extends LogTenderControlTransaction implements LogRedeemTransactionIfc
{
    /**
     * revision number supplied by source-code-control system
     **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.ixretail.transaction.v21.LogTenderControlTransaction#createBaseElements()
     */
    @Override
    protected void createBaseElements() throws XMLConversionException
    {
        super.createBaseElements();

        RedeemTransactionIfc redeemTrans  = (RedeemTransactionIfc)transaction;
        Redemption360Ifc     redemption   = getSchemaTypesFactory().getRedemption360Instance();
        TenderLineItemIfc    redeemTender = redeemTrans.getRedeemTender();
        // redeemTender would be null if redeem is canceled 
        // before capturing customer information 
        // so it should be checked for not being null 
        if (redeemTender != null) 
        {
        	redemption.setTenderType(Integer.toString(redeemTender.getTypeCode()));
        	redemption.setRedemptionID(redeemTrans.getRedeemID());
        	redemption.setCurrencyID(redeemTrans.getCurrencyID()); //I18N
        
        	redemption.setRedemptionState("Redeemed");      // this appears to be hardcoded

        	// add the captured customer data: 
        	LogCustomerIfc customerLogger = IXRetailGateway.getFactory().getLogCustomerInstance();
        	RetailTransactionCustomer360Ifc customerElement = getSchemaTypesFactory().getRetailTransactionCustomerInstance();
        	if(redeemTrans.getCaptureCustomer() != null)
        	{
        		customerLogger.createElement(redeemTrans.getCaptureCustomer(), null, customerElement);
        		redemption.setCustomer(customerElement);
        	}

        	CurrencyIfc redeemAmount = null;
        	if (redeemTender instanceof TenderGiftCardIfc)
        	{
        		TenderGiftCardIfc giftCard = (TenderGiftCardIfc)redeemTender;
        		redemption.setRedemptionID(giftCard.getEncipheredCardData().getMaskedAcctNumber());
        		redemption.setEncryptedGiftCardAcctNumber(giftCard.getEncipheredCardData().getEncryptedAcctNumber());
        		redeemAmount = giftCard.getAmountTender();
        	}
        	else if (redeemTender instanceof TenderGiftCertificateIfc)
        	{
        		TenderGiftCertificateIfc giftCert = (TenderGiftCertificateIfc)redeemTender;
        		redeemAmount = giftCert.getAmountTender();
        		redemption.setFaceValue(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(giftCert.getFaceValueAmount())));
        		redemption.setGiftCertificateStatus(giftCert.getState());
            
        	}
        	else if (redeemTender instanceof TenderStoreCreditIfc)
        	{
        		TenderStoreCreditIfc storeCredit = (TenderStoreCreditIfc)redeemTender;
        		redeemAmount = storeCredit.getAmount();
        		redemption.setStoreCreditStatus(storeCredit.getState());
        	}

        	//redemption.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(redeemTender.getAmountTender())));
        	redemption.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(redeemAmount)));
        
        }
        
        LogTenderLineItemIfc tenderLogger = IXRetailGateway.getFactory().getLogTenderLineItemInstance();
        
        int lineItemSequenceNumber = 0;
        for (TenderLineItemIfc tender : redeemTrans.getTenderLineItemsVector())
        {
            RetailTransactionLineItemIfc lineItemElement = getSchemaTypesFactory().getRetailTransactionLineItemInstance();
            tenderLogger.createElement(tender,
                                       null,
                                       lineItemElement,
                                       lineItemSequenceNumber++);

            // add line item element to redeem element by creating a redeem line item and copying the tender element etc over
            RedemptionTenderLineItem360Ifc redemptionLine = getSchemaTypesFactory().getRedemptionTenderLineItem360Instance();
            redemptionLine.setSequenceNumber(lineItemElement.getSequenceNumber());
            redemptionLine.setTender(lineItemElement.getTender());
            redemptionLine.setEntryMethod(lineItemElement.getEntryMethod());
            redemption.addLineItem(redemptionLine);
        }
        
        tenderControlTransactionElement.setRedemption360(redemption);
        
    }    

}
