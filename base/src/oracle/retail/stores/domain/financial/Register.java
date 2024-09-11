/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/Register.java /main/21 2014/07/09 13:10:48 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     08/20/14 - Added MMdd to string returned by getNextUniqueID() 
 *                         for a forward port for a problem of the same store
 *                         credit number being created and not persisted to 
 *                         the db.  This change affects order, layaway, and 
 *                         store credit.
 *    icole     06/26/14 - Forward port fix for handling the condition of two
 *                         registers opened with same till with one or both
 *                         offline at time of open.
 *    tzgarba   08/27/12 - Merge project Echo (MPOS) into trunk.
 *    icole     07/31/12 - To correct problems with store credits sometimes not
 *                         being usable for tender code is changed to use one
 *                         'uniqueID' for both normal and training modes which
 *                         is in line with prior changes for customer and
 *                         transaction sequence numbers.
 *    cgreene   03/22/12 - correct context value for register id
 *    cgreene   03/07/12 - added support for separating client logging into
 *                         separate contexts
 *    npoola    08/11/10 - Actual register is used for training mode instead of
 *                         OtherRegister.
 *    tzgarba   07/16/10 - Fixed transaction and customer sequence number
 *                         rollover
 *    npoola    06/02/10 - removed the training mode increment id dependency
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/01/10 - removed and updated deprecated methods in Register
 *                         class
 *    cgreene   02/01/10 - added method setSequenceNumbers
 *    abondala  01/03/10 - update header date
 *    mchellap  03/10/09 - Modified getHardTotalsData() to remove locale
 *                         specific formatting for float amount
 *    mahising  11/19/08 - Updated for review comments
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         7/9/2007 3:34:48 PM    Anda D. Cadar   I18N
 *         change for CR 27494
 *    7    360Commerce 1.6         4/25/2007 10:00:54 AM  Anda D. Cadar   I18N
 *         merge
 *    6    360Commerce 1.5         2/6/2007 11:12:59 AM   Anil Bondalapati
 *         Merge from Register.java, Revision 1.3.1.0
 *    5    360Commerce 1.4         12/26/2006 3:18:21 PM  Charles D. Baker CR
 *         23955 - Updated the sequence number for store credit which was
 *         being
 *         lost when the application is stopped. This number is stored in the
 *         register
 *         object which isn't refreshed with each transaction. Corrected some
 *         other
 *         minor issues which had the potential to cause problems.
 *    4    360Commerce 1.3         1/22/2006 11:41:30 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:38 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:38 PM  Robert Pearse
 *
 *   Revision 1.9  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.8  2004/08/19 14:59:31  lzhao
 *   @scr 6654: clean up.
 *
 *   Revision 1.7  2004/08/16 21:14:50  lzhao
 *   @scr 6654: remove the relationship for training mode sequence number from real training mode sequence number.
 *
 *   Revision 1.6  2004/08/11 19:58:58  cdb
 *   @scr 5963 added null pointer safety to register.
 *
 *   Revision 1.5  2004/07/23 22:17:23  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.4  2004/07/13 22:33:39  cdb
 *   @scr 5970 in Services Impact Tracker database - removed hardcoding of class names
 *   in all getHardTotalsData methods.
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:35:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.8   Jun 10 2003 11:50:36   jgs
 * Backout hardtotals deprecations and compression change due to performance consideration.
 *
 *    Rev 1.7   May 20 2003 07:42:36   jgs
 * Deprecated getHardTotalsData() and setHardTotalsData() methods.
 * Resolution for 2573: Modify Hardtotals compress to remove dependency on code modifications.
 *
 *    Rev 1.6   May 10 2003 15:16:44   mpm
 * Added support for configurable sequence-number rollover settings.
 *
 *    Rev 1.5   May 05 2003 14:36:46   RSachdeva
 * Till Id Comparison Check
 * Resolution for POS SCR-2271: POS is crashed at till reconcile in below case.
 *
 *    Rev 1.4   Feb 12 2003 18:56:36   DCobb
 * Added hasFloatingTill indicator w/ accessors and methods getOpenTillByCashierID(String), getSuspendedTillByCashierID(String), & getOpenOrSuspendedTillByCashierID(String).
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.3   Dec 20 2002 11:15:28   DCobb
 * Add floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.2   24 Jun 2002 11:48:38   jbp
 * merge from 5.1 SCR 1726
 * Resolution for POS SCR-1726: Void - Void of new special order gets stuck in the queue in DB2
 *
 *    Rev 1.1   13 Jun 2002 17:00:54   pdd
 * Fixed sequence number rollover.
 * Resolution for POS SCR-1728: Sequence number rollover does not work.
 *
 *    Rev 1.0   Jun 03 2002 16:52:32   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.common.utility.Util;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * This class represents the state of the register during POS operations.
 * <P>
 * Included as attributes in this class are the sign-on operator, a status, open
 * and close time, business day, sequence number of last transaction and a
 * accountability flag (see Till).
 * <P>
 * There is also two FinancialTotals classes (one for expected totals and one
 * for entered totals) associated with this class, as well as an array of tills.
 * 
 */
