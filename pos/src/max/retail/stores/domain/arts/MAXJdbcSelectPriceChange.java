/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved. 
 *	
 *	Rev 1.1		20 Feb,2017			Nadia Arora		MMRP Changes
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import max.retail.stores.domain.event.MAXItemPriceMaintenanceEventIfc;
import max.retail.stores.domain.event.MAXPriceChangeConstantsIfc;
import max.retail.stores.domain.event.MAXPriceChangeIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.JdbcSelectPriceChange;
import oracle.retail.stores.domain.data.AbstractDBUtils;
import oracle.retail.stores.domain.event.EventConstantsIfc;
import oracle.retail.stores.domain.event.ItemPriceMaintenanceEventIfc;
import oracle.retail.stores.domain.event.PriceChangeConstantsIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;

/**
 * @author cgreene
 * @since 12.0.1
 */
public class MAXJdbcSelectPriceChange extends JdbcSelectPriceChange implements MAXARTSDatabaseIfc
{
    private static final long serialVersionUID = 3181486930203527239L;

    // column aliases
    public static final String EVT_ID = TABLE_EVENT + "."
	    + FIELD_EVENT_EVENT_ID;

    public static final String RST_ID = TABLE_EVENT + "."
	    + FIELD_EVENT_RETAIL_STORE_ID;
    
    
  
    /**
     * Updates a {@link PLUItemIfc} with {@link PriceChangeIfc} information. The
     * item in the data action should contain an item id and store id for search
     * purposes.
     *  
     * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
     */
    public void execute(DataTransactionIfc dt, DataConnectionIfc dc, DataActionIfc da) throws DataException
    {
    	
        JdbcDataConnection connection = (JdbcDataConnection) dc;
        PLUItemIfc searchObj = (PLUItemIfc) da.getDataObject();   
        
        PriceChangeIfc[] changes = readTemporaryPriceChanges(connection, searchObj, Calendar.getInstance()); // this is not timezone safe
        searchObj.setTemporaryPriceChanges(changes);
        changes = readClearancePriceChanges(connection, searchObj, Calendar.getInstance()); // this is not timezone safe
        searchObj.setClearancePriceChanges(changes);
        changes = readPermanentPriceChanges(connection, searchObj, Calendar.getInstance()); // this is not timezone safe
        searchObj.setPermanentPriceChanges(changes);
        dt.setResult(searchObj);
    }

