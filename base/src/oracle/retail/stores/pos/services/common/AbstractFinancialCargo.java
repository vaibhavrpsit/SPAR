/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/AbstractFinancialCargo.java /main/20 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    hyin      10/03/12 - set itemFromWebStore when going through different
 *                         flow.
 *    cgreene   03/07/12 - added support for separating client logging into
 *                         separate contexts
 *    cgreene   03/01/12 - add system error map to user access cargo
 *    hyin      02/25/11 - add methods for enrolledNewFingerprint
 *    blarsen   02/23/11 - Added stubbed fingerprint methods that were added to
 *                         EmployeeCargoIfc. See comment on method for more
 *                         details.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    nkgautam  09/20/10 - refractored code to use a single class for checking
 *                         cash in drawer
 *    npoola    06/02/10 - removed the training mode increment id dependency
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mjwallac  03/19/09 - check in files for bug 826822 - enable MSR during
 *                         House Account.
 *    arathore  03/02/09 - Code Formatted.
 *    arathore  03/02/09 - Updated to display MSR Entry screen on CPOI device.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:26 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:19 PM  Robert Pearse
 *
 *   Revision 1.14  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.13  2004/09/15 16:34:22  kmcbride
 *   @scr 5881: Deprecating parameter retrieval logic in cargo classes and logging parameter exceptions
 *
 *   Revision 1.12  2004/07/23 22:17:25  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.11  2004/06/10 23:06:36  jriggins
 *   @scr 5018 Added logic to support replacing PriceAdjustmentLineItemIfc instances in the transaction which happens when shuttling to and from the pricing service
 *
 *   Revision 1.10  2004/06/07 14:58:49  jriggins
 *   @scr 5016 Added logic to persist previously entered transactions with price adjustments outside of the priceadjustment service so that a user cannot enter the same receipt multiple times in a transaction.
 *
 *   Revision 1.9  2004/04/13 17:19:32  crain
 *   @scr 4206 Updating Javadoc
 *
 *   Revision 1.8  2004/04/09 16:55:58  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.7  2004/03/19 07:16:09  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.6  2004/03/18 21:27:45  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.5  2004/03/14 21:12:41  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.4  2004/03/08 23:34:38  blj
 *   *** empty log message ***
 *
 *   Revision 1.3  2004/02/12 16:48:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   08 Nov 2003 01:00:04   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.1   Nov 06 2003 00:45:38   cdb
 * Updated to use AbstractFinancialCargoIfc.
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.0   Nov 04 2003 18:59:56   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import java.util.HashMap;
import java.util.Map;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;

import org.apache.log4j.Logger;

/**
 * This abstract class carries the financial data used through much of the
 * application. This consists of the storeStatus class and the Register class,
 * which also includes all the tills and financial totals.
 *
 * @version $Revision: /main/20 $
 */
public class AbstractFinancialCargo extends UserAccessCargo implements AbstractFinancialCargoIfc
{
    private static final long serialVersionUID = 8818196355580894569L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/20 $";

    /**
     * The financial data for the store
     */
    protected StoreStatusIfc storeStatus;

    /**
     * the financial data for the register
     */
    protected RegisterIfc register;

    /**
     * data exception error code
     */
    protected int dataExceptionErrorCode = DataException.NONE;

    /**
     * tender limits
     */
    protected TenderLimitsIfc tenderLimits = null;

    /**
     * The screen prompt text.
     */
    protected String operatorIdPromptText = null;

    /**
     * The screen name.
     */
    protected String operatorIdScreenName = null;

    /**
     * identifier of last reprintable transaction
     */
    protected String lastReprintableTransactionID = "";

    /**
     * Cash Drawer Under Warning Boolean
     */
    protected boolean cashDrawerUnderWarning;

    /**
     * Indicates, MSR is disabled or not
     */
    protected boolean msrDisabled;

    /**
     * Flag indicates if item is from webstore
     */
    protected boolean itemFromWebStore = false;
    
    /**
     * Customer info
     */
    protected CustomerInfoIfc customerInfo;

    /**
     * Manager for retrieving reason code text - this variable will be removed
     * when all calls to this.getCodeListMap() have been replaced with calls to
     * UtilityManager.getCodeListMap()
     */
    protected static UtilityManagerIfc utility = (UtilityManagerIfc) Gateway.getDispatcher().getManager(
            UtilityManagerIfc.TYPE);

