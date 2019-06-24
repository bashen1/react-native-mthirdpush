import {NativeModules, DeviceEventEmitter} from 'react-native';

const {RNReactNativeMthirdpush} = NativeModules;

export function notifyThirdJSDidLoad(cb) {
    return RNReactNativeMthirdpush.notifyThirdJSDidLoad(resultCode => {
        cb(resultCode)
    });
}

export function addReceiveThirdNotificationListener(cb) {
    DeviceEventEmitter.addListener('addReceiveThirdNotificationListener', cb);
}

export function getLaunchAppNotification(cb) {
    return RNReactNativeMthirdpush.getLaunchAppNotification(res => {
        cb(res)
    });
}