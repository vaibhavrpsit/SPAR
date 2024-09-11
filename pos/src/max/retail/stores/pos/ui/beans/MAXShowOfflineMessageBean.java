/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*	Rev 1.1     Dec 22, 2016		Ashish Yadav			Credit Card FES
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */ 
package max.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.BaseBeanAdapter;
import oracle.retail.stores.pos.ui.beans.DisplayTextBeanModel;

public class MAXShowOfflineMessageBean extends BaseBeanAdapter{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 8156196798447909419L;

	/**
    revision number for this class
**/
public static final String revisionNumber = "$Revision: 3$";

/** the bean name **/
protected String beanName = "MAXShowOfflineMessageBean";

/** The bean model **/
protected DisplayTextBeanModel beanModel = new DisplayTextBeanModel();

/** The main panel **/
protected JPanel displayTextPane = null;
protected JPanel displayTextPane1 = null;

/** The area to display the text - transaction **/
protected JLabel displayTextLabel = null;

protected JLabel displayPlutusOfflineLabel = null;

protected JLabel titleLabel = null;

/** Title wrapper */
protected JPanel titleLabelWrap = null;

/** Error message component */
protected JTextPane messagePanel = null;

/** Error message wrapper */
protected JPanel messagePanelWrap = null;


/** Error message wrapper */

/** The scroll listener **/
//protected ScrollListener listener = new ScrollListener();

//---------------------------------------------------------------------
/**
   Default class Constructor and initializes its components.
 **/
//---------------------------------------------------------------------
public MAXShowOfflineMessageBean ()
{
    super();
    initialize();
}


protected void initialize()
{
    setName("MAXShowOfflineMessageBean");
    UI_PREFIX = "MAXShowOfflineMessageBean";
    uiFactory.configureUIComponent(this, UI_PREFIX);

    initComponents();
    initLayout();
}

protected void initComponents()
{
    createTitleLabel();
    createMessagePanel();
//    createButtonPanel();
}

protected void initLayout()
{
    setLayout(new GridBagLayout());
// Changes starts for rev 1.1 (Ashish : Credit Card)
    // create the constraints
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.weightx = 1.0;
//    gbc.weighty = 0.1;
    //gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    gbc.gridy = 0;
    
    // add the title label
    add(titleLabelWrap, gbc);

    //add the TextPane
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridy = 2;
    gbc.weighty = 0.8;
    add(messagePanelWrap, gbc);
 // Changes ends for rev 1.1 (Ashish : Credit Card)
}

//--------------------------------------------------------------------------
/**
 *  Creates the text pane that holds the dialog message.
 */
//--------------------------------------------------------------------------
//Changes starts for rev 1.1 (Ashish : Credit Card)
protected void createMessagePanel()
{
    String prefix = UI_PREFIX + ".message";

    messagePanel = new JTextPane(new DefaultStyledDocument());
    uiFactory.configureUIComponent(messagePanel, prefix);

    messagePanel.setMargin(UIManager.getInsets(prefix + ".margin"));
    messagePanel.setEditable(false);
    messagePanel.setText("\n\n\n                                            		Plutus is offline.           \n                 		Please click offline button to tender in offline mode.                            ");
    
    messagePanel.setFont(new Font("Arial", Font.BOLD, 17));
    Style def =
        StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

    Style s = messagePanel.addStyle("regular", def);

    Font dialogFont = messagePanel.getFont();

    if(dialogFont != null)
    {
        StyleConstants.setFontSize(s, dialogFont.getSize());
        StyleConstants.setFontFamily(s, dialogFont.getFamily());
        StyleConstants.setBold(s,dialogFont.isBold());
    }
    s = messagePanel.addStyle("centered",def);
    StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);

    s = messagePanel.addStyle("justified",def);
    StyleConstants.setAlignment(s, StyleConstants.ALIGN_JUSTIFIED);
    messagePanelWrap = new JPanel(new BorderLayout());
    messagePanelWrap.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    uiFactory.configureUIComponent(messagePanelWrap, prefix + "Wrap");
    messagePanelWrap.add(messagePanel, BorderLayout.CENTER);
}
//Changes ends for rev 1.1 (Ashish : Credit Card)

//--------------------------------------------------------------------------
/**
 *  Creates the label that holds the dialog title.
 */
//--------------------------------------------------------------------------
protected void createTitleLabel()
{
    titleLabelWrap = new JPanel(new BorderLayout());

    uiFactory.configureUIComponent(titleLabelWrap, UI_PREFIX + ".header");

    titleLabel =
        uiFactory.createLabel("DialogTitle", "Plutus Offline", null, UI_PREFIX + ".header.label");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
    titleLabel.setForeground(Color.WHITE);
    titleLabelWrap.setBackground(Color.RED);
    titleLabelWrap.add(titleLabel, BorderLayout.CENTER);
}
//----------------------------------------------------------------------
/**
   Returns the revision number of the class.
   <P>
   @return String representation of revision number
**/
//----------------------------------------------------------------------
public String getRevisionNumber()
{
    return(Util.parseRevisionNumber(revisionNumber));
}

/**
 * @param args
 */
public static void main(String[] args)
{
    UIUtilities.setUpTest();
    MAXShowOfflineMessageBean bean = new MAXShowOfflineMessageBean();

    StringBuffer text = new StringBuffer("This is some text. ");
    text.append("And this is some more text. There is still more text. ");
    text.append("Even more text.");

   // bean.setDisplayText(text.toString());

    UIUtilities.doBeanTest(bean);
}

}
