/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  	Rev 1.0  01/June/2013	Jyoti Rawal, Initial Draft: Changes for Bug 6090 :Incorrect EJ of the transaction in which Hire
*  	Purchase is used as a tender type 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ado.journal;

import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournal;
import oracle.retail.stores.pos.ado.journal.RegisterJournalFormatterIfc;

public class MAXRegisterJournal extends RegisterJournal {

	/**
	 * Retrieves the formatter from the cache. If it doesn't exist yet, it is
	 * created and cached.
	 * 
	 * @param family
	 *            The key to determining which formatter to use
	 * @return The formatter.
	 */
	protected RegisterJournalFormatterIfc getFormatter(JournalFamilyEnum family) {
		// get the formatter from the map
		RegisterJournalFormatterIfc formatter = (RegisterJournalFormatterIfc) formatterMap.get(family);
		// if we don't have one yet, create it
		if (formatter == null) {
			if (family == JournalFamilyEnum.TENDER) {
				// Returns the MAXTenderFormatter instance
				formatter = new MAXTenderFormatter();
			} else {
				super.getFormatter(family);
			}

			formatterMap.put(family, formatter);
		}
		return formatter;
	}

}
