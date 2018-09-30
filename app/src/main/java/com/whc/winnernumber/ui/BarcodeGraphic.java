/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whc.winnernumber.ui;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;


import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.google.android.gms.vision.barcode.Barcode;
import com.whc.winnernumber.Control.Common;
import com.whc.winnernumber.DataBase.PriceDB;
import com.whc.winnernumber.DataBase.WinnerDB;
import com.whc.winnernumber.Model.PriceVO;

import java.util.HashMap;

import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_EXCLAMATION;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_FLAG;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_STAR;


/**
 * Graphic instance for rendering barcode position, size, and ID within an associated graphic
 * overlay view.
 */
public class BarcodeGraphic extends TrackedGraphic<Barcode> {
    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN
    };
    private static int mCurrentColorIndex = 0;

    private Paint mRectPaint;
    private Paint mTextPaint;
    private volatile Barcode mBarcode;
    private Activity context;
    public static HashMap<Integer, String> hashMap;
    private PriceDB priceDB;
    private String[] level = {"first", "second", "third", "fourth", "fifth", "sixth"};
    private PriceVO priceVO;
    private HashMap<String, Integer> levellength;
    private HashMap<String, String> levelprice;
    private String EleNul, periodNow;
    private int max;
    public static String result;



    BarcodeGraphic(GraphicOverlay overlay, Activity context) {
        super(overlay);
        hashMap = new HashMap<>();
        result = null;
        this.context = context;
        WinnerDB winnerDB = new WinnerDB(context);
        priceDB = new PriceDB(winnerDB.getReadableDatabase());
        String sMax = priceDB.findMaxPeriod();
        if (sMax != null) {
            max = Integer.parseInt(sMax);
        }
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];
        mRectPaint = new Paint();
        mRectPaint.setColor(selectedColor);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(4.0f);
        //paint
        mTextPaint = new Paint();
        mTextPaint.setColor(selectedColor);
        mTextPaint.setTextSize(36.0f);
        levelprice = getHashLP();
        levellength = getlevellength();

    }


    private static HashMap<String, Integer> getlevellength() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("super", 8);
        hashMap.put("spc", 8);
        hashMap.put("first", 8);
        hashMap.put("second", 7);
        hashMap.put("third", 6);
        hashMap.put("fourth", 5);
        hashMap.put("fifth", 4);
        hashMap.put("sixth", 3);
        return hashMap;
    }


    private static HashMap<String, String> getHashLP() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("super", "特別獎1000萬元");
        hashMap.put("spc", "特獎200萬元");
        hashMap.put("first", "頭獎20萬元");
        hashMap.put("second", "二獎4萬元");
        hashMap.put("third", "三獎1萬元");
        hashMap.put("fourth", "四獎4千元");
        hashMap.put("fifth", "五獎1千元");
        hashMap.put("sixth", "六獎200元");
        return hashMap;
    }

    /**
     * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateItem(Barcode barcode) {
        mBarcode = barcode;
        if (barcode == null) {
            postInvalidate();
            return;
        }

        String stringOne;
        try {
            stringOne = barcode.rawValue;
            String nul = stringOne.substring(0, 10);
            new Integer(nul.substring(2));
            new Integer(stringOne.substring(10, 17));
            stringOne.substring(45, 53);
            stringOne.substring(17, 21);
        } catch (Exception e) {
            stringOne = null;
        }

        if (stringOne != null) {
            EleNul = mBarcode.rawValue.substring(0, 10);
            periodNow= mBarcode.rawValue.substring(10, 17);
            if (MultiTrackerActivity.oldElu == null || (!MultiTrackerActivity.oldElu.equals(EleNul))||MultiTrackerActivity.periodOld==null||(!MultiTrackerActivity.periodOld.equals(periodNow))) {
                MultiTrackerActivity.oldElu = EleNul;
                MultiTrackerActivity.periodOld=periodNow;
                MultiTrackerActivity.isold = false;
                MultiTrackerActivity.colorChange++;
            } else {
                MultiTrackerActivity.isold = true;
                postInvalidate();
                return;
            }
            String day = mBarcode.rawValue.substring(10, 17);
            int mon = Integer.parseInt(day.substring(3, 5));
            if (mon % 2 == 1) {
                if (mon == 11) {
                    day = day.substring(0, 4) + "2";
                } else if (mon == 10) {
                    day = day.substring(0, 4) + "1";
                } else if (mon == 9) {
                    day = day.substring(0, 3) + "10";
                } else {
                    mon = mon + 1;
                    day = day.substring(0, 4) + String.valueOf(mon);
                }
            } else {
                day = day.substring(0, 5);
            }
            MultiTrackerActivity.p = getPeriod(day);
            if (Integer.valueOf(day) > max) {

                MultiTrackerActivity.result = "over";
                postInvalidate();
                return;
            }
            priceVO = priceDB.getPeriodAll(day);
            if (priceVO == null) {
                MultiTrackerActivity.result = "no";
                postInvalidate();
                return;
            }
            MultiTrackerActivity.result = anwswer(EleNul.substring(2), priceVO);
        }
    }


    private String firsttofourprice(String nul, String pricenul) {
        for (int i = 0; i < 6; i++) {
            if (nul.substring(i).equals(pricenul.substring(i))) {
                return level[i];
            }
        }
        return "N";
    }

    private String anwswer(String nul, PriceVO priceVO) {
        String threenul = nul.substring(5);
        String s;
        if (nul.equals(priceVO.getSuperPrizeNo())) {
            levelprice.put("win", priceVO.getSuperPrizeNo());
            return "super";
        }
        if (nul.equals(priceVO.getSpcPrizeNo())) {
            levelprice.put("win", priceVO.getSpcPrizeNo());
            return "spc";
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo1());
        if (!s.equals("N")) {
            levelprice.put("win", priceVO.getFirstPrizeNo1());
            return s;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo2());
        if (!s.equals("N")) {
            levelprice.put("win", priceVO.getFirstPrizeNo2());
            return s;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo3());
        if (!s.equals("N")) {
            levelprice.put("win", priceVO.getFirstPrizeNo3());
            return s;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo1())) {
            levelprice.put("win", priceVO.getSixthPrizeNo1());
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo2())) {
            levelprice.put("win", priceVO.getSixthPrizeNo2());
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo3())) {
            levelprice.put("win", priceVO.getSixthPrizeNo3());
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo4())) {
            levelprice.put("win", priceVO.getSixthPrizeNo4());
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo5())) {
            levelprice.put("win", priceVO.getSixthPrizeNo5());
            return "sixth";
        }
        if (threenul.equals(priceVO.getSixthPrizeNo6())) {
            levelprice.put("win", priceVO.getSixthPrizeNo6());
            return "sixth";
        }
        return "N";
    }


    /**
     * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;
        if (barcode != null) {
            // Draws the bounding box around the barcode.
            RectF rect = new RectF(barcode.getBoundingBox());
            rect.left = translateX(rect.left);
            rect.top = translateY(rect.top);
            rect.right = translateX(rect.right);
            rect.bottom = translateY(rect.bottom);
            canvas.drawRect(rect, mRectPaint);
        }
        if (MultiTrackerActivity.result == null) {
            MultiTrackerActivity.answer.setText("請對準左邊QRCode~");
            MultiTrackerActivity.awardTitle.setText(null);
            return;
        }
        if (!MultiTrackerActivity.isold) {
            int textColor;
            switch (MultiTrackerActivity.colorChange%2)
            {
                case 0:
                    textColor=Color.BLUE;
                    MultiTrackerActivity.awardTitle.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                    break;
                default:
                    textColor=Color.parseColor("#00AA55");
                    MultiTrackerActivity.awardTitle.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                    break;
            }
            if(MultiTrackerActivity.result.equals("over"))
            {
                String total=MultiTrackerActivity.p+"尚未開獎\n 發票號碼 : "+MultiTrackerActivity.oldElu;
                Spannable content = new SpannableString(total);
                content.setSpan(new ForegroundColorSpan(textColor), total.indexOf(":")+1, total.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                MultiTrackerActivity.answer.setText(content);

                MultiTrackerActivity.awardTitle.setText(null);
                return;
            }
            if(MultiTrackerActivity.result.equals("no"))
            {
                String total=MultiTrackerActivity.p+"已過兌獎期限\n 發票號碼 : " + MultiTrackerActivity.oldElu;
                Spannable content = new SpannableString(total);
                content.setSpan(new ForegroundColorSpan(textColor), total.indexOf(":")+1, total.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                MultiTrackerActivity.answer.setText(content);
                MultiTrackerActivity.answer.setText(content);

                MultiTrackerActivity.awardTitle.setText(null);
                return;
            }
            if (MultiTrackerActivity.result.equals("N")) {
                String total=MultiTrackerActivity.p+"\n發票號碼:"+MultiTrackerActivity.oldElu;
                Spannable content = new SpannableString(total);
                content.setSpan(new ForegroundColorSpan(textColor), 0,total.indexOf("發") , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setSpan(new ForegroundColorSpan(textColor), total.indexOf(":")+1, total.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                MultiTrackerActivity.answer.setText(content);

                BootstrapText text = new BootstrapText.Builder(context)
                        .addText(" " + "沒有中獎" + " ")
                        .addFontAwesomeIcon(FA_EXCLAMATION)
                        .addText(" " + "再接再厲" + " ")
                        .addFontAwesomeIcon(FA_FLAG)
                        .build();
                MultiTrackerActivity.awardTitle.setText(text);


            } else {
                if(priceVO!=null)
                {
                    String peroid = getPeriod(priceVO.getInvoYm());
                    StringBuffer sb=new StringBuffer();
                    sb.append(peroid).append(levelprice.get("win")).append("\n發票號碼").append(MultiTrackerActivity.oldElu);
                    Spannable content = new SpannableString(sb.toString());
                    content.setSpan(new ForegroundColorSpan(Color.RED), peroid.length()+levelprice.get("win").length()-levellength.get(MultiTrackerActivity.result),peroid.length()+levelprice.get("win").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    content.setSpan(new ForegroundColorSpan(Color.MAGENTA), sb.length()-(levellength.get(MultiTrackerActivity.result)), sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    MultiTrackerActivity.answer.setText(content);
                    BootstrapText text = new BootstrapText.Builder(context)
                            .addFontAwesomeIcon(FA_STAR)
                            .addText(" " + levelprice.get(MultiTrackerActivity.result) + " ")
                            .addFontAwesomeIcon(FA_STAR)
                            .build();
                    MultiTrackerActivity.awardTitle.setText(text);
                    MultiTrackerActivity.awardTitle.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                }
            }
        }
    }


    private String getPeriod(String inYm) {
        String day = inYm.substring(3, 5);
        String period;
        if (day.equals("02")) {
            period = inYm.substring(0, 3) + "年01-02月";
        } else if (day.equals("04")) {
            period = inYm.substring(0, 3) + "年03-04月";
        } else if (day.equals("06")) {
            period = inYm.substring(0, 3) + "年05-06月";
        } else if (day.equals("08")) {
            period = inYm.substring(0, 3) + "年07-08月";
        } else if (day.equals("10")) {
            period = inYm.substring(0, 3) + "年09-10月";
        } else {
            period = inYm.substring(0, 3) + "年11-12月";
        }
        return period;
    }

}
