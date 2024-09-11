/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveCodeList.java /main/19 2012/09/12 11:57:12 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    npoola 08/25/10 - passed the connection object to the IdentifierService
 *                      getNextID method to use right connection
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - updating deprecated names
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
      7    360Commerce 1.6         6/8/2006 3:54:24 PM    Brett J. Larsen CR
           18490 - UDM - columns CD_MTH_PRDV, CD_SCP_PRDV and CD_BAS_PRDV's
           type was changed to INTEGER
      6    360Commerce 1.5         6/1/2006 12:48:12 PM   Charles D. Baker
           Remove unused imports
      5    360Commerce 1.4         5/31/2006 5:04:01 PM   Brendan W. Farrell
           Move from party to id gen.
           
      4    360Commerce 1.3         1/25/2006 4:11:21 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/10/2005 11:24:15    Jason L. DeLeau 5783:
           Fix issues with closing the device for unleashed.
      3    360Commerce1.2         3/31/2005 15:28:43     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:47     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
     $
     Revision 1.7  2004/08/12 23:29:43  cdb
     @scr 6644 DB2 updates.

     Revision 1.6  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:38  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:47  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:18  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   Sep 26 2003 09:09:46   bwf
 * Merged from 6.01.  Revert back to using getReferenceKey instead of getRuleID and removed unused imports.
 * Resolution for 3395: Selecting Done after a new reason code has been added results in a database error.
 *
 *    Rev 1.1   Sep 03 2003 16:21:40   mrm
 * DB2 support
 * Resolution for POS SCR-3357: Add support needed by RSS
 *
 *    Rev 1.0   Aug 29 2003 15:32:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jun 10 2002 11:14:54   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.2   Jun 07 2002 17:47:40   epd
 * Merging in fixes made for McDonald's Oracle demo
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.1   Mar 18 2002 22:48:08   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:00   msg
 * Initial revision.
 *
 *    Rev 1.1   28 Oct 2001 07:59:54   mpm
 * Initial check-in for clock-function feature.
 *
 *    Rev 1.0   Sep 20 2001 15:59:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:08   msg
 * header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;

import java.util.Locale;

import oracle.retail.stores.common.identifier.IdentifierConstantsIfc;
import oracle.retail.stores.common.identifier.IdentifierServiceLocator;
import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation performs data operations pertaining to code entries.
 * 
 * @version $Revision: /main/19 $
 * @see oracle.retail.stores.domain.arts.CodeDataTransaction
 * @see oracle.retail.stores.domain.arts.JdbcUpdateCodeList
 * @see oracle.retail.stores.domain.utility.CodeListMapIfc
 * @see oracle.retail.stores.domain.utility.CodeListIfc
 * @see oracle.retail.stores.domain.utility.AbstractRoutable
 */
