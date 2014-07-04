/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.web.ui.CauseGIBDD;

import com.haulmont.cuba.core.global.MessageProvider;
import com.haulmont.cuba.core.global.UserSessionProvider;
import com.haulmont.cuba.gui.components.IFrame;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.ext.core.entity.CauseGIBDD;
import com.haulmont.taskman.web.ui.common.CardSend;
import com.haulmont.workflow.core.entity.Card;
import com.haulmont.workflow.core.entity.CardInfo;

import java.util.Locale;
import java.util.Map;

/**
 * Created by mahdi on 12/11/13.
 */
public class CauseGIBDDSend extends CardSend {

    public CauseGIBDDSend(IFrame frame) {
        super(frame);
    }

    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected CardInfo createCardInfo(Card card, User user, String comment) {
        CardInfo ci = super.createCardInfo(card, user, comment);
        if (card instanceof CauseGIBDD) {
            CauseGIBDD cause = (CauseGIBDD) card;
            ci.setDescription(MessageProvider.getMessage(CauseGIBDDSend.class, "CauseGIBDDSend.comment",
                    (user.getLanguage() != null) ? new Locale(user.getLanguage()) : UserSessionProvider.getUserSession().getLocale())
                    + " " + cause.getNumber() + " (" + comment + ")");
        }
        return ci;
    }
}
