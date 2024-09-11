/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/PrintingLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:50:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Feb 12 2003 18:23:40   crain
 * Remove deprecated calls
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 * 
 *    Rev 1.0   Apr 29 2002 15:20:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 27 2002 17:27:28   mpm
 * Restructured end-of-transaction processing.
 * Resolution for POS SCR-1440: Enhance end-of-transaction processing for performance reasons
 *
 *    Rev 1.0   Sep 21 2001 11:21:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

// Foundation imports
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.services.printing.PrintingCargo;

//--------------------------------------------------------------------------
/**
   Shuttle used to transfer layaway payment related data.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class PrintingLaunchShuttle
extends FinancialCargoShuttle
implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7773953410665989009L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected LayawayCargo layawayCargo = null;

    //----------------------------------------------------------------------
    /**
        Loads the shuttle data from the parent service's cargo into this shuttle.
        The data elements to transfer are determined by the interfaces that
        the parent cargo implements.  For example, none of the layawaySearchCargo
        data elements will be transferred if the calling service's cargo implements
        layawaySummaryCargoIfc.

        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        layawayCargo = (LayawayCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Unloads the shuttle data into the cargo.

        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        cargo.setTransaction((TenderableTransactionIfc) layawayCargo.getTenderableTransaction());
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.  <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            new String("Class: "    + getClass().getName() +
                       "(Revision " + getRevisionNumber()  +
                       ") @" + hashCode());

        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

}
