/*
 * Copyright (c) 2011 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.haulmont.ext.web;

import com.haulmont.cuba.web.DefaultConnection;
import com.haulmont.docflow.web.DocflowAppWindow;

public class ExtAppWindow extends DocflowAppWindow {

    public ExtAppWindow(DefaultConnection connection) {
        super(connection);
    }

//    @Override
//    protected boolean isCreateDocumentButtonVisible() {
//        return false;
//    }
//
//    @Override
//    protected boolean isCreateContractButtonVisible() {
//        return false;
//    }
}
