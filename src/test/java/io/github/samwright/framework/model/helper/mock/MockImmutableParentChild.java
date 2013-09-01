package io.github.samwright.framework.model.helper.mock;

import io.github.samwright.framework.model.common.ChildOf;
import io.github.samwright.framework.model.common.ParentOf;
import io.github.samwright.framework.model.common.Replaceable;
import io.github.samwright.framework.model.helper.ChildrenManager;
import io.github.samwright.framework.model.helper.ParentManager;
import lombok.Getter;

import java.util.List;

/**
 * User: Sam Wright Date: 12/07/2013 Time: 13:11
 */
public class MockImmutableParentChild extends MockProcessor
        implements ChildOf<MockImmutableParentChild>, ParentOf<MockImmutableParentChild> {

    @Getter
    private final ParentManager<MockImmutableParentChild, MockImmutableParentChild> parentManager;
    @Getter
    private final ChildrenManager<MockImmutableParentChild, MockImmutableParentChild> childrenManager;

    public MockImmutableParentChild() {
        childrenManager = new ChildrenManager<>(this);
        parentManager = new ParentManager<>(this);
    }

    public MockImmutableParentChild(MockImmutableParentChild previous) {
        super(previous);
        childrenManager = new ChildrenManager<>(this, previous.getChildren());
        parentManager = new ParentManager<>(this, previous.getParent());
    }

    @Override
    public MockImmutableParentChild getParent() {
        return parentManager.getParent();
    }

    @Override
    public MockImmutableParentChild withParent(MockImmutableParentChild newParent) {
        return parentManager.withParent(newParent);
    }

    @Override
    public List<MockImmutableParentChild> getChildren() {

        return childrenManager.getChildren();
    }

    @Override
    public MockImmutableParentChild withChildren(List<MockImmutableParentChild> newChildren) {
        return childrenManager.withChildren(newChildren);
    }

    @Override
    public MockImmutableParentChild createMutableClone() {
        return new MockImmutableParentChild(this);
    }

    @Override
    public void replace(Replaceable toReplace) {
        parentManager.beforeReplacing((MockImmutableParentChild) toReplace);
        childrenManager.beforeReplacing((MockImmutableParentChild) toReplace);

        super.replace(toReplace);
    }

    @Override
    public void delete() {
        super.delete();
        parentManager.orphanChild();
    }

    @Override
    public void discardPrevious() {
        super.discardPrevious();
        childrenManager.discardPrevious();
    }

    @Override
    public void discardNext() {
        super.discardNext();
        childrenManager.discardNext();
    }

    @Override
    public void afterReplacement() {
        childrenManager.afterReplacement();
    }
}
