package max.retail.stores.domain.tender;

import max.retail.stores.pos.services.tender.oxigenwallet.MAXOxigenTenderConstants;
import oracle.retail.stores.domain.tender.TenderCash;

public class MAXTenderEWallet  extends TenderCash {

	
	public static final String TRANSACTIONID = null;
	public  final String MOBILENUMBER = "mobileNumber";
	public final String MAXTenderEWallet = null;
	public boolean isEWalletTenderType;
	
	

	public boolean isEWalletTenderType() {
		return isEWalletTenderType;
	}

	public void setEWalletTenderType(boolean isEWalletTenderType) {
		this.isEWalletTenderType = isEWalletTenderType;
	}
}