    /**
     * Read a collection of permanent price changes based on the PLUItemIfc specified.
     * The store id and item id attribute of this object are required as
     * search parameters.
     * 
     * @param dataConnection the data connection to use
     * @param pluItem the item to search for its price changes
     * @return PriceChangeIfc[]
     */
    protected PriceChangeIfc[] readPermanentPriceChanges(JdbcDataConnection dataConnection, PLUItemIfc pluItem, Calendar when)
        throws DataException
    {
         try
        {
            // build sql
        	 List<PriceChangeIfc> results;
        	//System.out.println("EmpID 96 kamlesh :"+pluItem);7
        	//System.out.println("maxjdbcselectpricechange 111========== :"+pluItem.getEmpID());
        	 
        	//Changes Starts by Kamlesh Pant for SpecialEmpDiscount
        	 SQLSelectStatement sql = new SQLSelectStatement();
        	 if(pluItem.getEmpID())
        	 {
        		 
        		// System.out.println("107=================");
        		 sql = buildSelectSpecialEmpDiscChanges(pluItem.getStoreID(), pluItem.getItemID(), when);
        		// System.out.println("sql============== "+sql.getSQLString());
        		 dataConnection.execute(sql.getSQLString(), sql.getParameterValues()); 
        		// System.out.println("112=================");
        		 results = parseSpeclEmpDiscPriceChangeResults(dataConnection, pluItem);
				// System.out.println("113 ::============="+results.toString());
				
				
				  if(results.toString().equalsIgnoreCase("[]")) {
				  
				 // System.out.println("118::=================");
				  sql =buildSelectPermanentPriceChanges(pluItem.getStoreID(),pluItem.getItemID(),when); 
				  dataConnection.execute(sql.getSQLString(),sql.getParameterValues());
				  results = parsePermanentPriceChangeResults(dataConnection, pluItem); 
				  }
				 
				 
				 
        	 }
        	//Changes Ends for SpecialEmpDiscount
        	 else
        	 {
        		//System.out.println("126::=================");
        		sql = buildSelectPermanentPriceChanges(pluItem.getStoreID(), pluItem.getItemID(), when);
        		dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
        		//System.out.println("130======");
        		results = parsePermanentPriceChangeResults(dataConnection, pluItem);
        		//System.out.println("132======");
        	 }
        	 //System.out.println("134 Result===============::"+results.toString());
        	// System.out.println("135 Price================= ::"+sql.getSQLString().toString());
            // execute sql
          //  dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
            // parse results
           // List<PriceChangeIfc> results = parsePermanentPriceChangeResults(dataConnection, pluItem);
            
            return results.toArray(new PriceChangeIfc[results.size()]);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readPermanentPriceChanges", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readPermanentPriceChanges", e);
        }
    }

    /**
     * Read a collection of temporary price changes based on the PLUItemIfc specified.
     * The store id and item id attribute of this object are required as
     * search parameters.
     * 
     * @param dataConnection the data connection to use
     * @param pluItem the item to search for its price changes
     * @return PriceChangeIfc[]
     */
    protected PriceChangeIfc[] readTemporaryPriceChanges(JdbcDataConnection dataConnection, PLUItemIfc pluItem, Calendar when)
        throws DataException
    {
        try
        {
            // build sql
            SQLSelectStatement sql = buildSelectTemporaryPriceChanges(pluItem.getStoreID(), pluItem.getItemID(), when);
            // execute sql
            dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
            // parse results
            List<PriceChangeIfc> results = parseTemporaryPriceChangeResults(dataConnection, pluItem);
            return results.toArray(new PriceChangeIfc[results.size()]);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readTemporaryPriceChanges", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readTemporaryPriceChanges", e);
        }
    }
    
    /**
     * Read a collection of clearance price changes based on the PLUItemIfc specified.
     * The store id and item id attribute of this object are required as
     * search parameters.
     * 
     * @param dataConnection the data connection to use
     * @param pluItem the item to search for its price changes
     * @return PriceChangeIfc[]
     */
    protected PriceChangeIfc[] readClearancePriceChanges(JdbcDataConnection dataConnection, PLUItemIfc pluItem, Calendar when)
        throws DataException
    {
        try
        {
            // build sql
            SQLSelectStatement sql = buildSelectClearancePriceChanges(pluItem.getStoreID(), pluItem.getItemID(), when);
            // execute sql
            dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
            // parse results
            List<PriceChangeIfc> results = parseClearancePriceChangeResults(dataConnection, pluItem);
            return results.toArray(new PriceChangeIfc[results.size()]);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readClearancePriceChanges", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readClearancePriceChanges", e);
        }
    }


    /**
     * Read a collection of temporary price changes based on the PLUItemIfc specified.
     * The store id and item id attribute of this object are required as
     * search parameters.
     * 
     * @param dataConnection the data connection to use
     * @param pluItem the item to search for its price changes
     * @return PriceChangeIfc[]
     */
    protected PriceChangeIfc[] readAllTemporaryPriceChanges(JdbcDataConnection dataConnection, PLUItemIfc pluItem, Calendar when)
        throws DataException
    {
        try
        {
            // build sql
            SQLSelectStatement sql = buildSelectAllTemporaryPriceChanges(pluItem.getStoreID(), pluItem.getItemID(), when);
            // execute sql
            dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
            // parse results
            List<PriceChangeIfc> results = parseTemporaryPriceChangeResults(dataConnection, pluItem);
            return results.toArray(new PriceChangeIfc[results.size()]);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readTemporaryPriceChanges", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readTemporaryPriceChanges", e);
        }
    }

    /**
     * Return a SQLSelectStatement object to select permanent price changes
     * based on the store and item id. Example SQL query follows.
     * <p><blockquote><pre>
     * SELECT
     *      E.ID_EV, TY_EV_MNT, TS_EV_MNT_EF, UN_PRI_EV, UN_DG_LS_PRC,
     *      MO_CHN_PRN_UN_PRC, TY_CHN_PRN_UN_PRC, PPCI.MO_OVRD_PRC
     * FROM CO_EV E
     * JOIN CO_EV_MNT EVMNT ON EVMNT.ID_EV = E.ID_EV
     *      AND EVMNT.ID_STR_RT = E.ID_STR_RT
     * JOIN MA_PRC_ITM IPM ON IPM.ID_EV = EVMNT.ID_EV
     *      AND IPM.ID_STR_RT = EVMNT.ID_STR_RT
     * JOIN TR_CHN_PRN_PRC PPC ON PPC.ID_EV = IPM.ID_EV
     *      AND PPC.ID_STR_RT = IPM.ID_STR_RT
     * JOIN MA_ITM_PRN_PRC_ITM PPCI ON PPCI.ID_EV = PPC.ID_EV
     *      AND PPCI.ID_STR_RT = PPC.ID_STR_RT
     * WHERE TS_EV_MNT_EF <= '2009-01-21-14.39.37.640000' 
     * AND PPCI.ID_STR_RT = '00692' 
     * AND PPCI.ID_ITM = '13001227';
     * </pre></blockquote>
     *
     * @param storeId
     * @param itemId
     * @return
     */
    protected SQLSelectStatement buildSelectPermanentPriceChanges(String storeId, String itemId, Calendar when)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(TABLE_EVENT, ALIAS_EVENT);
        // add columns
        sql.addColumn(ALIAS_EVENT, FIELD_EVENT_EVENT_ID);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_TYPE_CODE);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_EXPIRATION_DATE);
        sql.addColumn(FIELD_ITEM_PRICE_MAINTENANCE_EVENT_PRIORITY);
        sql.addColumn(FIELD_ITEM_PRICE_MAINTENANCE_PRICE_LAST_DIGIT);
        sql.addColumn(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT);
        sql.addColumn(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT_TYPE_CODE);
        sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE_ITEM, FIELD_PERMANENT_PRICE_CHANGE_ITEM_PRICE_OVERRIDE_AMOUNT);
        /* 12.0.9IN: MMRP related Changes Starts here */
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE + "."
    		+ FIELD_PERMANENT_PRICE_CHANGE_APPLIED_ON);
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_PERMANENT_PRICE_CHANGE_ITEM_MAXIMUM_RETAIL_PRICE);
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_MRP_ACTIVATION_DATE);
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_MRP_INACTIVATION_DATE);
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_MRP_PRIMARY_STATUS_CODE);
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_MRP_ACTIVE_CODE);
    	/* 12.0.9IN: MMRP related Changes Ends here */
        // joins
        sql.addOuterJoinQualifier(" JOIN " + TABLE_MAINTENANCE_EVENT + " " + ALIAS_MAINTENANCE_EVENT + 
                " ON " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_EVENT + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_EVENT + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_ITEM_PRICE_MAINTENANCE + " " + ALIAS_ITEM_PRICE_MAINTENANCE + 
                " ON " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_PERMANENT_PRICE_CHANGE + " " + ALIAS_PERMANENT_PRICE_CHANGE + 
                " ON " + ALIAS_PERMANENT_PRICE_CHANGE + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_PERMANENT_PRICE_CHANGE + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_PERMANENT_PRICE_CHANGE_ITEM + " " + ALIAS_PERMANENT_PRICE_CHANGE_ITEM + 
                " ON " + ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_PERMANENT_PRICE_CHANGE + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_PERMANENT_PRICE_CHANGE + "." + FIELD_RETAIL_STORE_ID);
        // add qualifiers
        sql.addQualifier(new SQLParameterValue(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE + " <= ?", calendarToTimestamp(when)));
        sql.addQualifier(new SQLParameterValue(ALIAS_PERMANENT_PRICE_CHANGE_ITEM, FIELD_RETAIL_STORE_ID, storeId));
        sql.addQualifier(new SQLParameterValue(ALIAS_PERMANENT_PRICE_CHANGE_ITEM, FIELD_PERMANENT_PRICE_CHANGE_ITEM_ITEM_ID, itemId));
    	/* 12.0.9IN: MMRP related Changes Starts here */
    	sql.addQualifier(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_MRP_ACTIVE_CODE,
    		makeStringFromBoolean(true));
    	/* 12.0.9IN: MMRP related Changes Ends here */
        //ordering
        sql.addOrdering(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE + " desc ");
	
	//Rev 1.1
	/*
	sql.addOrdering(TABLE_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_MRP_ACTIVATION_DATE 
            + " desc");  
    sql.addOrdering(TABLE_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_PERMANENT_PRICE_CHANGE_ITEM_MAXIMUM_RETAIL_PRICE 
            + " desc");*/
			
		
	
	sql.addOrdering(ALIAS_MAINTENANCE_EVENT + "."+ FIELD_MAINTENANCE_EVENT_EVENT_ID + " desc");
					//Rev 1.1
	/* 12.0.9IN: MMRP related Changes Ends here */

        return sql;
    }

    
  //Changes Starts by Kamlesh Pant for SpecialEmpDiscount
    
    protected SQLSelectStatement buildSelectSpecialEmpDiscChanges(String storeId, String itemId, Calendar when)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(TABLE_EVENT, ALIAS_EVENT);
        // add columns
        sql.addColumn(ALIAS_EVENT, FIELD_EVENT_EVENT_ID);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_TYPE_CODE);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_EXPIRATION_DATE);
        sql.addColumn(FIELD_ITEM_PRICE_MAINTENANCE_EVENT_PRIORITY);
        sql.addColumn(FIELD_ITEM_PRICE_MAINTENANCE_PRICE_LAST_DIGIT);
        sql.addColumn(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT);
        sql.addColumn(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT_TYPE_CODE);
        sql.addColumn(ALIAS_SPECIAL_EMP_PRICE, FIELD_PERMANENT_PRICE_CHANGE_ITEM_PRICE_OVERRIDE_AMOUNT);
        /* 12.0.9IN: MMRP related Changes Starts here */
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE + "."
    		+ FIELD_PERMANENT_PRICE_CHANGE_APPLIED_ON);
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_PERMANENT_PRICE_CHANGE_ITEM_MAXIMUM_RETAIL_PRICE);
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_MRP_ACTIVATION_DATE);
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_MRP_INACTIVATION_DATE);
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_MRP_PRIMARY_STATUS_CODE);
    	sql.addColumn(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_MRP_ACTIVE_CODE);
    	
    	sql.addColumn(FIELD_SPECIAL_EMP_DISCOUNT);
    	
    	/* 12.0.9IN: MMRP related Changes Ends here */
        // joins
        sql.addOuterJoinQualifier(" JOIN " + TABLE_MAINTENANCE_EVENT + " " + ALIAS_MAINTENANCE_EVENT + 
                " ON " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_EVENT + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_EVENT + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_ITEM_PRICE_MAINTENANCE + " " + ALIAS_ITEM_PRICE_MAINTENANCE + 
                " ON " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_PERMANENT_PRICE_CHANGE + " " + ALIAS_PERMANENT_PRICE_CHANGE + 
                " ON " + ALIAS_PERMANENT_PRICE_CHANGE + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_PERMANENT_PRICE_CHANGE + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_PERMANENT_PRICE_CHANGE_ITEM + " " + ALIAS_PERMANENT_PRICE_CHANGE_ITEM + 
                " ON " + ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_PERMANENT_PRICE_CHANGE + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_PERMANENT_PRICE_CHANGE + "." + FIELD_RETAIL_STORE_ID);
       //Added by Kamalesh Pant
        
        sql.addOuterJoinQualifier(" JOIN " + TABLE_SPECIAL_EMP_PRICE + " " + ALIAS_SPECIAL_EMP_PRICE + 
                " ON " + ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_SPECIAL_EMP_PRICE + "." + FIELD_RETAIL_STORE_ID +
                " AND " + ALIAS_PERMANENT_PRICE_CHANGE + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_SPECIAL_EMP_PRICE + "." + FIELD_RETAIL_STORE_ID +
                " AND " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_SPECIAL_EMP_PRICE + "." + FIELD_RETAIL_STORE_ID +
                " AND " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_SPECIAL_EMP_PRICE + "." + FIELD_RETAIL_STORE_ID +
                " AND " + ALIAS_EVENT + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_SPECIAL_EMP_PRICE + "." + FIELD_RETAIL_STORE_ID +
                " AND " + ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_PERMANENT_PRICE_CHANGE_ITEM_ITEM_ID + " = " + ALIAS_SPECIAL_EMP_PRICE + "." + FIELD_PERMANENT_PRICE_CHANGE_ITEM_ITEM_ID );
        
        // add qualifiers
        sql.addQualifier(new SQLParameterValue(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE + " <= ?", calendarToTimestamp(when)));
        sql.addQualifier(new SQLParameterValue(ALIAS_PERMANENT_PRICE_CHANGE_ITEM, FIELD_RETAIL_STORE_ID, storeId));
        sql.addQualifier(new SQLParameterValue(ALIAS_PERMANENT_PRICE_CHANGE_ITEM, FIELD_PERMANENT_PRICE_CHANGE_ITEM_ITEM_ID, itemId));
    	/* 12.0.9IN: MMRP related Changes Starts here */
    	sql.addQualifier(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."
    		+ FIELD_MRP_ACTIVE_CODE,
    		makeStringFromBoolean(true));
    	
    	//added by kamlesh
    	sql.addQualifier(ALIAS_PERMANENT_PRICE_CHANGE_ITEM + "."+ FIELD_MRP_PRIMARY_STATUS_CODE,makeStringFromBoolean(true));
    	/* 12.0.9IN: MMRP related Changes Ends here */
        //ordering
        sql.addOrdering(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE + " desc ");
	
	//Rev 1.1
	/*
	sql.addOrdering(TABLE_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_MRP_ACTIVATION_DATE 
            + " desc");  
    sql.addOrdering(TABLE_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_PERMANENT_PRICE_CHANGE_ITEM_MAXIMUM_RETAIL_PRICE 
            + " desc");*/
			
		
	
	sql.addOrdering(ALIAS_MAINTENANCE_EVENT + "."+ FIELD_MAINTENANCE_EVENT_EVENT_ID + " desc");
					//Rev 1.1
	/* 12.0.9IN: MMRP related Changes Ends here */

        return sql;
    }
    
  //Changes Ends for SpecialEmpDiscount
    /**
     * Return a SQLSelectStatement object to select temporary price changes
     * based on the store and item id. Example SQL query follows.
     * <p><blockquote><pre>
     * SELECT
     *      E.ID_EV, TY_EV_MNT, TS_EV_MNT_EF, TS_EV_MNT_EP, UN_PRI_EV,
     *      UN_DG_LS_PRC, MO_CHN_PRN_UN_PRC, TY_CHN_PRN_UN_PRC,
     *      TPCI.MO_OVRD_PRC, ID_PRM, ID_PRM_CMP, ID_PRM_CMP_DTL 
     * FROM CO_EV E
     * JOIN CO_EV_MNT EVMNT ON EVMNT.ID_EV = E.ID_EV
     *      AND EVMNT.ID_STR_RT = E.ID_STR_RT
     * JOIN MA_PRC_ITM IPM ON IPM.ID_EV = EVMNT.ID_EV
     *      AND IPM.ID_STR_RT = EVMNT.ID_STR_RT
     * JOIN TR_CHN_TMP_PRC TPC ON TPC.ID_EV = IPM.ID_EV
     *      AND TPC.ID_STR_RT = IPM.ID_STR_RT
     * JOIN MA_ITM_TMP_PRC_CHN TPCI ON TPCI.ID_EV = TPC.ID_EV
     *      AND TPCI.ID_STR_RT = TPC.ID_STR_RT
     * WHERE TS_EV_MNT_EF <= '2009-01-21-14.39.37.640000' 
     * AND TS_EV_MNT_EP >= '2009-01-21-14.39.43.484000' 
     * AND TCPI.ID_STR_RT = '00692' 
     * AND TCPI.ID_ITM = '13001227';
     * </pre></blockquote>
     * 
     * @param storeId
     * @param itemId
     * @param when
     * @param returnExpired set to true to also retrieve expired price changes.
     * @return
     */
    protected SQLSelectStatement buildSelectTemporaryPriceChanges(String storeId,
              String itemId, Calendar when, boolean returnExpired)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(TABLE_EVENT, ALIAS_EVENT);
        // add columns
        sql.addColumn(ALIAS_EVENT, FIELD_EVENT_EVENT_ID);
        sql.addColumn(ALIAS_EVENT, FIELD_EVENT_NAME);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_TYPE_CODE);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_EXPIRATION_DATE);
        sql.addColumn(FIELD_ITEM_PRICE_MAINTENANCE_EVENT_PRIORITY);
        sql.addColumn(FIELD_ITEM_PRICE_MAINTENANCE_PRICE_LAST_DIGIT);
        sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_SALE_UNIT_AMOUNT);
        sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_SALE_UNIT_AMOUNT_TYPE_CODE);
        sql.addColumn(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM, FIELD_TEMPORARY_PRICE_CHANGE_ITEM_EFFECTIVE_DATE);
        sql.addColumn(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM, FIELD_TEMPORARY_PRICE_CHANGE_ITEM_EXPIRATION_DATE);
        sql.addColumn(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM, FIELD_TEMPORARY_PRICE_CHANGE_ITEM_PRICE_OVERRIDE_AMOUNT);
        sql.addColumn(FIELD_PROMOTION_ID);
        sql.addColumn(FIELD_PROMOTION_COMPONENT_ID);
        sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID);
		sql.addColumn(ALIAS_ITEM_PRICE_MAINTENANCE,
				FIELD_CUSTOMER_PRICING_GROUP_ID);
		/* 12.0.9IN: MMRP related Changes Starts here */
		sql.addColumn(ALIAS_TEMPORARY_PRICE_CHANGE + "."
				+ FIELD_TEMPORARY_PRICE_CHANGE_APPLIED_ON);
		sql.addColumn(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM + "."
				+ FIELD_TEMPORARY_PRICE_CHANGE_ITEM_MAXIMUM_RETAIL_PRICE);
		sql.addColumn(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM + "."
				+ FIELD_MRP_ACTIVATION_DATE);
		sql.addColumn(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM + "."
				+ FIELD_MRP_INACTIVATION_DATE);
		sql.addColumn(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM + "."
				+ FIELD_MRP_PRIMARY_STATUS_CODE);
		sql.addColumn(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM + "."
				+ FIELD_MRP_ACTIVE_CODE);
		/* 12.0.9IN: MMRP related Changes Ends here */
        
        // joins
        sql.addOuterJoinQualifier(" JOIN " + TABLE_MAINTENANCE_EVENT + " " + ALIAS_MAINTENANCE_EVENT + 
                " ON " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_EVENT + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_EVENT + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_ITEM_PRICE_MAINTENANCE + " " + ALIAS_ITEM_PRICE_MAINTENANCE + 
                " ON " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_TEMPORARY_PRICE_CHANGE + " " + ALIAS_TEMPORARY_PRICE_CHANGE + 
                " ON " + ALIAS_TEMPORARY_PRICE_CHANGE + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_TEMPORARY_PRICE_CHANGE + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_TEMPORARY_PRICE_CHANGE_ITEM + " " + ALIAS_TEMPORARY_PRICE_CHANGE_ITEM + 
                " ON " + ALIAS_TEMPORARY_PRICE_CHANGE_ITEM + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_TEMPORARY_PRICE_CHANGE + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_TEMPORARY_PRICE_CHANGE_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_TEMPORARY_PRICE_CHANGE + "." + FIELD_RETAIL_STORE_ID);
        // add qualifiers
        sql.addQualifier(new SQLParameterValue(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM,
                FIELD_TEMPORARY_PRICE_CHANGE_ITEM_EFFECTIVE_DATE + " <= ?", calendarToTimestamp(when)));
    	sql.addQualifier(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM + "."
    			+ FIELD_MRP_ACTIVE_CODE,
    			makeStringFromBoolean(true));
    		/* 12.0.9IN: MMRP related Changes Ends here */

        if (!returnExpired)
        {
            sql.addQualifier(new SQLParameterValue(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM,
                   FIELD_TEMPORARY_PRICE_CHANGE_ITEM_EXPIRATION_DATE + " >= ?", calendarToTimestamp(when)));
        }
        sql.addQualifier(new SQLParameterValue(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM, FIELD_RETAIL_STORE_ID, storeId));
        sql.addQualifier(new SQLParameterValue(ALIAS_TEMPORARY_PRICE_CHANGE_ITEM, FIELD_TEMPORARY_PRICE_CHANGE_ITEM_ITEM_ID, itemId));
        return sql;
    }
    
    
    /**
     * Return a SQLSelectStatement object to select clearance price changes
     * based on the store and item id. Example SQL query follows.
     * <p><blockquote><pre>
     * SELECT
     *      E.ID_EV, TY_EV_MNT, TS_EV_MNT_EF, TS_EV_MNT_EP, UN_PRI_EV,
     *      UN_DG_LS_PRC, MO_CHN_CLR_UN_PRC, TY_CHN_CLR_UN_PRC,
     *      TPCI.MO_OVRD_PRC
     * FROM CO_EV E
     * JOIN CO_EV_MNT EVMNT ON EVMNT.ID_EV = E.ID_EV
     *      AND EVMNT.ID_STR_RT = E.ID_STR_RT
     * JOIN MA_PRC_ITM IPM ON IPM.ID_EV = EVMNT.ID_EV
     *      AND IPM.ID_STR_RT = EVMNT.ID_STR_RT
     * JOIN TR_CHN_TMP_PRC TPC ON TPC.ID_EV = IPM.ID_EV
     *      AND TPC.ID_STR_RT = IPM.ID_STR_RT
     * JOIN MA_ITM_TMP_PRC_CHN TPCI ON TPCI.ID_EV = TPC.ID_EV
     *      AND TPCI.ID_STR_RT = TPC.ID_STR_RT
     * WHERE TS_EV_MNT_EF <= '2009-01-21-14.39.37.640000' 
     * AND TS_EV_MNT_EP >= '2009-01-21-14.39.43.484000' 
     * AND TCPI.ID_STR_RT = '00692' 
     * AND TCPI.ID_ITM = '13001227';
     * </pre></blockquote>
     * 
     * @param storeId
     * @param itemId
     * @param when
     * @return
     */
    protected SQLSelectStatement buildSelectClearancePriceChanges(String storeId,
              String itemId, Calendar when)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(TABLE_EVENT, ALIAS_EVENT);
        // add columns
        sql.addColumn(ALIAS_EVENT, FIELD_EVENT_EVENT_ID);
        sql.addColumn(ALIAS_EVENT, FIELD_EVENT_NAME);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_TYPE_CODE);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE);
        sql.addColumn(FIELD_MAINTENANCE_EVENT_EXPIRATION_DATE);
        sql.addColumn(FIELD_ITEM_PRICE_MAINTENANCE_EVENT_PRIORITY);
        sql.addColumn(FIELD_ITEM_PRICE_MAINTENANCE_PRICE_LAST_DIGIT);
        sql.addColumn(FIELD_CLEARANCE_PRICE_CHANGE_SALE_UNIT_AMOUNT);
        sql.addColumn(FIELD_CLEARANCE_PRICE_CHANGE_SALE_UNIT_AMOUNT_TYPE_CODE);
        sql.addColumn(ALIAS_CLEARANCE_PRICE_CHANGE_ITEM, FIELD_TEMPORARY_PRICE_CHANGE_ITEM_PRICE_OVERRIDE_AMOUNT);
        sql.addColumn(ALIAS_ITEM_PRICE_MAINTENANCE, FIELD_CUSTOMER_PRICING_GROUP_ID);
        // joins
        sql.addOuterJoinQualifier(" JOIN " + TABLE_MAINTENANCE_EVENT + " " + ALIAS_MAINTENANCE_EVENT + 
                " ON " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_EVENT + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_EVENT + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_ITEM_PRICE_MAINTENANCE + " " + ALIAS_ITEM_PRICE_MAINTENANCE + 
                " ON " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_MAINTENANCE_EVENT + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_CLEARANCE_PRICE_CHANGE + " " + ALIAS_CLEARANCE_PRICE_CHANGE + 
                " ON " + ALIAS_CLEARANCE_PRICE_CHANGE + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_CLEARANCE_PRICE_CHANGE + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_ITEM_PRICE_MAINTENANCE + "." + FIELD_RETAIL_STORE_ID);
        sql.addOuterJoinQualifier(" JOIN " + TABLE_CLEARANCE_PRICE_CHANGE_ITEM + " " + ALIAS_CLEARANCE_PRICE_CHANGE_ITEM + 
                " ON " + ALIAS_CLEARANCE_PRICE_CHANGE_ITEM + "." + FIELD_EVENT_EVENT_ID + " = " + ALIAS_CLEARANCE_PRICE_CHANGE + "." + FIELD_EVENT_EVENT_ID +
                " AND " + ALIAS_CLEARANCE_PRICE_CHANGE_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_CLEARANCE_PRICE_CHANGE + "." + FIELD_RETAIL_STORE_ID);
        // add qualifiers
        sql.addQualifier(new SQLParameterValue(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE + " <= ?", calendarToTimestamp(when)));
        
        sql.addQualifier(new SQLParameterValue(ALIAS_CLEARANCE_PRICE_CHANGE_ITEM, FIELD_RETAIL_STORE_ID, storeId));
        sql.addQualifier(new SQLParameterValue(ALIAS_CLEARANCE_PRICE_CHANGE_ITEM, FIELD_TEMPORARY_PRICE_CHANGE_ITEM_ITEM_ID, itemId));
        return sql;
    }

    /**
     * Calls buildSelectTemporaryPriceChanges with <code>false</code> for returnExpired
     */
    protected SQLSelectStatement buildSelectTemporaryPriceChanges(String storeId, String itemId, Calendar when)
    {
        return buildSelectTemporaryPriceChanges(storeId, itemId, when, true);
    }

    /**
     * Calls buildSelectTemporaryPriceChanges with <code>true</code> for returnExpired
     */
    protected SQLSelectStatement buildSelectAllTemporaryPriceChanges(String storeId, String itemId, Calendar when)
    {
        return buildSelectTemporaryPriceChanges(storeId, itemId, when, true);
    }

    protected List<PriceChangeIfc> parsePermanentPriceChangeResults(DataConnectionIfc dataConnection, PLUItemIfc pluItem)
        throws DataException, SQLException
    {
        List<PriceChangeIfc> results = new ArrayList<PriceChangeIfc>();
        List<ItemPriceMaintenanceEventIfc> maintenanceEvents = new ArrayList<ItemPriceMaintenanceEventIfc>();
    
        ResultSet rs = (ResultSet)dataConnection.getResult();
        //System.out.println("628=============");
        if (rs != null)
        {
        	//System.out.println("630=============");
            while (rs.next())
            {
            	//System.out.println("633=================");
                // event id
                int eventID = rs.getInt(FIELD_EVENT_EVENT_ID);
                ItemPriceMaintenanceEventIfc event = getItemPriceMaintenanceEvent(maintenanceEvents, eventID);
                if (event == null)
                {
                    event = createItemPriceMaintenanceEvent(rs, eventID, pluItem.getStoreID(), false);
                    maintenanceEvents.add(event);
                }
                // price change
                MAXPriceChangeIfc priceChange = (MAXPriceChangeIfc) DomainGateway.getFactory().getPriceChangeInstance();
                // override amount
                BigDecimal amt = rs.getBigDecimal(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT);
                if (amt != null)
                {
                    priceChange.setOverridePriceAmount(DomainGateway.getBaseCurrencyInstance(amt));
                }
                // use override price amount instead of sale unit amount if available
                amt = rs.getBigDecimal(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_PRICE_OVERRIDE_AMOUNT);
                if (amt != null)
                {
                    priceChange.setOverridePriceAmount(DomainGateway.getBaseCurrencyInstance(amt));
                }
                //System.out.println("656==========");
                priceChange.setItem(pluItem);                
                /* 12.0.9IN: MMRP related Changes Starts here */
               
                pluItem.setSpclEmpDisc("Normal");
                priceChange.setSpclEmpDis("Normal");
        		// Set the MRP amount in the PriceChange object.
        		buildMaximumRetailPriceChange(rs, priceChange);
        		/* 12.0.9IN: MMRP related Changes ends here */
                event.addItem(priceChange);
                results.add(priceChange);
            }
        }
        return results;
    }
    
  //Changes Starts by Kamlesh Pant for SpecialEmpDiscount
    
    protected List<PriceChangeIfc> parseSpeclEmpDiscPriceChangeResults(DataConnectionIfc dataConnection, PLUItemIfc pluItem)
            throws DataException, SQLException
        {
            List<PriceChangeIfc> results = new ArrayList<PriceChangeIfc>();
            List<ItemPriceMaintenanceEventIfc> maintenanceEvents = new ArrayList<ItemPriceMaintenanceEventIfc>();
        
            ResultSet rs = (ResultSet)dataConnection.getResult();
            //System.out.println("682=============");
            if (rs != null)
            {
            	//System.out.println("685=============");
                while (rs.next())
                {
                	//System.out.println("688=================");
                    // event id
                    int eventID = rs.getInt(FIELD_EVENT_EVENT_ID);
                    ItemPriceMaintenanceEventIfc event = getItemPriceMaintenanceEvent(maintenanceEvents, eventID);
                    if (event == null)
                    {
                        event = createItemPriceMaintenanceEvent(rs, eventID, pluItem.getStoreID(), false);
                        maintenanceEvents.add(event);
                    }
                    // price change
                    MAXPriceChangeIfc priceChange = (MAXPriceChangeIfc) DomainGateway.getFactory().getPriceChangeInstance();
                    // override amount
                    BigDecimal amt = rs.getBigDecimal(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT);
                    if (amt != null)
                    {
                        priceChange.setOverridePriceAmount(DomainGateway.getBaseCurrencyInstance(amt));
                    }
                    // use override price amount instead of sale unit amount if available
                    amt = rs.getBigDecimal(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_PRICE_OVERRIDE_AMOUNT);
                    if (amt != null)
                    {
                        priceChange.setOverridePriceAmount(DomainGateway.getBaseCurrencyInstance(amt));
                    }
                    
                    priceChange.setItem(pluItem);
                   // System.out.println("713=============");
                    ModifyTransactionDiscountCargo mtd = new ModifyTransactionDiscountCargo();
                     if(pluItem.getEmpID())
                     {
                    //	System.out.println("720============="+pluItem.getEmpID());
                    	String spclDisc = rs.getString(FIELD_SPECIAL_EMP_DISCOUNT);
                    //	System.out.println("722============="+spclDisc);               	
                        if(spclDisc!=null) {
                    	priceChange.setSpclEmpDis(spclDisc);
                    	pluItem.setSpclEmpDisc(spclDisc);
                    	//mtd.setSpclEmpDisc(spclDisc);
                    	//System.out.println("mtd.setSpclEmpDis()============= "+mtd.getSpclEmpDisc());
                    }}
                	/* 12.0.9IN: MMRP related Changes Starts here */
                    
            		// Set the MRP amount in the PriceChange object.
            		buildMaximumRetailPriceChange(rs, priceChange);
            		/* 12.0.9IN: MMRP related Changes ends here */
                    event.addItem(priceChange);
                    results.add(priceChange);
                }
            }
            return results;
        }
  //Changes Ends for SpecialEmpDiscount
    
    protected List<PriceChangeIfc> parseTemporaryPriceChangeResults(DataConnectionIfc dataConnection, PLUItemIfc pluItem)
        throws DataException, SQLException
    {
        List<PriceChangeIfc> results = new ArrayList<PriceChangeIfc>();
        List<ItemPriceMaintenanceEventIfc> maintenanceEvents = new ArrayList<ItemPriceMaintenanceEventIfc>();
    
        ResultSet rs = (ResultSet)dataConnection.getResult();
        if (rs != null)
        {
            while (rs.next())
            {
                // event id
                int eventID = rs.getInt(FIELD_EVENT_EVENT_ID);
                ItemPriceMaintenanceEventIfc event = getItemPriceMaintenanceEvent(maintenanceEvents, eventID);
                if (event == null)
                {
                    event = createItemPriceMaintenanceEvent(rs, eventID, pluItem.getStoreID(), true);
                    maintenanceEvents.add(event);
                }
                // price change
                PriceChangeIfc priceChange = DomainGateway.getFactory().getPriceChangeInstance();
                // override amount
                BigDecimal amt = rs.getBigDecimal(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_PRICE_OVERRIDE_AMOUNT);
                if (amt != null)
                {
                    priceChange.setOverridePriceAmount(DomainGateway.getBaseCurrencyInstance(amt));
                }
                
                Timestamp effectiveDate = rs.getTimestamp(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_EFFECTIVE_DATE);
                if (effectiveDate != null)
                {
                    event.setEffectiveDateTimestamp(timestampToEYSDate(effectiveDate));
                }
                
                Timestamp expirationDate = rs.getTimestamp(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_EXPIRATION_DATE);
                if (expirationDate != null)
                {
                    event.setExpirationDateTimestamp(timestampToEYSDate(expirationDate));
                }
                priceChange.setPromotionId(rs.getLong(FIELD_PROMOTION_ID));
                priceChange.setPromotionComponentId(rs.getLong(FIELD_PROMOTION_COMPONENT_ID));
                priceChange.setPromotionComponentDetailId(rs.getLong(FIELD_PROMOTION_COMPONENT_DETAIL_ID));
                priceChange.setPromotionName(rs.getString(FIELD_EVENT_NAME));
				BigDecimal pricingGroupID = rs.getBigDecimal(FIELD_CUSTOMER_PRICING_GROUP_ID);
                
				if (pricingGroupID != null)
				{
				    priceChange.setPricingGroupID(pricingGroupID.intValue());
				}
                priceChange.setItem(pluItem);
                /* 12.0.9IN: MMRP related Changes Starts here */
        		buildMaximumRetailPriceChange(rs, priceChange);
        		/* 12.0.9IN: MMRP related Changes ends here */

                event.addItem(priceChange);
                results.add(priceChange);
            }
        }
        return results;
    }
    
    protected List<PriceChangeIfc> parseClearancePriceChangeResults(DataConnectionIfc dataConnection, PLUItemIfc pluItem)
    throws DataException, SQLException
{
    List<PriceChangeIfc> results = new ArrayList<PriceChangeIfc>();
    List<ItemPriceMaintenanceEventIfc> maintenanceEvents = new ArrayList<ItemPriceMaintenanceEventIfc>();

    ResultSet rs = (ResultSet)dataConnection.getResult();
    if (rs != null)
    {
        while (rs.next())
        {
            // event id
            int eventID = rs.getInt(FIELD_EVENT_EVENT_ID);
            ItemPriceMaintenanceEventIfc event = getItemPriceMaintenanceEvent(maintenanceEvents, eventID);
            if (event == null)
            {
                event = createItemPriceMaintenanceEvent(rs, eventID, pluItem.getStoreID(), true);
                maintenanceEvents.add(event);
            }
            // price change
            PriceChangeIfc priceChange = DomainGateway.getFactory().getPriceChangeInstance();
            // override amount
            BigDecimal amt = rs.getBigDecimal(FIELD_CLEARANCE_PRICE_CHANGE_ITEM_PRICE_OVERRIDE_AMOUNT);
            if (amt != null)
            {
                priceChange.setOverridePriceAmount(DomainGateway.getBaseCurrencyInstance(amt));
            }
            
            Timestamp effectiveDate = rs.getTimestamp(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE);
            if (effectiveDate != null)
            {
                event.setEffectiveDateTimestamp(timestampToEYSDate(effectiveDate));
            }
            
            Timestamp expirationDate = rs.getTimestamp(FIELD_MAINTENANCE_EVENT_EXPIRATION_DATE);
            if (expirationDate != null)
            {
                event.setExpirationDateTimestamp(timestampToEYSDate(expirationDate));
            }
            priceChange.setPromotionName(rs.getString(FIELD_EVENT_NAME));
            BigDecimal pricingGroupID = rs.getBigDecimal(FIELD_CUSTOMER_PRICING_GROUP_ID);
            
            if (pricingGroupID != null)
            {
                priceChange.setPricingGroupID(pricingGroupID.intValue());
            }
            priceChange.setItem(pluItem);
            event.addItem(priceChange);
            results.add(priceChange);
        }
    }
    return results;
}
    
    /**
     * Retrieves the source-code-control system revision number.
     * @return String representation of revision number
     */
    protected String getRevisionNumber()
    {
        return revisionNumber;
    }
    
    /**
     * Returns the string representation of this object.
     * @return String representation of object
     */
    public String toString()
    {
        return Util.classToStringHeader("JdbcSelectPriceChange",
                                        getRevisionNumber(),
                                        hashCode()).toString() ;
    }

    /**
     * Return a new ItemPriceMaintenanceEventIfc
     * 
     * @param rs
     * @param eventID
     * @param storeID
     * @return
     * @throws SQLException
     */
    private ItemPriceMaintenanceEventIfc createItemPriceMaintenanceEvent(ResultSet rs, int eventID, String storeID, boolean isTemporary)
        throws SQLException
    {
        ItemPriceMaintenanceEventIfc event = DomainGateway.getFactory().getItemPriceMaintenanceEventInstance();
        event.setEventID(eventID);

        // store id
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(storeID);
        event.setStore(store);

        // type code
        String typeCode = rs.getString(FIELD_MAINTENANCE_EVENT_TYPE_CODE);
        event.setTypeCode(EventConstantsIfc.EVENT_TYPE_UNDEFINED);
        for (int i = 0; i < EventConstantsIfc.EVENT_TYPE_CODE.length; i++)
        {
            if (EventConstantsIfc.EVENT_TYPE_CODE[i].equals(typeCode))
            {
                event.setTypeCode(i);
                break;
            }
        }

        // effective date
        Timestamp effectiveDate = rs.getTimestamp(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE);
        event.setEffectiveDateTimestamp(timestampToEYSDate(effectiveDate));

        // expiry date
        Timestamp expirationDate = rs.getTimestamp(FIELD_MAINTENANCE_EVENT_EXPIRATION_DATE);
        if (expirationDate != null)
        {
            event.setExpirationDateTimestamp(timestampToEYSDate(expirationDate));
        }

        BigDecimal amt = null;
        /* 12.0.9IN: MMRP related Changes Starts here */
    	String appliedOn = null;
    	/* 12.0.9IN: MMRP related Changes Ends here */
        if (isTemporary)
        {
            amt = rs.getBigDecimal(FIELD_TEMPORARY_PRICE_CHANGE_SALE_UNIT_AMOUNT);
            typeCode = rs.getString(FIELD_TEMPORARY_PRICE_CHANGE_SALE_UNIT_AMOUNT_TYPE_CODE);
            /* 12.0.9IN: MMRP related Changes Starts here */
    	    appliedOn = rs.getString(FIELD_TEMPORARY_PRICE_CHANGE_APPLIED_ON);
    	    if (appliedOn == null) {
    		appliedOn = MAXPriceChangeConstantsIfc.APPLIED_ON_SELLING_RETAIL
    			.getStringCode();
    	    }
    	    /* 12.0.9IN: MMRP related Changes Ends here */
        }
        else
        {
            amt = rs.getBigDecimal(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT);
            typeCode = rs.getString(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT_TYPE_CODE);
            /* 12.0.9IN: MMRP related Changes Starts here */
    	    appliedOn = rs.getString(FIELD_PERMANENT_PRICE_CHANGE_APPLIED_ON);
    	    if (appliedOn == null) {
    		appliedOn = MAXPriceChangeConstantsIfc.APPLIED_ON_MRP
    			.getStringCode();
    	    }
    	    /* 12.0.9IN: MMRP related Changes Ends here */
        }
        event.setSaleUnitAmount(amt);
        event.setApplicationCode(PriceChangeConstantsIfc.APPLICATION_CODE_UNDEFINED);
        event.setTypeCode(EventConstantsIfc.EVENT_TYPE_UNDEFINED);
    	/* 12.0.9IN: MMRP related Changes Starts here */
    	((MAXItemPriceMaintenanceEventIfc)event).setAppliedOn(appliedOn);
    	/* 12.0.9IN: MMRP related Changes Ends here */
    	
        for (int i = 0; i < PriceChangeConstantsIfc.APPLICATION_CODE.length; i++)
        {
            if (PriceChangeConstantsIfc.APPLICATION_CODE[i].equals(typeCode))
            {
                event.setApplicationCode(i);
                break;
            }
        }

        // priority
        event.setPriority(rs.getInt(FIELD_ITEM_PRICE_MAINTENANCE_EVENT_PRIORITY));
        // last digit
        String lastDigit = rs.getString(FIELD_ITEM_PRICE_MAINTENANCE_PRICE_LAST_DIGIT);
        if (lastDigit == null)
        {
            event.setLastPriceDigit(-1);
        }
        else
        {
            event.setLastPriceDigit(Integer.parseInt(lastDigit));
        }
        return event;
    }

    /**
     * Return a ItemPriceMaintenanceEventIfc if it exists in the list. Checking
     * by event id.
     * 
     * @param maintenanceEvents
     * @param eventID
     * @return ItemPriceMaintenanceEventIfc
     */
    private ItemPriceMaintenanceEventIfc getItemPriceMaintenanceEvent(List<ItemPriceMaintenanceEventIfc> maintenanceEvents, int eventID)
    {
        for (Iterator<ItemPriceMaintenanceEventIfc> iter = maintenanceEvents.iterator(); iter.hasNext();)
        {
            ItemPriceMaintenanceEventIfc event = iter.next();
            if (event.getEventID() == eventID)
            {
                return event;
            }
        }
        return null;
    }

