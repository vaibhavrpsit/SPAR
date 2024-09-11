/* ===========================================================================
* Copyright (c) 2006, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/dao/EmployeeDAO.java /main/21 2013/04/30 21:48:40 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  04/30/13 - employee id can be alphanumeric.
 *    rabhawsa  03/15/13 - Update the the employee with correct alternate id
 *                         during update so that alternate id is not reset to
 *                         login id during login
 *    abhineek  02/06/13 - fix for update employee's first & last name
 *    mkutiana  09/12/12 - Modifications to support Biometrics Quickwin -
 *                         support for multiple FP per employee
 *    abondala  04/11/11 - XbranchMerge abondala_bug11827952-salting_passwords
 *                         from main
 *    abondala  03/28/11 - update after code review comments
 *    abondala  03/25/11 - merging
 *    abondala  03/23/11 - Implemented salting for the passwords
 *    hyin      02/28/11 - convert to blob
 *    hyin      02/18/11 - add last login column
 *    hyin      02/01/11 - remove unused method
 *    hyin      01/28/11 - change fingerprint column to varchar to accommodate
 *                         derby limitation
 *    blarsen   01/18/11 - Integrated changes to BlobFactory.
 *    blarsen   06/09/10 - XbranchMerge blarsen_biometrics-poc from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    blarsen   05/25/10 - Added support for blob updates. This is for the new
 *                         fingerprint biometric template which supports login
 *                         via fingerprint.
 *    rsnayak   07/19/10 - Employee update fix
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  01/29/09 - updated files related to hashing algorithm which can
 *                         be configured through properties file.
 *    glwang    12/08/08 - deprecated employee full name
 *    glwang    12/08/08 - save employee full name as first + last if first +
 *                         middle + last exceeds 250 chars
 *    glwang    12/05/08 - build full name with only first name and last name
 *                         if first+middle+last exceed 250 bytes
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         11/2/2006 9:45:58 AM   Christian Greene
 *       refactor DAO functions and add some methods for getting SQL so that
 *       Spring can set them if they need to change
 *  2    360Commerce 1.1         10/13/2006 2:48:38 PM  Christian Greene Added
 *       null-check around password creation date when inserting an employee
 *  1    360Commerce 1.0         10/12/2006 8:17:59 AM  Christian Greene
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.dao;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.utility.EncodingIfc;
import oracle.retail.stores.domain.arts.JdbcSaveEmployee;
import oracle.retail.stores.domain.data.AbstractDAO;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.data.DataException;

import org.apache.log4j.Logger;

/**
 * @author cgreene
 *
 */
public class EmployeeDAO extends AbstractDAO implements EmployeeDAOIfc, EmployeeSQLIfc
{

    /** Logger for debug logging. */
    private static final Logger logger = Logger.getLogger(EmployeeDAO.class);

    private static final int FULL_NAME_MAX_BYTE_LEN = 250;

