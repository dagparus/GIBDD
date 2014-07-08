/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.core.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by mahdi on 6/23/14.
 */
@Entity(name = "ext$Article")
@Table(name = "ext_article")
@NamePattern("%s|name")
public class Article extends StandardEntity {

    @Column(name = "part", length = 10)
    private String part;

    @Column(name = "number", length = 10)
    private String number;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "template", length = 1000)
    private String template;

    @Column(name = "penalty", length = 10)
    private String penalty;

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }
}
