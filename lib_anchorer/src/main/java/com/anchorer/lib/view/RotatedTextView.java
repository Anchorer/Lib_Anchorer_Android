package com.anchorer.lib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * View: RotatedTextView
 * 自定义TextView，支持文字倾斜显示。
 *
 * Created by Anchorer/duruixue on 2013/8/29.
 * @author Anchorer
 */
public class RotatedTextView extends TextView {
    //倾斜角度，默认45度
    private int rotateDegree = 45;
	
	public RotatedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    public void setRotateDegree(int rotateDegree) {
        this.rotateDegree = rotateDegree;
    }

    @Override
	protected void onDraw(Canvas canvas) {
		//倾斜45度
		canvas.rotate(rotateDegree, getMeasuredWidth()/2, getMeasuredHeight()/2);
		super.onDraw(canvas);
	}

}