private List parseDistinctMaximumRetailPricePriceChanges(
	    JdbcDataConnection dataConnection, PriceChangeIfc[] priceChanges,boolean active)
	    throws SQLException, DataException {
	ResultSet rs = (ResultSet) dataConnection.getResult();
	List list/*<MaximumRetailPriceChangeIfc>*/=Collections.synchronizedList(new ArrayList(10));
	if (rs != null) {
	    while (rs.next()) {
		BigDecimal mrp = null;
		mrp = rs.getBigDecimal(FIELD_MAXIMUM_RETAIL_PRICE_CHANGE_MRP);
		MAXMaximumRetailPriceChangeIfc mrpChange = null;
		mrpChange = getLatestMaximumRetailPriceChangeForMRP(mrp,
			priceChanges,active);
		  /*if(list.size() > 0)
          {
              for(int i = 0; list.size() > i; i++)
                  if(!mrpChange.equals(list.get(i)))
                      list.add(mrpChange);

          } else
          {
              list.add(mrpChange);
          }*/
		
		if(!list.contains(mrpChange))
			list.add(mrpChange);
	    }
	}
	return list;
    }
    
    private SQLSelectStatement buildSelectDistinctMaximumRetailPricePrice(
	    String storeId, String itemId, Calendar when, boolean active) {
	// TODO Auto-generated method stub
	SQLSelectStatement sql = new SQLSelectStatement();
	// add tables
	sql.addTable(TABLE_MAINTENANCE_EVENT);
	sql.addTable(TABLE_PERMANENT_PRICE_CHANGE_ITEM);
	sql.addTable(TABLE_EVENT);

	sql.setDistinctFlag(true);
	sql.addColumn(FIELD_MAXIMUM_RETAIL_PRICE_CHANGE_MRP);
	sql.addColumn(FIELD_MRP_ACTIVATION_DATE);
	// Event Id
	sql.addQualifier(EVT_ID, TABLE_PERMANENT_PRICE_CHANGE_ITEM + "."
		+ FIELD_PERMANENT_PRICE_CHANGE_ITEM_EVENT_ID);
	sql.addQualifier(EVT_ID, TABLE_MAINTENANCE_EVENT + "."
		+ FIELD_MAINTENANCE_EVENT_EVENT_ID);
	// Store Id
	sql.addQualifier(RST_ID, TABLE_PERMANENT_PRICE_CHANGE_ITEM + "."
		+ FIELD_PERMANENT_PRICE_CHANGE_ITEM_RETAIL_STORE_ID);
	sql.addQualifier(RST_ID, TABLE_MAINTENANCE_EVENT + "."
		+ FIELD_MAINTENANCE_EVENT_RETAIL_STORE_ID);
	// Effective Date,ItemId and StoreId
	sql.addQualifier(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE + " <= "
		+ dateToSQLTimestampString(when.getTime()));
	sql.addQualifier(TABLE_PERMANENT_PRICE_CHANGE_ITEM + "."
		+ FIELD_PERMANENT_PRICE_CHANGE_ITEM_RETAIL_STORE_ID,
		inQuotes(storeId));
	sql.addQualifier(TABLE_PERMANENT_PRICE_CHANGE_ITEM + "."
		+ FIELD_PERMANENT_PRICE_CHANGE_ITEM_ITEM_ID, inQuotes(itemId));
	sql.addQualifier(TABLE_PERMANENT_PRICE_CHANGE_ITEM + "."
		+ FIELD_MRP_ACTIVE_CODE,
		makeStringFromBoolean(active));
		//Rev 1.1 change
	sql.addOrdering(TABLE_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_MRP_ACTIVATION_DATE 
            + " desc");  
    sql.addOrdering(TABLE_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_PERMANENT_PRICE_CHANGE_ITEM_MAXIMUM_RETAIL_PRICE 
            + " desc");
	return sql;
    }
    
    public MAXMaximumRetailPriceChangeIfc getLatestMaximumRetailPriceChangeForMRP(
    	    BigDecimal mrp, PriceChangeIfc[] priceChanges,boolean active) {
    	MAXMaximumRetailPriceChangeIfc matchedMRPChange=null;
    	if(priceChanges!=null && active){
    	    for (int i=0;i<priceChanges.length;i++) {
    	    	MAXMaximumRetailPriceChangeIfc mrpChange = (MAXMaximumRetailPriceChangeIfc)((MAXPriceChangeIfc)priceChanges[i]).getMaximumRetailPriceChange();
    		// Since PriceChange are always sorted by effective date it is
    		// appropriate to pick the first one.
    		if (mrpChange.getMaximumRetailPrice().getDecimalValue().compareTo(mrp)==0) {
    		    matchedMRPChange=mrpChange;
    		    break;
    		}
    	    }
    	}
    	else{
    	   //For Inactive MRP's or null PriceChanges passed just return the MaximumRetailPriceChange
    	    matchedMRPChange=((MAXDomainObjectFactory)DomainGateway.getFactory()).getMaximumRetailPriceChangeInstance();
    	    matchedMRPChange.setMaximumRetailPrice(DomainGateway.getBaseCurrencyInstance(mrp));
    	}
    	return matchedMRPChange;
        }
    
    /**
     * Builds the MaximumRetailPriceChange object and sets in the price
     * change object
     *
     *
     * @param ResultSet
     * @param PriceChangeIfc
     * @since 12.0.9IN
     */
	private void buildMaximumRetailPriceChange(ResultSet rs,
			PriceChangeIfc priceChange) throws SQLException {

		// Build MaximumRetailPriceChange object
		MAXMaximumRetailPriceChangeIfc maximumRetailPriceChange = ((MAXDomainObjectFactory) DomainGateway
				.getFactory()).getMaximumRetailPriceChangeInstance();
		// Set the MRP
		BigDecimal amt = rs
				.getBigDecimal(FIELD_MAXIMUM_RETAIL_PRICE_CHANGE_MRP);
		if (amt != null) {
			maximumRetailPriceChange.setMaximumRetailPrice(DomainGateway
					.getBaseCurrencyInstance(amt));
			maximumRetailPriceChange.setRetailSellingPrice(priceChange.getOverridePriceAmount()); // Rev 1.1 changes
		}
		// ActivationDate
		Timestamp activationDate = rs.getTimestamp(FIELD_MRP_ACTIVATION_DATE);
		if (activationDate != null) {
			maximumRetailPriceChange
					.setActivationDate(timestampToEYSDate(activationDate));
		}
		// InActivationDate
		Timestamp inActivationDate = rs
				.getTimestamp(FIELD_MRP_INACTIVATION_DATE);
		if (inActivationDate != null) {
			maximumRetailPriceChange
					.setInActivationDate(timestampToEYSDate(inActivationDate));
		}
		// PrimaryFlag
		String primaryMRP = null;
		primaryMRP = rs.getString(FIELD_MRP_PRIMARY_STATUS_CODE);
		if (primaryMRP != null) {
			maximumRetailPriceChange.setPrimary(AbstractDBUtils.TRUE
					.equals(primaryMRP) ? true : false);
		}
		// ActiveFlag
		String activeMRP = rs.getString(FIELD_MRP_ACTIVE_CODE);
		if (activeMRP != null) {
			maximumRetailPriceChange.setActive(AbstractDBUtils.TRUE
					.equals(activeMRP) ? true : false);
		}
		// Set the Item Id and RetailStore Id
		maximumRetailPriceChange.setItemId(priceChange.getItem().getItemID());
		maximumRetailPriceChange.setRetailStoreId(priceChange.getItem()
				.getStoreID());
		((MAXPriceChangeIfc) priceChange)
				.setMaximumRetailPriceChange(maximumRetailPriceChange);
	}

    /**
     * This method reads the Unique MaximumRetailPriceChange either active
     * or inactive
     *
     * @param storeId
     * @param itemId
     * @param active
     * @return MaximumRetailPriceChangeIfc
     */
	protected MAXMaximumRetailPriceChangeIfc[] readEffectiveUniqueMaximumRetailPriceChanges(
			JdbcDataConnection dataConnection, Calendar when, String storeId,
			String itemId, boolean active, PriceChangeIfc[] priceChanges)
			throws DataException {
		try {
			// build sql
			SQLSelectStatement sql = buildSelectDistinctMaximumRetailPricePrice(
					storeId, itemId, when, active);
			// execute sql
			dataConnection.execute(sql.getSQLString());
			// parse results
			List results/* <MaximumRetailPriceChangeIfc> */= parseDistinctMaximumRetailPricePriceChanges(
					dataConnection, priceChanges, active);
			return (MAXMaximumRetailPriceChangeIfc[]) results
					.toArray(new MAXMaximumRetailPriceChangeIfc[results.size()]);
		} catch (SQLException se) {
			throw new DataException(DataException.SQL_ERROR,
					"readEffectiveUniqueMaximumRetailPrice", se);
		} catch (DataException de) {
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN,
					"readEffectiveUniqueMaximumRetailPrice", e);
		}
	}
}
