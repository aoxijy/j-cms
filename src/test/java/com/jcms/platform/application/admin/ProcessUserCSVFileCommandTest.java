/*
 * Copyright 2022 J-CMS Maintainers (https://github.com/aoxijy/j-cms)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jcms.platform.application.admin;

import com.jcms.platform.WidgetBase;
import com.jcms.platform.application.audit.SaveAuditEventCommand;
import com.jcms.platform.application.cms.SaveFilePartCommand;
import com.jcms.platform.application.filesystem.FileSystemCommand;
import com.jcms.platform.application.register.SaveUserCommand;
import com.jcms.platform.domain.model.Group;
import com.jcms.platform.domain.model.User;
import com.jcms.platform.domain.model.cms.FileItem;
import com.jcms.platform.infrastructure.persistence.GroupRepository;
import com.jcms.platform.infrastructure.persistence.UserRepository;
import com.jcms.platform.presentation.controller.AuditEventCommand;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

/**
 * Proves ProcessUserCSVFileCommand.processCSV() only counts (and records a "success" audit event for)
 * CSV rows that SaveUserCommand.saveUser() actually persisted. A row where saveUser() returns null --
 * a caught DB-level failure inside UserRepository.add() -- must not inflate the "N users added" total
 * reported back to the admin, and must be recorded as a failure instead, mirroring the null-check
 * UsersListWidget#addUserAction already applies to the same saveUser() call (issue behind PR for
 * over-counted CSV import success).
 */
class ProcessUserCSVFileCommandTest extends WidgetBase {

  private Path csvFile;

  @AfterEach
  void cleanup() throws Exception {
    if (csvFile != null) {
      Files.deleteIfExists(csvFile);
    }
  }

  @Test
  void aRowWhereSaveUserReturnsNullIsNotCountedAndIsRecordedAsAFailure() throws Exception {
    setRoles(widgetContext, ADMIN);

    csvFile = Files.createTempFile("user-import", ".csv");
    Files.write(csvFile, ("Email,First Name,Last Name\n" +
        "ok@example.com,Ok,User\n" +
        "broken@example.com,Broken,User\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);

    FileItem fileItemBean = new FileItem();
    fileItemBean.setFileServerPath("uploads/user-import.csv");

    Group allUsersGroup = new Group();
    allUsersGroup.setId(1L);
    allUsersGroup.setName("All Users");

    User savedOkUser = new User();
    savedOkUser.setId(42L);
    savedOkUser.setEmail("ok@example.com");

    try (MockedStatic<SaveFilePartCommand> saveFilePart = mockStatic(SaveFilePartCommand.class);
        MockedStatic<FileSystemCommand> fileSystem = mockStatic(FileSystemCommand.class);
        MockedStatic<GroupRepository> groupRepository = mockStatic(GroupRepository.class);
        MockedStatic<UserRepository> userRepository = mockStatic(UserRepository.class);
        MockedStatic<SaveUserCommand> saveUserCommand = mockStatic(SaveUserCommand.class);
        MockedStatic<SaveAuditEventCommand> audit = mockStatic(SaveAuditEventCommand.class)) {

      saveFilePart.when(() -> SaveFilePartCommand.saveFile(widgetContext)).thenReturn(fileItemBean);
      fileSystem.when(FileSystemCommand::getFileServerRootPath).thenReturn("/tmp/");
      fileSystem.when(() -> FileSystemCommand.resolveWithinRoot(anyString(), anyString()))
          .thenReturn(csvFile.toFile());
      groupRepository.when(() -> GroupRepository.findByName("All Users")).thenReturn(allUsersGroup);
      userRepository.when(() -> UserRepository.findByUsername(anyString())).thenReturn(null);

      // Simulate SaveUserCommand.saveUser() returning null for one row, as it does on a caught
      // DB-level failure inside UserRepository.add() -- the other row persists normally
      saveUserCommand.when(() -> SaveUserCommand.saveUser(any(User.class))).thenAnswer(invocation -> {
        User candidate = invocation.getArgument(0);
        if ("broken@example.com".equals(candidate.getEmail())) {
          return null;
        }
        return savedOkUser;
      });

      int userCount = ProcessUserCSVFileCommand.processCSV(widgetContext);

      Assertions.assertEquals(1, userCount, "Only the row that actually persisted should be counted");

      audit.verify(() -> SaveAuditEventCommand.recordAdminEvent(eq(AuditEventCommand.USER_MANAGEMENT),
          eq("user.create"), eq(AuditEventCommand.SUCCESS), eq(1L), any(), any(), any(),
          eq("user"), eq("42"), eq("ok@example.com"), anyString()), times(1));

      audit.verify(() -> SaveAuditEventCommand.recordAdminEvent(eq(AuditEventCommand.USER_MANAGEMENT),
          eq("user.create"), eq(AuditEventCommand.FAILURE), eq(1L), any(), any(), any(),
          eq("user"), eq(null), eq("broken@example.com"), anyString()), times(1));
    }
  }
}
