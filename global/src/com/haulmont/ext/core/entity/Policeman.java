/*
 * Copyright (c) 2014 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.ext.core.entity;

import com.haulmont.chile.core.annotations.NamePattern;

import javax.persistence.*;

/**
 * Created by mahdi on 6/26/14.
 */
@Entity(name = "ext$Policeman")
@Table(name = "ext_policeman")
@PrimaryKeyJoinColumn(name = "PERSON_ID", referencedColumnName = "ID")
@DiscriminatorValue("PM")
@NamePattern("%s|name")
public class Policeman extends NaturalPerson {

    @Column(name = "post", length = 50)
    protected String post;

    @Column(name = "post_of", length = 50)
    protected String postOf;

    @Column(name = "area", length = 50)
    protected String area;

    @Column(name = "address_organ", length = 100)
    protected String addressOrgan;

    @Column(name = "city", length = 50)
    protected String city;

    @Column(name = "badge_number")
    protected Integer badgeNumber;

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPostOf() {
        return postOf;
    }

    public void setPostOf(String postOf) {
        this.postOf = postOf;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddressOrgan() {
        return addressOrgan;
    }

    public void setAddressOrgan(String addressOrgan) {
        this.addressOrgan = addressOrgan;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(Integer badgeNumber) {
        this.badgeNumber = badgeNumber;
    }
}
