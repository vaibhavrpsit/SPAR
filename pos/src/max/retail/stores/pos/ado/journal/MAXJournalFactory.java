/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  	Rev 1.0  01/June/2013	Jyoti Rawal, Initial Draft: Changes for Bug 6090 :Incorrect EJ of the transaction in which Hire
*  	Purchase is used as a tender type 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ado.journal;

import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;

/**
 * Class MAXJournalFactory
 * 
 */
public class MAXJournalFactory implements JournalFactoryIfc {

	/**
	 * The cached journal instance
	 */
	protected RegisterJournalIfc registerJournal;

	/**
	 * Singleton instance
	 */
	protected static JournalFactoryIfc instance;

	/**
	 * private to enforce Singleton pattern
	 */
	private MAXJournalFactory() {
	}

	/**
	 * Singleton factory method
	 * 
	 * @return
	 */
	public static JournalFactoryIfc getInstance() throws ADOException {
		final String APP_PROP_GROUP = "application";
		final String UTILITY_KEY = "ado.MAXJournalFactory";
		final String DEFAULT = MAXJournalFactory.class.getName();

		if (instance == null) {
			try {

				String className = Gateway.getProperty(APP_PROP_GROUP, UTILITY_KEY, DEFAULT);

				if (className.length() == 0) {
					throw new ADOException("Failed to find factory class for " + UTILITY_KEY);
				}
				Class utilityClass = Class.forName(className);
				instance = (JournalFactoryIfc) utilityClass.newInstance();
				return instance;
			} catch (ADOException e) {
				throw e;
			} catch (ClassNotFoundException e) {
				throw new ADOException("Factory Class not found for " + UTILITY_KEY, e);
			} catch (InstantiationException e) {
				throw new ADOException("Failed to Instantiate factory for " + UTILITY_KEY, e);
			} catch (IllegalAccessException e) {
				throw new ADOException("IllegalAccessException creating factory for " + UTILITY_KEY, e);
			} catch (NullPointerException e) {
				throw new ADOException("Failed to find class for " + UTILITY_KEY, e);
			} catch (Throwable eth) {
				throw new ADOException("Failed to create factory for " + UTILITY_KEY, eth);
			}
		}
		return instance;
	}

	/**
	 * Journal factory method
	 */
	public RegisterJournalIfc getRegisterJournal() {
		if (registerJournal == null) {
			registerJournal = new MAXRegisterJournal();
		}
		return registerJournal;
	}
}
