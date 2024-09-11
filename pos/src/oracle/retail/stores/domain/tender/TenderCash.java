package oracle.retail.stores.domain.tender;

import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.utility.I18NHelper;
import org.apache.log4j.Logger;

import max.retail.stores.pos.services.tender.MAXTenderCash;

public class TenderCash extends AbstractTenderLineItem implements TenderCashIfc, TenderAlternateCurrencyIfc {
	static final long serialVersionUID = 5759286850073653057L;
	public static final String revisionNumber = "$Revision: /main/14 $";
	protected CurrencyIfc alternateCurrencyTendered;
	private transient Logger myLogger;
	
	public boolean isEWalletTenderType;

	public TenderCash() {
		this.alternateCurrencyTendered = null;
		this.myLogger = Logger.getLogger(TenderCash.class);
		this.typeCode = 0;
		this.setHasDenominations(true);
	}

	public TenderCash(CurrencyIfc tender) {
		this();
		this.setAmountTender(tender);
	}

	public Object clone() {
		TenderCash tc = new TenderCash();
		this.setCloneAttributes(tc);
		return tc;
	}

	protected void setCloneAttributes(TenderCash newClass) {
		super.setCloneAttributes(newClass);
		if (this.getAlternateCurrencyTendered() != null) {
			newClass.setAlternateCurrencyTendered((CurrencyIfc) this.getAlternateCurrencyTendered().clone());
		}
		//System.out.println("isEWalletTenderType() :"+isEWalletTenderType());
		 if (isEWalletTenderType())
		 {
			    newClass.setEWalletTenderType(isEWalletTenderType());
			    newClass.setTypeCode(typeCode);
			    //((CurrencyIfc)getAlternateCurrencyTendered().clone());
			    }

	}
	

	public int checkLimitsForSale() throws IllegalStateException {
		int errorCode = 0;
		if (this.getTenderLimits() == null) {
			if (this.myLogger.isDebugEnabled()) {
				this.myLogger.debug("TenderLimits were not initialized.");
			}

			throw new IllegalStateException("TenderLimits were not initialized.");
		} else {
			CurrencyIfc limitValue = null;
			limitValue = this.getTenderLimits().getCurrencyLimit("MaximumCashAccepted");
			if (!limitValue.equals(TenderLimits.getTenderNoLimitAmount())
					&& this.amountTender.compareTo(limitValue) == 1) {
				if (this.myLogger.isDebugEnabled()) {
					this.myLogger.debug("Maximum cash accepted amount exceeded.");
				}

				errorCode = 1;
			}

			return errorCode;
		}
	}

	public int checkLimitsForReturn(boolean hasReceipt) throws IllegalStateException {
		int errorCode = 0;
		if (this.getTenderLimits() == null) {
			if (this.myLogger.isDebugEnabled()) {
				this.myLogger.debug("TenderLimits were not initialized.");
			}

			throw new IllegalStateException("TenderLimits were not initialized.");
		} else {
			CurrencyIfc limitValue = null;
			CurrencyIfc cmpValue = this.amountTender.negate();
			if (hasReceipt) {
				limitValue = this.getTenderLimits().getCurrencyLimit("MaximumCashRefund");
				if (!limitValue.equals(TenderLimits.getTenderNoLimitAmount()) && cmpValue.compareTo(limitValue) == 1) {
					if (this.myLogger.isDebugEnabled()) {
						this.myLogger.debug("Maximum cash refund exceeded.");
					}

					errorCode = 4;
				}
			} else {
				limitValue = this.getTenderLimits().getCurrencyLimit("MaximumCashRefundWithoutReceipt");
				if (!limitValue.equals(TenderLimits.getTenderNoLimitAmount()) && cmpValue.compareTo(limitValue) == 1) {
					if (this.myLogger.isDebugEnabled()) {
						this.myLogger.debug("Maximum cash refund without receipt exceeded.");
					}

					errorCode = 5;
				}
			}

			return errorCode;
		}
	}

	public CurrencyIfc getAmountMaximumChange() throws IllegalStateException {
		if (this.getTenderLimits() == null) {
			if (this.myLogger.isDebugEnabled()) {
				this.myLogger.debug("TenderLimits were not initialized.");
			}

			throw new IllegalStateException("TenderLimits were not initialized.");
		} else {
			return this.getTenderLimits().getCurrencyLimit("MaximumCashChange");
		}
	}

