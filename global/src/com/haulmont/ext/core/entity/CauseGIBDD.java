/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.core.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.docflow.core.entity.Doc;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mahdi on 7/1/14.
 */
@Entity(name = "ext$CauseGIBDD")
@Table(name = "ext_cause")
@DiscriminatorValue("130")
@PrimaryKeyJoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID")
@Listeners("com.haulmont.docflow.core.listeners.DocEntityListener")
@NamePattern("%s|serial")
public class CauseGIBDD extends Doc {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFENDER_ID")
    protected NaturalPerson offender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICEMAN_ID")
    protected Policeman policeman;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAR_ID")
    protected Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENT_ID")
    protected Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WITNESS_ID")
    protected NaturalPerson witness;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VICTIM_ID")
    protected NaturalPerson victim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TESTIMONY_ID")
    protected Testimony testimony;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID")
    protected Article article;

    @Column(name = "SERIAL")
    protected String serial;

    @Column(name = "PETITION")
    protected Boolean petition;

    @Column(name = "SETTLING_DATE")
    @Temporal(TemporalType.DATE)
    protected Date settlingDate;

    @Column(name = "SETTLING_PLACE")
    protected String settlingPlace;

    @Column(name = "VIOLATION_DATE")
    protected Date violationDate;

    @Column(name = "VIOLATION_ESSENCE")
    protected String violationEssence;

    @Column(name = "REVIEW_PLACE")
    protected String reviewPlace;

    @Column(name = "CONDITION")
    protected String condition;

    @Column(name = "VIOLATION_PLACE")
    protected String violationPlace;

    public NaturalPerson getOffender() {
        return offender;
    }

    public void setOffender(NaturalPerson offender) {
        this.offender = offender;
    }

    public Policeman getPoliceman() {
        return policeman;
    }

    public void setPoliceman(Policeman policeman) {
        this.policeman = policeman;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public NaturalPerson getWitness() {
        return witness;
    }

    public void setWitness(NaturalPerson witness) {
        this.witness = witness;
    }

    public NaturalPerson getVictim() {
        return victim;
    }

    public void setVictim(NaturalPerson victim) {
        this.victim = victim;
    }

    public Testimony getTestimony() {
        return testimony;
    }

    public void setTestimony(Testimony testimony) {
        this.testimony = testimony;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Boolean getPetition() {
        return petition;
    }

    public Date getSettlingDate() {
        return settlingDate;
    }

    public void setSettlingDate(Date settlingDate) {
        this.settlingDate = settlingDate;
    }

    public String getSettlingPlace() {
        return settlingPlace;
    }

    public void setSettlingPlace(String settlingPlace) {
        this.settlingPlace = settlingPlace;
    }

    public String getViolationEssence() {
        return violationEssence;
    }

    public void setViolationEssence(String violationEssence) {
        this.violationEssence = violationEssence;
    }

    public void setPetition(Boolean petition) {
        this.petition = petition;
    }

    public Date getViolationDate() {
        return violationDate;
    }

    public void setViolationDate(Date violationDate) {
        this.violationDate = violationDate;
    }

    public String getReviewPlace() {
        return reviewPlace;
    }

    public void setReviewPlace(String reviewPlace) {
        this.reviewPlace = reviewPlace;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getViolationPlace() {
        return violationPlace;
    }

    public void setViolationPlace(String violationPlace) {
        this.violationPlace = violationPlace;
    }
}
