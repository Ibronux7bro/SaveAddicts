package com.saveaddicts.app.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.saveaddicts.app.models.RecoveryRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Custom View that renders the clinician-updated Optimism Graph (FR5 & FR8).
 * Displays a dated recovery timeline with optimism percentage (0-100%).
 */
public class OptimismGraphView extends View {

    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint scoreBadgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emptyStatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final List<RecoveryRecord> records = new ArrayList<>();

    private int primaryColor = Color.parseColor("#00897B"); // Teal
    private int navyColor = Color.parseColor("#0D2538");
    private int gridColor = Color.parseColor("#E2E8F0");
    private int textColor = Color.parseColor("#64748B");

    public OptimismGraphView(Context context) {
        super(context);
        init();
    }

    public OptimismGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OptimismGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        axisPaint.setColor(gridColor);
        axisPaint.setStrokeWidth(dpToPx(1.5f));
        axisPaint.setStyle(Paint.Style.STROKE);

        gridPaint.setColor(gridColor);
        gridPaint.setStrokeWidth(dpToPx(1.0f));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setPathEffect(new DashPathEffect(new float[]{dpToPx(4), dpToPx(4)}, 0));

        linePaint.setColor(primaryColor);
        linePaint.setStrokeWidth(dpToPx(3.5f));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint.setStyle(Paint.Style.FILL);

        pointOuterPaint.setColor(primaryColor);
        pointOuterPaint.setStyle(Paint.Style.FILL);

        pointInnerPaint.setColor(Color.WHITE);
        pointInnerPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(textColor);
        textPaint.setTextSize(dpToPx(11f));
        textPaint.setTextAlign(Paint.Align.CENTER);

        scoreBadgePaint.setColor(navyColor);
        scoreBadgePaint.setTextSize(dpToPx(12f));
        scoreBadgePaint.setFakeBoldText(true);
        scoreBadgePaint.setTextAlign(Paint.Align.CENTER);

        emptyStatePaint.setColor(textColor);
        emptyStatePaint.setTextSize(dpToPx(13f));
        emptyStatePaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setRecords(List<RecoveryRecord> newRecords) {
        this.records.clear();
        if (newRecords != null && !newRecords.isEmpty()) {
            this.records.addAll(newRecords);
            // Sort chronologically
            Collections.sort(this.records, Comparator.comparingLong(RecoveryRecord::getTimestamp));
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float paddingLeft = dpToPx(40);
        float paddingRight = dpToPx(24);
        float paddingTop = dpToPx(28);
        float paddingBottom = dpToPx(36);

        float chartWidth = width - paddingLeft - paddingRight;
        float chartHeight = height - paddingTop - paddingBottom;

        if (chartWidth <= 0 || chartHeight <= 0) return;

        // Draw horizontal grid lines and Y-axis scale (0%, 25%, 50%, 75%, 100%)
        int[] levels = {0, 25, 50, 75, 100};
        for (int level : levels) {
            float y = paddingTop + chartHeight - (chartHeight * level / 100f);
            canvas.drawLine(paddingLeft, y, width - paddingRight, y, gridPaint);

            // Y-axis label
            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(level + "%", paddingLeft - dpToPx(8), y + dpToPx(4), textPaint);
        }

        // Base axis line
        canvas.drawLine(paddingLeft, paddingTop + chartHeight, width - paddingRight, paddingTop + chartHeight, axisPaint);

        if (records.isEmpty()) {
            emptyStatePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("No recovery records recorded yet by your physician.", width / 2f, height / 2f - dpToPx(4), emptyStatePaint);
            canvas.drawText("Optimism trajectory will appear following clinical assessment.", width / 2f, height / 2f + dpToPx(16), textPaint);
            return;
        }

        int count = records.size();
        float[] pointX = new float[count];
        float[] pointY = new float[count];

        for (int i = 0; i < count; i++) {
            RecoveryRecord r = records.get(i);
            if (count == 1) {
                pointX[i] = paddingLeft + (chartWidth / 2f);
            } else {
                pointX[i] = paddingLeft + (chartWidth * i / (count - 1f));
            }
            float score = Math.max(0, Math.min(100, r.getOptimismScore()));
            pointY[i] = paddingTop + chartHeight - (chartHeight * (score / 100f));
        }

        // Draw fill area below line
        if (count > 1) {
            Path fillPath = new Path();
            fillPath.moveTo(pointX[0], paddingTop + chartHeight);
            fillPath.lineTo(pointX[0], pointY[0]);

            for (int i = 1; i < count; i++) {
                float prevX = pointX[i - 1];
                float prevY = pointY[i - 1];
                float curX = pointX[i];
                float curY = pointY[i];

                float cx = (prevX + curX) / 2f;
                fillPath.cubicTo(cx, prevY, cx, curY, curX, curY);
            }

            fillPath.lineTo(pointX[count - 1], paddingTop + chartHeight);
            fillPath.close();

            LinearGradient gradient = new LinearGradient(
                    0, paddingTop,
                    0, paddingTop + chartHeight,
                    Color.argb(70, 0, 137, 123),
                    Color.argb(5, 0, 137, 123),
                    Shader.TileMode.CLAMP
            );
            fillPaint.setShader(gradient);
            canvas.drawPath(fillPath, fillPaint);
        }

        // Draw graph line
        if (count == 1) {
            canvas.drawCircle(pointX[0], pointY[0], dpToPx(6), linePaint);
        } else {
            Path linePath = new Path();
            linePath.moveTo(pointX[0], pointY[0]);
            for (int i = 1; i < count; i++) {
                float prevX = pointX[i - 1];
                float prevY = pointY[i - 1];
                float curX = pointX[i];
                float curY = pointY[i];

                float cx = (prevX + curX) / 2f;
                linePath.cubicTo(cx, prevY, cx, curY, curX, curY);
            }
            canvas.drawPath(linePath, linePaint);
        }

        // Draw points, date labels, and score labels
        for (int i = 0; i < count; i++) {
            RecoveryRecord r = records.get(i);
            float px = pointX[i];
            float py = pointY[i];

            // Outer and inner point circles
            canvas.drawCircle(px, py, dpToPx(6f), pointOuterPaint);
            canvas.drawCircle(px, py, dpToPx(3.5f), pointInnerPaint);

            // Date label below axis
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(r.getShortDate(), px, paddingTop + chartHeight + dpToPx(16), textPaint);

            // Score badge above point
            scoreBadgePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(r.getOptimismScore() + "%", px, py - dpToPx(10), scoreBadgePaint);
        }
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
