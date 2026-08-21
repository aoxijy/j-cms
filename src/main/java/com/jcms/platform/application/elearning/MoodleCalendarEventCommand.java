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

package com.jcms.platform.application.elearning;

import com.fasterxml.jackson.databind.JsonNode;
import com.jcms.platform.application.cms.HtmlCommand;
import com.jcms.platform.application.cms.LocalDateCommand;
import com.jcms.platform.domain.model.elearning.Event;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.jcms.platform.application.elearning.MoodleApiClientCommand.GET_CALENDAR_EVENT_BY_ID;

/**
 * Commands for working with Moodle Calendar Events
 *
 * @author matt rajkowski
 * @created 7/12/2022 10:18 PM
 */
public class MoodleCalendarEventCommand {

  private static Log LOG = LogFactory.getLog(MoodleCalendarEventCommand.class);

  public static Event retrieveEventById(long eventId) {

    if (eventId < 1) {
      return null;
    }

    Map<String, String> parameters = new HashMap<>();
    parameters.put("eventid", String.valueOf(eventId));

    // Make the request
    JsonNode json = MoodleApiClientCommand.sendHttpGet(GET_CALENDAR_EVENT_BY_ID, parameters);

    // Verify that record(s) have been returned
    if (json == null || !json.has("event")) {
      LOG.debug("No records found");
      return null;
    }

    // Parse the records
    LOG.debug("Parsing record...");
    JsonNode eventNode = json.get("event");

    // Event information
    Event event = new Event();
    if (eventNode.has("id")) {
      String remoteId = eventNode.get("id").asText();
      event.setRemoteId(remoteId);
    }
    if (eventNode.has("url")) {
      event.setUrl(eventNode.get("url").asText());
    }
    if (eventNode.has("name")) {
      event.setName(StringUtils.trimToNull(eventNode.get("name").asText()));
    }
    if (eventNode.has("description")) {
      //description format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)
      String summary = eventNode.get("description").asText();
      event.setSummaryHtml(StringUtils.trimToNull(summary));
      event.setSummary(StringUtils.trimToNull(HtmlCommand.text(event.getSummaryHtml())));
    }
    if (eventNode.has("eventtype")) {

    }
    if (eventNode.has("timestart")) {
      long timestart = eventNode.get("timestart").asLong() * 1000;
      event.setStartDate(LocalDateCommand.convertToLocalDate(new Date(timestart)));
      event.setStartTime(LocalDateCommand.convertToLocalTime(new Date(timestart)));
    }
    if (eventNode.has("timeduration")) {

    }
    if (eventNode.has("timemodified")) {

    }
    return event;
  }
}
