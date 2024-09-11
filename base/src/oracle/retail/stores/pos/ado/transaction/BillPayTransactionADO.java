/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/26/11 - check for missing tokens when voiding transactions
 *    rsnayak   09/05/11 - Added House Account to tender options
 *    blarsen   09/29/10 - Post void BillPay trans with credit tender, system
 *                         doesn't sent credit auth to ISD and no sig-cap at
 *                         CPOI
 *    nkgautam  06/23/10 - bill pay changes
 *    nkgautam  06/21/10 - Bill Pay Transaction ADO Class creation
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.BillPayTransaction;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;

public class BillPayTransactionADO extends AbstractRetailTransactionADO
    implements RetailTransactionADOIfc  
{
    /**vGenerated Serial UID */
    private static final long serialVersionUID = -725269482878565871L;

    /**
     * Balance Due
     */
    protected CurrencyIfc balanceDue;

    /**
     * Creates an instance of Bill Pay Transaction
     */
    protected TransactionIfc instantiateTransactionRDO()
    {
        // Create the RDO transaction
        transactionRDO = DomainGateway.getFactory().getBillPayTransactionInstance();
        return transactionRDO;
    }

    /**
     * The given RDO is used to populate the current ADO with state currently
     * contained within the given RDO.
     * 
     * @param rdo The object from which the current ADO will be populated.
     */
    public void fromLegacy(EYSDomainIfc rdo) 
    {
        transactionRDO = (BillPayTransaction) rdo;
        
        // qc 813
        Iterator<TenderLineItemIfc> iter = ((TenderableTransactionIfc) transactionRDO).getTenderLineItemsVector().iterator();
        while (iter.hasNext())
        {
            TenderLineItemIfc tenderRDO = iter.next();
            TenderTypeEnum type = TenderTypeEnum.makeEnumFromString(tenderRDO.getTypeDescriptorString());
            TenderFactoryIfc factory;
            try
            {
                factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
                TenderADOIfc tenderADO;
                if (type != null)
                {
                    tenderADO = factory.createTender(type);
                }
                else
                {
                    tenderADO = factory.createTender(tenderRDO);
                }
                ((ADO) tenderADO).fromLegacy(tenderRDO);
                addTenderNoValidation(tenderADO);
            }
            catch (ADOException e)
            {
                logger.error("ADOException caught in BillPayTransactionADO.fromLegacy()", e);
                throw new RuntimeException("ADOException caught in BillPayTransactionADO.fromLegacy()", e);
            }
        }
    }

    /**
     * Converts this ADO to an RDO equivalent object for use in parts of the
     * application which are not yet equipped to use ADO class objects.
     * 
     * @return An RDO.
     */
    public EYSDomainIfc toLegacy()
    {
        return transactionRDO;
    }

    /**
     * If an ADO is composed of multiple RDO types, it may make sense to request
     * the particular type that we need.
     * 
     * @param type The RDO object corresponding to the desired type.
     * @return an RDO.
     */
    @SuppressWarnings("rawtypes")
    public EYSDomainIfc toLegacy(Class type) 
    {
        return toLegacy();
    }

    /**
     * Returns whether a transaction is voidable or not.
     */
    public boolean isVoidable(String currentTillID) throws VoidException
    {
        // check if authorized tenders are not voidable due to missing account ID token
        voidCheckAuthorizedTenderMissingToken();

        // 1) Make sure we have the same Till ID
        voidCheckForSameTill(currentTillID);

        // 2) Transaction should not already be voided
        voidCheckForPreviousVoid();

        // 3) Make sure any issued tenders have not been used.
        voidCheckForIssuedTenderModifications();

        // 4) Check that void is allowed for tranasactions
        //    containing debit tenders
        voidCheckDebitAllowed();

        // 5) Make sure the transaction is not suspended
        voidCheckForSuspendedTransaction();

        return true;
    }

    /**
     * Gets the enabled Tender Options based on the parameter
     */
    public TenderTypeEnum[] getEnabledTenderOptions()
    {
        UtilityIfc util = getUtility();
        String[] options = util.getParameterValueList("BillPayTransactionTenders");
        List<TenderTypeEnum> tenderList = new ArrayList<TenderTypeEnum>(16);
        for (int i= 0; i< options.length; i++)
        {
            if(util.isStringListed("Cash", options))
            {
                tenderList.add(TenderTypeEnum.CASH);
            }
            if(util.isStringListed("Debit", options))
            {
                tenderList.add(TenderTypeEnum.DEBIT);
            }
            if(util.isStringListed("Credit", options))
            {
                tenderList.add(TenderTypeEnum.CREDIT);
            }
            if(util.isStringListed("Check", options))
            {
                tenderList.add(TenderTypeEnum.CHECK);
            }
            if(util.isStringListed("TravelCk", options))
            {
                tenderList.add(TenderTypeEnum.TRAVELERS_CHECK);
            }
            if(util.isStringListed("GiftCert", options))
            {
                tenderList.add(TenderTypeEnum.GIFT_CERT);
            }
            if(util.isStringListed("MailCheck", options))
            {
                tenderList.add(TenderTypeEnum.MAIL_CHECK);
            }
            if(util.isStringListed("Debit", options))
            {
                tenderList.add(TenderTypeEnum.DEBIT);
            }
            if(util.isStringListed("Coupon", options))
            {
                tenderList.add(TenderTypeEnum.COUPON);
            }
            if(util.isStringListed("GiftCard", options))
            {
                tenderList.add(TenderTypeEnum.GIFT_CARD);
            }
            if(util.isStringListed("StoreCr", options))
            {
                tenderList.add(TenderTypeEnum.STORE_CREDIT);
            }
            if(util.isStringListed("MallCert", options))
            {
                tenderList.add(TenderTypeEnum.MALL_CERT);
            }
            if(util.isStringListed("P.O.", options))
            {
                tenderList.add(TenderTypeEnum.PURCHASE_ORDER);
            }
            if(util.isStringListed("MoneyOrder", options))
            {
                tenderList.add(TenderTypeEnum.MONEY_ORDER);
            }
            if(util.isStringListed("InstantCredit", options))
            {
                tenderList.add(TenderTypeEnum.HOUSE_ACCOUNT);
            } 
        }

        // convert list to array
        TenderTypeEnum[] tenderTypeArray = new TenderTypeEnum[tenderList.size()];
        tenderTypeArray = tenderList.toArray(tenderTypeArray);
        return tenderTypeArray;
    }

    /**
     * Save this transaction to persistent storage
     * @param registerADO Information from the register is required by legacy
     *                       persistence machanism.
     */
    public void save(RegisterADO registerADO) throws DataException
    {
        // TODO Auto-generated method stub
    }
}
