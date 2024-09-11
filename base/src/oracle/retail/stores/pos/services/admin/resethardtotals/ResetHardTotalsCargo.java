/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/resethardtotals/ResetHardTotalsCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:24:44 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:13:44 PM  Robert Pearse   
 *
 *Revision 1.3  2004/02/12 16:48:54  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:36:47  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:38   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:44   msg
 * Initial revision.
 * 
 *    Rev 1.2   19 Nov 2001 15:12:46   pdd
 * Added security override.
 * Resolution for POS SCR-309: Convert to new Security Override design.
 * 
 *    Rev 1.1   16 Nov 2001 10:36:32   epd
 * deprecated/removed unused fields and methods
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:11:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.resethardtotals;

// Foundation imports
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;

//------------------------------------------------------------------------------
/**
    
    Contains the hard totals to be reset.
     
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ResetHardTotalsCargo extends UserAccessCargo
{
    /**    
        The register at which operations are being performed
    **/
    protected boolean manualReset = false;
    /**
       Store ID
       @deprecated No longer used.
    **/ 
    protected String storeID = null;
    /**
       Register ID
    @deprecated No longer used.
    **/ 
    protected String registerID = null;
    /**
       Cashier ID
    @deprecated No longer used.
    **/ 
    protected String cashierID = null;

    //----------------------------------------------------------------------
    /**
        Returns the register at which operations are being performed.
        <P>
        @param  store   The store where operations are being performed.
    **/
    //----------------------------------------------------------------------
    public boolean getManualReset()
    {
        return(manualReset);        
    }
    
    //----------------------------------------------------------------------
    /**
        Sets the register at which operations are to be performed.
        <P>
        @param  store   The store where operations are being performed.
    **/
    //----------------------------------------------------------------------
    public void setManualReset(boolean value)
    {
        manualReset = value;
    }
 
    //----------------------------------------------------------------------
    /**
        Sets the store ID
        
        @param id String store ID
        @deprecated No longer used.
    **/
    //----------------------------------------------------------------------
    public void setStoreID(String id)
    {
        storeID = id;
    }

    //----------------------------------------------------------------------
    /**
        Sets the register ID
        
        @param id String register ID
        @deprecated No longer used.
    **/
    //----------------------------------------------------------------------
    public void setRegisterID(String id)
    {
        registerID = id;
    }

    //----------------------------------------------------------------------
    /**
        Sets the cashier ID
        
        @param id String cashier ID
        @deprecated No longer used.
    **/
    //----------------------------------------------------------------------
    public void setCashierID(String id)
    {
        cashierID = id;
    }

    //----------------------------------------------------------------------
    /**
        Gets the store ID
        
        @return String store ID
        @deprecated No longer used.
    **/
    //----------------------------------------------------------------------
    public String getStoreID()
    {
        return storeID;
    }

    //----------------------------------------------------------------------
    /**
        Gets the register ID
        
        @return String register ID
        @deprecated No longer used.
    **/
    //----------------------------------------------------------------------
    public String getRegisterID()
    {
        return registerID;
    }

    //----------------------------------------------------------------------
    /**
        Gets the cashier ID
        
        @return String cashier ID
        @deprecated No longer used.
    **/
    //----------------------------------------------------------------------
    public String getCashierID()
    {
        return cashierID;
    }

    //----------------------------------------------------------------------
    /**
        Returns the appropriate function ID.
        @return int RoleFunctionIfc.RESET_HARD_TOTALS
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.RESET_HARD_TOTALS;
    }
    
    //--------------------------------------------------------------------------
    /**
        Create a SnapshotIfc which can subsequently be used to restore
            the cargo to its current state. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>The cargo is able to make a snapshot.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>A snapshot is returned which contains enough data to restore the 
            cargo to its current state.
        </UL>
        @return an object which stores the current state of the cargo.
        @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
    */
    //--------------------------------------------------------------------------
    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    
    //--------------------------------------------------------------------------
    /**
        Reset the cargo data using the snapshot passed in. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>The snapshot represents the state of the cargo, possibly relative
        to the existing state of the cargo.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>The cargo state has been restored with the contents of the snapshot.
        </UL>
        @param snapshot is the SnapshotIfc which contains the desired state 
            of the cargo.
        @exception ObjectRestoreException is thrown when the cargo cannot
            be restored with this snapshot 
    */
    //--------------------------------------------------------------------------

    public void restoreSnapshot(SnapshotIfc snapshot)
        throws ObjectRestoreException
    {
    }

}
