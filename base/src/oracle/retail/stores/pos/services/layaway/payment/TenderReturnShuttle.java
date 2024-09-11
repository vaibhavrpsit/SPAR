/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/TenderReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:56 PM  Robert Pearse   
 *
 *   Revision 1.4.2.1  2004/10/15 18:50:30  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.5  2004/10/12 20:03:59  bwf
 *   @scr 7318 Fixed layway delete.  Removed unecessary log to screens.
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
 *    Rev 1.3   Nov 04 2003 11:21:54   epd
 * Updates for repackaging
 * 
 *    Rev 1.2   Oct 23 2003 17:24:38   epd
 * Updated to use renamed ADO packages
 * 
 *    Rev 1.1   Oct 17 2003 12:57:50   epd
 * Updated for new ADO tender service
 * 
 *    Rev 1.0   Aug 29 2003 16:00:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:20:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   06 Mar 2002 16:29:42   baa
 * Replace get/setAccessEmployee with get/setOperator
 * Resolution for POS SCR-802: Security Access override for Reprint Receipt does not journal to requirements
 *
 *    Rev 1.0   Sep 21 2001 11:21:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

// foundation imports
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//--------------------------------------------------------------------------
/**
   Shuttle used to transfer layaway payment related data.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TenderReturnShuttle
extends FinancialCargoShuttle
implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1503327616201274386L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected TenderCargo tenderCargo = null;

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

        tenderCargo = (TenderCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Unloads the shuttle data into the cargo.
       If used as both a launch and return shuttle, this cargo will reset the
       references in the calling service to refer to the same objects they
       originally transferred.

        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                               // begin unload()
        super.unload(bus);

        LayawayCargo cargo = (LayawayCargo)bus.getCargo();

        cargo.setTenderableTransaction(
                (TenderableTransactionIfc)((ADO)tenderCargo.getCurrentTransactionADO()).toLegacy());

        cargo.setOperator(tenderCargo.getOperator());
        cargo.setRegister(tenderCargo.getRegister());
    }                                               // end unload()

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
