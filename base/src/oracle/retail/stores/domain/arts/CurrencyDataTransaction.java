/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/CurrencyDataTransaction.java /main/14 2013/02/20 16:04:36 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  02/18/13 - Added call to read Currency Rounding Rule list -
 *                         jpacommand
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       03/02/09 - add locale support to retrieve a currency type's
 *                         denominations
 *    sgu       03/02/09 - use denomination descriptions from its I18n table
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    5    360Commerce 1.4         5/9/2007 9:50:47 AM    Anda D. Cadar   check
 *          in for fixing tests
 *    4    360Commerce 1.3         4/25/2007 10:01:07 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:27:33 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:30 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:16 PM  Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:29:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   May 21 2003 20:06:06   mpm
 * Modified to use convenience method.
 * Resolution for Backoffice SCR-1957: Integrate Kintore code
 *
 *    Rev 1.0   Jun 03 2002 16:34:26   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeListIfc;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.CurrencyRoundingRuleSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.storeservices.entities.currency.CurrencyRoundingRule;

import org.apache.log4j.Logger;

/**
 * The DataTransaction to perform read operations regarding currency.
 * 
 * @version $Revision: /main/14 $
 */
public class CurrencyDataTransaction extends DataTransaction
{
    private static final long serialVersionUID = -8057019290316821599L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(CurrencyDataTransaction.class);

    /**
     * revision number of this class
     */
    public static String revisionNumber = "$Revision: /main/14 $";

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "CurrencyDataTransaction";
    

    /**
     * Class constructor.
     */
    public CurrencyDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     */
    public CurrencyDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Retrieves a currency type by country code and a default business date.
     * The business date is used to retrieve the effective exchange rate. If no
     * effective exchange rate is found, a DataException of type NO_DATA is
     * thrown.
     * 
     * @param countryCode country code
     * @return CurrencyTypeIfc object
     * @exception DataException when an error occurs
     * @see oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc
     */
    public CurrencyTypeIfc readCurrencyType(LocaleRequestor localeReq, String countryCode) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("CurrencyDataTransaction.readCurrencyType");

        // set data actions and execute
        StringSearchCriteria criteria = new StringSearchCriteria(localeReq, countryCode);

        DataAction da = new DataAction();
        da.setDataOperationName("ReadCurrencyType");
        da.setDataObject(criteria);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = da;

        setDataActions(dataActions);
        CurrencyTypeIfc retrievedType = (CurrencyTypeIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("CurrencyDataTransaction.readCurrencyType");

        return retrievedType;

    }

    /**
     * Retrieves a currency type list for a given business date. The business
     * date is used to retrieve the effective exchange rate. If no effective
     * exchange rate is found, a currency is not included in the list.
     * 
     * @return CurrencyTypeListIfc object
     * @exception DataException when an error occurs
     * @see oracle.retail.stores.commerceservices.common.currency.CurrencyTypeListIfc
     */
    public CurrencyTypeListIfc readCurrencyTypeList(LocaleRequestor localeReq) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("CurrencyDataTransaction.readCurrencyTypeList");

        // set data actions and execute
        DataAction da = new DataAction();
        da.setDataOperationName("ReadCurrencyTypeList");
        da.setDataObject(localeReq);
        setDataActions(new DataActionIfc[] { da });

        CurrencyTypeListIfc retrievedTypeList = (CurrencyTypeListIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("CurrencyDataTransaction.readCurrencyTypeList");

        return (retrievedTypeList);
    }
    
    /**
     * Retrieves the Currency Rounding Rules
     * 
     * @param CurrencyRoundingRuleSearchCriteriaIfc Search Criteria for Currency Rounding Rules
     * @return List<CurrencyRoundingRule> List of Currency Rounding Rules
     * @throws DataException
     */

    public List<CurrencyRoundingRule> readCurrencyRoundingRuleList(CurrencyRoundingRuleSearchCriteriaIfc currencyRoundingRuleSearchCriteria) throws DataException
    {
        logger.debug("CurrencyDataTransaction/CurrencyRoundingRuleDataTransaction.readCurrencyRoundingRuleList");
        
        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadCurrencyRoundingRuleList");
       
        da.setDataObject(currencyRoundingRuleSearchCriteria);
        dataActions[0] = da;
        setDataActions(dataActions);

        @SuppressWarnings("unchecked")
        List<CurrencyRoundingRule> currencyRoundingRuleList = (List<CurrencyRoundingRule>) getDataManager().execute(this);

        logger.debug("CustomerReadDataTransaction/CurrencyRoundingRuleDataTransaction.readCurrencyRoundingRuleList");

        return currencyRoundingRuleList;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = Util.classToStringHeader("CurrencyDataTransaction", getRevisionNumber(), hashCode());
        // pass back result
        return (strResult.toString());
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

}