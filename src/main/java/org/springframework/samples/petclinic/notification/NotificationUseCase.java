/*
 * Copyright 2016-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.notification;

import org.springframework.samples.petclinic.notification.dto.VisitCreatedDto;
import org.springframework.samples.petclinic.owner.domain.event.VisitCreated;

/**
 * Input port (use case) for notification operations.
 * This is the entry point to the notification module's application layer.
 * Located at module root for Spring Modulith compliance.
 *
 * @author GitHub Copilot
 */
public interface NotificationUseCase {

    /**
     * Process a visit created event and send notification.
     *
     * @param visitCreated The visit created domain event
     */
    void processVisitCreated(VisitCreatedDto visitCreated);
}
