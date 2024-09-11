/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadStoresInGroupsByStoreID.java /main/4 2013/09/05 10:36:19 abondala Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* hyin        05/14/12 - check telphone number
* hyin        04/30/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.arts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocalizedText;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.util.DBUtils;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class JdbcReadStoresInGroupsByStoreID extends JdbcDataOperation
implements ARTSDatabaseIfc
{
	
    private static final long serialVersionUID = 2463932832870502950L;

	//---------------------------------------------------------------------
    /**
       Executes the SQL statements against the database.
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc dataAction)
        throws DataException
    {
        JdbcDataConnection dataConn = (JdbcDataConnection) dataConnection;

        StoreIfc[] retrievedStores = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;

        
        if (dataAction.getDataObject() instanceof StringSearchCriteria)
        {
           StringSearchCriteria criteria = (StringSearchCriteria) dataAction.getDataObject();
           String storeID = criteria.getIdentifier();
           String lcl = criteria.getLocaleRequestor().getDefaultLocale().getLanguage();
           
           String selectSQL = "select str.id_str_rt, i8.nm_loc, i8.lcl, addr.ty_ads, addr.a1_cnct,addr.a2_cnct,addr.a3_cnct," +
           		"addr.ci_cnct, addr.st_cnct,addr.pc_cnct,addr.co_cnct,addr.ta_cnct,addr.tl_cnct, addr.cc_cnct from pa_str_rtl str, " +
           		"pa_str_rtl_i8 i8, lo_ads addr where str.id_str_rt = i8.id_str_rt AND str.id_prty = addr.id_prty AND i8.lcl = ? AND" +
           		" str.id_str_rt IN (select unique id_str_rt from st_asctn_strgp_str where id_strgp in (select asctn.id_strgp " +
           		"from st_asctn_strgp_str asctn, co_strgp strgp where asctn.id_str_rt=? and asctn.id_strgp_fnc=0 and " +
           		"asctn.id_strgp=strgp.id_strgp and strgp.ty_strgp=2))";
        
           
           Connection conn = dataConn.getConnection();
           try {
				stmt = conn.prepareStatement(selectSQL);
				stmt.setString(1, lcl);
				stmt.setString(2, storeID);

				rs = stmt.executeQuery();
				
				if (rs != null)
				{
					retrievedStores = parseResultSet(rs);
				}

			} catch (SQLException e) {
				throw new DataException(DataException.SQL_ERROR,
				        "ReadStoresInGroupsByStoreID", e);

			} finally {
				DBUtils.getInstance().closeResultSet(rs);
				DBUtils.getInstance().closeStatement(stmt);
			}
           
        }
        dataTransaction.setResult(retrievedStores);
    }
    
    private StoreIfc[] parseResultSet(ResultSet rs) throws SQLException, DataException
    {
    	ArrayList<StoreIfc> stores = new ArrayList<StoreIfc>();
    	while (rs.next())
    	{
    		StoreIfc store = DomainGateway.getFactory().getStoreInstance();
    		store.setStoreID(getSafeString(rs, 1));
    		LocalizedTextIfc localTxt = new LocalizedText();
    		localTxt.putText(new Locale(rs.getString(FIELD_LOCALE)), rs.getString(FIELD_RETAIL_STORE_LOCATION_NAME));
    		store.setLocalizedLocationNames(localTxt);
    		
    		AddressIfc address = DomainGateway.getFactory().getAddressInstance();
    		address.setAddressType(Integer.parseInt(rs.getString(FIELD_ADDRESS_TYPE_CODE)));
    		Vector<String> addressLines = new Vector<String>(3);
    		addressLines.add(rs.getString(FIELD_CONTACT_ADDRESS_LINE_1));
    		addressLines.add(rs.getString(FIELD_CONTACT_ADDRESS_LINE_2));
    		addressLines.add(rs.getString(FIELD_CONTACT_ADDRESS_LINE_3));
    		address.setLines(addressLines);
    		address.setCity(rs.getString(FIELD_CONTACT_CITY));
    		address.setState(rs.getString(FIELD_CONTACT_STATE));
    		address.setPostalCode(rs.getString(FIELD_CONTACT_POSTAL_CODE));
    		String country = rs.getString(FIELD_CONTACT_COUNTRY);
    		address.setCountry(country);
    		String telCountryCode = rs.getString(FIELD_CONTACT_COUNTRY_TELEPHONE_CODE);
    		store.setAddress(address);
    		
    		
    		PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
    		//phone.setAreaCode(rs.getString(FIELD_CONTACT_AREA_TELEPHONE_CODE));
    		String areaCode = rs.getString(FIELD_CONTACT_AREA_TELEPHONE_CODE);
    		if (areaCode == null)
    		{
    			areaCode = "";
    		}
    		String telNumber = rs.getString(FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER); 
    		if (telNumber == null)
    		{
    			telNumber = "";
    		}
    		phone.setPhoneNumber( areaCode + telNumber);
    				
    		phone.setCountry(telCountryCode);
    		PhoneIfc[] phones = new PhoneIfc[1];
    		phones[0] = phone;
    		store.setPhones(phones);
    		
    		stores.add(store);
    	}
    	
    	StoreIfc[] arrayStores = null;
        if (stores.isEmpty())
        {
            String msg = "JdbcReadStoresInGroupsByStoreID: stores not found.";
            throw new DataException(DataException.NO_DATA, msg);
        }
        else
        {
            // copy vector elements to array
            arrayStores = new StoreIfc[stores.size()];
            arrayStores = stores.toArray(new StoreIfc[stores.size()]);
            
            if (logger.isInfoEnabled())
            {
                logger.info("Matches found:  " + stores.size());
            }
                        
        }

        return arrayStores;
    }
}