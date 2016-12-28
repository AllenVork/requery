package com.zhangruofan.requery;

import android.app.Application;

import com.zhangruofan.requery.model.Models;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.rx.RxSupport;
import io.requery.rx.SingleEntityStore;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;

/**
 * Created by zhangruofan on 16-12-26.
 */

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();

    private SingleEntityStore dataStore;

    @Override
    public void onCreate() {
        super.onCreate();
//        StrictMode.enableDefaults();
    }

    public SingleEntityStore getData() {
        if (dataStore == null) {
            DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 1);

            Configuration configuration = source.getConfiguration();
            dataStore = RxSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));
        }
        return dataStore;
    }
}
