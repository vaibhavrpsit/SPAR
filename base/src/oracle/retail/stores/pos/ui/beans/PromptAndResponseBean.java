/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PromptAndResponseBean.java /main/36 2014/03/18 16:18:17 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   03/18/14 - Make allowable characters configurable.
 *    cgreene   11/20/13 - refactoring to allow for fingerprint mgr override
 *    cgreene   11/18/13 - reverted fingerprint login close dialog changes
 *    mkutiana  11/05/13 - handle fingerprint event for override popup
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    jswan     09/06/11 - Fixed issues with gift card balance when
 *                         issuing/reloading multiple gift cards and one card
 *                         fails.
 *    asinton   09/01/11 - prevent a null pointer exception which broke swiping
 *                         at defaultMSR.
 *    mchellap  08/24/11 - BUG#12413374 Scanned Temporary pass not recognized
 *    cgreene   08/11/11 - UI tweaks for global button size and prompt area
 *                         alignment
 *    rrkohli   06/13/11 - field Highlighting CR
 *    rrkohli   05/30/11 - making response text field rounded for POS UI
 *                         quickwin
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    blarsen   02/04/11 - a fingerprint reader method was renamed to aid
 *                         clarity.
 *    blarsen   06/09/10 - XbranchMerge blarsen_biometrics-poc from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    blarsen   05/25/10 - Added setFingerprintData() to receive
 *                         FingerprintReader events and move the tour forward.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     02/08/10 - Added comment due to code review.
 *    jswan     02/08/10 - Fixed issue scanning data in a NumericByteDocument.
 *                         This was causing a problem scanning gift card
 *                         numbers.
 *    cgreene   01/27/10 - Added method setCurrentResponseText
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    cgreene   06/18/09 - added index for reason code search performance
 *    asinton   06/16/09 - In setPINPadData changed logic to check for empty
 *                         string instead of null.
 *    acadar    04/23/09 - translate credit card expiration date format
 *    cgreene   03/11/09 - if setting same type of field widget onto this bean,
 *                         just ignore it
 *    sgu       02/25/09 - fix max lenght for all item quantity fields
 *    asinton   11/19/08 - Changed calls from getScanData to getScanLabelData.
 *
 * ===========================================================================
 * $Log:
 *    13   360Commerce 1.12        6/6/2008 2:18:05 PM    Anil Rathore
 *         Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *    12   360Commerce 1.11        5/28/2008 3:46:48 PM   Anil Rathore
 *         Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *    11   360Commerce 1.10        4/11/2008 11:14:06 AM  Alan N. Sinton  CR
 *         29819: Clearing scanned entry from response field.  Code change was
 *          reviewed by Mathews Kochummen.
 *    10   360Commerce 1.9         4/4/2008 3:54:34 AM    Chengegowda Venkatesh
 *          CR 29819 : Cleared the item id from active response text area in
 *         the PromptAndResponsePanel
 *    9    360Commerce 1.8         2/27/2008 3:19:23 PM   Alan N. Sinton  CR
 *         29989: Changed masked to truncated for UI renders of PAN.
 *    8    360Commerce 1.7         2/5/2008 2:41:29 PM    Siva Papenini
 *         CR-30,115 Unable to cancel from Credit Debit dialog
 *         Fixed null pointer exception
 *    8    I18N_P2    1.6.1.0     2/6/2008 2:14:02 PM    Sandy Gu        Set
 *         promps to optional take multi byte characters.
 *    7    360Commerce 1.6         11/21/2007 1:59:17 AM  Deepti Sharma   CR
 *         29598: changes for credit/debit PAPB
 *    6    360Commerce 1.5         11/13/2007 2:38:51 PM  Jack G. Swan
 *         Modified to support retrieving a byte array from the UI for card
 *         numbers instead of a String object.
 *    5    360Commerce 1.4         10/10/2007 1:02:00 PM  Anda D. Cadar
 *         Changes to not allow double byte chars
 *    4    360Commerce 1.3         1/25/2006 4:11:39 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:26 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:28 PM  Robert Pearse
 *
 *    5    .v700     1.2.1.1     1/4/2006 16:10:41      Jason L. DeLeau 6613:
 *         Allow scanned items to be of any length.
 *    4    .v700     1.2.1.0     10/25/2005 15:16:39    Jason L. DeLeau 6176:
 *         Make sure the focus stays on the prompt and response screen after a
 *         help window is closed.
 *   Revision 1.13.2.2  2005/01/03 16:52:53  cdb
 *   @scr 4223 Updated to attempt to preserve old behavior of clearing prompt and response region during scanning while
 *   simultaneously preserving behavior addressed by SCR's 3103, 3124, 3129, and 3130
 *
 *   Revision 1.13.2.1  2004/10/25 21:53:52  cdb
 *   @scr 7451 Merge Services Impact SCR from Trunk to Branch 7.0.0.
 *
 *   Revision 1.14  2004/10/15 21:49:22  cdb
 *   @scr 5953 Modified so that there is no limit on size of scanned number.
 *
 *   Revision 1.13  2004/08/27 17:33:26  lzhao
 *   @scr 6244: update after code review.
 *
 *   Revision 1.12  2004/08/27 14:19:15  lzhao
 *   @scr 6244: set minLength for document listeners no matter response area exist or not.
 *
 *   Revision 1.11  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.10  2004/06/23 23:13:19  lzhao
 *   @scr 5793: after scan, the letter should be search for item inquiry
 *
 *   Revision 1.9  2004/06/22 22:03:53  crain
 *   @scr 5348 Barcode scanning not working on Gift Certificate Issue or Tender
 *
 *   Revision 1.8  2004/05/11 14:33:00  jlemieux
 *   @scr
 *   270 Fixed by adding a veto mechanism to the lifting of the GlassComponent. In particular, the GlassComponent lift is now vetoed when the scanner's scan queue contains 1 or more items and we are on a multiscan screen in POS. This effectively makes POS "prefer" to drain scan queues rather than service user input, which is what we want.
 *
 *   Revision 1.7  2004/05/07 19:00:30  jdeleau
 *   @scr 4386 AlphaNumeric Text Fields should allow spaces.
 *
 *   Revision 1.6  2004/05/07 14:43:38  awilliam
 *   @scr 4405 fix for employee number minlength 1 maxlength 10 and needs to accept spaces
 *
 *   Revision 1.5  2004/04/21 18:56:40  epd
 *   @scr 4322 Fixing UI for tender invariant
 *
 *   Revision 1.4  2004/03/22 06:17:50  baa
 *   @scr 3561 Changes for handling deleting return items
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.8   27 Jan 2004 18:44:42   Tim Fritz
 * Removed the first line of the updateBean() method.
 *
 *    Rev 1.7   Dec 11 2003 17:10:52   epd
 * Updated to respond to actions from buttons pressed on CID
 *
 *    Rev 1.6   Dec 04 2003 11:15:26   epd
 * When PINPad device is cancelled by customer on CPOI, the letter mailed is now "NoPin", it used to be "Cancel".  I had to distinguish between cancelling from the PIN pad device and the UI.
 *
 *    Rev 1.5   Dec 01 2003 16:12:32   epd
 * fixed bug with MSR model
 *
 *    Rev 1.4   Nov 19 2003 18:20:56   crain
 * Moved formatEYSDateField() to updateBean()
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.3   Nov 19 2003 17:18:04   crain
 * Added check for null
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.2   Oct 22 2003 19:18:08   epd
 * added support for date field
 *
 *    Rev 1.1   Sep 08 2003 17:30:40   DCobb
 * Migration to jvm 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:11:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.12   23 Jul 2003 01:04:36   baa
 * add grabFocus property
 *
 *    Rev 1.11   Jul 22 2003 17:34:50   bwf
 * Check if there is a pin before authorizing.
 * Resolution for 3191: Ingenico Device- Pressing Cancel after entering numbers on the Pin Pad acts the same a enter
 *
 *    Rev 1.10   Jun 17 2003 14:17:46   bwf
 * Checked to make sure minimum length is satisfied.  If it is not, reset scanner.
 * Resolution for 2299: Scanning a GC# of 4 digits the number is accepted. The Min/Max is 13-19
 *
 *    Rev 1.9   May 29 2003 14:09:28   RSachdeva
 * Re-enable Scans
 * Resolution for POS SCR-2433: Retrieve suspended layaway trans & enter layaway number, POS client is crashed
 *
 *    Rev 1.9   May 29 2003 14:04:40   RSachdeva
 * Re-enable scans
 * Resolution for POS SCR-2433: Retrieve suspended layaway trans & enter layaway number, POS client is crashed
 *
 *    Rev 1.8   May 12 2003 15:59:34   RSachdeva
 * Scanning Layaway Number
 * Resolution for POS SCR-2433: Retrieve suspended layaway trans & enter layaway number, POS client is crashed
 *
 *    Rev 1.7   May 08 2003 11:28:24   bwf
 * Added MSRModel.
 * Resolution for 1933: Employee Login enhancements
 *
 *    Rev 1.6   Apr 16 2003 12:23:12   baa
 * defect fixes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.5   Apr 11 2003 18:08:06   pdd
 * removed the stripping of the non-numeric in setScannerData()... we need the alpha.
 * Resolution for 1933: Employee Login enhancements
 *
 *    Rev 1.4   Aug 07 2002 19:34:24   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Jul 05 2002 17:58:50   baa
 * code conversion and reduce number of color settings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   03 Jun 2002 11:51:24   baa
 * get insets from uifactory
 * Resolution for POS SCR-1713: Follow ui  dev. guidelines
 *
 *    Rev 1.7   03 Jun 2002 11:47:50   baa
 * get insets from ui factory instead of uimanager
 * Resolution for POS SCR-1713: Follow ui  dev. guidelines
 *
 *    Rev 1.6   30 May 2002 08:50:54   baa
 * export prompt area dimensions to property files
 * Resolution for POS SCR-1553: cleanup dead code
 *
 *    Rev 1.5   26 Apr 2002 00:10:56   baa
 * fix prompt response area, export dialog btns
 * Resolution for POS SCR-1553: cleanup dead code
 *
 *    Rev 1.4   19 Apr 2002 16:43:16   baa
 * revert changes until problem with linux gets resolve
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 *    Rev 1.0   Mar 18 2002 11:57:00   msg
 * Initial revision.
 *
 *    Rev 1.12   05 Mar 2002 17:20:10   jbp
 * control retrieving signature from device.
 * Resolution for POS SCR-75: Verfiy Signature screen displays previous signature
 *
 *    Rev 1.11   20 Feb 2002 12:55:00   jbp
 * check negative alllowed for nonzero text field.
 * Resolution for POS SCR-1326: UoM entered as -100  at sell item crashes application
 *
 *    Rev 1.10   19 Feb 2002 23:08:14   vxs
 * Added if condition in setMSRData()
 * Resolution for POS SCR-595: GIft card inquiry - text on screen incorrect
 *
 *    Rev 1.9   Feb 12 2002 18:57:26   mpm
 * Added support for text externalization.
 * Resolution for POS SCR-228: Merge VABC, Pier 1 changes
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.8   11 Feb 2002 10:43:02   jbp
 * Added checks for dynamic setting of prompt and respnse length.
 * Resolution for POS SCR-1177: Zip Code prompt for customer not accepting alpha characters
 * Resolution for POS SCR-1178: Zip Code and phone number entry allowing too many characters
 *
 *    Rev 1.7   30 Jan 2002 16:42:50   baa
 * ui fixes
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 *    Rev 1.6   Jan 23 2002 10:06:28   mpm
 * UI fix
 * Resolution for POS SCR-228: Merge VABC, Pier 1 changes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalTextFieldUI;
