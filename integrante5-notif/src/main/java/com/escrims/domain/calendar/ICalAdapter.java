package com.escrims.domain.calendar;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

public class ICalAdapter {
    public static byte[] scrimToIcs(UUID scrimId, String juego, String region, ZonedDateTime start, int durMinutes){
        Calendar cal = new Calendar();
        cal.getProperties().add(new ProdId("-//eScrims//iCal Adapter//ES"));
        cal.getProperties().add(Version.VERSION_2_0);
        cal.getProperties().add(CalScale.GREGORIAN);

        VEvent event = new VEvent();
        event.getProperties().add(new Uid(scrimId.toString()));
        event.getProperties().add(new Summary("Scrim " + juego + " (" + region + ")"));
        event.getProperties().add(new Description("Scrim programado"));
        event.getProperties().add(new net.fortuna.ical4j.model.property.DtStart(new DateTime(java.util.Date.from(start.toInstant()))));
        event.getProperties().add(new Duration(new Dur(0,0,durMinutes,0)));
        event.getProperties().add(new Url(URI.create("https://app.escrims/scrims/" + scrimId)));
        cal.getComponents().add(event);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new CalendarOutputter().output(cal, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
