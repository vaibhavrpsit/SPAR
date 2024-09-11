/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.0     Nov 17, 2016	        Ashish Yadav		Changes for Employee Discount FES
 *
 ********************************************************************************/
package max.retail.stores.pos.services.modifytransaction.discount;

import java.math.BigDecimal;
import java.math.BigInteger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifytransaction.discount.AmountEnteredAisle;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

//--------------------------------------------------------------------------
/**
    This aisle will validate the  amount entered is valid.
    <P>
    @version $Revision: 7$
**/
//--------------------------------------------------------------------------
public class MAXAmountEnteredAisle extends AmountEnteredAisle
{
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	/**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 7$";
    /**
       length of available space for discount value
    **/
    public static int AVAIL_DISCOUNT_LENGTH = 23;
    /**
       constant for parameter name
       @deprecated as of release 7.0. No replacement
    **/
    public static final String MAX_DISC_PCT = "MaximumTransactionDiscountAmountPercent";
    /**
       constant for error dialog screen
       @deprecated as of release 7.0. No replacement
    **/
    public static final String INVALID_DISC = "InvalidDiscount";
    /**
       constant for error dialog screen
       @deprecated as of release 7.0. No replacement
    **/
    public static final String INVALID_TRANS_DISC = "InvalidTransactionDiscountPercent";
    /**
       constant for error dialog screen
    **/
    public static final String INVALID_REASON_CODE = "InvalidReasonCode";

    /**
	    resource id for invalid transaction discount dialog
	**/
	protected static final String INVALID_TRANSACTION_DISCOUNT_DIALOG = "InvalidTransactionDiscountAmount";

    //----------------------------------------------------------------------
    /**
       Stores the amount and reason code.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Get access to common elements
        ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo) bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // Retrieve data from UI model
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DecimalWithReasonBeanModel beanModel = (DecimalWithReasonBeanModel) uiManager.getModel(POSUIManagerIfc.TRANS_DISC_AMT);
        BigDecimal discountAmount = beanModel.getValue();
        // Changes start for Rev 1.0 (Ashish : Employee discount)
        String  reason = beanModel.getSelectedReasonKey();
        // Changes ends for Rev 1.0 (Ashish : Employee discount)
        String dialog = null;
        BigDecimal percent;

        // need to handle if there are not items in the transaction you can still set the discount amount for the transaction.
        CurrencyIfc itemTotal = cargo.getItemTotal();
        if(itemTotal != null && itemTotal.signum() != CurrencyIfc.ZERO)
        {
        	BigDecimal itemTotalAmount = new BigDecimal(cargo.getItemTotal().toString());
        	percent = discountAmount.divide(itemTotalAmount, 2, BigDecimal.ROUND_HALF_UP);
        }
        else
        {
        	percent = new BigDecimal("0.0");
        }
// Changes starts for code merging(commenting below lines as per MAX)
        // Validate the Reason Code ID Check Digit, Valid Reason Code exists
        /*CodeEntryIfc reasonEntry = null;
        if (cargo.getReasonCodes() != null)
        {
            reasonEntry = cargo.getReasonCodes().findListEntry(reason, false);
        }*/
// Changes ends for code merging
        CodeEntryIfc reasonEntry = null;
     // Changes start for code emrging(adding below line as per MAX)
        CodeListIfc rcl = cargo.getLocalizedDiscountAmountReasonCodes();
        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        if (rcl != null)
        {
        	 // Changes start for Rev 1.0 (Ashish : Employee discount)
        	reasonEntry = rcl.findListEntryByCode (reason);
            localizedCode.setCode(reason);
            localizedCode.setText(reasonEntry.getLocalizedText());
            // Changes ends for Rev 1.0 (Ashish : Employee discount)
        }
        else
        {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }
        //Changes ends for code merging
     // Changes start for Rev 1.0 (Ashish : Employee discount)
        if (reasonEntry == null || !isValidCheckDigit(utility, reasonEntry.getCode(), bus.getServiceName()))
        {
            dialog = INVALID_REASON_CODE;
        }
     // Changes ends for Rev 1.0 (Ashish : Employee discount)
        else if (dialog == null)
        {
            if (isValidDiscount(bus, percent,cargo,uiManager))
            {
            CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance(String.valueOf(discountAmount));
            
            // changes starts for code merging(commenting below line as per MAX(updated as per 14))
            //TransactionDiscountByAmountIfc amountDiscount= createDiscountStrategy(cargo,beanModel,amount,bus.getServiceName());
            TransactionDiscountByAmountIfc amountDiscount = createDiscountStrategy(localizedCode, amount);
            // Changes ends for code merging
            // reference this discount in the cargo
            cargo.setDiscount(amountDiscount);
            cargo.setDoDiscount(true);
	            // bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }
            else
            {
                dialog = INVALID_TRANSACTION_DISCOUNT_DIALOG;
            }
        }

        if (dialog == null)
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else if (dialog.equals(INVALID_REASON_CODE))
        {
            showInvalidReasonCodeDialog(uiManager);
        }
        else if (dialog.equals(INVALID_TRANSACTION_DISCOUNT_DIALOG))
        {
            ParameterManagerIfc pm =
                (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            // get maximum disc % allowed parameter
            BigInteger maxTransDiscPct = getMaximumDiscountPercent(pm,
                                                                   bus.getServiceName());

            String[] msg = { LocaleUtilities.formatNumber(maxTransDiscPct,
                                                          LocaleConstantsIfc.USER_INTERFACE) };

            showInvalidTransactionDiscountDiscountDialog(uiManager, msg);
        }
        else
        {
            logger.error(
                        "Unexpected dialog requested in AmountEnteredAisle: " + dialog);
        }
     }

    //--------------------------------------------------------------------------
    /**
        Clears the transaction discount.
        <P>
        @param cargo         The cargo containing discounts to be cleared
        @deprecated as of release 7.0. No replacement
    **/
    //----------------------------------------------------------------------
    public void clearDiscount(MAXModifyTransactionDiscountCargo cargo)
    {
        // get journal manager
        JournalManagerIfc mgr = (JournalManagerIfc)Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);

        cargo.setClearDiscount(true);

        // journal removal of discount due to Clear key
        if (cargo.getDiscount() != null)
        {
            TransactionDiscountByAmountIfc discountAmount =
                ((TransactionDiscountByAmountIfc)(cargo.getDiscount()));
            CurrencyIfc discountCurr = discountAmount.getDiscountAmount();
            String discountAmountStr = discountCurr.toFormattedString().trim();
            StringBuffer msg = new StringBuffer();
            msg.append(Util.EOL)
                .append("TRANS: Discount")
                .append(Util.SPACES.substring(discountAmountStr.length(), AVAIL_DISCOUNT_LENGTH))
                .append(discountAmountStr)
                .append(Util.EOL)
                .append("  Discount: $ Deleted")            //RRNdebug - replaced "Removed"
                .append(Util.EOL)
                .append("  Disc. Rsn.: ")
                .append(discountAmount.getReasonCodeText());
            String str = "";
            mgr.journal(str, str, msg.toString());
        }

    }

