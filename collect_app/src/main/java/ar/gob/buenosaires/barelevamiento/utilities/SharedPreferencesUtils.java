/*
 * Copyright (C) 2017 Shobhit
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package ar.gob.buenosaires.barelevamiento.utilities;

import org.json.JSONException;
import org.json.JSONObject;
import ar.gob.buenosaires.barelevamiento.application.Collect;
import ar.gob.buenosaires.barelevamiento.preferences.AdminSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.PreferenceSaver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import ar.gob.buenosaires.barelevamiento.application.Collect;
import ar.gob.buenosaires.barelevamiento.preferences.AdminKeys;
import ar.gob.buenosaires.barelevamiento.preferences.AdminSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralKeys;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.PreferenceSaver;
import timber.log.Timber;

import static ar.gob.buenosaires.barelevamiento.preferences.AdminKeys.ALL_KEYS;
import static ar.gob.buenosaires.barelevamiento.preferences.AdminKeys.KEY_ADMIN_PW;
import static ar.gob.buenosaires.barelevamiento.preferences.GeneralKeys.DEFAULTS;
import static ar.gob.buenosaires.barelevamiento.preferences.GeneralKeys.KEY_PASSWORD;

public final class SharedPreferencesUtils {

    private SharedPreferencesUtils() {

    }

    static String getJSONFromPreferences(Collection<String> passwordKeys) throws JSONException {
        Collection<String> keys = new ArrayList<>(passwordKeys);
        keys.addAll(GeneralKeys.DEFAULTS.keySet());
        JSONObject sharedPrefJson = getModifiedPrefs(keys);
        Timber.i(sharedPrefJson.toString());
        return sharedPrefJson.toString();
    }

    private static JSONObject getModifiedPrefs(Collection<String> keys) throws JSONException {
        JSONObject prefs = new JSONObject();
        JSONObject adminPrefs = new JSONObject();
        JSONObject generalPrefs = new JSONObject();

        //checking for admin password
        if (keys.contains(AdminKeys.KEY_ADMIN_PW)) {
            String password = (String) AdminSharedPreferences.getInstance().get(AdminKeys.KEY_ADMIN_PW);
            if (!password.equals("")) {
                adminPrefs.put(AdminKeys.KEY_ADMIN_PW, password);
            }
            keys.remove(AdminKeys.KEY_ADMIN_PW);
        }

        for (String key : keys) {
            Object defaultValue = GeneralKeys.DEFAULTS.get(key);
            Object value = GeneralSharedPreferences.getInstance().get(key);

            if (value == null) {
                value = "";
            }
            if (defaultValue == null) {
                defaultValue = "";
            }

            if (!defaultValue.equals(value)) {
                generalPrefs.put(key, value);
            }
        }
        prefs.put("general", generalPrefs);

        for (String key : AdminKeys.ALL_KEYS) {

            Object defaultValue = AdminSharedPreferences.getInstance().getDefault(key);
            Object value = AdminSharedPreferences.getInstance().get(key);
            if (defaultValue != value) {
                adminPrefs.put(key, value);
            }
        }
        prefs.put("admin", adminPrefs);

        return prefs;
    }

    public static boolean loadSharedPreferencesFromJSONFile(File src) {
        boolean res = false;
        BufferedReader br = null;

        try {
            String line = null;
            StringBuilder builder = new StringBuilder();
            br = new BufferedReader(new FileReader(src));

            while ((line = br.readLine()) != null) {
                builder.append(line);
            }

            new PreferenceSaver(GeneralSharedPreferences.getInstance(), AdminSharedPreferences.getInstance()).fromJSON(builder.toString(), null);

            Collect.getInstance().initializeJavaRosa();
            res = true;
        } catch (IOException e) {
            Timber.e(e, "Exception while loading preferences from file due to : %s ", e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Timber.e(ex, "Exception thrown while closing an input stream due to: %s ", ex.getMessage());
            }
        }

        return res;
    }

    public static Collection<String> getAllGeneralKeys() {
        Collection<String> keys = new HashSet<>(GeneralKeys.DEFAULTS.keySet());
        keys.add(GeneralKeys.KEY_PASSWORD);
        return keys;
    }

    public static Collection<String> getAllAdminKeys() {
        Collection<String> keys = new HashSet<>(AdminKeys.ALL_KEYS);
        keys.add(AdminKeys.KEY_ADMIN_PW);
        return keys;
    }
}

