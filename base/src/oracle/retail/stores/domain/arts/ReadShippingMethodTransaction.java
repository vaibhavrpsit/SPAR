/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ReadShippingMethodTransaction.java /main/16 2012/04/06 09:51:57 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    blarsen   07/15/11 - Fix misspelled word: retrival
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    acadar    05/28/10 - merge to tip
 *    acadar    05/28/10 - merged with tip
 *    acadar    05/26/10 - refactor shipping code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/14/10 - initial version for external order processing
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/7/2007 2:21:04 PM    Sandy Gu
 *         enhance shipping method retrieval and internal tax engine to handle
 *         tax rules
 *    4    360Commerce 1.3         4/25/2007 10:01:07 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:31 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:33 PM  Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:42:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:51:02   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:10:24   msg
 * Initial revision.
 *
 *    Rev 1.3   11 Jan 2002 16:31:40   sfl
 * Code cleanup based on good suggestions collected during
 * code review.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.2   04 Jan 2002 16:06:32   sfl
 * More comments clean up.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.1   03 Jan 2002 10:55:24   sfl
 * Clean up the comments.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.0   03 Dec 2001 18:12:06   sfl
 * Initial revision.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * The ReadShippingMethodTransaction implements the Shipping Method information
 * read operation. $Revision: /main/16 $
 **/
// -------------------------------------------------------------------------
public class ReadShippingMethodTransaction extends DataTransaction
{
    private static final long serialVersionUID = -5734923733985574661L;

    /**
     * The logger to which log messages will be sent.
     **/
    private static final Logger logger = Logger.getLogger(ReadShippingMethodTransaction.class);

    /**
     * revision number of this class
     **/
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * The name that links this transaction to a command within the DataScript.
     **/
    protected static String dataCommandName = "ReadShippingMethodTransaction";

    /**
     * Default constructor
     */
    public ReadShippingMethodTransaction()
    {
        super(dataCommandName);
    }



    /**
     * Reads the shipping method based on a ShippingMethodSearchCriteria. This
     * method does not need to be I18N.
     * 
     * @param ShippingMethodSearchCriteriaIfc
     * @return an array of ShippingMethods
     * @throws DataException
     */
    public ShippingMethodIfc[] readShippingMethod(ShippingMethodSearchCriteriaIfc criteria) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("ReadShippingMethodTransaction.readShippingMethod by search criteria");

        // set data actions and execute
        DataAction da = new DataAction();
        da.setDataOperationName("ReadShippingMethod");
        da.setDataObject(criteria);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = da;

        setDataActions(dataActions);
        ShippingMethodIfc[] retrievedShippingMethods = (ShippingMethodIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("ReadShippingMethodTransaction.readShippingMethod by search criteria");

        return (retrievedShippingMethods);
    }
    
    /**
     * Reads the shipping method based on a ShippingMethodSearchCriteria. This
     * method does not need to be I18N.
     * 
     * @param ShippingMethodSearchCriteriaIfc
     * @return an array of ShippingMethods
     * @throws DataException
     */
    public ShippingMethodIfc[] readStoreSendShippingMethod(ShippingMethodSearchCriteriaIfc criteria) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("ReadShippingMethodTransaction.readShippingMethod by search criteria");

        // set data actions and execute
        DataAction da = new DataAction();
        da.setDataOperationName("ReadShippingMethod");
        da.setDataObject(criteria);

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = da;

        setDataActions(dataActions);
        ShippingMethodIfc[] retrievedShippingMethods = (ShippingMethodIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("ReadShippingMethodTransaction.readShippingMethod by search criteria");

        return (retrievedShippingMethods);
    }    

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class: ReadShippingMethodTransaction");
        strResult.append(") @").append(hashCode());
        return (strResult.toString());
    }
}
