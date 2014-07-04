/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.core.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.taskman.core.enums.SexEnum;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mahdi on 6/23/14.
 */
@Entity(name = "ext$NaturalPerson")
@Table(name = "ext_natural_person")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("NP")
@NamePattern("%s|name")
public class NaturalPerson extends StandardEntity {

    @Column(name = "name", length = 50)
    protected String name;

    @Column(name = "surname", length = 70)
    protected String surname;

    @Column(name = "patronymic", length = 60)
    protected String patronymic;

    @Column(name = "birthdate")
    @Temporal(TemporalType.DATE)
    protected Date birthDate;

    @Column(name = "birthplace", length = 200)
    protected String birthPlace;

    @Column(name = "residence", length = 200)
    protected String residence;

    @Column(name = "domicile", length = 200)
    protected String domicile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENT_ID")
    protected Document document;

    @Column(name = "telephone", length = 50)
    protected String telephone;

    @Column(name = "sex")
    protected String sex = SexEnum.MALE.getId();

    public SexEnum getSex() {
        return SexEnum.fromId(sex);
    }

    public void setSex(SexEnum sex) {
        this.sex = sex == null ? null : sex.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
