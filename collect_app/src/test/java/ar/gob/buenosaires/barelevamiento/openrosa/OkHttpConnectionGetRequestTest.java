package ar.gob.buenosaires.barelevamiento.openrosa;

import android.webkit.MimeTypeMap;

import ar.gob.buenosaires.barelevamiento.openrosa.okhttp.OkHttpConnection;
import ar.gob.buenosaires.barelevamiento.openrosa.okhttp.OkHttpOpenRosaServerClientProvider;

import okhttp3.OkHttpClient;

public class OkHttpConnectionGetRequestTest extends OpenRosaGetRequestTest {

    @Override
    protected OpenRosaHttpInterface buildSubject() {
        return new OkHttpConnection(
                new OkHttpOpenRosaServerClientProvider(new OkHttpClient()),
                new CollectThenSystemContentTypeMapper(MimeTypeMap.getSingleton()),
                USER_AGENT
        );
    }
}