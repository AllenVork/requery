package com.zhangruofan.requery.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.requery.CascadeAction;
import io.requery.Column;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToMany;
import io.requery.ManyToOne;
import io.requery.OneToMany;
import io.requery.OneToOne;
import io.requery.query.MutableResult;
import io.requery.query.Result;

/**
 * Created by zhangruofan on 16-12-26.
 */
@Entity(name = "PersonProxy")
public interface Person {

    //主键，自增
    @Key
    @Generated
    int getId();

    String getName();

    String getEmail();

    Date getBirthday();
//    void setBirthday(Date date);

    int getAge();

    //如果一个表中嵌入另一个表，须指明@ForeignKey以及对应关系，如@OneToOne
    @OneToOne
    @ForeignKey
    @Column(name = "address")
    Address getAddress();

    @OneToMany(mappedBy = "owner", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    MutableResult<Phone> getPhoneNumbers();

    UUID getUUID();

    @OneToMany(mappedBy = "owner", cascade = {CascadeAction.DELETE, CascadeAction.SAVE})
    List<Phone> getPhoneNumberList();
}
