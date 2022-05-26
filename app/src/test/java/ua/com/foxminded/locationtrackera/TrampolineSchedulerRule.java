package ua.com.foxminded.locationtrackera;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class TrampolineSchedulerRule implements TestRule {

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
                try {
                    base.evaluate();
                } finally {
                    RxAndroidPlugins.reset();
                }
            }
        };
    }
}
