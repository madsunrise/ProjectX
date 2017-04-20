package com.github.projectx.utils;

/**
 * Created by ivan on 16.04.17.
 */

public class Constants {
    public enum Menu {
        SEARCH_SERVICE, ADD_SERVICE, MY_SERVICES, SETTINGS, LOGOUT
    }

    public enum RequestCode {
        NEW_SERVICE, MY_SERVICES
    }

    public enum Keys {
        KEY1("token"),
        KEY2("session_id");

        private final String value;

        Keys(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
