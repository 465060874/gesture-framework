package BestSoFar.framework.core;

import BestSoFar.framework.core.helper.ChildrenManager;
import BestSoFar.framework.core.helper.ParentManager;
import BestSoFar.framework.core.helper.TypeData;
import BestSoFar.framework.immutables.ImmutableVersion;
import BestSoFar.framework.immutables.common.EventuallyImmutable;
import lombok.Delegate;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract implementation of Workflow.  Manages parent, the list of elements in the workflow,
 * typedata, and mutation management.
 */
public abstract class AbstractWorkflow<I, O> implements Workflow<I, O> {
    @Getter @NonNull private final TypeData<I, O> typeData;
    @Getter private ImmutableVersion version;
    @Getter private boolean mutable, deleted;
    private final ParentManager<Workflow<I, O>, WorkflowContainer<I, O>> parentManager;
    private final ChildrenManager<Element<?, ?>, Workflow<?, ?>> childrenManager;

    {
        deleted = false;
        version = new ImmutableVersion(this);
        parentManager = new ParentManager<>((Workflow<I, O>) this);
    }

    public AbstractWorkflow(TypeData<I, O> typeData) {
        this.typeData = typeData;
        mutable = false;
        childrenManager = new ChildrenManager<Element<?, ?>, Workflow<?, ?>>(this);
    }

    @SuppressWarnings("unchecked")
    public AbstractWorkflow(AbstractWorkflow<I, O> oldWorkflow, TypeData<I, O> typeData) {
        this.typeData = typeData;
        mutable = true;
        childrenManager = new ChildrenManager<Element<?, ?>, Workflow<?, ?>>(this, oldWorkflow.getChildren());
    }

    @Override
    public WorkflowContainer<I, O> getParent() {
        return parentManager.getParent();
    }

    @Override
    public List<Element<?, ?>> getChildren() {
        return childrenManager.getChildren();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Workflow<I, O> withChildren(List<Element<?, ?>> newChildren) {
        return (Workflow<I, O>) childrenManager.withChildren(newChildren);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Workflow<I, O> withParent(WorkflowContainer<I, O> parent) {
        if (parent != null && !typeData.equals(getParent().getTypeData())) {
            String msg = String.format(
                    "Workflow%s must have same type data as WorkflowContainer%s",
                    typeData.toString(),
                    getParent().getTypeData().toString()
            );
            throw new ClassCastException(msg);
        }

        return parentManager.withParent(parent);
    }

    @Override
    abstract public AbstractWorkflow<I, O> createMutableClone();

    @Override
    public void delete() {
        deleted = true;
        parentManager.delete();
        childrenManager.delete();
    }

    @Override
    public void replaceWith(EventuallyImmutable proposed) {
        if (this == proposed)
            return;

        ImmutableVersion nextVersion = proposed.getVersion().withPrevious(this);
        version = getVersion().withNext(proposed);
        proposed.finalise(nextVersion);
    }

    @Override
    public void finalise(ImmutableVersion version) {
        this.version = version;
        parentManager.finalise(version);
        childrenManager.finalise(version);
        mutable = false;
    }
}
