/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.core.entity.Enum;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

/**
 * Created by mahdi on 6/29/14.
 */
public enum DocumentType implements EnumClass<String> {

    NONE ("NN"),
    PASSPORT ("PP"),
    DRIVER_LICENSE ("DL"),
    BIRTH_CERTIFICATE ("BC"),
    INTERNATIONAL_PASSPORT ("IP"),
    WAR_CERTIFICATE ("WC"),
    PENSION_CERTIFICATE ("PC");

    private String id;

    private DocumentType(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static DocumentType fromId(String id) {
        if ("NN".equals(id))
            return NONE;
        else if ("PP".equals(id))
            return PASSPORT;
        else if ("DL".equals(id))
            return DRIVER_LICENSE ;
        else if ("BC".equals(id))
            return BIRTH_CERTIFICATE;
        else if ("IP".equals(id))
            return INTERNATIONAL_PASSPORT;
        else if ("WC".equals(id))
            return WAR_CERTIFICATE;
        else if ("PC".equals(id))
            return PENSION_CERTIFICATE;
        else return null;
    }
}
