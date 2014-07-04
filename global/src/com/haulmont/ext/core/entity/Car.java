/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.core.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;

@Entity(name = "ext$Car")
@Table(name = "ext_car")
@NamePattern("%s|brand")
public class Car extends StandardEntity{

    @Column(name = "BRAND", length = 100)
    private String brand;

    @Column(name = "MODEL", length = 100)
    private String model;

    @Column(name = "STATE_LICENSE_TOKEN", length = 100)
    private String stateLicenseToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    private NaturalPerson owner;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStateLicenseToken() {
        return stateLicenseToken;
    }

    public void setStateLicenseToken(String stateLicenseToken) {
        this.stateLicenseToken = stateLicenseToken;
    }

    public NaturalPerson getOwner() {
        return owner;
    }

    public void setOwner(NaturalPerson owner) {
        this.owner = owner;
    }
}

