/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderCouponADO.java /main/15 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       11/24/09 - add validation for coupon code
 *    vchengeg  11/07/08 - To fix BAT test failure
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 8:52:55 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         12/13/2005 4:42:32 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:23 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:55 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:49 PM  Robert Pearse
 *
 *   Revision 1.8  2004/07/30 18:49:12  epd
 *   @scr 6323 Now saves entry method in couponRDO. I don't know if this fixes poslog, but it's a first step
 *
 *   Revision 1.7  2004/07/14 22:11:52  kmcbride
 *   @scr 5954 (Services Impact): Adding log4j loggers to these classes to make them easier to debug, also fixed some catch statements that were not logging or re-throwing a new exception w/o nesting the original.
 *
 *   Revision 1.6  2004/07/14 18:47:08  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.5  2004/07/13 19:49:52  epd
 *   @scr 5957 (from ServicesImpact) I removed the static array of hardcoded coupon values and replaced it with new properties file
 *
 *   Revision 1.4  2004/04/22 21:03:53  epd
 *   @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 *   Revision 1.3  2004/04/08 20:33:03  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.2  2004/02/12 16:47:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Feb 05 2004 13:46:26   rhafernik
 * log4j changes
 *
 *    Rev 1.2   Jan 06 2004 11:09:48   epd
 * refactoring to remove references to TenderHelper, DomainGateway
 *
 *    Rev 1.1   Nov 13 2003 17:03:06   epd
 * Refactoring: updated to use new method to access context
 *
 *    Rev 1.0   Nov 04 2003 11:13:12   epd
 * Initial revision.
 *
 *    Rev 1.2   Oct 30 2003 12:53:52   crain
 * Implemented coupon
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.1   Oct 21 2003 10:00:58   epd
 * Refactoring.  Moved RDO tender to abstract class
 *
 *    Rev 1.0   Oct 17 2003 12:33:44   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderCouponIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;

import org.apache.log4j.Logger;

/**
 *
 */
public class TenderCouponADO extends AbstractTenderADO
{
    private static final long serialVersionUID = -6311849276167280375L;
    /**
	 *  our logger
	 **/
	protected transient Logger logger = Logger.getLogger(TenderCouponADO.class);

	/**
	 * This class is instantiated by the tender factory
	 */
	protected TenderCouponADO() {}

	/* (non-Javadoc)
	 * @see oracle.retail.stores.ado.tender.AbstractTenderADO#initializeTenderRDO()
	 */
	protected void initializeTenderRDO()
	{
		tenderRDO = DomainGateway.getFactory().getTenderCouponInstance();
	}

	/* (non-Javadoc)
	 * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderType()
	 */
	public TenderTypeEnum getTenderType()
	{
		return TenderTypeEnum.COUPON;
	}

	/* (non-Javadoc)
	 * @see oracle.retail.stores.ado.tender.TenderADOIfc#validate()
	 */
	public void validate() throws TenderException
	{
	}

	/* (non-Javadoc)
	 * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderAttributes()
	 */
	public HashMap getTenderAttributes()
	{
		HashMap map = new HashMap(5);
		map.put(TenderConstants.TENDER_TYPE, getTenderType());
		map.put(TenderConstants.AMOUNT,
				getAmount().getStringValue());
		map.put(TenderConstants.COUPON_NUMBER, ((TenderCouponIfc)tenderRDO).getCouponNumber());
		map.put(TenderConstants.COUPON_TYPE, (CouponTypeEnum.COUPON_TYPE_MANUFACTURER.getCouponTypeDescriptor()));
		map.put(TenderConstants.ENTRY_METHOD, ((TenderCouponIfc)tenderRDO).getEntryMethod());

		return map;
	}

