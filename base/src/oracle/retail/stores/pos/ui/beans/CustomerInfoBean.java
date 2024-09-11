/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerInfoBean.java /main/79 2014/06/17 08:04:16 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/13/14 - Handle the case "items" is null.
 *    jswan     11/07/14 - Improve performance of retrieving item images.
 *    asinton   10/17/14 - Added scroll area for customer info so that
 *                         reducing the size of the application window still
 *                         allows the user to access customer info fields.
 *    asinton   09/09/14 - Added code to preserve selected tab when user
 *                         temporarily views another screen (ie. F1 help)
 *    asinton   06/16/14 - fix to allow other uses of CustomerInfoBean to work
 *    asinton   06/04/14 - made fixes to address disabled tabs and when tabbed
 *                         is is not shown.
 *    asinton   06/02/14 - Added support for extended customer data.
 *    yiqzhao   01/28/14 - Remove editable for JComboBox otherwise users can
 *                         enter something in dropdown objects.
 *    abhinavs  01/07/14 - Fix to save/update customer phone numbers correctly
 *                         and excluding update phonelist method call when
 *                         updateBean method is called
 *    abananan  10/07/13 - pricingGroupField width changed from 10 to 25 in
 *                         CustomerInfoBean and CustomerAddBean
 *    rgour     06/07/13 - marking a phone record as deleted if it is removed
 *    subrdey   06/03/13 - Change email field length to 60
 *    vbongu    03/01/13 - Change tax cert field lenght to 30
 *    abhineek  01/31/13 - Added preference field to CusotmerInfo screen
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    mchellap  12/13/12 - Hide receipt preference in business customer lookup
 *    mchellap  11/30/12 - Added customer receipt preference
 *    vbongu    10/01/12 - show fields as read-only in the customerlink on
 *                         status panel
 *    asinton   09/12/12 - prevent null pointer by getting ParameterManagerIfc
 *                         from Gateway instead of ADOContext.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    rabhawsa  09/12/12 - tax certificate should be encrypted.
 *    cgreene   08/29/12 - widened email field because it was too short in
 *                         customer demos
 *    acadar    05/31/12 - fixes for Xchannel
 *    acadar    05/29/12 - changes for cross channel
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    vtemker   03/29/12 - Merged conflicts
 *    vtemker   03/29/12 - Sensitive data from getDecryptedData() of
 *                         EncipheredData class fetched into byte array and
 *                         later, deleted
 *    asinton   03/26/12 - Customer UI changes to accomodate multiple
 *                         addresses.
 *    mkutiana  03/06/12 - XbranchMerge
 *                         mkutiana_hpqc1463_13_4_1_taxcertificate_not_retrieved
 *                         from rgbustores_13.4x_generic_branch
 *    mkutiana  03/05/12 - fixes to taxid and tax certificate saving -
 *                         encrypt/decrypt issues
 *    rabhawsa  12/23/11 - defect183 email field length increased to 64
 *                         characters
 *    sthallam  12/13/11 - getting the plain value of tax certificate instead
 *                         of decrypted value
 *    sthallam  12/13/11 - getting the plain value of tax certificate
 *                         instead of decrypted value
 *    sthallam  12/13/11 - XbranchMerge sthallam_bug-13495305 from main
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    cgreene   07/21/11 - correct if statement checking tax id
 *    rrkohli   07/01/11 - Encryption CR
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    mpbarnet  02/26/10 - Set the country code in the ui model.
 *    abondala  01/03/10 - update header date
 *    acadar    12/29/09 - cleanup
 *    acadar    12/29/09 - use setValue when setting email values
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    kkhirema  04/16/09 - Updated the condition for checking the ID type
 *    kkhirema  04/16/09 - added the condition to check if the ID type is
 *                         selected by user
 *    kkhirema  04/08/09 - Updated the setChangeValue method to remove the
 *                         reason code check when it was not selected as MBC
 *                         customer info
 *    ohorne    03/30/09 - added email address format validation
 *    mahising  03/17/09 - Fixed issue if items designated at different
 *                         delivery address
 *    aariyer   03/14/09 - For blanking the postal code field
 *    nkgautam  03/12/09 - fixed issue for Telephone field being mandatory for
 *                         customerlookup screens
 *    sgu       03/11/09 - change text fields to alphanumerice field
 *    mahising  03/06/09 - fixed issue for suspended transaction
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *    glwang    02/24/09 - allow first/last name fields with one character.
 *    mahising  02/22/09 - Fixed for displaying duplicate customers
 *    mahising  02/21/09 - Fixed issue for id type allignment
 *    mkochumm  02/19/09 - set country
 *    vchengeg  02/18/09 - Set the Selected country into the model before
 *                         setting the telephone number so that new phone
 *                         number is associated with the correct country.
 *    glwang    01/29/09 - set max length of user id as 10
 *    mahising  01/21/09 - fixed required fild issue for telephone field for
 *                         search customer/business customer
 *    mahising  01/20/09 - fix special direction issue
 *    mahising  01/19/09 - fixed shipping address issue
 *    mkochumm  01/16/09 - fix layout for non-english locales
 *    mahising  01/13/09 - fix QA issue
 *    miparek   01/08/09 - fixing d#1470, required mark not show when fields
 *                         are non editable while deleting business customer
 *    aphulamb  01/05/09 - fixed QA issue
 *    aphulamb  01/02/09 - fix delivery issues
 *    aphulamb  01/02/09 - fix delivery issues
 *    mahising  12/31/08 - fix QA issue
 *    vchengeg  12/15/08 - ej defect fixes
 *    vchengeg  12/12/08 - EJ defect fixes
 *    mkochumm  12/11/08 - remove phone type on search screen
 *    mkochumm  12/09/08 - fix screen layout
 *    mahising  11/28/08 - review comments fix
 *    mahising  11/25/08 - updated due to merge
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/07/08 - i18n changes for phone and postalcode fields
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         10/8/2007 11:36:46 AM  Anda D. Cadar   UI
 *         changes to not allow double bytes chars in some cases
 *    7    360Commerce 1.6         3/29/2007 7:23:54 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         7    .v8x      1.5.1.0     3/12/2007 12:30:18 PM  Maisa De Camargo
 *         Updated Reason Code Default Settings.
 *    6    360Commerce 1.5         8/10/2006 11:18:42 AM  Christian Greene Make
 *          customerIdField un-editable since the system assigns the customer
 *         its own id.
 *    5    360Commerce 1.4         1/25/2006 4:10:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:44 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse
 *:
 *    4    .v700     1.2.3.0     11/2/2005 13:35:27     Jason L. DeLeau 6592:
 *         Prevent hang that occurs when trying to delete a business customer.
 *    3    360Commerce1.2         3/31/2005 15:27:37     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:39     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:22     Robert Pearse
 *
 *Log:
 *    8    360Commerce 1.7         10/8/2007 11:36:46 AM  Anda D. Cadar   UI
 *         changes to not allow double bytes chars in some cases
 *    7    360Commerce 1.6         3/29/2007 7:23:54 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         7    .v8x      1.5.1.0     3/12/2007 12:30:18 PM  Maisa De Camargo
 *         Updated Reason Code Default Settings.
 *    6    360Commerce 1.5         8/10/2006 11:18:42 AM  Christian Greene Make
 *          customerIdField un-editable since the system assigns the customer
 *         its own id.
 *    5    360Commerce 1.4         1/25/2006 4:10:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:44 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse
 *: CustomerInfoBean.java,v $
 *Log:
 *    8    360Commerce 1.7         10/8/2007 11:36:46 AM  Anda D. Cadar   UI
 *         changes to not allow double bytes chars in some cases
 *    7    360Commerce 1.6         3/29/2007 7:23:54 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         7    .v8x      1.5.1.0     3/12/2007 12:30:18 PM  Maisa De Camargo
 *         Updated Reason Code Default Settings.
 *    6    360Commerce 1.5         8/10/2006 11:18:42 AM  Christian Greene Make
 *          customerIdField un-editable since the system assigns the customer
 *         its own id.
 *    5    360Commerce 1.4         1/25/2006 4:10:54 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:44 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse
 *:
 *    5    .v710     1.2.2.1     10/26/2005 19:56:44    Charles Suehs
 *         Editability of CustomerID now sensitive to editability, lookup
 *         status, etc.
 *    4    .v710     1.2.2.0     9/21/2005 13:39:23     Brendan W. Farrell
 *         Initial Check in merge 67.
 *    3    360Commerce1.2         3/31/2005 15:27:37     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:39     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:22     Robert Pearse
 *
 *   Revision 1.17  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.16  2004/07/29 17:15:00  aachinfiev
 *   @scr 6597 - Fixed typo and alphabetical order problems in State/Region field
 *
 *   Revision 1.15  2004/07/27 20:50:12  aschenk
 *   @scr 6356 - Changed business name is now updated on slip for MBC Customer
 *
 *   Revision 1.14  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.13  2004/06/21 22:36:32  rsachdeva
 *   @scr 4670 Send: Multiple Sends Default As USA Fixed
 *
 *   Revision 1.12  2004/06/17 13:15:46  lzhao
 *   @scr 4670: avoid phone list is null.
 *
 *   Revision 1.11  2004/06/16 21:44:15  lzhao
 *   @scr 4670: add dialog, update phone, state, country
 *
 *   Revision 1.10  2004/05/13 15:44:31  aschenk
 *   @scr 3822 - The dropdown boxes are now disabled while reviewing a customer on the Delete Customer screen.
 *
 *   Revision 1.9  2004/05/11 16:17:04  rsachdeva
 *   @scr 4670 Send: Multiple Sends
 *
 *   Revision 1.8  2004/05/11 15:58:45  aschenk
 *   @scr 4711 - MBC Customer screen hads blank fields when returned to by using esc.  Now it is repopulated with the customer info if there was one.
 *
 *   Revision 1.7  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.6  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.5  2004/02/17 23:02:21  bjosserand
 *   @scr 0
 *
 *   Revision 1.4  2004/02/17 22:54:53  bjosserand
 *   @scr 0
 *
 *   Revision 1.3  2004/02/17 22:22:23  bjosserand
 *   @scr 0
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.10   Feb 05 2004 14:28:30   bjosserand
 * Mail Bank Check.
 *
 *    Rev 1.9   Oct 02 2003 14:38:34   rsachdeva
 * setChangeStateValue using getOrgName
 * Resolution for POS SCR-3396: Update Success appears when no update has been done on MBC Customer
 *
 *    Rev 1.8   Sep 29 2003 16:13:22   rsachdeva
 * country index and state index in the model should match
 * Resolution for POS SCR-3056: UI Testing:  Data lost if selected FIND & then ESC from populated MBC Cust.
 *
 *    Rev 1.7   Sep 17 2003 11:43:32   rsachdeva
 * getOrgName used to set Business Customer Name.
 * so that when doing  Add Customer ValidatingBean does not show Error dialog for Business Customer
 * Resolution for POS SCR-2805: Sp. Order Busn. Cust. screen Min/Max discrepencies
 *
 *    Rev 1.6   Sep 16 2003 10:37:16   rsachdeva
 * Journal State Country
 * Resolution for POS SCR-2612: Create new layaway, state and country info are written to E Journal
 *
 *    Rev 1.5   Sep 12 2003 17:05:42   rsachdeva
 * customerNameField
 * Resolution for POS SCR-2889: Layaway Payment Detail Screen has discrepencies on min/max fields
 *
 *    Rev 1.4   Sep 11 2003 11:40:36   rsachdeva
 * taxCertificateField Min/Max
 * Resolution for POS SCR-2805: Sp. Order Busn. Cust. screen Min/Max discrepencies
 *
 *    Rev 1.3   Sep 11 2003 11:07:00   rsachdeva
 * customerNameField Min
 * Resolution for POS SCR-2816: Layaway Business screen min / max discrepencies
 *
 *    Rev 1.2   Sep 11 2003 10:23:20   rsachdeva
 * FirstNameField and LastNameField Max
 * Resolution for POS SCR-2815: Layaway Customer - Min / Max discrepencies between system and functional requirement doc
 *
 *    Rev 1.1   Sep 10 2003 15:30:24   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:09:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.29   Jul 24 2003 11:45:48   baa
 * add  label for ext postal code for error screen
 * Resolution for 3200: Add customer screen missing validation of fields
 *
 *    Rev 1.28   24 Jul 2003 00:09:44   baa
 * fix validation of optional fields
 *
 *    Rev 1.27   20 Jul 2003 00:38:54   baa
 * use action listener instead of an item listener for the country/state fields
 *
 *    Rev 1.26   17 Jul 2003 04:28:40   baa
 * business customr fix
 *
 *    Rev 1.25   17 Jul 2003 03:35:34   baa
 * customer fixes
 *
 *    Rev 1.24   Jul 16 2003 10:41:10   baa
 * change the order of phone type and country fields to appear ahead of phone and state.
 * Resolution for 3159: Send Transaction- Modifying Customer or Adding New Customer unable to select Canadian Province.
 *
 *    Rev 1.23   11 Jul 2003 02:17:04   baa
 * set phone types for regular customers only
 *
 *    Rev 1.22   Jul 09 2003 09:53:52   baa
 * set customer home phone as default
 * Resolution for 3061: Customer Add not Saving Phone Number
 *
 *    Rev 1.21   May 27 2003 08:49:32   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 *
 *    Rev 1.20   May 14 2003 16:22:58   baa
 * fix focus and required fields
 * Resolution for 2455: Layaway Customer screen, blank customer name is accepted
 *
 *    Rev 1.19   May 11 2003 23:30:08   baa
 * modify displayable fields
 *
 *    Rev 1.18   May 09 2003 12:50:48   baa
 * more fixes to business customer
 * Resolution for POS SCR-2366: Busn Customer - Tax Exempt- Does not display Tax Cert #
 *
 *    Rev 1.17   May 06 2003 20:11:58   baa
 * add assumption that spaces in postal code mask are optional when calculating min length
 * Resolution for POS SCR-2329: Add Customer does not accept valid 7-digit Canadian Postal Code
 *
 *    Rev 1.16   May 06 2003 13:41:12   baa
 * updates for business customer
 * Resolution for POS SCR-2203: Business Customer- unable to Find previous entered Busn Customer
 *
 *    Rev 1.15   Apr 28 2003 09:47:28   baa
 * updates to for business customer
 * Resolution for POS SCR-2217: System crashes if new business customer is created and Return is selected
 *
 *    Rev 1.14   Apr 16 2003 18:04:20   baa
 * add focus for mail back check screen
 * Resolution for POS SCR-2165: System crashes if FIND or ADD is selected from blank MBC Customer screen
 *
 *    Rev 1.13   Apr 11 2003 17:35:14   baa
 * removing 2nd column of data
 * Resolution for POS SCR-2147: Modidy   Two columns (label/field pair)  screens
 *
 *    Rev 1.12   Apr 03 2003 14:14:18   baa
 * rename business customer classes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.11   Apr 02 2003 17:50:46   baa
 * customer and screen changes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.10   Apr 02 2003 13:52:20   baa
 * I18n Database support for customer groups
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.9   Mar 26 2003 16:41:46   baa
 * fix minor bugs with customer refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.8   Mar 26 2003 10:42:48   baa
 * add changes from acceptance test
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.7   Mar 24 2003 10:08:18   baa
 * remove reference to foundation.util.EMPTY_STRING
 * Resolution for POS SCR-2101: Remove uses of  foundation constant  EMPTY_STRING
 *
 *    Rev 1.6   Mar 21 2003 10:58:46   baa
 * Refactor mailbankcheck customer screen, second wave
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.5   Mar 20 2003 18:18:58   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.4   Sep 23 2002 13:26:44   baa
 * fix decimal field
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Sep 20 2002 18:03:06   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 18 2002 17:15:30   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:17:06   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:54   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:33:36   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:54:52   msg
 * Initial revision.
 *
 *    Rev 1.16   02 Mar 2002 19:53:20   baa
 * modify screen layout
 * Resolution for POS SCR-1361: Top part of First Name field on Find Cust Info cut off
 *
 *    Rev 1.15   Feb 28 2002 19:21:06   mpm
 * Internationalization
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.14   18 Feb 2002 19:40:36   baa
 * set labels for required fields
 * Resolution for POS SCR-1306: Invalid Data Notice missing after selecting Enter with no data for text area fields
 *
 *    Rev 1.13   18 Feb 2002 11:30:02   baa
 * ui changes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.12   01 Feb 2002 14:25:34   baa
 * fix focus on screen
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 *    Rev 1.11   30 Jan 2002 16:42:38   baa
 * ui fixes
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 *    Rev 1.10   26 Jan 2002 18:52:28   baa
 * ui fixes
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.9   25 Jan 2002 21:03:48   baa
 * ui fixes for customer
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.8   Jan 22 2002 06:32:30   mpm
 * UI fixes.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.7   Jan 19 2002 10:29:36   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import oracle.retail.stores.common.customer.CustomerGiftList;
