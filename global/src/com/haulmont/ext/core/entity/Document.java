/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.core.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.ext.core.entity.Enum.DocumentType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mahdi on 6/27/14.
 */
@Entity(name = "ext$Document")
@Table(name = "ext_document")
@NamePattern("%s|docType")
public class Document extends StandardEntity {

    @Column(name = "doctype", length = 30)
    private String docType = DocumentType.NONE.getId();

    @Column(name = "serial")
    private Integer serial;

    @Column(name = "number")
    private Integer number;

    @Column(name = "whom_give_out")
    private String whomGiveOut;

    @Column(name = "when_give_out")
    @Temporal(TemporalType.DATE)
    protected Date whenGiveOut;

    @Column(name = "expiration_date")
    @Temporal(TemporalType.DATE)
    protected Date expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    protected NaturalPerson owner;

    public NaturalPerson getOwner() {
        return owner;
    }
    public void setOwner(NaturalPerson owner) {
        this.owner = owner;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getWhenGiveOut() {
        return whenGiveOut;
    }

    public void setWhenGiveOut(Date whenGiveOut) {
        this.whenGiveOut = whenGiveOut;
    }

    public String getWhomGiveOut() {
        return whomGiveOut;
    }

    public void setWhomGiveOut(String whomGiveOut) {
        this.whomGiveOut = whomGiveOut;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getSerial() {
        return serial;
    }

    public void setSerial(Integer serial) {
        this.serial = serial;
    }

    public DocumentType getDocType() {
        return DocumentType.fromId(docType);
    }

    public void setDocType(DocumentType docType) {
        this.docType = docType == null ? null : docType.getId();
    }
}
