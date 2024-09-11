/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadNewTaxRules.java /main/24 2014/07/24 15:23:28 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/22/14 - set tax authority name
 *    rahravin  06/05/14 - set geoCode to taxRulesVO
 *    mjwallac  12/19/13 - fix POS null dereferences (part 1)
 *    mjwallac  12/11/13 - fix null dereferences
 *    abondala  09/04/13 - initialize collections
 *    rgour     03/04/13 - correcting Compound Tax order based on the sequence
 *                         number
 *    rgour     02/28/13 - added capped tax rule
 *    rabhawsa  11/09/12 - tax should be zero if pincode having no tax rules.
 *                         send functionality.
 *    jswan     09/13/12 - Modified to support deprecation of JdbcPLUOperation.
 *    jswan     02/04/12 - Fix issues with loading tax rules when some rules
 *                         have logical errors.
 *    jswan     02/03/12 - XbranchMerge jswan_bug-13599093 from
 *                         rgbustores_13.4x_generic_branch
 *    jswan     01/30/12 - Modified to: 1) provide a more detailed log message
 *                         when a tax rule is invalid, and 2) allow valid tax
 *                         rules to load even if one or more other rules are
 *                         not valid.
 *    sgu       10/04/11 - rework table tax using tax rules instead of
 *                         calculator
 *    vtemker   04/07/11 - Fix for bug # 12333404
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/30/2007 5:38:35 PM   Sandy Gu        added
 *          api to handle inclusive tax
 *    4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:44 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse
 *:
 *    4    .v700     1.2.2.0     11/7/2005 10:00:28     Jason L. DeLeau 4216:
 *         Make all protected methods public for extensibility purposes.
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:44     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.14.2.3  2004/11/10 23:45:22  jdeleau
 *   @scr 7677 Make a value of false correspond to 0 when generating a tax uniqueID
 *
 *   Revision 1.14.2.2  2004/11/10 16:00:14  jdeleau
 *   @scr 7611 Fix Null Pointer Exceptions that may occur during excise
 *   rule tax calculation on a pro-rated basis.
 *
 *   Revision 1.14.2.1  2004/11/09 19:55:02  mwisbauer
 *   @scr 7611 added prorating items accross transaction for same tax rule itmes.
 *
 *   Revision 1.14  2004/09/28 20:58:27  kll
 *   @scr 6644: db2 syntax consideration
 *
 *   Revision 1.13  2004/09/16 14:27:47  jdeleau
 *   @scr 7195 Add percentage support to table based taxes
 *
 *   Revision 1.12  2004/07/29 18:30:17  jdeleau
 *   @scr 6598 Add single quotes around postal code
 *
 *   Revision 1.11  2004/07/26 15:17:37  epd
 *   @scr 6464 made protected method public
 *
 *   Revision 1.10  2004/07/15 01:22:04  kmcbride
 *   @scr 6250: Added single quotes around store id for DB2s liking.
 *
 *   Revision 1.9  2004/07/02 19:11:27  jdeleau
 *   @scr 5982 Support Tax Holiday
 *
 *   Revision 1.8  2004/06/18 22:54:55  jdeleau
 *   @scr 2775 Fix the way uniqueId's were being generated
 *
 *   Revision 1.7  2004/06/18 18:52:00  jdeleau
 *   @scr 2775 Further updates to the way tax is calculated, correcting table tax calculation errors.
 *
 *   Revision 1.6  2004/06/18 13:59:09  jdeleau
 *   @scr 2775 Unify the way rules are generated, so that flat files and
 *   the database use the same business logic
 *
 *   Revision 1.5  2004/06/15 21:13:51  jdeleau
 *   @scr 2775 Fix the way tax is read to be compliant with database
 *   standards
 *
 *   Revision 1.4  2004/06/15 20:41:36  cschellenger
 *   @scr 2775 Tax rules for unknown item
 *
 *   Revision 1.3  2004/06/10 14:21:29  jdeleau
 *   @scr 2775 Use the new tax data for the tax flat files
 *
 *   Revision 1.2  2004/06/07 18:19:31  jdeleau
 *   @scr 2775 Add tax Service, Multiple Geo Codes screens
 *
 *   Revision 1.1  2004/06/03 16:22:41  jdeleau
 *   @scr 2775 Initial Drop of send item tax support.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.factory.DomainObjectFactoryIfc;
import oracle.retail.stores.domain.tax.CappedTaxRuleIfc;
import oracle.retail.stores.domain.tax.ExciseTaxRuleIfc;
import oracle.retail.stores.domain.tax.FixedAmountTaxCalculatorIfc;
import oracle.retail.stores.domain.tax.GeoCodeVO;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.tax.TableTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxCalculatorIfc;
import oracle.retail.stores.domain.tax.TaxRateCalculatorIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.domain.tax.TaxTableLineItemIfc;
import oracle.retail.stores.domain.tax.ValueAddedTaxRuleIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

/**
 * Read in Taxrules from the database, based on GeoCode/PostalCode and
 * TaxGroupID.
 *
 * @since 7.0
 * $Revision: /main/24 $
 */
