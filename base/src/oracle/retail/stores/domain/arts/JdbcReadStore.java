/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadStore.java /main/16 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    yiqzha 03/12/10 - add date as part of the search criteria and store close
 *                      status is 2 not 0.
 *    abonda 01/03/10 - update header date
 *    ohorne 10/22/08 - I18N StoreInfo-related changes
 *
 * ===========================================================================

     $Log:
      5    360Commerce 1.4         8/3/2006 5:23:51 PM    Brett J. Larsen CR
           19009 - workaround for case when an employee is deleted out from
           under the POS - this should not happen - it is not allowed from our
            interfaces - however, external entities have access to our
           database and this was a problem for services

           v7x->360Commerce merge
      4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse
     $:

      5    .v7x      1.3.1.0     6/16/2006 11:11:19 AM  Michael Wisbauer added
           code to handle not finding the employee.

      4    .v700     1.2.1.0     11/16/2005 16:27:34    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
     $
     Revision 1.5  2004/05/27 16:59:22  mkp1
     @scr 2775 Checking in first revision of new tax engine.

     Revision 1.4  2004/02/17 16:18:47  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:32:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:38:06   msg
 * Initial revision.
 *
 *    Rev 1.2   25 Mar 2002 12:29:44   epd
 * Jose asked me to check these in for him.  Updates to use TenderDescriptor
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.5   06 Feb 2002 18:21:32   sfl
 * Use work around to avoid using getInt because
 * Postgresql database doesn't support getInt.
 * Resolution for Domain SCR-28: Porting POS 5.0 to Postgresql
 *
 *    Rev 1.4   25 Jan 2002 09:15:32   epd
 * Removed reference to KB Toys  :)
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.3   08 Jan 2002 14:23:54   sfl
 * Added actual store location information --- city, state, postal code, and country data to the storeIfc object when the POS
 * starts.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.2   03 Dec 2001 16:08:54   epd
 * fixed deprecation message
 * Added code to read valid store safe tender types
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   24 Oct 2001 17:34:26   mpm
 * Merged changes from Pier 1/Virginia ABC effort relating to inventory inquiry.
 *
 *    Rev 1.0   Sep 20 2001 15:55:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:22   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;

// java imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.store.DistrictIfc;
import oracle.retail.stores.domain.store.RegionIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;

