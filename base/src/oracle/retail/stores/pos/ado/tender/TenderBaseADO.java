/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderBaseADO.java /rgbustores_13.4x_generic_branch/3 2011/07/28 21:02:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/28/11 - implement credit decline manager override
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  02/06/09 - overloaded parseAmount method to include locale as
 *                         an argument
 *    sgu       12/23/08 - fixed the crash in foreign check tender
 *    ranojha   11/13/08 - Fixed Foreign Currency Till Reconciliation
 *
 * ===========================================================================
 * $Log:
 *  7    360Commerce 1.6         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *       29954: Refactor of EncipheredCardData to implement interface and be
 *       instantiated using a factory.
 *  6    360Commerce 1.5         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
 *       29761: FR 8: Prevent repeated decryption of PAN data.
 *  5    360Commerce 1.4         4/25/2007 8:52:55 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  4    360Commerce 1.3         1/22/2006 11:45:00 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:30:22 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:25:53 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:48 PM  Robert Pearse
 * $
 * Revision 1.8  2004/09/17 22:09:45  blj
 * @scr 5867 resolution for requirements change
 *
 * Revision 1.7  2004/07/15 16:13:22  kmcbride
 * @scr 5954 (Services Impact): Adding logging to these ADOs, also fixed some exception handling issues.
 *
 * Revision 1.6  2004/07/13 16:16:42  cdb
 * @scr 5421 Removed unused import.
 *
 * Revision 1.5  2004/07/12 21:42:19  bwf
 * @scr 6125 Made available expiration validation of debit before pin.
 *
 * Revision 1.4  2004/02/20 17:01:09  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.3  2004/02/12 16:47:55  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:19:47  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * Revision 1.1.1.1 2004/02/11 01:04:11
 * cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.3 Feb 05 2004 13:46:22 rhafernik log4j changes
 *
 * Rev 1.2 Feb 04 2004 15:25:46 blj more gift card refund work.
 *
 * Rev 1.1 Jan 19 2004 16:37:42 epd removed reference to CurrencyUser. Found a
 * better way to abstract DomainGateway for purposes of Unit testing.
 *
 * Rev 1.0 Jan 06 2004 10:55:52 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyFormatter;
import oracle.retail.stores.commerceservices.common.currency.CurrencyFormatterIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.CardTypeIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

/**
 * Defines tender related methods useful to tendering
 */
abstract public class TenderBaseADO extends ADO
{
    private static final long serialVersionUID = 7466039256507137232L;
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(TenderBaseADO.class);

    /**
     * Attempts to create a CurrencyIfc object from a passed in amount
     *
     * @param amountString
     *            String representing amount
     * @return a CurrencyIfc instance representing the proper amount
     * @throws TenderException
     */
    protected CurrencyIfc parseAmount(String amountString)
        throws TenderException
    {
        CurrencyIfc amount = null;
        try
        {
            amount = DomainGateway.getBaseCurrencyInstance(amountString);
        }
        catch (Exception e)
        {
            throw new TenderException(
                "Attempted to parse amount string",
                TenderErrorCodeEnum.INVALID_AMOUNT,
                e);
        }
        return amount;
    }

    /**
     * 
     * @param amountString
     * @param lcl
     * @return
     * @throws TenderException
     */
    protected CurrencyIfc parseAmount(String amountString, Locale lcl)
    throws TenderException
    {
      CurrencyIfc amount = null;
      try
      {
        CurrencyFormatterIfc currFormatter = new CurrencyFormatter();
        if(amountString != null)
        {
          BigDecimal amountBigDecimal = currFormatter.parseCurrency(amountString,lcl);
          amount = DomainGateway.getBaseCurrencyInstance(amountBigDecimal);
        }
        else
        {
          amount = DomainGateway.getBaseCurrencyInstance(amountString);
        }
      }
      catch (Exception e)
      {
          throw new TenderException("Attempted to parse amount string", TenderErrorCodeEnum.INVALID_AMOUNT, e);
      }
      return amount;
    }

    /**
     * Attempts to create a Alternate CurrencyIfc for alternate amount
     *
     * @param alternateAmountString
     *            String representing alternate amount
     * @return a CurrencyIfc instance representing the proper amount
     * @throws TenderException
     *             tender exception
     */
    protected CurrencyIfc parseAlternateAmount(String alternateAmountString)
        throws TenderException
    {
        CurrencyIfc alternateAmount = null;
        try
        {
            alternateAmount = getAlternateInstance();
            alternateAmount.setStringValue(alternateAmountString);
        }
        catch (Exception e)
        {
            throw new TenderException(
                "Attempted to parse alternate amount string",
                TenderErrorCodeEnum.INVALID_AMOUNT,
                e);
        }
        return alternateAmount;
    }

    /**
     * Attempts to create a Alternate CurrencyIfc for alternate amount
     *
     * @param alternateAmountString String representing alternate amount,
     * @param tenderAttributes HashMap containing alternate currency details
     * @return a CurrencyIfc instance representing the proper amount
     * @throws TenderException
     *             tender exception
     */
    protected CurrencyIfc parseAlternateAmount(String alternateAmount, HashMap<String,Object> tenderAttributes)
        throws TenderException
    {
	    CurrencyIfc amount = null;
	    try
	    {
	        amount = getAlternateInstance(tenderAttributes);
	        amount.setStringValue(alternateAmount);
	    }
	    catch (Exception e)
	    {
	        throw new TenderException(
	            "Attempted to parse alternate amount string",
	            TenderErrorCodeEnum.INVALID_AMOUNT,
	            e);
	    }
	    return amount;
    }


