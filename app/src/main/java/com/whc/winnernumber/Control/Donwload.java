package com.whc.winnernumber.Control;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whc.winnernumber.DataBase.PriceDB;
import com.whc.winnernumber.DataBase.WinnerDB;
import com.whc.winnernumber.R;
import com.whc.winnernumber.model.PriceVO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;


public class Donwload extends Fragment {

    private Activity activity;
    private CircleProgressView circleProgressView;
    private TextView remain;
    private AdView adView;
    private PriceDB priceDB;
    private Gson gson;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            activity = getActivity();
        }
        WinnerDB winnerDB = new WinnerDB(activity);
        priceDB = new PriceDB(winnerDB.getReadableDatabase());
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    remain.setText("更新失敗!");
                    circleProgressView.setValue(100);
                    break;
                case 1:
                    HashMap<String, Integer> monthYear = (HashMap<String, Integer>) msg.obj;
                    remain.setText("正在下載" + monthYear.get("year") + "年" + monthYear.get("month") + "月兌獎號碼..");
                    circleProgressView.setValue(msg.arg1);
                    break;
                case 2:
                    remain.setText("下載完成，正在更新!");
                    circleProgressView.setValue(100);
                    toNewFragment();
                    break;
            }
        }
    };


    private Runnable downloadData = new Runnable() {
        @Override
        public void run() {
            if (priceDB.getAll().size() <= 0) {
                searchAllPrice();
            } else {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                String maxPeriod = priceDB.findMaxPeriod();
                int maxYear = new Integer(maxPeriod.substring(0, maxPeriod.length() - 2)) + 1911;
                int maXMonth = new Integer(maxPeriod.substring(maxPeriod.length() - 2));
                int differentMonth = (year - maxYear) * 12 + (month - maXMonth);
                if (differentMonth >= 6) {
                    searchAllPrice();
                } else {
                    if (differentMonth < 2) {
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);

                        return;
                    } else if (differentMonth == 2) {
                        if (day < 25) {
                            Message message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                            return;
                        }
                    }
                    //下載到檔案庫月份
                    String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?";
                    StringBuffer period;
                    month = month + 1;
                    if (month % 2 == 1) {
                        month = month - 1;
                    }
                    int total = 0;
                    HashMap<String, Integer> monthYear = new HashMap<>();
                    while (true) {
                        period = new StringBuffer();
                        if (month <= 0) {
                            month = 12 + month;
                            year = year - 1;
                        }
                        period.append((year - 1911));
                        if (String.valueOf(month).length() == 1) {
                            period.append("0");
                        }
                        period.append(month);
                        String p = period.toString();
                        if (p.equals(maxPeriod)) {
                            break;
                        }
                        if (Integer.valueOf(maxPeriod) > Integer.valueOf(p)) {
                            break;
                        }
                        HashMap<String, String> data = getPriceMap(p);
                        String jsonin = getRemoteData(url, data);
                        if (jsonin.equals("timeout") || jsonin.equals("error")) {
                            Message message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                            break;
                        }
                        JsonObject js = gson.fromJson(jsonin, JsonObject.class);
                        String code = js.get("code").getAsString().trim();
                        if (code.equals("200")) {
                            PriceVO priceVO = jsonToPriceVO(jsonin);
                            priceDB.insert(priceVO);
                            //顯示%
                            Message message = new Message();
                            message.what = 1;
                            total = total + 1;
                            message.arg1 = (total / differentMonth) * 100;
                            monthYear.put("year", year);
                            monthYear.put("month", month);
                            message.obj = monthYear;
                            handler.sendMessage(message);
                            Log.d("insert", "insert price :" + priceVO.getInvoYm());
                        }
                        month = month - 2;
                    }
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                }
            }
        }
    };


    private void toNewFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.body, new PriceActivity());
        fragmentTransaction.commit();
    }


    private void searchAllPrice() {
        String url = "https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp?";
        //從今天日期 往後下載
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        //取雙數月份
        if (month % 2 == 1) {
            month = month - 1;
        }
        StringBuffer period;
        HashMap<String, Integer> monthYear = new HashMap<>();
        int total = 0;
        for (int i = 0; i < 7; i++) {
            period = new StringBuffer();
            if (month <= 0) {
                month = 12 + month;
                year = year - 1;
            }
            period.append((year - 1911));
            if (String.valueOf(month).length() == 1) {
                period.append("0");
            }
            period.append(month);
            Log.d("period", "insert period :" + period.toString());
            HashMap<String, String> data = getPriceMap(period.toString());
            String jsonin = getRemoteData(url, data);
            if (jsonin.equals("timeout") || jsonin.equals("error")) {
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
                break;
            }
            JsonObject js = gson.fromJson(jsonin, JsonObject.class);
            String code = js.get("code").getAsString().trim();
            if (code.equals("200")) {
                PriceVO priceVO = jsonToPriceVO(jsonin);
                priceDB.insert(priceVO);
                //顯示%
                Message message = new Message();
                message.what = 1;
                total = total + 1;
                message.arg1 = total * 16;
                monthYear.put("year", year);
                monthYear.put("month", month);
                message.obj = monthYear;
                handler.sendMessage(message);
                Log.d("insert", "insert price :" + priceVO.getInvoYm());
            }
            month = month - 2;
        }
        Message message = new Message();
        message.what = 2;
        handler.sendMessage(message);
    }

    private PriceVO jsonToPriceVO(String jsonin) {
        Gson gson = new Gson();
        JsonObject js = gson.fromJson(jsonin, JsonObject.class);
        PriceVO priceVO = new PriceVO();
        priceVO.setInvoYm(js.get("invoYm").getAsString());
        priceVO.setSuperPrizeNo(js.get("superPrizeNo").getAsString());
        priceVO.setSpcPrizeNo(js.get("spcPrizeNo").getAsString());
        priceVO.setFirstPrizeNo1(js.get("firstPrizeNo1").getAsString());
        priceVO.setFirstPrizeNo2(js.get("firstPrizeNo2").getAsString());
        priceVO.setFirstPrizeNo3(js.get("firstPrizeNo3").getAsString());
        priceVO.setSixthPrizeNo1(ifnull(js.get("sixthPrizeNo1").getAsString()));
        priceVO.setSixthPrizeNo2(ifnull(js.get("sixthPrizeNo2").getAsString()));
        priceVO.setSixthPrizeNo3(ifnull(js.get("sixthPrizeNo3").getAsString()));
        priceVO.setSuperPrizeAmt(js.get("superPrizeAmt").getAsString());
        priceVO.setSpcPrizeAmt(js.get("spcPrizeAmt").getAsString());
        priceVO.setFirstPrizeAmt(js.get("firstPrizeAmt").getAsString());
        priceVO.setSecondPrizeAmt(js.get("secondPrizeAmt").getAsString());
        priceVO.setThirdPrizeAmt(js.get("thirdPrizeAmt").getAsString());
        priceVO.setFourthPrizeAmt(js.get("fourthPrizeAmt").getAsString());
        priceVO.setFifthPrizeAmt(js.get("fifthPrizeAmt").getAsString());
        priceVO.setSixthPrizeAmt(js.get("sixthPrizeAmt").getAsString());
        priceVO.setSixthPrizeNo4(ifnull(js.get("sixthPrizeNo4").getAsString()));
        priceVO.setSixthPrizeNo5(ifnull(js.get("sixthPrizeNo5").getAsString()));
        priceVO.setSixthPrizeNo6(ifnull(js.get("sixthPrizeNo6").getAsString()));
        return priceVO;
    }

    private String ifnull(String a) {
        if (a == null || a.trim().length() == 0) {
            return "0";
        }
        return a;
    }

    private HashMap<String, String> getPriceMap(String date) {
        HashMap<String, String> data = new HashMap();
        data.put("version", "0.2");
        data.put("action", "QryWinningList");
        data.put("invTerm", date);
        data.put("UUID", "second");
        data.put("appID", "EINV3201711184648");
        return data;
    }


    private String getRemoteData(String url, HashMap data) {
        StringBuilder jsonIn = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(2000);
            conn.setConnectTimeout(2000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(data));
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    jsonIn.append(line);
                }
                Log.d("jsonIn", "jsonIn " + jsonIn);
            }
        } catch (SocketTimeoutException e) {
            jsonIn = new StringBuilder();
            jsonIn.append("timeout");
        } catch (Exception e) {
            Log.d("jsonIn", "error" + e.getMessage() + e.toString());
            jsonIn = new StringBuilder();
            jsonIn.append("error");
        } finally {
            conn.disconnect();
        }
        return jsonIn.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.download, container, false);
        circleProgressView = (CircleProgressView) view.findViewById(R.id.circleView);
        remain = (TextView) view.findViewById(R.id.remain);
        adView = (AdView) view.findViewById(R.id.adView);
        Common.setAdView(adView, activity);
        gson = new Gson();
        new Thread(downloadData).start();
        return view;
    }

}
