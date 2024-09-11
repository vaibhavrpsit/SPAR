/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/employeediscount/ApplyDiscountPercentSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:30 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/03/22 03:49:28  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.7  2004/03/19 21:48:41  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review modifications. Further abstractions.
 *
 *   Revision 1.6  2004/03/17 23:03:11  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review modifications.
 *
 *   Revision 1.5  2004/03/04 22:01:04  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Clear markdown items.
 *
 *   Revision 1.4  2004/02/26 18:26:20  cdb
 *   @scr 3588 Item Discounts no longer have the Damage
 *   selection. Use the Damage Discount flow to apply Damage
 *   Discounts.
 *
 *   Revision 1.3  2004/02/23 22:27:50  dcobb
 *   @scr 3588 Abstract common code  to abstract class.
 *
 *   Revision 1.2  2004/02/21 18:30:39  cdb
 *   @scr 3588 Updated prorating algorithm to actually prorate discounts.
 *
 *   Revision 1.1  2004/02/20 17:34:58  cdb
 *   @scr 3588 Removed "developmental" log entries from file header.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.employeediscount;

import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.pos.services.pricing.AbstractApplyDiscountPercentSite;

//--------------------------------------------------------------------------
/**
 *   Apply the previously validated discounts by percent.
 *   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class ApplyDiscountPercentSite extends AbstractApplyDiscountPercentSite
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";    

    //----------------------------------------------------------------------
    /**
     * Returns the discount basis.
     * @return DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE
     */
    //----------------------------------------------------------------------
    protected int getDiscountBasis()
    {
        return DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE;
    }

    //----------------------------------------------------------------------
    /**
     * Returns true if the discount is a damage discount.
     * @return false
     */
    //----------------------------------------------------------------------
    protected boolean isDamageDiscount()
    {
        return false;
    }
    
    //----------------------------------------------------------------------
    /**
     * Returns true if the discount is a markdown.
     * @return false
     */
    //----------------------------------------------------------------------
    protected boolean isMarkdown()
    {
        return false;
    }

}
