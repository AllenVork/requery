package com.zhangruofan.requery.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zhangruofan.requery.MyApplication;
import com.zhangruofan.requery.R;
import com.zhangruofan.requery.adapter.PersonAdapter;
import com.zhangruofan.requery.model.Person;
import com.zhangruofan.requery.util.CreatePeople;
import com.zhangruofan.requery.util.CustomizeSubscriber;

import io.requery.query.Scalar;
import io.requery.rx.SingleEntityStore;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    private SingleEntityStore data;
    private PersonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        data = ((MyApplication) getApplication()).getData();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new PersonAdapter(data, this);
        recyclerView.setAdapter(adapter);

        int count = ((Scalar<Integer>) data.count(Person.class).get()).value();

        if (count <= 0) {
            new CreatePeople(data).call()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CustomizeSubscriber<Iterable<Person>>() {
                        @Override
                        public void onNext(Iterable<Person> persons) {
                            adapter.queryAsync();
                        }
                    });
        } else {
            adapter.queryAsync();
        }
    }
}
