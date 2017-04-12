package project.android;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import project.android.net.ServerInfo;

import static project.android.ConnectionFragment.sConnectionAlive;

/**
 * @author Sayantan Majumdar
 */

class CustomAdapter extends ArrayAdapter<ServerInfo> {

    private Activity mActivity;
    private ConnectionFragment mConnectionFragment;

    /**
     * Constructor.<br/>
     * Initializes this <code>CustomAdapter</code>.
     * @param activity The current <code>android.app.Activity</code> object.
     * @param textViewResourceId The resource ID for a layout file containing a TextView to use when instantiating views.
     * @param arrayList The {@link project.android.net.ServerInfo} objects to represent in the <code>ListView</code>.
     */
    CustomAdapter(Activity activity, int textViewResourceId, ArrayList<ServerInfo> arrayList) {
        super(activity, textViewResourceId, arrayList);
        mActivity = activity;
        mConnectionFragment = (ConnectionFragment) MainActivity.getConnectionFragment();;
    }

    private class ViewHolder {
        private TextView mTextView;
        private ImageView mImageView;
    }

    /**
     * Overrides <code>android.widget.ArrayAdapter.getCount()</code>.<br/>
     *
     * Number of items in the data set represented by this Adapter.
     * @return Count of items.
     */
    @Override
    public int getCount() {
        super.getCount();
        return mConnectionFragment.getNetworkList().size();
    }

    /**
     * Overrides <code>android.widget.ArrayAdapter.getView(int, View, ViewGroup)</code>.<br/>
     *
     * Get a <code>View</code that displays the data at the specified position in the data set.
     * @param position The position of the item within the adapter's data set of the item whose view is required.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to
     * @return A <code>View</code> corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

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