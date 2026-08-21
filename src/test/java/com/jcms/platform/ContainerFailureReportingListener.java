/*
 * Copyright 2026 J-CMS Maintainers (https://github.com/aoxijy/j-cms)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jcms.platform;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

/**
 * Ant's junitlauncher prints each class's "Tests run: N, Failures: M" line using
 * TestExecutionSummary#getTestsFailedCount() (test-level only), but sets the
 * failureproperty that fails the build using #getTotalFailureCount() (test+container
 * level). A container failure -- a @BeforeAll/@AfterAll or extension callback throwing --
 * can fail the build while every printed per-class line still reads "Failures: 0", making
 * ci-test fail with no visible cause anywhere in the log. This listener prints the
 * container, its source, and the exception whenever that gap would otherwise hide it.
 */
public class ContainerFailureReportingListener implements TestExecutionListener {

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult result) {
        if (!testIdentifier.isContainer() || result.getStatus() != TestExecutionResult.Status.FAILED) {
            return;
        }
        System.out.println("================================================================================");
        System.out.println("CONTAINER-LEVEL TEST FAILURE (invisible to any class's \"Tests run\" summary line)");
        System.out.println("Container: " + testIdentifier.getDisplayName());
        System.out.println("Source:    " + testIdentifier.getSource().map(Object::toString).orElse("<unknown>"));
        System.out.println("================================================================================");
        result.getThrowable().ifPresent(t -> t.printStackTrace(System.out));
        System.out.println("================================================================================");
    }
}
