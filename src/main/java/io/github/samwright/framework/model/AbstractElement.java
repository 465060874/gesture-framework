package io.github.samwright.framework.model;

import io.github.samwright.framework.model.common.ElementObserver;
import io.github.samwright.framework.model.helper.*;
import lombok.Delegate;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.*;

/**
 * Abstract implementation of {@link Element} for elemental processors to extend,
 * which requires a one-to-one mapping of input data to output data (without training necessary).
 * <p/>
 * Concrete {@code Element} implementations can derive from this to let it handle its parent
 * {@link Workflow}, its {@link TypeData}, mutation management, and
 * {@link io.github.samwright.framework.model.common.ElementObserver} management).
 */
public abstract class AbstractElement implements Element {
    @Getter private Set<ElementObserver> observers;
    private final ParentManager<Element, Workflow> parentManager;

    @Delegate(excludes = MutabilityHelper.ForManualDelegation.class)
    private final MutabilityHelper<Element> mutabilityHelper;

    @Delegate
    private final TypeDataManager<Element> typeDataManager;

    private Set<UUID> observerUUIDs;
    private Map<UUID,Processor> dictionary;

    /**
     * Constructs the initial (and immutable) {@code AbstractElement}.
     */
    public AbstractElement() {
        typeDataManager = new TypeDataManager<Element>(this);
        mutabilityHelper = new MutabilityHelper<Element>(this, false);
        observers = Collections.emptySet();
        parentManager = new ParentManager<Element, Workflow>(this);
    }

    /**
     * Constructs a mutable clone of the given {@code AbstractElement}.
     *
     * @param oldElement the {@code AbstractElement} to clone.
     */
    public AbstractElement(AbstractElement oldElement) {
        if (oldElement.isMutable())
            throw new RuntimeException("Cannot clone a mutable object");

        typeDataManager = new TypeDataManager<Element>(this, oldElement.getTypeData());
        mutabilityHelper = new MutabilityHelper<Element>(this, true);
        this.observers = oldElement.getObservers();
        parentManager = new ParentManager<Element, Workflow>(this, oldElement.getParent());
    }

    @Override
    public Element withParent(Workflow newParent) {
        return parentManager.withParent(newParent);
    }

    @Override
    public Workflow getParent() {
        return parentManager.getParent();
    }

    @Override
    public void delete() {
        parentManager.delete();
    }

    @Override
    public void discardNext() {
        mutabilityHelper.discardNext();
        parentManager.discardNext();
    }

    @Override
    public void discardPrevious() {
        mutabilityHelper.discardPrevious();
        parentManager.discardPrevious();
    }

    @Override
    public void setAsCurrentVersion() {
        if (!isCurrentVersion()) {
            mutabilityHelper.setAsCurrentVersion();
            parentManager.setAsCurrentVersion();
        }
    }

    @Override
    public void fixAsVersion(VersionInfo versionInfo) {
        setBeingFixed();
        parentManager.beforeFixAsVersion(versionInfo);
        mutabilityHelper.fixAsVersion(versionInfo);
    }

    @Override
    public void afterVersionFixed() {
        if (this.dictionary == null) {
            observers = new HashSet<>(observers);
            VersionInfo.updateAllToLatest(observers);
        } else {
            for (UUID uuid : observerUUIDs) {
                Processor observer = dictionary.get(uuid);
                if (observer == null)
                    ModelLoader.getProcessor(uuid);
                if (observer != null)
                    observers.add((ElementObserver) observer);
            }
            this.dictionary = null;
        }
    }

    @Override
    public Element withObservers(Set<ElementObserver> newObservers) {
        if (isMutable()) {
            observers = Collections.unmodifiableSet(newObservers);
            return this;
        } else {
            return createMutableClone().withObservers(newObservers);
        }
    }

    @Override
    public CompletedTrainingBatch processCompletedTrainingBatch(CompletedTrainingBatch completedTrainingBatch) {
        Set<Mediator> allOutputs = completedTrainingBatch.getAll();

        Set<Mediator> successfulOutputs = completedTrainingBatch.getSuccessful();

        Set<Mediator> allInputs = Mediator.rollbackMediators(allOutputs);
        Set<Mediator> successfulInputs = Mediator.rollbackMediators(successfulOutputs);

        return new CompletedTrainingBatch(allInputs, successfulInputs);
    }

    @Override
    public org.w3c.dom.Element getXMLForDocument(Document doc) {
        org.w3c.dom.Element observerNode, node = mutabilityHelper.getXMLForDocument(doc);

        Node observersNode = doc.createElement("Observers");
        node.appendChild(observersNode);

        for (ElementObserver observer : observers)
            if (observer instanceof Processor) {
                observerNode = doc.createElement("observer");
                UUID observerUUID = ((Processor) observer).getUUID();
                observerNode.setAttribute("UUID", observerUUID.toString());
                observersNode.appendChild(observerNode);
            }

        return node;
    }

    @Override
    public Element withXML(org.w3c.dom.Element node, Map<UUID, Processor> map) {
        if (!isMutable())
            return (Element) createMutableClone().withXML(node, map);

        this.dictionary = map;
        withParent(null);

        observerUUIDs = new HashSet<>();
        org.w3c.dom.Element observersNode = XMLHelper.getFirstChildWithName(node, "Observers");

        for (org.w3c.dom.Element observerNode : XMLHelper.iterator(observersNode)) {
            String uuidString = observerNode.getAttribute("UUID");
            observerUUIDs.add(UUID.fromString(uuidString));
        }

        return ((Element) mutabilityHelper.withXML(node, map))
                .withTypeData(getTypeData().withXML(node, map));
    }

    @Override
    public String toString() {
        String fullString = super.toString();
        return getClass().getSimpleName() + fullString.substring(fullString.length() - 4);
    }

    @Override
    public String getModelIdentifier() {
        return mutabilityHelper.getModelIdentifier();
    }
}