    //----------------------------------------------------------------------
    /**
     *   Displays the invalid discount error screen. <P>
     *   @param  uiManager       The POSUIManager
     */
    //----------------------------------------------------------------------
    protected void showInvalidReasonCodeDialog(POSUIManagerIfc uiManager)
    {
        // display the invalid discount error screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(INVALID_REASON_CODE);
        dialogModel.setType(DialogScreensIfc.ERROR);

        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
     *   Creates discount strategy.
     *   @param  cargo       The bus cargo
     *   @param  beanModel   The DecimalWithReasonBeanModel
     *   @param  discount    Discount amount
     *   @param  serviceName The name of the calling service
     *   @return The TransactionDiscountByAmount strategy
     */
    // Changes start for Rev 1.0 (Ashish : Employee Discount (removing below method as alternate is present in base 14)
    //----------------------------------------------------------------------
    /*protected TransactionDiscountByAmountIfc createDiscountStrategy(MAXModifyTransactionDiscountCargo cargo,
                                                          DecimalWithReasonBeanModel beanModel,
                                                          CurrencyIfc discount,
                                                          String serviceName)
    {

        String reason = beanModel.getSelectedReason();

        // Validate the Reason Code ID Check Digit, Valid Reason Code exists
        CodeEntryIfc reasonEntry = cargo.getReasonCodes().findListEntry(reason, false, null);

        int reasonInt = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;
        if (reasonEntry != null)
        {
            try
            {
                reasonInt = Integer.parseInt(reasonEntry.getCode());
            }
            catch(Exception e)
            {
                reasonInt = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;
                logger.warn( "Valid Reason Code Not Found for " + reason + "!");
            }
        }
        TransactionDiscountByAmountIfc amountDiscount = DomainGateway.getFactory().getTransactionDiscountByAmountInstance();
        amountDiscount.setDiscountAmount(discount);
        amountDiscount.setReasonCode(reasonInt);
        amountDiscount.setReasonCodeText(reason);
        return amountDiscount;
    }*/
    
    protected TransactionDiscountByAmountIfc createDiscountStrategy(LocalizedCodeIfc reason, CurrencyIfc discount)
    {

        TransactionDiscountByAmountIfc amountDiscount = DomainGateway.getFactory()
                .getTransactionDiscountByAmountInstance();
        amountDiscount.setDiscountAmount(discount);
        amountDiscount.setReason(reason);
        return amountDiscount;
    }
 // Changes ends for Rev 1.0 (Ashish : Employee Discount)
    //--------------------------------------------------------------------------
    /**
        Check digit validation.
        <P>
        @param utility       utility manager
        @param reasonCodeID  the reason code ID that needs to be checked
        @param serviceName   service name
        @return boolean      return true if valid, otherwise return false
    **/
    //----------------------------------------------------------------------
    public static boolean isValidCheckDigit(UtilityManagerIfc utility,
                                            String reasonCodeID,
                                            String serviceName)
    {
        boolean isValid = false;
        if ( !utility.validateCheckDigit(
                        CheckDigitUtility.CHECK_DIGIT_FUNCTION_REASON_CODE,
                        reasonCodeID))
        {
            // If check digit is not configured for reason code, the check digit function will always return true
            if (logger.isInfoEnabled()) logger.info(
                        "Invalid number received. check digit is invalid. Prompting user to re-enter the information ...");
        }
        else
        {
            isValid = true;
        }
        return isValid;
    }

    //----------------------------------------------------------------------
    /**
       Returns a BigInteger, the maximum discount % allowed from
       the parameter file. <P>
           @param pm ParameterManagerIfc reference
           @param serviceName service name (for log)
       @return maximum discount percent allowed as BigInteger
    **/
    //----------------------------------------------------------------------
    private BigInteger getMaximumDiscountPercent(ParameterManagerIfc pm,
                                                 String serviceName)
    {
        BigInteger maximum = new BigInteger("100");  // default
        try
        {
            String s = pm.getStringValue(MAX_DISC_PCT);
            s.trim();
            maximum = new BigInteger(s);
            if (logger.isInfoEnabled()) logger.info(
                         "Parameter read: " + MAX_DISC_PCT + "=[" + maximum + "]");
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }

        return(maximum);
    }

    //--------------------------------------------------------------------------
    /**
        Validates the discount.
        <P>
        @param bus           The service bus
        @param percent       The percentage of the discount as a Big Decimal
        @return boolean      return true if valid
    **/
    //----------------------------------------------------------------------
    public boolean isValidDiscount(BusIfc bus, BigDecimal percent,ModifyTransactionDiscountCargo cargo,POSUIManagerIfc uiManager)
    {
        BigInteger percentInt = percent.movePointRight(2).toBigInteger();

        ParameterManagerIfc pm =
            (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        // get maximum disc % allowed parameter
        BigInteger maxTransDiscPct = getMaximumDiscountPercent(pm,
                                                               bus.getServiceName());
        DecimalWithReasonBeanModel beanModel = (DecimalWithReasonBeanModel) uiManager
				.getModel(POSUIManagerIfc.TRANS_DISC_AMT);
		 String tranAmtParameter=null;
		// BigDecimal response = new BigDecimal("0.00");
		 LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
	        String codeName = null;
	        int   pmValue = 0;
	        String reason = beanModel.getSelectedReasonKey();
	        if (localizedCode != null)
	        {
	            localizedCode = DomainGateway.getFactory().getLocalizedCode();
	            CodeListIfc rcl = cargo.getLocalizedDiscountAmountReasonCodes();
	            CodeEntryIfc reasonEntry = null;
	            
	            if (rcl != null)
	            {
	            	reasonEntry = rcl.findListEntryByCode(reason);
	                codeName=reasonEntry.getCodeName();
	                codeName=codeName.replaceAll("\\s","");
	            }
	        }
	        tranAmtParameter=codeName.concat("TranAmtValue");
	         ParameterManagerIfc pm2 = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
	       
				try {
					pmValue = pm2.getIntegerValue(tranAmtParameter).intValue();
				} catch (ParameterException e) {
					if (logger.isInfoEnabled())
						logger.info("MAXPercentEnteredAisle.isValidDiscount(), cannot find tranPercParameter paremeter.");
				}
				
			//	BigInteger response = percent.movePointRight(2).toBigInteger();
				BigInteger response=BigInteger.valueOf(pmValue);
				boolean validdisc=false;
				
				 if(percentInt.compareTo(maxTransDiscPct) < 1 && percentInt.compareTo(response) < 1) {
					 validdisc=true;
				 }else {
					 validdisc=false;
				 }
		return validdisc;
	}

       // return (percentInt.compareTo(maxTransDiscPct) < 1);

  //  }

    //----------------------------------------------------------------------
    /**
     *   Displays the invalid discount error screen. <P>
     *   @param  uiManager The POSUIManager
     *   @param  msg       The string array representing the arguments for
     *                     the dialog
     */
    //----------------------------------------------------------------------
    protected void showInvalidTransactionDiscountDiscountDialog(POSUIManagerIfc uiManager, String[] msg)
    {
        // display the invalid discount error screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(INVALID_TRANSACTION_DISCOUNT_DIALOG);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(msg);

        // display dialog
        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
    }

}
