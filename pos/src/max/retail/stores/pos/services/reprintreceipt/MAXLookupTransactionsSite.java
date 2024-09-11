/********************************************************************************
 *   
 *	Copyright (c) 2018 - 2019 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 * 	Rev	1.1 	Aug 26, 2019		Karni		Changes for Easy Buy Totals	
 *	Rev	1.0 	Aug 16, 2016		Ashish		Changes for Food Totals	
 *
 *
 ********************************************************************************/

package max.retail.stores.pos.services.reprintreceipt;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Locale;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.stock.MerchandiseClassification;
import oracle.retail.stores.domain.stock.MerchandiseClassificationIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.reprintreceipt.ReprintReceiptCargo;

import org.apache.commons.lang3.StringUtils;

/**
 * This site checks to see that the transaction exists, was created on the same
 * day and at the same register and is reprintable.
 */
@SuppressWarnings("serial")
public class MAXLookupTransactionsSite extends PosSiteActionAdapter
{
    /**
     * Retrieves the transaction and confirms that the transaction may be
     * reprinted.
     *
     * @param bus
     *            Service Bus
     */
	protected BigDecimal foodTotals = null;
	protected BigDecimal nonFoodTotals = null;
	private String FOOD_CATEFORY = "Yes";
	
	protected BigDecimal easyBuyTotals = null;
	private String EASYBUY_CATEFORY = "EASY BUY";
	
    @Override
    public void arrive(BusIfc bus)
    {
        ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();

        //get utility manager
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // get the customer manager
        CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);

        // Lookup the transaction ID stored in the cargo.
        TransactionIfc transaction = null;
        String transactionID = cargo.getTransactionID();
        EYSDate today = cargo.getBusinessDate();

        TransactionIfc searchTransaction = null;

        TransactionReadDataTransaction trans = null;

        trans = (TransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);

        String letterName = null;
        boolean notFound = true;