import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.gui.ButtonSpec;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.localization.AddressField;
import oracle.retail.stores.pos.ui.localization.OrderableField;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This bean is used for displaying the Customer information screen based on the
 * data from the CustomerInfoBeanModel.
 * 
 * @see oracle.retail.stores.pos;ui.beans.CustomerInfoBeanModel
 */
public class CustomerInfoBean extends TabbedUIBean
{
    /** Constant for Summary tab index. */
    private static final long serialVersionUID = 5017616502026681173L;

    /** Constant for Summary tab index. */
    public static final int SUMMARY_TAB_INDEX = 0;

    /** Constant for Recent tab index. */
    public static final int RECENT_TAB_INDEX = 1;

    /** Constant for Recommended tab index. */
    public static final int RECOMMENDED_TAB_INDEX = 2;

    /** Constant for Gift Lists tab index. */
    public static final int GIFT_LISTS_TAB_INDEX = 3;

    // instructions label tag
    protected static final String INSTRUCTIONS_LABEL = "Special_Direction";

    // Fields and labels that contain customer data
    /** customerIdLabel */
    protected JLabel customerIDLabel = null;
    /** employeeIDLabel */
    protected JLabel employeeIDLabel = null;
    /** firstNameLabel */
    protected JLabel firstNameLabel = null;
    /** lastNameLabel */
    protected JLabel lastNameLabel = null;
    /** addressLine1Label */
    protected JLabel addressLine1Label = null;
    /** addressLine2Label */
    protected JLabel addressLine2Label = null;
    /** cityLabel */
    protected JLabel cityLabel = null;
    /** stateLabel */
    protected JLabel stateLabel = null;
    /** postalCodeLabel */
    protected JLabel postalCodeLabel = null;
    /** countryLabel */
    protected JLabel countryLabel = null;
    /** telephoneLabel */
    protected JLabel telephoneLabel = null;
    /** emailLabel */
    protected JLabel emailLabel = null;
    /** emailLabel */
    protected JLabel preferredReceiptModeLabel = null;
    /** customerNameLabel */
    protected JLabel customerNameLabel = null;
    /** phoneTypeLabel */
    protected JLabel phoneTypeLabel = null;
    /** discountLabel */
    protected JLabel discountLabel = null;
    /** taxCertificateLabel */
    protected JLabel taxCertificateLabel;
    /** reasonCodeLabel */
    protected JLabel reasonCodeLabel;
    /** custTaxIDLabel */
    protected JLabel custTaxIDLabel = null;
    /** pricingGroupLabel */
    protected JLabel pricingGroupLabel = null;
    /** instrLabel */
    protected JLabel instrLabel = null;
    /** customerIDField */
    protected ConstrainedTextField customerIDField = null;
    /** taxCertificateField */
    protected AlphaNumericTextField taxCertificateField;
    /** reasonCodeField */
    protected ValidatingComboBox reasonCodeField;
    /** customerNameField */
    protected ValidatingTextField customerNameField = null;
    /** phoneTypeField */
    protected ValidatingComboBox phoneTypeField = null;
    /** emailField */
    protected ValidatingFormattedTextField emailField = null;
    /** PreferredReceiptModeField */
    protected ValidatingComboBox preferredReceiptModeField = null;
    /** employeeIDField */
    protected AlphaNumericTextField employeeIDField = null;
    /** firstNameField */
    protected ConstrainedTextField firstNameField = null;
    /** lastNameField */
    protected ConstrainedTextField lastNameField = null;
    /** addressLine1Field */
    protected ConstrainedTextField addressLine1Field = null;
    /** addressLine2Field */
    protected ConstrainedTextField addressLine2Field = null;
    /** cityField */
    protected ConstrainedTextField cityField = null;
    /** custTaxIDField */
    protected ConstrainedTextField custTaxIDField = null;
    /** postalCodeField */
    protected ValidatingFormattedTextField postalCodeField = null;
    /** telephoneField */
    protected ValidatingFormattedTextField telephoneField = null;
    /** stateField */
    protected ValidatingComboBox stateField = null;
    /** countryField */
    protected ValidatingComboBox countryField = null;
    /** discountField */
    protected ValidatingComboBox discountField = null;
    /** pricingGroupField */
    protected ValidatingComboBox pricingGroupField = null;
    /** instrField */
    protected ConstrainedTextAreaField instrField = null;
    /** instrScrollPane */
    protected JScrollPane instrScrollPane = null;

    /** handle to the container that will contain all of the elements
     * in this customer info bean.
     */
    protected JPanel thisPanel = null;

    /**
     * String representing the label tags for extended item lists.
     */
    protected String labelTags = null;

