/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  24/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ado.tender;

import max.retail.stores.domain.manager.tenderauth.MAXGiftCardAuthResponse;
import max.retail.stores.domain.manager.tenderauth.MAXTenderAuthResponse;
import max.retail.stores.domain.manager.tenderauth.MAXTenderAuthConstantsIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.pos.ado.tender.AuthResponseCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderGiftCardADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;

public class MAXTenderGiftCardADO extends TenderGiftCardADO{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -575119397968317457L;

	protected void validateBinRange() throws TenderException
    {
        boolean binRangeValid = false;
        // Changes start for code merging(commenting below line)
        //binRangeValid = isValidBinRange(((TenderGiftCardIfc)tenderRDO).getCardNumber(), TenderTypeEnum.GIFT_CARD);
        binRangeValid = isValidBinRange(((TenderGiftCardIfc)tenderRDO).getEncipheredCardData(), TenderTypeEnum.GIFT_CARD);
        // changes end for code merging
        if (!binRangeValid)
        {
            //throw new TenderException("Invalid Bin Range", TenderErrorCodeEnum.INVALID_CARD_NUMBER);
        }
    }
// Changes done for ocd emerging(changing TenderAuthResponse to MAXTenderAuthResponse)
    //public void processResponse(TenderAuthResponse tenderAuthResponse, String amount) throws AuthorizationException
	public void processResponse(MAXTenderAuthResponse tenderAuthResponse, String amount) throws AuthorizationException
// Chnages ends for code merging	
	{
        if(logger.isInfoEnabled())
        {
            logger.info("Received gift card authorization response...");
        }
        //    get the response code
        String responseCode = tenderAuthResponse.getResponseCode();
        responseCode = "00";
// changes starts for code merging(changing to MAXGiftCardAuthResponse)
        //if(tenderAuthResponse instanceof GiftCardAuthResponse)
        if(tenderAuthResponse instanceof MAXGiftCardAuthResponse)
        {
            //GiftCardAuthResponse response = (GiftCardAuthResponse)tenderAuthResponse;
        	MAXGiftCardAuthResponse response = (MAXGiftCardAuthResponse)tenderAuthResponse;
 // Changes ends for code merging
            // store response elements common to all response types
            // may need to be the gift card available balance if we have a split tender situation

            ((TenderGiftCardIfc)tenderRDO).setAuthorizationCode(response.getApprovalCode());
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationResponse(response.getResponseText());
            // default to these values and change them if necessary
            ((TenderGiftCardIfc)tenderRDO).setFinancialNetworkStatus(AuthorizableTenderIfc.AUTHORIZATION_NETWORK_ONLINE);
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_AUTO);
            GiftCardIfc giftCardObject = response.getGiftCard();
            authorizationResponseCode = response.getResponseCode();
            ((TenderGiftCardIfc)tenderRDO).setSettlementData(response.getSettlementData());
            ((TenderGiftCardIfc)tenderRDO).setAuthorizedDateTime(response.getAuthorizationDateTime());

            // put the gift card in the tender item
            if (giftCardObject != null)
            {
                // set the status of the gift card
                //giftCardObject.setStatus(authorizationResponseCode);

                ((TenderGiftCardIfc)tenderRDO).setGiftCard(giftCardObject);
            }

        //    Process the response
         if (responseCode.equals(MAXTenderAuthConstantsIfc.APPROVED))
         {
            CurrencyIfc availBalance = DomainGateway.getBaseCurrencyInstance(amount);
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationAmount(availBalance);
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED);
         }
         else if (responseCode.equals(MAXTenderAuthConstantsIfc.DECLINED_PARTIAL))
         {
            //setAuthorizationAmount(giftCardObject.getCurrentBalance());
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationAmount(giftCardObject.getCurrentBalance());

            ((TenderGiftCardIfc)tenderRDO).setAmountTender(giftCardObject.getCurrentBalance());
            // Update tender attributes with new tender amount
            resetAmountForAuthPartialDecline(giftCardObject.getCurrentBalance().toString());

            ((TenderGiftCardIfc)tenderRDO).setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED);

            // This flag is used to determine if the transaction totals should be recalculated.
            setDirtyFlag(true);
         }
         //SimAuth will not return TenderAuthConstantsIfc.ERROR_RETRY for gift card
         //These lines may need to removed.
         else if (responseCode.equals(MAXTenderAuthConstantsIfc.ERROR_RETRY))
         {
            throw new AuthorizationException("Gift Card Auth Error Retry",
                                             AuthResponseCodeEnum.ERROR_RETRY,
                                             response.getResponseText());
         }
         else if (responseCode.equals(MAXTenderAuthConstantsIfc.OFFLINE))
         {
            ((TenderGiftCardIfc)tenderRDO).setFinancialNetworkStatus(AuthorizableTenderIfc.AUTHORIZATION_NETWORK_OFFLINE);
            throw new AuthorizationException("Gift Card Auth TIMEOUT/OFFLINE",
                                              AuthResponseCodeEnum.OFFLINE,
                                              response.getResponseText());
         }
         else if(responseCode.equals(MAXTenderAuthConstantsIfc.TIMEOUT))
         {
             ((TenderGiftCardIfc)tenderRDO).setFinancialNetworkStatus(AuthorizableTenderIfc.AUTHORIZATION_NETWORK_OFFLINE);
             throw new AuthorizationException("Gift Card Auth TIMEOUT/OFFLINE",
                                               AuthResponseCodeEnum.TIMEOUT,
                                               response.getResponseText());
         }
         else if (responseCode.equals(MAXTenderAuthConstantsIfc.REFERRAL))
         {
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationMethod(AuthorizableTenderIfc.AUTHORIZATION_METHOD_MANUAL);
            throw new AuthorizationException("Gift Card Auth CALL CENTER",
                                              AuthResponseCodeEnum.REFERRAL,
                                              response.getResponseText());
         }
         else
         {
             // when gift card expended, decline dialog will be displayed.
             // UPDATE: No longer an expended status for gift cards.
            ((TenderGiftCardIfc)tenderRDO).setAuthorizationStatus(AuthorizableTenderIfc.AUTHORIZATION_STATUS_DECLINED);
             throw new AuthorizationException("Gift Card Auth DECLINED",
                                               AuthResponseCodeEnum.DECLINED,
                                               response.getResponseCode());
         }
        }
        else
        {

            //If this is not a giftCardAuthResponse, we have an offline situation.
            authorizationResponseCode = responseCode;
            ((TenderGiftCardIfc)tenderRDO).setFinancialNetworkStatus(AuthorizableTenderIfc.AUTHORIZATION_NETWORK_OFFLINE);
            throw new AuthorizationException("Gift Card Auth TIMEOUT/OFFLINE",
                                              AuthResponseCodeEnum.OFFLINE,
                                              tenderAuthResponse.getResponseText());
        }
    }
	
	

}
