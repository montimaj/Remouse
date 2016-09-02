package sxccal.edu.android.remouse;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import sxccal.edu.android.remouse.net.ClientConnectionThread;
import sxccal.edu.android.remouse.net.ClientIOThread;

public class ConnectionFragment extends ListFragment {

    private static ArrayList<String> mNetWorkList = new ArrayList<>();
    private static ArrayAdapter<String> mAdapter;

    private static final int REQUEST_INTERNET_ACCESS = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_connect, container, false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInternetPermission();
        }
        if(mAdapter != null) mAdapter.clear();
        mAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_selectable_list_item, mNetWorkList);
        setListAdapter(mAdapter);
        discoverLocalDevices();
        return view;
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
        ClientIOThread clientIOThread = new ClientIOThread(getActivity(), address);
        new Thread(clientIOThread).start();
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

    private void discoverLocalDevices() {
        ClientConnectionThread clientConnectionThread = new ClientConnectionThread(getContext(), getActivity());
        new Thread(clientConnectionThread).start();
    }

    public static void addItems(HashSet<String> address) {
        for(String addr: address)   mNetWorkList.add(addr);
        mAdapter.notifyDataSetChanged();
    }
}