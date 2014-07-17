/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.web.ui.Document;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.ext.core.entity.Document;
import com.haulmont.ext.core.entity.NaturalPerson;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by mahdi on 6/28/14.
 */
public class DocumentEditor extends AbstractEditor {

    protected LookupPickerField owner;

    NaturalPerson nowNP, nextNP;
    Document docObject;

    @Inject
    protected Datasource documentsDs;

    @Inject
    protected CollectionDatasource naturalPersonsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        owner = getComponent("owner");
        try {
            nowNP = ((Document)params.get("item")).getOwner();
            docObject = (Document)params.get("item");
        } catch (Exception e) {
            nowNP = null;
            docObject = (Document)params.get("item");
        }

    }

    @Override
    public void commitAndClose() {
        try {
            nextNP = owner.getValue();
            if(nowNP == null) {
                nextNP.setDocument(docObject);
                nowNP.getName();
            }
            else if(nowNP != null && nowNP != nextNP) {
                nowNP.setDocument(null);
                nextNP.setDocument(docObject);
                nowNP.getName();
            }
        } catch (Exception e) {
            nextNP = null;
            nowNP = null;
        }
        if (frame instanceof Window.Editor) {
            ((Editor) frame).commitAndClose();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
