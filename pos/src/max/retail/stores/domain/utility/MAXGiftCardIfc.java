/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.1  08/08/2013     Jyoti Rawal, Changed the Gift Card Tender flow
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.utility;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCardIfc;

public interface MAXGiftCardIfc extends GiftCardIfc {

	public String getQcApprovalCode();

	public void setQcApprovalCode(String qcApprovalCode);

	public String getQcInvoiceNumber();

	public void setQcInvoiceNumber(String qcInvoiceNumber);

	public String getQcTransactionId();

	public void setQcTransactionId(String qcTransactionId);

	public String getQcBatchNumber();

	public void setQcBatchNumber(String qcBatchNumber);

	public String getQcCardType();

	public void setQcCardType(String qcCardType);

	public boolean isSwiped();

	public void setSwiped(boolean isSwiped);// Rev 1.1 change

	public boolean isScanned();

	public void setScanned(boolean isSwiped); // Rev 1.1 change

	public String getTrackData();

	public void setTrackData(String trackData);

	public EYSDate getExpirationDate();// Rev 1.1 change

	public void setExpirationDate(EYSDate value);// Rev 1.1 change

	public void setCardPin(String cardPin);

	public String getCardPin();

}
