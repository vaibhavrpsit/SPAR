/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/journal/JournalConstants.java /main/12 2013/02/12 16:58:40 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  02/12/13 - Added journal constants for RoundingAdjustment and
 *                         ChangeGiven
 *    cgreene   05/26/10 - convert to oracle packaging
 *    crain     03/05/10 - Add BALANCE_DUE
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         12/13/2005 4:42:31 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:28:47 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:22:57 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:10 PM  Robert Pearse   
 *
 * Revision 1.5  2004/06/24 19:33:12  bwf
 * @scr 5743 Fixed journaling of void transactions.
 *
 * Revision 1.4  2004/04/27 20:50:13  epd
 * @scr 4513 Fixes for printing and journalling when forced cash change is present
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.0 Nov 04 2003 11:11:10 epd Initial revision.
 * 
 * Rev 1.0 Oct 17 2003 12:31:18 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.journal;

/**
 * Keys for the journal memento Maps
 */
public class JournalConstants
{
    // Journal memento keys for tenders
    public static final String DESCRIPTOR = "Descriptor";
    public static final String TOTAL_TENDER = "TotalTender";
    public static final String BALANCE = "Balance";
    public static final String BALANCE_DUE = "BalanceDue";
    public static final String ROUNDING_ADJUSTMENT = "RoundingAdjustment";
    public static final String CHANGE_GIVEN = "ChangeGiven";
    public static final String CARD_TYPE = "CardType";
    public static final String ENTRY_METHOD = "EntryMethod";
    public static final String ISSUED_STORE_CREDITS = "IssuedStoreCredits";
    public static final String IRS_CUSTOMER = "IRSCustomer";

    // Journal memento keys for transaction
    public static final String VOID_REASON_CODE = "VoidReasonCode";
    public static final String ORIGINAL_TXN_ID = "OriginalTxnId";
    public static final String TRANSACTION_RDO_TYPE = "TxnRDOType";
    public static final String TOTAL_CHANGE = "TotalChange";

    // journal memento keys for operators
    public static final String OPERATOR = "Operator";
    public static final String OVERRIDE_OPERATOR = "OverrideOperator";

    // misc
    public static final String FUNCTION = "Function";
    public static final String BOOLEAN = "Boolean";
}
