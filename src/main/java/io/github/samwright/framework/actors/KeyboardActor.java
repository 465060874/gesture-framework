package io.github.samwright.framework.actors;

import com.sun.glass.ui.Application;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.datatypes.Classification;
import io.github.samwright.framework.model.helper.CompletedTrainingBatch;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import io.github.samwright.framework.model.helper.XMLHelper;
import javafx.scene.input.KeyCode;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;

import java.util.*;

/**
 * User: Sam Wright Date: 13/09/2013 Time: 11:49
 */
public class KeyboardActor extends AbstractElement {

    private static com.sun.glass.ui.Robot robot = Application.GetApplication().createRobot();

    @Getter private Map<String, String> keyCodes;
    @Getter private List<String> allTags = new ArrayList<>();
    @Setter @Getter private boolean active;

    public KeyboardActor() {
        super(new TypeData(Classification.class, Classification.class));
        keyCodes = Collections.emptyMap();
        setActive(true);
    }

    public KeyboardActor(KeyboardActor oldElement) {
        super(oldElement);
        keyCodes = oldElement.getKeyCodes();
        setActive(oldElement.isActive());
    }

    @Override
    public Mediator process(Mediator input) {
        Classification classification = (Classification) input.getData();
        String keyToPress = keyCodes.get(classification.getTag());
        if (isActive() && !keyToPress.isEmpty()) {
            int key = KeyCode.getKeyCode(keyToPress).impl_getCode();
            robot.keyPress(key);
            robot.keyRelease(key);
        }

        return input.createNext(this, input.getData());
    }

    @Override
    public List<Mediator> processTrainingData(Mediator input) {
        return Arrays.asList(input.createNext(this, input.getData()));
    }

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        allTags.clear();

        Map<String,String> newKeyCodes = new HashMap<>(keyCodes);

        for (Mediator mediator : completedTrainingBatch.getAll()) {
            Classification classification = (Classification) mediator.getData();
            String tag = classification.getTag();
            allTags.add(tag);
            if (!newKeyCodes.containsKey(tag))
                newKeyCodes.put(tag, "");
        }

        this.keyCodes = Collections.unmodifiableMap(newKeyCodes);
        return super.processCompletedTrainingBatch(completedTrainingBatch);
    }

    @Override
    public KeyboardActor createMutableClone() {
        return new KeyboardActor(this);
    }

    public KeyboardActor withKeyCodes(Map<String, String> keyCodes) {
        if (isMutable()) {
            this.keyCodes = Collections.unmodifiableMap(keyCodes);
            return this;
        } else {
            return createMutableClone().withKeyCodes(keyCodes);
        }
    }

    @Override
    public boolean isValid() {
        return robot != null && keyCodes.keySet().containsAll(allTags);
    }

    @Override
    public KeyboardActor withXML(org.w3c.dom.Element node, Map<UUID, Processor> map) {
        if (!isMutable())
            return createMutableClone().withXML(node, map);

        super.withXML(node, map);
        org.w3c.dom.Element keyCodesNode = XMLHelper.getFirstChildWithName(node, "KeyMap");
        Map<String, String> keyCodes = new HashMap<>();

        for (org.w3c.dom.Element entryNode : XMLHelper.iterator(keyCodesNode)) {
            String tag = XMLHelper.getDataUnderNode(entryNode, "Tag");
            String key = XMLHelper.getDataUnderNode(entryNode, "Key");
            keyCodes.put(tag, key);
        }
        withKeyCodes(keyCodes);

        return this;
    }

    @Override
    public org.w3c.dom.Element getXMLForDocument(Document doc) {
        org.w3c.dom.Element node = super.getXMLForDocument(doc);
        org.w3c.dom.Element keyCodesNode = doc.createElement("KeyMap");
        node.appendChild(keyCodesNode);

        for (Map.Entry<String,String> entry : keyCodes.entrySet()) {
            String tag = entry.getKey();
            String key = entry.getValue();

            org.w3c.dom.Element entryNode = doc.createElement("Entry");
            keyCodesNode.appendChild(entryNode);

            XMLHelper.addDataUnderNode(entryNode, "Tag", tag);
            XMLHelper.addDataUnderNode(entryNode, "Key", key);
        }

        return node;
    }
}
