package ar.gob.buenosaires.barelevamiento.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ar.gob.buenosaires.barelevamiento.application.Collect;

public class ConnectivityProvider implements NetworkStateProvider {

    public boolean isDeviceOnline() {
        NetworkInfo networkInfo = getNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public NetworkInfo getNetworkInfo() {
        return getConnectivityManager().getActiveNetworkInfo();
    }

    private ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) Collect.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
