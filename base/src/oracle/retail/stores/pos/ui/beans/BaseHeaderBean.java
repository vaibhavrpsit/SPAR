/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BaseHeaderBean.java /main/17 2014/05/20 12:14:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  09/11/14 - Sorting item search result by default for a  
                           given column when it gets displayed first time.
 *    cgreene   05/20/14 - refactor list model sorting
 *    abhinavs  05/11/14 - Sorting Item search results enhancement
 *    cgreene   05/01/13 - fixed size calculation to allow headers to get
 *                         bigger if font size is increased
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 4    360Commerce 1.3         1/25/2006 4:10:50 PM   Brett J. Larsen merge
 *    7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 3    360Commerce 1.2         3/31/2005 4:27:16 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:19:47 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:09:34 PM  Robert Pearse
 *:
 4    .v700     1.2.1.0     9/13/2005 15:37:43     Jason L. DeLeau Ifan
 *    id_itm_pos maps to multiple id_itms, let the user choose which one to
 *    use.
 3    360Commerce1.2         3/31/2005 15:27:16     Robert Pearse
 2    360Commerce1.1         3/10/2005 10:19:47     Robert Pearse
 1    360Commerce1.0         2/11/2005 12:09:34     Robert Pearse
 *
Revision 1.4  2004/03/16 17:15:22  build
Forcing head revision
 *
Revision 1.3  2004/03/16 17:15:16  build
Forcing head revision
 *
Revision 1.2  2004/02/11 20:56:26  rhafernik
@scr 0 Log4J conversion and code cleanup
 *
Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:09:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 14:47:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:52:18   msg
 * Initial revision.
 *
 *    Rev 1.4   Feb 28 2002 19:21:04   mpm
 * Internationalization
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   Feb 25 2002 10:51:14   mpm
 * Internationalization
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   Feb 23 2002 15:04:10   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.1   30 Jan 2002 16:42:36   baa
 * ui fixes
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SortOrder;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.comparators.BeanPropertyComparator;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * A configurable panel of text labels. This is used mainly for list headers.
 *
 * @version $Revision: /main/17 $
 */
public class BaseHeaderBean extends JPanel
{
    private static final long serialVersionUID = 8300450321429320380L;
    public static final Logger logger = Logger.getLogger(BaseHeaderBean.class);

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/17 $";

    /** The default height of this bean, and also the labels herein. */
    public static final int DEFAULT_HEIGHT = 19;
    /** The default width of the list that has this header. */
    public static final int DEFAULT_WIDTH = 300;
    /** Button UI name. Equals "BaseTableHeaderUI". */
    public static final String uiClassID = "BaseTableHeaderUI";
    /** Key used to retrieve ascending icon from plaf. */
    public static final String ICON_KEY_ASCENDING = "buttonIconSortAscending";
    /** Key used to retrieve descending icon from plaf. */
    public static final String ICON_KEY_DESCENDING = "buttonIconSortDescending";

    public String UI_PREFIX = "List.header";
    public String UI_LABEL = UI_PREFIX + ".label";

    /**
     * Convenient reference to UIFactory.
     * @deprecated as of 14.1. Use local references instead.
     */
    protected UIFactory uiFactory = UIFactory.getInstance();

    /** an array of label alignments */
    protected int[] alignments = null;

    /** an array of labels */
    protected JLabel[] labels = null;

    /** an array of text for the labels */
    protected String[] labelText = null;

    /** an array of property tags for the labels */
    protected String[] labelTags = null;

    /** an array of weights for the labels */
    protected int[] labelWeights = null;

    /** the width of the entire header (this bean) */
    protected int headerWidth = DEFAULT_WIDTH;

    /** the minimum height of a label (default is 19) */
    protected int labelHeight = DEFAULT_HEIGHT;

    /** The properties object which contains local specific text */
    protected Properties props = null;

    /** Reference to the list that this header is displayed over. */
    protected EYSList list = null;

    /** Direction in which the list is being sorted. */
    protected SortOrder sortOrder = SortOrder.UNSORTED;

    /** The column being sorted. */
    protected int sortedColumn = -1;

    /** properties on which column can be sorted */
    protected String[] columnSortProperties = null;
    
    /** Default column name to be sorted */
    protected String defaultSortedColumn = null;

    /** Icon for when sorted ascending. */
    protected ImageIcon iconAscending = null;

    /** Icon for descending ascending. */
    protected ImageIcon iconDescending = null;

