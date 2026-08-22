package com.jcms.platform.presentation.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminUiCommandTest {

  @Test
  void resolvesOnlySupportedLocales() {
    assertEquals(Locale.SIMPLIFIED_CHINESE, AdminUiCommand.resolveLocale("zh_CN"));
    assertEquals(Locale.ENGLISH, AdminUiCommand.resolveLocale("en"));
    assertEquals(Locale.ENGLISH, AdminUiCommand.resolveLocale("invalid"));
    assertEquals(Locale.ENGLISH, AdminUiCommand.resolveLocale(null));
  }

  @Test
  void resolvesOnlySupportedThemes() {
    assertEquals("colorful", AdminUiCommand.resolveTheme("colorful"));
    assertEquals("default", AdminUiCommand.resolveTheme("invalid"));
    assertEquals("default", AdminUiCommand.resolveTheme(null));
  }

  @Test
  void translatesChineseAndFallsBackToSourceText() {
    assertEquals("后台管理", AdminUiCommand.message("page.title.admin", "Admin", Locale.SIMPLIFIED_CHINESE));
    assertEquals("Source", AdminUiCommand.message("missing.key", "Source", Locale.SIMPLIFIED_CHINESE));
  }

  @Test
  void createsStableMessageKeysFromRoutes() {
    assertEquals("admin.web.page", AdminUiCommand.keyForPath("/admin/web-page"));
  }

  @Test
  void englishAndChineseBundlesContainTheSameKeys() throws IOException {
    Properties english = loadProperties("i18n/admin.properties");
    Properties chinese = loadProperties("i18n/admin_zh_CN.properties");
    assertEquals(english.stringPropertyNames(), chinese.stringPropertyNames());
  }

  private static Properties loadProperties(String resource) throws IOException {
    Properties properties = new Properties();
    try (InputStream input = AdminUiCommandTest.class.getClassLoader().getResourceAsStream(resource)) {
      if (input == null) {
        throw new IOException("Missing test resource: " + resource);
      }
      properties.load(input);
    }
    return properties;
  }
}
