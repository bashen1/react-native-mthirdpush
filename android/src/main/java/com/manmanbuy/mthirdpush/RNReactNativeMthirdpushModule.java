
package com.manmanbuy.mthirdpush;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.util.Log;
import com.facebook.react.bridge.*;

import com.facebook.react.modules.core.DeviceEventManagerModule;

public class RNReactNativeMthirdpushModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {
    private static String TAG = "RNReactNativeMthirdpushModule";
    private final ReactApplicationContext reactContext;
    private static ReactApplicationContext mRAC;
    private static ReadableMap cacheInfo;

    /**
     * 消息Id
     **/
    private static final String KEY_MSGID = "msg_id";
    /**
     * 该通知的下发通道
     **/
    private static final String KEY_WHICH_PUSH_SDK = "rom_type";
    /**
     * 通知标题
     **/
    private static final String KEY_TITLE = "n_title";
    /**
     * 通知内容
     **/
    private static final String KEY_CONTENT = "n_content";
    /**
     * 通知附加字段
     **/
    private static final String KEY_EXTRAS = "n_extras";

    private static SparseArray<Callback> sCacheMap;

    public RNReactNativeMthirdpushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
        reactContext.addLifecycleEventListener(this);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNReactNativeMthirdpush";
    }

    @Override
    public void initialize() {
        super.initialize();
        sCacheMap = new SparseArray<>();
    }

    @Override
    public void onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy();
        Log.i(TAG, "onCatalystInstanceDestroy");
        if (null != sCacheMap) {
            sCacheMap.clear();
        }
        mRAC = null;
    }

    @ReactMethod
    public void notifyThirdJSDidLoad(Callback callback) {
        // send cached event
        if (getReactApplicationContext().hasActiveCatalystInstance()) {
            mRAC = getReactApplicationContext();
            callback.invoke(0);
        }
    }

    @ReactMethod
    public void getLaunchAppNotification(Callback callback) {
        // send cached event
        if (getReactApplicationContext().hasActiveCatalystInstance()) {
            mRAC = getReactApplicationContext();
            if (cacheInfo != null) {
                callback.invoke(cacheInfo);
                cacheInfo = null;
            } else {
                callback.invoke(0);
            }
        }
    }

    public static void handleIntent(String data) {
        WritableMap map = Arguments.createMap();
        JSONObject mapJSON = null;
        try {
            mapJSON = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String msgId = mapJSON.optString(KEY_MSGID);
        byte whichPushSDK = (byte) mapJSON.optInt(KEY_WHICH_PUSH_SDK);
        String title = mapJSON.optString(KEY_TITLE);
        String content = mapJSON.optString(KEY_CONTENT);
        String extras = mapJSON.optString(KEY_EXTRAS);

        map.putString("id", String.valueOf(msgId));
        map.putString("content", String.valueOf(content));
        map.putString("title", String.valueOf(title));
        map.putString("extras", String.valueOf(extras));
        map.putString("platform", getPushSDKName(whichPushSDK));
        cacheInfo = map;//缓存数据
        if (mRAC != null) {
            sendEvent(mRAC, "addReceiveThirdNotificationListener", map);
            cacheInfo = null;
        }
    }

    private static void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    private static String getPushSDKName(byte whichPushSDK) {
        String name;
        switch (whichPushSDK) {
            case 0:
                name = "jpush";
                break;
            case 1:
                name = "xiaomi";
                break;
            case 2:
                name = "huawei";
                break;
            case 3:
                name = "meizu";
                break;
            case 4:
                name = "oppo";
                break;
            case 8:
                name = "fcm";
                break;
            default:
                name = "jpush";
        }
        return name;
    }

    @Override
    public void onHostResume() {
        Log.d(TAG, "onHostResume");
    }

    @Override
    public void onHostPause() {
        Log.d(TAG, "onHostPause");
    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, "onHostDestroy");
        mRAC = null;
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d("onNewIntent", ">>>>>>>>>>>>>>>>>");
        Activity activity = getCurrentActivity();
        if (activity != null) {
            activity.setIntent(intent); // 后台运行时点击通知会调用
        }
    }
}