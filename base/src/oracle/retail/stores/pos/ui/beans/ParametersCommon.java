/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ParametersCommon.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:50 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.foundation.manager.ifc.parameter.SourceIfc;

//------------------------------------------------------------------------------
/**
    This interface collects code used in multiple places in the parameter
    manager and reason code services.

    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//------------------------------------------------------------------------------

public interface ParametersCommon
{
    /** This is the user visible string indicating that the scope of
        the parameters will be the corporation. **/
    public static final String USER_CORPORATION = "Corporation";
    /** This is the user visible string indicating that the scope of
        the parameters will be a single store. **/
    public static final String USER_STORE = "Store";

    /** This indicates that the scope of the parameters will be the
        corporation. **/
    public static final String CORPORATION = "corporate";
    /** This indicates that the scope of the parameters will be a
        single store. **/
    public static final String STORE = "store";

    /** These are all of the possible levels indicating the scope of
        the parameters.  In a later release, the levels will be
        provided dynamically from the ParameterManagerAdmin.  **/
    public static final String[] LOCATION_LEVELS
    = {USER_CORPORATION, USER_STORE};

    /** These are all of the parameter groups.  In a later release, the
        groups will be provided dynamically from the
        ParameterManagerAdmin.  **/
    public static final String[] PARAMETER_GROUPS =
    {
        SourceIfc.DEFAULT_SETTINGS_PACKAGE,
        "Customer",
        "Discount",
        "Electronic Journal",
        "Employee",
        "Item",
        "Tender",
        "Till"
    };

    /** This is the value displayed to the user that indicates that a
        Boolean valued parameter is true.  **/
    public static final String USER_TRUE = "Yes";
    /** This is the value displayed to the user that indicates that a
        Boolean valued parameter is false.  **/
    public static final String USER_FALSE = "No";


    ///////////////////////////////////////////////////////////////////////////
    //                    Letter Names
    ///////////////////////////////////////////////////////////////////////////

    /** The name of the letter indicating that data has been stored in
        the cargo after the user has clicked "Accept" and the data was valid.  **/
    public static final String ACCEPT_DATA = "AcceptData";

    /** The name of the letter indicating that data has been stored in
     the cargo after the user has clicked "Accept" but the data was invalid.  **/
    public static final String REJECT_DATA = "RejectData";

    /** The name of the letter indicating that data has been stored in
        the cargo after the user has clicked "Accept."  **/
    public static final String ACCEPT_LIST_DATA = "AcceptListData";

    /** The name of the letter indicating that data has been saved
        successfully by the parameter manager.  **/
    public static final String SAVE_SUCCEEDED = "SaveSucceeded";

    /** The name of the letter indicating that data has not been saved
        successfully by the parameter manager.  **/
    public static final String SAVE_FAILED = "SaveFailed";

}
