/*
 *  Copyright 2020 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.curity.identityserver.plugin.authenticationaction.date_time

import spock.lang.Specification
import spock.lang.Unroll

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

import static io.curity.identityserver.plugin.authenticationaction.date_time.TimeZoneDisplay.Europe_Stockholm as Stockholm
import static io.curity.identityserver.plugin.authenticationaction.date_time.TimeZoneDisplay.US_Pacific

class TimeComparerTest extends Specification {

    @Unroll
    def "It is possible to check that the current time (#clock) is at or after #noAccessBefore in the timezone #timezone"() {
        given: "A time comparer that allows access at or after #noAccessBefore and at or before #noAccessAfter in #timezone"
        def timeComparer = TimeDenyAuthenticationAction.TimeComparer.create(noAccessBefore, noAccessAfter, timezone, clock)

        when: "the current time is checked to see if it is at or after the allowed start time"
        boolean access = timeComparer.isNowAfterStartTime()

        then: "the result is as expected"
        assert access == expectedAccess: "noAccessBefore = $noAccessBefore, noAccesssAfter = $noAccessAfter, clock = $clock"

        where:
        noAccessBefore | noAccessAfter | timezone   | clock               | expectedAccess
        tc(11)         | tc(11)        | Stockholm  | c(timezone, 11)     | true
        tc(11)         | tc(11)        | Stockholm  | c(timezone, 10, 59) | false
        tc(11)         | tc(11)        | Stockholm  | c(timezone, 11, 1)  | true

        // 10 UTC        // 10 UTC                   // 19 UTC
        tc(12)         | tc(12)        | Stockholm  | c(US_Pacific, 12)   | true

        // 19 UTC        // 19 UTC                    // 10 UTC
        tc(12)         | tc(12)        | US_Pacific | c(Stockholm, 12)    | false
    }

    @Unroll
    def "It is possible to check that the current time (#clock) at or after #noAccessAfter in the timezone #timezone"() {
        given: "A time comparer that allows access at or after #noAccessBefore and at or before #noAccessAfter in #timezone"
        def timeComparer = TimeDenyAuthenticationAction.TimeComparer.create(noAccessBefore, noAccessAfter, timezone, clock)

        when: "the current time is checked to see if it is at or before the allowed end time"
        boolean access = timeComparer.isNowBeforeEndTime()

        then: "the result is as expected"
        assert access == expectedAccess: "noAccessBefore = $noAccessBefore, noAccesssAfter = $noAccessAfter, clock = $clock"

        where:
        noAccessBefore | noAccessAfter | timezone   | clock               | expectedAccess
        tc(11)         | tc(11)        | Stockholm  | c(timezone, 11)     | true
        tc(11)         | tc(11)        | Stockholm  | c(timezone, 10, 59) | true
        tc(11)         | tc(11)        | Stockholm  | c(timezone, 11, 1)  | false

        // 10 UTC        // 10 UTC                   // 19 UTC
        tc(12)         | tc(12)        | Stockholm  | c(US_Pacific, 12)   | false

        // 19 UTC        // 19 UTC                    // 10 UTC
        tc(12)         | tc(12)        | US_Pacific | c(Stockholm, 12)    | true
    }

    private static Clock c(TimeZoneDisplay timeZone, int year, int month, int day, int hour = 0, int minutes = 0) {
        ZoneId zoneId = ZoneIdUtil.getZoneId(timeZone)
        ZonedDateTime zonedDateTime = ZonedDateTime.of(year, month, day, hour, minutes, 0, 0, zoneId)

        return Clock.fixed(zonedDateTime.toInstant(), zoneId)
    }

    private static Clock c(TimeZoneDisplay timeZone, int hour = 0, int minutes = 0) {
        ZoneId zoneId = ZoneIdUtil.getZoneId(timeZone)
        def now = LocalDate.now(zoneId)

        return c(timeZone, now.year, now.monthValue, now.dayOfMonth, hour, minutes)
    }

    private static TimeConfiguration tc(int hour = 0, int minutes = 0) {
        return new TimeConfiguration() {
            @Override
            int getHour() {
                return hour
            }

            @Override
            int getMinute() {
                return minutes
            }

            @Override
            String toString() {
                return String.format("%02d:%02d", hour, minutes)
            }
        }
    }
}
