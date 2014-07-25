/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.web.ui.CauseGIBDD;

import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.ServiceLocator;
import com.haulmont.cuba.gui.components.AbstractAction;
import com.haulmont.cuba.report.Report;

/**
 * Created with IntelliJ IDEA.
 * User: Магомедзапир
 * Date: 28.01.14
 * Time: 12:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractActionPrint extends AbstractAction {
    protected String reportName;

    AbstractActionPrint(String id) {
        super(id);
        this.reportName = id;
    }

    protected Report loadReport(String reportName) {
        LoadContext ctx = new LoadContext(Report.class);
        LoadContext.Query query = ctx.setQueryString("select r from report$Report r where r.name = :reportName");
        query.addParameter("reportName", this.reportName);
        Report report = ServiceLocator.getDataService().load(ctx);
        return report;
    }
}
