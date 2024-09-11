/* ===========================================================================
* Copyright (c) 2006, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ReadTransactionsForReturn.java /main/14 2013/11/14 12:22:36 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  11/14/13 - configure the retry flag through the spring context
 *                         in persistenceContext.xml for the data transactions
 *    abondala  11/13/13 - when the pos server is rebooted, the first
 *                         webservice call never makes to the server becuase of
 *                         RMI lookup excetion. Solution is to retry the RMI
 *                         lookup.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         11/9/2006 6:42:35 PM   Jack G. Swan    
 *   $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

/**
 * @author jgs
 *
 */
public class ReadTransactionsForReturn extends TransactionReadDataTransaction
{

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public ReadTransactionsForReturn()
    {
        super();
        this.transactionName = "ReadTransactionsForReturn";
    }

    /**
     * @param name
     */
    public ReadTransactionsForReturn(String name)
    {
        super(name);
    }
}
