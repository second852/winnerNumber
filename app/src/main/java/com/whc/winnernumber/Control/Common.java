package com.whc.winnernumber.Control;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapText;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.whc.winnernumber.Model.PriceVO;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_CALCULATOR;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_CALENDAR_CHECK_O;
import static com.beardedhen.androidbootstrap.font.FontAwesome.FA_ID_CARD_O;

/**
 * Created by Wang on 2017/11/19.
 */

public class Common {

    public static int length=0;
    public static boolean showfirstgrid = false;
    public static boolean showsecondgrid = false;


    public static SimpleDateFormat sOne = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
    public static SimpleDateFormat sTwo = new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat sThree = new SimpleDateFormat("yyyy 年 MM 月");
    public static SimpleDateFormat sFour = new SimpleDateFormat("yyyy 年");
    public static SimpleDateFormat sDay = new SimpleDateFormat("MM/dd");
    public static SimpleDateFormat sHour = new SimpleDateFormat("hh");
    public static SimpleDateFormat sYear = new SimpleDateFormat("yyy 年 MM 月");
    public static NumberFormat nf = NumberFormat.getNumberInstance();


    public static Screen screenSize;
    public enum Screen {
        xLarge,
        large,
        normal
    }

    public static String doubleRemoveZero(double d)
    {
        int a= (int) d;
        if(a==d)
        {
            return String.valueOf(a);
        }else {
            return String.valueOf(d);
        }
    }


    public static int identify(byte[] bytes) {
        String[] charsetsToBeTested = {"UTF-8", "big5"};
        boolean isRight;
        int i=0;
        for(String c:charsetsToBeTested)
        {
            try {
                Charset charset= Charset.forName(c);
                CharsetDecoder decoder = charset.newDecoder();
                decoder.reset();
                decoder.decode(ByteBuffer.wrap(bytes));
                isRight=true;
            } catch (CharacterCodingException e) {
                isRight=false;
            }
            if(isRight)
            {
              i++;
            }
        }
        return i;
    }

    public static void setScreen(Screen screen,DisplayMetrics displayMetrics)
    {
        if(screen==null)
        {
            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            if(dpWidth>650)
            {
                Common.screenSize= Common.Screen.xLarge;
            }else if(dpWidth>470)
            {
                Common.screenSize= Common.Screen.large;
            }else{
                Common.screenSize= Common.Screen.normal;
            }
        }
    }


    public static void setChargeDB(Context activity)
    {

    }

