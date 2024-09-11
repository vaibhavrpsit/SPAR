/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/delete/LayawayDeleteReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:50:48  mcs
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
 *    Rev 1.0   Aug 29 2003 16:00:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:21:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:34:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Jan 2002 16:35:14   jbp
 * modified for security access
 * Resolution for POS SCR-638: Manager Override not working for Layaway Delete
 *
 *    Rev 1.0   Sep 21 2001 11:21:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.delete;

// foundation imports
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
//--------------------------------------------------------------------------
/**
    Common shuttle used to transfer layaway related data between layaway services. <P>
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LayawayDeleteReturnShuttle
extends FinancialCargoShuttle
implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 56773383321704806L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        layaway cargo reference
    **/
    protected LayawayDeleteCargo layawayDeleteCargo = null;

    //----------------------------------------------------------------------
    /**
       Loads the cargo data in to the shuttle.
       If used as both a launch and return shuttle, this cargo will reset the
       references in the calling service to refer to the same objects they
       originally transferred.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);

        layawayDeleteCargo = (LayawayDeleteCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Unloads the shuttle data into the cargo.
       If used as both a launch and return shuttle, this cargo will reset the
       references in the calling service to refer to the same objects they
       originally transferred.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);

        LayawayCargo cargo = (LayawayCargo)bus.getCargo();

        cargo.setLayawaySearchID(layawayDeleteCargo.getLayawaySearchID());

        cargo.setSaleTransaction(layawayDeleteCargo.getSaleTransaction());

        cargo.setCustomer(layawayDeleteCargo.getCustomer());

        cargo.setSalesAssociate(layawayDeleteCargo.getSalesAssociate());

        cargo.setTenderableTransaction(layawayDeleteCargo.getTenderableTransaction());
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
