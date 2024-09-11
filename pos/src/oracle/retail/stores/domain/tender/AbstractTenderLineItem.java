package oracle.retail.stores.domain.tender;

import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.factory.DomainObjectFactoryIfc;
import oracle.retail.stores.foundation.utility.Util;

// Referenced classes of package oracle.retail.stores.domain.tender:
//            TenderLineItemIfc, TenderTypeMapIfc, TenderLimits, TenderLimitsIfc

public abstract class AbstractTenderLineItem
    implements TenderLineItemIfc
{


    static final long serialVersionUID = 0x5b3be213ac271d7bL;
    public static String revisionNumber = "$Revision: /main/14 $";
    protected CurrencyIfc amountTender;
    protected int typeCode;
    protected int lineNumber;
    protected TenderLimitsIfc tenderLimits;
    protected boolean hasDenominations;
    protected int currencyID;
    protected boolean collected;
	protected String tenderDateTime;
	protected SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	
    public AbstractTenderLineItem()
    {
        amountTender = null;
        typeCode = -1;
        lineNumber = 0;
        hasDenominations = false;
        collected = true;
        amountTender = DomainGateway.getBaseCurrencyInstance();	
	    tenderDateTime = myFormat.format(new Date());
	}

    public abstract Object clone();

    protected void setCloneAttributes(TenderLineItemIfc newClass)
    {
        if(amountTender != null)
            newClass.setAmountTender((CurrencyIfc)amountTender.clone());
        newClass.setTenderLimits(tenderLimits);
        newClass.setLineNumber(lineNumber);
        newClass.setHasDenominations(getHasDenominations());
		newClass.setTenderDateTime(tenderDateTime);
    }

    public int checkLimitsForSale()
        throws IllegalStateException
    {
        return 0;
    }

    public int checkLimitsForReturn(boolean hasReceipt)
        throws IllegalStateException
    {
        return 0;
    }

    public int checkLimitsForReturn()
        throws IllegalStateException
    {
        return checkLimitsForReturn(true);
    }

    public CurrencyIfc getAmountTender()
    {
        return amountTender;
    }

    public void setAmountTender(CurrencyIfc value)
    {
        amountTender = value;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public void setLineNumber(int value)
    {
        lineNumber = value;
    }

    public byte[] getNumber()
    {
        return new byte[0];
    }

    public int getTypeCode()
    {
        return typeCode;
    }

    public String getTypeCodeString()
    {
        return DomainGateway.getFactory().getTenderTypeMapInstance().getCode(typeCode);
    }

    public String getTypeDescriptorString()
    {
        String result;
        try
        {
            result = DomainGateway.getFactory().getTenderTypeMapInstance().getDescriptor(typeCode);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            result = (new StringBuilder()).append("Unknown [").append(typeCode).append("]").toString();
        }
        return result;
    }

    public void setTenderLimits(TenderLimitsIfc value)
    {
        tenderLimits = value;
    }

    public TenderLimitsIfc getTenderLimits()
    {
        return tenderLimits;
    }

    public void setHasDenominations(boolean value)
    {
        hasDenominations = value;
    }

    public boolean getHasDenominations()
    {
        return hasDenominations;
    }

    public boolean IsExemptFromMaxCashLimit()
    {
        return false;
    }

    public boolean isCollected()
    {
        return collected;
    }

    public void setCollected(boolean collected)
    {
        this.collected = collected;
    }

    public CurrencyIfc getAmountMaximumChange()
    {
        CurrencyIfc amountMaximumChange = TenderLimits.getTenderNoLimitAmount();
        if(amountMaximumChange != null)
            amountMaximumChange = (CurrencyIfc)amountMaximumChange.clone();
        return amountMaximumChange;
    }

    public abstract String toJournalString(Locale locale);

    public String abstractTenderLineItemAttributesToJournalString(Locale journalLocale)
    {
        String tenderTypeString = getTypeDescriptorString();
        StringBuilder sb = new StringBuilder(40);
        sb.append(Util.EOL).append(tenderTypeString).append(" ");
        String transTypeString = getTransactionTypeString();
        String amountString = amountTender.toGroupFormattedString();
        sb.append(transTypeString);
        int offset = tenderTypeString.length() + transTypeString.length() + amountString.length();
        int numSpaces = 37;
        if(amountString.startsWith("("))
            numSpaces++;
        if(offset < numSpaces)
            sb.append("                                        ".substring(offset, numSpaces));
        sb.append(amountString);
        return sb.toString();
    }

    public String abstractTenderLineItemAttributesToString()
    {
        StringBuilder strResult = new StringBuilder("Class:  AbstractTenderLineItem (Revision ");
        strResult.append(getRevisionNumber()).append(") @").append(hashCode()).append(Util.EOL).append("lineNumber:              [").append(lineNumber).append("]").append(Util.EOL).append(abstractTenderLineItemAttributesToJournalString(LocaleMap.getLocale("locale_Journaling"))).append(Util.EOL).append("typeCode:                [").append(typeCode).append("]").append(Util.EOL).append("typeCodeString:          [").append(getTypeCodeString()).append("]").append(Util.EOL).append("typeDescriptorString:    [").append(getTypeDescriptorString()).append("]").append("hasDenominations:        [").append(getHasDenominations()).append("]").append(Util.EOL);
        if(tenderLimits == null)
            strResult.append("Tender limits:                          [null]").append(Util.EOL);
        else
            strResult.append("Sub").append(tenderLimits.toString()).append(Util.EOL);
        return strResult.toString();
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = false;
        if(obj instanceof AbstractTenderLineItem)
        {
            AbstractTenderLineItem c = (AbstractTenderLineItem)obj;
            if(Util.isObjectEqual(amountTender, c.amountTender) && typeCode == c.typeCode && lineNumber == c.getLineNumber() && getHasDenominations() == c.getHasDenominations() && Util.isObjectEqual(tenderLimits, c.tenderLimits))
                isEqual = true;
        }
        return isEqual;
    }

    public static String getCodeFromDescriptor(String tenderDescriptor)
    {
        TenderTypeMapIfc typeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
        return typeMap.getCode(typeMap.getTypeFromDescriptor(tenderDescriptor));
    }

    public static String getDescriptorFromCode(String tenderTypeCode)
    {
        TenderTypeMapIfc typeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
        return typeMap.getDescriptor(typeMap.getTypeFromCode(tenderTypeCode));
    }

    public static int getTypeFromCode(String tenderTypeCode)
    {
        return DomainGateway.getFactory().getTenderTypeMapInstance().getTypeFromCode(tenderTypeCode);
    }

    public static int getTypeFromDescriptor(String descriptor)
    {
        return DomainGateway.getFactory().getTenderTypeMapInstance().getTypeFromDescriptor(descriptor);
    }

    public String getTransactionTypeString()
    {
        String transTypeString;
        if(amountTender.signum() == -1)
            transTypeString = JOURNAL_REVERSED;
        else
            transTypeString = JOURNAL_TENDERED;
        return transTypeString;
    }

    public String getRevisionNumber()
    {
        return revisionNumber;
    }

    public int getCurrencyID()
    {
        return currencyID;
    }

    public void setCurrencyID(int currencyID)
    {
        this.currencyID = currencyID;
    } 

		public String getTenderDateTime()
    {
        return tenderDateTime;
    }
	
	public void setTenderDateTime(String tenderDateTime)
    {
        this.tenderDateTime = tenderDateTime;
    } 

}