    /** The listener for the list that will fire when the model is changed. */
    protected PropertyChangeListener modelChangeListener;

    /**
     * Default constructor.
     * @deprecated as of 14.1
     */
    @Deprecated
    public BaseHeaderBean()
    {
        this(null);
    }

    /**
     * Constructor that takes the list that this is a header of.
     * 
     * @param list the child list.
     * @since 14.1
     */
    public BaseHeaderBean(EYSList list)
    {
        this.setList(list);
        modelChangeListener = new ModelChangeListener();
        // configure this bean from the look and feel defaults
        UIFactory uiFactory = UIFactory.getInstance();
        uiFactory.configureUIComponent(this, UI_PREFIX);
        // Setting sorting arrow icons on default header label
        setIconAscending(UIUtilities.getImageIcon(ICON_KEY_ASCENDING, this));
        setIconDescending(UIUtilities.getImageIcon(ICON_KEY_DESCENDING, this));
    }

    /**
     * Activates this bean.
     */
    public void activate()
    {
        // the labels aren't usually set on instantiation,
        // so we initialize here.
        initialize();
    }

    /**
     * Initialize this class. Inits components and sets up sorting icons.
     */
    protected void initialize()
    {
        // set up the header width and label height
        int width = getWidth();

        if (width != 0)
        {
            headerWidth = width;
        }
        initComponents();
    }

    /**
     * Initialize the components in this bean. This loops through the array of
     * label text and creates a label for each one.
     */
    protected void initComponents()
    {
        // if we have label text, create the labels
        if (labelText != null && labelText.length > 0)
        {
            // flag that determines if labels need recreating
            boolean remake = (labels == null || labels.length != labelText.length);

            // reinitialize the label array if needed
            if (remake)
            {
                labels = new JLabel[labelText.length];
            }
            for (int i = 0; i < labelText.length; i++)
            {
                // make a new label if we need to
                if (remake)
                {
                    labels[i] = makeHeaderLabel(labelText[i], labelWeights[i]);
                }
                // otherwise just set the label text
                else
                {
                    labels[i].setText(labelText[i]);
                }
                // see if the the alignment needs setting
                if (alignments != null && i < alignments.length)
                {
                    labels[i].setHorizontalAlignment(alignments[i]);
                }
            }
            // initialize the layout if necessary
            if (remake)
            {
                initLayout();
            }
        }
    }

    /**
     * Create this bean's layout and layout the components. This will assign a
     * weight to each label that is equivalent to the integer weights stored in
     * the labelweights array.
     */
    protected void initLayout()
    {
        removeAll();
        setLayout(new GridBagLayout());

        // create a constraints object
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weighty = 1.0;

        // if we have labels, add each one to the panel
        if (labels != null && labels.length > 0)
        {
            for (int i = 0; i < labels.length; i++)
            {
                // the actual grid weight is set to 1/10th of
                // the value stored in the weights array
                constraints.weightx = labelWeights[i] / 0.1;
                add(labels[i], constraints);
            }
        }
    }

    /**
     * Sets the alignments for the labels. The string parameter is a
     * comma-delimited list that is parsed into an array of swing alignment
     * constants.
     *
     * @param alignString the delimited list of alignments
     */
    public void setAlignments(String alignString)
    {
        // parse the string list into an array
        String[] alignList = UIUtilities.parseDelimitedList(alignString, ",");

        // if the array has values, create the alignment array
        if (alignList != null && alignList.length > 0)
        {
            int len = alignList.length;
            alignments = new int[len];

            for (int i = 0; i < len; i++)
            {
                alignments[i] = UIUtilities.getAlignmentValue(alignList[i]);
            }
        }
    }

    /**
     * get BaseHeaderBean uiClassID
     */
    @Override
    public String getUIClassID()
    {
        return uiClassID;
    }

    /**
     * Sets the property tags to use when looking up localized text properties
     * in a resource bundle. The string parameter is a comma-delimited list that
     * is parsed into an array of label tags.
     *
     * @param tagString the delimited list of label tags
     */
    public void setLabelTags(String tagString)
    {
        // parse the string list into an array
        String[] newTags = UIUtilities.parseDelimitedList(tagString, ",");

        // if the array has values, set the labeltext array
        if (newTags.length > 0)
        {
            labelTags = newTags;
        }
        updatePropertyFields();
    }

