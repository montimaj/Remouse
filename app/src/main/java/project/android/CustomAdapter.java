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
 * Displays the list of available servers in a <code>ListView</code>
 * format.
 *
 * <p>
 *     This <code>CustomAdapter</code> populates the <code>ListView</code>
 *     in the {@link ConnectionFragment}. List items consist of
 *     {@link project.android.net.ServerInfo} objects which are added/removed
 *     dynamically.
 * </p>
 *
 * @see project.android.ConnectionFragment
 * @see project.android.net.ServerInfo
 */
class CustomAdapter extends ArrayAdapter<ServerInfo> {

    private Activity mActivity;
    private ConnectionFragment mConnectionFragment;

    /**
     * Constructor.
     *
     * Initializes this <code>CustomAdapter</code>.
     *
     * @param activity the current <code>android.app.Activity</code> object.
     * @param textViewResourceId the resource ID for a layout file containing a
     *                           <code>TextView</code> to use when instantiating views.
     * @param arrayList the {@link project.android.net.ServerInfo} objects to be
     *                  displayed in the <code>ListView</code>.
     * @see project.android.net.ServerInfo
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
     * Overrides the <code>ArrayAdapter.getCount()</code>
     * method.
     *
     * Returns the number of items in the data set represented by this adapter.
     *
     * @return the count of items.
     */
    @Override
    public int getCount() {
        super.getCount();
        return mConnectionFragment.getNetworkList().size();
    }

    /**
     * Overrides <code>ArrayAdapter.getView(int, View, ViewGroup)</code>
     * method.
     *
     * Gets a <code>View</code> that displays the data at the specified
     * position in the data set.
     *
     * @param position the position of the item within the adapter's data
     *                 set of the item whose view is required.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A <code>View</code> corresponding to the data at the specified
     *         position.
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