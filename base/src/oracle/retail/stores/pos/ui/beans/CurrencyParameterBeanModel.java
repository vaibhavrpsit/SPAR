/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CurrencyParameterBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
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
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:34 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:31 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:17 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 21 2002 18:26:20   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// foundation imports
import oracle.retail.stores.foundation.manager.parameter.Parameter;

//----------------------------------------------------------------------------
/**
    This class packages a retail parameter and its fields that the
    user may change for currency.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class CurrencyParameterBeanModel extends DecimalParameterBeanModel
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
        Default constructor
    **/
    //---------------------------------------------------------------------
    public CurrencyParameterBeanModel()
    {
    }

    //---------------------------------------------------------------------
    /**
        Class constructor that uses the validation restrictions from the
        provided parameter. <p>
        @param param the Parameter and its associated validator
    **/
    //---------------------------------------------------------------------
    public CurrencyParameterBeanModel(Parameter param)
    {
        super(param);
    }
}
