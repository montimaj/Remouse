package project.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Set;

public class BtNetworkState {

    private final BluetoothAdapter mAdapter;
    private ArrayList<BluetoothDevice> mBondedDevices;
    private BluetoothDevice mConnectedDevice;

    public BtNetworkState() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mBondedDevices = new ArrayList<>();
        mConnectedDevice = null;
    }

    public BluetoothAdapter getAdapter() { return mAdapter; }
    public BluetoothDevice getConnectedDevice() { return mConnectedDevice; }
    public ArrayList<BluetoothDevice> getBondedDevices() { return mBondedDevices; }

    public void setConnectedDevice(BluetoothDevice device) { mConnectedDevice = device; }
    public void setConnDeviceToNull() { mConnectedDevice = null; }

    public boolean isConnected() { return mConnectedDevice != null; }
    public boolean isBluetoothEnabled() { return mAdapter.isEnabled(); }

    public void replaceConnDevices(Set newSet) {
        mBondedDevices.clear();
        mBondedDevices.addAll(newSet);
    }

    public String getBondedDeviceAddr(int pos) { return mBondedDevices.get(pos).getAddress(); }
    public String getBondedDeviceName(int pos) { return mBondedDevices.get(pos).getName(); }
    public int noOfBondedDevices() {return mBondedDevices.size(); }

    public void cancelDiscovery() { mAdapter.cancelDiscovery(); }
}
