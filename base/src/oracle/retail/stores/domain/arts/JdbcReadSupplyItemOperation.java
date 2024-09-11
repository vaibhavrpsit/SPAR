/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadSupplyItemOperation.java /main/14 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                      rgbustores_13.5x_generic
 *    cgreen 05/16/12 - arrange order of businessDay column to end of primary
 *                      key to improve performance since most receipt lookups
 *                      are done without the businessDay
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech75 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                      SQLException to DataException
 *    abonda 01/03/10 - update header date
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *
 * ===========================================================================

     $Log:
      6    360Commerce 1.5         4/25/2007 10:01:12 AM  Anda D. Cadar   I18N
           merge
      5    360Commerce 1.4         1/25/2006 4:11:18 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce 1.3         1/22/2006 11:41:19 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:27:11    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
     $
     Revision 1.6  2004/09/23 00:30:50  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.5  2004/02/17 17:57:36  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:45  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:22  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:32:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Feb 15 2003 17:32:30   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.supply.SupplyItemIfc;
import oracle.retail.stores.domain.supply.SupplyItemSearchCriteria;
import oracle.retail.stores.domain.supply.SupplyItemSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
    The JdbcReadSupplyItemByCategoryOperation implements the supply item
    lookup JDBC data retrieve operation.
    @version $Revision: /main/14 $
 */
