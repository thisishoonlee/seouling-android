/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobile.seouling.com.framework.rx;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * A {@link Scheduler} backed by a {@link Handler}.
 */
public final class MainThreadScheduler extends Scheduler {
    private final Handler handler;

    public MainThreadScheduler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public Worker createWorker() {
        return new MainThreadWorker(handler);
    }

    private static class MainThreadWorker extends Worker {
        private final Handler handler;

        private final CompositeDisposable compositeSubscription = new CompositeDisposable();

        MainThreadWorker(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void dispose() {
            compositeSubscription.dispose();
        }

        @Override
        public boolean isDisposed() {
            return compositeSubscription.isDisposed();
        }

        @Override
        public Disposable schedule(@NonNull Runnable runnable, long delay, @NonNull TimeUnit unit) {
            if (isDisposed()) {
                return Disposables.disposed();
            }

            ScheduledRunnable scheduled = new ScheduledRunnable(handler, runnable);
            compositeSubscription.add(scheduled);
            handler.postDelayed(scheduled, unit.toMillis(delay));

            // Re-check disposed state for removing in case we were racing a call to dispose().
            if (isDisposed()) {
                handler.removeCallbacks(scheduled);
                return Disposables.disposed();
            }
            return scheduled;
        }

        @Override
        public Disposable schedule(@NonNull Runnable runnable) {
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                runnable.run();
                return Disposables.disposed();
            } else {
                return schedule(runnable, 0, TimeUnit.MILLISECONDS);
            }
        }
    }

    private static final class ScheduledRunnable implements Runnable, Disposable {
        private final Handler handler;
        private final Runnable runnable;

        private volatile boolean disposed;

        ScheduledRunnable(Handler handler, Runnable runnable) {
            this.handler = handler;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch (Throwable t) {
                IllegalStateException ie = new IllegalStateException("Fatal Exception thrown on Scheduler.", t);
                RxJavaPlugins.onError(ie);
                Thread thread = Thread.currentThread();
                thread.getUncaughtExceptionHandler().uncaughtException(thread, ie);
            }
        }

        @Override
        public void dispose() {
            disposed = true;
            handler.removeCallbacks(this);
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }
}
