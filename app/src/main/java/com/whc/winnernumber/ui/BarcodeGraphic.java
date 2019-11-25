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
import android.app.Service;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;


import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.google.android.gms.vision.barcode.Barcode;
import com.whc.winnernumber.Control.Common;
import com.whc.winnernumber.DataBase.ConsumeDB;
import com.whc.winnernumber.DataBase.PriceDB;
import com.whc.winnernumber.DataBase.WinnerDB;
import com.whc.winnernumber.Model.ConsumeVO;
import com.whc.winnernumber.Model.PriceVO;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
    private PriceDB priceDB;
    private String[] level = {"first", "second", "third", "fourth", "fifth", "sixth"};
    private PriceVO priceVO;
    private HashMap<String, Integer> levelLength;
    private HashMap<String, String> levelPrice;
    private String EleNul, periodNow;
    private int max;
    public static String result;
    private ConsumeDB consumeDB;
    private ConsumeVO consumeVO;
    public boolean isExist;
    public String stringOne, randomNumber, period, money;
    public int year, month, day;
    public Calendar cTime;

    BarcodeGraphic(GraphicOverlay overlay, Activity context) {
        super(overlay);
        result = null;
        this.context = context;
        WinnerDB winnerDB = new WinnerDB(context);
        priceDB = new PriceDB(winnerDB);
        consumeDB = new ConsumeDB(winnerDB);

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
        levelPrice = getHashLP();
        levelLength = getlevellength();
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
    }


    private String firsttofourprice(String nul, String pricenul) {
        for (int i = 0; i < 6; i++) {
            if (nul.substring(i).equals(pricenul.substring(i))) {
                return level[i];
            }
        }
        return "N";
    }

    private List<String> answer(String nul, PriceVO priceVO) {
        String threeNul = nul.substring(5);
        String s;
        List<String> stringList=new ArrayList<>();
        if (nul.equals(priceVO.getSuperPrizeNo())) {
            levelPrice.put("win", priceVO.getSuperPrizeNo());
            stringList.add("super");
            stringList.add(priceVO.getSuperPrizeNo());
            return stringList;
        }
        if (nul.equals(priceVO.getSpcPrizeNo())) {
            levelPrice.put("win", priceVO.getSpcPrizeNo());
            stringList.add("spc");
            stringList.add(priceVO.getSpcPrizeNo());
            return stringList;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo1());
        if (!s.equals("N")) {
            levelPrice.put("win", priceVO.getFirstPrizeNo1());
            stringList.add(s);
            stringList.add(priceVO.getFirstPrizeNo1());
            return stringList;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo2());
        if (!s.equals("N")) {
            levelPrice.put("win", priceVO.getFirstPrizeNo2());
            stringList.add(s);
            stringList.add(priceVO.getFirstPrizeNo2());
            return stringList;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo3());
        if (!s.equals("N")) {
            levelPrice.put("win", priceVO.getFirstPrizeNo3());
            stringList.add(s);
            stringList.add(priceVO.getFirstPrizeNo3());
            return stringList;
        }
        if (threeNul.equals(priceVO.getSixthPrizeNo1())) {
            levelPrice.put("win", priceVO.getSixthPrizeNo1());
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo1());
            return stringList;
        }
        if (threeNul.equals(priceVO.getSixthPrizeNo2())) {
            levelPrice.put("win", priceVO.getSixthPrizeNo2());
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo2());
            return stringList;
        }
        if (threeNul.equals(priceVO.getSixthPrizeNo3())) {
            levelPrice.put("win", priceVO.getSixthPrizeNo3());
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo3());
            return stringList;
        }
        if (threeNul.equals(priceVO.getSixthPrizeNo4())) {
            levelPrice.put("win", priceVO.getSixthPrizeNo4());
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo4());
            return stringList;
        }
        if (threeNul.equals(priceVO.getSixthPrizeNo5())) {
            levelPrice.put("win", priceVO.getSixthPrizeNo5());
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo5());
            return stringList;
        }
        if (threeNul.equals(priceVO.getSixthPrizeNo6())) {
            levelPrice.put("win", priceVO.getSixthPrizeNo6());
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo6());
            return stringList;
        }
        stringList.add("N");
        stringList.add("N");
        return stringList;
    }


    /**
     * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Barcode barcode = mBarcode;

        if (barcode == null) {
            result = null;
            consumeVO = null;
            MultiTrackerActivity.answer.setText(null);
            Animation animation = MultiTrackerActivity.answer.getAnimation();
            if (!(animation == null)) {
                animation.cancel();
            }
            MultiTrackerActivity.answer.setVisibility(View.GONE);
            Log.d("BarcodeGraphic", "NULL");
            return;
        }


        if (barcode != null) {
            // Draws the bounding box around the barcode.
            RectF rect = new RectF(barcode.getBoundingBox());
            rect.left = translateX(rect.left);
            rect.top = translateY(rect.top);
            rect.right = translateX(rect.right);
            rect.bottom = translateY(rect.bottom);
            canvas.drawRect(rect, mRectPaint);

        }


        if (MultiTrackerActivity.qrCode.size() >= 2) {

            if(MultiTrackerActivity.qrCode.contains(barcode.rawValue))
            {
                boolean qrCodeTrue = false;
                for (String qq : MultiTrackerActivity.qrCode) {
                    if (qq.contains("**********")) {
                        qrCodeTrue = true;
                    }

                }
                MultiTrackerActivity.qrCode.clear();
                if (!qrCodeTrue) {
                    MultiTrackerActivity.answer.setText("QR Code格式有誤! 請手動兌獎");
                    Common.setShowAnimation(MultiTrackerActivity.answer, 1);
                    return;
                }
            }else{
                MultiTrackerActivity.qrCode.clear();
            }
        }else {
            MultiTrackerActivity.qrCode.add(barcode.rawValue);
        }

        stringOne = barcode.rawValue;

        if (stringOne == null || !stringOne.contains("**********")) {
            return;
        }

        Animation animation = MultiTrackerActivity.answer.getAnimation();
        if (!(animation == null)) {
            if (!animation.hasEnded()) {
                return;
            }
        }

        consumeVO = null;
        isExist = true;
        try {
            stringOne = barcode.rawValue;
            String nul = stringOne.substring(0, 10);
            Integer.valueOf(nul.substring(2));
            Integer.valueOf(stringOne.substring(10, 17));
            stringOne.substring(45, 53);
            randomNumber = stringOne.substring(17, 21);
            period = stringOne.substring(10, 17);
            year = Integer.valueOf(period.substring(0, 3)) + 1911;
            month = Integer.parseInt(period.substring(3, 5)) - 1;
            day = Integer.parseInt(period.substring(5));
            cTime = new GregorianCalendar(year, month, day);
            money = stringOne.substring(29, 37);
            Date iTime = new Date(cTime.getTimeInMillis());
            consumeVO = consumeDB.findByNulAndAmountAndRd(nul, randomNumber, iTime);
            if (consumeVO == null) {
                consumeVO = new ConsumeVO();
                consumeVO.setNumber(nul);
                consumeVO.setMaintype("未知");
                consumeVO.setSecondType("未知");
                consumeVO.setDate(iTime);
                consumeVO.setRdNumber(randomNumber);
                consumeVO.setRealMoney(String.valueOf(Integer.parseInt(money, 16)));
                consumeVO.setFixDate(String.valueOf(false));
                consumeVO.setNotify(String.valueOf(false));
                consumeVO.setFkKey(UUID.randomUUID().toString());
                consumeVO.setAuto(false);
                consumeVO.setAutoId(-1);
                consumeVO.setIsWin("0");
                consumeVO.setIsWinNul("0");
                isExist = false;
                consumeDB.insert(consumeVO);
            }

        } catch (Exception e) {
            Log.d("error", e.toString());
            MultiTrackerActivity.answer.setText("QRCode格式有誤!\n請手動兌獎!");
            Log.d("BarcodeGraphic", "QRCode格式有誤! 請手動兌獎");
            consumeVO = null;
            isExist = false;
            Common.setShowAnimation(MultiTrackerActivity.answer, 1);
            return;
        }


        if (consumeVO != null) {

            StringBuilder periodString = new StringBuilder();
            if (isExist) {
                periodString.append("此張發票已兌獎過!\n");
            }
            StringBuilder sPeriod = new StringBuilder();
            StringBuilder bPeriod = new StringBuilder();
            sPeriod.append((year - 1911));
            bPeriod.append((year - 1911));
            periodString.append((year - 1911));

            int realMonth = month + 1;
            switch (realMonth) {
                case 1:
                case 2:
                    sPeriod.append("02");
                    periodString.append("年01-02月");
                    bPeriod.append("年01-02月");
                    break;
                case 3:
                case 4:
                    sPeriod.append("04");
                    periodString.append("年03-04月");
                    bPeriod.append("年03-04月");
                    break;
                case 5:
                case 6:
                    sPeriod.append("06");
                    periodString.append("年05-06月");
                    bPeriod.append("年05-06月");
                    break;
                case 7:
                case 8:
                    sPeriod.append("08");
                    periodString.append("年07-08月");
                    bPeriod.append("年07-08月");
                    break;
                case 9:
                case 10:
                    sPeriod.append("10");
                    periodString.append("年09-10月");
                    bPeriod.append("年09-10月");
                    break;
                case 11:
                case 12:
                    sPeriod.append("12");
                    periodString.append("年11-12月");
                    bPeriod.append("年11-12月");
                    break;
            }

            if (Integer.valueOf(sPeriod.toString()) > max) {
                result = "over";
            } else {

                priceVO = priceDB.getPeriodAll(sPeriod.toString());
                if (priceVO == null) {
                    result = "no";
                } else {
                    List<String> answer =answer(consumeVO.getNumber().substring(2), priceVO);
                    result = answer.get(0);
                    consumeVO.setIsWin(answer.get(0));
                    consumeVO.setIsWinNul(answer.get(1));
                }
            }



            Spannable content;
            int textColor = Color.BLUE;
            int remainColor = Color.parseColor("#5bc0de");
            switch (result) {
                case "no":
                    periodString.append("已過兌獎期限\n 發票號碼 : " + consumeVO.getNumber());
                    content = new SpannableString(periodString.toString());
                    content.setSpan(new ForegroundColorSpan(textColor), periodString.indexOf(":") + 1, periodString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case "over":
                    periodString.append("尚未開獎\n 發票號碼 : " + consumeVO.getNumber());
                    content = new BootstrapText.Builder(context)
                            .addText(periodString.toString())
                            .build();
                    content.setSpan(new ForegroundColorSpan(textColor), periodString.indexOf(":") + 1, periodString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                    break;
                case "N":


                    periodString.append("\n發票號碼:" + consumeVO.getNumber()).append("\n");
                    content = new BootstrapText.Builder(context)
                            .addText(periodString.toString())
                            .addText(" " + "沒有中獎" + " ")
                            .addFontAwesomeIcon(FA_EXCLAMATION)
                            .addText(" " + "再接再厲" + " ")
                            .addFontAwesomeIcon(FA_FLAG).addText(" ")
                            .build();
                    content.setSpan(new ForegroundColorSpan(textColor), 0, periodString.indexOf("發"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    content.setSpan(new ForegroundColorSpan(textColor), periodString.indexOf(":") + 1, periodString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//
                    break;
                default:

                    String winNumber = levelPrice.get("win");
                    periodString.append(winNumber).append("\n中獎號碼").append(consumeVO.getNumber()).append("\n");

                    content = new BootstrapText.Builder(context)
                            .addText(periodString.toString())
                            .addFontAwesomeIcon(FA_STAR)
                            .addText(" " + levelPrice.get(result) + " ")
                            .addFontAwesomeIcon(FA_STAR)
                            .build();


                    String showPrice = " " + levelPrice.get(result) + " ";
                    String showString = content.toString();

                    int priceStart = showString.indexOf(showPrice) - 1;
                    int priceEnd = showString.length();


                    int winNumberEnd = showString.indexOf(winNumber) + winNumber.length();
                    int winNumberStart = winNumberEnd - levelLength.get(result);
                    int nowNumberEnd = showString.indexOf(consumeVO.getNumber()) + consumeVO.getNumber().length();
                    int nowNumberStart = nowNumberEnd - levelLength.get(result);

                    content.setSpan(new ForegroundColorSpan(Color.RED), winNumberStart, winNumberEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    content.setSpan(new ForegroundColorSpan(Color.MAGENTA), nowNumberStart, nowNumberEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    content.setSpan(new ForegroundColorSpan(Color.RED), priceStart, priceEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    Vibrator myVibrator = (Vibrator) this.context.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(500);


                    break;
            }

            if (isExist) {
                content.setSpan(new ForegroundColorSpan(remainColor), periodString.indexOf("此"), periodString.indexOf("!") + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            MultiTrackerActivity.answer.setText(content);
            Log.d("BarcodeGraphic", mBarcode.rawValue);
            Log.d("BarcodeGraphic", content.toString());
            Log.d("BarcodeGraphic", result);
            Common.setShowAnimation(MultiTrackerActivity.answer, 2);
        } else {
            MultiTrackerActivity.answer.setVisibility(View.GONE);
        }

    }


}
