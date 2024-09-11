/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.returns.returnoptions;

import java.util.HashMap;

import com.qwikcilver.clientapi.svpos.GCPOS;

import max.retail.stores.domain.transaction.MAXSearchCriteriaIfc;
import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;
import oracle.retail.stores.pos.services.returns.returnoptions.ValidateCreditCardInfoAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

//------------------------------------------------------------------------------
/**
 * @version $Revision: 1.4 $
 **/
// ------------------------------------------------------------------------------

public class MAXValidateGiftCardInfoAisle extends ValidateCreditCardInfoAisle {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5961872365896030848L;

	/**
	 * credit card text
	 */
	public static final String GIFT_CARD = "GiftCardText";
	
	// Changes starts for code merging(added below constant as it is not present in base 14)
	public static final String ACCOUNT_FIELD        = "cardNumberField";
	// Changes ends for code merging

	/**
	 * "Common" message key to text to use when the account information is
	 * invalid
	 */
	public static final String CARD_NUMBER_TEXT = "GiftCardCardNumber";

	public boolean isSwiped = false;
	public boolean isScanned = false;
	String trackData = "";
	

	// --------------------------------------------------------------------------
	/**
	 * @param bus
	 *            the bus traversing this lane
	 **/
	// --------------------------------------------------------------------------

	public void traverse(BusIfc bus) {
		
		MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
		GCPOS pos = utilObj.getInstance();
		String var1 = ";";
		String var2 = "=";
		String var3 = "?";
		trackData = "";
		
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		DataInputBeanModel model = (DataInputBeanModel) ui.getModel(POSUIManagerIfc.RETURN_BY_GIFTCARD);
		ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();
		int selection = model.getSelectionIndex(ReturnUtilities.DATE_RANGE_FIELD);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		if (model.isCardSwiped() && !ReturnUtilities.isMSRDataValid(model.getMsrModel())) {
			String[] args = { utility.retrieveText(BundleConstantsIfc.COMMON, BundleConstantsIfc.RETURNS_BUNDLE_NAME, GIFT_CARD, GIFT_CARD) };
			UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, ReturnUtilities.BAD_MAG_SWIPE, args, CommonLetterIfc.RETRY);
		} else {
			String cardNumber = null;
			String cardNum = model.getValueAsString(ACCOUNT_FIELD);
			
			if(cardNum != null && cardNum.length()!= 0)
			{
				if(cardNum.length() > 28)
					isSwiped = true;
				else if(cardNum.length() == 26)
					isScanned = true;
			}
			
			
			String cardNumberTracked = null;
			
			if(isScanned)
			{
				trackData = ui.getInput();
				cardNumber = utilObj.getCardNumberFromTrackData(ui.getInput(),true);
			}
			else if(isSwiped)
			{
				trackData = var1 + cardNum.substring(0, 16) + var2 + cardNum.substring(16) + var3;
				cardNumber = utilObj.getCardNumberFromTrackData(trackData,true);
			}
			else{
				cardNumber = cardNum;
			}
			
			
			
			/*if(cardNum.length()> 16){
				cardNumber = cardNum.substring(0,16);
			}else{
				cardNumber = cardNum;
			}*/
			// Get Account info
			String accountNumber = cardNumber;
			boolean inTraining = getTrainingMode(cargo);

			if (isCardValid(bus, accountNumber, inTraining)) {
				MAXSearchCriteriaIfc searchCriteria = (MAXSearchCriteriaIfc) cargo.getSearchCriteria();
				if (searchCriteria == null) {
					searchCriteria = (MAXSearchCriteriaIfc) DomainGateway.getFactory().getSearchCriteriaInstance();
				}
				ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
				searchCriteria.setDateRange(ReturnUtilities.calculateDateRange(selection, pm));

				searchCriteria.setGiftCardNumber(accountNumber);

				// Save search criteria to cargo
				cargo.setSearchCriteria(searchCriteria);
				cargo.setSearchByTender(true);
				cargo.setHaveReceipt(false);
				bus.mail(new Letter(CommonLetterIfc.VALIDATE), BusIfc.CURRENT);
			} else {
				// show error dialog
				String arg = utility.retrieveCommonText(CARD_NUMBER_TEXT);
				String[] args = { arg, arg };
				UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, INVALID_CARD_NUMBER, args, CommonLetterIfc.RETRY);
			}
		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Determine if this is a valid gift card
	 * 
	 * @param bus
	 *            the bus
	 * @param accountNumber
	 *            String for the account number
	 * @param inTraining
	 *            boolean training mode flag
	 * @return String the card type
	 */
	// --------------------------------------------------------------------------
	protected boolean isCardValid(BusIfc bus, String accountNumber, boolean inTraining) {
		boolean isValid = false;

		MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
		GCPOS pos = utilObj.getInstance();
		HashMap balanceEnquiryMap = new HashMap();
		String cardNumber = accountNumber;
		if(!isSwiped || !isScanned)
			balanceEnquiryMap = utilObj.balanceEnquiryForRecipt(pos, cardNumber, "0.0", "BLC");
		else
			balanceEnquiryMap = utilObj.balanceEnquiry(pos, cardNumber, "0.0", "BLC" , trackData);

		if (balanceEnquiryMap != null && balanceEnquiryMap.size() != 0 && ("0").equals(balanceEnquiryMap.get("ResponseCode").toString())
				&& balanceEnquiryMap.get("Amount") != null) {
			isValid = true;
		}

		return isValid;
	}
	
	// Changes starts for code merging(added below method as it is not present in base 14)
	protected boolean getTrainingMode(ReturnOptionsCargo cargo)
    {
        boolean inTraining = false;
        //boolean inTraining = cargo.isTrainingMode();
        //Need to be able to retrieve training mode when no trans is available.
        if (cargo.getTransaction() != null)
        {
            inTraining = cargo.getTransaction().getWorkstation().isTrainingMode();
        }
        else
        {
            // Try the register
            inTraining = cargo.getRegister().getWorkstation().isTrainingMode();
        }
        return inTraining;
    }
	// Change ends for code merging
}
