/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.core.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

/**
 * Created by mahdi on 6/29/14.
 */
@Entity(name = "ext$Testimony")
@Table(name = "ext_testimony")
@NamePattern("%s|witness")
public class Testimony extends StandardEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WITNESS_ID")
    private NaturalPerson witness;

    @Column(name = "testimony", length = 1000)
    private String testimony;

    public NaturalPerson getWitness() {
        return witness;
    }

    public void setWitness(NaturalPerson witness) {
        this.witness = witness;
    }

    public String getTestimony() {
        return testimony;
    }

    public void setTestimony(String testimony) {
        this.testimony = testimony;
    }
}
