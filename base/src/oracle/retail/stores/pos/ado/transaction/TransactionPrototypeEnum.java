/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/TransactionPrototypeEnum.java /main/14 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    nkgautam  06/21/10 - added prototype enum for bill pay transaction
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    vapartha  06/01/09 - Added code to support invalid POST VOIDS
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:16 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/07/08 23:34:30  jdeleau
 *   @scr 6086 payroll till pay out on post void was crashing the system.  In fact it
 *   was not implemented at all.  Now its implemented just as normal till pay out.
 *
 *   Revision 1.4  2004/03/14 21:12:41  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.3  2004/03/11 20:03:28  blj
 *   @scr 3871 - added/updated shuttles to/from redeem, to/from tender, to/from completesale.
 *   also updated sites cargo for new redeem transaction.
 *
 *   Revision 1.2  2004/02/12 16:47:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.5   Feb 10 2004 15:16:12   blj
 * gift card refund
 * 
 *    Rev 1.4   Feb 05 2004 13:22:54   rhafernik
 * log4j conversion
 * 
 *    Rev 1.3   18 Dec 2003 08:01:04   Tim Fritz
 * Added a new proto type enum for the instant credit enrollment transaction type.
 * 
 *    Rev 1.2   Nov 20 2003 16:25:08   epd
 * added no sale transaction
 * 
 *    Rev 1.1   Nov 19 2003 16:21:10   epd
 * Refactoring updates
 * 
 *    Rev 1.0   Nov 04 2003 11:14:38   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 12:35:24   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import java.util.HashMap;
import java.util.Iterator;

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;

/**
 *
 * 
 */
