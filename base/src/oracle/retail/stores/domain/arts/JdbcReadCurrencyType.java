/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadCurrencyType.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    sgu       03/02/09 - add locale support to retrieve a currency type's
 *                         denominations
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         5/9/2007 9:50:47 AM    Anda D. Cadar   check
 *          in for fixing tests
 *    6    360Commerce 1.5         5/3/2007 3:15:30 PM    Peter J. Fierro Use
 *         currency service and daos.
 *    5    360Commerce 1.4         4/25/2007 10:01:15 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/25/2006 4:11:14 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:42 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:31    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:42     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:16  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:31:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:36:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:47:12   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:33:32   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.0   Sep 20 2001 15:55:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.Connection;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.commerceservices.common.currency.persistence.CurrencyTypeDAOIfc;
import oracle.retail.stores.commerceservices.common.currency.persistence.DenominationDAO;
import oracle.retail.stores.commerceservices.common.currency.persistence.DenominationDAOIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.data.DAOFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This class provides the methods needed to read a currency type.
 * 
 * @see oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadCurrencyType extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadCurrencyType.class);

    /**
     * exchange rate scale
     */
    public static final int EXCHANGE_RATE_SCALE = 6;

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCurrencyType.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // pull the data from the action object
        StringSearchCriteria searchCriteria = (StringSearchCriteria) action.getDataObject();
        LocaleRequestor localeReq = searchCriteria.getLocaleRequestor();
        String countryCode = searchCriteria.getIdentifier();

        //get the type object
        CurrencyTypeIfc retrievedType = readCurrencyType(connection, localeReq, countryCode);
        dataTransaction.setResult(retrievedType);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadCurrencyType.execute()");
    }

    /**
       Returns the currency type instance which matches the
       specified country code.  This retrieves the
       currency data from CO_CNY and the currently effective exchange
       rate data from CO_RT_EXC.  If no currently effective exchange rate
       is found, it is treated as if the currency type is not found
       (a DataException with type NO_DATA is thrown).
       @param  dataConnection  connection to the db
       @param  countryCode country code
       @return currency type matching criteria
       @exception DataException upon error
     */
    public CurrencyTypeIfc readCurrencyType(JdbcDataConnection dataConnection,
    				LocaleRequestor localeReq, String countryCode)
        throws DataException
    {

        CurrencyTypeIfc retrievedType = null;
        try
        {
            Connection connection = dataConnection.getConnection();
            CurrencyTypeDAOIfc currencyTypeDAO = (CurrencyTypeDAOIfc)DAOFactory.createBean(CurrencyTypeDAOIfc.CURRENCY_DAO_BEAN_KEY);
            currencyTypeDAO.setConnection(connection);
            retrievedType = currencyTypeDAO.readCurrencyType(countryCode);
            if(retrievedType != null)
            {
                DenominationDAOIfc denominationDAO = new DenominationDAO(connection);
                currencyTypeDAO.addDenomination(denominationDAO, localeReq, retrievedType);
            }

        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "readCurrencyType", e);
        }

        return(retrievedType);
    }

    /**
       Returns the currency type instance which matches the
       specified country code.  This retrieves the
       currency data from CO_CNY and the currently effective exchange
       rate data from CO_RT_EXC.  If no currently effective exchange rate
       is found, it is treated as if the currency type is not found
       (a DataException with type NO_DATA is thrown).
       @param  dataConnection  connection to the db
       @param  countryCode country code
       @return currency type matching criteria
       @exception DataException upon error
       @deprecated As of 13.1 Use {@link #readCurrencyType(JdbcDataConnection, LocaleRequestor, String)}
     */
    public CurrencyTypeIfc readCurrencyType(JdbcDataConnection dataConnection,
                                            String countryCode)
        throws DataException
    {

    	LocaleRequestor localeReq = new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT));
    	return readCurrencyType(dataConnection, localeReq, countryCode);
    }
}
