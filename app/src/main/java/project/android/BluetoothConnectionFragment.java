package project.android;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.BoringLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import project.android.bluetooth.ConnectThread;
import project.android.bluetooth.DiscoveryThread;
import project.android.net.Client;
import project.android.net.BroadcastReceiverThread;
import project.android.net.ConnectionTask;
import project.android.net.NetworkService;
import project.android.net.ServerInfo;

public class BluetoothConnectionFragment extends Fragment {
    private View mView;
    private ListView mListView;
    private TextView mTextView;
    private BluetoothCustomAdapter mCustomAdapter;
//    private ArrayList<BluetoothDevice> mNetworkList; ////TODO List of connections not Strings
    private AlertDialog mAlertDialog;
    private BluetoothAdapter mBluetoothAdapter;

    private static final int REQUEST_INTERNET_ACCESS = 1001;
    private static final int PAIRING_KEY_LENGTH = 6;

    public static HashMap<String, Boolean> sConnectionAlive = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment_connect_bluetooth, container, false);
            mCustomAdapter = new BluetoothCustomAdapter(getActivity(), R.layout.local_devices, MainActivity.sState.getBondedDevices());
            mListView = (ListView) mView.findViewById(R.id.listView);
            mListView.setAdapter(mCustomAdapter);
            mTextView = (TextView) getActivity().findViewById(R.id.device_conn);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            addItem("1");
//            addItem("2");
//            addItem("3");
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInternetPermission();
        }
//        if(!mInitDiscover) {
//            discoverLocalDevices();
//            mInitDiscover = true;
//        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                BluetoothDevice dev = (BluetoothDevice) adapterView.getAdapter().getItem(position);
                Toast.makeText(getContext(), dev.getName(), Toast.LENGTH_SHORT).show();
                ConnectThread ct = new ConnectThread(dev);
                if(ct.isSocketCreated())
                    new Thread(ct).start();
            }
        });

        new Thread(new DiscoveryThread(getActivity())).start();
        Log.d("BluetoothConnFragment", "foo");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_INTERNET_ACCESS: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this.getActivity(), "Permission denied!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public View getViewByPosition(int pos) {
        final int firstListItemPosition = mListView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + mListView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return mCustomAdapter.getView(pos, null, mListView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return mListView.getChildAt(childIndex);
        }
    }

//    ArrayList<BluetoothDevice> getNetworkList() { return  mNetworkList; } ////TODO

    public String getTextFromList(int position) {
        String name = MainActivity.sState.getBondedDeviceName(position);
        String addr = MainActivity.sState.getBondedDeviceAddr(position);
        return name + addr;
    }

    public void notifyDataSetChanged() { mCustomAdapter.notifyDataSetChanged(); }

//    public void replaceList(Set newSet) {
//        mNetworkList.clear();
//        mNetworkList.addAll(newSet);
//        mCustomAdapter.notifyDataSetChanged();
//    }

//    private void disconnectDevice(int position) {
//        getActivity().stopService(new Intent(getContext(), NetworkService.class));
////        resetIcon(position);
//        sSelectedServer.remove(0);
//    }

    private void getInternetPermission() {
        boolean hasPermission1 = (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermission2 = (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermission3 = (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission1 || !hasPermission2 || !hasPermission3) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    REQUEST_INTERNET_ACCESS);
        }
    }

//    public void addItem(String s) {
//        mNetworkList.add(s);
//        mCustomAdapter.notifyDataSetChanged();
//    }

//    public void removeItem(ServerInfo serverInfo) {
//        sSelectedServer.remove(serverInfo);
//        sConnectionAlive.put(serverInfo.getAddress(), false);
//        mNetworkList.remove(serverInfo);
//        mCustomAdapter.notifyDataSetChanged();
//    }

//    public void resetIcon(ServerInfo serverInfo) {
//        int position = mCustomAdapter.getPosition(serverInfo);
//        ImageView img = (ImageView) getViewByPosition(position).findViewById(R.id.connectIcon);
//        img.setImageResource(R.mipmap.laptop_icon);
//        mTextView.setText(R.string.no_device_connected);
//    }

//    public void resetIcon(int position) {
//        ImageView img = (ImageView) getViewByPosition(position).findViewById(R.id.connectIcon);
//        img.setImageResource(R.mipmap.laptop_icon);
//        mTextView.setText(R.string.no_device_connected);
//    }

//    public void setIcon() {
//        ImageView img = (ImageView) getViewByPosition(mSelectedServerPos).findViewById(R.id.connectIcon);
//        img.setImageResource(R.mipmap.connect_icon);
//        ServerInfo serverInfo = mCustomAdapter.getItem(mSelectedServerPos);
//        if(serverInfo != null) {
//            String s = "\nConnected to:\n" + serverInfo.getServerInfo();
//            s += "\nIP: " + serverInfo.getAddress();
//            mTextView.setText(s);
//        }
//    }

    public void dismissAlertDialog() { if(mAlertDialog != null)    mAlertDialog.dismiss(); }
}
