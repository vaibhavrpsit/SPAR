/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderMailCheckADO.java /main/25 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   04/03/12 - removed deprecated methods
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    mchellap  02/15/10 - Fix to print Business name in receipts
 *    abondala  01/03/10 - update header date
 *    nkgautam  04/24/09 - fix for printing full name of business
 *                         customer/normal customer in return receipts
 *    mkochumm  01/23/09 - set country field
 *    vchengeg  11/07/08 - To fix BAT test failure
 *    abondala  11/06/08 - updated files related to reason codes
 *    sswamygo  11/05/08 - Checkin after merges
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         4/25/2007 8:52:54 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  4    360Commerce 1.3         12/13/2005 4:42:33 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:26:01 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:55 PM  Robert Pearse
 * $
 * Revision 1.17  2004/07/16 01:11:54  jdeleau
 * @scr 5446 Correct the way phone numbers are sent to e-journal for
 * mail bank checks, remove the use of deprecated constants.
 *
 * Revision 1.16  2004/05/13 20:17:25  kll
 * @scr 4303: insert city into map
 *
 * Revision 1.15  2004/04/22 21:03:53  epd
 * @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 * Revision 1.14  2004/03/16 18:30:43  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.13  2004/03/09 20:26:17  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.12  2004/03/01 20:52:32  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.11  2004/02/27 23:17:44  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.10  2004/02/27 16:39:40  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.9  2004/02/25 20:26:44  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.8  2004/02/17 22:54:54  bjosserand
 * @scr 0
 *
 * Revision 1.7  2004/02/16 22:08:33  bwf
 * @scr 0 Organize imports.
 *
 * Revision 1.6  2004/02/16 19:57:18  bjosserand
 * @scr 0
 *
 * Revision 1.5  2004/02/13 15:09:44  bjosserand
 * @scr 0
 * Revision 1.4 2004/02/12 16:47:55 mcs
 * Forcing head revision
 *
 * Revision 1.3 2004/02/12 00:47:15 bjosserand @scr 0
 *
 * Revision 1.2 2004/02/11 21:19:47 rhafernik @scr 0 Log4J conversion and code
 * cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:11 cschellenger updating to pvcs
 * 360store-current
 *
 *
 * Rev 1.4 Feb 09 2004 14:21:46 bjosserand Mail Bank Check.
 *
 * Rev 1.3 Feb 05 2004 14:24:26 bjosserand Mail Bank Check.
 *
 * Rev 1.2 Feb 01 2004 13:38:48 bjosserand Mail Bank Check.
 *
 * Rev 1.1 Jan 28 2004 13:18:38 bjosserand Mail Bank Check.
 *
 * Rev 1.0 Nov 04 2003 11:13:18 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderMailBankCheckIfc;
import oracle.retail.stores.domain.utility.Address;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.PersonName;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.domain.utility.Phone;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 *
 */
public class TenderMailCheckADO extends AbstractTenderADO
{
	/**
	 * No-arg constructor It is intended that the tender factory instantiate these
	 */
	protected TenderMailCheckADO()
	{
	}

	/**
	 *
	 * sets the "rdo" object.
	 */
	protected void initializeTenderRDO()
	{
		tenderRDO = DomainGateway.getFactory().getTenderMailBankCheckInstance();
	}

	/**
	 *
	 * get Mail Bank Check type.
	 */
	public TenderTypeEnum getTenderType()
	{
		return TenderTypeEnum.MAIL_CHECK;
	}

	/**
	 *
	 * not used here.
	 */
	public void validate() throws TenderException
	{
	}

	//----------------------------------------------------------------------
	/**
	 * This method gets the Locale so that it can be override in the unit tests.
	 *
	 * @return Locale
	 */
	//----------------------------------------------------------------------
	protected Locale getLocale()
	{
		return LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
	}

	/**
	 *
	 * Obtain values from Tender Mail Bank Check hashmap.
	 *
	 * @return HashMap
	 */
	public HashMap getTenderAttributes()
	{
		HashMap map = new HashMap(18);

		map.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.MAIL_CHECK);
		map.put(
				TenderConstants.AMOUNT,
				getAmount().getStringValue());

		PersonNameIfc custName = (PersonNameIfc) ((TenderMailBankCheckIfc) tenderRDO).getPayeeName();

		map.put(TenderConstants.FIRST_NAME, custName.getFirstName());
		map.put(TenderConstants.LAST_NAME, custName.getLastName());


