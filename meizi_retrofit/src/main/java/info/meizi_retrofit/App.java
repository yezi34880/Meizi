package info.meizi_retrofit;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Mr_Wrong on 15/10/30.
 */
public class App extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        RealmConfiguration myConfig = new RealmConfiguration.Builder(getContext())
                .name("myRealm")
                .schemaVersion(2)
//                .setModules(new MyCustomSchema())
                .build();


        Realm.setDefaultConfiguration(myConfig);
    }

    public static Context getContext() {
        return mContext;
    }

}
