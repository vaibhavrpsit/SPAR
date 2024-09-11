/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/PosSiteActionAdapter.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:34 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    07/29/10 - performance logging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   09/17/09 - added serialVersionUID
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:26 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:16 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:19 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/09 17:23:47  baa
 *   @scr 3561 Add bin range, check digit and bad swipe dialogs
 *
 *   Revision 1.3  2004/02/12 16:48:46  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:52:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 14:57:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:18:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:10:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.logging.PerformanceLevel;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;

/**
 * Site action adapter used by point-of-sale services.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class PosSiteActionAdapter extends SiteActionAdapter
{
    private static final long serialVersionUID = -9065325483751597814L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The logger to which log messages will be sent
     */
    protected static final Logger logger = Logger.getLogger(PosSiteActionAdapter.class);

    /**
     * Performance logger for measuring performance
     */
    protected static  final  Logger  perfLogger = Logger.getLogger(PerformanceLevel.PERFORMANCE_CAT);

}