		if (((TenderMailBankCheckIfc)tenderRDO).getPersonalIDType()!= null)
		{
			map.put(TenderConstants.LOCALIZED_ID_TYPE, ((TenderMailBankCheckIfc) tenderRDO).getPersonalIDType());
			map.put(TenderConstants.ID_TYPE, ((TenderMailBankCheckIfc) tenderRDO).getPersonalIDType().getCodeName());
		}

		boolean busCust = ((TenderMailBankCheckIfc) tenderRDO).isBusinessCustomer();
		int addressType;
		if (busCust)
		{
			addressType = AddressConstantsIfc.ADDRESS_TYPE_WORK;
		}
		else
		{
			addressType = AddressConstantsIfc.ADDRESS_TYPE_HOME;
		}

		AddressIfc address = ((TenderMailBankCheckIfc) tenderRDO).getAddressByType(addressType);
		if (address != null)
		{
			map.put(TenderConstants.STATE, address.getState());
			map.put(TenderConstants.COUNTRY, address.getCountry());
			map.put(TenderConstants.CITY, address.getCity());
			map.put(TenderConstants.POSTAL_CODE_1, address.getPostalCode());
			map.put(TenderConstants.POSTAL_CODE_2, address.getPostalCodeExtension());

			Vector addressLines = address.getLines();
			int addressLinesSize = addressLines.size();
			if ((addressLinesSize > 0) && (addressLinesSize < 4))
			{
				switch (addressLinesSize)
				{
				case 3 :
					map.put(TenderConstants.ADDRESS_3, (String) addressLines.elementAt(2));
				case 2 :
					map.put(TenderConstants.ADDRESS_2, (String) addressLines.elementAt(1));
				case 1 :
					map.put(TenderConstants.ADDRESS_1, (String) addressLines.elementAt(0));
					break;
				default :
					logger.error("TenderMailCheckADO.getTenderAttributes() - no addresses");
				}
			}
		}

		map.put(TenderConstants.BUSINESS_NAME, ((TenderMailBankCheckIfc) tenderRDO).getBusinessName());

		map.put(TenderConstants.BUSINESS_CUSTOMER, new Boolean(busCust));

		map.put(TenderConstants.PHONES, ((TenderMailBankCheckIfc) tenderRDO).getPhones());

