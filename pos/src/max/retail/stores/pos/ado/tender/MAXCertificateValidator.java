/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.1     Mar 30, 2017            Nitika Arora        Changes for setting the Redeem status in case lookup from local db.
 *  Rev 1.0     Dec 19, 2016	        Ashish Yadav		Changes for StoreCredit FES
 *
 ********************************************************************************/

package max.retail.stores.pos.ado.tender;

import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXCertificateTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.tender.MAXTenderStoreCredit;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.CertificateTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificate;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderStoreCredit;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.pos.ado.tender.CertificateValidator;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;

public class MAXCertificateValidator extends CertificateValidator implements MAXCertificateValidatorIfc {

	static Logger logger = Logger.getLogger(max.retail.stores.pos.ado.tender.MAXCertificateValidator.class);
	/**
	 * No-arg constructor Note: the constructor is protected by design.
	 */
	protected MAXCertificateValidator() {

	}

	/**
	 * Note: the constructor is protected by design.
	 *
	 * @param tender
	 */
	protected MAXCertificateValidator(TenderCertificateIfc tender) {

		super(tender);
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns Amount.
	 *
	 * @return Amount CurrencyIfc
	 */
	// ----------------------------------------------------------------------
	@Override
	public CurrencyIfc getAmount() {
		return ((TenderStoreCredit) tenderRDO).getAmount();
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns ExpirationDate.
	 *
	 * @return ExpirationDate EYSDate
	 */
	// ----------------------------------------------------------------------
	@Override
	public EYSDate getExpirationDate() {
		return ((TenderStoreCredit) tenderRDO).getStoreCredit().getExpirationDate();
	}
	//akanksha for bug_9576
	@Override
	public String getStoreCreditStatus() {

		return ((MAXTenderStoreCredit) tenderRDO).getStoreCreditStatus();
	}
	//akanksha


	//Changes for Rev 1.0:Aakash(EYLLP):Starts
	/**
	 * LSPIPL vesrion of super MAXCertificateValidator#lookupCertificate().
	 *
	 * @throws TenderException
	 *             thrown when certificate not found.
	 */
	@Override
	public void lookupCertificate() throws TenderException
	{
		if(logger.isInfoEnabled())
		{
			logger.info("Looking up ceritifcate...");
		}

		MAXCertificateTransaction dataTransaction = null;

		dataTransaction = (MAXCertificateTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.MAXCERTIFICATE_TRANSACTION);

		if(!transactionReentryMode)
		{
			try
			{
				if(tenderRDO instanceof TenderGiftCertificate)
					throw new TenderException("ValidationOffline", TenderErrorCodeEnum.VALIDATION_OFFLINE, null);
				else	
					tenderRDO = dataTransaction.readCertificate(tenderRDO);
				/*tenderRDO = dataTransaction.readCertificate(tenderRDO);*/
				if (tenderRDO.isRedeemed())
				{
					throw new TenderException("Certificate Tendered", TenderErrorCodeEnum.CERTIFICATE_TENDERED, tenderRDO);
					
				}

				if (tenderRDO.getPostVoided())
				{
					throw new TenderException("Certificate Voided", TenderErrorCodeEnum.CERTIFICATE_VOIDED, tenderRDO);
				}

				CurrencyIfc faceValue = null;
				CurrencyIfc currencyTendered = null;
				if (tenderRDO instanceof TenderGiftCertificateIfc)
				{
					faceValue = ((TenderGiftCertificateIfc)tenderRDO).getDocument().getAmount();
					currencyTendered = ((TenderGiftCertificateIfc)tenderRDO).getAlternateCurrencyTendered();
					if (currencyTendered == null)
					{
						currencyTendered = tenderRDO.getAmountTender();
					}
				}
				else
					if (tenderRDO instanceof TenderStoreCreditIfc)
					{
						faceValue = ((TenderStoreCreditIfc)tenderRDO).getStoreCredit().getAmount();
						currencyTendered = ((TenderStoreCreditIfc)tenderRDO).getAlternateCurrencyTendered();
						if (currencyTendered == null)
						{
							currencyTendered = tenderRDO.getAmountTender();
						}
					}

				if (faceValue != null)
				{
					// The face value and the requested tender type must be the same
					if (!(currencyTendered.getType().getCurrencyCode().equals(faceValue.getType().getCurrencyCode())))
					{
						throw new TenderException("Certificate Invalid Currency", TenderErrorCodeEnum.INVALID_CURRENCY, tenderRDO);
					}

					// If the face value is same currency as the base currency, set it directly on the amount tender.
					if (tenderRDO.getAmountTender().getType().getCurrencyCode().equals(faceValue.getType().getCurrencyCode()))
					{
						tenderRDO.setAmountTender(faceValue);
					}
					else
					{
						// If it is not the same currency, set the alternate currency amount.
						((TenderAlternateCurrencyIfc)tenderRDO).setAlternateCurrencyTendered(faceValue);
						tenderRDO.setAmountTender(DomainGateway.convertToBase(faceValue));
					}
				}
			}
			catch (DataException de)
			{
				if (de.getErrorCode() == DataException.CONNECTION_ERROR)
				{
					throw new TenderException("ValidationOffline", TenderErrorCodeEnum.VALIDATION_OFFLINE, de);
				}
				else
				{
					MAXCertificateTransaction dataTransaction1 = null;

					dataTransaction1 = (MAXCertificateTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.MAXCERTIFICATE_TRANSACTION);

					if(!transactionReentryMode)
					{
						try
						{
							tenderRDO = dataTransaction1.readCertificatefromlocal(tenderRDO);
							if (tenderRDO.isRedeemed())
							{
								((MAXTenderStoreCredit)tenderRDO).setStoreCreditStatus("REDEEM");
								throw new TenderException("Certificate Tendered", TenderErrorCodeEnum.CERTIFICATE_TENDERED, tenderRDO);
								
							}

							if (tenderRDO.getPostVoided())
							{
								throw new TenderException("Certificate Voided", TenderErrorCodeEnum.CERTIFICATE_VOIDED, tenderRDO);
							}

							CurrencyIfc faceValue = null;
							CurrencyIfc currencyTendered = null;
							if (tenderRDO instanceof TenderGiftCertificateIfc)
							{
								faceValue = ((TenderGiftCertificateIfc)tenderRDO).getDocument().getAmount();
								currencyTendered = ((TenderGiftCertificateIfc)tenderRDO).getAlternateCurrencyTendered();
								if (currencyTendered == null)
								{
									currencyTendered = tenderRDO.getAmountTender();
								}
							}
							else
								if (tenderRDO instanceof TenderStoreCreditIfc)
								{
									faceValue = ((TenderStoreCreditIfc)tenderRDO).getStoreCredit().getAmount();
									currencyTendered = ((TenderStoreCreditIfc)tenderRDO).getAlternateCurrencyTendered();
									if (currencyTendered == null)
									{
										currencyTendered = tenderRDO.getAmountTender();
									}
									
								}

							if (faceValue != null)
							{
								// The face value and the requested tender type must be the same
								if (!(currencyTendered.getType().getCurrencyCode().equals(faceValue.getType().getCurrencyCode())))
								{
									throw new TenderException("Certificate Invalid Currency", TenderErrorCodeEnum.INVALID_CURRENCY, tenderRDO);
								}

								// If the face value is same currency as the base currency, set it directly on the amount tender.
								if (tenderRDO.getAmountTender().getType().getCurrencyCode().equals(faceValue.getType().getCurrencyCode()))
								{
									tenderRDO.setAmountTender(faceValue);
								}
								else
								{
									// If it is not the same currency, set the alternate currency amount.
									((TenderAlternateCurrencyIfc)tenderRDO).setAlternateCurrencyTendered(faceValue);
									tenderRDO.setAmountTender(DomainGateway.convertToBase(faceValue));
								}
							}
						}
						catch (DataException de1)
						{	
							((MAXTenderStoreCreditIfc)tenderRDO).setError(de1);
							if(de1.getErrorCode() == DataException.NO_DATA)
							{
								((MAXTenderStoreCreditIfc)tenderRDO).setStoreCreditStatus("NO_DATA");
							}
						else if (de1.getErrorCode() == DataException.CONNECTION_ERROR)
							{
								throw new TenderException("ValidationOffline", TenderErrorCodeEnum.VALIDATION_OFFLINE, de1);
							}
							else
							{
								throw new TenderException("Invalid certificate", TenderErrorCodeEnum.INVALID_CERTIFICATE, de1);
							}
						}
						catch (Exception e)
						{
							logger.error("Error in local lookup", e);
							if (e instanceof TenderException)
							{
								logger.error("Error in local lookup", e);
								throw (TenderException)e;
							}
							throw new TenderException("Invalid certificate", TenderErrorCodeEnum.INVALID_CERTIFICATE, e);
						}
				}
			}
			}
			catch (Exception e)
			{
				if (e instanceof TenderException)
				{
					throw (TenderException)e;
				}
				throw new TenderException("Invalid certificate", TenderErrorCodeEnum.INVALID_CERTIFICATE, e);
			}
		}
	}
	//Changes for Rev 1.0:Aakash(EYLLP):Ends
}
