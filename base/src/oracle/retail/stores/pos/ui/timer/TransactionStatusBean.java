/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/timer/TransactionStatusBean.java /rgbustores_13.4x_generic_branch/2 2011/08/16 13:50:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/16/11 - implement timeout capability for admin menu
 *    abondala  01/03/10 - update header date
 *    cgreene   05/15/09 - corrected method accessor since there is already a
 *                         singleton instance
 *    mchellap  11/21/08 - Renamed TransactionStatus to TransactionStatusBean
 *    mchellap  11/21/08 - Bean Class to maintain transaction status
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.timer;

/**
 * Singleton class for maintaining transaction status
 * @deprecated as of 13.4. Use {@link TimeoutSettingsUtility} instead.
 */
public class TransactionStatusBean
{
    /**
     * The transaction status object
     */
    private static TransactionStatusBean transactionStatus = null;

    /**
     * Flag indicating a open transaction
     */
    private static boolean transactionON = false;

    /**
     * Private Constructor
     */
    private TransactionStatusBean()
    {

    }

    /**
     * Returns the transaction status object
     * @return TranactionStatus
     */
    public static TransactionStatusBean getTransactionStatusObject()
    {
        if (transactionStatus == null)
        {
            transactionStatus = new TransactionStatusBean();
        }
        return transactionStatus;
    }

    /**
     * This method returns true if a tranaction is open in the register
     * @return the hasOpenTransaction
     */
    public boolean isTransactionON()
    {
        return transactionON;
    }

    /**
     * Sets the transaction status
     * @param hasOpenTransaction the hasOpenTransaction to set
     */
    public void setOpenTransaction(boolean transactionON)
    {
        TransactionStatusBean.transactionON = transactionON;
    }
}