public class Register extends AbstractFinancialEntity implements RegisterIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 8946553529339582476L;

    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(Register.class);

    /**
     * sequence number of last transaction
     */
    protected int lastTransactionSequenceNumber = 0;

    /**
     * sequence number to use when in training mode
     * @deprecated as of 13.3 use {@link #lastTransactionSequenceNumber} instead
     */
    protected int trainingTransactionSequenceNumber = 0;

    /**
     * The other register If this is the real register, otherRegister contains
     * the training mode register and vice versa.
     */
    protected volatile transient RegisterIfc otherRegister = null;

    /**
     * identifier of current till
     */
    protected String currentTillID = "";

    /**
    * identifier of reconciling till
    */
   protected String closingTillID =null;

   /**
    * Indicates whether or not the till associated with the register is being closed. 
    */
   protected boolean tillClose = false;
   
    /**
     * workstation
     */
    protected WorkstationIfc workstation = null;

    /**
     * tills vector
     */
    protected Vector<TillIfc> tillsVector = new Vector<TillIfc>(3);

    /**
     * accountability flag
     * 
     * @see oracle.retail.stores.domain.financial.AbstractStatusEntityIfc
     */
    protected int accountability = AbstractStatusEntityIfc.ACCOUNTABILITY_REGISTER;

    /**
     * Register Setting: Till Float Amount
     */
    protected CurrencyIfc tillFloatAmount = null;

    /**
     * Register Setting: Till Count Till At Reconcile valid values: Summary,
     * Detail, No. Defaults to {@link FinancialCountIfc#COUNT_TYPE_SUMMARY} per requirements.
     */
    protected int tillCountTillAtReconcile = FinancialCountIfc.COUNT_TYPE_SUMMARY;

    /**
     * Register Setting: Till Count Float At Open valid values: Summary, Detail,
     * No. Defaults to {@link FinancialCountIfc#COUNT_TYPE_SUMMARY} per requirements.
     */
    protected int tillCountFloatAtOpen = FinancialCountIfc.COUNT_TYPE_SUMMARY;

    /**
     * Register Setting: Till Count Float At Reconcile valid values: Summary,
     * Detail, No. Defaults to {@link FinancialCountIfc#COUNT_TYPE_SUMMARY} per requirements.
     */
    protected int tillCountFloatAtReconcile = FinancialCountIfc.COUNT_TYPE_SUMMARY;

    /**
     * Register Setting: Till Count Cash Loan valid values: Summary, Detail, No.
     * Defaults to {@link FinancialCountIfc#COUNT_TYPE_SUMMARY} per requirements.
     */
    protected int tillCountCashLoan = FinancialCountIfc.COUNT_TYPE_SUMMARY;

    /**
     * Register Setting: Till Count Cash Pickup valid values: Summary, Detail,
     * No. Defaults to {@link FinancialCountIfc#COUNT_TYPE_SUMMARY} per requirements.
     */
    protected int tillCountCashPickup = FinancialCountIfc.COUNT_TYPE_SUMMARY;

    /**
     * Register Setting: Till Count Check Pickup valid values: Summary, Detail,
     * No. Defaults to {@link FinancialCountIfc#COUNT_TYPE_NONE} per requirements.
     */
    protected int tillCountCheckPickup = FinancialCountIfc.COUNT_TYPE_NONE;

    /**
     * Register Setting: Till Reconcile. Defaults to true per requirements.
     */
    protected boolean tillReconcile = true;

    /**
     * Floating till indicator - set to true when a till is suspended and
     * removed from the drawer
     */
    protected boolean floatingTillFlag = false;

    /**
     * drawer status flag
     * 
     * @deprecated Not sufficient. This information now encapsulated in
     *             DrawerIfc.
     */
    protected int drawerStatus = AbstractStatusEntityIfc.DRAWER_STATUS_UNOCCUPIED;

    /**
     * Array of register drawers. NOTE: Currently assumes one drawer but makes
     * it trivial to add drawers
     */
    protected Map<String,DrawerIfc> drawerMap = new HashMap<String,DrawerIfc>(1);

    /**
     * current used unique identifier 0 - 9, A - Z skipping A, E, I, O, U
     */
    protected String currentUniqueID = "0000";

    /**
     * previous used unique identifier 0 - 9, A - Z skipping A, E, I, O, U
     */
    protected String previousUniqueID = "0000";

    /**
     * training mode unique ID 0 - 9, A - Z skipping A, E, I, O, U
     */
    protected String trainingModeUniqueID = "0000";

    /**
     * skip-zero-at-rollover flag
     */
    protected boolean sequenceNumberSkipZeroFlag = DEFAULT_SEQUENCE_NUMBER_SKIP_ZERO_PROPERTY.booleanValue();

    /**
     * maximum transaction sequence number
     */
    protected int maximumTransactionSequenceNumber = DEFAULT_MAXIMUM_TRANSACTION_SEQUENCE_NUMBER;

    /**
     * maximum customer sequence number
     */
    protected int maximumCustomerSequenceNumber = DEFAULT_MAXIMUM_CUSTOMER_SEQUENCE_NUMBER;

    /**
     * last customer sequence number
     */
    protected int lastCustomerSequenceNumber = 0;

    /**
     * last training customer sequence number
     * @deprecated as of 13.3 use non-training mode sequence instead
     */
    protected int trainingCustomerSequenceNumber = 0;

    /**
     * Constructs Register object.
     */
    public Register()
    {
        setProperties();
    }

    /**
     * Sets properties gleaned from DomainGateway.
     */
    protected void setProperties()
    {
        String sequenceNumberSkipZeroProperty = DomainGateway.getProperty(SEQUENCE_NUMBER_SKIP_ZERO_PROPERTY_NAME,
                DEFAULT_SEQUENCE_NUMBER_SKIP_ZERO_PROPERTY.toString());
        String maximumTransactionSequenceNumber = DomainGateway.getProperty(
                MAXIMUM_TRANSACTION_SEQUENCE_NUMBER_PROPERTY, Integer
                        .toString(DEFAULT_MAXIMUM_TRANSACTION_SEQUENCE_NUMBER));

        String maximumCustomerSequenceNumber = DomainGateway.getProperty(MAXIMUM_CUSTOMER_SEQUENCE_NUMBER_PROPERTY,
                Integer.toString(DEFAULT_MAXIMUM_CUSTOMER_SEQUENCE_NUMBER));
        setMaximumCustomerSequenceNumber(Integer.parseInt(maximumCustomerSequenceNumber));

        setSequenceNumberSkipZeroFlag(Boolean.valueOf(sequenceNumberSkipZeroProperty).booleanValue());
        setMaximumTransactionSequenceNumber(Integer.parseInt(maximumTransactionSequenceNumber));
    }

    /**
     * Creates clone of this object.
     * 
     * @return Object clone of this object
     */
    public Object clone()
    {
        // instantiate new object
        Register c = new Register();

        // set clone attributes
        setCloneAttributes(c);

        // pass back Object
        return (c);
    }

    /**
     * Creates clone of this object except for otherRegister attribute.
     * 
     * @return Object clone of this object
     */
    protected Register cloneWithoutOtherRegister()
    {
        // instantiate new object
        Register c = new Register();

        // set clone attributes
        setCloneAttributes(c);

        // pass back Object
        return (c);
    }

    /**
     * Sets attributes in clone.
     * 
     * @param newClass new instance of class
     */
    protected void setCloneAttributes(Register newClass)
    {
        super.setCloneAttributes(newClass);
        if (trainingModeUniqueID != null)
        {
            newClass.trainingModeUniqueID = trainingModeUniqueID;
        }

        if (currentUniqueID != null)
        {
            newClass.setCurrentUniqueID(new String(currentUniqueID));
        }

        if (previousUniqueID != null)
        {
            newClass.setPreviousUniqueID(new String(previousUniqueID));
        }

        if (currentTillID != null)
        {
            newClass.setCurrentTillID(new String(currentTillID));
        }
        if (workstation != null)
        {
            newClass.setWorkstation((WorkstationIfc)workstation.clone());
        }
        newClass.setLastTransactionSequenceNumber(lastTransactionSequenceNumber);

        newClass.setLastCustomerSequenceNumber(lastCustomerSequenceNumber);

        // clone tills
        TillIfc[] t = getTills();
        if (t != null)
        {
            TillIfc[] tclone = new TillIfc[t.length];
            for (int i = 0; i < t.length; i++)
            {
                tclone[i] = (TillIfc)t[i].clone();
            }
            newClass.setTills(tclone);
        }

        DrawerIfc[] d = getDrawers();
        if (d != null)
        {
            for (int i = 0; i < d.length; i++)
            {
                DrawerIfc dclone = (DrawerIfc)d[i].clone();
                newClass.addDrawer(dclone);
            }
        }

        newClass.setAccountability(accountability);
        newClass.setTillCountCashLoan(tillCountCashLoan);
        newClass.setTillCountCashPickup(tillCountCashPickup);
        newClass.setTillCountCheckPickup(tillCountCheckPickup);
        newClass.setTillCountFloatAtReconcile(tillCountFloatAtReconcile);
        newClass.setTillCountFloatAtOpen(tillCountFloatAtOpen);
        newClass.setTillCountTillAtReconcile(tillCountTillAtReconcile);
        newClass.setTillFloatAmount(getTillFloatAmount());
        newClass.setTillReconcile(tillReconcile);
        newClass.setSequenceNumberSkipZeroFlag(getSequenceNumberSkipZeroFlag());
    }


    /**
     * Determine if two objects are identical.
     * 
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (obj instanceof Register)
        {
            Register c = (Register)obj; // downcast the input object

            // compare all the attributes of Register
            if (super.equals(obj)
                    && getLastTransactionSequenceNumber() == c.getLastTransactionSequenceNumber()
                    && Util.isObjectEqual(getCurrentUniqueID(), c.getCurrentUniqueID())
                    && Util.isObjectEqual(getPreviousUniqueID(), c.getPreviousUniqueID())
                    && getAccountability() == c.getAccountability()
                    && getTillCountCashLoan() == c.getTillCountCashLoan()
                    && getTillCountCashPickup() == c.getTillCountCashPickup()
                    && getTillCountCheckPickup() == c.getTillCountCheckPickup()
                    && getTillCountFloatAtReconcile() == c.getTillCountFloatAtReconcile()
                    && getTillCountFloatAtOpen() == c.getTillCountFloatAtOpen()
                    && getTillCountTillAtReconcile() == c.getTillCountTillAtReconcile()
                    && Util.isObjectEqual(getTillFloatAmount(), c.getTillFloatAmount())
                    && isTillReconcile() == c.isTillReconcile()
                    && Util.isObjectEqual(getCurrentTillID(), c.getCurrentTillID())
                    && Util.isObjectEqual(getWorkstation(), c.getWorkstation())
                    //&& Util.isObjectEqual(getOtherRegister(), c.getOtherRegister())
                    && Util.isObjectEqual(getLastStatusChangeTime(), c.getLastStatusChangeTime())
                    && Util.isObjectEqual(getTills(), c.getTills())
                    && getSequenceNumberSkipZeroFlag() == c.getSequenceNumberSkipZeroFlag()
                    && getMaximumTransactionSequenceNumber() == c.getMaximumTransactionSequenceNumber()
                    && getMaximumCustomerSequenceNumber() == c.getMaximumCustomerSequenceNumber())
            {
                isEqual = true; // set the return code to true
            }
            else
            {
                isEqual = false; // set the return code to false
            }
        }
        return (isEqual);
    }

    /**
     * Retrieves TillIfc object matching specified identifier. If no match is
     * found, null is returned.
     * 
     * @param tillID requested till identifier
     * @return TillIfc object if found; null if not found
     */
    public TillIfc getTillByID(String tillID)
    {
        TillIfc t = null;
        // loop through tills
        for (TillIfc listTill : tillsVector)
        {
            // if match found, set tillID and exit
            if (listTill.getTillID().equalsIgnoreCase(tillID))
            {
                t = listTill;
                break;
            }
        }
        return (t);
    }

    /**
     * Retrieves the Current TillIfc object
     * 
     * @return Current TillIfc object
     */
    public TillIfc getCurrentTill()
    {
        TillIfc t = null;

        // Check to ensure that whenever the current
        // tillID was set, there was a real till added
        // to the register.
        if (currentTillID != null)
        {
            t = getTillByID(currentTillID);
        }

        return (t);
    }

    /**
     * Adds till to array. If till matching ID already exists,
     * HardTotalsFormatException is thrown.
     * 
     * @param till TillIfc object to be added to list
     * @exception IllegalArgumentException if specified till already exists in
     *                array
     */
    public void addTill(TillIfc till) throws IllegalArgumentException
    {
        // confirm till doesn't already exist
        if (getTillByID(till.getTillID()) != null)
        {
            throw new IllegalArgumentException("Specified till already exists in array.");
        }
        // add element to vector, list
        tillsVector.add(till);
    }

    /**
     * Add sale or return transaction to totals object for register and current
     * till. If accountability set to CASHIER and specified till isn't found,
     * HardTotalsFormatException is thrown.
     * 
     * @param SaleReturnTransaction object to add
     */
    public FinancialTotalsIfc addSaleReturnTransaction(SaleReturnTransaction t)
    {
        return addTransaction(t);
    }

    /**
     * Add transaction to totals object for register and current till.
     * 
     * @param RetailTransactionIfc object to add
     */
    public FinancialTotalsIfc addTransaction(TenderableTransactionIfc t)
    {
        // get totals for transaction
        FinancialTotalsIfc totals = t.getFinancialTotals();

        // add to totals for the register
        getTotals().add(totals);

        // add to totals for the current till
        TillIfc currentTill = getTillByID(currentTillID);
        if (currentTill != null)
        {
            currentTill.getTotals().add(totals);
        }
        return (totals);
    }

    /**
     * Adds to number of no-sale transactions.
     * 
     * @param value increment number of no-sale transactions
     */
    public void addNumberNoSales(int value)
    {
        // add to register totals
        getTotals().addNumberNoSales(value);

        // add to totals for current till
        TillIfc currentTill = getTillByID(currentTillID);
        if (currentTill != null)
        {
            currentTill.getTotals().addNumberNoSales(value);
        }
    }

    /**
     * Adds to number of Void line items.
     * 
     * @param value increment number of Void line items
     */
    public void addUnitsLineVoids(BigDecimal value)
    {
        // add to register totals
        getTotals().addUnitsLineVoids(value);

        // add to current till totals
        TillIfc currentTill = getTillByID(currentTillID);
        if (currentTill != null)
        {
            currentTill.getTotals().addUnitsLineVoids(value);
        }
    }

    /**
     * Adds to amount of line voids (deleted lines).
     * 
     * @param value increment amount of line voids (deleted lines)
     */
    public void addAmountLineVoids(CurrencyIfc value)
    {
        // add to register totals
        getTotals().addAmountLineVoids(value);

        // add to current till totals
        TillIfc currentTill = getTillByID(currentTillID);
        if (currentTill != null)
        {
            currentTill.getTotals().addAmountLineVoids(value);
        }
    }

    /**
     * Adds to amount of cancelled transactions.
     * 
     * @param value increment amount of cancelled transactions
     */
    public void addAmountCancelledTransactions(CurrencyIfc value)
    {
        // add to register totals
        getTotals().addAmountCancelledTransactions(value);

        // add to current till totals
        TillIfc currentTill = getTillByID(currentTillID);
        if (currentTill != null)
        {
            currentTill.getTotals().addAmountCancelledTransactions(value);
        }
    }

    /**
     * Adds to number of cancelled transactions.
     * 
     * @param value increment number of cancelled transactions
     */
    public void addNumberCancelledTransactions(int value)
    {
        // add to register totals
        getTotals().addNumberCancelledTransactions(value);

        // add to current till totals
        TillIfc currentTill = getTillByID(currentTillID);
        if (currentTill != null)
        {
            currentTill.getTotals().addNumberCancelledTransactions(value);
        }
    }

    /**
     * Retrieves array of cashiers from tills.
     * 
     * @return array of cashiers (EmployeeIfc)
     */
    public EmployeeIfc[] getCashiers()
    {
        List<EmployeeIfc> cashiers = new ArrayList<EmployeeIfc>(tillsVector.size());
        EmployeeIfc[] arrayCashiers = null;
        for (TillIfc listTill : tillsVector)
        {
            arrayCashiers = listTill.getCashiers();
            for (int j = 0; j < arrayCashiers.length; j++)
            {
                cashiers.add(arrayCashiers[j]);
            }
        }
        // return array
        return cashiers.toArray(new EmployeeIfc[cashiers.size()]);
    }

    /**
     * Retrieves EmployeeIfc object matching specified identifier in any of the
     * register's tills. If no match is found, null is returned.
     * 
     * @param cashierID requested cashier identifier
     * @return EmployeeIfc object if found; null if not found
     */
    public EmployeeIfc getCashierByID(String cashierID)
    {
        EmployeeIfc listCashier[] = null;
        // loop through tills
        for (TillIfc t : tillsVector)
        {
            listCashier = t.getCashiers();
            // loop through till's cashiers
            for (int j = 0; j < listCashier.length; j++)
            {
                if (listCashier[j].getEmployeeID().equals(cashierID))
                {
                    return listCashier[j];
                }
            }
        }
        return null;
    }

    /**
     * Retrieves TillIfc object matching specified cashier identifier in any of
     * the register's tills. If no match is found, null is returned.
     * 
     * @param cashierID requested cashier identifier
     * @return TillIfc object if found; null if not found
     */
    public TillIfc getTillByCashierID(String cashierID)
    {
        EmployeeIfc listCashier[] = null;
        // loop through tills
        for (TillIfc t : tillsVector)
        {
            listCashier = t.getCashiers();
            // loop through till's cashiers
            for (int j = 0; j < listCashier.length; j++)
            {
                if (listCashier[j].getEmployeeID().equals(cashierID))
                {
                    // if match found, return till
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves TillIfc object matching specified cashier identifier in any of
     * the register's open or suspended tills. If no match is found, null is
     * returned.
     * 
     * @param cashierID requested cashier identifier
     * @return TillIfc object if found; null if not found
     */
    public TillIfc getOpenOrSuspendedTillByCashierID(String cashierID)
    {
        TillIfc returnTill = null;
        returnTill = getOpenTillByCashierID(cashierID);
        if (returnTill == null)
        {
            returnTill = getSuspendedTillByCashierID(cashierID);
        }
        return returnTill;
    }

    /**
     * Retrieves TillIfc object matching specified cashier identifier in any of
     * the register's open tills. If no match is found, null is returned.
     * 
     * @param cashierID requested cashier identifier
     * @return TillIfc object if found; null if not found
     */
    public TillIfc getOpenTillByCashierID(String cashierID)
    {
        EmployeeIfc listCashier[] = null;
        // loop through tills
        for (TillIfc t : tillsVector)
        {
            listCashier = t.getCashiers();
            // loop through till's cashiers
            for (int j = 0; j < listCashier.length; j++)
            {
                if (listCashier[j].getEmployeeID().equals(cashierID)
                        && t.getStatus() == AbstractStatusEntityIfc.STATUS_OPEN)
                {
                    // if match found, return till
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves TillIfc object matching specified cashier identifier in any of
     * the register's suspended tills. If no match is found, null is returned.
     * 
     * @param cashierID requested cashier identifier
     * @return TillIfc object if found; null if not found
     */
    public TillIfc getSuspendedTillByCashierID(String cashierID)
    {
        EmployeeIfc listCashier[] = null;
        // loop through tills
        for (TillIfc t : tillsVector)
        {
            listCashier = t.getCashiers();
            // loop through till's cashiers
            for (int j = 0; j < listCashier.length; j++)
            {
                if (listCashier[j].getEmployeeID().equals(cashierID)
                        && t.getStatus() == AbstractStatusEntityIfc.STATUS_SUSPENDED)
                {
                    // if match found, return till
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves sequence number of last transaction.
     * 
     * @return sequence number of last transaction
     */
    public int getLastTransactionSequenceNumber()
    {
        return lastTransactionSequenceNumber;
    }

    /**
     * Sets sequence number of last transaction.
     * 
     * @param value sequence number of last transaction
     */
    public void setLastTransactionSequenceNumber(int value)
    {
    	lastTransactionSequenceNumber = value;
    }

    /**
     * gets training mode sequence number of last transaction.
     * 
     * @return value sequence number of last transaction
     * @deprecated as of 13.3 use {@link #getLastTransactionSequenceNumber()} instead
     */
    public int getTrainingTransactionSequenceNumber()
    {
        return trainingTransactionSequenceNumber;
    }

    /**
     * Sets training sequence number of last transaction.
     * 
     * @param value sequence number of last transaction
     * @deprecated as of 13.3 use {@link #setLastTransactionSequenceNumber(int)} instead
     */
    public void setTrainingTransactionSequenceNumber(int value)
    {
        trainingTransactionSequenceNumber = value;
    }

    /**
     * Sets sequence number of last customer.
     * 
     * @param value sequence number of last customer
     */
    public void setLastCustomerSequenceNumber(int value)
    {
       lastCustomerSequenceNumber = value;
    }

    /**
     * Gets sequence number of last customer.
     * 
     * @param value sequence number of last customer
     */
    public int getLastCustomerSequenceNumber()
    {
      return	lastCustomerSequenceNumber;
    }

    /**
     * gets training mode sequence number of last customer.
     * 
     * @return value sequence number of last customer
     * @deprecated as of 13.3 use non-training mode sequence instead
     */
    public int getTrainingCustomerSequenceNumber()
    {
        return trainingCustomerSequenceNumber;
    }

    /**
     * Sets training sequence number of last customer.
     * 
     * @param value sequence number of last customer
     * @deprecated as of 13.3 use non-training mode sequence instead
     */
    public void setTrainingCustomerSequenceNumber(int value)
    {
        trainingCustomerSequenceNumber = value;
    }

    /**
     * There may be two register exist, real register and training mode
     * register. Current register can be real or training, the method is to get
     * the other register
     * @return value the other register
     * @deprecated in 13.3. Training mode register is removed and actual register is used for training mode instead. 
     */
    public RegisterIfc getOtherRegister()
    {
        return otherRegister;
    }

    /**
     * There may be two register exist, real register and training mode
     * register. Current register can be real or training, the method is to set
     * the other register
     * @param otherRegister the other register
     * @deprecated in 13.3. Training mode register is removed and actual register is used for training mode instead. 
     */
    public void setOtherRegister(RegisterIfc otherRegister)
    {
        this.otherRegister = otherRegister;
    }
    /**
     * Set the sequence numbers from the specified register object onto this
     * register.
     * 
     * @param sequenceNumbers the other register
     */
    public void setSequenceNumbers(RegisterIfc sequenceNumbers)
    {
        Register register = (Register)sequenceNumbers;
        lastCustomerSequenceNumber = register.lastCustomerSequenceNumber;
        lastTransactionSequenceNumber = register.lastTransactionSequenceNumber;
    }

    /**
     * Retrieves sequence number for next transaction, rolling from last
     * transaction sequence number. The last transaction sequence number is
     * incremented and checked to see if it is past the limit. If it is past the
     * limit, the next transaction sequence number is set to 0.
     * 
     * @return sequence number for next transaction
     */
    public int getNextTransactionSequenceNumber()
    {
        // confirm not past limit and roll
        if (lastTransactionSequenceNumber < maximumTransactionSequenceNumber)
        {
        	lastTransactionSequenceNumber = lastTransactionSequenceNumber + 1;
        }
        else
        {
            // Need to roll the sequence number over to zero
            lastTransactionSequenceNumber = 0;
            if (isSequenceNumberSkipZeroFlag()) // check for skip-zero-at-rollover possibility
            {
            	lastTransactionSequenceNumber++;
            }
        }

        return lastTransactionSequenceNumber;
    }

    /**
     * Retrieves sequence number for next customer, rolling from last customer
     * sequence number. The last customer sequence number is incremented and
     * checked to see if it is past the limit. If it is past the limit, the next
     * customer sequence number is set to 0.
     * 
     * @return sequence number for next customer
     */
    public int getNextCustomerSequenceNumber()
    {

        // confirm not past limit and roll
        if (lastCustomerSequenceNumber < maximumCustomerSequenceNumber)
        {
        	lastCustomerSequenceNumber = lastCustomerSequenceNumber + 1;
        }
        else
        {
            // Need to roll the sequence number over to zero
            lastCustomerSequenceNumber = 0;
            if (isSequenceNumberSkipZeroFlag()) // check for skip-zero-at-rollover possibility
            {
            	lastCustomerSequenceNumber++;
            }
        }

        return lastCustomerSequenceNumber;
    }

    /**
     * Retrieves current unique identifier.
     * 
     * @return current unique identifier
     */
    public String getCurrentUniqueID()
    {
        return this.currentUniqueID;
    }

    /**
     * Sets current unique identifier.
     * 
     * @param value current unique identifier
     */
    public void setCurrentUniqueID(String currentUniqueID)
    {
        this.currentUniqueID = currentUniqueID;
        setPreviousUniqueID(currentUniqueID);
    }

    /**
     * Retrieves previous unique identifier.
     * 
     * @return previous unique identifier
     */
    public String getPreviousUniqueID()
    {
        return this.previousUniqueID;
    }

    /**
     * Sets previous unique identifier.
     * 
     * @param value previous unique identifier
     */
    public void setPreviousUniqueID(String previousUniqueID)
    {
        this.previousUniqueID = previousUniqueID;
    }

    /**
     * Creates and returns a 18 character unique identifier, rolling from last
     * unique identifiers last 4 digits. The last unique identifier is
     * incremented and checked to see if it is past the limit
     * (RegisterIfc.MAXIMUM_UNIQUE_IDENTIFIER). If it is past the limit, the
     * next unique identifier is set to initialUniqueID. Sets the
     * currentUniqueID to the new unique value.
     * 
     * @return next unique identifier
     */
    public String getNextUniqueID()
    {
        String initialUniqueID = "0000";

        String returnString = null;

        char[] newCharArray = null;
        String activeUniqueID = "";
        if (getWorkstation().isTrainingMode())
        {
            newCharArray = trainingModeUniqueID.toCharArray();
            activeUniqueID = trainingModeUniqueID;
        }
        else
        {
        newCharArray = currentUniqueID.toCharArray();
        activeUniqueID = currentUniqueID;
        setPreviousUniqueID(currentUniqueID);
        }
        String newString = null; // updated unique identifier string

        char updatedChar = ' '; // char updated to next value
        char activeCharcter = ' '; // currently active character
        // confirm not past limit and roll
        if (activeUniqueID.compareTo(RegisterIfc.MAXIMUM_UNIQUE_IDENTIFIER) < 0)
        {
            for (int i = activeUniqueID.length() - 1; i >= 0; i--)
            {
                activeCharcter = activeUniqueID.charAt(i); // update char
                updatedChar = updateUniqueCharacter(activeCharcter); // assign updated char
                // reset the char in the array
                newCharArray[i] = updatedChar;
                if (updatedChar != '0') // not wrapped then stop
                {
                    i = -1;
                }
            }
            newString = new String(newCharArray);
        }
        else
        {
            newString = initialUniqueID; // wrapped
        }

        // reset the last unique identifier with new value
        // Only reset currentUniqueID if NOT in training mode
        if (!getWorkstation().isTrainingMode())
        {
        currentUniqueID = newString;
        }
        trainingModeUniqueID = newString;

        TransactionIDIfc transactionID = DomainGateway.getFactory().getTransactionIDInstance();
        transactionID.setStoreID(getWorkstation().getStoreID());

        returnString = new String(transactionID.getFormattedStoreID() + getWorkstation().getWorkstationID()
                + getBusinessDate().toFormattedString("yyMMdd") + newString);

        return (returnString);
    }

    /**
     * Increments and returns the updated character based upon the following: if
     * '9' then returns 'A', if 'Z' then returns '0' otherwise increments to the
     * ASCII character. Supports 0 - 9, A- Z.
     * 
     * @return next updated character
     */
    protected char updateUniqueCharacter(char letter)
    {
        char updatedChar = ' '; // return character

        switch (letter)
        {
            // skip A, E, I, O, U
            case '9': // if 9 then set to B
            {
                updatedChar = 'B';
                break;
            }
            case 'D': // if D then set to F
            {
                updatedChar = 'F';
                break;
            }
            case 'H': // if H then set to J
            {
                updatedChar = 'J';
                break;
            }
            case 'N': // if N then set to P
            {
                updatedChar = 'P';
                break;
            }
            case 'T': // if T then set to V
            {
                updatedChar = 'V';
                break;
            }
            case 'Z': // if Z then set to 0 - wrapped
            {
                updatedChar = '0';
                break;
            }
            default: // increment ASCII code to next char
            {
                updatedChar = (char)(letter + 1);
                break;
            }
        }

        return (updatedChar);
    }

    /**
     * Retrieves identifier of current till.
     * 
     * @return identifier of current till
     */
    public String getCurrentTillID()
    {
        return (currentTillID);
    }

    /**
     * Sets identifier of current till.
     * 
     * @param value identifier of current till
     */
    public void setCurrentTillID(String value)
    {
        currentTillID = value;
    }

    /**
     * Retrieves workstation.
     * 
     * @return workstation
     */
    public WorkstationIfc getWorkstation()
    {
        return (workstation);
    }

    /**
     * Sets workstation.
     * 
     * @param value workstation
     */
    public void setWorkstation(WorkstationIfc value)
    {
        workstation = value;
    }

    /**
     * Retrieves tills for this register, loading tills array <code>till</code>
     * from <code>tillVector</code>.
     * 
     * @return tills for this register
     */
    public TillIfc[] getTills()
    {
        return tillsVector.toArray(new TillIfc[tillsVector.size()]);
    }

    /**
     * Sets tills for this register, loading tills array <code>tills</code> into
     * <code>tillVector</code>.
     * 
     * @param value tills for this register
     */
    public void setTills(TillIfc[] value)
    {
        resetTills();
        for (int i = 0; i < value.length; i++)
        {
            tillsVector.add(value[i]);
        }
    }

    /**
     * @return the closingTillID
     */
    public String getClosingTillID()
    {
        return closingTillID;
    }

    /**
     * @param closingTillID the closingTillID to set
     */
    public void setClosingTillID(String closingTillID)
    {
        this.closingTillID = closingTillID;
    }

    /**
     * @return the tillClose
     */
    public boolean isTillClose()
    {
        return tillClose;
    }

    /**
     * @param tillClose the tillClose to set
     */
    public void setTillClose(boolean tillClose)
    {
        this.tillClose = tillClose;
    }

    /**
     * Resets tills for this register.
     */
    public void resetTills()
    {
        tillsVector = new Vector<TillIfc>(3);
    }

    /**
     * Returns floating till indicator.
     * 
     * @return true if there is a floating till associated with this register.
     */
    public boolean hasFloatingTill()
    {
        return floatingTillFlag;
    }

    /**
     * Sets floating till indicator.
     * 
     * @param floating till indicator
     */
    public void setFloatingTill(boolean value)
    {
        floatingTillFlag = value;
    }

    /**
     * Retrieves accountability flag.
     * 
     * @return accountability flag
     * @see AbstractFinancialEntityIfc
     */
    public int getAccountability()
    {
        return (accountability);
    }

    /**
     * Sets accountability flag.
     * 
     * @param value accountability flag
     * @see AbstractFinancialEntityIfc
     */
    public void setAccountability(int value)
    {
        accountability = value;
    }

    /**
     * Retrieves Till Float Amount
     * 
     * @return CurrencyIfc containing float amount
     */
    public CurrencyIfc getTillFloatAmount()
    {
        if (tillFloatAmount == null)
        {
            try
            {
                tillFloatAmount = DomainGateway.getBaseCurrencyInstance("100.00");
            }
            catch (Exception e)
            {

                logger.error("Currency Information is not available; error connecting to server/database");
            }
        }
        return tillFloatAmount;
    }

    /**
     * Sets Till Float Amount
     * 
     * @param tillFloatAmount CurrencyIfc containing till float amount setting
     */
    public void setTillFloatAmount(CurrencyIfc tillFloatAmount)
    {
        this.tillFloatAmount = tillFloatAmount;
    }

    /**
     * Retrieves Till Count Till At Reconcile
     * 
     * @return int containing setting
     */
    public int getTillCountTillAtReconcile()
    {
        return tillCountTillAtReconcile;
    }

    /**
     * Sets Till Count Till At Reconcile
     * 
     * @param tillCountTillAtReconcile int containing till setting
     */
    public void setTillCountTillAtReconcile(int tillCountTillAtReconcile)
    {
        this.tillCountTillAtReconcile = tillCountTillAtReconcile;
    }

    /**
     * Retrieves Till Count Float At Open
     * 
     * @return int containing setting
     */
    public int getTillCountFloatAtOpen()
    {
        return tillCountFloatAtOpen;
    }

    /**
     * Sets Till Count Float At Open
     * 
     * @param tillCountFloatAtOpen int containing till setting
     */
    public void setTillCountFloatAtOpen(int tillCountFloatAtOpen)
    {
        this.tillCountFloatAtOpen = tillCountFloatAtOpen;
    }

    /**
     * Retrieves Till Count Float At Reconcile
     * 
     * @return int containing setting
     */
    public int getTillCountFloatAtReconcile()
    {
        return tillCountFloatAtReconcile;
    }

    /**
     * Sets Till Count Float At Reconcile
     * 
     * @param tillCountFloatAtReconcile int containing till setting
     */
    public void setTillCountFloatAtReconcile(int tillCountFloatAtReconcile)
    {
        this.tillCountFloatAtReconcile = tillCountFloatAtReconcile;
    }

    /**
     * Retrieves Till Count Cash Loan
     * 
     * @return int containing setting
     */
    public int getTillCountCashLoan()
    {
        return tillCountCashLoan;
    }

    /**
     * Sets Till Count Cash Loan
     * 
     * @param tillCountCashLoan int containing till setting
     */
    public void setTillCountCashLoan(int tillCountCashLoan)
    {
        this.tillCountCashLoan = tillCountCashLoan;
    }

    /**
     * Retrieves Till Count Cash Pickup
     * 
     * @return int containing setting
     */
    public int getTillCountCashPickup()
    {
        return tillCountCashPickup;
    }

    /**
     * Sets Till Count Cash Pickup
     * 
     * @param tillCountCashPickup int containing till setting
     */
    public void setTillCountCashPickup(int tillCountCashPickup)
    {
        this.tillCountCashPickup = tillCountCashPickup;
    }

    /**
     * Retrieves Till Count Check Pickup
     * 
     * @return int containing setting
     */
    public int getTillCountCheckPickup()
    {
        return tillCountCheckPickup;
    }

    /**
     * Sets Till Count Check Pickup
     * 
     * @param tillCountCheckPickup int containing till setting
     */
    public void setTillCountCheckPickup(int tillCountCheckPickup)
    {
        this.tillCountCheckPickup = tillCountCheckPickup;
    }

    /**
     * Retrieves Till Reconcile setting
     * 
     * @return boolean containing setting
     */
    public boolean isTillReconcile()
    {
        return tillReconcile;
    }

    /**
     * Sets Till Reconcile setting
     * 
     * @param tillReconcile boolean containing till setting
     */
    public void setTillReconcile(boolean tillReconcile)
    {
        this.tillReconcile = tillReconcile;
    }

    /**
     * Sets transaction sequence number skip-zero-at-rollover flag.
     * 
     * @param value transaction sequence number skip-zero-at-rollover flag
     */
    public void setSequenceNumberSkipZeroFlag(boolean value)
    {
        sequenceNumberSkipZeroFlag = value;
    }

    /**
     * Returns transaction sequence number skip-zero-at-rollover flag.
     * 
     * @return true if zero should be skipped when transaction sequence number
     *         rolls over, false otherwise
     */
    public boolean getSequenceNumberSkipZeroFlag()
    {
        return (isSequenceNumberSkipZeroFlag());
    }

    /**
     * Returns transaction sequence number skip-zero-at-rollover flag.
     * 
     * @return true if zero should be skipped when transaction sequence number
     *         rolls over, false otherwise
     */
    public boolean isSequenceNumberSkipZeroFlag()
    {
        return (sequenceNumberSkipZeroFlag);
    }

    /**
     * Sets maximum transaction sequence number.
     * 
     * @param value maximum transaction sequence number
     */
    public void setMaximumTransactionSequenceNumber(int value)
    {
        maximumTransactionSequenceNumber = value;
    }

    /**
     * Returns maximum transaction sequence number.
     * 
     * @return maximum transaction sequence number
     */
    public int getMaximumTransactionSequenceNumber()
    {
        return (maximumTransactionSequenceNumber);
    }

    /**
     * Sets maximum customer sequence number.
     * 
     * @param value maximum customer sequence number
     */
    public void setMaximumCustomerSequenceNumber(int value)
    {
        maximumCustomerSequenceNumber = value;
    }

    /**
     * Returns maximum customer sequence number.
     * 
     * @return maximum customer sequence number
     */
    public int getMaximumCustomerSequenceNumber()
    {
        return (maximumCustomerSequenceNumber);
    }

    /**
     * returns a specific cash drawer object.
     * 
     * @param drawerID The ID of the desired drawer
     * @return a drawer object or null if drawer not found.
     */
    public DrawerIfc getDrawer(String drawerID)
    {
        return drawerMap.get(drawerID);
    }

    /**
     * returns all cash drawer objects. This is a convenience method for use in
     * the main package during application startup.
     * 
     * @return an array of cash drawers
     */
    public DrawerIfc[] getDrawers()
    {
        Collection<DrawerIfc> values = drawerMap.values();
        return values.toArray(new DrawerIfc[drawerMap.size()]);
    }

    /**
     * Adds a cash drawer object to the register.
     * 
     * @param drawer A drawer object. Can be a new empty instance or prefilled
     *            with data.
     */
    public void addDrawer(DrawerIfc drawer)
    {
        drawerMap.put(drawer.getDrawerID(), drawer);
    }

    /**
     * Sets the status information about a specific cash drawer. If the drawer
     * is unoccupied, set tillID = ""
     * 
     * @param drawerID The ID of the desired drawer
     * @param drawerStatus The new status of the cash drawer
     * @param occupyingTillID The ID of the occupying Till in the drawer.
     * @throws IllegalArgumentException
     */
    public void setDrawerStatus(String drawerID, int drawerStatus, String occupyingTillID)
    {
        if (!drawerMap.containsKey(drawerID))
        {
            throw new IllegalArgumentException("Could not find drawer with id: " + drawerID);
        }

        DrawerIfc drawer = drawerMap.get(drawerID);
        drawer.setDrawerStatus(drawerStatus, occupyingTillID);
    }

    /**
     * Returns accountability type determined by string.
     * 
     * @param str accountability string
     * @return accountability
     */
    public static int stringToAccountability(String str)
    {
        int acct = AbstractFinancialEntityIfc.ACCOUNTABILITY_INVALID;
        for (int i = 0; i < AbstractFinancialEntityIfc.ACCOUNTABILITY_DESCRIPTORS.length; i++)
        {
            if (str.equals(AbstractFinancialEntityIfc.ACCOUNTABILITY_DESCRIPTORS[i]))
            {

                // if match found, set count type and exit loop
                acct = i;
                break;
            }
        }

        return (acct);

    }

    /**
     * Returns accountability descriptor string, checking for invalid
     * accountability value.
     * 
     * @param value accountability code
     * @return String accountability descriptor
     * @see AbstractStatusEntityIfc#ACCOUNTABILITY_DESCRIPTORS
     */
    public String accountabilityToString()
    {
        String strResult;
        try
        {
            strResult = AbstractFinancialEntityIfc.ACCOUNTABILITY_DESCRIPTORS[accountability];
        }
        // if not valid value, say unknown
        catch (ArrayIndexOutOfBoundsException e)
        {
            strResult = "Unknown [" + accountability + "]";
        }
        return (strResult);
    }

    /**
     * This method converts hard totals information to a comma delimited String.
     * 
     * @return String
     */
    public void getHardTotalsData(HardTotalsBuilderIfc builder)
    {
        builder.appendStringObject(getClass().getName());
        super.getHardTotalsData(builder);
        builder.appendInt(lastTransactionSequenceNumber);
        builder.appendInt(lastCustomerSequenceNumber);
        builder.appendStringObject(currentUniqueID);
        builder.appendStringObject(currentTillID);
        builder.appendInt(accountability);
        CurrencyIfc floatAmt = getTillFloatAmount();
        if (floatAmt != null)
        {
            builder.appendStringObject(floatAmt.toString());
        }
        builder.appendInt(tillCountTillAtReconcile);
        builder.appendInt(tillCountFloatAtOpen);
        builder.appendInt(tillCountFloatAtReconcile);
        builder.appendInt(tillCountCashLoan);
        builder.appendInt(tillCountCashPickup);
        builder.appendInt(tillCountCheckPickup);
        builder.appendStringObject(Boolean.toString(getSequenceNumberSkipZeroFlag()));
        builder.appendInt(getMaximumTransactionSequenceNumber());

        if (workstation == null)
        {
            builder.appendStringObject("null");
        }
        else
        {
            workstation.getHardTotalsData(builder);
        }

        int len = 0;
        TillIfc[] tills = getTills();
        if (tills != null)
        {
            len = tills.length;
        }
        builder.appendInt(len);
        for (int i = 0; i < len; i++)
        {
            tills[i].getHardTotalsData(builder);
        }

        // set drawers
        len = drawerMap.size();
        builder.appendInt(len);
        for (DrawerIfc d : drawerMap.values())
        {
            d.getHardTotalsData(builder);
        }
    }

    /**
     * This method populates this object from a comma delimited string.
     * 
     * @param String String containing hard totals data.
     */
    public void setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException
    {
        super.setHardTotalsData(builder);

        lastTransactionSequenceNumber = builder.getIntField();
        lastCustomerSequenceNumber = builder.getIntField();
        currentUniqueID = builder.getStringObject();
        currentTillID = builder.getStringObject();
        accountability = builder.getIntField();
        tillFloatAmount = DomainGateway.getBaseCurrencyInstance(builder.getStringObject());
        tillCountTillAtReconcile = builder.getIntField();
        tillCountFloatAtOpen = builder.getIntField();
        tillCountFloatAtReconcile = builder.getIntField();
        tillCountCashLoan = builder.getIntField();
        tillCountCashPickup = builder.getIntField();
        tillCountCheckPickup = builder.getIntField();

        setSequenceNumberSkipZeroFlag(Boolean.valueOf(builder.getStringObject()));
        setMaximumTransactionSequenceNumber(builder.getIntField());

        // Get the workstation
        workstation = (WorkstationIfc)builder.getFieldAsClass();
        if (workstation != null)
        {
            workstation.setHardTotalsData(builder);
        }

        // Get the Till objects for the Phone vector
        int number = builder.getIntField();
        for (int i = 0; i < number; i++)
        {
            TillIfc till = (TillIfc)builder.getFieldAsClass();
            if (till != null)
            {
                till.setHardTotalsData(builder);
            }
            addTill(till);
        }

        // Get Drawer objects
        int numDrawers = builder.getIntField();
        for (int i = 0; i < numDrawers; i++)
        {
            DrawerIfc d = (DrawerIfc)builder.getFieldAsClass();
            if (d != null)
            {
                d.setHardTotalsData(builder);
            }
            addDrawer(d);
        }
    }

    /**
     * Removes till from array.
     * 
     * @param till ID String - ID of till to be removed.
     * @exception IllegalArgumentException if specified till do not exists in
     *                array
     */
    public void removeTill(String tillID) throws IllegalArgumentException
    {
        TillIfc till = getTillByID(tillID);

        if (till != null)
        {
            tillsVector.remove(till);

        }
        else
        {
            throw new IllegalArgumentException("Specified till does not exist in array.");
        }
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // build result string
        StringBuilder strResult = new StringBuilder("**** START REGISTER   **** START REGISTER   **** START REGISTER\n");
        strResult.append("Class:  Register ").append("@").append(hashCode()).append("\n");

        // Add Register specific attributes
        strResult.append("accountability:                         [").append(accountabilityToString()).append("]\n");
        strResult.append("lastTransactionSequenceNumber:          [").append(lastTransactionSequenceNumber).append("]\n");
        strResult.append("lastCustomerSequenceNumber:             [").append(lastCustomerSequenceNumber).append("]\n");
        strResult.append("currentUniqueID:                        [").append(currentUniqueID).append("]\n");
        strResult.append("currentTillID:                          [").append(currentTillID).append("]\n");
        if (workstation == null)
        {
            strResult.append("workstation:                            [null]\n");
        }
        else
        {
            strResult.append("Workstation:\nSub").append(workstation.toString()).append("\n");
        }
        if (otherRegister == null)
        {
            strResult.append("otherRegister:                            [null]\n");
        }
        else
        {
            strResult.append("otherRegister:\nSub").append(otherRegister.toString()).append("\n");
        }
        // add attributes to string
        strResult.append(attributesToString());

        // add the tills
        if (getTills() == null)
        {
            strResult.append("tills:                                  [null]\n");
        }
        else
        {
            TillIfc[] tills = getTills();
            if (tills.length == 0)
            {
                strResult.append("tills:                                  [null]\n");
            }
            for (int i = 0; i < tills.length; i++)
            {
                strResult.append("Till ").append((i + 1)).append("\n");
                strResult.append(tills[i].toString()).append("\n");
            }
        }
        strResult.append(Util.formatToStringEntry("sequence number skip zero flag", getSequenceNumberSkipZeroFlag())
                + Util.formatToStringEntry("maximum transaction sequence number", getMaximumTransactionSequenceNumber()));
        strResult.append("\n\n**** END REGISTER **** END REGISTER **** END REGISTER");

        // pass back result
        return strResult.toString();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.commerceservices.logging.MappableContextIfc#getContextValue()
     */
    @Override
    public Object getContextValue()
    {
        if (getWorkstation() != null)
        {
            return getWorkstation().getContextValue();
        }

        StringBuilder builder = new StringBuilder("Register[id=");
        builder.append(getCurrentTillID());
        builder.append("]");
        return builder.toString();
    }

    /**
     * Register main method.
     * 
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        Register c = new Register();
        // output toString()
        System.out.println(c.toString());
        try
        {
            // instantiate class
            HardTotalsStringBuilder builder = null;
            Serializable obj = null;
            RegisterIfc a2 = null;
            Register a1 = new Register();

            builder = new HardTotalsStringBuilder();
            a1.getHardTotalsData(builder);
            obj = builder.getHardTotalsOutput();
            builder.setHardTotalsInput(obj);
            a2 = (RegisterIfc)builder.getFieldAsClass();
            a2.setHardTotalsData(builder);

            if (a1.equals(a2))
            {
                System.out.println("Empty Registers are equal");
            }
            else
            {
                System.out.println("Empty Registers are NOT equal");
            }
        }
        catch (HardTotalsFormatException iae)
        {
            System.out.println("Register convertion failed:");
            iae.printStackTrace();
        }
    }
}
