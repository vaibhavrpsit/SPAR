/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadShippingMethod.java /main/21 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    yiqzhao   10/19/12 - For external order, lineitem in searchcriteria is
 *                         null. Shipping charge is retrieved along with the
 *                         order instead of from the database.
 *    jswan     06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    blarsen   07/15/11 - Fix misspelled word: retrival
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - additional changes for process order flow
 *    acadar    05/17/10 - added call to ExternalOrderMAnager; additional fixes
 *    acadar    05/14/10 - initial version for external order processing
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    sgu       11/17/08 - read tax group id as an integer instead of float
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         5/7/2007 2:21:04 PM    Sandy Gu
 *         enhance shipping method retrieval and internal tax engine to handle
 *         tax rules
 *    5    360Commerce 1.4         4/25/2007 10:01:13 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:46    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
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
 *    Rev 1.0   Aug 29 2003 15:32:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:38:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:54   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:56   msg
 * Initial revision.
 *
 *    Rev 1.5   06 Feb 2002 18:20:46   sfl
 * Use work around to avoid using getInt because
 * Postgresql database doesn't support getInt.
 * Resolution for Domain SCR-28: Porting POS 5.0 to Postgresql
 *
 *    Rev 1.4   11 Jan 2002 16:33:02   sfl
 * Code cleanup based on good suggestions collected during
 * code review.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.3   04 Jan 2002 16:08:02   sfl
 * More comments clean up.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.2   03 Jan 2002 10:28:34   sfl
 * Clean up the comments.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.1   04 Dec 2001 13:38:00   sfl
 * Retrieve two new columns from the shipping method table
 * to have weight based shipping charge rate and
 * flat rate information.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.0   03 Dec 2001 18:14:20   sfl
 * Initial revision.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import oracle.retail.stores.domain.shipping.ShippingChargeIfc;
import oracle.retail.stores.domain.shipping.ShippingItemIfc;
import oracle.retail.stores.domain.shipping.ShippingMethod;
import oracle.retail.stores.domain.shipping.ShippingMethodSearchCriteriaIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;



