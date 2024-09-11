/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DecimalWithReasonBeanModel.java /main/14 2011/12/05 12:16:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   7    360Commerce 1.6         5/12/2006 5:25:33 PM   Charles D. Baker
 *        Merging with v1_0_0_53 of Returns Managament
 *   6    360Commerce 1.5         1/25/2006 4:10:57 PM   Brett J. Larsen merge
 *        7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *   5    360Commerce 1.4         1/22/2006 11:45:23 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   4    360Commerce 1.3         12/13/2005 4:42:44 PM  Barry A. Pape
 *        Base-lining of 7.1_LA
 *   3    360Commerce 1.2         3/31/2005 4:27:42 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:52 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:31 PM  Robert Pearse   
 *:
 *   4    .v700     1.2.1.0     9/19/2005 13:48:08     Jason L. DeLeau Make
 *        sure CurrencyTextFields can have a blank default value.
 *   3    360Commerce1.2         3/31/2005 15:27:42     Robert Pearse
 *   2    360Commerce1.1         3/10/2005 10:20:52     Robert Pearse
 *   1    360Commerce1.0         2/11/2005 12:10:31     Robert Pearse
 *
 *   4    360Commerce1.3         12/13/2005 4:42:44 PM  Barry A. Pape
 *: DecimalWithReasonBeanModel.java,v $
 *        Base-lining of 7.1_LA
 *   3    360Commerce1.2         3/31/2005 3:27:42 PM   Robert Pearse   
 *   2    360Commerce1.1         3/10/2005 10:20:52 AM  Robert Pearse   
 *   1    360Commerce1.0         2/11/2005 12:10:31 PM  Robert Pearse   
 *:
 *   5    .v710     1.2.2.1     10/24/2005 14:20:53    Charles Suehs   Merged
 *        from .v700 to fix CR 3965.
 *   4    .v710     1.2.2.0     10/20/2005 18:24:16    Charles Suehs   Merge
 *        from DecimalWithReasonBeanModel.java, Revision 1.2.1.0
 *   3    360Commerce1.2         3/31/2005 15:27:42     Robert Pearse
 *   2    360Commerce1.1         3/10/2005 10:20:52     Robert Pearse
 *   1    360Commerce1.0         2/11/2005 12:10:31     Robert Pearse
 *
 *  Revision 1.6  2004/03/22 19:27:00  cdb
 *  @scr 3588 Updating javadoc comments
 *
 *  Revision 1.5  2004/03/22 03:49:28  cdb
 *  @scr 3588 Code Review Updates
 *
 *  Revision 1.4  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 27 2004 14:00:24   cdb
 * Added Damaged flag to UI for damage discounts
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 * 
 *    Rev 1.0   Aug 29 2003 16:10:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:53:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   25 Apr 2002 18:52:22   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.math.BigDecimal;

//--------------------------------------------------------------------------
/**
    This bean model contains a BigDecimal and a reason code. It is used by the
    following beans:
    <UL>
    <LI>DiscAmountBean
    <LI>DiscPercentBean
    <LI>PriceOverrideBean
    <LI>TaxOverideBean
    </UL>
    @see oracle.retail.stores.pos.ui.beans.DiscAmountEntryBean
    @see oracle.retail.stores.pos.ui.beans.DiscPercentEntryBean
    @see oracle.retail.stores.pos.ui.beans.PriceOverrideEntryBean
    @see oracle.retail.stores.pos.ui.beans.TaxOverrideAmountBean
    @see oracle.retail.stores.pos.ui.beans.TaxOverrideRateBean
 */
//--------------------------------------------------------------------------
public class DecimalWithReasonBeanModel extends ReasonBeanModel
{
    //--------------------------------------------------------------------------
    /**
     *  Revision Number supplied by TeamConnection.
     */
    //--------------------------------------------------------------------------
    protected static final String revisionNumber = "$Revision: /main/14 $";
    //--------------------------------------------------------------------------
    /**
     *  The decimal value entered in Model.
     */
    //--------------------------------------------------------------------------
    //protected BigDecimal fieldValue = BigDecimal.ZERO;
    protected BigDecimal fieldValue = null;
    //--------------------------------------------------------------------------
    /**
     *  The boolean value entered in Model.
     */
    //--------------------------------------------------------------------------
    protected boolean booleanValue = false;

    //--------------------------------------------------------------------------
    /**
     *  Constructor
     */
    //--------------------------------------------------------------------------
    public DecimalWithReasonBeanModel()
    {
        super();
    }
    //--------------------------------------------------------------------------
    /**
     *  Gets the value property (BigDecimal) value.
     *  @return  BigDecimal
     */
    //--------------------------------------------------------------------------
    public BigDecimal getValue()
    {
        return fieldValue;
    }
    //--------------------------------------------------------------------------
    /**
     *  Sets the value property (BigDecimal) value.
     *  @param value BigDecimal
     */
     //--------------------------------------------------------------------------
    public void setValue(BigDecimal value)
    {
        fieldValue = value;
    }
}
