/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametervaluelisteditor/ListEditorCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:52 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:51  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:54   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   22 Jan 2002 13:52:54   KAC
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:11:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametervaluelisteditor;

import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;

//------------------------------------------------------------------------------
/**


    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class ListEditorCargo
extends oracle.retail.stores.pos.services.admin.listeditor.ListEditorCargo
{
    /** The reason code group (e.g., CheckIDTypes) currently selected
        for setting.  **/
    protected ReasonCodeGroupBeanModel reasonCodeGroup = null;

    /** The reason code currently being worked on.  **/
    protected ReasonCode reasonCode = null;

    /** The operation requested by the user.  Among other things, this
        will let us distinguish between adds and edits.  **/
    protected String operationRequested = null;

    /** This is the error message that will be displayed to the user.  **/
    protected String errorMessage = null;

    //--------------------------------------------------------------------------
    /**
       Class constructor. <P>
    */
    //--------------------------------------------------------------------------
    public ListEditorCargo()
    {
    }

    //--------------------------------------------------------------------------
    /**
       Gets the operation the user has requested. <P>

       @return String
    */
    //--------------------------------------------------------------------------
    public String getOperationRequested()
    {
        return operationRequested;
    }

    //--------------------------------------------------------------------------
    /**
       Gets the error message. <P>

       @return String
    */
    //--------------------------------------------------------------------------
    public String getErrorMessage()
    {
        return errorMessage;
    }

    //--------------------------------------------------------------------------
    /**
       Gets the reason code group bean model. <P>

       @return ReasonCodeGroupBeanModel
    */
    //--------------------------------------------------------------------------
    public ReasonCodeGroupBeanModel getReasonCodeGroup()
    {
        return reasonCodeGroup;
    }

    //--------------------------------------------------------------------------
    /**
       Gets the reason code model. <P>

       @return ReasonCode
    */
    //--------------------------------------------------------------------------
    public ReasonCode getReasonCode()
    {
        return reasonCode;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the operation the user has requested. <P>

       @param String
    */
    //--------------------------------------------------------------------------
    public void setOperationRequested(String value)
    {
        operationRequested = value;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the error message to display. <P>

       @param String
    */
    //--------------------------------------------------------------------------
    public void setErrorMessage(String value)
    {
        errorMessage = value;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the reason code group bean model. <P>

       @param ReasonCodeGroupBeanModel
    */
    //--------------------------------------------------------------------------
    public void setReasonCodeGroup(ReasonCodeGroupBeanModel value)
    {
        reasonCodeGroup = value;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the reason code model. <P>

       @param ReasonCode
    */
    //--------------------------------------------------------------------------
    public void setReasonCode(ReasonCode value)
    {
        reasonCode = value;
    }

    //--------------------------------------------------------------------------
    /**
       Create a SnapshotIfc which can subsequently be used to restore
       the cargo to its current state. <P>

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

    //--------------------------------------------------------------------------
    /**
       Return all fields to their initial values. <P>
       All fields are returned to their initial values (existing values
       are erased).
    **/
    //--------------------------------------------------------------------------
    public void reset()
    {
        if (logger.isInfoEnabled()) logger.info( "ListEditorCargo.reset");
        reasonCodeGroup = null;
        reasonCode = null;
        operationRequested = null;
        errorMessage = null;
    }

}
