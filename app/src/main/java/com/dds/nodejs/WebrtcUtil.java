package com.dds.nodejs;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.dds.webrtclib.WebRTCManager;
import com.dds.webrtclib.bean.MediaType;
import com.dds.webrtclib.bean.MyIceServer;
import com.dds.webrtclib.ui.ChatRoomActivity;
import com.dds.webrtclib.ui.ChatSingleActivity;
import com.dds.webrtclib.ws.IConnectEvent;

/**
 * Created by dds on 2019/1/7.
 * android_shuai@163.com
 */
public class WebrtcUtil {


    public static final String HOST = "192.168.1.17";
    public static final String ROOM_ID = "10086";

//    public static final String HOST = "wss://echo.websocket.org";


    // turn and stun
    private static final MyIceServer[] iceServers = {
            new MyIceServer("stun:stun.l.google.com:19302"),

            // 测试地址1
            new MyIceServer("stun:" + HOST + ":3478?transport=udp"),
            new MyIceServer("turn:" + HOST + ":3478?transport=udp",
                    "ddssingsong",
                    "123456"),
            new MyIceServer("turn:" + HOST + ":3478?transport=tcp",
                    "ddssingsong",
                    "123456"),
    };

    // signalling
//    private static String WSS = "wss://" + HOST + "/wss";
    //本地测试信令地址
    private static final String WSS = "ws://192.168.1.17:3000";

    // one to one
    public static void callSingle(Activity activity, String wss, String roomId, boolean videoEnable) {
        if (TextUtils.isEmpty(wss)) {
            wss = WSS;
        }
        if (TextUtils.isEmpty(roomId)) {
            wss = ROOM_ID;
        }
//        wss = "wss://echo.websocket.org";
        WebRTCManager.getInstance().init(wss, iceServers, new IConnectEvent() {
            @Override
            public void onSuccess() {
                Log.i("WebrtcUtil", "onSuccess");
                ChatSingleActivity.openActivity(activity, videoEnable);
            }

            @Override
            public void onFailed(String msg) {
                Log.e("WebrtcUtil", "onFailed:  " + msg);
            }
        });
        WebRTCManager.getInstance().connect(videoEnable ? MediaType.TYPE_VIDEO : MediaType.TYPE_AUDIO, roomId);
    }

    // Videoconferencing
    public static void call(Activity activity, String wss, String roomId) {
        if (TextUtils.isEmpty(wss)) {
            wss = WSS;
        }
        if (TextUtils.isEmpty(roomId)) {
            wss = ROOM_ID;
        }
//        wss = "wss://echo.websocket.org";
        WebRTCManager.getInstance().init(wss, iceServers, new IConnectEvent() {
            @Override
            public void onSuccess() {
                ChatRoomActivity.openActivity(activity);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
        WebRTCManager.getInstance().connect(MediaType.TYPE_MEETING, roomId);
    }


}
