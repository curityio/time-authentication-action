package io.curity.identityserver.plugin.authenticationaction.date_time

import spock.lang.Specification
import spock.lang.Unroll

import java.time.ZoneId

class ZoneIdUtilTest extends Specification {

    @Unroll
    def "All time zones in Time Zone Display can be translated to a ZoneId"(){
        given: "A time zone with display name"
        def TimeZoneDisplay timeZoneDisplay = configTimeZone;

        when: "Converting to ZoneId"
        ZoneId zoneId = ZoneIdUtil.getZoneId(configTimeZone)
        String timeZone = configTimeZone.getTimeZone()

        then: "Valid ZoneId"
        if (configTimeZone.getTimeZone() == "SYSTEM_TIME") {
            assert ZoneId.systemDefault() == zoneId
        } else {
            assert ZoneId.of(timeZone) == zoneId : "Time zone = $timeZone"
        }

        where:
        configTimeZone << TimeZoneDisplay.getEnumConstants();
    }

}
