/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*	Rev 1.5     May 11, 2017		Ashish Yadav		Changes for M-Coupon Issuance  FES
*	Rev 1.4     Dec 06, 2016		Ashish Yadav		Changes for Employee Discount  FES
*	Rev 1.3     Nov 11, 2016		Ashish Yadav		Changes for Home Delivery Send FES
*	Rev 1.2		Nov 08, 2016        Nadia Arora         MAX-StoreCredi_Return requirement.
*	Rev 1.1     Oct 19, 2016		Mansi Goel			Changes for Customer FES
*	Rev 1.0     Oct 17, 2016		Nitesh Khadaria		Code Merge
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import java.util.ArrayList;
import javax.swing.text.Element;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.io.BufferedReader;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import org.apache.commons.io.FileUtils;
import org.apache.fop.datatypes.Length;
import org.apache.log4j.Logger;

import com._360commerce.common.sql.SQLSelectStatement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itextpdf.text.log.SysoCounter;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.discountCoupon.MAXDiscountCouponIfc;
import max.retail.stores.domain.financial.MAXShippingMethodIfc;
import max.retail.stores.domain.gstin.GSTInvoice;
import max.retail.stores.domain.gstin.GSTInvoiceItem;
import max.retail.stores.domain.gstin.GSTSaveRequest;
import max.retail.stores.domain.gstin.MAXGSTINValidationResponse;
import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
import max.retail.stores.domain.lineitem.MAXItemTax;
import max.retail.stores.domain.lineitem.MAXItemTaxIfc;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
//import max.retail.stores.domain.stock.MAXGiftCardPLUItemIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.gstinJob.MAXGSTINAutomationTransaction;
import max.retail.stores.gstinJob.utility.gstin.MAXGSTINConstantsIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.JdbcSaveRetailTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.lineitem.TaxableLineItemIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import javax.swing.text.rtf.RTFEditorKit;

public class MAXJdbcSaveRetailTransaction extends JdbcSaveRetailTransaction implements MAXARTSDatabaseIfc {

	/**
	* 
	*/
	private static final long serialVersionUID = -1823015324877068302L;
	private static final Connection conn = null;
	/**
	 * The logger to which log messages will be sent.
	 **/