//-------------------------------------------------------------------------
/**
    This class provides the methods needed to read from the Store History
    and Store Tender History tables.
    <p>
    @version $Revision: /main/16 $
**/
//-------------------------------------------------------------------------
public abstract class JdbcReadStore extends JdbcDataOperation
                                    implements ARTSDatabaseIfc
{

    private static final long serialVersionUID = 3000855248470347327L;

    // Tender type map
    protected static TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();

    //---------------------------------------------------------------------
    /**
        Returns the current status for the given store and business date.
        <P>
        @param  dataConnection  connection to the db
        @param  storeStatus     the store and business date
        @return status of store, or null if no store history record.
        @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public StoreStatusIfc readStoreStatus(JdbcDataConnection dataConnection,
                                          StoreStatusIfc storeStatus)
                                          throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_STORE_HISTORY, ALIAS_STORE_HISTORY);
        sql.addTable(TABLE_REPORTING_PERIOD, ALIAS_REPORTING_PERIOD);
        sql.addTable(TABLE_BUSINESS_DAY, ALIAS_BUSINESS_DAY);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_STORE_HISTORY_STATUS_CODE);
        sql.addColumn(ALIAS_STORE_HISTORY + "." + FIELD_OPERATOR_ID);
        sql.addColumn(FIELD_STORE_START_DATE_TIMESTAMP);
        sql.addColumn(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE);

        /*
         * Add Qualifiers
         */
        // For the specified store only
        String storeID = storeStatus.getStore().getStoreID();
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(storeID));

        // For the specified business day only
        sql.addQualifier(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE
                         + " = " + dateToSQLDateString(storeStatus.getBusinessDate().dateValue()));

        // Join Store History and Reporting Period
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_REPORTING_PERIOD_TYPE_CODE
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_TYPE_CODE);
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_REPORTING_PERIOD_ID
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_ID);

        // Join Reporting Period and Business Day
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_WEEK_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_WEEK_NUMBER);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_DAY_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_DAY_NUMBER);


        StoreStatusIfc status = null;
        try
        {
            dataConnection.execute(sql.getSQLString());
            StoreStatusIfc[] list = parseResultSet(dataConnection, storeID);

            // should only be one item in the list
            status = list[0];
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readStoreStatus", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readStoreStatus", e);
        }

        return(status);
    }

    //---------------------------------------------------------------------
    /**
        Returns the current status for the given store.  This method looks
        for the last business day in the store history table.
        <P>
        @param  dataConnection  connection to the db
        @param  storeID         the store id
        @return status of store, or null if no store history records.
        @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public StoreStatusIfc readStoreStatus(JdbcDataConnection dataConnection,
                                          String storeID)
                                          throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_STORE_HISTORY, ALIAS_STORE_HISTORY);
        sql.addTable(TABLE_REPORTING_PERIOD, ALIAS_REPORTING_PERIOD);
        sql.addTable(TABLE_BUSINESS_DAY, ALIAS_BUSINESS_DAY);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_STORE_HISTORY_STATUS_CODE);
        sql.addColumn(ALIAS_STORE_HISTORY + "." + FIELD_OPERATOR_ID);
        sql.addColumn(FIELD_STORE_START_DATE_TIMESTAMP);
        sql.addColumn(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE);

        /*
         * Add Qualifiers
         */

        // For the specified store only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(storeID));

        // Join Store History and Reporting Period
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_REPORTING_PERIOD_TYPE_CODE
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_TYPE_CODE);
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_REPORTING_PERIOD_ID
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_ID);

        // Join Reporting Period and Business Day
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_WEEK_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_WEEK_NUMBER);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_DAY_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_DAY_NUMBER);

        /*
         * Add Ordering
         */
        sql.addOrdering(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE + " DESC");

        StoreStatusIfc status = null;
        try
        {
            dataConnection.execute(sql.getSQLString());
            StoreStatusIfc[] list = parseResultSet(dataConnection, storeID);
            // use first item in list
            status = list[0];
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readStoreStatus", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readStoreStatus", e);
        }

        return(status);
    }


    //---------------------------------------------------------------------
    /**
        Returns the current status for the given store.  This method looks
        for the last business day in the store history table.
        <P>
        @param  dataConnection  connection to the db
        @param  storeID         the store id
        @return status of store, or null if no store history records.
        @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public StoreStatusIfc readStoreStatus(JdbcDataConnection dataConnection,
                                          String storeID,
                                          EYSDate businessDate)
                                          throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_STORE_HISTORY, ALIAS_STORE_HISTORY);
        sql.addTable(TABLE_REPORTING_PERIOD, ALIAS_REPORTING_PERIOD);
        sql.addTable(TABLE_BUSINESS_DAY, ALIAS_BUSINESS_DAY);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_STORE_HISTORY_STATUS_CODE);
        sql.addColumn(ALIAS_STORE_HISTORY + "." + FIELD_OPERATOR_ID);
        sql.addColumn(FIELD_STORE_START_DATE_TIMESTAMP);
        sql.addColumn(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE);

        /*
         * Add Qualifiers
         */

        // For the specified store only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(storeID));

        // Join Store History and Reporting Period
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_REPORTING_PERIOD_TYPE_CODE
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_TYPE_CODE);
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_REPORTING_PERIOD_ID
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_ID);

        // Join Reporting Period and Business Day
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_WEEK_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_WEEK_NUMBER);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_DAY_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_DAY_NUMBER);