    public static void setAdView(AdView adView, Context activity)
    {
        try {
            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                }
            });
        }catch (Exception e)
        {
            Log.d("adError",e.toString());
        }
    }



    public static int[] colorlist = {
            Color.parseColor("#FF8888"),
            Color.parseColor("#FFDD55"),
            Color.parseColor("#77DDFF"),
            Color.parseColor("#9999FF"),
            Color.parseColor("#D28EFF"),
            Color.parseColor("#00DDDD")};

    public static int[] getColor(int size)
    {
        int[] cc=new int[size];
        length=colorlist.length;
        for(int i=0;i<size;i++)
        {
            if(i>=length)
            {
                String c="#";
               for(int j=0;j<6;j++)
               {
                   int idex= (int) (Math.random()*16);
                   c=c+colorRadom().get(idex);
               }
               cc[i]= Color.parseColor(c);
            }else{
                cc[i]=colorlist[i];
            }
        }
        return cc;
    }

    public static List<String> colorRadom()
    {
        List<String> color=new ArrayList<>();
        for(int i=0;i<=9;i++)
        {
            color.add(String.valueOf(i));
        }
        for(int i=65;i<=70;i++)
        {
            color.add(String.valueOf((char)i));
        }
        return color;
    }


    //四捨五入
    public static int DoubleToInt(double a)
    {
        double b = new BigDecimal(a)
                .setScale(0, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
        return (int)b;
    }


    //Base64
    @NonNull
    public static String[] Base64Convert(String base64) throws UnsupportedEncodingException {
        byte[] bytes = Base64.decode(base64.trim(), Base64.DEFAULT);
        String debase64 = new String(bytes, "UTF-8");
        return debase64.trim().split(":");
    }

    //Big5
    @NonNull
    public static String Big5Convert(String result) throws UnsupportedEncodingException {
        String answer;
        try {
            int codeNumber = Common.identify(result.getBytes("ISO-8859-1"));
            switch (codeNumber) {
                case 1:
                    answer =new String(result.replaceAll("\\s+", "").getBytes("ISO-8859-1"), "Big5");
                    break;
                default:
                    answer = result;
                    break;
            }
        } catch (Exception e1) {
            answer = result;
        }
        return answer;
    }

    //QRCode-Convert
    public static StringBuilder QRCodeToString(List<String> resultString, StringBuilder sb){
        ArrayList<String> result = new ArrayList<>();
        Double total, price, amount;
        for (String s : resultString) {
            String answer=s.replaceAll("\\s+", "");
            if(answer.length()<=0)
            {
                continue;
            }
            result.add(answer);
            if (result.size() == 3) {
                price = Double.valueOf(Common.onlyNumber(result.get(2)));
                amount = Double.valueOf(Common.onlyNumber(result.get(1)));
                total = price * amount;
                sb.append(result.get(0) + " :\n").append(result.get(2) + " X ").append(result.get(1) + " = ").append(Common.DoubleToInt(total) + "\n");
                result.clear();
            }
        }
        return sb;
    }

    //QRCode Error
    public static StringBuilder QRCodeError(String resultString, StringBuilder sb){
        sb = new StringBuilder();
        String[] resultS=resultString.trim().split(":");
        try {
            int i=0;
            for (String s : resultS) {
                    sb.append(s.replaceAll("\\s+", ""));
                    int j =i % 3;
                    if (j == 2) {
                        sb.append("\n");
                    } else {
                        sb.append(":");
                    }
                i++;
            }
        }catch (Exception e)
        {
            sb = new StringBuilder();
            sb.append("QRCode轉換失敗，請用\"QRCode下載功能\"");
        }
        return sb;
    }



    public static String[] WeekSetSpinnerBS=
            {"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};



    public static String[] DaySetSpinnerBS()
    {
        String[] strings=new String[31];
        for (int i = 0; i < 31; i++) {
            strings[i]=" "+ String.valueOf(i+1) +"日";
        }
        return strings;
    }


    public static String[] MonthSetSpinnerBS()
    {
        String[] strings=new String[12];
        for (int i = 0; i < 12; i++) {
            strings[i]=" " + String.valueOf(i+1) + "月";
        }
        return strings;
    }



    public static String[] DateStatueSetSpinner={"每天","每周","每月","每年"};


    public static List<BootstrapText> DateChoiceSetBsTest(Activity activity, String[] data)
    {
        List<BootstrapText> bootstrapTexts=new ArrayList<>();
        for (String s:data)
        {
            BootstrapText text = new BootstrapText.Builder(activity)
                    .addText(s+" ")
                    .addFontAwesomeIcon(FA_CALCULATOR)
                    .build();
            bootstrapTexts.add(text);
        }
        return bootstrapTexts;
    }


    public static BootstrapText setCarrierSetBsTest(Activity activity, String data)
    {
            BootstrapText text = new BootstrapText.Builder(activity)
                    .addText(data+"  ")
                    .addFontAwesomeIcon(FA_ID_CARD_O)
                    .build();
        return text;
    }

    public static BootstrapText setPriceHandSetBsTest(Activity activity, String data)
    {
        BootstrapText text = new BootstrapText.Builder(activity)
                .addText(data+"  ")
                .addFontAwesomeIcon(FA_CALCULATOR)
                .build();
        return text;
    }

    public static BootstrapText setPeriodSelectCBsTest(Activity activity, String data)
    {
        BootstrapText text = new BootstrapText.Builder(activity)
                .addText(data+"  ")
                .addFontAwesomeIcon(FA_CALENDAR_CHECK_O)
                .build();
        return text;
    }


    public static ArrayList<String> DateStatueSetSpinner()
    {
        ArrayList<String> strings=new ArrayList<>();
        strings.add("每天");
        strings.add("每周");
        strings.add("每月");
        strings.add("每年");
        return strings;
    }
    //純數字
    public static String onlyNumber(String s){
        StringBuilder sb=new StringBuilder();
        Pattern ptn= Pattern.compile("[0-9]|[.]|[-]");
        Matcher mch=ptn.matcher(s);
        while(mch.find()) {
            sb.append(mch.group());
        }
        double dValue= Double.valueOf(sb.toString());
        int value=(int)dValue;
        if(value==dValue)
        {
            return String.valueOf(value);
        }else{
            return sb.toString();
        }
    }




    public static void showToast(Context context, String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {

        }
    }
    //收鍵盤
    public static void  clossKeyword(Activity context)
    {
        InputMethodManager imm = (InputMethodManager)context
        .getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static String Utf8forURL(HashMap<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }


    public static void askPermissions(String s, Activity context, int requestCode) {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組

        String[] permissions={s};
        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(context,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    requestCode);
        }
    }

    //電子發票 Card類別
    public static HashMap<String,String> CardType() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("3J0002","手機條碼");
        hashMap.put("1K0001","悠遊卡");
        hashMap.put("1H0001","一卡通");
        hashMap.put("2G0001","愛金卡");
        hashMap.put("EG0002","家樂福");
        return hashMap;
    }



    //price month
    public static HashMap<Integer,String> getPriceMonth() {
        HashMap<Integer,String> hashMap=new HashMap<>();
        hashMap.put(2,"01-02月\n");
        hashMap.put(4,"03-04月\n");
        hashMap.put(6,"05-06月\n");
        hashMap.put(8,"07-08月\n");
        hashMap.put(10,"09-10月\n");
        hashMap.put(12,"11-12月\n");
        return hashMap;
    }

    //price set
    public static HashMap<String,Integer> getlevellength() {
        HashMap<String,Integer> hashMap=new HashMap<>();
        hashMap.put("super",2);
        hashMap.put("spc",2);
        hashMap.put("first",2);
        hashMap.put("second",3);
        hashMap.put("third",4);
        hashMap.put("fourth",5);
        hashMap.put("fifth",6);
        hashMap.put("sixth",7);
        return hashMap;
    }

    public static HashMap<String,String> getPriceName() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("super","特別獎");
        hashMap.put("spc","特獎");
        hashMap.put("first","頭獎");
        hashMap.put("second","二獎");
        hashMap.put("third","三獎");
        hashMap.put("fourth","四獎");
        hashMap.put("fifth","五獎");
        hashMap.put("sixth","六獎");
        return hashMap;
    }

    public static HashMap<String,String> getOtherType() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("O","其他");
        hashMap.put("0","未知");
        return hashMap;
    }

    public static String getType(String s)
    {
        String a=getOtherType().get(s);
        if(a==null)
        {
          return s;
        }else{
            return a;
        }
    }


    public static HashMap<String,String> getPrice() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("super","1000萬元");
        hashMap.put("spc","200萬元");
        hashMap.put("first","20萬元");
        hashMap.put("second","4萬元");
        hashMap.put("third","1萬元");
        hashMap.put("fourth","4千元");
        hashMap.put("fifth","1千元");
        hashMap.put("sixth","200元");
        return hashMap;
    }

    public static HashMap<String,Integer> getIntPrice() {
        HashMap<String,Integer> hashMap=new HashMap<>();
        hashMap.put("super",10000000);
        hashMap.put("spc",2000000);
        hashMap.put("first",200000);
        hashMap.put("second",40000);
        hashMap.put("third",10000);
        hashMap.put("fourth",4000);
        hashMap.put("fifth",1000);
        hashMap.put("sixth",200);
        return hashMap;
    }

    //自動兌獎
    private String[] level = {"first", "second", "third", "fourth", "fifth", "sixth"};





    private String firsttofourprice(String nul, String pricenul) {
        for (int i = 0; i < 6; i++) {
            if (nul.substring(i).equals(pricenul.substring(i))) {
                return level[i];
            }
        }
        return "N";
    }

    private List<String> anwswer(String nul, PriceVO priceVO) {
        String threenul = nul.substring(5);
        String s;
        List<String> stringList=new ArrayList<>();
        if (nul.equals(priceVO.getSuperPrizeNo())) {
            stringList.add("super");
            stringList.add(priceVO.getSuperPrizeNo());
            return stringList;
        }
        if (nul.equals(priceVO.getSpcPrizeNo())) {
            stringList.add("spc");
            stringList.add(priceVO.getSpcPrizeNo());
            return stringList;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo1());
        if (!s.equals("N")) {
            stringList.add(s);
            stringList.add(priceVO.getFirstPrizeNo1());
            return stringList;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo2());
        if (!s.equals("N")) {
            stringList.add(s);
            stringList.add(priceVO.getFirstPrizeNo2());
            return stringList;
        }
        s = firsttofourprice(nul, priceVO.getFirstPrizeNo3());
        if (!s.equals("N")) {
            stringList.add(s);
            stringList.add(priceVO.getFirstPrizeNo3());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo1())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo1());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo2())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo2());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo3())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo3());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo4())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo4());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo5())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo5());
            return stringList;
        }
        if (threenul.equals(priceVO.getSixthPrizeNo6())) {
            stringList.add("sixth");
            stringList.add(priceVO.getSixthPrizeNo6());
            return stringList;
        }
        stringList.add("N");
        stringList.add("N");
        return stringList;
    }


}
