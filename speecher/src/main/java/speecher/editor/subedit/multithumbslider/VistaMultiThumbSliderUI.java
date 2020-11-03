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

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class VistaMultiThumbSliderUI<T> extends DefaultMultiThumbSliderUI<T> {

    public VistaMultiThumbSliderUI(MultiThumbSlider<T> slider) {
        super(slider);
        DEPTH = 4;
        FOCUS_PADDING = 2;
        trackHighlightColor = new Color(0x3a99fc);
    }

    @Override
    protected int getPreferredComponentDepth() {
        return 22;
    }

    @Override
    protected void paintFocus(Graphics2D g) {
        // do nothing, this is really handled in paintThumb now
    }

    @Override
    protected Dimension getThumbSize(int thumbIndex) {
        Thumb thumb = getThumb(thumbIndex);
        if (Thumb.Hourglass.equals(thumb)) {
            return new Dimension(8, 16);
        } else if (Thumb.Triangle.equals(thumb)) {
            return new Dimension(9, 18);
        } else if (Thumb.Rectangle.equals(thumb)) {
            return new Dimension(4, 16);
        } else {
            return new Dimension(16, 16);
        }
    }

    @Override
    protected void paintTrack(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Shape trackShape = getTrackOutline();

        Paint fill = new Color(0xe7eaea);
        g.setPaint(fill);
        g.fill(trackShape);
        g.setPaint(new Color(0, 0, 0, 16));
        g.drawLine(trackRect.x, trackRect.y, trackRect.x + trackRect.width,
                trackRect.y);
        g.drawLine(trackRect.x, trackRect.y, trackRect.x, trackRect.y
                + trackRect.height);
        g.drawLine(trackRect.x + trackRect.width, trackRect.y, trackRect.x
                + trackRect.width, trackRect.y + trackRect.height);
        g.setPaint(new Color(255, 255, 255, 16));
        g.drawLine(trackRect.x, trackRect.y + trackRect.height, trackRect.x
                + trackRect.width, trackRect.y + trackRect.height);

        paintTrackHighlight(g);

        if (slider.isPaintTicks()) {
            g.setColor(new Color(0, 0, 0, 40));
            g.setStroke(new BasicStroke(1));
            paintTick(g, .25f, 4, 8, false);
            paintTick(g, .5f, 4, 8, false);
            paintTick(g, .75f, 4, 8, false);
            paintTick(g, 0f, 4, 8, false);
            paintTick(g, 1f, 4, 8, false);
        }
    }

    @Override
    protected Shape getTrackOutline() {
        trackRect = calculateTrackRect();
        return trackRect;
    }

    @Override
    protected void paintThumb(Graphics2D g, int thumbIndex, float selected) {
        Shape outline = getThumbShape(thumbIndex);

        Rectangle2D thumbBounds = ShapeBounds.getBounds(outline);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        Paint fill;
        Paint strokePaint;
        if (mouseIsDown && thumbIndex == slider.getSelectedThumb()) {
            fill = new LinearGradientPaint(new Point2D.Double(0,
                    thumbBounds.getMinY()), new Point2D.Double(0,
                    thumbBounds.getMaxY()), new float[]{0, .55f, .5501f, 1},
                    new Color[]{new Color(0xe5f4fc), new Color(0x9dd5f3),
                            new Color(0x6cbbe5), new Color(0x50a1cc)});
            strokePaint = new Color(0x2c628b);
        } else {
            fill = new LinearGradientPaint(new Point2D.Double(0,
                    thumbBounds.getMinY()), new Point2D.Double(0,
                    thumbBounds.getMaxY()), new float[]{0, .55f, .5501f, 1},
                    new Color[]{
                            AnimationManager.tween(new Color(0xf2f2f2),
                                    new Color(0xe9f6fd), selected),
                            AnimationManager.tween(new Color(0xebebeb),
                                    new Color(0xd8effc), selected),
                            AnimationManager.tween(new Color(0xdbdbdb),
                                    new Color(0xbde6fd), selected),
                            AnimationManager.tween(new Color(0xd7d7d7),
                                    new Color(0xaedef8), selected)});
            strokePaint = AnimationManager.tween(new Color(0x707070),
                    new Color(0x3c7fb1), selected);
        }
        g.setPaint(fill);
        g.fill(outline);

        if (!(Thumb.Rectangle.equals(getThumb(thumbIndex)) || Thumb.Hourglass
                .equals(getThumb(thumbIndex)))) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.clip(outline);
            g2.setColor(new Color(255, 255, 255, 200));
            g2.setStroke(new BasicStroke(4));
            g2.draw(outline);
            g2.dispose();
        }
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE);
        g.setStroke(new BasicStroke(1f));
        g.setPaint(strokePaint);
        g.draw(outline);
    }
}