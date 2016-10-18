package sxccal.edu.android.remouse;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import sxccal.edu.android.remouse.net.Client;
import sxccal.edu.android.remouse.net.ClientConnectionThread;
import sxccal.edu.android.remouse.net.server.NetworkManager;
import sxccal.edu.android.remouse.net.server.NetworkThread;

import static sxccal.edu.android.remouse.MouseFragment.sConnectionAlive;

/**
 * @author Sayantan Majumdar
 */

public class ConnectionFragment extends ListFragment {

    private static ArrayList<String> mNetWorkList = new ArrayList<>();
    private static ArrayAdapter<String> mAdapter;
    private NetworkManager mNetworkManager;
    private SwitchCompat mSwitch;

    public static boolean sListItemClicked;
    public static Client sClient;
    private static boolean sMouseStopped;
    private static final int REQUEST_INTERNET_ACCESS = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_connect, container, false);
        mSwitch = (SwitchCompat) view.findViewById(R.id.switch1);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mSwitch.setChecked(true);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getInternetPermission();
                    }
                    if(!sListItemClicked && (mAdapter == null || mAdapter.isEmpty())) {
                        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_selectable_list_item, mNetWorkList);
                        setListAdapter(mAdapter);
                        discoverLocalDevices();
                    }

                } else {
                    mSwitch.setChecked(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            closeActiveConnections();
                        }
                    }).start();

                    mAdapter.clear();
                    sListItemClicked = false;
                    try {
                        if(mNetworkManager != null) mNetworkManager.stopServer();
                    } catch(IOException e) {}
                }
            }
        });

        return view;
    }

    private void closeActiveConnections() {
        sConnectionAlive = false;
        try {
            sClient.sendMouseData(-1, -1, -1);
            sMouseStopped = sClient.getStopSignal();
        } catch (IOException e) { sMouseStopped = false; }
        try {
            if(sClient != null && sMouseStopped)    sClient.close();
        } catch (IOException e) {}
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

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        String address = mAdapter.getItem(position);
        Log.d("ListItem: ",address);
        if (!sListItemClicked) {
            startCommunication(address);
            sListItemClicked = true;
        }
    }

    private void getInternetPermission() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.INTERNET},
                    REQUEST_INTERNET_ACCESS);
        }
    }

    private void startCommunication (final String address) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Enter pairing key as shown in PC");
        final EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        editText.setSelection(editText.getText().length());
        editText.setHint("Password");
        editText.setTextSize(14);
        alert.setView(editText);

        alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int button) {
                final String pairingKey = editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        makeConnection(address, pairingKey);
                    }
                }).start();
            }
        });

        alert.setTitle("Connect to PC");
        alert.setCancelable(false);
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void makeConnection(final String address, String pairingKey) {
        try {
            sClient = new Client(address, NetworkManager.TCP_PORT);
            sClient.sendPairingKey(pairingKey);
            System.out.println("Pairing key: " + pairingKey);
            final Activity activity = getActivity();
            if (!sClient.getConfirmation()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Incorrect Pin! Try connecting again",
                                Toast.LENGTH_LONG).show();
                    }
                });
                sListItemClicked = false;
                sClient.close();
            } else {
                try {
                    if (mNetworkManager != null)    mNetworkManager.stopServer();
                } catch(IOException e) { e.printStackTrace(); }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Connected to " + address +
                                        "\nOpen either Mouse or Keyboard Tabs from the navigation bar",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void discoverLocalDevices() {
        mNetworkManager = new NetworkManager();

        ClientConnectionThread clientConnectionThread = new ClientConnectionThread(getContext(), getActivity());
        NetworkThread networkThread = new NetworkThread(mNetworkManager);

        new Thread(clientConnectionThread).start();
        new Thread(networkThread).start();
    }

    public static void addItems(HashSet<String> address) {
        for(String addr: address)   mNetWorkList.add(addr);
        mAdapter.notifyDataSetChanged();
    }
}