    //---------------------------------------------------------------------
    /**
     * Attempts to create a Alternate CurrencyIfc instance
     *
     * @return a CurrencyIfc instance representing the proper amount
     */
    //---------------------------------------------------------------------
    protected CurrencyIfc getAlternateInstance()
    {
        CurrencyTypeIfc[] alternateCurrencyTypes =
            DomainGateway.getAlternateCurrencyTypes();
        CurrencyIfc alternateInstance = null;
        try
        {
            alternateInstance =
                DomainGateway.getAlternateCurrencyInstance(
                    alternateCurrencyTypes[0].getCountryCode());
        }
        catch (IllegalArgumentException arg)
        {
            logger.error("Unable to retrieve the alternate currency for: \"" + alternateCurrencyTypes[0].getCountryCode() + "\"");
        }

        return alternateInstance;
    }

    /**
     * Attempts to create a Alternate CurrencyIfc instance
     * @param tenderAttributes HashMap has alternate currency type details
     * @return a CurrencyIfc instance representing the proper amount
     */
    protected CurrencyIfc getAlternateInstance(HashMap<String,Object> tenderAttributes)
    {
        CurrencyTypeIfc[] alternateCurrencyTypes =
            DomainGateway.getAlternateCurrencyTypes();
        CurrencyIfc alternateInstance = null;
        int i = 0;
        try
        {
            alternateInstance =
                DomainGateway.getAlternateCurrencyInstance(
                    alternateCurrencyTypes[0].getCountryCode());
        	CurrencyTypeIfc selectedAlternateCurrencyType = (CurrencyTypeIfc)tenderAttributes.get(TenderConstants.ALTERNATE_CURRENCY_TYPE);
        	if(selectedAlternateCurrencyType!=null)
        	{
            	String countryCode = selectedAlternateCurrencyType.getCountryCode();
            	for(i=0;i<alternateCurrencyTypes.length;i++)
            	{
            		if(countryCode.equals(alternateCurrencyTypes[i].getCountryCode()))
            		{
                        alternateInstance =
                            DomainGateway.getAlternateCurrencyInstance(
                                alternateCurrencyTypes[i].getCountryCode());
                        break;
            		}
            	}
        	}

        }
        catch (IllegalArgumentException arg)
        {
            logger.error("Unable to retrieve the alternate currency for: \"" + alternateCurrencyTypes[i].getCountryCode() + "\"");
        }

        return alternateInstance;
    }


    /**
     * Takes an expiration date as entered from the UI and converts it into an
     * EYS date
     *
     * @param format
     *            A date format describing the formatting of expirationDateStr
     * @param expirationDateStr
     *            a String to convert
     * @return an EYSDate representative of the expirationDate string
     */
    protected EYSDate parseExpirationDate(
        String format,
        String expirationDateStr)
        throws TenderException
    {
        UtilityIfc util = getUtility();
        return util.parseExpirationDate(format, expirationDateStr);
    }

    /**
     * Attempt to determine what type of credit the number represents.
     *
     * @see TenderLineItemIfc for valid tender types.
     * @param cardData
     *            The EncipheredCardData instance to identify.
     * @return The credit type.
     */
    protected CreditTypeEnum determineCreditType(EncipheredCardDataIfc cardData)
    {
        // get the Card Type utility from the utility manager
        ADOContextIfc context = getContext();
        UtilityManagerIfc utility =
            (UtilityManagerIfc) context.getManager(UtilityManagerIfc.TYPE);
        CardTypeIfc cardTypeUtility = utility.getConfiguredCardTypeInstance();

        // return the card type
        String cardType =
            cardTypeUtility.identifyCardType(
                cardData,
                TenderTypeEnum.CREDIT.toString());
        return CreditTypeEnum.makeEnumFromString(cardType);
    }

    /**
     * Determines whether a given cardnumber and tender type is identifiable
     *
     * @param cardData
     *            The EncipheredCardData instance to be typed
     * @param tenderType
     *            The type to try and identify the card as
     * @return flag indicating whether the card is identifiable or not.
     */
    protected boolean isValidBinRange(
        EncipheredCardDataIfc cardData,
        TenderTypeEnum tenderType)
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Validating bin range...");
        }

        boolean isValid = false;
        UtilityManagerIfc utility =
            (UtilityManagerIfc) getContext().getManager(UtilityManagerIfc.TYPE);
        CardTypeIfc cardType = utility.getConfiguredCardTypeInstance();
        if (cardType != null)
        {
            String retCardType =
                cardType.identifyCardType(cardData, tenderType.toString());
            if (!retCardType.equals(CardTypeIfc.UNKNOWN))
            {
                if(logger.isDebugEnabled())
                {
                    logger.debug("Valid bin range detected");
                }

                isValid = true;
            }
            else
            {
                if(logger.isDebugEnabled())
                {
                    logger.debug("Invalid bin range detected");
                }
            }
        }
        return isValid;
    }

    /**
     * Attempts to calculate the base currency amount from a passed in foreign
     * amount
     *
     * @param amountString
     *            String representing the foreign amount
     * @return a CurrencyIfc instance representing the base currency amount
     * @throws TenderException
     */
    protected CurrencyIfc calculateBaseCurrencyAmount(
        String amountString,
        BigDecimal conversionRate)
        throws TenderException
    {
        return parseAmount(amountString).multiply(conversionRate);
    }

    /**
     * Checks parameter settings to see if an auth decline
     * can be overridden.
     * @return
     */
    public boolean isManagerOverrideForAuthEnabled(String paramValue)
    {
        boolean result = false;
        UtilityIfc util = getUtility();
        List<String> declineOverrideSettings = Arrays.asList(util.getParameterValueList(ParameterConstantsIfc.TENDERAUTHORIZATION_ManagerOverrideParameters));
        if (declineOverrideSettings.contains(paramValue))
        {
            result = true;
        }

        return result;
    }
}
