/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.web.ui.NaturalPerson;

import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DsListenerAdapter;
import com.haulmont.cuba.security.global.UserUtils;
import com.haulmont.ext.core.entity.NaturalPerson;
import com.haulmont.taskman.core.enums.SexEnum;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by mahdi on 7/8/14.
 */
public class NaturalPersonEditor extends AbstractEditor {

    protected OptionsGroup sexField;
    protected TextField surnameField;
    protected TextField nameField;
    protected TextField patronymicField;
    protected TextField fullnameField;
    protected TextField surnameOfField;
    protected TextField nameOfField;
    protected TextField patronymicOfField;
    protected TextField fullnameOfField;

    private static final List<String> PROPERTY_NAMES = Arrays.asList("name", "patronymic", "surname");

    @Inject
    protected Datasource naturalPersonsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        sexField = getComponent("sex");
        surnameField = getComponent("surname");
        nameField = getComponent("name");
        patronymicField = getComponent("patronymic");
        fullnameField = getComponent("fullname");
        surnameOfField = getComponent("surnameOf");
        nameOfField = getComponent("nameOf");
        patronymicOfField = getComponent("patronymicOf");
        fullnameOfField = getComponent("fullnameOf");

        naturalPersonsDs.addListener(new DsListenerAdapter() {
            private String pattern;
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
                NaturalPerson SexObjext = (NaturalPerson) source;
                SexEnum sexIn = SexObjext.getSex();
                String surnameValue = surnameField.getValue();
                if (surnameValue == null) surnameValue = "";
                String nameValue = nameField.getValue();
                if (nameValue == null) nameValue = "";
                String patronymicValue = patronymicField.getValue();
                if (patronymicValue == null) patronymicValue = "";
                if ("sex".equals(property) & (surnameField.getValue() != null || nameField.getValue() != null || patronymicField.getValue() != null)) {
                    if (value == SexEnum.FEMALE) {
                        surnameOfField.setValue(surnameValue.substring(0, surnameValue.length() - 1) + (char) 1086 + (char) 1081);
                        nameOfField.setValue(nameValue.substring(0, nameValue.length() - 1) + (char) 1099);
                        patronymicOfField.setValue(patronymicValue.substring(0, patronymicValue.length() - 1) + (char) 1099);
                        fullnameOfField.setValue((String) surnameOfField.getValue() + " " + nameOfField.getValue() + " " + patronymicOfField.getValue());
                    } else {
                        surnameOfField.setValue(surnameValue + (char) 1072);
                        nameOfField.setValue(nameValue + (char) 1072);
                        patronymicOfField.setValue(patronymicValue + (char) 1072);
                        fullnameOfField.setValue((String) surnameOfField.getValue() + " " + nameOfField.getValue() + " " + patronymicOfField.getValue());
                    }
                }
                if ("surname".equals(property)) {
                    if (sexIn == SexEnum.FEMALE) surnameOfField.setValue(surnameValue.substring(0, surnameValue.length() - 1) + (char) 1086 + (char) 1081);
                    else surnameOfField.setValue(surnameValue + (char) 1072);
                    fullnameOfField.setValue((String) surnameOfField.getValue() + " " + (isNull(nameOfField.getValue()) ? "" : nameOfField.getValue()) + " " + (isNull(patronymicOfField.getValue()) ? "" : patronymicOfField.getValue()));
                }
                if ("name".equals(property)) {
                    if (sexIn == SexEnum.FEMALE) nameOfField.setValue(nameValue.substring(0, nameValue.length() - 1) + (char) 1099);
                    else nameOfField.setValue(nameValue + (char) 1072);
                    fullnameOfField.setValue((String) (isNull(surnameOfField.getValue()) ? "" : surnameOfField.getValue()) + " " + nameOfField.getValue() + " " + (isNull(patronymicOfField.getValue()) ? "" : patronymicOfField.getValue()));

                }
                if ("patronymic".equals(property)) {
                    if (sexIn == SexEnum.FEMALE) patronymicOfField.setValue(patronymicValue.substring(0, patronymicValue.length() - 1) + (char) 1099);
                    else patronymicOfField.setValue(patronymicValue + (char) 1072);
                    fullnameOfField.setValue((String) (isNull(surnameOfField.getValue()) ? "" : surnameOfField.getValue()) + " " + (isNull(nameOfField.getValue()) ? "" : nameOfField.getValue()) + " " + patronymicOfField.getValue());
                }
                if (!PROPERTY_NAMES.contains(property)) {
                    surnameValue = surnameField.getValue();
                    if (surnameValue == null) surnameValue = "";
                    nameValue = nameField.getValue();
                    if (nameValue == null) nameValue = "";
                    patronymicValue = patronymicField.getValue();
                    if (patronymicValue == null) patronymicValue = "";

                    String displayedName;
                    try {
                        if (this.pattern == null) {
                            pattern = AppContext.getProperty("cuba.user.fullNamePattern");
                            if (StringUtils.isBlank(pattern))
                                pattern = "{LL| }{F|. }{M|. }";
                        }

                        displayedName = UserUtils.formatName(pattern, nameValue, surnameValue, patronymicValue);
                    } catch (ParseException pe) {
                        displayedName = "";
                    }

                    fullnameField.setValue(displayedName);
                }
            }

        });
    }

    private boolean isNull(Object line) {
        if (line == null) return true;
        else return false;
    }
}
