/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.web.ui.Document.temp;

import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.IFrame;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.RefreshAction;
import com.haulmont.cuba.web.filestorage.WebExportDisplay;
import com.haulmont.cuba.web.gui.components.WebButtonsPanel;
import com.haulmont.cuba.web.gui.components.WebFilter;
import com.haulmont.docflow.web.actions.ThesisExcelAction;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by mahdi on 6/28/14.
 */
public class DocumentBrowser extends AbstractLookup {
    @Inject
    protected Table documentsTable;

    @Inject
    protected WebFilter genericFilter;

    @Inject
    protected Component verificationactsPanel;

    public DocumentBrowser(IFrame frame) {
        super(frame);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        documentsTable.setMultiSelect(true);
        verificationactsPanel = getComponent("verificationactsPanel");
        if (verificationactsPanel != null) {
            WebButtonsPanel buttonsPanel = new WebButtonsPanel();
            buttonsPanel.add(verificationactsPanel);
            documentsTable.setButtonsPanel(buttonsPanel);
            documentsTable.setRowsCount(documentsTable.getRowsCount());
        }

        ComponentsHelper.createActions(documentsTable);
        documentsTable.addAction(new RefreshAction(documentsTable));
        documentsTable.addAction(new ThesisExcelAction(documentsTable,
                new WebExportDisplay(), genericFilter));
    }
}

