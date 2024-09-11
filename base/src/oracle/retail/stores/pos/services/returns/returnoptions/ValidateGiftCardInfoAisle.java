/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ValidateGiftCardInfoAisle.java /rgbustores_13.4x_generic_branch/2 2011/07/15 10:58:29 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/15/11 - Modified to support changes to lookup transaction by
 *                         account number including account number token.
 *    asinton   12/20/10 - XbranchMerge asinton_bug-10407292 from
 *                         rgbustores_13.3x_generic_branch
 *    asinton   12/17/10 - deprecated hashed account ID.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         4/24/2008 2:48:55 PM   Deepti Sharma   CR
 *         31473: Added check to see if gift card is swiped. Code reviewed by
 *         Alan Sinton
 *    7    360Commerce 1.6         3/31/2008 7:07:09 PM   Alan N. Sinton  CR
 *         30913: Updated code to handle search transaction by gift card
 *         number as encrypted account number.  Changes reviewed by Anil
 *         Bondalapati.
 *    6    360Commerce 1.5         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *         29954: Refactor of EncipheredCardData to implement interface and be
 *          instantiated using a factory.
 *    5    360Commerce 1.4         1/10/2008 1:05:19 PM   Alan N. Sinton  CR
 *         29761:  Code review changes per Tony Zgarba and Jack Swan.
 *    4    360Commerce 1.3         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
 *         29761: FR 8: Prevent repeated decryption of PAN data.
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse   
 *
 *   Revision 1.9  2004/07/12 20:13:55  mweis
 *   @scr 6158 "Gift Card ID:" label not appearing correctly
 *
 *   Revision 1.8  2004/05/21 20:56:27  mweis
 *   @scr 4902 Returns' INVALID_CARD_NUMBER message and key prompt incorrect
 *
 *   Revision 1.7  2004/03/15 21:43:29  baa
 *   @scr 0 continue moving out deprecated files
 *
 *   Revision 1.6  2004/03/15 15:16:52  baa
 *   @scr 3561 refactor/clean item size code, search by tender changes
 *
 *   Revision 1.5  2004/03/09 17:23:47  baa
 *   @scr 3561 Add bin range, check digit and bad swipe dialogs
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Jan 23 2004 16:10:22   baa
 * continue returns developement
 * 
 *    Rev 1.1   Dec 29 2003 15:36:26   baa
 * return enhancements
 * 
 *    Rev 1.0   Dec 17 2003 11:37:26   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardUtilities;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

//------------------------------------------------------------------------------
/**
     
    @version $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//------------------------------------------------------------------------------

public class ValidateGiftCardInfoAisle extends ValidateCreditCardInfoAisle
{
    /**
     * serial version UID
     */
    private static final long serialVersionUID = 616614026450150754L;
    
    public static final String ACCOUNT_FIELD     = "cardNumberField";
    /**
     * credit card text
     */
    public static final String GIFT_CARD       = "GiftCardText";
    
    /**
     * "Common" message key to text to use when the account information is invalid
     */
    public static final String CARD_NUMBER_TEXT = "GiftCardCardNumber";
 
    //--------------------------------------------------------------------------
    /**
             @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DataInputBeanModel model = (DataInputBeanModel) ui.getModel(POSUIManagerIfc.RETURN_BY_GIFTCARD);
        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();
        int selection = model.getSelectionIndex(ReturnUtilities.DATE_RANGE_FIELD);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        if (model.isCardSwiped() && !ReturnUtilities.isMSRDataValid(model.getMsrModel()))
        {
            String[] args = {utility.retrieveText(BundleConstantsIfc.COMMON,BundleConstantsIfc.RETURNS_BUNDLE_NAME,GIFT_CARD,GIFT_CARD)};
            UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, ReturnUtilities.BAD_MAG_SWIPE, args, CommonLetterIfc.RETRY);
        }
        else
        {
        	EncipheredCardDataIfc cardData = null;
        	
            if(model.isCardSwiped())
        	{
            	cardData= model.getMsrModel().getEncipheredCardData();
        	}
        	else
        	{	
        		//Get Account info
        		byte[] accountNumber = (byte[])model.getValueAsByteArray(ACCOUNT_FIELD);
        		try
	            {
	                cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(accountNumber);
	            }
	            catch(EncryptionServiceException ese)
	            {
	                logger.error("Card number could not be encrypted.", ese);
	            }
	            finally
	            {
	                Util.flushByteArray(accountNumber);
	                model.clearValue(ACCOUNT_FIELD);
	            }
        	}
            
            boolean inTraining = getTrainingMode(cargo);
            
            if (cardData != null && isCardValid(bus, cardData,inTraining))
            {
                SearchCriteriaIfc searchCriteria = cargo.getSearchCriteria();
                if (searchCriteria == null)
                {
                    searchCriteria = DomainGateway.getFactory().getSearchCriteriaInstance();
                }
                ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
                searchCriteria.setDateRange(ReturnUtilities.calculateDateRange(selection, pm));
                searchCriteria.setMaskedGiftCardNumber(cardData.getMaskedAcctNumber());

                // Save search criteria to cargo
                cargo.setSearchCriteria(searchCriteria);
                cargo.setSearchByTender(true);
                cargo.setHaveReceipt(false);                
                bus.mail(new Letter(CommonLetterIfc.VALIDATE), BusIfc.CURRENT);
            }
            else
            {
                // show error dialog
                String arg = utility.retrieveCommonText(CARD_NUMBER_TEXT);
                String[] args = { arg, arg };
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, INVALID_CARD_NUMBER, args, CommonLetterIfc.RETRY);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Retrieve training mode
     * @param cargo ReturnOptionsCargo
     * @return boolean the training mode flag
     */
    //--------------------------------------------------------------------------
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

    //--------------------------------------------------------------------------
    /**
     * Determine if this is a valid gift card
     * @param bus  the bus
     * @param cardData
     * @param inTraining boolean training mode flag
     * @return String the card type
     */
    //--------------------------------------------------------------------------
    protected boolean isCardValid(BusIfc bus, EncipheredCardDataIfc cardData, boolean inTraining)
    {
        boolean isValid =false;
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        if (!inTraining)
        {
            if (cardData != null && GiftCardUtilities.isValidBinRange(pm, utility, cardData, logger, bus.getServiceName()))
            {
                isValid =  utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_GIFTCARD, cardData);
            }
        }
        else
        {
            isValid = true;  // ignore checkdigit and bin range validation if in training mode
        }
        return isValid;
    }    
}
