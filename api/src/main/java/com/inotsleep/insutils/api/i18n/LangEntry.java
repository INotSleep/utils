package com.inotsleep.insutils.api.i18n;

import java.util.List;

public final class LangEntry {
    private final String value;
    private final List<String> listValue;

    public LangEntry(String value) {
        this.value = value;
        this.listValue = null;
    }

    public LangEntry(List<String> listValue) {
        this.listValue = List.copyOf(listValue);
        this.value = null;
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
