# react-native-mthirdpush

[![npm version](https://badge.fury.io/js/react-native-mthirdpush.svg)](https://badge.fury.io/js/react-native-mthirdpush)

需要配合极光使用，这边只是提取各大厂商的SDK

[厂商集成文档](https://docs.jiguang.cn/jpush/client/Android/android_3rd_guide)

|   极光版本  |   本模块版本  |
| :---: | :---: |
| 3.2.0 | 2.0.5 |
| 3.6.0 | 3.0.1 |
| 3.8.0 | 3.1.0 |
| 4.0.X | 3.2.0 |
| 4.8.1 | 3.3.0 |
| 4.8.5 | 3.4.0 |
| 4.9.0 | 3.5.0 |
| 5.0.0 | 3.6.0 |
| 5.0.0 | 3.7.0 |
| 5.5.3 | 3.8.0 |

## 安装

``` sh

npm i react-native-mthirdpush --save
react-native link react-native-mthirdpush

```

## 配置

### 编辑 build.gradle 加入以下

android/app/build.gradle

``` gradle
android {
    defaultConfig {
        applicationId "你的包名"
        manifestPlaceholders = [
            JPUSH_PKGNAME : applicationId,
            JPUSH_APPKEY : "你的极光AppKey", //JPush上注册的包名对应的极光AppKey
            JPUSH_CHANNEL : "developer-default", //暂时填写默认值即可.
            MEIZU_APPKEY  : "MZ-aaaa",
            MEIZU_APPID   : "MZ-aaa",
            XIAOMI_APPID  : "aaa",
            XIAOMI_APPKEY : "aaa",
            HUAWEI_APPID  : "appid=aaaa",
            OPPO_APPKEY   : "OP-aaa",
            OPPO_APPID    : "OP-aaa",
            OPPO_APPSECRET: "OP-aaaa",
            VIVO_APPKEY   : "aaaa",
            VIVO_APPID    : "123123",
            HONOR_APPID   : "123123"
        ]
    }
}
```

### 编辑 AndroidManifest.xml 加入以下

``` xml
<!-- 点击通知时，要打开的 activity -->
<activity android:name="你的包名.jpush.OpenClickActivity"
          android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <action android:name="你的包名.jpush.OpenClickActivity"/>
    </intent-filter>
</activity>
```

### 包名下创建 jpush 目录，在目录下创建 OpenClickActivity 文件

```java
package 你的包名.jpush;

import android.content.Intent;
import android.content.pm.PackageManager;
import com.facebook.react.ReactActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.maochunjie.mthirdpush.RNReactNativeMthirdpushModule;

public class OpenClickActivity extends ReactActivity {
    private static final String TAG = "OpenClickActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleOpenClick();
    }

    /**
     * 处理点击事件，当前启动配置的Activity都是使用
     * Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
     * 方式启动，只需要在onCreat中调用此方法进行处理
     */
    private void handleOpenClick() {
        Log.d(TAG, "用户点击打开了通知");
        String data = null;
        //获取华为平台附带的jpush信息
        if (getIntent().getData() != null) {
            data = getIntent().getData().toString();
        }

        //获取fcm或oppo平台附带的jpush信息
        if (TextUtils.isEmpty(data) && getIntent().getExtras() != null) {
            data = getIntent().getExtras().getString("JMessageExtra");
        }

        Log.w(TAG, "msg content is " + String.valueOf(data));
        if (TextUtils.isEmpty(data)) return;
        RNReactNativeMthirdpushModule.handleIntent(this, data);
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
        startActivity(intent);
        finish();
    }
}

```

## 使用

```javascript
import * as Mthirdpush from 'react-native-mthirdpush';

/**
 * 初始化模块
 * Android Only
 */
Mthirdpush.notifyThirdJSDidLoad((resultCode) => {
    if (resultCode === 0) {
    }
});

/**
 * 添加App内推送点击监听
 * Android Only
 */
Mthirdpush.addReceiveThirdNotificationListener(map => {
    if (map !== undefined) {
        //操作
    }
});

/**
 * 打开APP触发，因为点击推送优先触发Android端，但是JS还未载入，所以冷热启动用这个
 * Android Only
 */
Mthirdpush.getLaunchAppNotification(map => {
    if (map !== 0) {
        //操作
    }
});

/**
 * 是否支持OPPO推送
 * Android Only
 */
let isOppoPush = Mthirdpush.isSupportOppoPushSync();

/**
 * 创建推送通道，如果存在则不创建
 * Android Only
 */
Mthirdpush.createNotificationChannel({
    channelId: 'xxx',
    channelName: '订阅通知',
    channelDescription: '测试',
});

/**
 * 删除推送通道
 * Android Only
 */
Mthirdpush.deleteNotificationChannel({
    channelId: 'xxx',
})
```
