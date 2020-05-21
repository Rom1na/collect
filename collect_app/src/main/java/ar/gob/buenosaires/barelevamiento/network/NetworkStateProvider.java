package ar.gob.buenosaires.barelevamiento.network;

import android.net.NetworkInfo;

public interface NetworkStateProvider {
    boolean isDeviceOnline();

    NetworkInfo getNetworkInfo();
}
