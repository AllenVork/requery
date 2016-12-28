package com.zhangruofan.requery.model;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToOne;

/**
 * Created by zhangruofan on 16-12-26.
 */
@Entity
public interface Phone {
    @Key @Generated
    int getId();

    String getPhoneNumber();
    void setPhoneNumber(String phoneNumber);

    @ManyToOne
    Person getOwner();
//    void setOwner(Person person);
}
