/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/TenderTDOConstants.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:57 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:48:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Jan 05 2004 17:32:48   rsachdeva
 * Initial revision.
 * Resolution for POS SCR-3551: Tender using Canadian Cash/Canadian Travelers Check/Canadian Check
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;
/**
   This class defines the constants to be used for TDO's.
   <P>
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
public class TenderTDOConstants
{
    /**
       revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * This class need not ever be instantiated.
     */
    private TenderTDOConstants() {}
    /** 
        attributeMap constant for Bus
    **/
    public static final String BUS = "Bus";
    /** 
        attributeMap constant for Transaction
    **/
    public static final String TRANSACTION = "Transaction";
    /** 
        attributeMap constant for alternate currency instance
    **/
    public static final String ALTERNATE_CURRENCY = "AlternateCurrency";
}