//      For the specified business day only
        sql.addQualifier(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE
                         + " = " + getBusinessDate(businessDate));


        StoreStatusIfc status = null;
        try
        {
            dataConnection.execute(sql.getSQLString());
            StoreStatusIfc[] list = parseResultSet(dataConnection, storeID);
            // use first item in list
            status = list[0];
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readStoreStatus", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readStoreStatus", e);
        }

        return(status);
    }


    /**
     * Returns the districts for a region.
     * @param dataConnection connection to the db
     * @param regionID the region id
     * @return
     * @throws DataException upon error
     * @deprecated As of release 13.1 use  {@link #readDistricts(JdbcDataConnection, String, LocaleRequestor)}
     */
    public DistrictIfc[] readDistricts(JdbcDataConnection dataConnection,
                                       String regionID)
                                       throws DataException
    {
        return readDistricts(dataConnection, regionID, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }

    /**
     * Returns the districts for a region.
     * @param dataConnection connection to the db
     * @param regionID the region id
     * @param localeRequestor the requested locales
     * @return
     * @throws DataException upon error
     */
    public DistrictIfc[] readDistricts(JdbcDataConnection dataConnection,
                                       String regionID,
                                       LocaleRequestor localeRequestor)
                                       throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_STORE_DISTRICTS, ALIAS_STORE_DISTRICTS);
        sql.addTable(TABLE_STORE_DISTRICTS_I8, ALIAS_STORE_DISTRICTS_I8);

        /*
         * Add desired columns
         */
        sql.addColumn(ALIAS_STORE_DISTRICTS_I8 + "." + FIELD_STORE_DISTRICT_ID);
        sql.addColumn(ALIAS_STORE_DISTRICTS_I8 + "." + FIELD_LOCALE);
        sql.addColumn(ALIAS_STORE_DISTRICTS_I8 + "." + FIELD_STORE_DISTRICT_NAME);

        /*
         * Add Qualifiers
         */
        sql.addQualifier(ALIAS_STORE_DISTRICTS + "." + FIELD_STORE_REGION_ID  + " = " + inQuotes(regionID));
        sql.addQualifier(FIELD_LOCALE + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));
        sql.addQualifier(ALIAS_STORE_DISTRICTS + "." + FIELD_STORE_DISTRICT_ID + " = " + ALIAS_STORE_DISTRICTS_I8 + "." + FIELD_STORE_DISTRICT_ID);

        /*
         * Add Ordering
         */
        sql.addOrdering(ALIAS_STORE_DISTRICTS_I8 + "." + FIELD_STORE_DISTRICT_ID);


        Vector<DistrictIfc> resultVector = new Vector<DistrictIfc>(4);
        ResultSet rs = null;
        try
        {
            dataConnection.execute(sql.getSQLString());

            rs = (ResultSet) dataConnection.getResult();
            if (rs != null)
            {
                DistrictIfc district = null;
                while (rs.next())
                {
                    String identifier = getSafeString(rs, 1);

                    //the ResultSet, is sorted by districtID, and may contain rows for more than one district;
                    //however, since a district's localized data results in one row per locale each row may or
                    //may not be for the same district.
                    //Therefore, we only instantiate a new District when the districtID obtained from the resultSet
                    //doesn't match the current District's identifier.
                    if (district == null || !identifier.equals(district.getIdentifier()))
                    {
                        //new district
                        district = DomainGateway.getFactory().getDistrictInstance();
                        district.setIdentifier(identifier);

                        district.getLocalizedDescriptions().setDefaultLocale(localeRequestor.getDefaultLocale());

                        resultVector.addElement(district);
                    }

                    //add locale and localized description from resultSet to District's localizedDescriptions
                    JdbcDataOperation.parseLocalizedNameResults(rs, district.getLocalizedDescriptions(), 2, 3);
                }
            }
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readRegionDistrict", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readRegionDistrict", e);
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException se)
                {
                    throw new DataException(DataException.SQL_ERROR, "error closing ResultSet", se);
                }
            }
        }

        // copy vector elements to array
        int n = resultVector.size();
        DistrictIfc[] arrayDistricts = new DistrictIfc[n];
        resultVector.copyInto(arrayDistricts);

        return(arrayDistricts);
    }

    //---------------------------------------------------------------------
    /**
        Returns the region with its districts for a store.
        <P>
        @param  dataConnection  connection to the db
        @param  storeID         the store id
        @return status of store, or null if no store history records.
        @exception DataException upon error
        @deprecated As of release 13.1 use  {@link #readRegionDistrict(JdbcDataConnection, StoreIfc, LocaleRequestor)}
    **/
    //---------------------------------------------------------------------
    public StoreIfc readRegionDistrict(JdbcDataConnection dataConnection,
                                          StoreIfc store)
                                          throws DataException
    {
        return readRegionDistrict(dataConnection, store, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }

    /**
     * Returns the region with its districts for a store.
     * @param dataConnection connection to the db
     * @param store the store id
     * @param localeRequestor the requested locales
     * @return status of store, or null if no store history records.
     * @throws DataException upon error
     */
    public StoreIfc readRegionDistrict(JdbcDataConnection dataConnection,
                                          StoreIfc store,
                                          LocaleRequestor localeRequestor)
                                          throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_RETAIL_STORE, ALIAS_RETAIL_STORE);
        sql.addTable(TABLE_RETAIL_STORE_I8, ALIAS_RETAIL_STORE_I8);
        sql.addTable(TABLE_STORE_REGIONS_I8, ALIAS_STORE_REGIONS_I8);
        sql.addTable(TABLE_STORE_DISTRICTS_I8, ALIAS_STORE_DISTRICTS_I8);
        sql.addTable(TABLE_ADDRESS, ALIAS_ADDRESS);


        /*
         * Add desired columns
         */
        sql.addColumn(ALIAS_RETAIL_STORE + "." + FIELD_STORE_REGION_ID);
        sql.addColumn(ALIAS_RETAIL_STORE + "." + FIELD_STORE_DISTRICT_ID);

        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_CITY);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_STATE);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_POSTAL_CODE);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_COUNTRY);
        sql.addColumn(ALIAS_RETAIL_STORE + "." + FIELD_GEO_CODE);

        //localized fields
        sql.addColumn(ALIAS_RETAIL_STORE_I8 + "." + FIELD_LOCALE);
        sql.addColumn(ALIAS_RETAIL_STORE_I8 + "." + FIELD_RETAIL_STORE_LOCATION_NAME);
        sql.addColumn(ALIAS_STORE_REGIONS_I8 + "." + FIELD_STORE_REGION_NAME);
        sql.addColumn(ALIAS_STORE_DISTRICTS_I8 + "." + FIELD_STORE_DISTRICT_NAME);


        /*
         * Add Qualifiers
         */

        // For the specified store only
        sql.addQualifier(ALIAS_RETAIL_STORE + "." + FIELD_RETAIL_STORE_ID + " = " + inQuotes(store.getStoreID()));
        sql.addQualifier(ALIAS_RETAIL_STORE_I8 + "." + FIELD_LOCALE + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));
        sql.addQualifier(ALIAS_RETAIL_STORE_I8 + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_RETAIL_STORE + "." + FIELD_RETAIL_STORE_ID);

        sql.addQualifier(ALIAS_STORE_REGIONS_I8 + "." + FIELD_STORE_REGION_ID + " = " + ALIAS_RETAIL_STORE + "." + FIELD_STORE_REGION_ID);
        sql.addQualifier(ALIAS_STORE_DISTRICTS_I8 + "." + FIELD_STORE_DISTRICT_ID + " = " + ALIAS_RETAIL_STORE + "." + FIELD_STORE_DISTRICT_ID);

        sql.addQualifier(ALIAS_RETAIL_STORE_I8 + "." + FIELD_LOCALE + " = " + ALIAS_STORE_REGIONS_I8 + "." + FIELD_LOCALE);
        sql.addQualifier(ALIAS_RETAIL_STORE_I8 + "." + FIELD_LOCALE + " = " + ALIAS_STORE_DISTRICTS_I8 + "." + FIELD_LOCALE);

        sql.addQualifier(ALIAS_RETAIL_STORE + "." + FIELD_PARTY_ID + " = " + ALIAS_ADDRESS + "." + FIELD_PARTY_ID);

        ResultSet rs = null;
        try
        {
            dataConnection.execute(sql.getSQLString());

            RegionIfc region  = DomainGateway.getFactory().getRegionInstance();
            rs = (ResultSet) dataConnection.getResult();
            if (rs != null)
            {
                if (rs.next())
                {
                    int index = 0;
                    String regionID = getSafeString(rs, ++index);
                    String districtID = getSafeString(rs, ++index);
                    String city = getSafeString(rs, ++index);
                    String state = getSafeString(rs, ++index);
                    String postalCode = getSafeString(rs, ++index);
                    String country = getSafeString(rs, ++index);
                    String geoCode = getSafeString(rs, ++index);

                    DistrictIfc district = DomainGateway.getFactory().getDistrictInstance();
                    AddressIfc address = DomainGateway.getFactory().getAddressInstance();
                    region.setIdentifier(regionID);

                    district.setIdentifier(districtID);
                    store.setStoreRegion(region);
                    store.setStoreDistrict(district);
                    address.setCity(city);
                    address.setState(state);
                    address.setPostalCode(postalCode);
                    address.setCountry(country);
                    store.setAddress(address);
                    store.setGeoCode(geoCode);

                    //handle localized names/descriptions
                    int localeIndex = ++index;
                    int locationIndex = ++index;
                    int regionIndex = ++index;
                    int districtIndex = ++index;

                    LocalizedTextIfc locationNames = DomainGateway.getFactory().getLocalizedText();
                    LocalizedTextIfc regionNames = DomainGateway.getFactory().getLocalizedText();
                    LocalizedTextIfc districtNames = DomainGateway.getFactory().getLocalizedText();
                    do
                    {
                        locationNames =  JdbcDataOperation.parseLocalizedNameResults(rs, locationNames, localeIndex, locationIndex);
                        regionNames   =  JdbcDataOperation.parseLocalizedNameResults(rs, regionNames, localeIndex, regionIndex);
                        districtNames =  JdbcDataOperation.parseLocalizedNameResults(rs, districtNames, localeIndex, districtIndex);
                    }
                    while (rs.next());

                    locationNames.setDefaultLocale(localeRequestor.getDefaultLocale());
                    store.setLocalizedLocationNames(locationNames);

                    regionNames.setDefaultLocale(localeRequestor.getDefaultLocale());
                    region.setLocalizedDescriptions(regionNames);

                    districtNames.setDefaultLocale(localeRequestor.getDefaultLocale());
                    district.setLocalizedDescriptions(districtNames);
                }
            }

            region.setDistrictsInRegion(readDistricts(dataConnection, region.getIdentifier(), localeRequestor));
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readRegionDistrict", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readRegionDistrict", e);
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException se)
                {
                    throw new DataException(DataException.SQL_ERROR, "error closing ResultSet", se);
                }
            }
        }
        return(store);
    }

    //---------------------------------------------------------------------
    /**
        Parses result set and creates store status. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>ResultSet is non-null
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>none
        </UL>
        @param dataConnection data connection
        @param storeID store identifier
        @return StoreStatusIfc object
        @exception SQLException thrown if result set cannot be parsed
    **/
    //---------------------------------------------------------------------
    protected StoreStatusIfc[] parseResultSet(JdbcDataConnection dataConnection,
                                              String storeID)
                                              throws SQLException, DataException
    {                                   // begin parseResultSet()
        Vector<StoreStatusIfc> resultVector = new Vector<StoreStatusIfc>(4);
        StoreIfc store = instantiateStoreIfc();
        store.setStoreID(storeID);
        String operatorID = null;
        StoreStatusIfc arrayStoreStatuses[];
        StoreStatusIfc status = null;
        ResultSet rs = (ResultSet) dataConnection.getResult();
        int recordsFound = 0;

        while (rs.next())
        {
            recordsFound++;
            /*
             * Grab the fields selected from the database
             */
            int index = 0;
            Float statusCodeF = new Float(rs.getFloat(++index));
            int statusCode = statusCodeF.intValue();
            operatorID = getSafeString(rs, ++index);
            EYSDate openTime = timestampToEYSDate(rs, ++index);
            EYSDate businessDate = getEYSDateFromString(rs, ++index);
            status = instantiateStoreStatusIfc();
            status.setStatus(statusCode);
            status.setStore(store);
            status.setOpenTime(openTime);
            status.setBusinessDate(businessDate);
            EmployeeIfc emp = instantiateEmployeeIfc();
            emp.setEmployeeID(operatorID);
            status.setSignOnOperator(emp);
            resultVector.addElement(status);
        }

        // close result set
        rs.close();

        // handle not found
        if (recordsFound == 0)
        {
            String msg = "JdbcReadStoreStatus: status not found.";
            throw new DataException(DataException.NO_DATA, msg);
        }
        // add employee objects
        else
        {
            // copy vector elements to array
            int n = resultVector.size();
            arrayStoreStatuses = new StoreStatusIfc[n];
            resultVector.copyInto(arrayStoreStatuses);
            for (int i = 0; i < n; i++)
            {
                {
                    EmployeeIfc signOnOperator = arrayStoreStatuses[i].getSignOnOperator();
                    operatorID = signOnOperator.getEmployeeID();

                    // Attempt to pull the latest employee information for the sign-on operator
                    // from the DB. If there is an issue with pulling the data, then use the
                    // EmployeeIfc already created. Hopefully this will allow us to continue on
                    // with reading the store status.
                    try
                    {
                        signOnOperator = getEmployee(dataConnection, operatorID);
                    }
                    catch(DataException de)
                    {
                        logger.warn("Unable to retrieve employee " + operatorID +
                                ". Using employee info available to contruct the EmployeeIfc object.");
                        logger.warn("Reason: " + de);
                    }
                    arrayStoreStatuses[i].setSignOnOperator(signOnOperator);
                }


            }
        }

        return(arrayStoreStatuses);
    }                                   // end parseResultSet()

    //---------------------------------------------------------------------
    /**
        Reads the Tenders For Store Safe and returns an ArrayList containing the tenders
        <P>
        @param  dataConnection  connection to the db
        @return ArrayList of Store Safe Tenders, or null if none.
        @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    //public String[] readSafeTenders(JdbcDataConnection dataConnection)
    public TenderDescriptorIfc[] readSafeTenders(JdbcDataConnection dataConnection)
                                          throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        TenderDescriptorIfc[] tenders;

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_STORE_SAFE_TENDER, ALIAS_STORE_SAFE_TENDER);

        /*
         * Add desired columns
        *  We will SELECT DISTINCT in order to insure that we get no duplicates
         */
        sql.addColumn("DISTINCT " + FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_TENDER_SUBTYPE);
        sql.addColumn(FIELD_CURRENCY_ISSUING_COUNTRY_CODE);
        sql.addColumn(FIELD_CURRENCY_ID); // I18N


        try
        {
            dataConnection.execute(sql.getSQLString());
            tenders = parseTenderResultSet(dataConnection);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readStoreTenders", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readStoreTenders", e);
        }

        return(tenders);
    }

    //---------------------------------------------------------------------
    /**
        Parses result set and creates the TendersForStoreSafe ArrayList. <P>
        @param dataConnection data connection
        @return ArrayList object
        @exception SQLException thrown if result set cannot be parsed
    **/
    //---------------------------------------------------------------------
    protected TenderDescriptorIfc[] parseTenderResultSet(JdbcDataConnection dataConnection)
                                              throws SQLException, DataException
    {                                   // begin parseResultSet()
        ArrayList<TenderDescriptorIfc> tenderList = new ArrayList<TenderDescriptorIfc>();

        ResultSet rs = (ResultSet) dataConnection.getResult();
        int recordsFound = 0;

        while (rs.next())
        {
            recordsFound++;
            /*
             * Grab the fields selected from the database
             */
            int index = 0;
            String typeCode = getSafeString(rs, ++index);
            String subType = getSafeString(rs, ++index);
            String countryCode = getSafeString(rs, ++index);
            int currencyID = rs.getInt(++index); // I18N

            TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
            descriptor.setTenderType(getTenderType(typeCode));
            descriptor.setTenderSubType(subType);
            descriptor.setCountryCode(countryCode);
            descriptor.setCurrencyID(currencyID); // I18N

            tenderList.add(descriptor);
        }

        // close result set
        rs.close();

        // handle not found
        if (recordsFound == 0)
        {
            String msg = "JdbcReadStoreStatus: tenders not found.";
            throw new DataException(DataException.NO_DATA, msg);
        }
        TenderDescriptorIfc[] returnArray = (TenderDescriptorIfc[]) tenderList.toArray( new TenderDescriptorIfc[tenderList.size()] );

        return(returnArray);
    }                                   // end parseResultSet()

    //---------------------------------------------------------------------
    /**
        Returns a database safe string for the store id.
        <p>
        @return the store id
    **/
    //---------------------------------------------------------------------
    protected String getStoreID(String storeID)
    {
        return("'" + storeID +"'");
    }

    //---------------------------------------------------------------------
    /**
        Instantiates a store object.
        <p>
        @return new StoreIfc object
    **/
    //---------------------------------------------------------------------
    protected StoreIfc instantiateStoreIfc()
    {
        return(DomainGateway.getFactory().getStoreInstance());
    }

    //---------------------------------------------------------------------
    /**
        Instantiates a store status object.
        <p>
        @return new StoreStatusIfc object
    **/
    //---------------------------------------------------------------------
    protected StoreStatusIfc instantiateStoreStatusIfc()
    {
        return(DomainGateway.getFactory().getStoreStatusInstance());
    }

    //---------------------------------------------------------------------
    /**
        Instantiates a employee object.
        <p>
        @return new EmployeeIfc object
    **/
    //---------------------------------------------------------------------
    protected EmployeeIfc instantiateEmployeeIfc()
    {
        return(DomainGateway.getFactory().getEmployeeInstance());
    }

    //---------------------------------------------------------------------
    /**
       Returns the tender type
       <p>
       @param  tenderType  The type of tender
       @return the tender type
    **/
    //---------------------------------------------------------------------
    protected String getTenderTypeDesc(String tenderTypeCode)
    {
        return tenderTypeMap.getDescriptor(tenderTypeMap.getTypeFromCode(tenderTypeCode));
    }

    //---------------------------------------------------------------------
    /**
       Returns the tender type
       <p>
       @param tenderTypeCode String {@link TenderLineItemIfc}
       @return int tender type
    **/
    //---------------------------------------------------------------------
    protected int getTenderType(String tenderTypeCode)
    {
        int value = tenderTypeMap.getTypeFromCode(tenderTypeCode);

        if (value == -1)
        {
            value = TenderLineItemIfc.TENDER_TYPE_CASH;
        }

        return value;
    }

    //---------------------------------------------------------------------
    /**
       Returns a database safe string for the business date
       <p>
       @param businessDate The date object to get a string value for
       @return the business date
    **/
    //---------------------------------------------------------------------
    protected String getBusinessDate(EYSDate businessDate)
    {
        return(dateToSQLDateString(businessDate.dateValue()));
    }
}