public class JdbcReadNewTaxRules extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
     * 
     */
    private static final long serialVersionUID = -6131620944510177222L;
    private static final int TAX_RULE_USAGE_CODE_TABLE_RULE = 2;
    private static final int TAX_RULE_USAGE_CODE_THRESHOLD_RULE = 3;
    private static final int TAX_RULE_USAGE_CODE_CAPPED_RULE = 4;

    private static final int TAX_CAL_METHOD_BY_LINE = 1;
    private static final int TAX_CAL_METHOD_PRORATE = 2;

    private static final int TAX_TYPE_CODE_RATE = 1;
    private static final int TAX_TYPE_CODE_FIXED_AMT = 2;

    /**
     *  Default constructor
     */
    public JdbcReadNewTaxRules()
    {
        super();
        setName("JdbcReadNewTaxRules");
    }

    /**
     * Executes the SQL statements against the database.
     * <P>
     * @param  dataTransaction     The data transaction
     * @param  dataConnection      The connection to the data source
     * @param  action              The information passed by the valet
     *
     * @exception DataException upon error
     *
     * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action) throws DataException
    {
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        NewTaxRuleSearchCriteria searchCriteria = (NewTaxRuleSearchCriteria)action.getDataObject();

        TaxRulesVO taxRules = readTaxRules(connection, searchCriteria);

        dataTransaction.setResult(taxRules);
    }

    /**
     *  Retrieve the tax rules
     *
     *  @param connection DatabaseConnection
     *  @param searchCriteria SearchCriteria
     *  @return taxRules value Object
     *
     *  @exception DataException upon error
     */
    public TaxRulesVO readTaxRules(JdbcDataConnection connection, NewTaxRuleSearchCriteria searchCriteria)
      throws DataException
    {
        String geoCode = searchCriteria.getGeoCode();
        TaxRulesVO taxRulesVO = new TaxRulesVO();
        // First convert postal code to geoCode
        if(searchCriteria.getSearchType() == NewTaxRuleSearchCriteria.SEARCH_BY_POSTAL_CODE)
        {
            geoCode = null;
            GeoCodeVO[] geoCodes = readGeoCodeFromPostalCode(connection, searchCriteria.getPostalCode());
            // Only one geoCode for the postalCode, fast path
            if(geoCodes.length == 1)
            {
                geoCode = geoCodes[0].getGeoCode();
            }
            // 0, or more than 1 geoCodes found for a postal code.  Some other dialog
            // will have to show up on the UI.
            else
            {
                taxRulesVO.setGeoCodes(geoCodes);
            }
        }
        else if(searchCriteria.getSearchType() == NewTaxRuleSearchCriteria.SEARCH_BY_STORE)
        {
            GeoCodeVO geoCodeVO = readGeoCodeFromStoreId(connection, searchCriteria.getStoreId());
            geoCode = geoCodeVO.getGeoCode();
        }
        else if(searchCriteria.getSearchType() == NewTaxRuleSearchCriteria.SEARCH_BY_DEPARTMENT)
        {
            int groupId = readGroupIdFromDepartment(connection, searchCriteria.getDepartmentId());
            searchCriteria.addTaxGroupID(groupId);
        }

        // GeoCode was found, (only 1) Now I can get the tax rules
        if(geoCode != null)
        {
            int[] taxGroupIDs = searchCriteria.getTaxGroupIDs();
            if(taxGroupIDs != null && taxGroupIDs.length > 0)
            {
                for(int i=0; i<taxGroupIDs.length; i++)
                {
                    TaxRuleIfc[] rules = retrieveItemTaxRules(connection, taxGroupIDs[i], geoCode);
                    taxRulesVO.addTaxRules(taxGroupIDs[i], rules);
                }
            }
            // Get the tax rules corresponding to each taxGroupID
            else
            {
                ArrayList<Integer> taxGroupIds = retrieveItemTaxRules(connection, geoCode);
                for (int i = 0; i < taxGroupIds.size(); i++)
                {
                    int taxGroupId = ((Integer)taxGroupIds.get(i)).intValue();
                    TaxRuleIfc[] rules = retrieveItemTaxRules(connection, taxGroupId, geoCode);
                    taxRulesVO.addTaxRules(taxGroupId, rules);
				}
                taxRulesVO.setGeoCode(geoCode);
			}
        }
        // Didnt find multiple geoCodes, and didn't find geoCode
        else if(taxRulesVO.getGeoCodes().length < 1)
        {
            throw new DataException(DataException.NO_DATA, "GeoCode not found for search type "+searchCriteria.getSearchType());
        }
        return taxRulesVO;
    }

    /**
     * Return an ArrayList of GeoCodeVO objects.
     *
     *  @param connection Database Connection
     *  @param postalCode postalCodes to get GeoCodes for
     *  @return Array of GeoCodeVo objects, one object for each GeoCode in the given PostalCode.
     *  @throws DataException in case of error.
     */
    public GeoCodeVO[] readGeoCodeFromPostalCode(JdbcDataConnection connection, String postalCode)
      throws DataException
    {
        ArrayList<GeoCodeVO> geoCodes = new ArrayList<GeoCodeVO>();
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_GEO_TAX_JURISDICTION, ALIAS_TABLE_GEO_TAX_JURISDICTION);
        sql.addTable(TABLE_GEO_CODE, ALIAS_TABLE_GEO_CODE);

        // add columns
        sql.addColumn(ALIAS_TABLE_GEO_TAX_JURISDICTION, FIELD_GEO_CODE);
        sql.addColumn(ALIAS_TABLE_GEO_CODE, FIELD_GEO_CODE_NAME);

        // add qualifiers
        sql.addQualifier(ALIAS_TABLE_GEO_TAX_JURISDICTION, FIELD_CONTACT_POSTAL_CODE, makeSafeString(postalCode));
        sql.addJoinQualifier(ALIAS_TABLE_GEO_CODE, FIELD_GEO_CODE, ALIAS_TABLE_GEO_TAX_JURISDICTION, FIELD_GEO_CODE);

        // Execute SQL Query
        try
        {
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();

            // loop through result set
            while (rs.next())
            {
                int index = 0;
                GeoCodeVO geoCodeVO = new GeoCodeVO();
                geoCodeVO.setGeoCode(rs.getString(++index));
                geoCodeVO.setName(rs.getString(++index));
                geoCodes.add(geoCodeVO);
            }
            rs.close();
        }
        catch(SQLException sqlException)
        {
            connection.logSQLException(sqlException, "selectGeoCode");
            throw new DataException(DataException.SQL_ERROR, "selectGeoCode", sqlException);
        }
        return (GeoCodeVO[]) geoCodes.toArray(new GeoCodeVO[0]);
    }

    /**
     *  Read the geoCode based off of storeID.
     *
     *  @param connection databaseConnection
     *  @param storeId storeID to use when retrieving geoCode
     *  @return GeoCodeVO object containing geoCode
     *  @throws DataException If there is a problem reading the data
     */
    public GeoCodeVO readGeoCodeFromStoreId(JdbcDataConnection connection, String storeId)
      throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_RETAIL_STORE, ALIAS_RETAIL_STORE);
        sql.addTable(TABLE_GEO_CODE, ALIAS_TABLE_GEO_CODE);

        // add columns
        sql.addColumn(ALIAS_TABLE_GEO_CODE, FIELD_GEO_CODE);
        sql.addColumn(ALIAS_TABLE_GEO_CODE, FIELD_GEO_CODE_NAME);

        // add qualifiers
        sql.addQualifier(ALIAS_RETAIL_STORE, FIELD_RETAIL_STORE_ID, "'" + storeId + "'");
        sql.addJoinQualifier(ALIAS_TABLE_GEO_CODE, FIELD_GEO_CODE, ALIAS_RETAIL_STORE, FIELD_GEO_CODE);

        // Execute SQL Query
        GeoCodeVO geoCode = new GeoCodeVO();
        try
        {
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();

            // loop through result set
            int index=0;
            while (rs.next())
            {
                geoCode.setGeoCode(rs.getString(++index));
                geoCode.setName(rs.getString(++index));
            }
            rs.close();
        }
        catch(SQLException sqlException)
        {
            connection.logSQLException(sqlException, "selectGeoCode");
            throw new DataException(DataException.SQL_ERROR, "selectGeoCode", sqlException);
        }
        return geoCode;
    }

    /**
     * From the departmentId, find the groupId
     *
     *  @param connection
     *  @param departmentId
     *  @return groupId
     *  @throws DataException
     */
    public int readGroupIdFromDepartment(JdbcDataConnection connection, String departmentId) throws DataException
    {
        int returnValue = 0;
        try
        {
            SQLSelectStatement sql = new SQLSelectStatement();

            sql.addTable(TABLE_POS_DEPARTMENT);

            sql.addColumn(FIELD_TAX_GROUP_ID);

            sql.addQualifier(FIELD_POS_DEPARTMENT_ID, makeSafeString(departmentId));

            connection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)connection.getResult();

            if(rs.next())
            {
                returnValue = rs.getInt(1);
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "The department id (" + departmentId + ") does not exist.");
            }

        }
        catch(SQLException sqlException)
        {
            connection.logSQLException(sqlException, "selectDefaultTaxGroupIdFromDepartment");
            throw new DataException(DataException.SQL_ERROR, "selectDefaultTaxGroupIdFromDepartment", sqlException);
        }
        return returnValue;
    }

    /**
     * Given a taxGroupID, and the results of the query to the flat file,
     * return the set of tax rules that apply. This method contains only business logic
     * to construct the appropriate value objects.  This contains no queries for data.
     *
     *  @param ffTaxVOs collection of flat file tax value objects.
     *  @return tax rules
     *  @throws DataException if there is an error reading any of the data
     */
    public TaxRuleIfc[] retrieveItemTaxRules(ArrayList ffTaxVOs) throws DataException
    {
        // Order by compound sequence number.
        Collections.sort(ffTaxVOs);

        HashMap<String, TaxRuleIfc> taxRules = new HashMap<String, TaxRuleIfc>(1);
        DomainObjectFactoryIfc factory = DomainGateway.getFactory();
        Iterator<FFTaxVO> iterator = ffTaxVOs.iterator();
        boolean taxHolidayRuleExists = false;

        while(iterator.hasNext())
        {
            FFTaxVO ffTaxVO = iterator.next();

            // Setup the tax rule with its attribute data
            StringBuffer uniqueID = new StringBuffer(String.valueOf(ffTaxVO.getTaxAuthorityId()));
            uniqueID.append("-");
            uniqueID.append(ffTaxVO.getTaxGroupId());
            uniqueID.append("-");
            uniqueID.append(ffTaxVO.getTaxType());
            uniqueID.append("-");
            if(ffTaxVO.getTaxHoliday() == false)
            {
                uniqueID.append("0");
            }
            else
            {
                uniqueID.append("1");
            }

            // If the current date doesn't fall in between the effective and expired timestamps,
            // then skip this tax rule as it does not apply.
            EYSDate currentDate = new EYSDate();
            if(ffTaxVO.getTaxRateExpirationTimestamp() != null)
            {
                if(currentDate.after(ffTaxVO.getTaxRateExpirationTimestamp()))
                    continue;
            }
            if(ffTaxVO.getTaxRateEffectiveTimestamp() != null)
            {
                if(currentDate.before(ffTaxVO.getTaxRateEffectiveTimestamp()))
                    continue;
            }

            // Generate the appropriate tax rules.
            TaxRuleIfc taxRule = null;
            boolean exciseTax = false;
            boolean tableTax = false;
            boolean cappedTax = false;
            if(ffTaxVO.getCompoundSequenceNumber() == 0 || ffTaxVO.getCompoundSequenceNumber() == 1)
            {
                if (ffTaxVO.getTaxRateRuleUsageCode() == TAX_RULE_USAGE_CODE_TABLE_RULE)
                {
                    // create or retrieve existing table tax rule
                    if(taxRules.get(uniqueID.toString()) != null)
                    {
                        taxRule = (TaxRuleIfc) taxRules.get(uniqueID.toString());
                    }
                    else
                    {
                        taxRule = factory.getTableTaxRuleInstance();
                    }
                    tableTax = true;
                }
                else if(ffTaxVO.getTaxRateRuleUsageCode() == TAX_RULE_USAGE_CODE_THRESHOLD_RULE)
                {
                    taxRule = factory.getExciseTaxRuleInstance();
                    exciseTax = true;
                }
                else if (ffTaxVO.getTaxRateRuleUsageCode() == TAX_RULE_USAGE_CODE_CAPPED_RULE)
                {
                    taxRule = factory.getCappedTaxRuleInstance();
                    cappedTax = true;
                }
                else
                {
                    boolean valid = true;
                    switch(ffTaxVO.getCalculationMethodCode())
                    {
                        case TAX_CAL_METHOD_BY_LINE:
                            taxRule = factory.getTaxByLineRuleInstance();
                            break;
                        case TAX_CAL_METHOD_PRORATE:
                            taxRule = factory.getTaxProrateRuleInstance();
                            break;
                        default:
                            logger.error(ffTaxVO.getValidationErrorMessage("Invalid tax rule calculation method code:" + ffTaxVO.getCalculationMethodCode()));
                            valid = false;
                    }
                    if (!valid)
                    {
                        continue;
                    }
                }
            }
            else if(ffTaxVO.getCompoundSequenceNumber() == 2 || ffTaxVO.getCompoundSequenceNumber() == 3)
            {
                if(ffTaxVO.getTaxRateRuleUsageCode() == TAX_RULE_USAGE_CODE_TABLE_RULE)
                {
                    logger.error(ffTaxVO.getValidationErrorMessage("Table tax rule can not be a compound sequence number greater than 1, value:"+ffTaxVO.getCompoundSequenceNumber()));
                    continue;
                }
                else if(ffTaxVO.getTaxRateRuleUsageCode() == TAX_RULE_USAGE_CODE_THRESHOLD_RULE)
                {
                    logger.error(ffTaxVO.getValidationErrorMessage("Excise tax rule can not be a compound sequence number greater than 1, value:"+ffTaxVO.getCompoundSequenceNumber()));
                    continue;
                }
                else if (ffTaxVO.getTaxRateRuleUsageCode() == TAX_RULE_USAGE_CODE_CAPPED_RULE)
                {
                    logger.error("Capped tax rule can not be a compound sequence number greater than 1, value:"
                            + ffTaxVO.getCompoundSequenceNumber());
                    continue;
                }

                boolean valid = true;
                ValueAddedTaxRuleIfc valueAddedTaxRule = null;
                switch(ffTaxVO.getCalculationMethodCode())
                {
                    case TAX_CAL_METHOD_BY_LINE:
                        taxRule = valueAddedTaxRule = factory.getValueAddedTaxByLineRuleInstance();
                        break;
                    case TAX_CAL_METHOD_PRORATE:
                        taxRule = valueAddedTaxRule = factory.getValueAddedTaxProrateRuleInstance();
                        break;
                    default:
                        logger.error(ffTaxVO.getValidationErrorMessage("Unknown calculation method:"+ffTaxVO.getCalculationMethodCode()));
                        valid = false;
                }
                if (!valid)
                {
                    continue;
                }
                
                TaxRuleIfc tempTaxRule = null;
                boolean initialRuleFound = false;
                // Find the rule to compound the tax on and set its uniqueID
                int taxRuleToFind = ffTaxVO.getCompoundSequenceNumber() - 1;
                for(Iterator<TaxRuleIfc> iter = taxRules.values().iterator(); iter.hasNext(); )
                {
                    tempTaxRule = iter.next();
                    if(tempTaxRule.getOrder() <= taxRuleToFind && valueAddedTaxRule != null)
                    {
                        valueAddedTaxRule.addValueAddedTaxUniqueId(tempTaxRule.getUniqueID());
                        initialRuleFound = true;
                    }
                    tempTaxRule = null;
                }

                if(!initialRuleFound )
                {
                    logger.error(ffTaxVO.getValidationErrorMessage("Could not find intial rule for compound tax rule."));
                    continue;
                }
            }
            else
            {
                logger.error(ffTaxVO.getValidationErrorMessage("Invalid compound sequence number received for tax rule:"+ffTaxVO.getCompoundSequenceNumber()));
                continue;
            }

            boolean valid = true;
            TaxCalculatorIfc taxCalculator = null;
            switch(ffTaxVO.getTaxTypeCode())
            {
              case TAX_TYPE_CODE_RATE:
              {
                TaxRateCalculatorIfc taxRateCalculator = factory.getTaxRateCalculatorInstance(ffTaxVO.getInclusiveTaxFlag());
                taxRateCalculator.setTaxRate(ffTaxVO.getTaxPercentage().movePointLeft(2));
                taxCalculator = taxRateCalculator;
                break;
              }
              case TAX_TYPE_CODE_FIXED_AMT:
              {
                FixedAmountTaxCalculatorIfc fixedAmountTaxCalculator = factory.getFixedAmountTaxCalculatorInstance();
                fixedAmountTaxCalculator.setTaxAmount(ffTaxVO.getTaxAmount());
                taxCalculator = fixedAmountTaxCalculator;
                break;
              }
              default:
                logger.error(ffTaxVO.getValidationErrorMessage("Unknown type ("+ffTaxVO.getTaxTypeCode()+" received for tax calculator."));
                valid = false;
            }
            if (!valid)
            {
                continue;
            }

            if (tableTax)
            {
                TableTaxRuleIfc tableTaxRule = (TableTaxRuleIfc) taxRule;
                TaxTableLineItemIfc taxTableLineItem = factory.getTaxTableLineItemInstance();
                if (tableTaxRule != null)
                {
                    tableTaxRule.addTaxTableLineItem(taxTableLineItem);
                    tableTaxRule.setProrated(ffTaxVO.getCalculationMethodCode() == 2);
                }
                taxTableLineItem.setTaxCalculator(taxCalculator);
                taxTableLineItem.setMaxTaxableAmount(ffTaxVO.getMaximumTaxableAmount());
            }
            else if (exciseTax)
            {
                ExciseTaxRuleIfc exciseTaxRule = (ExciseTaxRuleIfc) taxRule;
                if (exciseTaxRule != null)
                {
                    exciseTaxRule.setTaxCalculator(taxCalculator);
                    exciseTaxRule.setTaxEntireAmount(ffTaxVO.isTaxAboveThresholdAmountFlag() == false);
                    exciseTaxRule.setThresholdAmount(ffTaxVO.getTaxThresholdAmount());
                    exciseTaxRule.setProrated(ffTaxVO.getCalculationMethodCode() == 2);
                }
            }
            else if (cappedTax)
            {
                CappedTaxRuleIfc cappedTaxRule = (CappedTaxRuleIfc)taxRule;
                // The MaximumTaxableAmount from Tax Rate table is the capped
                // amount for CappedTaxRule
                if (cappedTaxRule != null)
                {
                    cappedTaxRule.setTaxCalculator(taxCalculator);
                    cappedTaxRule.setCappedAmount(ffTaxVO.getMaximumTaxableAmount());
                    cappedTaxRule.setProrated(ffTaxVO.getCalculationMethodCode() == 2);
                }
            }
            else
            {
                taxRule.setTaxCalculator(taxCalculator);
            }


            // Tax calculator was set, now retrieve it and set the rounding information
            if(taxCalculator == null)
            {
                logger.error(ffTaxVO.getValidationErrorMessage("Tax calculator is null."));
                continue;
            }
            else
            {
                taxCalculator.setRoundingMode(ffTaxVO.getRoundingCode());
                taxCalculator.setScale(ffTaxVO.getRoundingDigits());
            }

            // Tax Calculator retrieved
            taxRule.setUniqueID(uniqueID.toString());
            taxRule.setTaxAuthorityID(ffTaxVO.getTaxAuthorityId());
            taxRule.setTaxAuthorityName(ffTaxVO.getTaxAuthorityName());
            taxRule.setTaxGroupID(ffTaxVO.getTaxGroupId());
            taxRule.setTaxTypeCode(ffTaxVO.getTaxType());
            taxRule.setTaxHoliday(ffTaxVO.getTaxHoliday());
            taxRule.setTaxRuleName(ffTaxVO.getTaxRuleName());
            taxRule.setOrder(ffTaxVO.getCompoundSequenceNumber());
            taxRule.setInclusiveTaxFlag(ffTaxVO.getInclusiveTaxFlag());

            //use price before discount
            taxRule.setUseBasePrice(ffTaxVO.isTaxOnGrossAmountFlag());

            if(taxRule.isValid() == false)
            {
                // An error message has already be logged; 
                // just go to the iteration of the loop.
                continue;
            }

            // Put the tax rule in the list
            if(taxRules.get(uniqueID.toString()) == null)
            {
                taxRules.put(uniqueID.toString(), taxRule);
            }
            else if (!tableTax)
            {
                logger.error(ffTaxVO.getValidationErrorMessage("Duplicate rule found for "+taxRule.getUniqueID()));
                continue;
            }

            if(taxRule.isTaxHoliday() == true)
            {
                taxHolidayRuleExists = true;
            }

        }

        Collection<TaxRuleIfc> returnRules = taxRules.values();
        // Now, if we have rules with tax holidays, all rules without tax holidays need to
        // be discarded, but only if they have the same tax group id.
        if(taxHolidayRuleExists)
        {
            returnRules = filterForTaxHoliday(taxRules);
        }

        return (TaxRuleIfc[]) returnRules.toArray(new TaxRuleIfc[0]);
    }

    /**
     * For each taxGroupId, return that rule if it is a tax
     * holiday rule, and discard any rules that are not tax holiday rules.  If,
     * for a given taxGroupId, no tax holiday rules exist then return the normal
     * rules.  This method will return the aggregate of all rules following this
     * algorithm, for each taxGroupId.
     *
     *  @param taxRules Hashmap of tax rules, key uniqueID value taxRule.
     *  @return rules filtered for tax holiday purposes.
     */
    public ArrayList<TaxRuleIfc> filterForTaxHoliday(HashMap<String, TaxRuleIfc> taxRules)
    {
        ArrayList<TaxRuleIfc> returnRules = new ArrayList<TaxRuleIfc>();
        ArrayList<TaxRuleIfc> normalTaxRules = new ArrayList<TaxRuleIfc>();
        HashSet<String> returnTaxGroups = new HashSet<String>();
        returnRules.clear();
        Iterator<String> iter = taxRules.keySet().iterator();
        // Find the tax holiday rules and track what taxGroupId they belong to.
        // Separate the tax holiday from the normal rules.
        while(iter.hasNext())
        {
            String key = iter.next();
            TaxRuleIfc rule = (TaxRuleIfc) taxRules.get(key);
            if(rule.isTaxHoliday())
            {
                returnRules.add(rule);
                returnTaxGroups.add(String.valueOf(rule.getTaxGroupID()));
            }
            else
            {
                normalTaxRules.add(rule);
            }
        }
        // Add any rules that are not covered by a tax holiday rule for
        // their taxGroupId.
        ArrayList<TaxRuleIfc> rulesToAdd = new ArrayList<TaxRuleIfc>();
        for(int i=0; i<normalTaxRules.size(); i++)
        {
            TaxRuleIfc normalTaxRule = (TaxRuleIfc) normalTaxRules.get(i);
            String taxGroupId = String.valueOf(normalTaxRule.getTaxGroupID());
            if(!returnTaxGroups.contains(taxGroupId))
            {
                rulesToAdd.add(normalTaxRule);
            }
        }
        // Add the normal (non-tax-holiday) rules to the tax holiday rules.
        returnRules.addAll(rulesToAdd);
        return returnRules;
    }
    public TaxRuleIfc[] retrieveItemTaxRules(JdbcDataConnection dataConnection, int taxGroupID, String geoCode)
            throws DataException
    {
        ResultSet resultSet = null;
        try
        {
            ArrayList<FFTaxVO> ffTaxVOs = new ArrayList<FFTaxVO>();
            TaxGroupInformationHolder[] taxGroupInformation = retrieveTaxGroupInformation(dataConnection, taxGroupID,
                    geoCode);
            for (int i = 0; i < taxGroupInformation.length; i++)
            {
                FFTaxVO ffTaxVO = new FFTaxVO();
                ffTaxVO.setTaxAuthorityId(taxGroupInformation[i].taxAuthority);
                ffTaxVO.setTaxAuthorityName(taxGroupInformation[i].taxAuthorityName);
                ffTaxVO.setTaxGroupId(taxGroupID);
                ffTaxVO.setTaxType(taxGroupInformation[i].taxType);
                ffTaxVO.setTaxRuleName(taxGroupInformation[i].taxRuleName);
                ffTaxVO.setCompoundSequenceNumber(taxGroupInformation[i].compoundSequenceNumber);
                ffTaxVO.setTaxOnGrossAmountFlag(taxGroupInformation[i].taxOnGrossAmountFlag);
                ffTaxVO.setCalculationMethodCode(taxGroupInformation[i].calculationMethodCode);
                ffTaxVO.setTaxRateRuleUsageCode(taxGroupInformation[i].taxRateUsageCode);
                ffTaxVO.setRoundingCode(taxGroupInformation[i].roundingCode);
                ffTaxVO.setRoundingDigits(taxGroupInformation[i].roundingDigits);
                ffTaxVO.setTaxHoliday(taxGroupInformation[i].taxHoliday);
                ffTaxVO.setInclusiveTaxFlag(taxGroupInformation[i].inclusiveTaxFlag);

                SQLSelectStatement sql = new SQLSelectStatement();
                sql.addTable(TABLE_TAX_RATE_RULE);

                sql.addColumn(FIELD_TYPE_CODE);
                sql.addColumn(FIELD_TAX_PERCENTAGE);
                sql.addColumn(FIELD_TAX_AMOUNT);
                sql.addColumn(FIELD_TAX_ABOVE_THRESHOLD_AMOUNT_FLAG);
                sql.addColumn(FIELD_TAX_THRESHOLD_AMOUNT);
                sql.addColumn(FIELD_MINIMUM_TAXABLE_AMOUNT);
                sql.addColumn(FIELD_MAXIMUM_TAXABLE_AMOUNT);
                sql.addColumn(FIELD_TAX_RATE_EXPIRATION_TIMESTAMP);
                sql.addColumn(FIELD_TAX_RATE_EFFECTIVE_TIMESTAMP);

                sql.addQualifier(new SQLParameterValue(FIELD_TAX_AUTHORITY_ID, taxGroupInformation[i].taxAuthority));
                sql.addQualifier(new SQLParameterValue(FIELD_TAX_GROUP_ID, taxGroupID));
                sql.addQualifier(new SQLParameterValue(FIELD_TAX_TYPE, taxGroupInformation[i].taxType));
                sql.addQualifier(new SQLParameterValue(FIELD_TAX_HOLIDAY, taxGroupInformation[i].taxHoliday));
                sql.addOrdering(FIELD_TAX_RATE_RULE_SEQUENCE_NUMBER);

                resultSet = execute(dataConnection, sql);
                while (resultSet.next())
                {
                    int index = 0;
                    ffTaxVO.setTaxTypeCode(resultSet.getInt(++index));
                    BigDecimal pct = resultSet.getBigDecimal(++index);
                    if (pct != null)
                    {
                        pct.setScale(TAX_PERCENTAGE_SCALE);
                        ffTaxVO.setTaxPercentage(pct);
                    }
                    ffTaxVO.setTaxAmount(getCurrencyFromDecimal(resultSet, ++index));
                    ffTaxVO.setTaxAboveThresholdAmountFlag(getBooleanFromString(resultSet, ++index));
                    ffTaxVO.setTaxThresholdAmount(getCurrencyFromDecimal(resultSet, ++index));
                    ffTaxVO.setMinimumTaxableAmount(getCurrencyFromDecimal(resultSet, ++index));
                    ffTaxVO.setMaximumTaxableAmount(getMaximumTaxableAmount(resultSet.getString(++index)));
                    ffTaxVO.setTaxRateExpirationTimestamp(JdbcDataOperation.dateToEYSDate(resultSet, ++index));
                    ffTaxVO.setTaxRateEffectiveTimestamp(JdbcDataOperation.dateToEYSDate(resultSet, ++index));
                    ffTaxVOs.add(ffTaxVO);
                    ffTaxVO = (FFTaxVO)ffTaxVO.clone();
                }
                resultSet.close();
            }
            JdbcReadNewTaxRules reader = new JdbcReadNewTaxRules();
            return reader.retrieveItemTaxRules(ffTaxVOs);
        }
        catch (SQLException sqlException)
        {
            dataConnection.logSQLException(sqlException, "Error Retrieving Tax Calculator");
            throw new DataException(DataException.SQL_ERROR, "Tax lookup", sqlException);
        }
        finally
        {
            if (resultSet != null)
            {
                try
                {
                    resultSet.close();
                }
                catch (SQLException se)
                {
                    dataConnection.logSQLException(se, "Tax lookup -- Could not close result handle");
                }
            }
        }
    }

    /*
     * This method returns null if the database value is null. This is required
     * by the Tax Table processing. If the database value is not null, it
     * returns the value as a currency object.
     * @param amount String
     * @return CurrencyIfc
     */
    protected CurrencyIfc getMaximumTaxableAmount(String amount)
    {
        CurrencyIfc c = null;

        if (amount != null)
        {
            c = DomainGateway.getBaseCurrencyInstance(amount);
        }

        return c;
    }

    /**
     * Get information about the taxGroup we want tax information for. Example
     * query.
     * <p>
     * <blockquote>
     * 
     * <pre>
     * SELECT TXJURAUTHLNK.ID_ATHY_TX,
     *     TXRU.TY_TX,
     *     TXRU.FLG_TX_HDY,
     *     TXRU.NM_RU_TX,
     *     TXRU.DE_RU_TX,
     *     TXRU.AI_CMPND,
     *     TXRU.FL_TX_GS_AMT,
     *     TXRU.CD_CAL_MTH,
     *     TXRU.CD_TX_RT_RU_USG,
     *     TXRU.FL_TX_INC,
     *     ATHY.SC_RND,
     *     ATHY.QU_DGT_RND
     * FROM RU_TX_GP TXRU
     * LEFT JOIN PA_ATHY_TX ATHY ON ATHY.ID_ATHY_TX = TXRU.ID_ATHY_TX
     * LEFT JOIN CO_TX_JUR_ATHY_LNK TXJURAUTHLNK ON TXJURAUTHLNK.ID_ATHY_TX = ATHY.ID_ATHY_TX
     * WHERE TXRU.ID_GP_TX = '100'
     * AND TXJURAUTHLNK.ID_CD_GEO = '78729'
     * ORDER BY TXRU.AI_CMPND
     * </pre>
     * 
     * </blockquote>
     * 
     * @param dataConnection
     * @param taxGroupId
     * @param geoCode
     * @return TaxGroup information
     * @throws DataException
     */
    public TaxGroupInformationHolder[] retrieveTaxGroupInformation(JdbcDataConnection dataConnection, int taxGroupId,
            String geoCode) throws DataException
    {
        ResultSet resultSet = null;
        ArrayList<TaxGroupInformationHolder> taxGroupInformations = new ArrayList<TaxGroupInformationHolder>();

        try
        {
            SQLSelectStatement sql = new SQLSelectStatement();
            // add tables
            sql.addTable(TABLE_TAX_GROUP_RULE, ALIAS_TAX_GROUP_RULE);
            // add column

            sql.addColumn(ALIAS_TABLE_TAX_JURISDICTION_AUTH_LNK, FIELD_TAX_AUTHORITY_ID);
            sql.addColumn(ALIAS_TAX_GROUP_RULE, FIELD_TAX_TYPE);
            sql.addColumn(ALIAS_TAX_GROUP_RULE, FIELD_TAX_HOLIDAY);
            sql.addColumn(ALIAS_TAX_GROUP_RULE, FIELD_TAX_RULE_NAME);
            sql.addColumn(ALIAS_TAX_GROUP_RULE, FIELD_TAX_RULE_DESCRIPTION);
            sql.addColumn(ALIAS_TAX_GROUP_RULE, FIELD_COMPOUND_SEQUENCE_NUMBER);
            sql.addColumn(ALIAS_TAX_GROUP_RULE, FIELD_TAX_ON_GROSS_AMOUNT_FLAG);
            sql.addColumn(ALIAS_TAX_GROUP_RULE, FIELD_CALCULATION_METHOD_CODE);
            sql.addColumn(ALIAS_TAX_GROUP_RULE, FIELD_TAX_RATE_RULE_USAGE_CODE);
            sql.addColumn(ALIAS_TAX_GROUP_RULE, FIELD_FLG_TAX_INCLUSIVE);
            sql.addColumn(ALIAS_TAX_AUTHORITY, FIELD_TAX_AUTHORITY_NAME);    
            sql.addColumn(ALIAS_TAX_AUTHORITY, FIELD_ROUNDING_CODE);
            sql.addColumn(ALIAS_TAX_AUTHORITY, FIELD_ROUNDING_DIGITS_QUANTITY);
            // add joins
            sql.addOuterJoinQualifier("LEFT", TABLE_TAX_AUTHORITY + " " + ALIAS_TAX_AUTHORITY, ALIAS_TAX_AUTHORITY,
                    FIELD_TAX_AUTHORITY_ID, ALIAS_TAX_GROUP_RULE, FIELD_TAX_AUTHORITY_ID);
            sql.addOuterJoinQualifier("LEFT", TABLE_TAX_JURISDICTION_AUTH_LNK + " "
                    + ALIAS_TABLE_TAX_JURISDICTION_AUTH_LNK, ALIAS_TABLE_TAX_JURISDICTION_AUTH_LNK,
                    FIELD_TAX_AUTHORITY_ID, ALIAS_TAX_GROUP_RULE, FIELD_TAX_AUTHORITY_ID);
            // add qualifiers
            sql.addQualifier(new SQLParameterValue(ALIAS_TAX_GROUP_RULE, FIELD_TAX_GROUP_ID, taxGroupId));
            sql.addQualifier(new SQLParameterValue(ALIAS_TABLE_TAX_JURISDICTION_AUTH_LNK, FIELD_GEO_CODE, geoCode));

            sql.addOrdering(ALIAS_TAX_GROUP_RULE + "." + FIELD_COMPOUND_SEQUENCE_NUMBER);

            resultSet = execute(dataConnection, sql);

            if (resultSet != null)
            {
                TaxGroupInformationHolder taxGroupInformation = null;
                while (resultSet.next())
                {
                    int index = 0;
                    taxGroupInformation = new TaxGroupInformationHolder();
                    taxGroupInformation.taxAuthority = resultSet.getInt(++index);
                    taxGroupInformation.taxType = resultSet.getInt(++index);
                    taxGroupInformation.taxHoliday = getBooleanFromString(resultSet, ++index);
                    taxGroupInformation.taxRuleName = getSafeString(resultSet, ++index);
                    taxGroupInformation.taxRuleDescription = getSafeString(resultSet, ++index);
                    taxGroupInformation.compoundSequenceNumber = resultSet.getInt(++index);
                    taxGroupInformation.taxOnGrossAmountFlag = getBooleanFromString(resultSet, ++index);
                    taxGroupInformation.calculationMethodCode = resultSet.getInt(++index);
                    taxGroupInformation.taxRateUsageCode = resultSet.getInt(++index);
                    taxGroupInformation.inclusiveTaxFlag = getBooleanFromString(resultSet, ++index);
                    taxGroupInformation.taxAuthorityName = getSafeString(resultSet, ++index);
                    taxGroupInformation.roundingCode = resultSet.getInt(++index);
                    taxGroupInformation.roundingDigits = resultSet.getInt(++index);
                    taxGroupInformations.add(taxGroupInformation);
                }
            }
        }
        catch (SQLException sqlException)
        {
            dataConnection.logSQLException(sqlException, "Error Retrieving Tax Rule");
            throw new DataException(DataException.SQL_ERROR, "Tax lookup", sqlException);
        }
        finally
        {
            if (resultSet != null)
            {
                try
                {
                    resultSet.close();
                }
                catch (SQLException se)
                {
                    dataConnection.logSQLException(se, "Tax lookup -- Could not close result handle");
                }
            }
        }
        return taxGroupInformations.toArray(new TaxGroupInformationHolder[taxGroupInformations.size()]);
    }
    
    /**
     * Request for all tax rules, regardless of taxGroupID. This is to get a set
     * of rules for a store.
     * 
     * @param dataConnection
     * @param geoCode
     * @return taxRules for the geoCode.
     * @throws DataException on error
     */
    public ArrayList<Integer> retrieveItemTaxRules(JdbcDataConnection dataConnection, String geoCode)
            throws DataException
    {
        // Get a list of all taxGroupIDS.
        try
        {
            SQLSelectStatement sql = new SQLSelectStatement();
            sql.addTable(TABLE_TAXABLE_GROUP);
            sql.addColumn(FIELD_TAX_GROUP_ID);

            // Finish getting all taxGroupIds
            ArrayList<Integer> taxGroupIds = new ArrayList<Integer>();
            ResultSet resultSet = execute(dataConnection, sql);
            if (resultSet != null)
            {
                while (resultSet.next())
                {
                    int taxGroupId = resultSet.getInt(1);
                    taxGroupIds.add(Integer.valueOf(taxGroupId));
                }
                resultSet.close();
            }
            return taxGroupIds;
        }
        catch (SQLException sqlException)
        {
            dataConnection.logSQLException(sqlException, "Error Retrieving Tax Group IDS");
            throw new DataException(DataException.SQL_ERROR, "Tax lookup", sqlException);
        }
    }

    /**
     * Executes the SQL Statement.
     * 
     * @param dataConnection a connection to the database
     * @param sql the SQl statement
     * @param int id comparison basis type
     * @return ArrayList of Advanced Pricing rules.
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected static ResultSet execute(JdbcDataConnection dataConnection, SQLSelectStatement sql) throws DataException
    {
        ResultSet rs;
        String sqlString = sql.getSQLString();
        dataConnection.execute(sqlString, sql.getParameterValues());
        rs = (ResultSet)dataConnection.getResult();
        return rs;
    }
}
