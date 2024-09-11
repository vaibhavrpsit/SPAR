/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/SpecialOrderShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:03 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:08 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:21 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:52:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:07:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:01:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:48:02   msg
 * Initial revision.
 * 
 *    Rev 1.2   Dec 06 2001 17:26:26   dfh
 * updates to prepare for security override, cleanup
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.1   Dec 04 2001 16:11:58   dfh
 * No change.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Dec 04 2001 14:48:24   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder;

// foundation imports
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    Common shuttle used to transfer special order related data between  
    special order services. <P>
    <P>       
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SpecialOrderShuttle
extends FinancialCargoShuttle
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        special order cargo reference
    **/
    protected SpecialOrderCargo specialOrderCargo = null;

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

        specialOrderCargo = (SpecialOrderCargo)bus.getCargo();
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

        SpecialOrderCargo cargo = (SpecialOrderCargo)bus.getCargo();

        cargo.setOperator(specialOrderCargo.getOperator());

        cargo.setOrderTransaction(specialOrderCargo.getOrderTransaction());

        cargo.setCustomer(specialOrderCargo.getCustomer());

        cargo.setSalesAssociate(specialOrderCargo.getSalesAssociate());
 
        cargo.setRegister(specialOrderCargo.getRegister());         
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
        return(Util.parseRevisionNumber(revisionNumber));
    }

}
