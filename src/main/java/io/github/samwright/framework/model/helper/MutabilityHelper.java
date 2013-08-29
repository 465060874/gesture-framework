package io.github.samwright.framework.model.helper;

import io.github.samwright.framework.controller.ModelController;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.Workflow;
import io.github.samwright.framework.model.WorkflowContainer;
import io.github.samwright.framework.model.common.ChildOf;
import io.github.samwright.framework.model.common.EventuallyImmutable;
import io.github.samwright.framework.model.common.HasUUID;
import io.github.samwright.framework.model.common.Replaceable;
import lombok.Getter;
import lombok.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.UUID;

/**
 * A helper object that manages an {@link EventuallyImmutable} object (which delegates to this).
 */
public class MutabilityHelper<T extends EventuallyImmutable>
        implements EventuallyImmutable, HasUUID {
    private static final Object[] writeLock = new Object[0];
    private static MutabilityHelper mutationStarter;

    private VersionInfo<T> versionInfo;
    @Getter private boolean mutable, deleted, beingFixed;
    @Getter private ModelController<T> controller;
    private UUID uuid = ModelLoader.makeNewUUID();

    public static interface ForManualDelegation {
        void fixAsVersion(VersionInfo versionInfo);
        void delete();
        Processor createMutableClone();
        void discardNext();
        void discardPrevious();
        void setAsCurrentVersion();
        void afterVersionFixed();
        Node getXMLForDocument(Document doc);
        Processor withXML(Element node, Map<UUID, Processor> dictionary);
        String getModelIdentifier();
    }

    /**
     * Construct a new {@code MutabilityHelper} to manage the given {@link Processor}
     * with the given mutability.
     *
     * @param thisImmutable the object for this to manage.
     * @param mutable the initial mutability of the object to manage.
     */
    public MutabilityHelper(@NonNull T thisImmutable, boolean mutable) {
        versionInfo = VersionInfo.createForFirst(thisImmutable);
        this.mutable = mutable;
        deleted = false;
        beingFixed = false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setController(ModelController controller) {
        synchronized (writeLock) {
            if (this.controller != controller) {
                EventuallyImmutable next = versionInfo.getNext();
                this.controller = controller;
                if (next != null) {
                    next.setController(controller);
                } else if (controller != null) {
                    T model = versionInfo.getThisVersion();
                    controller.setModel(model);
                }
            }
        }
    }

    @Override
    public T createMutableClone() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public VersionInfo<T> versionInfo() {
        synchronized (writeLock) {
            return versionInfo;
        }
    }

    @Override
    public void setBeingFixed() {
        synchronized (writeLock) {
            beingFixed = true;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fixAsVersion(@NonNull VersionInfo versionInfo) {
        synchronized (writeLock) {
            startMutation();

            this.versionInfo = versionInfo;
            this.mutable = false;
            this.beingFixed = false;
            if (versionInfo.getPrevious() != null) {
                setController(versionInfo.getPrevious().getController());
                if (versionInfo.getPrevious() instanceof Processor)
                    setUUID(((Processor) versionInfo.getPrevious()).getUUID());
            }

            setAsCurrentVersion();
            pauseMutationIfThisStartedIt();
        }

        endMutationIfPaused();
    }

    @Override
    public void afterVersionFixed() {
        // Dummy implementation
    }

    @Override
    @SuppressWarnings("unchecked")
    public void replaceWith(@NonNull Replaceable replacement) {
        synchronized (writeLock) {
            if (versionInfo.getThisVersion() == replacement)
                throw new RuntimeException("SKIPPING REPLACEMENT!! Tried replacing with self: " + replacement);

            startMutation();

            if (isMutable())
                throw new RuntimeException("Cannot replace a mutable object - fix this first");
            if (isDeleted())
                throw new RuntimeException("Cannot replace a deleted object - discard first.");
            if (replacement.versionInfo().getPrevious() != null)
                throw new RuntimeException("Replacement has already replaced something else");

            if (versionInfo.getNext() != null)
                versionInfo.getThisVersion().discardNext();

            versionInfo = versionInfo.withNext((T) replacement);
            VersionInfo<T> nextVersionInfo =
                    replacement.versionInfo().withPrevious(versionInfo.getThisVersion());

            ((T) replacement).fixAsVersion(nextVersionInfo);

            if (pauseMutationIfThisStartedIt())
                getTopModel().afterVersionFixed();
        }

        endMutationIfPaused();
    }

    private void notifyTopController() {
        EventuallyImmutable topModel = getTopModel();
        System.out.println("Notifying top controller: " + topModel);
        if (topModel.getController() != null)
            topModel.getController().handleUpdatedModel();
    }

    private EventuallyImmutable getTopModel() {
        EventuallyImmutable parent = versionInfo.getThisVersion();

        while (parent instanceof ChildOf && ((ChildOf) parent).getParent() != null)
            parent = (EventuallyImmutable) ((ChildOf) parent).getParent();

        return parent;
    }

    private void startMutation() {
        if (mutationStarter == null)
            mutationStarter = this;
    }

    private boolean pauseMutationIfThisStartedIt() {
        if (mutationStarter == this) {
            mutationStarter = null;
            return true;
        }
        return false;
    }

    private void endMutationIfPaused() {
        if (!Thread.holdsLock(writeLock)) {
            notifyTopController();
        } else if (mutationStarter == null) {
            mutationStarter = this;
        }
    }

    @Override
    public void discardNext() {
        synchronized (writeLock) {
            startMutation();

            if (versionInfo.getNext() != null) {
                T oldNext = versionInfo.getNext();
                versionInfo = versionInfo.withNext(null);
                oldNext.discardPrevious();
            }
            deleted = false;
            pauseMutationIfThisStartedIt();

            versionInfo.getThisVersion().setAsCurrentVersion();
        }

        endMutationIfPaused();
    }

    @Override
    public void discardPrevious() {
        synchronized (writeLock) {
            if (versionInfo.getPrevious() != null) {
                startMutation();
                T oldPrevious = versionInfo.getPrevious();
                versionInfo = versionInfo.withPrevious(null);
                if (versionInfo.getNext() == null) {
                    setUUID(ModelLoader.makeNewUUID());
                    ModelController oldPreviousController = oldPrevious.getController();
                    if (oldPreviousController == null)
                        setController(null);
                    else
                        setController(oldPreviousController.createClone());
                    versionInfo.getThisVersion().setAsCurrentVersion();
                }
                oldPrevious.discardNext();
                pauseMutationIfThisStartedIt();
            }
        }

        endMutationIfPaused();
    }

    @Override
    public void delete() {
        synchronized (writeLock) {
            if (versionInfo.getNext() != null)
                throw new RuntimeException("Cannot delete - already been replaced.");
            if (isDeleted())
                throw new RuntimeException("Already been deleted.");

            deleted = true;
        }
    }

    public Element getXMLForDocument(Document doc) {
        if (!(versionInfo.getThisVersion() instanceof Processor))
            throw new RuntimeException("Can only get XML of Processor");
        Processor thisProcessor = (Processor) versionInfo.getThisVersion();

        String processorString;
        if (thisProcessor instanceof Workflow)
            processorString = "Workflow";
        else if (thisProcessor instanceof WorkflowContainer)
            processorString = "WorkflowContainer";
        else if (thisProcessor instanceof Element)
            processorString = "Element";
        else
            processorString = "Processor";

        Element element = doc.createElement(processorString);
        element.setAttribute("model", thisProcessor.getModelIdentifier());
        element.setAttribute("UUID", thisProcessor.getUUID().toString());
        element.appendChild(thisProcessor.getTypeData().getXMLForDocument(doc));

        return element;
    }

    public Processor withXML(Element node, Map<UUID, Processor> dictionary) {
        System.out.println("Mutability.withXML for " + versionInfo.getThisVersion());
        if (!(versionInfo.getThisVersion() instanceof Processor))
            throw new RuntimeException("Can only use XML on Processor");
        Processor thisProcessor = (Processor) versionInfo.getThisVersion();

        String uuidString = node.getAttribute("UUID");
        dictionary.put(UUID.fromString(uuidString), thisProcessor);
        return thisProcessor;
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(UUID uuid) {
        if (!this.uuid.equals(uuid)) {
            this.uuid = uuid;
            EventuallyImmutable next = versionInfo.getNext();
            if (next != null && next instanceof HasUUID)
                ((HasUUID) next).setUUID(uuid);
        }
    }

    @Override
    public String getModelIdentifier() {
        return versionInfo.getThisVersion().getClass().getName();
    }

    @Override
    public void setAsCurrentVersion() {
        synchronized (writeLock) {
            startMutation();

            if (versionInfo.getThisVersion() instanceof Processor) {
                if (controller != null)
                    controller.setModel(versionInfo.getThisVersion());
                ModelLoader.registerProcessor((Processor) versionInfo.getThisVersion());
            }
            pauseMutationIfThisStartedIt();
        }

        endMutationIfPaused();
    }

    @Override
    public boolean isCurrentVersion() {
        return ModelLoader.getProcessor(getUUID()) == versionInfo.getThisVersion();
    }
}