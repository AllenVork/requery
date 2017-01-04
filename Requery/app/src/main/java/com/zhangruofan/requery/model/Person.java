package com.zhangruofan.requery.model;

import com.zhangruofan.requery.util.converter.EmailConverter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.requery.CascadeAction;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Generated;
import io.requery.Key;
import io.requery.OneToMany;
import io.requery.OneToOne;
import io.requery.Persistable;
import io.requery.query.MutableResult;

/**
 * Created by zhangruofan on 16-12-26.
 */
@Entity(name = "PersonProxy")
public interface Person extends Persistable {

    //主键，自增
    @Key
    @Generated
    int getId();

    String getName();

    Date getBirthday();
//    void setBirthday(Date date);

    String getExtra();

    int getAge();
    void setAge(int age);

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

    @Convert(EmailConverter.class)
    List<Email> getEmail();
    void setEmail(List<Email> email);
}