    /**
     * Default constructor.  Sets {@link EmployeeSQLIfc#INSERT_EMPLOYEE_SQL}
     * and {@link EmployeeSQLIfc#UPDATE_EMPLOYEE_SQL}.
     */
    public EmployeeDAO () {
        setInsertSQL(INSERT_EMPLOYEE_SQL);
        setUpdateSQL(UPDATE_EMPLOYEE_SQL + where(FIELD_EMPLOYEE_ID));
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.arts.dao.EmployeeDAOIfc#insert(oracle.retail.stores.domain.employee.EmployeeIfc)
     */
    public boolean insert(EmployeeIfc employee) throws DataException
    {
        try
        {
            PreparedStatement ps = getInsertStatement(employee);
            return executeUpdate(ps);
        }
        catch(SQLException se)
        {
            logger.error(se);
            throw new DataException(DataException.SQL_ERROR, "Insert Employee", se);
        }
    } // end insert(EmployeeIfc)

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.arts.dao.EmployeeDAOIfc#update(oracle.retail.stores.domain.employee.EmployeeIfc)
     */
    public boolean update(EmployeeIfc employee) throws DataException
    {
        try
        {
            PreparedStatement ps = getUpdateStatement(employee);
            if (executeUpdate(ps))
            {
                return updateFingerprints(employee);
            }
            else
            {
                return false;
            }
        
        }
        catch(SQLException se)
        {
            logger.error(se);
            throw new DataException(DataException.SQL_ERROR, "Update Employee", se);
        }
    } // end update(EmployeeIfc)

    /*
     * Update the fingerprint data for the provided employee. First delete any existing data.
     *
     * @param employee The employee who's fingerprint data is to be updated.
     *                 Fingerprint data is located @see oracle.retail.stores.persistence.utility.ArtsDatabaseIfc.TABLE_BIOMETRIC_DATA
     * @return Returns true if update was successful.
     * @throws DataException
     */
    private boolean updateFingerprints(EmployeeIfc employee) throws DataException
    {
        boolean status = false;
        try
        {
            
            List<byte[]> employeeFingerprints = employee.getFingerprintBiometrics();
            //null check in case the flow is not biometric enabled
            if( null != employeeFingerprints){
            	
            	//Before adding a new fingerprint to an employee delete all the old ones associated with that employee
                deleteFingerprints(employee);
                
            	for(byte[] fp : employeeFingerprints){
                    PreparedStatement stmt = connection.prepareStatement(" INSERT INTO CO_DTA_BMC(ID_EM,VL_ACS_EM_BMC)  "
                            + " VALUES(?, ?)");
                    stmt.setString(1, employee.getEmployeeID());
                    stmt.setBlob(2, new ByteArrayInputStream(fp));
                    executeUpdate(stmt);
                    stmt.close();                
                }
            }
            
            status = true;        
         }
        catch(SQLException se)
        {
            logger.error(se);
            throw new DataException(DataException.SQL_ERROR, "Update Employee", se);
        }
        
        return status;
    }
    
    /*
     * Delete Biometrics Data for an Employee.
     * @param employee The employee for whom ALL fingerprints data is to be deleted.
     *                  Biometric or Fingerprint data is located @see oracle.retail.stores.persistence.utility.ArtsDatabaseIfc.TABLE_BIOMETRIC_DATA
     * @return void 
     * @throws DataException
     */
    private void deleteFingerprints(EmployeeIfc employee) throws DataException {
        SQLDeleteStatement deleteSQL = new SQLDeleteStatement();
        boolean result = false;
        try
        {
            deleteSQL.setTable(TABLE_BIOMETRIC_DATA);
            deleteSQL.addQualifier(FIELD_BIOMETRICDATA_EMPLOYEE_ID , inQuotes(employee.getEmployeeID()));
            PreparedStatement ps = connection.prepareStatement(deleteSQL.getSQLString());
            result = executeUpdate(ps);
            ps.close();
        }
        catch(SQLException se)
        {
            logger.error(se);
            throw new DataException(DataException.SQL_ERROR, "Delete Fingerprints for employee", se);
        }
        finally
        {
            logger.debug(deleteSQL.getSQLString() + " " + result);
        }
    }
    

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.arts.dao.EmployeeDAOIfc#getInsertStatement(oracle.retail.stores.domain.employee.EmployeeIfc)
     */
    public PreparedStatement getInsertStatement(EmployeeIfc employee)
        throws SQLException
    {
        String insertSql = getInsertSQL();
        logger.debug(insertSql);
        PreparedStatement ps = connection.prepareStatement(insertSql);
        ps.setString(IDX_EMPLOYEE_ID, employee.getEmployeeID());
        ps.setString(IDX_PARTY_ID, JdbcSaveEmployee.PRTY_ID_EMPLOYEE);
        ps.setString(IDX_EMPLOYEE_ID_LOGIN, employee.getLoginID());
        ps.setString(IDX_EMPLOYEE_ID_ALT, employee.getLoginID());
        String hex = JdbcUtilities.base64encode(employee.getPasswordBytes());
        ps.setString(IDX_EMPLOYEE_ACCESS_PASSWORD, hex);
        ps.setString(IDX_EMPLOYEE_NAME, this.getEmployeeFullName(employee.getPersonName()));
        ps.setString(IDX_EMPLOYEE_LAST_NAME, employee.getPersonName().getLastName());
        ps.setString(IDX_EMPLOYEE_FIRST_NAME, employee.getPersonName().getFirstName());
        ps.setString(IDX_EMPLOYEE_MIDDLE_NAME, employee.getPersonName().getMiddleName());
        ps.setInt(IDX_EMPLOYEE_STATUS_CODE, employee.getLoginStatus());
        ps.setInt(IDX_EMPLOYEE_ROLE_ID, employee.getRole().getRoleID());
        Locale preferredLocale = employee.getPreferredLocale();
        ps.setString(IDX_LOCALE, (preferredLocale != null)? preferredLocale.toString() : null);
        ps.setInt(IDX_EMPLOYEE_NUMBER_OF_DAYS_VALID, employee.getDaysValid());
        set360DateAsDate(ps, IDX_EMPLOYEE_EXPIRATION_DATE, employee.getExpirationDate());
        ps.setInt(IDX_EMPLOYEE_TYPE, employee.getType().getDBVal());
        ps.setString(IDX_EMPLOYEE_STORE_ID, employee.getStoreID());
        ps.setBoolean(IDX_EMPLOYEE_NEW_PASSWORD_REQUIRED, employee.isPasswordChangeRequired());
        if (employee.getPasswordCreationDate() != null)
        {
            setDateAsTimestamp(ps, IDX_EMPLOYEE_PASSWORD_CREATED_TIMESTAMP, new java.sql.Date(employee.getPasswordCreationDate().getTime()));
        }
        else
        {
            setDateAsTimestamp(ps, IDX_EMPLOYEE_PASSWORD_CREATED_TIMESTAMP, new java.sql.Date(System.currentTimeMillis()));
        }
        ps.setInt(IDX_EMPLOYEE_NUMBER_FAILED_PASSWORDS, employee.getNumberFailedPasswords());
        ps.setString(IDX_EMPLOYEE_PASSWORD_SALT, employee.getEmployeePasswordSalt());
        return ps;
    } // end getInsertStatement(EmployeeIfc)

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.employee.dao.EmployeeDAOIfc#update(oracle.retail.stores.domain.employee.EmployeeIfc)
     */
    public PreparedStatement getUpdateStatement(EmployeeIfc employee) throws SQLException
    {
        PreparedStatement ps = null;
        String updateSql = getUpdateSQL();
        logger.debug(updateSql);
        ps = connection.prepareStatement(updateSql);
        ps.setString(IDX_EMPLOYEE_ID, employee.getEmployeeID());
        ps.setString(IDX_PARTY_ID, JdbcSaveEmployee.PRTY_ID_EMPLOYEE);
        ps.setString(IDX_EMPLOYEE_ID_LOGIN, employee.getLoginID());
        ps.setString(IDX_EMPLOYEE_ID_ALT, employee.getAlternateID());
        String hex = JdbcUtilities.base64encode(employee.getPasswordBytes());
        ps.setString(IDX_EMPLOYEE_ACCESS_PASSWORD, hex);
        ps.setString(IDX_EMPLOYEE_NAME, this.getEmployeeFullName(employee.getPersonName()));
        ps.setString(IDX_EMPLOYEE_LAST_NAME, employee.getPersonName().getLastName());
        ps.setString(IDX_EMPLOYEE_FIRST_NAME, employee.getPersonName().getFirstName());
        ps.setString(IDX_EMPLOYEE_MIDDLE_NAME, employee.getPersonName().getMiddleName());
        ps.setInt(IDX_EMPLOYEE_STATUS_CODE, employee.getLoginStatus());
        ps.setInt(IDX_EMPLOYEE_ROLE_ID, employee.getRole().getRoleID());
        Locale preferredLocale = employee.getPreferredLocale();
        ps.setString(IDX_LOCALE, (preferredLocale != null)? preferredLocale.toString() : null);
        ps.setInt(IDX_EMPLOYEE_NUMBER_OF_DAYS_VALID, employee.getDaysValid());
        set360DateAsDate(ps, IDX_EMPLOYEE_EXPIRATION_DATE, employee.getExpirationDate());
        ps.setInt(IDX_EMPLOYEE_TYPE, employee.getType().getDBVal());
        ps.setString(IDX_EMPLOYEE_STORE_ID, employee.getStoreID());
        ps.setBoolean(IDX_EMPLOYEE_NEW_PASSWORD_REQUIRED, employee.isPasswordChangeRequired());
        setDateAsTimestamp(ps, IDX_EMPLOYEE_PASSWORD_CREATED_TIMESTAMP, new java.sql.Date(employee.getPasswordCreationDate().getTime()));
        ps.setInt(IDX_EMPLOYEE_NUMBER_FAILED_PASSWORDS, employee.getNumberFailedPasswords());
        ps.setString(IDX_EMPLOYEE_PASSWORD_SALT, employee.getEmployeePasswordSalt());
        setDateAsTimestamp(ps,IDX_EMPLOYEE_LAST_LOGIN_TIME, new java.sql.Date(employee.getLastLoginTime().getTime()));

        // add qualifier
        int idxQualifier = IDX_EMPLOYEE_LAST_LOGIN_TIME + 1;
        ps.setString(idxQualifier, employee.getEmployeeID());
        return ps;
    } // end getUpdateStatement(EmployeeIfc)

    /**
     * This returns the full name will be saved into database.
     * Now the employee's full name is built with first + middle + last name.
     * And employee's first, middle, last names are defined as varchar(120) in
     * pa_em table. So the full name might exceed 250 chararters.
     *
     * So if the full name exceeds varchar(250), this returns first + " " + last
     * name as full name.
     *
     * This is related to business logic, and not belong to this class. But since
     * the full name is deprecated, we leave this here to avoid this logic in
     * multiple places.
     * @return
     */
    private String getEmployeeFullName(PersonNameIfc employeeName){
        String fullName = employeeName.getFirstMiddleLastName();
        try 
        {
            byte[] strBytes =fullName.getBytes(EncodingIfc.UTF8);
            if (strBytes != null && strBytes.length > FULL_NAME_MAX_BYTE_LEN) {
                fullName = employeeName.getFirstLastName();
            }
        }
        catch (UnsupportedEncodingException e)
        {
            // something terrible wrong.
            logger.error(e);
        }
        return fullName;
    }
}
