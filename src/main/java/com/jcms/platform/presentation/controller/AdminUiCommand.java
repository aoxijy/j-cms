/*
 * Copyright 2026 J-CMS Maintainers (https://github.com/aoxijy/j-cms)
 * Licensed under the Apache License, Version 2.0
 */
package com.jcms.platform.presentation.controller;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** Resolves the administrator UI locale/theme with safe, backwards-compatible defaults. */
public final class AdminUiCommand {

  public static final String DEFAULT_LANGUAGE = "en";
  public static final String DEFAULT_THEME = "default";
  private static final String BUNDLE = "i18n.admin";

  private AdminUiCommand() {
  }

  public static Locale resolveLocale(String configuredLanguage) {
    return "zh_CN".equals(configuredLanguage) ? Locale.SIMPLIFIED_CHINESE : Locale.ENGLISH;
  }

  public static String resolveTheme(String configuredTheme) {
    return "colorful".equals(configuredTheme) ? "colorful" : DEFAULT_THEME;
  }

  public static String keyForPath(String path) {
    if (path == null || path.isBlank()) {
      return "unknown";
    }
    return path.replaceAll("^/+", "").replaceAll("[^A-Za-z0-9]+", ".");
  }

  /** Returns the English/source text whenever a key or locale-specific translation is absent. */
  public static String message(String key, String fallback, Locale locale) {
    if (key == null || key.isBlank()) {
      return fallback;
    }
    try {
      ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE, locale == null ? Locale.ENGLISH : locale);
      return bundle.containsKey(key) ? bundle.getString(key) : fallback;
    } catch (MissingResourceException e) {
      return fallback;
    }
  }
}
