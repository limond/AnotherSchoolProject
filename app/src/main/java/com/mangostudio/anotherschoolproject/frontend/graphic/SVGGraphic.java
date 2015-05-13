package com.mangostudio.anotherschoolproject.frontend.graphic;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.caverock.androidsvg.SVG;

public class SVGGraphic extends Graphic {

    private final SVG svg;

    public SVGGraphic(SVG svg) {
        super(-svg.getDocumentWidth()/2, -svg.getDocumentHeight()/2, svg.getDocumentWidth(), svg.getDocumentHeight());
        this.svg = svg;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.translate(translation.x, translation.y);
        canvas.scale(size.x/originalSize.x, size.y/originalSize.y);
        svg.renderToCanvas(canvas);
        canvas.restore();
    }
}
