package site.hanschen.runwithyou.application;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import site.hanschen.api.user.UserCenterApi;
import site.hanschen.api.user.UserCenterApiImpl;
import site.hanschen.api.user.UserCenterApiWrapper;
import site.hanschen.runwithyou.dagger.AppContext;
import site.hanschen.runwithyou.database.gen.DaoMaster;
import site.hanschen.runwithyou.database.gen.DaoSession;
import site.hanschen.runwithyou.database.repository.SettingRepository;
import site.hanschen.runwithyou.database.repository.SettingRepositoryImpl;
import site.hanschen.runwithyou.database.repository.StepRepository;
import site.hanschen.runwithyou.database.repository.StepRepositoryImpl;
import site.hanschen.runwithyou.service.RunnerManager;

/**
 * @author HansChen
 */
@Module
class ApplicationModule {

    private RunnerApplication mApp;

    ApplicationModule(RunnerApplication app) {
        this.mApp = app;
    }

    @Provides
    @AppContext
    Context provideAppContext() {
        return mApp.getApplicationContext();
    }

    @Provides
    RunnerManager provideRunnerManager() {
        return mApp.getRunnerManager();
    }

    @Provides
    @Singleton
    SharedPreferences provideDefaultSharedPreferences(@AppContext Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    SensorManager provideSensorManager(@AppContext Context context) {
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Provides
    @Singleton
    NotificationManager provideNotificationManager(@AppContext Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @Nullable
    BluetoothAdapter provideBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    @Provides
    @Singleton
    SettingRepository provideSettingRepository(@AppContext Context context) {
        return new SettingRepositoryImpl(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    @Provides
    @Singleton
    DaoSession provideDaoSession(@AppContext Context context) {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, "runner-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        return new DaoMaster(db).newSession();
    }

    @Provides
    @Singleton
    StepRepository provideStepRepository(DaoSession daoSession) {
        return new StepRepositoryImpl(daoSession.getStepRecordEntityDao());
    }

    @Provides
    @Singleton
    UserCenterApi provideUserCenterApi() {
        boolean remoteServer = true;
        return new UserCenterApiImpl(remoteServer ? "www.hanschen.site" : "192.168.1.3", 8980);
    }

    @Provides
    @Singleton
    UserCenterApiWrapper provideUserCenterApiWrapper(UserCenterApi api) {
        return new UserCenterApiWrapper(api);
    }
}