    /**
     * @return the list
     */
    public EYSList getList()
    {
        return list;
    }

    /**
     * Sets the list. Listens for if the model changes and sort the list if
     * neccessary.
     *
     * @param list the list to set
     */
    public void setList(EYSList list)
    {
        installModelListener(list);
        this.list = list;
        sortList();
    }

    /**
     * @return the labels
     */
    public JLabel[] getLabels()
    {
        return labels;
    }

    /**
     * @param labels the labels to set
     */
    public void setLabels(JLabel[] labels)
    {
        this.labels = labels;
    }

    /**
     * @return the iconAsc
     */
    public ImageIcon getIconAscending()
    {
        return iconAscending;
    }

    /**
     * @param iconAsc the iconAsc to set
     */
    public void setIconAscending(ImageIcon iconAscending)
    {
        this.iconAscending = iconAscending;
    }

    /**
     * @return the iconDesc
     * @since 14.1
     */
    public ImageIcon getIconDescending()
    {
        return iconDescending;
    }

    /**
     * @param iconDesc the iconDesc to set
     * @since 14.1
     */
    public void setIconDescending(ImageIcon iconDescending)
    {
        this.iconDescending = iconDescending;
    }

    /**
     * @return the columnSortProperties
     * @since 14.1
     */
    public String[] getColumnSortProperties()
    {
        return columnSortProperties;
    }

    /**
     * @param columnSortProperties the columnSortProperties to set
     * @since 14.1
     */
    public void setColumnSortProperties(String[] columnSortProperties)
    {
        this.columnSortProperties = columnSortProperties;
    }
    
    /**
     * @return defaultSortedColumn the column name sorted by default
     * @since 14.1
     */
    public String getDefaultSortedColumn()
    {
        return defaultSortedColumn;
    }
    
    /**
     * @param defaultSortedColumn the defaultSortedColumn to set
     * @since 14.1
     */
    public void setDefaultSortedColumn(String defaultSortedColumn)
    {
        this.defaultSortedColumn = defaultSortedColumn;
    }

    /**
     * @return the sortOrder
     */
    public SortOrder getSortOrder()
    {
        return sortOrder;
    }

    /**
     * @return the sortedColumn
     */
    public int getSortedColumn()
    {
        return sortedColumn;
    }

    /**
     * Sets the sort order and which "column" is being sorted. Fires "sortOrder"
     * property change.
     *
     * @param sortOrder the sortOrder to set
     * @param sortedColumn an index that corresponds to  the "column" in {@link #getLabels()}.
     */
    public void setSortOrder(SortOrder sortOrder, int sortedColumn)
    {
        SortOrder oldValue = this.sortOrder;
        this.sortOrder = sortOrder;
        this.sortedColumn = sortedColumn;
        firePropertyChange("sortOrder", oldValue, sortOrder);
    }

    /**
     * This method get the beanProperty at the index specified, if
     * {@link #getColumnSortProperties()} is not null and the index is in bounds.
     * Also, if the column property is set to the word "null", null is returned.
     * 
     * @param columnIndex
     * @return
     * @since 14.1
     */
    public String getColumnSortProperty(int columnIndex)
    {
        if (getColumnSortProperties() != null && getColumnSortProperties().length > columnIndex)
        {
            String columnProperty = getColumnSortProperties()[columnIndex];
            return "null".equals(columnProperty)? null : columnProperty;
        }

        return null;
    }

    /**
     * Sets the text to be used in the labels. The text is a comma-delimited
     * string that is parsed into an array of label text.
     *
     * @param textString the delimited list of label text
     */
    public void setLabelText(String textString)
    {
        // parse the string list into an array
        String[] newText = UIUtilities.parseDelimitedList(textString, ",");

        // if the array has values, set the labeltext array
        if (newText != null && newText.length > 0)
        {
            labelText = newText;
        }
    }

    /**
     * Sets the space that each label will take up. The weights should be sent
     * in as a string representing a comma-delimited list of numbers. The
     * integer given for each label position is approximately the percentage of
     * the header area that the label will take up. The sum of the weight list
     * should be 100.
     *
     * @param weightString the delimited list of label weights
     */
    public void setLabelWeights(String weightString)
    {
        int[] tempList = UIUtilities.getIntArrayFromString(weightString);

        if (tempList != null)
        {
            labelWeights = tempList;
        }
    }

