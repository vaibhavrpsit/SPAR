/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CancelLetterAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:52 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:46 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *    Rev 1.1   08 Nov 2003 01:04:36   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 18:59:56   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This aisle sends a Cancel Letter.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class CancelLetterAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -9192715392893172362L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Sends a Cancel letter.
     * 
     * @param bus Service Bus
     */
    public void traverse(BusIfc bus)
    {
        bus.mail(new Letter(CommonLetterIfc.CANCEL), BusIfc.CURRENT);
    }
}
