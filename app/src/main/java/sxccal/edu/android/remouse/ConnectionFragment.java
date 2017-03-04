package sxccal.edu.android.remouse;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import sxccal.edu.android.remouse.net.Client;
import sxccal.edu.android.remouse.net.ClientConnectionThread;
import sxccal.edu.android.remouse.net.ClientIOThread;
import sxccal.edu.android.remouse.net.ServerInfo;

/**
 * @author Sayantan Majumdar
 */

public class ConnectionFragment extends Fragment {

    private View mView;
    private ListView mListView;
    private CustomAdapter mCustomAdapter;
    private ArrayList<ServerInfo> mNetworkList;
    private AlertDialog mAlertDialog;
    private boolean mInitDiscover;

    public int listItemPos;

    private static final int REQUEST_INTERNET_ACCESS = 1001;
    private static final int PAIRING_KEY_LENGTH = 6;

    public static Client sSecuredClient;
    public static ArrayList<ServerInfo> sSelectedServer = new ArrayList<>();
    public static HashMap<String, Boolean> sConnectionAlive = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment_connect, container, false);
            mNetworkList = new ArrayList<>();
            mCustomAdapter = new CustomAdapter(getActivity(), R.layout.local_devices, mNetworkList);
            mListView = (ListView) mView.findViewById(R.id.listView);
            mListView.setAdapter(mCustomAdapter);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInternetPermission();
        }
        if(!mInitDiscover) {
            discoverLocalDevices();
            mInitDiscover = true;
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ServerInfo serverInfo = (ServerInfo) adapterView.getAdapter().getItem(position);
                if (serverInfo != null) {
                    if (!sSelectedServer.contains(serverInfo))  {
                        sSelectedServer.add(serverInfo);
                        if(sSelectedServer.size() > 1) {
                            Toast.makeText(getContext(), "Device already connected! " +
                                    "Disconnect to connect another device", Toast.LENGTH_SHORT).show();
                            sSelectedServer.remove(serverInfo);
                        } else {
                            listItemPos = position;
                            startCommunication(serverInfo);
                        }
                    } else disconnectDevice(position);
                }
            }
        });
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

    ArrayList<ServerInfo> getNetworkList() { return  mNetworkList; }

    private void disconnectDevice(int position) {
        getActivity().stopService(new Intent(getContext(), NetworkService.class));
        setImage(position, R.mipmap.laptop_icon);
        sSelectedServer.remove(0);
    }

    private void getInternetPermission() {
        boolean hasPermission1 = (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermission2 = (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission1 || !hasPermission2) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.INTERNET, Manifest.permission.CHANGE_WIFI_MULTICAST_STATE},
                    REQUEST_INTERNET_ACCESS);
        }
    }

    private void startCommunication (final ServerInfo serverInfo) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sSecuredClient = new Client(serverInfo.getAddress());
                } catch (IOException ignored) {}
            }
        }).start();

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Enter pairing key as shown in PC");
        final EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        editText.setSelection(editText.getText().length());
        editText.setHint("Password");
        editText.setTextSize(18);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(PAIRING_KEY_LENGTH);
        editText.setFilters(FilterArray);
        alert.setView(editText);

        alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int button) {
                final String pairingKey = editText.getText().toString();
                if(sSecuredClient != null) {
                    try {
                        new Thread(new ClientIOThread(getActivity(), pairingKey, serverInfo)).start();
                    } catch (IOException ignored) {}
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(sSecuredClient != null)  {
                    try {
                        sSecuredClient.sendStopSignal(false);
                        sSecuredClient.close();
                        sSelectedServer.remove(serverInfo);
                    } catch (IOException ignored) {}
                    dialog.dismiss();
                }
            }
        });

        alert.setTitle("Connect to PC");
        alert.setCancelable(false);
        mAlertDialog = alert.create();
        mAlertDialog.show();
    }

    private void discoverLocalDevices() {
        WifiManager wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock lock = wifi.createMulticastLock("remouseMulticastLock");
        ClientConnectionThread clientConnectionThread = new ClientConnectionThread(getActivity(), lock);
        new Thread(clientConnectionThread).start();
    }

    public void addItem(ServerInfo serverInfo) {
        mNetworkList.add(serverInfo);
        mCustomAdapter.notifyDataSetChanged();
    }

    public void removeItem(ServerInfo serverInfo) {
        sSelectedServer.remove(serverInfo);
        sConnectionAlive.put(serverInfo.getAddress(), false);
        mNetworkList.remove(serverInfo);
        mCustomAdapter.notifyDataSetChanged();
    }

    public void setImage(ServerInfo serverInfo, int imageId) {
        int position = mCustomAdapter.getPosition(serverInfo);
        ImageView img = (ImageView) getViewByPosition(position).findViewById(R.id.connectIcon);
        img.setImageResource(imageId);
    }

    public void setImage(int position, int imageId) {
        ImageView img = (ImageView) getViewByPosition(position).findViewById(R.id.connectIcon);
        img.setImageResource(imageId);
    }

    public void dismissAlertDialog() { if(mAlertDialog != null)    mAlertDialog.dismiss(); }
}