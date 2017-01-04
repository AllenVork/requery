package com.zhangruofan.requery.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.zhangruofan.requery.MyApplication;
import com.zhangruofan.requery.R;
import com.zhangruofan.requery.model.Address;
import com.zhangruofan.requery.model.AddressEntity;
import com.zhangruofan.requery.model.Email;
import com.zhangruofan.requery.model.Person;
import com.zhangruofan.requery.model.PersonProxy;
import com.zhangruofan.requery.model.Phone;
import com.zhangruofan.requery.model.PhoneEntity;
import com.zhangruofan.requery.util.Constants;
import com.zhangruofan.requery.util.CustomizeSubscriber;

import java.util.List;

import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.rx.SingleEntityStore;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class PersonEditActivity extends AppCompatActivity {
    private static final String TAG = PersonEditActivity.class.getSimpleName();

    private SingleEntityStore<Persistable> data;
    private PersonProxy person;

    private ImageView picture;
    private EditText name;
    private EditText phoneNum;
    private EditText email;
    private EditText street;
    private EditText state;
    private EditText zip;
    private EditText country;
    private EditText city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_edit);

        initView();
        initData();
    }

    private void initView() {
        picture = (ImageView) findViewById(R.id.picture);
        name = (EditText) findViewById(R.id.name);
        phoneNum = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        state = (EditText) findViewById(R.id.state);
        street = (EditText) findViewById(R.id.street);
        zip = (EditText) findViewById(R.id.zip);
        country = (EditText) findViewById(R.id.country);
        city = (EditText) findViewById(R.id.city);
    }

    private void initData() {
        data = ((MyApplication) getApplication()).getData();

        int personId = getIntent().getIntExtra(Constants.Extra.EXTRA_PERSON_ID, -1);
        if (personId == -1) {
            person = new PersonProxy();
        } else {
            data.findByKey(PersonProxy.class, personId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CustomizeSubscriber<PersonProxy>() {
                        @Override
                        public void onNext(PersonProxy personProxy) {
                            person = personProxy;
                            data.refresh(person);
                            updateData(person);
                        }
                    });
        }
    }

    private void updateData(PersonProxy person) {
        name.setText(person.getName());
        Phone phone;
        if (person.getPhoneNumberList().isEmpty()) {
            phone = new PhoneEntity();
//            phone.setOwner(person);
            person.getPhoneNumberList().add(phone);
        } else {
            phone = person.getPhoneNumberList().get(0);
        }
        phoneNum.setText(phone.getPhoneNumber());
        List<Email> emails = person.getEmail();
        if (emails != null) {
            email.setText(emails.get(0).getEmail() + "  " + emails.get(1).getEmail());
        }
        state.setText(person.getAddress().getState());
        street.setText(person.getAddress().getLine1());
        zip.setText(person.getAddress().getZip());
        city.setText(person.getAddress().getCity());
        country.setText(person.getAddress().getCountry());
    }

    private void basicDbOperation() {
        data.count(Person.class).get().toSingle().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                data.select(PersonProxy.class, PersonProxy.ID, PersonProxy.NAME, PersonProxy.AGE)
                        .get()
                        .toObservable()
                        //如果不用buffer的话，有多少个数据就会调用多少次onNext()，这里是将所有数据缓存起来，然后一次性发过去
                        .buffer(integer)
                        .subscribe(new CustomizeSubscriber<List<PersonProxy>>() {
                            @Override
                            public void onNext(List<PersonProxy> personProxies) {
                                for (PersonProxy person : personProxies) {
                                    person.setAge(18);
                                }

                                //将修改后的数据更新到数据库中
                                data.update(personProxies).subscribe(new CustomizeSubscriber<Iterable<PersonProxy>>() {
                                    @Override
                                    public void onCompleted() {
                                        data.select(Person.class).get().toObservable().subscribe(new Action1<Person>() {
                                            @Override
                                            public void call(Person person) {
                                                Log.e(TAG, "call: personAge = " + person.getAge());
                                            }
                                        });
                                    }
                                });
                            }
                        });
            }
        });

        data.delete(AddressEntity.class)
                .where(AddressEntity.CITY.notLike("Hong Kong"))
                .and(AddressEntity.COUNTRY.eq("China"))
                .orderBy(AddressEntity.STATE.desc())
                .limit(5)
                .get()
                .toSingle().subscribe();

        data.delete().get().toSingle().subscribe();

        data.update(Person.class)
                .set(PersonProxy.AGE, 22)
                .where(PersonProxy.ID.greaterThan(5))
                .get()
                .toSingle()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }
                });
    }
}
