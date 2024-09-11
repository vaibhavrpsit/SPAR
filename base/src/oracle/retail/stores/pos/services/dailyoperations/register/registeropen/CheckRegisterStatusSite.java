/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registeropen/CheckRegisterStatusSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         4/1/2008 2:30:37 PM    Deepti Sharma   CR
 *         31016 forward port from v12x -> trunk
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registeropen;
// foundation imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site makes sure the store is open before opening the register.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckRegisterStatusSite extends PosSiteActionAdapter
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final String STORE_STATUS_IS_STALE = "Stale";
    //----------------------------------------------------------------------
    /**
        Checks the status of register and the store. Mail letters that indciate
        if the register is open, close or if the store status is stale (uncertain).
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        AbstractFinancialCargo cargo = (AbstractFinancialCargo) bus.getCargo();
        StoreStatusIfc status = cargo.getStoreStatus();
        RegisterIfc register = cargo.getRegister();

        String letterName = null;
        if (register.getStatus() == AbstractStatusEntityIfc.STATUS_OPEN)
        {
            letterName = CommonLetterIfc.SUCCESS;
        }
        else
        {
            if (status.isStale())
            {
                letterName = STORE_STATUS_IS_STALE;
            }
            else
            {
                letterName = CommonLetterIfc.FAILURE;
            }
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  CheckOpenStoreSite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
