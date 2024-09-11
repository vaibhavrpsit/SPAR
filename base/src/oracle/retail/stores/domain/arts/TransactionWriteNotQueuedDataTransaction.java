/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/TransactionWriteNotQueuedDataTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:30:36 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:26:27 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:15:19 PM  Robert Pearse   
 *
 Revision 1.1  2004/04/17 19:49:35  tmorris
 @scr 4332 -Class created to replace overloaded instantiation(new) of TransactionWriteDataTransaction objects with Factory call.
 *
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

//-------------------------------------------------------------------------
/**
 The DataTransaction to perform persistent Non Queued operations on the POS Transaction object.
 @see oracle.retail.stores.domain.arts.TransactionWriteDataTransaction
 **/
//-------------------------------------------------------------------------

public class TransactionWriteNotQueuedDataTransaction extends TransactionWriteDataTransaction
{
    
    public TransactionWriteNotQueuedDataTransaction()
    { //begin TransactionWriteNotQueuedDataTransaction()
        super(notQueuedSaveName);
    } //end TransactionWriteNotQueuedDataTransaction()

}