public class TransactionPrototypeEnum implements TypesafeEnumIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5473389124295718474L;

    /** Constant for property group name */
    private static String APP_PROP_GROUP = "application";
    
    /** Constant for empty string */
    private static String EMPTY_STRING = "";
    
    /** The internal representation of an enumeration instance */
    private String enumer;
    
    /** The map containing the singleton enumeration instances */
    protected static final HashMap map = new HashMap(0);
    
    /////////////////////////////////////////////////////////////////////////
    // Enumeration values
    /////////////////////////////////////////////////////////////////////////
    
    // Sale Return transaction
    public static final TransactionPrototypeEnum SALE_RETURN = 
        new TransactionPrototypeEnum("SaleReturnTxnRDO",
                                     new int[] {TransactionIfc.TYPE_SALE,
                                                 TransactionIfc.TYPE_RETURN,
                                                 TransactionIfc.TYPE_EXCHANGE },
                                     "SaleReturnTxnADO");

    // Void transaction
    public static final TransactionPrototypeEnum VOID = 
        new TransactionPrototypeEnum("VoidTxnRDO",
                                     new int[] {TransactionIfc.TYPE_VOID},
                                     "VoidTxnADO");

    // Till Adjustment transaction
    public static final TransactionPrototypeEnum TILL_ADJUSTMENT = 
        new TransactionPrototypeEnum("TillAdjustmentTxnRDO",
                                     new int[] {TransactionIfc.TYPE_PAYIN_TILL,
                                                TransactionIfc.TYPE_PAYOUT_TILL,
                                                TransactionIfc.TYPE_PICKUP_TILL,
                                                TransactionIfc.TYPE_LOAN_TILL,
                                                TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL},
                                     "TillAdjustmentTxnADO");
                                     
    // Payment transaction
    public static final TransactionPrototypeEnum PAYMENT = 
        new TransactionPrototypeEnum("PaymentTxnRDO",
                                     new int[] {TransactionIfc.TYPE_HOUSE_PAYMENT,
                                                TransactionIfc.TYPE_LAYAWAY_PAYMENT},
                                     "PaymentTxnADO");

    // Layaway transaction
    public static final TransactionPrototypeEnum LAYAWAY = 
        new TransactionPrototypeEnum("LayawayTxnRDO",
                                     new int[] {TransactionIfc.TYPE_LAYAWAY_COMPLETE,
                                                TransactionIfc.TYPE_LAYAWAY_DELETE,
                                                TransactionIfc.TYPE_LAYAWAY_INITIATE},
                                     "LayawayTxnADO");
                                     
    // Order transaction
    public static final TransactionPrototypeEnum ORDER =
        new TransactionPrototypeEnum("OrderTxnRDO",
                                     new int[] {TransactionIfc.TYPE_ORDER_CANCEL,
                                                TransactionIfc.TYPE_ORDER_COMPLETE,
                                                TransactionIfc.TYPE_ORDER_INITIATE,
                                                TransactionIfc.TYPE_ORDER_PARTIAL},
                                     "OrderTxnADO");
                                     
    // Till Open Close transaction
    public static final TransactionPrototypeEnum TILL_OPEN_CLOSE =
        new TransactionPrototypeEnum("TillOpenCloseTxnRDO",
                                     new int[] {TransactionIfc.TYPE_OPEN_TILL,
                                                TransactionIfc.TYPE_CLOSE_TILL},
                                     "TillOpenCloseTxnADO");
                                     
    // Register Open Close transaction
    public static final TransactionPrototypeEnum REGISTER_OPEN_CLOSE =
        new TransactionPrototypeEnum("RegisterOpenCloseTxnRDO",
                                     new int[] {TransactionIfc.TYPE_OPEN_REGISTER,
                                                TransactionIfc.TYPE_CLOSE_REGISTER},
                                     "RegisterOpenCloseTxnADO");
                                     
    // Store Open Close transaction
    public static final TransactionPrototypeEnum STORE_OPEN_CLOSE =
        new TransactionPrototypeEnum("StoreOpenCloseTxnRDO",
                                     new int[] {TransactionIfc.TYPE_OPEN_STORE,
                                                TransactionIfc.TYPE_CLOSE_STORE},
                                     "StoreOpenCloseTxnADO");

    // No Sale transaction
    public static final TransactionPrototypeEnum NO_SALE =
        new TransactionPrototypeEnum("NoSaleTxnRDO",
                                     new int[] {TransactionIfc.TYPE_NO_SALE},
                                     "NoSaleTxnADO");

    // Instant Credit Enrollment transaction
    public static final TransactionPrototypeEnum INSTANT_CREDIT_ENROLLMENT =
        new TransactionPrototypeEnum("InstantCreditEnrollmentTxnRDO",
                                     new int[] {TransactionIfc.TYPE_INSTANT_CREDIT_ENROLLMENT},
                                     "InstantCreditEnrollmentTxnADO");
    
    // Enter Exit Training Mode transaction
    public static final TransactionPrototypeEnum ENTER_EXIT_TRAINING_MODE =
        new TransactionPrototypeEnum("EnterExitTrainingModeTxnRDO",
                                     new int[] {TransactionIfc.TYPE_ENTER_TRAINING_MODE,
                                                TransactionIfc.TYPE_EXIT_TRAINING_MODE},
                                     "EnterExitTrainingModeTxnADO");
                                     
    //  Instant Credit Enrollment transaction
    public static final TransactionPrototypeEnum UNKNOWN =
        new TransactionPrototypeEnum("NoVoidTxnRDO",
                                     new int[] {TransactionIfc.TYPE_UNKNOWN},
                                     "NoVoidADO");
    
    // Instant Credit Enrollment transaction
    public static final TransactionPrototypeEnum REDEEM =
        new TransactionPrototypeEnum("RedeemTxnRDO",
                                     new int[] {TransactionIfc.TYPE_REDEEM},
                                     "RedeemTxnADO");
    
    //Bill Pay Transaction ADO
    public static final TransactionPrototypeEnum BILL_PAY =
        new TransactionPrototypeEnum("BillPayRDO",
                                     new int[] {TransactionIfc.TYPE_BILL_PAY},
                                     "BillPayTxnADO");

    /////////////////////////////////////////////////////////////////////////
    
    /** 
     * Array of integer constants representing types that represent
     * this transaction type
     * @see oracle.retail.stores.domain.transaction.TransactionConstantsIfc
     */ 
    protected final int[] transactionTypes;
    
    /** The prototype RDO transaction type key*/
    protected final String transactionTypeRDOPropertyKey;
    
    /** The prototype ADO transaction type key*/
    protected final String transactionTypeADOPropertyKey;
    
    /**
     * Constructor adds a couple arguments which are meant to be used as a mapping
     * between the RDO class name, associated RDO integer transaction types and
     * the ADO transaction type.   
     * @param rdoTypeKey The key to the RDO type
     * @param transactionTypes An array of transaction types that map to this RDO
     *                            transaction type.
     * @param adoTypeKey An key to the ADO type
     */
    protected TransactionPrototypeEnum(String rdoTypeKey, 
                                       int[] transactionTypes,
                                       String adoTypeKey)
    {
           this.transactionTypes = transactionTypes;

        this.transactionTypeRDOPropertyKey = rdoTypeKey;
           this.transactionTypeADOPropertyKey = adoTypeKey;
        
        this.enumer = Gateway.getProperty(APP_PROP_GROUP, rdoTypeKey, EMPTY_STRING);
        map.put(enumer, this);
    }
    
    /**
     * If all that is known is the RDO integer transaction type, then 
     * this method can be used to retrieve the proper enumeration instance,
     * which in turn can be used to return a new ADO transaction instance
     * @see getTransactionADOInstance()
     * @param transactionType The integer RDO transaction type
     * @return an enumeration instance representing the desired transaction
     */
    public static TransactionPrototypeEnum makeEnumFromTransactionType(int transactionType) 
    {
        Iterator iter = map.values().iterator();
        TransactionPrototypeEnum result = null;
        loop:
        while (iter.hasNext())
        {
            TransactionPrototypeEnum enumer = (TransactionPrototypeEnum)iter.next();
            for (int i=0; i<enumer.transactionTypes.length; i++)
            {
                if (transactionType == enumer.transactionTypes[i])
                {
                    result = enumer;
                    break loop;
                }
            }
        }
        
        if(result == null) 
        {
        	result = UNKNOWN;
        }
        return result;
    }
    
    
    /**
     * Returns a clone of the prototype transaction instance this
     * particular enumeration represents.
     * @return a new ADO transaction instance
     */
    public RetailTransactionADOIfc getTransactionADOInstance()
    throws ADOException
    {
        try
        {
            String className = Gateway.getProperty(APP_PROP_GROUP, 
                                                   transactionTypeADOPropertyKey, 
                                                   EMPTY_STRING);
            Class txnClass = Class.forName(className);
            return (RetailTransactionADOIfc)txnClass.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            throw new ADOException("Class not found for " + enumer, e);
        }
        catch (InstantiationException e)
        {
            throw new ADOException("Failed to Instantiate txn for " + enumer, e);
        }
        catch (IllegalAccessException e)
        {
            throw new ADOException("IllegalAccessException creating txn for " + enumer, e);
        }
        catch (NullPointerException e)
        {
            throw new ADOException("Failed to find class for " + enumer, e);
        }
        catch (Throwable eth)
        {
            throw new ADOException("Failed to create txn for " + enumer, eth);
        }
    }
    
    /** get internal representation */
    public String toString()
    {
        return enumer;
    }
    
    /** factory method.  May return null */
    public static TransactionPrototypeEnum makeEnumFromString(String enumer)
    {
        return (TransactionPrototypeEnum)map.get(enumer);
    }
    
    /** fix deserialization */
    public Object readResolve()
    throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }
    
}
