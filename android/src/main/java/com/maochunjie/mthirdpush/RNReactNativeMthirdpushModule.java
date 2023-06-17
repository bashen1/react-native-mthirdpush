package com.maochunjie.mthirdpush;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import androidx.annotation.Nullable;

import android.os.Build;
import android.util.SparseArray;
import android.util.Log;

import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.heytap.msp.push.HeytapPushManager;

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

    /**
     * 是否支持 OPPO PUSH
     */
    @ReactMethod(isBlockingSynchronousMethod = true)
    public boolean isSupportOppoPushSync() {
        boolean res = false;
        try {
            res = HeytapPushManager.isSupportPush(reactContext);
        } catch (Exception ignored) {
        }
        return res;
    }

    /**
     * 创建推送渠道
     */
    @ReactMethod
    public void createNotificationChannel(ReadableMap data) {
        try {
            NotificationManager manager = (NotificationManager) reactContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
                String channelId = data.getString("channelId");
                String channelName = data.getString("channelName");
                String channelDescription = data.getString("channelDescription");
                int importance = NotificationManager.IMPORTANCE_HIGH; // 使用系统默认的声音与震动

                NotificationChannel channel = manager.getNotificationChannel(channelId);
                if (channel == null) {
                    channel = new NotificationChannel(channelId, channelName, importance);
                    channel.setDescription(channelDescription);
                    channel.enableLights(true);
                    channel.enableVibration(true);
                    manager.createNotificationChannel(channel);
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 删除推送渠道
     */
    @ReactMethod
    public void deleteNotificationChannel(ReadableMap data) {
        try {
            NotificationManager manager = (NotificationManager) reactContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && manager != null) {
                String channelId = data.getString("channelId");

                NotificationChannel channel = manager.getNotificationChannel(channelId);
                if (channel != null) {
                    manager.deleteNotificationChannel(channelId);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void handleIntent(Activity context, String data) {
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

        map.putString("messageID", String.valueOf(msgId));
        map.putString("content", String.valueOf(content));
        map.putString("title", String.valueOf(title));
        map.putString("extras", String.valueOf(extras));
        map.putString("platform", getPushSDKName(whichPushSDK));
        //上报点击事件
        JPushInterface.reportNotificationOpened(context, msgId, whichPushSDK);

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