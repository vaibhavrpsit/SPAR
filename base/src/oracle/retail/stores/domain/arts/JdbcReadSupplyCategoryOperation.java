/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadSupplyCategoryOperation.java /main/16 2013/09/05 10:36:19 abondala Exp $
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
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
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
      4    360Commerce 1.3         1/25/2006 4:11:18 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:27:19    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
     $
     Revision 1.6  2004/09/23 00:30:49  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.5  2004/02/17 17:57:35  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:44  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:21  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:32:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Feb 15 2003 17:32:28   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.supply.SupplyCategoryIfc;
import oracle.retail.stores.domain.supply.SupplyItemSearchCriteria;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
    The JdbcReadSupplyCategoryByCategoryOperation implements the supply item
    lookup JDBC data retrieve operation.
    @version $Revision: /main/16 $
 */
public class JdbcReadSupplyCategoryOperation extends JdbcDataOperation
                               implements DataOperationIfc, ARTSDatabaseIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3873540030759938901L;

    /**
       revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/16 $";
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadSupplyCategoryOperation.class);

    /**
       Class constructor.
     */
    public JdbcReadSupplyCategoryOperation()
    {
        super();
        setName("ReadSupplyCategoryOperation");
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
       @deprecated As of release 13.1
     */
    public void execute(DataTransactionIfc transaction,
                        DataConnectionIfc connection,
                        DataActionIfc action)
        throws DataException
    {

        SupplyItemSearchCriteria searchCriteria = (SupplyItemSearchCriteria)action.getDataObject();

        SupplyCategoryIfc[] supplyCategories;
        SupplyCategoryIfc supplyCategory;
        Vector supplyCategoryVector = new Vector(2);


        String storeID = searchCriteria.getStoreID();

        try
        {
            SQLSelectStatement sql = new SQLSelectStatement();

            sql.addTable(TABLE_MERCHANDISE_CLASSIFICATION_CODE, ALIAS_MERCHANDISE_CLASSIFICATION_CODE); // ALIAS_MERCHANDISE_CLASSIFICATION_CODE

            // TABLE_MERCHANDISE_CLASSIFICATION_CODE
            sql.addColumn(FIELD_MERCHANDISE_CLASSIFICATION_CODE);
            sql.addColumn(FIELD_MERCHANDISE_CLASSIFICATION_DESCRIPTION);
            sql.addOrdering(FIELD_MERCHANDISE_CLASSIFICATION_DESCRIPTION);
            setSupplyItemSubselect(sql, searchCriteria);

            connection.execute(sql.getSQLString());

            // Extract data from the result set.
            int recordsFound = 0;
            ResultSet rs = (ResultSet)connection.getResult();

            while (rs.next())
            {
                recordsFound++;
                String strCategoryId = getSafeString(rs,1);
                String strDescription = getSafeString(rs,2);

                supplyCategory = DomainGateway.getFactory().getSupplyCategoryInstance();
                supplyCategory.setCategoryID(strCategoryId);
                supplyCategory.setDescription(strDescription);

                supplyCategoryVector.add(supplyCategory);

            } // while (rs.next())

            rs.close();

            if (supplyCategoryVector.size() == 0)
            {
                String msg = "No supply categorys were was found processing the result set in JdbcReadSupplyCategoryOperation.readSupplyCategory().";
                throw new DataException(DataException.NO_DATA, msg);
            }
            // add employee objects
            else
            {
                // copy vector elements to array
                int n = supplyCategoryVector.size();
                supplyCategories = new SupplyCategoryIfc[n];
                supplyCategoryVector.copyInto(supplyCategories);
                if (logger.isInfoEnabled()) logger.info(
                            "" + "Matches found:  " + "" + Integer.toString(n) + "");
            }

        }
        catch (SQLException e)
        {
            ((JdbcDataConnection)connection).logSQLException(e, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR,
                                    "An SQL Error occurred processing the result set from selecting a supply item in JdbcReadSupplyCategoryOperation.readSupplyCategory().", e);
        }
        catch (DataException de)
        {
            throw de;
        }

        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadSupplyCategoryOperation.readSupplyCategory", e);
        }

        transaction.setResult(supplyCategories);
    }


    /**
       Sets the subselect that will determine distinct Categories in the Supplyitem Table.
       @param sql The SQL statement to which the subquery will be appended.
       @exception SQLException is thrown if the operation cannot be completed.
    **/
    
    public void setSupplyItemSubselect(SQLSelectStatement sql,
                                       SupplyItemSearchCriteria searchCriteria)
                                       throws DataException
    {

            SQLSelectStatement subSelect = new SQLSelectStatement();

            subSelect.addTable(TABLE_SUPPLY_ITEM, ALIAS_SUPPLY_ITEM+ "x");

            // TABLE_SUPPLY_ITEM
            subSelect.addColumn("DISTINCT "+ ALIAS_SUPPLY_ITEM+ "x",
                                FIELD_SUPPLY_ITEM_CLASS_CODE);

            setStoreSupplyItemSubselect(subSelect, searchCriteria);

            // class.category IN ( .....)
            sql.addQualifier(ALIAS_MERCHANDISE_CLASSIFICATION_CODE + "." +
                FIELD_MERCHANDISE_CLASSIFICATION_CODE + " IN (" +
                subSelect.getSQLString() + ")");


    } // setSupplyItemSubselect

    /**
       Sets the subselect that will determine distinct items that the store can carry.
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

            if (!(Util.isEmpty(searchCriteria.getStoreID())))
            {
              subSelect.addQualifier(ALIAS_STORE_SUPPLY_ITEM + "x."+
                                     FIELD_RETAIL_STORE_ID + " = '" +
                                     searchCriteria.getStoreID() + "'");
            }

            // class.category IN ( .....)
            sql.addQualifier(ALIAS_SUPPLY_ITEM+"x." + FIELD_SUPPLY_ITEM_SHORT_ID +
                             " IN (" + subSelect.getSQLString() + ")");

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
        String strResult = new String("Class:  JdbcReadSupplyCategoryOperation (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }

    /**
       Returns the description with a "%" appended  since all
       reads of strings should include a wildcard.
       <p>
       @param  str A supply description or class description (category)
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