public class JdbcReadSupplyItemOperation extends JdbcDataOperation
                               implements DataOperationIfc, ARTSDatabaseIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6590616215374050794L;

    /**
       revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/14 $";
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadSupplyItemOperation.class);

    /**
       Class constructor.
     */
    public JdbcReadSupplyItemOperation()
    {
        super();
        setName("ReadSupplyItemOperation");
    }

    /**
       This method is used to execute a specific operation for a specific
       transaction against a specific datastore.
       <B>Pre-Condition</B>
       <UL>
       <LI>
       The DataTransactionIfc contains any application-specific data elements.
       <LI>
       The DataConnectionIfc is valid.
       <LI>
       The DataActionIfc contains the necessary DataObjects.
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>The appropriate data operations have been executed by the
       DataConnection using the input data provided by the DataTransactionIfc
       and the DataActionIfc.
       <LI>Any results have been posted to the DataTransactionIfc.
       </UL>
       @param dt The DataTransactionIfc that provides a place to post results.
       @param dc The DataConnection that provides a connection to the datastore.
       @param da The DataActionIfc that provides specific input data for this operation.
       @exception DataException is thrown if the operation cannot be completed.
     */
    public void execute(DataTransactionIfc transaction,
                        DataConnectionIfc connection,
                        DataActionIfc action)
        throws DataException
    {
        SupplyItemIfc[] supplyItems;

        SupplyItemSearchCriteria searchCriteria = (SupplyItemSearchCriteria)action.getDataObject();

        supplyItems = readSupplyItems(connection, searchCriteria);

        transaction.setResult(supplyItems);
    }


    /**
        This method reads a supplyItem info object associated with the specified
        search criteria passed in (Store number and category).

        @param  connection data connection interface object
        @param  the supply item search criteria to be read from the database.
        @return an interface to the SupplyItem info object read from the database.
        @exception DataException upon error
        @deprecated As of release 13.1
     */
    public SupplyItemIfc[] readSupplyItems(DataConnectionIfc connection, SupplyItemSearchCriteria searchCriteria)
        throws DataException
    {
        SupplyItemIfc[] supplyItems = new SupplyItemIfc[0];
        SupplyItemIfc supplyItem = null;
        Vector supplyItemVector = new Vector(2);

        // int resultCount = 0;
        // Build SQL statement.

        String storeID = searchCriteria.getStoreID();
        String lookupString = searchCriteria.getSearchString();

        try
        {
            SQLSelectStatement sql = new SQLSelectStatement();

            sql.addTable(TABLE_SUPPLY_ITEM, ALIAS_SUPPLY_ITEM);
            sql.addTable(TABLE_STORE_TYPE_VOLUME, ALIAS_STORE_TYPE_VOLUME);
            sql.addTable(TABLE_SUPPLY_STANDARD_ORDER_QUANTITY, ALIAS_SUPPLY_STANDARD_ORDER_QTY);
            sql.addTable(TABLE_MERCHANDISE_CLASSIFICATION_CODE, ALIAS_MERCHANDISE_CLASSIFICATION_CODE); // ALIAS_MERCHANDISE_CLASSIFICATION_CODE

            // TABLE_SUPPLY_ITEM
            sql.addColumn(ALIAS_SUPPLY_ITEM, FIELD_SUPPLY_ITEM_SHORT_ID);
            sql.addColumn(ALIAS_SUPPLY_ITEM, FIELD_SUPPLY_ITEM_ID);
            sql.addColumn(ALIAS_SUPPLY_ITEM, FIELD_SUPPLY_ITEM_SHORT_DESCRIPTION);
            sql.addColumn(ALIAS_SUPPLY_ITEM, FIELD_SUPPLY_ITEM_DESCRIPTION);
            sql.addColumn(ALIAS_SUPPLY_ITEM, FIELD_SUPPLY_ITEM_CLASS_CODE);
            sql.addColumn(ALIAS_SUPPLY_ITEM, FIELD_SUPPLY_ITEM_COST);
            sql.addColumn(ALIAS_SUPPLY_ITEM, FIELD_SUPPLY_ITEM_PACKAGE_QUANTITY);

            // TABLE_SUPPLY_STANDARD_ORDER_QUANTITY
            sql.addColumn(ALIAS_SUPPLY_STANDARD_ORDER_QTY, FIELD_STANDARD_ORDER_QUANTITY);

            // TABLE_MERCHANDISE_CLASSIFICATION_CODE
            sql.addColumn(ALIAS_MERCHANDISE_CLASSIFICATION_CODE, FIELD_MERCHANDISE_CLASSIFICATION_DESCRIPTION);

            sql.addOrdering(ALIAS_MERCHANDISE_CLASSIFICATION_CODE, FIELD_MERCHANDISE_CLASSIFICATION_DESCRIPTION);
            sql.addOrdering(ALIAS_SUPPLY_ITEM, FIELD_SUPPLY_ITEM_SHORT_DESCRIPTION);

            // create a subquery to exclude item numbers not available to this store
            setStoreSupplyItemSubselect(sql, searchCriteria);

            // Filter out storeTypeVolumne records not belonging to this store
            sql.addQualifier(ALIAS_STORE_TYPE_VOLUME, FIELD_RETAIL_STORE_ID, "'"+searchCriteria.getStoreID()+"'");

            // Join the SupplyItem and StandardOrderQuantity tables by Short_ID
            sql.addJoinQualifier("("+ALIAS_SUPPLY_ITEM, FIELD_SUPPLY_ITEM_SHORT_ID,
                                 ALIAS_SUPPLY_STANDARD_ORDER_QTY, FIELD_STANDARD_ORDER_QUANTITY_SHORT_ID);

            // Join the StoreTypeVolume and StandardOrderQuantity tables by StoreType and VolumneCode
            sql.addJoinQualifier(ALIAS_STORE_TYPE_VOLUME, FIELD_SUPPLY_STORE_TYPE,
                                 ALIAS_SUPPLY_STANDARD_ORDER_QTY, FIELD_STANDARD_ORDER_QUANTITY_STORE_TYPE);
            sql.addJoinQualifier(ALIAS_STORE_TYPE_VOLUME, FIELD_SUPPLY_STORE_VOLUME_CODE,
                                 ALIAS_SUPPLY_STANDARD_ORDER_QTY, FIELD_STANDARD_ORDER_QUANTITY_VOLUME_CODE+")");

            // Join the SupplyItem and MerchandiseClassification tables by category
            sql.addJoinQualifier(ALIAS_SUPPLY_ITEM, FIELD_SUPPLY_ITEM_CLASS_CODE,
                                 ALIAS_MERCHANDISE_CLASSIFICATION_CODE, FIELD_MERCHANDISE_CLASSIFICATION_CODE);


            switch (searchCriteria.getSearchBy())
            {
              case SupplyItemSearchCriteriaIfc.SEARCH_BY_CATEGORY:
              {
                // Select only the records that match the category selected
                sql.addQualifier(ALIAS_SUPPLY_ITEM, FIELD_SUPPLY_ITEM_CLASS_CODE, "'"+searchCriteria.getSearchString()+"'");
                break;
              }
              case SupplyItemSearchCriteriaIfc.SEARCH_BY_DESCRIPTION:
              {
                // Select only the records that match the category selected
                sql.addQualifier("("+ ALIAS_SUPPLY_ITEM+"."+FIELD_SUPPLY_ITEM_SHORT_DESCRIPTION+
                                 " LIKE '%"+searchCriteria.getSearchString()+"%' OR "+
                                 ALIAS_SUPPLY_ITEM+"."+FIELD_SUPPLY_ITEM_DESCRIPTION+
                                 " LIKE '%"+searchCriteria.getSearchString()+"%'"+ ")");
                break;
              }
              default:
              {
                // Filter only by store
                break;
               }
            }  // switch (searchBy)

            connection.execute(sql.getSQLString());  // Do it

            // Extract data from the result set.
            ResultSet rs = (ResultSet)connection.getResult();

            //resultCount = rs.getFetchSize();

            // Populate the vector
            while (rs.next())
            {
                String strShortId = getSafeString(rs,1);
                String strItemId = getSafeString(rs,2);
                String strShortDesc = getSafeString(rs,3);
                String strDesc = getSafeString(rs,4);
                String strCategoryId = getSafeString(rs,5);
                // CurrencyIfc unitCost = getCurrencyFromDecimal(rs,6);
                CurrencyIfc unitCost = (CurrencyIfc) DomainGateway.getBaseCurrencyInstance(getSafeString(rs,6));

                String strPkgQuantity = getSafeString(rs,7);
                String strStdOrderQty = getSafeString(rs,8);
                String strCategoryDescription = getSafeString(rs,9);

                supplyItem = DomainGateway.getFactory().getSupplyItemInstance();
                supplyItem.setShortItemID(strShortId);
                supplyItem.setItemID(strItemId);
                supplyItem.setDescription(strShortDesc);
                //supplyItem.setDescription(strDesc);
                supplyItem.setCategoryID(strCategoryId);

                supplyItem.setUnitCost(unitCost);
                supplyItem.setPackageQuantity(new BigDecimal(strPkgQuantity));
                supplyItem.setStandardOrderQuantity(new BigDecimal(strStdOrderQty));
                supplyItem.setCategoryDescription(strCategoryDescription);

                supplyItemVector.add(supplyItem);

            }
            rs.close();

            // handle not found
            //if (resultCount == 0)
            if (supplyItem == null)
            {
                String msg = "JdbcReadSupplyItemOperation: No Items found.";
                throw new DataException(DataException.NO_DATA, msg);
            }
            // add employee objects
            else
            {
                // copy vector elements to array
                int n = supplyItemVector.size();
                supplyItems = new SupplyItemIfc[n];
                supplyItemVector.copyInto(supplyItems);
                if (logger.isInfoEnabled()) logger.info(
                            "" + "Matches found:  " + "" + Integer.toString(n) + "");
            }

        }
        catch (SQLException e)
        {
            ((JdbcDataConnection)connection).logSQLException(e, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR,
                                    "An SQL Error occurred processing the result set from selecting a supply item in JdbcReadSupplyItemOperation.readSupplyItems()", e);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadSupplyItemOperation.readSupplyItems().", e);
        }

        return supplyItems;
    }


    /**
       Sets the subselect that will determine which items ths store can order,
       using the StoreSupplyItem table
       @param sql The SQL statement to which the subquery will be appended.
       @exception SQLException is thrown if the operation cannot be completed.
    **/
    
    public void setStoreSupplyItemSubselect(SQLSelectStatement sql,
                                       SupplyItemSearchCriteria searchCriteria)
                                       throws DataException
    {

            SQLSelectStatement subSelect = new SQLSelectStatement();

            subSelect.addTable(TABLE_STORE_SUPPLY_ITEM, ALIAS_STORE_SUPPLY_ITEM+ "x");

            // TABLE_STORE_SUPPLY_ITEM
            subSelect.addColumn(ALIAS_STORE_SUPPLY_ITEM+ "x",
                                FIELD_STORE_SUPPLY_ITEM_SHORT_ID);

            // TABLE_STORE_SUPPLY_ITEM
            subSelect.addQualifier(ALIAS_STORE_SUPPLY_ITEM+ "x",
                                   FIELD_RETAIL_STORE_ID,
                                   "'"+searchCriteria.getStoreID()+"'");

            // SupplyItem.Short_id IN ( .....)
            sql.addQualifier(ALIAS_SUPPLY_ITEM + "." +
                FIELD_SUPPLY_ITEM_SHORT_ID + " IN (" +
                subSelect.getSQLString() + ")");


    } // setSupplyItemSubselect

    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  JdbcReadSupplyItemOperation (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }

    /**
       Returns the description with a "%" appended  since all
       reads of strings should include a wildcard.
       <p>
       @param  str A supply description or category description
       @return the string with "%" appended
     */
    protected String getSearchString(String str)
    {
        return("'" + str + "%'");
    }

    /**
       Returns the revision number of the class.
       <P>
       @param none
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }

}