import javax.swing.text.Document;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.FingerprintReaderModel;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.device.PINPadModel;
import oracle.retail.stores.foundation.manager.device.ScannerModel;
import oracle.retail.stores.foundation.manager.device.ScannerSession;
import oracle.retail.stores.foundation.manager.gui.InputDataAdapter;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.pos.device.FormModel;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.ClearActionListener;
import oracle.retail.stores.pos.ui.behavior.ResponseDocumentListener;
import oracle.retail.stores.pos.ui.behavior.ResponseTextListener;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * The prompt and response bean holds both the prompt area and the input area.
 * The prompt area is on the left, input panel is on the right.
 *
 * @version $Revision: /main/36 $
 */
public class PromptAndResponseBean extends BaseBeanAdapter
    implements ClearActionListener, ResponseTextListener
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3238897113749845147L;

    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(PromptAndResponseBean.class);

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/36 $";

    /** A reference to the bean model */
    protected PromptAndResponseModel promptModel = null;

    /** The prompt area */
    protected PromptAreaBean promptArea = null;

    /** The number of input characters to allow
     * @deprecated as of 13.4. Field resizes with screen. 
     */
    protected int inputColumns;

    protected JPanel responseFieldPanel;

    /** Default response field */
    protected JTextField defaultResponseField = null;

    /** Active response field */
    protected JTextField activeResponseField = null;

    /** layout constraints for the prompt bean. */
    protected GridBagConstraints promptContraints;

    /** layout constraints for the response bean. */
    protected GridBagConstraints responseContraints;

    /** Zero allowed or not in the CurrencyTextField */
    protected boolean zeroAllowed = false;

    /** Negative value allowed or not in the CurrencyTextField */
    protected boolean negativeAllowed = true;

    /** Indicates if the user can enter data into the response field. */
    protected boolean enterData = false;

    /** The current prompt to display. */
    protected String currentPromptText = "";

    /**
     * The argument array for text replacement
     */
    protected String[] args = null;

    /** The current response text. */
    protected String currentResponseText = "";

    /** Minimum length of the activeResponseField **/
    protected String minLength = "0";

    /** Maximum length of the activeResponseField **/
    protected String maxLength = "255";

    /** indicates that the focus should be kept in the response field */
    protected boolean grabFocus = true;

    /** The document listener for the active response field **/
    protected DocumentListener activeResponseFieldListener = null;

    /**
     * flag that indicates if double byte chars are allowed
     */
    protected boolean doubleByteCharsAllowed = true;

    /**
     * flag to indicate that the response field should be cleared upon
     * setScannerData
     */
    protected boolean clearResponseOnSetScannerData = false;
    
    /**
     * allowable characters can be entered
     */
    protected char[] allowableCharacters = new char[0];

    /**
     * Default constructor.
     */
    public PromptAndResponseBean()
    {
        super();
    }

    /**
     * Configures this bean.
     */
    public void configure()
    {
        UI_PREFIX = "PromptResponseArea";
        inputColumns = 14;
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initLayout();
        initComponents();
    }

    /**
     * Initializes the components for this bean and add them to container.
     */
    protected void initComponents()
    {
        promptArea = new PromptAreaBean();
        add(promptArea);// adds to west

        responseFieldPanel = new JPanel(new GridBagLayout());
        responseFieldPanel.setOpaque(false);
        add(responseFieldPanel); //adds to east

        defaultResponseField = new JTextField();
        setActiveResponseField(defaultResponseField);
    }

    /**
     * Configures the layout to a {@link GridBagLayout}.
     */
    protected void initLayout()
    {
        setLayout(new GridLayout(1, 2));
    }

    /**
     * Sets the flag clearResponseOnSetScannerData. This is set from the
     * *uicfg.XML and is used by screens where a new PromptAndResponseModel is
     * created within a site because the prompt text is dynamic. Usually, the
     * PromptAndResponseModel is created automatically by the UI Subsystem. The
     * adverse affect is that after scanning the data, which implies a "Next"
     * button press, the response field still retains the text even after the
     * scanned value has been processed.
     *
     * @param clear
     */
    public void setClearResponseOnSetScannerData(String clear)
    {
        clearResponseOnSetScannerData = UIUtilities.getBooleanValue(clear);
    }

    /**
     * Sets the response field by instantiating the class name in the parameter.
     * This field is set from the XML using a BEANPROPERTY.
     *
     * @param responseFieldClassName the response field class name
     */
    public void setResponseField(String responseFieldClassName)
    {
        if (activeResponseField != null && activeResponseField.getClass().getName().equals(responseFieldClassName))
        {
            return; // quick exit. no need to set same widget onto this bean
        }

        JTextField responseField = (JTextField) UIUtilities.getNamedClass(responseFieldClassName);

        if (responseField != null)
        {
            setActiveResponseField(responseField);
            // If dealing with a CurrencyTextField,
            // then have to set boolean indicating zero allowed.
            if (activeResponseField instanceof CurrencyTextField)
            {
                setZeroAllowed(new Boolean(zeroAllowed).toString());
                setNegativeAllowed(new Boolean(negativeAllowed).toString());
            }
            else if (activeResponseField instanceof NonZeroDecimalTextField)
            {
                setNegativeAllowed(new Boolean(negativeAllowed).toString());
            }
            // UI guidelines requirements state alphaNumeric fields must allow
            // spaces
            else if (activeResponseField instanceof AlphaNumericPlusTextField)
            {
                ((AlphaNumericPlusTextField) activeResponseField).setSpaceAllowed(false);
                setDoubleByteCharsAllowed(new Boolean(doubleByteCharsAllowed).toString());
                
                ((AlphaNumericPlusTextField) activeResponseField).setAllowableCharacters(getAllowableCharacters());   
            }
            else if (activeResponseField instanceof AlphaNumericTextField)
            {
                ((AlphaNumericTextField) activeResponseField).setSpaceAllowed(false);
                setDoubleByteCharsAllowed(new Boolean(doubleByteCharsAllowed).toString());
            }
            else if (activeResponseField instanceof ConstrainedTextField)
            {
                setDoubleByteCharsAllowed(new Boolean(doubleByteCharsAllowed).toString());
            }

        }
    }

    /**
     * Sets the response field attribute. Can be called before or after setting
     * of response field, so this method is also called from within
     * setResponseField() This field is set from the XML using a BEANPROPERTY.
     *
     * @param value true if a zero value should be allowed, false otherwise
     */
    public void setZeroAllowed(String value)
    {
        zeroAllowed = new Boolean(value).booleanValue();

        if (activeResponseField instanceof CurrencyTextField)
        {
            ((CurrencyTextField) activeResponseField).setZeroAllowed(zeroAllowed);
        }
    }

    /**
     * Formats the EYSDateField
     */
    public void formatEYSDateField()
    {
    	if (promptModel.getResponseTypeDateFormat() != -1)
    	{
    		((EYSDateField) activeResponseField).setFormat(promptModel.getResponseTypeDateFormat());
            SimpleDateFormat dateFormat = DomainGateway.getSimpleDateFormat(getDefaultLocale(), "MM/yyyy");
            String translatedLabel = getTranslatedDatePattern(dateFormat.toPattern());
            currentPromptText = LocaleUtilities.formatComplexMessage(currentPromptText,translatedLabel);
            promptArea.setText(currentPromptText);
    	}
    }

    /**
     * Sets the response field attribute. Can be called before or after setting
     * of response field, so this method is also called from within
     * setResponseField() This field is set from the XML using a BEANPROPERTY.
     *
     * @param value true if a zero value should be allowed, false otherwise
     */
    public void setNegativeAllowed(String value)
    {
        negativeAllowed = new Boolean(value).booleanValue();

        if (activeResponseField instanceof CurrencyTextField)
        {
            ((CurrencyTextField) activeResponseField).setNegativeAllowed(negativeAllowed);
        }
        else if (activeResponseField instanceof NonZeroDecimalTextField)
        {
            ((NonZeroDecimalTextField) activeResponseField).setNegativeAllowed(negativeAllowed);
        }
    }

    /**
     * Swaps out the active response field. This method does not allow the field
     * to be set to null. Also, if the same field is set as already is set, then
     * this method will do nothing.
     *
     * @param rspField a class the inherits from JTextField
     */
    public void setActiveResponseField(JTextField rspField)
    {
        // compare references directly for performance
        if (rspField == null || rspField == activeResponseField)
        {
            return;
        }
        if (activeResponseField != null)
        {
            responseFieldPanel.remove(activeResponseField);
        }

        // reset prompt dimension and get response field
        activeResponseField = rspField;
        uiFactory.configureUIComponent(activeResponseField, UI_PREFIX + ".field");
        activeResponseField.setName("InputField");

        // make response text field rounded
        activeResponseField.setUI(new RoundTextFieldUI());

        responseFieldPanel.add(activeResponseField, getResponseFieldConstraints());
        revalidate();
    }

    /**
     * Sets the the prompt gridbag constraints
     *
     * @returns gbc the gridbag constraints
     */
    protected GridBagConstraints getPromptConstraints()
    {
        if (promptContraints == null)
        {
            UIFactory factory = UIFactory.getInstance();
            promptContraints = new GridBagConstraints();

            promptContraints.anchor = GridBagConstraints.WEST;
            promptContraints.fill = GridBagConstraints.BOTH;
            promptContraints.insets = factory.getInsets("promptArea");
            promptContraints.gridx = 0;
            promptContraints.gridy = 0;

            promptContraints.weightx = 0.1;
            promptContraints.weighty = 1.0;

            promptContraints.gridwidth = 1;
            promptContraints.gridheight = 1;
        }
        return promptContraints;
    }

    /**
     * Sets the the prompt gridbag constraints
     *
     * @returns gbc the gridbag constraints
     * @deprecated as of 13.4. No longer used. The layout is GridLayout.
     */
    protected GridBagConstraints getResponseFieldConstraints()
    {
        if (responseContraints == null)
        {
            UIFactory factory = UIFactory.getInstance();
            responseContraints = new GridBagConstraints();
            responseContraints.anchor = GridBagConstraints.WEST;
            responseContraints.fill = GridBagConstraints.HORIZONTAL;
            responseContraints.insets = factory.getInsets("responseField");
            responseContraints.weightx = 1.0;
        }
        return responseContraints;
    }

    /**
     * Gets the corresponding Dimension
     *
     * @returns Dimension the dimension associated to the specified property
     */
    public Dimension getDimension(String value)
    {
        UIFactory factory = UIFactory.getInstance();
        return factory.getDimension(value);
    }

    /**
     * Sets the the prompt text value on the screen. This field is set from the
     * XML using a BEANPROPERTY.
     *
     * @param value the prompt text
     */
    public void setPromptText(String value)
    {
        currentPromptText = value;
        if (args != null && args.length > 0)
        {
            if (currentPromptText != null)
            {
                currentPromptText = LocaleUtilities.formatComplexMessage(currentPromptText, args);
            }
            else if (args[0] != null)
            {
                currentPromptText = args[0];
            }
        }
        promptArea.setText(currentPromptText);

    }

    /**
     * Sets the the prompt text value based on the property tag on the screen.
     * This field is set from the XML using a BEANPROPERTY.
     *
     * @param value the prompt text
     */
    public void setPromptTextTag(String value)
    {
        currentPromptText = retrieveText(value, "Prompt Text Not Found");
        setPromptText(currentPromptText);
    }

    /**
     * Returns the active response text.
     *
     * @return String representing the active response text.
     */
    public String getResponseText()
    {
        return activeResponseField.getText();
    }

    /**
     * Set the request focus boolean on the response area.
     *
     * @param acceptInput a string with the value of "true" or "false"
     */
    public void setEnterData(String acceptInput)
    {
        enterData = UIUtilities.getBooleanValue(acceptInput);
    }

    /**
     * Sets the grab focus indicator. This field is set from the XML using a
     * BEANPROPERTY.
     *
     * @param value true if focus should be grabbed, false otherwise
     */
    public void setGrabFocus(String value)
    {
        grabFocus = new Boolean(value).booleanValue();
    }

    /**
     * Specific implementation of updateBean.
     * 
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#updateBean()
     */
    @Override
    protected void updateBean()
    {
        promptModel = beanModel.getPromptAndResponseModel();
        if (promptModel != null)
        {
            if (promptModel.getPromptText() != null)
            {
                setPromptText(promptModel.getPromptText());
            }
            if (promptModel.getArguments() != null && promptModel.getArguments().length > 0)
            {
                if (currentPromptText != null)
                {
                    currentPromptText = LocaleUtilities.formatComplexMessage(currentPromptText, promptModel
                            .getArguments());
                }
                else
                {
                    currentPromptText = promptModel.getArguments()[0];
                }
                promptArea.setText(currentPromptText);
            }
            if (promptModel.getResponseText() != null)
            {
                currentResponseText = promptModel.getResponseText();
            }
            if (promptModel.getResponseEnabledClass() != null)
            {
                enterData = promptModel.getResponseEnabled();
            }

            if (promptModel.getResponseFieldType() != null)
            {
                setResponseField(promptModel.getResponseFieldType());
                // reset response field to null
                promptModel.setResponseTypeDefault();
            }
            if (promptModel.getMaxLength() != null)
            {
                setMaxLength(promptModel.getMaxLength());
                activeResponseField.revalidate();
            }

            if (promptModel.getMinLength() != null)
            {
                setMinLength(promptModel.getMinLength());
            }
            if (promptModel.getGrabFocus() != null)
            {
                if (promptModel.getGrabFocus().booleanValue())
                {
                    grabFocus = true;
                }
                else
                {
                    grabFocus = false;
                }
            }
            // format the EYSDateField
            formatEYSDateField();
        }
        else
        {
            activeResponseField.setText("");
        }

    }

    /**
     * Specific implementation of updateModel.
     */
    @Override
    public void updateModel()
    {
        if (beanModel != null)
        {
            if (promptModel == null)
            {
                promptModel = new PromptAndResponseModel();
            }

            // Modified to support PBCP; stores and handles PAN numbers as
            // bytes so that they can be over written
            if (activeResponseField instanceof BytesRetrievableIfc)
            {
                BytesRetrievableIfc br = (BytesRetrievableIfc) activeResponseField;
                byte[] text = br.getTextBytes();
                String value = null;
                if (text != null)
                {
                    value = new String(text);
                }
                if (text != null && value != null && value.trim().length() != 0)
                {
                    byte[] bytes = new byte[text.length];
                    System.arraycopy(text, 0, bytes, 0, text.length);
                    br.clearTextBytes();
                    promptModel.setResponseBytes(bytes);
                }
                else
                { // CR 30,115 -- Unable to cancel from Credit Debit dialog
                    promptModel.setResponseText("");
                } // CR 30,115
            }
            else
            {
                promptModel.setResponseText(activeResponseField.getText());
            }
            beanModel.setPromptAndResponseModel(promptModel);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        if (activeResponseField != null)
        {
            activeResponseField.setText(currentResponseText);
            if (!enterData)
            {
                hideResponseField(activeResponseField);
            }
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#deactivate()
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        enterData = false;
        currentPromptText = null;
        args = null;
        currentResponseText = "";
        activeResponseField.getDocument().removeDocumentListener(activeResponseFieldListener);
        activeResponseFieldListener = null;
        activeResponseField.setText(currentResponseText);
        minLength = "0";
        maxLength = "255";
        currentComponent = null;
    }

    /**
     * Override JPanel set Visible to request focus.
     *
     * @param aFlag indicates if the component should be visible or not.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        if (aFlag)
        {
            if (enterData)
            {
                if (promptModel != null)
                {
                    activeResponseField.setEditable(promptModel.isResponseEditable());
                }
                else
                {
                    activeResponseField.setEditable(true);
                }

                if (activeResponseField.isEditable())
                {
                    activeResponseField.setFocusable(true);
                    activeResponseField.selectAll();
                    setCurrentFocus(activeResponseField);
                }
                else
                {
                    activeResponseField.setFocusable(false);
                }

                activeResponseField.setEnabled(true);
                if (grabFocus && activeResponseField.isEditable())
                {
                    // restrict focus to the active field
                    activeResponseField.setFocusTraversalKeysEnabled(false);
                }
                else
                {
                    activeResponseField.setFocusTraversalKeysEnabled(true);
                }

                activeResponseField.setVisible(true);

                int minLengthInt = Integer.parseInt(minLength);
                int maxLengthInt = Integer.parseInt(maxLength);

                Document doc = activeResponseField.getDocument();
                if (doc instanceof ConstrainedTextDocument)
                {
                    if ((maxLengthInt > 0) && (maxLengthInt >= minLengthInt))
                    {
                        ((ConstrainedTextDocument) doc).setMaxLength(maxLengthInt);
                    }
                }
                if (activeResponseField instanceof ValidatingTextField)
                {
                    if (minLengthInt >= 0)
                    {
                        ((ValidatingTextField) activeResponseField).setMinLength(minLengthInt);

                        if (activeResponseFieldListener != null)
                        {
                            if (activeResponseFieldListener instanceof ResponseDocumentListener)
                            {
                                ((ResponseDocumentListener) activeResponseFieldListener).setMinLength(minLengthInt);
                            }
                        }
                    }
                }
            }
            else
            {
                // data not being entered. disable field
                // for performance, not necessary to remove field once invisible
                hideResponseField(activeResponseField);
                promptArea.revalidate();
            }
            int minLengthInt = Integer.parseInt(minLength);
            int maxLengthInt = Integer.parseInt(maxLength);

            Document doc = activeResponseField.getDocument();

            if (doc instanceof ConstrainedTextDocument)
            {
                if ((maxLengthInt > 0) && (maxLengthInt >= minLengthInt))
                {
                    ((ConstrainedTextDocument) doc).setMaxLength(maxLengthInt);
                }
            }
            if (activeResponseField instanceof ValidatingTextField)
            {
                if (minLengthInt >= 0)
                {
                    ((ValidatingTextField) activeResponseField).setMinLength(minLengthInt);
                }
            }
            if (activeResponseFieldListener != null && activeResponseFieldListener instanceof ResponseDocumentListener
                    && minLengthInt >= 0)
            {
                if (activeResponseFieldListener instanceof GlobalNavigationButtonBean)
                {
                    ((ResponseDocumentListener) activeResponseFieldListener).setMinLength(minLengthInt);
                }
            }
            revalidate();
            if (enterData)
            {
                setCurrentFocus(activeResponseField);
            }
        }
    }

    /**
     * Makes the specified text field invisible and not available for input.
     *
     * @param textFieldToHide
     */
    protected void hideResponseField(JTextField textFieldToHide)
    {
        textFieldToHide.setEnabled(false);
        textFieldToHide.setEditable(false);
        textFieldToHide.setVisible(false);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#updatePropertyFields()
     */
    @Override
    protected void updatePropertyFields()
    {
    }

    /**
     * This event is called when the user indicates he is ready to go on.
     *
     * @param e
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if (activeResponseField != null)
        {
            activeResponseField.setText("");
        }
    }

    /**
     * Adds a listener that will receive events from this object.
     *
     * @param l the listener to add
     */
    public void addDocumentListener(DocumentListener listener)
    {
        activeResponseFieldListener = listener;
        activeResponseField.getDocument().addDocumentListener(listener);
    }

    /**
     * Removes a listener that will no longer receive events from this object.
     *
     * @param l the listener to remove
     */
    public void removeDocumentListener(DocumentListener listener)
    {
        activeResponseField.getDocument().removeDocumentListener(listener);
    }

    /**
     * Set the {@link #currentResponseText}. Call this method when setting the
     * input programatically. Override to massage the input as needed.
     * 
     * @param inputText
     */
    protected void setCurrentResponseText(String inputText)
    {
        currentResponseText = inputText;
    }

    /**
     * Receive scanner data. Called by the UI Framework.
     *
     * @param data DeviceModelIfc
     */
    public void setScannerData(DeviceModelIfc data)
    {
        if (beanModel == null)
            return;
        boolean resetFlag = false;
        if (logger.isInfoEnabled())
            logger.info("Received scanner data: " + data);

        ScannerModel scannerModel = (ScannerModel) data;
        setCurrentResponseText(new String(scannerModel.getScanLabelData()));

        // Make sure scanned data fits
        Document doc = activeResponseField.getDocument();
        if (doc instanceof ConstrainedTextDocument)
        {
            ((ConstrainedTextDocument) doc).setMaxLength(currentResponseText.length() + 1);
        }

        activeResponseField.setText(currentResponseText);
        if (!Util.isEmpty(currentResponseText))
        {
            // The result of the call to updateModel() at this point means that this method will 
            // be called twice: once in response to the scan event and a second time when tour 
            // retrieves the scanned value from the bean.  All the document objects behind 
            // response bean, except the NumericByteDocument, require this behavior.  
            //
            // In the NumericByteDocument the first call to updateModel() clears out the data;
            // this behavior is purposeful.  It makes sure that a PAN is in memory for as short 
            // a time as possible.  This is the only place to prevent the getTextBytes from being
            // called on NumericByteDocument twice.
            if (!(doc instanceof NumericByteDocument))
            {
                updateModel();
            }
            if (promptModel == null)
            {
                updateModel();
            }
            
            promptModel.setScanned(true);

            // dont send next letter if data length is less than minimum length
            int minLen = 0;
            try
            {
                minLen = Integer.parseInt(minLength);
            }
            catch (NumberFormatException nfe)
            {
                logger.error("setScannerData: Minimum length is not a number");
            }
            if (currentResponseText.length() >= minLen)
            {
                // Mail the letter for an implied 'Enter'
                if (beanModel instanceof ItemInfoBeanModel)
                {
                    UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.SEARCH), true);
                }
                else
                {
                    // clear the scanned text from the response field
                    if (clearResponseOnSetScannerData)
                    {
                        activeResponseField.setText("");
                    }
                    UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);
                }
            }
            else
            {
                resetFlag = true;
            }
        }
        else
        {
            resetFlag = true;
        }
        if (resetFlag)
        {
            // re-enable scans
            try
            {
                DeviceTechnicianIfc dt = (DeviceTechnicianIfc) Gateway.getDispatcher().getLocalTechnician(
                        DeviceTechnicianIfc.TYPE);

                if (dt != null)
                {
                    try
                    {
                        String sessionName = ScannerSession.TYPE;
                        ScannerSession scannerSession = (ScannerSession) dt.getDeviceSession(sessionName);
                        scannerSession.setEnabled(true);
                    }
                    catch (DeviceException e)
                    {
                        logger.error("Error enabling scanner", e);
                    }
                }
            }
            catch (TechnicianNotFoundException e)
            {
                logger.error("Can't get DeviceTechnician", e);
            }
            catch (Exception e)
            {
                logger.error("Error occurred resetting scanner", e);
            }
        }
    }

    /**
     * Receive MSR data. Called by the UI Framework.
     *
     * @param data DeviceModelIfc
     */
    public void setMSRData(DeviceModelIfc data)
    {
        if (logger.isInfoEnabled())
            logger.info("Received MSR data: " + data);

        MSRModel msrModel = (MSRModel) data;
        setCurrentResponseText(msrModel.getEncipheredCardData().getTruncatedAcctNumber());
        if (currentResponseText == null)
        {
            setCurrentResponseText(new String(msrModel.getTrack2Data()));
        }
        activeResponseField.setText(currentResponseText);
        updateModel();
        promptModel.setSwiped(true);
        promptModel.setMSRModel(msrModel);

        // Mail the letter for an implied 'Enter'
        UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);
    }

    /**
     * Receive Fingerprint data. Called by the {@link InputDataAdapter} via the
     * event dispatch framework. This event will also cause the dialog to close
     * if this bean is in a dialog.
     *
     * @param data DeviceModelIfc
     */
    public void setFingerprintData(DeviceModelIfc data)
    {
        if (logger.isInfoEnabled())
            logger.info("Received Fingerprint data: " + data);

        FingerprintReaderModel fingerprintModel = (FingerprintReaderModel) data;
        
        updateModel();
        promptModel.setFingerprintRead(true);
        promptModel.setFingerprintModel(fingerprintModel);

        // mail the letter.
        UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);

        // In case this bean is in a dialog, the letter above is ignore.
        // The UISubsystem will discern from the response command.
        try
        {
            promptModel.setResponseCommand(CommonLetterIfc.NEXT);
            UISubsystem.getInstance().closeDialog();
        }
        catch (UIException e)
        {
            logger.error("Unable to close dialog upon fingerprint event.", e);
        }
    }

    /**
     * Receive MSR data. Called by the UI Framework. This one is used just for
     * employee login cards. This assumes that an employee login card is a
     * normal credit card. The account number is cut to 10 digits and the login
     * is set from the name to 10 digits. If you are doing a service project for
     * a specific card with either employee id or login id on it, it is better
     * to use the method setMSRData().
     *
     * @param data DeviceModelIfc
     */
    public void setMSRDataEmployee(DeviceModelIfc data)
    {
        if (logger.isInfoEnabled())
            logger.info("Received MSR data: " + data);

        MSRModel msrModel = (MSRModel) data;
        updateModel();
        promptModel.setSwiped(true);
        promptModel.setMSRModel(msrModel);

        // Mail the letter for an implied 'Enter'
        UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);
    }

    /**
     * Receive PINPad data. Called by the UI Framework.
     *
     * @param data DeviceModelIfc
     */
    public void setPINPadData(DeviceModelIfc data)
    {
        if (logger.isInfoEnabled())
            logger.info("Received PINPad data: " + data);

        PINPadModel pinPadModel = (PINPadModel) data;
        Letter pinLetter = null;
        if (Util.isEmpty(pinPadModel.getEncryptedPIN()))
        {
            pinLetter = new Letter("NoPin");
        }
        else
        {
            updateModel();
            promptModel.setPINPadData(pinPadModel);
            pinLetter = new Letter(CommonLetterIfc.SUCCESS);
        }
        UISubsystem.getInstance().mail(pinLetter, true);
    }

    /**
     * Receive CIDScreen data. Called by the UI Framework.
     *
     * @param data DeviceModelIfc
     */
    public void setCIDScreenData(DeviceModelIfc data)
    {
        logger.debug("PromptAndResponseBean, setCIDScreenData");
        if (logger.isInfoEnabled())
           logger.info("Received CIDSCreen data: " + data);

        updateModel();

        FormModel formModel = (FormModel) data;
        promptModel.setFormModel(formModel);

        String action = formModel.getButtonAction();
        Letter letter = new Letter(action);
        UISubsystem.getInstance().mail(letter, true);
    }

    /**
     * Receive Signature data. Called by the UI Framework.
     *
     * @param data DeviceModelIfc
     */
    public void setSigData(DeviceModelIfc data)
    {
        if (logger.isInfoEnabled())
            logger.info("Received SigCapModel data: " + data);

        updateModel();

        // Mail the letter for an implied 'Enter'
        UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);
    }

    /**
     * Sets the minimum length of the active response field on the screen. This
     * field is set from the XML using a BEANPROPERTY.
     *
     * @param value the minimum length as String
     */
    public void setMinLength(String value)
    {
        minLength = value;
    }

    /**
     * Gets the minimum length of the active response field on the screen. This
     * field is set from the XML using a BEANPROPERTY.
     *
     * @return minLength as String
     */
    public String getMinLength()
    {
        return minLength;
    }

    /**
     * Sets the maximum length of the active response field on the screen. This
     * field is set from the XML using a BEANPROPERTY.
     *
     * @param value the maximum length as String
     */
    public void setMaxLength(String value)
    {
        maxLength = value;
    }

    /**
     * Gets the maximum length of the active response field on the screen. This
     * field is set from the XML using a BEANPROPERTY.
     *
     * @return maxLength as String
     */
    public String getMaxLength()
    {
        return maxLength;
    }
    
    /**
     * Sets the allowable characters of the active response field on the screen. This
     * field is set from the XML using a BEANPROPERTY.
     *
     * @param value the maximum length as String
     */
    public void setAllowableCharacters(String allowableCharacters)
    {
        char[] chars = allowableCharacters.toCharArray();
        if (chars != null)
        {
            this.allowableCharacters = chars;
        }
    }

    /**
     * Gets the allowable characters of the active response field on the screen. This
     * field is set from the XML using a BEANPROPERTY.
     *
     * @return maxLength as String
     */
    public char[] getAllowableCharacters()
    {
        return allowableCharacters;
    }

    /**
     * Sets the response field attribute. Can be called before or after setting
     * of response field, so this method is also called from within setResponseField()
     * This field is set from the xml using a BEANPROPERTY.
     * @param value true if multi byte should be allowed,
     * false otherwise
     */
    @SuppressWarnings("deprecation")
    public void setDoubleByteCharsAllowed(String doubleByteCharsAllowedFlag)
    {
        this.doubleByteCharsAllowed = new Boolean(doubleByteCharsAllowedFlag).booleanValue();

        if (activeResponseField instanceof ConstrainedTextField)
        {
            ((ConstrainedTextField)activeResponseField).setDoubleByteCharsAllowed(doubleByteCharsAllowed);
        }
    }

    /**
     * Does nothing. This widget will control when the fields get added and
     * removed for performance reasons.
     *
     * @see java.awt.Container#removeAll()
     */
    @Override
    public void removeAll()
    {
        // do nothing
    }

    /**
     * Main entry point for testing.
     */
    public static void main(String args[])
    {
        UIUtilities.setUpTest();

        PromptAndResponseBean bean = new PromptAndResponseBean();
        bean.configure();
        bean.setPromptText("Prompt test.");

        UIUtilities.doBeanTest(bean);
        System.out.println(bean);
        for (int i = 0; i < bean.getComponentCount(); i++)
            System.out.println(bean.getComponent(i));
    }
}

class RoundTextFieldUI extends MetalTextFieldUI
{
    private Color backGroundColor = UIManager.getColor("PromptResponseArea.field.background");

    public static ComponentUI createUI(JComponent c)
    {
        return new RoundTextFieldUI();
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);
        c.setBorder(new RoundBorder());
        Font font = UIManager.getFont("PromptResponseArea.field.font");
        c.setFont(font);
        c.setOpaque(false);
    }

    protected void paintSafely(Graphics g)
    {
        JComponent c = getComponent();
        if (!c.isOpaque())
        {
            g.setColor(backGroundColor);
            g.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 21, 21);

        }
        super.paintSafely(g);
    }

    private static class RoundBorder extends AbstractBorder
    {
        private static final long serialVersionUID = 6776438867260014L;

        public Insets getBorderInsets(Component c)
        {
            return new Insets(4, 4, 4, 4);
        }

        public Insets getBorderInsets(Component c, Insets insets)
        {
            insets.left = insets.top = insets.right = insets.bottom = 4;
            return insets;
        }
    }
}
