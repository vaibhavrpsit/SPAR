/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveTransactionIDsByBusinessDate.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:20 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:25    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:43     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:47     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:39  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:50  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Feb 15 2003 17:32:30   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntryIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation reads a list of transaction IDs and business dates from a
 * database.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcRetrieveTransactionIDsByBusinessDate extends JdbcRetrieveTransactionIDsByBatchID
{
    private static final long serialVersionUID = 5633122953315487852L;

    /**
     * Class constructor.
     */
    public JdbcRetrieveTransactionIDsByBusinessDate()
    {
        super();
        setName("JdbcRetrieveTransactionIDsByBusinessDate");
    }

    /**
     * Adds qualifiers for SQL statement to be used for selecting transaction
     * IDs.
     * 
     * @param sql SQLSelectStatement under construction
     * @param tLogEntry TLog entry object to be used as key
     */
    protected void addSelectTransactionIDsQualifiers(SQLSelectStatement sql, POSLogTransactionEntryIfc tLogEntry)
    {
        // add store ID qualifier, if necessary
        if (!Util.isEmpty(tLogEntry.getStoreID()))
        {
            sql.addQualifier(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID, inQuotes(tLogEntry.getStoreID()));
        }

        // add business date qualifier, if necessary
        if (tLogEntry.getBusinessDate() != null)
        {
            sql.addQualifier(ARTSDatabaseIfc.FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(tLogEntry.getBusinessDate()));
        }

    }
}
