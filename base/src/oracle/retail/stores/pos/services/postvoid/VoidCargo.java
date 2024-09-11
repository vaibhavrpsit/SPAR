/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/VoidCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:50 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 10/24/08 - localization of post void reason codes
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         12/13/2005 4:42:34 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:30:46 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:26:46 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:15:33 PM  Robert Pearse
     $
     Revision 1.5  2004/09/27 22:32:04  bwf
     @scr 7244 Merged 2 versions of abstractfinancialcargo.

     Revision 1.4  2004/04/09 16:56:01  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.3  2004/02/12 16:48:15  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:28:20  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Nov 04 2003 11:16:10   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 23 2003 17:28:40   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 13:03:28   epd
 * Initial revision.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.postvoid;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeListIfc;

/**
 * The cargo needed for the Void service.
*/
public class VoidCargo extends AbstractFinancialCargo
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.postvoid.VoidCargo.class);
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       The transaction ID of the original transaction to be voided.
    **/
    protected String originalTransactionID;
    /**
       The reason for the void
       @deprecated as of 13.1.
    **/
    protected String reasonCode;

    /**
     * Localized Reason Code
     */
    protected LocalizedCodeIfc localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();

    /**
     * The Code List for CODE_LIST_POST_VOID_REASON_CODES
     */
    protected CodeListIfc localizedReasonCodes = null;


    /** Used for next of void tender. **/
    protected TenderADOIfc nextTender = null;

    /**
     * The original ADO transaction
     */
    protected RetailTransactionADOIfc originalTransactionADO;

    /**
     * @return
     */
    public RetailTransactionADOIfc getOriginalTransactionADO()
    {
        return originalTransactionADO;
    }

    /**
     * @param transactionADO
     */
    public void setOriginalTransactionADO(RetailTransactionADOIfc transactionADO)
    {
        this.originalTransactionADO = transactionADO;
    }

    /**
     * Returns the transaction ID of the original transaction to be voided.
     * <P>
     * @return the transaction ID of the original transaction to be voided.
    */
    public String getOriginalTransactionID()
    {
        return(originalTransactionID);
    }

    /**
     * Sets the transaction ID of the original transaction to be voided.
     * <P>
     * @param transactionID  The original transaction ID to be voided.
    **/
    public void setOriginalTransactionID(String transactionID)
    {
        originalTransactionID = transactionID;
    }

    /**
       Returns the reason for the void
       <P>
       @return the reason for the void
       @deprecated as of 13.1. Use {@link getLocalizedReasonCode()}
    */
    public String getReasonCode()
    {
        return(reasonCode);
    }

    /**
       Sets the reason for the void
       <P>
       @param code  The reason for the void
       @deprecated as of 13.1. Use{@link setLocalizedReasonCode(LocalizedCodeIfc)}
    **/
    public void setReasonCode(String code)
    {
        reasonCode = code;
    }

    /**
     * Gets the localized reason code
     * @return LocalizedCodeIfc
     */
    public LocalizedCodeIfc getLocalizedReasonCode()
    {
        return localizedReasonCode;
    }

    /**
     * Sets the localized reason code
     * @param LocalizedCodeIfc
     */
    public void setLocalizedReasonCode(LocalizedCodeIfc reasonCode)
    {
        localizedReasonCode = reasonCode;
    }

    /**
     * @return Returns the nextTender.
     */
    public TenderADOIfc getNextTender() {
        return nextTender;
    }


    /**
     * @param nextTender The nextTender to set.
     */
    public void setNextTender(TenderADOIfc nextTender) {
        this.nextTender = nextTender;
    }

    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    public String toString()
    {
        String strResult = new String("Class:  VoidCargo  @" + hashCode());
        return(strResult);
    }



    /**
     * @return the localizedReasonCodes
     */
    public CodeListIfc getLocalizedReasonCodes()
    {
        return localizedReasonCodes;
    }

    /**
     * @param localizedReasonCodes the localizedReasonCodes to set
     */
    public void setLocalizedReasonCodes(CodeListIfc localizedReasonCodes)
    {
        this.localizedReasonCodes = localizedReasonCodes;
    }

}
