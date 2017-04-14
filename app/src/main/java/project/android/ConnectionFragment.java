package project.android;

import android.Manifest;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import project.android.net.Client;
import project.android.net.BroadcastReceiverThread;
import project.android.net.ConnectionTask;
import project.android.net.NetworkService;
import project.android.net.ServerInfo;

/**
 * Class representing the <code>Fragment</code> that provides GUI frontend for
 * the network module.
 *
 * <p>
 *     It performs the following operations:
 *     <ul>
 *         <li>
 *             Displays the list of available servers with appropriate icons. It
 *             maintains a list of servers available.
 *         </li>
 *         <li>
 *             Displays an alert dialog upon selecting a server from the list to
 *             input the pairing key as shown in the PC.
 *         </li>
 *         <li>
 *             Connects with the respective server upon successful authentication
 *             and starts the communication by creating a TCP socket. It also changes
 *             the icon of the selected server in the list.
 *         </li>
 *     </ul>
 * </p>
 *
 * @see project.android.net.ServerInfo
 * @see project.android.net.Client
 * @see BroadcastReceiverThread
 * @see project.android.net.ConnectionTask
 */
public class ConnectionFragment extends Fragment {

    private View mView;
    private ListView mListView;
    private TextView mTextView;
    private CustomAdapter mCustomAdapter;
    private ArrayList<ServerInfo> mNetworkList;
    private AlertDialog mAlertDialog;
    private boolean mInitDiscover;
    private int mSelectedServerPos;

    public ServerInfo mServerInfo;

    private static final int REQUEST_INTERNET_ACCESS = 1001;
    private static final int PAIRING_KEY_LENGTH = 6;

    public static Client sSecuredClient;
    public static ArrayList<ServerInfo> sSelectedServer = new ArrayList<>();
    public static HashMap<String, Boolean> sConnectionAlive = new HashMap<>();

