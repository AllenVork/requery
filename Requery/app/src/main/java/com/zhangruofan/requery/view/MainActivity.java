package com.zhangruofan.requery.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.zhangruofan.requery.MyApplication;
import com.zhangruofan.requery.R;
import com.zhangruofan.requery.adapter.PersonAdapter;
import com.zhangruofan.requery.model.Person;
import com.zhangruofan.requery.model.PersonProxy;
import com.zhangruofan.requery.util.CreatePeople;
import com.zhangruofan.requery.util.CustomizeSubscriber;

import java.util.List;

import io.requery.query.Scalar;
import io.requery.rx.RxResult;
import io.requery.rx.RxScalar;
import io.requery.rx.SingleEntityStore;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    //若不传入<Persistable>的话，下面的select.get会返回一个Object对象，要自己进行强制转换
    private SingleEntityStore/*<Persistable>*/ data;
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

    /**
     * 对比PersonEditActivity中的forCompare
     */
    private void forCompare() {
        ((RxScalar<Integer>) data.count(Person.class).get()).toSingle().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {

            }
        });

        //将Object对象强制转换为RxResult对象
        RxResult<Person> result = (RxResult) data.select(Person.class).get();
        result.toObservable().subscribe(new Action1<Person>() {
            @Override
            public void call(Person person) {

            }
        });
    }
}
