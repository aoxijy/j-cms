/*
 * Copyright 2026 J-CMS Maintainers (https://github.com/aoxijy/j-cms)
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

package com.jcms.platform.domain.events.mailinglists;

import com.jcms.platform.domain.events.Event;
import com.jcms.platform.domain.model.User;
import com.jcms.platform.domain.model.mailinglists.MailingList;
import com.jcms.platform.domain.model.mailinglists.MailingListMember;

/**
 * Event details for when a new mailing list member is created (issue #452).
 *
 * @author J-CMS Maintainers
 */
public class MailingListMemberCreatedEvent extends Event {

  public static final String ID = "mailing-list-member-created";

  private final MailingListMember member;
  private final MailingList mailingList;
  private final User user;

  public MailingListMemberCreatedEvent(MailingListMember member, MailingList mailingList, User user) {
    this.member = member;
    this.mailingList = mailingList;
    this.user = user;
  }

  @Override
  public String getDomainEventType() {
    return ID;
  }

  public MailingListMember getMember() {
    return member;
  }

  public MailingList getMailingList() {
    return mailingList;
  }

  /** The user who triggered the subscription, or null for an anonymous/public signup. */
  public User getUser() {
    return user;
  }
}
