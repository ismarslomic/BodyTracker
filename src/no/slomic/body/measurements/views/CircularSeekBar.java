/**
 * @Author: Ismar Slomic (ismar@slomic.no)
 */

/**
 * Copyright (C) 2010 Jesse Wilson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.slomic.body.measurements.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public final class CircularSeekBar extends View {
    /** Dimensions and graphical shapes of the circle and buttons **/
    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;
    private int mDiameter;
    private RectF mOuterCircle;
    private RectF mInnerCircle;
    private RectF mButtonCircle;
    private final Path mPath = new Path();
    private static final int INSETS = 6;
    private int mStepThumbTickness = 2;

    /** Circle colors **/
    private Paint mEmptyCircleColor = new Paint();
    private Paint mThumbColor = new Paint();
    private Paint mSelectedCircleColor = new Paint();

    /** Text syle for the text in the midle of the circle **/
    private Paint mTextStyle = new Paint();

    /** Buttons **/
    private boolean mIsIncreasePushed;
    private boolean mIsDecreasePushed;
    private Paint mButtonPushedColor = new Paint();
    private int mButtonChangeInterval = 5;

    /** Angles **/
    private int mStartAngle = 270; // 360 in path.arcTo
    private int mAngleIncrement = 10;

    /** Steps **/
    private int mSelectedStep = 0;
    private int mTotalSteps = 360 / this.mAngleIncrement; // 360 degrees
    private int mRoundTrips = 0; // count of round trips in the circle

    /** Array of values that the slider iterates through **/
    private double[] mValueArray = new double[0];
    private String mValueUnitName = "kg";

    /** Logging **/
    private String TAG = CircularSeekBar.class.getName();

    public CircularSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        /** Initialize colors of the circles **/
        this.mEmptyCircleColor.setColor(Color.rgb(115, 115, 115)); // grey color
        this.mEmptyCircleColor.setAntiAlias(true);
        this.mSelectedCircleColor.setColor(Color.rgb(255, 0, 165)); // pink
                                                                    // color
        this.mSelectedCircleColor.setAntiAlias(true);
        this.mThumbColor.setColor(Color.WHITE);
        this.mThumbColor.setAntiAlias(true);

        /** Initialize the text paint **/
        this.mTextStyle.setSubpixelText(true);
        this.mTextStyle.setAntiAlias(true);
        this.mTextStyle.setColor(Color.WHITE);
        this.mTextStyle.setTextAlign(Paint.Align.CENTER);

        /** Initialize the buttons **/
        this.mButtonPushedColor.setColor(Color.argb(102, 115, 115, 115)); // light
                                                                          // grey
                                                                          // color
        this.mButtonPushedColor.setAntiAlias(true);
    }

    /****************** INTERFACE METHODS ****************/

    /**
     * Main method, orchestrating the drawing of the circular seek bar, buttons
     * and rest of the layout
     **/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /** A. Calculates dimension of the circular seek bar */
        if (getWidth() != this.mWidth || getHeight() != this.mHeight) {
            this.mWidth = getWidth();
            this.mHeight = getHeight();
            this.mCenterX = this.mWidth / 2;
            this.mCenterY = this.mHeight / 2;

            this.mDiameter = Math.min(this.mWidth, this.mHeight) - (2 * INSETS);
            int thickness = this.mDiameter / 15;

            int left = (this.mWidth - this.mDiameter) / 2;
            int top = (this.mHeight - this.mDiameter) / 2;
            int bottom = top + this.mDiameter;
            int right = left + this.mDiameter;
            this.mOuterCircle = new RectF(left, top, right, bottom);

            int innerDiameter = this.mDiameter - thickness * 2;
            this.mInnerCircle = new RectF(left + thickness, top + thickness, left + thickness
                    + innerDiameter, top + thickness + innerDiameter);

            int offset = thickness * 2;
            int buttonDiameter = this.mDiameter - offset * 2;
            this.mButtonCircle = new RectF(left + offset, top + offset, left + offset
                    + buttonDiameter, top + offset + buttonDiameter);

            this.mTextStyle.setTextSize(this.mDiameter * 0.20f);
        }

        /** B. Calls the helper method to draw the circular seek bar **/
        drawCircularSeekBar(canvas);

        /**
         * C. Calls the helper method to draw the text and buttons of the seek
         * bar
         **/
        drawTextAndButtons(canvas);
    }

    /**
     * Accept a touches near the circle's edge, translate it to an angle, and
     * update the sweep angle.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(this.TAG, "onTouchEvent() called");

        if (this.mOuterCircle == null) {
            return true; // ignore all events until the canvas is drawn
        }

        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        this.mIsIncreasePushed = false;
        this.mIsDecreasePushed = false;

        int distanceFromCenterX = this.mCenterX - touchX;
        int distanceFromCenterY = this.mCenterY - touchY;
        int distanceFromCenterSquared = distanceFromCenterX * distanceFromCenterX
                + distanceFromCenterY * distanceFromCenterY;
        float maxSlider = (this.mDiameter * 1.3f) / 2;
        float maxUpDown = (this.mDiameter * 0.8f) / 2;

        // handle increment/decrement button events
        if (distanceFromCenterSquared < (maxUpDown * maxUpDown)) {
            boolean isIncrease = touchX > this.mCenterX;

            if (event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_MOVE) {
                if (isIncrease) {
                    this.mIsIncreasePushed = true;
                    increaseStep(this.mButtonChangeInterval);
                } else {
                    this.mIsDecreasePushed = true;
                    decreaseStep(this.mButtonChangeInterval);
                }
            }

            postInvalidate();
            return true;

            // if it's on the slider, handle sliders events
        } else if (distanceFromCenterSquared < (maxSlider * maxSlider)) {
            int angle = pointToAngle(touchX, touchY);
            int sweepAngle = convertToSweepAngle(angle);
            int step = getStepForSweepAngle(sweepAngle);
            setSelectedStep(step);

            return true;

        } else {
            return false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(this.TAG, "onMeasure() called");

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // Don't use the full screen width on tablets!
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        float maxWidthInches = 2.3f;

        width = Math.min(width, (int) (maxWidthInches * metrics.densityDpi));
        height = Math.min(height, (int) (width * 0.7f));

        setMeasuredDimension(width, height);
    }

    /**************** COLOR METHODS ****************/

    /** Sets the color of the remaining/empty circle in the middle **/
    public void setEmptyCircleColor(int color) {
        this.mEmptyCircleColor.setColor(color);
    }

    /**
     * Sets the color of the circle in the middle for selected steps from start
     * to selected step
     **/
    public void setSelectedCircleColor(int color) {
        this.mSelectedCircleColor.setColor(color);
    }

    /** Sets the color of the seek bar thumb **/
    public void setSeekBarThumsColor(int color) {
        this.mThumbColor.setColor(color);
    }

    /** Sets the color of the buttons in the middle when they are pushed **/
    public void setButtonPushedColor(int color) {
        this.mButtonPushedColor.setColor(color);
    }

    /**************** DRAWING HELPER METHODS ****************/

    /**
     * Draw a circle and an arc of the selected step, from start thru end.
     */
    private void drawCircularSeekBar(Canvas canvas) {
        int sweepDegrees = getSweepAngleForStep(this.mSelectedStep) - 1;
        int startAngle = this.mStartAngle;

        // the colored "filled" part of the circle
        drawArc(canvas, startAngle, sweepDegrees, this.mSelectedCircleColor);
        Log.d(this.TAG, "drawCircularSeekBar() selected part startAngle: " + this.mStartAngle
                + " sweepDegrees: " + sweepDegrees);

        // the white selected part of the circle
        startAngle += sweepDegrees;
        drawArc(canvas, startAngle, this.mStepThumbTickness, this.mThumbColor);
        Log.d(this.TAG, "drawCircularSeekBar() thumb startAngle:"
                + (this.mStartAngle + sweepDegrees) + " sweepDegrees: " + this.mStepThumbTickness);

        // the grey empty part of the circle
        startAngle += this.mStepThumbTickness;
        drawArc(canvas, startAngle, 360 - sweepDegrees - this.mStepThumbTickness,
                this.mEmptyCircleColor);
        Log.d(this.TAG, "drawCircularSeekBar() empty part startAngle: "
                + (this.mStartAngle + sweepDegrees + this.mStepThumbTickness) + " sweepDegrees: "
                + (360 - sweepDegrees - this.mStepThumbTickness));
    }

    /**
     * Write labels in the middle of the circle
     */
    private void drawTextAndButtons(Canvas canvas) {
        Log.d(this.TAG, "drawClockTextAndButtons() called");

        // up/down button backgrounds
        if (this.mIsIncreasePushed) {
            canvas.drawArc(this.mButtonCircle, 270, 180, true, this.mButtonPushedColor);
        }
        if (this.mIsDecreasePushed) {
            canvas.drawArc(this.mButtonCircle, 90, 180, true, this.mButtonPushedColor);
        }

        // Writing the text in the middle
        canvas.drawText(getValueAtStep(this.mSelectedStep) + "", this.mCenterX, this.mCenterY
                - (this.mDiameter * 0.08f), this.mTextStyle);
        canvas.drawText(this.mValueUnitName, this.mCenterX, this.mCenterY
                + (this.mDiameter * 0.08f), this.mTextStyle);

        // up/down buttons
        Paint downPaint = this.mIsDecreasePushed ? this.mThumbColor : this.mEmptyCircleColor;
        canvas.drawRect(this.mCenterX - this.mDiameter * 0.32f, this.mCenterY - this.mDiameter
                * 0.01f, this.mCenterX - this.mDiameter * 0.22f, this.mCenterY + this.mDiameter
                * 0.01f, downPaint);

        Paint upPaint = this.mIsIncreasePushed ? this.mThumbColor : this.mEmptyCircleColor;
        canvas.drawRect(this.mCenterX + this.mDiameter * 0.22f, this.mCenterY - this.mDiameter
                * 0.01f, this.mCenterX + this.mDiameter * 0.32f, this.mCenterY + this.mDiameter
                * 0.01f, upPaint);
        canvas.drawRect(this.mCenterX + this.mDiameter * 0.26f, this.mCenterY - this.mDiameter
                * 0.05f, this.mCenterX + this.mDiameter * 0.28f, this.mCenterY + this.mDiameter
                * 0.05f, upPaint);
    }

    /** Generic method for drawing arcs **/
    private void drawArc(Canvas canvas, int startAngle, int sweepDegrees, Paint paint) {
        Log.d(this.TAG, "drawArc() called");

        if (sweepDegrees <= 0)
            return;

        this.mPath.reset();
        this.mPath.arcTo(this.mOuterCircle, startAngle, sweepDegrees);
        this.mPath.arcTo(this.mInnerCircle, startAngle + sweepDegrees, -sweepDegrees);
        this.mPath.close();
        canvas.drawPath(this.mPath, paint);
    }

    /******************* GETTERS AND SETTES *************/

    /**
     * Returns sweep angle for given step. Example: step 12 returns sweep angle
     * 120
     */
    public int getSweepAngleForStep(int step) {
        step = step % this.mTotalSteps; // in case the current step belong to
                                        // other round trips
        return step * this.mAngleIncrement;
    }

    /**
     * Returns step for given sweep angle. Example: sweep angle 120 returns step
     * 12
     */
    public int getStepForSweepAngle(int sweepAngle) {
        return sweepAngle / this.mAngleIncrement;
    }

    /** Returns the round trips in the circle seek bar **/
    public int getRoundTrips() {
        return this.mRoundTrips;
    }

    /**
     * Sets the array of double values of the seek bar and invalidate current
     * step selection
     **/
    public void setValueArray(double[] values) {
        this.mValueArray = values;
        this.mSelectedStep = 0;
        this.mRoundTrips = 0;
        postInvalidate();
    }

    /**
     * Sets the selected step in the circle according to the current round trip.
     * Must be positive value and less then valueArray.length
     */
    public void setSelectedStep(int step) {
        if (step < 0) // ignore negative steps
            step = 0;

        if (step > this.mTotalSteps) // the step is set from the code
        {
            this.mRoundTrips = (step / this.mTotalSteps); // set round trips
            this.mSelectedStep = step; // set selected step
            Log.d(this.TAG, "Setting selected step to: " + this.mSelectedStep
                    + " and round trips is now: " + this.mRoundTrips);
        } else // the step is set from seek bar
        {
            step += (this.mRoundTrips * this.mTotalSteps);

            if (this.mSelectedStep == step || step > this.mValueArray.length) // do
                                                                              // nothing
                                                                              // if
                                                                              // the
                                                                              // step
                                                                              // is
                                                                              // the
                                                                              // same
                                                                              // as
                                                                              // the
                                                                              // current
                                                                              // selected
                                                                              // step
                                                                              // or
                                                                              // greater
                                                                              // then
                                                                              // array
                                                                              // lenght
            {
                step = 0;
                return;
            }

            Log.d(this.TAG, "Selected step: " + this.mSelectedStep + ", new step: " + step
                    + ", total steps: " + this.mTotalSteps + ", round trips: " + this.mRoundTrips
                    + ", modulus: " + (this.mSelectedStep % this.mTotalSteps));

            if (this.mSelectedStep - step == this.mTotalSteps - 1) // add one
                                                                   // round trip
                this.mRoundTrips++;
            else if (this.mSelectedStep - step == -(this.mTotalSteps - 1) && this.mRoundTrips != 0) // reduce
                                                                                                    // one
                                                                                                    // round
                                                                                                    // trip
                this.mRoundTrips--;

            this.mSelectedStep = step;

            Log.d(this.TAG, "Setting selected step to: " + this.mSelectedStep
                    + " and round trips is now: " + this.mRoundTrips);
        }
        postInvalidate();
    }

    /**
     * Returns value at given step.
     * 
     * @param step positive value in the index range of the valueArray
     * @return value at given step. If the step is outside of the index range of
     *         valueArray 0.00 will be returned
     */
    public double getValueAtStep(int step) {
        if (step < 0 || this.mValueArray == null || this.mValueArray.length == 0
                || step >= this.mValueArray.length)
            return 0.00;

        return this.mValueArray[step];
    }

    /**
     * Returns the value for the selected step in the seek bar.
     * 
     * @return value for the selected step or 0.00 if the valueArray is empty
     **/
    public double getSelectedValue() {
        if (this.mValueArray != null && this.mValueArray.length > 0)
            return this.mValueArray[this.mSelectedStep];
        else
            return 0.00;
    }

    /**
     * Finds the first occurrence of the value in the valueArray and sets the
     * selected step to it.
     * 
     * @param value that is going to be selected in the seek bar. If not found
     *            no change will be done.
     */
    public void setSelectedStepForValue(double value) {
        // find the first index/step of the value in the valueArray
        if (this.mValueArray != null && this.mValueArray.length > 0) {
            for (int step = 0; step < this.mValueArray.length; step++) {
                if (this.mValueArray[step] == value) {
                    // set the selected step to the value index/step
                    setSelectedStep(step);
                    return;
                }
            }
        }
    }

    /**
     * The unit name of the values. This name is displayed in the middle of the
     * circle
     **/
    public void setValueUnitName(String name) {
        this.mValueUnitName = name;
    }

    /** Returns the unit name of the values in the seek bar **/
    public String getValueUnitName() {
        return this.mValueUnitName;
    }

    /** Returns the selected step in the seek bar **/
    public int getSelectedStep() {
        return this.mSelectedStep;
    }

    /**
     * Sets the increment/decrement value of the seek bar
     * 
     * @param stepInterval positive value
     */
    public void setButtonChangeInterval(int stepInterval) {
        if (stepInterval < 0)
            return;

        this.mButtonChangeInterval = stepInterval;
    }

    /*********************** STEP MANAGEMENT METHODS ******************/

    /**
     * Increases selected step with given increment.
     * 
     * @param increment positive value less then valueArray.length
     **/
    public void increaseStep(int increment) {
        if (increment < 0)
            return;

        int step = this.mSelectedStep + increment;

        if (step >= this.mValueArray.length)
            step = this.mValueArray.length - 1;

        this.mRoundTrips = (step / this.mTotalSteps);
        this.mSelectedStep = step;

        postInvalidate();
    }

    /**
     * Decreases selected step with given decrement.
     * 
     * @param decrement positive value. Will not decrease to step below zero.
     **/
    public void decreaseStep(int decrement) {
        int step = this.mSelectedStep - decrement;

        if (step < 0)
            step = 0;

        this.mRoundTrips = (step / this.mTotalSteps);
        this.mSelectedStep = step;

        postInvalidate();
    }

    /****************** METHODS FOR GETTING SELECTED ANGLE ***********/

    /**
     * Convert the angle into a sweep angle. The sweep angle is a positive angle
     * between the start angle and the touched angle.
     */
    public int convertToSweepAngle(int angle) {
        int sweepAngle = 360 + angle - this.mStartAngle;
        sweepAngle = roundToNearest(sweepAngle);
        if (sweepAngle > 360) {
            sweepAngle = sweepAngle - 360;
        }

        Log.d(this.TAG, "Converting from angle: " + angle + " to sweepAngle: " + sweepAngle);

        return sweepAngle;
    }

    /**
     * Returns the number of degrees (0-359) for the given point, such that 0
     * starts at 90 degrees and 180 degrees is at 270 degrees.
     */
    private int pointToAngle(int x, int y) {

        /*
         * Get the angle from a triangle by dividing opposite by adjacent and
         * taking the atan. This code is careful not to divide by 0. adj | opp |
         * opp +180 | +270 adj _________|_________ | adj +90 | +0 opp | opp |
         * adj
         */

        if (x >= this.mCenterX && y < this.mCenterY) // [0..90]
        {
            double opp = x - this.mCenterX;
            double adj = this.mCenterY - y;
            Log.d(this.TAG, "pointToAngle(): [0..90] called. opp: " + opp + ", adj: " + adj + " = "
                    + (270 + (int) Math.toDegrees(Math.atan(opp / adj))));
            return 270 + (int) Math.toDegrees(Math.atan(opp / adj));
        } else if (x > this.mCenterX && y >= this.mCenterY) // [90..180]
        {
            double opp = y - this.mCenterY;
            double adj = x - this.mCenterX;
            Log.d(this.TAG, "pointToAngle() [90..180] called. opp: " + opp + ", adj: " + adj
                    + " = " + (int) Math.toDegrees(Math.atan(opp / adj)));
            return (int) Math.toDegrees(Math.atan(opp / adj));
        } else if (x <= this.mCenterX && y > this.mCenterY) // [180..270]
        {
            double opp = this.mCenterX - x;
            double adj = y - this.mCenterY;
            Log.d(this.TAG, "pointToAngle() // [180..270] called. opp: " + opp + ", adj: " + adj
                    + " = " + (90 + (int) Math.toDegrees(Math.atan(opp / adj))));
            return 90 + (int) Math.toDegrees(Math.atan(opp / adj));
        } else if (x < this.mCenterX && y <= this.mCenterY) // [270..359]
        {
            double opp = this.mCenterY - y;
            double adj = this.mCenterX - x;
            Log.d(this.TAG, "pointToAngle() // [270..360] called. opp: " + opp + ", adj: " + adj
                    + " = " + (180 + (int) Math.toDegrees(Math.atan(opp / adj))));
            return 180 + (int) Math.toDegrees(Math.atan(opp / adj));
        }

        throw new IllegalArgumentException();
    }

    /**
     * Rounds the angle to the nearest 5 degrees, which equals 1 step on a
     * circle seek bar. Not strictly necessary, but it discourages fat-fingered
     * users from being frustrated when trying to select a fine-grained period.
     */
    private int roundToNearest(int angle) {
        return ((angle + 5) / 10) * 10;
    }
}
