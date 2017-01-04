package com.zhangruofan.requery.model;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;

/**
 * Created by zhangruofan on 16-12-26.
 */
@Entity
public interface Address {

    @Key @Generated
    int getId();

    String getLine1();
    void setLine1(String line1);

    String getLine2();
    void setLine2(String line2);

    String getZip();
    void setZip(String zip);

    String getCountry();
    void setCountry(String country);

    String getCity();
    void setCity(String city);

    String getState();
    void setState(String state);

//    @OneToOne(mappedBy = "address")
//    Person getPerson();
}
