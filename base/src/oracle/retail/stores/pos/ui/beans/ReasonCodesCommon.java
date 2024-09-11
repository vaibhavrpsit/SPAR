/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReasonCodesCommon.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:41 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


//------------------------------------------------------------------------------
/**
    This interface collects code used in multiple places in the reason code
    manager and reason code services.

    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//------------------------------------------------------------------------------

public interface ReasonCodesCommon
{
    /** This is the user visible string indicating that the scope of
        the reason codes will be the corporation. **/
    public static final String USER_CORPORATION = "Corporation";
    /** This is the user visible string indicating that the scope of
        the reason codes will be a single store. **/
    public static final String USER_STORE = "Store";

    /** This indicates that the scope of the reason codes will be the
        corporation. **/
    public static final String CORPORATION = "corporate";
    /** This indicates that the scope of the reason codes will be a
        single store. **/
    public static final String STORE = "store";

    /** These are all of the possible levels indicating the scope of
        the reason codes.  In a later release, the levels will be
        provided dynamically from the ParameterManagerAdmin.  **/
    public static final String[] LOCATION_LEVELS
    = {USER_CORPORATION, USER_STORE};

    /** These are all of the reason code groups.  In a later release, the
        groups will be provided dynamically from the
        ParameterManagerAdmin.  **/
    public static final String[] REASON_CODE_GROUPS = {"DiscountAmount",
                                                       "DiscountPercent",
                                                       "NoSale",
                                                       "ModifyTax",
                                                       "PriceOverride"};

    ///////////////////////////////////////////////////////////////////////////
    //                    Letter Names
    ///////////////////////////////////////////////////////////////////////////

    /** The name of the letter indicating that data has been stored in
        the cargo after the user has clicked "Accept."  **/
    public static final String ACCEPT_DATA = "AcceptData";

    /** The name of the letter indicating that the user need to
        confirm the deletion of the reason code.  **/
    public static final String CONFIRM_DELETE = "ConfirmDelete";

    /** The name of the letter indicating that the reason code could
        not be deleted because it was the only one in the group.  **/
    public static final String DELETE_FAILED = "DeleteFailed";

    /** The name of the letter indicating that data has been stored
        prior to a save.  **/
    public static final String DONE_STORING = "ReallyDone";

    /** The name of the letter indicating that data has been saved
        successfully by the reason code manager.  **/
    public static final String SAVE_SUCCEEDED = "SaveSucceeded";

    /** The name of the letter indicating that data has not been saved
        successfully by the reason code manager.  **/
    public static final String SAVE_FAILED = "SaveFailed";

    /** The name of the letter indicating that a new reason code
        should be created. **/
    public static final String SHOW_ADD_SCREEN = "ShowAddScreen";

    /** The name of the letter indicating that an existing reason code
        should be edited. **/
    public static final String SHOW_EDIT_SCREEN = "ShowEditScreen";

}