public abstract class JdbcSaveCodeList extends JdbcDataOperation implements ARTSDatabaseIfc, CodeConstantsIfc,
        DiscountRuleConstantsIfc
{
    private static final long serialVersionUID = 8844938128151615326L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveCodeList.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /**
     * default begin date
     */
    public static EYSDate DEFAULT_BEGIN_DATE = new EYSDate(1980, 1, 1);

    /**
     * default end date
     */
    public static EYSDate DEFAULT_END_DATE = new EYSDate(2040, 12, 31);

    /**
     * Replaces a code list. The previous entries for this list are lost.
     * <P>
     * The source of the code list, provided by
     * {@link oracle.retail.stores.domain.utility.AbstractRoutable}, is used to
     * identify which entity should be updated.
     * 
     * @param dataConnection connection to the db
     * @param codeList code list object
     * @return true if successful
     * @exception DataException upon error
     */
    public boolean updateCodeList(JdbcDataConnection dataConnection, CodeListIfc codeList) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveCodeList.updateCodeList()");

        boolean returnCode = false;

        if (codeList.getSource().equals(CODE_SOURCE_CODE_TABLE))
        {
            returnCode = updateCodeListTable(dataConnection, codeList);
        }
        else if (codeList.getSource().equals(CODE_SOURCE_DEPARTMENT_TABLE))
        {
            returnCode = updateDepartmentTable(dataConnection, codeList);
        }
        else if (codeList.getSource().equals(CODE_SOURCE_UNIT_OF_MEASURE_TABLE))
        {
            returnCode = updateUnitOfMeasureTable(dataConnection, codeList);
        }
        else if (codeList.getSource().equals(CODE_SOURCE_PRICE_DERIVATION_RULE_TABLE))
        {
            returnCode = updatePriceDerivationRuleTable(dataConnection, codeList);
        }
        else
        {
            // if not found, issue message and not-found exception
            String msg = "JdbcSaveCodeList.updateCodeList:  Code list has unknown source [" + codeList.getSource()
                    + "].";
            throw new DataException(DataException.NO_DATA, msg);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveCodeList.updateCodeList()");

        return (returnCode);
    }

    /**
     * Replaces a code list in the code list table. The previous entries for
     * this list are lost.
     * <P>
     * To facilitate the replacement, the existing records are deleted and the
     * new records inserted.
     * 
     * @param dataConnection connection to the db
     * @param codeList code list object
     * @return true if successful
     * @exception DataException upon error
     */
    protected boolean updateCodeListTable(JdbcDataConnection dataConnection, CodeListIfc codeList) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveCodeList.updateCodeListTable()");

        boolean returnCode = false;

        try
        {
            // delete existing records
            deleteCodeListTableEntries(dataConnection, codeList);
        }
        // if data not found, it's OK
        catch (DataException de)
        {
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                logger.error(de);
                throw de;
            }
        }

        // insert new records
        returnCode = insertCodeListTableEntries(dataConnection, codeList);

        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveCodeList.updateCodeListTable()");

        return (returnCode);
    }

    /**
     * Deletes all code list table entries matching list description and store
     * identifier.
     * 
     * @param dataConnection The connection to the data source
     * @param codeList CodeListIfc object
     * @exception DataException thrown if error occurs
     */
    protected void deleteCodeListTableEntries(JdbcDataConnection dataConnection, CodeListIfc codeList)
            throws DataException
    {
        SQLDeleteStatement sql = new SQLDeleteStatement();

        // table
        sql.setTable(TABLE_CODE_LIST);

        // qualifier
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(codeList));
        sql.addQualifier(FIELD_CODE_LIST_DESCRIPTION + " = " + getListDescription(codeList));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "deleteCodeListTableEntries", e);
        }

    }

    /**
     * Inserts code list table entries.
     * 
     * @param dataConnection The connection to the data source
     * @param codeList CodeListIfc object
     * @return boolean indicator that records were inserted
     * @exception DataException thrown if error occurs
     */
    protected boolean insertCodeListTableEntries(JdbcDataConnection dataConnection, CodeListIfc codeList)
            throws DataException
    {
        boolean returnCode = false;
        // loop through entries and insert
        CodeEntryIfc[] entries = codeList.getEntries();
        int numEntries = codeList.getNumberOfEntries();
        for (int i = 0; i < numEntries; i++)
        {
            insertCodeListTableEntry(dataConnection, codeList, entries[i]);
        }
        // set return code if records inserted
        if (numEntries > 0)
        {
            returnCode = true;
        }
        return (returnCode);
    }

    /**
     * Inserts an entry into the code list table.
     * 
     * @param dataConnection connection to the data source
     * @param codeList code list object
     * @param entry code entry object
     * @exception DataException thrown if error occurs
     */
    protected void insertCodeListTableEntry(JdbcDataConnection dataConnection, CodeListIfc codeList, CodeEntryIfc entry)
            throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // determine if this is default entry
        boolean isDefault = false;
        if (codeList.getDefaultCodeString().equals(entry.getCode()))
        {
            isDefault = true;
        }
        // Table
        sql.setTable(TABLE_CODE_LIST);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(codeList));
        sql.addColumn(FIELD_CODE_LIST_DESCRIPTION, getListDescription(codeList));
        sql.addColumn(FIELD_CODE_LIST_GROUP_NAME, getGroupName(codeList));
        sql.addColumn(FIELD_CODE_LIST_NUMERIC_CODES_FLAG, makeStringFromBoolean(codeList.isNumericCodes()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_TEXT, getEntryText(entry));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_CODE, getEntryCodeString(entry));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, entry.getSortIndex());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_ENABLED_FLAG, makeStringFromBoolean(entry.isEnabled()));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertCodeListTableEntry", e);
        }

    }

    /**
     * Replaces a department list in the department table. The previous entries
     * for this list are lost.
     * <P>
     * To facilitate the replacement, the existing records are deleted and the
     * new records inserted.
     * 
     * @param dataConnection connection to the db
     * @param codeList code list object
     * @return true if successful
     * @exception DataException upon error
     */
    protected boolean updateDepartmentTable(JdbcDataConnection dataConnection, CodeListIfc codeList)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveCodeList.updateDepartmentTable()");

        boolean returnCode = false;

        // if corporate update, handle POS department table
        if (codeList.getStoreID().equals(STORE_ID_CORPORATE))
        {
            try
            {
                deletePOSDepartmentTableEntries(dataConnection, codeList);
            }
            // if data not found, it's OK
            catch (DataException de)
            {
                if (de.getErrorCode() != DataException.NO_DATA)
                {
                    logger.error(de);
                    throw de;
                }
            }
            // insert new records
            returnCode = insertPOSDepartmentTableEntries(dataConnection, codeList);
        }

        try
        {
            // delete existing records
            deleteRetailStoreDepartmentTableEntries(dataConnection, codeList);
        }
        // if data not found, it's OK
        catch (DataException de)
        {
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                logger.error(de);
                throw de;
            }
        }

        // insert new records
        returnCode = insertRetailStoreDepartmentTableEntries(dataConnection, codeList);

        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveCodeList.updateDepartmentTable()");

        return (returnCode);
    }

    /**
     * Deletes all retail-store-department table entries.
     * 
     * @param dataConnection The connection to the data source
     * @param codeList CodeListIfc object
     * @exception DataException thrown if error occurs
     */
    protected void deleteRetailStoreDepartmentTableEntries(JdbcDataConnection dataConnection, CodeListIfc codeList)
            throws DataException
    {
        SQLDeleteStatement sql = new SQLDeleteStatement();

        // table
        sql.setTable(TABLE_RETAIL_STORE_POS_DEPARTMENT);

        // qualifier
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(codeList));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "deleteRetailStoreDepartmentTableEntries", e);
        }

    }

    /**
     * Deletes all POS-department table entries.
     * 
     * @param dataConnection The connection to the data source
     * @param codeList CodeListIfc object
     * @exception DataException thrown if error occurs
     */
    protected void deletePOSDepartmentTableEntries(JdbcDataConnection dataConnection, CodeListIfc codeList)
            throws DataException
    {
        SQLDeleteStatement sql = new SQLDeleteStatement();

        // table
        sql.setTable(TABLE_POS_DEPARTMENT);

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "deletePOSDepartmentTableEntries", e);
        }

    }

    /**
     * Inserts retail-store-department table entries.
     * 
     * @param dataConnection The connection to the data source
     * @param codeList CodeListIfc object
     * @return boolean indicator that records were inserted
     * @exception DataException thrown if error occurs
     */
    protected boolean insertRetailStoreDepartmentTableEntries(JdbcDataConnection dataConnection, CodeListIfc codeList)
            throws DataException
    {
        boolean returnCode = false;
        // loop through entries and insert
        CodeEntryIfc[] entries = codeList.getEntries();
        int numEntries = codeList.getNumberOfEntries();
        for (int i = 0; i < numEntries; i++)
        {
            insertRetailStoreDepartmentTableEntry(dataConnection, codeList, entries[i]);

            if (!(codeList.getStoreID().equals(STORE_ID_CORPORATE)))
            {
                updatePOSDepartmentTable(dataConnection, entries[i]);
            }
        }
        // set return code if records inserted
        if (numEntries > 0)
        {
            returnCode = true;
        }
        return (returnCode);
    }

    /**
     * Inserts POS-department table entries.
     * 
     * @param dataConnection The connection to the data source
     * @param codeList CodeListIfc object
     * @return boolean indicator that records were inserted
     * @exception DataException thrown if error occurs
     */
    protected boolean insertPOSDepartmentTableEntries(JdbcDataConnection dataConnection, CodeListIfc codeList)
            throws DataException
    {
        boolean returnCode = false;
        // loop through entries and insert
        CodeEntryIfc[] entries = codeList.getEntries();
        int numEntries = codeList.getNumberOfEntries();
        for (int i = 0; i < numEntries; i++)
        {
            insertPOSDepartmentTableEntry(dataConnection, entries[i]);
        }
        // set return code if records inserted
        if (numEntries > 0)
        {
            returnCode = true;
        }
        return (returnCode);
    }

    /**
     * Inserts an entry into the department table.
     * 
     * @param dataConnection connection to the data source
     * @param codeList code list object
     * @param entry code entry object
     * @exception DataException thrown if error occurs
     */
    protected void insertRetailStoreDepartmentTableEntry(JdbcDataConnection dataConnection, CodeListIfc codeList,
            CodeEntryIfc entry) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // determine if this is default entry
        boolean isDefault = false;
        if (codeList.getDefaultCodeString().equals(entry.getCode()))
        {
            isDefault = true;
        }

        // Table
        sql.setTable(TABLE_RETAIL_STORE_POS_DEPARTMENT);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(codeList));
        sql.addColumn(FIELD_POS_DEPARTMENT_ID, getEntryCodeString(entry));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, entry.getSortIndex());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_ENABLED_FLAG, makeStringFromBoolean(entry.isEnabled()));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertRetailStoreDepartmentTableEntry", e);
        }

    }

    /**
     * Updates POS department table with possible change in department name.
     * 
     * @param dataConnection connection to the data source
     * @param entry code entry object
     * @exception DataException thrown if error occurs
     */
    protected void updatePOSDepartmentTable(JdbcDataConnection dataConnection, CodeEntryIfc entry) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_POS_DEPARTMENT);

        // Fields
        sql.addColumn(FIELD_POS_DEPARTMENT_NAME, getEntryText(entry));

        // Qualifier
        sql.addQualifier(FIELD_POS_DEPARTMENT_ID + " = " + getEntryCodeString(entry));
        try
        {
            dataConnection.execute(sql.getSQLString());

            // insert if no updates
            if (0 == dataConnection.getUpdateCount())
            {
                insertPOSDepartmentTableEntry(dataConnection, entry);
            }
        }
        // if data not found, it's OK
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                insertPOSDepartmentTableEntry(dataConnection, entry);
            }
            else
            {
                logger.error(de);
                throw de;
            }
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updatePOSDepartmentTable", e);
        }

    }

    /**
     * Inserts row into POS department table.
     * 
     * @param dataConnection connection to the data source
     * @param entry code entry object
     */
    protected void insertPOSDepartmentTableEntry(JdbcDataConnection dataConnection, CodeEntryIfc entry)
            throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_POS_DEPARTMENT);

        // Fields
        sql.addColumn(FIELD_POS_DEPARTMENT_NAME, getEntryText(entry));
        sql.addColumn(FIELD_POS_DEPARTMENT_ID, getEntryCodeString(entry));
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        // if data not found, it's OK
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updatePOSDepartmentTable", e);
        }

    }

    /**
     * Updates the unit of measure table entries. An update is attempted on each
     * entry in the list. If the update fails, an insert is attempted.
     * 
     * @param dataConnection connection to the db
     * @param codeList code list object
     * @return true if successful
     * @exception DataException upon error
     */
    protected boolean updateUnitOfMeasureTable(JdbcDataConnection dataConnection, CodeListIfc codeList)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveUnitOfMeasure.updateUnitOfMeasureTable()");

        boolean returnCode = false;
        // loop through entries and insert
        CodeEntryIfc[] entries = codeList.getEntries();
        int numEntries = codeList.getNumberOfEntries();
        for (int i = 0; i < numEntries; i++)
        {
            returnCode = updateUnitOfMeasureTableEntry(dataConnection, codeList, entries[i]);
            if (returnCode == false)
            {
                insertUnitOfMeasureTableEntry(dataConnection, codeList, entries[i]);
            }
        }
        // set return code if records inserted
        if (numEntries > 0)
        {
            returnCode = true;
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveUnitOfMeasure.updateUnitOfMeasureTable()");

        return (returnCode);
    }

    /**
     * Updates an entry in the unit-of-measure table.
     * <P>
     * 
     * @param dataConnection connection to the data source
     * @param codeList code list object
     * @param entry code entry object
     * @return if record updated; false otherwise
     * @exception DataException thrown if error occurs
     */
    protected boolean updateUnitOfMeasureTableEntry(JdbcDataConnection dataConnection, CodeListIfc codeList,
            CodeEntryIfc entry) throws DataException
    {
        boolean returnCode = false;
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // determine if this is default entry
        boolean isDefault = false;
        if (codeList.getDefaultCodeString().equals(entry.getCode()))
        {
            isDefault = true;
        }
        // Table
        sql.setTable(TABLE_UNIT_OF_MEASURE);

        // Fields
        sql.addColumn(FIELD_UNIT_OF_MEASURE_NAME, getEntryText(entry));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, Integer.toString(entry.getSortIndex()));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_ENABLED_FLAG, makeStringFromBoolean(entry.isEnabled()));

        // qualifier
        sql.addQualifier(FIELD_UNIT_OF_MEASURE_CODE + " = " + getEntryCodeString(entry));

        try
        {
            dataConnection.execute(sql.getSQLString());
            if (0 < dataConnection.getUpdateCount())
            {
                returnCode = true;
            }
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateUnitOfMeasureTableEntry", e);
        }
        return (returnCode);
    }

    /**
     * Inserts an entry into the unit-of-measure table.
     * 
     * @param dataConnection connection to the data source
     * @param codeList code list object
     * @param entry code entry object
     * @exception DataException thrown if error occurs
     */
    protected void insertUnitOfMeasureTableEntry(JdbcDataConnection dataConnection, CodeListIfc codeList,
            CodeEntryIfc entry) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // determine if this is default entry
        boolean isDefault = false;
        if (codeList.getDefaultCodeString().equals(entry.getCode()))
        {
            isDefault = true;
        }
        // Table
        sql.setTable(TABLE_UNIT_OF_MEASURE);

        // Fields
        sql.addColumn(FIELD_UNIT_OF_MEASURE_NAME, getEntryText(entry));
        sql.addColumn(FIELD_UNIT_OF_MEASURE_CODE, getEntryCodeString(entry));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, entry.getSortIndex());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_ENABLED_FLAG, makeStringFromBoolean(entry.isEnabled()));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertUnitOfMeasureTableEntry", e);
        }
    }

    /**
     * Replaces a manual discount rule list in the price derivation rule table.
     * The previous entries for this list are lost.
     * <P>
     * To facilitate the replacement, the existing records are deleted and the
     * new records inserted.
     * 
     * @param dataConnection connection to the db
     * @param codeList manual discount rule object
     * @return true if successful
     * @exception DataException upon error
     */
    protected boolean updatePriceDerivationRuleTable(JdbcDataConnection dataConnection, CodeListIfc codeList)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveCodeList.updatePriceDerivationRuleTable()");

        boolean returnCode = false;

        int scope = getDiscountScope(codeList.getListDescription());
        int method = getDiscountMethod(codeList.getListDescription(), scope);
        // loop through entries and insert
        CodeEntryIfc[] entries = codeList.getEntries();
        int numEntries = codeList.getNumberOfEntries();
        for (int i = 0; i < numEntries; i++)
        {
            returnCode = updatePriceDerivationRuleTableEntry(dataConnection, codeList, entries[i], scope, method);

            if (returnCode == false)
            {
                insertPriceDerivationRuleTableEntry(dataConnection, codeList, entries[i], scope, method);
            }
        }
        // set return code if records inserted
        if (numEntries > 0)
        {
            returnCode = true;
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveCodeList.updatePriceDerivationRuleTable()");

        return (returnCode);
    }

    /**
     * Updates an entry in the price derivation rule table.
     * 
     * @param dataConnection connection to the data source
     * @param codeList manual discount rule object
     * @param entry code entry object
     * @param scope scope of discount rule
     * @param method method of discount rule
     * @return true if records updated, false if no records updated
     * @exception DataException thrown if error occurs
     */
    protected boolean updatePriceDerivationRuleTableEntry(JdbcDataConnection dataConnection, CodeListIfc codeList,
            CodeEntryIfc entry, int scope, int method) throws DataException
    {
        boolean returnCode = false;

        // Can't update if we don't have primary key Discount Rule ID
        if (!Util.isEmpty(entry.getReferenceKey()))
        {
            SQLUpdateStatement sql = new SQLUpdateStatement();

            // determine if this is default entry
            boolean isDefault = false;
            if (codeList.getDefaultCodeString().equals(entry.getCode()))
            {
                isDefault = true;
            }
            // derive status from entry
            int status = STATUS_INACTIVE;
            if (entry.isEnabled())
            {
                status = STATUS_ACTIVE;
            }
            // Table
            sql.setTable(TABLE_PRICE_DERIVATION_RULE);

            // Fields
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE, scope);
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, method);
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_NAME, getEntryText(entry));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_DESCRIPTION, getEntryText(entry));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_REASON_CODE, entry.getCode());
            sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_STATUS_CODE, makeSafeString(STATUS_DESCRIPTORS[status]));
            sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, Integer.toString(entry.getSortIndex()));

            sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(codeList));
            sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_ID + " = " + getRuleID(entry));

            try
            {
                dataConnection.execute(sql.getSQLString());
            }
            catch (DataException de)
            {
                logger.error(de);
                throw de;
            }
            catch (Exception e)
            {
                logger.error(e);
                throw new DataException(DataException.UNKNOWN, "updatePriceDerivationRuleTableEntry", e);
            }

            // confirm update occurred
            if (0 < dataConnection.getUpdateCount())
            {
                returnCode = true;
            }
        }

        return (returnCode);
    }

    /**
     * Inserts an entry into the price derivation rule table.
     * 
     * @param dataConnection connection to the data source
     * @param codeList manual discount rule object
     * @param entry code entry object
     * @param scope scope of discount rule
     * @param method method of discount rule
     * @exception DataException thrown if error occurs
     */
    protected void insertPriceDerivationRuleTableEntry(JdbcDataConnection dataConnection, CodeListIfc codeList,
            CodeEntryIfc entry, int scope, int method) throws DataException
    {

        if (Util.isEmpty(entry.getReferenceKey()))
        {
            entry.setReferenceKey(getPriceDerivationRuleID(dataConnection, codeList.getStoreID()));
        }
        SQLInsertStatement sql = new SQLInsertStatement();

        // determine if this is default entry
        boolean isDefault = false;
        if (codeList.getDefaultCodeString().equals(entry.getCode()))
        {
            isDefault = true;
        }
        // derive status from entry
        int status = STATUS_INACTIVE;
        if (entry.isEnabled())
        {
            status = STATUS_ACTIVE;
        }
        // Table
        sql.setTable(TABLE_PRICE_DERIVATION_RULE);

        // Fields
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE, scope);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, method);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_NAME, getEntryText(entry));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_DESCRIPTION, getEntryText(entry));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_REASON_CODE, entry.getCode());
        sql.addColumn(FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG, makeStringFromBoolean(isDefault));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_STATUS_CODE, makeSafeString(STATUS_DESCRIPTORS[status]));
        sql.addColumn(FIELD_CODE_LIST_ENTRY_SORT_INDEX, Integer.toString(entry.getSortIndex()));
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(codeList));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE, ASSIGNMENT_MANUAL);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ID, getRuleID(entry));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_TRANSACTION_CONTROL_BREAK_CODE, "'" + APPLIED_CODES[APPLIED_DETAIL]
                + "'");
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE, dateToSQLTimestampString(DEFAULT_BEGIN_DATE));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE, dateToSQLTimestampString(DEFAULT_END_DATE));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_INCLUDED_IN_BEST_DEAL_FLAG,
                makeStringFromBoolean(Boolean.valueOf("false").booleanValue()));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_TYPE_CODE, "'"
                + DISCOUNT_APPLICATION_TYPE_CODE[DISCOUNT_APPLICATION_TYPE_MANUAL] + "'");
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertPriceDerivationRuleTableEntry", e);
        }
    }

    /**
     * Returns discount scope derived from code list description.
     * 
     * @param desc list description
     * @return scope constant
     * @exception DataException thrown if scope cannot be determined
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc
     */
    protected int getDiscountScope(String desc) throws DataException
    {
        int scope = -1;
        if (desc.equals(CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE)
                || desc.equals(CODE_LIST_TRANSACTION_DISCOUNT_BY_AMOUNT)
                || desc.equals(CODE_LIST_PREFERRED_CUSTOMER_DISCOUNT))
        {
            scope = DISCOUNT_SCOPE_TRANSACTION;
        }
        else if (desc.equals(CODE_LIST_ITEM_DISCOUNT_BY_PERCENTAGE) || desc.equals(CODE_LIST_ITEM_DISCOUNT_BY_AMOUNT))
        {
            scope = DISCOUNT_SCOPE_ITEM;
        }
        else
        {
            // if not found, issue message and not-found exception
            String msg = "JdbcSaveCodeList.updatePriceDerivationRuleTable:  " + "Code list has unknown discount type ["
                    + desc + "].";
            throw new DataException(DataException.NO_DATA, msg);
        }

        return (scope);
    }

    /**
     * Returns discount method derived from code list description.
     * 
     * @param desc list description
     * @param scope discount scope
     * @return method constant
     * @exception DataException thrown if method cannot be determined
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc
     */
    protected int getDiscountMethod(String desc, int scope) throws DataException
    {
        int method = DISCOUNT_METHOD_NONE;
        switch (scope)
        {
        case DISCOUNT_SCOPE_TRANSACTION:
            if (desc.equals(CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE))
            {
                method = DISCOUNT_METHOD_PERCENTAGE;
            }
            else if (desc.equals(CODE_LIST_TRANSACTION_DISCOUNT_BY_AMOUNT))
            {
                method = DISCOUNT_METHOD_AMOUNT;
            }
            break;
        case DISCOUNT_SCOPE_ITEM:
            if (desc.equals(CODE_LIST_ITEM_DISCOUNT_BY_PERCENTAGE))
            {
                method = DISCOUNT_METHOD_PERCENTAGE;
            }
            else if (desc.equals(CODE_LIST_ITEM_DISCOUNT_BY_AMOUNT))
            {
                method = DISCOUNT_METHOD_AMOUNT;
            }
            break;
        }
        // if no method found, throw exception)
        if (method == DISCOUNT_METHOD_NONE)
        {
            // if not found, issue message and not-found exception
            String msg = "JdbcSaveCodeList.updatePriceDerivationRuleTable:  " + "Code list has unknown discount type ["
                    + desc + "].";
            throw new DataException(DataException.NO_DATA, msg);
        }

        return (method);
    }

    /**
     * Gets unique identifier for reference key. This is done by getting the
     * maximum value for rule identifier in the price derivation rule table and
     * adding one to it.
     * 
     * @param dataConnection connection to data source
     * @param storeID store identifier
     * @return rule identifier
     * @exception DataException thrown if error occurs
     */
    public String getPriceDerivationRuleID(JdbcDataConnection dataConnection, String storeID) throws DataException
    {
        int id = IdentifierServiceLocator.getIdentifierService().getNextID(dataConnection.getConnection(),
                IdentifierConstantsIfc.COUNTER_PRICE_DERIVATION_RULE);
        return String.valueOf(id);
    }

    /**
     * Returns store identifier from code list formatted for use in SQL
     * statement.
     * 
     * @param codeList code list object
     * @return formatted store identifier
     */
    protected String getStoreID(CodeListIfc codeList)
    {
        return ("'" + codeList.getStoreID() + "'");
    }

    /**
     * Returns list description from code list formatted for use in SQL
     * statement.
     * 
     * @param codeList code list object
     * @return formatted list description
     */
    protected String getListDescription(CodeListIfc codeList)
    {
        return (makeSafeString(codeList.getListDescription()));
    }

    /**
     * Returns list group name from code list formatted for use in SQL
     * statement.
     * 
     * @param codeList code list object
     * @return formatted list group name
     */
    protected String getGroupName(CodeListIfc codeList)
    {
        return (makeSafeString(emptyStringToSpaceString(codeList.getGroupName())));
    }

    /**
     * Returns entry text from code list entry formatted for use in SQL
     * statement.
     * 
     * @param entry CodeEntryIfc object
     * @return formatted entry text
     */
    protected String getEntryText(CodeEntryIfc entry)
    {
        Locale lcl = LocaleMap.getLocale(LocaleMap.DEFAULT);
        return (makeSafeString(entry.getText(lcl)));
    }

    /**
     * Returns entry code from code list entry formatted for use in SQL
     * statement.
     * 
     * @param entry CodeEntryIfc object
     * @return formatted entry code
     */
    protected String getEntryCodeString(CodeEntryIfc entry)
    {
        return ("'" + entry.getCode() + "'");
    }

    /**
     * Returns price derivation rule ID as reference key from code list entry
     * formatted for use in SQL statement.
     * 
     * @param entry CodeEntryIfc object
     * @return formatted reference key
     */
    protected String getRuleID(CodeEntryIfc entry)
    {
        return (entry.getReferenceKey());
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Returns the string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        return (Util.classToStringHeader("JdbcSaveCodeList", getRevisionNumber(), hashCode()).toString());
    }

}