        try
        {
            searchTransaction =
                DomainGateway.getFactory().getTransactionInstance();
            searchTransaction.initialize(transactionID);
            if (searchTransaction.getBusinessDay() == null)
            {
                searchTransaction.setBusinessDay(today);
            }
            searchTransaction.setTrainingMode(cargo.isTrainingMode());
            searchTransaction.setTransactionStatus(
                TransactionIfc.STATUS_COMPLETED);
            searchTransaction.setLocaleRequestor(utility.getRequestLocales());
            transaction = trans.readTransaction(searchTransaction);
            getCustomerForTransaction(customerManager, utility, transaction, cargo);
            if (transaction instanceof LayawayTransactionIfc)
            {
                if (!(transaction.getTransactionType()
                    == TransactionIfc.TYPE_LAYAWAY_DELETE))
                {
                    CurrencyIfc LayawayFee =
                        ((LayawayTransactionIfc) transaction)
                            .getLayaway()
                            .getCreationFee();
                    CurrencyIfc grandTotal =
                        ((TenderableTransactionIfc) transaction)
                            .getTransactionTotals()
                            .getGrandTotal();
                    ((TenderableTransactionIfc) transaction)
                        .getTransactionTotals()
                        .setGrandTotal(
                        grandTotal.add(LayawayFee));

                    CurrencyIfc balanceDue =
                        ((TenderableTransactionIfc) transaction)
                            .getTransactionTotals()
                            .getBalanceDue();
                    ((TenderableTransactionIfc) transaction)
                        .getTransactionTotals()
                        .setBalanceDue(
                        balanceDue.add(LayawayFee));
                    
                    LayawayTransactionIfc layawayTransaction = (LayawayTransactionIfc) transaction;
                    if ( layawayTransaction.getLayaway() != null )
                    {
                        layawayTransaction.getLayaway().setCustomer(layawayTransaction.getCustomer());
                    }
                }
            }
            else if (transaction instanceof LayawayPaymentTransactionIfc)
            {
                LayawayPaymentTransactionIfc layawayPaymentTransaction = (LayawayPaymentTransactionIfc) transaction;
                if ( layawayPaymentTransaction.getLayaway() != null )
                {
                    layawayPaymentTransaction.getLayaway().setCustomer(layawayPaymentTransaction.getCustomer());
                    //The value of totalAmountPaid from Layaway was retrieved from the database, which is the accumulated value including the first payment when creating 
                    //the layaway transaction. For reprint receipt, the total tender amount is the total payment for the transaction.
                    layawayPaymentTransaction.getLayaway().setTotalAmountPaid(layawayPaymentTransaction.getCollectedTenderTotalAmount());
                }
            }

            if (logger.isInfoEnabled())
                logger.info(
                    "Training mode: " + transaction.isTrainingMode());

            notFound = false;
        }
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                notFound = true;
            }
            else
            {
                logger.error("Can't find transaction for ID " + transactionID, de);
                letterName = CommonLetterIfc.DB_ERROR;
                notFound = true;
                cargo.setDataExceptionErrorCode(de.getErrorCode());
            }
        }
        catch (Exception e)
        {
            logger.error("Can't find transaction for ID " + transactionID, e);
            letterName = CommonLetterIfc.DB_ERROR;
            cargo.setDataExceptionErrorCode(DataException.UNKNOWN);
            notFound = true;
        }

        // look for suspended transaction
        if (notFound
            && !Util.isObjectEqual(letterName, CommonLetterIfc.DB_ERROR))
        {
            try
            {
                searchTransaction.setTransactionStatus(TransactionIfc.STATUS_SUSPENDED);
                transaction = trans.readTransaction(searchTransaction);
                getCustomerForTransaction(customerManager, utility, transaction, cargo);
                notFound = false;
            }
            catch (DataException de)
            {
                if (de.getErrorCode() == DataException.NO_DATA)
                {
                    notFound = true;
                    logger.warn("Can't find transaction for ID " + transactionID, de);
                    letterName = CommonLetterIfc.NOT_FOUND;
                }
                else
                {
                    logger.error("Can't find transaction for ID " + transactionID, de);
                    letterName = CommonLetterIfc.DB_ERROR;
                    cargo.setDataExceptionErrorCode(de.getErrorCode());
                    notFound = true;
                }
            }
            catch (Exception e)
            {
                logger.error("Can't find transaction for ID " + transactionID, e);
                letterName = CommonLetterIfc.DB_ERROR;
                cargo.setDataExceptionErrorCode(DataException.UNKNOWN);
                notFound = true;
            }
        }

        // check to determine if transaction can be re-printed
        if (notFound == false)
        {
            if (transaction instanceof TenderableTransactionIfc
                && allowReprint(bus, transaction, cargo))
            {
            	if (isTransactionPrintable(transaction, cargo))
            	{
                    // retrieve Enable Reprint Original Receipt parameter
                    boolean reprintOriginalReceiptAllowed = true;
                    ParameterManagerIfc pm =
                        (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
                    try
                    {
                        reprintOriginalReceiptAllowed =
                            pm.getBooleanValue(ParameterConstantsIfc.PRINTING_EnableReprintOriginalReceipt);
                    }
                    catch (ParameterException pe)
                    {
                        logger.warn(
                        "Parameter unavailable, Reprint original receipt will be allowed.");
                    }

                    if (reprintOriginalReceiptAllowed)
                    {
                        letterName = CommonLetterIfc.SUCCESS;
                        cargo.setTransaction((TenderableTransactionIfc) transaction);
                        if (cargo.isCompletedSaleReturnExchange(transaction))
                        {
                            letterName = ReprintReceiptCargo.SUCCESS_SRE_LETTER;
                        }
                        else if (transaction instanceof TillAdjustmentTransactionIfc){
                            letterName = ReprintReceiptCargo.SUCCESS_TILL_ADJUSTMENT_LETTER;
                        }
                    }
                    else
                    {
                        if (cargo.isCompletedSaleReturnExchange(transaction))
                        {
                            letterName = ReprintReceiptCargo.SUCCESS_SRE_LETTER;
                            cargo.setTransaction((TenderableTransactionIfc) transaction);
                        }
                        else
                        {
                            if (logger.isInfoEnabled())
                                logger.info(bus.getServiceName() + "Reprint original transaction is not enabled.");

                            letterName = ReprintReceiptCargo.NOT_REPRINTABLE_ERROR_LETTER;
                            cargo.setNonReprintableErrorID(ReprintReceiptCargo.NOT_REPRINTABLE_ERROR_TRANSACTION_NOT_PRINTABLE);
                        }
                    }
            	}
            	else
            	{
            		if (logger.isInfoEnabled())
                        logger.info(bus.getServiceName() + "Transaction " + transactionID
                                + " is not reprintable; it is of type "
                                + Integer.toString(transaction.getTransactionType()));

                    letterName = ReprintReceiptCargo.NOT_REPRINTABLE_ERROR_LETTER;
                    cargo.setNonReprintableErrorID(
                        ReprintReceiptCargo
                            .NOT_REPRINTABLE_ERROR_TRANSACTION_NOT_PRINTABLE);
            	}

            }
            else if (transaction instanceof TenderableTransactionIfc)
            {
                if (logger.isInfoEnabled())
                    logger.info("Transaction " + transactionID + " register ID "
                            + transaction.getWorkstation().getWorkstationID() + " does not match current register ID "
                            + cargo.getRegisterID() + ".");
                letterName = ReprintReceiptCargo.NOT_REPRINTABLE_ERROR_LETTER;
                cargo.setNonReprintableErrorID(ReprintReceiptCargo.NOT_REPRINTABLE_ERROR_DIFFERENT_REGISTER);
            }
            else
            // cannot reprint - wrong type
            {
                if (logger.isInfoEnabled())
                    logger.info(bus.getServiceName() + "Transaction " + transactionID
                            + " is not reprintable; it is of type "
                            + Integer.toString(transaction.getTransactionType()));

                letterName = ReprintReceiptCargo.NOT_REPRINTABLE_ERROR_LETTER;
                cargo.setNonReprintableErrorID(ReprintReceiptCargo.NOT_REPRINTABLE_ERROR_TRANSACTION_NOT_PRINTABLE);
            }
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    } // end arrive()

    /**
     * Retrieves the customer for the given transaction.
     * @param customerManager
     * @param utility
     * @param transaction
     * @param instance of the <code>ReprintReceiptCargo</code>.
     */
    protected void getCustomerForTransaction(CustomerManagerIfc customerManager, UtilityManagerIfc utility, TransactionIfc transaction, ReprintReceiptCargo cargo)
    {
        if(transaction instanceof TenderableTransactionIfc && StringUtils.isNotBlank(((TenderableTransactionIfc)transaction).getCustomerId()))
        {
            TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)transaction;
            try
            {
                CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID, tenderableTransaction.getCustomerId(), utility.getRequestLocales());
                Locale extendedDataRequestLocale = cargo.getOperator().getPreferredLocale();
                if(extendedDataRequestLocale == null)
                {
                    extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                }
                criteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
                int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
                criteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
                int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
                criteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
                int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
                criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);

                //search for customer
                CustomerIfc customer = customerManager.getCustomer(criteria);
                
                tenderableTransaction.setCustomer(customer);
            }
            catch(DataException de)
            {
                logger.warn("Could not retrieve customer: " + tenderableTransaction.getCustomerId(), de);
            }
        }
    }

    /**
     * Determine if the reprint request should be allowed. The parameters
     * <code>ReprintVerifyRegisterID</code> and <code>ReprintVerifyStoreID</code>
     * are retrieved via the ParameterManager. If the parameters are set to
     * <code>"Y"</code> then the workstation id and store id of the
     * transaction, must be the same as the requesting register id and store
     * id, respectively. If the parameter is set to <code>"N"</code> then the
     * ids can be different and any receipt can be printed.
     *
     * @param bus
     *            Transporter of the reprint request.
     * @param trans
     *            Information retrieved containing the receipt data.
     * @param cargo
     *            The request information concerning the reprint.
     * @return true = allow reprint, false = do not allow reprint
     */
    protected boolean allowReprint(BusIfc bus, TransactionIfc trans, ReprintReceiptCargo cargo)
    {
        boolean requireWorkstationIDVerification = false;
        boolean returnValue = false;
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        // retrieve ReprintReceiptVerifyRegisterID parameter
        try
        {
            requireWorkstationIDVerification = pm.getBooleanValue(ParameterConstantsIfc.PRINTING_VerifyRegisterIDOnReprintReceipt);
        }
        catch (ParameterException pe)
        {
            logger.warn("Parameter unavailable, Register and store ID will be verified.");
        }

        // check against cargo register ID, if required
        if (requireWorkstationIDVerification)
        {
            returnValue = Util.isObjectEqual(trans.getWorkstation().getWorkstationID(), cargo.getRegisterID());
        }
        else
        {
            returnValue = true;
        }

        return (returnValue);
    }

    /**
     * This method is used to check whether the receipt can be printed or not
     * for a post voided transaction.
     * 
     * @param trans
     * @param cargo
     * @return boolean
     */
    private boolean isTransactionPrintable(TransactionIfc trans, ReprintReceiptCargo cargo)
    {
        if (cargo.isDuplicateReceipt() && trans.getTransactionType() == TransactionConstantsIfc.TYPE_VOID)
        {
            return false;
        }
        
        return true;
    }
    
	public void depart(BusIfc bus) {
		ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();
		if(bus.getCurrentLetter().getName().equals("SuccessSRE"))
		if(cargo.getTransaction() != null && cargo.getTransaction() instanceof SaleReturnTransactionIfc && ((MAXSaleReturnTransaction)cargo.getTransaction()).iseComSendTransaction()){
			calcFoodAndNonFoodTotals((SaleReturnTransactionIfc) cargo.getTransaction());
			((MAXSaleReturnTransaction)cargo.getTransaction()).setFoodTotals(foodTotals);
			((MAXSaleReturnTransaction)cargo.getTransaction()).setNonFoodTotals(nonFoodTotals);
			((MAXSaleReturnTransaction)cargo.getTransaction()).setEasyBuyTotals(easyBuyTotals);
			}
	}
	private void calcFoodAndNonFoodTotals(SaleReturnTransactionIfc txnRDO) {
		foodTotals = new BigDecimal("0.00");
		nonFoodTotals = new BigDecimal("0.00");
		easyBuyTotals = new BigDecimal("0.00");
		
		for (int i = 0; i < ((SaleReturnTransaction) txnRDO)
				.getItemContainerProxy().getLineItemsVector().size(); i++) {
			SaleReturnLineItem lineItem = (SaleReturnLineItem) ((SaleReturnTransaction) txnRDO)
					.getItemContainerProxy().getLineItemsVector().get(i);
			Iterator itr = (Iterator) lineItem.getPLUItem()
					.getItemClassification()
					.getMerchandiseClassificationIterator();
			int j = 0;
			while (itr.hasNext()) {
				
				MerchandiseClassificationIfc mrc = (MerchandiseClassification) itr
						.next();
				// Ashish : Changes start fro Rev 1.0 (Food Totals)
				if (j == 2) {
				if (mrc != null) {
					if (mrc.getIdentifier().equalsIgnoreCase(FOOD_CATEFORY))
						foodTotals = foodTotals.add(lineItem.getItemPrice()
								.getExtendedDiscountedSellingPrice()
								.getDecimalValue());
				}
				}
				j++;
				// Ashish : Changes ends for Rev 1.0 (Food Totals)
			}
			j = 0;
			itr = (Iterator) lineItem.getPLUItem()
					.getItemClassification()
					.getMerchandiseClassificationIterator();
			while (itr.hasNext()) {
				
				MerchandiseClassificationIfc mrc = (MerchandiseClassification) itr.next();
				 //	Rev	1.1  Karni: Changes for Easy Buy Totals	
				if (j == 4) {
				if (mrc != null) {
					if (mrc.getIdentifier().equalsIgnoreCase(EASYBUY_CATEFORY))
						easyBuyTotals = easyBuyTotals.add(lineItem.getItemPrice()
								.getExtendedDiscountedSellingPrice()
								.getDecimalValue());
				}
				}
				j++;
				// Karni: Changes End for Easy Buy Totals	
			}
		}
		int flag = foodTotals.compareTo(txnRDO.getTransactionTotals().getGrandTotal().getDecimalValue());
		if(flag <= 0)
			nonFoodTotals = txnRDO.getTransactionTotals().getGrandTotal().getDecimalValue().subtract(foodTotals);
	}

}