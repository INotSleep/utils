package com.inotsleep.utils.i18n.config;

import java.util.List;

public class LangEntry {
    private String value = null;
    private List<String> listValue = null;

    public LangEntry(String value) {
        this.value = value;
    }

    public LangEntry(List<String> listValue) {
        this.listValue = listValue;
    }

    public String getValue() {
        return value;
    }

    public List<String> getListValue() {
        return listValue;
    }

    public boolean isList() {
        return listValue != null;
    }
    public boolean isString() {
        return value != null;
    }
}
