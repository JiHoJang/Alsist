package com.example.alsist;

import android.app.Activity;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;


public class Weather extends Activity {
    ArrayList<String> getdata = new ArrayList<String>();
    //String htmlPageUrl = "http://api.openweathermap.org/data/2.5/weather?&mode=json&id=5125771&APPID=43735c18b195ded966ff4a6ed49f106e";
    private WebView mWebView;
    private WebSettings mWebSettings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mWebView = (WebView)findViewById(R.id.web);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings=mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

        mWebView.loadUrl("https://weather.com/weather/tenday/l/Washington+DC+USDC0001:1:US");
//        https://www.google.co.kr/search?q=washington+weather&oq=washington&aqs=chrome.0.69i59j69i57j69i61l2j0l2.6004j0j4&sourceid=chrome&ie=UTF-8

    }


}