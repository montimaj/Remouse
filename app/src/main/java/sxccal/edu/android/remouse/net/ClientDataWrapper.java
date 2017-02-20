package sxccal.edu.android.remouse.net;

import com.google.gson.Gson;

import sxccal.edu.android.remouse.sensor.representation.Quaternion;

/**
 * @author Sayantan Majumdar
 */

class ClientDataWrapper {

    private String mOperationType;
    private String mData;
    private Quaternion mQuaternion;
    private boolean mIsInitQuat;

    ClientDataWrapper(String operationType, String data) {
        mOperationType = operationType;
        mData = data;
    }

    ClientDataWrapper(Quaternion quaternionObject, boolean isInitQuat) {
        mOperationType = "Mouse_Move";
        mIsInitQuat = isInitQuat;
        mQuaternion = quaternionObject;
    }

    static String getGsonString(Object object) {
        return new Gson().toJson(object);
    }

    String getOperationType() { return mOperationType; }
    String getData() { return mData; }
    boolean isInitQuat() { return mIsInitQuat; }
    Quaternion getQuaternionObject() { return mQuaternion; }
}
