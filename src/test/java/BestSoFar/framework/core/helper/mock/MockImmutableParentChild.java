package BestSoFar.framework.core.helper.mock;

import BestSoFar.framework.core.common.ChildOf;
import BestSoFar.framework.core.common.ParentOf;
import BestSoFar.framework.core.helper.ChildrenManager;
import BestSoFar.framework.core.helper.ParentManager;
import BestSoFar.framework.core.helper.VersionInfo;
import lombok.Getter;

import java.util.List;

/**
 * User: Sam Wright Date: 12/07/2013 Time: 13:11
 */
public class MockImmutableParentChild extends MockEventuallyImmutable
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
        super(true);
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
    public void fixAsVersion(VersionInfo versionInfo) {
        if (isMutable()) {
            parentManager.beforeFixAsVersion(versionInfo);
            childrenManager.beforeFixAsVersion(versionInfo);
        }

        super.fixAsVersion(versionInfo);
    }

    @Override
    public void delete() {
        super.delete();
        parentManager.afterDelete();
        childrenManager.afterDelete();
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
}
