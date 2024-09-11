/* ===========================================================================
* Copyright (c) 2002, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/MallCertificateSelectedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:00 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:26 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:32 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:38  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Mar 28 2003 17:37:30   DCobb
 * Initial revision.
 * Resolution for POS SCR-1821: POS 6.0 Mall Gift Certificates
 *
 *    Rev 1.0   Nov 18 2002 12:40:46   DCobb
 * Initial revision.
 * Resolution for POS SCR-1821: POS 6.0 Mall Gift Certificates
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

// foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;


//--------------------------------------------------------------------------
/**
    Sets the currency selected.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class MallCertificateSelectedAisle extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Sets the currency selected.
       <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        cargo.setCurrentActivityOrCharge(DomainGateway.getFactory()
                                           .getTenderTypeMapInstance()
                                           .getDescriptor(TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE));

        String letterName = "CountSummary";

        if (!cargo.getSummaryFlag())
        {
            if (cargo.currentHasDenominations())
            {
                letterName = "CashDetail";
            }
            else
            {
                letterName = "CountDetail";
            }
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
}
