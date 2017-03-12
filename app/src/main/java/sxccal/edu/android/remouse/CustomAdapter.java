package sxccal.edu.android.remouse;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sxccal.edu.android.remouse.net.ServerInfo;

import static sxccal.edu.android.remouse.ConnectionFragment.sConnectionAlive;

/**
 * @author Sayantan Majumdar
 */

class CustomAdapter extends ArrayAdapter<ServerInfo> {

    private Activity mActivity;
    private ConnectionFragment mConnectionFragment;

    CustomAdapter(Activity activity, int textViewResourceId, ArrayList<ServerInfo> arrayList) {
        super(activity, textViewResourceId, arrayList);
        mActivity = activity;
        mConnectionFragment = (ConnectionFragment) MainActivity.getConnectionFragment();;
    }

    private class ViewHolder {
        private TextView mTextView;
        private ImageView mImageView;
    }

    @Override
    public int getCount() {
        super.getCount();
        return mConnectionFragment.getNetworkList().size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            convertView = layoutInflater.inflate(R.layout.local_devices, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.code);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.connectIcon);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArrayList<ServerInfo> serverList = mConnectionFragment.getNetworkList();
        if(!serverList.isEmpty()) {
            ServerInfo serverInfo = serverList.get(position);
            viewHolder.mTextView.setText(serverInfo.getServerInfo() + " (" + serverInfo.getAddress() + ")");
            if(sConnectionAlive.get(serverInfo.getAddress())) {
                viewHolder.mImageView.setImageResource(R.mipmap.connect_icon);
            } else {
                viewHolder.mImageView.setImageResource(R.mipmap.laptop_icon);
            }
        }
        return convertView;
    }
}