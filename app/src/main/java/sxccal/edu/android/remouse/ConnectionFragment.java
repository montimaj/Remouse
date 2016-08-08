package sxccal.edu.android.remouse;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import sxccal.edu.android.remouse.net.ClientConnectionThread;

public class ConnectionFragment extends Fragment implements View.OnClickListener {

    private Button mDiscover, mConnect;
    public static boolean sActiveConnection = false;
    private static final int REQUEST_INTERNET_ACCESS = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_connect, container, false);
        mDiscover= (Button) view.findViewById(R.id.discover_button);
        mConnect= (Button) view.findViewById(R.id.connect_button);
        mDiscover.setOnClickListener(this);
        mConnect.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInternetPermission();
        }
        try {
            if(view.getId() == R.id.connect_button) {
                if(!sActiveConnection) {
                    connect();
                    sActiveConnection = true;
                }
            } else if(view.getId() == R.id.discover_button) {
                // TODO: Local network discovery module
            }
        }catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_INTERNET_ACCESS: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this.getActivity(), "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
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
    }

    private void connect() {
        Thread clientConnectionThread = new Thread(new ClientConnectionThread(this.getContext()));
        clientConnectionThread.start();
    }
}