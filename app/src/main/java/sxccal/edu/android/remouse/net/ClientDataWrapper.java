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

    ClientDataWrapper(String operationType, String data) {
        mOperationType = operationType;
        mData = data;
    }

    ClientDataWrapper(Quaternion quaternionObject) {
        mOperationType = "Mouse_Move";
        mQuaternion = quaternionObject;
    }

    static String getGsonString(Object object) {
        return new Gson().toJson(object);
    }

    String getOperationType() { return mOperationType; }
    String getData() { return mData; }
    Quaternion getQuaternionObject() { return mQuaternion; }
}
