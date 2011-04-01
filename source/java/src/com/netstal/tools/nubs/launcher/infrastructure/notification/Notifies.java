package com.netstal.tools.nubs.launcher.infrastructure.notification;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Notifies {
   Class<? extends IListener>[] value();
}