    /**
     * String representing the label weights for extended item lists.
     */
    protected String labelWeights = null;

    /**
     * telephone type selected .
     */
    private int fieldTelephoneTypeSelected = PhoneConstantsIfc.PHONE_TYPE_HOME;

    /**
     * Customer lookup flag
     */
    protected boolean customerLookup = false;

    /**
     * Business customer flag
     */
    protected boolean businessCustomer = false;

    /**
     * Delivery address flag
     */
    protected boolean deliveryAddress = false;

    /**
     * Customer search spec flag
     */
    protected boolean customerSearchSpec = false;

    /**
     * Initial country index
     */
    protected int initialCountryIndex;

    /**
     * editable indicator
     **/
    protected boolean editableFields = true;

    /**
     * TaxCertificate indicator
     */
    boolean isTaxCertificate;
    
    /**
     * update phone list indicator
     * This bean needs to keep track of when the bean is updating,
     * so that UI events are not handled when they shouldn’t be.
     */
    private boolean updatingBean=false;

    /**
     * Mapping for tab names to component used by super class, {@link TabbedUIBean}.
     */
    protected Map<String, JComponent> componentMap;

    /** Recent items list bean */
    private ExtendedItemDataListBean recentItemsBean = null;

    /** Recommended items list bean */
    private ExtendedItemDataListBean recommededItemsBean = null;

    /** Gift lists tabbed UI bean */
    private CustomerGiftListsBean giftListsBean = null;

    /**
     * Value to indicate orientation of tabs.  Specified tab placement of either: JTabbedPane.TOP,
     * JTabbedPane.BOTTOM, JTabbedPane.LEFT, or JTabbedPane.RIGHT. The default is JTabbedPane.TOP.
     */
    private int giftListsBeanOrientation = JTabbedPane.TOP;

    /** Gift lists list bean class */
    private String giftListsListBeanClass = "oracle.retail.stores.pos.ui.beans.ExtendedItemDataListBean"; 

    /** Tabbed Button specs */
    protected ButtonSpec[] tabbedButtonSpecs;

    /**
     * Default Constructor.
     */
    public CustomerInfoBean()
    {
        initialize();
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        super.initialize();
        componentMap = new HashMap<String, JComponent>(3);
        initializeFields();
        initializeLabels();
        initLayout();
    }

