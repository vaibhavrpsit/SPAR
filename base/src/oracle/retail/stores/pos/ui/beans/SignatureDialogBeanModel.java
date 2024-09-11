/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SignatureDialogBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Point;

//----------------------------------------------------------------------------
/**
     Adds a signature field to the standard dialog bean model.
**/
//----------------------------------------------------------------------------
public class SignatureDialogBeanModel extends DialogBeanModel
{
    /**
        revision number supplied by version control.
    **/
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    /**
        The array of points that defines the signature.
    **/
    Point[]     signature   =   null;

    //----------------------------------------------------------------------------
    /**
        Default constructor.
    **/
    //----------------------------------------------------------------------------
    public SignatureDialogBeanModel()
    {
        super();
    }
    //----------------------------------------------------------------------------
    /**
        Constructor initializes fieldArgs with String[].
        @param String[] args - the arguments for the model
    **/
    //----------------------------------------------------------------------------
    public SignatureDialogBeanModel(String[] args)
    {
        super(args);
    }
    //----------------------------------------------------------------------------
    /**
        Constructor initializes signature field.
        @param Serializable - the signature data
    **/
    //----------------------------------------------------------------------------
    public SignatureDialogBeanModel(Point[] data)
    {
        signature = data;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the signature field
        @return the value of signature
    **/
    //----------------------------------------------------------------------------
    public Point[] getSignature()
    {
        return signature;
    }
    //----------------------------------------------------------------------------
    /**
        Set the value of the signature field
        @param Serializable value
    **/
    //----------------------------------------------------------------------------
    public void setSignature(Point[] value)
    {
        signature = value;
    }
    //----------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @returns string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        return(super.toString() + "Signature  [" + signature + "]\n");
    }
}
