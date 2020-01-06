package bandit;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.EmptyIcon;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BanditConfigurationPanel implements SearchableConfigurable {
    private final ProjectService state;
    private final Project project;

    private TextFieldWithHistoryWithBrowseButton banditExecutableField;
    private TextFieldWithHistoryWithBrowseButton banditConfigField;
    private JRadioButton searchForConfigRadioButton;
    private JRadioButton useSpecificConfigRadioButton;
    private JPanel rootPanel;
    private JBList ruleList;
    private CollectionListModel<RuleItem> ruleListModel;
    private List<String> baseRules;


    public BanditConfigurationPanel(@NotNull Project project) {
        this.state = ProjectService.getInstance(project).getState();
        this.project = project;
        this.ruleListModel = new CollectionListModel<>();
    }

    @NotNull
    @Override
    public String getId() {
        return "bandit";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Bandit";
    }

    public List<RuleItem> loadRulesFile() throws ParserConfigurationException, IOException, SAXException {
        ArrayList<RuleItem> rules = new ArrayList<>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("rules.xml");
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(in);
        NodeList doc_rules = doc.getElementsByTagName("rule");
        for (int i=0; i < doc_rules.getLength(); i++) {
            NamedNodeMap attrib = doc_rules.item(i).getAttributes();
            String code = attrib.getNamedItem("code").getNodeValue();
            String label = attrib.getNamedItem("label").getNodeValue();
            rules.add(new RuleItem(code, label));
        }
        return rules;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        this.ruleList.setModel(this.ruleListModel);
        try {
            this.ruleListModel.addAll(0, loadRulesFile());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        this.ruleList.setCellRenderer(new ColoredListCellRenderer() {
            @Override
            protected void customizeCellRenderer(@NotNull JList list, Object value, int index, boolean selected, boolean hasFocus) {
                append(((RuleItem)value).AsString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                setIcon(EmptyIcon.ICON_16);
            }
        });
        return rootPanel;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    public class RuleItem {
        protected String code;
        protected String label;

        public RuleItem(String code, String label){
            this.code = code;
            this.label = label;
        }

        public String AsString(){
            return this.code + " : " + this.label;
        }
    }
}
