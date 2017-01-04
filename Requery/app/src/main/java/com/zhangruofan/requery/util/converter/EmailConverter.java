package com.zhangruofan.requery.util.converter;

import android.text.TextUtils;

import com.zhangruofan.requery.model.Email;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.requery.Converter;
import io.requery.Nullable;

/**
 * Created by zhangruofan on 16-12-30.
 */

public class EmailConverter implements Converter<List<Email>, String> {

    private static final String SEPARATOR = "\0007";

    @Override
    public Class<List<Email>> getMappedType() {
        return (Class) List.class;
    }

    @Override
    public Class<String> getPersistedType() {
        return String.class;
    }

    @Nullable
    @Override
    public Integer getPersistedSize() {
        return null;
    }

    @Override
    public String convertToPersisted(List<Email> value) {
        if (value == null || value.size() <= 0) return null;

        StringBuilder sb = new StringBuilder();

        for (Email email : value) {
            sb.append(email.getEmail());
            sb.append(SEPARATOR);
        }

        return sb.toString();
    }

    @Override
    public List<Email> convertToMapped(Class<? extends List<Email>> type, String value) {
        if (TextUtils.isEmpty(value)) return Collections.emptyList();

        String[] emailArr = value.split(SEPARATOR);

        List<Email> emails = new ArrayList<>(emailArr.length);

        for (String str : emailArr) {
            Email email = new Email();
            email.setEmail(str);
            emails.add(email);
        }

        return emails;
    }
}