    /**
     * Overrides the
     * <code>Fragment.onCreateView(LayoutInflater, ViewGroup, Bundle)</code>
     * method of the Android API. <br/>
     *
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The <code>LayoutInflater</code> object that can be used
     *                 to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI
     *                  should be attached to. The fragment should not add the view
     *                  itself, but this can be used to generate the
     *                  <code>LayoutParams</code> of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return the <code>View</code> for the fragment's UI, or <code>null</code>.
     */
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
            mTextView = (TextView) getActivity().findViewById(R.id.device_conn);
        }
        return mView;
    }

    /**
     * Overrides the <code>Fragment.onViewCreated(View, Bundle)</code>.
     * method of the Android API. <br/>
     *
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     *
     * @param view The <code>View</code> returned by
     *             {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
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
                            mSelectedServerPos = position;
                            startCommunication(serverInfo);
                        }
                    } else disconnectDevice(position);
                }
            }
        });
    }

    /**
     * Overrides the <code>Fragment.onResume()</code> method
     * of the Android API. <br/>
     *
     * Called when the activity will start interacting with
     * the user.
     */
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Overrides the <code>Fragment.onPause()</code> method
     * of the Android API. <br/>
     *
     * Called when the system is about to start resuming a
     * previous activity.
     */
    @Override
    public void onPause() {
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    /**
     * Override the
     * <code>Fragment.onRequestPermissionsResult(int, String[], int[])</code>
     * method of the Android API.
     *
     * @param requestCode The request code.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either <code>PERMISSION_GRANTED</code> or
     *                     <code>PERMISSION_DENIED</code>. Never null.
     */
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

    /**
     * Returns the <code>View</code> of the clicked list item.
     *
     * @param pos Position of the item.
     * @return the <code>View</code> of the clicked list item.
     */
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

    /**
     * Returns a list of <code>ServerInfo</code> objects.
     *
     * @return a list of {@link project.android.net.ServerInfo}
     *         objects.
     * @see project.android.net.ServerInfo
     */
    ArrayList<ServerInfo> getNetworkList() { return  mNetworkList; }

    private void disconnectDevice(int position) {
        getActivity().stopService(new Intent(getContext(), NetworkService.class));
        resetIcon(position);
        sSelectedServer.remove(0);
    }

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

    private void startCommunication (final ServerInfo serverInfo) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sSecuredClient = new Client(serverInfo.getAddress());
                } catch (IOException ignored) {}
            }
        }).start();

        EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        editText.setSelection(editText.getText().length());
        editText.setHint("Password");
        editText.setTextSize(18);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(PAIRING_KEY_LENGTH);
        editText.setFilters(FilterArray);

        displayAlertDialog(editText, serverInfo);
    }

    private void displayAlertDialog(final EditText editText, final ServerInfo serverInfo) {
        mAlertDialog = new AlertDialog.Builder(getContext())
                .setView(editText)
                .setTitle("Connect to PC")
                .setMessage("Enter pairing key as shown in PC")
                .setPositiveButton(R.string.Send, null)
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .create();

        mAlertDialog.show();
        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pairingKey = editText.getText().toString();
                serverInfo.setPairingKey(pairingKey);
                if(sSecuredClient != null) {
                    mServerInfo = serverInfo;
                    new ConnectionTask().execute(serverInfo);
                }
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sSecuredClient != null)  {
                    try {
                        sSecuredClient.sendStopSignal(false);
                        sSecuredClient.close();
                        sSelectedServer.remove(serverInfo);
                    } catch (IOException ignored) {}
                    mAlertDialog.dismiss();
                }
            }
        });
    }

    private void discoverLocalDevices() {
        WifiManager wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock lock = wifi.createMulticastLock("remouseMulticastLock");
        BroadcastReceiverThread broadcastReceiverThread = new BroadcastReceiverThread(getActivity(), lock);
        new Thread(broadcastReceiverThread).start();
    }

    /**
     * Adds the <code>ServerInfo</code> object of an active server
     * to the list of servers.
     *
     * @param serverInfo the active {@link project.android.net.ServerInfo}
     *                   object.
     * @see project.android.net.ServerInfo
     */
    public void addItem(ServerInfo serverInfo) {
        mNetworkList.add(serverInfo);
        mCustomAdapter.notifyDataSetChanged();
    }

    /**
     * Removes the <code>ServerInfo</code> object of an inactive server
     * from the list of servers.
     *
     * @param serverInfo the inactive {@link project.android.net.ServerInfo}
     *                   object.
     * @see project.android.net.ServerInfo
     */
    public void removeItem(ServerInfo serverInfo) {
        sSelectedServer.remove(serverInfo);
        sConnectionAlive.put(serverInfo.getAddress(), false);
        mNetworkList.remove(serverInfo);
        mCustomAdapter.notifyDataSetChanged();
    }

    /**
     * Sets an icon for an inactive server.
     *
     * @param serverInfo the {@link project.android.net.ServerInfo}
     *                   of the inactive server.
     * @see project.android.net.ServerInfo
     */
    public void resetIcon(ServerInfo serverInfo) {
        int position = mCustomAdapter.getPosition(serverInfo);
        ImageView img = (ImageView) getViewByPosition(position).findViewById(R.id.connectIcon);
        img.setImageResource(R.mipmap.laptop_icon);
        mTextView.setText(R.string.no_device_connected);
    }

    /**
     * Sets an icon for an inactive server.
     *
     * @param position List position of the inactive server.
     */
    public void resetIcon(int position) {
        ImageView img = (ImageView) getViewByPosition(position).findViewById(R.id.connectIcon);
        img.setImageResource(R.mipmap.laptop_icon);
        mTextView.setText(R.string.no_device_connected);
    }

    /**
     * Sets an icon for an active server.
     */
    public void setIcon() {
        ImageView img = (ImageView) getViewByPosition(mSelectedServerPos).findViewById(R.id.connectIcon);
        img.setImageResource(R.mipmap.connect_icon);
        ServerInfo serverInfo = mCustomAdapter.getItem(mSelectedServerPos);
        if(serverInfo != null) {
            String s = "\nConnected to:\n" + serverInfo.getServerInfo();
            s += "\nIP: " + serverInfo.getAddress();
            mTextView.setText(s);
        }
    }

    /**
     * Method to dismiss the alert dialog prompting the pairing key input.
     */
    public void dismissAlertDialog() { if(mAlertDialog != null)    mAlertDialog.dismiss(); }
}