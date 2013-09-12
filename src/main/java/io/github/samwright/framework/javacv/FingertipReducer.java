package io.github.samwright.framework.javacv;

import io.github.samwright.framework.javacv.helper.Fingertips;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.CvPoint;

/**
 * User: Sam Wright Date: 11/09/2013 Time: 18:02
 */
public class FingertipReducer extends AbstractElement {

    @Getter private int minFingerDepth, maxFingerAngle;

    public FingertipReducer() {
        super(new TypeData(Fingertips.class, Fingertips.class));
        minFingerDepth = 15;
        maxFingerAngle = 150;
    }

    public FingertipReducer(FingertipReducer oldElement) {
        super(oldElement);
        minFingerDepth = oldElement.getMinFingerDepth();
        maxFingerAngle = oldElement.getMaxFingerAngle();
    }

    @Override
    public Mediator process(Mediator input) {
        Fingertips allPoints = (Fingertips) input.getData();
        List<CvPoint> newTips = new ArrayList<>();
        List<CvPoint> newFolds = new ArrayList<>();
        List<Float> newDepths = new ArrayList<>();

        int numPoints = allPoints.getTips().size();

        for (int i = 0; i < numPoints; ++i) {
            if (allPoints.getDepths().get(i) < minFingerDepth)
                continue;

            // look at fold points on either side of a tip
            int pdx = (i == 0) ? (numPoints - 1) : (i - 1); // predecessor of i
            int sdx = (i == numPoints - 1) ? 0 : (i + 1);   // successor of i

            int angle = angleBetween(
                    allPoints.getTips().get(i),
                    allPoints.getFolds().get(pdx),
                    allPoints.getFolds().get(sdx)
            );

            if (angle >= maxFingerAngle)
                continue;      // angle between finger and folds too wide

            newTips.add(allPoints.getTips().get(i));
            newFolds.add(allPoints.getFolds().get(i));
            newDepths.add(allPoints.getDepths().get(i));
        }

        return input.createNext(this, new Fingertips(newTips, newFolds, newDepths, allPoints.getSourceTaggedImage()));
    }

    @Override
    public Element createMutableClone() {
        return new FingertipReducer(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    private int angleBetween(CvPoint tip, CvPoint next, CvPoint prev) {
        return Math.abs((int) Math.round(
                Math.toDegrees(
                        Math.atan2(next.x() - tip.x(), next.y() - tip.y()) -
                                Math.atan2(prev.x() - tip.x(), prev.y() - tip.y()))));
    }
}
