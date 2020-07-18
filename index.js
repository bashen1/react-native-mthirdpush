import {NativeModules, DeviceEventEmitter} from 'react-native';
const {RNReactNativeMthirdpush} = NativeModules;
const listeners = {};
class Mthirdpush {
    static notifyThirdJSDidLoad=(cb)=>{
        return RNReactNativeMthirdpush.notifyThirdJSDidLoad(resultCode => {
            cb(resultCode)
        });
    }

    static addReceiveThirdNotificationListener=(cb)=>{
        listeners[cb] = DeviceEventEmitter.addListener('addReceiveThirdNotificationListener', cb);
    }

    static getLaunchAppNotification=(cb)=>{
        return RNReactNativeMthirdpush.getLaunchAppNotification(res => {
            cb(res)
        });
    }

    static removeListener=(callback)=>{
        if (!listeners[callback]) {
            return
        }
        listeners[callback].remove()
        listeners[callback] = null
    }
}

export default Mthirdpush;