	/* (non-Javadoc)
	 * @see oracle.retail.stores.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
	 */
	public void setTenderAttributes(HashMap tenderAttributes)
	throws TenderException
	{
		if(logger.isDebugEnabled())
		{
			logger.debug("Setting coupon attributes: Amount=" + tenderAttributes.get(TenderConstants.AMOUNT)
					+ " Coupon number=" + tenderAttributes.get(TenderConstants.COUPON_NUMBER));
		}

		// get the amount
		((TenderCouponIfc)tenderRDO).setAmountTender(parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT)));

		// get the coupon number
		((TenderCouponIfc)tenderRDO).setCouponNumber((String)tenderAttributes.get(TenderConstants.COUPON_NUMBER));

		// set the coupon type to coupon type manufacturer
		((TenderCouponIfc)tenderRDO).setCouponType(CouponTypeEnum.COUPON_TYPE_MANUFACTURER.getCouponTypeRDO());

		// entry methdod
		((TenderCouponIfc)tenderRDO).setEntryMethod((EntryMethod)tenderAttributes.get(TenderConstants.ENTRY_METHOD));
	}

	/**
	 * Indicates Coupon is a NOT type of PAT Cash
	 * @return false
	 */
	public boolean isPATCash()
	{
		return false;
	}

	/**
	 * Attempts to calculate the coupon amount based on the coupon number
	 * @throws TenderException
	 */
	public void calculateCouponAmount()
	throws TenderException
	{
		if(logger.isDebugEnabled())
		{
			logger.debug("Calculating coupon amount...");
		}

		String couponNumber = (String)getTenderAttributes().get(TenderConstants.COUPON_NUMBER);
		if (isValidCouponNumber(couponNumber))
		{
			// Determine the coupon's face value based on 2 character coupon code.
			CurrencyIfc amount = parseAmount(getCouponValue(couponNumber.substring(9, 11)));
			if (amount.signum() == CurrencyIfc.ZERO)
			{
				// coupon amount is zero; manual input of amount is needed
				throw new TenderException("Manual input for coupon amount needed", TenderErrorCodeEnum.MANUAL_INPUT);
			}
			else
			{
				if(logger.isDebugEnabled())
				{
					logger.debug("Setting tender amount to: " + amount);
				}

				((TenderCouponIfc)tenderRDO).setAmountTender(amount);
			}
		}
		else
		{
			if (isTriggerNumber(couponNumber))
			{
				// trigger number for coupon; manual input of amount is needed
				throw new TenderException("Manual input for coupon amount needed", TenderErrorCodeEnum.MANUAL_INPUT);
			}
			else
			{
				throw new TenderException("Invalid coupon number", TenderErrorCodeEnum.INVALID_COUPON);
			}
		}
	}
	/* (non-Javadoc)
	 * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
	 */
	@SuppressWarnings("unchecked")
	public Map getJournalMemento()
	{
		Map memento = getTenderAttributes();

		// add tender descriptor
		memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
		return memento;
	}

	/* (non-Javadoc)
	 * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
	 */
	public void fromLegacy(EYSDomainIfc rdo)
	{
		//assert(rdo instanceof TenderCouponIfc);

		tenderRDO = (TenderCouponIfc)rdo;
	}

	/* (non-Javadoc)
	 * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
	 */
	public EYSDomainIfc toLegacy()
	{
		// update with current amount
		return tenderRDO;
	}

	/* (non-Javadoc)
	 * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
	 */
	public EYSDomainIfc toLegacy(Class type)
	{
		return toLegacy();
	}


    //--------------------------------------------------------------------------
    /**
       Validate that coupon Code contains only digits or not.
       @param String coupon Code
       @return true if valid coupon Code
    **/
    //--------------------------------------------------------------------------
    protected boolean isValidCouponCode(String couponCode)
    {
        boolean validCode = false;

        if (containsOnlyNumbers(couponCode))
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Valid coupon Code found: " + couponCode);
            }

            validCode = true;
        }
        else
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Invalid coupon Code found: " + couponCode);
            }
        }

        return(validCode);
    }

    //--------------------------------------------------------------------------
    /**
       Validate that coupon Code contains only digits or not.
       @param String coupon Code
       @return true if valid coupon Code
    **/
    //--------------------------------------------------------------------------
    public boolean containsOnlyNumbers(String couponCode)
    {
        if (couponCode == null || couponCode.length() == 0)
        {
        	return false;
        }

        for (int i = 0; i < couponCode.length(); i++)
        {
            //If we find a non-digit character we return false.
            if (!Character.isDigit(couponCode.charAt(i)))
                return false;
        }
        return true;
    }

	//--------------------------------------------------------------------------
	/**
	 Validate that coupon is 12 digits and begins with '5'.
	 @param String coupon number
	 @return true if valid coupon number
	 **/
	//--------------------------------------------------------------------------
	protected boolean isValidCouponNumber(String couponNumber)
	{
		boolean validNum = false;

		// KLM: Will these coupon number constraints never
		// change???
		if ((couponNumber.length() == 12) && couponNumber.startsWith("5"))
		{
			String couponCode = couponNumber.substring(9, 11);
			if (isValidCouponCode(couponCode))
			{
				validNum = true;
			}
		}

		if (validNum)
		{
			if(logger.isDebugEnabled())
			{
				logger.debug("Valid coupon number found: " + couponNumber);
			}
		}
		else
		{
			if(logger.isDebugEnabled())
			{
				logger.debug("Invalid coupon number found: " + couponNumber);
			}
		}

		return(validNum);
	}
	//--------------------------------------------------------------------------
	/**
	 Compare operator input to the NonstoreCouponUnknownTriggerNumber parameter
	 @param String coupon number
	 @return true if input matches the trigger number parameter
	 **/
	//--------------------------------------------------------------------------
	protected boolean isTriggerNumber(String couponNumber)
	{
		boolean trigger = false;

		UtilityIfc utility = null;
		try
		{
			utility = Utility.createInstance();
		}
		catch (ADOException e)
		{
			String message = "Configuration problem: could not instantiate UtilityIfc instance";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
		String value = utility.getParameterValue("NonstoreCouponUnknownTriggerNumber", "");
		if (value.equalsIgnoreCase(couponNumber))
		{
			if(logger.isDebugEnabled())
			{
				logger.debug("Coupon number: " + couponNumber + " IS NonstoreCouponUnknownTriggerNumber");
			}

			trigger = true;
		}
		else
		{
			if(logger.isDebugEnabled())
			{
				logger.debug("Coupon number: " + couponNumber + " IS NOT NonstoreCouponUnknownTriggerNumber");
			}
		}

		return trigger;
	}

	//--------------------------------------------------------------------------
	/**
	 Determine the coupon's face value, $x.xx, based on the mapping of coupon codes to
	 array elements in TenderCouponIfc. <p>
	 @param String coupon code
	 @return coupon face value as CurrencyIfc
	 **/
	//--------------------------------------------------------------------------
	protected String getCouponValue(String couponCode)
	{
		// converting to Integer and back to ensure proper String format
		final String code = new Integer(couponCode).toString();
		final String propName = "couponValue." + code;
		Properties props = Gateway.getProperties("couponvalues.properties");
		String value = (String)props.get(propName);

		if(logger.isDebugEnabled())
		{
			logger.debug("Returning coupon value: " + value + " for couponCode: " + couponCode);
		}

		return value;
	}

}