	public CurrencyIfc getAmountMaximumRefund() throws IllegalStateException {
		if (this.getTenderLimits() == null) {
			if (this.myLogger.isDebugEnabled()) {
				this.myLogger.debug("TenderLimits were not initialized.");
			}

			throw new IllegalStateException("TenderLimits were not initialized.");
		} else {
			return this.getTenderLimits().getCurrencyLimit("MaximumCashRefund");
		}
	}

	public CurrencyIfc getAlternateCurrencyTendered() {
		return this.alternateCurrencyTendered;
	}

	public void setAlternateCurrencyTendered(CurrencyIfc value) {
		this.alternateCurrencyTendered = value;
	}

	public String toJournalString(Locale journalLocale) {
		String journalString = this.abstractTenderLineItemAttributesToJournalString(journalLocale);
		StringBuffer sb = new StringBuffer();
		sb.append(journalString);
		String transTypeString;
		if (this.amountTender.signum() == -1) {
			transTypeString = JOURNAL_REVERSED;
		} else {
			transTypeString = JOURNAL_RECEIVED;
		}

		if (this.alternateCurrencyTendered != null) {
			Object[] dataArgs = new Object[]{this.getAlternateCurrencyTendered().getCurrencyCode(), transTypeString};
			sb.append(Util.EOL)
					.append(I18NHelper.getString("EJournal", "JournalEntry.TenderCashAmountLabel", dataArgs,
							journalLocale))
					.append(Util.EOL).append(this.amountTender
							.divide(this.getAlternateCurrencyTendered().getBaseConversionRate()).toISOFormattedString())
					.append(Util.EOL);
			Object[] dataArgs2 = new Object[]{this.alternateCurrencyTendered.getBaseConversionRate()};
			sb.append(I18NHelper.getString("EJournal", "JournalEntry.ExchangeRateLabel", dataArgs2, journalLocale));
		}

		return sb.toString();
	}

	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof TenderCash) {
			TenderCash c = (TenderCash) obj;
			if (super.equals(obj)
					&& Util.isObjectEqual(this.getAlternateCurrencyTendered(), c.getAlternateCurrencyTendered())) {
				isEqual = true;
			}
		}

		return isEqual;
	}

	public String toString() {
		StringBuffer strResult = new StringBuffer("Class:  TenderCash (Revision ");
		strResult.append(this.getRevisionNumber()).append(") @").append(this.hashCode()).append(Util.EOL)
				.append("alternate currency tendered:  [");
		if (this.getAlternateCurrencyTendered() == null) {
			strResult.append("null]").append(Util.EOL);
		} else {
			strResult.append(this.getAlternateCurrencyTendered()).append("]").append(Util.EOL)
					.append("alternate currency type:      [")
					.append(this.getAlternateCurrencyTendered().getCurrencyCode()).append("]").append(Util.EOL);
		}

		strResult.append(this.abstractTenderLineItemAttributesToString());
		return strResult.toString();
	}

	public String getRevisionNumber() {
		return "$Revision: /main/14 $";
	}

	public static void main(String[] args) {
		TenderCash c = new TenderCash(DomainGateway.getBaseCurrencyInstance("45.00"));
		TenderLimitsIfc t = new TenderLimits();
		t.setCurrencyLimit("MaximumCashAccepted", "1000.00");
		t.setCurrencyLimit("MaximumCashChange", "50.00");
		t.setCurrencyLimit("MaximumCashRefund", "100.00");
		t.setCurrencyLimit("MaximumCashRefundWithoutReceipt", "20.00");
		c.setTenderLimits(t);
		if (args.length > 0) {
			c.setAmountTender(DomainGateway.getBaseCurrencyInstance(args[0]));
			//int errorCode = true;
			int errorCode;
			if (args.length > 1) {
				if (args[1].equals("Return")) {
					if (args.length > 2) {
						if (args[2].equals("NoReceipt")) {
							errorCode = c.checkLimitsForReturn(false);
						} else {
							errorCode = c.checkLimitsForReturn(true);
						}
					} else {
						errorCode = c.checkLimitsForReturn(true);
					}
				} else {
					errorCode = c.checkLimitsForSale();
				}
			} else {
				errorCode = c.checkLimitsForSale();
			}

			System.out.println(c.toString());
			System.out.println("Check of limits results:  " + t.tenderLimitsErrorToString(errorCode));
		} else {
			System.out.println(c.toString());
		}

	}
	
	public boolean isEWalletTenderType() {
		return isEWalletTenderType;
	}

	public void setEWalletTenderType(boolean isEWalletTenderType) {
		this.isEWalletTenderType = isEWalletTenderType;
	}

	public void setTypeCode(int value) {
		super.typeCode=value;
		
	}

}