    /**
     * Sets the text of one of the labels.
     *
     * @param index the index of the label
     * @param text the new text for the label
     */
    public void setOneLabel(int index, String text)
    {
        if (labelText != null && index < labelText.length)
        {
            labelText[index] = text;
        }
        if (labels != null && index < labels.length)
        {
            labels[index].setText(text);
        }
    }

    /**
     * Set the properties to be used by this bean.
     *
     * @param props the properties object
     */
    public void setProps(Properties props)
    {
        this.props = props;
        updatePropertyFields();
    }

    /**
     * Sort the list's model by the {@link #sortOrder} if column properties have
     * been set. Assumes {@link #getSortedColumn()} is with in the index of
     * {@link #getLabels()}.
     * <p>
     * Uses {@link BeanPropertyComparator}.
     * 
     * @since 14.1
     */
    public void sortList()
    {
        // sort the model if needed.
        if (getSortOrder() != SortOrder.UNSORTED)
        {
            String beanProperty = getColumnSortProperty(getSortedColumn());
            if (beanProperty != null && getList().getModel() instanceof POSListModel)
            {
                logger.debug("Sorting model...");
                POSListModel model = (POSListModel)getList().getModel();
                model.sort(new BeanPropertyComparator(beanProperty, getSortOrder()));

                // repaint the sorted list
                getList().repaint();
            }
        }
    }

    /**
     * Returns default display string.
     *
     * @return String representation of object
     */
    public String toString()
    {
        StringBuilder builder = new StringBuilder( Util.getSimpleClassName(getClass()));
        builder.append("(Revision ").append(getRevisionNumber());
        builder.append(") @").append(hashCode());
        return builder.toString();

    }

    /**
     * Retrieves a localized text string from the resource bundle.
     *
     * @param tag a property bundle tag for localized text
     * @param defaultText a plain text value to use as a default
     * @return a localized text string
     */
    protected String retrieveText(String tag, String defaultText)
    {
        String result = defaultText;

        if (props != null && tag != null)
        {
            result = props.getProperty(tag, defaultText);
        }

        return result;
    }

    /**
     * Update the fields based on the properties.
     */
    protected void updatePropertyFields()
    {
        if (props != null && labelTags != null)
        {
            StringBuffer labelString = new StringBuffer();

            String tag = null;
            String defaultLabelText = null;
            for (int i = 0; i < labelTags.length; i++)
            {
                // append comma to each entry (except the last one)
                if (i > 0)
                {
                    labelString.append(",");
                }

                tag = labelTags[i];
                if (labelText != null && labelText.length > i)
                {
                    defaultLabelText = labelText[i];
                }
                else
                {
                    defaultLabelText = "";
                }
                // retrieve new tag
                labelString.append(retrieveText(tag, defaultLabelText));
            }
            // set new labels
            setLabelText(labelString.toString());
        }
    }

    /**
     * Creates a label from the provided text string and sets its preferred size
     * based upon the parent's width and the preferred {@link #labelHeight}.
     *
     * @param text the text for the label
     * @param widthWeight the weigth to
     */
    protected JLabel makeHeaderLabel(String text, int widthWeight)
    {
        // get a label from the UI Factory
        UIFactory uiFactory = UIFactory.getInstance();
        JLabel label = uiFactory.createLabel("header." + text, text, null, UI_LABEL);

        // set the preferred size based on the weight
        int parentWidth = getWidth();
        if (parentWidth <= 0)
        {
            parentWidth = headerWidth;
        }

        int prefHeight = Math.max(labelHeight, label.getPreferredSize().height);
        Dimension dim = UIUtilities.sizeFromWeight(parentWidth, widthWeight, prefHeight);

        label.setPreferredSize(dim);

        return label;
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
     * Removes model listener from old {@link #list}. Installs listener onto
     * specified newList.
     *
     * @param newList
     * @since 14.1
     */
    protected void installModelListener(EYSList newList)
    {
        // remove it from previous list
        if (this.list != null)
        {
            this.list.removePropertyChangeListener("model", modelChangeListener);
        }
        // add it to current list
        if (newList != null)
        {
            newList.addPropertyChangeListener("model", modelChangeListener);
        }
    }

    // -------------------------------------------------------------------------
    /**
     * Listener to sort the list if the model changes.
     *
     * @author cgreene
     * @since 14.0.1
     */
    protected class ModelChangeListener implements PropertyChangeListener
    {

        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            sortList();
        }
    }
}