	protected MAXTICCustomerIfc ticCustomerIfc = null;
	protected SaleReturnTransaction saleReturnTransaction = null;
	protected String customerLoyaltyNumber = null;
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveRetailTransaction.class);

	HashMap inputData = new HashMap();
	HashMap invoiceInput = new HashMap();
	HashMap outputData = new HashMap();

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 **/
	// ---------------------------------------------------------------------
	public MAXJdbcSaveRetailTransaction() {
		super();
		setName("MAXJdbcSaveRetailTransaction");
	}

	// ---------------------------------------------------------------------
	/**
	 * Saves the retail_transaction. This method first tries to update the
	 * transaction. If that fails, it will attempt to insert the transaction.
	 * <p>
	 * Modifies both the Transaction and RetailTransaction tables.
	 * <P>
	 * 
	 * @param dataConnection the connection to the data source
	 * @param transaction    The Retail Transaction to update
	 * @exception DataException
	 **/
	// ---------------------------------------------------------------------
	public void saveRetailTransaction(JdbcDataConnection dataConnection, TenderableTransactionIfc posTransaction)
			throws DataException {

		MAXSaleReturnTransactionIfc maxSaleReturn = null;
		if (posTransaction != null && posTransaction instanceof MAXSaleReturnTransactionIfc) {
			if (posTransaction instanceof MAXLayawayTransaction)
				maxSaleReturn = (MAXLayawayTransaction) posTransaction;
			else
				maxSaleReturn = (MAXSaleReturnTransaction) posTransaction;
			if (!maxSaleReturn.getCapillaryCouponsApplied().isEmpty())
				insertCapillaryCoupons(dataConnection, maxSaleReturn);
		}

		/* changes for Rev 1.5 start */

		if (posTransaction != null) {
			if (posTransaction instanceof MAXSaleReturnTransactionIfc
					&& posTransaction instanceof MAXSaleReturnTransaction) {
				MAXSaleReturnTransaction MAXSaleReturnTrans = (MAXSaleReturnTransaction) posTransaction;
				if (MAXSaleReturnTrans.getMcouponList() != null && !MAXSaleReturnTrans.getMcouponList().isEmpty()) {
					new MAXJdbcSaveMcouponDeatils().saveMcoupons(dataConnection, MAXSaleReturnTrans);
				}
			}
		}

		if (posTransaction != null) {
			if (posTransaction instanceof MAXSaleReturnTransactionIfc
					&& posTransaction instanceof MAXSaleReturnTransaction) {
				MAXSaleReturnTransaction trans = (MAXSaleReturnTransaction) posTransaction;
				String panNo = trans.getPanNumber();
				String form60 = trans.getForm60IDNumber();
				String visa = trans.getVisaNumber();
				String passportno = trans.getPassportNumber();
				String itrack = trans.getITRAckNumber();
				if (panNo != null || form60 != null || visa != null || passportno != null || itrack != null) {
					saveCustomerTypePANDetails(dataConnection, trans);
				}
			}
		}

		if (maxSaleReturn.getGSTINNumber() != null && !maxSaleReturn.getGSTINNumber().isEmpty()) {
			logger.info("Saving rtf file ");
			logger.warn(maxSaleReturn + "SAVING RTF FILE--------------------------");
			getInvoiceLocationAndWrite(maxSaleReturn);

			logger.warn("RTFCATCH_________________________________________________________");
			saveEInvoiceGSTNumber(dataConnection, maxSaleReturn);
			logger.warn("INVOICE==================================");

		}

		/* changes for Rev 1.5 end */
		/*
		 * If the insert fails, then try to update the transaction
		 */
		try {
			insertRetailTransaction(dataConnection, posTransaction);
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			logger.error("Couldn't save retail transaction.");
			logger.error("" + e + "");
			throw new DataException(DataException.UNKNOWN, "Couldn't save retail transaction.", e);
		}
	}

	public void saveTransactionShippings(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction)
			throws DataException {

		String firstName = "";
		String lastName = "";
		String businessName = "";
		boolean isBusinessCustomer = false;
		String addrLine1 = "";
		String addrLine2 = "";
		String city = "";
		String state = "";
		String zip = "";
		String zipExt = "";
		String country = "";
		String phoneNumber = "";
		String phoneType = "";
		String phoneAreaCode = "";
		String phoneExtension = "";
		String shippingCarrier = "";
		String shippingType = "";
		CurrencyIfc baseShippingCharge = null;
		CurrencyIfc weightBasedShippingCharge = null;
		CurrencyIfc flatRate = null;
		String shippingInstruction = "";
		CurrencyIfc calculatedShippingCharge = null;
		int taxGroupId = 0;
		CurrencyIfc taxAmount = null;
		CurrencyIfc inclusiveTaxAmount = null;
		// Chnages start for Rev 1.3(Send)
		boolean isExternalSend = false;
		// Changes ends for rev 1.3 (Send)

		int count = 0;
		if (transaction.getTransactionTotals() != null) {
			// Changes start for Rev 1.3 (Send : Type cast error)
			count = ((RetailTransactionIfc) transaction).getItemSendPackagesCount();
		}
		if (count > 0) {
			SendPackageLineItemIfc[] sendPackages = ((RetailTransactionIfc) transaction).getSendPackages();
			// Changes ends for Rev 1.3 (Send)
			if (sendPackages.length == count) {
				for (int i = 0; i < count; i++) {
					SQLInsertStatement sql = new SQLInsertStatement();

					// Table
					sql.setTable(TABLE_SHIPPING_RECORDS);

					MAXShippingMethodIfc shippingMethod = (MAXShippingMethodIfc) sendPackages[i].getShippingMethod();
					CustomerIfc shippingCustomer = sendPackages[i].getCustomer();

					// Fields
					sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
					sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
					sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
					sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
					sql.addColumn(FIELD_SEND_LABEL_COUNT, String.valueOf(i + 1));

					firstName = shippingCustomer.getFirstName();
					if (firstName != null) {
						sql.addColumn(FIELD_SHIPPING_RECORDS_FIRST_NAME, makeSafeString(firstName));
					}
					lastName = shippingCustomer.getLastName();
					if (lastName != null) {
						sql.addColumn(FIELD_SHIPPING_RECORDS_LAST_NAME, makeSafeString(lastName));
					}
					businessName = shippingCustomer.getCustomerName();
					if (businessName != null) {
						sql.addColumn(FIELD_SHIPPING_RECORDS_BUSINESS_NAME, makeSafeString(businessName));
					}

					isBusinessCustomer = shippingCustomer.isBusinessCustomer();
					sql.addColumn(FIELD_SHIPPING_RECORDS_BUSINESS_CUSTOMER, makeStringFromBoolean(isBusinessCustomer));

					Vector addressVector = shippingCustomer.getAddresses();
					if (!addressVector.isEmpty()) {
						AddressIfc addr = (AddressIfc) addressVector.elementAt(0);

						Vector lines = addr.getLines();
						if (lines.size() != 0) {
							addrLine1 = (String) lines.elementAt(0);
							if (lines.size() >= 1 && !lines.elementAt(1).equals("")) {
								addrLine2 = (String) lines.elementAt(1);
							}
						}
						city = addr.getCity();
						state = addr.getState();
						zip = addr.getPostalCode();
						country = addr.getCountry();
						Vector phones = shippingCustomer.getPhones();
						if (phones.size() > 0) {
							for (int p = 0; p < phones.size(); p++) {
								PhoneIfc phone = (PhoneIfc) phones.elementAt(p);
								if (phone != null) {
									phoneType = String.valueOf(phone.getPhoneType());
									phoneNumber = phone.getPhoneNumber();
									phoneAreaCode = phone.getCountry();
									phoneExtension = phone.getExtension();
									if (!Util.isEmpty(phoneNumber)) {
										// only one telephone number is there
										// for shipping customer
										break;
									}
								}
							}
						}

						if (addr.getPostalCodeExtension() != null && !addr.getPostalCodeExtension().equals("")) {
							zipExt = addr.getPostalCodeExtension();
						}

						sql.addColumn(FIELD_SHIPPING_RECORDS_LINE1, makeSafeString(addrLine1));
						sql.addColumn(FIELD_SHIPPING_RECORDS_LINE2, makeSafeString(addrLine2));
						sql.addColumn(FIELD_SHIPPING_RECORDS_CITY, makeSafeString(city));
						sql.addColumn(FIELD_SHIPPING_RECORDS_STATE, makeSafeString(state));
						sql.addColumn(FIELD_SHIPPING_RECORDS_POSTAL_CODE, makeSafeString(zip));
						sql.addColumn(FIELD_SHIPPING_RECORDS_ZIP_EXT, makeSafeString(zipExt));
						sql.addColumn(FIELD_SHIPPING_RECORDS_COUNTRY, makeSafeString(country));
						sql.addColumn(FIELD_PHONE_TYPE, makeSafeString(phoneType));
						sql.addColumn(FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER, makeSafeString(phoneNumber));
						sql.addColumn(FIELD_CONTACT_AREA_TELEPHONE_CODE, makeSafeString(phoneAreaCode));
						sql.addColumn(FIELD_CONTACT_EXTENSION, makeSafeString(phoneExtension));
						sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
						sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
					}
					// Changes start for Rev 1.3 (Send)
					shippingCarrier = shippingMethod
							.getShippingCarrier(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
					shippingType = shippingMethod
							.getShippingType(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
					// Changes start for Rev 1.3 (Send)
					baseShippingCharge = shippingMethod.getBaseShippingCharge();
					weightBasedShippingCharge = shippingMethod.getShippingChargeRateByWeight();
					flatRate = shippingMethod.getFlatRate();
					shippingInstruction = shippingMethod.getShippingInstructions();
					calculatedShippingCharge = shippingMethod.getCalculatedShippingCharge();
					// Chnages start for Rev 1.3 (Send : removed below lines in base 14)
					/*
					 * taxGroupId = shippingMethod.getTaxGroupID taxAmount = ((TaxableLineItemIfc)
					 * sendPackages[i]).getItemTax().getItemTaxAmount(); inclusiveTaxAmount =
					 * ((TaxableLineItemIfc) sendPackages[i]).getItemTax()
					 * .getItemInclusiveTaxAmount();
					 */
					isExternalSend = sendPackages[i].isExternalSend();
					// Chnages end for Rev 1.3 (Send)
					// Type of shipping method id is Integer
					sql.addColumn(FIELD_SHIPPING_METHOD_ID,
							String.valueOf(Integer.valueOf(shippingMethod.getShippingMethodID())));
					sql.addColumn(FIELD_SHIPPING_CARRIER, makeSafeString(shippingCarrier));
					sql.addColumn(FIELD_SHIPPING_TYPE, makeSafeString(shippingType));
					sql.addColumn(FIELD_SHIPPING_BASE_CHARGE, baseShippingCharge.getStringValue());
					sql.addColumn(FIELD_SHIPPING_CHARGE_RATE_BY_WEIGHT, weightBasedShippingCharge.getStringValue());
					sql.addColumn(FIELD_FLAT_RATE, flatRate.getStringValue());
					sql.addColumn(FIELD_SHIPPING_CHARGE, calculatedShippingCharge.getStringValue());
					sql.addColumn(FIELD_SPECIAL_INSTRUCTION, makeSafeString(shippingInstruction));
					// Changes start for Rev 1.3 (Send : removed below lines in base 14)
					/*
					 * sql.addColumn(FIELD_TAX_GROUP_ID, taxGroupId);
					 * sql.addColumn(FIELD_TAX_AMOUNT, taxAmount.getStringValue());
					 * sql.addColumn(FIELD_TAX_INC_AMOUNT, inclusiveTaxAmount.getStringValue());
					 */
					sql.addColumn(FIELD_EXTERNAL_SHIPPING_FLAG, makeStringFromBoolean(isExternalSend));
					// Changes ends for Rev 1.3 (Send)

					if (shippingMethod instanceof MAXShippingMethodIfc
							&& ((MAXShippingMethodIfc) shippingMethod).getExpectedDeliveryDate() != null
							&& ((MAXShippingMethodIfc) shippingMethod).getExpectedDeliveryTime() != null) {
						EYSDate expectedDeliveryDate = ((MAXShippingMethodIfc) shippingMethod)
								.getExpectedDeliveryDate();
						EYSTime expectedDeliveryTime = ((MAXShippingMethodIfc) shippingMethod)
								.getExpectedDeliveryTime();

						sql.addColumn("EXP_DEL_DATE", dateToSQLTimestampFunction(expectedDeliveryDate));
						sql.addColumn("EXP_DEL_TIME", makeSafeString(expectedDeliveryTime.toString()));
					}
					try {
						dataConnection.execute(sql.getSQLString());
					} catch (DataException de) {
						logger.error("" + de + "");
						throw de;
					} catch (Exception e) {
						throw new DataException(DataException.UNKNOWN, "saveTransactionShippings", e);
					}

					// Changes starts for Rev 1.3 (Send : Below method is not present in base 14
					// (removed))
					// saveTransactionShippingTaxInformation(dataConnection, transaction,
					// sendPackages[i], i + 1);
					// Changes ends for Rev 1.3 (Send)
				}
			}
		}
	}

	public void insertRetailTransaction(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction)
			throws DataException {

		customerLoyaltyNumber = null;
		MAXCustomerIfc customer = null;
		MAXCustomer localTicCustomer = null;
		if (transaction != null && transaction instanceof SaleReturnTransaction) {
			saleReturnTransaction = (SaleReturnTransaction) transaction;
		}

		if (saleReturnTransaction != null && (saleReturnTransaction.getCustomer() != null
				&& saleReturnTransaction.getCustomer() instanceof MAXCustomer)) {

			customer = (MAXCustomerIfc) saleReturnTransaction.getCustomer();
			// Changes starts for Rev 1.4(Ashish : Employee Discount)
			if (((MAXSaleReturnTransactionIfc) saleReturnTransaction).getMAXTICCustomer() != null) {
				ticCustomerIfc = (MAXTICCustomer) ((MAXSaleReturnTransactionIfc) saleReturnTransaction)
						.getMAXTICCustomer();
			} else if (((MAXSaleReturnTransactionIfc) saleReturnTransaction).getTicCustomer() != null
					&& ((MAXSaleReturnTransactionIfc) saleReturnTransaction).getTicCustomer() instanceof MAXCustomer) {
				localTicCustomer = (MAXCustomer) ((MAXSaleReturnTransactionIfc) saleReturnTransaction).getTicCustomer();
			}
			// Changes starts for Rev 1.4(Ashish : Employee Discount)
			if (customer != null && customer.getLoyaltyCardNumber() != null) {
				customerLoyaltyNumber = customer.getLoyaltyCardNumber();
			} else if (ticCustomerIfc != null && ticCustomerIfc.getTICCustomerID() != null
					&& !ticCustomerIfc.getTICCustomerID().equalsIgnoreCase("")) {
				customerLoyaltyNumber = ticCustomerIfc.getTICCustomerID();
			} else if (localTicCustomer != null && localTicCustomer.getCustomerID() != null
					&& !localTicCustomer.getCustomerID().equalsIgnoreCase("")) {
				customerLoyaltyNumber = localTicCustomer.getCustomerID();
			}

		} else if (transaction != null && transaction.getCustomer() != null
				&& transaction.getCustomer() instanceof MAXCustomer) {

			customerLoyaltyNumber = ((MAXCustomer) transaction.getCustomer()).getLoyaltyCardNumber();
			customer = (MAXCustomerIfc) transaction.getCustomer();
		}

		/*
		 * Insert the transaction in the Transaction table first.
		 */
		insertTransaction(dataConnection, transaction);

		// if a suspended transaction save all applicable advanced pricing rules
		if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED
				&& transaction instanceof SaleReturnTransactionIfc) {

			((SaleReturnTransactionIfc) transaction).clearBestDealDiscounts();
			insertAdvancedPricingRules(dataConnection, transaction);
		}

		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_RETAIL_TRANSACTION);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		if (customer != null && customer.getCustomerType() != null
				&& customer.getCustomerType().equalsIgnoreCase("L")) {
			sql.addColumn(FIELD_CUSTOMER_ID, makeSafeString(customer.getCustomerID()));
		}

		if (customer != null && !customer.isCustomerTag()
				&& customer.getCustomerType().equals(MAXCustomerConstantsIfc.CRM)
				&& customer.isBothLocalAndLoyaltyCustomerAttached()) {
			sql.addColumn(FIELD_CUSTOMER_ID, makeSafeString(customer.getCustomerID()));
			sql.addColumn(FIELD_ORDER_ID, makeSafeString(customerLoyaltyNumber));
			sql.addColumn(FIELD_TIC_CUSTOMER_ID, makeSafeString(customerLoyaltyNumber));
		} else if (customerLoyaltyNumber != null && !customerLoyaltyNumber.trim().equalsIgnoreCase("")) {
			sql.addColumn(FIELD_ORDER_ID, makeSafeString(customerLoyaltyNumber));
			sql.addColumn(FIELD_TIC_CUSTOMER_ID, makeSafeString(customerLoyaltyNumber));
		}

		if (customer != null && customer.getCustomerType() != null
				&& customer.getCustomerType().equals(MAXCustomerConstantsIfc.CRM)) {
			sql.addColumn(FIELD_CUSTOMER_ID, makeSafeString(customer.getCustomerID()));
		}
		if (saleReturnTransaction != null && saleReturnTransaction instanceof MAXLayawayTransaction && customer != null
				&& customer.getCustomerType() != null
				&& customer.getCustomerType().equals(MAXCustomerConstantsIfc.CRM)) {
			sql.addColumn(FIELD_CUSTOMER_ID, makeSafeString(customer.getCustomerID()));

		}

		sql.addColumn(FIELD_SUSPENDED_TRANSACTION_REASON_CODE, getSuspendedTransactionReasonCode(transaction));

		addTransactionTotalsColumns((SQLUpdatableStatementIfc) sql, transaction);

		sql.addColumn(FIELD_MASKED_PERSONAL_ID_NUMBER, getPersonalIDNumber(transaction));
		sql.addColumn(FIELD_PERSONAL_ID_REQUIRED_TYPE, getPersonalIDType(transaction));
		sql.addColumn(FIELD_PERSONAL_ID_STATE, getPersonalIDState(transaction));
		sql.addColumn(FIELD_PERSONAL_ID_COUNTRY, getPersonalIDCountry(transaction));
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_SEND_PACKAGE_COUNT, getSendPackageCount(transaction));
		sql.addColumn(FIELD_TRANSACTION_COUNTRY, getTransactionCountryCode(transaction));
		CurrencyIfc adjustment = getTenderChangeRoundedAmount(transaction);
		sql.addColumn(FIELD_TRANSACTION_OFF_TOTAL, adjustment.toString());

		if (transaction instanceof SaleReturnTransactionIfc) {
			SaleReturnTransactionIfc srTransaction = (SaleReturnTransactionIfc) transaction;
			if (srTransaction.getAgeRestrictedDOB() != null) {
				sql.addColumn(FIELD_AGE_RESTRICTED_DOB, getAgeRestrictedDOB(srTransaction));
			}

			if (srTransaction.isSendCustomerLinked()) {
				sql.addColumn(FIELD_SEND_CUSTOMER_TYPE, "'0'");
			} else {
				sql.addColumn(FIELD_SEND_CUSTOMER_TYPE, "'1'");
			}
			if (srTransaction.isCustomerPhysicallyPresent()) {
				sql.addColumn(FIELD_SEND_CUSTOMER_PHYSICALLY_PRESENT, "'1'");
			} else {
				sql.addColumn(FIELD_SEND_CUSTOMER_PHYSICALLY_PRESENT, "'0'");
			}
			if (((SaleReturnTransaction) transaction).isTransactionLevelSendAssigned()) {
				sql.addColumn(FIELD_TRANSACTION_LEVEL_SEND, "'1'");
			} else {
				sql.addColumn(FIELD_TRANSACTION_LEVEL_SEND, "'0'");
			}
		}

		// if it's a retail transaction, add these columns
		if (transaction instanceof RetailTransactionIfc) {
			RetailTransactionIfc rt = (RetailTransactionIfc) transaction;
			sql.addColumn(FIELD_EMPLOYEE_ID, getSalesAssociateID(rt));
			sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryString(rt));
		}

		if (transaction.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_PARTIAL
				|| transaction.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_COMPLETE) {
			CurrencyIfc appliedDeposit = getAppliedOrderDeposit(transaction);
			sql.addColumn(FIELD_DEPOSIT_AMOUNT_APPLIED, appliedDeposit.getStringValue());
		}

		if (transaction instanceof LayawayPaymentTransactionIfc) {
			LayawayPaymentTransactionIfc lt = (LayawayPaymentTransactionIfc) transaction;
			if (lt.getLayaway() != null) {
				sql.addColumn(FIELD_LAYAWAY_ID, makeSafeString(lt.getLayaway().getLayawayID()));
			}
		}
		if (transaction.getIRSCustomer() != null) {
			sql.addColumn(FIELD_IRS_CUSTOMER_ID, getIRSCustomerID(transaction));
		}

		if (transaction instanceof MAXSaleReturnTransaction
				&& ((MAXSaleReturnTransaction) transaction).getEReceiptOTP() != null) {
			sql.addColumn(FIELD_ERECEIPT_OTP, getEReceiptOTPNumber((MAXSaleReturnTransaction) transaction));
		}

		if (transaction instanceof MAXSaleReturnTransaction) {
			MAXSaleReturnTransaction tran = (MAXSaleReturnTransaction) transaction;
			if (tran.iseComSendTransaction()) {
				sql.addColumn(FIELD_ECOM_ORDER_NO, getEComOrderNo(tran));
				sql.addColumn(FIELD_ECOM_ORDER_AMOUNT, getEComOrderAmount(tran));
				sql.addColumn(FIELD_ECOM_ORDER_TRANS_NO, getEComOrderTransNo(tran));
				sql.addColumn(FIELD_ECOM_ORDER_TYPE, getEComOrderType(tran)); // Chnages to save Order type - Karni

			}

			// PAN Changes sql.addColumn(FIELD_PAN_NUMBER,getPANNumber(tran));

			if (((MAXSaleReturnTransaction) transaction).getGSTINNumber() != null
					&& !((MAXSaleReturnTransaction) transaction).getGSTINNumber().isEmpty())
				sql.addColumn(FIELD_EXTERNAL_ORDER_ID, getGSTINNumber(tran));

		}

		try {
			logger.warn("sql.getSQLString()" + sql.getSQLString());
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			logger.error("" + e + "");
			throw new DataException(DataException.UNKNOWN, "insertRetailTransaction", e);
		}

		finally {
			saleReturnTransaction = null;
			ticCustomerIfc = null;
		}
		// Changes start for Rev 1.4 (Ashish : Employee Discount)
		if (transaction instanceof SaleReturnTransaction) {
			if ((((SaleReturnTransaction) transaction).getItemSendPackagesCount() > 0)) {
				saveTransactionShippings(dataConnection, transaction);
			}
		}
		// Changes start for Rev 1.4 (Ashish : Employee Discount)
	}

	private MAXLayawayTransaction makeSafeString(MAXSaleReturnTransaction transaction) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * public String getPANNumber(MAXSaleReturnTransaction transaction) { String
	 * panNumber = "null"; if (transaction.getPanNumber() != null) { panNumber = "'"
	 * + transaction.getPanNumber() + "'"; } return panNumber; }
	 */

	/**
	 * This method gets the total deposit applied to this transaction.
	 * 
	 * @param transaction
	 * @return
	 */
	private CurrencyIfc getAppliedOrderDeposit(TenderableTransactionIfc transaction) {
		CurrencyIfc depositPaid = DomainGateway.getBaseCurrencyInstance();
		AbstractTransactionLineItemIfc[] lineItems = ((OrderTransactionIfc) transaction).getLineItems();
		depositPaid.setZero();
		for (int i = 0; i < lineItems.length; i++) {
			depositPaid = depositPaid
					.add(((SaleReturnLineItemIfc) lineItems[i]).getOrderItemStatus().getDepositAmount());
		}
		return depositPaid;
	}

	public String getTicCustomerId(TenderableTransactionIfc transaction) {
		CustomerIfc customer = ((MAXSaleReturnTransaction) transaction).getTicCustomer();
		String customerID = "null";
		if (customer != null) {
			customerID = "'" + customer.getCustomerID() + "'";
		}
		return (customerID);
	}

	/**
	 * This method saves the capillary coupon's details in the table DC_MAS.
	 * 
	 * @param dataConnection
	 * @param saleReturnTransaction
	 * 
	 * @throws DataException
	 */
	public void insertCapillaryCoupons(JdbcDataConnection dataConnection,
			MAXSaleReturnTransactionIfc saleReturnTransaction) throws DataException {
		Object coupons[] = saleReturnTransaction.getCapillaryCouponsApplied().toArray();

		for (int i = 0; i < coupons.length; i++) {
			MAXDiscountCouponIfc capillaryCoupon = (MAXDiscountCouponIfc) coupons[i];
			SQLInsertStatement sql = new SQLInsertStatement();
			sql.setTable(TABLE_DISCOUNT_CARD);
			sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(saleReturnTransaction.getWorkstation().getStoreID()));
			sql.addColumn(FIELD_WORKSTATION_ID,
					makeSafeString(saleReturnTransaction.getWorkstation().getWorkstationID()));
			sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(saleReturnTransaction.getBusinessDay()));
			sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, saleReturnTransaction.getTransactionSequenceNumber());
			sql.addColumn(FIELD_GIFT_CARD_ACTIVATION_ADJUDICATION_CODE,
					makeSafeString(capillaryCoupon.getRedeemstatus() == true ? "Y" : "N"));
			sql.addColumn(FIELD_SALE_USRID, makeSafeString(saleReturnTransaction.getSalesAssociateID()));
			sql.addColumn(FIELD_GIFT_VOUCHER_NUMBER, makeSafeString(capillaryCoupon.getCouponNumber()));
			sql.addColumn(FIELD_CARD_TYPE, makeSafeString(capillaryCoupon.getCampaignId()));
			// sequence number in case of capillary coupon redemption will be 0
			// only
			sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, '0');
			sql.addColumn(FIELD_RED_CODE, makeSafeString("0")); // NOT NULL
			try {
				dataConnection.execute(sql.getSQLString());
			} catch (DataException de) {
				logger.error("" + de + "");
				throw de;
			} catch (Exception e) {
				throw new DataException(DataException.UNKNOWN, "insertCapillaryCoupons", e);
			}
		}
	}

	public String getEComOrderNo(MAXSaleReturnTransaction transaction) {
		String eComOrderNo = "null";
		if (transaction.geteComOrderNumber() != null) {
			eComOrderNo = "'" + transaction.geteComOrderNumber() + "'";
		}
		return eComOrderNo;
	}

	public String getEComOrderTransNo(MAXSaleReturnTransaction transaction) {
		String eComOrderTransNo = "null";
		if (transaction.geteComOrderTransNumber() != null) {
			eComOrderTransNo = transaction.geteComOrderTransNumber();
		}
		return eComOrderTransNo;
	}

	public String getEComOrderAmount(MAXSaleReturnTransaction transaction) {
		String eComOrderAmount = "null";
		if (transaction.geteComOrderAmount() != null) {
			eComOrderAmount = transaction.geteComOrderAmount().getStringValue();
		}
		return eComOrderAmount;
	}

	public String getEComOrderType(MAXSaleReturnTransaction transaction) {
		String eComOrderType = "null";
		if (transaction.geteComOrderType() != null) {
			eComOrderType = "'" + transaction.geteComOrderType() + "'";
		}
		return eComOrderType;
	}

	public String getGSTINNumber(MAXSaleReturnTransaction transaction) {
		String gstin = "null";
		if (transaction.getGSTINNumber() != null) {
			gstin = "'" + transaction.getGSTINNumber() + "'";
		}
		return gstin;
	}

	public String getEReceiptOTPNumber(MAXSaleReturnTransaction transaction) {

		String eReceiptOTP = "";
		if (transaction.getEReceiptOTP() != null) {
			eReceiptOTP = "'" + transaction.getEReceiptOTP() + "'";
		}
		return eReceiptOTP;
	}

	public void saveTransactionShippingTaxInformation(JdbcDataConnection dataConnection,
			TenderableTransactionIfc transaction, SendPackageLineItemIfc sendPackage, int sendLabelCount)
			throws DataException {
		TaxInformationIfc[] taxInfo = ((TaxLineItemInformationIfc) sendPackage).getTaxInformationContainer()
				.getTaxInformation();
		if (taxInfo != null) {
			for (int i = 0; i < taxInfo.length; i++) {
				saveShippingTaxInformation(dataConnection, transaction, sendPackage, sendLabelCount, taxInfo[i]);
			}
		}
	}

	public void saveShippingTaxInformation(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction,
			SendPackageLineItemIfc sendPackage, int sendLabelCount, TaxInformationIfc taxInfo) throws DataException {
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_SHIPPING_RECORDS_TAX);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_SEND_LABEL_COUNT, String.valueOf(sendLabelCount));
		sql.addColumn(FIELD_TAX_AUTHORITY_ID, taxInfo.getTaxAuthorityID());
		sql.addColumn(FIELD_TAX_GROUP_ID, taxInfo.getTaxGroupID());
		sql.addColumn(FIELD_TAX_TYPE, taxInfo.getTaxTypeCode());
		sql.addColumn(FIELD_TAX_HOLIDAY, makeStringFromBoolean(taxInfo.getTaxHoliday()));
		sql.addColumn(FIELD_TAX_MODE, taxInfo.getTaxMode());
		sql.addColumn(FIELD_TAXABLE_SALE_RETURN_AMOUNT, taxInfo.getTaxableAmount().toString());
		sql.addColumn(FIELD_FLG_TAX_INCLUSIVE, makeStringFromBoolean(taxInfo.getInclusiveTaxFlag()));
		sql.addColumn(FIELD_SALE_RETURN_TAX_AMOUNT, taxInfo.getTaxAmount().toString());
		sql.addColumn(FIELD_ITEM_TAX_AMOUNT_TOTAL,
				((TaxableLineItemIfc) sendPackage).getItemTax().getItemTaxAmount().getStringValue());
		sql.addColumn(FIELD_ITEM_TAX_INC_AMOUNT_TOTAL,
				((TaxableLineItemIfc) sendPackage).getItemTax().getItemInclusiveTaxAmount().getStringValue());
		sql.addColumn(FIELD_TAX_RULE_NAME, makeSafeString(taxInfo.getTaxRuleName()));
		sql.addColumn(FIELD_TAX_PERCENTAGE, String.valueOf(taxInfo.getTaxPercentage().floatValue()));
		if (taxInfo.getUniqueID() != null && !taxInfo.getUniqueID().equals("")) {
			sql.addColumn(FIELD_UNIQUE_ID, makeSafeString(taxInfo.getUniqueID()));
		}
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

		try {
			dataConnection.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "saveShippingTaxInformation", e);
		}
	}

	public CurrencyIfc getTenderChangeRoundedAmount(TenderableTransactionIfc transaction) {
		CurrencyIfc adjustment = null;
		if (transaction instanceof OrderTransactionIfc) {
			adjustment = ((OrderTransactionIfc) transaction).getTenderTransactionTotals()
					.getCashChangeRoundingAdjustment();
		} else if (transaction instanceof LayawayTransactionIfc) {
			adjustment = ((LayawayTransactionIfc) transaction).getTenderTransactionTotals()
					.getCashChangeRoundingAdjustment();
		} else {
			adjustment = transaction.getTransactionTotals().getCashChangeRoundingAdjustment();
		}
		return adjustment;
	}

	// Changes starts for Rev 1.5
	public void insertTransaction(JdbcDataConnection dataConnection, TransactionIfc transaction) throws DataException {
		SQLInsertStatement sql = new SQLInsertStatement();

		// Table
		sql.setTable(TABLE_TRANSACTION);

		// Fields
		sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
		sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
		sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
		sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
		sql.addColumn(FIELD_OPERATOR_ID, getOperatorID(transaction));
		sql.addColumn(FIELD_TRANSACTION_BEGIN_DATE_TIMESTAMP, getTransactionBeginDateString(transaction));
		sql.addColumn(FIELD_TRANSACTION_END_DATE_TIMESTAMP, getTransactionEndDateString(transaction));
		sql.addColumn(FIELD_TRANSACTION_TYPE_CODE, getTransactionType(transaction));
		sql.addColumn(FIELD_TRANSACTION_TRAINING_FLAG, getTrainingFlag(transaction));
		sql.addColumn(FIELD_EMPLOYEE_ID, getSalesAssociateID(transaction));
		if ((transaction.getCustomerInfo().getPhoneNumber()) != null) {
			sql.addColumn(FIELD_CUSTOMER_INFO, getCustomerInfo(transaction));
		} else {
			sql.addColumn(FIELD_CUSTOMER_INFO, getCustomerInfo(transaction));
			
		}
		
		sql.addColumn(FIELD_CUSTOMER_INFO, getCustomerInfo(transaction));
		sql.addColumn(FIELD_CUSTOMER_INFO_TYPE, getCustomerInfoType(transaction));
		sql.addColumn(FIELD_TRANSACTION_STATUS_CODE, getTransactionStatus(transaction));
		sql.addColumn(FIELD_TENDER_REPOSITORY_ID, getTillID(transaction));
		sql.addColumn(FIELD_TRANSACTION_POST_PROCESSING_STATUS_CODE, transaction.getPostProcessingStatus());
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_TRANSACTION_REENTRY_FLAG, getTransReentryFlag(transaction));
		sql.addColumn(FIELD_TRANSACTION_SALES_ASSOCIATE_MODIFIED, getSalesAssociateModifiedFlag(transaction));
		/*
		 * status desciption 0 in all condition rest of 1,2 1 request successfull, and
		 * capillary responded with generated coupons 2 request successfull, and
		 * capillary not generates coupons
		 */

		String capillaryRequestStats = "0";
		String MCouponStatusMessage = "";
		String submitinvfl = "N";

		if (transaction instanceof MAXSaleReturnTransaction) {
			MAXSaleReturnTransaction maxTransaction = (MAXSaleReturnTransaction) transaction;
			// System.out.println("maxTransaction.getSubmitinvresponse();"+maxTransaction.getSubmitinvresponse());
			if (maxTransaction.getMcouponList() != null && maxTransaction.getMcouponList().size() > 0) {
				capillaryRequestStats = "1";
			} else if (maxTransaction.getMcouponList() != null && maxTransaction.getMcouponList().size() == 0) {
				capillaryRequestStats = "2";
			}
			MCouponStatusMessage = maxTransaction.getMcouponStatusMessage();
			submitinvfl = maxTransaction.getSubmitinvresponse();
		}
		// System.out.println("Inside MAXjdbcSaveRetailTransaction");
		sql.addColumn(FIELD_CAPILLARY_REQ_STATUS, makeSafeString(capillaryRequestStats));
		sql.addColumn(FIELD_CAPILLARY_MESSAGE_STATUS, makeSafeString(MCouponStatusMessage));
		// sql.addColumn(FIELD_SUBMIT_INV_FLAG, makeSafeString(submitinvfl));
		if (submitinvfl != null) {
			sql.addColumn(FIELD_SUBMIT_INV_FLAG, makeSafeString(submitinvfl));
		} else {
			sql.addColumn(FIELD_SUBMIT_INV_FLAG, makeSafeString("N"));
		}
		try {
			// System.out.println("sql.getSQLString()"+sql.getSQLString().toString());
			dataConnection.execute(sql.getSQLString());

		} catch (DataException de) {
			logger.error(de.toString());
			throw de;
		} catch (Exception e) {
			logger.error(e.toString());
			throw new DataException(DataException.UNKNOWN, "insertTransaction", e);
		}

		// update transaction sequence number for register after each transaction.
		updateTransactionSequenceNumber(dataConnection, transaction);

	}

	public void saveCustomerTypePANDetails(JdbcDataConnection connection, MAXSaleReturnTransaction transaction)
			throws DataException {

		String panNum = "";
		String form60Num = "";
		String passportNo = "";
		String visaNum = "";
		String ackNum = "";
		MAXCustomerIfc customer = null;
		SQLInsertStatement sql = new SQLInsertStatement();
		sql.setTable(MAXARTSDatabaseIfc.TABLE_TRAN_PAN_DETAILS);

		sql.addColumn(MAXARTSDatabaseIfc.FIELD_ID_STR_RT, makeSafeString(transaction.getWorkstation().getStoreID()));
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_ID_WS, makeSafeString(transaction.getWorkstation().getWorkstationID()));
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_AI_TRN, getTransactionSequenceNumber(transaction));
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(transaction.getBusinessDay()));
		panNum = transaction.getPanNumber();
		customer = (MAXCustomerIfc) transaction.getCustomer();
		TransactionTotalsIfc totals = transaction.getTransactionTotals();
		if (panNum != null) {

			try {
				EncipheredDataIfc panData = FoundationObjectFactory.getFactory()
						.createEncipheredDataInstance(panNum.getBytes());
				String pan = panData.getEncryptedNumber().toString();
				// searchCriteria.setTaxID(taxData.getMaskedNumber());
				sql.addColumn(MAXARTSDatabaseIfc.FIELD_PAN_NUM, makeSafeString(pan));
			} catch (EncryptionServiceException ese) {
				logger.warn("could not encrypt pan no", ese);
			}

		}
		form60Num = transaction.getForm60IDNumber();
		if (form60Num != null) {
			sql.addColumn(MAXARTSDatabaseIfc.FIELD_FORM60_IDNUM, makeSafeString(form60Num));
		}
		passportNo = transaction.getPassportNumber();
		if (passportNo != null) {
			sql.addColumn(MAXARTSDatabaseIfc.FIELD_PASSPORT_NUM, makeSafeString(passportNo));
		}
		visaNum = transaction.getVisaNumber();
		if (visaNum != null) {
			sql.addColumn(MAXARTSDatabaseIfc.FIELD_VISA_NUM, makeSafeString(visaNum));
		}
		ackNum = transaction.getITRAckNumber();
		if (ackNum != null) {
			sql.addColumn(MAXARTSDatabaseIfc.FIELD_ITRACK_NUM, makeSafeString(ackNum));
		}
		if (customer != null) {
			sql.addColumn(FIELD_CUSTOMER_ID, makeSafeString(customer.getCustomerID()));
		}
		sql.addColumn(FIELD_TRANSACTION_NET_TOTAL, totals.getGrandTotal().getStringValue());
		sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

		try {
			logger.info("Save CustomerType L" + sql.getSQLString());
			connection.execute(sql.getSQLString(), false);
		} catch (DataException de) {
			logger.error(de);
			throw de;
		} catch (Exception e) {
			logger.error(e);
			throw new DataException(0, "CustomerType", e);
		}
	}

	// Changes starts for Rev 1.5

	private void getInvoiceLocationAndWrite(MAXSaleReturnTransactionIfc maxSaleReturn) {
		logger.info("TRYING TO SAVE THE RTF FILE12" + maxSaleReturn.getReceiptData());
		if (maxSaleReturn.getReceiptData() != null) {
			invoiceInput.put(MAXCodeConstantsIfc.STORE_ID, maxSaleReturn.getWorkstation().getStoreID());

			invoiceInput.put(MAXCodeConstantsIfc.GSTIN_AUTOMATION, 1);
			try {
				MAXGSTINAutomationTransaction gstinTransaction = (MAXGSTINAutomationTransaction) DataTransactionFactory
						.create(MAXDataTransactionKeys.GSTIN_INVOICE_AUTOMATION);
				logger.info("TRYING TO SAVE THE RTF FILE");
				outputData = gstinTransaction.getGstinCongiguration(invoiceInput);
				String strReceiptDirectory = (String) outputData.get(MAXGSTINConstantsIfc.RTF_LOCATION);
				logger.error(MAXGSTINConstantsIfc.RTF_LOCATION + " strReceiptDirectory " + strReceiptDirectory);

				String txnID = maxSaleReturn.getTransactionID();
				String folderDate = convertDateFolder(maxSaleReturn.getBusinessDay().asISODate());
				String eReceiptDirectory = strReceiptDirectory.concat((folderDate).concat("\\")
						.concat((txnID.substring(0, 5)).concat("\\").concat((txnID.substring(5, 8)).concat("\\"))));
				new File(eReceiptDirectory).mkdirs();
				logger.error("rtffile");
				File UIFile = new File(eReceiptDirectory + txnID + folderDate +"CUSTOMER COPY" + ".rtf");
				File UIFile1 = new File(eReceiptDirectory + txnID + folderDate +"TRANSPORTER COPY" + ".rtf");

				if (!UIFile.exists()) {
					UIFile.createNewFile();
				}
				FileUtils.writeByteArrayToFile(UIFile, maxSaleReturn.getReceiptData());
				//added by vaibhav
				FileWriter writer = new FileWriter(UIFile, true);
				writer.write("--CUSTOMER COPY--");
				writer.close();
				if (!UIFile1.exists()) {
					UIFile1.createNewFile();
				}
				FileUtils.writeByteArrayToFile(UIFile1, maxSaleReturn.getReceiptData());
				FileWriter writer1 = new FileWriter(UIFile1, true);
				writer1.write("--TRANSPORTER COPY==");
				writer1.close();
				
				
				logger.error("RTF fILE WRITTEN " + UIFile.getName());
				logger.error("RTF fILE WRITTEN " + UIFile1.getName());
				//Added by Vaibhav
				
				 File UIFile2 = new File(eReceiptDirectory + txnID + folderDate  + ".rtf");
				 if (!UIFile2.exists()) {
						UIFile2.createNewFile();
					}
			   	    String file1 = strReceiptDirectory+UIFile.getName(); 
				    String file2 = strReceiptDirectory+UIFile1.getName();
				    String outputFile = strReceiptDirectory+UIFile2.getName();
				    try {
			            // Read the contents of the first RTF file
			            StringBuilder content1 = new StringBuilder();
			            try (BufferedReader reader = new BufferedReader(new FileReader(file1))) {
			                String line;
			                while ((line = reader.readLine()) != null) {
			                    content1.append(line).append("\n");
			                }
			            }

			            // Read the contents of the second RTF file
			            StringBuilder content2 = new StringBuilder();
			            try (BufferedReader reader = new BufferedReader(new FileReader(file2))) {
			                String line;
			                while ((line = reader.readLine()) != null) {
			                    content2.append(line).append("\n");
			                }
			            }

			            // Concatenate the contents of the two files
			            StringBuilder combinedContent = new StringBuilder(content1.toString())
			                    .append(content2.toString());

			            // Write the combined content to the output file
			            try (BufferedWriter writer2 = new BufferedWriter(new FileWriter(outputFile))) {
			                writer2.write(combinedContent.toString());
			            }

			            System.out.println("RTF files concatenated successfully!");
			            logger.info("RTF fILE WRITTEN " + UIFile2.getName());
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
			    
			
								
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("getInvoiceLocationAndWrite " + e);
			}
		}
	}
	//added by vaibhav code for merging 2 rtf's
	
	
	//

	public String getRequestobject(MAXSaleReturnTransactionIfc transaction,
			MAXGSTINValidationResponseIfc storeGSTDetails, String stateCode, JdbcDataConnection connection)
			throws DataException, SQLException {

		SimpleDateFormat dm = new SimpleDateFormat("dd-MM-yyyy");

		SimpleDateFormat dmNew = new SimpleDateFormat("yy-MM-dd");
		transaction.getStoreGSTINNumber();
		SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[]) transaction.getLineItems();
		GSTInvoice invoice = new GSTInvoice();

		MAXGSTINValidationResponseIfc customerVatDetails = transaction.getGstinresp();

		// invoice.setLocationGstin(transaction.getStoreGSTINNumber());
		// invoice.setBillFromGstin(transaction.getStoreGSTINNumber());
		invoice.setLocationGstin(Gateway.getProperty("application", "store_gstin", ""));
		invoice.setBillFromGstin(Gateway.getProperty("application", "store_gstin", ""));
		invoice.setAutoPushOrGenerate("EINV");
		invoice.setDocumentDate(dm.format(new Date()));
		invoice.setPurpose("EINV");
		invoice.setSupplyType("s");
		if (transaction.getTransactionType() == 2) {
			invoice.setDocumentType("CRN");
		} else {
			invoice.setDocumentType("INV");
		}
		// invoice.setDocumentType("INV");
		invoice.setTransactionType("B2B");
		invoice.setBillToGstin(customerVatDetails.getGstin());
		invoice.setBillToLegalName(customerVatDetails.getLgnm());
		invoice.setBillToTradeName(customerVatDetails.getTradeNam());
		invoice.setBillToVendorCode(null);
		invoice.setBillToAddress1(customerVatDetails.getStj());
		invoice.setBillToAddress2(customerVatDetails.getBnm());
		invoice.setBillToCity(customerVatDetails.getLoc());
		invoice.setBillToStateCode(customerVatDetails.getGstin().substring(0, 2));
		invoice.setBillToPincode(customerVatDetails.getPncd());
		invoice.setPos(customerVatDetails.getGstin().substring(0, 2));	

		invoice.setPaymentDate(dm.format(new Date()));
		invoice.setPaymentRemarks("NA");
		invoice.setPaymentTerms("NA");
		invoice.setPaymentInstruction("NA");
		invoice.setBillFromLegalName(storeGSTDetails.getLgnm());
		invoice.setBillFromTradeName(storeGSTDetails.getTradeNam());
		invoice.setBillFromAddress1(storeGSTDetails.getStj());
		invoice.setBillFromAddress2(storeGSTDetails.getSt());
		invoice.setBillFromCity(storeGSTDetails.getLoc());
		invoice.setOriginalTaxableValue(null);	

		invoice.setBillFromStateCode(String.format("%02d", Integer.parseInt(stateCode)));
		invoice.setBillFromPincode(storeGSTDetails.getPncd());

		invoice.setDocumentNumber(transaction.getTransactionID());
		CurrencyIfc invoiceTotal = DomainGateway.getBaseCurrencyInstance();
		for (SaleReturnLineItemIfc item : items) {
			GSTInvoiceItem invItem = new GSTInvoiceItem();

			invItem.setBarcode(item.getItemID());
			
			invItem.setSerialNumber(item.getLineNumber()+"");
			invItem.setItemDescription(item.getPLUItem().getItemID());
			invItem.setUqc("BOX");
			invItem.setQuantity(String.valueOf(item.getItemQuantity()));
			
			invItem.setLossUnitOfMeasure("BOX");
			
			
			 CurrencyIfc priceperQuantity =item.getExtendedDiscountedSellingPrice().divide(DomainGateway.getBaseCurrencyInstance(item.getItemQuantity().toString()));
			  
			 
			 

			MAXLineItemTaxBreakUpDetailIfc[] lineItemBreakUpDetails = ((MAXItemTaxIfc) (item.getItemPrice()
					.getItemTax())).getLineItemTaxBreakUpDetail();
			double taxRate = 0;
			double stateRate = 0;
			CurrencyIfc taxableAmt = DomainGateway.getBaseCurrencyInstance();
			CurrencyIfc discountAmt = DomainGateway.getBaseCurrencyInstance();
			CurrencyIfc igstAmt = DomainGateway.getBaseCurrencyInstance();
			CurrencyIfc cgstAmt = DomainGateway.getBaseCurrencyInstance();
			CurrencyIfc sgstAmt = DomainGateway.getBaseCurrencyInstance();
			CurrencyIfc cessAmt = DomainGateway.getBaseCurrencyInstance();
			CurrencyIfc stateCessAmt = DomainGateway.getBaseCurrencyInstance();
			CurrencyIfc Rate = DomainGateway.getBaseCurrencyInstance();
			
			boolean igstFlag = false;
			for (int i = 0; i < lineItemBreakUpDetails.length; i++) {
				MAXLineItemTaxBreakUpDetailIfc breakupDetails = lineItemBreakUpDetails[i];
				taxableAmt = breakupDetails.getTaxableAmount().abs();
				MAXTaxAssignmentIfc taxAssignment = breakupDetails.getTaxAssignment();
				if (taxAssignment.getTaxCodeDescription() != null
						&& taxAssignment.getTaxCodeDescription().toUpperCase().contains("CGST")) {
					taxRate = taxRate + Double.parseDouble(breakupDetails.getTaxRate().trim());
					cgstAmt = breakupDetails.getTaxAmount().abs();
				} else if (taxAssignment.getTaxCodeDescription() != null
						&& (taxAssignment.getTaxCodeDescription().toUpperCase().contains("SGST")
								|| taxAssignment.getTaxCodeDescription().toUpperCase().contains("UTGST"))) {
					taxRate = taxRate + Double.parseDouble(breakupDetails.getTaxRate().trim());
					sgstAmt = breakupDetails.getTaxAmount().abs();
				} else if (taxAssignment.getTaxCodeDescription() != null
						&& taxAssignment.getTaxCodeDescription().toUpperCase().contains("IGST")) {
					taxRate = taxRate + Double.parseDouble(breakupDetails.getTaxRate().trim());
					igstAmt = breakupDetails.getTaxAmount().abs();
					igstFlag = true;
				} else if (taxAssignment.getTaxCodeDescription() != null
						&& taxAssignment.getTaxCodeDescription().toUpperCase().contains("CESS")) {
					stateRate = Double.parseDouble(breakupDetails.getTaxRate().trim());
					if (stateRate > 0) {
						stateCessAmt = breakupDetails.getTaxAmount().abs();
					}
				}
			}
			/*
			 * invItem.setRate(String.valueOf(taxRate)); invItem.setCessRate("");
			 * if(stateRate > 0) { invItem.setStateCessRate(String.valueOf(stateRate));
			 * }else { invItem.setStateCessRate(""); } invItem.setCessNonAdvaloremRate("");
			 */
			  
			 
											 
			 
			invItem.setPreTaxValue(null);
			//invItem.setHsn("48201010"); 
			if(item.getPLUItem()  instanceof MAXPLUItemIfc) {
				String hsnNo = ((MAXPLUItemIfc) item.getPLUItem()).getHsnNum();
				if(hsnNo != null && hsnNo.length()>2 && hsnNo.substring(0, 2).equals("99")) {
					invItem.setIsService("Y");
				}else {
					invItem.setIsService("N");								
				}
				if (hsnNo.length()==7 ||hsnNo.length()==5) {
					hsnNo="0"+hsnNo;
					System.out.println("hsnno" + hsnNo);
				}
				invItem.setHsn(hsnNo);
			}else
				if(item.getPLUItem()  instanceof MAXGiftCardPLUItem) {
					invItem.setHsn(((MAXGiftCardPLUItem) item.getPLUItem()).getHsnNum());
					invItem.setIsService("N");
				}

			invItem.setProductCode(item.getPLUItem().getItemID());
			
			  if(cgstAmt.compareTo(DomainGateway.getBaseCurrencyInstance()) !=0) {
			  invItem.setCgstAmount(cgstAmt.getDecimalValue().setScale(2,BigDecimal.ROUND_HALF_UP).abs().toString()); }
			  else { 
			  if(!igstFlag && (taxRate ==0 ||cgstAmt.getDecimalValue().compareTo(DomainGateway.getBaseCurrencyInstance().getDecimalValue()) ==0)) 
			  { invItem.setCgstAmount("0.00"); 
			  }else {
			  invItem.setCgstAmount("");
			  } }
			  if(sgstAmt.compareTo(DomainGateway.getBaseCurrencyInstance()) !=0) {
			  invItem.setSgstAmount(sgstAmt.getDecimalValue().setScale(2,BigDecimal.ROUND_HALF_UP).abs().toString()); }
			  else { 
			 if(!igstFlag && (taxRate==0 ||sgstAmt.getDecimalValue().compareTo(DomainGateway.getBaseCurrencyInstance().getDecimalValue()) ==0)) 
			 { invItem.setSgstAmount("0.00");
			 }else {
			  invItem.setSgstAmount(""); 
			  } }
			 
			invItem.setCessAmount("");
			if (stateCessAmt.compareTo(DomainGateway.getBaseCurrencyInstance()) != 0) {
				invItem.setStateCessAmount(stateCessAmt.getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP).abs().toString());
			} else {
				invItem.setStateCessAmount("");
			}
			
			 invItem.setDiscountAmount("0");
			   CurrencyIfc lineItemPrice = item.getExtendedDiscountedSellingPrice().subtract(igstAmt).subtract(cgstAmt).subtract(sgstAmt).subtract(cessAmt).subtract(stateCessAmt)
						.add(discountAmt);
			   System.out.println("lineItemPrice"+lineItemPrice.toString() +"test"+item.getItemTax().getTaxableAmount());
			   logger.warn("lineItemPrice"+lineItemPrice.toString() +"test"+item.getItemTax().getTaxableAmount());
			   invItem.setPricePerQuantity(priceperQuantity.toString());
			   invItem.setGrossAmount(lineItemPrice.toString());
			   invItem.setOtherCharges(null); 
			   invItem.setTaxableValue(lineItemPrice.toString());
			CurrencyIfc total = lineItemPrice.add(igstAmt).add(cgstAmt).add(sgstAmt).add(cessAmt).add(stateCessAmt)
					.subtract(discountAmt);
			
			invoiceTotal = invoiceTotal.add(total);

			invItem.setStateCessNonAdvaloremAmount("");
			invItem.setCessNonAdvaloremAmount("");
			invItem.setOrderLineReference(null);
			invItem.setOriginCountry(null);
			invItem.setItemSerialNumber(null);
			invItem.setItemTotal(total.getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP).abs().toString());
			invItem.setItemAttributeDetails(null);
			invItem.setTaxType(null);
			invItem.setBatchNameNumber(null);
			invItem.setBatchExpiryDate(null);
			invItem.setWarrantyDate(null);
			invItem.setItcEligibility(null);
			invItem.setItcIgstAmount(null);
			invItem.setItcCgstAmount(null);
			invItem.setItcSgstAmount(null);
			invItem.setItcCessAmount(null);
			invItem.setCustomItem1(null);
			invItem.setCustomItem2(null);
			invItem.setCustomItem3(null);
			invItem.setCustomItem4(null);
			invItem.setCustomItem5(null);
			invItem.setCustomItem6(null);
			invItem.setCustomItem7(null);
			invItem.setCustomItem8(null);
			invItem.setCustomItem9(null);
			invItem.setCustomItem10(null);
			invItem.setDocumentValue(invoiceTotal.getDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP).abs().toString());					
			// items.add(invItem);

			MAXLineItemTaxBreakUpDetailIfc[] taxInfos = ((MAXItemTax) item.getItemTax()).getLineItemTaxBreakUpDetail();
			BigDecimal rate = new BigDecimal("0.00");
			for (MAXLineItemTaxBreakUpDetailIfc taxInfo : taxInfos) {

				rate = rate.add(new BigDecimal(taxInfo.getTaxRate()));

			}
			invItem.setRate(rate.toString());

			invoice.getItems().add(invItem);
		}

		GSTSaveRequest request = new GSTSaveRequest();

		request.getData().add(invoice);
		Gson gson = new GsonBuilder().serializeNulls().create();
		logger.error(gson.toJson(request));

		return gson.toJson(request);

	}

	private String getStoreCode(String stateName, JdbcDataConnection connection) throws SQLException, DataException {

		SQLSelectStatement sql = new SQLSelectStatement();
		sql.setTable(TABLE_GSTIN_STATE_MASTER);
		logger.error("gstinstatemaster");

		sql.addColumn(FIELD_STATE_CODE);
		logger.warn("STSTE_NAME=====================================" + stateName);

		sql.addQualifier(FIELD_STATE_NAME, makeSafeString(stateName.toUpperCase()));

		connection.execute(sql.getSQLString());
		logger.warn(sql + "QUERY===========================");

		ResultSet rs = (ResultSet) connection.getResult();
		logger.error(rs);
		String code = "";
		if (rs.next()) {
			code = rs.getString(1);
		}

		return code;

	}

	private MAXGSTINValidationResponseIfc getStoreGSTINDetails(JdbcDataConnection connection, String storeGSTIN,
			String storeID) {

		MAXGSTINValidationResponseIfc response = new MAXGSTINValidationResponse();
		logger.warn(response + "response is here ");
		/// HashMap<String, String> map = new HashMap<String, String>();
		ResultSet rs = null;
		try {

			String query = "select * from GSTIN_STORE_DETAILS where ID_STR_RT= '" + storeID + "' and STORE_GSTIN= '"
					+ storeGSTIN + "'";/// not correct for now its ok better use prepared statemnt
			
			connection.execute(query);
			rs = (ResultSet) connection.getResult();
			if (rs.next()) {
				response.setGstin(storeGSTIN);

				response.setLgnm(rs.getString(FIELD_LEGAL_NAME));
				response.setStj(rs.getString(FIELD_STATE_JURISDICTION_CD));
				response.setDty(rs.getString(FIELD_TAXPAYER_TYPE));
				response.setCxdt(rs.getString(FIELD_DATE_OF_CANCEL));
				response.setBnm(rs.getString(FIELD_BUILDING_NM));
				response.setSt(rs.getString(FIELD_STREET));
				response.setLoc(rs.getString(FIELD_LOCALITY));
				response.setBno(rs.getString(FIELD_BUILDING_NO));
				response.setStcd(rs.getString(FIELD_STATE));
				response.setCity(rs.getString(FIELD_CITY));
				response.setDst(rs.getString(FIELD_DISTRICT));
				response.setFlno(rs.getString(FIELD_FLOOR_NO));
				response.setLt(rs.getString(FIELD_LATITUTE));
				response.setPncd(rs.getString(FIELD_PIN_CODE));
				response.setLg(rs.getString(FIELD_LONGTITUTE));
				response.setLstupdt(rs.getString(FIELD_LAST_UPDATED));
				response.setRgdt(rs.getString(FIELD_REGISTRATION_DATE));
				response.setCtb(rs.getString(FIELD_BUSINESS_CONSTITUTION));
				response.setSts(rs.getString(FIELD_GSTN_STATUS));
				response.setCtjCd(rs.getString(FIELD_CENTR_JURISDICTION_CD));
				response.setCtj(rs.getString(FIELD_CENTR_JURISDICTION_NM));
				response.setTradeNam(rs.getString(FIELD_REGISTR_TRADE_NAME));

			} else {
				response.setGstin(storeGSTIN);
			}

		} catch (Exception exception) {
			logger.error("Couldn't save retail transaction.");
			logger.error("" + exception + "");
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.warn(e);
			}
		}
		return response;
	}

	private void saveEInvoiceGSTNumber(JdbcDataConnection dataConnection, MAXSaleReturnTransactionIfc maxSaleReturn)
			throws DataException {

		try {
			String storegstin = null;

			if (maxSaleReturn.getStoreGSTINNumber() != null
					&& maxSaleReturn.getStoreGSTINNumber().equalsIgnoreCase(" ")) {
				storegstin = maxSaleReturn.getStoreGSTINNumber();
				logger.warn(storegstin + "EFGDFG===================");

			} else {
				storegstin = Gateway.getProperty("application", "store_gstin", "");
				logger.warn(storegstin + "newchanges---------------------");

			}

			// MAXGSTINValidationResponseIfc storeGSTDetails =
			// getStoreGSTINDetails(dataConnection,
			// maxSaleReturn.getStoreGSTINNumber(),
			// maxSaleReturn.getWorkstation().getStoreID());
			MAXGSTINValidationResponseIfc storeGSTDetails = getStoreGSTINDetails(dataConnection, storegstin,
					maxSaleReturn.getWorkstation().getStoreID());
			logger.warn("saveEInvoiceGSTNumber: " + storegstin);
			logger.info("saveEInvoiceGSTNumber: " + storeGSTDetails.getGstin());
			String storeCode = null;
			if (storeGSTDetails.getStcd() != null) {
				storeCode = getStoreCode(storeGSTDetails.getStcd(), dataConnection);
			} else {
				storeCode =getStoreID(maxSaleReturn);
				logger.warn(storeCode + "======================================================");
			}
			String storegstin1 = null;
			logger.warn("getStoreCode: " + storeCode);
			logger.info("getStoreCode: " + storeCode);
			String request = getRequestobject(maxSaleReturn, storeGSTDetails, storeCode, dataConnection);
			logger.error("gstinquerry");

			String query = "INSERT INTO GSTIN_E_INVOICE_DETAILS \r\n"
					+ "( ID_STR_RT, ID_WS, AI_TRN, DC_DY_BSN, TY_TRN, SC_TRN, CUST_GSTIN, STORE_GSTIN, CO_TRANSFER_STATUS, GET_INVOICE_STATUS, INVOICE_REQUEST) VALUES \r\n"
					+ "(?,?,?,?,?,?,?,?,?,?,?)";
			logger.warn("connection initilization");
			PreparedStatement ps = dataConnection.getConnection().prepareStatement(query);
			
			ps.setString(1, maxSaleReturn.getWorkstation().getStoreID());
			logger.warn(getStoreID(maxSaleReturn));

			logger.info(getStoreID(maxSaleReturn));
			ps.setString(2, maxSaleReturn.getWorkstation().getWorkstationID());

			ps.setString(3, getTransactionSequenceNumber(maxSaleReturn));

			ps.setString(4, maxSaleReturn.getBusinessDay().asISODate());
			ps.setInt(5, maxSaleReturn.getTransactionType());
			ps.setInt(6, maxSaleReturn.getTransactionStatus());

			ps.setString(7, maxSaleReturn.getGSTINNumber());
			if (maxSaleReturn.getStoreGSTINNumber() != null
					&& maxSaleReturn.getStoreGSTINNumber().equalsIgnoreCase(" ")) {
				storegstin1 = maxSaleReturn.getStoreGSTINNumber();
				logger.warn(storegstin1 + "EFGDFG===================");

			} else {
				storegstin1 = Gateway.getProperty("application", "store_gstin", "");
				logger.warn(storegstin1 + "newchanges---------------------");

			}
			ps.setString(8, storegstin1);
			logger.warn(storegstin1 + "dfsfsds----------------");
			ps.setInt(9, 0);
			ps.setInt(10, 0);

			Clob clob = dataConnection.getConnection().createClob();
			clob.setString(1, request);
			ps.setClob(11, clob);
			ps.execute();

		} catch (Exception e) {
			logger.error(e);/// this prints in log file
			e.printStackTrace();/// this means prints on console

		}

	}
	protected void insertCustomerGSTINDetails(JdbcDataConnection connection, MAXGSTINValidationResponseIfc response,
            TenderableTransactionIfc transaction) throws DataException {



       SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_GSTIN_CUSTOMER_DETAILS);
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_TRANSACTION_TYPE_CODE, getTransactionType(transaction));
        sql.addColumn(FIELD_INVOICE_TO_GSTIN, makeSafeString(response.getGstin()));
        sql.addColumn(FIELD_LEGAL_NAME, makeSafeString(response.getLgnm()));
        sql.addColumn(FIELD_STATE_JURISDICTION_CD, makeSafeString(response.getStj()));
        sql.addColumn(FIELD_TAXPAYER_TYPE, makeSafeString(response.getDty()));
        sql.addColumn(FIELD_DATE_OF_CANCEL, makeSafeString(response.getCxdt()));



       sql.addColumn(FIELD_BUILDING_NM, makeSafeString(response.getBnm()));
        sql.addColumn(FIELD_STREET, makeSafeString(response.getSt()));
        sql.addColumn(FIELD_LOCALITY, makeSafeString(response.getLoc()));
        sql.addColumn(FIELD_BUILDING_NO, makeSafeString(response.getBno()));
        sql.addColumn(FIELD_STATE, makeSafeString(response.getStcd()));



       sql.addColumn(FIELD_CITY, makeSafeString(response.getCity()));
        sql.addColumn(FIELD_DISTRICT, makeSafeString(response.getDst()));            
        sql.addColumn(FIELD_FLOOR_NO, makeSafeString(response.getFlno()));
        sql.addColumn(FIELD_LATITUTE, makeSafeString(response.getLt()));
        sql.addColumn(FIELD_PIN_CODE, makeSafeString(response.getPncd()));
        sql.addColumn(FIELD_LONGTITUTE, makeSafeString(response.getLg()));




        sql.addColumn(FIELD_LAST_UPDATED, makeSafeString(response.getLstupdt()));
        sql.addColumn(FIELD_REGISTRATION_DATE, makeSafeString(response.getRgdt()));
        sql.addColumn(FIELD_BUSINESS_CONSTITUTION, makeSafeString(response.getCtb()));
        sql.addColumn(FIELD_GSTN_STATUS, makeSafeString(response.getSts()));
        sql.addColumn(FIELD_CENTR_JURISDICTION_CD, makeSafeString(response.getCtjCd()));
        sql.addColumn(FIELD_CENTR_JURISDICTION_NM, makeSafeString(response.getCtj()));
        sql.addColumn(FIELD_REGISTR_TRADE_NAME, makeSafeString(response.getTradeNam()));



       sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                getSQLCurrentTimestampFunction());
        try {
            logger.info(sql.getSQLString());
            connection.execute(sql.getSQLString());
        } catch (DataException de) {
            logger.error("" + de + "");
            throw de;
        } catch (Exception e) {
            logger.error("Exception during saving request response Youth Card"
                    + e);
            throw new DataException(DataException.UNKNOWN);
        }
    }

	public static String convertDateFolder(String stringData) {

		String businessDate = null;

		Date initDate;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			df.setLenient(false);
			initDate = df.parse(stringData);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			businessDate = formatter.format(initDate);
		} catch (Exception e) {
			logger.error(e);
		}

		return businessDate;

	}

}
