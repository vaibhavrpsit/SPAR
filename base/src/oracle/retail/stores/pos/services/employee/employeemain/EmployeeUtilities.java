/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeemain/EmployeeUtilities.java /rgbustores_13.4x_generic_branch/2 2011/07/04 12:12:27 masahu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    masahu    07/04/11 - FORTIFY FIX: Employee Sensitive informstion is
 *                         printed in logs and in console
 *    abondala  04/11/11 - XbranchMerge abondala_bug11827952-salting_passwords
 *                         from main
 *    abondala  03/28/11 - update after code review comments
 *    abondala  03/25/11 - implement salting for the passwords
 *    abondala  03/23/11 - Implemented salting for the passwords
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *6    360Commerce 1.5         11/12/2007 2:14:22 PM  Tony Zgarba
 *     Deprecated all existing encryption APIs and migrated the code to the
 *     new encryption API.
 *5    360Commerce 1.4         10/12/2006 8:17:49 AM  Christian Greene Adding
 *     new functionality for PasswordPolicy.  Employee password will now be
 *     persisted as a byte[] in hexadecimal.  Updates include UI changes,
 *     persistence changes, and AppServer configuration changes.  A database
 *     rebuild with the new SQL scripts will be required.
 *4    360Commerce 1.3         9/29/2006 11:36:15 AM  Christian Greene
 *     determine password length from parameters and pass to secmgr
 *3    360Commerce 1.2         3/31/2005 4:27:59 PM   Robert Pearse
 *2    360Commerce 1.1         3/10/2005 10:21:21 AM  Robert Pearse
 *1    360Commerce 1.0         2/11/2005 12:10:51 PM  Robert Pearse
 *
 Revision 1.4  2004/04/09 16:56:01  cdb
 @scr 4302 Removed double semicolon warnings.
 *
 Revision 1.3  2004/02/12 16:50:19  mcs
 Forcing head revision
 *
 Revision 1.2  2004/02/11 21:49:04  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 updating to pvcs 360store-current
 *
 *
 *    Rev 1.0   Sep 21 2001 11:23:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeemain;

import java.io.Serializable;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;

import org.apache.log4j.Logger;

/**
 * The EmployeeUtilites contains methods that are shared by more than one
 * Employee service.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class EmployeeUtilities {

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger
            .getLogger(EmployeeUtilities.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * Default maximum matches to use when parameter is not available.
     */
    protected static int defaultMaximumMatches = 20;

    /**
     * Default temporary password length to use when parameter is not available.
     */
    protected static int defaultTemporaryPasswordLength = 12;

    /**
     * Get Maximum matches retireves the maximum matches parameter or sets the
     * maximum matches to a default.
     * @param parameterManager
     * @param callingSite
     * @param operatorID
     * @return value of maximum matches
     * @exception
     */
    public static int getMaximumMatches(ParameterManagerIfc parameterManager, String callingSite, String operatorID) {
        // look up maximum matches parameter

        String maximumMatches = "";
        int maxMatches;
        boolean bOk = true;
        Integer intMaximum;

        Serializable[] values = null;

        maxMatches = defaultMaximumMatches; // initialize to default
        try { // begin try maximum matches parameter
            String parm = new String("EmployeeMaximumMatches");
            values = parameterManager.getParameterValues(parm);
            maximumMatches = (String) values[0];
        } // end try maximum matches parameter
        catch (ParameterException e) { // begin catch maximum matches parameter
            bOk = false;
            logger.warn("Maximum employee matches parameter could not be found.");

        } // end catch parameter exception

        if (bOk) // if the parameter was retrieved
        {
            try // make it into an Integer
            {
                // make the string into an integer.
                intMaximum = new Integer(maximumMatches);
                // make the Integer into an int
                maxMatches = intMaximum.intValue();
            } catch (NumberFormatException e) // a number exception occurs
            {
                // log the exception
            }

        } // end if the parameter was retrieved

        return (maxMatches);
    }// end getMaximumMatches

    /**
     * Get the length to generate temporary passwords, as specified by the
     * parameters.  The {@value #defaultTemporaryPasswordLength} will be used
     * if the parameters are not found
     * <p>
     * An error will be logged if the parameter could not be retrieved.
     * @param parameterManager The Parameter Manager
     * @return the length to generate temporary passwords.
     */
    public static int getTemporaryPasswordLength(ParameterManagerIfc parameterManager) {
        try
        {
            Serializable[] values = parameterManager.getParameterValues("TemporaryPasswordLength");
            String tempLength = (String)values[0];
            return Integer.parseInt(tempLength);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            logger.error("Temporary password length parameter could not be found.", e);
        }
        catch (ParameterException ex)
        {
            logger.warn("Temporary password length parameter could not be found.");
        }
        catch (NumberFormatException nfe)
        {
            logger.error("Temporary password length parameter could be parsed into a number.", nfe);
        }
        return defaultTemporaryPasswordLength;
    }// end getTemporaryPasswordLength

    /**
     * Convienence method to convert a plain text string to a hashed byte array
     * then set it to the specified employee.
     *
     * @param cryptoManager the manager capable of encryption
     * @param employee The employee to receive the hashed password.
     * @param plainTextPassword the password to hash
     * @return true if the password is successfully hashed and set
     */
    public static boolean hashAndSetPassword(
            KeyStoreEncryptionManagerIfc cryptoManager,
            EmployeeIfc employee,
            String plainTextPassword)
    {
        try
        {

            byte[] bytes = cryptoManager.getUTF8Bytes(plainTextPassword);

            String pwdSalt = cryptoManager.getRandomUniqueID();
            bytes =  cryptoManager.superHash(bytes, pwdSalt, false);

            // apply hashed password
            employee.setPasswordBytes(bytes);
            employee.setEmployeePasswordSalt(pwdSalt);
            return true;
        }
        catch (EncryptionServiceException e)
        {
            logger.error("An error occurred hashing password for " + employee.getLoginID(), e);
        }
        return false;
    }// end hashAndSetPassword

}// end class EmployeeUtilites
