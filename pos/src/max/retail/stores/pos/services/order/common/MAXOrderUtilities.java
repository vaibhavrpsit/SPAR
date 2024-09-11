/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.common;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.order.common.OrderUtilities;

public class MAXOrderUtilities extends OrderUtilities {

	public void journalOrder(OrderIfc order, String transactionID,
			int serviceType, AbstractFinancialCargo cargo,
			ParameterManagerIfc pm) {
		// set the text to be written to the journal depending on the Calling
		// Lane

		// Since the Journal feature is not yet going through I18N changes, we
		// should pull out the English text for display.
		String serviceName = "UNKNOWN SERVICE TYPE";
		if (serviceType != MAXOrderCargoIfc.SERVICE_TYPE_NOT_SET)
			serviceName = MAXOrderCargoIfc.SERVICE_NAME_TEXT_LIST[serviceType];
		else
			logger.warn("unknown service type "
					+ new Object[] { new Integer(serviceType) } + "!!!");

		String serviceNameCAPS = serviceName.toUpperCase() + " ORDER";

		// the date, time and cashier (operator)
		if (serviceType != MAXOrderCargoIfc.SERVICE_PICKUP_TYPE) {
			Date date = new Date();
			Locale defaultLocale = LocaleMap
					.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
			DateTimeServiceIfc dateTimeService = DateTimeServiceLocator
					.getDateTimeService();
			String dateTimeString = dateTimeService.formatDate(date,
					defaultLocale, DateFormat.SHORT)
					+ "               "
					+ dateTimeService.formatTime(date, defaultLocale,
							DateFormat.SHORT);
			journalText.append(dateTimeString);
			journalText.append(getOperatorID(cargo));
		} else {
			journalText.append("\n            ");
		}
		journalText.append(serviceNameCAPS);

		// journal the order header (order #, customer name, order status)
		journalText.append(printOrderInformation(order));

		// If called from Fill Order, journal the order location
		// Otherwise, skip this piece of code
		if (serviceType == MAXOrderCargoIfc.SERVICE_FILL_TYPE) {
			journalText.append("\n  Location: ").append(
					order.getStatus().getLocation());
		}

		// journal the order detail (item #, item description, quantity and
		// price)
		item = order.getOrderLineItems();
		for (int i = 0; i < item.length; i++) {
			journalText.append(printLineItem(item[i]));
		}

		if ((serviceType == MAXOrderCargoIfc.SERVICE_PICKUP_TYPE)
				|| (serviceType == MAXOrderCargoIfc.SERVICE_PRINT_TYPE)
				|| (serviceType == MAXOrderCargoIfc.SERVICE_CANCEL_TYPE)) {
			JournalFormatterManagerIfc formatter = (JournalFormatterManagerIfc) Gateway
					.getDispatcher()
					.getManager(JournalFormatterManagerIfc.TYPE);
			journalText.append(formatter.journalOrderTotals(order, serviceType,
					pm));
		}

		JournalManagerIfc jmi = (JournalManagerIfc) Gateway.getDispatcher()
				.getManager(JournalManagerIfc.TYPE);

		// Journal the entry.
		if (jmi != null) {
			jmi.journal(employeeID, transactionID, journalText.toString());
		} else {
			logger.warn("No journal manager found!");
		}
	}
}
