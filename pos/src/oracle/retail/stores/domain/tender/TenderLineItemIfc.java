package oracle.retail.stores.domain.tender;

import java.util.Locale;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

// Referenced classes of package oracle.retail.stores.domain.tender:
//            TenderLineItemConstantsIfc, TenderLimitsIfc

public interface TenderLineItemIfc
    extends EYSDomainIfc, TenderLineItemConstantsIfc
{

    public abstract int checkLimitsForSale();

    public abstract int checkLimitsForReturn(boolean flag);

    public abstract int checkLimitsForReturn();

    public abstract CurrencyIfc getAmountTender();

    public abstract void setAmountTender(CurrencyIfc currencyifc);

    public abstract void setCollected(boolean flag);

    public abstract boolean isCollected();

    public abstract byte[] getNumber();

    public abstract int getLineNumber();

    public abstract void setLineNumber(int i);

    public abstract int getTypeCode();

    public abstract String getTypeCodeString();

    public abstract String getTypeDescriptorString();

    public abstract String toJournalString(Locale locale);

    public abstract void setTenderLimits(TenderLimitsIfc tenderlimitsifc);

    public abstract TenderLimitsIfc getTenderLimits();

    public abstract CurrencyIfc getAmountMaximumChange();

    public abstract void setHasDenominations(boolean flag);

    public abstract boolean getHasDenominations();

    public abstract boolean IsExemptFromMaxCashLimit();

    public abstract int getCurrencyID();

    public abstract void setCurrencyID(int i);
	
	public abstract String getTenderDateTime();
	
	public abstract void setTenderDateTime(String tenderDateTime);

    public static final String revisionNumber = "$Revision: /main/14 $";
}
