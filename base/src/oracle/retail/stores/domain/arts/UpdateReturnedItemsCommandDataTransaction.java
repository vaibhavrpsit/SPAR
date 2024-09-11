/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/UpdateReturnedItemsCommandDataTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:26:35 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:15:25 PM  Robert Pearse   
 *
 Revision 1.1  2004/04/19 15:54:11  tmorris
 @scr 4332 -Class created to replace overloaded instantiation(new) of UpdateReturnedItemsDataTransaction objects with Factory call.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

//-------------------------------------------------------------------------
/**
 The DataTransaction to update returned items on original Command transactions.
 **/
//-------------------------------------------------------------------------

public class UpdateReturnedItemsCommandDataTransaction extends UpdateReturnedItemsDataTransaction
{
    
    public UpdateReturnedItemsCommandDataTransaction()
    { //begin UpdateReturnedItemsCommandDataTransaction()
        super(dataCommandName);
    } //end UpdateReturnedItemsCommandDataTransaction()

}
