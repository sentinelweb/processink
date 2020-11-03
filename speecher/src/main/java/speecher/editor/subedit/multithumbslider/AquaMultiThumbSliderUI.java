/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package speecher.editor.subedit.multithumbslider;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

//import com.pump.geom.ShapeBounds;

public class AquaMultiThumbSliderUI<T> extends DefaultMultiThumbSliderUI<T> {

    private static Color UPPER_GRAY = new Color(168, 168, 168);
    private static Color LOWER_GRAY = new Color(218, 218, 218);
    private static Color OUTLINE_OPACITY = new Color(0, 0, 0, 75);

    public AquaMultiThumbSliderUI(MultiThumbSlider<T> slider) {
        super(slider);
        DEPTH = 4;
        FOCUS_PADDING = 2;
        trackHighlightColor = new Color(0x3a99fc);
    }

    protected Shape getTrackOutline() {
        trackRect = calculateTrackRect();
        float k = 4;
        int z = 3;
        if (slider.getOrientation() == MultiThumbSlider.VERTICAL) {
            return new RoundRectangle2D.Float(trackRect.x, trackRect.y - z,
                    trackRect.width, trackRect.height + 2 * z, k, k);
        }
        return new RoundRectangle2D.Float(trackRect.x - z, trackRect.y,
                trackRect.width + 2 * z, trackRect.height, k, k);
    }

    @Override
    protected int getPreferredComponentDepth() {
        return 24;
    }

    @Override
    protected void paintFocus(Graphics2D g) {
        // do nothing, this is really handled in paintThumb now
    }

    @Override
    protected Dimension getThumbSize(int thumbIndex) {
        Thumb thumb = getThumb(thumbIndex);
        if (Thumb.Hourglass.equals(thumb)) {
            return new Dimension(5, 16);
        } else if (Thumb.Triangle.equals(thumb)) {
            return new Dimension(16, 18);
        } else if (Thumb.Rectangle.equals(thumb)) {
            return new Dimension(10, 20);
        } else {
            return new Dimension(16, 16);
        }
    }

    @Override
    protected void paintTrack(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Shape trackShape = getTrackOutline();

        GradientPaint gradient;
        if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
            gradient = new GradientPaint(new Point(trackRect.x, trackRect.y),
                    UPPER_GRAY, new Point(trackRect.x, trackRect.y
                    + trackRect.height), LOWER_GRAY);
        } else {
            gradient = new GradientPaint(new Point(trackRect.x, trackRect.y),
                    UPPER_GRAY, new Point(trackRect.x + trackRect.width,
                    trackRect.y), LOWER_GRAY);
        }
        g.setPaint(gradient);
        g.fill(trackShape);

        paintTrackHighlight(g);

        g.setPaint(OUTLINE_OPACITY);
        g.setStroke(new BasicStroke(1));
        g.draw(trackShape);

        if (slider.isPaintTicks()) {
            g.setColor(new Color(0x777777));
            g.setStroke(new BasicStroke(1));
            paintTick(g, .25f, 4, 9, false);
            paintTick(g, .5f, 4, 9, false);
            paintTick(g, .75f, 4, 9, false);
            paintTick(g, 0f, 4, 9, false);
            paintTick(g, 1f, 4, 9, false);
        }
    }

    @Override
    protected Rectangle calculateTrackRect() {
        Rectangle r = super.calculateTrackRect();

        // why so much dead space? I don't know. This only tries to emulate
        // what Apple is doing.
        int k = 22;
        if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
            r.x = k;
            r.width = slider.getWidth() - k * 2;
        } else {
            r.y = k;
            r.height = slider.getHeight() - k * 2;
        }
        return r;

    }

    @Override
    protected void paintThumb(Graphics2D g, int thumbIndex, float selected) {
        Shape outline = getThumbShape(thumbIndex);

        if (Thumb.Triangle.equals(getThumb(thumbIndex))) {
            if (slider.getOrientation() == MultiThumbSlider.HORIZONTAL) {
                g.translate(0, 2);
            } else {
                g.translate(2, 0);
            }
        }

        Rectangle2D thumbBounds = ShapeBounds.getBounds(outline);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        Paint fill = new LinearGradientPaint(new Point2D.Double(0,
                thumbBounds.getMinY()), new Point2D.Double(0,
                thumbBounds.getMaxY()), new float[]{0, .5f, .501f, 1},
                new Color[]{new Color(0xFFFFFF), new Color(0xF4F4F4),
                        new Color(0xECECEC), new Color(0xEDEDED)});
        g.setPaint(fill);
        g.fill(outline);

        if (mouseIsDown && thumbIndex == slider.getSelectedThumb()) {
            g.setPaint(new Color(0, 0, 0, 28));
            g.fill(outline);
        }

        if (Thumb.Triangle.equals(getThumb(thumbIndex))) {
            g.setStroke(new BasicStroke(2f));
            g.setPaint(new Color(0, 0, 0, 10));
            g.draw(outline);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_NORMALIZE);
            g.setPaint(new Color(0, 0, 0, 85));
            g.setStroke(new BasicStroke(1f));
            g.draw(outline);
        } else {
            g.setStroke(new BasicStroke(1f));
            g.setPaint(new Color(0, 0, 0, 110));
            g.draw(outline);
        }

        if (thumbIndex == slider.getSelectedThumb()) {
            Color focusColor = new Color(0xa7, 0xd5, 0xff, 240);
            PlafPaintUtils.paintFocus(g, outline, FOCUS_PADDING, focusColor);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE);
            g.setStroke(new BasicStroke(1f));
            g.setPaint(new Color(0, 0, 0, 23));
            g.draw(outline);
        }
    }
}