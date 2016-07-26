package sxccal.edu.android.remouse;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import sxccal.edu.android.remouse.net.ConnectionManager;
import sxccal.edu.android.remouse.net.NsdHelper;

public class ConnectionFragment extends Fragment implements View.OnClickListener {

    private Button mRegister, mDiscover, mConnect;
    private NsdHelper mNsdHelper;
    private ConnectionManager mConnectionManager;
    private static final String TAG = "NsdConnect";
    private static final int REQUEST_INTERNET_ACCESS = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_connect, container, false);
        mRegister= (Button) view.findViewById(R.id.reg_button);
        mDiscover= (Button) view.findViewById(R.id.discover_button);
        mConnect= (Button) view.findViewById(R.id.connect_button);
        mRegister.setOnClickListener(this);
        mDiscover.setOnClickListener(this);
        mConnect.setOnClickListener(this);

        mConnectionManager=new ConnectionManager();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInternetPermission();
        } else {
            initializeNsd();
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.reg_button) {
            if(mConnectionManager.getLocalPort() > -1) {
                mNsdHelper.registerService(mConnectionManager.getLocalPort());
            } else {
                Log.d(TAG, "ServerSocket isn't bound.");
            }

        } else if(view.getId() == R.id.discover_button) {
            mNsdHelper.discoverServices();

        } else if(view.getId() == R.id.connect_button) {
            NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
            if (service != null) {
                Log.d(TAG, "Connecting.");
                mConnectionManager.connectToServer(service.getHost(),
                        service.getPort());
            } else {
                Log.d(TAG, "No service to connect to!");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_INTERNET_ACCESS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeNsd();
                } else {
                    Toast.makeText(this.getContext(), "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
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
        else {
            initializeNsd();
        }
    }

    private void initializeNsd() {
        mNsdHelper = new NsdHelper(this.getContext());
        mNsdHelper.initializeNsd();
    }

}