		return map;
	}

	/**
	 *
	 * Set values in Tender Mail Bank Check hashmap.
	 *
	 * @param HashMap
	 * @throws TenderException
	 */
	public void setTenderAttributes(HashMap tenderAttributes) throws TenderException
	{
		// get the amount
		CurrencyIfc amount = parseAmount((String) tenderAttributes.get(TenderConstants.AMOUNT));
		((TenderMailBankCheckIfc) tenderRDO).setAmountTender(amount);
		//alternate currency
		//String alternateAmountValue = (String) tenderAttributes.get(TenderConstants.ALTERNATE_AMOUNT);
		//if (alternateAmountValue != null)
		//{
		//    CurrencyIfc alternateAmount = parseAlternateAmount(alternateAmountValue);
		//    ((TenderAlternateCurrencyIfc) tenderRDO).setAlternateCurrencyTendered(alternateAmount);
		//}

		PersonName custName = null;
		Address address = new Address();
		String addressLine = (String) tenderAttributes.get(TenderConstants.ADDRESS_1);
		if (addressLine != null)
		{
			address.addAddressLine(addressLine);
		}
		addressLine = (String) tenderAttributes.get(TenderConstants.ADDRESS_2);
		if (addressLine != null)
		{
			address.addAddressLine(addressLine);
		}
		addressLine = (String) tenderAttributes.get(TenderConstants.ADDRESS_3);
		if (addressLine != null)
		{
			address.addAddressLine(addressLine);
		}

		address.setCity((String) tenderAttributes.get(TenderConstants.CITY));
		address.setCountry((String) tenderAttributes.get(TenderConstants.COUNTRY));
		address.setState((String) tenderAttributes.get(TenderConstants.STATE));
		address.setPostalCode((String) tenderAttributes.get(TenderConstants.POSTAL_CODE_1));
		address.setPostalCodeExtension((String) tenderAttributes.get(TenderConstants.POSTAL_CODE_2));

		int phoneType;
		Boolean isBusinessCustomer = (Boolean) tenderAttributes.get(TenderConstants.BUSINESS_CUSTOMER);
		if (isBusinessCustomer.booleanValue())
		{
			address.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_WORK);
			phoneType = PhoneConstantsIfc.PHONE_TYPE_WORK;
			custName =
	      new PersonName(
	          (String) tenderAttributes.get(TenderConstants.BUSINESS_NAME),"");
		}
		else
		{
			address.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
			phoneType = PhoneConstantsIfc.PHONE_TYPE_HOME;
			custName =
	      new PersonName(
	          (String) tenderAttributes.get(TenderConstants.FIRST_NAME),
	          (String) tenderAttributes.get(TenderConstants.LAST_NAME));
		}

		((TenderMailBankCheckIfc) tenderRDO).setPayeeName(custName);

		// override phone type with value in tender constants if available
		Integer phoneTypeObj = (Integer) tenderAttributes.get(TenderConstants.PHONE_TYPE);
		if (phoneTypeObj != null)
		{
			phoneType = phoneTypeObj.intValue();
		}

		((TenderMailBankCheckIfc) tenderRDO).addAddress(address);

		((TenderMailBankCheckIfc) tenderRDO).setBusinessCustomer(isBusinessCustomer.booleanValue());
		((TenderMailBankCheckIfc) tenderRDO).setBusinessName(
				(String) tenderAttributes.get(TenderConstants.BUSINESS_NAME));

		LocalizedCodeIfc personalIDType = (LocalizedCodeIfc) tenderAttributes.get(TenderConstants.LOCALIZED_ID_TYPE);
		if(personalIDType != null)
		{
			((TenderMailBankCheckIfc) tenderRDO).setPersonalIDType(personalIDType);
		}

		PhoneIfc phone = new Phone();
		phone.setPhoneType(phoneType);
		phone.setPhoneNumber((String) tenderAttributes.get(TenderConstants.PHONE_NUMBER));
		phone.setCountry((String) tenderAttributes.get(TenderConstants.COUNTRY));
		((TenderMailBankCheckIfc) tenderRDO).addPhone(phone);
	}

	/**
	 * Indicates Mail Check is a NOT type of PAT Cash
	 * @return false
	 */
	public boolean isPATCash()
	{
		return false;
	}

	/**
	 * Print out map entries for debugging purposes.
	 *
	 * @param HashMap
	 */
	private void printMap(HashMap attributeMap)
	{
		System.out.println("<<<<<<<<<<<<<Attribute map start - TenderMailCheckADO>>>>>>>>>>>>>>");
		System.out.println(attributeMap.get(TenderConstants.FIRST_NAME));
		System.out.println(attributeMap.get(TenderConstants.LAST_NAME));
		System.out.println(attributeMap.get(TenderConstants.COUNTRY));
		System.out.println(attributeMap.get(TenderConstants.CITY));
		System.out.println(attributeMap.get(TenderConstants.ADDRESS_1));
		System.out.println(attributeMap.get(TenderConstants.ADDRESS_2));
		System.out.println(attributeMap.get(TenderConstants.ADDRESS_3));
		System.out.println(attributeMap.get(TenderConstants.POSTAL_CODE_1));
		System.out.println(attributeMap.get(TenderConstants.POSTAL_CODE_2));
		System.out.println(attributeMap.get(TenderConstants.BUSINESS_CUSTOMER));
		System.out.println(attributeMap.get(TenderConstants.ID_TYPE));
		System.out.println(attributeMap.get(TenderConstants.BUSINESS_NAME));
		System.out.println(attributeMap.get(TenderConstants.PHONE_NUMBER));
		System.out.println("<<<<<<<<<<<<<Attribute map end - TenderMailCheckADO>>>>>>>>>>>>>>>>>");
	}

	/**
	 *
	 * Obtain "memento" of a tender mail bank check ado object.
	 *
	 * @return Map
	 */
	public Map getJournalMemento()
	{
		Map memento = getTenderAttributes();
		// add tender descriptor
		memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
		return memento;
	}

	/**
	 *
	 * Set "rdo" object.
	 *
	 * @param EYSDomainIfc
	 */
	public void fromLegacy(EYSDomainIfc rdo)
	{
		tenderRDO = (TenderLineItemIfc) rdo;
	}

	/**
	 *
	 * Obtain "rdo" object.
	 *
	 * @return EYSDomainIfc
	 */
	public EYSDomainIfc toLegacy()
	{
		return tenderRDO;
	}

	/**
	 *
	 * Obtain "rdo" object for a specific type.
	 *
	 * @return EYSDomainIfc
	 */
	public EYSDomainIfc toLegacy(Class type)
	{
		return toLegacy();
	}
}