    /** The current ADO transction */
    protected RetailTransactionADOIfc currentTransactionADO;

    /**
     * Mapping of original transactions in which a price adjustment was
     * performed. This data is used to keep previously entered price adjustment
     * line items from showing up in subsequent transactions.
     */
    protected Map<String, SaleReturnTransactionIfc> originalPriceAdjustmentTransactions;

    /**
     * Returns the store status.
     *
     * @return The store status.
     */
    public StoreStatusIfc getStoreStatus()
    {
        return storeStatus;
    }

    /**
     * Sets the store status.
     *
     * @param value The store status.
     */
    public void setStoreStatus(StoreStatusIfc value)
    {
        storeStatus = value;
    }

    /**
     * Returns the register object.
     *
     * @return The register object.
     */
    public RegisterIfc getRegister()
    {
        return register;
    }

    /**
     * Sets the register object.
     *
     * @param value The register object.
     */
    public void setRegister(RegisterIfc value)
    {
        register = value;
    }

    /**
     * Sets reference to tender limits interface.
     *
     * @param value reference to tender limits interface
     */
    public void setTenderLimits(TenderLimitsIfc value)
    {
        tenderLimits = value;
    }

    /**
     * Retrieves reference to tender limits interface.
     *
     * @return reference to tender limits interface
     */
    public TenderLimitsIfc getTenderLimits()
    {
        return (tenderLimits);
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

    /**
     * Creates new register object based on new business date, workstation.
     * <P>
     * Defaults are assumed for Till settings <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     *
     * @param ws Workstation object
     * @param bd business date
     * @return RegisterIfc object
     */
    static public RegisterIfc createRegister(WorkstationIfc ws, EYSDate bd)
    {
        RegisterIfc register = instantiateRegisterIfc();
        register.setWorkstation(ws);
        register.setStatus(AbstractFinancialEntityIfc.STATUS_CLOSED);
        register.setBusinessDate(bd);
        register.setOpenTime();
        register.setCloseTime();
        return (register);
    }

    /**
     * Instantiates register object.
     *
     * @return new workstation object
     */
    static public RegisterIfc instantiateRegisterIfc()
    {
        return (DomainGateway.getFactory().getRegisterInstance());
    }

    /**
     * Create the WorktationIfc from the workstation configuration setting.
     *
     * @param store store reference
     * @param logger Logger reference
     * @param isTrainingMode Is this workstation for training mode
     * @return the instantiated workstationIfc
     */
    static public WorkstationIfc createWorkstation(StoreIfc store, Logger logger, boolean isTrainingMode)
    {
        // Get the workstation id
        String workstationID = Gateway.getProperty("application", "WorkstationID", null);
        WorkstationIfc ws = null;

        if (workstationID != null && workstationID.length() > 0)
        {
            // initialize workstation based on workstation ID parameter
            ws = instantiateWorkstationIfc();
            // format workstation ID
            ws.setFormattedWorkstationID(workstationID);
            ws.setStore(store);
            ws.setTrainingMode(isTrainingMode);
        }

        return ws;
    }

    /**
     * Instantiates workstation object.
     *
     * @return new workstation object
     */
    static public WorkstationIfc instantiateWorkstationIfc()
    {
        return (DomainGateway.getFactory().getWorkstationInstance());
    }

    /**
     * Gets the prompt Enter ID prompt text for this service. Returning a null
     * will allow the default prompt text to be displayed.
     *
     * @return the prompt text.
     */
    public String getOperatorIdPromptText()
    {
        return null;
    }

    /**
     * Sets the prompt text for this service.
     *
     * @param value The prompt text.
     */
    public void setOperatorIdPromptText(String value)
    {
        operatorIdPromptText = value;
    }

    /**
     * Gets the screen name for this service. Returning a null will allow the
     * default screen name to be displayed.
     *
     * @return the screen name.
     */
    public String getOperatorIdScreenName()
    {
        return null;
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
     * Sets the screen name for this service.
     *
     * @param value The screen name.
     */
    public void setOperatorIdScreenName(String value)
    {
        operatorIdScreenName = value;
    }

    /**
     * Returns the customer info.
     *
     * @return The customer info
     */
    public CustomerInfoIfc getCustomerInfo()
    {
        return customerInfo;
    }

    /**
     * Sets the customer info.
     *
     * @param customerInfo CustomerInfoIfc the customer info
     */
    public void setCustomerInfo(CustomerInfoIfc customerInfo)
    {
        this.customerInfo = customerInfo;
    }

    /**
     * Create a SnapshotIfc which can subsequently be used to restore the cargo
     * to its current state.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The cargo is able to make a snapshot.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>A snapshot is returned which contains enough data to restore the
     * cargo to its current state.
     * </UL>
     *
     * @return an object which stores the current state of the cargo.
     * @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
     */
    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    /**
     * Reset the cargo data using the snapshot passed in.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The snapshot represents the state of the cargo, possibly relative to
     * the existing state of the cargo.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>The cargo state has been restored with the contents of the snapshot.
     * </UL>
     *
     * @param snapshot is the SnapshotIfc which contains the desired state of
     *            the cargo.
     * @exception ObjectRestoreException is thrown when the cargo cannot be
     *                restored with this snapshot
     */
    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.commerceservices.logging.MappableContextIfc#getContextValue()
     */
    @Override
    public Object getContextValue()
    {
        if (register != null)
        {
            return register.getContextValue();
        }
        return super.getContextValue();
    }

    /**
     * Returns a string representation of the attributes of this object. This
     * will be used by the class extended by this class for their
     * abstractToString() methods.
     *
     * @return String representation of attributes of this object
     */
    public String abstractToString()
    {
        // result string
        String strResult = new String();
        if (storeStatus == null)
        {
            strResult += "Store status:                           [null]\n";
        }
        else
        {
            strResult += "Store status\nSub" + storeStatus.toString() + "\n";
        }
        if (register == null)
        {
            strResult += "Register:                               [null]\n";
        }
        else
        {
            strResult += "Register:\nSub" + register.toString() + "\n";
        }
        if (tenderLimits == null)
        {
            strResult += "tenderLimits:                           [null]\n";
        }
        else
        {
            strResult += "Sub" + tenderLimits.toString() + "\n";
        }
        strResult += "Data exception error code:              [" + dataExceptionErrorCode + "]\n";
        if (operator == null)
        {
            strResult += "Operator:                               [null]\n";
        }
        else
        {
            strResult += "Operator:\nSub" + operator.toString() + "\n";
        }
        if (customerInfo == null)
        {
            strResult += "Customer Info:                               [null]\n";
        }
        else
        {
            strResult += "Customer Info:\nSub" + customerInfo.toString() + "\n";
        }
        // pass back result
        return (strResult);
    }

    /**
     * Retrieves the source-code-control system revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * @return
     */
    public RetailTransactionADOIfc getCurrentTransactionADO()
    {
        return currentTransactionADO;
    }

    /**
     * @param currentTransactionADO
     */
    public void setCurrentTransactionADO(RetailTransactionADOIfc currentTransactionADO)
    {
        this.currentTransactionADO = currentTransactionADO;
    }

    /**
     * Returns the originalPriceAdjustmentTransactions Map.
     *
     * @return The originalPriceAdjustmentTransactions Map.
     */
    public Map<String,SaleReturnTransactionIfc> getOriginalPriceAdjustmentTransactions()
    {
        return originalPriceAdjustmentTransactions;
    }

    /**
     * Sets the originalPriceAdjustmentTransactions Map
     *
     * @param originalPriceAdjustmentTransactions The
     *            originalPriceAdjustmentTransactions to set.
     */
    public void setOriginalPriceAdjustmentTransactions(Map<String,SaleReturnTransactionIfc> originalPriceAdjustmentTransactions)
    {
        if (originalPriceAdjustmentTransactions == null)
        {
            resetOriginalPriceAdjustmentTransactions();
        }

        this.originalPriceAdjustmentTransactions = originalPriceAdjustmentTransactions;
    }

    /**
     * Resets the originalPriceAdjustmentTransactions Map
     */
    public void resetOriginalPriceAdjustmentTransactions()
    {
        originalPriceAdjustmentTransactions = new HashMap<String,SaleReturnTransactionIfc>(1);
    }

    /**
     * Adds a new transaction to the originalPriceAdjustmentTransaction map.
     *
     * @param transaction the transaction to add
     */
    public void addOriginalPriceAdjustmentTransaction(SaleReturnTransactionIfc transaction)
    {
        if (originalPriceAdjustmentTransactions == null)
        {
            resetOriginalPriceAdjustmentTransactions();
        }

        originalPriceAdjustmentTransactions.put(transaction.getTransactionID(), transaction);
    }

    /**
     * Returns a SaleReturnTransactionIfc instance that matches the provided
     * transaction ID if it is available in the Map
     *
     * @return A SaleReturnTransactionIfc instance that matches the provided
     *         transaction ID if it is available in the Map or null if no match
     *         is available.
     */
    public SaleReturnTransactionIfc getOriginalPriceAdjustmentTransaction(String transactionID)
    {
        SaleReturnTransactionIfc originalPriceAdjustmentTransaction;

        if (originalPriceAdjustmentTransactions == null)
        {
            resetOriginalPriceAdjustmentTransactions();
        }

        originalPriceAdjustmentTransaction =originalPriceAdjustmentTransactions.get(transactionID);

        return originalPriceAdjustmentTransaction;
    }

    /**
     * Removes the original price adjustment transction from the map that
     * corresponds to the provided transaction ID
     *
     * @param transactionID Transaction ID to use as a key
     * @return A SaleReturnTransactionIfc which corresponds to the removed
     *         transaction or null if no match was found.
     */
    public SaleReturnTransactionIfc removeOriginalPriceAdjustmentTransaction(String transactionID)
    {
        SaleReturnTransactionIfc originalPriceAdjustmentTransaction;

        if (originalPriceAdjustmentTransactions == null)
        {
            resetOriginalPriceAdjustmentTransactions();
        }

        originalPriceAdjustmentTransaction = originalPriceAdjustmentTransactions.remove(transactionID);

        return originalPriceAdjustmentTransaction;

    }

    /**
     *
     * This fingerprint method is here because SaleCargoIfc extends EmployeeCargoIfc and
     * many classes extend SaleCargoIfc.
     *
     * EmployeeCargoIfc contains an employee's enrolled fingerprint data while it's
     * being verified.
     *
     * This fingerprint method is just a stub for all the non-EmployeeCargo classes which
     * ultimately implement EmployeeCargoIfc.
     *
     * @see oracle.retail.stores.domain.employee.EmployeeIfc#getFingerprintBiometrics()
     */
    public byte[] getFingerprintEnrollmentTemplate()
    {
        return null;
    }

    /**
     * @see AbstractFinancialCargo#getFingerprintEnrollmentTemplate()
     * @see oracle.retail.stores.domain.employee.EmployeeIfc#setFingerprintBiometrics(byte[])
     */
    public void setFingerprintEnrollmentTemplate(byte[] fingerprintEnrollmentTemplate)
    {
    }

    /**
     * Same reason like FingerprintEnrollmentTemplate
     * Returns enrolledNewFingerprint
     * @return the enrolledNewFingerprint
     */
    public boolean isEnrolledNewFingerprint()
    {
        return false;
    }

    /**
     * Same reason like FingerprintEnrollmentTemplate
     * Sets enrolledNewFingerprint
     * @param enrolledNewFingerprint the enrolledNewFingerprint to set
     */
    public void setEnrolledNewFingerprint(boolean enrolledNewFingerprint)
    {

    }


    /**
     * Returns msrDisabled
     *
     * @return msrDisabled
     */
    public boolean isMsrDisabled()
    {
        return msrDisabled;
    }

    /**
     * Sets msrDisabled.
     *
     * @param msrDisabled
     */
    public void setMsrDisabled(boolean msrDisabled)
    {
        this.msrDisabled = msrDisabled;
    }

    /**
     * Gets the cash drawer under warning boolean
     *
     * @return
     */
    public boolean isCashDrawerUnderWarning()
    {
        return cashDrawerUnderWarning;
    }

    /**
     * sets the Cash drawer under warning boolean
     *
     * @param cashDrawerUnderWarning
     */
    public void setCashDrawerUnderWarning(boolean cashDrawerUnderWarning)
    {
        this.cashDrawerUnderWarning = cashDrawerUnderWarning;
    }
    
    /**
     * return flag itemFromWebStore
     * @return itemFromWebStore
     */
    public boolean isItemFromWebStore() {
        return itemFromWebStore;
    }

    /**
     * set flag itemFromWebStore
     * @param itemFromWebStore
     */
    public void setItemFromWebStore(boolean itemFromWebStore) {
        this.itemFromWebStore = itemFromWebStore;
    }
    
}