    /**
     * Initialize the layout.
     */
    protected void initLayout()
    {
        JPanel postalPanel = uiFactory.createPostalPanel(postalCodeField);
        JPanel panel1 = createPanel(discountField, pricingGroupLabel, pricingGroupField);

        // initial list of fields in the order they occur in the UI currently
        List<OrderableField> orderableFields = new ArrayList<OrderableField>(16);
        orderableFields.add(new OrderableField(firstNameLabel, firstNameField, AddressField.FIRST_NAME));
        orderableFields.add(new OrderableField(lastNameLabel, lastNameField, AddressField.LAST_NAME));
        orderableFields.add(new OrderableField(customerNameLabel, customerNameField, AddressField.BUSINESS_NAME));
        orderableFields.add(new OrderableField(addressLine1Label, addressLine1Field, AddressField.ADDRESS_LINE_1));
        orderableFields.add(new OrderableField(addressLine2Label, addressLine2Field, AddressField.ADDRESS_LINE_2));
        orderableFields.add(new OrderableField(cityLabel, cityField, AddressField.CITY));
        orderableFields.add(new OrderableField(countryLabel, countryField, AddressField.COUNTRY));
        orderableFields.add(new OrderableField(stateLabel, stateField, AddressField.STATE));
        orderableFields.add(new OrderableField(postalCodeLabel, postalPanel, AddressField.POSTAL_CODE));
        orderableFields.add(new OrderableField(phoneTypeLabel, phoneTypeField, AddressField.TELEPHONE_TYPE));
        orderableFields.add(new OrderableField(telephoneLabel, telephoneField, AddressField.TELEPHONE));
        orderableFields.add(new OrderableField(emailLabel, emailField));
        orderableFields.add(new OrderableField(preferredReceiptModeLabel, preferredReceiptModeField));
        orderableFields.add(new OrderableField(discountLabel, panel1));
        if (isTaxCertificate == true)
        {
            orderableFields.add(new OrderableField(reasonCodeLabel, reasonCodeField));
        }
        else
        {
            JPanel panel2 = createPanel(taxCertificateField, reasonCodeLabel, reasonCodeField);
            orderableFields.add(new OrderableField(taxCertificateLabel, panel2));
        }
        orderableFields.add(new OrderableField(custTaxIDLabel, custTaxIDField));
        orderableFields.add(new OrderableField(instrLabel, instrScrollPane));

        List<OrderableField> orderedFields = CustomerUtilities.arrangeInAddressFieldOrder(orderableFields);

        setLayout(new GridBagLayout());

        int xValue = 0;
        thisPanel = new JPanel();
        thisPanel.setLayout(new GridBagLayout());
        if (!isCustomerLookup()) // if customer info lookup do not layout the
                                 // customer id
        {
            UIUtilities.layoutComponent(thisPanel, customerIDLabel, customerIDField, 0, 0, false);
            customerIDField.setLabel(customerIDLabel);

            UIUtilities.layoutComponent(thisPanel, employeeIDLabel, employeeIDField, 0, 1, false);

            employeeIDField.setLabel(employeeIDLabel);
            xValue = 2;
        }

        for (OrderableField orderedField : orderedFields)
        {
            UIUtilities.layoutComponent(thisPanel, orderedField.getLabel(), orderedField.getField(), 0, xValue, false);
            xValue++;
        }
        // init labels for fields
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);
        customerNameField.setLabel(customerNameLabel);
        preferredReceiptModeField.setLabel(preferredReceiptModeLabel);
        // update the component to conform to UI look and feel
        uiFactory.configureUIComponent(thisPanel, UI_PREFIX);

    }

    /**
     * Creates a JPanel containing the given components.
     * 
     * @param component1
     * @param component2
     * @param component3
     * @return a JPanel containing the given components.
     */
    protected JPanel createPanel(JComponent component1, JComponent component2, JComponent component3)
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIManager.getColor("beanBackground"));
        panel.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        panel.add(component1, constraints);

        constraints.insets = new Insets(0, 3, 0, 0);
        panel.add(component2);
        panel.add(component3, constraints);

        return panel;
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        addressLine1Field = uiFactory.createConstrainedField("addressLine1Field", "1", "60", "30");
        addressLine2Field = uiFactory.createConstrainedField("addressLine2Field", "1", "60", "30");
        cityField = uiFactory.createConstrainedField("cityField", "1", "30", "20");
        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "30", "16");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "30", "20");
        postalCodeField = uiFactory.createValidatingFormattedTextField("postalCodeField", "", true, "20", "15");
        customerIDField = uiFactory.createAlphaNumericField("customerIDField", "1", "14", false);
        employeeIDField = uiFactory.createAlphaNumericField("employeeIDField", "1", "10", "14", false);
        stateField = uiFactory.createValidatingComboBox("stateField", "false", "20");
        countryField = uiFactory.createValidatingComboBox("countryField", "false", "15");
        emailField = uiFactory.createValidatingFormattedTextField("emailField", "", "60", "30");
        preferredReceiptModeField = uiFactory.createValidatingComboBox("PreferredReceiptModeField", "false", "20");
        telephoneField = uiFactory.createValidatingFormattedTextField("telephoneField", "", "30", "20");
        phoneTypeField = uiFactory.createValidatingComboBox("phoneTypeField", "false", "10");
        discountField = uiFactory.createValidatingComboBox("discountField", "false", "12");
        discountField.setEditable(false);
        customerNameField = uiFactory.createConstrainedField("customerNameField", "2", "30", "30");
        taxCertificateField = uiFactory.createAlphaNumericField("taxCertificateField", "1", "15", "30", false);
        reasonCodeField = uiFactory.createValidatingComboBox("reasonCodeField", "false", "5");
        custTaxIDField = uiFactory.createAlphaNumericField("custTaxIDField", "1", "15");
        pricingGroupField = uiFactory.createValidatingComboBox("pricingGroupField", "false", "25");
        instrScrollPane = uiFactory.createConstrainedTextAreaFieldPane("instrViaScrollPane", "0", "250", "100", "true",
                "true", JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        instrField = (ConstrainedTextAreaField)instrScrollPane.getViewport().getView();

    }

    /**
     * Initializes the labels.
     */
    protected void initializeLabels()
    {
        addressLine1Label = uiFactory.createLabel("addressLine1Label", "addressLine1Label", null, UI_LABEL);
        addressLine2Label = uiFactory.createLabel("addressLine2Label", "addressLine2Label", null, UI_LABEL);
        cityLabel = uiFactory.createLabel("cityLabel", "cityLabel", null, UI_LABEL);
        customerIDLabel = uiFactory.createLabel("customerIDLabel", "customerIDLabel", null, UI_LABEL);

        employeeIDLabel = uiFactory.createLabel("employeeIDLabel", "employeeIDLabel", null, UI_LABEL);

        firstNameLabel = uiFactory.createLabel("firstNameLabel", "firstNameLabel", null, UI_LABEL);
        lastNameLabel = uiFactory.createLabel("lastNameLabel", "lastNameLabel", null, UI_LABEL);
        postalCodeLabel = uiFactory.createLabel("postalCodeLabel", "postalCodeLabel", null, UI_LABEL);
        stateLabel = uiFactory.createLabel("stateLabel", "stateLabel", null, UI_LABEL);
        countryLabel = uiFactory.createLabel("countryLabel", "countryLabel", null, UI_LABEL);
        telephoneLabel = uiFactory.createLabel("telephoneLabel", "telephoneLabel", null, UI_LABEL);
        emailLabel = uiFactory.createLabel("emailLabel", "emailLabel", null, UI_LABEL);
        preferredReceiptModeLabel = uiFactory.createLabel("receiptPreferenceLabel", "Receipt Preference:", null, UI_LABEL);
        customerNameLabel = uiFactory.createLabel("customerNameLabel", "customerNameLabel", null, UI_LABEL);
        phoneTypeLabel = uiFactory.createLabel("phoneTypeLabel", "phoneTypeLabel", null, UI_LABEL);
        discountLabel = uiFactory.createLabel("discountLabel", "discountLabel", null, UI_LABEL);
        taxCertificateLabel = uiFactory.createLabel("taxCertificateLabel", "taxCertificateLabel", null, UI_LABEL);

        reasonCodeLabel = uiFactory.createLabel("reasonCodeLabel", "reasonCodeLabel", null, UI_LABEL);

        custTaxIDLabel = uiFactory.createLabel("custTaxIDLabel", "custTaxIDLabel", null, UI_LABEL);
        pricingGroupLabel = uiFactory.createLabel("pricingGroupLabel", "pricingGroupLabel", null, UI_LABEL);
        instrLabel = uiFactory.createLabel("instrLabel", "instrLabel", null, UI_LABEL);

    }

    /**
     * Updates the model from the screen.
     */
    @Override
    public void updateModel()
    {
        setChangeStateValue();
        if (beanModel instanceof CustomerInfoBeanModel)
        {
            CustomerInfoBeanModel model = (CustomerInfoBeanModel)beanModel;

            if (isBusinessCustomer() || model.isBusinessCustomer())
            {
                model.setOrgName(customerNameField.getText());
                model.setLastName(customerNameField.getText());

                if (!isCustomerLookup())
                {
                    EncipheredDataIfc customerTaxCert = null;

                    try
                    {
                        customerTaxCert = FoundationObjectFactory.getFactory().createEncipheredDataInstance(
                                taxCertificateField.getText().getBytes());
                    }
                    catch (EncryptionServiceException ex)
                    {
                        logger.error("Could not encrypt text", ex);
                    }
                    model.setEncipheredTaxCertificate(customerTaxCert);
                    model.setSelected(false);
                    String reason = model.getSelectedReason();
                    if (reason != null)
                    {
                        model.setSelectedReasonCode(reasonCodeField.getSelectedIndex());
                        model.setSelected(true);
                    }
                }
                model.setCountryIndex(countryField.getSelectedIndex());
                model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
                model.setTelephoneNumber(telephoneField.getFieldValue());
                
            }
            else
            {
                if (model instanceof MailBankCheckInfoBeanModel)
                {
                    model.setSelected(false);
                    String reason = model.getSelectedReason();
                    if (reason != null)
                    {
                        model.setSelectedReasonCode(reasonCodeField.getSelectedIndex());
                        model.setSelected(true);
                    }
                }
                // For shipping to customer we can have business name
                // along with fist and last name
                boolean shippingToCustomer = model instanceof ShippingMethodBeanModel;
                if (shippingToCustomer && !Util.isEmpty(customerNameField.getText()))
                {
                    model.setOrgName(customerNameField.getText());
                }

                model.setFirstName(firstNameField.getText());
                model.setLastName(lastNameField.getText());
                model.setTelephoneType(phoneTypeField.getSelectedIndex());
                model.setCountryIndex(countryField.getSelectedIndex());

                int indx = phoneTypeField.getSelectedIndex();
                if (indx == -1)
                {
                    indx = 0;
                }
                model.setTelephoneType(indx);

                if (shippingToCustomer)
                {
                    // remove previously defined phone number.
                    // there is only phone number allowed for shipping customer.
                    // One of the reason is that the first available phone
                    // number will be loaded in updateBean()
                    if (model.getPhoneList() != null)
                    {
                        for (int i = 0; i < model.getPhoneList().length; i++)
                        {
                            model.setTelephoneNumber("", i);
                        }
                    }
                }
                model.setCountryIndex(countryField.getSelectedIndex());
                model.setTelephoneNumber(telephoneField.getFieldValue(), indx);
            }
            model.setSelectedReceiptMode(preferredReceiptModeField.getSelectedIndex());
            model.setPostalCode(postalCodeField.getFieldValue());

            model.setCountryIndex(countryField.getSelectedIndex());

            if (!isCustomerLookup())
            {
                if (!model.isContactInfoOnly())
                {
                    model.setSelectedCustomerGroupIndex(discountField.getSelectedIndex());
                }
                if (!(model instanceof MailBankCheckInfoBeanModel))
                {
                    model.setEmployeeID(employeeIDField.getText());
                    model.setEmail(emailField.getFieldValue());
                }

                model.setAddressLine1(addressLine1Field.getText());
                model.setAddressLine2(addressLine2Field.getText());
                model.setCity(cityField.getText());
                model.setCountryIndex(countryField.getSelectedIndex());
                model.setStateIndex(stateField.getSelectedIndex());

                try
                {
                    EncipheredDataIfc customerTaxID = FoundationObjectFactory.getFactory()
                            .createEncipheredDataInstance(custTaxIDField.getText().getBytes());
                    model.setTaxID(customerTaxID);
                }
                catch (EncryptionServiceException e)
                {
                    logger.error("Could not encrypt text.", e);
                }
                model.setSelectedCustomerPricingGroup(pricingGroupField.getSelectedIndex());

                if (instrField != null && !instrField.getText().equals(""))
                {
                    model.setInstructions(instrField.getText());
                }

            }
        }
        // update EJournal
        setJournalStringUpdates();
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    @Override
    protected void updateBean()
    {
        String decryptedTaxId = "";
        String maskedTaxId = "";

        String decryptedTaxCertificate = "";
        String maskedTaxCertificate = "";
        this.updatingBean=true;
        if (beanModel instanceof CustomerInfoBeanModel)
        {
            // get model
            CustomerInfoBeanModel model = (CustomerInfoBeanModel)beanModel;

            // set edit mode
            boolean editMode = model.getEditableFields();

            if (model.isBusinessCustomer())
            {
                businessCustomer = true;
            }
            else
            {
                businessCustomer = false;
            }

            if (model.isDeliveryAddress())
            {
                deliveryAddress = true;
            }
            // if customer search flow , then
            // enable customersearchspec as true
            if (model.isCustomerSearchSpec())
            {
                customerSearchSpec = true;
            }

            // business Customer fields to show
            // only for add/update screen
            boolean isBusinessCustomerUpdate = (businessCustomer && !customerLookup && !model.isContactInfoOnly());

            customerIDField.setText(model.getCustomerID());
            setupComponent(customerIDField, false, !customerLookup && !model.isContactInfoOnly());
            customerIDField.setRequired(false);

            // hide first && last name fields if business customer
            firstNameField.setText(model.getFirstName());
            setupComponent(firstNameField, editMode, !businessCustomer);
            firstNameField.setRequired(!businessCustomer);
            if(!businessCustomer)
            {
                if (customerLookup)
                {
                    setFieldRequired(firstNameField, false);
                }
                else
                {
                    setFieldRequired(firstNameField, true);    
                }
            }
            

            lastNameField.setText(model.getLastName());
            setupComponent(lastNameField, editMode, !businessCustomer);
            lastNameField.setRequired(!businessCustomer);
            if(!businessCustomer)
            {
                if (customerLookup)
                {
                    setFieldRequired(lastNameField, false);
                }
                else
                {
                    setFieldRequired(lastNameField, true);
                }
            }

            instrField.setRequired(false);
            // setupComponent(instrField, editMode,false);
            setupComponent(instrScrollPane, editMode, deliveryAddress);

            employeeIDField.setText(model.getEmployeeID());
            setupComponent(employeeIDField, editMode, !(model instanceof MailBankCheckInfoBeanModel)
                    && businessCustomer && !customerLookup);

            if (!customerLookup && !model.isContactInfoOnly())
            {
                // set up preferred customer field
                String[] discountStrings = model.getCustomerGroupStrings();
                // if strings exist, set them in field
                if (discountStrings != null)
                {
                    if (discountStrings.length > 0)
                    {
                        discountStrings[0] = retrieveText("NoneLabel", "None");
                    }
                    discountField.setModel(new ValidatingComboBoxModel(discountStrings));
                    // if index valid, set it
                    int index = model.getSelectedCustomerGroupIndex();
                    if (index > -1)
                    {
                        discountField.setSelectedIndex(index);
                    }
                }
            }
            setupComponent(discountField, editMode, !customerLookup && !model.isContactInfoOnly());
            discountField.setFocusable(editMode);
            discountField.setEnabled(editMode);

            addressLine1Field.setText(model.getAddressLine1());
            setupComponent(addressLine1Field, editMode, !customerLookup);

            addressLine2Field.setText(model.getAddressLine2());
            setupComponent(addressLine2Field, editMode, !customerLookup);

            cityField.setText(model.getCity());
            setupComponent(cityField, editMode, !customerLookup);

            // Retrieve countries and update combo box
            setComboBoxModel(model.getCountryNames(), countryField, model.getCountryIndex());
            int countryIndex = model.getCountryIndex();
            String countryCode = model.getCountry(countryIndex).getCountryCode();
            UtilityManagerIfc util = (UtilityManagerIfc)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
            String phoneFormat = util.getPhoneFormat(countryCode);
            telephoneField.setFormat(phoneFormat);
            if (customerLookup)
            {
                setFieldRequired(telephoneField, false);
            }
            else
            {
                setFieldRequired(telephoneField, true);
            }
            

            String phoneValidationRegexp = null;
            phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
            telephoneField.setValidationRegexp(phoneValidationRegexp);

            String postalCodeFormat = util.getPostalCodeFormat(countryCode);
            postalCodeField.setFormat(postalCodeFormat);
            String postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
            postalCodeField.setValidationRegexp(postalCodeValidationRegexp);
            countryField.setFocusable(editMode);
            countryField.setEnabled(editMode);

            boolean shippingToCustomer = model instanceof ShippingMethodBeanModel;
            if (shippingToCustomer)
            {
                countryField.setRequired(true);
            }

            // update the state combo box with the new list of states
            setComboBoxModel(model.getStateNames(), stateField, model.getStateIndex());
            setupComponent(stateField, editMode, !customerLookup);
            stateField.setFocusable(editMode);
            stateField.setEnabled(editMode);
            if (shippingToCustomer)
            {
                stateField.setRequired(true);
            }

            postalCodeField.setValue(model.getPostalCode());
            postalCodeField.setEditable(editMode);
            postalCodeField.setEnabled(editMode);

            // update the phone
            int index = model.getTelephoneIntType();
            if (index < 0)
            {
                index = 0;
            }
            // update the phone type list
            setComboBoxModel(model.getPhoneTypes(), phoneTypeField, index);
            setupComponent(phoneTypeField, editMode, !customerLookup && !businessCustomer && !(model.isMailBankCheck()));
            for (int i = 0; i < phoneTypeField.getItemCount(); i++)
            {
                String phoneNumber = model.getTelephoneNumber(i);
                if (!Util.isEmpty(phoneNumber))
                {
                    // make first available phone the default
                    index = i;
                    fieldTelephoneTypeSelected = i;
                    i = phoneTypeField.getItemCount();
                }
            }
            phoneTypeField.setFocusable(editMode);
            phoneTypeField.setEnabled(editMode);
            telephoneField.setValue(model.getTelephoneNumber(index));
            telephoneField.setEditable(editMode);
            telephoneField.setEmptyAllowed(false);

            setupComponent(telephoneField, editMode, true); // added - brian j.

            if (!businessCustomer)
            {
                phoneTypeField.setSelectedIndex(index);
            }

            // force a clear text, in order to get rid of any previous values
            emailField.setText("");
            emailField.setValue("");
            emailField.setValidationRegexp(util.getEmailValidationRegexp());
            emailField.setValue(model.getEmail());
            setupComponent(emailField, editMode, !customerLookup && !(model instanceof MailBankCheckInfoBeanModel));
            
            if (model.getReceiptModes() != null)
            {
                 preferredReceiptModeField.setModel(new ValidatingComboBoxModel(model.getReceiptModes()));
                 preferredReceiptModeField.setSelectedIndex(model.getSelectedReceiptMode());
            }
            
            // show receipt preference field in customer master for business and non business customer
            setupComponent(preferredReceiptModeField, editMode, !customerLookup);
            preferredReceiptModeField.getLabel().setVisible(!customerLookup);
            
            // set email field mandatory if receipt option is email/print and email
            if (model.getSelectedReceiptMode() == ReceiptConstantsIfc.EMAIL_RECEIPT
                    || model.getSelectedReceiptMode() == ReceiptConstantsIfc.PRINT_AND_EMAIL_RECEIPT)
            {
                setFieldRequired(emailField, true);
            }

            // update postalfields
            int countryIndx = model.getCountryIndex();
            if (countryIndx == -1)
            {
                countryIndx = 0;
            }
            initialCountryIndex = countryIndx;
            String[] stateList = model.getStateNames();

            // update the state combo box with the new list of states
            ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

            stateField.setModel(stateModel);
            int stateIndx = model.getStateIndex();
            if (stateIndx == -1)
            {
                stateIndx = 0;
            }
            stateField.setSelectedIndex(stateIndx);

            setPostalFields();

            // postalCodeField.setRequired(editMode);
            // these are business customer specific fields
            customerNameField.setText(model.getOrgName());
            // This field is displayed for businessCustomer as well as
            // shippingToCustomer
            // This field is optional for shippingToCustomer
            setupComponent(customerNameField, editMode, businessCustomer || shippingToCustomer);

            customerNameField.setRequired(businessCustomer);
            if (customerLookup)
            {
                setFieldRequired(customerNameField, false);
            }
            else
            {
                setFieldRequired(customerNameField, businessCustomer && editMode);    
            }

            
            byte[] taxCertificate = null;

            try
            {
                taxCertificate = model.getEncipheredTaxCertificate().getDecryptedNumber();
                decryptedTaxCertificate = new String(taxCertificate);
            }
            finally
            {
                Util.flushByteArray(taxCertificate);
            }

            maskedTaxCertificate = model.getEncipheredTaxCertificate().getMaskedNumber();

            if (editMode)
            {
                taxCertificateField.setText(decryptedTaxCertificate);
            }
            else
            {
                taxCertificateField.setText(maskedTaxCertificate);
            }
           
            setupComponent(taxCertificateField, editMode, isBusinessCustomerUpdate);
            if (!taxCertificateField.isVisible() || !taxCertificateField.isDisplayable())
            {
                isTaxCertificate = true;
            }

            if (model.getReasonCodes() != null)
            {
                ValidatingComboBoxModel listModel = new ValidatingComboBoxModel(model.getReasonCodes());
                reasonCodeField.setModel(listModel);

                if (model.isSelected())
                {
                    reasonCodeField.setSelectedItem(UIUtilities.retrieveCommonText(model.getSelectedReason()));
                }
                else
                {
                    reasonCodeField.setSelectedItem(UIUtilities.retrieveCommonText(model.getDefaultValue()));
                }
            }
            setupComponent(reasonCodeField, editMode, isBusinessCustomerUpdate || model.isMailBankCheck());
            reasonCodeField.setFocusable(editMode);
            reasonCodeField.setEnabled(editMode);

            if (model instanceof MailBankCheckInfoBeanModel)
            {
                reasonCodeLabel.setText(retrieveText("IdTypeLabel", reasonCodeLabel));
            }

            byte[] taxID = null;
            try
            {
                taxID = model.getTaxID().getDecryptedNumber();
                decryptedTaxId = new String(taxID);
            }
            finally
            {
                Util.flushByteArray(taxID);
            }
            maskedTaxId = model.getTaxID().getMaskedNumber();
            if (editMode)
            {
                custTaxIDField.setText(decryptedTaxId);
            }
            else
            {
                custTaxIDField.setText(maskedTaxId);
            }
            setupComponent(custTaxIDField, editMode, !customerLookup && !model.isContactInfoOnly());
            custTaxIDField.setFocusable(editMode);
            custTaxIDField.setEnabled(editMode);
            String[] pricingStrings = model.getPricingGroups();
            if (pricingStrings != null)
            {
                if (pricingStrings.length > 0)
                {
                    pricingStrings[0] = retrieveText("NoneLabel", "None");
                }
                pricingGroupField.setModel(new ValidatingComboBoxModel(pricingStrings));
                // if index valid, set it
                int pricingIndex = model.getSelectedCustomerPricingGroup();
                if (pricingIndex > -1)
                {
                    pricingGroupField.setSelectedIndex(pricingIndex);
                }

                // get state on whether to show tabbed UI, or not.
                setShowTabbedPane(model.isShowTabbedPane());
            }
            setupComponent(pricingGroupField, editMode, !customerLookup && !model.isContactInfoOnly());
            pricingGroupField.setFocusable(editMode);
            pricingGroupField.setEnabled(editMode);

        }
        
        this.updatingBean=false;
        buildComponentMap();
        reinitialize();
        // set the selected index
        if(tabbedPane != null && beanModel instanceof CustomerInfoBeanModel)
        {
            int selectedIndex = ((CustomerInfoBeanModel)beanModel).getSelectedTabIndex();
            if(selectedIndex >= 0 && selectedIndex < tabbedPane.getTabCount())
            {
                tabbedPane.setSelectedIndex(selectedIndex);
            }
            tabbedPane.addChangeListener(this);
            tabbedPane.addChangeListener(recentItemsBean);
            tabbedPane.addChangeListener(recommededItemsBean);
            tabbedPane.addChangeListener(giftListsBean);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.TabbedUIBean#reinitialize()
     */
    @Override
    protected void reinitialize()
    {
        super.reinitialize();
        if(tabbedPane != null && beanModel instanceof CustomerInfoBeanModel)
        {
            CustomerInfoBeanModel model = (CustomerInfoBeanModel)beanModel;
            // set the tabs enabled if there is data
            if(tabbedPane.getTabCount() > RECENT_TAB_INDEX)
            {
                boolean enabled = model.getRecentItems() != null && model.getRecentItems().size() > 0;
                tabbedPane.setEnabledAt(RECENT_TAB_INDEX, enabled);
            }
            if(tabbedPane.getTabCount() > RECOMMENDED_TAB_INDEX)
            {
                boolean enabled = model.getRecommendedItems() != null && model.getRecommendedItems().size() > 0;
                tabbedPane.setEnabledAt(RECOMMENDED_TAB_INDEX, enabled);
            }
            if(tabbedPane.getTabCount() > GIFT_LISTS_TAB_INDEX)
            {
                boolean enabled = model.getGiftLists() != null && model.getGiftLists().size() > 0;
                tabbedPane.setEnabledAt(GIFT_LISTS_TAB_INDEX, enabled);
            }
        }
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    protected void setupComponent(JComponent field, boolean isEditable, boolean isVisible)
    {
        if (field instanceof ValidatingFieldIfc)
        {
            ((ValidatingFieldIfc)field).getLabel().setVisible(isVisible);
        }

        if (field instanceof JTextField)
        {
            ((JTextField)field).setEditable(isEditable);
            ((JTextField)field).setEnabled(isEditable);
        }
        if (field instanceof ValidatingComboBox)
        {
            ((ValidatingComboBox)field).setEnabled(isEditable);
        }

        field.setFocusable(isEditable);
        field.setVisible(isVisible);
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        firstNameField.addFocusListener(this);
        customerNameField.addFocusListener(this);
        customerIDField.addFocusListener(this);
        employeeIDField.addFocusListener(this);
        if (isDeliveryAddress())
        {
            instrLabel.setVisible(true);
        }
        else
        {
            instrLabel.setVisible(false);
        }

        countryField.addActionListener(this);
        phoneTypeField.addActionListener(this);
        // disable pricing group label and field
        // if parameter CustomerSpecificPricing is false
        ParameterManagerIfc pm = (ParameterManagerIfc)Gateway.getDispatcher().getManager(ParameterManagerIfc.TYPE);
        try
        {
            if (!(pm.getBooleanValue("CustomerSpecificPricing")))
            {
                // if customer search flow then, make pricing group field
                // visible but disabled
                if (isCustomerSearchSpec())
                {
                    pricingGroupField.setVisible(true);
                    pricingGroupLabel.setVisible(true);
                    pricingGroupField.setEnabled(false);
                    pricingGroupField.setEditable(false);
                }
                else
                {
                    pricingGroupField.setVisible(false);
                    pricingGroupLabel.setVisible(false);
                }
            }
        }
        catch (ParameterException e)
        {
            logger.error("parameter not found for CustomerSpecificPricing");
        }
    }

    /**
     * Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        firstNameField.removeFocusListener(this);
        customerNameField.removeFocusListener(this);
        customerIDField.removeFocusListener(this);
        employeeIDField.removeFocusListener(this);
        instrField.setText("");

        countryField.removeActionListener(this);
        phoneTypeField.removeActionListener(this);
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.pos.ui.beans.ValidatingBean#actionPerformed(java
     * .awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == countryField)
        {
            updateStates();
            setPostalFields();
            int selectedCountryIndex = countryField.getSelectedIndex();
            int countryIndex = ((CustomerInfoBeanModel)beanModel).getCountryIndex();
            updatePhoneFormat(selectedCountryIndex);
            updatePostalFormat(selectedCountryIndex, countryIndex);
        }
        else if (event.getSource() == phoneTypeField && !(this.updatingBean))
        {
            updatePhoneList(event);
        }
        else if(!this.updatingBean)
        {
            super.actionPerformed(event);
        }
    }

    /**
     * Updates the phone format for the selected country index.
     * 
     * @param selectedCountryIndex
     */
    private void updatePhoneFormat(int selectedCountryIndex)
    {
        int countryIndex = ((CustomerInfoBeanModel)beanModel).getCountryIndex();
        String countryCode = ((CustomerInfoBeanModel)beanModel).getCountry(selectedCountryIndex).getCountryCode();
        UtilityManager util = (UtilityManager)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        if (((CustomerInfoBeanModel)beanModel).getTelephoneNumber() != null)
        {
            if ((selectedCountryIndex != countryIndex)
                    || (((CustomerInfoBeanModel)beanModel).getTelephoneNumber().equals("")))
            {
                String phoneFormat = null;
                String phoneValidationRegexp = null;
                phoneFormat = util.getPhoneFormat(countryCode);
                telephoneField.setFormat(phoneFormat);
                phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
                telephoneField.setValidationRegexp(phoneValidationRegexp);
                ((CustomerInfoBeanModel)beanModel).setTelephoneNumber("");
            }
        }
    }

    /**
     * Updates the postal code format given the selected country index and
     * 
     * @param selectedCountryIndex
     * @param countryIndex
     */
    private void updatePostalFormat(int selectedCountryIndex, int countryIndex)
    {
        String countryCode = ((CustomerInfoBeanModel)beanModel).getCountry(selectedCountryIndex).getCountryCode();
        UtilityManager util = (UtilityManager)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);

        if ((selectedCountryIndex != countryIndex) || (((CustomerInfoBeanModel)beanModel).getPostalCode().equals("")))
        {
            String postalCodeFormat = null;
            String postalCodeValidationRegexp = null;
            postalCodeFormat = util.getPostalCodeFormat(countryCode);
            postalCodeField.setFormat(postalCodeFormat);
            postalCodeValidationRegexp = util.getPostalCodeValidationRegexp(countryCode);
            postalCodeField.setValidationRegexp(postalCodeValidationRegexp);
            ((CustomerInfoBeanModel)beanModel).setCountryIndex(selectedCountryIndex);
            ((CustomerInfoBeanModel)beanModel).setPostalCode("");
        }
    }

    /**
     * Requests focus on parameter value name field if visible is true.
     * 
     * @param visible true if setting visible, false otherwise
     */
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if (visible && !errorFound())
        {
            if (customerIDField.isVisible() && customerIDField.isEditable())
            {
                setCurrentFocus(customerIDField);
            }
            else
            {
                if (employeeIDField.isVisible())
                {
                    setCurrentFocus(employeeIDField);
                }
                else
                {
                    if (isBusinessCustomer())
                    {
                        setCurrentFocus(customerNameField);
                    }
                    else
                    {
                        setCurrentFocus(firstNameField);
                    }
                }
            }
        }
    }

    /**
     * Indicates whether this screens is used for a lookup. True indicates it
     * will be used for a lookup, false otherwise.
     * 
     * @param propValue customer lookup indicator
     */
    public void setCustomerLookup(String propValue)
    {
        customerLookup = (new Boolean(propValue)).booleanValue();
    }

    /**
     * Retrieves the customer lookup indicator. True indicates it will be used
     * for a lookup, false otherwise.
     * 
     * @deprecated As of 14.0, please use
     *             {@link CustomerInfoBean#isCustomerLookup()} instead.
     */
    public boolean getCustomerLookup()
    {
        return (customerLookup);
    }

    /**
     * Retrieves the customer lookup indicator. True indicates it will be used
     * for a lookup, false otherwise.
     */
    public boolean isCustomerLookup()
    {
        return customerLookup;
    }

    /**
     * Indicates whether this is a business customer scenario or not.
     * 
     * @param propValue the business customer indicator
     */
    public void setBusinessCustomer(String propValue)
    {
        businessCustomer = (new Boolean(propValue)).booleanValue();
    }

    /**
     * Set customerSearchSpec
     */
    public void setCustomersearchSpec(boolean value)
    {
        this.customerSearchSpec = value;
    }

    /**
     * Get customerSearchSpec, true if custome rsearch flow is on
     */
    public boolean isCustomerSearchSpec()
    {
        return customerSearchSpec;
    }

    /**
     * Retrieves the business customer indicator. True indicates it is a
     * business customer, false it is not.
     */
    public boolean isBusinessCustomer()
    {
        return (businessCustomer);
    }

    /**
     * Retrieves true if we need delivery address screen
     */
    public boolean isDeliveryAddress()
    {
        return (deliveryAddress);
    }

    /**
     * Update the Phone number in phone number text field , based on the selected phone type
     * 
     * @param e the listSelection event
     */
    public void updatePhoneList(ActionEvent e)
    {
        int indx = phoneTypeField.getSelectedIndex();
        if (indx == -1)
        {
            indx = 0;
        }          
        String phonenumber=telephoneField.getFieldValue().trim();
        PhoneIfc phone = ((CustomerInfoBeanModel)beanModel).getPhoneList()[fieldTelephoneTypeSelected];
        if (phone != null)
        {
            if (oracle.retail.stores.common.utility.Util.isEmpty(phonenumber))
            {
                phone.setStatusCode(PhoneConstantsIfc.STATUS_INACTIVE);
            }
            else
            {
                phone.setStatusCode(PhoneConstantsIfc.STATUS_ACTIVE);
                phone.setPhoneNumber(phonenumber);
            }
        }
        else
        {
            if(!oracle.retail.stores.common.utility.Util.isEmpty(phonenumber))
            {

                ((CustomerInfoBeanModel)beanModel).setTelephoneNumber(phonenumber, fieldTelephoneTypeSelected);

            }
        }
        fieldTelephoneTypeSelected = indx;
        ((CustomerInfoBeanModel)beanModel).setTelephoneType(indx);
        telephoneField.setValue(((CustomerInfoBeanModel)beanModel).getTelephoneNumber(indx));
    }

    /**
     * Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        customerIDLabel.setText(retrieveText("CustomerIDLabel", customerIDLabel));
        employeeIDLabel.setText(retrieveText("EmployeeIDLabel", employeeIDLabel));
        customerNameLabel.setText(retrieveText("OrgNameLabel", customerNameLabel));
        firstNameLabel.setText(retrieveText("FirstNameLabel", firstNameLabel));
        lastNameLabel.setText(retrieveText("LastNameLabel", lastNameLabel));

        discountLabel.setText(retrieveText("DiscountLabelWithColon", discountLabel));
        taxCertificateLabel.setText(retrieveText("TaxCertificateLabel", taxCertificateLabel));

        reasonCodeLabel.setText(retrieveText("ReasonCodeLabel", reasonCodeLabel));

        addressLine1Label.setText(retrieveText("AddressLine1Label", addressLine1Label));
        addressLine2Label.setText(retrieveText("AddressLine2Label", addressLine2Label));
        cityLabel.setText(retrieveText("CityLabel", cityLabel));
        stateLabel.setText(retrieveText("StateProvinceLabel", stateLabel));
        countryLabel.setText(retrieveText("CountryLabel", countryLabel));
        postalCodeLabel.setText(retrieveText("PostalCodeLabel", postalCodeLabel));
        emailLabel.setText(retrieveText("EmailLabel", emailLabel));
        preferredReceiptModeLabel.setText(retrieveText("ReceiptPreferenceLabel", preferredReceiptModeLabel));

        telephoneLabel.setText(retrieveText("TelephoneNumberLabel", telephoneLabel));
        phoneTypeLabel.setText(retrieveText("PhoneTypeLabel", phoneTypeLabel));

        custTaxIDLabel.setText(retrieveText("TaxIDLabel", custTaxIDLabel));
        pricingGroupLabel.setText(retrieveText("PricingGroupLabel", pricingGroupLabel));
        instrLabel.setText(retrieveText(INSTRUCTIONS_LABEL, instrLabel));
        // customer info
        customerIDField.setLabel(customerIDLabel);
        employeeIDField.setLabel(employeeIDLabel);
        customerNameField.setLabel(customerNameLabel);
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);

        discountField.setLabel(discountLabel);
        taxCertificateField.setLabel(taxCertificateLabel);
        reasonCodeField.setLabel(reasonCodeLabel);

        // address properties
        addressLine1Field.setLabel(addressLine1Label);
        addressLine2Field.setLabel(addressLine2Label);
        cityField.setLabel(cityLabel);
        stateField.setLabel(stateLabel);
        countryField.setLabel(countryLabel);
        postalCodeField.setLabel(postalCodeLabel);
        emailField.setLabel(emailLabel);

        // phone properties
        telephoneField.setLabel(telephoneLabel);
        phoneTypeField.setLabel(phoneTypeLabel);

        // for tax ID and pricing group
        custTaxIDField.setLabel(custTaxIDLabel);
        pricingGroupField.setLabel(pricingGroupLabel);
        instrField.setLabel(instrLabel);

    }

    /**
     * Update states as country selection changes
     */
    public void updateStates()
    {
        int countryIndx = countryField.getSelectedIndex();
        if (countryIndx < 0)
        {
            countryIndx = 0;
        }
        String[] stateList = ((CountryModel)beanModel).getStateNames(countryIndx);

        stateList = LocaleUtilities.sort(stateList, getLocale());

        // update the state combo box with the new list of states
        ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

        stateField.setModel(stateModel);
        // select 1st element of the list for the current country
        stateField.setSelectedIndex(0);
    }

    /**
     * Determine what postal fields should be enable/required.
     */
    protected void setPostalFields()
    {
        // CountryModel countryModel = (CountryModel)beanModel;
        // setFieldRequired(postalCodeField,
        // countryModel.isPostalCodeRequired(countryField.getSelectedIndex()));
        setFieldRequired(postalCodeField, false);
    }

    protected int getMinLength(String value)
    {
        int minLen = 0;
        for (int i = 0; i < value.length(); i++)
        {
            // parse string to ignoring empty spaces
            // NOTE: the assumption that spaces are optional
            // characters
            if (!Character.isSpaceChar(value.charAt(i)))
            {
                minLen++;
            }
        }
        return minLen;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: CustomerInfoBean (Revision " + getRevisionNumber() + ") @" + hashCode());
        if (beanModel != null)
        {

            strResult += "\n\nbeanModel = ";
            strResult += beanModel.toString();

        }
        else
        {
            strResult += "\nbeanModel = null\n";
        }

        if (customerIDField != null)
        {

            strResult += "\ncustomerIDField text = ";
            strResult += customerIDField.getText();
            strResult += ", min length = ";
            strResult += customerIDField.getMinLength();
            strResult += ", max length = ";
            strResult += customerIDField.getMaxLength();

        }
        else
        {
            strResult += "\ncustomerIDField = null\n";
        }

        if (employeeIDField != null)
        {

            strResult += "\nemployeeIDField text = ";
            strResult += employeeIDField.getText();
            strResult += ", min length = ";
            strResult += employeeIDField.getMinLength();
            strResult += ", max length = ";
            strResult += employeeIDField.getMaxLength();

        }
        else
        {
            strResult += "\nemployeeIDField = null\n";
        }

        if (firstNameField != null)
        {

            strResult += "\nfirstNameField text = ";
            strResult += firstNameField.getText();
            strResult += ", min length = ";
            strResult += firstNameField.getMinLength();
            strResult += ", max length = ";
            strResult += firstNameField.getMaxLength();

        }
        else
        {
            strResult += "\nfirstNameField = null\n";
        }

        if (lastNameField != null)
        {

            strResult += "\nlastNameField text = ";
            strResult += lastNameField.getText();
            strResult += ", min length = ";
            strResult += lastNameField.getMinLength();
            strResult += ", max length = ";
            strResult += lastNameField.getMaxLength();

        }
        else
        {
            strResult += "\nlastNameField = null\n";
        }

        if (addressLine1Field != null)
        {

            strResult += "\naddressLine1Field text = ";
            strResult += addressLine1Field.getText();
            strResult += ", min length = ";
            strResult += addressLine1Field.getMinLength();
            strResult += ", max length = ";
            strResult += addressLine1Field.getMaxLength();

        }
        else
        {
            strResult += "\naddressLine1Field = null\n";
        }

        if (addressLine2Field != null)
        {

            strResult += "\naddressLine2Field text = ";
            strResult += addressLine2Field.getText();
            strResult += ", min length = ";
            strResult += addressLine2Field.getMinLength();
            strResult += ", max length = ";
            strResult += addressLine2Field.getMaxLength();

        }
        else
        {
            strResult += "\naddressLine2Field = null\n";
        }

        if (cityField != null)
        {

            strResult += "\ncityField text = ";
            strResult += cityField.getText();
            strResult += ", min length = ";
            strResult += cityField.getMinLength();
            strResult += ", max length = ";
            strResult += cityField.getMaxLength();

        }
        else
        {
            strResult += "\ncityField = null\n";
        }

        if (postalCodeField != null)
        {

            strResult += "\npostalCodeField text = ";
            strResult += postalCodeField.getText();

        }
        else
        {
            strResult += "\npostalCodeField = null\n";
        }

        strResult += "\neditableFields =" + editableFields + "\n";

        // pass back result
        return (strResult);
    }

    /**
     * Tests each data field to determine if user has entered or updated data.
     * If data has changed then set the set change status to true, otherwise set
     * it to false.
     */
    protected void setChangeStateValue()
    {
        // convert the telephoneField to String for comparison
        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
        phone.parseString(telephoneField.getFieldValue());
        String phoneNumber = new String(phone.getPhoneNumber());

        if (beanModel instanceof MailBankCheckInfoBeanModel)
        {
            MailBankCheckInfoBeanModel model = (MailBankCheckInfoBeanModel)beanModel;
            if ((!model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getFirstName(),
                    firstNameField.getText()) != 0))
                    || (!model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getLastName(),
                            lastNameField.getText()) != 0))
                    || (model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getOrgName(),
                            customerNameField.getText()) != 0))
                    || (LocaleUtilities.compareValues(model.getPostalCode(), postalCodeField.getText()) != 0)
                    || (LocaleUtilities.compareValues(model.getAddressLine1(), addressLine1Field.getText()) != 0)
                    || (LocaleUtilities.compareValues(model.getAddressLine2(), addressLine2Field.getText()) != 0)
                    || (LocaleUtilities.compareValues(model.getCity(), cityField.getText()) != 0)
                    || (LocaleUtilities.compareValues(model.getTelephoneNumber(phoneTypeField.getSelectedIndex()),
                            phoneNumber) != 0)
                    || ((reasonCodeField.getSelectedIndex() != -1) && ((model.getSelectedIndex() == 0) || (model
                            .getSelectedIndex() != reasonCodeField.getSelectedIndex())))
                    || (model.getStateIndex() != stateField.getSelectedIndex())
                    || (initialCountryIndex != countryField.getSelectedIndex()))
            {
                model.setChangeState(true); // user changed data
            }
            else
            {
                model.setChangeState(false); // same data as in the model
            }
        }
    }

    /**
     * Tests each data field to determine if user has entered or updated data.
     * If data has changed then construct the journalString with the old and new
     * values for this field.
     */
    protected void setJournalStringUpdates()
    {
        Object[] dataArgs = null;
        if (beanModel instanceof MailBankCheckInfoBeanModel)
        {
            MailBankCheckInfoBeanModel model = (MailBankCheckInfoBeanModel)beanModel;
            // convert the telephoneField to String for comparison
            PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
            phone.parseString(telephoneField.getFieldValue());
            String phoneNumber = new String(phone.getPhoneNumber());

            if (model.isBusinessCustomer())
            {
                if (model.getCustomerName().compareTo(customerNameField.getText()) != 0)
                {
                    dataArgs = new Object[] { model.getCustomerName() };
                    String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.OLD_BUSINESS_NAME_LABEL, dataArgs);
                    dataArgs = new Object[] { customerNameField.getText() };
                    String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.NEW_BUSINESS_NAME_LABEL, dataArgs);
                    model.setJournalString(model.getJournalString() + Util.EOL + oldValue + Util.EOL + newValue);
                }
            }
            else
            {
                // test every field and set journal string for those that have
                // changed
                if (model.getFirstName().compareTo(firstNameField.getText()) != 0)
                {
                    dataArgs = new Object[] { model.getFirstName() };
                    String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.OLD_FIRST_NAME_LABEL, dataArgs);
                    dataArgs = new Object[] { firstNameField.getText() };
                    String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.NEW_FIRST_NAME_LABEL, dataArgs);
                    model.setJournalString(model.getJournalString() + Util.EOL + oldValue + Util.EOL + newValue);
                }
                if (model.getLastName().compareTo(lastNameField.getText()) != 0)
                {
                    dataArgs = new Object[] { model.getLastName() };
                    String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.OLD_LAST_NAME_LABEL, dataArgs);
                    dataArgs = new Object[] { lastNameField.getText() };
                    String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.NEW_LAST_NAME_LABEL, dataArgs);
                    model.setJournalString(model.getJournalString() + Util.EOL + oldValue + Util.EOL + newValue);
                }
            }
            if (model.getAddressLine1().compareTo(addressLine1Field.getText()) != 0)
            {
                dataArgs = new Object[] { model.getAddressLine1() };
                String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_ADDRESS_LINE_1_LABEL, dataArgs);
                dataArgs = new Object[] { addressLine1Field.getText() };
                String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.NEW_ADDRESS_LINE_1_LABEL, dataArgs);
                model.setJournalString(model.getJournalString() + Util.EOL + oldValue + Util.EOL + newValue);
            }
            if (model.getAddressLine2().compareTo(addressLine2Field.getText()) != 0)
            {
                dataArgs = new Object[] { model.getAddressLine2() };
                String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_ADDRESS_LINE_2_LABEL, dataArgs);
                dataArgs = new Object[] { addressLine2Field.getText() };
                String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.NEW_ADDRESS_LINE_2_LABEL, dataArgs);
                model.setJournalString(model.getJournalString() + Util.EOL + oldValue + Util.EOL + newValue);
            }

            if (model.getCity().compareTo(cityField.getText()) != 0)
            {
                dataArgs = new Object[] { model.getCity() };
                String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_CITY_LABEL, dataArgs);
                dataArgs = new Object[] { cityField.getText() };
                String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.NEW_CITY_LABEL, dataArgs);
                model.setJournalString(model.getJournalString() + Util.EOL + oldValue + Util.EOL + newValue);
            }
            if (model.getStateIndex() != stateField.getSelectedIndex())
            {
                dataArgs = new Object[] { model.getState() };
                String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_STATE_LABEL, dataArgs);
                dataArgs = new Object[] { (String)stateField.getSelectedItem() };
                String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.NEW_STATE_LABEL, dataArgs);
                model.setJournalString(model.getJournalString() + Util.EOL + oldValue + Util.EOL + newValue);
            }
            if (model.getCountryIndex() != countryField.getSelectedIndex())
            {
                dataArgs = new Object[] { model.getCountry() };
                String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_COUNTRY_LABEL, dataArgs);
                dataArgs = new Object[] { (String)countryField.getSelectedItem() };
                String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.NEW_COUNTRY_LABEL, dataArgs);
                model.setJournalString(model.getJournalString() + Util.EOL + oldValue + Util.EOL + newValue);
            }
            if ((model.getPostalCode().compareTo(postalCodeField.getFieldValue()) != 0))
            {
                dataArgs = new Object[] { model.getPostalCode() };
                String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_POSTAL_CODE_LABEL, dataArgs);
                dataArgs = new Object[] { postalCodeField.getFieldValue() };
                String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.NEW_POSTAL_CODE_LABEL, dataArgs);
                model.setJournalString(model.getJournalString() + Util.EOL + oldValue + Util.EOL + newValue);
            }
            if (model.getTelephoneNumber(model.getTelephoneIntType()).compareTo(phoneNumber) != 0)
            {
                dataArgs = new Object[] { model.getTelephoneNumber(model.getTelephoneIntType()) };
                String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_PHONE_NUMBER_LABEL, dataArgs);
                dataArgs = new Object[] { phoneNumber };
                String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.NEW_PHONE_NUMBER_LABEL, dataArgs);
                model.setJournalString(model.getJournalString() + Util.EOL + oldValue + Util.EOL + newValue);
            }

            if (model.getEmail().compareTo(emailField.getFieldValue()) != 0)
            {
                dataArgs = new Object[] { model.getEmail() };
                String oldValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.OLD_EMAIL_LABEL, dataArgs);
                dataArgs = new Object[] { emailField.getFieldValue() };
                String newValue = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.NEW_EMAIL_LABEL, dataArgs);
                model.setJournalString(model.getJournalString() + Util.EOL + oldValue + Util.EOL + newValue);
            }
            
        }
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Builds the component map for the Tabbed UI if used.
     */
    protected void buildComponentMap()
    {
        if(componentMap == null)
        {
            componentMap = new HashMap<String, JComponent>();
        }
        componentMap.clear();
        if(tabbedPane != null)
        {
            tabbedPane.removeChangeListener(this);
            tabbedPane.removeChangeListener(recentItemsBean);
            tabbedPane.removeChangeListener(recommededItemsBean);
            tabbedPane.removeChangeListener(giftListsBean);
            tabbedPane.removeAll();
        }
        if(tabbedButtonSpecs != null && tabbedButtonSpecs.length > 0)
        {
            JScrollPane scrollPane = new JScrollPane(thisPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            componentMap.put(tabbedButtonSpecs[SUMMARY_TAB_INDEX].getLabelTag(), scrollPane);
            if (beanModel instanceof CustomerInfoBeanModel && isShowTabbedPane())
            {
                CustomerInfoBeanModel model = (CustomerInfoBeanModel)beanModel;
                // set up recent items
                ExtendedItemDataListBean recentItems = getRecentItemsBean();
                recentItems.getHeaderBean().setLabelTags(labelTags);
                recentItems.getHeaderBean().setLabelWeights(labelWeights);
                recentItems.getHeaderBean().setProps(props);
                ExtendedItemDataListBeanModel recentModel = 
                        configureExtendedItemDataListBeanModel(model.getRecentItems());
                configureExtendedItemsDataListBean(recentModel, recentItems);
                componentMap.put(tabbedButtonSpecs[RECENT_TAB_INDEX].getLabelTag(), recentItems);

                // set up recommended items
                ExtendedItemDataListBean recommededItems = getRecommededItemsBean();
                recommededItems.getHeaderBean().setLabelTags(labelTags);
                recommededItems.getHeaderBean().setLabelWeights(labelWeights);
                recommededItems.getHeaderBean().setProps(props);
                ExtendedItemDataListBeanModel recommededModel = 
                        configureExtendedItemDataListBeanModel(model.getRecommendedItems());
                configureExtendedItemsDataListBean(recommededModel, recommededItems);
                componentMap.put(tabbedButtonSpecs[RECOMMENDED_TAB_INDEX].getLabelTag(), recommededItems);

                // setup gift lists
                List<CustomerGiftList> giftLists = model.getGiftLists();
                CustomerGiftListsBean giftListsTabbedPane = getGiftListsBean();
                giftListsTabbedPane.setProps(props);
                giftListsTabbedPane.setLabelTags(labelTags);
                giftListsTabbedPane.setLabelWeights(labelWeights);
                giftListsTabbedPane.setCustomerGiftLists(giftLists);
                giftListsTabbedPane.setOrientation(getGiftListsBeanOrientation());
                giftListsTabbedPane.initialize();
                componentMap.put(tabbedButtonSpecs[GIFT_LISTS_TAB_INDEX].getLabelTag(), giftListsTabbedPane);
            }
        }
        else
        {
            // if no button spec then no tabs
            componentMap.put("Summary", thisPanel);
        }
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.TabbedUIBean#getTabNames()
     */
    @Override
    protected List<String> getTabNames()
    {
        List<String> names = null;
        if(tabbedButtonSpecs != null)
        {
            names = new ArrayList<String>(tabbedButtonSpecs.length);
            String name = null;
            for(ButtonSpec buttonSpec : tabbedButtonSpecs)
            {
                name = buttonSpec.getLabelTag();
                names.add(name);
            }
        }
        else
        {
            // if no configured button spec, return an empty list
            names = new ArrayList<String>(0);
        }
        return names;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.gui.ButtonBarBeanIfc#configureButtons(oracle.retail.stores.foundation.manager.gui.ButtonSpec[])
     */
    @Override
    public void configureButtons(ButtonSpec[] buttons)
    {
        this.tabbedButtonSpecs = buttons;
    }

    /**
     * Builds an ExtendedItemDataListBeanModel from a list of ExtendedItemData objects 
     * @param items a list of ExtendedItemData objects
     * @return ExtendedItemDataListBeanModel
     */
    protected ExtendedItemDataListBeanModel configureExtendedItemDataListBeanModel(List<ExtendedItemData> items)
    {
        ExtendedItemDataListBeanModel listModel = new ExtendedItemDataListBeanModel();
        ExtendedItemDataBeanModel itemModel;
        List<ExtendedItemDataBeanModel> itemModels = new ArrayList<ExtendedItemDataBeanModel>();
        if (items != null)
        {
            for(ExtendedItemData itemData: items)
            {
                itemModel = new ExtendedItemDataBeanModel();
                itemModel.setItem(itemData);
                itemModels.add(itemModel);
            }
        }
        listModel.setItemModels(itemModels);
        return listModel;
    }

    /**
     * Configures the given <code>ExtendedItemDataListBean</code> instance.
     * @param itemsModel The <code>ExtendedItemDataListBeanModel</code> instance
     * @param itemsBean The <code>ExtendedItemDataListBean</code> instance
     */
    protected void configureExtendedItemsDataListBean(ExtendedItemDataListBeanModel itemsModel, ExtendedItemDataListBean itemsBean)
    {
        itemsBean.setHeaderBean(null);
        itemsBean.setList(null);
        itemsBean.setRenderer(ExtendedItemDataListRenderer.class.getName());
        itemsBean.setModel(itemsModel);
        itemsBean.updateBean();
        itemsBean.activate();
        itemsBean.configureScrollPane();
    }

    /**
     * Returns the <code>recentItemsBean</code> value.
     * @return the recentItemsBean
     */
    public ExtendedItemDataListBean getRecentItemsBean()
    {
        if(this.recentItemsBean == null)
        {
            // if null, initialize with default
            this.setRecentItemsBean(null);
        }
        return recentItemsBean;
    }

    /**
     * Sets the recent items bean via the beanClass specified from the *uicfg.xml file.
     * @param beanClass The bean class to use for the <code>recentItemsBean</code> attribute.  Must be an extension of {@link ExtendedItemDataListBean}.
     */
    public void setRecentItemsBean(String propValue)
    {
        if (propValue != null)
        {
            if(recentItemsBean == null ||
               !recentItemsBean.getClass().getName().equals(propValue))
            {
                recentItemsBean = (ExtendedItemDataListBean)UIUtilities.getNamedClass(propValue);
                recentItemsBean.setHeaderBean(null);
                recentItemsBean.setList(null);
                recentItemsBean.setRenderer(ExtendedItemDataListRenderer.class.getName());
                recentItemsBean.activate();
                recentItemsBean.configureScrollPane();
            }
        }
        if (recentItemsBean == null)
        {
            recentItemsBean = new ExtendedItemDataListBean();
        }
    }

    /**
     * Returns the <code>recommededItemsBean</code> value.
     * @return the recommededItemsBean
     */
    public ExtendedItemDataListBean getRecommededItemsBean()
    {
        if(this.recommededItemsBean == null)
        {
            // if null, initialize with default
            this.setRecommendedItemsBean(null);
        }
        return recommededItemsBean;
    }

    /**
     * Sets the <code>recommededItemsBean</code> value.
     * @param recommededItemsBean the recommededItemsBean to set
     */
    public void setRecommededItemsBean(ExtendedItemDataListBean recommededItemsBean)
    {
        this.recommededItemsBean = recommededItemsBean;
    }

    /**
     * Sets the recommended items bean via the beanClass specified from the *uicfg.xml file.
     * @param beanClass The bean class to use for the <code>recommededItemsBean</code> attribute.  Must be an extension of {@link ListBean}.
     */
    public void setRecommendedItemsBean(String propValue)
    {
        if (propValue != null)
        {
            if(recommededItemsBean == null ||
               !recommededItemsBean.getClass().getName().equals(propValue))
            {
                recommededItemsBean = (ExtendedItemDataListBean)UIUtilities.getNamedClass(propValue);
                recommededItemsBean.setHeaderBean(null);
                recommededItemsBean.setList(null);
                recommededItemsBean.setRenderer(ExtendedItemDataListRenderer.class.getName());
                recommededItemsBean.activate();
                recommededItemsBean.configureScrollPane();
            }
        }
        if (recommededItemsBean == null)
        {
            recommededItemsBean = new ExtendedItemDataListBean();
        }
    }

    /**
     * Returns the <code>giftListsBean</code> value.
     * @return the giftListsBean
     */
    public CustomerGiftListsBean getGiftListsBean()
    {
        return giftListsBean;
    }

    /**
     * Sets the <code>giftListsBean</code> value.
     * @param giftListsBean the giftListsBean to set
     */
    public void setGiftListsBean(CustomerGiftListsBean giftListsBean)
    {
        this.giftListsBean = giftListsBean;
    }

    /**
     * Sets the gift lists tabbed UI bean via the beanClass specified from the *uicfg.xml file.
     * @param beanClass The bean class to use for the <code>giftListsBean</code> attribute.  Must be an extension of {@link TabbedUIBean}.
     */
    public void setGiftListsBean(String propValue)
    {
        if (propValue != null)
        {
            if(giftListsBean == null ||
               !giftListsBean.getClass().getName().equals(propValue))
            {
                giftListsBean = (CustomerGiftListsBean)UIUtilities.getNamedClass(propValue);
            }
        }
        if (giftListsBean == null)
        {
            giftListsBean = new CustomerGiftListsBean();
        }
    }

    /**
     * Returns the <code>giftListsBeanOrientation</code> value.
     * @return the giftListsBeanOrientation
     */
    public int getGiftListsBeanOrientation()
    {
        return giftListsBeanOrientation;
    }

    /**
     * Sets the <code>giftListsBeanOrientation</code> value.
     * @param giftListsBeanOrientation the giftListsBeanOrientation to set
     */
    public void setGiftListsBeanOrientation(int giftListsBeanOrientation)
    {
        this.giftListsBeanOrientation = giftListsBeanOrientation;
    }

   /**
    * This method allows for the setting of the tab position from within the <code>*.uicfg.xml</code> configuration.
    * Set by using bean configuration in the <code>*uicfg.xml</code> as in the following example: <br/>
    * <pre>
    * {@code
    * <BEANPROPERTY
    *      propName="giftListsBeanOrientation"
    *      propValue="RIGHT"/>
    * }
    * </pre>
    * <br/>
    * Valid values are TOP, RIGHT, BOTTOM, LEFT.<br/>
    * @param orientation The desired orientation as a string described above
    */
    public void setGiftListsBeanOrientation(String propValue)
    {
        if("BOTTOM".equalsIgnoreCase(propValue))
        {
            this.giftListsBeanOrientation = JTabbedPane.BOTTOM;
        }
        else if("RIGHT".equalsIgnoreCase(propValue))
        {
            this.giftListsBeanOrientation = JTabbedPane.RIGHT;
        }
        else if("LEFT".equalsIgnoreCase(propValue))
        {
            this.giftListsBeanOrientation = JTabbedPane.LEFT;
        }
        else
        {
            this.giftListsBeanOrientation = JTabbedPane.TOP;
        }
    }

    /**
     * Returns the <code>giftListsListBeanClass</code> value.
     * @return the giftListsListBeanClass
     */
    public String getGiftListsListBeanClass()
    {
        return giftListsListBeanClass;
    }

    /**
     * Sets the <code>giftListsListBeanClass</code> value.
     * @param giftListsListBeanClass the giftListsListBeanClass to set
     */
    public void setGiftListsListBeanClass(String giftListsListBeanClass)
    {
        this.giftListsListBeanClass = giftListsListBeanClass;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.TabbedUIBean#getComponentMap()
     */
    @Override
    protected Map<String, JComponent> getComponentMap()
    {
        return this.componentMap;
    }

    /**
     * Sets the <code>labelTags</code> value.
     * @param labelTags the new <code>labelTags</code> value
     */
    public void setLabelTags(String labelTags)
    {
        this.labelTags = labelTags;
    }

    /**
     * Sets the <code>labelWeights</code> value.
     * @param labelWeights the new <code>labelWeights</code> value
     */
    public void setLabelWeights(String labelWeights)
    {
        this.labelWeights = labelWeights;
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        CustomerInfoBean bean = new CustomerInfoBean();
        bean.telephoneField.setValue("4043865851");
        System.out.println("1: " + bean.telephoneField.getFieldValue());
        bean.telephoneField.setValue("4(512)555-1212");
        System.out.println("2: " + bean.telephoneField.getFieldValue());
        bean.telephoneField.setValue("(512)555-1212");
        System.out.println("3: " + bean.telephoneField.getFieldValue());

        UIUtilities.doBeanTest(bean);
    }


}
