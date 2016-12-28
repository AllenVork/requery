package com.zhangruofan.requery.util;

import android.util.Log;

import com.zhangruofan.requery.model.AddressEntity;
import com.zhangruofan.requery.model.Person;
import com.zhangruofan.requery.model.PersonProxy;
import com.zhangruofan.requery.model.Phone;
import com.zhangruofan.requery.model.PhoneEntity;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import io.requery.rx.SingleEntityStore;
import rx.Single;

/**
 * Created by zhangruofan on 16-12-26.
 */

public class CreatePeople {
    private static final String TAG = CreatePeople.class.getSimpleName();
    private final SingleEntityStore data;

    public CreatePeople(SingleEntityStore data) {
        this.data = data;
    }

    public Single<Iterable<Person>> call() {
        Random random = new Random();

        List<Person> people = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            PersonProxy person = new PersonProxy();
            String[] firstNames = Constants.Name.firstNames;
            String[] lastNames = Constants.Name.lastNames;
            String first = firstNames[random.nextInt(firstNames.length)];
            String last = lastNames[random.nextInt(lastNames.length)];

            person.setName(first + " " + last);
            person.setUUID(UUID.randomUUID());
            person.setEmail(Character.toLowerCase(first.charAt(0)) +
                    last.toLowerCase() + "@gmail.com");
            person.setBirthday(new Date());

            AddressEntity address = new AddressEntity();
            address.setLine1("123 Market St");
            address.setZip("94105");
            address.setCity("San Francisco");
            address.setState("CA");
            address.setCountry("US");

            Phone phone = new PhoneEntity();
            phone.setPhoneNumber("12354565");
//            phone.setOwner(person);
            person.getPhoneNumberList().add(phone);

            person.setAddress(address);

            people.add(person);
        }
        return data.insert(people);
    }
}
