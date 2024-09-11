/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/ReprintReceiptCargo.java /main/18 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   03/06/12 - added setAppID() method so MPOS can set its id.
 *    cgreene   03/01/12 - add system error map to user access cargo
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   02/24/10 - added access function title
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         5/20/2008 11:47:17 AM  Kun Lu          Port
 *         the fix from V7x for CR 31223
 *    6    360Commerce 1.5         6/4/2007 12:50:12 PM   Alan N. Sinton  CR
 *         26484 - Changes per review comments.
 *    5    360Commerce 1.4         4/30/2007 4:56:45 PM   Alan N. Sinton  CR
 *         26484 - Merge from v12.0_temp.
 *    4    360Commerce 1.3         1/25/2006 4:11:42 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:43 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:43 PM  Robert Pearse
 *:
 *    5    .v700     1.2.1.1     11/15/2005 14:57:23    Jason L. DeLeau 4204:
 *         Remove duplicate instances of UserAccessCargoIfc
 *    4    .v700     1.2.1.0     10/31/2005 11:58:35    Deepanshu       CR
 *         6092: Implemented getter/setter method for Sales Associate
 *    3    360Commerce1.2         3/31/2005 15:29:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:24:43     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:13:43     Robert Pearse
 *
 *   Revision 1.8  2004/04/27 22:25:53  dcobb
 *   @scr 4452 Feature Enhancement: Printing
 *   Code review updates.
 *
 *   Revision 1.7  2004/04/26 19:51:14  dcobb
 *   @scr 4452 Feature Enhancement: Printing
 *   Add Reprint Select flow.
 *
 *   Revision 1.6  2004/04/22 21:26:38  dcobb
 *   @scr 4452 Feature Enhancement: Printing
 *   Only completed sale, return or exchange transactions are displayed in REPRINT_SELECT.
 *
 *   Revision 1.5  2004/04/22 17:39:00  dcobb
 *   @scr 4452 Feature Enhancement: Printing
 *   Added REPRINT_SELECT screen and flow to Reprint Receipt use case..
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:51:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Oct 20 2003 16:13:42   epd
 * added empty methods as required by interface
 *
 *    Rev 1.0   Aug 29 2003 16:05:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   29 Jan 2003 08:39:54   mrm
 * Additional method implementation for UserAccessCargoIfc
 * Resolution for POS SCR-1958: Implement JAAS Support
 *
 *    Rev 1.1   Sep 12 2002 13:55:00   jriggins
 * Now nonReprintableErrorCodeToString(int) pulls text from the bundles.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:07:12   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:44:50   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 04 2002 11:05:06   dfh
 * added transactionIDEntered flag (setter/getter)
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *
 *    Rev 1.1   22 Jan 2002 17:54:12   pdd
 *
 * Modified to use the new security design.
 *
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *
 *
 *    Rev 1.0   Sep 21 2001 11:23:10   msg
 *
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 13:12:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.reprintreceipt;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.pos.services.printing.PrintingCargo;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

import org.apache.log4j.Logger;

/**
 * Cargo class for ReprintReceipt service.
 *
 * @see oracle.retail.stores.pos.services.printing.PrintingCargo
 */
