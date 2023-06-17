import {NativeModules, DeviceEventEmitter} from 'react-native';
const {RNReactNativeMthirdpush} = NativeModules;
const listeners = {};
class Mthirdpush {
    static notifyThirdJSDidLoad = (cb) => {
        return RNReactNativeMthirdpush.notifyThirdJSDidLoad(resultCode => {
            cb(resultCode);
        });
    }

    static addReceiveThirdNotificationListener = (cb) => {
        listeners[cb] = DeviceEventEmitter.addListener('addReceiveThirdNotificationListener', cb);
    }

    static getLaunchAppNotification = (cb) => {
        return RNReactNativeMthirdpush.getLaunchAppNotification(res => {
            cb(res);
        });
    }

    static removeListener = (callback) => {
        if (!listeners[callback]) {
            return;
        }
        listeners[callback].remove();
        listeners[callback] = null;
    }

    static isSupportOppoPushSync = () => {
        return RNReactNativeMthirdpush.isSupportOppoPushSync();
    }

    static createNotificationChannel = (params = {}) => {
        let channelId = params.channelId;
        let channelName = params.channelName;
        let channelDescription = params.channelDescription;
        if (channelId !== undefined && channelName !== undefined && channelDescription !== undefined) {
            RNReactNativeMthirdpush.createNotificationChannel({
                channelId: channelId,
                channelName: channelName,
                channelDescription: channelDescription,
            });
        }
    }

    static deleteNotificationChannel = (params = {}) => {
        let channelId = params.channelId;
        if (channelId !== undefined) {
            RNReactNativeMthirdpush.deleteNotificationChannel({
                channelId: channelId,
            });
        }
    }
}

export default Mthirdpush;
