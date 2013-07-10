package BestSoFar.framework.core.helper.mock;

import BestSoFar.framework.core.common.EventuallyImmutable;
import BestSoFar.framework.core.helper.MutabilityHelper;
import lombok.Delegate;
import lombok.Getter;

/**
 * User: Sam Wright Date: 10/07/2013 Time: 13:00
 */
public class MockEventuallyImmutable implements EventuallyImmutable {

    private static interface ToExclude {
        EventuallyImmutable createMutableClone();
    }

    @Delegate(excludes = ToExclude.class)
    @Getter private MutabilityHelper mutabilityHelper;

    public MockEventuallyImmutable() {
        this(false);
    }

    private MockEventuallyImmutable(boolean mutable) {
        this.mutabilityHelper = new MutabilityHelper(this, mutable);
    }

    @Override
    public MockEventuallyImmutable createMutableClone() {
        return new MockEventuallyImmutable(true);
    }

}