/**
    This operation reads all of the shipping methods stored in database
    Shipping Method table.
    $Revision: /main/21 $
**/
public class JdbcReadShippingMethod extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
     *
     */
    private static final long serialVersionUID = 6625051046573613699L;
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadShippingMethod.class);

    /**
       Class constructor.
     */
    public JdbcReadShippingMethod()
    {
        super();
        setName("JdbcReadShippingMethod");
    }

    /**
       Executes the SQL statements against the database.
       @param  dataTransaction
       @param  dataConnection
       @param  action
       @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadShippingMethod.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        ShippingMethodSearchCriteriaIfc searchCriteria = (ShippingMethodSearchCriteriaIfc)action.getDataObject();
        
	    // grab arguments and call ReadShippingMethod()
	    Vector<ShippingMethodIfc> shippingMethodVector = readShippingMethod(connection, searchCriteria);
	         
	    if ( searchCriteria.getShippingItems() != null )
	    {
	    	calculateShippingCharge(connection, searchCriteria.getShippingItems(), shippingMethodVector);
	    }
	    ShippingMethodIfc[] shippingMethodList = new ShippingMethodIfc[shippingMethodVector.size()];
	    shippingMethodVector.copyInto(shippingMethodList);
	
	    dataTransaction.setResult(shippingMethodList);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadShippingMethod.execute");
    }



	/**
       Executes the SQL statements against the database.
       @return  A vector of shipping methods
       @param  dataConnection
       @param geoCode the geo code
       @exception DataException upon error
       @deprecated as of 13.3. Use {@link #readShippingMethod(JdbcDataConnection, ShippingMethodSearchCriteriaIfc)}
     */
    public Vector<ShippingMethodIfc> readShippingMethod(JdbcDataConnection connection,
    		String geoCode, LocaleRequestor localeReq) throws DataException
    {
        ShippingMethodSearchCriteriaIfc searchCriteria = DomainGateway.getFactory().getShippingMethodSearchCriteria();
        searchCriteria.setGeoCode(geoCode);
        searchCriteria.setLocaleRequestor(localeReq);
        return readShippingMethod(connection, searchCriteria);

    }

    /**
       Executes the SQL statements against the database.
       @return  A vector of shipping methods
       @param  dataConnection
       @param geoCode the geo code
       @exception DataException upon error
       @deprecated As of 13.1 Use {@link JdbcReadShippingMethod#readShippingMethod(JdbcDataConnection, ShippingMethodSearchCriteriaIfc)}
     */
    public Vector<ShippingMethodIfc> readShippingMethod(JdbcDataConnection connection,
    		String geoCode) throws DataException
    {
    	LocaleRequestor localeReq = new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT));
        ShippingMethodSearchCriteriaIfc searchCriteria = DomainGateway.getFactory().getShippingMethodSearchCriteria();
        searchCriteria.setGeoCode(geoCode);
        searchCriteria.setLocaleRequestor(localeReq);
        return readShippingMethod(connection, searchCriteria);
    }

    /**
     * Reads the shipping method based on a search criteria
     * @param connection
     * @param searchCriteria
     * @return
     * @throws DataException
     */
    public Vector<ShippingMethodIfc> readShippingMethod(JdbcDataConnection connection, ShippingMethodSearchCriteriaIfc searchCriteria) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadShippingMethod.ReadShippingMethod()");

        SQLSelectStatement sql = new SQLSelectStatement();
        LocaleRequestor localeReq = searchCriteria.getLocaleRequestor();

        // add tables
        sql.addTable(TABLE_SHIPPING_METHOD, ALIAS_METHOD);
        sql.addTable(TABLE_SHIPPING_METHOD_I8 + " " + ALIAS_METHOD_I18N + ", " + TABLE_SHIPPING_METHOD_I8 + " " + ALIAS_METHOD_I18N + "_2");

        // add columns
        sql.addColumn(ALIAS_METHOD + "." + FIELD_SHIPPING_METHOD_ID);
        sql.addColumn(ALIAS_METHOD_I18N + "." + FIELD_LOCALE);
        sql.addColumn(ALIAS_METHOD_I18N + "." + FIELD_SHIPPING_CARRIER);
        sql.addColumn(ALIAS_METHOD_I18N + "." + FIELD_SHIPPING_TYPE);
        sql.addColumn(FIELD_SHIPPING_CHARGE);
        sql.addColumn(FIELD_SHIPPING_CHARGE_RATE_BY_WEIGHT);
        sql.addColumn(FIELD_FLAT_RATE);
        //sql.addColumn(FIELD_SHIPPING_TAX_EXEMPT_CODE);
        //sql.addColumn(FIELD_TAX_GROUP_ID);
        sql.addColumn(ALIAS_METHOD + "." + FIELD_SHIPPING_CALCULATION_TYPE);
        sql.addColumn(ALIAS_METHOD_I18N + "_2" + "." + FIELD_SHIPPING_TYPE);

        // add qualifiers
        String qualifier = ALIAS_METHOD + "." + FIELD_SHIPPING_METHOD_ID + " = " + ALIAS_METHOD_I18N + "." + FIELD_SHIPPING_METHOD_ID;
        sql.addQualifier(qualifier);
        qualifier = ALIAS_METHOD_I18N + "." + FIELD_SHIPPING_METHOD_ID + " = " + ALIAS_METHOD_I18N + "_2" + "." + FIELD_SHIPPING_METHOD_ID;
        sql.addQualifier(qualifier);
        qualifier = ALIAS_METHOD_I18N + "_2"  + "." + FIELD_LOCALE + " = " + inQuotes(LocaleMap.getBestMatch(localeReq.getSortByLocale()).toString());
        sql.addQualifier(qualifier);
        Set<Locale> bestMatches = LocaleMap.getBestMatch(null, localeReq.getLocales());
        qualifier = ALIAS_METHOD_I18N + "." + FIELD_LOCALE + " " + buildINClauseString(bestMatches);;
        sql.addQualifier(qualifier);

        if(!StringUtils.isEmpty(searchCriteria.getShippingCarrier()))
        {
            qualifier = ALIAS_METHOD + "." + FIELD_SHIPPING_CARRIER + " = " + makeSafeString(searchCriteria.getShippingCarrier());
            sql.addQualifier(qualifier);
        }

        if(!StringUtils.isEmpty(searchCriteria.getShippingType()))
        {
            qualifier = ALIAS_METHOD + "." + FIELD_SHIPPING_TYPE + " = " + makeSafeString(searchCriteria.getShippingType());
            sql.addQualifier(qualifier);
        }

        // Ordering
        sql.addOrdering(ALIAS_METHOD_I18N + "_2"  + "." +FIELD_SHIPPING_TYPE+ " ASC");
        sql.addOrdering(ALIAS_METHOD + "." + FIELD_SHIPPING_METHOD_ID+ " ASC");

        // Instantiate ShippingMethod
        int rsStatus = 0;
        ShippingMethodIfc currentShippingMethod = null;
        Vector<ShippingMethodIfc> shippingMethodVector = new Vector<ShippingMethodIfc>(1);
        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();


            int index;

            // loop through result set
            while (rs.next())
            {
                ++rsStatus;
                index = 0;
                // parse the data from the database
                Float shippingMethodIDF = new Float(rs.getFloat(++index));
                int shippingMethodID = shippingMethodIDF.intValue();
                Locale locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, ++index));
                String shippingCarrier = getSafeString(rs, ++index);
                String shippingType =  getSafeString(rs, ++index);

                if ((currentShippingMethod == null) ||
                    (currentShippingMethod.getShippingMethodID() != shippingMethodID))
                {
                    // This is a different shipping method
                    CurrencyIfc baseShippingCharge = getCurrencyFromDecimal(rs, ++index);
                    CurrencyIfc shippingChargeRateByWeight = getCurrencyFromDecimal(rs, ++index);
                    CurrencyIfc flatRate = getCurrencyFromDecimal(rs, ++index);

                    int calculationType = rs.getInt(++index);

                    // Instantiate ShippingMethod
                    currentShippingMethod = DomainGateway.getFactory().getShippingMethodInstance();
                    currentShippingMethod.setShippingMethodID(shippingMethodID);
                    currentShippingMethod.setBaseShippingCharge(baseShippingCharge);
                    currentShippingMethod.setShippingChargeRateByWeight(shippingChargeRateByWeight);
                    currentShippingMethod.setFlatRate(flatRate);
                    currentShippingMethod.setShippingChargeCalculationType(calculationType);

                    shippingMethodVector.addElement(currentShippingMethod);
                }

                currentShippingMethod.setShippingCarrier(locale, shippingCarrier);
                currentShippingMethod.setShippingType(locale, shippingType);
            }
            // end loop through result set
            // close result set
            rs.close();

            //assignTaxRules(connection, shippingMethodVector, searchCriteria.getGeoCode());
        }
        catch (DataException de)
        {
            logger.warn(de);
            if (de.getErrorCode() == DataException.UNKNOWN)
            {
                throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
            }
            else
            {
                throw de;
            }
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "readShippingMethod");
            throw new DataException(DataException.SQL_ERROR, "readShippingMethod", se);
        }

        if (rsStatus == 0)
        {
            logger.warn( "No shipping method found");
            throw new DataException(DataException.NO_DATA, "No shipping method found");
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadShippingMethod.ReadShippingMethod()");

        return(shippingMethodVector);

    }
    
    /**
     * Reads the shipping method based on a search criteria
     * @param connection
     * @param searchCriteria
     * @return
     * @throws DataException
     */
    public Vector<oracle.retail.stores.domain.shipping.ShippingMethodIfc> readStoreSendShippingMethod(JdbcDataConnection connection, ShippingMethodSearchCriteriaIfc searchCriteria) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadShippingMethod.ReadShippingMethod()");

        SQLSelectStatement sql = new SQLSelectStatement();
        LocaleRequestor localeReq = searchCriteria.getLocaleRequestor();

        // add tables
        sql.addTable(TABLE_SHIPPING_METHOD, ALIAS_METHOD);
        sql.addTable(TABLE_SHIPPING_METHOD_I8 + " " + ALIAS_METHOD_I18N + ", " + TABLE_SHIPPING_METHOD_I8 + " " + ALIAS_METHOD_I18N + "_2");

        // add columns
        sql.addColumn(ALIAS_METHOD + "." + FIELD_SHIPPING_METHOD_ID);
        sql.addColumn(ALIAS_METHOD_I18N + "." + FIELD_LOCALE);
        sql.addColumn(ALIAS_METHOD_I18N + "." + FIELD_SHIPPING_CARRIER);
        sql.addColumn(ALIAS_METHOD_I18N + "." + FIELD_SHIPPING_TYPE);
        sql.addColumn(FIELD_SHIPPING_CHARGE);
        sql.addColumn(FIELD_SHIPPING_CHARGE_RATE_BY_WEIGHT);
        sql.addColumn(FIELD_FLAT_RATE);
        sql.addColumn(FIELD_SHIPPING_TAX_EXEMPT_CODE);
        sql.addColumn(FIELD_TAX_GROUP_ID);
        sql.addColumn(ALIAS_METHOD_I18N + "_2" + "." + FIELD_SHIPPING_TYPE);
        sql.addColumn(ALIAS_METHOD + "." + FIELD_SHIPPING_CALCULATION_TYPE);

        // add qualifiers
        String qualifier = ALIAS_METHOD + "." + FIELD_SHIPPING_METHOD_ID + " = " + ALIAS_METHOD_I18N + "." + FIELD_SHIPPING_METHOD_ID;
        sql.addQualifier(qualifier);
        qualifier = ALIAS_METHOD_I18N + "." + FIELD_SHIPPING_METHOD_ID + " = " + ALIAS_METHOD_I18N + "_2" + "." + FIELD_SHIPPING_METHOD_ID;
        sql.addQualifier(qualifier);
        qualifier = ALIAS_METHOD_I18N + "_2"  + "." + FIELD_LOCALE + " = " + inQuotes(LocaleMap.getBestMatch(localeReq.getSortByLocale()).toString());
        sql.addQualifier(qualifier);
        Set<Locale> bestMatches = LocaleMap.getBestMatch(null, localeReq.getLocales());
        qualifier = ALIAS_METHOD_I18N + "." + FIELD_LOCALE + " " + buildINClauseString(bestMatches);;
        sql.addQualifier(qualifier);

        if(!StringUtils.isEmpty(searchCriteria.getShippingCarrier()))
        {
            qualifier = ALIAS_METHOD + "." + FIELD_SHIPPING_CARRIER + " = " + makeSafeString(searchCriteria.getShippingCarrier());
            sql.addQualifier(qualifier);
        }

        if(!StringUtils.isEmpty(searchCriteria.getShippingType()))
        {
            qualifier = ALIAS_METHOD + "." + FIELD_SHIPPING_TYPE + " = " + makeSafeString(searchCriteria.getShippingType());
            sql.addQualifier(qualifier);
        }

        // Ordering
        sql.addOrdering(ALIAS_METHOD_I18N + "_2"  + "." +FIELD_SHIPPING_TYPE+ " ASC");
        sql.addOrdering(ALIAS_METHOD + "." + FIELD_SHIPPING_METHOD_ID+ " ASC");

        // Instantiate ShippingMethod
        int rsStatus = 0;
        oracle.retail.stores.domain.shipping.ShippingMethodIfc currentShippingMethod = null;
        Vector<oracle.retail.stores.domain.shipping.ShippingMethodIfc> shippingMethodVector = new Vector<oracle.retail.stores.domain.shipping.ShippingMethodIfc>(1);
        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();


            int index;

            // loop through result set
            while (rs.next())
            {
                ++rsStatus;
                index = 0;
                // parse the data from the database
                Float shippingMethodIDF = new Float(rs.getFloat(++index));
                int shippingMethodID = shippingMethodIDF.intValue();
                Locale locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, ++index));
                String shippingCarrier = getSafeString(rs, ++index);
                String shippingType =  getSafeString(rs, ++index);

                if ((currentShippingMethod == null) ||
                    (currentShippingMethod.getShippingMethodID() != shippingMethodID))
                {
                    // This is a different shipping method
                    CurrencyIfc baseShippingCharge = getCurrencyFromDecimal(rs, ++index);
                    CurrencyIfc shippingChargeRateByWeight = getCurrencyFromDecimal(rs, ++index);
                    CurrencyIfc flatRate = getCurrencyFromDecimal(rs, ++index);
                    boolean taxable = getBooleanFromString(rs, ++index);
                    int taxGroupID = rs.getInt(++index);
                    int calculationType = rs.getInt(++index);

                    // Instantiate ShippingMethod
                    currentShippingMethod = new oracle.retail.stores.domain.shipping.ShippingMethod(); //DomainGateway.getFactory().getShippingMethodInstance();
                    currentShippingMethod.setShippingMethodID(shippingMethodID);
                    currentShippingMethod.setBaseShippingCharge(baseShippingCharge);
                    currentShippingMethod.setShippingChargeRateByWeight(shippingChargeRateByWeight);
                    currentShippingMethod.setFlatRate(flatRate);

                    currentShippingMethod.setShippingChargeCalculationType(calculationType);

                    shippingMethodVector.addElement(currentShippingMethod);
                }

                currentShippingMethod.setShippingCarrier(locale, shippingCarrier);
                currentShippingMethod.setShippingType(locale, shippingType);
            }
            // end loop through result set
            // close result set
            rs.close();

            //assignTaxRules(connection, shippingMethodVector, searchCriteria.getGeoCode());
        }
        catch (DataException de)
        {
            logger.warn(de);
            if (de.getErrorCode() == DataException.UNKNOWN)
            {
                throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
            }
            else
            {
                throw de;
            }
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "readShippingMethod");
            throw new DataException(DataException.SQL_ERROR, "readShippingMethod", se);
        }

        if (rsStatus == 0)
        {
            logger.warn( "No shipping method found");
            throw new DataException(DataException.NO_DATA, "No shipping method found");
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadShippingMethod.ReadShippingMethod()");

        return(shippingMethodVector);

    }    


    
    protected void calculateShippingCharge(JdbcDataConnection connection,
			List<ShippingItemIfc> shippingItems,
			Vector<ShippingMethodIfc> shippingMethodVector) throws DataException
    {
		// TODO Auto-generated method stub
    	ArrayList<ShippingChargeIfc> dollarAmountShippingChargeList = null;
    	String currencyCode = DomainGateway.getBaseCurrencyType().getCountryCode();
    	Iterator iter = shippingMethodVector.iterator();
    	while ( iter.hasNext() )
    	{
    		ShippingMethodIfc shippingMethod = (ShippingMethodIfc)iter.next();
    		
    		if ( shippingMethod.getShippingChargeCalculationType() == ShippingMethod.SHIPPING_CHARGE_BY_WEIGHT )
    		{
    			CurrencyIfc additionalCharge = getByWeightShippingCharge(shippingItems, shippingMethod.getShippingChargeRateByWeight());
    			shippingMethod.setCalculatedShippingCharge(shippingMethod.getBaseShippingCharge().add(additionalCharge));
    		}
    		else if ( shippingMethod.getShippingChargeCalculationType() == ShippingMethod.SHIPPING_CHARGE_BY_AMOUNT )
    		{
    			if ( dollarAmountShippingChargeList == null )
    			{
    				dollarAmountShippingChargeList = readDollarAmountShippingCharges(connection);
    			}
    			CurrencyIfc additionalCharge = getByDollarAmountShippingCharge(dollarAmountShippingChargeList, shippingItems);
    			shippingMethod.setCalculatedShippingCharge(shippingMethod.getBaseShippingCharge().add(additionalCharge));        		
    		}
    		else if ( shippingMethod.getShippingChargeCalculationType() == ShippingMethod.SHIPPING_CHARGE_BY_FLAT_RATE )
    		{
    			shippingMethod.setCalculatedShippingCharge(shippingMethod.getFlatRate());
    		}
    		else
    		{
    			shippingMethod.setCalculatedShippingCharge(DomainGateway.getCurrencyInstance(currencyCode));
    		}
    	}
	}
    
    protected void calculateStoreSendShippingCharge(JdbcDataConnection connection,
			ArrayList<ShippingItemIfc> shippingItems,
			Vector<oracle.retail.stores.domain.shipping.ShippingMethodIfc> shippingMethodVector) throws DataException
    {
		// TODO Auto-generated method stub
    	ArrayList<ShippingChargeIfc> dollarAmountShippingChargeList = null;
    	String currencyCode = DomainGateway.getBaseCurrencyType().getCountryCode();
    	Iterator iter = shippingMethodVector.iterator();
    	while ( iter.hasNext() )
    	{
    		ShippingMethodIfc shippingMethod = (ShippingMethodIfc)iter.next();
    		
    		if ( shippingMethod.getShippingChargeCalculationType() == ShippingMethod.SHIPPING_CHARGE_BY_WEIGHT )
    		{
    			CurrencyIfc additionalCharge = getByWeightShippingCharge(shippingItems, shippingMethod.getShippingChargeRateByWeight());
    			shippingMethod.setCalculatedShippingCharge(shippingMethod.getBaseShippingCharge().add(additionalCharge));
    		}
    		else if ( shippingMethod.getShippingChargeCalculationType() == ShippingMethod.SHIPPING_CHARGE_BY_AMOUNT )
    		{
    			if ( dollarAmountShippingChargeList == null )
    			{
    				dollarAmountShippingChargeList = readDollarAmountShippingCharges(connection);
    			}
    			CurrencyIfc additionalCharge = getByDollarAmountShippingCharge(dollarAmountShippingChargeList, shippingItems);
    			shippingMethod.setCalculatedShippingCharge(shippingMethod.getBaseShippingCharge().add(additionalCharge));
    		}
    		else if ( shippingMethod.getShippingChargeCalculationType() == ShippingMethod.SHIPPING_CHARGE_BY_FLAT_RATE )
    		{
    			shippingMethod.setCalculatedShippingCharge(shippingMethod.getFlatRate());
    		}
    		else
    		{
    			shippingMethod.setCalculatedShippingCharge(DomainGateway.getCurrencyInstance(currencyCode));
    		}
    	}
	}

    protected ArrayList<ShippingChargeIfc> readDollarAmountShippingCharges(
			JdbcDataConnection connection) throws DataException 
	{
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_SHIPPING_CHARGE, ALIAS_CHARGE);

        // add columns
        sql.addColumn(ALIAS_CHARGE + "." + FIELD_LOWER_AMOUNT);
        sql.addColumn(ALIAS_CHARGE + "." + FIELD_UPPER_AMOUNT);
        sql.addColumn(ALIAS_CHARGE + "." + FIELD_CHARGE_AMOUNT);
 

        // Instantiate ShippingMethod
        int rsStatus = 0;
        ShippingChargeIfc currentShippingCharge = null;
        ArrayList<ShippingChargeIfc> shippingCharges = new ArrayList<ShippingChargeIfc>();
        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();

            int index;

            // loop through result set
            while (rs.next())
            {
                ++rsStatus;
                index = 0;
                // parse the data from the database
                CurrencyIfc lowerAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc upperAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc chargeAmount = getCurrencyFromDecimal(rs, ++index); 
                currentShippingCharge = DomainGateway.getFactory().getShippingChargeInstance();
                currentShippingCharge.setLowerAmount(lowerAmount);
                currentShippingCharge.setUpperAmount(upperAmount);
                currentShippingCharge.setChargeAmount(chargeAmount);
                shippingCharges.add(currentShippingCharge);
            }
            // end loop through result set
            // close result set
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de);
            if (de.getErrorCode() == DataException.UNKNOWN)
            {
                throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
            }
            else
            {
                throw de;
            }
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "readShippingCharge");
            throw new DataException(DataException.SQL_ERROR, "readShippingCharge", se);
        }

        if (rsStatus == 0)
        {
            logger.warn( "No shipping method found");
            throw new DataException(DataException.NO_DATA, "No shipping method found");
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadShippingMethod.ReadShippingMethod()");

        return(shippingCharges);
	}

	protected CurrencyIfc getByWeightShippingCharge(List<ShippingItemIfc> shippingItems,
													CurrencyIfc shippingChargeRateByWeight) 
	{
		BigDecimal totalWeight = new BigDecimal(0.0);
    	Iterator iter = shippingItems.iterator();
    	while ( iter.hasNext() )
    	{
    		ShippingItemIfc shippingItem = (ShippingItemIfc)iter.next();
    		totalWeight = totalWeight.add(shippingItem.getItemWeight().multiply(shippingItem.getQuantity()));
    	}
    	return shippingChargeRateByWeight.multiply(totalWeight);
	}
	
	protected CurrencyIfc getItemTotalPrice(List<ShippingItemIfc> shippingItems)
	{
        CurrencyIfc total= DomainGateway.getBaseCurrencyInstance();

        ShippingItemIfc  item = null;
        for ( int i = 0; i < shippingItems.size(); i++)
        {
            item = (ShippingItemIfc) shippingItems.get(i);
            total = total.add(item.getItemTotalExtendedDiscountedPrice());
        }
        return total;
	}
	
    public CurrencyIfc getByDollarAmountShippingCharge(ArrayList<ShippingChargeIfc> shippingChargeList, 
    		List<ShippingItemIfc> shippingItems)
    {
		CurrencyIfc total = getItemTotalPrice(shippingItems);
		for (int i=0; i<shippingChargeList.size(); i++)
		{
			ShippingChargeIfc shippingCharge = shippingChargeList.get(i);
			if ( total.compareTo(shippingCharge.getLowerAmount())>=0 && 
				 total.compareTo(shippingCharge.getUpperAmount())<=0 )
			{
				return DomainGateway.getBaseCurrencyInstance().add(shippingCharge.getChargeAmount());
			}
		}
		return null;
    }
}
