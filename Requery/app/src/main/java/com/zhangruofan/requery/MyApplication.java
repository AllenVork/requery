package com.zhangruofan.requery;

import android.app.Application;

import com.zhangruofan.requery.model.Models;

import io.requery.Persistable;
import io.requery.android.sqlcipher.SqlCipherDatabaseSource;
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

    private SingleEntityStore<Persistable> dataStore;

    @Override
    public void onCreate() {
        super.onCreate();
//        StrictMode.enableDefaults();
    }

    public SingleEntityStore<Persistable> getData() {
        if (dataStore == null) {
            DatabaseSource source = new DatabaseSource(this, Models.DEFAULT, 2);
            //如果要加密数据库就用下面这个，当然密码要妥善处理，不能直接这样写
            //SqlCipherDatabaseSource source = new SqlCipherDatabaseSource(this, Models.DEFAULT, "name", "password",  1);

            Configuration configuration = source.getConfiguration();
            dataStore = RxSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));
        }
        return dataStore;
    }
}
