package sxccal.edu.android.remouse;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import sxccal.edu.android.remouse.net.ClientIOThread;

/**
 * @author Sayantan Majumdar
 */

public class ConnectionFragment extends ListFragment {

    private SwitchCompat mSwitch;
    private static AlertDialog sAlertDialog;
    private static ArrayList<String> sNetWorkList = new ArrayList<>();
    private static ArrayAdapter<String> sAdapter;

    public static Client sSecuredClient;

    public static boolean sListItemClicked;
    public static boolean sSwitchChecked;
    private static final int REQUEST_INTERNET_ACCESS = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_connect, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInternetPermission();
        }
        mSwitch = (SwitchCompat) view.findViewById(R.id.switch1);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    sSwitchChecked = true;
                    mSwitch.setChecked(true);
                    if(!sListItemClicked && (sAdapter == null || sAdapter.isEmpty())) {
                        sAdapter = new ArrayAdapter<>(getActivity(), android.R.layout
                                .simple_selectable_list_item, sNetWorkList);
                        setListAdapter(sAdapter);
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(!sListItemClicked && (sAdapter == null || sAdapter.isEmpty())) {
                                discoverLocalDevices();
                            }
                        }
                    }).start();
                } else {
                    sSwitchChecked = false;
                    mSwitch.setChecked(false);
                    getActivity().stopService(new Intent(getContext(), NetworkService.class));
                    sListItemClicked = false;
                }
            }
        });

        return view;
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

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        String address = sAdapter.getItem(position);
        Log.d("ListItem: ",address);
        if (!sListItemClicked)  startCommunication(address);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sSecuredClient = new Client(address);
                } catch (IOException e) {}
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
        alert.setView(editText);

        alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int button) {
                final String pairingKey = editText.getText().toString();
                if(sSecuredClient != null) {
                    try {
                        new Thread(new ClientIOThread(getActivity(), pairingKey, address)).start();
                    } catch (IOException e) {}
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
                    } catch (IOException e) {}
                    sListItemClicked = false;
                    dialog.dismiss();
                }
            }
        });

        alert.setTitle("Connect to PC");
        alert.setCancelable(false);
        sAlertDialog = alert.create();
        sAlertDialog.show();
    }

    private void discoverLocalDevices() {
        ClientConnectionThread clientConnectionThread = new ClientConnectionThread(getContext(), getActivity());
        new Thread(clientConnectionThread).start();
    }

    public static void addItems(HashSet<String> addressSet) {
        if(!sSwitchChecked) {
            sNetWorkList.clear();
        } else {
            for (String address : addressSet) {
                if (!sNetWorkList.contains(address)) sNetWorkList.add(address);
            }
        }
        sAdapter.notifyDataSetChanged();
    }

    public static void removeItem(String address) {
        sNetWorkList.remove(address);
        if(sAdapter != null)    sAdapter.notifyDataSetChanged();
    }

    public static void dismissAlertDialog() {
        if(sAlertDialog != null)    sAlertDialog.dismiss();
    }
}