package com.zhangruofan.requery.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.zhangruofan.requery.MyApplication;
import com.zhangruofan.requery.R;
import com.zhangruofan.requery.model.PersonProxy;
import com.zhangruofan.requery.model.Phone;
import com.zhangruofan.requery.model.PhoneEntity;
import com.zhangruofan.requery.util.Constants;
import com.zhangruofan.requery.util.CustomizeSubscriber;

import java.util.List;

import io.requery.rx.SingleEntityStore;
import rx.android.schedulers.AndroidSchedulers;

public class PersonEditActivity extends AppCompatActivity {

    private SingleEntityStore data;
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
                            updateData(personProxy);
                            person = personProxy;
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
        email.setText(person.getEmail());
        state.setText(person.getAddress().getState());
        street.setText(person.getAddress().getLine1());
        zip.setText(person.getAddress().getZip());
        city.setText(person.getAddress().getCity());
        country.setText(person.getAddress().getCountry());
    }
}
