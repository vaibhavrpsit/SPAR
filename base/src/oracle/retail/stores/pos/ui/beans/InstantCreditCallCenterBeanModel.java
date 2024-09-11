/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/InstantCreditCallCenterBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:07 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:25 PM  Robert Pearse   
 *
 *Revision 1.4  2004/04/08 20:33:02  cdb
 *@scr 4206 Cleaned up class headers for logs and revisions.
 *
 *Revision 1.3  2004/03/16 17:15:17  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:26  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 21 2003 14:40:44   nrao
 * Changed copyright message.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// pos imports

//----------------------------------------------------------------------------
/**
    Data transport between the bean and the application for instant credit
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class InstantCreditCallCenterBeanModel extends POSBaseBeanModel
{
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected String serviceNumber;
    protected String referenceNumber;

    public String getServiceNumber()
    {
        return this.serviceNumber;
    }

    public void setServiceNumber(String serviceNumber)
    {
        this.serviceNumber = serviceNumber;
    }

    public String getReferenceNumber()
    {
        return this.referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber)
    {
        this.referenceNumber = referenceNumber;
    }

}
