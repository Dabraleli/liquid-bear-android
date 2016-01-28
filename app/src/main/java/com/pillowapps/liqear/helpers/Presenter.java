package com.pillowapps.liqear.helpers;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class Presenter<V> {

    @NonNull
    private final CompositeSubscription subscriptionsToUnsubscribeOnUnbindView = new CompositeSubscription();

    private volatile V view;

    @CallSuper
    public void bindView(@NonNull V view) {
        final V previousView = this.view;

        if (previousView != null) {
            throw new IllegalStateException("Previous view is not unbounded! previousView = " + previousView);
        }

        this.view = view;
    }

    @NonNull
    protected V view() {
        if (view == null) {
            throw new RuntimeException("View must be bound to presenter");
        }
        return view;
    }

    protected final void unsubscribeOnUnbindView(@NonNull Subscription subscription, @NonNull Subscription... subscriptions) {
        subscriptionsToUnsubscribeOnUnbindView.add(subscription);

        for (Subscription s : subscriptions) {
            subscriptionsToUnsubscribeOnUnbindView.add(s);
        }
    }

    @CallSuper
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public void unbindView(@NonNull V view) {
        final V previousView = this.view;

        if (previousView == view) {
            this.view = null;
        } else {
            throw new IllegalStateException("Unexpected view! previousView = " + previousView + ", view to unbind = " + view);
        }

        // Unsubscribe all subscriptions that need to be unsubscribed in this lifecycle state.
        subscriptionsToUnsubscribeOnUnbindView.clear();
    }
}
