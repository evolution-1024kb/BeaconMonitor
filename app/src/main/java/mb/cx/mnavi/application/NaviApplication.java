package mb.cx.mnavi.application;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import mb.cx.mnavi.BuildConfig;
import mb.cx.mnavi.utility.RealmSetup;

/**
 * アプリケーションクラス
 * Created by toshiaki on 2017/01/22.
 */

public class NaviApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
        }

        Realm.init(this);

        final RealmSetup realmSetup = new RealmSetup();
        realmSetup.setup(this);

        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                            .build());
        }
    }
}
