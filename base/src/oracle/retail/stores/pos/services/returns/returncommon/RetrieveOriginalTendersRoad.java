/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/RetrieveOriginalTendersRoad.java /main/12 2012/04/02 10:35:11 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    sgu       09/08/11 - add house account as a refund tender
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:50 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:52 PM  Robert Pearse
 *
 *   Revision 1.1.2.1  2004/10/22 22:08:46  bwf
 *   @scr 7486, 7488 Made sure to get refund tenders during retrieve by customer.
 *
 *   Revision 1.1  2004/02/17 20:40:28  baa
 *   @scr 3561 returns
 *
 *   Revision 1.3  2004/02/12 16:51:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:05:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:05:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:45:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Jan 19 2002 10:28:28   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:24:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

// foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This road captures the tenders used on the original transaction
    <p>
    @version $Revision: /main/12 $
    @deprecated 13.3 no longer used
**/
//--------------------------------------------------------------------------
public class RetrieveOriginalTendersRoad extends LaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    //----------------------------------------------------------------------
    /**
      Captures the tenders used on the original transaction
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        AbstractFindTransactionCargo cargo = (AbstractFindTransactionCargo)bus.getCargo();
        SaleReturnTransactionIfc retrievedTrans = cargo.getOriginalTransaction();

        ReturnTenderDataElementIfc[] tenderList = getOriginalTenders(retrievedTrans.getTenderLineItems());
        cargo.setOriginalTenders(tenderList);
    }

    //----------------------------------------------------------------------
    /**
     * Retrieve tenders from original transaction
     * @param tenderList
     * @return ReturnTenderDataElement[] list of tenders
     */
    //----------------------------------------------------------------------
    protected ReturnTenderDataElementIfc[] getOriginalTenders(TenderLineItemIfc[] tenderList)
    {
        ReturnTenderDataElementIfc [] tenders = new ReturnTenderDataElementIfc[tenderList.length];
        for (int i =0; i < tenderList.length; i++)
        {
            tenders[i]=DomainGateway.getFactory().getReturnTenderDataElementInstance();
            tenders[i].setTenderType(tenderList[i].getTypeCode());
            if (tenderList[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
            {
                tenders[i].setCardType(((TenderChargeIfc)tenderList[i]).getCardType());
            }
            tenders[i].setAccountNumber(new String(tenderList[i].getNumber()));
            tenders[i].setTenderAmount(tenderList[i].getAmountTender());
        }
        return tenders;
    }
 }
