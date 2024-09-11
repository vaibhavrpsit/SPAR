/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tilloptions/TillOptionsCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:06 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:27:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:30:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:19:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tilloptions;

// foudation imports
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.services.common.TillCargo;

//------------------------------------------------------------------------------
/**
    TillOptions cargo for holding the float count type, tillcloseregister and
    methods for updating and accessing these data members.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TillOptionsCargo extends TillCargo
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
       Float count type.
    **/
    protected int floatCountType = FinancialCountIfc.COUNT_TYPE_NONE;

    /**
        tillClose Register object clone in case user cancelled
    **/    
    protected RegisterIfc tillCloseRegisterClone = null;
    
    /**
        Selection Type
    **/    
    protected String selectionType = "";
    
    //----------------------------------------------------------------------
    /**
        Returns the Calling Service. <P>
        @return The String type.
    **/
    //----------------------------------------------------------------------
    public String getSelectionType()
    {
        return selectionType;
    }
    
    //----------------------------------------------------------------------
    /**
        Set the Calling Service. <P>
        @param  value The String.
    **/
    //----------------------------------------------------------------------
    public void setSelectionType(String str)
    {
       selectionType = str; 
    }

    //----------------------------------------------------------------------
    /**
        Returns the float count type. <P>
        @return The float count type.
    **/
    //----------------------------------------------------------------------
    public int getFloatCountType()
    {                                   // begin getFloatCountType()
        return floatCountType;
    }                                   // end getFloatCountType()

    //----------------------------------------------------------------------
    /**
        Sets the float count type. <P>
        @param  value  The float count type.
    **/
    //----------------------------------------------------------------------
    public void setFloatCountType(int value)
    {                                   // begin setFloatCountType()
        floatCountType = value;
    }                                   // end setFloatCountType()

    //----------------------------------------------------------------------
    /**
        Returns the tillclose Register. <P>
        @return The tillclose Register.
    **/
    //----------------------------------------------------------------------
    public RegisterIfc getTillCloseRegister()
    {
       return tillCloseRegisterClone;
    }
    
    //----------------------------------------------------------------------
    /**
        Set  the tillclose Register. <P>
        @param  value The tillclose Register.
    **/
    //----------------------------------------------------------------------
    public void setTillCloseRegister(RegisterIfc tillCloseReg)
    {
       tillCloseRegisterClone = tillCloseReg; 
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
