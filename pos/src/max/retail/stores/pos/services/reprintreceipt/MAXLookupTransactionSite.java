/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/LookupTransactionSite.java /main/19 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 *Rev 1.0	Aug 29,2016	Ashish yadav	Changes for code merging
 *
 * ===========================================================================
 */
package max.retail.stores.pos.services.reprintreceipt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import max.retail.stores.domain.transaction.MAXTransactionIfc;
import max.retail.stores.pos.services.common.MAXRoundingConstantsIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
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
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.reprintreceipt.LookupTransactionSite;
import oracle.retail.stores.pos.services.reprintreceipt.ReprintReceiptCargo;

/**
 * This site checks to see that the transaction exists, was created on the same
 * day and at the same register and is reprintable.
 */
@SuppressWarnings("serial")
public class MAXLookupTransactionSite extends LookupTransactionSite
{

// Changes starts for rev 1.0
protected String  StrRounding;
    protected List roundingDenominations;
	//Changes end for rev 1.0
    /**
     * Retrieves the transaction and confirms that the transaction may be
     * reprinted.
     *
     * @param bus
     *            Service Bus
     */
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
        MAXTransactionIfc transaction = null;
        String transactionID = cargo.getTransactionID();
        EYSDate today = cargo.getBusinessDate();

        MAXTransactionIfc searchTransaction = null;

        TransactionReadDataTransaction trans = null;

        trans = (TransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);

        String letterName = null;
        boolean notFound = true;

        try
        {
            searchTransaction =
                (MAXTransactionIfc) DomainGateway.getFactory().getTransactionInstance();
            searchTransaction.initialize(transactionID);
            if (searchTransaction.getBusinessDay() == null)
            {
                searchTransaction.setBusinessDay(today);
            }
            searchTransaction.setTrainingMode(cargo.isTrainingMode());
            searchTransaction.setTransactionStatus(
                TransactionIfc.STATUS_COMPLETED);
//Changes Starts for Rev 1.0
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            try
            {
            	StrRounding = pm.getStringValue(MAXRoundingConstantsIfc.ROUNDING);
            	searchTransaction.setRounding(StrRounding);
            	String[] roundingDenominationsArray = pm.getStringValues(MAXRoundingConstantsIfc.ROUNDING_DENOMINATIONS);
               
    			
            	if(roundingDenominationsArray == null || roundingDenominationsArray.length == 0)
            		{
            			throw new ParameterException("List of parameters undefined");
            		}
            		roundingDenominations = new ArrayList();
            		roundingDenominations.add(0,new BigDecimal(0.0));
            		for(int i=0;i<roundingDenominationsArray.length;i++)
            		{
            			roundingDenominations.add(new BigDecimal(roundingDenominationsArray[i]));
            		}
            		roundingDenominations.add(roundingDenominationsArray.length,new BigDecimal(1.00));

//            		List must be sorted before setting on the cargo.
            		Collections.sort(roundingDenominations,new Comparator()	{
            			public int compare(Object o1, Object o2) {
            				BigDecimal denomination1 = (BigDecimal)o1;
            				BigDecimal denomination2 = (BigDecimal)o2;
            				return denomination1.compareTo(denomination2);
            			}
            		});
            		
            		searchTransaction.setRoundingDenominations(roundingDenominations);
//            	}
            }
            catch(ParameterException pe)
            {
            	//if there is an error with the parameters, the price rounding logic should be disabled
            	 //cargo.setRoundingEnabledLogic(false);
            	 logger.error( "" + Util.throwableToString(pe) + "");
            }
// Changes ends for Rev 1.0

            searchTransaction.setLocaleRequestor(utility.getRequestLocales());
            transaction = (MAXTransactionIfc) trans.readTransaction(searchTransaction);
            getCustomerForTransaction(customerManager, utility, transaction, cargo);
			//Changes starts for Rev 1.0
            //if (transaction instanceof LayawayTransactionIfc)
			if((transaction instanceof LayawayTransactionIfc) && transaction.getTransactionType() != 22)
			//Changes ends for Rev 1.0
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
                transaction = (MAXTransactionIfc) trans.readTransaction(searchTransaction);
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
	// Changes starts for Rev 1.0
       // if (cargo.isDuplicateReceipt() && trans.getTransactionType() == TransactionConstantsIfc.TYPE_VOID)
        //{
          //  return false;
        //}
        
        //return true;
		 return !cargo.getDuplicateReceipt() || trans.getTransactionType() != 3;
		// Changes ends for Rev 1.0
    }

}