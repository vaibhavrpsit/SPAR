package max.retail.stores.pos.services.tender;

import oracle.retail.stores.domain.tender.TenderCash;

public class MAXTenderCash extends TenderCash
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean isEwalletTenderType = false;
	//protected int typeCode;
	
	 public MAXTenderCash()
	 {
		 super();
		 
	 }
	
	public boolean isEwalletTenderType() {
		return isEwalletTenderType;
	}
	public void setEwalletTenderType(boolean isEwalletTenderType) {
		this.isEwalletTenderType = isEwalletTenderType;
	}
	public void setTypeCode(int value) {
		super.typeCode=value;
	}
	
	 protected void setCloneAttributes(MAXTenderCash newClass)
	   {
    super.setCloneAttributes(newClass);
	  if (isEwalletTenderType())
    {
	    newClass.setEwalletTenderType(isEwalletTenderType());
	    newClass.setTypeCode(typeCode);
	    //((CurrencyIfc)getAlternateCurrencyTendered().clone());
	    }
	   }
	

}
