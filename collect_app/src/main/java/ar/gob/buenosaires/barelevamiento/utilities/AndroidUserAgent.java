package ar.gob.buenosaires.barelevamiento.utilities;

import ar.gob.buenosaires.barelevamiento.BuildConfig;
import ar.gob.buenosaires.utilities.UserAgentProvider;

public final class AndroidUserAgent implements UserAgentProvider {

    @Override
    public String getUserAgent() {
        return String.format("%s/%s %s",
                BuildConfig.APPLICATION_ID,
                BuildConfig.VERSION_NAME,
                System.getProperty("http.agent"));
    }

}
