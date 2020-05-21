package ar.gob.buenosaires.barelevamiento.utilities;

import ar.gob.buenosaires.barelevamiento.preferences.AdminSharedPreferences;

import ar.gob.buenosaires.barelevamiento.preferences.AdminKeys;
import ar.gob.buenosaires.barelevamiento.preferences.AdminSharedPreferences;

import static ar.gob.buenosaires.barelevamiento.preferences.AdminKeys.KEY_ADMIN_PW;

public class AdminPasswordProvider {
    private final AdminSharedPreferences adminSharedPreferences;

    public AdminPasswordProvider(AdminSharedPreferences adminSharedPreferences) {
        this.adminSharedPreferences = adminSharedPreferences;
    }

    public boolean isAdminPasswordSet() {
        String adminPassword = getAdminPassword();
        return adminPassword != null && !adminPassword.isEmpty();
    }

    public String getAdminPassword() {
        return (String) adminSharedPreferences.get(AdminKeys.KEY_ADMIN_PW);
    }
}
