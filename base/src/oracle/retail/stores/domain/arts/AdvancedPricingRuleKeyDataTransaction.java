/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/AdvancedPricingRuleKeyDataTransaction.java /main/13 2011/01/27 19:03:04 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:26 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.6  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.5  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.4  2004/02/12 19:58:06  baa
 *   @scr 0 fix javadoc
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:29:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   14 Jul 2003 02:03:38   mwright
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.discount.AdvancedPricingRuleKeyIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;

import org.apache.log4j.Logger;

/**
 * This class handles the DataTransaction behavior retrieving the
 * AdvacedPricingRule objects from the database.
 * 
 * @version $Revision: /main/13 $
 */
public class AdvancedPricingRuleKeyDataTransaction extends DataTransaction
{
    private static final long serialVersionUID = -8652856934007752632L;

    /** The logger to which log messages will be sent. */
    private final static Logger logger = Logger.getLogger(AdvancedPricingRuleKeyDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "AdvancedPricingRuleKeyDataTransaction";

    /**
     * Class constructor.
     */
    public AdvancedPricingRuleKeyDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     * 
     * @param name data command name
     */
    public AdvancedPricingRuleKeyDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Retrieves a list of ruleIDs that satisfy an
     * AdvancedPricingRuleSearchCriteria .
     * 
     * @param criteria the search criteria object
     * @return a collection of retrieved rules.
     * @exception DataException when an error occurs.
     **/
    public AdvancedPricingRuleKeyIfc[] fetchAdvancedPricingRuleKeys(
                     AdvancedPricingRuleSearchCriteriaIfc criteria)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "AdvancedPricingRuleKeyDataTransaction.fetchKeys");

        AdvancedPricingRuleKeyIfc[] ruleIDs = null;

        applyDataObject(criteria);

        // execute data request
        ruleIDs = (AdvancedPricingRuleKeyIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "" + "AdvancedPricingRuleKeyDataTransaction.fetchKeys" + "");
        return(ruleIDs);
    }

    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class: AdvancedPricingListDataTransaction (Revision "
                                      + getRevisionNumber() + ") @"
                                      + hashCode());
        return(strResult);
    }
}