public class ReprintReceiptCargo extends PrintingCargo implements DBErrorCargoIfc, UserAccessCargoIfc
{

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ReprintReceiptCargo.class);
    /**
     * revision number of this class
     **/
    public static String revisionNumber = "$Revision: /main/18 $";

    /**
     * non-reprintable error undefined
     */
    public static final int NOT_REPRINTABLE_ERROR_UNDEFINED = -1;

    /**
     * non-reprintable error constant for last-transaction-not-available
     */
    public static final int NOT_REPRINTABLE_ERROR_LAST_TRANSACTION_NOT_AVAILABLE = 0;

    /**
     * non-reprintable error constant for transaction-not-printable
     */
    public static final int NOT_REPRINTABLE_ERROR_TRANSACTION_NOT_PRINTABLE = 1;

    /**
     * non-reprintable error constant for transaction-not-printable
     */
    public static final int NOT_REPRINTABLE_ERROR_DIFFERENT_REGISTER = 2;

    /**
     * non-reprintable error message bundle tags
     */
    public static final String[] NOT_REPRINTABLE_ERROR_DESCRIPTOR_TAGS = { "TransactionIDNotAvailable",
            "NonPrintableTransaction", "DifferentRegister" };

    /**
     * non-reprintable error message default texts
     */
    public static final String[] NOT_REPRINTABLE_ERROR_DESCRIPTORS = { "the last transaction ID is not available.",
            "the transaction is a non-printable transaction.", "the transaction was completed on a different register." };

    /**
     * unknown error bundle tag
     */
    public static final String UNKNOWN_ERROR_TAG = "UnknownError";

    /**
     * unknown error default text
     */
    public static final String UNKNOWN_ERROR_TEXT = "an unknown error occurred (code {0})";

    /**
     * non-reprintable error letter
     */
    public static final String NOT_REPRINTABLE_ERROR_LETTER = "NotReprintable";

    /**
     * letter for successful lookup of a completed sale return exchange
     * transaction
     */
    public static final String SUCCESS_SRE_LETTER = "SuccessSRE";

    /**
     * letter for successful lookup of a till adjustment transaction
     */
    public static final String SUCCESS_TILL_ADJUSTMENT_LETTER = "SuccessTillAdj";

    /**
     * identifier of last reprintable transaction
     */
    protected String lastReprintableTransactionID = "";

    /**
     * transaction identifier
     */
    protected String transactionID = "";

    /**
     * register identifier
     */
    protected String registerID = "";

    /**
     * business date
     */
    protected EYSDate businessDate = null;

    /**
     * access employee
     */
    protected EmployeeIfc accessEmployee = null;

    /**
     * data exception error code
     */
    protected int dataExceptionErrorCode = DataException.NONE;

    /**
     * non-reprintable error
     */
    protected int nonReprintableErrorID = NOT_REPRINTABLE_ERROR_UNDEFINED;

    /**
     * transaction id entered flag
     */
    protected boolean transactionIDEntered = false;

    /**
     * training mode flag
     */
    protected boolean trainingModeFlag = false;

    /**
     * application identifier for POS
     */
    protected String appID = new String(STATIONARY_POS_APPLICATION_NAME);

    /**
     * the override operator
     */
    protected EmployeeIfc overrideOperator = null;

    /**
     * the sales associate
     */
    protected EmployeeIfc salesAssociate = null;

    /**
     * list of selected indexes
     */
    protected int[] selectedIndexes;

    /**
     * Access Function ID value.
     */
    protected int accessFunctionID = RoleFunctionIfc.REPRINT_RECEIPT;

    /**
     * Optional Access Function title
     */
    protected String functionTitle;

    /**
     * UI model retain in case of a device exception which can be used for a
     * retry.
     */
    protected POSBaseBeanModel model;

    /**
     * Constructs ReprintReceiptCargo class.
     */
    public ReprintReceiptCargo()
    {
        setDuplicateReceipt(true);
    }

    /**
     * Sets transaction identifier.
     *
     * @param value new transaction identifier
     */
    public void setTransactionID(String value)
    {
        transactionID = value;
    }

    /**
     * Returns transaction identifier.
     *
     * @return transaction identifier
     */
    public String getTransactionID()
    {
        return (transactionID);
    }

    /**
     * Sets register identifier.
     *
     * @param value new register identifier
     */
    public void setRegisterID(String value)
    {
        registerID = value;
    }

    /**
     * Returns register identifier.
     *
     * @return register identifier
     */
    public String getRegisterID()
    {
        return (registerID);
    }

    /**
     * Sets business date.
     *
     * @param value new business date
     */
    public void setBusinessDate(EYSDate value)
    {
        businessDate = value;
    }

    /**
     * Returns business date.
     *
     * @return business date
     */
    public EYSDate getBusinessDate()
    {
        return (businessDate);
    }

    /**
     * Sets identifier of last reprintable transaction.
     *
     * @param value new identifier of last reprintable transaction
     */
    public void setLastReprintableTransactionID(String value)
    {
        lastReprintableTransactionID = value;
    }

    /**
     * Returns identifier of last reprintable transaction.
     *
     * @return identifier of last reprintable transaction
     */
    public String getLastReprintableTransactionID()
    {
        return (lastReprintableTransactionID);
    }

    /**
     * Sets non-reprintable error identifier.
     *
     * @param value new non-reprintable error identifier
     */
    public void setNonReprintableErrorID(int value)
    {
        nonReprintableErrorID = value;
    }

    /**
     * Returns non-reprintable error identifier.
     *
     * @return non-reprintable error identifier
     */
    public int getNonReprintableErrorID()
    {
        return (nonReprintableErrorID);
    }

    /**
     * Returns error for non-reprintable error.
     *
     * @param value error code value
     * @return String descriptor of error code
     */
    public String nonReprintableErrorCodeToString(int value)
    {
        // if exception, use Unknown verbiage
        String errorCodeString = "";
        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);

        try
        {
            errorCodeString = utility.retrieveText("Error", BundleConstantsIfc.REPRINT_RECEIPT_BUNDLE_NAME,
                    NOT_REPRINTABLE_ERROR_DESCRIPTOR_TAGS[value], NOT_REPRINTABLE_ERROR_DESCRIPTORS[value]);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            // display real value
            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            Object errorCodeStringParms[] = { LocaleUtilities.formatNumber(value, locale) };
            String errorCodeStringPattern = utility.retrieveText("Error",
                    BundleConstantsIfc.REPRINT_RECEIPT_BUNDLE_NAME, UNKNOWN_ERROR_TAG, UNKNOWN_ERROR_TEXT);
            errorCodeString = LocaleUtilities.formatComplexMessage(errorCodeStringPattern, errorCodeStringParms);
        }
        return (errorCodeString);
    }

    /**
     * Returns error for non-reprintable error.
     *
     * @return String descriptor of error code
     */
    public String nonReprintableErrorCodeToString()
    {
        return (nonReprintableErrorCodeToString(nonReprintableErrorID));
    }

    /**
     * Returns the data exception error code.
     *
     * @return The data exception error code.
     */
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    /**
     * Sets the data exception error code.
     *
     * @param value The data exception error code.
     */
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.commerceservices.logging.MappableContextIfc#getContextValue()
     */
    @Override
    public Object getContextValue()
    {
        if (!Util.isEmpty(registerID))
        {
            StringBuilder builder = new StringBuilder("Register[id=");
            builder.append(registerID);
            builder.append("]");
            return builder.toString();
        }
        if (accessEmployee != null)
        {
            StringBuilder builder = new StringBuilder("Employee[id=");
            builder.append(accessEmployee.getLoginID());
            builder.append("]");
            return builder.toString();
        }
        return getClass().getSimpleName() + "@" + hashCode();
    }

    /**
     * Returns the returns the employee requesting access to the current
     * function.
     *
     * @return an EmployeeIfc Object
     */
    public EmployeeIfc getAccessEmployee()
    {
        return accessEmployee;
    }

    /**
     * Sets training-mode flag value.
     *
     * @param value new training-mode flag value
     */
    public void setTrainingModeFlag(boolean value)
    {
        trainingModeFlag = value;
    }

    /**
     * Returns training-mode flag value.
     *
     * @return training-mode flag value
     */
    public boolean getTrainingModeFlag()
    {
        return (trainingModeFlag);
    }

    /**
     * Returns training-mode flag value.
     *
     * @return training-mode flag value
     */
    public boolean isTrainingMode()
    {
        return (getTrainingModeFlag());
    }

    /**
     * Set the application ID.
     *
     * @param String string representing the application ID.
     */
    public void setAppID(String appID)
    {
        this.appID = appID;
    }

    /**
     * Get the application ID.
     *
     * @return String string representing the application ID.
     */
    public String getAppID()
    {
        return appID;
    }

    /**
     * The access employee returned by this cargo is the currently logged on
     * cashier or an Override Security Employee
     *
     * @param value The access employee
     */
    public void setAccessEmployee(EmployeeIfc value)
    {
        accessEmployee = value;
    }

    /**
     * Sets the current operator.
     *
     * @param value EmployeeIfc
     */
    public void setOperator(EmployeeIfc value)
    {
        setAccessEmployee(value);
    }

    /**
     * Returns the current operator.
     *
     * @return EmployeeIfc Object
     */
    public EmployeeIfc getOperator()
    {
        return getAccessEmployee();
    }

    /**
     * Returns the function ID whose access is to be checked.
     *
     * @return int function ID
     */
    public int getAccessFunctionID()
    {
        return this.accessFunctionID;
    }

    /**
     * Sets the function ID whose access is to be checked.
     *
     * @param value int
     */
    public void setAccessFunctionID(int value)
    {
        this.accessFunctionID = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc#getAccessFunctionTitle()
     */
    public String getAccessFunctionTitle()
    {
        // TODO Auto-generated method stub
        return functionTitle;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc#setAccessFunctionTitle(java.lang.String)
     */
    public void setAccessFunctionTitle(String title)
    {
        this.functionTitle = title;
    }

    /**
     * Returns the resource id for the Security Error Screen
     *
     * @return String the resource id for the Security error screen
     */
    public String getResourceID()
    {
        return "SecurityError";
    }

    /**
     * Sets the resource id for the Security Error Screen
     *
     * @param value String
     */
    public void setResourceID(String value)
    {
    }

    /**
     * Returns true if the transaction id to reprint was entered by the user,
     * false otherwise (last transaction selected)
     *
     * @return boolean flag indicating whether user entered transaction id to
     *         reprint
     */
    public boolean getTransactionIDEntered()
    {
        return transactionIDEntered;
    }

    /**
     * Sets the flag indicating whether user entered transaction id to reprint
     *
     * @param value boolean
     */
    public void setTransactionIDEntered(boolean value)
    {
        transactionIDEntered = value;
    }

    /**
     * If an override employee has been set, it will be returned. If not, the
     * current operator will be returned.
     *
     * @see oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc#getOverrideOperator()
     * @return The override operator
     */
    public EmployeeIfc getOverrideOperator()
    {
        return overrideOperator;
    }

    /**
     * Sets the override operator when a successful override has occurred.
     *
     * @see oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc#setOverrideOperator(oracle.retail.stores.domain.employee.EmployeeIfc)
     * @param value The override operator
     */
    public void setOverrideOperator(EmployeeIfc value)
    {
        overrideOperator = value;
    }

    /**
     * Get list of indexes of selected items.
     *
     * @return The list of indexes of selected items
     */
    public int[] getSelectedIndexes()
    {
        return selectedIndexes;
    }

    /**
     * Sets the list of selected indexes.
     *
     * @param values The list of selected indexes
     */
    public void setSelectedIndexes(int[] values)
    {
        selectedIndexes = values;
    }

    /**
     * Determines if the transaction is a completed sale, return or exchange
     * transaction.
     *
     * @param tenderableTransaction The transaction to test
     * @return boolean value of true if the transaction is a completed sale,
     *         return, or exchange transaction, false otherwise.
     */
    public boolean isCompletedSaleReturnExchange(TransactionIfc tenderableTransaction)
    {
        boolean returnValue = false;
        if (tenderableTransaction != null)
        {
            if (tenderableTransaction instanceof SaleReturnTransactionIfc)
            {
                int status = tenderableTransaction.getTransactionStatus();
                if (status == TransactionConstantsIfc.STATUS_COMPLETED)
                {
                    returnValue = true;
                }
            }
        }
        return returnValue;
    }

    /**
     * Returns the string representation of this object.
     *
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = Util.classToStringHeader("ReprintReceiptCargo", revisionNumber, hashCode()).toString();
        return (strResult);
    }

    /**
     * If a setSalesAssociate has been set, it will be returned. If not, the
     * current operator will be returned.
     *
     * @see oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc#getSalesAssociate()
     * @return The getSalesAssociate
     */
    public EmployeeIfc getSalesAssociate()
    {
        return salesAssociate;
    }

    /**
     * Sets the salesAssociate
     *
     * @see oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc#setSalesAssociate(oracle.retail.stores.domain.employee.EmployeeIfc)
     * @param value The setSalesAssociate
     */
    public void setSalesAssociate(EmployeeIfc value)
    {
        salesAssociate = value;
    }

    /**
     * this returns the model
     *
     * @return
     */
    public POSBaseBeanModel getModel()
    {
        return model;
    }

    /**
     * Save the model in case of a device exception which can be used for a
     * retry.
     *
     * @param model
     */
    public void setModel(POSBaseBeanModel model)
    {
        this.model = model;
    }

}